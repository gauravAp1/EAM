package com.example.eam.TechnicianTeam.Service;

import com.example.eam.Enum.TechnicianTeamStatus;
import com.example.eam.Technician.Dto.TechnicianDetailsResponse;
import com.example.eam.Technician.Entity.Technician;
import com.example.eam.Technician.Repository.TechnicianRepository;
import com.example.eam.TechnicianTeam.Dto.*;
import com.example.eam.TechnicianTeam.Entity.TechnicianTeam;
import com.example.eam.TechnicianTeam.Repository.TechnicianTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianTeamService {

    private final TechnicianTeamRepository technicianTeamRepository;
    private final TechnicianRepository technicianRepository;

    @Transactional
    public TechnicianTeamDetailsResponse createTeam(TechnicianTeamCreateRequest request) {
        TechnicianTeamStatus status = request.getStatus() != null ? request.getStatus() : TechnicianTeamStatus.ACTIVE;

        TechnicianTeam team = TechnicianTeam.builder()
                .teamName(request.getTeamName().trim())
                .teamDescription(request.getTeamDescription())
                .status(status)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .notes(request.getNotes())
                .build();

        TechnicianTeam saved = technicianTeamRepository.save(team);
        applyTeamMembership(saved, request.getTechnicianIds(), request.getTeamLeaderId());
        TechnicianTeam reloaded = getTeamOrThrow(saved.getId());
        return toDetailsResponse(reloaded);
    }

    @Transactional(readOnly = true)
    public TechnicianTeamDetailsResponse getTeam(Long id) {
        TechnicianTeam team = getTeamOrThrow(id);
        return toDetailsResponse(team);
    }

    @Transactional(readOnly = true)
    public TechnicianTeamListResponse listTeams(Pageable pageable) {
        Page<TechnicianTeam> page = technicianTeamRepository.findAll(pageable);
        List<TechnicianTeamDetailsResponse> rows = page.getContent().stream()
                .map(this::toDetailsResponse)
                .toList();

        return TechnicianTeamListResponse.builder()
                .teams(rows)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Transactional
    public TechnicianTeamDetailsResponse patchTeam(Long id, TechnicianTeamPatchRequest request) {
        TechnicianTeam team = getTeamOrThrow(id);

        if (request.getTeamName() != null) {
            String name = request.getTeamName().trim();
            if (name.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name cannot be blank");
            }
            team.setTeamName(name);
        }

        if (request.getTeamDescription() != null) team.setTeamDescription(request.getTeamDescription());
        if (request.getStatus() != null) team.setStatus(request.getStatus());
        if (request.getStartDate() != null) team.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) team.setEndDate(request.getEndDate());
        if (request.getNotes() != null) team.setNotes(request.getNotes());

        technicianTeamRepository.save(team);
        applyTeamMembership(team, request.getTechnicianIds(), request.getTeamLeaderId());

        TechnicianTeam reloaded = getTeamOrThrow(team.getId());
        return toDetailsResponse(reloaded);
    }

    @Transactional
    public void deleteTeam(Long id) {
        TechnicianTeam team = getTeamOrThrow(id);
        if (technicianRepository.existsByTeam_Id(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete team with assigned technicians");
        }
        technicianTeamRepository.delete(team);
    }

    private void applyTeamMembership(TechnicianTeam team, List<Long> technicianIds, Long requestedLeaderId) {
        boolean replaceMembership = technicianIds != null;
        if (!replaceMembership && requestedLeaderId == null) {
            return;
        }

        List<Technician> currentMembers = technicianRepository.findByTeam_Id(team.getId());
        Set<Long> desiredIds;

        if (replaceMembership) {
            if (technicianIds.stream().anyMatch(Objects::isNull)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Technician IDs cannot be null");
            }
            desiredIds = technicianIds.stream()
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            desiredIds = currentMembers.stream()
                    .map(Technician::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (requestedLeaderId != null && !desiredIds.contains(requestedLeaderId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team leader must be part of the assigned technicians");
        }

        if (replaceMembership) {
            syncTeamTechnicians(team, desiredIds, requestedLeaderId, currentMembers);
        } else {
            updateLeaderOnly(currentMembers, requestedLeaderId);
        }
    }

    private void syncTeamTechnicians(TechnicianTeam team,
                                     Set<Long> desiredIds,
                                     Long requestedLeaderId,
                                     List<Technician> currentMembers) {
        if (desiredIds.isEmpty()) {
            if (currentMembers.isEmpty()) return;
            currentMembers.forEach(member -> {
                member.setTeam(null);
                member.setTeamLeader(false);
            });
            technicianRepository.saveAll(currentMembers);
            return;
        }

        List<Technician> requestedTechnicians = technicianRepository.findAllById(desiredIds);
        if (requestedTechnicians.size() != desiredIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more technicians were not found");
        }

        Long leaderId = resolveLeaderId(requestedLeaderId, desiredIds, currentMembers);
        Map<Long, Technician> requestedMap = requestedTechnicians.stream()
                .collect(Collectors.toMap(Technician::getId, tech -> tech));

        List<Technician> dirty = new ArrayList<>();

        for (Technician member : currentMembers) {
            if (!desiredIds.contains(member.getId())) {
                member.setTeam(null);
                member.setTeamLeader(false);
                dirty.add(member);
            }
        }

        for (Long technicianId : desiredIds) {
            Technician technician = requestedMap.get(technicianId);
            technician.setTeam(team);
            technician.setTeamLeader(leaderId != null && leaderId.equals(technicianId));
            dirty.add(technician);
        }

        if (!dirty.isEmpty()) {
            technicianRepository.saveAll(dirty);
        }
    }

    private void updateLeaderOnly(List<Technician> members, Long leaderId) {
        if (leaderId == null) return;
        if (members.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team has no technicians to assign as leader");
        }

        boolean found = false;
        for (Technician member : members) {
            boolean isLeader = leaderId.equals(member.getId());
            if (isLeader) {
                found = true;
            }
            member.setTeamLeader(isLeader);
        }

        if (!found) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team leader must be part of the team");
        }

        technicianRepository.saveAll(members);
    }

    private Long resolveLeaderId(Long requestedLeaderId, Set<Long> desiredIds, List<Technician> currentMembers) {
        if (requestedLeaderId != null) {
            return requestedLeaderId;
        }

        return currentMembers.stream()
                .filter(Technician::isTeamLeader)
                .map(Technician::getId)
                .filter(desiredIds::contains)
                .findFirst()
                .orElse(null);
    }

    private TechnicianTeam getTeamOrThrow(Long id) {
        return technicianTeamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technician team not found"));
    }

    private TechnicianTeamDetailsResponse toDetailsResponse(TechnicianTeam team) {
        List<Technician> members = team.getTechnicians();
        List<TechnicianDetailsResponse> technicians = members == null
                ? List.of()
                : members.stream().map(this::mapTechnician).toList();

        Technician leader = findTeamLeader(members);

        return TechnicianTeamDetailsResponse.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .teamDescription(team.getTeamDescription())
                .status(team.getStatus())
                .startDate(team.getStartDate())
                .endDate(team.getEndDate())
                .notes(team.getNotes())
                .teamLeaderId(leader != null ? leader.getId() : null)
                .teamLeaderName(leader != null ? leader.getFullName() : null)
                .technicians(technicians)
                .build();
    }

    private TechnicianDetailsResponse mapTechnician(Technician technician) {
        TechnicianTeam assignedTeam = technician.getTeam();
        return TechnicianDetailsResponse.builder()
                .id(technician.getId())
                .firstName(technician.getFirstName())
                .lastName(technician.getLastName())
                .fullName(technician.getFullName())
                .technicianType(technician.getTechnicianType())
                .skills(technician.getSkills())
                .phoneNumber(technician.getPhoneNumber())
                .email(technician.getEmail())
                .address(technician.getAddress())
                .status(technician.getStatus())
                .hireDate(technician.getHireDate())
                .workShift(technician.getWorkShift())
                .certifications(technician.getCertifications())
                .notes(technician.getNotes())
                .teamId(assignedTeam != null ? assignedTeam.getId() : null)
                .teamName(assignedTeam != null ? assignedTeam.getTeamName() : null)
                .teamLeader(technician.isTeamLeader())
                .build();
    }

    private Technician findTeamLeader(List<Technician> members) {
        if (members == null) return null;
        return members.stream()
                .filter(Technician::isTeamLeader)
                .findFirst()
                .orElse(null);
    }
}

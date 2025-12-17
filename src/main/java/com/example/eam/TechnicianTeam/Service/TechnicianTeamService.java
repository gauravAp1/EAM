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

import java.util.List;

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
        return toDetailsResponse(saved);
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

        TechnicianTeam saved = technicianTeamRepository.save(team);
        return toDetailsResponse(saved);
    }

    @Transactional
    public void deleteTeam(Long id) {
        TechnicianTeam team = getTeamOrThrow(id);
        if (technicianRepository.existsByTeam_Id(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete team with assigned technicians");
        }
        technicianTeamRepository.delete(team);
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

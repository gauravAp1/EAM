package com.example.eam.FailureCode.Service;

import com.example.eam.FailureCode.Dto.*;
import com.example.eam.FailureCode.Entity.FailureCode;
import com.example.eam.FailureCode.Repository.FailureCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FailureCodeService {

    private final FailureCodeRepository failureCodeRepository;

    // ---------------- CREATE ----------------
    @Transactional
    public FailureCodeResponseDto create(FailureCodeCreateDto dto) {
        FailureCode failureCode = FailureCode.builder()
                .failureSymptomCode(dto.getFailureSymptomCode())
                .symptomDescription(dto.getSymptomDescription())
                .failureCauseCode(dto.getFailureCauseCode())
                .causeDescription(dto.getCauseDescription())
                .actionCode(dto.getActionCode())
                .actionDescription(dto.getActionDescription())
                .build();

        FailureCode saved = failureCodeRepository.save(failureCode);
        return toResponse(saved);
    }

    // ---------------- READ ----------------
    @Transactional(readOnly = true)
    public FailureCodeResponseDto get(Long id) {
        FailureCode failureCode = failureCodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Failure Code not found"));
        return toResponse(failureCode);
    }

    @Transactional(readOnly = true)
    public List<FailureCodeResponseDto> list() {
        List<FailureCode> failureCodes = failureCodeRepository.findAll();
        return failureCodes.stream()
                .map(this::toResponse)
                .toList();
    }

    // ---------------- UPDATE ----------------
    @Transactional
    public FailureCodeResponseDto update(Long id, FailureCodeUpdateDto dto) {
        FailureCode failureCode = failureCodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Failure Code not found"));

        failureCode.setFailureSymptomCode(dto.getFailureSymptomCode());
        failureCode.setSymptomDescription(dto.getSymptomDescription());
        failureCode.setFailureCauseCode(dto.getFailureCauseCode());
        failureCode.setCauseDescription(dto.getCauseDescription());
        failureCode.setActionCode(dto.getActionCode());
        failureCode.setActionDescription(dto.getActionDescription());

        FailureCode updated = failureCodeRepository.save(failureCode);
        return toResponse(updated);
    }

    // ---------------- DELETE ----------------
    @Transactional
    public void delete(Long id) {
        FailureCode failureCode = failureCodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Failure Code not found"));
        failureCodeRepository.delete(failureCode);
    }

    // Helper method to map FailureCode to FailureCodeResponseDto
    private FailureCodeResponseDto toResponse(FailureCode failureCode) {
        return FailureCodeResponseDto.builder()
                .id(failureCode.getId())
                .failureSymptomCode(failureCode.getFailureSymptomCode())
                .symptomDescription(failureCode.getSymptomDescription())
                .failureCauseCode(failureCode.getFailureCauseCode())
                .causeDescription(failureCode.getCauseDescription())
                .actionCode(failureCode.getActionCode())
                .actionDescription(failureCode.getActionDescription())
                .build();
    }
}

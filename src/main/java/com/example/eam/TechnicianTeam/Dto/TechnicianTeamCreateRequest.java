package com.example.eam.TechnicianTeam.Dto;

import com.example.eam.Enum.TechnicianTeamStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TechnicianTeamCreateRequest {

    @NotBlank
    private String teamName;

    private String teamDescription;

    private TechnicianTeamStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String notes;

    private List<Long> technicianIds;

    private Long teamLeaderId;
}

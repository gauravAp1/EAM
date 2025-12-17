package com.example.eam.TechnicianTeam.Dto;

import com.example.eam.Enum.TechnicianTeamStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TechnicianTeamPatchRequest {

    private String teamName;

    private String teamDescription;

    private TechnicianTeamStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String notes;
}

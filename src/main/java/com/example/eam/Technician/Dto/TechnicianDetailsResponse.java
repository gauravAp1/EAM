package com.example.eam.Technician.Dto;

import com.example.eam.Enum.TechnicianStatus;
import com.example.eam.Enum.TechnicianType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TechnicianDetailsResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private TechnicianType technicianType;
    private String skills;
    private String phoneNumber;
    private String email;
    private String address;
    private TechnicianStatus status;
    private LocalDate hireDate;
    private String workShift;
    private String certifications;
    private String notes;
    private Long teamId;
    private String teamName;
    private boolean teamLeader;
}

package com.example.eam.Technician.Dto;

import com.example.eam.Enum.TechnicianStatus;
import com.example.eam.Enum.TechnicianType;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TechnicianPatchRequest {

    private String firstName;

    private String lastName;

    private TechnicianType technicianType;

    private String skills;

    private String phoneNumber;

    @Email
    private String email;

    private String address;

    private TechnicianStatus status;

    private LocalDate hireDate;

    private String workShift;

    private String certifications;

    private String notes;
}

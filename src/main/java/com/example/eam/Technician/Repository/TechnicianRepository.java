package com.example.eam.Technician.Repository;

import com.example.eam.Technician.Entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByTeam_Id(Long teamId);

    List<Technician> findByTeam_Id(Long teamId);
}

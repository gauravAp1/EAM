package com.example.eam.FailureCode.Repository;

import com.example.eam.FailureCode.Entity.FailureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureCodeRepository extends JpaRepository<FailureCode, Long> {
    // Custom queries can be added here if needed
}


package com.example.eam.ServiceContract.Repository;

import com.example.eam.ServiceContract.Entity.ServiceContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceContractRepository extends JpaRepository<ServiceContract, Long> {
    boolean existsByContractId(String contractId);

    Optional<ServiceContract> findTopByOrderByIdDesc();

    Optional<ServiceContract> findByIdAndDeletedFalse(Long id);

    Page<ServiceContract> findByDeletedFalse(Pageable pageable);
}


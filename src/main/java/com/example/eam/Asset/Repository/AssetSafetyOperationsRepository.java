package com.example.eam.Asset.Repository;

import com.example.eam.Asset.Entity.AssetSafetyOperations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetSafetyOperationsRepository extends JpaRepository<AssetSafetyOperations, Long> {

    Optional<AssetSafetyOperations> findByAsset_Id(Long assetId);
}


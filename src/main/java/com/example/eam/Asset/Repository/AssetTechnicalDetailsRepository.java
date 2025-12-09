package com.example.eam.Asset.Repository;

import com.example.eam.Asset.Entity.AssetTechnicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetTechnicalDetailsRepository extends JpaRepository<AssetTechnicalDetails, Long> {

    Optional<AssetTechnicalDetails> findByAsset_Id(Long assetId);
}


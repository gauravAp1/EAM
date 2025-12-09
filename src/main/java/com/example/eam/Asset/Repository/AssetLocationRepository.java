package com.example.eam.Asset.Repository;

import com.example.eam.Asset.Entity.AssetLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetLocationRepository extends JpaRepository<AssetLocation, Long> {

    Optional<AssetLocation> findByAsset_Id(Long assetId);
}


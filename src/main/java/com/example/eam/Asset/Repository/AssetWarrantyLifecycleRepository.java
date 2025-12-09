package com.example.eam.Asset.Repository;

import com.example.eam.Asset.Entity.AssetWarrantyLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetWarrantyLifecycleRepository extends JpaRepository<AssetWarrantyLifecycle, Long> {

    Optional<AssetWarrantyLifecycle> findByAsset_Id(Long assetId);
}


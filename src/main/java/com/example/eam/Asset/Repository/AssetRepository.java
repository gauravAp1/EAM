package com.example.eam.Asset.Repository;


import com.example.eam.Asset.Entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    boolean existsByAssetId(String assetId);
}


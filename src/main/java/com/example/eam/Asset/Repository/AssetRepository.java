package com.example.eam.Asset.Repository;


import com.example.eam.Asset.Entity.Asset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    boolean existsByAssetId(String assetId);
    List<Asset> findByAssetCategory(String assetCategory);
}


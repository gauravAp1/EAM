package com.example.eam.Asset.Repository;


import com.example.eam.Asset.Entity.AssetFinancialDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetFinancialDetailsRepository extends JpaRepository<AssetFinancialDetails, Long> {

    Optional<AssetFinancialDetails> findByAsset_Id(Long assetId);
}


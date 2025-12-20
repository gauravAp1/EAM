package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.StockLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLedgerEntryRepository extends JpaRepository<StockLedgerEntry, Long> {
}

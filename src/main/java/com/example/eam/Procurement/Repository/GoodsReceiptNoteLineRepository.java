package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.GoodsReceiptNoteLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceiptNoteLineRepository extends JpaRepository<GoodsReceiptNoteLine, Long> {

    List<GoodsReceiptNoteLine> findByGrnId(Long grnId);
}

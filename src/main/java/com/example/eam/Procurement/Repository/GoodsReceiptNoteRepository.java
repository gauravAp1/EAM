package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.GoodsReceiptNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceiptNoteRepository extends JpaRepository<GoodsReceiptNote, Long> {

    boolean existsByPoId(Long poId);

    List<GoodsReceiptNote> findByPoId(Long poId);

    List<GoodsReceiptNote> findByDayKeyUtcBetween(String fromDayKeyInclusive, String toDayKeyInclusive);

    List<GoodsReceiptNote> findByDayKeyUtcStartingWith(String prefix);
}

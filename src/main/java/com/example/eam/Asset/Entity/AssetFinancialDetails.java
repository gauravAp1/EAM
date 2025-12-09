package com.example.eam.Asset.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.eam.Enum.DepreciationMethod;

@Entity
@Table(name = "asset_financial_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetFinancialDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false, unique = true)
    private Asset asset;

    @Column(name = "acquisition_date", nullable = false)
    private LocalDate acquisitionDate;

    @Column(name = "acquisition_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal acquisitionCost;

    @Column(name = "supplier", length = 255)
    private String supplier;

    @Column(name = "po_invoice_number", length = 128)
    private String poInvoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", length = 32)
    private DepreciationMethod depreciationMethod;

    @Column(name = "useful_life_years")
    private Integer usefulLifeYears;

    @Column(name = "depreciation_start_date")
    private LocalDate depreciationStartDate;

    @Column(name = "salvage_value", precision = 19, scale = 4)
    private BigDecimal salvageValue;

    @Column(name = "accumulated_depreciation", precision = 19, scale = 4)
    private BigDecimal accumulatedDepreciation;

    @Column(name = "current_book_value", precision = 19, scale = 4)
    private BigDecimal currentBookValue;
}

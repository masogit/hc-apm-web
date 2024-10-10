package com.ge.apm.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AssetOutStockDto {


    private Integer qty;

    private BigDecimal price;

    private BigDecimal total;

    private Integer stockedDeptId;
    private String stockedDeptUid;
    private String stockedDeptName;

    private String unit;

    private Date outStocDate;

    private String outStockNo;

    private String recruiter;

}

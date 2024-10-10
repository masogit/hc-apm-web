package com.ge.apm.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepreciationResultData {

    private BigDecimal depreciationEquipment;  //设备折旧

    private Integer assetId;

    private String date;

}

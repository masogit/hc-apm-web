package com.ge.apm.domain.pure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SimplePartsItemValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申领的时候根据备件ID得到库存数量
     */
    private int inStockNum;

    /**
     * 每个申领单根据相应的库存所计算出来的预估单价
     */
    private BigDecimal estimatedUnitPrice;



}

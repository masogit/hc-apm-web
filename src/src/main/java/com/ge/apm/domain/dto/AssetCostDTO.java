package com.ge.apm.domain.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetCostDTO {
    private String qrCode;
    private String assetUid;
    private BigDecimal amount;
    private BigDecimal checkNumberPeople;  //检查人次

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCheckNumberPeople() {
        return checkNumberPeople;
    }

    public void setCheckNumberPeople(BigDecimal checkNumberPeople) {
        this.checkNumberPeople = checkNumberPeople;
    }
}

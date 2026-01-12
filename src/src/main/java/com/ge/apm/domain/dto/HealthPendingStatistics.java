package com.ge.apm.domain.dto;

public class HealthPendingStatistics {
    Integer countAssetsToMeter = 0;
    Integer countOpenIssues = 0;
    Integer countOutOfWarranty = 0;
    Integer countPmLv2 = 0;
    Integer countPmLv3 = 0;

    public Integer getCountAssetsToMeter() {
        return countAssetsToMeter;
    }

    public void setCountAssetsToMeter(Integer countAssetsToMeter) {
        this.countAssetsToMeter = countAssetsToMeter;
    }

    public Integer getCountOpenIssues() {
        return countOpenIssues;
    }

    public void setCountOpenIssues(Integer countOpenIssues) {
        this.countOpenIssues = countOpenIssues;
    }

    public Integer getCountOutOfWarranty() {
        return countOutOfWarranty;
    }

    public void setCountOutOfWarranty(Integer countOutOfWarranty) {
        this.countOutOfWarranty = countOutOfWarranty;
    }

    public Integer getCountPmLv2() {
        return countPmLv2;
    }

    public void setCountPmLv2(Integer countPmLv2) {
        this.countPmLv2 = countPmLv2;
    }

    public Integer getCountPmLv3() {
        return countPmLv3;
    }

    public void setCountPmLv3(Integer countPmLv3) {
        this.countPmLv3 = countPmLv3;
    }
}

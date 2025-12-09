package com.ge.apm.service.analysis;

public class FieldFillRateDTO {

    private long totalCount;
    private long filledCount;
    private double fillRate;

    public FieldFillRateDTO(long totalCount, long filledCount, double fillRate) {
        this.totalCount = totalCount;
        this.filledCount = filledCount;
        this.fillRate = fillRate;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getFilledCount() {
        return filledCount;
    }

    public double getFillRate() {
        return fillRate;
    }
}

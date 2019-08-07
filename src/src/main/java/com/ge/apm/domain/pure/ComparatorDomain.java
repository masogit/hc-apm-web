package com.ge.apm.domain.pure;

public class ComparatorDomain {

    private Integer idx;
    private String value;

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ComparatorDomain(Integer idx, String value) {
        this.idx = idx;
        this.value = value;
    }
}

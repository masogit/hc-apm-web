package com.ge.apm.domain;

import java.util.Objects;

public class Progress {
    private Integer value = 0;

    private Integer total;

    private Integer complete=0;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete() {
        ++this.complete;
        if (Objects.nonNull(total)) {
            value = 100*complete / total;
        }
    }
}

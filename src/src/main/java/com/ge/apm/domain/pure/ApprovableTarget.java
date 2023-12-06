package com.ge.apm.domain.pure;

import java.math.BigDecimal;

public interface ApprovableTarget {

    BigDecimal getTotalPriceToBigDecimal();

    String getBusinessNumber();

    String getBusinessUid();

    int getBusinessType();

    default String getBusinessDigest() {
        return null;
    }

}

package com.ge.apm.domain.pure;

import java.math.BigDecimal;

public interface ApprovableTarget {

    BigDecimal getTotalPrice();

    String getBusinessNumber();

    int getBusinessType();
}

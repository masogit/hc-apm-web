package com.ge.apm.web;

import lombok.Getter;
import lombok.Setter;

public class DepTranForTrans {

    @Getter
    @Setter
    Integer userId;
    @Getter @Setter
    String startTime;
    @Getter @Setter
    String endTime;

    @Getter @Setter
    Integer pageNumber;
    @Getter @Setter
    Integer pageSize;

}

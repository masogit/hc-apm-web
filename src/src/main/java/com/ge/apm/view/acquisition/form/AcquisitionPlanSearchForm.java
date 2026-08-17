package com.ge.apm.view.acquisition.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AcquisitionPlanSearchForm {


    private String applicationHospitalName;

    private String assetName;

    private String applicationType;

    private BigDecimal budgetTotalAmountBegin;
    private BigDecimal budgetTotalAmountEnd;


}

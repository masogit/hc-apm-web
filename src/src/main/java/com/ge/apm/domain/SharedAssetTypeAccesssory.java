/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

/**
 *
 * @author caoyong
 */
public class SharedAssetTypeAccesssory{
    
    private String name;//附件名称
    private Boolean borrowStatus;
    private Boolean returnStatus;
    public String getName() {
        if(name==null)
            return "";
        else
            return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the borrowStatus
     */
    public Boolean getBorrowStatus() {
        return borrowStatus;
    }

    /**
     * @param borrowStatus the borrowStatus to set
     */
    public void setBorrowStatus(Boolean borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    /**
     * @return the returnStatus
     */
    public Boolean getReturnStatus() {
        return returnStatus;
    }

    /**
     * @param returnStatus the returnStatus to set
     */
    public void setReturnStatus(Boolean returnStatus) {
        this.returnStatus = returnStatus;
    }
}

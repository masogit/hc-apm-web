package com.ge.apm.domain;

/**
 * @author : 503189362
 * @date : 2021/6/28
 * @description :
 */
public class SvcEngineLogisticsOrderDetailVO {

    private static final long serialVersionUID = 1L;

    public SvcEngineLogisticsOrderDetailVO(String _awb,String _hyperlink,String _carrier,String _carrierChinese){
        this.awb = _awb;
        this.hyperlink = _hyperlink;
        this.carrier = _carrier;
        this.carrierChinese = _carrierChinese;
    }

    private String awb;

    private String hyperlink;

    private String carrier;

    private String carrierChinese;

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCarrierChinese() {
        return carrierChinese;
    }

    public void setCarrierChinese(String carrierChinese) {
        this.carrierChinese = carrierChinese;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

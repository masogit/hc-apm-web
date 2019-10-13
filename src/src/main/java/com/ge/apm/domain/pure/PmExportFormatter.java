package com.ge.apm.domain.pure;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.web.ExportFields;
import webapp.framework.web.WebUtil;

import java.util.*;

public class PmExportFormatter<T> {
    private String assetName;
    private String serialNUm;
    private String assetNumber;
    private String ruleLevel;

    private String pmType;
    private String clincialName;
    private String creator;
    private String owner;
    private String pmNum;
    private String currentStep;
    private String orderType;
    private Date planTime;
    private OrgInfoRepository orgDao;
    private List<I18nMessage> i18PmtypeList=null;
    private List<I18nMessage> i18OrderTypeList=null;
    private V2_PmOrder v2_pmOrder;
    private String type = null;
    public String getCreator() {
        return v2_pmOrder.getCreatorName();
    }

    public void setCreator(String creator) {
        this.creator = v2_pmOrder.getCreatorName();
    }
    I18nMessageRepository I18dao = null;

    public PmExportFormatter(V2_PmOrder v2_pmOrder) {
        this.v2_pmOrder = v2_pmOrder;
        I18dao = WebUtil.getBean(I18nMessageRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        i18PmtypeList =I18dao.getByMsgType("pmType");
        i18OrderTypeList  =I18dao.getByMsgType("intExtType");
    }

    public String getFromI18(List<I18nMessage> msgList, Object tmpType ){
        msgList.forEach(item->{
            if(item.getMsgKey().equalsIgnoreCase(tmpType.toString())){
                type =item.getValueZh();
            }
        });
        return type;
    }

    public Map<String, Object> buildExcelCoxlumnKeyValueMap() {

        Map<String, Object> map = new HashMap<>();
        map.put(ExportFields.ASSETNAME.getChinese(), this.getAssetName());
        map.put(ExportFields.SERIALNUM.getChinese(), this.getSerialNUm());
        map.put(ExportFields.RUNLEVEL.getChinese(), this.getRuleLevel());
        map.put(ExportFields.PMTYPE.getChinese(), this.getFromI18(i18PmtypeList,v2_pmOrder.getPmType()));
        map.put(ExportFields.CLINICALNAME.getChinese(), this.getClincialName());
        map.put(ExportFields.CREATOR.getChinese(), this.getCreator());
        map.put(ExportFields.OWNER.getChinese(), this.getOwner());
        map.put(ExportFields.PMNUM.getChinese(), this.getPmNum());
        map.put(ExportFields.CURRENTSTEP.getChinese(), this.getCurrentStep());
        map.put(ExportFields.ORDERTYPE.getChinese(), this.getFromI18(i18OrderTypeList,v2_pmOrder.getIntExtType()));
        map.put(ExportFields.PLANTIME.getChinese(), this.getPlanTime());
        return map;
    }

    public String getClincialName() {

        return orgDao.getByUid(v2_pmOrder.getClinicalDeptId()).getName();
    }


    public String getOwner() {
        return v2_pmOrder.getOwnerName();
    }

    public void setOwner(String owner) {
        this.owner = v2_pmOrder.getOwnerName();
    }

    public String getPmNum() {
        return v2_pmOrder.getPmNum();
    }

    public void setPmNum(String pmNum) {
        this.pmNum = v2_pmOrder.getPmNum();
    }

    public String getCurrentStep() {
        return v2_pmOrder.getCurrentStepName();
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = v2_pmOrder.getCurrentStepName();;
    }



   /* public void setOrderType(String orderType) {
        this.orderType = v2_pmOrder.getIntExtType().toString();
    }*/
   public String getRuleLevel() {
       return v2_pmOrder.getRuleLevel().toString();
   }

    public void setRuleLevel(String ruleLevel) {
        this.ruleLevel = v2_pmOrder.getRuleLevel().toString();
    }

    public String getAssetName() {
        return v2_pmOrder.getAssetInfo().getName();
    }

    public void setAssetName(String assetName) {
        this.assetName = v2_pmOrder.getAssetName();
    }

    public String getAssetNumber() {
        return v2_pmOrder.getAssetInfo().getDepartNum();
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }


    public String getSerialNUm() {
        return v2_pmOrder.getAssetInfo().getSerialNum();
    }


    public Date getPlanTime() {
        return v2_pmOrder.getPlanTime();
    }

    public void setPlanTime(Date planTime) {
        this.planTime = v2_pmOrder.getPlanTime();
    }
 /* public String getPmType() {
        i18PmtypeList.forEach(item->{
                    if(item.getMsgKey().equalsIgnoreCase(v2_pmOrder.getPmType().toString())){
                         pmType =item.getValueZh();
                    }
                });
                return pmType;
    }
    public String getOrderType() {
        i18OrderTypeList.forEach(item->{
            if(item.getMsgKey().equalsIgnoreCase(v2_pmOrder.getIntExtType().toString())){
                orderType =item.getValueZh();
            }
        });
        return orderType;

    }*/

}

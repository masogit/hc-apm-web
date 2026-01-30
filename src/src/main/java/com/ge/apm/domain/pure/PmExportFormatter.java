package com.ge.apm.domain.pure;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.pm.V2PmOrderController;
import com.ge.apm.view.sysutil.UserContextService;
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
    private Date actualCompletionTime;
    private OrgInfoRepository orgDao;
    private List<I18nMessage> i18PmtypeList=null;
    private List<I18nMessage> i18OrderTypeList=null;
    private List<I18nMessage> i18nRuleLevel=null;
    private List<I18nMessage> i18nAssetGroup=null;
    private V2_PmOrder v2_pmOrder;
    private String type = null;

    private V2PmOrderController v2PmOrderController;
    private PmOrderRule pmOrderRule;
    public String getCreator() {
        return v2_pmOrder.getCreatorName();
    }

    public void setCreator(String creator) {
        this.creator = v2_pmOrder.getCreatorName();
    }
    I18nMessageRepository I18dao = null;
    I18nMessageService i18nMessageService;

    public PmExportFormatter(V2_PmOrder v2_pmOrder) {
        this.v2_pmOrder = v2_pmOrder;
        I18dao = WebUtil.getBean(I18nMessageRepository.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        i18PmtypeList =I18dao.getByMsgType("pmType");
        i18OrderTypeList  =I18dao.getByMsgType("intExtType");
        i18nRuleLevel  =I18dao.getByMsgType("ruleLevelFour");
        i18nAssetGroup  = i18nMessageService.getByMsgType("assetGroup");
        v2PmOrderController = WebUtil.getBean(V2PmOrderController.class);
        pmOrderRule = v2PmOrderController.getRulePm(v2_pmOrder);
    }

    public String getFromI18(List<I18nMessage> msgList, Object tmpType ){
        msgList.forEach(item->{
            if(item.getMsgKey().equalsIgnoreCase(tmpType.toString())){
                type =item.getValueZh();
            }
        });
        return type;
    }

    public Map<String, Object> buildExcelColumnKeyValueMap(int queryIndex) {

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        Map<String, Object> map = new HashMap<>();
        if(userContextService.isLocale()){
            map.put(ExportFields.SITENAME.getChinese(), this.getSiteName());

            map.put(ExportFields.ASSETNAME.getChinese(), this.getAssetName());
            map.put(ExportFields.SERIALNUM.getChinese(), this.getSerialNUm());
            map.put(ExportFields.ASSETNUM.getChinese(), this.getAssetNumber());
            map.put(ExportFields.FUNCTIONTYPE.getChinese(), this.getFunctionType());
            map.put(ExportFields.ASSETGROUP.getChinese(),this.getFromI18(i18nAssetGroup,v2_pmOrder.getAssetInfo().getAssetGroup()));
//            if("1".equals(this.getRuleLevel())){
//                map.put(ExportFields.RUNLEVEL.getChinese(), "巡检");
//            }else if("4".equals(this.getRuleLevel())){
//                map.put(ExportFields.RUNLEVEL.getChinese(), "质量控制");
//            }else {
//                map.put(ExportFields.RUNLEVEL.getChinese(), this.getRuleLevel());
//            }
            map.put(ExportFields.RUNLEVEL.getChinese(), this.getFromI18(i18nRuleLevel,v2_pmOrder.getRuleLevel()));
            map.put(ExportFields.PMTYPE.getChinese(), this.getFromI18(i18PmtypeList,v2_pmOrder.getPmType()));
            map.put(ExportFields.CLINICALNAME.getChinese(), this.getClincialName());
            map.put(ExportFields.PMRULE.getChinese(), this.getRuleName());
            map.put(ExportFields.MAINTENANCEPERIOD.getChinese(), this.getPeriod());
            map.put(ExportFields.CREATOR.getChinese(), this.getCreator());
            map.put(ExportFields.OWNER.getChinese(), this.getOwner());
            map.put(ExportFields.PMNUM.getChinese(), this.getPmNum());
            map.put(ExportFields.CURRENTSTEP.getChinese(), this.getCurrentStep());
            map.put(ExportFields.ORDERTYPE.getChinese(), this.getFromI18(i18OrderTypeList,v2_pmOrder.getIntExtType()));
            map.put(ExportFields.ORDERCOMMENTS.getChinese(), this.getComments());
            map.put(ExportFields.PLANTIME.getChinese(), this.getPlanTime());
            map.put(ExportFields.TRAVELTIME.getChinese(), this.getTravelTime());
            map.put(ExportFields.ENDTIME.getChinese(), v2_pmOrder.getEndTime());
            if(queryIndex == 7){
                map.put(ExportFields.ACTUALCOMPLETIONTIME.getChinese(), this.getActualCompletionTime());
            }

        }else {
            map.put(ExportFields.SITENAME.getEnglish(), this.getSiteName());

            map.put(ExportFields.ASSETNAME.getEnglish(), this.getAssetName());
            map.put(ExportFields.SERIALNUM.getEnglish(), this.getSerialNUm());
            map.put(ExportFields.ASSETNUM.getEnglish(), this.getAssetNumber());
            map.put(ExportFields.FUNCTIONTYPE.getEnglish(), this.getFunctionType());
            if("1".equals(this.getRuleLevel())){
                map.put(ExportFields.RUNLEVEL.getEnglish(), "巡检");
            }else if("4".equals(this.getRuleLevel())){
                map.put(ExportFields.RUNLEVEL.getEnglish(), "质量控制");
            }else {
                map.put(ExportFields.RUNLEVEL.getEnglish(), this.getRuleLevel());
            }
            map.put(ExportFields.ASSETGROUP.getEnglish(),this.getFromI18(i18nAssetGroup,v2_pmOrder.getAssetInfo().getAssetGroup()));

            map.put(ExportFields.PMTYPE.getEnglish(), this.getFromI18(i18PmtypeList,v2_pmOrder.getPmType()));
            map.put(ExportFields.CLINICALNAME.getEnglish(), this.getClincialName());
            map.put(ExportFields.PMRULE.getEnglish(), this.getRuleName());
            map.put(ExportFields.MAINTENANCEPERIOD.getEnglish(), this.getPeriod());
            map.put(ExportFields.CREATOR.getEnglish(), this.getCreator());
            map.put(ExportFields.OWNER.getEnglish(), this.getOwner());
            map.put(ExportFields.PMNUM.getEnglish(), this.getPmNum());
            map.put(ExportFields.CURRENTSTEP.getEnglish(), this.getCurrentStep());
            map.put(ExportFields.ORDERTYPE.getEnglish(), this.getFromI18(i18OrderTypeList,v2_pmOrder.getIntExtType()));
            map.put(ExportFields.ORDERCOMMENTS.getEnglish(), this.getComments());
            map.put(ExportFields.PLANTIME.getEnglish(), this.getPlanTime());
            map.put(ExportFields.TRAVELTIME.getEnglish(), this.getTravelTime());
            if(queryIndex == 7){
                map.put(ExportFields.ACTUALCOMPLETIONTIME.getEnglish(), this.getActualCompletionTime());
            }
            map.put(ExportFields.ENDTIME.getEnglish(), v2_pmOrder.getEndTime());
        }
        return map;
    }


    public String getRuleName() {
        return v2_pmOrder.getRuleName();
    }

    public String getPeriod() {
        return Optional.ofNullable(v2_pmOrder)
                .map(order -> order.getPeriod())
                .map(period -> period.toString())
                .orElse(null); // 或者返回一个默认值，如空字符串 ""
    }
    public String getClincialName() {
        OrgInfo orgInfo = Optional.ofNullable(v2_pmOrder)
                .map(V2_PmOrder::getAssetInfo)
                .map(AssetInfo::getClinicalDeptUID)
                .map(orgDao::getByUid)
                .orElse(null);
        if (orgInfo==null){
            return null;
        }
        return orgInfo.getName();
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

    public String getComments() {
        return v2_pmOrder.getComments();
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


    public String getSiteName() {
       if(v2_pmOrder.getSiteOrg()!=null){
           return v2_pmOrder.getSiteOrg().getName();
       }
       return null;
    }

    public void setAssetName(String assetName) {
        this.assetName = v2_pmOrder.getAssetName();
    }

    public String getAssetNumber() {
        return v2_pmOrder.getAssetInfo().getDepartNum();
    }
    public String getFunctionType() {
        return v2_pmOrder.getAssetInfo().getFunctionType();
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
    public Date getActualCompletionTime() {
        return v2_pmOrder.getEndTime();
    }

    public void setActualCompletionTime(Date actualCompletionTime) {
        this.actualCompletionTime = actualCompletionTime;
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


    public Double getTravelTime() {
        return v2_pmOrder.getTravelTime();
    }


}

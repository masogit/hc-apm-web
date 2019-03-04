package com.ge.apm.view.asset;

import com.ge.apm.domain.OwnerConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.OwnerConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2019/2/25.
 */
@ManagedBean
@ViewScoped
public class DepartmentOwnerSettingController {

    private UserCommonService userCommonService;
    private OwnerConfigService ownerConfigService;

    private List<UserAccount> userAccountList;

    private String clinicalDeptUID;

    private UserAccount woOwner;
    private UserAccount woOwner2;
    private UserAccount woOwner3;
    private UserAccount pmOwner;
    private Boolean isChanegeWoOwner;
    private Boolean isChanegeWoOwner2;
    private Boolean isChanegeWoOwner3;
    private Boolean isChanegePmOwner;

    private String woOwnerConfigID;
    private String woOwnerConfigID2;
    private String woOwnerConfigID3;
    private String pmOwnerConfigID;

    private UserAccount currentUser;

    @PostConstruct
    protected void init() {

        userCommonService = WebUtil.getBean(UserCommonService.class);
        ownerConfigService = WebUtil.getBean(OwnerConfigService.class);

        userAccountList = userCommonService.getWorkerList(null,"");
        currentUser = UserContextService.getCurrentUserAccount();

    }

    public List<UserAccount> autoCompleteOwner(String query) {

        if (userAccountList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = userAccountList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void loadOwnerConfig(){

        this.resetOwnerConfig();

        List<OwnerConfig> ownerConfigList = ownerConfigService.loadDepartmentOwnerConfig(clinicalDeptUID, currentUser.getSiteUID());

        for(OwnerConfig ownerConfig : ownerConfigList){
            if(OwnerConfig.OwnerType.WO.name().equals(ownerConfig.getOwnerType())){
                if(1 == ownerConfig.getIndex()){
                    isChanegeWoOwner = Boolean.TRUE;
                    woOwner = ownerConfig.getOwner();
                    woOwnerConfigID = ownerConfig.getId();
                }else if(2 == ownerConfig.getIndex()){
                    isChanegeWoOwner2 = Boolean.TRUE;
                    woOwner2 = ownerConfig.getOwner();
                    woOwnerConfigID2 = ownerConfig.getId();
                }else if(3 == ownerConfig.getIndex()){
                    isChanegeWoOwner3 = Boolean.TRUE;
                    woOwner3 = ownerConfig.getOwner();
                    woOwnerConfigID3 = ownerConfig.getId();
                }
            }else if(OwnerConfig.OwnerType.PM.name().equals(ownerConfig.getOwnerType())){
                if(1 == ownerConfig.getIndex()){
                    isChanegePmOwner = Boolean.TRUE;
                    pmOwner = ownerConfig.getOwner();
                    pmOwnerConfigID = ownerConfig.getId();
                }
            }
        }

    }

    private void resetOwnerConfig(){
        woOwner = null;
        woOwner2 = null;
        woOwner3 = null;
        pmOwner = null;

        woOwnerConfigID = null;
        woOwnerConfigID2 = null;
        woOwnerConfigID3 = null;
        pmOwnerConfigID = null;

        isChanegeWoOwner = Boolean.FALSE;
        isChanegeWoOwner2 = Boolean.FALSE;
        isChanegeWoOwner3 = Boolean.FALSE;
        isChanegePmOwner = Boolean.FALSE;
    }

    public void saveChange() {

        if(!isChanegeWoOwner || !isChanegeWoOwner2 || !isChanegeWoOwner3 || isChanegePmOwner){
            WebUtil.addErrorMessage("请设置维修负责人或保养负责人");
        }

        List<OwnerConfig> ownerConfigList = new ArrayList<>();

        if(isChanegeWoOwner){
            ownerConfigList.add(buildOwnerConfig(1, OwnerConfig.OwnerType.WO.name(), woOwner, woOwnerConfigID));
        }
        if(isChanegeWoOwner2){
            ownerConfigList.add(buildOwnerConfig(2, OwnerConfig.OwnerType.WO.name(), woOwner2, woOwnerConfigID2));
        }
        if(isChanegeWoOwner3){
            ownerConfigList.add(buildOwnerConfig(3, OwnerConfig.OwnerType.WO.name(), woOwner3, woOwnerConfigID3));
        }
        if(isChanegePmOwner){
            ownerConfigList.add(buildOwnerConfig(1, OwnerConfig.OwnerType.PM.name(), pmOwner, pmOwnerConfigID));
        }

        ownerConfigService.saveDepartmentOwner(ownerConfigList);
    }

    private OwnerConfig buildOwnerConfig(Integer index, String ownerType, UserAccount owner, String ownerConfigID){
        OwnerConfig ownerConfig = new OwnerConfig();

        if(ownerConfigID != null){
            ownerConfig.setId(ownerConfigID);
        }

        ownerConfig.setTenantUID(currentUser.getTenantUID());
        ownerConfig.setInstitutionUID(currentUser.getInstitutionUID());
        ownerConfig.setHospitalUID(currentUser.getHospitalUID());
        ownerConfig.setSiteUID(currentUser.getSiteUID());

        ownerConfig.setOwnerUID(owner != null ? owner.getUid() : null);
        ownerConfig.setIndex(index);
        ownerConfig.setOwnerType(ownerType);
        ownerConfig.setOwnerConfigType(OwnerConfig.OwnerConfigType.Department.name());
        ownerConfig.setTypeUID(clinicalDeptUID);

        ownerConfig.setCreatedBy(currentUser.getLoginName());
        ownerConfig.setCreatedDate(new Date());
        ownerConfig.setLastModifiedBy(currentUser.getLoginName());
        ownerConfig.setLastModifiedDate(new Date());

        return ownerConfig;
    }

    public void onCheckChange(){
    }

    public void onOwnerChange(SelectEvent event){

    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public UserAccount getWoOwner() {
        return woOwner;
    }

    public void setWoOwner(UserAccount woOwner) {
        this.woOwner = woOwner;
    }

    public UserAccount getWoOwner2() {
        return woOwner2;
    }

    public void setWoOwner2(UserAccount woOwner2) {
        this.woOwner2 = woOwner2;
    }

    public UserAccount getWoOwner3() {
        return woOwner3;
    }

    public void setWoOwner3(UserAccount woOwner3) {
        this.woOwner3 = woOwner3;
    }

    public UserAccount getPmOwner() {
        return pmOwner;
    }

    public void setPmOwner(UserAccount pmOwner) {
        this.pmOwner = pmOwner;
    }

    public Boolean getChanegeWoOwner() {
        return isChanegeWoOwner;
    }

    public void setChanegeWoOwner(Boolean chanegeWoOwner) {
        isChanegeWoOwner = chanegeWoOwner;
    }

    public Boolean getChanegeWoOwner2() {
        return isChanegeWoOwner2;
    }

    public void setChanegeWoOwner2(Boolean chanegeWoOwner2) {
        isChanegeWoOwner2 = chanegeWoOwner2;
    }

    public Boolean getChanegeWoOwner3() {
        return isChanegeWoOwner3;
    }

    public void setChanegeWoOwner3(Boolean chanegeWoOwner3) {
        isChanegeWoOwner3 = chanegeWoOwner3;
    }

    public Boolean getChanegePmOwner() {
        return isChanegePmOwner;
    }

    public void setChanegePmOwner(Boolean chanegePmOwner) {
        isChanegePmOwner = chanegePmOwner;
    }
}

package com.ge.apm.view;

import com.ge.apm.dao.SharedAssetConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.SharedAssetConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 212605082 on 2018/7/12.
 */
@ManagedBean
@ViewScoped
public class SharedAssetConfigController {

    private SharedAssetConfigRepository dao;
    private UserAccountRepository userAccountDao;

    private UserAccount currentUser;

    private SharedAssetConfig sharedAssetConfig;

    private List<UserAccount> userList;

    private Date startTime;
    private Date endTime;

    @PostConstruct
    protected void init() {

        dao = WebUtil.getBean(SharedAssetConfigRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();

        userList = this.loadUserList();

        this.loadSharedAssetConfig();
    }

    public void saveSharedAssetConfig() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sharedAssetConfig.setStartTime(sdf.format(startTime).split(" ")[1]);
        sharedAssetConfig.setEndTime(sdf.format(endTime).split(" ")[1]);

        if(sharedAssetConfig.getId() == null){
            sharedAssetConfig.setTenantUID(currentUser.getTenantUID());
            sharedAssetConfig.setInstitutionUID(currentUser.getInstitutionUID());
            sharedAssetConfig.setHospitalUID(currentUser.getHospitalUID());
            sharedAssetConfig.setSiteUID(currentUser.getSiteUID());
        }

        dao.save(sharedAssetConfig);
    }

    public void loadSharedAssetConfig() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());

        try {
            List<SharedAssetConfig> sacList = dao.findBySiteUID(currentUser.getSiteUID());
            if(!sacList.isEmpty()) {
                sharedAssetConfig = sacList.get(0);

                this.startTime = sharedAssetConfig.getStartTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + sharedAssetConfig.getStartTime()) : null;
                this.endTime = sharedAssetConfig.getEndTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + sharedAssetConfig.getEndTime()) : null;
            }else {
                sharedAssetConfig = new SharedAssetConfig();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<UserAccount> loadUserList(){

        List<UserAccount> userAccountList = new ArrayList<>();
        if(currentUser.getOrgLevel() == 0){
            userAccountList = userAccountDao.findByTenantUIDAndStaffRole(currentUser.getTenantUID());
        }else if(currentUser.getOrgLevel() == 1){
            userAccountList = userAccountDao.findByInstitutionUIDAndStaffRole(currentUser.getInstitutionUID());
        }else if(currentUser.getOrgLevel() == 2){
            userAccountList = userAccountDao.findByHospitalUIDAndStaffRole(currentUser.getHospitalUID());
        }else if(currentUser.getOrgLevel() == 3){
            userAccountList = userAccountDao.findBySiteUIDAndStaffRole(currentUser.getSiteUID());
        }

        return userAccountList;
    }

    public SharedAssetConfig getSharedAssetConfig() {
        return sharedAssetConfig;
    }

    public void setSharedAssetConfig(SharedAssetConfig sharedAssetConfig) {
        this.sharedAssetConfig = sharedAssetConfig;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<UserAccount> getUserList() {
        return userList;
    }

    public void setUserList(List<UserAccount> userList) {
        this.userList = userList;
    }
}

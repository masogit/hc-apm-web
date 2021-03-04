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
import java.util.*;
import java.util.stream.Collectors;

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

        //先删除已经保存的user
        dao.deleteBySiteUid(currentUser.getSiteUID());
        //再重新保存新的user
        sharedAssetConfig.getOwners().stream().forEach(user -> {
            SharedAssetConfig config = new SharedAssetConfig();
            config.setOwner(user.getUid());
            config.setStartTime(sdf.format(startTime).split(" ")[1]);
            config.setEndTime(sdf.format(endTime).split(" ")[1]);
            config.setTel(sharedAssetConfig.getTel());
            config.setTenantUID(currentUser.getTenantUID());
            config.setInstitutionUID(currentUser.getInstitutionUID());
            config.setHospitalUID(currentUser.getHospitalUID());
            config.setSiteUID(currentUser.getSiteUID());
            dao.save(config);
        });
    }

    //置空配置
    public void resetConfig(){
        //删除已经保存配置
        dao.deleteBySiteUid(currentUser.getSiteUID());
        loadSharedAssetConfig();
    }

    public void loadSharedAssetConfig() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());

        try {
            List<SharedAssetConfig> sacList = dao.findBySiteUID(currentUser.getSiteUID());
            if(!sacList.isEmpty()) {
                sharedAssetConfig = sacList.get(0);
                sharedAssetConfig.setOwners(sacList.stream().map(config -> userAccountDao.getByUid(config.getOwner())).filter(con->userList.contains(con)).collect(Collectors.toList()));
                this.startTime = sharedAssetConfig.getStartTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + sharedAssetConfig.getStartTime()) : null;
                this.endTime = sharedAssetConfig.getEndTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + sharedAssetConfig.getEndTime()) : null;
            }else {
                sharedAssetConfig = new SharedAssetConfig();
                this.startTime = null;
                this.endTime = null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<UserAccount> loadUserList(){

        List<UserAccount> userAccountList = new ArrayList<>();
        userAccountList = userAccountDao.findByTenantUIDAndRoleName(currentUser.getTenantUID(), "SharedAssetMgmt");

        if(!userAccountList.isEmpty()){
            if(currentUser.getOrgLevel() != 0){
                if(currentUser.getOrgLevel() == 1){
                    userAccountList = userAccountList.stream()
                            .filter(item-> currentUser.getInstitutionUID().equals(item.getInstitutionUID()))
                            .collect(Collectors.toList());
                }else if(currentUser.getOrgLevel() == 2){
                    userAccountList = userAccountList.stream()
                            .filter(item-> currentUser.getHospitalUID().equals(item.getHospitalUID()))
                            .collect(Collectors.toList());
                }else{
                    userAccountList = userAccountList.stream()
                            .filter(item-> currentUser.getSiteUID().equals(item.getSiteUID()))
                            .collect(Collectors.toList());
                }
            }
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

package com.ge.apm.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.apm.domain.SysRole;
import com.ge.apm.service.uaa.AdminConsoleService;
import com.ge.apm.service.uaa.TenantInfoConfigService;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;
@ManagedBean
@ViewScoped
public class AdminConsoleController {

	@Getter
	@Setter
    private String versionName;
	@Getter
	@Setter
	private TenantInfoConfigService tenantConfigService;
	@Getter
	@Setter
	private List<String> versionLabelKey;
	@Getter
	@Setter
	private String labelKey;
	@Getter
	@Setter
	private String containsLabelKey;
	@Getter
	@Setter
	private Integer opType;//0:add,1:delete
	@Getter
	@Setter
	private String sysRoleName;
	@Getter
	@Setter
	private List<String> listSysRoleName;
	@Getter
	@Setter
	private Date selectDate;
	@Autowired
	private AdminConsoleService adminConsoleService;
	private SysRole currentSysRole;
	private List<SysRole> listSysRole;
	@PostConstruct
	public void init() {
		// TODO Auto-generated method stub
		tenantConfigService=WebUtil.getBean(TenantInfoConfigService.class);
		tenantConfigService.init();
		formatVersionInfo();
		adminConsoleService=WebUtil.getBean(AdminConsoleService.class);
		listSysRole=adminConsoleService.getSysRole();
		currentSysRole=listSysRole.get(0);
		listSysRoleName=new ArrayList<String>();
		for(int i=0;i<listSysRole.size();++i){
			SysRole tempRole=listSysRole.get(i);
			listSysRoleName.add(tempRole.getName()+"_"+tempRole.getRoleDesc()+"_"+tempRole.getId());
		}
		sysRoleName=currentSysRole.getName()+"_"+currentSysRole.getRoleDesc()+"_"+currentSysRole.getId();
		sysRoleChange();
	}
	public void sysRoleChange(){
		String [] tempArr=sysRoleName.split("_");
		String tempName=tempArr[0];
		for(int i=0;i<listSysRole.size();++i){
			if(tempName.equals(listSysRole.get(i).getName())){
				currentSysRole=listSysRole.get(i);
				break;
			}
		}
		String menu=currentSysRole.getMenus();
		tenantConfigService.parseMenuList(menu);
	}
	public void updateSysMenu(){
		currentSysRole.setMenus(tenantConfigService.toMenusStr());
		adminConsoleService.saveSysRole(currentSysRole);
	}
	public void updateVersion(){
		String tempVersion="";
		String tempKey="";
		String tempContainsKey="";
		if(StringUtils.isNotEmpty(versionName)){
			if(opType!=null){
				if(StringUtils.isNotEmpty(labelKey)){
					tempKey=labelKey.split(",")[1];
					tempContainsKey=containsLabelKey.split(",")[1];
					tempVersion=versionName;
					if(opType==0){
						adminConsoleService.addVersion(tempVersion,tempContainsKey, tempKey);
					}
					else if(opType==1){
						adminConsoleService.deleteVersion(tempVersion,tempContainsKey, tempKey);
					}
					WebUtil.addSuccessMessage("已完成更新操作！");
				}
				else{
					WebUtil.addErrorMessage("必要参数为空，请检查！");
				}
			}
		}
	}
	public void updateNurseSiteUID(){
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adminConsoleService.updateNurseSiteUID();
			}
		});
		t.start();
		WebUtil.addSuccessMessage("数据已更新，请检查数据库！");
	}
	public void updateNurseSiteDeleteAsset(){
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adminConsoleService.updateNurseSiteDeleteAsset();
			}
		});
		t.start();
		WebUtil.addSuccessMessage("数据已更新，请检查数据库！");
	}
	public void updateNurseSiteStatus(){
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adminConsoleService.updateNurseSiteStatus();
			}
		});
		t.start();
		WebUtil.addSuccessMessage("数据已更新，请检查数据库！");
	}
	private void formatVersionInfo(){
		List<String> listKey=tenantConfigService.getListKey();
		List<String> listLabel=tenantConfigService.getListLabel();
		versionLabelKey=new ArrayList<String>();
		for(int i=0;i<listKey.size();++i){
			versionLabelKey.add(listLabel.get(i)+","+listKey.get(i));
		}
	}
	public void dailyUpdateNurseSiteJob(){
		if(selectDate==null){
			Calendar ca=Calendar.getInstance();
			selectDate=ca.getTime();
		}
		adminConsoleService.dailyUpdateNurseSiteJob(selectDate);
		WebUtil.addSuccessMessage("数据已更新，请检查数据库！");
	}
	public void deleteObjectHub(){
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adminConsoleService.deleteObjectHub();
			}
		});
		t.start();
		WebUtil.addSuccessMessage("数据已更新，请检查数据库！");
	}
}
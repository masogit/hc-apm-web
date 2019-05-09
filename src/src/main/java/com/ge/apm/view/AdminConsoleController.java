package com.ge.apm.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.apm.domain.I18nMessage;
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
	private Integer opType;//0:add,1:delete
	@Getter
	@Setter
	private String[] menus;
	@Getter
	@Setter
	private String sysRoleName;
	@Getter
	@Setter
	private List<String> listMenus;
	@Getter
	@Setter
	private List<I18nMessage> listI18nMenus;
	@Getter
	@Setter
	private List<I18nMessage> selectedMenus;
	@Getter
	@Setter
	private List<String> listSysRoleName;
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
		menus=new String[100];
		listSysRole=adminConsoleService.getSysRole();
		currentSysRole=listSysRole.get(0);
		listSysRoleName=new ArrayList<String>();
		for(int i=0;i<listSysRole.size();++i){
			SysRole tempRole=listSysRole.get(i);
			listSysRoleName.add(tempRole.getName()+"_"+tempRole.getRoleDesc()+"_"+tempRole.getId());
		}
		sysRoleName=listSysRoleName.get(0);
		listMenus=new ArrayList<String>();
		selectedMenus=new ArrayList<I18nMessage>();
		listI18nMenus = adminConsoleService.getI18Menus();
		selectedMenus.addAll(listI18nMenus);
		for(int i=0;i<listI18nMenus.size();++i){
			I18nMessage i18n=listI18nMenus.get(i);
			listMenus.add(i18n.getMsgKey()+"-"+i18n.getValue());
		}
		sysRoleChange();
	}
	public void sysRoleChange(){
		selectedMenus.clear();
		String [] tempArr=sysRoleName.split("_");
		String tempName=tempArr[0];
		for(int i=0;i<listSysRole.size();++i){
			if(tempName.equals(listSysRole.get(i).getName())){
				currentSysRole=listSysRole.get(i);
				break;
			}
		}
		String menu=currentSysRole.getMenus();
		if(StringUtils.isNotEmpty(menu)){
			menu=menu.replaceAll(" ", "");
			String [] tempArr2=menu.split(",");
			for(int i=0;i<tempArr2.length;++i){
				for(int j=0;j<listI18nMenus.size();++j){
					if(listI18nMenus.get(j).getMsgKey().equals(tempArr2[i])){
						selectedMenus.add(listI18nMenus.get(j));
						break;
					}
				}
			}
		}
	}
	public void updateSysMenu(){
		if(selectedMenus!=null && selectedMenus.size()>0){
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<selectedMenus.size();++i){
				sb.append(selectedMenus.get(i).getMsgKey()+",");
			}
			String tempStr=sb.toString();
			currentSysRole.setMenus(tempStr.substring(0,tempStr.length()-1));
		}
		adminConsoleService.saveSysRole(currentSysRole);
	}
	public void updateVersion(){
		String tempVersion="";
		String tempKey="";
		if(StringUtils.isNotEmpty(versionName)){
			if(opType!=null){
				if(StringUtils.isNotEmpty(labelKey)){
					tempKey=labelKey.split(",")[1];
					tempVersion=versionName;
					if(opType==0){
						adminConsoleService.addVersion(tempVersion, tempKey);
					}
					else if(opType==1){
						adminConsoleService.deleteVersion(tempVersion, tempKey);
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
	private void clear(){
		for(int i=0;i<menus.length;++i){
			menus[i]="";
		}
	}
	private void formatVersionInfo(){
		List<String> listKey=tenantConfigService.getListKey();
		List<String> listLabel=tenantConfigService.getListLabel();
		versionLabelKey=new ArrayList<String>();
		for(int i=0;i<listKey.size();++i){
			versionLabelKey.add(listLabel.get(i)+","+listKey.get(i));
		}
	}
}
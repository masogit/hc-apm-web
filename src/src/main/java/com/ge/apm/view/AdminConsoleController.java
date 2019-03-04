package com.ge.apm.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.domain.TenantInfoConfig;
import com.ge.apm.service.uaa.AdminConsoleService;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class AdminConsoleController extends SqlDataController {

	@Getter
	@Setter
    private String versionName;
	@Getter
	@Setter
	private TenantInfoConfig tenantConfig;
	@Getter
	@Setter
	private List<String> versionLabelKey;
	@Getter
	@Setter
	private String labelKey;
	@Getter
	@Setter
	private Integer opType;//0:add,1:delete
	
	@Autowired
	private AdminConsoleService adminConsoleService;
	@Override
	public void init() {
		// TODO Auto-generated method stub
		tenantConfig=TenantInfoConfig.build();
		formatVersionInfo();
		adminConsoleService=WebUtil.getBean(AdminConsoleService.class);
	}
	@Override
	public String getColumnNameList() {
		// TODO Auto-generated method stub
		return "";
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
	
	private void formatVersionInfo(){
		List<String> listKey=tenantConfig.getListKey();
		List<String> listLabel=tenantConfig.getListLabel();
		versionLabelKey=new ArrayList<String>();
		for(int i=0;i<listKey.size();++i){
			versionLabelKey.add(listLabel.get(i)+","+listKey.get(i));
		}
		
	}
}
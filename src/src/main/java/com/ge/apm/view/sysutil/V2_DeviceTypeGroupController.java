package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import com.ge.apm.dao.DeviceTypeRepository;
import com.ge.apm.dao.V2_DeviceTypeGroupRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_DeviceType;
import com.ge.apm.domain.V2_DeviceTypeGroup;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class V2_DeviceTypeGroupController {

	private DualListModel<Integer> availableAssets;
	private List<Integer> sourceAssets;
	private List<Integer> targetAssets;
	private V2_DeviceTypeGroupRepository dao;
	private DeviceTypeRepository deviceTypeDao;
	private Integer deviceTypeId;
	private UserAccount user;
	private List<V2_DeviceType> deviceTypeList;
	
	@PostConstruct
	protected void init() {
		dao = WebUtil.getBean(V2_DeviceTypeGroupRepository.class);
		deviceTypeDao = WebUtil.getBean(DeviceTypeRepository.class);
		user = UserContextService.getCurrentUserAccount();
		deviceTypeId = 1;//默认设备资产
		sourceAssets = new ArrayList<Integer>();
		targetAssets = new ArrayList<Integer>();
		getQueryAssetsList();
		deviceTypeList = deviceTypeDao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(user.getTenantUID(),user.getInstitutionUID(), user.getHospitalUID(), user.getSiteUID());
	}

	public void getQueryAssetsList() {
		this.sourceAssets.clear();
		this.targetAssets.clear();
		List<I18nMessage> messages = FieldValueMessageController.doGetFieldValueList("assetGroup", -1);
		List<V2_DeviceTypeGroup> sortedAllList = dao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(
				user.getTenantUID(), user.getInstitutionUID(), user.getHospitalUID(),user.getSiteUID());
		List<Integer> all = new ArrayList<Integer>();
		messages.forEach((item) -> all.add(Integer.parseInt(item.getMsgKey())));
		//1、没有配置过设备类型分组
		if(CollectionUtils.isEmpty(sortedAllList)){
			sourceAssets.addAll(all);
			availableAssets = new DualListModel<>(sourceAssets, targetAssets);
			return;
		}
		List<Integer> sortedList = new ArrayList<Integer>();
		sortedAllList.forEach((item) -> sortedList.add(item.getAssetGroup()));
		//2、没有配置某种类型的设备类型分组
		List<V2_DeviceTypeGroup> sortedTypeList = dao.findByDeviceTypeIdAndTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(
					deviceTypeId,user.getTenantUID(), user.getInstitutionUID(), user.getHospitalUID(),user.getSiteUID());
		if(CollectionUtils.isEmpty(sortedTypeList)){
			all.removeAll(sortedList);//移除已分类的设备类型
			sourceAssets.addAll(all);
			availableAssets = new DualListModel<>(sourceAssets, targetAssets);
			return;
		}
		//3、已经配置了某种类型的设备类型分组
		sortedTypeList.forEach((item) -> targetAssets.add(item.getAssetGroup()));
		all.removeAll(sortedList);
		sourceAssets.addAll(all);
		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
	}

	public void onAssetsTransfer(TransferEvent event) {
		for (Object item : event.getItems()) {
			if (event.isAdd()) {
				if(!targetAssets.contains((Integer) item)){
					targetAssets.add((Integer) item);
				}
			} else {
				targetAssets.remove((Integer) item);
			}
		}
	}

	public void saveChange() {
		if(targetAssets.isEmpty()){
			dao.deleteRecordByDeviceTypeIdAndUids(deviceTypeId,user.getTenantUID(), user.getInstitutionUID(), user.getHospitalUID(),user.getSiteUID());
		}else{
			dao.deleteRecordByDeviceTypeIdAndUids(deviceTypeId,user.getTenantUID(), user.getInstitutionUID(), user.getHospitalUID(),user.getSiteUID());
			List<V2_DeviceTypeGroup> saveList = new ArrayList<V2_DeviceTypeGroup>();
			for (Integer assetGroup : targetAssets) {
				V2_DeviceTypeGroup dtg = new V2_DeviceTypeGroup();
				dtg.setDeviceTypeId(deviceTypeId);
				dtg.setAssetGroup(assetGroup);
				dtg.setTenantUID(user.getTenantUID());
				dtg.setInstitutionUID(user.getInstitutionUID());
				dtg.setHospitalUID(user.getHospitalUID());
				dtg.setSiteUID(user.getSiteUID());
				saveList.add(dtg);
			}
			dao.save(saveList);
		}
	}
	public Integer getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(Integer deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public DualListModel<Integer> getAvailableAssets() {
		return availableAssets;
	}

	public void setAvailableAssets(DualListModel<Integer> availableAssets) {
		this.availableAssets = availableAssets;
	}

	public List<V2_DeviceType> getDeviceTypeList() {
		return deviceTypeList;
	}

	public void setDeviceTypeList(List<V2_DeviceType> deviceTypeList) {
		this.deviceTypeList = deviceTypeList;
	}

}

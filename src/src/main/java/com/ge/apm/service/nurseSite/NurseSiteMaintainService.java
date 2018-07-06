package com.ge.apm.service.nurseSite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainDataRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Component
public class NurseSiteMaintainService{

	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
	
	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;
	
	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	AssetInfoRepository assetInfoRepository;
	
	@Autowired
    private UaaService uaaService;
	
	public List<NurseSiteMaintainConfig> getMaintainConfigBySiteUid(String siteUid){
		return nurseSiteMaintainConfigRepository.findBySiteUid(siteUid);
	}
	
	public NurseSiteMaintainConfig getMaintainConfigById(String id){
		return nurseSiteMaintainConfigRepository.findById(id);
	}
	
	public List<NurseSiteMaintain> getMaintainData(Date startDate,Date endDate,String siteUid,String userUid){
		Calendar ca=Calendar.getInstance();
		ca.setTime(endDate);
		ca.set(Calendar.DATE, ca.get(Calendar.DATE)+1);
		return nurseSiteMaintainRepository.findBySiteUidAndDate(startDate,ca.getTime(),siteUid,userUid);
	}
	
	public List<NurseSiteMaintain> findBySearchFilter(List<SearchFilter> searchFilters){
		return nurseSiteMaintainRepository.findBySearchFilter(searchFilters);
	}
	
	public AssetInfo getAssetInfoByUid(String uid){
		return assetInfoRepository.getByUid(uid);
	}
	
	public List<AssetInfo> getListAsset(String siteUid,String userUid){
		return assetInfoRepository.findByAccessScope(siteUid,userUid);
	}
	
	public List<AssetInfo> getListAssetInfoByUid(List<String> assetUids){
		return assetInfoRepository.findByUidIn(assetUids);
	}
	
	public List<AssetInfo> getListAssetInfoByMaintainConfigParentId(String parentId){
		return assetInfoRepository.findAssetByMaintainConfigParentId(parentId);
	}
	
	public List<NurseSiteMaintainConfigItem> findByParentId(String parentId){
		return nurseSiteMaintainConfigItemRepository.findByParentId(parentId);
	}
	
	public boolean canAddMaintainData(Date scheduleDate,String siteUid,String userUid){
		return nurseSiteMaintainRepository.findBySiteUidAndScheduleDate(scheduleDate, siteUid, userUid)==null?true:false;
	}
	
	public void save(NurseSiteMaintain obj){
		nurseSiteMaintainRepository.save(obj);
	}
	
	public void saveMaintainItem(Set<NurseSiteMaintainItem> obj){
		nurseSiteMaintainItemRepository.save(obj);
	}
	public void saveMaintainData(Set<NurseSiteMaintainData> obj){
		nurseSiteMaintainDataRepository.save(obj);
	}
	
	public void saveNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> obj){
		nurseSiteMaintainConfigItemRepository.save(obj);
	}
	
	public void save(List<NurseSiteMaintain> obj){
		nurseSiteMaintainRepository.save(obj);
	}
	@Transactional
	public void saveAndDelete(List<NurseSiteMaintain> obj,List<NurseSiteMaintain> obj2){
		nurseSiteMaintainRepository.save(obj);
		nurseSiteMaintainRepository.delete(obj2);
	}
	
	public void save(NurseSiteMaintainConfig obj){
		nurseSiteMaintainConfigRepository.save(obj);
	}
	
	public void delete(NurseSiteMaintain obj){
		nurseSiteMaintainRepository.delete(obj);
	}
	public void deleteMaintain(List<NurseSiteMaintain> obj){
		nurseSiteMaintainRepository.delete(obj);
	}

	public void deleteNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> list){
		nurseSiteMaintainConfigItemRepository.delete(list);
	}
	
	public void delete(List<NurseSiteMaintainItem> list){
		nurseSiteMaintainItemRepository.delete(list);
	}
	
	public void delete(NurseSiteMaintainConfig obj){
		nurseSiteMaintainConfigRepository.delete(obj);
	}
	
	public List<String> getOrgListByUser(String userUid){
		return uaaService.getOrgListByUser(userUid).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
	}

	public NurseSiteMaintainRepository getNurseSiteMaintainRepository() {
		return nurseSiteMaintainRepository;
	}

	public void setNurseSiteMaintainRepository(NurseSiteMaintainRepository nurseSiteMaintainRepository) {
		this.nurseSiteMaintainRepository = nurseSiteMaintainRepository;
	}

	public NurseSiteMaintainConfigRepository getNurseSiteMaintainConfigRepository() {
		return nurseSiteMaintainConfigRepository;
	}

	public void setNurseSiteMaintainConfigRepository(NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository) {
		this.nurseSiteMaintainConfigRepository = nurseSiteMaintainConfigRepository;
	}

	public AssetInfoRepository getAssetInfoRepository() {
		return assetInfoRepository;
	}

	public void setAssetInfoRepository(AssetInfoRepository assetInfoRepository) {
		this.assetInfoRepository = assetInfoRepository;
	}
	
	public Set<NurseSiteMaintainItem> loadConfigCheckItems(NurseSiteMaintainConfig nurseSiteMaintainConfig,List<AssetInfo> listCheckAssets){
		Set<NurseSiteMaintainItem> listMaintainItem = new HashSet<NurseSiteMaintainItem>();
		String strCheckItems = nurseSiteMaintainConfig.getCheckItems();
		if (StringUtils.isNotEmpty(strCheckItems) && listCheckAssets!=null) {
			String arr2[] = strCheckItems.split(",");
			for (int i = 0; i < listCheckAssets.size(); ++i) {
				NurseSiteMaintainItem item=new NurseSiteMaintainItem();
				item.setAssetInfo(listCheckAssets.get(i));
				item.setAssetUid(listCheckAssets.get(i).getUid());
				Set<NurseSiteMaintainData> listData=new HashSet<NurseSiteMaintainData>();
				item.setListData(listData);
				for (int j = 0; j < arr2.length; ++j) {
					NurseSiteMaintainData p = new NurseSiteMaintainData();
					p.setCheckItem(arr2[j]);
					p.setCheckValue(false);
					p.setItem(item);
					listData.add(p);
				}
				listMaintainItem.add(item);
			}
		}
		return listMaintainItem;
	}
	
	private int updateMaintainData(NurseSiteMaintain obj,Boolean defaultValue){
		Set<NurseSiteMaintainItem> listItem=obj.getListNurseSiteMaintainItem();
		Iterator<NurseSiteMaintainItem> it=listItem.iterator();
		int status=1;
		while(it.hasNext()){
			NurseSiteMaintainItem tempItem=it.next();
			Set<NurseSiteMaintainData> listData=tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData=listData.iterator();
			while(itData.hasNext()){
				NurseSiteMaintainData tempData=itData.next();
				Boolean value=tempData.getCheckValue();
				if(defaultValue!=null){
					tempData.setCheckValue(defaultValue);
				}
				if(value!=null && value==false && (status==1 || status==2)){
					status=3;
					if(defaultValue==null){
						return status;
					}
				}
				if(status!=3){
					status=2;
				}
			}
		}
		return status;
	}
	
	public void updateMaintainDataStatus(NurseSiteMaintain obj,boolean batchUpdate){
		if(obj.getStatus()==null){
			obj.setStatus(0);
		}
		if(batchUpdate){
			if(obj.getStatus()==0){
				obj.setStatus(2);
				updateMaintainData(obj,true);
			}
		}
		else{
			obj.setStatus(updateMaintainData(obj, null));
		}
	}
	
	public boolean updateMaintain(NurseSiteMaintain obj,boolean batchUpdate,UserAccount currentUser){
		if(batchUpdate){
			//insert new record
			//0：未检查,1:已检查，2:通过，3有问题
			if(obj.getStatus()==null || obj.getStatus()==0){
				Date currentDate=new Date();
				obj.setCheckDate(currentDate);
				obj.setModifyBy(currentUser);
				obj.setModifyByUid(currentUser.getUid());
				obj.setModifyDate(currentDate);
			}
		}
		else{
			Date currentDate=new Date();
			obj.setStatus(1);
			obj.setCheckDate(currentDate);
			obj.setModifyBy(currentUser);
			obj.setModifyByUid(currentUser.getUid());
			obj.setModifyDate(currentDate);
		}
		return true;
	}
	
	public NurseSiteMaintain createMaintain(Date date,UserAccount currentUser,NurseSiteMaintainConfig nurseSiteMaintainConfig,List<AssetInfo> listCheckAssets){
		Set<NurseSiteMaintainItem> listItem=loadConfigCheckItems(nurseSiteMaintainConfig, listCheckAssets);
		NurseSiteMaintain tempNurseSiteMaintain=new NurseSiteMaintain();
		Date currentDate=new Date();
		tempNurseSiteMaintain.setTenantUID(currentUser.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(currentUser.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(currentUser.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(currentUser.getSiteUID());
		tempNurseSiteMaintain.setCheckDate(currentDate);
		tempNurseSiteMaintain.setCreateBy(currentUser);
		tempNurseSiteMaintain.setCreateByUid(currentUser.getUid());
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyBy(currentUser);
		tempNurseSiteMaintain.setModifyByUid(currentUser.getUid());
		tempNurseSiteMaintain.setModifyDate(currentDate);
		tempNurseSiteMaintain.setScheduleDate(date);
		tempNurseSiteMaintain.setStatus(0);
		tempNurseSiteMaintain.setName(nurseSiteMaintainConfig.getName());
		tempNurseSiteMaintain.setListNurseSiteMaintainItem(listItem);
		tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
		Iterator<NurseSiteMaintainItem> it=listItem.iterator();
		while(it.hasNext()){
			NurseSiteMaintainItem tempItem=it.next();
			tempItem.setNurseSiteMaintain(tempNurseSiteMaintain);
			tempItem.setCheckDate(currentDate);
			tempItem.setCreateByUid(currentUser.getUid());
			tempItem.setCreateDate(currentDate);
			tempItem.setModifyByUid(currentUser.getUid());
			tempItem.setModifyDate(currentDate);
			tempItem.setStatus(0);
		}
		return tempNurseSiteMaintain;
	}
	
	@Transactional
	public boolean saveMaintain(NurseSiteMaintain tempNurseSiteMaintain){
		save(tempNurseSiteMaintain);
		return true;
	}
	
	@Transactional
	public boolean deleteMaintain(NurseSiteMaintain tempNurseSiteMaintain){
		delete(tempNurseSiteMaintain);
		return true;
	}
	
	@Transactional
	public boolean saveMaintainConfig(NurseSiteMaintainConfig tempConfig,List<AssetInfo> listCheckAssets){
		save(tempConfig);
		saveConfigChildren(tempConfig,listCheckAssets);
		return true;
	}
	
	public boolean saveConfigChildren(NurseSiteMaintainConfig tempConfig,List<AssetInfo> listCheckAssets){
		String parentId=tempConfig.getId();
		if(StringUtils.isNotEmpty(parentId)){
			List<NurseSiteMaintainConfigItem> list=findByParentId(parentId);
			if(list!=null){
				deleteNurseSiteMaintainConfigItem(list);
			}
			if(listCheckAssets!=null && listCheckAssets.size()>0){
				list=new ArrayList<NurseSiteMaintainConfigItem>();
				for(int i=0;i<listCheckAssets.size();++i){
					NurseSiteMaintainConfigItem obj =new NurseSiteMaintainConfigItem();
					obj.setAssetUid(listCheckAssets.get(i).getUid());
					obj.setCheckItems(tempConfig.getCheckItems());
					obj.setCheckItemsDetail(tempConfig.getCheckItemsDetail());
					obj.setCreateBy(tempConfig.getCreateBy());
					obj.setCreateByUid(tempConfig.getCreateByUid());
					obj.setCreateDate(tempConfig.getCreateDate());
					obj.setHospitalUID(tempConfig.getHospitalUID());
					obj.setInstitutionUID(tempConfig.getInstitutionUID());
					obj.setModifyBy(tempConfig.getModifyBy());
					obj.setModifyByUid(tempConfig.getModifyByUid());
					obj.setModifyDate(tempConfig.getModifyDate());
					obj.setParentId(tempConfig.getId());
					obj.setSiteUID(tempConfig.getSiteUID());
					obj.setTenantUID(tempConfig.getTenantUID());
					obj.setType(tempConfig.getType());
					obj.setTypeValue(tempConfig.getTypeValue());
					list.add(obj);
				}
			}
			if(list!=null && list.size()>0){
				saveNurseSiteMaintainConfigItem(list);
			}
		}
		return true;
	}
	
	@Transactional
	public boolean deleteMaintainConfig(NurseSiteMaintainConfig tempConfig){
		deleteConfigChildren(tempConfig.getId());
		delete(tempConfig);
		return true;
	}
	public boolean deleteConfigChildren(String parentId){
		if(StringUtils.isNotEmpty(parentId)){
			List<NurseSiteMaintainConfigItem> list=findByParentId(parentId);
			if(list!=null){
				deleteNurseSiteMaintainConfigItem(list);
			}
		}
		return true;
	}
	
	public void updateMaintainData(NurseSiteMaintain nurseSiteMaintain,NurseSiteMaintainConfig newConfig,UserAccount user){
		String checkItems=newConfig.getCheckItems();
		if(StringUtils.isEmpty(checkItems)){
			return;
		}
		if(nurseSiteMaintain.getStatus()!=null && nurseSiteMaintain.getStatus()!=0){
			return;
		}
		Set<NurseSiteMaintainItem> listOldNurseSiteMaintainItem=nurseSiteMaintain.getListNurseSiteMaintainItem();
		List<AssetInfo> listNewAssetInfo=getListAssetInfoByMaintainConfigParentId(newConfig.getId());
		if(listOldNurseSiteMaintainItem==null){
			listOldNurseSiteMaintainItem=new HashSet<NurseSiteMaintainItem>();
		}
		Set<NurseSiteMaintainItem> listItem=loadConfigCheckItems(newConfig, listNewAssetInfo);
        Date currentDate=new Date();
        nurseSiteMaintain.setTenantUID(user.getTenantUID());
        nurseSiteMaintain.setInstitutionUID(user.getInstitutionUID());
        nurseSiteMaintain.setHospitalUID(user.getHospitalUID());
        nurseSiteMaintain.setSiteUID(user.getSiteUID());
        nurseSiteMaintain.setCheckDate(currentDate);
        nurseSiteMaintain.setCreateBy(user);
        nurseSiteMaintain.setCreateByUid(user.getUid());
        nurseSiteMaintain.setCreateDate(currentDate);
        nurseSiteMaintain.setModifyBy(user);
        nurseSiteMaintain.setModifyByUid(user.getUid());
        nurseSiteMaintain.setModifyDate(currentDate);
//        nurseSiteMaintain.setScheduleDate(date);
        nurseSiteMaintain.setStatus(0);
        nurseSiteMaintain.setName(newConfig.getName());
        nurseSiteMaintain.setListNurseSiteMaintainItem(listItem);
        nurseSiteMaintain.setConfigId(newConfig.getId());
        Iterator<NurseSiteMaintainItem> it=listItem.iterator();
        while(it.hasNext()){
            NurseSiteMaintainItem tempItem=it.next();
            tempItem.setNurseSiteMaintain(nurseSiteMaintain);
            tempItem.setCheckDate(currentDate);
            tempItem.setCreateByUid(user.getUid());
            tempItem.setCreateDate(currentDate);
            tempItem.setModifyByUid(user.getUid());
            tempItem.setModifyDate(currentDate);
            tempItem.setStatus(0);
        }
	}
	
	private void mergeMaintainDataBackup(NurseSiteMaintain nurseSiteMaintain,NurseSiteMaintainConfig newConfig,UserAccount user){
		String checkItems=newConfig.getCheckItems();
		if(StringUtils.isEmpty(checkItems)){
			return;
		}
		String name=nurseSiteMaintain.getName();
		String newName="-"+newConfig.getName();
		if(!name.contains(newName)){
			nurseSiteMaintain.setName(name+newName);
		}
		List<String> listCheckItem=new ArrayList<String>(Arrays.asList(checkItems.split(",")));
		boolean addCheckItem=false;
		Set<NurseSiteMaintainItem> listOldNurseSiteMaintainItem=nurseSiteMaintain.getListNurseSiteMaintainItem();
		List<AssetInfo> listNewAssetInfo=getListAssetInfoByMaintainConfigParentId(newConfig.getId());
		if(listOldNurseSiteMaintainItem==null){
			listOldNurseSiteMaintainItem=new HashSet<NurseSiteMaintainItem>();
		}
		//upload old item
		Iterator<NurseSiteMaintainItem> itOldItem=listOldNurseSiteMaintainItem.iterator();
		while(itOldItem.hasNext()){
			NurseSiteMaintainItem tempOldItem=itOldItem.next();
			if(addCheckItem==false){
				Set<NurseSiteMaintainData> listOldData=tempOldItem.getListData();
				Iterator<NurseSiteMaintainData> itAddCheckItem=listOldData.iterator();
				while(itAddCheckItem.hasNext()){
					NurseSiteMaintainData dataAddCheckItem=itAddCheckItem.next();
					if(!listCheckItem.contains(dataAddCheckItem.getCheckItem())){
						listCheckItem.add(dataAddCheckItem.getCheckItem());
					}
				}
				addCheckItem=true;
			}
			Set<NurseSiteMaintainData> listOldData=tempOldItem.getListData();
			if(listOldData==null){
				listOldData=new HashSet<NurseSiteMaintainData>();
			}
//			addNurseSiteMaintainData(listCheckItem, listOldData,tempOldItem);
		}
		//insert new item
		if(listNewAssetInfo!=null){
			for(int j=0;j<listNewAssetInfo.size();++j){
				String newAssetUid=listNewAssetInfo.get(j).getUid();
				NurseSiteMaintainItem findOldItem=null;
				itOldItem=listOldNurseSiteMaintainItem.iterator();
				while(itOldItem.hasNext()){
					NurseSiteMaintainItem tempOldItem=itOldItem.next();
					String oldAssetUid=tempOldItem.getAssetUid();
					if(newAssetUid.equals(oldAssetUid)){
						findOldItem=tempOldItem;
						break;
					}
				}
				if(findOldItem==null){
					Date date=new Date();
					NurseSiteMaintainItem tempItem=new NurseSiteMaintainItem();
					tempItem.setAssetUid(listNewAssetInfo.get(j).getUid());
					tempItem.setCreateByUid(user.getUid());
					tempItem.setCreateDate(date);
					tempItem.setModifyByUid(user.getUid());
					tempItem.setModifyDate(date);
					tempItem.setNurseSiteMaintain(nurseSiteMaintain);
					Set<NurseSiteMaintainData> listNewData=new HashSet<NurseSiteMaintainData>();
					tempItem.setListData(listNewData);
					listOldNurseSiteMaintainItem.add(tempItem);
//					addNurseSiteMaintainData(listCheckItem, tempItem.getListData(),tempItem);
				}
			}
		}
	}
	
	private void addNurseSiteMaintainData(String checkItems,Set<NurseSiteMaintainData> list,NurseSiteMaintainItem item){
		String []arr=checkItems.split(",");
		for(int i=0;i<arr.length;++i){
			boolean contains=false;
			Iterator<NurseSiteMaintainData> it=list.iterator();
			while(it.hasNext()){
				NurseSiteMaintainData data=it.next();
				if(data.getCheckItem().equals(arr[i])){
					contains=true;
					break;
				}
			}
			if(!contains){
				NurseSiteMaintainData temp=new NurseSiteMaintainData();
				temp.setCheckItem(arr[i]);
				temp.setCheckValue(false);
				temp.setItem(item);
				list.add(temp);
			}
		}
	}
	
//	private void formatExistMaintainDate(){
//	List<Date> tempList=new ArrayList<Date>();
//	for(int i=0;i<existMaintainDate.size();++i){
//		Date date=existMaintainDate.get(i);
//		try {
//			date=sdf.parse(sdf.format(date));
//			tempList.add(date);
//		} catch (ParseException e) {
//			LOGGER.error(e.getMessage());
//		}
//	}
//	existMaintainDate=tempList;
//}
}

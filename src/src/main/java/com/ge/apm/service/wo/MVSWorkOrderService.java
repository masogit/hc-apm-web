/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.ServiceRequestMsgRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.WorkOrderCoworkerRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.common.UserCommonService;

@Service
public class MVSWorkOrderService {
	@Autowired
	private V2_WorkOrderRepository v2WorkOrderRepository;
	@Autowired
	private AssetInfoRepository assetDao;
	@Autowired
	private ServiceRequestRepository srDao;
	@Autowired
	private WorkOrderDetailRepository detailDao;
	@Autowired
	private UserAccountRepository userDao;
	@Autowired
	private ServiceRequestApiService srApi;
	@Autowired
	OrgInfoRepository orgInfoRepository;
	@Autowired
	AssetInfoRepository assetInfoRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	private WorkOrderCoworkerRepository workOrderCoworkerRepository;
	@Autowired
	private UserCommonService ucService;
	@Autowired
	private ServiceRequestMsgRepository msgDao;
	
	public void saveWorkOrder(V2_WorkOrder workOrder){
		if(workOrder!=null){
			v2WorkOrderRepository.save(workOrder);
		}
	}
	public void supplementDetail(List<V2_WorkOrder_Detail> listDetail) {
		Map<String, V2_WorkOrder> mapV2_WorkOrder = new HashMap<String, V2_WorkOrder>();
		List<V2_WorkOrder> listV2_WorkOrder = new ArrayList<V2_WorkOrder>();
		//
		Set<String> setOrgInfoUid = new HashSet<String>();
		Map<String, OrgInfo> mapOrgInfo = new HashMap<String, OrgInfo>();
		List<OrgInfo> listOrgInfo = null;
		for (int i = 0; i < listDetail.size(); ++i) {
			V2_WorkOrder tempWorkOrder=listDetail.get(i).getV2WorkOrder();
			if(tempWorkOrder!=null){
				listV2_WorkOrder.add(tempWorkOrder);
			}
		}
		if (listV2_WorkOrder != null) {
			supplementWorkOrder(listV2_WorkOrder);
			for (int i = 0; i < listV2_WorkOrder.size(); ++i) {
				V2_WorkOrder tempV2_WorkOrder = listV2_WorkOrder.get(i);
				mapV2_WorkOrder.put(tempV2_WorkOrder.getId(), tempV2_WorkOrder);
				AssetInfo tempAsset=tempV2_WorkOrder.getAssetInfo();
				if(tempAsset!=null){
					String hospitalUid = tempAsset.getHospitalUID();
					String siteUid = tempAsset.getSiteUID();
					if (StringUtils.isNotEmpty(hospitalUid)) {
						setOrgInfoUid.add(hospitalUid);
					}
					if (StringUtils.isNotEmpty(siteUid)) {
						setOrgInfoUid.add(siteUid);
					}
				}
			}
		}
		if (setOrgInfoUid.size() > 0) {
			listOrgInfo = orgInfoRepository.findByUids(setOrgInfoUid);
		}
		if (listOrgInfo != null) {
			for (int i = 0; i < listOrgInfo.size(); ++i) {
				OrgInfo tempOrgInfo = listOrgInfo.get(i);
				mapOrgInfo.put(tempOrgInfo.getUid(), tempOrgInfo);
			}
		}
		for (int i = 0; i < listDetail.size(); ++i) {
			V2_WorkOrder_Detail tempV2_WorkOrder_Detail = listDetail.get(i);
			String temp = tempV2_WorkOrder_Detail.getWoId();
			if (StringUtils.isNotEmpty(temp)) {
				tempV2_WorkOrder_Detail.setV2WorkOrder(mapV2_WorkOrder.get(temp));
			}
			String hospitalUid = tempV2_WorkOrder_Detail.getHospitalUID();
			String siteUid = tempV2_WorkOrder_Detail.getSiteUID();
			if (StringUtils.isNotEmpty(hospitalUid)) {
				tempV2_WorkOrder_Detail.setHospitalOrg(mapOrgInfo.get(hospitalUid));
			}
			if (StringUtils.isNotEmpty(siteUid)) {
				tempV2_WorkOrder_Detail.setSiteOrg(mapOrgInfo.get(siteUid));
			}
		}
	}
//	public void supplementDetail(List<V2_WorkOrder_Detail> listDetail) {
//		Set<String> setV2_WorkOrderUid = new HashSet<String>();
//		Map<String, V2_WorkOrder> mapV2_WorkOrder = new HashMap<String, V2_WorkOrder>();
//		List<V2_WorkOrder> listV2_WorkOrder = null;
//		//
//		Set<String> setOrgInfoUid = new HashSet<String>();
//		Map<String, OrgInfo> mapOrgInfo = new HashMap<String, OrgInfo>();
//		List<OrgInfo> listOrgInfo = null;
//		for (int i = 0; i < listDetail.size(); ++i) {
//			String temp = listDetail.get(i).getWoId();
//			if (StringUtils.isNotEmpty(temp)) {
//				setV2_WorkOrderUid.add(temp);
//			}
//		}
//		if (setV2_WorkOrderUid.size() > 0) {
//			listV2_WorkOrder = v2WorkOrderRepository.getWorkOrderById(setV2_WorkOrderUid);
//		}
//		if (listV2_WorkOrder != null) {
//			supplementWorkOrder(listV2_WorkOrder);
//			for (int i = 0; i < listV2_WorkOrder.size(); ++i) {
//				V2_WorkOrder tempV2_WorkOrder = listV2_WorkOrder.get(i);
//				mapV2_WorkOrder.put(tempV2_WorkOrder.getId(), tempV2_WorkOrder);
//				AssetInfo tempAsset=tempV2_WorkOrder.getAssetInfo();
//				if(tempAsset!=null){
//					String hospitalUid = tempAsset.getHospitalUID();
//					String siteUid = tempAsset.getSiteUID();
//					if (StringUtils.isNotEmpty(hospitalUid)) {
//						setOrgInfoUid.add(hospitalUid);
//					}
//					if (StringUtils.isNotEmpty(siteUid)) {
//						setOrgInfoUid.add(siteUid);
//					}
//				}
//			}
//		}
//		if (setOrgInfoUid.size() > 0) {
//			listOrgInfo = orgInfoRepository.findByUids(setOrgInfoUid);
//		}
//		if (listOrgInfo != null) {
//			for (int i = 0; i < listOrgInfo.size(); ++i) {
//				OrgInfo tempOrgInfo = listOrgInfo.get(i);
//				mapOrgInfo.put(tempOrgInfo.getUid(), tempOrgInfo);
//			}
//		}
//		for (int i = 0; i < listDetail.size(); ++i) {
//			V2_WorkOrder_Detail tempV2_WorkOrder_Detail = listDetail.get(i);
//			String temp = tempV2_WorkOrder_Detail.getWoId();
//			if (StringUtils.isNotEmpty(temp)) {
//				tempV2_WorkOrder_Detail.setV2WorkOrder(mapV2_WorkOrder.get(temp));
//			}
//			String hospitalUid = tempV2_WorkOrder_Detail.getHospitalUID();
//			String siteUid = tempV2_WorkOrder_Detail.getSiteUID();
//			if (StringUtils.isNotEmpty(hospitalUid)) {
//				tempV2_WorkOrder_Detail.setHospitalOrg(mapOrgInfo.get(hospitalUid));
//			}
//			if (StringUtils.isNotEmpty(siteUid)) {
//				tempV2_WorkOrder_Detail.setSiteOrg(mapOrgInfo.get(siteUid));
//			}
//		}
//	}
	
	private void supplementWorkOrder(List<V2_WorkOrder> listWorkOrder){
		Set<Integer> setUserAccountUid=new HashSet<Integer>();
		Map<Integer,UserAccount> mapUserAccount=new HashMap<Integer,UserAccount>();
		List<UserAccount> listUserAccount=null;
		for(int i=0;i<listWorkOrder.size();++i){
			Integer temp=listWorkOrder.get(i).getCurrentPersonId();
			if(temp!=null){
				setUserAccountUid.add(temp);
			}
		}
		if(setUserAccountUid.size()>0){
			listUserAccount=userAccountRepository.getByIds(setUserAccountUid);
		}
		if(listUserAccount!=null){
			for(int i=0;i<listUserAccount.size();++i){
				UserAccount tempUserAccount=listUserAccount.get(i);
				mapUserAccount.put(tempUserAccount.getId(), tempUserAccount);
			}
		}
		for(int i=0;i<listWorkOrder.size();++i){
			V2_WorkOrder tempV2_WorkOrder=listWorkOrder.get(i);
			Integer temp=tempV2_WorkOrder.getCurrentPersonId();
			if(temp!=null){
				tempV2_WorkOrder.setFEOwner(mapUserAccount.get(temp));
			}
		}
	}


}

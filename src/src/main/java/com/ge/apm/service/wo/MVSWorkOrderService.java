/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import java.text.SimpleDateFormat;
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
import com.ge.apm.domain.MVSAssetInfoOverview;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.ExcelDocument;

import webapp.framework.web.WebUtil;

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

	private static final String HEADER_REQUESTTIME = "报修时间";
	private static final String HEADER_MVSTYPE = "MVS工单类型";
	private static final String HEADER_PRIORITY = "普通/加急";
	private static final String HEADER_FEOWNER = "FE工程师";
	private static final String HEADER_HOSPITAL = "所属医院";
	private static final String HEADER_SITE = "所属院区";
	private static final String HEADER_DEPARTNUM = "设备编号";
	private static final String HEADER_ASSETNAME = "设备名称";
	private static final String HEADER_PARTNAME = "备件名称";
	private static final String HEADER_QUANTITY = "数量";
	private static final String HEADER_PRICE = "采购单价";
	private static final String HEADER_SERIANUM = "序列号";
	private static final String HEADER_INVOICENUM = "发票号";
	private static final String HEADER_COMMENT = "备注";
	private static final String HEADER_WARRANTYDATE = "质保期";
	private static final String HEADER_PURCHASETIME = "采购时间";
	private static final String HEADER_DELIVERTIME = "发货时间";
	private static final String HEADER_ARRIVEDTIME = "收货时间";
	private static final String HEADER_ARRIVEDGOOD = "是否完好";
	private static final String HEADER_ARRIVEDGOODRETURN = "好还/坏还";
	private static final String SHEET_USER = "datalist";

	public void saveWorkOrder(V2_WorkOrder workOrder) {
		if (workOrder != null) {
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
			V2_WorkOrder tempWorkOrder = listDetail.get(i).getV2WorkOrder();
			if (tempWorkOrder != null) {
				listV2_WorkOrder.add(tempWorkOrder);
			}
		}
		if (listV2_WorkOrder != null) {
			supplementWorkOrder(listV2_WorkOrder);
			for (int i = 0; i < listV2_WorkOrder.size(); ++i) {
				V2_WorkOrder tempV2_WorkOrder = listV2_WorkOrder.get(i);
				mapV2_WorkOrder.put(tempV2_WorkOrder.getId(), tempV2_WorkOrder);
				AssetInfo tempAsset = tempV2_WorkOrder.getAssetInfo();
				if (tempAsset != null) {
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
			try {
				hospitalUid=tempV2_WorkOrder_Detail.getV2WorkOrder().getAssetInfo().getHospitalUID();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				siteUid=tempV2_WorkOrder_Detail.getV2WorkOrder().getAssetInfo().getSiteUID();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (StringUtils.isNotEmpty(hospitalUid)) {
				tempV2_WorkOrder_Detail.setHospitalOrg(mapOrgInfo.get(hospitalUid));
			}
			if (StringUtils.isNotEmpty(siteUid)) {
				tempV2_WorkOrder_Detail.setSiteOrg(mapOrgInfo.get(siteUid));
			}
		}
	}
	// public void supplementDetail(List<V2_WorkOrder_Detail> listDetail) {
	// Set<String> setV2_WorkOrderUid = new HashSet<String>();
	// Map<String, V2_WorkOrder> mapV2_WorkOrder = new HashMap<String,
	// V2_WorkOrder>();
	// List<V2_WorkOrder> listV2_WorkOrder = null;
	// //
	// Set<String> setOrgInfoUid = new HashSet<String>();
	// Map<String, OrgInfo> mapOrgInfo = new HashMap<String, OrgInfo>();
	// List<OrgInfo> listOrgInfo = null;
	// for (int i = 0; i < listDetail.size(); ++i) {
	// String temp = listDetail.get(i).getWoId();
	// if (StringUtils.isNotEmpty(temp)) {
	// setV2_WorkOrderUid.add(temp);
	// }
	// }
	// if (setV2_WorkOrderUid.size() > 0) {
	// listV2_WorkOrder =
	// v2WorkOrderRepository.getWorkOrderById(setV2_WorkOrderUid);
	// }
	// if (listV2_WorkOrder != null) {
	// supplementWorkOrder(listV2_WorkOrder);
	// for (int i = 0; i < listV2_WorkOrder.size(); ++i) {
	// V2_WorkOrder tempV2_WorkOrder = listV2_WorkOrder.get(i);
	// mapV2_WorkOrder.put(tempV2_WorkOrder.getId(), tempV2_WorkOrder);
	// AssetInfo tempAsset=tempV2_WorkOrder.getAssetInfo();
	// if(tempAsset!=null){
	// String hospitalUid = tempAsset.getHospitalUID();
	// String siteUid = tempAsset.getSiteUID();
	// if (StringUtils.isNotEmpty(hospitalUid)) {
	// setOrgInfoUid.add(hospitalUid);
	// }
	// if (StringUtils.isNotEmpty(siteUid)) {
	// setOrgInfoUid.add(siteUid);
	// }
	// }
	// }
	// }
	// if (setOrgInfoUid.size() > 0) {
	// listOrgInfo = orgInfoRepository.findByUids(setOrgInfoUid);
	// }
	// if (listOrgInfo != null) {
	// for (int i = 0; i < listOrgInfo.size(); ++i) {
	// OrgInfo tempOrgInfo = listOrgInfo.get(i);
	// mapOrgInfo.put(tempOrgInfo.getUid(), tempOrgInfo);
	// }
	// }
	// for (int i = 0; i < listDetail.size(); ++i) {
	// V2_WorkOrder_Detail tempV2_WorkOrder_Detail = listDetail.get(i);
	// String temp = tempV2_WorkOrder_Detail.getWoId();
	// if (StringUtils.isNotEmpty(temp)) {
	// tempV2_WorkOrder_Detail.setV2WorkOrder(mapV2_WorkOrder.get(temp));
	// }
	// String hospitalUid = tempV2_WorkOrder_Detail.getHospitalUID();
	// String siteUid = tempV2_WorkOrder_Detail.getSiteUID();
	// if (StringUtils.isNotEmpty(hospitalUid)) {
	// tempV2_WorkOrder_Detail.setHospitalOrg(mapOrgInfo.get(hospitalUid));
	// }
	// if (StringUtils.isNotEmpty(siteUid)) {
	// tempV2_WorkOrder_Detail.setSiteOrg(mapOrgInfo.get(siteUid));
	// }
	// }
	// }

	private void supplementWorkOrder(List<V2_WorkOrder> listWorkOrder) {
		Set<Integer> setUserAccountUid = new HashSet<Integer>();
		Map<Integer, UserAccount> mapUserAccount = new HashMap<Integer, UserAccount>();
		List<UserAccount> listUserAccount = null;
		for (int i = 0; i < listWorkOrder.size(); ++i) {
			Integer temp = listWorkOrder.get(i).getCurrentPersonId();
			if (temp != null) {
				setUserAccountUid.add(temp);
			}
		}
		if (setUserAccountUid.size() > 0) {
			listUserAccount = userAccountRepository.getByIds(setUserAccountUid);
		}
		if (listUserAccount != null) {
			for (int i = 0; i < listUserAccount.size(); ++i) {
				UserAccount tempUserAccount = listUserAccount.get(i);
				mapUserAccount.put(tempUserAccount.getId(), tempUserAccount);
			}
		}
		for (int i = 0; i < listWorkOrder.size(); ++i) {
			V2_WorkOrder tempV2_WorkOrder = listWorkOrder.get(i);
			Integer temp = tempV2_WorkOrder.getCurrentPersonId();
			if (temp != null) {
				tempV2_WorkOrder.setFEOwner(mapUserAccount.get(temp));
			}
		}
	}

	public List<Map<String, Object>> parseToMap(List<MVSAssetInfoOverview> listDetail) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listDetail != null && listDetail.size() > 0) {
			for (int i = 0; i < listDetail.size(); ++i) {
				MVSAssetInfoOverview tempDetail = listDetail.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				try {
					if (tempDetail.getV2WorkOrderServiceRequestRequestTime()!=null) {
						tempMap.put(HEADER_REQUESTTIME, sdf2.format(tempDetail.getV2WorkOrderServiceRequestRequestTime()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getV2WorkOrderMvsType()!=null) {
						String result=WebUtil.getFieldValueMessage("mvsType", tempDetail.getV2WorkOrderMvsType()+"");
						tempMap.put(HEADER_MVSTYPE, result);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getPriority()!=null) {
						String result="";
						if(tempDetail.getPriority().equals("1")){
							result="普通";
						}
						else if(tempDetail.getPriority().equals("2")){
							result="加急";
						}
						tempMap.put(HEADER_PRIORITY,result);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (tempDetail.getV2WorkOrderFEOwnerName()!=null) {
						tempMap.put(HEADER_FEOWNER, tempDetail.getV2WorkOrderFEOwnerName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getHospitalOrgName()!=null) {
						tempMap.put(HEADER_HOSPITAL, tempDetail.getHospitalOrgName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getSiteOrgName()!=null) {
						tempMap.put(HEADER_SITE, tempDetail.getSiteOrgName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getV2WorkOrderAssetInfoDepartNum()!=null) {
						tempMap.put(HEADER_DEPARTNUM, tempDetail.getV2WorkOrderAssetInfoDepartNum());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getV2WorkOrderAssetInfoName()!=null) {
						tempMap.put(HEADER_ASSETNAME, tempDetail.getV2WorkOrderAssetInfoName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getParts()!=null) {
						tempMap.put(HEADER_PARTNAME, tempDetail.getParts());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getPartsQuantity()!=null) {
						tempMap.put(HEADER_QUANTITY,tempDetail.getPartsQuantity());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getMvsPrice()!=null) {
						tempMap.put(HEADER_PRICE, tempDetail.getMvsPrice());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getSerialNum()!=null) {
						tempMap.put(HEADER_SERIANUM, tempDetail.getSerialNum());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getInvoiceNum()!=null) {
						tempMap.put(HEADER_INVOICENUM, tempDetail.getInvoiceNum());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (tempDetail.getComment()!=null) {
						tempMap.put(HEADER_COMMENT, tempDetail.getComment());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				try {
					if (tempDetail.getWarrantyDate()!=null) {
						tempMap.put(HEADER_WARRANTYDATE, sdf.format(tempDetail.getWarrantyDate()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getPurchaseTime()!=null) {
						tempMap.put(HEADER_PURCHASETIME, sdf2.format(tempDetail.getPurchaseTime()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getDeliverTime()!=null) {
						tempMap.put(HEADER_DELIVERTIME, sdf2.format(tempDetail.getDeliverTime()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getArrivedTime()!=null) {
						tempMap.put(HEADER_ARRIVEDTIME,sdf2.format(tempDetail.getArrivedTime()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getArrivedGood()!=null) {
						tempMap.put(HEADER_ARRIVEDGOOD, tempDetail.getArrivedGood()?"是":"否");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					if (tempDetail.getArrivedGoodReturn()!=null) {
						tempMap.put(HEADER_ARRIVEDGOODRETURN, tempDetail.getArrivedGoodReturn()?"是":"否");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
	}

	public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {
		Integer keyRow = 1;
		Integer dataRow = 2;
		for (Map<String, Object> item : data) {
			doc.writeRowData(SHEET_USER, keyRow, dataRow++, item);
		}
		return Boolean.TRUE;
	}
}

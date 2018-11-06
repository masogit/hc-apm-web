package com.ge.apm.view.wo;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.V2_WorkOrderDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.MVSWorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MVSWorkOrderMgrController extends GenericCRUDUUIDController<V2_WorkOrder_Detail> implements Serializable, ServerEventInterface {

	private V2_WorkOrderDetailRepository dao;
	private MVSWorkOrderService service;
	private I18nMessageRepository i18nDao;
	private UserAccount currentUser;
	private List<V2_WorkOrder_Detail> listDetail;
	private StreamedContent exportFile;
	private OrgInfo selectSite;
	private PageRequest originPageRequest;
	private List<SearchFilter> listNewSearchFilter;
	@Override
	protected void init() {
		dao = WebUtil.getBean(V2_WorkOrderDetailRepository.class);
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		service = WebUtil.getBean(MVSWorkOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		
	}

	@Override
	protected GenericRepositoryUUID<V2_WorkOrder_Detail> getDAO() {
		return dao;
	}

	@Override
	protected Page<V2_WorkOrder_Detail> loadData(PageRequest pageRequest) {
		if (this.searchFilters == null) {
			this.searchFilters = new ArrayList<>();
		}
		if (this.originPageRequest == null) {
			this.originPageRequest = pageRequest;
		}

		// searchFilters.add(new SearchFilter("tenantUID",
		// SearchFilter.Operator.EQ, currentUser.getTenantUID()));
		// if (currentUser.getOrgLevel() != 0) {
		// if (currentUser.getOrgLevel() == 1) {
		// searchFilters.add(new SearchFilter("institutionUID",
		// SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
		// } else if (currentUser.getOrgLevel() == 2) {
		// searchFilters.add(new SearchFilter("hospitalUID",
		// SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
		// } else {
		// searchFilters.add(new SearchFilter("siteUID",
		// SearchFilter.Operator.EQ, currentUser.getSiteUID()));
		// }
		// }
		// eager
		updateFilter();
		Page<V2_WorkOrder_Detail> pageResult = dao.findBySearchFilter(searchFilters, pageRequest);
		if (pageRequest != null) {
			listDetail = pageResult.getContent();
			if (listDetail != null) {
				service.supplementDetail(listDetail);
			}
		}
		return pageResult;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {

	}

	@Override
	public void onBeforeNewObject(V2_WorkOrder_Detail object) {
		super.onBeforeNewObject(object);
	}

	@Override
	public void save() {
		// service.saveWorkOrder(this.selected.getV2WorkOrder());
		super.save();
		this.selected = null;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		super.delete();
		this.selected = null;
	}

	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return WebUtil.getMessage("MVS设备维修记录 " + WebUtil.getMessage(actionName));
	}

	public void exportAll() {
		if (listDetail == null || listDetail.size() == 0) {
			WebUtil.addErrorMessage("无数据");
			return;
		}
		downloadFile(service.parseToMap(listDetail));
	}

	public void onSelectSiteTreeNode(NodeSelectEvent event) {
		if (null != event) {
			if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
				selectSite = (OrgInfo) event.getTreeNode().getData();
				listNewSearchFilter=new ArrayList<SearchFilter>();
				Integer orgType = selectSite.getOrgType();
				if(selectSite.getId()==null || selectSite.getId()==-1){
				}
				else if (orgType == 3) {
					listNewSearchFilter.add(new SearchFilter("v2WorkOrder.assetInfo.siteUID", SearchFilter.Operator.EQ, selectSite.getSiteUID()));
				} else if (orgType == 2) {
					listNewSearchFilter.add(new SearchFilter("v2WorkOrder.assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectSite.getHospitalUID()));
				} else if (orgType == 1) {
					listNewSearchFilter.add(new SearchFilter("v2WorkOrder.assetInfo.institutionUID", SearchFilter.Operator.EQ, selectSite.getInstitutionUID()));
				} else if (orgType == 0) {
					listNewSearchFilter.add(new SearchFilter("v2WorkOrder.assetInfo.tenantUID", SearchFilter.Operator.EQ, selectSite.getTenantUID()));
				}
			} else {
				selectSite = null;
			}
		}
	}
	
	private void updateFilter(){
		searchFilters.add(new SearchFilter("v2WorkOrder.assetInfo.mvsEquip", SearchFilter.Operator.EQ, "1"));
		for (int i = 0;i>-1 && i < searchFilters.size(); ++i) {
			SearchFilter tempFilter = searchFilters.get(i);
			if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.siteUID")) {
				searchFilters.remove(i);
				--i;
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.hospitalUID")) {
				searchFilters.remove(i);
				--i;
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.institutionUID")) {
				searchFilters.remove(i);
				--i;
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.tenantUID")) {
				searchFilters.remove(i);
				--i;
			}
		}
		if(listNewSearchFilter!=null){
			searchFilters.addAll(listNewSearchFilter);
		}
	}

	private void downloadFile(List<Map<String, Object>> data) {
		try {
			InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/woMgr/mvs/APM_MVSWorkOrder_Template.xlsx");
			ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
			Boolean exportFlag = service.exportData(doc, data);
			if (exportFlag) {
				exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "MVSWorkOrder.xlsx");
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
	}

	public StreamedContent getExportFile() {
		return exportFile;
	}

	public void setExportFile(StreamedContent exportFile) {
		this.exportFile = exportFile;
	}

	public List<V2_WorkOrder_Detail> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<V2_WorkOrder_Detail> listDetail) {
		this.listDetail = listDetail;
	}

	public OrgInfo getSelectSite() {
		return selectSite;
	}

	public void setSelectSite(OrgInfo selectSite) {
		this.selectSite = selectSite;
	}

	// private Page<V2_WorkOrder_Detail> filterResult(Page<V2_WorkOrder_Detail>
	// pageDetail){
	// List<V2_WorkOrder_Detail> listDetail=pageDetail.getContent();
	// List<V2_WorkOrder_Detail> newListDetail=new
	// ArrayList<V2_WorkOrder_Detail>();
	// for(int i=0;i<listDetail.size();++i){
	// V2_WorkOrder_Detail tempDetail=listDetail.get(i);
	// V2_WorkOrder order=tempDetail.getV2WorkOrder();
	// AssetInfo asset=null;
	// Integer mvsEquip=null;
	// if(order!=null){
	// asset=order.getAssetInfo();
	// }
	// if(asset!=null){
	// mvsEquip=asset.getMvsEquip();
	// }
	// if(mvsEquip!=null && mvsEquip==1){
	// newListDetail.add(tempDetail);
	// }
	// }
	// Page<V2_WorkOrder_Detail> newPage=new
	// PageImpl<V2_WorkOrder_Detail>(newListDetail);
	// return newPage;
	// }

	// private void changeFilter(List<SearchFilter> searchFilters, PageRequest
	// request) {
	// if (searchFilters != null) {
	// for (int i = 0; i < searchFilters.size(); ++i) {
	// SearchFilter tempFilter = searchFilters.get(i);
	// String name = tempFilter.fieldName;
	// System.out.println(name);
	// if (name.equals("v2WorkOrder.mvsType")) {
	//
	// } else if (name.equals("priority")) {
	//
	// } else if (name.equals("v2WorkOrder.FEOwner.name")) {
	//
	// } else if (name.equals("hospitalOrg.name")) {
	//
	// } else if (name.equals("siteOrg.name")) {
	//
	// } else if (name.equals("v2WorkOrder.assetInfo.departNum")) {
	//
	// } else if (name.equals("v2WorkOrder.assetInfo.name")) {
	//
	// } else if (name.equals("parts")) {
	//
	// } else if (name.equals("apartsQuantity")) {
	//
	// } else if (name.equals("mvsPrice")) {
	//
	// } else if (name.equals("serialNum")) {
	//
	// } else if (name.equals("invoiceNum")) {
	//
	// } else if (name.equals("arrivedGood")) {
	//
	// } else if (name.equals("arrivedGoodReturn")) {
	//
	// }
	// }
	// }
	// }

}

package com.ge.apm.view.wo;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.mapper.V2WorkOrderDetailMapper;
import com.ge.apm.domain.MVSAssetInfoOverview;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.MVSWorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * @author Nick Liu
 * @since 2018.10.2 release
 */
@ManagedBean
@ViewScoped
public class MVSWorkOrderMgrController extends MyCRUDController<MVSAssetInfoOverview> implements Serializable, ServerEventInterface {

	private static final Logger logger = LoggerFactory.getLogger(MVSWorkOrderMgrController.class);

	private V2WorkOrderDetailMapper dao;
	private MVSWorkOrderService service;
	private I18nMessageRepository i18nDao;
	private UserAccount currentUser;
	private List<V2_WorkOrder_Detail> listDetail;
	private List<SearchFilter> listNewSearchFilter;
	@Getter
	@Setter
	private StreamedContent exportFile;
	@Getter
	@Setter
	private OrgInfo selectSite;

	@Override
	protected void init() {
		dao = WebUtil.getBean(V2WorkOrderDetailMapper.class);
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		service = WebUtil.getBean(MVSWorkOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
	}

	@Override
	protected BaseMapper<MVSAssetInfoOverview> getDAO() {
		return dao;
	}

	@Override
	protected List<MVSAssetInfoOverview> loadData(Pagination pageRequest, String sortField, boolean isAscending){
		if (this.searchFilters == null) {
			this.searchFilters = new HashSet<SearchFilter>();
		}
		updateFilter();
		int rowCount=this.lazyModel.getRowCount();
		if(rowCount==0){
			this.lazyModel.setRowCount(dao.getAllCount(searchFilters));
		}
		List<MVSAssetInfoOverview> listResult = dao.getAll(searchFilters,sortField,isAscending,pageRequest.getLimit(),pageRequest.getOffset());
		return listResult;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {

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
				listNewSearchFilter = new ArrayList<SearchFilter>();
				Integer orgType = selectSite.getOrgType();
				if (selectSite.getId() == null || selectSite.getId() == -1) {
				} else if (orgType == 3) {
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

	private void updateFilter() {
		searchFilters.add(new SearchFilter("v2WorkOrder.assetInfo.mvsEquip", SearchFilter.Operator.EQ, "1"));
		Iterator<SearchFilter> it=searchFilters.iterator();
		while(it.hasNext()){
			SearchFilter tempFilter =it.next();
			if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.siteUID")) {
				searchFilters.remove(tempFilter);
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.hospitalUID")) {
				searchFilters.remove(tempFilter);
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.institutionUID")) {
				searchFilters.remove(tempFilter);
			} else if (tempFilter.fieldName.equals("v2WorkOrder.assetInfo.tenantUID")) {
				searchFilters.remove(tempFilter);
			}
		}
		if (listNewSearchFilter != null) {
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

}

package com.ge.apm.view.nurseSite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.NurseSiteMaintainItemMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the "日常检查附件" (Daily Inspection Attachments) list page.
 * Displays daily inspection records that have uploaded attachments, at the equipment item level.
 */
@javax.faces.bean.ManagedBean
@javax.faces.bean.ViewScoped
public class MaintainAttachmentListController implements Serializable, ServerEventInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainAttachmentListController.class);

    private UserAccount currentUser;
    private NurseSiteMaintainItemMapper maintainItemMapper;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private BlobFileService blobFileService;
    private BlobObjectRepository blobObjectRepository;
    private OrgInfoRepository orgInfoRepository;
    private UaaService uaaService;
    private SearchController searchController;
    private NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
    private LazyDataModel<Map<String, Object>> lazyModel;
    private List<String> orgUidList = Collections.singletonList("-1");
    private NurseSiteMaintain nurseSiteMaintain;
    private List<V2_BlobObject> attachmentList;
    private OrgInfo selectedDept;
    @Getter @Setter
    private Map<String, Object> selectedItem;

    @PostConstruct
    public void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        maintainItemMapper = WebUtil.getBean(NurseSiteMaintainItemMapper.class);
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
        nurseSiteMaintainService.init();
        blobFileService = WebUtil.getBean(BlobFileService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        searchController = WebUtil.getBean(SearchController.class);
        nurseSiteMaintainItemRepository = WebUtil.getBean(NurseSiteMaintainItemRepository.class);

        searchController.setCallingController(this);
        searchController.setType(3);
        searchController.setBlnOrgTreeUpdate(true);
        searchController.setBlnDepartOrgUpdate(true);
        searchController.setUpdateViewIDs(":maintainAttachmentForm:datalist");
        searchController.updateData();

        orgUidList = uaaService.getOrgListByUser(currentUser.getUid())
                .stream().map(OrgInfo::getUid).collect(Collectors.toList());

        initLazyModel();
    }

    private void initLazyModel() {
        lazyModel = new LazyDataModel<Map<String, Object>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<Map<String, Object>> load(int first, int pageSize, String sortField,
                                                   SortOrder sortOrder, Map<String, Object> filters) {
                Map<String, Object> param = buildQueryParams(filters);
                param.put("pageNum",first);
                param.put("pageSize",pageSize);
                List<Map<String, Object>> result = maintainItemMapper.searchMaintainItemsWithAttachment(param);
               int count = maintainItemMapper.searchMaintainItemsWithAttachmentCount(param);
                this.setRowCount(count);
                return result;
            }

            @Override
            public Map<String, Object> getRowData(String rowKey) {
                if (rowKey == null) {
                    return null;
                }
                List<Map<String, Object>> data = getWrappedData();
                if (data != null) {
                    for (Map<String, Object> row : data) {
                        Object id = row.get("itemId");
                        if (id != null && rowKey.equals(id.toString())) {
                            return row;
                        }
                    }
                }
                return null;
            }
        };
    }

    private Map<String, Object> buildQueryParams(Map<String, Object> filters) {
        Map<String, Object> param = new HashMap<>();
        param.put("tenantUid", currentUser.getTenantUID());

        OrgInfo selectedSite = searchController.getSelectedOrg();
        if (ObjectUtil.isNotEmpty(selectedSite)) {
            param.put("siteUid", selectedSite.getUid());
        }
        if (ObjectUtil.isNotEmpty(selectedDept)) {
            param.put("deptUid", selectedDept.getUid());
        }

        param.put("orgUidList", orgUidList);

        // Apply PrimeFaces filter values
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String field = entry.getKey();
                Object filterValue = entry.getValue();
                if (filterValue == null) continue;
                String filterStr = String.valueOf(filterValue).trim();
                if (filterStr.isEmpty()) continue;

                switch (field) {
                    case "maintainName":
                        param.put("maintainName", filterStr);
                        break;
                    case "assetName":
                        param.put("assetName", filterStr);
                        break;
                    case "serialNum":
                        param.put("serialNum", filterStr);
                        break;
                    case "qrCode":
                        param.put("qrCode", filterStr);
                        break;
                    case "clinicalDeptName":
                        param.put("clinicalDeptName", filterStr);
                        break;
                    case "usingDeptName":
                        param.put("usingDeptName", filterStr);
                        break;
                }
            }
        }
        return param;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        // Capture department selection from SearchController org tree
        if (eventObject instanceof OrgInfo) {
            OrgInfo orgInfo = (OrgInfo) eventObject;
            if (orgInfo != null && orgInfo.getOrgType() != null && orgInfo.getOrgType() == 4) {
                selectedDept = orgInfo;
            } else {
                selectedDept = null;
            }
        } else {
            selectedDept = null;
        }
    }

    public void onFilter(FilterEvent filterEvent) {
        // Filter values are handled by the LazyDataModel.load() method
    }

    /**
     * Called when a row is selected in the data table.
     * Loads the work order details and attachments for the selected item.
     */
    public void onRowSelect(SelectEvent selectEvent) {
        Map<String, Object> item = (Map<String, Object>) selectEvent.getObject();
        String maintainId = (String) item.get("maintainId");
        String itemId = (String) item.get("itemId");

        // Load work order details
        if (maintainId != null) {
            nurseSiteMaintain = nurseSiteMaintainService.findByIds(Collections.singleton(maintainId)).get(0);
            List<NurseSiteMaintainItem> listItem = nurseSiteMaintainItemRepository.findByIdIn(Arrays.asList(itemId));
            nurseSiteMaintain.setListNurseSiteMaintainItem(listItem);
            nurseSiteMaintainService.supplementMaintainItem(listItem);
            generateShowLables();
        }

        // Load attachments
        attachmentList = blobObjectRepository.fetchListByMaintainId(maintainId);
    }

    private void generateShowLables() {
        if (nurseSiteMaintain == null) return;
        List<NurseSiteMaintainConfigCheckItem> showLables = nurseSiteMaintain.getShowLables();
        if (showLables == null) {
            showLables = new ArrayList<>();
        } else {
            showLables.clear();
        }

        try {
            List<NurseSiteMaintainItem> items = nurseSiteMaintain.getListNurseSiteMaintainItem();
            if (items != null && !items.isEmpty()) {
                List<NurseSiteMaintainData> listTempData = items.iterator().next().getListData();
                if (listTempData != null) {
                    List<String> collect = listTempData.stream()
                            .map(NurseSiteMaintainData::getCheckItem)
                            .collect(Collectors.toList());
                    NurseSiteMaintainConfig maintainConfigById = nurseSiteMaintainService
                            .getMaintainConfigById(nurseSiteMaintain.getConfigId());
                    if (maintainConfigById != null) {
                        String strTemp = maintainConfigById.getCheckItemsDetail();
                        if (StringUtils.isNotEmpty(strTemp)) {
                            JSONArray jsonArray = JSONObject.parseArray(strTemp);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                NurseSiteMaintainCheckItem checkItem = JSONObject
                                        .parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
                                if (collect.contains(checkItem.getName())) {
                                    NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(checkItem);
                                    showLables.add(p);
                                }
                            }
                        }
                    }
                }
            }
            nurseSiteMaintain.setShowLables(showLables);
        } catch (Exception e) {
            LOGGER.error("getLables error {}", e.getMessage(), e);
        }
    }

    private NurseSiteMaintainConfigCheckItem initConfigCheckItem(NurseSiteMaintainCheckItem checkItem) {
        NurseSiteMaintainConfigCheckItem p = new NurseSiteMaintainConfigCheckItem();
        p.setId(checkItem.getId());
        p.setName(checkItem.getName());
        p.setDefaultValue(checkItem.getDefaultValue());
        p.setDescription(checkItem.getDescription());
        p.setFieldType(checkItem.getFieldType());
        p.setRequired(checkItem.getRequired());
        return p;
    }

    /**
     * Download an attachment file.
     */
    public void downloadAttachment(String objectStorageId) {
        try {
            StreamedContent file = blobFileService.getBlobObjectByStorageId(objectStorageId);
            if (file != null) {
                javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
                javax.servlet.http.HttpServletResponse response =
                        (javax.servlet.http.HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
                org.apache.commons.io.IOUtils.copy(file.getStream(), response.getOutputStream());
                facesContext.responseComplete();
            }
        } catch (Exception e) {
            LOGGER.error("downloadAttachment error: {}", e.getMessage(), e);
        }
    }

    /**
     * Preview an image attachment inline.
     */
    public StreamedContent getAttachmentPreview(String objectStorageId) {
        try {
            return blobFileService.getBlobObjectByStorageId(objectStorageId);
        } catch (Exception e) {
            LOGGER.error("getAttachmentPreview error: {}", e.getMessage(), e);
            return null;
        }
    }

    public LazyDataModel<Map<String, Object>> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Map<String, Object>> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public NurseSiteMaintain getNurseSiteMaintain() {
        return nurseSiteMaintain;
    }

    public void setNurseSiteMaintain(NurseSiteMaintain nurseSiteMaintain) {
        this.nurseSiteMaintain = nurseSiteMaintain;
    }

    public List<V2_BlobObject> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<V2_BlobObject> attachmentList) {
        this.attachmentList = attachmentList;
    }


    public String getShowTitle(Map<String, Object> map) {
        //0：未检查,1:已检查，2:通过，3有问题
        String strStatus="未检查";
        Integer status =(Integer) map.get("status");
        String name =(String) map.get("maintainName");
        Integer sequence =(Integer) map.get("sequence");
        if(status==null || status==0){
        }
        else if(status==1){
            strStatus="已检查";
        }
        else if(status==2|| status==5|| status==6|| status==7){
            strStatus="通过";
        }
        else if(status==3 || status==8 || status==9){
            strStatus="有问题";
        }
        else if(status==4){
            strStatus="进行中";
        }

        String str=name;
        if(sequence==null||sequence<=0){
            str+="-"+strStatus;
        }
        else{
            str+="-"+sequence+"-"+strStatus;
        }
        return str;
    }
}

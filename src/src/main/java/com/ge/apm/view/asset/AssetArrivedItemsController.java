package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.AssetTypeGroupTreeComponentController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedItemsController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository dao = null;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private List<V2_BlobObject> picList;

    private ArrivalListItem selectedItem;

    private Date varArrivalDateTo = null;
    private String arrivalFormatTime = null;

    private OrgInfo fileterDeptOrg;
    
    private String exportQrCodeTemplateId;
    
    private QrCodeService qrCodeService;

    private OrgInfoTreeService orgInfoTreeService;

    private OrgInfoRepository orgInfoDao;

    private UserAccountRepository userAccountRepository;

    private TreeNode selectedNode;

    private String filterClinicalDeptOrg;

    private Set<String> clinicalDeptOrgUidSet;

    private StreamedContent excelFile;

    private SupplierService supplierService;

    private ArrivalListRepository arrivalListDao = null;

    private String mergeContractNum;

    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;

    private AssetTypeGroupTreeComponentController assetTypeGroupTreeComponentController;

    private List<OrgInfo> clinicalDeptList;

    @Override
    protected void init() {
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        addDateTimeFields("arrivalList.arrivalDate");
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        getSavedArrivalContractNum();
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);
        assetTypeGroupTreeComponentController = WebUtil.getBean(AssetTypeGroupTreeComponentController.class);
        assetTypeGroupTreeComponentController.registerController(this);
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
    }

    @Override
    protected ArrivalListItemRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("arrivalList.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }

        if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
            /*if (selectedNode.isLeaf()) {
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterDeptOrg.getUid(), "1*"));
            } else {
                List<OrgInfo> allClinicals = orgInfoTreeService.collectChildClinicalDeptByParent(selectedNode);
                List<String> clinicalUids = allClinicals.stream().map(OrgInfo::getUid).collect(Collectors.toList());
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalUids, "1*"));
            }*/
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptOrgUidSet, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onRowSelect(SelectEvent event) {
        this.selected = (ArrivalListItem) event.getObject();
        medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, selected.getMedicalApparatusClassification());
        assetTypeGroupTreeComponentController.onServerEvent(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE, selected.getEncoded() + "#" + selected.getTenantUid());
        picList = aiService.getPicList(selected.getId());
    }

    public void prepareTransOrder(String itemId) {
        selectedItem = dao.findById(itemId);
    }

    public List<UserAccount> getWorkerList() {
        return aiService.getWorkerList();
    }

    public void reassignWorkOrder() {
        aiService.reAssignWorkOrder(selectedItem);
        cancel();
    }

    public void takeWorkOrder(String itemId) {
        ArrivalListItem item = dao.findById(itemId);
        item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
        aiService.reAssignWorkOrder(item);
        cancel();
    }

    public void takeWorkOrderBatch(String itemId) {
        ArrivalListItem item = dao.findById(itemId);
        String contractNum = item.getArrivalList().getContractNum();
        String clinicalDeptUID = item.getClinicalDeptUID();
        String errorMessage = "";
        if(StringUtils.isEmpty(contractNum) || StringUtils.isEmpty(clinicalDeptUID)){
            item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
            Boolean responseStatus = aiService.reAssignWorkOrder(item);
            if(!responseStatus){
                errorMessage = "记录提交失败";
            }
        }else{
            List<ArrivalListItem> itemList = dao.getArrivalListItem(contractNum,clinicalDeptUID,"new");
            for(ArrivalListItem arrivalListItem:itemList){
                arrivalListItem.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
                Boolean responseStatus = aiService.reAssignWorkOrder(arrivalListItem);
                if(!responseStatus){
                    String name = arrivalListItem.getArrivalList().getName();
                    if(errorMessage.contains("到货单名称/编号："+name)){
                        errorMessage = errorMessage + "、"+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }else{
                        errorMessage = errorMessage + "到货单名称/编号：" + name +","+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }
                }
            }
        }
        if(StringUtils.isBlank(errorMessage)){
            WebUtil.addSuccessMessage("接单成功");
        }else{
            WebUtil.addErrorMessage(errorMessage);
        }
        cancel();
    }

    public Boolean isAssignToMe(String currentPersonUID) {
        return UserContextService.getCurrentUserAccount().getUid().equals(currentPersonUID);
    }

    @Override
    public List<ArrivalListItem> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    public void cancel() {
        this.selected = null;
        selectedItem = null;
    }

    public void setSelected(String id) {
        if (Strings.isNullOrEmpty(id)) {
            selected = null;
        }
        selected = getDAO().findById(id);

    }
    public boolean Show(String assetUid) {
        selected= getDAO().getArrivalListItemByAssetUid(assetUid);
        if(selected!=null){
            return true;
        }else{
            return false;
        }
    }

    public List<I18nMessage> getInstallStatusList() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
        List<I18nMessage> statusList = FieldValueMessageController.getFieldValueList("arrivalListItemStatus", siteId);
        if (Boolean.FALSE.equals(userContextService.needInstorageCheck())) {
            return statusList.stream().filter(item -> !ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getMsgKey())).collect(Collectors.toList());
        }
        return statusList;
    }

    public String getOrgNameByUid(String orgUid) {
        if (!Strings.isNullOrEmpty(orgUid)) {
            OrgInfo org = aiService.getOrgInfoByUid(orgUid);
            return org==null? null:org.getName();
        }else {
            return null;
        }
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public List<V2_BlobObject> getAckPicList(String type) {
        return picList == null ? null : picList.stream().filter(item -> type.equals(item.getBoSubType())).collect(Collectors.toList());
    }
    
    public String getInstallUrl(String itemId,String srcPage){
        return UrlEncryptController.encodeUrlParam("ItemEdit?faces-redirect=true&src=".concat(srcPage).concat("&itemId=").concat(itemId));
    }

    public ArrivalListItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ArrivalListItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (deptOrg.getOrgType() == 4) {
            fileterDeptOrg = deptOrg;
        } else {
            fileterDeptOrg = null;
        }

    }
    
    public String getQrCodeImageForAsset() {
        AssetInfo asset = new AssetInfo();
        asset.setQrCode(selected.getQrCode());
        asset.setName(selected.getArrivalList().getAssetName());
        asset.setAssetGroup(selected.getArrivalList().getAssetGroup());
        asset.setSiteUID(selected.getArrivalList().getSiteUID());
        asset.setFunctionType(selected.getArrivalList().getFunctionType());
        asset.setClinicalDeptUID(selected.getClinicalDeptUID());
        asset.setSerialNum(selected.getSerialNum());
        asset.setDepartNum(selected.getDepartNum());
        asset.setFinancingNum(selected.getFinancingNum());
        asset.setModalityId(selected.getModalityId());
        asset.setSystemId(selected.getSystemId());
        return qrCodeService.getQrCodeImageBase64WithTemplate(asset, exportQrCodeTemplateId,selected.getArrivalList().getSiteUID() );
    }
    
    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    public String getArrivalFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varArrivalDateTo == null) {
            return arrivalFormatTime;
        } else {
            return "~" + sdf.format(varArrivalDateTo);
        }
    }

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    public Date getVarArrivalDateTo() {
        return varArrivalDateTo;
    }

    public void setVarArrivalDateTo(Date varArrivalDateTo) {
        this.varArrivalDateTo = varArrivalDateTo;
    }

    public OrgInfo getFileterDeptOrg() {
        return fileterDeptOrg;
    }

    public void setFileterDeptOrg(OrgInfo fileterDeptOrg) {
        this.fileterDeptOrg = fileterDeptOrg;
    }

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }
    public boolean activeSupplier(){
        return supplierService.activeSupplier();
    }

    public void getSavedArrivalContractNum(){
        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
        if(!orgUidList.isEmpty()){
            List<String> arrivalListContractNum = dao.getArrivalListNotInstalledContractNum(orgUidList,UserContextService.getCurrentUserAccount().getUid(),ArrivalList.SupplierStatus.Saved.name());
            mergeContractNum = arrivalListContractNum.toString().replace("[","").replace("]","");
        }
    }

    public String saveCompletedArrivedList(){
        ArrivalListItem item = dao.findById(selected.getId());
        ArrivalList arrivalList = item.getArrivalList();
        arrivalList.setAssetName(selected.getArrivalList().getAssetName());
        item.setBrand(selected.getBrand());
        item.setFunctionType(selected.getFunctionType());
        item.setImportEquipment(selected.getImportEquipment());
        item.setWarrantyMonth(selected.getWarrantyMonth());
        item.setAnnualMaintenanceTimes(selected.getAnnualMaintenanceTimes());
        item.setSerialNum(selected.getSerialNum());
        item.setLocationName(selected.getLocationName());
        arrivalListDao.save(arrivalList);
        dao.save(item);
        if(StringUtils.isNotBlank(item.getAssetUID())){
            aiService.updateAssetInfo(item);
        }
        return "arrivalItemsList?faces-redirect=true";
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }

        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void exportExcel() throws IOException {
        List<ArrivalListItem> arrivalListItems = dao.findBySearchFilter(this.searchFilters);
        downloadFile(parseArrivalItemsListToMap(arrivalListItems));
    }

    public List<Map<String, Object>> parseArrivalItemsListToMap(List<ArrivalListItem> itemList) {
        List<Map<String, Object>> listMapResult = new ArrayList<>();
        Set<String> userUidList = itemList.stream().map(ArrivalListItem::getCurrentPersonUID).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<UserAccount> users = userAccountRepository.getSimpleByUids(userUidList);
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(UserAccount::getUid, UserAccount::getName));
        Set<String> orgUidList = itemList.stream().map(ArrivalListItem::getClinicalDeptUID).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<OrgInfo> orgInfos = orgInfoDao.findByUids(orgUidList);
        Set<String> siteUidList = orgInfos.stream().map(OrgInfo::getSiteUID).collect(Collectors.toSet());
        orgInfos.addAll(orgInfoDao.findByUids(siteUidList));
        Map<String, OrgInfo> orgMap = orgInfos.stream().collect(Collectors.toMap(OrgInfo::getUid, o -> o));
        for (ArrivalListItem arrivalListItem : itemList) {
            Map<String, Object> tempMap = MapUtil.newHashMap();
            tempMap.put("到货单名称/编号", arrivalListItem.getArrivalList().getName());
            tempMap.put("合同号", arrivalListItem.getArrivalList().getContractNum());
            tempMap.put("资产名称", arrivalListItem.getArrivalList().getAssetName());
            OrgInfo orgInfo = orgMap.get(arrivalListItem.getClinicalDeptUID());
            tempMap.put("所属院区", Optional.ofNullable(orgInfo).map(OrgInfo::getSiteUID).map(orgMap::get).map(OrgInfo::getName).orElse(null));
            tempMap.put("所属科室", Optional.ofNullable(orgInfo).map(OrgInfo::getName).orElse(null));
            tempMap.put("创建人", arrivalListItem.getArrivalList().getCreator().getName());
            tempMap.put("经办人", arrivalListItem.getArrivalList().getAgent());
            tempMap.put("到货日期", DateUtil.format(arrivalListItem.getArrivalList().getArrivalDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("商务验收日期", DateUtil.format(arrivalListItem.getBusinessAckDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("设备型号", arrivalListItem.getFunctionType());
            tempMap.put("资产编号", arrivalListItem.getFinancingNum());
            tempMap.put("当前状态", FieldValueMessageController.fieldValue("arrivalListItemStatus", arrivalListItem.getInstallStatus()));
            tempMap.put("安装流程类型", FieldValueMessageController.fieldValue("arrivalListProcessType", arrivalListItem.getArrivalList().getProcessType()));
            tempMap.put("制造商", arrivalListItem.getArrivalList().getManufacture());
            tempMap.put("供应商", arrivalListItem.getArrivalList().getVendor());
            tempMap.put("制造商序列号", arrivalListItem.getSerialNum());
            tempMap.put("采购日期", DateUtil.format(arrivalListItem.getPurchaseDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("当前处理人", userMap.get(arrivalListItem.getCurrentPersonUID()));
            tempMap.put("安装工时", arrivalListItem.getWorkHours());
            tempMap.put("主设备", arrivalListItem.getArrivalList().getName());
            tempMap.put("设备别名", arrivalListItem.getAliasName());
            tempMap.put("设备编号（院方）", arrivalListItem.getDepartNum());
            tempMap.put("二维码", arrivalListItem.getQrCode());
            tempMap.put("出厂日期", DateUtil.format(arrivalListItem.getManufactDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("启用日期", DateUtil.format(arrivalListItem.getInstallDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("质保截止日期", DateUtil.format(arrivalListItem.getFactoryWarrantyDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("维保截止日期", DateUtil.format(arrivalListItem.getWarrantyDate(), DatePattern.NORM_DATE_PATTERN));
            tempMap.put("维修负责人", userMap.get(arrivalListItem.getAssetOwnerUID()));
            tempMap.put("维修负责人2", userMap.get(arrivalListItem.getAssetOwnerUID2()));
            tempMap.put("维修负责人3", userMap.get(arrivalListItem.getAssetOwnerUID3()));
            tempMap.put("使用责任人", userMap.get(arrivalListItem.getClinicalOwnerUid()));
            tempMap.put("安装位置编码", arrivalListItem.getLocationCode());
            tempMap.put("安装位置", arrivalListItem.getLocationName());
            tempMap.put("备注", arrivalListItem.getComments());
            listMapResult.add(tempMap);
        }
        return listMapResult;
    }

    private void downloadFile(List<Map<String, Object>> data) {
        try {//.getClassLoader().getResourceAsStream("/template/APM_AssetArrivalItem_Template.xlsx")
            InputStream stream = AssetArrivedItemsController.class.getClassLoader().getResourceAsStream("/template/APM_AssetArrivalItem_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = exportData(doc, data);
            if (Boolean.TRUE.equals(exportFlag)) {
                excelFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ArrivalItemsList.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {
        Integer keyRow = 1;
        int dataRow = 2;
        for (Map<String, Object> item : data) {
            doc.writeRowData("datalist", keyRow, dataRow++, item);
        }
        return Boolean.TRUE;
    }

    public String getMergeContractNum() {
        return mergeContractNum;
    }

    public void setMergeContractNum(String mergeContractNum) {
        this.mergeContractNum = mergeContractNum;
    }

    public void beforeUpdateInfo(){
        if(selected != null && StringUtils.isBlank(selected.getBrand())){
            selected.setBrand(selected.getArrivalList().getBrand());
        }
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }
}

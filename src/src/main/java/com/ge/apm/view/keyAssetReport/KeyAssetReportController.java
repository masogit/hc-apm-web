package com.ge.apm.view.keyAssetReport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.KeyAssetReportMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.keyAssetReport.KeyAssetReportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class KeyAssetReportController extends GenericCRUDUUIDController<KeyAssetReport> {

    private KeyAssetReportRepository dao;
    private KeyAssetReportOrgRepository keyAssetReportOrgRepository;
    private OrgInfoRepository orgDao;
    private AssetInfoRepository assetDao;
    private UserOrgInfoRepository userOrgInfoRepository;
    private OrgInfoRepository orgInfoRepository;

    private KeyAssetReportService service;

    private OrgInfo org;
    @Getter
    @Setter
    private TreeNode orgTree;
    @Getter
    @Setter
    private OrgInfo selectedDept;

    @Getter
    @Setter
    private TreeNode queryOrgTree;
    @Getter
    @Setter
    private OrgInfo dept;


    @Getter
    @Setter
    private Set<String> deptList;
    @Getter
    @Setter
    private Set<Integer> assetIds;

    @Getter
    @Setter
    private String assetName;
    @Getter
    @Setter
    private String assetGroup;
    @Getter
    @Setter
    private String functionType;
    @Getter
    @Setter
    private String departNum;
    @Getter
    @Setter
    private List<AssetInfo> allAssetList;// 待选设备列表
    @Getter
    @Setter
    private List<AssetInfo> addedAssetList;// 已添加设备列表

    @Getter
    @Setter
    private Map<String, Boolean> selectedAssetUid;// 待选设备列表，选中的设备uid
    @Getter
    @Setter
    private Set<AssetInfo> selectedAssetList;// 待选设备列表，选中的设备
    @Getter
    @Setter
    private Map<String, Boolean> addedAssetUid;// 已添加设备列表，选中的设备uid
    @Getter
    @Setter
    private Set<AssetInfo> addedSelectAssetList;// 已添加设备列表，选中的设备

    @Getter
    @Setter
    private Boolean allListSelected = Boolean.FALSE;// 待选设备列表，是否全部选中
    @Getter
    @Setter
    private Boolean cancelAllSelected = Boolean.FALSE;// 已添加设备列表，是否全部选中

    @Getter
    @Setter
    private Set<Map<String, Object>> assetDetail;
    @Getter
    @Setter
    private List<Map<String, String>> assetOperation;
    @Getter
    @Setter
    private List<Map<String, String>> assetOperationForYear;
    @Getter
    @Setter
    private Map<String, String> assetOperationDetail;
    @Getter
    @Setter
    private String operationType;
    @Getter
    @Setter
    private String filterFillTime = null;
    @Getter
    @Setter
    private Integer operationOrder;

    @Getter
    @Setter
    private String searchDeptName;

    private String previewUrl;
    private String previewUrlForYear;

    private UaaService uaaService;

    private KeyAssetReportMapper keyAssetReportMapper;
    private SysAuthGroupRepository sysAuthGroupDao;
    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;
    private JmReportRepository reportRepository;

    private UserAccount account;

    @Override
    protected void init() {
        dao = WebUtil.getBean(KeyAssetReportRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        service = WebUtil.getBean(KeyAssetReportService.class);
        userOrgInfoRepository = WebUtil.getBean(UserOrgInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        keyAssetReportMapper = WebUtil.getBean(KeyAssetReportMapper.class);
        keyAssetReportOrgRepository = WebUtil.getBean(KeyAssetReportOrgRepository.class);
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);
        org = orgDao.findById(UserContextService.getCurrentUserAccount().getOrgInfoId());
        sysAuthGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);
        account = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
//        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4));
        queryOrgTree = initQueryOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 3));
        reportRepository = WebUtil.getBean(JmReportRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
       /* previewUrl = request.getScheme()+"://"+request.getServerName() + "/apmReport/jmreport/view" +
                "/1019468057985105920?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&id=";
        previewUrlForYear = request.getScheme()+"://"+request.getServerName() + "/apmReport/jmreport/view" +
                "/1020890977340911616?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&id=";*/
        queryReport();

    }

    @Override
    protected GenericRepositoryUUID<KeyAssetReport> getDAO() {
        return dao;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        selectedDept = null;
        super.prepareCreate();
        this.selected.setFormFillTime(new Date());
        this.selected.setReportType(1);
        this.selected.setFormFiller(UserContextService.getCurrentUserAccount().getName());
        assetDetail = null;
        assetOperationDetail = null;
        assetOperationForYear = new ArrayList<>();
        initAssetOperation();
        operationOrder = 1;
        allAssetList = new ArrayList<>();
        selectedAssetList = new HashSet<>();
        addedAssetList = new ArrayList<>();
        deptList = new HashSet<>();
        assetIds = new HashSet<>();
    }

    @Override
    public void prepareEdit() {
        initAssetOperation();
        selectedDept = selected.getClinicalDept();
        if (selected.getAssetDetail() != null) {
            assetDetail = JSON.parseObject(selected.getAssetDetail(), Set.class);

        } else {
            assetDetail = new HashSet<>();
        }
        assetOperationDetail = null;
        List<KeyAssetReportOrg> byKeyAssetReportId = keyAssetReportOrgRepository.findByKeyAssetReportId(selected.getId());
        deptList = byKeyAssetReportId.stream().map(KeyAssetReportOrg::getOrgId).collect(Collectors.toSet());
        if (1 == this.selected.getReportType()) {
            assetOperation = JSON.parseObject(selected.getAssetOperation(), List.class);
            assetOperationForYear = new ArrayList<>();
            operationOrder = 1;
        } else if (2 == this.selected.getReportType()) {
            initAssetOperation();
            assetOperationForYear = JSON.parseObject(selected.getAssetOperation(), List.class);
            for (Map<String, String> stringStringMap : assetOperationForYear) {
                String netInCome = netIncomeComputing(stringStringMap);
                if (Objects.nonNull(netInCome)) {
                    stringStringMap.put("annualNetIncome", netInCome);
                }

            }

            operationOrder = assetOperationForYear.size() + 1;
        }
        assetIds = new HashSet<>();
        for (Map<String, Object> stringObjectMap : assetDetail) {
            if (stringObjectMap.get("assetId") != null) {
                String stringIds = (String) stringObjectMap.get("assetId");
                List<Integer> ids = Arrays.stream(stringIds.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt) // 转换为Integer
                        .collect(Collectors.toList());
                assetIds.addAll(ids);
            }
        }

        super.prepareEdit();
    }

    public void initAssetOperation() {
        assetOperation = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("month", i + "月");
            map.put("order", i + "");
            assetOperation.add(map);
        }
    }

    public void preCreateOperation() {
        operationType = "Create";
        assetOperationDetail = new HashMap<>();
    }

    public void preEditOperation() {
        operationType = "Edit";
    }

    public void deleteOperation() {
        Iterator<Map<String, String>> iterator = assetOperationForYear.iterator();

        while (iterator.hasNext()) {
            Map<String, String> next = iterator.next();
            if (next.get("month").equals(assetOperationDetail.get("month"))) {
                iterator.remove();
                break;
            }
        }
        assetOperationDetail = null;
    }

    public void saveAssetOperation() {
        if ("Create".equals(operationType)) {
            for (Map<String, String> map : assetOperationForYear) {
                if (map.get("month").equals(assetOperationDetail.get("month"))) {
                    WebUtil.addErrorMessage("年份不能重复！");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                }
            }
            assetOperationDetail.put("order", operationOrder + "");
            assetOperationForYear.add(assetOperationDetail);
            operationOrder += 1;
        }
        //年收入
        BigDecimal annualIncome = Objects.nonNull(assetOperationDetail.get("annualIncome")) ? BigDecimal.valueOf(Double.parseDouble(assetOperationDetail.get("annualIncome"))) : BigDecimal.valueOf(0.0);
        //折旧
        BigDecimal depreciation = Objects.nonNull(assetOperationDetail.get("depreciation")) ? BigDecimal.valueOf(Double.parseDouble(assetOperationDetail.get("depreciation"))) : BigDecimal.valueOf(0.0);
        //年维修支出
        BigDecimal annualRepairExpenses = Objects.nonNull(assetOperationDetail.get("annualRepairExpenses")) ? BigDecimal.valueOf(Double.parseDouble(assetOperationDetail.get("annualRepairExpenses"))) : BigDecimal.valueOf(0.0);
        //年维保支出
        BigDecimal annualMaintenanceExpenses = Objects.nonNull(assetOperationDetail.get("annualMaintenanceExpenses")) ? BigDecimal.valueOf(Double.parseDouble(assetOperationDetail.get("annualMaintenanceExpenses"))) : BigDecimal.valueOf(0.0);
        // 年耗材支出
        BigDecimal annualConsumablesExpenses = Objects.nonNull(assetOperationDetail.get("annualConsumablesExpenses")) ? BigDecimal.valueOf(Double.parseDouble(assetOperationDetail.get("annualConsumablesExpenses"))) : BigDecimal.valueOf(0.0);
        BigDecimal netIncome = annualIncome.subtract(depreciation.add(annualRepairExpenses).add(annualMaintenanceExpenses).add(annualConsumablesExpenses));
        //净收入
        String netIncomeStr = netIncome.toString();
        if (netIncomeStr.endsWith(".0")) {
            assetOperationDetail.put("annualNetIncome", String.valueOf(netIncome.longValue()));
        } else {
            assetOperationDetail.put("annualNetIncome", netIncome.toPlainString());
        }

    }

    @Override
    protected Page<KeyAssetReport> loadData(PageRequest pageRequest) {

        if (CollectionUtils.isEmpty(this.searchFilters)) {
            this.searchFilters = new ArrayList<>();
        }
        Set<String> orgUids = getOrgUidListByUserUid(UserContextService.getCurrentUserAccount().getUid());
        if (CollectionUtils.isNotEmpty(orgUids)) {
//            searchFilters.add(new SearchFilter("deptId", SearchFilter.Operator.IN, idList));
        } else {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }
//        List<KeyAssetReport> keyAssetReports = dao.selectList();
//        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
//        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        List<SearchFilter> searchFilters = this.searchFilters;
        Map<String, Object> param = new HashMap<>();
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        param.put("deptId", orgUids);
        param.put("tenantUid", UserContextService.getCurrentUserAccount().getTenantUID());

        covQueryCondition(searchFilters, param);
        List<KeyAssetReport> list = keyAssetReportMapper.findList(param);
        Long count = keyAssetReportMapper.findcount(param);
        return new PageImpl<>(list, pageRequest, count);
//        return dao.findBySearchFilter(searchFilters, page);
    }

    private Set<Integer> findOrgUidListByUserUid(String userUid) {
        Set<String> listResult = new HashSet<String>();
        List<UserOrgInfo> listUserOrgInfo = userOrgInfoRepository.findOrgUidListByUserUid(userUid);
        if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
            for (int i = 0; i < listUserOrgInfo.size(); ++i) {
                listResult.add(listUserOrgInfo.get(i).getOrgUid());
            }
        }
        if (CollectionUtils.isNotEmpty(listResult)) {
            Set<String> tempResult = orgInfoRepository.findClinicalDeptOrgUidListByOrgUid(listResult);
            if (tempResult != null) {
                listResult.addAll(tempResult);
            }
        }
        if (CollectionUtils.isNotEmpty(listResult)) {
            return orgInfoRepository.findIdListByUid(listResult);
        }
        return new HashSet<>();
    }

    private Set<String> getOrgUidListByUserUid(String userUid) {
        Set<String> listResult = new HashSet<String>();
        List<UserOrgInfo> listUserOrgInfo = userOrgInfoRepository.findOrgUidListByUserUid(userUid);
        if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
            for (int i = 0; i < listUserOrgInfo.size(); ++i) {
                listResult.add(listUserOrgInfo.get(i).getOrgUid());
            }
        }
        if (CollectionUtils.isNotEmpty(listResult)) {
            Set<String> tempResult = orgInfoRepository.findClinicalDeptOrgUidListByOrgUid(listResult);
            if (tempResult != null) {
                listResult.addAll(tempResult);
            }
        }
        if (CollectionUtils.isNotEmpty(listResult)) {
            return listResult;
        }
        return new HashSet<>();
    }

    @Transactional
    public void saveKeyAssetReport() {
        if (null == this.selected.getReportYear()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("reportYear") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (StringUtils.isBlank(this.selected.getAssetName())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("EquipmentName") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (StringUtils.isBlank(this.selected.getFormFiller())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("formFiller") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (null == this.selected.getFormFillTime()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("formFillTime") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
//        if(null == this.selected.getClinicalDept()){
//            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
////            WebUtil.addErrorMessage("请选择科室并选择设备!");
//            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//            return;
//        }
//        if(CollectionUtils.isEmpty(deptList)){
//            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
//            WebUtil.addErrorMessage("请选择科室并选择设备!");
//        }
        int countReport = 0;
        if ("Create".equals(this.crudActionName)) {
            countReport = dao.countByReport(this.selected.getAssetName(), this.selected.getAssetDeptName(), this.selected.getReportYear(), this.selected.getReportType());
        } else if ("Edit".equals(this.crudActionName)) {
            countReport = dao.countByReportAndId(this.selected.getAssetName(), this.selected.getAssetDeptName(),
                    this.selected.getReportYear(), this.selected.getId(), this.selected.getReportType());
        }
        if (countReport > 0) {
            WebUtil.addErrorMessage("年份+科室+设备名称+报表类型重复,不能提交!");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
//        if (null == assetDetail) {
//            WebUtil.addErrorMessage("请选择设备!");
//            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//            return;
//        }
        /*else{
            for (Map<String, Object> map : assetDetail) {
                if(null == map.get("price") || null == map.get("depreciation") || null == map.get("repairSpend")
                    || null == map.get("maintenanceSpend") || null == map.get("suppliesSpend")){
                    WebUtil.addErrorMessage("设备明细数据录入不全,请检查!");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                }
            }
        }*/
        if (2 == this.selected.getReportType()) {
            if (CollectionUtils.isEmpty(assetOperation)) {
                WebUtil.addErrorMessage("请录入设备运营情况数据!");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
       /* if(1 == this.selected.getReportType()){
            for (Map<String, String> map : assetOperation) {
                if((null == map.get("checkPeopleNum") && null != map.get("sumIncome")) ||
                        (null != map.get("checkPeopleNum") && null == map.get("sumIncome"))){
                    WebUtil.addErrorMessage("设备运营情况数据录入不全,请检查!");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                }
            }
        }else if(2 == this.selected.getReportType()){
            for (Map<String, String> map : assetOperationForYear) {
                if(null == map.get("month") || null == map.get("checkPeopleNum") || null == map.get("sumIncome")){
                    WebUtil.addErrorMessage("设备运营情况数据录入不全,请检查!");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                }
            }
        }*/
        if ("Create".equals(crudActionName)) {
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            this.selected.setTenantUID(userAccount.getTenantUID());
            this.selected.setInstitutionUID(userAccount.getInstitutionUID());
            this.selected.setHospitalUID(userAccount.getHospitalUID());
            this.selected.setSiteUID(userAccount.getSiteUID());
            this.selected.setCreator(userAccount.getName());
            this.selected.setCreateTime(new Date());
        }
        if (assetDetail != null) {
            this.selected.setAssetDetail(JSON.toJSONStringWithDateFormat(assetDetail, "yyyy-MM-dd", SerializerFeature.WriteDateUseDateFormat));
        }
        if (1 == this.selected.getReportType()) {
            dataFormat(assetOperation);
            this.selected.setAssetOperation(JSON.toJSONString(assetOperation));
        } else if (2 == this.selected.getReportType()) {
            this.selected.setAssetOperation(JSON.toJSONString(assetOperationForYear));
        }
        dao.save(this.selected);
        if ("Edit".equals(this.crudActionName)) {
            keyAssetReportOrgRepository.deleteByKeyAssetReportId(selected.getId());
        }
        ArrayList<KeyAssetReportOrg> keyAssetReportOrgs = new ArrayList<>();
        for (String orgUid : deptList) {
            KeyAssetReportOrg keyAssetReportOrg = new KeyAssetReportOrg();
            keyAssetReportOrg.setKeyAssetReportId(selected.getId());
            keyAssetReportOrg.setOrgId(orgUid);
            keyAssetReportOrgs.add(keyAssetReportOrg);
        }
        if (CollectionUtils.isNotEmpty(keyAssetReportOrgs)) {
            keyAssetReportOrgRepository.save(keyAssetReportOrgs);
        }
        deptList = new HashSet<>();
        assetIds = new HashSet<>();

    }

    @Override
    public void delete() {
        dao.delete(this.selected);
        keyAssetReportOrgRepository.deleteByKeyAssetReportId(selected.getId());
        this.selected = null;
        WebUtil.addSuccessMessage(WebUtil.getMessage("DeleteSuccess"));
    }

    public void saveAssetList() {
        if (CollectionUtils.isEmpty(assetDetail)) {
            assetDetail = new HashSet<>();
        }
        if (CollectionUtils.isEmpty(deptList)) {
            deptList = new HashSet<>();
        }
        List<AssetInfo> filterAssetList = addedAssetList;
        if (CollectionUtils.isNotEmpty(assetIds)) {
            filterAssetList = addedAssetList.stream().filter(item -> !assetIds.contains(item.getId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(filterAssetList)) {
            return;
        }
        assetDetail = service.groupAsset(filterAssetList, assetDetail);
        if (CollectionUtils.isEmpty(assetIds)) {
            assetIds = new HashSet<>();
        }
        assetIds.addAll(filterAssetList.stream().map(AssetInfo::getId).collect(Collectors.toSet()));
        Set<String> clinicalDeptUids = filterAssetList.stream().map(AssetInfo::getClinicalDeptUID).collect(Collectors.toSet());
        deptList.addAll(clinicalDeptUids);
    }

    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(Objects.equals(org.getOrgType(), 4));
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    public TreeNode initQueryOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode queryOrgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, queryOrgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(true);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return queryOrgTree;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedDept = (OrgInfo) node.getData();
        this.selected.setClinicalDept(selectedDept);
        this.selected.setDeptId(selectedDept.getId());
    }

    public void onSelectDeptNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        dept = (OrgInfo) node.getData();
    }

    public void resetSelectAssetPage() {
        dept = null;
        allAssetList = new ArrayList<>();
        addedAssetList = new ArrayList<>();
        selectedAssetUid = new HashMap<>(32);
        selectedAssetList = new HashSet<>();
        addedAssetUid = new HashMap<>(32);
        addedSelectAssetList = new HashSet<>();
        allListSelected = Boolean.FALSE;
        cancelAllSelected = Boolean.FALSE;
        assetName = "";
        assetGroup = "";
        functionType = "";
        departNum = "";
    }

    public void findAssetList() {
        List<SearchFilter> assetSearchFilter = new ArrayList<>();
        if (Objects.nonNull(medicalInstrumentsClassificationCompCtrl.getSelectedSysAuthGroup())) {
            String classificationCode = medicalInstrumentsClassificationCompCtrl.getSelectedSysAuthGroup().getClassificationCode();
            assetSearchFilter.add(new SearchFilter("medicalApparatusClassification", SearchFilter.Operator.EQ, classificationCode, "1*"));

        }
        //assetSearchFilter.add(new SearchFilter("status", SearchFilter.Operator.NE, "4", "1*"));
        assetSearchFilter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, dept.getInstitutionUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, dept.getHospitalUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, dept.getSiteUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IsNotNull, null, "1*"));
        if (Objects.equals(dept.getOrgType(), 4)) {
            List<OrgInfo> userAccessibleSiteListWithClinicalDept = uaaService.getUserAccessibleSiteListWithClinicalDept(account.getUid());
            Set<OrgInfo> clinicalDeptSet = new HashSet<>();
            collectChildClinicalDeptInExstingList(dept, userAccessibleSiteListWithClinicalDept, clinicalDeptSet);
            if (CollectionUtils.isNotEmpty(clinicalDeptSet)) {
                Set<String> clinicalDeptUids = clinicalDeptSet.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
                assetSearchFilter.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptUids), "1*"));

            }
//            assetSearchFilter.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, dept.getUid(), "1*"));

        }
        assetSearchFilter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        assetSearchFilter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));

        if (StringUtils.isNotBlank(this.getDepartNum())) {
            assetSearchFilter.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, this.getDepartNum(), "1*"));
        } else {
            if (StringUtils.isNotBlank(this.getAssetName())) {
                assetSearchFilter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
                assetSearchFilter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
            }
            if (StringUtils.isNotBlank(this.getAssetGroup())) {
                assetSearchFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.getAssetGroup(), "1*"));
            }
            if (StringUtils.isNotBlank(this.getFunctionType())) {
                assetSearchFilter.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, this.getFunctionType(), "1*"));
            }
        }

        allAssetList = assetDao.findBySearchFilter(assetSearchFilter);
        if (CollectionUtils.isNotEmpty(assetIds)) {
            allAssetList = allAssetList.stream().filter(item -> !assetIds.contains(item.getId())).collect(Collectors.toList());
        }
    }


    public void collectChildClinicalDeptInExstingList(OrgInfo parentOrg, List<OrgInfo> clinicalDeptList, Set<OrgInfo> clinicalDeptSetOutput) {

        clinicalDeptSetOutput.add(parentOrg);

        for (OrgInfo orgInfo : clinicalDeptList) {
            if (!orgInfo.getParentUID().equals(parentOrg.getUid())) continue;
            collectChildClinicalDeptInExstingList(orgInfo, clinicalDeptList, clinicalDeptSetOutput);

        }


    }

    // 待选设备列表，选中/取消选中
    public void updateSelectedAssetList(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox) event.getSource();
        AssetInfo asset = (AssetInfo) box.getAttributes().get("assetObj");
        if (box.isSelected()) {
            selectedAssetList.add(asset);
        } else {
            selectedAssetList.remove(asset);
        }
    }

    // 已添加设备列表，选中/取消选中
    public void updateAddedAssetList(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox) event.getSource();
        AssetInfo asset = (AssetInfo) box.getAttributes().get("assetObj");
        if (box.isSelected()) {
            addedSelectAssetList.add(asset);
        } else {
            addedSelectAssetList.remove(asset);
        }
    }

    // 待选设备列表，全选
    public void addAllAssetsOfCurrentPage() {
        if (allListSelected) {
            allAssetList.stream().forEach(t -> {
                selectedAssetUid.put(t.getUid(), Boolean.TRUE);
                selectedAssetList.add(t);
            });
        } else {
            allAssetList.stream().forEach(t -> {
                selectedAssetUid.put(t.getUid(), Boolean.FALSE);
                selectedAssetList.remove(t);
            });
        }
        allListSelected = !allListSelected;
    }

    // 已添加设备列表，全选
    public void addAddedAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        if (cancelAllSelected) {
            addedAssetList.stream().forEach(t -> {
                addedAssetUid.put(t.getUid(), Boolean.TRUE);
                addedSelectAssetList.add(t);
            });
        } else {
            addedAssetList.stream().forEach(t -> {
                addedAssetUid.put(t.getUid(), Boolean.FALSE);
                addedSelectAssetList.remove(t);
            });
        }
        cancelAllSelected = !cancelAllSelected;
    }

    public void addAssetInfo() {
        for (AssetInfo assetInfo : selectedAssetList) {
            if (!addedAssetList.contains(assetInfo)) {
                addedAssetList.add(assetInfo);
            }
        }
    }

    public void removeAssetInfo() {
        addedAssetList.removeAll(addedSelectAssetList);
        addedSelectAssetList.clear();
        addedAssetUid.clear();
    }

    public void clearAssetInfo() {
        addedAssetList.clear();
        addedAssetUid.clear();
        addedSelectAssetList.clear();
    }

    public String preview(String id, Integer type) {
        if (1 == type) {
            return previewUrl + id;
        } else {
            return previewUrlForYear + id;
        }
    }

    public void treeSeachCondition() {
        UserAccount account = UserContextService.getCurrentUserAccount();
        queryOrgTree = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 3));
        if (StrUtil.isNotBlank(searchDeptName)) {
            uaaService.filterTreeNode(queryOrgTree, searchDeptName, -1, -1);
        }
    }


    public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter searchFilter : searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "reportYear":
                    param.put("reportYear", Integer.parseInt(searchFilter.value.toString()));
                    break;
                case "reportType":
                    param.put("reportType", Integer.parseInt(searchFilter.value.toString()));
                    break;
                case "assetName":
                    param.put("assetName", searchFilter.value);
                    break;
                case "assetDeptName":
                    param.put("assetDeptName", searchFilter.value);
                    break;
                case "formFiller":
                    param.put("formFiller", searchFilter.value);
                    break;
                case "formFillTime":
                    if (searchFilter.operator.name().equals("GTE")) {
                        param.put("formFillTime", searchFilter.value);
                    } else {
                        param.put("formFillTime", searchFilter.value);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public String getMedicalApparatusClassification(String medicalApparatusClassification) {
        if (StringUtils.isNotBlank(medicalApparatusClassification)) {
            SysAuthGroup classificationCode = sysAuthGroupDao.findByClassificationCode(medicalApparatusClassification);
            if (Objects.nonNull(classificationCode)) {
                return classificationCode.getDisplayName();
            }
        }
        return null;
    }

    /**
     * 获取积木报表的url
     */
    public void queryReport() {
        JmReport reportYear = reportRepository.findReportByName("重点医疗设备使用状况报表(年)-2024");
        JmReport reportMonth = reportRepository.findReportByName("重点医疗设备使用状况报表(月)-2024");
        if (Objects.nonNull(reportYear)) {
            previewUrlForYear = reportYear.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&id=";
        }
        if (Objects.nonNull(reportMonth)) {
            previewUrl = reportMonth.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&id=";
        }

    }

    public void dataFormat(List<Map<String, String>> dataList) {
        for (Map<String, String> data : dataList) {
            String income = data.get("income");
            String repairSpend = data.get("repairSpend");
            String numberOfCheck = data.get("numberOfCheck");
            String maintenanceSpend = data.get("maintenanceSpend");
            String netIncome = data.get("netIncome");
            String depreciation = data.get("depreciation");
            String suppliesSpend = data.get("suppliesSpend");
            if (income == null && repairSpend == null && numberOfCheck == null && maintenanceSpend == null && netIncome == null && depreciation == null && suppliesSpend == null) {
                data.put("validData", "0");
            } else {
                data.put("validData", "1");
            }
        }
    }

    @Transactional
    public void deleteAsset(Map<String, Object> map) {
        logger.info(JSON.toJSON(map));

        String key = (String) map.get("key");
        String deptUids = (String) map.get("deptUid");
        String deleteAssetIds = (String) map.get("assetId");

        List<Integer> ids = Arrays.stream(deleteAssetIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        ids.forEach(assetIds::remove);
        Set<Integer> filterIds = new HashSet<>(assetIds);

        List<String> deleteDeptUids = Arrays.asList(deptUids.split(","));

        assetDetail = assetDetail.stream().filter(item -> !item.get("key").equals(key)).collect(Collectors.toSet());

        Set<String> clinicalDeptUids = new HashSet<>();
        for (Map<String, Object> stringObjectMap : assetDetail) {
            String deptUidString = (String) stringObjectMap.get("deptUid");
            List<String> deptUidList = Arrays.asList(deptUidString.split(","));
            clinicalDeptUids.addAll(deptUidList);

        }
        List<String> deleteDeptUidList = new ArrayList<>(deleteDeptUids);
        deleteDeptUidList.removeAll(clinicalDeptUids);
        if (selected.getId() != null) {
            keyAssetReportOrgRepository.deleteByKeyAssetReportIdAndOrgIdIn(selected.getId(), deleteDeptUidList);
            saveKeyAssetReport();
        }
        assetIds = filterIds;
        deptList = clinicalDeptUids;
    }

    public String netIncomeComputing(Map<String, String> data) {
        String annualNetIncome = data.get("annualNetIncome");
        if (Objects.nonNull(annualNetIncome))
            return null;
        //年收入
        BigDecimal annualIncome = Objects.nonNull(data.get("annualIncome")) ? BigDecimal.valueOf(Double.parseDouble(data.get("annualIncome"))) : BigDecimal.valueOf(0.0);
        //折旧
        BigDecimal depreciation = Objects.nonNull(data.get("depreciation")) ? BigDecimal.valueOf(Double.parseDouble(data.get("depreciation"))) : BigDecimal.valueOf(0.0);
        //年维修支出
        BigDecimal annualRepairExpenses = Objects.nonNull(data.get("annualRepairExpenses")) ? BigDecimal.valueOf(Double.parseDouble(data.get("annualRepairExpenses"))) : BigDecimal.valueOf(0.0);
        //年维保支出
        BigDecimal annualMaintenanceExpenses = Objects.nonNull(data.get("annualMaintenanceExpenses")) ? BigDecimal.valueOf(Double.parseDouble(data.get("annualMaintenanceExpenses"))) : BigDecimal.valueOf(0.0);
        // 年耗材支出
        BigDecimal annualConsumablesExpenses = Objects.nonNull(data.get("annualConsumablesExpenses")) ? BigDecimal.valueOf(Double.parseDouble(data.get("annualConsumablesExpenses"))) : BigDecimal.valueOf(0.0);
        BigDecimal netIncome = annualIncome.subtract(depreciation.add(annualRepairExpenses).add(annualMaintenanceExpenses).add(annualConsumablesExpenses));
        //净收入
        String netIncomeStr = netIncome.toString();
        if (netIncomeStr.endsWith(".0")) {
            return String.valueOf(netIncome.longValue());
        } else {
            return netIncome.toPlainString();
        }
    }

}

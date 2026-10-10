package com.ge.apm.view.surgeryAnesthesia;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentInfoRepository;
import com.ge.apm.dao.mapper.SurgeryAnesthesiaEquipmentInfoMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentInfo;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 手术室绩效管理 - 设备绩效分析Controller
 * 用于管理手术室绩效设备、设置阳性率、展示设备详细绩效
 */
@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaEquipmentInfoController extends GenericCRUDUUIDController<SurgeryAnesthesiaEquipmentInfo> {

    private SurgeryAnesthesiaEquipmentInfoRepository equipmentInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private SurgeryAnesthesiaEquipmentInfoMapper equipmentInfoMapper;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;

    private UserAccount user;

    @Getter @Setter
    private OrgInfo selectSite;

    @Getter @Setter
    private Date startDate ;

    @Getter @Setter
    private Date endDate = new Date();

    @Getter @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();

    @Getter @Setter
    private String batchDisplayName;

    @Getter @Setter
    private BigDecimal batchPositiveRate;

    @Getter @Setter
    private String filterClinicalDeptOrg;

    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;

    @Getter @Setter
    private Set<String> clinicalDeptOrgUidSet;

    private List<SurgeryAnesthesiaEquipmentInfo> currentPageAssets;

    @Override
    protected SurgeryAnesthesiaEquipmentInfoRepository getDAO() {
        return equipmentInfoRepository;
    }

    @Override
    protected void init() {
        equipmentInfoRepository = WebUtil.getBean(SurgeryAnesthesiaEquipmentInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        equipmentInfoMapper = WebUtil.getBean(SurgeryAnesthesiaEquipmentInfoMapper.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        user = UserContextService.getCurrentUserAccount();

        startDate = DateUtil.addMonth(new Date(), -3);
        loadClinicalDeptList();
    }

    @Override
    protected Page<SurgeryAnesthesiaEquipmentInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("tenantUid", user.getTenantUID());
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        param.put("pageSize", pageRequest.getPageSize());
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(orgUidList)){
            param.put("orgUidList",orgUidList);
        }
        if (selectSite != null) {
            param.put("siteUid", selectSite.getUid());
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            param.put("clinicalDeptUids", clinicalDeptOrgUidSet);
        }

        // 将 searchFilters 中的过滤条件转换到 param map
        covQueryCondition(searchFilters, param);

        // 排序
        Sort sort = pageRequest.getSort();
        if (sort != null && sort.iterator().hasNext()) {
            StringBuilder orderBy = new StringBuilder();
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                String dir = order.isAscending() ? " ASC" : " DESC";
                switch (property) {
                    case "assetName":
                    case "displayName":
                    case "functionType":
                    case "departNum":
                    case "makerShortName":
                    case "serialNum":
                    case "qrCode":
                    case "clinicalDeptName":
                    case "assetGroup":
                    case "totalUsageCount":
                    case "totalOccupationDays":
                    case "operatingRoomCoverageCount":
                    case "positiveRate":
                        orderBy.append("\"").append(property).append("\"").append(dir).append(",");
                        break;
                    case "uid":
                        // uid 排序会导致 PG 使用 PK 索引做 Index Scan，强制走 Nested Loop Join，禁用
                        break;
                    case "createdDate":
                        orderBy.append("e.created_date").append(dir).append(",");
                        break;
                    default:
                        break;
                }
            }
            if (orderBy.length() > 0) {
                param.put("orderBy", orderBy.substring(0, orderBy.length() - 1));
            }
        }

        List<SurgeryAnesthesiaEquipmentInfo> list = new ArrayList<>(equipmentInfoMapper.selectList(param));
        Long count = equipmentInfoMapper.selectListCount(param);
        currentPageAssets = list;
        return new PageImpl<>(list, pageRequest, count);
    }

    /**
     * 将 DataTable 的 SearchFilter 转换为 MyBatis 参数
     */
    private void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter sf : searchFilters) {
            String fieldName = sf.fieldName;
            switch (fieldName) {
                case "assetName":
                    param.put("assetName", sf.value);
                    break;
                case "displayName":
                    param.put("displayName", sf.value);
                    break;
                case "functionType":
                    param.put("functionType", sf.value);
                    break;
                case "departNum":
                    param.put("departNum", sf.value);
                    break;
                case "makerShortName":
                    param.put("makerShortName", sf.value);
                    break;
                case "serialNum":
                    param.put("serialNum", sf.value);
                    break;
                case "qrCode":
                    param.put("qrCode", sf.value);
                    break;
                case "clinicalDeptName":
                    param.put("clinicalDeptName", sf.value);
                    break;
                case "assetGroup":
                    try {
                        param.put("assetGroup", Integer.parseInt(sf.value.toString()));
                    } catch (Exception e) {
                        logger.warn("assetGroup 搜索数值转换异常", e);
                    }
                    break;
                case "positiveRate":
                    try {
                        double num = Double.parseDouble(sf.value.toString());
                        if ("GTE".equals(sf.operator.name())) {
                            param.put("positiveRateGTE", num);
                        } else {
                            param.put("positiveRateLTE", num);
                        }
                    } catch (Exception e) {
                        logger.warn("positiveRate 数值转换异常", e);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 批量编辑前重置表单字段
     */
    public void editBatch1() {
        batchDisplayName = null;
        batchPositiveRate = null;
    }

    /**
     * 批量编辑保存
     */
    public void editBatch() {
        if (batchPositiveRate != null && batchPositiveRate.compareTo(BigDecimal.valueOf(100)) > 0) {
            WebUtil.addErrorMessage("阳性率不能超过100");
            return;
        }
        List<String> selectedUids = getSelectedAssetUids();
        if (CollectionUtils.isEmpty(selectedUids)) {
            WebUtil.addErrorMessage("请选择要修改的设备");
            return;
        }
        if (batchDisplayName == null && batchPositiveRate == null) {
            WebUtil.addErrorMessage("请至少填写一项修改内容");
            return;
        }
        int count = equipmentInfoMapper.updateBatch(selectedUids, batchDisplayName, batchPositiveRate);
        WebUtil.addSuccessMessage("成功修改 " + count + " 台设备");
        batchDisplayName = null;
        batchPositiveRate = null;
    }

    /**
     * 批量添加绩效设备
     */
    public void addBatch(List<AssetInfo> selectedAssets) {
        if (CollectionUtils.isEmpty(selectedAssets)) {
            WebUtil.addErrorMessage("请选择要添加的设备");
            return;
        }

        List<SurgeryAnesthesiaEquipmentInfo> toSave = new ArrayList<>();
        for (AssetInfo asset : selectedAssets) {
            // 检查是否已存在
            SurgeryAnesthesiaEquipmentInfo existing = equipmentInfoRepository.findByTenantUidAndAssetUid(user.getTenantUID(), asset.getUid());
            if (existing != null) {
                continue; // 已存在，跳过
            }

            SurgeryAnesthesiaEquipmentInfo info = new SurgeryAnesthesiaEquipmentInfo();
            info.setAssetUid(asset.getUid());
            info.setAssetName(asset.getName());
            info.setDisplayName(asset.getName());
            info.setFunctionType(asset.getFunctionType());
            info.setDepartNum(asset.getDepartNum());
            info.setSerialNum(asset.getSerialNum());
            info.setQrCode(asset.getQrCode());
            info.setPositiveRate(BigDecimal.ZERO);
            info.setTenantUid(user.getTenantUID());
            info.setSiteUid(asset.getSiteUID());
            info.setCreatedByUid(user.getUid());
            info.setCreatedDate(new Date());
            toSave.add(info);
        }

        if (!toSave.isEmpty()) {
            equipmentInfoRepository.save(toSave);
            WebUtil.addSuccessMessage("成功添加 " + toSave.size() + " 台绩效设备");
        } else {
            WebUtil.addErrorMessage("所选设备均已添加过");
        }
    }

    /**
     * 删除绩效设备
     */
    public void deleteAsset(SurgeryAnesthesiaEquipmentInfo info) {
        equipmentInfoRepository.delete(info.getUid());
        WebUtil.addSuccessMessage("删除成功");
    }

    /**
     * 更新阳性率
     */
    public void updatePositiveRate(SurgeryAnesthesiaEquipmentInfo info) {
        equipmentInfoRepository.save(info);
        WebUtil.addSuccessMessage("阳性率更新成功");
    }

    /**
     * 检查是否有选中设备
     */
    public boolean isChecked() {
        return selectedMap != null && selectedMap.values().stream().anyMatch(v -> v);
    }

    /**
     * 获取选中的设备UID列表
     */
    public List<String> getSelectedAssetUids() {
        if (selectedMap == null) return Collections.emptyList();
        return selectedMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 选中行事件
     */
    public void onSelectCurrent(String uid) {
        if (selectedMap != null) {
            selectedMap.put(uid, !selectedMap.getOrDefault(uid, false));
        }
    }

    /**
     * 全选当前页
     */
    public void onSelectAllOfPage() {
        if (currentPageAssets == null) return;
        boolean isSelectedAll = !currentPageAssets.stream()
                .anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (SurgeryAnesthesiaEquipmentInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    /**
     * 加载科室列表
     */
    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
    }

    /**
     * 科室名称自动完成
     */
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

    /**
     * 科室过滤变更事件
     */
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

    /**
     * 院区树节点选择事件
     */
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }
        loadClinicalDeptList();
    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }
        String url= "assetUid=" + selected.getAssetUid();
        url = "SurgeryAnesthesiaEquipmentInfoDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

}
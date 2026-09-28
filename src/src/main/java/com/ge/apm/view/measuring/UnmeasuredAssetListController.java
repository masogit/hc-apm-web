package com.ge.apm.view.measuring;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 212605082
 * @date 2018/6/4
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper = true)
public class UnmeasuredAssetListController extends JpaCRUDController<AssetInfo> {

    private Integer queryIndex;
    private Map<String, Boolean> selectedMap;
    private OrgInfo filterOrg;
    @Getter
    @Setter
    private OrgInfo filterAssetDeptOrg;
    private UserAccount currentUser;
    private List<AssetInfo> currentPageAssets;

    private AssetInfoRepository dao;
    private UaaService uaaService;
    private TreeNode selectedNode;
    private UserAuth customRoleAuth;
    private OrgInfoTreeService orgInfoTreeService;
    private AssetInfoMapper assetInfoMapper;

    private OrgTreeController orgTreeController;
    private MeasuringService measuringService;


    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth = UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
        orgTreeController = WebUtil.getBean(OrgTreeController.class);
        this.selectedMap = MapUtil.newHashMap();
        this.searchFilters = new ArrayList<>();
        queryIndex = 0;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        Wrapper<AssetInfo> wrapper = getSearchFiltersConvert();
        List<AssetInfo> assetInfos = assetInfoMapper.selectUnMeasureResult(pagination, wrapper);
//        return dao.findBySearchFilter(searchFilters, pageRequest);
        return new PageImpl<>(assetInfos, pageRequest, pagination.getTotal());
    }

    private Wrapper<AssetInfo> getSearchFiltersConvert() {
        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
//            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            wrapper.andNew().in("tenant_uid",orgUidList)
                       .or().in("institution_uid",orgUidList)
                       .or().in("hospital_uid",orgUidList)
                       .or().in("site_uid",orgUidList)
                       .or().in("clinical_dept_uid",orgUidList)
                       .or().in("asset_dept_uid",orgUidList);
        }else{
            Optional<SearchFilter> siteUID = getFilterOnField("siteUID");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("site_uid",searchFilter.value);
        }

        if (!this.hasFilterOnField("status")) {
//            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4, "1*"));
            wrapper.andNew().ne("status",4);
        }else{
            Optional<SearchFilter> siteUID = getFilterOnField("status");
            SearchFilter searchFilter = siteUID.get();
            wrapper.andNew().eq("status",searchFilter.value);
        }

        if (!this.hasFilterOnField("isValid")) {
//            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE, "1*"));
            wrapper.and().eq("is_valid",Boolean.TRUE);
        }else{
            Optional<SearchFilter> siteUID = getFilterOnField("isValid");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("is_valid",searchFilter.value);
        }

        if (!this.hasFilterOnField("isDeleted")) {
//            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE, "1*"));
            wrapper.and().eq("is_deleted",Boolean.FALSE);
        }else{
            Optional<SearchFilter> siteUID = getFilterOnField("isDeleted");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("is_deleted",searchFilter.value);
        }

        if (!this.hasFilterOnField("measureGrade")) {
//            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
//            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
            wrapper.andNew().ne("measure_grade",4).or().isNull("measure_grade");
        } else {
            boolean hasNullMeasureGrade = false;
            Integer measureGradeValue = null;
            for (SearchFilter sf : this.searchFilters) {
                //判断是否有计量等级的过滤条件
                if (sf.fieldName.equals("measureGrade")) {
                    if (sf.value == null) {
                        hasNullMeasureGrade = true;
                    } else {
                        measureGradeValue = Integer.valueOf(sf.value.toString());
                    }
                }
            }

            //判断计量等级过滤条件是否为空
            if (hasNullMeasureGrade) {
                this.removeFilterOnField("measureGrade");
//                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
//                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
                wrapper.andNew().ne("measure_grade",4).or().isNull("measure_grade");
            } else {
//                this.updateSearchFilter(new SearchFilter("measureGrade", SearchFilter.Operator.EQ, measureGradeValue, "1*"));
                wrapper.and().eq("measure_grade",measureGradeValue);
            }
        }

        if (null != filterOrg) {
            if (selectedNode.isLeaf()) {
//                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filterOrg.getUid(), "1*"));
                wrapper.andNew().eq("clinical_dept_uid",filterOrg.getUid());
            } else {
                List<OrgInfo> allClinicals = orgInfoTreeService.collectChildClinicalDeptByParent(selectedNode);
                List<String> clinicalUids = allClinicals.stream().map(OrgInfo::getUid).collect(Collectors.toList());
//                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalUids, "1*"));
                wrapper.andNew().in("clinical_dept_uid",clinicalUids);
            }

        } else {
            if (this.hasFilterOnField("clinicalDeptUID")) {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && "1*".equals(item.conditionPath)).collect(Collectors.toList()));
            }
        }

        if (null != filterAssetDeptOrg) {
            wrapper.andNew().eq("asset_dept_uid", filterAssetDeptOrg.getUid());
        }


        switch (queryIndex) {
            case 0:
                // 删除计量条件
                this.removeFilterOnField("lastMeteringDate");
                this.removeFilterOnField("nextMeteringDate");
//                this.updateSearchFilter(new SearchFilter("lastMeteringDate", SearchFilter.Operator.IsNull, null, "1*"));
                wrapper.andNew().notExists("SELECT 1 FROM measuring_certificate_detail WHERE measuring_certificate_detail.asset_uid = ai.uid");
                break;
            case 1:
//                this.updateSearchFilter(new SearchFilter("nextMeteringDate", SearchFilter.Operator.LT, new Date(), "1*"));
                wrapper.andNew().lt("next_metering_date", new Date());
                break;
            case 2:
//                this.updateSearchFilter(new SearchFilter("nextMeteringDate", SearchFilter.Operator.GTE, new Date(), "1*"));
                wrapper.andNew().ge("next_metering_date", new Date());
                break;
            default:
                break;
        }
        if(this.hasFilterOnField("name")){
            Optional<SearchFilter> siteUID = getFilterOnField("name");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().like("name",(String) searchFilter.value);
        }
        if(this.hasFilterOnField("serialNum")){
            Optional<SearchFilter> siteUID = getFilterOnField("serialNum");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().like("serial_num",(String) searchFilter.value);
        }
        if(this.hasFilterOnField("financingNum")){
            Optional<SearchFilter> siteUID = getFilterOnField("financingNum");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().like("financing_num",(String) searchFilter.value);
        }
        if(this.hasFilterOnField("functionType")){
            Optional<SearchFilter> siteUID = getFilterOnField("functionType");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().like("function_type",(String) searchFilter.value);
        }
        if(this.hasFilterOnField("assetGroup")){
            Optional<SearchFilter> siteUID = getFilterOnField("assetGroup");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("asset_group",Integer.valueOf((String) searchFilter.value));
        }
        if(this.hasFilterOnField("measureGrade")){
            Optional<SearchFilter> siteUID = getFilterOnField("measureGrade");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("measure_grade",Integer.valueOf((String) searchFilter.value));
        }
        if(this.hasFilterOnField("measuringItemCategory")){
            Optional<SearchFilter> siteUID = getFilterOnField("measuringItemCategory");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().eq("measuring_item_category",Integer.valueOf((String) searchFilter.value));
        }
        if(this.hasFilterOnField("measuringOwnerName")){
            Optional<SearchFilter> siteUID = getFilterOnField("measuringOwnerName");
            SearchFilter searchFilter = siteUID.get();
            wrapper.and().like("measuring_owner_name",(String) searchFilter.value);
        }
        if(this.hasFilterOnField("nextMeteringDate")){
            for (SearchFilter searchFilter : searchFilters) {
                if(!searchFilter.fieldName.equals("nextMeteringDate")) continue;
                if(searchFilter.operator == SearchFilter.Operator.GTE){
                    wrapper.and().ge("next_metering_date",searchFilter.value);
                }
                if(searchFilter.operator == SearchFilter.Operator.LTE){
                    wrapper.and().le("next_metering_date", searchFilter.value);
                }
            }
        }
        if(this.hasFilterOnField("lastMeteringDate")){
            for (SearchFilter searchFilter : searchFilters) {
                if(!searchFilter.fieldName.equals("lastMeteringDate")) continue;
                if(searchFilter.operator == SearchFilter.Operator.GTE){
                    wrapper.and().ge("last_metering_date", searchFilter.value);
                }
                if(searchFilter.operator == SearchFilter.Operator.LTE){
                    wrapper.and().le("last_metering_date", searchFilter.value);
                }
            }
        }
        return wrapper;
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (Objects.equals(deptOrg.getOrgType(), 4)) {
            filterOrg = deptOrg;
        } else {
            filterOrg = null;
        }
    }

    public void onAssetDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (Objects.equals(deptOrg.getOrgType(), 4)) {
            filterAssetDeptOrg = deptOrg;
        } else {
            filterAssetDeptOrg = null;
        }
    }

    public void changeIndex(Integer queryIndex){
        this.queryIndex = queryIndex;
        filterOrg = null;
        orgTreeController.setTreeNodeNameFilter(null);
        orgTreeController.filterDeptOrgTree();

    }

    public void postProcessorExcel(Object obj){
        SystemEventAuditEnum eventAuditEnum = SystemEventAuditEnum.TABLE_DOWNLOAD_MEASURE;
        measuringService.postProcessorExcel(eventAuditEnum, "未检计量页面表格");
    }

}

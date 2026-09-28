package com.ge.apm.service.sharedasset;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.sharedasset.valuation.ValuationFactory;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.SharedAssetMapper;
import com.ge.apm.dao.mapper.SharedAssetRequestMapper;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.AllocationCenterUtils;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;

import javaslang.Tuple3;
import webapp.framework.web.WebUtil;

@Service
public class SharedAssetStockService {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetStockService.class);

    @Autowired
    private SharedAssetMapper sharedAssetMapper;

    @Autowired
    private SharedAssetRequestMapper sharedAssetRequestMapper;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private SharedAssetService sharedAssetService;

    @Autowired
    private OrgInfoRepository orgInfoRepository;
    
    @Autowired
	  AssetInfoRepository assetInfoRepository;

    @Autowired
    private SharedAssetReqDetailRepository sharedAssetReqDetailRepository;
    @Autowired
    private ConfigItemValueRepository configItemValueRepository ;
    @Autowired
    private ConfigService configService;

    public void buildDefaultFilters(Wrapper<AssetInfo> ew) {
        List<String> orgUidList = getOrgInfo();
        ew.andNew()
            .in("asset_tenant_uid", orgUidList)
            .or().in("asset_institution_uid", orgUidList)
            .or().in("asset_hospital_uid", orgUidList)
            .or().in("asset_site_uid", orgUidList)
            .or().in("clinical_dept_uid", orgUidList);
        ew.andNew()
            .eq("asset_is_deleted", false)
            .and().eq("asset_is_valid", true);

        List<String> allocationCenterUids =getWorkflowConfig();
        ew.andNew().in("clinical_dept_uid", allocationCenterUids);
    }

    public void addFiltersByQueryType(Wrapper<AssetInfo> ew, int queryType) {
        List<String> allocationCenterUids = getWorkflowConfig();
        switch (queryType) {
            case 1 :
                ew.andNew().eq("asset_status", 1).or().eq("asset_status", 3);
                ew.andNew().eq("shared_status",1).or().isNull("shared_status");
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            case 2:
                ew.andNew().notIn("asset_dept_uid", allocationCenterUids);
                break;
            case 3:
                ew.andNew().ne("asset_status", 1).andNew().ne("asset_status", 3);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            case 4:
                ew.andNew().in("asset_status", Arrays.asList(1,3)).andNew().eq("shared_status",2);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
            default:
        }
    }

    public void addSearchCriteria(Wrapper<AssetInfo> ew, String searchCriteria) {
        ew.andNew()
            .like("asset_name", searchCriteria)
            .or().like("asset_num", searchCriteria)
            .or().like("financing_num", searchCriteria)
            .or().like("qr_code", searchCriteria)
            .or().like("serial_num", searchCriteria)
            .or().like("manufacture", searchCriteria)
            .or().like("lease_org_name", searchCriteria)
            .or().like("voucher_num", searchCriteria);
    }

    public Integer getTotalRowCount(Wrapper<AssetInfo> wrapper) {
        return sharedAssetMapper.getTotalRowCount(wrapper);
    }

    public List<AssetInfo> loadAllSharedAssets(Pagination pageRequest, Wrapper<AssetInfo> wrapper) {
        List<AssetInfo> assetList = sharedAssetMapper.loadSharedAssets(pageRequest, wrapper);
        processAssets(assetList);
        calcRentTimeAndFee(assetList);
        return assetList;
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = getWorkflowConfig();
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        String siteUID = user.getSiteUID();
        if(selectSite != null){
            siteUID=selectSite.getSiteUID();
        }
        TreeNode rootNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(siteUID, user.getUid());

        TreeNodeUtil.sortTreeNodeByName(rootNode);

        List<SelectItem> dropdownList = new ArrayList<>();
        if (rootNode != null) {
            traverse(rootNode, dropdownList, allocationCenterUids);
        }
        if(isWhethertoenableunattended()){
            getVirtuallyOrgInfo(siteUID,dropdownList);
        }

        return dropdownList;
    }
    private void getVirtuallyOrgInfo(String siteUID,List<SelectItem> dropdownList){
         if(StringUtils.isEmpty(siteUID)) return ;
        char[] chars = siteUID.toCharArray();
        chars[0]='$';
        dropdownList.add(new SelectItem(String.valueOf(chars), "调剂无人值守", "", false));
    }
    public SharedAssetRequest findRequestById(String requestId) {
        if(logger.isDebugEnabled()) {
            logger.debug("Request id: {}", requestId);
        }
        SharedAssetRequest request = sharedAssetRequestMapper.findRequestById(requestId);
        if(logger.isDebugEnabled()) {
            logger.debug("SharedAssetRequest: {}", request);
        }
        return request;
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(siteUid, user.getUid());
        if(orgInfos.size() == 0) {
            List<String> allocationDepts = uaaService.getRegulatorUidsBySiteUid(user.getSiteUID());
            orgInfos = uaaService.getClinicalDeptListBySiteUID(user.getSiteUID());
            if(allocationDepts != null && allocationDepts.size() > 0) {
                orgInfos = orgInfos.stream()
                        .filter(item -> !allocationDepts.contains(item.getUid()))
                        .collect(Collectors.toList());
            }
        }
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }

    public ResponseEntity<LinkedHashMap> forceLeaseAsset(AssetInfo asset) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date chargingBeginTime = asset.getSharedAssetReqDetail().getChargingBeginTime();
        Date chargingEndTime = asset.getSharedAssetReqDetail().getChargingEndTime();
        String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
        String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
        logger.info("assetDeptUID: {}, assetInfoUID: {}, chargingBeginTime: {}, chargingEndTime: {}, reqType: {}",
                        asset.getAssetDeptUID(), asset.getUid() , beginTimeStr , endTimeStr , asset.getSharedAssetReqDetail().getLeaseRequest().getType());

        //Build lease request data
        Map<String, Object> reqData = asset.buildLeaseReqHeader();
        Map<String, Object> reqDetail = asset.buildLeaseReqDetail();
        List reqDetailList = (List) reqData.get("details");
        reqDetailList.add(reqDetail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        reqData.put("applyTime",sdf.format(chargingBeginTime));

        ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetService.leaseAssets(reqData);
        return borrowResponse;
    }

    public ResponseEntity<LinkedHashMap> forceReturnAsset(AssetInfo asset) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        SharedAssetReqDetail requestDetail = asset.getSharedAssetReqDetail();
        Date chargingBeginTime = requestDetail.getChargingBeginTime();
        Date chargingEndTime = requestDetail.getChargingEndTime();
        String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
        String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
        Boolean isIntact = requestDetail.getIntact();
        BigDecimal compensationAmount = requestDetail.getCompensationAmount();
        String reqComment = requestDetail.getReqComment();
        logger.info("assetDeptUID: {}, assetInfoUID: {}, chargingBeginTime: {}, chargingEndTime: {}, reqType: {}, intact: {}, compensationAmount: {}, reqComment: {}",
                        asset.getAssetDeptUID(), asset.getUid(), beginTimeStr, endTimeStr, asset.getSharedAssetReqDetail().getReturnRequest().getType(), isIntact.toString(), compensationAmount.toString(), reqComment);

        //Build return request data
        Map<String, Object> reqData = asset.buildReturnReqHeader();
        Map<String, Object> reqDetail = asset.buildReturnReqDetail();
        List reqDetailList = (List) reqData.get("details");
        reqDetailList.add(reqDetail);

        ResponseEntity<LinkedHashMap> returnResponse = sharedAssetService.returnAssets(reqData);
        return returnResponse;
    }

    //301医院则显示打印按钮
    public boolean validate301()
    {
        boolean isTargetHospital = false;
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        String hosName = getVoucherTitle4Print(currentUser, "301");
        if(hosName.contains("中国人民解放军总医院"))
        {
            isTargetHospital = true;
        }

        return isTargetHospital;
    }
    
    public AssetInfo getRealTimeAssetInfo(String uid)
    {
    	return assetInfoRepository.findByUid(uid);
    }

    public Map<String, String> generateVoucher(AssetInfo asset) {
        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
        Map<String, String> voucherInfo = new HashMap<>();
        
        SharedAssetReqDetail sharedAssetReqDetail = this.findLatestReqDetailByAsset(asset);
        if(sharedAssetReqDetail != null) {
            sharedAssetReqDetail = this.calcRentTimeAndFeeByReqDetail(asset, sharedAssetReqDetail);
            voucherInfo.put("实际ID", sharedAssetReqDetail.getVoucherNum());
            if(StringUtils.isBlank(sharedAssetReqDetail.getReturnId())) {//借出状态
            	UserAccount currentUser = UserContextService.getCurrentUserAccount();
                voucherInfo.put("抬头", getVoucherTitle4Print(currentUser, null));
                voucherInfo.put("经办人", currentUser.getName());
                if(StringUtils.isBlank(sharedAssetReqDetail.getVoucherNum()))
                {
                	SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd/HH:mm:ss");
                	voucherInfo.put("借据ID", sdf.format(sharedAssetReqDetail.getChargingBeginTime()));
                }
                else
                {
                	voucherInfo.put("借据ID", sharedAssetReqDetail.getVoucherNum());
                }
                voucherInfo.put("设备编号", asset.getDepartNum());
                voucherInfo.put("设备名称", asset.getName());
                
                AssetInfo realTimeAssetInfo = getRealTimeAssetInfo(asset.getUid());
                
                voucherInfo.put("设备型号", realTimeAssetInfo.getFunctionType());
                voucherInfo.put("设备分类", asset.getSharedAssetTypeEntity() == null ? null : asset.getSharedAssetTypeEntity().getSwapName());
                voucherInfo.put("租金", AllocationCenterUtils.getPdfUnitPrice(sharedAssetReqDetail));
                voucherInfo.put("状态", AllocationCenterUtils.getPdfStatus(realTimeAssetInfo));
                voucherInfo.put("借用时间", AllocationCenterUtils.getPdfDatetime(sharedAssetReqDetail.getChargingBeginTime()));
                voucherInfo.put("附属物品", AllocationCenterUtils.getPdfAccessories(sharedAssetReqDetail));
                voucherInfo.put("科室名称", sharedAssetReqDetail.getClinicalDeptName() == null ? null : sharedAssetReqDetail.getClinicalDept().getName());
                voucherInfo.put("下送", sharedAssetReqDetail.getWillBeDelivered() == null ? null : sharedAssetReqDetail.getWillBeDelivered().toString());
            } else {//可用，曾经借出或从未借出，若曾经借出，则计算最近一次借出的相关信息
                BigDecimal compensationAmount = sharedAssetReqDetail.getCompensationAmount();
                BigDecimal totalFee = sharedAssetReqDetail.getTotalAmount();
                if(compensationAmount == null || compensationAmount.doubleValue() == 0d) {
                    voucherInfo.put("赔偿费用", "/");
                    voucherInfo.put("总计费用", df.format(totalFee));
                } else {
                    voucherInfo.put("赔偿费用", df.format(compensationAmount));
                    voucherInfo.put("总计费用", df.format(totalFee.add(compensationAmount)));
                }
                voucherInfo.put("归还时间", AllocationCenterUtils.getPdfDatetime(sharedAssetReqDetail.getChargingEndTime()));
                Long rentalTimeInMinutes = AllocationCenterUtils.getPdfRentTime(sharedAssetReqDetail.getChargingBeginTime(), sharedAssetReqDetail.getChargingEndTime());
                String borrowDays = rentalTimeInMinutes < 360 ? "0.25" : df.format((double)rentalTimeInMinutes/(24*60));
                voucherInfo.put("总租用期", borrowDays);
                voucherInfo.put("租赁费用", df.format(totalFee));
                voucherInfo.put("备注", sharedAssetReqDetail.getReqComment());
                voucherInfo.put("还时状态", sharedAssetReqDetail.getIntact() == Boolean.FALSE ? "损坏" : "完好");
                voucherInfo.put("借据ID", StringUtils.isBlank(sharedAssetReqDetail.getVoucherNum()) ? "" : sharedAssetReqDetail.getVoucherNum());
            }
        }
        return  voucherInfo;
    }

    private String getVoucherTitle4Print(UserAccount currentUser, String flag) {
        List<String> orgUids = getWorkflowConfig();
        if(CollectionUtils.isEmpty(orgUids)){
            return "";
        }
        List<OrgInfo> allocDeptList = orgInfoRepository.findByUids(new HashSet<>(orgUids));
        OrgInfo allocDept = null;
        if (allocDeptList.stream().findFirst().isPresent()){
            allocDept = allocDeptList.stream().findFirst().get();
        }
        if(null != flag && flag.equals("301"))
        {
        	return allocDept.getOrgFullName();
        }
        else
        {
        	return allocDept.getParentOrg().getName() + allocDept.getName();
        }
    }

    private SharedAssetReqDetail findLatestReqDetailByAsset(AssetInfo asset) {
        List<SharedAssetReqDetail> sharedAssetReqDetailList = sharedAssetReqDetailRepository.findByAssetInfoUid(asset.getUid());
        SharedAssetReqDetail sharedAssetReqDetail = null;
        if (Optional.ofNullable(sharedAssetReqDetailList).isPresent()) {
            sharedAssetReqDetail = Optional.ofNullable(sharedAssetReqDetailList).get().stream()
                    .sorted((SharedAssetReqDetail o1, SharedAssetReqDetail o2) -> {
                        if(o1.getChargingEndTime() == null) {
                            return -1;
                        } else if(o2.getChargingEndTime() == null) {
                            return 1;
                        } else {
                            if(o1.getChargingEndTime().before(o2.getChargingEndTime())) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    })
                    .findFirst()
                    .orElse(null);
        }
        return sharedAssetReqDetail;
    }

    private void processAssets(List<AssetInfo> assets) {
        // hide deptName if asset is owned by allocation center
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids =getWorkflowConfig();
        for(AssetInfo asset : assets) {
            if(allocationCenterUids.contains(asset.getAssetDeptUID())) {
                asset.getAssetDeptOrg().setName("");
            }else{
                char[] chars = asset.getSiteUID().toCharArray();
                chars[0]='$';
                String s = String.valueOf(chars);
                if(s.equals(asset.getAssetDeptUID())){
                    OrgInfo orgInfo = new OrgInfo();
                    orgInfo.setName("调剂无人值守");
                    orgInfo.setUid(s);
                    asset.setAssetDeptOrg(orgInfo);
                }
            }
        }
    }

    private void calcRentTimeAndFee(List<AssetInfo> assets) {
        for(AssetInfo asset : assets) {
            SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
            if(reqDetail != null) {
                if(StringUtils.isBlank(reqDetail.getChargingType())) {
                    logger.info("charging_type is missed in request, so set charging_type from share_asset_type: " + asset.getUid() + ":" + asset.getSharedAssetTypeEntity().getChargingType());
                    reqDetail.setChargingType(asset.getSharedAssetTypeEntity().getChargingType());
                }
                SharedAssetReqDetail sharedAssetReqDetail = this.calcRentTimeAndFeeByReqDetail(asset, reqDetail);
                ValuationFactory.valuation(null,sharedAssetReqDetail);
                asset.setSharedAssetReqDetail(sharedAssetReqDetail);
            }
        }
    }

    private SharedAssetReqDetail calcRentTimeAndFeeByReqDetail(AssetInfo asset, SharedAssetReqDetail reqDetail) {
        Tuple3<Long, String, BigDecimal> billingDetail = AllocationCenterUtils.getBillingDetail(asset.getSharedAssetTypeEntity(), reqDetail);
        reqDetail.setRentalTime(billingDetail._2);
        reqDetail.setTotalAmount(billingDetail._3);
        if(StringUtils.isNotEmpty(reqDetail.getReturnId())) {
            SharedAssetRequest returnReq = this.findRequestById(reqDetail.getReturnId());
            if("1".equals(returnReq.getType())) {
                reqDetail.setTotalAmount(BigDecimal.valueOf(0));
            }
        } else if(StringUtils.isNotEmpty(reqDetail.getBorrowId())){
            //borrowId不应为空
            SharedAssetRequest borrowReq = this.findRequestById(reqDetail.getBorrowId());
            if(borrowReq != null && "1".equals(borrowReq.getType())) {
                reqDetail.setTotalAmount(BigDecimal.valueOf(0));
            }
        }
        return reqDetail;
    }

    private void traverse(TreeNode node, List<SelectItem> itemList, List<String> excludeItemList) {
        OrgInfo orgInfo = (OrgInfo) node.getData();

        if(excludeItemList.contains(orgInfo.getUid())) {
            //exclude allocation centers from dropdown list
        } else {
            SelectItem selectItem = new SelectItem(orgInfo.getUid(), orgInfo.getName(), "", !Objects.equals(orgInfo.getOrgType(), 4));
            itemList.add(selectItem);
        }

        if (!node.isLeaf()) {
            for (TreeNode nodeItem : node.getChildren()) {
                traverse(nodeItem, itemList, excludeItemList);
            }
        }
    }

    public Boolean isLogPatientInfoEnabled() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = userContextService.getSelectSite();
        if(selectSite != null){
            WorkflowConfig workflowConfig = configService.findConfigBySite(selectSite.getUid());
            return workflowConfig==null?false:workflowConfig.getEnableLogPatientInfo();
        }
        return configService.findConfigBySite(UserContextService.getCurrentUserAccount().getSiteUID()).getEnableLogPatientInfo();
    }
    private List<String> getWorkflowConfig(){
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        List<String> workflowConfigList=new ArrayList<>();
        if(selectSite != null){
            List<String> orgInfoUidsByparentUid = orgInfoRepository.getOrgInfoUidsByparentUid(selectSite.getUid());
            for (String s : orgInfoUidsByparentUid) {
                WorkflowConfig configBySite = configService.findConfigBySite(s);
                String deptForSharedAsset = null;
                if(ObjectUtil.isNotNull(configBySite) && StringUtils.isNotEmpty(deptForSharedAsset = configBySite.getDeptForSharedAsset())){
                    workflowConfigList.add(deptForSharedAsset);
                }
            }
        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            WorkflowConfig configBySite = configService.findConfigBySite(user.getSiteUID());
            String deptForSharedAsset = null;
            if(StringUtils.isNotEmpty(deptForSharedAsset = configBySite.getDeptForSharedAsset())){
                workflowConfigList.add(deptForSharedAsset);
            }
        }
        if (CollectionUtils.isEmpty(workflowConfigList)) {
            workflowConfigList.add("-1");
        }
        return workflowConfigList;
    }
    private List<String> getOrgInfo(){
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        List<String> orgInfList=new ArrayList<>();
        if(selectSite != null){
            List<String> orgInfoUidsByparentUid = orgInfoRepository.getOrgInfoUidsByparentUid(selectSite.getUid());
            orgInfList.addAll(orgInfoUidsByparentUid);
        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            orgInfList.addAll(orgUidList);
        }
        return orgInfList;
    }
    public boolean  isWhethertoenableunattended(){
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        if(selectSite != null){
            return checkWhethertoenableunattended(selectSite.getSiteUID(),selectSite.getHospitalUID(),selectSite.getInstitutionUID(),selectSite.getTenantUID());
        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            return checkWhethertoenableunattended(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID());
        }
    }

    private boolean checkWhethertoenableunattended(String siteUid,String hospitalUID,String InstitutionUID,String tenantUID){
        String key="whetherToEnableUnattended";
        String byKeyAndSiteForUseParent = configItemValueRepository.findByKeyAndSiteForUseParent(siteUid,
                hospitalUID,
                InstitutionUID,
                tenantUID, key);
        return Boolean.TRUE.toString().equalsIgnoreCase(byKeyAndSiteForUseParent);
    }
}

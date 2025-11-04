package com.ge.apm.service.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestNoAssetRepository;

import com.ge.apm.dao.ServiceRequestNoAssetStepRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceRequestNoAssetService {

    @Autowired
    private ServiceRequestNoAssetApiService serviceRequestNoAssetApiService;

    @Autowired
    private ServiceRequestNoAssetRepository dao;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private ServiceRequestNoAssetStepRepository srsDao;


    public void saveServiceRequest(V2_ServiceRequestNoAsset v2ServiceRequestNoAsset,  List<V2_BlobObject> blobObjectList){
        String token = UserContextService.getAccessToken();
        serviceRequestNoAssetApiService.createServiceRequest(token, v2ServiceRequestNoAsset, blobObjectList);
    }


    public List<UserAccount> getAssigneeUsersBySite(String orgUid) {
        String token = UserContextService.getAccessToken();
        Map<String, Object> res = serviceRequestNoAssetApiService.getAssigneeUsersBySite(token, orgUid);
        if (null != res) {
            List<LinkedHashMap> mapList = (List<LinkedHashMap>) res.get("data");
            List<UserAccount> userList = mapList.stream().map(item -> {
                UserAccount u = new UserAccount();
                u.setId(Integer.parseInt(item.get("id").toString()));
                u.setUid(item.get("uid").toString());
                u.setName(item.get("name").toString());
                u.setTelephone(Optional.ofNullable(item.get("telephone")).orElse("").toString());
                return u;
            }).collect(Collectors.toList());

            return userList;
        }
        return new ArrayList<>();
    }

    /**
     * 派工
     * @param serviceRequestNoAsset
     */
    public void dispatchSrNoAsset(V2_ServiceRequestNoAsset serviceRequestNoAsset){
        String token = UserContextService.getAccessToken();

        ServiceRequestNoAssetActionForm form = new ServiceRequestNoAssetActionForm();
        form.setAssigneeId(serviceRequestNoAsset.getCurrentPersonId().toString());
        form.setCurrentStepId(serviceRequestNoAsset.getCurrentStepId());
        String res = serviceRequestNoAssetApiService.invokeAction(token, form, "assign", serviceRequestNoAsset.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail",WebUtil.getMessage("dispatchSrNoAsset")));
        }
    }


    public void acceptSrNoAsset(V2_ServiceRequestNoAsset serviceRequestNoAsset, Date estimatedCloseTime, Integer extType, String comments) {
        String token = UserContextService.getAccessToken();
        ServiceRequestNoAssetActionForm formData = new ServiceRequestNoAssetActionForm();
        formData.setCurrentStepId(serviceRequestNoAsset.getCurrentStepId());
        formData.setEstimatedCloseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(estimatedCloseTime));
        formData.setRepairType(extType);
        formData.setDesc(comments);
        String res = serviceRequestNoAssetApiService.invokeAction(token, formData, "accept", serviceRequestNoAsset.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "接单"));
        }
        serviceRequestNoAsset.setCurrentStepId(7);
    }


    public void reassignSrNoAsset(V2_ServiceRequestNoAsset serviceRequestNoAsset, Integer assigneeId, String comments) {
        String token = UserContextService.getAccessToken();
        ServiceRequestNoAssetActionForm formData = new ServiceRequestNoAssetActionForm();
        formData.setCurrentStepId(serviceRequestNoAsset.getCurrentStepId());
        formData.setAssigneeId(String.valueOf(assigneeId));
        formData.setDesc(comments == null ? "" : comments);
        String res = serviceRequestNoAssetApiService.invokeAction(token, formData, "reassign", serviceRequestNoAsset.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "转单"));
        }
    }

    public void closeSrNoAsset(V2_ServiceRequestNoAsset serviceRequestNoAsset, String comments) {
        String token = UserContextService.getAccessToken();
        ServiceRequestNoAssetActionForm formData = new ServiceRequestNoAssetActionForm();
        formData.setCurrentStepId(serviceRequestNoAsset.getCurrentStepId());
        formData.setDesc(comments == null ? "" : comments);
        String res = serviceRequestNoAssetApiService.invokeAction(token, formData, "close", serviceRequestNoAsset.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "关单"));
        }
    }

    public void turnWorkOrder(V2_ServiceRequestNoAsset serviceRequestNoAsset, String assetUid) {
        String token = UserContextService.getAccessToken();
        ServiceRequestNoAssetActionForm formData = new ServiceRequestNoAssetActionForm();
        formData.setCurrentStepId(serviceRequestNoAsset.getCurrentStepId());
        formData.setAssetUid(assetUid);
        String res = serviceRequestNoAssetApiService.invokeAction(token, formData, "turnWorkOrder", serviceRequestNoAsset.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "转台账报修"));
        }
    }

    public void cancelSrNoAsset(String id, String reason, String assetStatus) {
        V2_ServiceRequestNoAsset wo = dao.findById(id);
        String token = UserContextService.getAccessToken();
        ServiceRequestNoAssetActionForm formData = new ServiceRequestNoAssetActionForm();
        formData.setDesc(reason);
        formData.setAssetStatus(assetStatus);
        formData.setCurrentStepId(wo.getCurrentStepId());
        formData.setCancelReason(reason);
        String res = serviceRequestNoAssetApiService.invokeAction(token, formData, "cancel", id);
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Cancel")));
        }
    }

    public List<AssetInfo> queryAssetByCondition(UserAccount userAccount, BudgetAssetSearchForm assetSearchForm) {
        List<AssetInfo> assetInfos = new ArrayList<>();
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
        if (StrUtil.isNotEmpty(assetSearchForm.getApplicationDeptId())) {
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, assetSearchForm.getApplicationDeptId()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetName())) {
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetBrand())) {
            filters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetModel())) {
            filters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getSerialNum())) {
            filters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, assetSearchForm.getSerialNum()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getFinancingNum())) {
            filters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, assetSearchForm.getFinancingNum()));
        }
        assetInfos = assetInfoRepository.findBySearchFilter(filters);
        return assetInfos;
    }


    public List<V2_ServiceRequestNoAssetStep> queryStepBySrId(String id){
        return srsDao.findBySrIdOrderByStartTimeDesc(id);
    }
}

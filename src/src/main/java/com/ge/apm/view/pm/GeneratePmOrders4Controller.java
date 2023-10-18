package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.domain.pure.AssetRuleModelList;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class GeneratePmOrders4Controller extends GenericCRUDUUIDController<RelRuleAsset> {

    public static final Logger LOGGER = LoggerFactory.getLogger(GeneratePmOrders4Controller.class);

    private PmOrderRequestApiService pmOrderRequestApiService;

    @Getter
    @Setter
    private RelRuleAssetRepository relRuleAssetRepository;

    @Getter
    @Setter
    private Boolean isAllSelected = false;

    @Getter
    @Setter
    private List<AssetRuleModel> onlyOneModels = new ArrayList<>();//只存在唯一一个质控绑定规则的记录默认批量创建

    @Getter
    @Setter
    private List<AssetRuleModel> notOnlyOneModels = new ArrayList<>();//存在多个绑定规则的列表（需用户选择是否批量创建）

    @Getter
    @Setter
    private List<AssetRuleModel> selectedNotOnlyOneModels = new ArrayList<>();//弹出框存在多个质控规则中选定的记录

    @Getter
    @Setter
    private List<AssetRuleModel> batchCreateModels = new ArrayList<>();//最终批量创建列表

    @Getter
    @Setter
    private Map<String, Boolean> selectedRuleMap;

    @Override
    protected GenericRepositoryUUID<RelRuleAsset> getDAO() {
        return relRuleAssetRepository;
    }

    @Override
    protected void init() {
        pmOrderRequestApiService = WebUtil.getBean(PmOrderRequestApiService.class);

    }

    public void generatePmOrdersForMuliPMRule4() {
        List<AssetRuleModel> models = new ArrayList<>();
        selectedNotOnlyOneModels = notOnlyOneModels.stream().filter(e->selectedRuleMap.get(e.getUniqueUid())).collect(Collectors.toList());
        batchCreateModels.addAll(selectedNotOnlyOneModels);
        batchCreateModels.addAll(onlyOneModels);
        models = batchCreateModels;
        AssetRuleModelList model = new AssetRuleModelList();
        model.setModels(models);
        String token = UserContextService.getAccessToken();
        String request = JSON.toJSONString(model);
        LOGGER.info("token is {}",token);
        LOGGER.info("list is {}",request);
        long start = System.currentTimeMillis();
        try{
            pmOrderRequestApiService.createPmOrdersByBatch(token, request);
            long end = System.currentTimeMillis();
            LOGGER.info("批量生成保养工单结束，耗时 : {} 秒 !",(end - start)/1000);
            WebUtil.addSuccessMessage("提交完成，工单生成中，请稍后在工作台中查看！");
            //WebUtil.addSuccessMessage("工单生成成功！");
        } catch(Exception e){
            //WebUtil.addErrorMessage("工单生成失败！");
            e.printStackTrace();
            LOGGER.error("工单生成失败，msg is {}",e.getMessage());
        }

    }

    public void updateCheckBox(){
//		selectedRuleMap.put(uniqueUid,!selectedRuleMap.get(uniqueUid));
//        selectedRuleMap.remove("header");
        isAllSelected = selectedRuleMap.values().stream().allMatch(e -> e.equals(true));
//        selectedRuleMap.put("header",b);
    }

    public void onSelectRuleAllOfPage() {
//		isAllRuleSelected = !isAllRuleSelected;
        notOnlyOneModels.stream().forEach(e -> {
            selectedRuleMap.put(e.getUniqueUid(), isAllSelected);
        });
    }

    public void cancel(){
        onlyOneModels.clear();
        notOnlyOneModels.clear();
        batchCreateModels.clear();
        selectedNotOnlyOneModels.clear();
    }
}

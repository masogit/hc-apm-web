package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.service.asset.DataEntryService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.pm.PmOrderPreCreateService;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.Base64Utils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class SystemConfigItemController extends GenericCRUDUUIDController<ConfigItem> {

    private ConfigItemRepository dao;
    private ConfigItemValueRepository configItemValueRepository;
    private OrgInfoRepository orgInfoRepository;
    private UaaService uaaService;
    private SharedAssetService sharedAssetService;

    private OrgInfo selectSite;
    private UserAccount user;
    private Boolean superAdminEdit = false;

    private LinkedHashMap<Integer, String> categoryMap;
    private List<String> multiSelected;
    private Map<String, List<String>> multiSelectedMap = MapUtil.newHashMap();
    private List<ConfigItem> configItemList;
    private Map<String, ConfigItem> configItemMap = MapUtil.newHashMap();
    private OrgInfo assetOutStockDept;

    private UserCommonService userCommonService;
    private UserAccountRepository userAccountRepository;

    private PmOrderPreCreateService pmOrderPreCreateService;

    private PmOrderRequestApiService srApi;

    private V2_PmOrderRepository v2PmOrderRepository;

    private DataEntryService dataEntryService;
    private Map<String, Object> selectedObjectMap = MapUtil.newHashMap();
    private List<OrgInfo> deptList;

    private List<UserAccount> assetStaffList;

    private List<UserAccount> assetHeadList;
    private final List<String> textAloneHandleType = ListUtil.newArrayList("setAssetOutStockDept", "systemLogo");

    private static final String VALUE ="value";
    private List<String> datePatternSelect = ListUtil.newArrayList("yyyy/MM/dd", "yyyy/MM/dd HH", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss");

    @Getter@Setter
    private ConfigItem selectedConfigItem;
    @Getter@Setter
    private String itemValue = "WorkOrderListColumn_0";
    Map<String, Boolean> fieldRenderedMap = new HashMap<>();
    @Getter@Setter
    private SelectItemDto selectItemDto=new SelectItemDto(null,null);
    @Getter@Setter
    private List<String> deleteEnumKey=new ArrayList<>();
    @Getter@Setter
    private String editEnumKey;
    @Override
    protected void init() {
        dao = WebUtil.getBean(ConfigItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        user = UserContextService.getCurrentUserAccount();
        userCommonService = WebUtil.getBean(UserCommonService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        pmOrderPreCreateService = WebUtil.getBean(PmOrderPreCreateService.class);
        v2PmOrderRepository = WebUtil.getBean(V2_PmOrderRepository.class);
        srApi = WebUtil.getBean(PmOrderRequestApiService.class);
        dataEntryService = WebUtil.getBean(DataEntryService.class);
        selectSite = orgInfoRepository.getByUid(user.getSiteUID());
        loadClinicalDept();
        loadAssetStaff();
        loadAssetHead();
        categoryMap = new LinkedHashMap<>();
        categoryMap.put(1,"设备档案");
        categoryMap.put(2,"集中调剂");
        categoryMap.put(3,"到货安装");
        categoryMap.put(4,"维修流程");
        categoryMap.put(5,"预防维护");
        categoryMap.put(6,"预算管理");
        categoryMap.put(7,"计量管理");
        categoryMap.put(8,"临床助手");
        categoryMap.put(9,"知识文档");
        categoryMap.put(10,"绩效分析");
        categoryMap.put(11,"质控评审");
        categoryMap.put(12,"盘点工单");
        categoryMap.put(13,"系统配置");
        categoryMap.put(14,"不良事件");
        categoryMap.put(15,"培训管理");
        categoryMap.put(16,"MVS院级工单");
        categoryMap.put(17,"IB报表链接");
        categoryMap.put(18,"列表字段配置");
        categoryMap.put(19,"质控管理");


    }

    @Override
    protected GenericRepositoryUUID<ConfigItem> getDAO() {
        return dao;
    }

    @Override
    protected Page<ConfigItem> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if(searchFilters == null){
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("superAdminEdit", SearchFilter.Operator.EQ, superAdminEdit));
        Page<ConfigItem> page = dao.findBySearchFilter(searchFilters, pageRequest);

        List<ConfigItem> list = page.getContent();

        if(CollectionUtils.isNotEmpty(list)){
            List<ConfigItemValue> configItemValues = configItemValueRepository.selectConfigItemAll(selectSite.getSiteUID(), selectSite.getHospitalUID(), selectSite.getInstitutionUID(), selectSite.getTenantUID());
            Map<String, ConfigItemValue> valueMap = configItemValues.stream().filter(value -> ObjectUtil.isNotEmpty(value) && ObjectUtil.isNotEmpty(value.getKey())).collect(Collectors.toMap(ConfigItemValue::getKey, value -> value));
            for (ConfigItem configItem : list) {
                ConfigItemValue value = valueMap.get(configItem.getKey());
                if(value != null){
                    configItem.setConfigItemValue(value);
                    configItem.setShowValue(value.getValue());
                }else{
                    configItem.setShowValue(configItem.getDefaultValue());
                }
                if (3 == configItem.getValueType()) {
                    configItem.setShowValue(Optional.ofNullable(configItem.getShowValue()).map(String::toLowerCase).orElse(""));
                }
                if((7 == configItem.getValueType() || 8 == configItem.getValueType())
                        && StringUtils.isNotEmpty(configItem.getValidation())){
                    String validation = configItem.getValidation();
                    List<Map<String, String>> mapList = JSON.parseObject(validation, new TypeReference<List<Map<String,String>>>(){});


                    if(value!=null){
                        String customValue = value.getCustomValue();
                        if(StringUtils.isNotEmpty(customValue)){
                            List<Map<String, String>> customValueMap = JSON.parseObject(customValue, new TypeReference<List<Map<String,String>>>(){});
                            mapList.addAll(customValueMap);
                        }
                    }

                    configItem.setEnumList(mapList);
                }
                if (9 == configItem.getValueType()) {
                    configItem.setFieldColumnDto(JSON.parseObject(configItem.getShowValue(), FieldColumnDto.class));
                }
                if(10 == configItem.getValueType()){
                    configItem.setFieldColumnDtoList(JSON.parseArray(configItem.getShowValue(), FieldColumnDto.class));
                }
            }
        }
        configItemList = page.getContent();
        for (ConfigItem configItem : configItemList) {
            if ("assetScrap".equals(configItem.getKey()) || "showAssetScrapNo".equals(configItem.getKey()) ) {
                configItemMap.put(configItem.getKey(),configItem);
            }
        }
         return page;
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                OrgInfo site = (OrgInfo) event.getTreeNode().getData();
                if(StringUtils.isNotEmpty(site.getUid())){
                    this.selectSite = site;
                }
            } else {
                this.selectSite = orgInfoRepository.getByUid(user.getSiteUID());
            }
        }
        onTabChange(tabId);
    }

    @Getter @Setter
    private String tabId;

    public void onTabChange(TabChangeEvent event) {
        tabId = event.getTab().getId();
        onTabChange(tabId);
    }

    public void onTabChange(String tabId) {
        crudActionName = null;
        if (ObjectUtil.isNotEmpty(tabId) && !"basicInfo".equals(tabId)) {
            String category = tabId.substring(2);
            this.searchFilters = ListUtil.newArrayList(new SearchFilter("category", SearchFilter.Operator.EQ, category));
            Page<ConfigItem> configItemPage = loadData(new PageRequest(0, 5000));
            configItemList = configItemPage.getContent();
            for (ConfigItem configItem : configItemList) {
                if (8 == configItem.getValueType()) {
                    multiSelected = getMultiEum(configItem);
                    multiSelectedMap.put(configItem.getKey(), getMultiEum(configItem));
                }
            }
            configItemMap.putAll(configItemList.stream().collect(Collectors.toMap(ConfigItem::getKey, item -> item)));
            if (category.equals("3")) {
                ConfigItem configItem = configItemMap.get("setAssetOutStockDept");
                selectedObjectMap.put("assetOutStockDept", orgInfoRepository.getByUid(configItem.getShowValue()));
                assetOutStockDept = orgInfoRepository.getByUid(configItem.getShowValue());
            }

            if (category.equals("4")) {
                ConfigItem configItem = configItemMap.get("NoAssetDispatchWorkers");
                String showValue = configItem.getShowValue();
                JSONObject jsonObject = JSONObject.parseObject(showValue);
                if(jsonObject!=null){
                    String NoAssetDispatchWorkers1 = (String)jsonObject.get("NoAssetDispatchWorkers1");
                    String NoAssetDispatchWorkers2 = (String)jsonObject.get("NoAssetDispatchWorkers2");
                    String AssetHead = (String)jsonObject.get("AssetHead");
                    if(StringUtils.isNotBlank(NoAssetDispatchWorkers1)){
                        selectedObjectMap.put("NoAssetDispatchWorkers1", userAccountRepository.getByUid(NoAssetDispatchWorkers1));
                    }
                    if(StringUtils.isNotBlank(NoAssetDispatchWorkers2)){
                        selectedObjectMap.put("NoAssetDispatchWorkers2", userAccountRepository.getByUid(NoAssetDispatchWorkers2));
                    }
                    if(StringUtils.isNotBlank(AssetHead)){
                        selectedObjectMap.put("AssetHead", userAccountRepository.getByUid(AssetHead));
                    }
                }
            }else if(category.equals("18")){
                configItemList = configItemList.stream().filter(t -> t.getCategory().equals(18)).collect(Collectors.toList());
                for(ConfigItem configItem : configItemList){
                    List<FieldColumnDto> list = configItemMap.get(configItem.getKey()).getFieldColumnDtoList();

                    Map<String, Boolean> map = list.stream().collect(Collectors.toMap(
                            dto -> configItem.getKey() + "_" + dto.getField(),
                            FieldColumnDto::getRendered
                    ));
                    fieldRenderedMap.putAll(map);
                }
            }
            this.searchFilters = null;
        }  else {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", "configItemList.xhtml");
        }
    }

    public List<ConfigItem> getWorkOrderListType(){
        return configItemList.stream().filter(item -> item.getKey().contains("WorkOrderListColumn")).collect(Collectors.toList());
    }

    public void selectedConfigItemChange(){
        List<FieldColumnDto> list = configItemMap.get(itemValue).getFieldColumnDtoList();
        fieldRenderedMap = list.stream().collect(Collectors.toMap(
                dto -> itemValue + "_" + dto.getField(),
                FieldColumnDto::getRendered
        ));
    }


    @Override
    public void prepareEdit() {
        if(8 == this.selected.getValueType()){
            this.multiSelected = new ArrayList<>();
            String showValue = this.selected.getShowValue();
            if(StringUtils.isNotEmpty(showValue)){
                List<Map<String, String>> valueList = JSON.parseObject(showValue, List.class);
                multiSelectedMap.put(this.selected.getKey(), valueList.stream().map(map -> map.get(VALUE)).collect(Collectors.toList()));
                for (Map<String, String> valueMap : valueList) {
                    multiSelected.add(valueMap.get(VALUE));
                }
            }
        }
        super.prepareEdit();
    }

    public List<String> getMultiEum(ConfigItem configItem) {
        List<String> multiEum = new ArrayList<>();
        String showValue = configItem.getShowValue();
        if(StringUtils.isNotEmpty(showValue)){
            List<Map<String, String>> valueList = JSON.parseObject(showValue, List.class);

            for (Map<String, String> valueMap : valueList) {
                multiEum.add(valueMap.get(VALUE));
            }
        }
        return multiEum;
    }



    public void onAssetScrapChange() {
         boolean isAssetScrapEnabled = Boolean.parseBoolean(
                configItemMap.get("assetScrap").getShowValue()
        );
         if (!isAssetScrapEnabled) {
             ConfigItem showAssetScrapNoItem = configItemMap.get("showAssetScrapNo");
            showAssetScrapNoItem.setShowValue("false"); // 同步值
         }
    }


    public void deleteConfigScrip(ConfigItem configItemF,ConfigItem configItemS) {
        deleteConfig(configItemF);
        deleteConfig(configItemS);
    }

    public void deleteConfig(ConfigItem configItem) {
        if(this.selectSite != null){
            ConfigItemValue configItemValue = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), this.selectSite.getSiteUID());
            if (ObjectUtil.isNotEmpty(configItemValue)) {
                configItemValueRepository.delete(configItemValue);
                configItem.setConfigItemValue(null);
                if ("setAssetOutStockDept".equals(configItem.getKey())) {
                    selectedObjectMap.remove("assetOutStockDept");
                }
                if ("NoAssetDispatchWorkers".equals(configItem.getKey())) {
                    selectedObjectMap.remove("NoAssetDispatchWorkers2");
                    selectedObjectMap.remove("NoAssetDispatchWorkers1");
                    selectedObjectMap.remove("AssetHead");
                }
                configItem.setShowValue(configItem.getDefaultValue());
                if (3 == configItem.getValueType()) {
                    configItem.setShowValue(Optional.ofNullable(configItem.getShowValue()).map(String::toLowerCase).orElse(""));
                }
                if (8 == configItem.getValueType()) {
                    multiSelected = getMultiEum(configItem);
                    multiSelectedMap.put(configItem.getKey(), getMultiEum(configItem));
                }
                if (9 == configItem.getValueType()) {
                    String value = configItemValueRepository.findByKeyAndSiteForUseParent(selectSite.getSiteUID(),
                            selectSite.getHospitalUID(),
                            selectSite.getInstitutionUID(),
                            selectSite.getTenantUID(), configItem.getKey());
                    configItem.setFieldColumnDto(JSON.parseObject(value, FieldColumnDto.class));
                }
                if (10 == configItem.getValueType()){
                    configItem.setFieldColumnDtoList(JSON.parseArray(configItem.getShowValue(), FieldColumnDto.class));
                    fieldRenderedMap = new HashMap<>();
                    for (FieldColumnDto dto : configItem.getFieldColumnDtoList()) {
                        String key = configItem.getKey() + "_" + dto.getField();
                        fieldRenderedMap.put(key, dto.getRendered());
                    }
                }
            }
            WebUtil.addErrorMessage("删除成功！");
        }
    }

    public void saveConfigAndUpdateData(){
        saveConfig(this.selected);
        siteToSiteAutoAck(this.selected);
    }
    public void saveConfig() {
        saveConfig(this.selected);
    }

    public void saveConfigScrap(ConfigItem configItemF,ConfigItem configItemS){
        saveScrap(configItemF);
        saveScrap(configItemS);
        WebUtil.addErrorMessage("保存成功！");
    }

    public void saveConfigMmOrderCreateByCycle() {
//        pmOrderPreCreateService.deleteBySiteUid(user.getSiteUID());
        saveConfig(this.selected);
        invokeBatchPmOrderCancel(user.getSiteUID());

    }

    public void saveScrap(ConfigItem configItemF){
        if(this.selectSite != null){
            ConfigItemValue configItemValue;
            configItemValue = configItemValueRepository.findFirstByKeyAndSiteUid(configItemF.getKey(), this.selectSite.getSiteUID());
            if(configItemValue == null){
                configItemValue = new ConfigItemValue();
                configItemValue.setTenantUid(selectSite.getTenantUID());
                configItemValue.setInstitutionUid(selectSite.getInstitutionUID());
                configItemValue.setHospitalUid(selectSite.getHospitalUID());
                configItemValue.setSiteUid(selectSite.getSiteUID());
                configItemValue.setKey(configItemF.getKey());
            }
            configItemValue.setValue(configItemF.getShowValue());
            configItemValueRepository.save(configItemValue);
            configItemF.setConfigItemValue(configItemValue);
        }
    }



    public void saveConfig(ConfigItem configItem){
        this.selected = configItem;
        if(this.selectSite != null){
            if((this.selected.getValueType()!=8 && this.selected.getValueType()!=7) &&  !regularCheck()){
                logger.info("配置项保存，正则校验不通过：");
                WebUtil.addErrorMessage("配置项保存失败，正则校验不通过！");
                return;
            }
            ConfigItemValue configItemValue;
            // 如果不为空，并且siteUid相等，说明已经有配置，直接更新
            // 如果siteUid不相等，说明使用的是上级配置，需要新增数据
            configItemValue = configItemValueRepository.findFirstByKeyAndSiteUid(this.selected.getKey(), this.selectSite.getSiteUID());
            if(configItemValue == null){
                configItemValue = new ConfigItemValue();
                configItemValue.setTenantUid(selectSite.getTenantUID());
                configItemValue.setInstitutionUid(selectSite.getInstitutionUID());
                configItemValue.setHospitalUid(selectSite.getHospitalUID());
                configItemValue.setSiteUid(selectSite.getSiteUID());
                configItemValue.setKey(this.selected.getKey());
            }

            if ("setAssetOutStockDept".equals(this.selected.getKey())) {
                this.selected.setShowValue(Optional.ofNullable(assetOutStockDept).map(OrgInfo::getUid).orElse(null));
            }

            if ("NoAssetDispatchWorkers".equals(this.selected.getKey())) {
                String  NoAssetDispatchWorkers1 = Optional.ofNullable((UserAccount)selectedObjectMap.get("NoAssetDispatchWorkers1")).map(UserAccount::getUid).orElse(null);
                String  NoAssetDispatchWorkers2 =Optional.ofNullable((UserAccount)selectedObjectMap.get("NoAssetDispatchWorkers2")).map(UserAccount::getUid).orElse(null);
                String  AssetHead =Optional.ofNullable((UserAccount)selectedObjectMap.get("AssetHead")).map(UserAccount::getUid).orElse(null);

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("NoAssetDispatchWorkers1",NoAssetDispatchWorkers1);
                jsonObject.put("NoAssetDispatchWorkers2",NoAssetDispatchWorkers2);
                jsonObject.put("AssetHead",AssetHead);
                this.selected.setShowValue(jsonObject.toJSONString());
            }

            if(4 == this.selected.getValueType() || 5 == this.selected.getValueType() || 6 == this.selected.getValueType()){
                try {
                    configItemValue.setValue(formatDate(this.selected.getShowValue()));
                } catch (ParseException e) {
                    logger.error("配置项保存，日期格式化异常：", e);
                    WebUtil.addErrorMessage("配置项保存异常，请联系系统管理员！");
                    return;
                }
            }else if(8 == this.selected.getValueType()){
                List<String> multiSelected;
                if ("Edit".equals(crudActionName)) {
                    multiSelected = this.multiSelected;
                } else {
                    Object selectedObj = multiSelectedMap.get(this.selected.getKey());
                    multiSelected = Arrays.asList((String[]) selectedObj);
                }
                if(CollectionUtils.isNotEmpty(multiSelected)){
                    List<Map<String, String>> maps = new ArrayList<>();
                    List<Map<String, String>> enumList = this.selected.getEnumList();
                    for (Object selectedVal : multiSelected) {
                        for (Map<String, String> stringMap : enumList) {
                            if(stringMap.containsValue(selectedVal)){
                                maps.add(stringMap);
                            }
                        }
                    }
                    String validation = configItem.getValidation();
                    List<Map<String, String>> mapList = JSON.parseObject(validation, new TypeReference<List<Map<String,String>>>(){});
                    List<Map<String, String>> keyMap = getDifference(enumList, mapList, "key");
                    if(CollectionUtils.isNotEmpty(keyMap)){
                        String jsonString = JSON.toJSONString(keyMap);
                        configItemValue.setCustomValue(jsonString);
                    }else{
                        configItemValue.setCustomValue(null);
                    }
                    configItemValue.setValue(JSON.toJSONString(maps));
                }
            } else if (9 == this.selected.getValueType()) {
                configItemValue.setValue(JSON.toJSONString(this.selected.getFieldColumnDto()));
            } else if(10 == this.selected.getValueType()) {
                configItemValue.setValue(JSON.toJSONString(this.selected.getFieldColumnDtoList()));
            }else{
                configItemValue.setValue(this.selected.getShowValue());
            }
            configItemValueRepository.save(configItemValue);
            this.selected.setConfigItemValue(configItemValue);
        }
        WebUtil.addErrorMessage("保存成功！");
    }


    public static List<Map<String, String>> getDifference(List<Map<String, String>> list1, List<Map<String, String>> list2, String keyName) {

        Set<String> list2Keys = list2.stream()
                .map(m -> m.get(keyName))
                .collect(Collectors.toSet());

        return list1.stream()
                .filter(m -> !list2Keys.contains(m.get(keyName)))
                .collect(Collectors.toList());
    }
    public boolean regularCheck(){
        if(selected == null) return Boolean.FALSE;
        String validation = selected.getValidation();
        if(StringUtils.isEmpty(validation)) return Boolean.TRUE;
        String showValue = selected.getShowValue();
        if(StringUtils.isEmpty(showValue)) return Boolean.FALSE;
        Pattern pattern = Pattern.compile(validation);
        Matcher matcher = pattern.matcher(showValue);
        return  matcher.matches();
    }
    public String formatDate(String value) throws ParseException {
        String formatValue = "";

        SimpleDateFormat sdf;

        switch (this.selected.getValueType()){
            case 4:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 5:
                sdf = new SimpleDateFormat("HH:mm:ss");
                break;
            case 6:
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            default:
                sdf = new SimpleDateFormat();
                break;
        }
        Date parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US).parse(value);
        formatValue = sdf.format(parse);

        return formatValue;
    }

    public String transferEnum(int valueType, String value, List<Map<String, String>> list){
        switch (valueType){
            case 7:
                if(CollectionUtils.isNotEmpty(list) && StringUtils.isNotEmpty(value)){
                    for (Map<String, String> map : list) {
                        if(value.equals(map.get(VALUE))){
                            return map.get("key");
                        }
                    }
                }
                break;
            case 8:
                StringBuilder str = new StringBuilder();
                if(CollectionUtils.isNotEmpty(list) && StringUtils.isNotEmpty(value)){
                    List<Map<String, String>> valueList = JSON.parseObject(value, List.class);

                    for (Map<String, String> valueMap : valueList) {
                        for (Map<String, String> map : list) {
                            if(valueMap.get(VALUE).equals(map.get(VALUE))){
                                str.append(map.get("key")).append(",");
                            }
                        }
                    }
                    if(str.length() > 0){
                        return str.substring(0,str.length() - 1);
                    }
                }
                break;
            default:
        }
        return "";
    }

    private void loadClinicalDept(){
        if(null == selectSite){
            deptList = Collections.EMPTY_LIST;
        }else{
            deptList = uaaService.findAccessClinicalUidByUserUid(user.getUid(), selectSite.getSiteUID());
            if(null != assetOutStockDept && !assetOutStockDept.getSiteUID().equals(selectSite.getSiteUID()) ){
                assetOutStockDept = null;
            }
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (deptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.deptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    private void loadAssetStaff(){
        if(null == selectSite){
            assetStaffList = Collections.EMPTY_LIST;
        }else{
            assetStaffList = userCommonService.getWorkerList(0, selectSite.getUid());
        }
    }

    private void loadAssetHead(){
        if(null == selectSite){
            assetHeadList = Collections.EMPTY_LIST;
        }else{
            assetHeadList = userAccountRepository.findBySiteUidAndRoleName(selectSite.getUid(), ApmSystemRole.AssetHead.name());
        }
    }

    public List<UserAccount> autoCompleteDispatchWorkers(String query) {
        if (assetStaffList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.assetStaffList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<UserAccount> autoCompleteAssetHead(String query) {
        if (assetHeadList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.assetHeadList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public UserAccount findByUid(String uid){
        if (StringUtils.isBlank(uid) || CollectionUtils.isEmpty(assetStaffList)) {
            return new UserAccount();
        }

        return assetStaffList.stream()
                .filter(i -> uid.equals(i.getUid()))
                .findFirst()
                .orElse(new UserAccount());
    }

    public void onDeptChange(String idx) {
        switch (idx) {
            case "assetOutStockDept":
                assetOutStockDept = (OrgInfo) selectedObjectMap.get("assetOutStockDept");
                ConfigItem stockDeptItem = configItemMap.get("setAssetOutStockDept");
                System.out.println(stockDeptItem);
                break;
        }
    }

    public void uploadImg(FileUploadEvent event) throws Exception {
        InputStream inputStream = event.getFile().getInputstream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[inputStream.available()];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        String base64 = Base64Utils.encodeToString(buffer);
        ConfigItem stockDeptItem = configItemMap.get("systemLogo");
        stockDeptItem.setShowValue("data:image/jpeg;base64," + base64);
    }

    public FieldColumnDto getFieldColumn(String key) {
        return getFieldColumn(selectSite, key);
    }

    public FieldColumnDto getFieldColumn(OrgInfo orgInfo, String key) {
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(orgInfo.getSiteUID(), orgInfo.getHospitalUID(), orgInfo.getInstitutionUID(), orgInfo.getTenantUID(), key);
        return JSON.parseObject(value, FieldColumnDto.class);
    }


    public void siteToSiteAutoAck(ConfigItem item){
        ConfigItemValue configItemValue = item.getConfigItemValue();
        String key = item.getKey();
        if(!"enableSimpleDepartmentSecondment".equals(key)){
            return;
        }
        String siteUid = configItemValue.getSiteUid();
        List<OrgInfo> hospitalChildrenByUids = orgInfoRepository.getHospitalChildrenByUids(Arrays.asList(siteUid));
        List<String> collect = hospitalChildrenByUids.stream().filter(org -> Integer.valueOf(3).equals(org.getOrgType())).map(OrgInfo::getUid).collect(Collectors.toList());
        for (String siteUID : collect) {
            sharedAssetService.asynRemoteCall(GatewayURL.SITE_TO_SITE_AUTO_ACK, HttpMethod.GET,new HashMap<>(),(url)->url+"?siteUID="+siteUID);
        }
    }

    public void pmOrderCreateByCycleDlg(ConfigItem configItem) {
        pmOrderPreCreateService.deleteBySiteUid(user.getSiteUID());
        saveConfig(configItem);
        invokeBatchPmOrderCancel(user.getSiteUID());
    }


    public void invokeBatchPmOrderCancel(String siteUid){
        String token = UserContextService.getAccessToken();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        List<V2_PmOrder> batchPmOrderList =v2PmOrderRepository.findBySiteUidAndPmTypeAndIsFinished(siteUid,1,false);
        if(CollectionUtils.isEmpty(batchPmOrderList)){
            return;
        }
        batchPmOrderList.forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setPmOrderId(item.getId());
            if(item.getPmType()==1){
                formData.setQualityChecking("2");
            }
            formData.setStepId(item.getCurrentStepId());
            formDataList.add(formData);
            formData.setDesc("配置项修改批量取消周期工单");
        });
        String res = srApi.invokeBatchPmOrderCancel(token,formDataList,"cancel");
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage("取消周期性保养工单失败");
        }

    }

    public void onItemRowReorder(ReorderEvent event){
        logger.info(event.toString());
        List<FieldColumnDto> list = configItemMap.get(itemValue).getFieldColumnDtoList();
        for (FieldColumnDto dto : list){
            dto.setRendered(fieldRenderedMap.get(itemValue + "_" + dto.getField()));
        }
    }

    public void columnSelectedChange(String field){
        logger.info(field);
        List<FieldColumnDto> list = configItemMap.get(itemValue).getFieldColumnDtoList();
        for (FieldColumnDto dto : list){
            if(field.equals(dto.getField())){
                logger.info(dto.getRendered());
                fieldRenderedMap.put(itemValue + "_"+ dto.getField(),dto.getRendered());
            }
        }
    }

    public void onItemRowReorderCommon(ReorderEvent event){
        logger.info(event.toString());
        logger.info(((DataTable)event.getSource()).getClientId());
        String ty = ((DataTable)event.getSource()).getClientId();
        ty = ty.replaceAll("configItemForm:tabView:", "");
        List<FieldColumnDto> list = configItemMap.get(ty).getFieldColumnDtoList();
        for (FieldColumnDto dto : list){
            dto.setRendered(fieldRenderedMap.get(ty + "_" + dto.getField()));
        }
    }

    public void columnSelectedChangeCommon(String field,String type){
        logger.info(field);
        List<FieldColumnDto> list = configItemMap.get(type).getFieldColumnDtoList();
        for (FieldColumnDto dto : list){
            if(field.equals(dto.getField())){
                logger.info(dto.getRendered());
                fieldRenderedMap.put(type + "_"+ dto.getField(),dto.getRendered());
            }
        }
    }

    public List<String> getMultiSelectedList(String key) {
        return multiSelectedMap.get(key);
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public LinkedHashMap<Integer, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(LinkedHashMap<Integer, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<String> getMultiSelected() {
        return multiSelected;
    }

    public void setMultiSelected(List<String> multiSelected) {
        this.multiSelected = multiSelected;
    }

    public Map<String, List<String>> getMultiSelectedMap() {
        return multiSelectedMap;
    }

    public void setMultiSelectedMap(Map<String, List<String>> multiSelectedMap) {
        this.multiSelectedMap = multiSelectedMap;
    }

    public List<ConfigItem> getConfigItemList() {
        return configItemList;
    }

    public void setConfigItemList(List<ConfigItem> configItemList) {
        this.configItemList = configItemList;
    }

    public Map<String, ConfigItem> getConfigItemMap() {
        return configItemMap;
    }

    public void setConfigItemMap(Map<String, ConfigItem> configItemMap) {
        this.configItemMap = configItemMap;
    }

    public OrgInfo getAssetOutStockDept() {
        return assetOutStockDept;
    }

    public void setAssetOutStockDept(OrgInfo assetOutStockDept) {
        this.assetOutStockDept = assetOutStockDept;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public List<String> getTextAloneHandleType() {
        return textAloneHandleType;
    }

    public List<String> getDatePatternSelect() {
        return datePatternSelect;
    }

    public void setDatePatternSelect(List<String> datePatternSelect) {
        this.datePatternSelect = datePatternSelect;
    }

    public void costTypeAdd(ConfigItem configItem) {
        if(StringUtils.isBlank(selectItemDto.getLabel())  || StringUtils.isBlank(selectItemDto.getValue())){

            WebUtil.addWarnMessage("成本类型的key和value必填");
        }
        List<Map<String, String>> enumList = configItem.getEnumList();

        for (Map<String, String> map : enumList) {
             if(map.containsKey(selectItemDto.getLabel())){
                 WebUtil.addWarnMessage("成本类型的key已存在");
             }
            if(map.containsValue(selectItemDto.getValue())){
                WebUtil.addWarnMessage("成本类型的value已存在");
            }
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("key",selectItemDto.getLabel());
        stringStringHashMap.put("value",selectItemDto.getValue());
        enumList.add(stringStringHashMap);
        configItem.setEnumList(enumList);
        saveConfig(configItem);
    }

    public void costTypeAddBefore() {
        selectItemDto=new SelectItemDto(null,null);
    }

    public void costTypeDelete(ConfigItem configItem) {
        if(configItem==null){
            return ;
        }
        if(CollectionUtils.isNotEmpty(deleteEnumKey)){
            List<Map<String, String>> enumList = configItem.getEnumList();

            enumList.removeIf(map -> {
                String value = map.get("value");
                return value != null && deleteEnumKey.contains(value);
            });
            configItem.setEnumList(enumList);
            saveConfig(configItem);
            dataEntryService.deleteByCostType(deleteEnumKey,selectSite.getUid());
        }
    }


    public void costTypeEdit(ConfigItem configItem) {
        if (configItem == null) {
            return;
        }

        List<Map<String, String>> enumList = configItem.getEnumList();

        enumList.removeIf(map -> {
            String value = map.get("value");
            return selectItemDto.getValue().equals(value);
        });
        configItem.setEnumList(enumList);
        costTypeAdd(configItem);
    }

    public List<Map<String, String>> costTypeDeleteBefore(ConfigItem configItem) {
        if(configItem==null){
            return null;
        }
        List<Map<String, String>> enumList = configItem.getEnumList();
        String validation = configItem.getValidation();
        List<Map<String, String>> mapList = JSON.parseObject(validation, new TypeReference<List<Map<String,String>>>(){});
        return getDifference(enumList, mapList, "key");

    }

    public void editSelectedEnum(ConfigItem configItem) {
        if(StringUtils.isBlank(editEnumKey)){
            return;
        }
        List<Map<String, String>> enumList = configItem.getEnumList();
        for (Map<String, String> map : enumList) {
            String value = map.get("value");
            if(value.equals(editEnumKey)){
                selectItemDto=new SelectItemDto(map.get("key"),map.get("value"));
            }
        }

    }
}

package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
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
    private Map<String, Object> selectedObjectMap = MapUtil.newHashMap();
    private List<OrgInfo> deptList;

    private List<UserAccount> assetStaffList;

    private List<UserAccount> assetHeadList;
    private final List<String> textAloneHandleType = ListUtil.newArrayList("setAssetOutStockDept", "systemLogo");

    private static final String VALUE ="value";
    private List<String> datePatternSelect = ListUtil.newArrayList("yyyy/MM/dd", "yyyy/MM/dd HH", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss");

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
        selectSite = orgInfoRepository.getByUid(user.getSiteUID());
        loadClinicalDept();
        loadAssetStaff();
        loadAssetHead();
        categoryMap = new LinkedHashMap<>();
        categoryMap.put(1,"设备档案");
        categoryMap.put(2,"设备调剂");
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
                    configItem.setEnumList(mapList);
                }
                if (9 == configItem.getValueType()) {
                    configItem.setFieldColumnDto(JSON.parseObject(configItem.getShowValue(), FieldColumnDto.class));
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

            }
            this.searchFilters = null;
        } else {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", "configItemList.xhtml");
        }
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
                    configItem.setFieldColumnDto(JSON.parseObject(configItem.getDefaultValue(), FieldColumnDto.class));
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
                    configItemValue.setValue(JSON.toJSONString(maps));
                }
            } else if (9 == this.selected.getValueType()) {
                configItemValue.setValue(JSON.toJSONString(this.selected.getFieldColumnDto()));
            } else{
                configItemValue.setValue(this.selected.getShowValue());
            }
            configItemValueRepository.save(configItemValue);
            this.selected.setConfigItemValue(configItemValue);
        }
        WebUtil.addErrorMessage("保存成功！");
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
}

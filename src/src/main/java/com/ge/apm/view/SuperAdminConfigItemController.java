package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.ConfigItemRepository;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
public class SuperAdminConfigItemController extends GenericCRUDUUIDController<ConfigItem> {

    private ConfigItemRepository dao;
    private ConfigItemValueRepository configItemValueRepository;
    private OrgInfoRepository orgInfoRepository;

    private FileDownloadService fileDownloadService;

    private OrgInfo selectSite;
    private UserAccount user;
    private Boolean superAdminEdit = true;

    private Map<Integer, String> categoryMap;
    private List<String> multiSelected;
    private static final String VALUE ="value";

    private UploadedFile file;
    @Getter
    @Setter
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;
    @Override
    protected void init() {
        dao = WebUtil.getBean(ConfigItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);

        user = UserContextService.getCurrentUserAccount();

        selectSite = orgInfoRepository.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        categoryMap = new HashMap<>();
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
                if((7 == configItem.getValueType() || 8 == configItem.getValueType())
                        && StringUtils.isNotEmpty(configItem.getValidation())){
                    String validation = configItem.getValidation();
                    List<Map<String, String>> mapList = JSON.parseObject(validation, new TypeReference<List<Map<String,String>>>(){});
                    configItem.setEnumList(mapList);
                }
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
    }

    @Override
    public void prepareEdit() {
        if(8 == this.selected.getValueType()){
            this.multiSelected = new ArrayList<>();
            String showValue = this.selected.getShowValue();
            if(StringUtils.isNotEmpty(showValue)){
                List<Map<String, String>> valueList = JSON.parseObject(showValue, List.class);

                for (Map<String, String> valueMap : valueList) {
                    multiSelected.add(valueMap.get(VALUE));
                }
            }
        }
        super.prepareEdit();
    }

    public void saveConfig(){
        if(this.selectSite != null){
            if( selected.getValueType()!=8 &&  !regularCheck()){
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

            if(4 == this.selected.getValueType() || 5 == this.selected.getValueType() || 6 == this.selected.getValueType()){
                try {
                    configItemValue.setValue(formatDate(this.selected.getShowValue()));
                } catch (ParseException e) {
                    logger.error("配置项保存，日期格式化异常：", e);
                    WebUtil.addErrorMessage("配置项保存异常，请联系系统管理员！");
                    return;
                }
            }else if(8 == this.selected.getValueType()){
                if(CollectionUtils.isNotEmpty(this.multiSelected)){
                    List<Map<String, String>> maps = new ArrayList<>();
                    List<Map<String, String>> enumList = this.selected.getEnumList();
                    for (String selectedVal : multiSelected) {
                        for (Map<String, String> stringMap : enumList) {
                            if(stringMap.containsValue(selectedVal)){
                                maps.add(stringMap);
                            }
                        }
                    }
                    configItemValue.setValue(JSON.toJSONString(maps));
                }
            }else{
                configItemValue.setValue(this.selected.getShowValue());
            }

            configItemValueRepository.save(configItemValue);
            clear();
        }
    }

    private void clear() {
        uploadedFileObject = null;
        file=null;
        selected = null;
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

    public void uploadDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        uploadedFileObject = blobService.uploadFile(file);
        if(uploadedFileObject!=null){
            selected.setShowValue(uploadedFileObject.getObjectStorageId());
        }
        logger.info("File is uploaded as " + uploadedFileObject.getObjectStorageId());
    }
    public void downloadFile(String fileId) {
        if(StringUtils.isNotEmpty(fileId)){
            fileDownloadService.downloadFile(fileId);
        }
        WebUtil.addWarnMessage("没有上传文件");

    }

    public void downloadConfigFile( ) {
        if( "workOrderScript".equals(selected.getKey())){
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("report/workOrderScript.xlsx");
            fileDownloadService.exportFile(is, "workOrderScript.xlsx");
        }
    }

    public String getDefaultValue(String key) {
        ConfigItem configItem = dao.findByDefaultValueForKey(key);
        return null == configItem ? "" : configItem.getDefaultValue();
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public Map<Integer, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<Integer, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<String> getMultiSelected() {
        return multiSelected;
    }

    public void setMultiSelected(List<String> multiSelected) {
        this.multiSelected = multiSelected;
    }
}

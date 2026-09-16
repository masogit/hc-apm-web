package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/5/15
 * @descript 历史留痕操作
 * @package com.ge.apm.service.asset
 */
@Component
@Slf4j
public class AssetHistoryService {
    public static final String FIELD_ASSET_DEPT_NAME = "assetDeptName";
    public static final String FILED_CLINICAL_DEPT_NAME = "clinicalDeptName";
    public static final String FILED_DEVICE_STATUS = "deviceStatus";
    public static final String FILED_STATUS = "status";
    public static final String IS_VALID = "isValid";
    public static final String ASSET_DISPOSAL_RESULTS = "assetDisposalResults";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private AssetInfoRepository dao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private I18nMessageService i18nMessageService;

    @Autowired
    private CustomConfigService ccService;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private ElasticService elasticService;

    /**
     * @return void
     * @throws
     * @params [assetHistory]
     * @author csh
     * @createTime 2020/5/15 13:41
     * @description 保存历史留痕操作记录
     */
    public void save(AssetHistory assetHistory) {
        assetHistoryRepository.save(assetHistory);
    }

    public List<AssetHistory> findByAssetUid(String assetUid) {
        return assetHistoryRepository.findByUidOrderByCreatedDateDescAtMostFive(assetUid);
    }

    public List<JSONObject> getAssetChangeHistoryByJSON(String assetUid) {
        return getAssetChangeHistoryByJSON(assetUid, null, null, 0, 5);
    }

    public List<JSONObject> getAssetChangeHistoryByJSON(String assetUid, Date startTime, Date endTime, int pageNum, int pageSize) {
        List<SearchFilter> searchFilters =new ArrayList<> ();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ, assetUid));
        if (ObjectUtil.isNotEmpty(startTime)) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (ObjectUtil.isNotEmpty(endTime)) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LTE, endTime));
        }
        List<String> assetsImportantFieldsAll = CustomConfigConstant.getAssetsImportantFieldsAll();
        searchFilters.add(new SearchFilter("has_columns", SearchFilter.Operator.IN, assetsImportantFieldsAll));
        Page<AssetHistory> assetHistoryPage = elasticService.searchAssetHistory(searchFilters, UserContextService.getAccessToken(), pageNum, pageSize);
        List<AssetHistory> resultList = assetHistoryPage.getContent();
        CustomConfigObject customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        resultList = resultList.stream().filter(assetHistory -> {
            List<AssetHistory.AssetHistoryInfo> assetHistoryInfos = JSONArray.parseArray(assetHistory.getJsonData(), AssetHistory.AssetHistoryInfo.class);
            assetHistoryInfos = assetHistoryInfos.stream().filter(assetHistoryInfo -> customConfig.getAssetsImportantFields().contains(assetHistoryInfo.getColumnName())).collect(Collectors.toList());
            assetHistory.setJsonData(JSONArray.toJSONString(assetHistoryInfos));
            return assetHistoryInfos.size() > 0;
        }).collect(Collectors.toList());
        return parseFieldsByJson(resultList);
    }

    /**
     * @return
     * @throws
     * @params [list]
     * @author csh
     * @createTime 2020/5/15 16:29
     * @description 解析json格式
     */
    public List<JSONObject> parseFieldsByJson(List<AssetHistory> list) {

        List<JSONObject> valueLists = new ArrayList<>();

        list.stream().forEach(assetHistory -> {
            String jsonData = assetHistory.getJsonData();
            JSONArray objects = JSONArray.parseArray(jsonData);
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("assetName", assetHistory.getAssetName());
            jsonObject.put("financingNum", assetHistory.getFinancingNum());
            jsonObject.put("createdBy", assetHistory.getCreatedBy());
            jsonObject.put("createdDate", assetHistory.getCreatedDate());
            jsonObject.put("assetUid", assetHistory.getAssetUid());
            jsonObject.put("modifySource", ModifySourceEnum.getLabelByCode(assetHistory.getModifySource()));

            //multiple column value changed
            objects.stream().forEach(item -> {
                String columnName = ((JSONObject) item).getString("columnName");
                if("createdBy".equals(columnName)){
                    return;
                }
                if("createdDate".equals(columnName)){
                    return;
                }
                String oldColumnValue = "";
                if (((JSONObject) item).containsKey("oldColumnValue")) {
                    oldColumnValue = ((JSONObject) item).getString("oldColumnValue");
                }
                String newColumnValue = ((JSONObject) item).getString("newColumnValue") != null ? ((JSONObject) item).getString("newColumnValue") : "";
                //String columnValue = "新值：" + newColumnValue + " -> 旧值：" + oldColumnValue;
                if("qrCode".equals(columnName)){
                    jsonObject.put(columnName+"Old", oldColumnValue);
                    jsonObject.put(columnName+"New", newColumnValue);
                }else {

                    String columnValue = "旧值:" + oldColumnValue + " -> 新值:" + newColumnValue;
                    jsonObject.put(columnName, columnValue);
                }


            });

            valueLists.add(jsonObject);
        });
        return valueLists;
    }

    private Optional<AssetHistory.AssetHistoryInfo> getSpecialFieldValue(AssetInfo oldAsset, AssetInfo newAsset, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Object oldFieldValue = null;
        Object newFieldValue = null;

        if (FILED_DEVICE_STATUS.equals(fieldName)) {   // 资产状态
            Field field = oldAsset.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            if (null != oldFieldValue) {
                oldFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("deviceStatus", String.valueOf(oldFieldValue));
            }
            if (null != newFieldValue) {
                newFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("deviceStatus", String.valueOf(newFieldValue));
            }

        } else if(FILED_STATUS.equals(fieldName)) {    // 当前状态
            Field field = oldAsset.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            if (null != oldFieldValue) {
                oldFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("assetStatus", String.valueOf(oldFieldValue));
            }
            if (null != newFieldValue) {
                newFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("assetStatus", String.valueOf(newFieldValue));
            }
        } else if(IS_VALID.equals(fieldName)) {
            Field field = oldAsset.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            if (null != oldFieldValue) {
               oldFieldValue = Boolean.valueOf(String.valueOf(oldFieldValue)) ? "是" : "否";
            }
            if (null != newFieldValue) {
                newFieldValue = Boolean.valueOf(String.valueOf(newFieldValue)) ? "是" : "否";
            }
        }else if(ASSET_DISPOSAL_RESULTS.equals(fieldName)) {
            Field field = oldAsset.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            if (null != oldFieldValue) {
                oldFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("assetDisposalResultsType", String.valueOf(oldFieldValue));
            }
            if (null != newFieldValue) {
                newFieldValue = i18nMessageService.getMsgLabelByMsgTypeAndMsgKey("assetDisposalResultsType", String.valueOf(newFieldValue));
            }
        }

        if (Objects.equals(oldFieldValue, newFieldValue)) {
            return Optional.empty();
        } else {
            AssetHistory.AssetHistoryInfo assetHistoryInfo = new AssetHistory.AssetHistoryInfo();
            assetHistoryInfo.setColumnName(fieldName);
            assetHistoryInfo.setOldColumnValue(objToString(fieldName, oldFieldValue));
            assetHistoryInfo.setNewColumnValue(objToString(fieldName, newFieldValue));
            assetHistoryInfo.setLabel(WebUtil.getMessage(fieldName));

            return Optional.of(assetHistoryInfo);
        }
    }

    private Optional<AssetHistory.AssetHistoryInfo> getCommonFieldValue(AssetInfo oldAsset, AssetInfo newAsset, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = oldAsset.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        Object oldFieldValue = field.get(oldAsset);
        Object newFieldValue = field.get(newAsset);

        if (Objects.equals(oldFieldValue, newFieldValue)) {
            return Optional.empty();
        }
        String oldValue = ObjectUtils.toString(oldFieldValue, "");
        String newValue = ObjectUtils.toString(newFieldValue, "");

        if(org.apache.commons.lang3.StringUtils.isBlank(oldValue) && org.apache.commons.lang3.StringUtils.isBlank(newValue)){
            return Optional.empty();
        }
        if ("purchasePrice".equals(fieldName)) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
            if (oldFieldValue != null) {
                oldFieldValue = df.format(oldFieldValue);
            }
            if (newFieldValue != null) {
                newFieldValue = df.format(newFieldValue);
            }
        }
        Temporal temporal = field.getAnnotation(Temporal.class);
        AssetHistory.AssetHistoryInfo assetHistoryInfo = new AssetHistory.AssetHistoryInfo();
        assetHistoryInfo.setColumnName(fieldName);
        if(temporal!=null && temporal.value().equals(TemporalType.DATE)){
            oldFieldValue= objToString(field, oldFieldValue);
            newFieldValue = objToString(field, newFieldValue);
            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }
            assetHistoryInfo.setOldColumnValue(oldFieldValue==null?null:oldFieldValue.toString());
            assetHistoryInfo.setNewColumnValue(newFieldValue==null?null:newFieldValue.toString());
        }else{
            assetHistoryInfo.setOldColumnValue(objToString(fieldName, oldFieldValue));
            assetHistoryInfo.setNewColumnValue(objToString(fieldName, newFieldValue));
        }

        assetHistoryInfo.setLabel(WebUtil.getMessage(fieldName));

        return Optional.of(assetHistoryInfo);

    }
    private String objToString(Field field, Object fieldValue) {
        if (null == fieldValue) {
            return null;
        }

        if (fieldValue instanceof Date) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            return  sdf2.format(fieldValue);
        }
        String result = fieldValue.toString();

        return result;
    }

    private String objToString(String fieldName, Object fieldValue) {
        if (null == fieldValue) {
            return null;
        }

        if (fieldValue instanceof Date) {
            return sdf.format(fieldValue);
        }

        String result = fieldValue.toString();

        return result;
    }

    public List<AssetHistory.AssetHistoryInfo> getAssetsHistoryModifiedFields(AssetInfo oldAsset, AssetInfo newAsset) {

        List<AssetHistory.AssetHistoryInfo> resultList = new ArrayList<>();
        Field[] declaredFields = newAsset.getClass().getDeclaredFields();
        List<Field> fields1 = Arrays.asList(declaredFields);
        List<Field> fields2 = fields1.stream().filter(item -> {
            Column annotation = item.getAnnotation(Column.class);
            return annotation!=null;
        }).collect(Collectors.toList());

        List<String> collect = fields2.stream().map(item -> item.getName()).collect(Collectors.toList());
        List<String> assetsImportantFieldsAll = CustomConfigConstant.getAssetsImportantFieldsAll();
        List<String> removeList=Arrays.asList("id","createdDate");
        collect.addAll(assetsImportantFieldsAll);
        collect.removeAll(removeList);
        collect = collect.stream().distinct().collect(Collectors.toList());
        collect.stream().forEach(fieldName -> {

            try {
                Optional<AssetHistory.AssetHistoryInfo> optionalResult;
                switch (fieldName) {
                    case FILED_DEVICE_STATUS:
                    case FILED_STATUS:
                    case IS_VALID:
                    case ASSET_DISPOSAL_RESULTS:
                        optionalResult = getSpecialFieldValue(oldAsset, newAsset, fieldName);
                        break;
                    case FIELD_ASSET_DEPT_NAME:
                        String oldName = orgDao.findNameByUID(oldAsset.getAssetDeptUID());
                        String newName = orgDao.findNameByUID(newAsset.getAssetDeptUID());
                        optionalResult = getSpecialFieldValue(FIELD_ASSET_DEPT_NAME, oldName, oldAsset.getAssetDeptUID(), newName, newAsset.getAssetDeptUID());
                        break;
                    case FILED_CLINICAL_DEPT_NAME:
                        optionalResult = getSpecialFieldValue(FILED_CLINICAL_DEPT_NAME, oldAsset.getClinicalDeptName(), oldAsset.getClinicalDeptUID(), newAsset.getClinicalDeptName(), newAsset.getClinicalDeptUID());
                        break;
                    default:
                        optionalResult = getCommonFieldValue(oldAsset, newAsset, fieldName);
                        break;
                }

                if (optionalResult.isPresent()) {
                    resultList.add(optionalResult.get());
                }

            } catch (Exception e) {
                log.error("getAssetsHistoryModifiedFields[]error", e);
                throw new RuntimeException("变更留痕保存失败");
            }
        });

        return resultList;
    }

    private Optional<AssetHistory.AssetHistoryInfo> getSpecialFieldValue(String columnName, String oldName, String oldUID, String newName, String newUID) {

        if (StringUtils.isEmpty(oldUID) && StringUtils.isEmpty(newUID)) {
            return Optional.empty();
        }

        if (StringUtils.hasLength(oldUID) && oldUID.equals(newUID)) {
            return Optional.empty();
        }

        String pOldOrgName = this.findOrgInfoParentNameByUid(oldUID);
        String pNewOrgName = this.findOrgInfoParentNameByUid(newUID);

        AssetHistory.AssetHistoryInfo assetHistoryInfo = new AssetHistory.AssetHistoryInfo();
        assetHistoryInfo.setColumnName(columnName);

        assetHistoryInfo.setOldColumnValue(StringUtil.joinNotEmpty("-", pOldOrgName, oldName));
        assetHistoryInfo.setNewColumnValue(StringUtil.joinNotEmpty("-", pNewOrgName, newName));
        assetHistoryInfo.setLabel(WebUtil.getMessage(columnName));
        return Optional.of(assetHistoryInfo);
    }

    private String findOrgInfoParentNameByUid(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return null;
        }
       return orgDao.findParentNameByUid(uid);
    }

    public void bathDepartmentConvert(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setClassName("AssetHistoryService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("bathDepartmentConvert");
        mqContext.setDescribe("资产批量转科");
        mqContext.setModifySource(ModifySourceEnum.BATCH_TRANSFER.getCode());
        mqContext.setSource("departmentConvert");
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
    }

    public void saveAssetHistoryIfUpdated(AssetInfo oldAsset, AssetInfo newAsset, String modifySource) {
        MqContext mqContext = new MqContext();
        mqContext.setClassName("AssetHistoryService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("saveAssetHistoryIfUpdated");
        mqContext.setDescribe(ModifySourceEnum.getLabelByCode(modifySource));
        mqContext.setModifySource(modifySource);
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
    }

    public void deleteAsset(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setClassName("AssetHistoryService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("deleteAsset");
        mqContext.setDescribe("删除设备");
        mqContext.setModifySource(ModifySourceEnum.ASSET_DELETE.getCode());
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
    }

    public  <T> T depCopy(T srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            T destList = (T) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String SHEET_IMPORT_NAME = "sheet1";


    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> dataList) {

        Integer keyRow_fileScanReport = 1;
        Integer dataRow_fileScanReport = 2;

        for (Map<String, Object> item : dataList) {
            doc.writeRowData(SHEET_IMPORT_NAME, keyRow_fileScanReport, dataRow_fileScanReport++, item);
        }

        return Boolean.TRUE;
    }

    public List<Map<String, Object>> createFileScanData(List<JSONObject> fileScanList){

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (JSONObject importFilesVO : fileScanList) {
            Map<String, Object> resultMap = new HashedMap();

            resultMap.put("资产名称", importFilesVO.get("assetName"));
            resultMap.put("资产编号", importFilesVO.get("financingNum"));
            resultMap.put("设备编号（院方）", importFilesVO.get("departNum"));
            resultMap.put("使用科室", importFilesVO.get("assetDeptName"));
            resultMap.put("所属科室", importFilesVO.get("clinicalDeptName"));
            resultMap.put("使用责任人", importFilesVO.get("clinicalOwnerName"));
            resultMap.put("安装位置", importFilesVO.get("locationName"));
            resultMap.put("资产状态", importFilesVO.get("deviceStatus"));
            resultMap.put("当前状态", importFilesVO.get("status"));
            resultMap.put("是否在用", importFilesVO.get("isValid"));
            resultMap.put("资产处置结果", importFilesVO.get("assetDisposalResults"));
            resultMap.put("采购日期", importFilesVO.get("purchaseDate"));
            resultMap.put("采购价格", importFilesVO.get("purchasePrice"));
            resultMap.put("二维码(旧)", importFilesVO.get("qrCodeOld"));
            resultMap.put("二维码(新)", importFilesVO.get("qrCodeNew"));
            resultMap.put("启用日期", importFilesVO.get("installDate"));
            resultMap.put("设备型号", importFilesVO.get("functionType"));
            resultMap.put("维修负责人", importFilesVO.get("assetOwnerName"));
            resultMap.put("制造商", importFilesVO.get("manufacture"));
            resultMap.put("品牌/制造商简称", importFilesVO.get("makerShortName"));
            resultMap.put("注册编号", importFilesVO.get("registrationNo"));
            resultMap.put("供应商", importFilesVO.get("vendor"));
            resultMap.put("医疗器械分类(旧)", importFilesVO.get("assetCode68"));
            resultMap.put("制造商序列号", importFilesVO.get("serialNum"));
            resultMap.put("出厂日期", importFilesVO.get("manufactDate"));
            resultMap.put("使用年限", importFilesVO.get("lifecycle"));
            resultMap.put("质保截止日期", importFilesVO.get("factoryWarrantyDate"));
            resultMap.put("用途分类", importFilesVO.get("assetUsage"));
            resultMap.put("资金来源", importFilesVO.get("capitalSource"));
            resultMap.put("计量等级", importFilesVO.get("measuringItemCategory"));
            resultMap.put("风险等级", importFilesVO.get("estimatedRiskLevel"));
            resultMap.put("是否急救设备", importFilesVO.get("emergencyEquipment"));
            resultMap.put("是否进口设备", importFilesVO.get("importEquipment"));
            resultMap.put("医疗器械分类", importFilesVO.get("functionGroup"));
            resultMap.put("修改人", importFilesVO.get("createdBy"));
            resultMap.put("修改来源", importFilesVO.get("modifySource"));
            if(importFilesVO.get("createdDate") != null){
                resultMap.put("修改时间", sdf.format(importFilesVO.get("createdDate")));
            }

            resultList.add(resultMap);
        }

        return resultList;

    }

}
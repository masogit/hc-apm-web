package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;

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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
        List<AssetHistory> resultList = assetHistoryRepository.findByUidOrderByCreatedDateDescAtMostFive(assetUid);
        CustomConfigObject customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        resultList = resultList.stream().filter(assetHistory -> {
            List<AssetHistory.AssetHistoryInfo> assetHistoryInfos = JSONArray.parseArray(assetHistory.getJsonData(), AssetHistory.AssetHistoryInfo.class);
            assetHistoryInfos = assetHistoryInfos.stream().filter(assetHistoryInfo -> customConfig.getAssetsImportantFields().contains(assetHistoryInfo.getColumnName())).collect(Collectors.toList());
            assetHistory.setJsonData(JSONArray.toJSONString(assetHistoryInfos));
            return assetHistoryInfos.size() > 0;
        }).collect(Collectors.toList());

        /*ListIterator<AssetHistory> assetHistoryList = resultList.listIterator();
        while(assetHistoryList.hasNext()){
            AssetHistory assetHistory = assetHistoryList.next();
            assetHistory
        }*/
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

            //multiple column value changed
            objects.stream().forEach(item -> {
                String columnName = ((JSONObject) item).getString("columnName");
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
        if ("purchasePrice".equals(fieldName)) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
            if (oldFieldValue != null) {
                oldFieldValue = df.format(oldFieldValue);
            }
            if (newFieldValue != null) {
                newFieldValue = df.format(newFieldValue);
            }
        }
        AssetHistory.AssetHistoryInfo assetHistoryInfo = new AssetHistory.AssetHistoryInfo();
        assetHistoryInfo.setColumnName(fieldName);
        assetHistoryInfo.setOldColumnValue(objToString(fieldName, oldFieldValue));
        assetHistoryInfo.setNewColumnValue(objToString(fieldName, newFieldValue));
        assetHistoryInfo.setLabel(WebUtil.getMessage(fieldName));

        return Optional.of(assetHistoryInfo);

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

        CustomConfigConstant.getAssetsImportantFieldsAll().stream().forEach(fieldName -> {

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

    public void saveAssetHistoryIfUpdated(AssetInfo oldAsset, AssetInfo newAsset) {

        List<AssetHistory.AssetHistoryInfo> resultList = getAssetsHistoryModifiedFields(oldAsset, newAsset);

        //no change, just return
        if (resultList.isEmpty()) {
            return;
        }

        //save changes
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        String jsonData = JSONArray.toJSONString(resultList);

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setAssetUid(newAsset.getUid());
        assetHistory.setAssetName(newAsset.getName());
        assetHistory.setFinancingNum(newAsset.getFinancingNum());
        assetHistory.setJsonData(jsonData);
        assetHistory.setCreatedDate(new Date());
        assetHistory.setCreatedBy(currentUserAccount.getName());

        assetHistory.setSiteUID(currentUserAccount.getSiteUID());
        assetHistory.setHospitalUID(currentUserAccount.getHospitalUID());
        assetHistory.setTenantUID(currentUserAccount.getTenantUID());
        assetHistory.setInstitutionUID(currentUserAccount.getInstitutionUID());
        assetHistory.setClinicalDeptUid(newAsset.getClinicalDeptUID());

        this.save(assetHistory);
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

}
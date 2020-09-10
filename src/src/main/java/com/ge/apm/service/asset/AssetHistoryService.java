package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.i18n.I18nService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author csh
 * @version 1.0
 * @date 2020/5/15
 * @descript 历史留痕操作
 * @package com.ge.apm.service.asset
 */
@Component
public class AssetHistoryService {
    public static final String FIELD_ASSET_DEPT_NAME = "assetDeptName";
    public static final String FILED_DEVICE_STATUS = "deviceStatus";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private I18nService i18nService;

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

    public List<AssetHistory> findByUidOrderByCreatedDateDescAtMostFive(String assetUid) {
        return assetHistoryRepository.findByUidOrderByCreatedDateDescAtMostFive(assetUid);
    }

    public List<JSONObject> getAssetChangeHistoryByJSON(String assetUid) {
        List<AssetHistory> resultList = assetHistoryRepository.findByUidOrderByCreatedDateDescAtMostFive(assetUid);
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
                String newColumnValue = ((JSONObject) item).getString("newColumnValue");
                String columnValue = "新值：" + newColumnValue + " -> 旧值：" + oldColumnValue;
                jsonObject.put(columnName, columnValue);

            });

            valueLists.add(jsonObject);
        });
        return valueLists;
    }

    private Optional<AssetHistory.AssetHistoryInfo> getSpecialFieldValue(AssetInfo oldAsset, AssetInfo newAsset, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Object oldFieldValue = null;
        Object newFieldValue = null;

        if (FIELD_ASSET_DEPT_NAME.equals(fieldName)) {
            Field field = oldAsset.getClass().getDeclaredField("assetDeptUID");
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            if (null != oldFieldValue) {
                OrgInfo oldOrg = orgDao.findByUid((String) oldFieldValue);
                if (null != oldOrg) {
                    oldFieldValue = oldOrg.getName();
                }
            }
            if (null != newFieldValue) {
                OrgInfo newOrg = orgDao.findByUid((String) newFieldValue);
                if (null != newOrg) {
                    newFieldValue = newOrg.getName();
                }
            }



        } else if (FILED_DEVICE_STATUS.equals(fieldName)){
            Field field = oldAsset.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            oldFieldValue = field.get(oldAsset);
            newFieldValue = field.get(newAsset);

            if (Objects.equals(oldFieldValue, newFieldValue)) {
                return Optional.empty();
            }

            AssetFileImportBaseService baseService = new AssetFileImportBaseService();
            if (null != oldFieldValue) {
                oldFieldValue = i18nService.findMatchValueByMsgKey((Integer) oldFieldValue, baseService.deviceStatusList);
            }
            if (null != newFieldValue) {
                newFieldValue = i18nService.findMatchValueByMsgKey((Integer) newFieldValue, baseService.deviceStatusList);
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

        if(fieldValue instanceof Date) {

            return sdf.format(fieldValue);
        }

        String result = fieldValue.toString();

        return result;
    }

    public List<AssetHistory.AssetHistoryInfo> getAssetsHistoryModifiedFields(AssetInfo oldAsset, AssetInfo newAsset, List<String> fieldNames) {

        List<AssetHistory.AssetHistoryInfo> resultList = new ArrayList<>();

        fieldNames.stream().forEach(fieldName -> {

            try {
                Optional<AssetHistory.AssetHistoryInfo> optionalResult;
                switch (fieldName) {
                    case FIELD_ASSET_DEPT_NAME:
                        optionalResult = getSpecialFieldValue(oldAsset, newAsset, fieldName);
                        break;

                    case FILED_DEVICE_STATUS:
                        optionalResult = getSpecialFieldValue(oldAsset, newAsset, fieldName);
                        break;

                    default:
                        optionalResult = getCommonFieldValue(oldAsset, newAsset, fieldName);
                        break;
                }

                if (optionalResult.isPresent()) {
                    resultList.add(optionalResult.get());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return resultList;
    }

    public void saveAssetHistoryIfUpdated(AssetInfo oldAsset, AssetInfo newAsset, List<String> fieldNames) {

        List<AssetHistory.AssetHistoryInfo> resultList = getAssetsHistoryModifiedFields(oldAsset, newAsset, fieldNames);

        //no change, just return
        if (resultList.isEmpty()) {
            return;
        }

        //save changes
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        String jsonData = JSONArray.toJSONString(resultList);

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setAssetUid(oldAsset.getUid());
        assetHistory.setAssetName(oldAsset.getName());
        assetHistory.setFinancingNum(oldAsset.getFinancingNum());

        assetHistory.setJsonData(jsonData);
        assetHistory.setCreatedDate(new Date());
        assetHistory.setCreatedBy(currentUserAccount.getName());

        assetHistory.setSiteUID(currentUserAccount.getSiteUID());
        assetHistory.setHospitalUID(currentUserAccount.getHospitalUID());
        assetHistory.setTenantUID(currentUserAccount.getTenantUID());
        assetHistory.setInstitutionUID(currentUserAccount.getInstitutionUID());

        this.save(assetHistory);
    }

}
package com.ge.apm.service.keyAssetReport;

import com.ge.apm.domain.AssetInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeyAssetReportService {

    public Set<Map<String, Object>> groupAsset(List<AssetInfo> assetList,Set<Map<String, Object>>  set){
//        Set<Map<String, Object>> set = new HashSet<>();

        if(CollectionUtils.isNotEmpty(assetList)){
            for (AssetInfo assetInfo : assetList) {
                String functionType = assetInfo.getFunctionType();
                String makerShortName = assetInfo.getMakerShortName();
                String locationName = assetInfo.getLocationName();
                if(null == functionType){
                    functionType = "";
                }
                if(null == makerShortName){
                    makerShortName = "";
                }
                if(null==locationName){
                    locationName = "";
                }
                String key = functionType + makerShortName+ locationName;
                Optional<Map<String, Object>> value = set.stream().filter(a -> key.equals(a.get("key"))).findFirst();

                if(value.isPresent()){
                    Map<String, Object> valueMap = value.get();
                    valueMap.put("quantity", Integer.parseInt(valueMap.get("quantity").toString()) + 1);
                    if(!valueMap.get("deptUid").toString().contains(assetInfo.getClinicalDeptUID())){
                        valueMap.put("deptUid", valueMap.get("deptUid")+","+assetInfo.getClinicalDeptUID());
                    }
                    // 获取启用日期
                    Date date = (Date) valueMap.get("installDate");
                    valueMap.put("locationName", locationName);

                    if(null != valueMap.get("installDate") && null != assetInfo.getInstallDate()){
                        if(date.getTime() > assetInfo.getInstallDate().getTime()){
                            valueMap.put("installDate", assetInfo.getInstallDate());
                        }
                    }
                }else{
                    Map<String, Object> valueMap = new HashMap<>();
                    valueMap.put("key", key);
                    valueMap.put("functionType", assetInfo.getFunctionType());
                    valueMap.put("brand", assetInfo.getMakerShortName());
                    valueMap.put("installDate", assetInfo.getInstallDate());
                    valueMap.put("quantity", 1);
                    valueMap.put("title", "设备明细");
                    valueMap.put("deptUid", assetInfo.getClinicalDeptUID());
                    valueMap.put("locationName", assetInfo.getLocationName());
                    set.add(valueMap);
                }
            }
        }
        return set;
    }

}

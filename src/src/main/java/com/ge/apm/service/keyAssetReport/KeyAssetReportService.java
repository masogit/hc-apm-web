package com.ge.apm.service.keyAssetReport;

import com.ge.apm.domain.AssetInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import webapp.framework.util.DateUtil;

import java.sql.Timestamp;
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
                    if(valueMap.get("deptUid")!=null){
                        if(!valueMap.get("deptUid").toString().contains(assetInfo.getClinicalDeptUID())){
                            valueMap.put("deptUid", valueMap.get("deptUid")+","+assetInfo.getClinicalDeptUID());
                        }
                    }else{
                        valueMap.put("deptUid", assetInfo.getClinicalDeptUID());
                    }
                    if(valueMap.get("assetId")!=null){
                        valueMap.put("assetId", valueMap.get("assetId")+","+assetInfo.getId());
                    }else{
                        valueMap.put("assetId", assetInfo.getId().toString());
                    }

                    // 获取启用日期
                    valueMap.put("locationName", locationName);
                    if(null == valueMap.get("installDate") && null != assetInfo.getInstallDate()){
                        valueMap.put("installDate", assetInfo.getInstallDate());
                    }
                    if(null != valueMap.get("installDate") && null != assetInfo.getInstallDate()){
                        Date date =null;
                        if(valueMap.get("installDate").getClass()==Date.class   || valueMap.get("installDate").getClass() == Timestamp.class  ||valueMap.get("installDate").getClass()==java.sql.Date.class){
                            date=(Date) valueMap.get("installDate");
                        }
                        if(valueMap.get("installDate").getClass()==String.class){
                            String installDate = (String) valueMap.get("installDate");
                            date = DateUtil.getDateByFormatStr(installDate, "yyyy-MM-dd");
                        }
                        if(date!=null){
                            if(date.getTime() > assetInfo.getInstallDate().getTime()){
                                valueMap.put("installDate", assetInfo.getInstallDate());
                            }
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
                    valueMap.put("assetId", assetInfo.getId().toString());
                    set.add(valueMap);
                }
            }
        }
        return set;
    }

}

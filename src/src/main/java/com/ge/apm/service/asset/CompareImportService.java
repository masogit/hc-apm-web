/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class CompareImportService {

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private ArrivalListItemRepository arrivalListItemDao;
    @Autowired
    private UaaService uaaService;

    public Map<String, Map<String, Object>> getAssetMapFromTemplate(ExcelDocument doc) throws Exception {

        Map<String, Map<String, Object>> result = new LinkedHashMap();
        int keyRow = 1;
        int dataRow = 2;
        String sheetName = doc.getWorkBook().getSheetAt(0).getSheetName();
        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());

        for (Map< String, String> rowMap = doc.getStringDataRowMap(sheetName, keyRow, dataRow); rowMap != null; rowMap = doc.getStringDataRowMap(sheetName, keyRow, ++dataRow)) {

            String assetName = Optional.ofNullable(rowMap.get("设备名称")).orElse("").toString();
            if (StringUtils.isEmpty(assetName)) {
                break; //return if go beyond the valid cell range
            }
            rowMap.put("RowIndex", String.valueOf(dataRow));
            AssetInfo asset = null;
            ArrivalListItem arrivalListItem = findArrivalListItemByMap(rowMap, orgUidList);
            if (null != arrivalListItem && arrivalListItem.getAssetUID() != null) {
                asset = assetDao.findByUid(arrivalListItem.getAssetUID());
            }

            Map< String, Object> dataMap =  new LinkedHashMap();
            
            dataMap.put("arrivalListItem", arrivalListItem);
            dataMap.put("asset", asset);
            dataMap.putAll(rowMap);

            result.put(String.valueOf(dataRow), dataMap);
        }
        return result;
    }

    private ArrivalListItem findArrivalListItemByMap(Map<String, String> map, List<String> userOrgList) {
        if (map == null) {
            return null;
        }
        List<SearchFilter> itemFilters = new ArrayList<>();
        itemFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
        itemFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
        itemFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
        itemFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
        String deptNum = Optional.ofNullable(map.get("设备编号")).orElse("").toString();
        if (!Strings.isNullOrEmpty(deptNum)) {
            itemFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, deptNum, "1*3+"));
        }
//        String qrcode = Optional.ofNullable(map.get("二维码")).orElse("").toString();
//        if (!Strings.isNullOrEmpty(qrcode)) {
//            itemFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.EQ, qrcode, "1*3+"));
//        }
//        String serialNum = Optional.ofNullable(map.get("制造商序列号")).orElse("").toString();
//        if (!Strings.isNullOrEmpty(serialNum)) {
//            itemFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.EQ, serialNum, "1*3+"));
//        }

        List<ArrivalListItem> itemList = arrivalListItemDao.findBySearchFilter(itemFilters);
        return (itemList != null && itemList.size() == 1) ? itemList.get(0) : null;
    }
    
    public ArrivalListItem updateArrivalListItem(ArrivalListItem item) {
       return arrivalListItemDao.save(item);
    }

}

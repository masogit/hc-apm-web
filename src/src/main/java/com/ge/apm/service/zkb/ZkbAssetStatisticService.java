package com.ge.apm.service.zkb;

import com.ge.apm.dao.mapper.ZkbAssetStatisticMapper;
import com.ge.apm.domain.ZkbAssetStatistic;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 503202755 2020/10/23
 */
@Component
public class ZkbAssetStatisticService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbAssetStatisticService.class);

    /**
     * 历史数据查询mapper
     */
    @Autowired
    private ZkbAssetStatisticMapper zkbAssetStatisticMapper;

    /**
     * excel模板名称
     */
    public static final String EXCEL_TEMPLATE_NAME="Asset_Statistic_Template.xlsx";

    /**
     * excel模板英文名称
     */
    public static final String EXCEL_TEMPLATE_EN_NAME="Asset_Statistic_Template_EN.xlsx";

    /**
     * 目录地址
     */
    public static final String DIRECOTORY_PATH="/portal/zkb/assetStatistic/";

    /**
     * excel sheet 名称
     */
    public static final String SHEET_ASSET_STATISTIC_LIST = "ASSET STATISTIC LIST";

    /**
     * 根据添加统计设备信息
     * @param tenantUid 租户UID
     * @param systemId SystemID/厂商报修ID
     * @return
     */
    public List<ZkbAssetStatistic> loadAllAsset(String tenantUid,String systemId){
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(tenantUid!=null)
        {
            SearchFilter dataTypeFilter = new SearchFilter("tenantUid", SearchFilter.Operator.EQ, tenantUid);
            searchFilters.add(dataTypeFilter);
        }
        if(systemId!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("systemId", SearchFilter.Operator.EQ, systemId);
            searchFilters.add(systemIdFilter);
        }
        List<ZkbAssetStatistic> assetList = zkbAssetStatisticMapper.loadAllAsset(searchFilters);
        if(CollectionUtils.isEmpty(assetList))
        {
            return null;
        }
        List<String> assetUIDList =assetList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getAssetUid()))),
                        ArrayList::new)).stream().map(ZkbAssetStatistic::getAssetUid).collect(Collectors.toList());
        List<ZkbAssetStatistic> assetList1 = zkbAssetStatisticMapper.loadQrCount(assetUIDList);
        if(CollectionUtils.isEmpty(assetList1))
        {
            return assetList;
        }
        Map<String, ZkbAssetStatistic> map = assetList1.stream().collect(Collectors.toMap(ZkbAssetStatistic::getBoUuid, Function.identity()));
        for(ZkbAssetStatistic zkbAssetStatistic:assetList)
        {
            ZkbAssetStatistic item=map.get(zkbAssetStatistic.getAssetUid());
            if(item==null) continue;
            zkbAssetStatistic.setQrCodeCount(item.getQrCodeCount());
        }
        return assetList;
    }

    /**
     * 整理需要导出的数据
     * @param zkbAssetStatisticList
     * @return
     */
    public List<Map<String, Object>> createZkbAssetStatisticData(List<ZkbAssetStatistic> zkbAssetStatisticList){
        List<Map<String, Object>> zkbAssetStatisticMapList = new ArrayList<>();
        if(CollectionUtils.isEmpty(zkbAssetStatisticList))
        {
            return zkbAssetStatisticMapList;
        }
        for (ZkbAssetStatistic zkbAssetStatistic : zkbAssetStatisticList) {
            Map<String, Object> zkbAssetStatisticMap = new HashMap<>();
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbHistory_agent"),zkbAssetStatistic.getName());
            zkbAssetStatisticMap.put(WebUtil.getMessage("systemId"),zkbAssetStatistic.getSystemId());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_countOfImage"),zkbAssetStatistic.getQrCodeCount());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_qrCodeOfPaste"),zkbAssetStatistic.getQrCodeCountState());
            zkbAssetStatisticMapList.add(zkbAssetStatisticMap);
        }
        return zkbAssetStatisticMapList;
    }
    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbAssetStatisticMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : zkbAssetStatisticMapList) {
            doc.writeRowData(SHEET_ASSET_STATISTIC_LIST, 1, dataRow++, item);
        }
    }

}

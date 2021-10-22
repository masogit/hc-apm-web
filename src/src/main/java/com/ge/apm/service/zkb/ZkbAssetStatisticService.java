package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbAssetQrCodeInfoRepository;
import com.ge.apm.dao.mapper.ZkbAssetStatisticMapper;
import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import com.ge.apm.domain.ZkbAssetStatistic;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ZkbAssetQrCodeInfoRepository zkbAssetQrCodeInfoRepository;

    /**
     * 根据添加统计设备信息
     * @param tenantUid 租户UID
     * @param systemId SystemID/厂商报修ID
     * @param qrCodeCount 二维码个数
     * @param qrCodeCountState 二维码状态
     * @param contractNo 合同编号
     * @param hospitalName 医院名称
     * @param functionType 设备型号
     * @param canNotPastQrCode 无法粘贴二维码
     * @return
     */
    public List<ZkbAssetStatistic> loadAllAsset(String tenantUid,String systemId,String qrCodeCount,
                                                String qrCodeCountState,String contractNo,String hospitalName,String functionType,String canNotPastQrCode){
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
        if(contractNo!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("contractNo", SearchFilter.Operator.EQ, contractNo);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(hospitalName))
        {
            SearchFilter systemIdFilter = new SearchFilter("hospitalName", SearchFilter.Operator.EQ, hospitalName);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(functionType))
        {
            SearchFilter systemIdFilter = new SearchFilter("functionType", SearchFilter.Operator.EQ, functionType);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(canNotPastQrCode))
        {
            SearchFilter systemIdFilter = new SearchFilter("canNotPastQrCode", SearchFilter.Operator.EQ, canNotPastQrCode);
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
        if(CollectionUtils.isNotEmpty(assetList1))
        {
            Map<String, ZkbAssetStatistic> map = assetList1.stream().collect(Collectors.toMap(ZkbAssetStatistic::getBoUuid, Function.identity()));
            for(ZkbAssetStatistic zkbAssetStatistic:assetList)
            {
                ZkbAssetStatistic item=map.get(zkbAssetStatistic.getAssetUid());
                if(item==null) continue;
                zkbAssetStatistic.setQrCodeCount(item.getQrCodeCount());
            }
        }
        if(StringUtils.isNotBlank(qrCodeCount))
        {
            assetList=assetList.stream().filter(s->qrCodeCount.equals(s.getQrCodeCount())).collect(Collectors.toList());
        }
        if(StringUtils.isNotBlank(qrCodeCountState))
        {
            assetList=assetList.stream().filter(s->qrCodeCountState.equals(s.getQrCodeCountState())).collect(Collectors.toList());
        }
        if(StringUtils.isNotBlank(canNotPastQrCode))
        {
            assetList=assetList.stream().filter(s->canNotPastQrCode.equals(s.getCanNotPastQrCode())).collect(Collectors.toList());
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
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_hospitalName"),zkbAssetStatistic.getHospitalName());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_functionType"),zkbAssetStatistic.getFunctionType());
            zkbAssetStatisticMap.put(WebUtil.getMessage("systemId"),zkbAssetStatistic.getSystemId());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_countOfImage"),zkbAssetStatistic.getQrCodeCount());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_contractNo"),zkbAssetStatistic.getContractNo());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_qrCodeOfPaste"),zkbAssetStatistic.getQrCodeCountState());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_canNotPast"),zkbAssetStatistic.getCanNotPastQrCode());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_canNotPastReason"),zkbAssetStatistic.getReason());
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

    public void regenerateZkbAssetQrCodeInfo(List<SearchFilter> searchFilter){
        //先清除数据在重新生成
        zkbAssetQrCodeInfoRepository.deleteZkbAssetQrCodeInfo();
        List<ZkbAssetQrCodeInfo> zkbAssetQrCodeInfoList = zkbAssetStatisticMapper.getZkbAssetQrCodeInfoList(searchFilter);
        if(zkbAssetQrCodeInfoList == null || zkbAssetQrCodeInfoList.size() == 0){
            WebUtil.addErrorMessage("没有新的二维码记录需要生成");
            return ;
        }
        for (ZkbAssetQrCodeInfo zkbAssetQrCodeInfo : zkbAssetQrCodeInfoList){
            handleQRCodeInfo(zkbAssetQrCodeInfo);
        }
        //这里主要是针对历史数据的数据补全
        List<ZkbAssetQrCodeInfo> zkbAssetQrCodeInfoList2 = zkbAssetStatisticMapper.getZkbAssetQrCode();
        if(zkbAssetQrCodeInfoList2 != null || zkbAssetQrCodeInfoList2.size() > 0){
            for (ZkbAssetQrCodeInfo zkbAssetQrCodeInfo : zkbAssetQrCodeInfoList2){
                zkbAssetQrCodeInfoRepository.save(zkbAssetQrCodeInfo);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleQRCodeInfo(ZkbAssetQrCodeInfo zkbAssetQrCodeInfo){
        if(zkbAssetQrCodeInfo == null){
            return;
        }
        String qrCodeCountState = zkbAssetQrCodeInfo.getQrCodeCountState();
        Date date = new Date();
        if(qrCodeCountState == null || qrCodeCountState.equals("N")){
            if(zkbAssetQrCodeInfo.getIbQrCodeTemporary() != null && zkbAssetQrCodeInfo.getIbQrCodeTemporary() == true){
                zkbAssetQrCodeInfo.setIbQrCodeState("Y");
                zkbAssetQrCodeInfo.setQrCodeDate(date);
                zkbAssetQrCodeInfo.setQrCodeCountState("Y");
                zkbAssetQrCodeInfo.setCreatedDate(date);
            }else{
                Integer qrCodeCount = zkbAssetQrCodeInfoRepository.findAttachedCount(zkbAssetQrCodeInfo.getAssetUid());
                if(qrCodeCount != null && qrCodeCount == 3){
                    zkbAssetQrCodeInfo.setQrCodeState("Y");
                    zkbAssetQrCodeInfo.setQrCodeDate(date);
                    zkbAssetQrCodeInfo.setQrCodeCountState("Y");
                    zkbAssetQrCodeInfo.setCreatedDate(date);
                }else if(zkbAssetQrCodeInfo.getId() == null || qrCodeCountState == null){//ID == null 则新增记录
                    zkbAssetQrCodeInfo.setQrCodeCountState("N");
                    zkbAssetQrCodeInfo.setCreatedDate(date);
                }
            }
        }
        zkbAssetQrCodeInfoRepository.save(zkbAssetQrCodeInfo);
    }
}

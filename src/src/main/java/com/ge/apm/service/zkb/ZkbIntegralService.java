package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbIntegralRecordRepository;
import com.ge.apm.dao.ZkbIntegralRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
@Service
public class ZkbIntegralService {

    public static Integer initialScore = 600;
    public static Integer initCoefficient = 1;
    public static Integer unitScore = 200;
    public static Integer KPI_FIRST_LEVEL_SCORE = 80;
    public static Integer KPI_SECOND_LEVEL_SCORE = 70;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ZkbIntegralRepository zkbIntegralRepository;

    @Autowired
    ZkbIntegralRecordRepository zkbIntegralRecordRepository;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public List<TenantInfo> getSourceTenantInfoList(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    @Transactional(rollbackFor = Exception.class)
    public String kpiScoreHandler(String tenantUID, Integer kpi,String recordType, UserAccount userAccount,String kpiMonth){
        boolean flag = true;
        String errorLog = "";
        if (StringUtils.isEmpty(tenantUID)){
            flag = false;
            errorLog += "tenantUID为空 ";
        }
        if (kpi == null){
            flag = false;
            errorLog += "kpi为空";
        }
        if (StringUtils.isEmpty(recordType)){
            flag = false;
            errorLog += "recordType为空 ";
        }
        if(StringUtils.isNotEmpty(errorLog.trim())){
            return errorLog;
        }
        ZkbIntegral zkbIntegral = zkbIntegralRepository.findFirstByTenantUID(tenantUID);
        Integer score = scoreCase(kpi);
        int cof = 1;
        if(StringUtils.isEmpty(kpiMonth)){
            kpiMonth = simpleDateFormat.format(new Date());
        }
        if(zkbIntegral != null){
            cof = calcCoefficient(zkbIntegral);
            score = cof * score;
            List<String> recordTypes = new ArrayList<>();
            recordTypes.add(ZkbIntegralRecord.RecordType.KpiAdd.toString());
            recordTypes.add(ZkbIntegralRecord.RecordType.KpiModify.toString());
            if(ZkbIntegralRecord.RecordType.KpiAdd.toString().equals(recordType)){
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score);
            }else if(ZkbIntegralRecord.RecordType.KpiModify.toString().equals(recordType)){
                ZkbIntegralRecord zkbIntegralRecord = zkbIntegralRecordRepository.findFirstByRecord(zkbIntegral.getId(),recordTypes,kpiMonth);
                Integer oldScore = 0;
                if(zkbIntegralRecord != null){
                    oldScore = zkbIntegralRecord.getScore();
                }
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score - oldScore);
            }else if (ZkbIntegralRecord.RecordType.KpiDelete.toString().equals(recordType)){
                ZkbIntegralRecord zkbIntegralRecord = zkbIntegralRecordRepository.findFirstByRecord(zkbIntegral.getId(),recordTypes,kpiMonth);
                Integer oldScore = 0;
                if(zkbIntegralRecord != null){
                    oldScore = zkbIntegralRecord.getScore();
                }
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() - oldScore);
            }
            zkbIntegral = zkbIntegralRepository.save(zkbIntegral);
        }else{
            zkbIntegral = new ZkbIntegral();
            zkbIntegral.setIntegralTotal(initialScore);
            zkbIntegral.setTenantUID(tenantUID);
            zkbIntegral.setCoefficient(initCoefficient);
            score = initCoefficient * score;
            if (ZkbIntegralRecord.RecordType.KpiDelete.toString().equals(recordType)){
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() - score);
            }else{
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score);
            }
            zkbIntegral = zkbIntegralRepository.save(zkbIntegral);
        }

        ZkbIntegralRecord zkbIntegralRecord = new ZkbIntegralRecord();
        zkbIntegralRecord.setRecordType(recordType);
        zkbIntegralRecord.setScore(score);
        zkbIntegralRecord.setCurrentKpi(kpi);
        zkbIntegralRecord.setCurrentCoefficient(cof);
        zkbIntegralRecord.setKpiMonth(kpiMonth);
        zkbIntegralRecord.setZkbIntegralId(zkbIntegral.getId());
        zkbIntegralRecord.setCreatedBy(userAccount.getName());
        zkbIntegralRecord.setCreatedDate(new Date());
        zkbIntegralRecordRepository.save(zkbIntegralRecord);
        return "";
    }

    @Transactional(rollbackFor = Exception.class)
    public void deductScore(String tenantUID,Integer qty,UserAccount userAccount,String recordType){
        ZkbIntegral zkbIntegral = zkbIntegralRepository.findFirstByTenantUID(tenantUID);
        Integer score = qty * unitScore;
        if(ZkbIntegralRecord.RecordType.QuotaDeduct.toString().equals(recordType)){
            zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() - score);
        }else if(ZkbIntegralRecord.RecordType.QuotaReturn.toString().equals(recordType)){
            zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score);
        }
        zkbIntegralRepository.save(zkbIntegral);

        ZkbIntegralRecord zkbIntegralRecord = new ZkbIntegralRecord();
        zkbIntegralRecord.setRecordType(recordType);
        if(ZkbIntegralRecord.RecordType.QuotaDeduct.toString().equals(recordType)){
            zkbIntegralRecord.setScore(0 - score);
        }else if(ZkbIntegralRecord.RecordType.QuotaReturn.toString().equals(recordType)){
            zkbIntegralRecord.setScore(score);
        }
        zkbIntegralRecord.setZkbIntegralId(zkbIntegral.getId());
        zkbIntegralRecord.setCreatedBy(userAccount.getName());
        zkbIntegralRecord.setCreatedDate(new Date());
        zkbIntegralRecordRepository.save(zkbIntegralRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(ZkbIntegral zkbIntegral,UserAccount userAccount){
        Integer score = 0;
        if(StringUtils.isEmpty(zkbIntegral.getId())){
            score = zkbIntegral.getIntegralTotal();
            zkbIntegral = zkbIntegralRepository.save(zkbIntegral);
        }else{
            ZkbIntegral old = zkbIntegralRepository.findById(zkbIntegral.getId());
            score = zkbIntegral.getIntegralTotal() - old.getIntegralTotal();
            zkbIntegral = zkbIntegralRepository.save(zkbIntegral);
        }
        ZkbIntegralRecord zkbIntegralRecord = new ZkbIntegralRecord();
        zkbIntegralRecord.setRecordType(ZkbIntegralRecord.RecordType.ScoreEdit.toString());
        zkbIntegralRecord.setScore(score);
        zkbIntegralRecord.setZkbIntegralId(zkbIntegral.getId());
        zkbIntegralRecord.setCreatedBy(userAccount.getName());
        zkbIntegralRecord.setCreatedDate(new Date());
        zkbIntegralRecordRepository.save(zkbIntegralRecord);
    }

    public Integer calcCoefficient(ZkbIntegral zkbIntegral){
        if(zkbIntegral.getValidityDate() == null){
            return initCoefficient;
        }
        if(zkbIntegral.getCoefficient().equals(initCoefficient)){
            return initCoefficient;
        }else if(zkbIntegral.getCoefficient() > initCoefficient){
            int subDay = ZkbPartOrderService.getBetweenDay(zkbIntegral.getValidityDate(),new Date());
            if(subDay > 0){
                return initCoefficient;
            }else{
                return zkbIntegral.getCoefficient();
            }
        }else {
            return initCoefficient;
        }
    }

    public Integer scoreCase(Integer kpi){
        if(kpi >= KPI_FIRST_LEVEL_SCORE){
            return 50;
        }else if(kpi>= KPI_SECOND_LEVEL_SCORE){
            return 10;
        }else{
            return 0;
        }
    }

    public boolean checkScoreFlag(String tenantUID,Integer qty){
        ZkbIntegral zkbIntegral = zkbIntegralRepository.findFirstByTenantUID(tenantUID);
        if(zkbIntegral == null) {
            return false;
        }
        if(zkbIntegral.getIntegralTotal() == null){
            return false;
        }
        if(zkbIntegral.getIntegralTotal() < qty * unitScore){
            return false;
        }
        return true;
    }

    public Integer queryScoreFlag(String tenantUID){
        ZkbIntegral zkbIntegral = zkbIntegralRepository.findFirstByTenantUID(tenantUID);
        if(zkbIntegral == null) {
            return 0;
        }
        if(zkbIntegral.getIntegralTotal() == null){
            return 0;
        }
        return zkbIntegral.getIntegralTotal();
    }

    public List<ZkbIntegralRecord> queryRecordByIntegralId(String integralId){
        List<ZkbIntegralRecord> zkbIntegralRecords = zkbIntegralRecordRepository.findByZkbIntegralIdOrderByCreatedDateDesc(integralId);
        return zkbIntegralRecords;
    }

    public HashMap<String,Object> createZkbPartOrderData(List<ZkbIntegral> zkbIntegralList){
        List<Map<String, Object>> zkbIntegralDataList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<Map<String, Object>> zkbIntegralDataRecordList = new ArrayList<>();
        HashMap<String, String> tenMap = new HashMap<>();
        if(zkbIntegralList.size() > 0){
            for(ZkbIntegral p : zkbIntegralList){
                ids.add(p.getId());
                Map<String, Object> zkbIntegralDataMap = new HashMap<>(4);
                TenantInfo tenantInfo = tenantInfoDao.findByuid(p.getTenantUID());
                if(tenantInfo != null){
                    zkbIntegralDataMap.put("代理商", tenantInfo.getName());
                }
                zkbIntegralDataMap.put("总积分", p.getIntegralTotal());
                zkbIntegralDataMap.put("系数", p.getCoefficient());
                if(p.getValidityDate() != null){
                    zkbIntegralDataMap.put("有效期", DateFormatUtils.format(p.getValidityDate(),"yyyy-MM-dd"));
                }
                tenMap.put(p.getId(),tenantInfo.getName());
                zkbIntegralDataList.add(zkbIntegralDataMap);
            }
        }

        if(zkbIntegralList.size() > 0 && ids.size() > 0){
            zkbIntegralDataRecordList = createZkbIntegralRecordData(ids,tenMap);
        }
        HashMap<String,Object> map = new HashMap<>(2);
        map.put("zkbIntegralDataList",zkbIntegralDataList);
        map.put("zkbIntegralDataRecordList",zkbIntegralDataRecordList);
        return map;
    }



    public List<Map<String, Object>> createZkbIntegralRecordData(List<String> ids,HashMap<String, String> tenMap){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("zkbIntegralId", SearchFilter.Operator.IN, ids));
        searchFilters.add(new SearchFilter("recordType", SearchFilter.Operator.NE, ZkbIntegralRecord.RecordType.KpiAdd.toString()));
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");

        List<ZkbIntegralRecord> zkbIntegralRecords = zkbIntegralRecordRepository.findBySearchFilter(searchFilters,sort);
        List<Map<String,Object>> zkbPartOrderItemDataList = new ArrayList<>();
        for(ZkbIntegralRecord i : zkbIntegralRecords){
            Map<String, Object> zkbIntegralRecordDataMap = new HashMap<>(10);
            zkbIntegralRecordDataMap.put("代理商", tenMap.get(i.getZkbIntegralId()));
            zkbIntegralRecordDataMap.put("类型", getRecordTypeName(i.getRecordType()));
            zkbIntegralRecordDataMap.put("积分", i.getScore());
            zkbIntegralRecordDataMap.put("KPI", i.getCurrentKpi());
            zkbIntegralRecordDataMap.put("系数", i.getCurrentCoefficient());
            zkbIntegralRecordDataMap.put("KPI月份", i.getKpiMonth());
            zkbIntegralRecordDataMap.put("操作时间", DateFormatUtils.format(i.getCreatedDate(),"yyyy-MM-dd HH:mm:ss"));
            zkbIntegralRecordDataMap.put("操作人", i.getCreatedBy());
            zkbPartOrderItemDataList.add(zkbIntegralRecordDataMap);
        }
        return zkbPartOrderItemDataList;
    }

    public String getRecordTypeName(String ty){
        switch (ty){
            case "KpiAdd" :
                return "KPI增加";
            case "KpiModify" :
                return "KPI修改";
            case "KpiDelete" :
                return "KPI删除";
            case "QuotaDeduct" :
                return "名额扣减";
            case "QuotaReturn" :
                return "名额返还";
            case "ScoreEdit" :
                return "编辑修改";
            default:
                return "";
        }
    }

    public static final String SHEET_ZKB_PART_ORDER = "积分信息";
    public static final String SHEET_ZKB_PART_ORDER_ITEM = "积分明细记录";
    public void exportData(ExcelDocument doc, HashMap<String, Object> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;

        List<Map<String, Object>> zkbIntegralDataList = (List<Map<String, Object>>) dataMap.get("zkbIntegralDataList");
        List<Map<String, Object>> zkbIntegralDataRecordList = (List<Map<String, Object>>) dataMap.get("zkbIntegralDataRecordList");
        for (Map<String, Object> item : zkbIntegralDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER, keyRow, dataRow++, item);
        }
        dataRow = 2;
        for (Map<String, Object> item : zkbIntegralDataRecordList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER_ITEM, keyRow, dataRow++, item);
        }

    }

}

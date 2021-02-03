package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbIntegralRecordRepository;
import com.ge.apm.dao.ZkbIntegralRepository;
import com.ge.apm.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    public String kpiScoreHandler(String tenantUID, Integer kpi,String recordType, UserAccount userAccount){
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
        String kpiMonth = simpleDateFormat.format(new Date());
        if(zkbIntegral != null){
            score = calcCoefficient(zkbIntegral) * score;
            if(ZkbIntegralRecord.RecordType.KpiAdd.toString().equals(recordType)){
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score);
            }else if(ZkbIntegralRecord.RecordType.KpiModify.toString().equals(recordType)){
                ZkbIntegralRecord zkbIntegralRecord = zkbIntegralRecordRepository.findFirstByZkbIntegralIdAndRecordTypeAndKpiMonthOrderByCreatedDateDesc(zkbIntegral.getId(),ZkbIntegralRecord.RecordType.KpiAdd.toString(),kpiMonth);
                Integer oldScore = 0;
                if(zkbIntegralRecord != null){
                    oldScore = zkbIntegralRecord.getScore();
                }
                zkbIntegral.setIntegralTotal(zkbIntegral.getIntegralTotal() + score - oldScore);
            }else if (ZkbIntegralRecord.RecordType.KpiDelete.toString().equals(recordType)){
                ZkbIntegralRecord zkbIntegralRecord = zkbIntegralRecordRepository.findFirstByZkbIntegralIdAndRecordTypeAndKpiMonthOrderByCreatedDateDesc(zkbIntegral.getId(),ZkbIntegralRecord.RecordType.KpiAdd.toString(),kpiMonth);
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
        zkbIntegralRecord.setCurrentCoefficient(zkbIntegral.getCoefficient());
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
        if(kpi > KPI_FIRST_LEVEL_SCORE){
            return 50;
        }else if(kpi> KPI_SECOND_LEVEL_SCORE){
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
}

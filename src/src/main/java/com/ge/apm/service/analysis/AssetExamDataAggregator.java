package com.ge.apm.service.analysis;

import com.ge.apm.domain.AssetClinicalRecordPojo;
import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.dao.ExamSummitRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetSummit;
import com.ge.apm.domain.ExamSummit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.broker.SiBroker;
import webapp.framework.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author 212547631
 */
@Component
public class AssetExamDataAggregator {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final int DAY = 1;
    @Autowired
    AssetClinicalRecordRepository assetClinicalRecordRepository;
    @Autowired
    AssetSummitRepository assetSummitRepository;
    @Autowired
    ExamSummitRepository examSummitRepository;
    @Autowired
    AssetInfoRepository assetInfoRepository;

    /*该方法会由route id=aggregationAssetSummit来调用*/
    public String aggregateAssetSummit() throws Exception{
        Date currentDate = new Date();
        logger.info("start to aggregateExamData");
        aggregateForAssetSumit(currentDate);
        return "success";
    }

    public String aggregateAssetSummitForYesterday() throws Exception{
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        logger.info("start to aggregateExamData for Yesterday");
        //calculate yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
        Date yesterday = dft.parse(dft.format(calendar.getTime()));
        aggregateForAssetSumit(yesterday);
        return "success";
    }
    public String aggregateExamSummit() throws Exception{
        logger.info("start to aggregateForExamSumit");
        Date currentDate = new Date();
        logger.info("start to aggregateExamData");
        aggregateForExamSumit(currentDate);
        return "success";
    }

    public String aggregateExamSummitForYesterday() throws Exception{
        logger.info("start to aggregateForExamSumit for yesterday");
        ///
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        logger.info("start to aggregateExamData");
        aggregateForExamSumit(currentDate);
        //calculate yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
        Date yesterday = dft.parse(dft.format(calendar.getTime()));
        aggregateForExamSumit(yesterday);
        return "success";
    }


    @Transactional
    public String aggregateForExamSumit(Date date)throws Exception{
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository
                .aggregateForExamSummitByDate(date);
        List<ExamSummit> asmList= new ArrayList<ExamSummit>();
        List<ExamSummit> newAsmList= new ArrayList<ExamSummit>();
        List<String> udpateIdList= new ArrayList<String>();
        List<String> newIdList= new ArrayList<String>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            ExamSummit asm1 =examSummitRepository.getExamSummitByAssetIdAndCreated(accrp.getAssetIds(),accrp.getHospitalIds(),accrp.getProcedureId(),accrp.getSiteIds(), date);
            if(asm1!=null){
             //   if(accrp.getExamCount()!=null && asm1.getExamCount()!=null)
             //       if(asm1.getExamCount().intValue()!=accrp.getExamCount().intValue()) {//如果exam count不相等才更新
                        extractorExamSummit(date, asmList, accrp, asm1);
               //     }

            }else {

                ExamSummit newAsm = new ExamSummit();
                extractorExamSummit(date, newAsmList, accrp, newAsm);
            }
        }
        if(asmList.size()>0) {
           // logger.info("how many records have already been in exam_summit --> "+asmList.size());
            //asmList.forEach(exam->udpateIdList.add(exam.getAssetId().toString()+":"+exam.getPartId()+":"+exam.getCreated()));
            logger.info(" updated assetIds with [assetId:partId:date] are --> "+udpateIdList.toString());
            examSummitRepository.save(asmList);
        }
        if(newAsmList.size()>0) {
          //  logger.info("how many new records to be inserted into exam_summit--> "+newAsmList.size());
           // newAsmList.forEach(exam->newIdList.add(exam.getAssetId().toString()+":"+exam.getPartId()+":"+exam.getCreated()));
            logger.info(" new assetIds with [assetId:partId:date] are --> "+newIdList.toString());
            examSummitRepository.save(newAsmList);
        }
            return "success";
    }

    @Transactional
    public String aggregateForAssetSumit(Date createdDate) throws Exception {
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(createdDate);
        logger.info("Asset Clinical Record Aggregator records size-->"+acrpList.size());
         List<AssetSummit> asmList= new ArrayList<AssetSummit>();
        List<AssetSummit> newAsmList= new ArrayList<AssetSummit>();
        List<String> udpateIdList= new ArrayList<String>();
        List<String> newIdList= new ArrayList<String>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            AssetSummit asm1 =assetSummitRepository.getAssetSummitByAssetIdAndCreated(accrp.getAssetIds(),createdDate);
            //如果某资产已经在assumit中录入过了 ，那就只要更新该资产相对应的inject_count等信息
            if(asm1!=null){
                extractorAssetSummit(createdDate, asmList, accrp, asm1);
            }else {
                AssetSummit newAsm = new AssetSummit();
                extractorAssetSummit(createdDate, newAsmList, accrp, newAsm);
            }
        }
        if(asmList.size()>0) {
            asmList.forEach(exam->udpateIdList.add(exam.getAssetId()+":"+exam.getCreated()));
            logger.info("updated assetIds with [assetId:date] are --> "+asmList.size());
             assetSummitRepository.save(asmList);
        }
        if(newAsmList.size()>0) {
            asmList.forEach(exam->newIdList.add(exam.getAssetId()+":"+exam.getCreated()));
            logger.info("new assetIds with [assetId:date] are --> "+newAsmList.size());
            assetSummitRepository.save(newAsmList);
        }
        return "success";

    }
    //在将asset clininical中聚合的数据写到assetsummit中去，将数据放到列表里面
    private void extractorAssetSummit(Date date, List<AssetSummit> asmList, AssetClinicalRecordPojo accrp, AssetSummit asm1) {
        AssetInfo assinfo = assetInfoRepository.findById(accrp.getAssetIds());
        if(assinfo!=null){
            asm1.setDeptId(assinfo.getClinicalDeptId());
            asm1.setAssetGroup(assinfo.getAssetGroup());
        }else{
            logger.info("gl_Warning: asset_id  "+accrp.getAssetIds()+" from asset_clinical_record does not exist in asset_info");
        return;
        }
        asm1.setCreated(date);//gl: created这个字段是做聚合那天的日期?
        asm1.setAssetId(accrp.getAssetIds());
        asm1.setSiteId(accrp.getSiteIds());
        asm1.setHospitalId(accrp.getHospitalIds());
        asm1.setExposeCount(accrp.getExposeCounts());
        asm1.setFilmCount(accrp.getFilmCounts());
        asm1.setInjectCount(accrp.getInjectCounts());

        if(accrp.getExamCount()==null){
            logger.info("gl: accrp.getExamCount() should not be null");
        }else {
            asm1.setExamCount(accrp.getExamCount().intValue());
        }
        if(accrp.getExamDurations()==null){
            logger.info("gl: accrp.getExamDurations() should not be null");
        }else {
            asm1.setExamDuration(accrp.getExamDurations().intValue());
        }
        if(accrp.getPriceAmounts()==null){
            logger.info("gl: accrp.getPriceAmounts() should not be null");
        }else {
            asm1.setRevenue(Math.round(accrp.getPriceAmounts() * 100.0) / 100.0);
        }
        asmList.add(asm1);
    }
    private void extractorExamSummit(Date date, List<ExamSummit> asmList, AssetClinicalRecordPojo accrp, ExamSummit asm1) {
     AssetInfo assinfo = assetInfoRepository.findById(accrp.getAssetIds());
        if(assinfo!=null){
            asm1.setDeptId(assinfo.getClinicalDeptId());
            asm1.setAssetGroup(assinfo.getAssetGroup());
        }else{
            logger.warn("gl_Warning: asset_id "+accrp.getAssetIds()+" does not exist in asset_info");
        return;
        }
        asm1.setCreated(accrp.getExamDate());
        asm1.setAssetId(accrp.getAssetIds());
        asm1.setSiteId(accrp.getSiteIds());
        asm1.setHospitalId(accrp.getHospitalIds());
        asm1.setPartId(accrp.getProcedureId());
        asm1.setSubPartId(1);
        asm1.setStepId(1);


        if(accrp.getExamCount()==null){
            logger.info("gl: accrp.getExamCount() should not be null");
        }else {
            asm1.setExamCount(accrp.getExamCount().intValue());
        }

        asmList.add(asm1);
    }
    public void initAssetAggregationDataByDateRange(Date fromDate, Date toDate){
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime dtFrom = TimeUtil.toJodaDate(fromDate);
        DateTime dtTo = TimeUtil.toJodaDate(toDate).plusDays(1);
       /* Date from = sdf.parse("2014-11-10");
        Date to = sdf.parse("2015-09-18");*/
        while(dtFrom.isBefore(dtTo)){
            params.put("theDate", TimeUtil.toString(dtFrom.toDate(), 0, "yyyy-MM-dd"));
            //gl:route_main.xml
            SiBroker.sendMessageWithHeaders("direct:initAssetAggregationDataByDateRange", null, params);
            dtFrom = dtFrom.plusDays(1);
        }

    }
}
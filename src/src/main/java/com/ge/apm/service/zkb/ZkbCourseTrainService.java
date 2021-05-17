package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2021/4/9
 * @description :
 */
@Service
public class ZkbCourseTrainService extends MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(ZkbCourseTrainService.class);


    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    ZkbCourseTrainRepository zkbCourseTrainRepository;

    @Autowired
    ZkbEngineerRepository zkbEngineerRepository;

    @Autowired
    ZkbContractCourseRepository zkbContractCourseRepository;
    @Autowired
    ZkbCourseTrainDetailRepository zkbCourseTrainDetailRepository;
    @Autowired
    private TenantInfoRepository tenantInfoDao;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    public List<TenantInfo> getSourceTenantInfoList(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }


    @Transactional(rollbackFor = Exception.class)
    public void generateTrain(ZkbContractCourseSchedule s, String joinFlag, UserAccount userAccount,String reason,Double c){

    }

    public Double mulByDouble(Double d1,Double d2){
        BigDecimal a1 = BigDecimal.valueOf(d1);
        BigDecimal a2 = BigDecimal.valueOf(d2);
        double result = (a1.multiply(a2)).doubleValue();
        return result;
    }

    public Double subByDouble(Double d1,Double d2){
        BigDecimal a1 = BigDecimal.valueOf(d1);
        BigDecimal a2 = BigDecimal.valueOf(d2);
        double result = (a1.subtract(a2)).doubleValue();
        return result;
    }

    public Double addByDouble(Double d1,Double d2){
        BigDecimal a1 = BigDecimal.valueOf(d1);
        BigDecimal a2 = BigDecimal.valueOf(d2);
        double result = (a1.add(a2)).doubleValue();
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(ZkbContractCourseSchedule s, ZkbCourseTrain t,UserAccount userAccount){
        Integer index = zkbCourseTrainRepository.getMaxIndex("ST"+simpleDateFormat.format(new Date())+"%") ;
        index = index == null ? 1 : index;
        String partOrderNo = "ST"+simpleDateFormat.format(new Date())+String.format("%04d",index);
        index++;
        t.setTrainNo(partOrderNo);
        t.setTrainNoIndex(index);
        t.setCourseScheduleId(s.getId());
        t.setTenantUID(userAccount.getTenantUID());
        t.setContractCourseId(s.getZkbContractCourseId());
        t.setCourseId(s.getCid());
        t.setCourseName(s.getCname());
        t.setTrainingDays(s.getTrainingDays());
        t.setTrainStartDate(s.getTrainStartDate());
        t.setTrainEndDate(s.getTrainEndDate());
        t.setTrainType(s.getType());
        t.setSignDate(new Date());
        t.setBatchNo(s.getBatchNo());
        t.setCreatedBy(userAccount.getName());
        t.setCreatedUid(userAccount.getUid());
        t.setCreatedDate(new Date());
        t.setInstructor(s.getInstructor());
        t.setLocation(s.getLocation());
        t.setDeductCount(s.getDeductCount());
        /*t.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.Y.toString());*/

        if(ZkbCourseTrain.ParticipateStatusEnum.Y.toString().equals(t.getParticipateStatus())){
            t.setPreQuota(mulByDouble(t.getDeductCount(),t.getPrePersonCount()));
        }
        zkbCourseTrainRepository.save(t);

        subQuotaSign(t,"add");
    }

    //region 流程事件
    @Transactional(rollbackFor = Exception.class)
    public void submitNext(ZkbCourseTrain c,List<ZkbCourseTrainDetail> zkbCourseTrainDetailList,List<V2_BlobObject> v2_blobObjects,UserAccount userAccount){
        String oldStep = c.getCurrentStep();
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString());
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.LetterSubmit.toString());
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.ApplyAuditPending.toString());
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplySubmit.toString());
            c.setActualPersonCount((double)zkbCourseTrainDetailList.size());
            c.setActualQuota(mulByDouble(c.getDeductCount(),c.getActualPersonCount()));
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelAccepted.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            c.setAuditDate(new Date());
            c.setAuditer(userAccount.getName());
            c.setAuditerUid(userAccount.getUid());
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.AuditAccepted.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            c.setAuditDate(new Date());
            c.setAuditer(userAccount.getName());
            c.setAuditerUid(userAccount.getUid());
        }
        zkbCourseTrainRepository.save(c);

        //名额变化
        if(ZkbCourseTrain.FlowStepEnum.ApplyAuditPending.toString().equals(oldStep)){
            //扣除申请中名额，增加已使用名额
            subQuotaUse(c);
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString().equals(oldStep)){
            //返还预扣名额
            subQuotaSign(c,"return");
        }if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(oldStep)){
            zkbCourseTrainDetailRepository.deleteByTrainId(c.getId());
            for(ZkbCourseTrainDetail d : zkbCourseTrainDetailList){
                d.setTrainId(c.getId());
            }
            zkbCourseTrainDetailRepository.save(zkbCourseTrainDetailList);
        }if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(oldStep)){
            for(V2_BlobObject v :v2_blobObjects){
                v.setBoUuid(c.getId());
            }
            blobObjectRepository.save(v2_blobObjects);
        }
        if(ZkbCourseTrain.FlowStatusEnum.ApplySubmit.toString().equals(c.getCurrentStatus())){
            sendEmail(c,UserContextService.getAccessToken());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void noJoinNext(ZkbCourseTrain t){
        String oldStep = t.getCurrentStep();
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(t.getCurrentStep())){
            t.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.N.toString());
            t.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            t.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.LetterCancelJoin.toString());
            subQuotaSign(t,"return");
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(t.getCurrentStep())){
            t.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.N.toString());
            t.setCurrentStep(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString());
            t.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelJoin.toString());
        }
        zkbCourseTrainRepository.save(t);
    }


    @Transactional(rollbackFor = Exception.class)
    public void rejectedNext(ZkbCourseTrain c,UserAccount userAccount){
        String oldStep = c.getCurrentStep();
        if(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setActualQuota(c.getPreQuota());
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelRejected.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            c.setAuditDate(new Date());
            c.setAuditer(userAccount.getName());
            c.setAuditerUid(userAccount.getUid());
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyAuditPending.toString().equals(oldStep)){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.AuditRejected.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString());
            c.setAuditDate(new Date());
            c.setAuditer(userAccount.getName());
            c.setAuditerUid(userAccount.getUid());
        }
        zkbCourseTrainRepository.save(c);
        if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelRejected.toString().equals(c.getCurrentStatus())){
            //扣除名额
            subQuotaUse(c);
        }
        sendWeChatMessages(c,UserContextService.getAccessToken());
    }

    @Transactional(rollbackFor = Exception.class)
    public void subQuotaUse(ZkbCourseTrain ct){
        ZkbContractCourse zkbContractCourse = zkbContractCourseRepository.findById(ct.getContractCourseId());
        Double preQuota = ct.getPreQuota() ==null? 0 : ct.getPreQuota();
        Double apply = subByDouble(zkbContractCourse.getMainCourseCountApply() == null ? 0 : zkbContractCourse.getMainCourseCountApply(),preQuota);
        zkbContractCourse.setMainCourseCountApply(apply);
        Double count = addByDouble(zkbContractCourse.getMainCourseCount() == null ? 0 : zkbContractCourse.getMainCourseCount(),preQuota);
        zkbContractCourse.setMainCourseCount(count);
        Double actQuota = ct.getActualQuota() == null ? 0 : ct.getActualQuota();
        Double use = addByDouble(zkbContractCourse.getMainCourseCountUse() == null ? 0 : zkbContractCourse.getMainCourseCountUse(),actQuota);
        zkbContractCourse.setMainCourseCountUse(use);
        count = subByDouble(zkbContractCourse.getMainCourseCount() == null ? 0 : zkbContractCourse.getMainCourseCount(),actQuota);
        zkbContractCourse.setMainCourseCount(count);
        zkbContractCourseRepository.save(zkbContractCourse);
    }

    @Transactional(rollbackFor = Exception.class)
    public void subQuotaSign(ZkbCourseTrain ct,String type){
        ZkbContractCourse zkbContractCourse = zkbContractCourseRepository.findById(ct.getContractCourseId());
        if(zkbContractCourse == null){
            return;
        }
        Double preQuota = ct.getPreQuota() ==null? 0 : ct.getPreQuota();
        if("add".equals(type)){
            Double apply = addByDouble(zkbContractCourse.getMainCourseCountApply() == null ? 0 : zkbContractCourse.getMainCourseCountApply(),preQuota);
            zkbContractCourse.setMainCourseCountApply(apply);
            Double count = subByDouble(zkbContractCourse.getMainCourseCount() == null ? 0 : zkbContractCourse.getMainCourseCount(),preQuota);
            zkbContractCourse.setMainCourseCount(count);
        }else{
            Double apply = subByDouble(zkbContractCourse.getMainCourseCountApply() == null ? 0 : zkbContractCourse.getMainCourseCountApply(),preQuota);
            zkbContractCourse.setMainCourseCountApply(apply);
            Double count = addByDouble(zkbContractCourse.getMainCourseCount() == null ? 0 : zkbContractCourse.getMainCourseCount(),preQuota);
            zkbContractCourse.setMainCourseCount(count);
        }
        zkbContractCourseRepository.save(zkbContractCourse);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelNext(ZkbCourseTrain c,String reason){
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.LetterCancelJoin.toString());
            c.setReason(reason);
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelJoin.toString());
            c.setReason(reason);
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelRejected.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
        }
        zkbCourseTrainRepository.save(c);
    }
    //endregion

    @Transactional(rollbackFor = Exception.class)
    public void delete(ZkbCourseTrain t){
        zkbCourseTrainRepository.delete(t);
        subQuotaSign(t,"return");
    }

    public List<ZkbCourseTrainDetail> queryInitDetails(ZkbCourseTrain zkbCourseTrain){
        List<ZkbEngineer> zkbEngineerList = zkbEngineerRepository.findByTenantUID(zkbCourseTrain.getTenantUID());
        if(zkbEngineerList == null || zkbEngineerList.size() == 0){
            return new ArrayList<>();
        }
        List<ZkbCourseTrainDetail> zkbCourseTrainDetails = new ArrayList<>();
        for(ZkbEngineer e : zkbEngineerList){
            ZkbCourseTrainDetail zkbCourseTrainDetail = null;
            if(ZkbCourseTrain.FlowStatusEnum.AuditRejected.toString().equals(zkbCourseTrain.getCurrentStatus())){
                zkbCourseTrainDetail = zkbCourseTrainDetailRepository.findFirstByTrainIdAndEid(zkbCourseTrain.getId(),e.getId());
            }
            if(zkbCourseTrainDetail == null){
                zkbCourseTrainDetail = new ZkbCourseTrainDetail();
                zkbCourseTrainDetail.setEid(e.getId());
                zkbCourseTrainDetail.setEname(e.getName());
                zkbCourseTrainDetail.setEphone(e.getPhone());
                zkbCourseTrainDetail.setEemail(e.getEmail());
                zkbCourseTrainDetail.setTrainId(zkbCourseTrain.getId());
            }

            List<V2_BlobObject> v2_blobObjects = blobObjectRepository.findByBoUuid(e.getId());
            zkbCourseTrainDetail.setV2_blobObjectList(v2_blobObjects);
            zkbCourseTrainDetails.add(zkbCourseTrainDetail);
        }
        return zkbCourseTrainDetails;
    }

    public List<ZkbCourseTrainDetail> querySaveDetail(ZkbCourseTrain zkbCourseTrain){
        List<ZkbCourseTrainDetail> zkbCourseTrainDetails = zkbCourseTrainDetailRepository.findByTrainId(zkbCourseTrain.getId());
        for(ZkbCourseTrainDetail c : zkbCourseTrainDetails){
            List<V2_BlobObject> v2_blobObjects = blobObjectRepository.findByBoUuid(c.getEid());
            c.setV2_blobObjectList(v2_blobObjects);
        }
        return zkbCourseTrainDetails;
    }

    public List<V2_BlobObject> queryBlobObjectByPid(String id){
        List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();
        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, id));
        sfs.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_COURSE));
        sfs.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_COURSE_TRAIN));
        v2_blobObjectList = blobObjectRepository.findBySearchFilter(sfs);
        return v2_blobObjectList;
    }


    public boolean isZkbAdmin(String tid){
        TenantInfo currentTenantInfo = tenantInfoDao.findByuid(tid);

        if(currentTenantInfo.getType() == null || currentTenantInfo.getTestStatus() == null){
            return Boolean.FALSE;
        }

        if(currentTenantInfo.getType().equals(3) && currentTenantInfo.getTestStatus().equals(-1)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Async
    public void sendWeChatMessages(ZkbCourseTrain t,String token){
        try {
            String url = getGatewayURL(GatewayURL.COURSE_TRAIN_SEND_WE_CHART).concat("?id=").concat(t.getId());
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(t.getTrainNo()+" sendWeChatMessages "+response.toString());
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendEmail(ZkbCourseTrain t,String token){
        try {
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("systemId", t.getId());
            String url = getGatewayURL(GatewayURL.COURSE_TRAIN_SEND_EMAIL_AUDIT);
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(t.getTrainNo()+" 发送邮件 "+response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

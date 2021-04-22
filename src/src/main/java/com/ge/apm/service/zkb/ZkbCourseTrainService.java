package com.ge.apm.service.zkb;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.ZkbContractCourseRepository;
import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/4/9
 * @description :
 */
@Service
public class ZkbCourseTrainService {

    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    ZkbCourseTrainRepository zkbCourseTrainRepository;

    @Autowired
    ZkbContractCourseRepository zkbContractCourseRepository;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    @Transactional(rollbackFor = Exception.class)
    public void generateTrain(ZkbContractCourseSchedule s, String joinFlag, UserAccount userAccount,String reason,Double c){
        ZkbCourseTrain t = new ZkbCourseTrain();
        Integer index = zkbCourseTrainRepository.getMaxIndex("ST"+simpleDateFormat.format(new Date())+"%") ;
        index = index == null ? 1 : index;
        String partOrderNo = "ST"+simpleDateFormat.format(new Date())+String.format("%04d",index);
        t.setTrainNo(partOrderNo);
        t.setCourseScheduleId(s.getId());
        t.setTenantUID(userAccount.getTenantUID());
        t.setContractCourseId(s.getZkbCourseId());
        t.setCourseId(s.getCid());
        t.setCourseName(s.getCname());
        t.setTrainingDays(s.getTrainingDays());
        t.setTrainType(s.getType());
        t.setSignDate(new Date());
        t.setBatchNo(s.getBatchNo());
        t.setCreatedBy(userAccount.getName());
        t.setCreatedDate(new Date());
        t.setInstructor(s.getInstructor());
        t.setLocation(s.getLocation());
        if(ZkbCourseTrain.ParticipateStatusEnum.Y.toString().equals(joinFlag)){
            t.setPreDeductCount(c);
            t.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.Y.toString());
            t.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ScheduleJoin.toString());
            t.setCurrentStep(ZkbCourseTrain.FlowStepEnum.LetterPending.toString());
        }else if(ZkbCourseTrain.ParticipateStatusEnum.N.toString().equals(joinFlag)){
            t.setReason(reason);
            t.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.N.toString());
            t.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ScheduleNoJoin.toString());
        }
        zkbCourseTrainRepository.save(t);
        dealPreQuota(t.getContractCourseId(),t.getPreDeductCount(),t.getTrainType());
    }

    @Transactional(rollbackFor = Exception.class)
    public void dealPreQuota(String zkbContractCourseId, Double c,String trainType){
        Double q = new Double(0);
        if(ZkbCourseTrain.TrainTypeEnum.Online.toString().equals(trainType)){
            q = mulByDouble(c, 0.5);
        }else {
            q = c;
        }
        ZkbContractCourse zkbContractCourse = zkbContractCourseRepository.findById(zkbContractCourseId);
        //zkbContractCourse.setCourseCount((zkbContractCourse.getCourseCount() == null ? 0 : zkbContractCourse.getCourseCount()) - q);
        //zkbContractCourse.setCourseCountApply((zkbContractCourse.getCourseCountApply() == null ? 0 : zkbContractCourse.getCourseCountApply()) + q);
        //zkbContractCourseRepository.save(zkbContractCourse);
    }

    public Double mulByDouble(Double d1,Double d2){
        BigDecimal a1 = BigDecimal.valueOf(d1);
        BigDecimal a2 = BigDecimal.valueOf(d2);
        double result = (a1.multiply(a2)).doubleValue();
        return result;
    }

    //region 流程事件
    @Transactional(rollbackFor = Exception.class)
    public void submitNext(ZkbCourseTrain c){
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString());
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.LetterSubmit.toString());
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplySubmit.toString());
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelAccepted.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
        }else if(ZkbCourseTrain.FlowStepEnum.LetterCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.ApplyCancelAccepted.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
            //返还名额
        }
        //名额变化
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(c.getCurrentStep())){
            //扣除申请中名额，增加已使用名额

        }else if(ZkbCourseTrain.FlowStepEnum.LetterCancelAuditPending.toString().equals(c.getCurrentStep())){
            //返还已使用名额
        }
        zkbCourseTrainRepository.save(c);
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
        }else if(ZkbCourseTrain.FlowStepEnum.LetterCancelAuditPending.toString().equals(c.getCurrentStep())){
            c.setCurrentStatus(ZkbCourseTrain.FlowStatusEnum.LetterCancelRejected.toString());
            c.setCurrentStep(ZkbCourseTrain.FlowStepEnum.Completed.toString());
        }
        zkbCourseTrainRepository.save(c);
    }
    //endregion


    public List<V2_BlobObject> queryBlobObjectByPid(String id){
        List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();
        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, id));
        sfs.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_COURSE));
        sfs.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_COURSE_TRAIN));
        v2_blobObjectList = blobObjectRepository.findBySearchFilter(sfs);
        return v2_blobObjectList;
    }
}

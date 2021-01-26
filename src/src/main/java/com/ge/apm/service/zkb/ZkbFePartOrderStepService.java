package com.ge.apm.service.zkb;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.ZkbFePartOrderStepRepository;
import com.ge.apm.domain.ZkbFePartOrderStep;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author 503202755 2020/12/8
 */
@Component
public class ZkbFePartOrderStepService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderStepService.class);

    /**
     * 人工申请主表repository
     */
    @Autowired
    private ZkbFePartOrderStepRepository zkbFePartOrderStepRepository;

    /**
     *i18 repository
     */
    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    private static final String I18_STEP_NAME = "zkbFePartOrder_steps";

    /**
     * @param partOrderId FE人工申请主表ID
     * @param stepKey step标识
     * @param ownerId 操作人UID
     * @param ownerName 操作人名称
     * @param startTime 开始操作时间
     * @param endTime 结束操作时间
     * @param remark 备注
     * @param tenantUID 租户ID
     */
    public void addNewItem(String partOrderId,
                          String stepKey,
                          String ownerId,
                          String ownerName,
                          Date startTime,
                          Date endTime,
                          String remark,
                          String tenantUID)
    {
        String stepName = this.i18nMessageRepository.getByTypeAndKey(I18_STEP_NAME,stepKey);
        ZkbFePartOrderStep zkbFePartOrderStep = new ZkbFePartOrderStep();
        zkbFePartOrderStep.setPartOrderId(partOrderId);
        zkbFePartOrderStep.setStepKey(stepKey);
        zkbFePartOrderStep.setStepName(stepName);
        zkbFePartOrderStep.setOwnerId(ownerId);
        zkbFePartOrderStep.setOwnerName(ownerName);
        zkbFePartOrderStep.setStartTime(startTime);
        zkbFePartOrderStep.setEndTime(endTime);
        zkbFePartOrderStep.setCreateDate(new Date());
        zkbFePartOrderStep.setRemark(remark);
        zkbFePartOrderStep.setTenantUID(tenantUID);
        this.zkbFePartOrderStepRepository.save(zkbFePartOrderStep);
    }

    /**
     * 更新结束时间
     * @param partOrderId 人工申请主表ID
     * @param endTime 当前时间
     */
    public void updateLatestStepEndTime(String partOrderId,Date endTime)
    {
        List<ZkbFePartOrderStep> list = this.zkbFePartOrderStepRepository.findByPartOrderIdOrderByCreateDateDesc(partOrderId);
        if(CollectionUtils.isEmpty(list)) return;
        ZkbFePartOrderStep latestStep = list.get(0);
        this.zkbFePartOrderStepRepository.updateEndDateById(endTime,latestStep.getId());
    }

    /**
     * 根据partOrder id 和step key 查找
     * @param partOrderId 流程主ID
     * @param stepKey 步骤名称
     * @return
     */
    public List<ZkbFePartOrderStep> findByPartOrderIdAndStepKey(String partOrderId,String stepKey)
    {
        return this.zkbFePartOrderStepRepository.findByPartOrderIdAndStepKey(partOrderId,stepKey);
    }






}

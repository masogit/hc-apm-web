package com.ge.apm.service.surgeryAnesthesia;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.HisAdjustBusinessJobRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentConnectRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentMatchRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentConnect;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentMatch;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 手麻系统设备匹配业务Service
 */
@Service
public class SurgeryAnesthesiaAssetService {

    @Autowired
    private SurgeryAnesthesiaEquipmentMatchRepository matchDao;

    @Autowired
    private SurgeryAnesthesiaEquipmentConnectRepository connectDao;

    @Autowired
    private HisAdjustBusinessJobRepository hisAdjustBusinessJobRepository;

    /**
     * 匹配APM台账与手麻系统设备
     * @param source APM资产设备
     * @param targets 手麻系统设备列表
     */
    @Transactional
    public void match(AssetInfo source, List<SurgeryAnesthesiaEquipmentConnect> targets) {
        List<String> ids = targets.stream().map(SurgeryAnesthesiaEquipmentConnect::getId).collect(Collectors.toList());

        List<SurgeryAnesthesiaEquipmentMatch> matchs = new ArrayList<>();
        for (SurgeryAnesthesiaEquipmentConnect connect : targets) {
            SurgeryAnesthesiaEquipmentMatch match = new SurgeryAnesthesiaEquipmentMatch();
            match.setDeviceCode(connect.getDeviceCode());
            match.setOrgCode(connect.getOrgCode());
            match.setConnectId(connect.getId());
            match.setDeviceName(connect.getDeviceName());
            match.setAssetUid(source.getUid());
            match.setAssetName(source.getName());
            match.setDepartNum(source.getDepartNum());
            match.setClinicalDeptUid(source.getClinicalDeptUID());
            match.setClinicalDeptName(source.getClinicalDeptName());
            match.setCreateDate(new Date());
            match.setCreateBy(UserContextService.getCurrentUserAccount().getLoginName());
            match.setTenantUid(connect.getTenantUid());
            match.setSiteUid(connect.getSiteUid());
            matchs.add(match);
        }
        matchDao.save(matchs);
        connectDao.updateMatchedByIds(ids);
    }

    /**
     * 取消匹配
     * @param hisAdjustBusinessJob 业务调整记录
     * @param equipmentMatch 匹配记录
     */
    @Transactional
    public void unmatch(HisAdjustBusinessJob hisAdjustBusinessJob, SurgeryAnesthesiaEquipmentMatch equipmentMatch) {
        hisAdjustBusinessJob.setOrgCode(equipmentMatch.getOrgCode());
        hisAdjustBusinessJob.setTaskType(4); // 4: 取消手麻系统设备匹配
        hisAdjustBusinessJob.setBusinessId(equipmentMatch.getId());
        hisAdjustBusinessJob.setOldStrJson(JSON.toJSONString(equipmentMatch));
        hisAdjustBusinessJob.setTaskStatus(0);
        hisAdjustBusinessJob.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
        hisAdjustBusinessJob.setCreatedDate(new Date());
        hisAdjustBusinessJob.setTenantUid(equipmentMatch.getTenantUid());
        hisAdjustBusinessJob.setSiteUid(equipmentMatch.getSiteUid());

        hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);

        connectDao.updateUnmatchedById(equipmentMatch.getConnectId());
        matchDao.delete(equipmentMatch.getId());
    }
}

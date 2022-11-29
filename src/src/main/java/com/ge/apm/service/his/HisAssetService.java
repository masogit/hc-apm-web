package com.ge.apm.service.his;

import com.ge.apm.dao.HisAdjustBusinessJobRepository;
import com.ge.apm.dao.HisEquipmentConnectRepository;
import com.ge.apm.dao.HisEquipmentMatchRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentMatch;
import com.ge.apm.view.sysutil.UserContextService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HisAssetService {

    @Autowired
    private HisEquipmentMatchRepository matchDao;
    @Autowired
    private HisEquipmentConnectRepository hisAssetDao;
    @Autowired
    private HisAdjustBusinessJobRepository hisAdjustBusinessJobRepository;

    @Transactional
    public void match(AssetInfo source, List<HisEquipmentConnect> targets) {

        List<String> ids = targets.stream().map(HisEquipmentConnect::getId).collect(Collectors.toList());

        List<HisEquipmentMatch> matchs = new ArrayList<>();
        for(HisEquipmentConnect connect: targets){
            HisEquipmentMatch match = new HisEquipmentMatch();
            match.setEqId(connect.getEqId());
            match.setEqLb(connect.getEqLb());
            match.setEqHosid(connect.getEqHosid());
            match.setConnectId(connect.getId());
            match.setEqName(connect.getEqName());
            match.setAssetUid(source.getUid());
            match.setAssetName(source.getName());
            match.setSbbm(source.getDepartNum());
            match.setKsbm(source.getClinicalDeptUID());
            match.setKsName(source.getClinicalDeptName());
            match.setCreateDate(new Date());
            matchs.add(match);
        }
        matchDao.save(matchs);
        hisAssetDao.updateHisEquipmentConnectByIds(ids);
    }

    @Transactional
    public void unmatch(HisAdjustBusinessJob hisAdjustBusinessJob, HisEquipmentMatch equipmentMatch) {
        hisAdjustBusinessJob.setOrgCode(equipmentMatch.getEqHosid());
        hisAdjustBusinessJob.setTaskType(2);//2 取消联机匹配
        hisAdjustBusinessJob.setBusinessId(equipmentMatch.getEqId());//LIS、RIS/PACS设备唯一识别编号
        hisAdjustBusinessJob.setOldStrJson(JSONObject.fromObject(equipmentMatch).toString());
        hisAdjustBusinessJob.setTaskStatus(0);
        hisAdjustBusinessJob.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
        hisAdjustBusinessJob.setCreatedDate(new Date());


        hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);

        hisAssetDao.updateHisEquipmentConnectById(equipmentMatch.getConnectId());
        matchDao.deleteById(equipmentMatch.getId());
    }
}

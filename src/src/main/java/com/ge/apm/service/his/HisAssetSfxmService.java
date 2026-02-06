package com.ge.apm.service.his;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.HisAdjustBusinessJobRepository;
import com.ge.apm.dao.HisAssetSfxmMatchRepository;

import com.ge.apm.dao.HisEquipmentMatchRepository;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.his.HisAssetSfxmMatch;

import com.ge.apm.domain.his.HisEquipmentMatch;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class HisAssetSfxmService {

    @Autowired
    private HisAssetSfxmMatchRepository matchDao;

    @Autowired
    private HisEquipmentMatchRepository hisEquipmentMatchRepository;

    @Autowired
    private HisAdjustBusinessJobRepository hisAdjustBusinessJobRepository;



    @Transactional
    public void unmatch(HisAdjustBusinessJob hisAdjustBusinessJob, HisAssetSfxmMatch hisAssetSfxmMatch) {
        hisAdjustBusinessJob.setOrgCode(hisAssetSfxmMatch.getOrgCode());
        hisAdjustBusinessJob.setTaskType(2);
        List<HisEquipmentMatch> byAssetUid = hisEquipmentMatchRepository.findByAssetUid(hisAssetSfxmMatch.getAssetUid());
        if(CollectionUtils.isEmpty(byAssetUid)){
            WebUtil.addSuccessMessage("apm设备未与his设备匹配");
            return;
        }
        Set<String> eqIds = byAssetUid.stream().map(HisEquipmentMatch::getEqId).collect(Collectors.toSet());


        String join = String.join(",", eqIds);
        hisAdjustBusinessJob.setBusinessId(join);
        hisAdjustBusinessJob.setOldStrJson(JSON.toJSONString(hisAssetSfxmMatch));
        hisAdjustBusinessJob.setTaskStatus(0);
        hisAdjustBusinessJob.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
        hisAdjustBusinessJob.setCreatedDate(new Date());
        hisAdjustBusinessJob.setTenantUid(hisAssetSfxmMatch.getTenantUid());
        hisAdjustBusinessJob.setSiteUid(hisAssetSfxmMatch.getSiteUid());
        hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);
        matchDao.deleteById(hisAssetSfxmMatch.getId());
    }
}

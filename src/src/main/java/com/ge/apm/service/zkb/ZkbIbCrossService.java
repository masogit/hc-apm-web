package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbIbCrossMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2021/6/9
 * @description :
 */
@Service
public class ZkbIbCrossService extends MicroServiceInvoker {


    @Autowired
    ZkbIbCrossUserStatusRepository zkbIbCrossUserStatusRepository;

    @Autowired
    ZkbIbCrossUserRepository zkbIbCrossUserRepository;

    @Autowired
    ZkbIbCrossRelationShipRepository zkbIbCrossRelationShipRepository;

    @Autowired
    ZkbContractRepository zkbContractRepository;

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    AssetInfoRepository assetInfoRepository;

    @Autowired
    ZkbIbCrossMapper zkbIbCrossMapper;

    @Autowired
    UserAccountRepository userAccountRepository;

    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional(rollbackFor = Exception.class)
    public void handleIbCrossJob(){
        String td = ymd.format(new Date());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = ymd.parse(td);
            d2 = addDays(d1,1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        zkbIbCrossUserRepository.deleteAllBatch();
        zkbIbCrossRelationShipRepository.deleteAllBatch();

        List<String> tenantUidList = zkbContractRepository.queryByLastModifiedDate(d1,d2);
        List<ZkbIbCrossUser> zkbIbCrossUserList = new ArrayList<>();
        HashMap<String, ZkbIbCrossUser> userHashMap = new HashMap<>();
        List<ZkbIbCrossRelationShip> zkbIbCrossRelationShipList = new ArrayList<>();
        HashMap<String, ZkbIbCrossRelationShip> relationShipHashMap = new HashMap<>();

        if(tenantUidList !=null && tenantUidList.size() > 0){
            for(String tenantUid : tenantUidList){
                List<ZkbContractAsset> zkbContractAssetList = zkbContractAssetRepository.findZkbContractAssetByTenantUID(tenantUid,d1);
                List<ZkbIbCrossUser> zkbIbCrossUsers = zkbIbCrossMapper.findUsersByTenantUid(tenantUid);
                for(ZkbIbCrossUser u : zkbIbCrossUsers){
                    if(userHashMap.containsKey(u.getLoginName())){
                        continue;
                    }
                    u.setLoginName("ZKB."+u.getLoginName());
                    u.setUserId(Integer.parseInt(u.getId()));
                    u.setId(null);
                    userHashMap.put(u.getLoginName(),u);
                    zkbIbCrossUserList.add(u);

                    for(ZkbContractAsset a : zkbContractAssetList){
                        if(relationShipHashMap.containsKey(u.getLoginName()+"|"+a.getAssetId())){
                            continue;
                        }
                        AssetInfo assetInfo = assetInfoRepository.findByUid(a.getAssetId());
                        if(StringUtils.isEmpty(assetInfo.getSystemId())){
                            continue;
                        }else if(!assetInfo.getSystemId().startsWith("0824")){
                            continue;
                        }
                        ZkbIbCrossRelationShip r = new ZkbIbCrossRelationShip();
                        r.setLoginName(u.getLoginName());
                        r.setSystemId(assetInfo.getSystemId());
                        r.setQrCode(assetInfo.getQrCode());
                        zkbIbCrossRelationShipList.add(r);
                        relationShipHashMap.put(u.getLoginName()+"|"+a.getAssetId(),r);
                    }
                }
            }
        }
        System.out.println("======");
        List<ZkbIbCrossUserStatus> zkbIbCrossUserStatusList = zkbIbCrossUserStatusRepository.find();
        for(ZkbIbCrossUserStatus s : zkbIbCrossUserStatusList){
            if(StringUtils.isEmpty(s.getLoginName())){
                continue;
            }
            if(userHashMap.containsKey(s.getLoginName())){
                continue;
            }
            if("D".equals(s.getStatus())){
                System.out.println(s.getLoginName());
                ZkbIbCrossUser user = new ZkbIbCrossUser();
                user.setLoginName("ZKB."+s.getLoginName());
                user.setDeleted(true);
                zkbIbCrossUserList.add(user);
            }else{
                List<ZkbIbCrossUser> zkbIbCrossUsers = zkbIbCrossMapper.findUsersByLoginName(s.getLoginName());
                if(zkbIbCrossUsers != null && zkbIbCrossUsers.size() > 0){
                    for(ZkbIbCrossUser u : zkbIbCrossUsers){
                        u.setLoginName("ZKB."+u.getLoginName());
                        if(userHashMap.containsKey(u.getLoginName())){
                            continue;
                        }
                        zkbIbCrossUserList.add(u);
                        userHashMap.put(u.getLoginName(),u);
                        List<ZkbContractAsset> assetList = zkbContractAssetRepository.findZkbContractAssetByTenantUID(u.getTenantUid(),d1);
                        for(ZkbContractAsset a : assetList){
                            if(relationShipHashMap.containsKey(u.getLoginName()+"|"+a.getAssetId())){
                                continue;
                            }
                            AssetInfo assetInfo = assetInfoRepository.findByUid(a.getAssetId());
                            if(StringUtils.isEmpty(assetInfo.getSystemId())){
                                continue;
                            }else if(!assetInfo.getSystemId().startsWith("0824")){
                                continue;
                            }
                            ZkbIbCrossRelationShip r = new ZkbIbCrossRelationShip();
                            r.setLoginName(u.getLoginName());
                            r.setSystemId(assetInfo.getSystemId());
                            r.setQrCode(assetInfo.getQrCode());
                            zkbIbCrossRelationShipList.add(r);
                            relationShipHashMap.put(u.getLoginName()+"|"+a.getAssetId(),r);
                        }
                    }
                }
            }
        }

        if(zkbIbCrossUserList.size() > 0){
            zkbIbCrossUserRepository.save(zkbIbCrossUserList);
        }
        if(zkbIbCrossRelationShipList.size() > 0){
            zkbIbCrossRelationShipRepository.save(zkbIbCrossRelationShipList);
        }
        if(zkbIbCrossUserStatusList.size() > 0){
            zkbIbCrossUserStatusRepository.delete(zkbIbCrossUserStatusList);
        }
    }



    public Date addDays(Date date,int days){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        Date d2 = calendar.getTime();
        return d2;
    }

    public void qRCodeJobTest(){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_QR_CODE_JOB);
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbIbCrossMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

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

    @Autowired
    ZkbIbCrossRelationShipLogRepository zkbIbCrossRelationShipLogRepository;

    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    List<ZkbIbCrossUser> zkbIbCrossUserList = new ArrayList<>();
    HashMap<String, ZkbIbCrossUser> userHashMap = new HashMap<>();
    List<ZkbIbCrossRelationShip> zkbIbCrossRelationShipList = new ArrayList<>();
    HashMap<String, ZkbIbCrossRelationShip> relationShipHashMap = new HashMap<>();
    HashMap<String, String> tenantMap = new HashMap<>();
    List<ZkbIbCrossRelationShipLog> zkbIbCrossRelationShipLogList = new ArrayList<>();


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


        if(tenantUidList !=null && tenantUidList.size() > 0){
            for(String tenantUid : tenantUidList){
                tenantMap.put(tenantUid,tenantUid);
                handleContractAsset(tenantUid, d1, d2,"modify");
            }
        }

        List<String> removeTenantUidList = zkbContractRepository.queryByEndDate(d1);
        if(removeTenantUidList != null && removeTenantUidList.size() > 0){
             for(String t : removeTenantUidList){
                if(tenantMap.containsKey(t)){
                    continue;
                }
                handleContractAsset(t, d1, d2,"remove");
            }
        }

        List<String> addTenantUidList = zkbContractRepository.queryByStartDate(d2);
        if(addTenantUidList !=null && addTenantUidList.size() > 0){
            for(String t : addTenantUidList){
                if(tenantMap.containsKey(t)){
                    continue;
                }
                List<ZkbContractAsset> zkbContractAssetList = zkbContractAssetRepository.findByStartDateAsset(t,d2);
                List<ZkbIbCrossUser> zkbIbCrossUsers = zkbIbCrossMapper.findUsersByTenantUid(t);

                for(ZkbIbCrossUser u : zkbIbCrossUsers){
                    if(userHashMap.containsKey("ZKB."+u.getLoginName())){
                        ZkbIbCrossUser uu = userHashMap.get("ZKB."+u.getLoginName());
                        if(uu.getDeleted() != null && uu.getDeleted()){
                            uu.setDeleted(null);
                        }
                    }else{
                        u.setLoginName("ZKB."+u.getLoginName());
                        //u.setUserId(Integer.parseInt(u.getId()));
                        u.setId(null);
                        userHashMap.put(u.getLoginName(),u);
                        zkbIbCrossUserList.add(u);
                    }


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

                        ZkbIbCrossRelationShipLog l = new ZkbIbCrossRelationShipLog();
                        l.setLoginName(u.getLoginName());
                        l.setSystemId(assetInfo.getSystemId());
                        l.setQrCode(assetInfo.getQrCode());
                        l.setCreateDate(new Date());
                        zkbIbCrossRelationShipLogList.add(l);
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
            if(userHashMap.containsKey("ZKB."+s.getLoginName())){
                ZkbIbCrossUser u = userHashMap.get(s.getLoginName());
                if("D".equals(s.getStatus())){
                    if(u.getDeleted() == null || !u.getDeleted()){
                        u.setDeleted(true);
                    }
                }else {
                    if(u.getDeleted()){
                        //如果发现数据库已经存在一条状态为删除的数据则不进行任何操作
                    }
                }
            }else{
                if("D".equals(s.getStatus())){
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
                            List<ZkbContractAsset> assetList = getContractAsset(u.getTenantUid(),d1,d2,"modify");
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
                                ZkbIbCrossRelationShipLog l = new ZkbIbCrossRelationShipLog();
                                l.setLoginName(u.getLoginName());
                                l.setSystemId(assetInfo.getSystemId());
                                l.setQrCode(assetInfo.getQrCode());
                                l.setCreateDate(new Date());
                                zkbIbCrossRelationShipLogList.add(l);
                                relationShipHashMap.put(u.getLoginName()+"|"+a.getAssetId(),r);
                            }
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
        if(zkbIbCrossRelationShipLogList.size() > 0){
            zkbIbCrossRelationShipLogRepository.save(zkbIbCrossRelationShipLogList);
        }

        zkbIbCrossUserList.clear();
        userHashMap.clear();
        zkbIbCrossRelationShipList.clear();
        zkbIbCrossRelationShipLogList.clear();
        relationShipHashMap.clear();
        tenantMap.clear();
    }

    public void handleContractAsset(String tenantUid,Date d1,Date d2,String type){
        List<ZkbContractAsset> zkbContractAssetList = getContractAsset(tenantUid,d1,d2,type);
        List<ZkbIbCrossUser> zkbIbCrossUsers = zkbIbCrossMapper.findUsersByTenantUid(tenantUid);
        if(zkbContractAssetList == null || zkbContractAssetList.size() ==0){
            for(ZkbIbCrossUser u : zkbIbCrossUsers) {
                if (userHashMap.containsKey("ZKB."+u.getLoginName())) {
                    continue;
                }
                u.setLoginName("ZKB." + u.getLoginName());
                u.setUserId(Integer.parseInt(u.getId()));
                u.setId(null);
                u.setDeleted(true);
                userHashMap.put(u.getLoginName(), u);
                zkbIbCrossUserList.add(u);
            }
        }else{
            for(ZkbIbCrossUser u : zkbIbCrossUsers){
                if(userHashMap.containsKey("ZKB."+u.getLoginName())){
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


    public Date addDays(Date date,int days){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        Date d2 = calendar.getTime();
        return d2;
    }

    public void qRCodeJobTest(){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_QR_CODE_JOB_SCHEDULE);
            if(url.indexOf("/gateway") > -1){
                url = url.replace("/gateway", "");
            }
            logger.info("-----------------------"+ url);
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ZkbContractAsset> getContractAsset(String tenantUid,Date currentDate,Date tomorrowDate,String type){
        List<ZkbContractAsset> zkbContractAssetList = zkbContractAssetRepository.findZkbContractAssetByTenantUID(tenantUid,currentDate);
        //快要过期的设备
        List<ZkbContractAsset> removeList = zkbContractAssetRepository.findByEndDateAsset(tenantUid,currentDate);


        List<ZkbContractAsset> newList = new ArrayList<ZkbContractAsset>();
        HashMap<String, String> removeMap = new HashMap<>();
        Iterator<ZkbContractAsset> iterator = zkbContractAssetList.iterator();
        for(ZkbContractAsset s : removeList){
            removeMap.put(s.getId(), s.getId());
        }
        while (iterator.hasNext()) {
            ZkbContractAsset s = iterator.next();
            if (removeMap.containsKey(s.getId())) {
                iterator.remove();
            }else {
                newList.add(s);
            }
        }
        if("modify".equals(type)){
            //要新增的设备
            List<ZkbContractAsset> addList = zkbContractAssetRepository.findByStartDateAsset(tenantUid,tomorrowDate);
            newList.addAll(addList);
        }

        return newList;
    }
    public  <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<T>();
        while (iter.hasNext()) {
            copy.add(iter.next());
        }
        return copy;
    }

}

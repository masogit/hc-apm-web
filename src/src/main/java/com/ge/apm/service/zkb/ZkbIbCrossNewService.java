package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbIbCrossMapper;
import com.ge.apm.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2023/6/16
 * @description :
 */
@Service
public class ZkbIbCrossNewService {

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

    private String NORMAL_D = "D";

    @Transactional(rollbackFor = Exception.class)
    public void handleIbCrossJob() {
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        String td = ymd.format(new Date());
        Date currentDate = null;
        Date tomorrowDate = null;
        try {
            currentDate = ymd.parse(td);
            tomorrowDate = addDays(currentDate, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap userMap = getUserHashMap(currentDate, tomorrowDate);
        saveCross(userMap, currentDate, tomorrowDate);
    }

    /**
     * 根据租户id得出所以需要同步的用户
     * status表中的userMap需要单独获取
     * @return
     */
    public HashMap<String, ZkbIbCrossUser> getUserHashMap(Date d1, Date d2){
        HashMap<String, ZkbIbCrossUser> um = new HashMap<>();
        Set<String> tenantSet = getTenantSet(d1, d2);
        List<ZkbIbCrossUser> zkbIbCrossUsers = zkbIbCrossMapper.findUsersByTenantSet(tenantSet);
        if(zkbIbCrossUsers != null && zkbIbCrossUsers.size() > 0){
            for(ZkbIbCrossUser u : zkbIbCrossUsers){
                if(um.containsKey("ZKB."+u.getLoginName())){
                    continue;
                }
                u.setLoginName("ZKB."+u.getLoginName());
                u.setUserId(Integer.parseInt(u.getId()));
                u.setId(null);
                um.put(u.getLoginName(),u);
            }
        }

        //根据status进行userMap判断,如果map存在，那么更新操作，如果不存在insert操作
        List<ZkbIbCrossUserStatus> zkbIbCrossUserStatusList = zkbIbCrossUserStatusRepository.find();
        for(ZkbIbCrossUserStatus s : zkbIbCrossUserStatusList){
            if(StringUtils.isEmpty(s.getLoginName())){
                continue;
            }
            String ln = "ZKB."+s.getLoginName();
            if(um.containsKey(ln)){
                ZkbIbCrossUser u = um.get(ln);
                if(NORMAL_D.equals(s.getStatus())){
                    if(u.getDeleted() == null || !u.getDeleted()){
                        u.setDeleted(true);
                    }
                }
            }else{
                if(NORMAL_D.equals(s.getStatus())){
                    ZkbIbCrossUser user = new ZkbIbCrossUser();
                    user.setLoginName(ln);
                    user.setDeleted(true);
                    um.put(user.getLoginName(),user);
                }else{
                    //通过loginName查询userAccount得出ZkbIbCrossUser，实际上一个loginName只有可能查询到一条ZkbIbCrossUser
                    //但是考虑重复loginName情况保险起见获得一个list
                    List<ZkbIbCrossUser> userList = zkbIbCrossMapper.findUsersByLoginName(s.getLoginName());
                    if(userList != null && userList.size() > 0){
                        for(ZkbIbCrossUser u : userList){
                            u.setLoginName("ZKB."+u.getLoginName());
                            if(um.containsKey(u.getLoginName())){
                                continue;
                            }
                            um.put(u.getLoginName(),u);
                        }
                    }

                }
            }
        }
        return um;
    }

    /**
     * 查询出合同修改的、合同临到期的、合同即将开始的
     * @param d1
     * @param d2
     * @return
     */
    public Set<String> getTenantSet(Date d1, Date d2){
        Set<String> set = new HashSet<>();
        List<String> tenantUidList = zkbContractRepository.queryByLastModifiedDate(d1,d2);
        List<String> removeTenantUidList = zkbContractRepository.queryByEndDate(d1);
        List<String> addTenantUidList = zkbContractRepository.queryByStartDate(d2);
        for(String s : tenantUidList){
            set.add(s);
        }

        for(String s : removeTenantUidList){
            set.add(s);
        }

        for(String s : addTenantUidList){
            set.add(s);
        }

        return set;
    }

    /**
     * 根据租户
     * 先根据sql查询中有效期合同内的设备以及马上进入有效期内的设备
     * 同时去除即将失效合同的设备
     */
    public List<String> getContractAssetNew(String tenantUid, Date currentDate, Date tomorrowDate){
        //不同合同可能包含相同的设备，所有要去重，使用distinct systemId进行去重
        List<String> zkbContractAssetList = zkbContractAssetRepository.queryAssetIds(tenantUid,currentDate,tomorrowDate);
        //快要过期的设备(去重)
        List<String> removeList = zkbContractAssetRepository.queryAssetIdsByEndDate(tenantUid,currentDate);

        List<String> newList = new ArrayList<String>();
        HashMap<String, String> removeMap = new HashMap<>();
        Iterator<String> iterator = zkbContractAssetList.iterator();
        for(String s : removeList){
            removeMap.put(s, s);
        }
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (removeMap.containsKey(s)) {
                iterator.remove();
            }else {
                newList.add(s);
            }
        }

        return newList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCross(HashMap<String, ZkbIbCrossUser> userHashMap,Date currentDate, Date tomorrowDate){
        List<ZkbIbCrossUser> zkbIbCrossUserList = new ArrayList<>();
        List<ZkbIbCrossRelationShip> zkbIbCrossRelationShipList = new ArrayList<>();
        List<ZkbIbCrossRelationShipLog> zkbIbCrossRelationShipLogList = new ArrayList<>();

        for(String s : userHashMap.keySet()){
            ZkbIbCrossUser zkbIbCrossUser = userHashMap.get(s);
            if(zkbIbCrossUser == null){
                continue;
            }
            if(zkbIbCrossUser.getDeleted() != null && zkbIbCrossUser.getDeleted()){
                zkbIbCrossUserList.add(zkbIbCrossUser);
            }else{
                zkbIbCrossUserList.add(zkbIbCrossUser);
                //根据用户所在租户获取该租户下所有符合条件的设备
                List<String> zkbContractAssetList = getContractAssetNew(zkbIbCrossUser.getTenantUid(), currentDate, tomorrowDate);
                for(String a : zkbContractAssetList){
                    AssetInfo assetInfo = assetInfoRepository.findByUid(a);
                    if(StringUtils.isEmpty(assetInfo.getSystemId())){
                        continue;
                    }else if(!assetInfo.getSystemId().startsWith("0824")){
                        continue;
                    }
                    ZkbIbCrossRelationShip r = new ZkbIbCrossRelationShip();
                    r.setLoginName(zkbIbCrossUser.getLoginName());
                    r.setSystemId(assetInfo.getSystemId());
                    r.setQrCode(assetInfo.getQrCode());
                    zkbIbCrossRelationShipList.add(r);

                    ZkbIbCrossRelationShipLog l = new ZkbIbCrossRelationShipLog();
                    l.setLoginName(zkbIbCrossUser.getLoginName());
                    l.setSystemId(assetInfo.getSystemId());
                    l.setQrCode(assetInfo.getQrCode());
                    l.setCreateDate(new Date());
                    zkbIbCrossRelationShipLogList.add(l);
                }
            }
        }
        //先清理数据里的数据
        zkbIbCrossUserRepository.deleteAllBatch();
        zkbIbCrossRelationShipRepository.deleteAllBatch();

        if(zkbIbCrossUserList.size() > 0){
            zkbIbCrossUserRepository.save(zkbIbCrossUserList);
        }
        if(zkbIbCrossRelationShipList.size() > 0){
            zkbIbCrossRelationShipRepository.save(zkbIbCrossRelationShipList);
            zkbIbCrossRelationShipLogRepository.save(zkbIbCrossRelationShipLogList);
        }

        zkbIbCrossUserStatusRepository.deleteAll();
    }

    public Date addDays(Date date,int days){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        Date d2 = calendar.getTime();
        return d2;
    }
}

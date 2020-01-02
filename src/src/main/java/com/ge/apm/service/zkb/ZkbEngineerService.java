package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.dao.ZkbEngineerTrackRecordRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbEngineer;
import com.ge.apm.domain.ZkbEngineerTrackRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 212605082 on 2019/12/18.
 */
@Component
public class ZkbEngineerService {

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private ZkbEngineerRepository ZkbEngineerDao;
    @Autowired
    private ZkbEngineerTrackRecordRepository zkbEngineerTrackRecordDao;

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));

        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    public List<ZkbEngineerTrackRecord> getZkbEngineerTrackRecordList(String zkbEngineerId){

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("zkbEngineerId", SearchFilter.Operator.EQ, zkbEngineerId));

        return zkbEngineerTrackRecordDao.findBySearchFilter(searchFilters);

    }

    @Transactional
    public void delete(ZkbEngineer zkbEngineer, List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList){
        ZkbEngineerDao.delete(zkbEngineer);
        zkbEngineerTrackRecordDao.delete(zkbEngineerTrackRecordList);
    }

    public ZkbEngineerTrackRecord saveZkbEngineerTrackRecord(ZkbEngineerTrackRecord zkbEngineerTrackRecord){
        return zkbEngineerTrackRecordDao.save(zkbEngineerTrackRecord);
    }

    public void deleteZkbEngineerTrackRecord(ZkbEngineerTrackRecord zkbEngineerTrackRecord){
        zkbEngineerTrackRecordDao.delete(zkbEngineerTrackRecord);
    }

    public List<ZkbEngineerTrackRecord> saveZkbEngineerTrackRecordList(List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList, ZkbEngineerTrackRecord currentZkbEngineerTrackRecord){
        List<ZkbEngineerTrackRecord> tempZkbEngineerTrackRecordList = new ArrayList<>();
        zkbEngineerTrackRecordList.forEach((item) -> {
            if(!item.getId().equals(currentZkbEngineerTrackRecord.getId())){
                tempZkbEngineerTrackRecordList.add(item);
            }
        });
        tempZkbEngineerTrackRecordList.add(currentZkbEngineerTrackRecord);

        return tempZkbEngineerTrackRecordList;
    }

    public List<ZkbEngineerTrackRecord> deleteZkbEngineerTrackRecordList(List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList, ZkbEngineerTrackRecord currentZkbEngineerTrackRecord){
        List<ZkbEngineerTrackRecord> tempZkbEngineerTrackRecordList = new ArrayList<>();
        zkbEngineerTrackRecordList.forEach((item) -> {
            if(!item.getId().equals(currentZkbEngineerTrackRecord.getId())){
                tempZkbEngineerTrackRecordList.add(item);
            }
        });

        return tempZkbEngineerTrackRecordList;
    }

}

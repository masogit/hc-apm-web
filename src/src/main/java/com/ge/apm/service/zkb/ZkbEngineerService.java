package com.ge.apm.service.zkb;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.dao.ZkbEngineerTrackRecordRepository;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 212605082 on 2019/12/18.
 */
@Component
public class ZkbEngineerService {

    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private ZkbEngineerRepository zkbEngineerDao;
    @Autowired
    private ZkbEngineerTrackRecordRepository zkbEngineerTrackRecordDao;

    @Autowired
    private ZkbEngineerRepository zkbEngineerRepository;

    @Autowired
    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;

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

    /**
     * 根据工程师ID获取培训记录
     * @param eid 工程师主键
     * @return
     */
    public List<ZkbContractCourseSchedule> getZkbEngineerTrainRecordList(String eid)
    {
        return this.zknContractCourseScheduleMapper.fetchByEid(eid);
    }

    @Transactional
    public void delete(ZkbEngineer zkbEngineer, List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList){
        zkbEngineerDao.delete(zkbEngineer);
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

    /**
     * 保存附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUploadFile(List<V2_BlobObject> v2_blobObjects, String id){
        if(CollectionUtils.isNotEmpty(v2_blobObjects)){
            for (V2_BlobObject v : v2_blobObjects){
                v.setBoUuid(id);
                blobObjectRepository.save(v);
            }
        }
    }

    /**
     * 移除附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFile(List<V2_BlobObject> v2_blobObjects){
        if(CollectionUtils.isNotEmpty(v2_blobObjects)){
            for (V2_BlobObject v : v2_blobObjects){
                blobObjectRepository.delete(v.getId());
            }
        }
    }

    /**
     * 生成SSO索引
     * @param zkbEngineer 当前工程师
     * @return
     */
    public ZkbEngineer fullSso(ZkbEngineer zkbEngineer, String orderPrefix)
    {
        String yearStr = DateUtil.getFormatStrByDate(new Date(),"yyyy");
        String endIndex = yearStr.substring(2);
        Object o= zkbEngineerRepository.getMaxIndex(orderPrefix+endIndex+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String sso = orderPrefix+endIndex+String.format("%04d",index);
        zkbEngineer.setSso(sso);
        zkbEngineer.setSsoIndex(index);
        return zkbEngineer;
    }

    @Transactional
    public void save(ZkbEngineer engineer) {
        zkbEngineerRepository.save(engineer);
    }
}

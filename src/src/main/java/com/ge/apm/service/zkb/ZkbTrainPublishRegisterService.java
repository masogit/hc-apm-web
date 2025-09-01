package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbTrainPublishRegisterMapper;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.Date;
import java.util.List;

@Service
public class ZkbTrainPublishRegisterService {

    @Autowired
    private ZkbTrainPublishRegisterMapper zkbTrainPublishRegisterMapper;

    @Autowired
    private ZkbTrainCourseRepository zkbTrainCourseRepository;

    @Autowired
    private ZkbEngineerRepository zkbEngineerRepository;

    @Autowired
    private ZkbTrainPublishRegisterRepository zkbTrainPublishRegisterRepository;

    @Autowired
    private ZkbTrainPublishRegisterDetailRepository zkbTrainPublishRegisterDetailRepository;

    @Autowired
    private ZkbTrainCourseQuotaRepository zkbTrainCourseQuotaRepository;

    @Autowired
    private ZkbTrainCourseQuotaDetailRepository zkbTrainCourseQuotaDetailRepository;
    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;

    public List<ZkbTrainPublishInfo> loadUnRegisterDataList(List<SearchFilter> searchFilters, String tenantUid){
        List<ZkbTrainPublishInfo> list = zkbTrainPublishRegisterMapper.loadUnRegisterDataList(searchFilters, tenantUid);
        if(CollectionUtils.isNotEmpty(list)){
            for(ZkbTrainPublishInfo p : list){
                ZkbTrainCourse zkbTrainCourse = zkbTrainCourseRepository.findById(p.getCourseId());
                p.setZkbTrainCourse(zkbTrainCourse);
            }
        }
        return list;
    }

    public List<ZkbEngineer> queryEngineerListByTenant(UserAccount userAccount){
        return zkbEngineerRepository.findByTenantUID(userAccount.getTenantUID());
    }

    public ZkbEngineer findEngineerById(String id){
        return zkbEngineerRepository.findById(id);
    }

    public void saveData(ZkbTrainPublishRegister register, List<ZkbTrainPublishRegisterDetail> registerDetails,UserAccount userAccount){
        register.setCreatedBy(userAccount.getName());
        register.setCreatedDate(new Date());
        register.setStatus(1);
        register.setRegisterDate(new Date());
        register.setTenantUid(userAccount.getTenantUID());
        zkbTrainPublishRegisterRepository.save(register);

        int quota= 0;
        for(ZkbTrainPublishRegisterDetail d : registerDetails){
            d.setId(null);
            d.setRegisterId(register.getId());
            d.setCreatedBy(userAccount.getName());
            d.setCreatedDate(new Date());
            quota++;
            d = zkbTrainPublishRegisterDetailRepository.save(d);
            for(V2_BlobObject blobObject : d.getV2_blobObjectList()){
                blobObject.setBoUuid(d.getId());
                blobObjectRepository.save(blobObject);
            }
            blobObjectRepository.save(d.getV2_blobObjectList());
        }
        ZkbTrainPublishInfo zkbTrainPublishInfo = zkbTrainPublishRepository.findById(register.getPid());

        ZkbTrainCourseQuota zkbTrainCourseQuota = zkbTrainCourseQuotaRepository.findFirstByTenantUidAndCourseId(userAccount.getTenantUID(), zkbTrainPublishInfo.getCourseId());
        zkbTrainCourseQuota.setQuota(zkbTrainCourseQuota.getQuota() - quota);
        zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
        if(zkbTrainCourseQuota != null){
            List<ZkbTrainCourseQuotaDetail> quotaDetails = zkbTrainCourseQuotaDetailRepository.findByQuotaIdOrderByUniqueIDDesc(zkbTrainCourseQuota.getId());
            for(ZkbTrainCourseQuotaDetail detail : quotaDetails){
                if(quota == 0){
                    break;
                }
                if(detail.getQuota() <= 0){
                    continue;
                }
                if(quota > detail.getQuota()){
                    detail.setQuota(0);
                }else{
                    detail.setQuota(detail.getQuota() - quota);
                }
                zkbTrainCourseQuotaDetailRepository.save(detail);
                quota--;
            }
        }
    }

    public void modifyDetail(ZkbTrainPublishRegisterDetail zkbTrainPublishRegisterDetail, List<V2_BlobObject> v2_blobObjectList){
        zkbTrainPublishRegisterDetailRepository.save(zkbTrainPublishRegisterDetail);
        for(V2_BlobObject v : v2_blobObjectList){
            if(StrUtil.isNotEmpty(v.getBoUuid())){
                continue;
            }
            v.setBoUuid(zkbTrainPublishRegisterDetail.getId());
            blobObjectRepository.save(v);
        }
    }

    public ZkbTrainPublishRegister getRegisterById(String id){
        return zkbTrainPublishRegisterRepository.findById(id);
    }

    public void deleteDetail(ZkbTrainPublishRegisterDetail detail){
        zkbTrainPublishRegisterDetailRepository.delete(detail);
    }

    public List<V2_BlobObject> queryBlobObject(String boUUid){
        return blobObjectRepository.findByBoUuid(boUUid);
    }

    public List<ZkbTrainPublishRegisterDetail> queryDetailByRegister(String id){
        return zkbTrainPublishRegisterDetailRepository.findByRegisterId(id);
    }

}

package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbTrainPublishRegisterMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ZkbTrainCourseQuotaService zkbTrainCourseQuotaService;

    public List<ZkbTrainPublishInfo> loadUnRegisterDataList(List<SearchFilter> searchFilters, String tenantUid) {
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

//    public void saveData(ZkbTrainPublishRegister register, List<ZkbTrainPublishRegisterDetail> registerDetails, UserAccount userAccount) {
//        register.setCreatedBy(userAccount.getName());
//        register.setCreatedDate(new Date());
//        register.setStatus(1);
//        register.setRegisterDate(new Date());
//        register.setTenantUid(userAccount.getTenantUID());
//        zkbTrainPublishRegisterRepository.save(register);
//
//        int quota = 0;
//        for (ZkbTrainPublishRegisterDetail d : registerDetails) {
//            d.setId(null);
//            d.setRegisterId(register.getId());
//            d.setCreatedBy(userAccount.getName());
//            d.setCreatedDate(new Date());
//            quota++;
//            d = zkbTrainPublishRegisterDetailRepository.save(d);
//            for (V2_BlobObject blobObject : d.getV2_blobObjectList()) {
//                blobObject.setBoUuid(d.getId());
//                blobObjectRepository.save(blobObject);
//            }
//            blobObjectRepository.save(d.getV2_blobObjectList());
//        }
//        ZkbTrainPublishInfo zkbTrainPublishInfo = zkbTrainPublishRepository.findById(register.getPid());
//
//        ZkbTrainCourseQuota zkbTrainCourseQuota = zkbTrainCourseQuotaRepository.findFirstByTenantUidAndCourseId(userAccount.getTenantUID(), zkbTrainPublishInfo.getCourseId());
//        zkbTrainCourseQuota.setQuota(zkbTrainCourseQuota.getQuota() - quota);
//        zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
//        if (zkbTrainCourseQuota != null) {
//            List<ZkbTrainCourseQuotaDetail> quotaDetails = zkbTrainCourseQuotaDetailRepository.findByQuotaIdOrderByUniqueIDDesc(zkbTrainCourseQuota.getId());
//            for (ZkbTrainCourseQuotaDetail detail : quotaDetails) {
//                if (quota == 0) {
//                    break;
//                }
//                if (detail.getQuota() <= 0) {
//                    continue;
//                }
//                if (quota > detail.getQuota()) {
//                    detail.setQuota(0);
//                } else {
//                    detail.setQuota(detail.getQuota() - quota);
//                }
//                zkbTrainCourseQuotaDetailRepository.save(detail);
//                quota--;
//            }
//        }
//    }

    public void saveData(ZkbTrainPublishRegister register, List<ZkbTrainPublishRegisterDetail> registerDetails, UserAccount userAccount) {

        if (CollectionUtils.isEmpty(registerDetails)) {
            throw new IllegalArgumentException("报名人员不能为空");
        }
        int personCount = registerDetails.size();
        ZkbTrainPublishInfo publishInfo = zkbTrainPublishRepository.findById(register.getPid());
        if (publishInfo == null) {
            throw new BusinessException("未找到课程发布信息");
        }
        String tenantUid = userAccount.getTenantUID();
        String courseId = publishInfo.getCourseId();
        zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -personCount, userAccount);

        // 保存主表
        register.setCreatedBy(userAccount.getName());
        register.setCreatedDate(new Date());
        register.setStatus(1);
        register.setRegisterDate(new Date());
        register.setTenantUid(tenantUid);
        zkbTrainPublishRegisterRepository.save(register);

        // 保存明细及附件
        for (ZkbTrainPublishRegisterDetail d : registerDetails) {
            d.setId(null);
            d.setRegisterId(register.getId());
            d.setCreatedBy(userAccount.getName());
            d.setCreatedDate(new Date());
            zkbTrainPublishRegisterDetailRepository.save(d);

            if (CollectionUtils.isNotEmpty(d.getV2_blobObjectList())) {
                for (V2_BlobObject blob : d.getV2_blobObjectList()) {
                    blob.setBoUuid(d.getId());
                    blob.setCreatedBy(userAccount.getName());
                    blob.setCreatedDate(new Date());
                    blobObjectRepository.save(blob);
                }
            }
        }
    }

//    public void modifyDetail(ZkbTrainPublishRegisterDetail zkbTrainPublishRegisterDetail, List<V2_BlobObject> v2_blobObjectList) {
//        zkbTrainPublishRegisterDetailRepository.save(zkbTrainPublishRegisterDetail);
//        for (V2_BlobObject v : v2_blobObjectList) {
//            if (StrUtil.isNotEmpty(v.getBoUuid())) {
//                continue;
//            }
//            v.setBoUuid(zkbTrainPublishRegisterDetail.getId());
//            blobObjectRepository.save(v);
//        }
//    }

    public void modifyDetail(ZkbTrainPublishRegisterDetail detail, List<V2_BlobObject> v2_blobObjectList, UserAccount currentUser) {

        boolean isCreate = detail.getId() == null;
        if (isCreate) {
            ZkbTrainPublishRegister register = zkbTrainPublishRegisterRepository.findById(detail.getRegisterId());
            if (register == null) {
                throw new BusinessException("未找到报名主表");
            }
            ZkbTrainPublishInfo publishInfo = zkbTrainPublishRepository.findById(register.getPid());
            if (publishInfo == null) {
                throw new BusinessException("未找到课程信息");
            }
            String tenantUid = register.getTenantUid();
            String courseId = publishInfo.getCourseId();
            zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -1, currentUser);
        }

        zkbTrainPublishRegisterDetailRepository.save(detail);

        // 保存新上传的文件
        if (CollectionUtils.isNotEmpty(v2_blobObjectList)) {
            for (V2_BlobObject blob : v2_blobObjectList) {
                if (StrUtil.isEmpty(blob.getBoUuid())) {
                    blob.setBoUuid(detail.getId());
                    blob.setCreatedBy(currentUser.getName());
                    blob.setCreatedDate(new Date());
                    blobObjectRepository.save(blob);
                }
            }
        }
    }

    public ZkbTrainPublishRegister getRegisterById(String id){
        return zkbTrainPublishRegisterRepository.findById(id);
    }

    public void deleteDetail(ZkbTrainPublishRegisterDetail detail, UserAccount currentUser) {
        if (detail == null || detail.getId() == null) return;

        ZkbTrainPublishRegister register = zkbTrainPublishRegisterRepository.findById(detail.getRegisterId());
        if (register == null) {
            throw new BusinessException("未找到报名主表");
        }

        ZkbTrainPublishInfo publishInfo = zkbTrainPublishRepository.findById(register.getPid());
        if (publishInfo == null) {
            throw new BusinessException("未找到课程信息");
        }

        // 删除附件
        List<V2_BlobObject> files = blobObjectRepository.findByBoUuid(detail.getId());
        if (CollectionUtils.isNotEmpty(files)) {
            blobObjectRepository.delete(files);
        }

        zkbTrainCourseQuotaService.adjustQuota(register.getTenantUid(), publishInfo.getCourseId(), +1, currentUser);

        zkbTrainPublishRegisterDetailRepository.delete(detail);
    }

    public List<V2_BlobObject> queryBlobObject(String boUUid){
        return blobObjectRepository.findByBoUuid(boUUid);
    }

    public List<ZkbTrainPublishRegisterDetail> queryDetailByRegister(String id){
        return zkbTrainPublishRegisterDetailRepository.findByRegisterId(id);
    }

    public List<ZkbTrainPublishRegister> getRegisterByPid(String pid) {
        return entityManager.createQuery(
                        "SELECT DISTINCT r FROM ZkbTrainPublishRegister r " +
                                "LEFT JOIN FETCH r.zkbTrainPublishRegisterDetails " +
                                "WHERE r.pid = :pid",
                        ZkbTrainPublishRegister.class)
                .setParameter("pid", pid)
                .getResultList();
    }

    @Transactional
    public void cancelRegister(ZkbTrainPublishRegister register, UserAccount currentUser) {
        if (register == null || register.getId() == null) return;

        ZkbTrainPublishInfo publishInfo = zkbTrainPublishRepository.findById(register.getPid());
        if (publishInfo == null) {
            throw new BusinessException("未找到课程发布信息");
        }

        List<ZkbTrainPublishRegisterDetail> details = zkbTrainPublishRegisterDetailRepository.findByRegisterId(register.getId());
        int personCount = CollectionUtils.size(details);

        if (personCount > 0) {
            zkbTrainCourseQuotaService.adjustQuota(register.getTenantUid(), publishInfo.getCourseId(), +personCount, currentUser);

            for (ZkbTrainPublishRegisterDetail detail : details) {
                List<V2_BlobObject> files = blobObjectRepository.findByBoUuid(detail.getId());
                if (CollectionUtils.isNotEmpty(files)) {
                    blobObjectRepository.delete(files);
                }
                zkbTrainPublishRegisterDetailRepository.delete(detail);
            }
        }
        zkbTrainPublishRegisterRepository.delete(register);
    }
}

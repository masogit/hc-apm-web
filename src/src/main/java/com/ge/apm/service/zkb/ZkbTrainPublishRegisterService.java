package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbTrainPublishRegisterMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ZkbTrainPublishRegisterService {

    private Logger logger = LoggerFactory.getLogger(ZkbTrainPublishRegisterService.class);

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
        if (CollectionUtils.isNotEmpty(list)) {
            for (ZkbTrainPublishInfo p : list) {
                ZkbTrainCourse zkbTrainCourse = zkbTrainCourseRepository.findById(p.getCourseId());
                p.setZkbTrainCourse(zkbTrainCourse);
            }
        }
        return list;
    }

    public List<ZkbEngineer> queryEngineerListByTenant(UserAccount userAccount) {
        return zkbEngineerRepository.findByTenantUID(userAccount.getTenantUID());
    }

    public ZkbEngineer findEngineerById(String id) {
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
        // 提取所有工程师ID（按报名顺序）
        List<String> engineerIds = registerDetails.stream()
                .map(ZkbTrainPublishRegisterDetail::getEngineerId)
                .collect(Collectors.toList());
        zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -personCount, engineerIds, userAccount);

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

    public void modifyDetail(ZkbTrainPublishRegisterDetail detail, UserAccount currentUser) {

        // 1. 获取原始明细
        ZkbTrainPublishRegisterDetail originalDetail = zkbTrainPublishRegisterDetailRepository.findById(detail.getId());
        if (originalDetail == null) {
            throw new BusinessException("未找到原始明细记录");
        }

        // 2. 检查是否需要处理工程师ID变更
        boolean engineerIdChanged = false;
        if (detail.getEngineerId() != null && !detail.getEngineerId().equals(originalDetail.getEngineerId())) {
            engineerIdChanged = true;
        }

        // ✅ 关键修改：直接处理工程师ID变更
        if (engineerIdChanged) {
            // 获取课程信息
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
            String oldEngineerId = originalDetail.getEngineerId();
            String newEngineerId = detail.getEngineerId();

            // 处理ID变更的配额调整
            if (StringUtils.hasText(oldEngineerId) && !StringUtils.hasText(newEngineerId)) {
                zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, 1, Collections.singletonList(oldEngineerId), currentUser);
            } else if (!StringUtils.hasText(oldEngineerId) && StringUtils.hasText(newEngineerId)) {
                zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -1, Collections.singletonList(newEngineerId), currentUser);
            } else if (StringUtils.hasText(oldEngineerId) && StringUtils.hasText(newEngineerId)) {
                zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, 1, Collections.singletonList(oldEngineerId), currentUser);
                zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -1, Collections.singletonList(newEngineerId), currentUser);
            }

            // ✅ 直接更新detail的engineerId（关键！）
            detail.setEngineerId(newEngineerId);
        }

        boolean isCreate = false;
        if (detail.getId() == null || detail.getId().contains("temp")) {
            isCreate = true;
            detail.setId(null);
        }
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
            logger.info("modifyDetail adjustQuota:{}", courseId);

            // 获取当前明细的工程师ID（精确匹配的关键）
            String engineerId = detail.getEngineerId();
            List<String> engineerIds = Collections.singletonList(engineerId);
            zkbTrainCourseQuotaService.adjustQuota(tenantUid, courseId, -1, engineerIds, currentUser);
        } else {
            blobObjectRepository.deleteByBoUuid(detail.getId());
        }

        zkbTrainPublishRegisterDetailRepository.save(detail);
        // 保存新上传的文件
        if (CollectionUtils.isNotEmpty(detail.getV2_blobObjectList())) {
            for (V2_BlobObject blob : detail.getV2_blobObjectList()) {
                if (StrUtil.isEmpty(blob.getBoUuid())) {
                    blob.setBoUuid(detail.getId());
                    blob.setCreatedBy(currentUser.getName());
                    blob.setCreatedDate(new Date());
                    blobObjectRepository.save(blob);
                }
            }
        }
    }

    public ZkbTrainPublishRegister getRegisterById(String id) {
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

        // 获取当前明细的工程师ID（精确匹配的关键）
        String engineerId = detail.getEngineerId();
        List<String> engineerIds = Collections.singletonList(engineerId);

        zkbTrainCourseQuotaService.adjustQuota(register.getTenantUid(), publishInfo.getCourseId(), +1, engineerIds, currentUser);
        zkbTrainPublishRegisterDetailRepository.delete(detail);
    }

    public List<V2_BlobObject> queryBlobObject(String boUUid) {
        return blobObjectRepository.findByBoUuid(boUUid);
    }

    public List<ZkbTrainPublishRegisterDetail> queryDetailByRegister(String id) {
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
        logger.info("cancelRegister personCount:{}", personCount);

        if (personCount > 0) {
            // 提取所有工程师ID
            List<String> engineerIds = details.stream()
                    .map(ZkbTrainPublishRegisterDetail::getEngineerId)
                    .collect(Collectors.toList());

            zkbTrainCourseQuotaService.adjustQuota(register.getTenantUid(), publishInfo.getCourseId(), +personCount, engineerIds, currentUser);

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

    public ZkbTrainPublishRegisterDetail getDetailById(String id) {
        ZkbTrainPublishRegisterDetail detail = zkbTrainPublishRegisterDetailRepository.findById(id);
        List<V2_BlobObject> files = queryBlobObject(detail.getId());
        detail.setV2_blobObjectList(files);
        return detail;
    }
}

package com.ge.apm.dao;

import com.ge.apm.domain.ExportRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;

public interface ExportRepository extends GenericRepository<ExportRecord> {

    /**
     * 根据 id、tenantUid、status 确定一条记录
     *
     * @param id        主键 id
     * @param tenantUid 租户 id
     * @param status    状态
     * @return 实体对象
     */
    ExportRecord findByIdAndTenantUidAndStatus(Long id, String tenantUid, String status);

    /**
     * 根据 id 查询导出记录
     *
     * @param id 主键 id
     * @return 实体对象
     */
    ExportRecord findById(Long id);

    /**
     * 根据 ids 查询导出记录集合
     *
     * @param ids 主键 ids
     * @return list
     */
    List<ExportRecord> findByIdIn(Collection<Long> ids);

    /**
     * 根据 ids 批量删除
     * @param ids 主键 ids
     * @return int
     */
    @Modifying
    @Transactional
    @Query(value = "delete from export_record where id in ?1",nativeQuery = true)
    Integer deleteByIds(List<Long> ids);
}

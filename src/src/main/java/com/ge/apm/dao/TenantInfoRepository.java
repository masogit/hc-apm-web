package com.ge.apm.dao;

import com.ge.apm.domain.TenantInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import java.util.Optional;

public interface TenantInfoRepository extends GenericRepository<TenantInfo> {
    
    public List<TenantInfo> getByName(String name);

    public TenantInfo findByuid(String uid);

    @Query("select t from TenantInfo t order by name")
    public List<TenantInfo> getAllOrderByName();

    @Query("select t from TenantInfo t where t.initialPassword is not null")
    List<TenantInfo> findTenantInfoByInitialPasswordIsNotNull();

    public List<TenantInfo> findAllByTestStatusIsNullOrTestStatusNotOrderByName(int testStatus);

    public Optional<TenantInfo> findFirstByName(String name);

    /**
     * 根据租户名称和类型查找数据
     * @param name 租户名称
     * @param type 租户类型
     * @return 返回租户列表
     */
    List<TenantInfo> findByNameAndType(String name,Integer type);

    /**
     * 根据类型查找数据
     * @param type 租户类型
     * @return 返回租户列表
     */
    List<TenantInfo> findByType(Integer type);
}

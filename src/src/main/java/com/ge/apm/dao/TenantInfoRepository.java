package com.ge.apm.dao;

import com.ge.apm.domain.TenantInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * 根据类型和名称获取
     * @param type  类型
     * @param name 名称
     * @return
     */
    List<TenantInfo> findByTypeAndNameNotLike(Integer type,String name);

    TenantInfo findByUid(String uid);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE tenant_info a SET qrcode_editable = ?1 WHERE a.uid = ?2")
    void updateQrCodeEditable(boolean editable, String uid);

    @Query(value = "SELECT DISTINCT t1.* from tenant_info t1 INNER JOIN zkb_contract z1 on t1.uid = z1.tenant_uid " +
            " where t1.name not like 'ZKB%' and NOT ((end_date < ?1) OR (start_date > ?2)) and t1.type =?3", nativeQuery = true)
    List<TenantInfo> findByAnyContractDate(Date firstDate, Date kpiDate, Integer type);

    @Query(value = "select * from tenant_info t where t.custom_tenant = ?1", nativeQuery = true)
    public List<TenantInfo> findByCustomTenant(String name);

    /**
     * 根据租户UID批量获取租户信息
     * @param uidSet 租户UID
     * @return
     */
    List<TenantInfo> findByUidIn(Set<String> uidSet);

    @Query(value = " select ti.* from tenant_info ti join supplier_tenant_join stj on stj.tenant_id = ti.id where stj.supplier_id = ?1  ", nativeQuery = true)
    List<TenantInfo> findSupplierJoinTenant(Integer  siteId);

    @Query(value = "select * from tenant_info where tenant_config like '%\"audit\"%' order by name", nativeQuery = true)
    public List<TenantInfo> getAllByTenantConfig();


    /**
     * 查询当前时间有效的合同的租户
     * @param
     * @param
     * @return
     */
    @Query(value = "SELECT ti.* from zkb_contract zkbc inner join tenant_info ti on zkbc.tenant_uid = ti.uid where ti.type='3' and NOT ((end_date < ?1) OR (start_date > ?2)) and zkbc.contract_amount='oneMillionMore' ", nativeQuery = true)
    List<TenantInfo> findTenantInfoListByContractDate(Date firstDay,Date lastDay);

}

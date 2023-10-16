package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFileDirectory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/7/30
 */
public interface ZkbFileDirectoryRepository extends GenericRepositoryUUID<ZkbFileDirectory> {


    /**
     * 根据目录等级信息获取目录信息去除自己本条数据
     * @param selfId 本身ID
     * @param level 1.主目录 2.子目录
     * @return
     */
    List<ZkbFileDirectory> findByIdIsNotAndLevel(String selfId,Integer level);

    /**
     * 根据目录等级获取目录信息
     * @param level 1.主目录 2.子目录
     * @return
     */
    List<ZkbFileDirectory> findByLevel(Integer level);

    /**
     * 获取子目录信息
     * @param zkbFileDirectory
     * @return
     */
    List<ZkbFileDirectory> findByZkbFileDirectory(ZkbFileDirectory zkbFileDirectory);

    /**
     * 根据目录级别和目录名称查找数据
     * @param level 目录级别
     * @param name  目录名称
     * @return
     */
    List<ZkbFileDirectory> findByLevelAndName(Integer level,String name);

    /**
     * 根据目录级别和目录名称查找非自身的数据
     * @param selfId 目录ID
     * @param level 目录级别
     * @param name 目录名称
     * @return
     */
    List<ZkbFileDirectory> findByIdIsNotAndLevelAndName(String selfId,Integer level,String name);


    /**
     * 根据主目录名称，子目录名称获取能够匹配到的目录个数
     * @param mainDirectory 主目录名称
     * @param secondDirectory 子目录名称
     * @param level 目录级别
     * @return
     */
    @Query(value = "select count(id) from zkb_file_directory where name = ?1 and id in (SELECT parent_id from zkb_file_directory where LEVEL = ?3 and name = ?2);",nativeQuery = true)
    Integer findDirecotryCount(String mainDirectory,String secondDirectory,Integer level);




    @Query(value = "select * from zkb_contract t where t.tenant_uid =?1 and t.start_date <= to_date(to_char(now(), 'yyyyMMdd'), 'yyyyMMdd') and t.end_date >= to_date(to_char(now(), 'yyyyMMdd'), 'yyyyMMdd')",nativeQuery = true)
    List<ZkbContract> getEffectiveContract(String tenantUid);
}

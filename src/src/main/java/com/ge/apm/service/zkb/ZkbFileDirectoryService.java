package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbFileDirectoryRepository;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFileDirectory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 503202755 2020/7/30
 */
@Component
public class ZkbFileDirectoryService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFileDirectoryService.class);

    /**
     * 主目录level值
     */
    public static final Integer MAIN_DIRECTORY_LEVEL_VALUE = new Integer(1);

    /**
     * 子目录level值
     */
    public static final Integer SUB_DIRECTORY_LEVEL_VALUE = new Integer(2);

    @Autowired
    private ZkbFileDirectoryRepository zkbFileDirectoryRepository;

    /**
     * 获取所有主目录信息
     * @param selfId 目录ID
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory(String selfId)
    {
        if(selfId==null)
        {
            return this.zkbFileDirectoryRepository.findByLevel(ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE);
        }
         return this.zkbFileDirectoryRepository.findByIdIsNotAndLevel(selfId,ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE);
    }

    /**
     * 检查是否有子目录
     * @param zkbFileDirectory
     * @return
     */
    public boolean checkSubDirectoryExist(ZkbFileDirectory zkbFileDirectory)
    {
        if(ZkbFileDirectoryService.SUB_DIRECTORY_LEVEL_VALUE.equals(zkbFileDirectory.getLevel()))
        {
            return false;
        }
        List<ZkbFileDirectory> list = this.zkbFileDirectoryRepository.findByZkbFileDirectory(zkbFileDirectory);
        if(CollectionUtils.isEmpty(list))
        {
            return false;
        }
        return true;
    }

    /**
     * 检查目录名称是否存在
     * @param zkbFileDirectory
     * @return
     */
    public boolean checkDirectoryNameExist(ZkbFileDirectory zkbFileDirectory)
    {
        boolean exist = true;
        if(zkbFileDirectory.getId()==null)
        {
            List<ZkbFileDirectory> list = this.zkbFileDirectoryRepository.findByLevelAndName(
                    zkbFileDirectory.getLevel(),
                    zkbFileDirectory.getName());
            if(CollectionUtils.isEmpty(list))
            {
                exist=false;
            }
        }else
            {
                List<ZkbFileDirectory> list = this.zkbFileDirectoryRepository.findByIdIsNotAndLevelAndName(
                        zkbFileDirectory.getId(),
                        zkbFileDirectory.getLevel(),
                        zkbFileDirectory.getName());
                if(CollectionUtils.isEmpty(list))
                {
                    exist=false;
                }
            }
        return exist;
    }

    /**
     * 获取子目录信息
     * @param zkbFileDirectory
     * @return
     */
    public List<ZkbFileDirectory> findByZkbFileDirectory(ZkbFileDirectory zkbFileDirectory)
    {
        return this.zkbFileDirectoryRepository.findByZkbFileDirectory(zkbFileDirectory);
    }

    /**
     * 根据目录级别和目录名称查找数据
     * @param level 目录级别
     * @param name  目录名称
     * @return
     */
    public List<ZkbFileDirectory> findByLevelAndName(Integer level,String name) {
        return this.zkbFileDirectoryRepository.findByLevelAndName(level,name);
    }

    public boolean checkEffectiveContract(String tenantUid) {
        List<ZkbContract> zkbContracts = this.zkbFileDirectoryRepository.getEffectiveContract(tenantUid);
        return zkbContracts.size()>0?true:false;
    }
}

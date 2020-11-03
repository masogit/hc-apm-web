package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbKeyBehaviorIndexConfigRepository;
import com.ge.apm.domain.ZkbKeyBehaviorIndexConfig;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 503202755 2020/10/20
 */
@Component
public class ZkbKeyBehaviorIndexConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbKeyBehaviorIndexConfigService.class);

    @Autowired
    private ZkbKeyBehaviorIndexConfigRepository zkbKeyBehaviorIndexConfigRepository;

    /**
     * 检查租户是否存在
     * @param zkbKeyBehaviorIndexConfig 权重配置信息
     * @return
     */
    public boolean checkTenantExist(ZkbKeyBehaviorIndexConfig zkbKeyBehaviorIndexConfig)
    {
        boolean exist = true;
        if(zkbKeyBehaviorIndexConfig.getId()==null)
        {
            List<ZkbKeyBehaviorIndexConfig> list = this.zkbKeyBehaviorIndexConfigRepository.findByTenantUID(zkbKeyBehaviorIndexConfig.getTenantUID());
            if(CollectionUtils.isEmpty(list))
            {
                exist=false;
            }
        }else
        {
            List<ZkbKeyBehaviorIndexConfig> list = this.zkbKeyBehaviorIndexConfigRepository.findByTenantUIDAndIdNot(zkbKeyBehaviorIndexConfig.getTenantUID(),zkbKeyBehaviorIndexConfig.getId());
            if(CollectionUtils.isEmpty(list))
            {
                exist=false;
            }
        }
        return exist;
    }

}

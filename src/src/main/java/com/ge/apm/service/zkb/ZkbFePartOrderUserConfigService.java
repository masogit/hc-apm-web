package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fe人工申请配置
 * @author 503202755 2020/12/14
 */
@Component
public class ZkbFePartOrderUserConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderUserConfigService.class);

    @Autowired
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;


}

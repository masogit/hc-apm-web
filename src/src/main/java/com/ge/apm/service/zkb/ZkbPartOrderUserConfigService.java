package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 503202755 2020/9/25
 */
@Component
public class ZkbPartOrderUserConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbPartOrderUserConfigService.class);

    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;




}

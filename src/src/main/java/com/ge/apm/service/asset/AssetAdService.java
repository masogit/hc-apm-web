package com.ge.apm.service.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.AssetAdRepository;
import com.ge.apm.dao.AssetInfoRepository;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class AssetAdService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetAdService.class);


	@Autowired
	private AssetAdRepository assetADRepository;
	
	@Autowired
	private AssetInfoRepository assetInfoRepository;
	
	public boolean checkSystemIdExists(String systemId){
		return assetInfoRepository.checkExistsBySystemId(systemId)==null?false:true;
	}
}

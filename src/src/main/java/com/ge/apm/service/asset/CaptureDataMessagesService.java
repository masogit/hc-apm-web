package com.ge.apm.service.asset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.CaptureDataMessagesRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.CaptureDataMessages;
import com.ge.apm.domain.OrgInfo;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class CaptureDataMessagesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CaptureDataMessagesService.class);


	@Autowired
	private CaptureDataMessagesRepository captureDMRepository;
	
	@Autowired
	private AssetInfoRepository assetInfoRepository;
	
	@Autowired
	private OrgInfoRepository orgInfoRepository;
	
	private final Logger log = LoggerFactory.getLogger(CaptureDataMessagesService.class);
	private String content="";
	
//	public boolean checkSystemIdExists(CaptureDataMessages captureDM){
//		try {
//			AssetInfo info=assetInfoRepository.getBySystemIdAndIbStatus(captureDM.getSystemId());
//			captureDM.setAssetUID(info.getUid());
//			captureDM.setGeDeviceType(info.getGeDeviceType());
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//			log.error(e.getMessage());
//			return false;
//		}
//	}
	
	public AssetInfo getAssetInfoBySystemId(String systemId){
		AssetInfo info=assetInfoRepository.getBySystemId(systemId);
		return info;
	}
	
	public OrgInfo getOrgInfoById(Integer id){
		OrgInfo info=orgInfoRepository.findById(id);
		return info;
	}
	
}

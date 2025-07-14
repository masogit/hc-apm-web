package com.ge.apm.service.inventory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 503189824 on 2020/12/14.
 */
@Service
public class InventoryOrderApiService extends MicroServiceInvoker {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryOrderApiService.class);

	@Autowired
	OrgInfoRepository orgDao;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	//初始化创建工单科室列表
	public List<OrgInfo> onCreateInventory(){

		HttpEntity<JSONObject> requestEntity = new HttpEntity<>(createHttpHeaders(UserContextService.getAccessToken()));
		String url = getGatewayURL(GatewayURL.BEFORE_CREATE_INVENTORY_ORDER) + "?filterNoneAsset=false";
		ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.GET,requestEntity,LinkedHashMap.class);
		return JSONObject.parseObject(JSONObject.toJSONString(response.getBody().get("data")), new TypeReference<List<OrgInfo>>() {});
	}

	// 创建盘点工单
	public ResponseEntity<LinkedHashMap> doCreate(InventoryOrder selected, List<OrgInfo> selectedOrgs){
		Map map =new HashMap<String,Object>();
		map.put("planStartTime",format.format(selected.getPlanStartTime()));
		map.put("planEndTime",format.format(selected.getPlanEndTime()));

		List<Map> list = new ArrayList<>();
		selectedOrgs.stream().forEach(org -> {
			Map p =new HashMap<String,Object>();
			p.put("uid", org.getUid());
			p.put("orgName", org.getOrgName());
			p.put("id", org.getId());
			p.put("selected", true);
			p.put("assetType", org.getAssetType());
			list.add(p);
		});
		map.put("orgSelections", list);
		HttpEntity<Map> requestEntity = new HttpEntity<>(map,createHttpHeaders(UserContextService.getAccessToken()));
		String url = getGatewayURL(GatewayURL.SUMBIT_CREATE_INVENTORY_ORDER);
		return template.exchange(url,HttpMethod.POST,requestEntity,LinkedHashMap.class);
	}

	// 关闭盘点工单
	public ResponseEntity<LinkedHashMap> doFinishOrder(InventoryOrder order){
		Map map =new HashMap<String,String>();
		map.put("startTime",format.format(order.getStartTime()));
		map.put("endTime",format.format(order.getEndTime()));
		HttpEntity<Map> requestEntity = new HttpEntity<>(map,createHttpHeaders(UserContextService.getAccessToken()));
		String url = getGatewayURL(GatewayURL.FINISH_INVENTORY_ORDER).replace("{orderId}", order.getId());;
		return template.exchange(url,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
	}

	//组织机构名称拼接
	public String getNameWithParent(OrgInfo org){
		if(org.getParentUID() == null){
			return org.getName();
		}
		String pName = orgDao.findNameByUID(org.getParentUID());
		return pName==null ? org.getName() : pName + " / " + org.getName();
	}

}

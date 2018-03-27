package com.ge.apm.service.pm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.InventoryOrderDetailRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.InventoryOrderDetail;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class InventoryOrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryOrderService.class);


	@Autowired
	private InventoryOrderRepository inventoryOrderRepository;
	
	@Autowired
	private InventoryOrderDetailRepository inventoryOrderDetailRepository;
	
	@Autowired
	private BlobObjectRepository blobObjectRepository;
	@Autowired
	private AssetInfoRepository assetInfoRepository;
	
	public List<InventoryOrder>findSomeTestRecords(){
		System.out.println("repository findSomeTestRecords");
//		List<V2_BlobObject> listBlob=blobObjectRepository.findSomeTestRecords();
//		System.out.println(listBlob);
//		if(listBlob!=null){
//			System.out.println("listBolob.size="+listBlob.size());
//		}
		AssetInfo assetInfo=assetInfoRepository.findTestRecord();
		List<InventoryOrderDetail> listDetail=inventoryOrderDetailRepository.findSomeTestRecords();
		System.out.println(listDetail);
		if(listDetail!=null){
			System.out.println("listDetail.size="+listDetail.size());
		}
		List<InventoryOrder> listOrder=inventoryOrderRepository.findSomeTestRecords();
		System.out.println(listOrder);
		if(listOrder!=null){
			System.out.println("listOrder.size="+listOrder.size());
		}
		
		for(InventoryOrderDetail detail:listDetail){
			System.out.println("xxx");
			System.out.println(detail.getId());
			detail.setAssetInfo(assetInfo);
		}
		for(InventoryOrder order:listOrder){
			System.out.println("xxx2");
			System.out.println(order.getId());
			order.setInventoryOrderDetailList(listDetail);
		}
		return listOrder;
	}
}

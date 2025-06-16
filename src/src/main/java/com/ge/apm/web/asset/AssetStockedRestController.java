package com.ge.apm.web.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInStockRecordRepository;
import com.ge.apm.dao.AssetOutStockRecordRepository;
import com.ge.apm.dao.AssetStockedRepository;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assetStocked")
public class AssetStockedRestController {
	public static  final Logger logger = LoggerFactory.getLogger(AssetStockedRestController.class);

    @Autowired
    private AssetStockedRepository assetStockedRepository;
	@Autowired
	private ArrivalListItemRepository arrivalListItemRepository;
	@Autowired
	private AssetInStockRecordRepository assetInStockRecordRepository;
	@Autowired
	private AssetOutStockRecordRepository assetOutStockRecordRepository;

	@RequestMapping(value = "/initData", method = RequestMethod.GET)
	public String initData() {
		List<AssetStocked> stockedList = ListUtil.newArrayList();
		List<AssetInStockRecord> inStockRecords = ListUtil.newArrayList();
		List<AssetOutStockRecord> outStockRecords = ListUtil.newArrayList();
		try {
			List<String> assetUidList = assetStockedRepository.find().stream().map(AssetStocked::getAssetUid).distinct().collect(Collectors.toList());
			List<SearchFilter> arrivalItemSearchList = ListUtil.newArrayList();
			arrivalItemSearchList.add(new SearchFilter("stockStatus", SearchFilter.Operator.IsNotNull, null, "1*"));
			arrivalItemSearchList.add(new SearchFilter("assetUID", SearchFilter.Operator.NotIN, assetUidList, "1*"));
			List<ArrivalListItem> itemList = arrivalListItemRepository.findBySearchFilter(arrivalItemSearchList);
			for (ArrivalListItem arrivalListItem : itemList) {
				AssetStocked assetStocked = new AssetStocked();
				BeanUtils.copyProperties(arrivalListItem, assetStocked);
				assetStocked.setId(null);
				assetStocked.setAssetUid(arrivalListItem.getAssetUID());
				assetStocked.setArrivalItemId(arrivalListItem.getId());
				if (arrivalListItem.getStockStatus() == 1) {
					assetStocked.setStockStatus(2);
				}
				stockedList.add(assetStocked);
				ArrivalList arrivalList = arrivalListItem.getArrivalList();
				if (ObjectUtil.isNotEmpty(arrivalListItem.getInStockNo())) {
					AssetInStockRecord assetInStockRecord = new AssetInStockRecord();
					BeanUtils.copyProperties(arrivalListItem, assetInStockRecord);
					assetInStockRecord.setId(null);
					assetInStockRecord.setAssetUid(arrivalListItem.getAssetUID());
					assetInStockRecord.setInStockNo(arrivalListItem.getInStockNo());
					assetInStockRecord.setTenantUid(arrivalList.getTenantUID());
					assetInStockRecord.setInstitutionUid(arrivalList.getInstitutionUID());
					assetInStockRecord.setHospitalUid(arrivalList.getHospitalUID());
					assetInStockRecord.setSiteUid(arrivalList.getSiteUID());
					assetInStockRecord.setArrivalItemId(arrivalListItem.getId());
					assetInStockRecord.setBusinessDate(arrivalListItem.getInStockBusinessDate());
					assetInStockRecord.setVendor(arrivalList.getVendor());
					assetInStockRecord.setInStockUser(arrivalListItem.getInStockUser());
					assetInStockRecord.setFinancingNum(arrivalListItem.getFinancingNum());
					assetInStockRecord.setAssetName(arrivalList.getAssetName());
					assetInStockRecord.setContractNum(arrivalList.getContractNum());
					assetInStockRecord.setInstallDate(arrivalListItem.getInstallDate());
					assetInStockRecord.setFunctionType(arrivalListItem.getFunctionType());
					assetInStockRecord.setPurchasePrice(arrivalListItem.getPurchasePrice());
					assetInStockRecord.setInvoiceNum(arrivalList.getInvoiceNum());
					assetInStockRecord.setInvoiceDate(arrivalList.getInvoiceDate());
					inStockRecords.add(assetInStockRecord);
				}
				if (ObjectUtil.isNotEmpty(arrivalListItem.getOutStockNo())) {
					AssetOutStockRecord assetOutStockRecord = new AssetOutStockRecord();
					BeanUtils.copyProperties(arrivalListItem, assetOutStockRecord);
					assetOutStockRecord.setId(null);
					assetOutStockRecord.setAssetUid(arrivalListItem.getAssetUID());
					assetOutStockRecord.setOutStockNo(arrivalListItem.getOutStockNo());
					assetOutStockRecord.setTenantUid(arrivalList.getTenantUID());
					assetOutStockRecord.setInstitutionUid(arrivalList.getInstitutionUID());
					assetOutStockRecord.setHospitalUid(arrivalList.getHospitalUID());
					assetOutStockRecord.setSiteUid(arrivalList.getSiteUID());
					assetOutStockRecord.setArrivalItemId(arrivalListItem.getId());
					assetOutStockRecord.setBusinessDate(arrivalListItem.getOutStockBusinessDate());
					assetOutStockRecord.setVendor(arrivalList.getVendor());
					assetOutStockRecord.setOutStockUser(arrivalListItem.getOutStockUser());
					assetOutStockRecord.setFinancingNum(arrivalListItem.getFinancingNum());
					assetOutStockRecord.setAssetName(arrivalList.getAssetName());
					assetOutStockRecord.setContractNum(arrivalList.getContractNum());
					assetOutStockRecord.setInstallDate(arrivalListItem.getInstallDate());
					assetOutStockRecord.setFunctionType(arrivalListItem.getFunctionType());
					assetOutStockRecord.setPurchasePrice(arrivalListItem.getPurchasePrice());
					assetOutStockRecord.setInvoiceNum(arrivalList.getInvoiceNum());
					assetOutStockRecord.setInvoiceDate(arrivalList.getInvoiceDate());
					assetOutStockRecord.setRecruiteDeptUid(arrivalListItem.getRecruiteDeptUid());
					assetOutStockRecord.setStockStatus(4);
					outStockRecords.add(assetOutStockRecord);
				}

			}
			assetStockedRepository.save(stockedList);
			assetInStockRecordRepository.save(inStockRecords);
			assetOutStockRecordRepository.save(outStockRecords);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping(value = "/removeData", method = RequestMethod.GET)
	public String removeData() {
		try {
			List<String> assetUidList = assetStockedRepository.find().stream().map(AssetStocked::getAssetUid).distinct().collect(Collectors.toList());
			List<SearchFilter> arrivalItemSearchList = ListUtil.newArrayList();
			arrivalItemSearchList.add(new SearchFilter("stockStatus", SearchFilter.Operator.IsNotNull, null, "1*"));
			arrivalItemSearchList.add(new SearchFilter("assetUID", SearchFilter.Operator.NotIN, assetUidList, "1*"));
			List<ArrivalListItem> itemList = arrivalListItemRepository.findBySearchFilter(arrivalItemSearchList);
			List<String> arrivalItemIds = itemList.stream().map(ArrivalListItem::getId).collect(Collectors.toList());
			assetStockedRepository.deleteByArrivalItemIdIn(arrivalItemIds);
			assetInStockRecordRepository.deleteByArrivalItemIdIn(arrivalItemIds);
			assetOutStockRecordRepository.deleteByArrivalItemIdIn(arrivalItemIds);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "success";
	}

}

package com.ge.apm.service.zkb;


import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2022/8/1
 * @description :
 */
@Service
public class ZkbAssetQrCodeFromIbService {

    String jsonData = "[{\"createdBy\":null,\"createdDate\":\"2020-11-09T14:14:35.544+08:00\",\"tenantUID\":\"26acd70242fd42f8b807cfcbf24a1877\",\"institutionUID\":\"4af07216814b4003922405d1b3c25885\",\"hospitalUID\":\"3a75b4bdf87b4a94aa934c0f2f99baef\",\"siteUID\":\"3a75b4bdf87b4a94aa934c0f2f99baef\",\"id\":555128,\"siteId\":46,\"name\":\"LOGIQ E8\",\"namePyCode\":\"logiqe8\",\"aliasName\":null,\"functionGroup\":null,\"functionType\":null,\"functionGrade\":null,\"manufacture\":null,\"manufactureId\":null,\"manufacturePyCode\":null,\"vendor\":null,\"supplierId\":null,\"supplier\":null,\"supplierPyCode\":null,\"maitananceId\":null,\"maitanance\":null,\"maitananceTel\":null,\"nationalStandardCode\":null,\"serialNum\":null,\"departNum\":\"082436070430\",\"financingNum\":null,\"barcode\":null,\"modalityId\":null,\"locationCode\":null,\"locationName\":null,\"locationNotes\":null,\"nurseLocation\":null,\"clinicalDeptId\":77661,\"clinicalDeptUID\":\"36a711dc850149629a7e89eb1b8a50d4\",\"clinicalDeptName\":\"唐山市丰润区人民医院_超声\",\"clinicalDeptNamePyCode\":\"tssfrqrmyycs\",\"assetGroup\":2,\"assetClass\":null,\"assetClassName\":null,\"assetGroupSecondary\":null,\"assetGroupName\":\"超声类\",\"assetDeptId\":77661,\"assetDeptUID\":\"36a711dc850149629a7e89eb1b8a50d4\",\"assetDeptName\":\"唐山市丰润区人民医院_超声\",\"assetOwnerId\":null,\"assetOwnerName\":null,\"assetOwnerTel\":null,\"isValid\":true,\"isCleaned\":null,\"status\":1,\"offlineUpdateDate\":null,\"manufactDate\":null,\"purchaseDate\":null,\"arriveDate\":null,\"installDate\":null,\"warrantyDate\":null,\"acceptDate\":null,\"warrantyMonth\":null,\"terminateDate\":null,\"lastPmDate\":null,\"lastMeasuringDate\":null,\"lastMeteringDate\":null,\"lastQaDate\":null,\"lastStockTakeDate\":null,\"clinicalOwnerId\":null,\"clinicalOwnerName\":null,\"clinicalOwnerTel\":null,\"registrationNo\":null,\"factoryWarrantyDate\":null,\"qrCode\":\"2011095844930630\",\"oneQrCode\":\"test\",\"assetOwnerId2\":null,\"assetOwnerName2\":null,\"assetOwnerTel2\":null,\"assetOwnerId3\":null,\"assetOwnerName3\":null,\"assetOwnerTel3\":null,\"feUserId\":null,\"dispatchMode\":null,\"purchasePrice\":null,\"medicalApparatusClassification\":null,\"salvageValue\":null,\"lifecycle\":null,\"depreciationMethod\":null,\"deviceName\":null,\"makerShortName\":null,\"receiptNumber\":null,\"productPlace\":null,\"guarantee\":null,\"annualMaintenanceTimes\":null,\"eamId\":null,\"systemId\":\"082436070430\",\"systemNum1\":null,\"systemNum2\":null,\"systemNum3\":null,\"systemNum4\":null,\"systemNum5\":null,\"isDeleted\":false,\"deviceStatus\":0,\"parentAssetId\":null,\"subAssets\":[],\"parentAssetSystemId\":null,\"parentAssetName\":null,\"inventoryStatus\":null,\"inventoryOwnerId\":null,\"inventoryTime\":null,\"assetCode68\":null,\"comments\":null,\"startUsingDate\":null,\"proprietaryClass\":null,\"subjectProject\":null,\"attachments\":[],\"hospitalId\":77660,\"address\":\"河北省唐山市丰润区曹雪芹西大街456号\",\"hospitalName\":null,\"authGroupUid\":null,\"isMvsTant\":false,\"tenantName\":\"GE后台运维\",\"institutionName\":\"唐山市丰润区人民医院\",\"hospitalNameV2\":\"唐山市丰润区人民医院\",\"siteName\":\"唐山市丰润区人民医院\",\"uid\":\"14dded8533ae4a9aa3742dccdce767a8\",\"orgPath\":null,\"assetCategory\":null,\"assetUsage\":null,\"capitalSource\":null,\"capitalSource2\":null,\"riskGrade\":null,\"measureGrade\":null,\"emergencyEquipment\":null,\"importEquipment\":null,\"measuringItemCategory\":null,\"meteringCycle\":null,\"usageStatus\":null,\"accountingCategory\":null,\"nextMeteringDate\":null,\"repairForm\":2,\"ibStatus\":null,\"geDeviceType\":3,\"swapType\":null,\"sharedAssetType\":null,\"sharable\":null,\"isWarranty\":null,\"isPm\":null,\"isFixed\":null,\"isChecked\":null,\"isOpenTube\":null,\"assetValueCategory\":null,\"pmOwnerId\":null,\"pmOwnerName\":null,\"pmOwnerTel\":null,\"mvsEquip\":null,\"enableLossOfCommunicationMonitoring\":null,\"locDurationDeviceStatus\":null,\"locDurationEnvMonitoring\":null,\"hasOpenAlert\":null,\"measuringOwnerId\":null,\"measuringOwnerName\":null,\"measuringOwnerTel\":null,\"ucmid\":\"HB00663\",\"connectivity\":null,\"connectToInsite\":3,\"ibDeviceStatus\":5,\"ownerDeptCode\":null,\"useDeptCode\":null,\"measureDevice\":null,\"hasRegistrationNo\":null,\"abnormalFeedback\":null,\"abnormalReason\":null,\"mainDirectory\":null,\"rfId\":null,\"rfGroup\":null,\"currentId\":null,\"secondDirectory\":null,\"supplierNewUID\":null,\"runningStatus\":null,\"tags\":[],\"iwicMonthlyReport\":[],\"owocReportData\":[],\"usReportHospital\":[],\"usReportDept\":[],\"diQuarterlyReport\":[],\"usQuarterlyReport\":[],\"ibActivity\":null,\"visibleStatus\":0,\"installStatus\":0,\"sharedStatus\":null,\"assetServiceMembers\":null,\"assetTags\":null,\"medicalApparatusClassName\":null,\"geibTable\":{\"id\":29207,\"systemId\":\"082436070430\",\"psiDescription\":\"LOGIQ E8\",\"mod1\":\"US\",\"eqSegment\":\"1_Premium\",\"status\":\"Installed\",\"assetStatus\":\"OC\",\"installedTime\":\"2016-01-17T16:00:00.000+0000\",\"serviceRegion\":\"N_HB_CN_SR\",\"province\":\"河北\",\"countyDistrict\":\"丰润区\",\"city\":\"唐山\",\"hospitalName\":\"唐山市丰润区人民医院\",\"address\":\"河北省唐山市丰润区曹雪芹西大街456号\",\"ucmId\":\"HB00663\",\"accountSegment1\":\"2_NKA\",\"warrantyStartDate\":\"2015-12-21T16:00:00.000+0000\",\"warrantyEndDate\":\"2016-12-19T16:00:00.000+0000\",\"expertName\":\"王丰\",\"expertSSO\":\"212733471\",\"zoneManagerName\":\"王冰\",\"zoneManagerSSO\":\"305007584\",\"saleName\":\"边曌\",\"saleSSO\":\"212605277\",\"saleManagerName\":\"王宝琳\",\"saleManagerSSO\":\"305023692\",\"geibContract\":null,\"zone\":\"TJ/HB\",\"ibType\":\"Main system\",\"productSegment\":\"GI\",\"assetNumber\":\"GIB_S_CN_082436070430\",\"modality\":\"Ultrasound\",\"mod2\":\"US\",\"productSeries\":null,\"shippedDate\":null,\"geDeviceType\":\"3\",\"region\":\"N\",\"salesRegion\":\"ULS NKA\",\"feChannel\":\"GE FE\",\"winback\":\"\",\"accountSegmentFirstLevel\":\"2_NKA\",\"accountSegmentSecondLevel\":\"\",\"psiCode\":\"ULOE8A\",\"vipSite\":\"\",\"vipLeaderSso\":\"\",\"vipLeaderName\":\"\",\"enterprise\":\"\",\"enterpriseVip\":\"\",\"enterpriseVipEntNames\":null,\"enterpriseVipLeaderSso\":\"\",\"enterpriseVipLeaderName\":\"\",\"systemCoverageleveLwarrantyc\":\"HBS\",\"cStart\":\"2020-11-05T16:00:00.000+0000\",\"cEnd\":\"2021-11-04T16:00:00.000+0000\",\"salesPnl\":\"L2/3\",\"firstOwnerIWICY\":null,\"firstOwnerIWICN\":null,\"firstOwnerOWOCY\":null,\"firstOwnerOWOCN\":null,\"firstOwnerIWICYSSO\":null,\"firstOwnerIWICNSSO\":null,\"firstOwnerOWOCYSSO\":null,\"firstOwnerOWOCNSSO\":null,\"secondOwnerIWICY\":null,\"secondOwnerIWICN\":null,\"secondOwnerOWOCY\":null,\"secondOwnerOWOCN\":null,\"secondOwnerIWICYSSO\":null,\"secondOwnerIWICNSSO\":null,\"secondOwnerOWOCYSSO\":null,\"secondOwnerOWOCNSSO\":null,\"salesRegionMgr\":\"刘伟\",\"salesRegionMgrSSO\":\"305001781\",\"feHeadName\":\"张章\",\"feHeadSSO\":\"305001795\",\"ibStatusPresent\":\"OC\",\"serviceableIb\":\"Serviceable IB\",\"contractType1\":\"ZKB\",\"area\":\"上海圈\",\"areaLeaderSso\":\"222220045\",\"areaLeaderName\":\"测试全量\",\"salesOrder\":null,\"productName\":\"LOGIQ E8\"},\"tubeWatchStatus\":null,\"tubeWatchCondition\":\"3\",\"empowermentStatus\":null,\"accessable\":true,\"showQrcodeIcon\":1,\"lifecycleInYear\":null,\"userFavorites\":null,\"assetGroupStr\":\"2\"}]";




    public void handleQRCodeFromIb(){
        List<HashMap> assetInfoList = (List<HashMap>) new Gson().fromJson(jsonData, HashMap.class);
        handleIbAssetData(assetInfoList);
    }


    @Transactional(rollbackFor = Exception.class)
    public void handleIbAssetData(List<HashMap> assetInfoList){
        int udFlag = 0;
        boolean ibQrCodeStatus = false;
        Integer installStatus = 0;
        for(HashMap a : assetInfoList){
            Boolean isDelete = a.get("isDeleted") == null ? false : (Boolean)a.get("isDeleted");
            if(!isDelete){
                //已贴码
                if(a.get("oneQrCode") != null && StringUtils.isNotEmpty(a.get("oneQrCode").toString())){
                    ibQrCodeStatus = true;
                }
                HashMap geIbTable = a.get("geibTable") ==null ? new HashMap() : (HashMap)a.get("geibTable");
                //已拆机
                if(geIbTable.get("status") != null && "Deinstalled".equals(geIbTable.get("status").toString())){
                    installStatus = 2;
                }
                udFlag++;
            }
        }
        if(udFlag == 0){
            for(HashMap a : assetInfoList){
                Boolean isDelete = a.get("isDeleted") == null ? false : (Boolean)a.get("isDeleted");
                if(isDelete){
                    HashMap geIbTable = a.get("geibTable") ==null ? new HashMap() : (HashMap)a.get("geibTable");
                    //已拆机
                    if(geIbTable.get("status") != null && "Deinstalled".equals(geIbTable.get("status").toString())){
                        installStatus = 2;
                    }
                }
            }
        }
    }

}

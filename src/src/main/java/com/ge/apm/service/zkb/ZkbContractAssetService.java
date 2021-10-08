package com.ge.apm.service.zkb;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbContractAsset;
import com.ge.apm.domain.ZkbFePartOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/9/26
 * @description :
 */
@Service
public class ZkbContractAssetService {

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;


    public Boolean checkHalfAsset(String contractId,String assetId){
        Boolean checkHalfAsset = null;
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(contractId,assetId);
        if(zkbContractAsset == null){
            return checkHalfAsset;
        }
        Integer applyNum = zkbContractAsset.getApplyNum();
        if(applyNum == null || applyNum == 0){
            long total = 0;
            long highCount = 0;

            total = zkbContractAssetRepository.countByContractIdAndAssetType(contractId,"highMachine");
            highCount = zkbContractAssetRepository.countByApplyNum(contractId,"highMachine");

            if(total == 0){
                return checkHalfAsset;
            }
            double d = total * 0.5 ;
            if((int)d != d){
                d= (int) d;
            }
            if(highCount < d){
                checkHalfAsset = true;
            }else{
                checkHalfAsset = false;
            }
        }else if(applyNum > 0){
            checkHalfAsset = true;
        }
        return checkHalfAsset;
    }

    public void addContractAssetApplyNum(String contractId,String assetId){
        zkbContractAssetRepository.addContractAssetApplyNum(contractId,assetId,"highMachine");
    }

    public void subtractContractAssetApplyNum(String contractId,String assetId){
        zkbContractAssetRepository.subtractContractAssetApplyNum(contractId,assetId,"highMachine");
    }

    /**功能：合同实际有效期、计数年校验（人工、备件和探头申请进行校验）
     * 注意：高端机备件申请时合同计数年第一年进行总数的50%验证，其他同人工、探头。
     * @param zkbContract
     * @param zkbFePartOrder
     * @param assetUid 高端机备件申请时 不能为空
     * @return
     */
    public boolean checkFiveNineNineContract(ZkbContract zkbContract, ZkbFePartOrder zkbFePartOrder, String assetUid) {
        boolean flag = true;
        String messageKey = null;
        if("Y".equals(zkbContract.getFiveNineNineFlag())) {
            Integer yearCount = zkbContract.getYearCount();
            if(yearCount == null || yearCount < 1){
                messageKey = "zkbFePartOrder_599contractYearCountError";
                return false;
            }
            String format = "yyyy-MM-dd";
            Date reportDate = zkbFePartOrder.getReportDate();
            if(reportDate == null){
                reportDate = new Date();
            }
            String dateStr = DateUtil.getFormatStrByDate(reportDate, format);
            Date reportDatetimp = DateUtil.getDateByFormatStr(dateStr, format);
            if (reportDatetimp.compareTo(zkbContract.getStartDate()) < 0 || reportDatetimp.compareTo(zkbContract.getEndDate()) > 0) {
//                messageKey = "zkbFePartOrder_contractValidPeriodError";
                messageKey = "zkbFePartOrder_599contractSupportTimesCannotBeZero";
                flag = false;
            }else if(ZkbFePartOrderService.highArtificialTypeValue.equals(zkbFePartOrder.getArtificialType()) &&
                    ZkbFePartOrder.OrderType.SpareParts.toString().equals(zkbFePartOrder.getOrderType()) && yearCount == 1){
                //高端机备件申请时第一年进行总数的50%验证
                if(assetUid != null){
                    Boolean halfAsset = checkHalfAsset(zkbContract.getId(),assetUid);
                    if(null == halfAsset){
                        messageKey = "zkbFePartOrder_599contractHighMachineNull";
                        flag = false;
                    }else if(!halfAsset){
                        messageKey = "zkbFePartOrder_599contractSupportTimesCannotBeZero";
                        flag = false;
                    }
                }
            }
        }
        if(!flag){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, WebUtil.getMessage(messageKey), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return flag;
    }
}

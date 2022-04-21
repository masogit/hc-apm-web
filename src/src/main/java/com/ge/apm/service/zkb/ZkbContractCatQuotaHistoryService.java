package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.dao.ZkbContractCatQuotaHistoryRepository;
import com.ge.apm.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.service.UserContext;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @autor by lyj 503277717
 * @date 2022/4/2
 */

@Component
public class ZkbContractCatQuotaHistoryService {

    @Autowired
    ZkbContractCatQuotaHistoryRepository zkbContractCatQuotaHistoryRepository;

    public static final String OLD_PROBE = "OldProbe";

    public static void main(String[] args) {
        int probeQuotaTotal = 0% 2 == 0 ? 0 :9+1;

    }


    public void insertZkbFeContractCatQuotaHistory(ZkbContract zct, ZkbFePartOrder zkbFePartOrder, UserAccount currentUser){
        ZkbContractCatQuotaHistory zkbContractCatQuotaHistory = new ZkbContractCatQuotaHistory();
        zkbContractCatQuotaHistory.setCreatedBy(currentUser.getUid());
        zkbContractCatQuotaHistory.setCreatedDate(new Date());
        zkbContractCatQuotaHistory.setLastModifiedBy(currentUser.getUid());
        zkbContractCatQuotaHistory.setLastModifiedDate(new Date());
        zkbContractCatQuotaHistory.setContractNo(zct.getContractNo());
        zkbContractCatQuotaHistory.setContractId(zct.getId());
        zkbContractCatQuotaHistory.setPartOrderId(zkbFePartOrder.getId());
        zkbContractCatQuotaHistory.setPartOrderNo(zkbFePartOrder.getPartOrderNo());
        zkbContractCatQuotaHistory.setQty(zkbFePartOrder.getQty());
        zkbContractCatQuotaHistory.setAssetType(zkbFePartOrder.getId());
        //高值扣普通  FourInOne 扣普通
        boolean logistic = ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zct.getQuotaDeductType())?true:false;
        if(logistic){
            if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                zkbContractCatQuotaHistory.setProbeType(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString());
            }else {
                zkbContractCatQuotaHistory.setProbeType(zkbFePartOrder.getZkbPoProbeQuotaType());
            };
        }else{
            zkbContractCatQuotaHistory.setProbeType(OLD_PROBE);
        }
        String assetType = zkbFePartOrder.getAssetType();
        if(StringUtils.isBlank(zkbFePartOrder.getAssetType())){
            Set<ZkbContractAsset> zkbContractAsset = zct.getZkbContractAssets().stream().filter(ass -> {
                if(ass.getAssetInfo()!=null){
                    return ass.getAssetInfo().getSystemId().equals(zkbFePartOrder.getSystemId());
                }
                return false;
            }).collect(Collectors.toSet());
            if(!zkbContractAsset.isEmpty()){
                assetType = zkbContractAsset.iterator().next().getAssetType();
            }
        }
        zkbContractCatQuotaHistory.setAssetType(assetType);
        zkbContractCatQuotaHistoryRepository.save(zkbContractCatQuotaHistory);
    }
    public void updateZkbFeContractCatQuotaHistoryStatusOrQty(ZkbContract zct, ZkbFePartOrder zkbFePartOrder, UserAccount currentUser,String status,Integer qty){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("contractNo", SearchFilter.Operator.EQ,zct.getContractNo()));
        filters.add(new SearchFilter("partOrderNo", SearchFilter.Operator.EQ,zkbFePartOrder.getPartOrderNo()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Y"));
        String assetType = zkbFePartOrder.getAssetType();
        if(StringUtils.isBlank(zkbFePartOrder.getAssetType())){
            Set<ZkbContractAsset> zkbContractAsset = zct.getZkbContractAssets().stream().filter(ass -> {
                if(ass.getAssetInfo()!=null){
                    return ass.getAssetInfo().getSystemId().equals(zkbFePartOrder.getSystemId());
                }
                return false;
            }).collect(Collectors.toSet());
            if(!zkbContractAsset.isEmpty()){
                assetType = zkbContractAsset.iterator().next().getAssetType();
            }
        }
        filters.add(new SearchFilter("assetType", SearchFilter.Operator.EQ,assetType));
        List<ZkbContractCatQuotaHistory> zkbContractCatQuotaHistoryList = zkbContractCatQuotaHistoryRepository.findBySearchFilter(filters);
        zkbContractCatQuotaHistoryList.stream().forEach(history ->{
            if(StringUtils.isNotBlank(status))
                history.setStatus(status);
            if(qty!=null)
                history.setQty(qty);
            history.setLastModifiedDate(new Date());
            history.setLastModifiedBy(currentUser.getUid());
        });
        zkbContractCatQuotaHistoryRepository.save(zkbContractCatQuotaHistoryList);
    }



    public Integer handleQty(ZkbContract zkbContract,ZkbFePartOrder zkbFePartOrder){
        Integer qty = new Integer(0);
        String assetType = new String ();
        Integer zkbContractQtyTotal = new Integer(0);
        boolean logistic = ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())?true:false;
        //TODO liyonjian ZKB - 2022年新探头逻辑
        if(logistic){
            if(ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                zkbContractQtyTotal=zkbContract.getProbeQuotaTotal();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                zkbContractQtyTotal=zkbContract.getProbeQuotaSpecialTotal();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                zkbContractQtyTotal=zkbContract.getProbeQuotaNormalTotal();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                zkbContractQtyTotal=zkbContract.getProbeQuotaNormalTotal();
            }
        }else{
            zkbContractQtyTotal=zkbContract.getProbeQuotaTotal();
        }
        int highMachine = (zkbContractQtyTotal % 2 == 0 ? zkbContractQtyTotal :zkbContractQtyTotal-1)/2;
        int lowMachine = (zkbContractQtyTotal % 2 == 0 ? zkbContractQtyTotal :zkbContractQtyTotal+1)/2;
        Set<ZkbContractAsset> zkbContractAsset = zkbContract.getZkbContractAssets().stream().filter(ass -> {
            if(ass.getAssetInfo()!=null){
                return ass.getAssetInfo().getSystemId().equals(zkbFePartOrder.getSystemId());
            }
            return false;
        }).collect(Collectors.toSet());
        if(!zkbContractAsset.isEmpty()){
            assetType = zkbContractAsset.iterator().next().getAssetType();
        }
        List<ZkbContractCatQuotaHistory> zkbContractCatQuotaHistoryList =   this.zkbContractCatQuotaHistoryRepository.queryZkbContractCatQuotaHistory(zkbContract.getId(),"Y");
        int highMachineUsed = zkbContractCatQuotaHistoryList.stream().filter(z -> this.checkFilter(logistic,"highMachine",z,zkbFePartOrder)).mapToInt(ZkbContractCatQuotaHistory::getQty).sum();
        int lowMachineUsed = zkbContractCatQuotaHistoryList.stream().filter(z -> this.checkFilter(logistic,"lowMachine",z,zkbFePartOrder)).mapToInt(ZkbContractCatQuotaHistory::getQty).sum();
        if("lowMachine".equals(assetType)){
            qty =  lowMachine - lowMachineUsed;
        }else if ("highMachine".equals(assetType)){
            qty = highMachine - highMachineUsed;
        }
        return qty;
    }

    public boolean checkFilter(boolean logistic,String assetType,ZkbContractCatQuotaHistory z,ZkbFePartOrder zkbFePartOrder){
        boolean returnArg = false;
        if(logistic){
            if(ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                returnArg = assetType.equals(z.getAssetType())&&ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(z.getProbeType());
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                returnArg = assetType.equals(z.getAssetType())&&ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(z.getProbeType());
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                returnArg = assetType.equals(z.getAssetType())&&ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(z.getProbeType());
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(zkbFePartOrder.getZkbPoProbeQuotaType())){
                returnArg = assetType.equals(z.getAssetType())&&ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(z.getProbeType());
            }
        }else{
            returnArg = assetType.equals(z.getAssetType());
        }
        return returnArg;

    }













}

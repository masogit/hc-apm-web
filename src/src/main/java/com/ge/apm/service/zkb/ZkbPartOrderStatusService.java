package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/10/24
 * @description :
 */
@Service
public class ZkbPartOrderStatusService {

    @Autowired
    ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Autowired
    ZkbContractRepository zkbContractRepository;

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    ZkbPartOrderService zkbPartOrderService;

    @Autowired
    @Getter@Setter
    ZkbPartOrderCloseWorkTimeRepository zkbPartOrderCloseWorkTimeRepository;

    @Autowired
    private I18nMessageRepository i18nDao;

    public void fieldRendered(UserAccount currentUser, ZkbPartOrderStatusVo zkbPartOrderStatusVo, ZkbPartOrder selected, ZkbPartOrderCloseInfo zkbPartOrderCloseInfo){
        if(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                zkbPartOrderStatusVo.setCanEditFlag(true);
            }else{
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                    List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                    if(zkbPartOrderUserConfigs.size() > 0){
                        boolean matchAny = zkbPartOrderUserConfigs.stream().anyMatch(t -> currentUser.getLoginName().equals(t.getLoginName()));
                        if(matchAny){
                            zkbPartOrderStatusVo.setCanEditFlag(true);
                        }
                    }
                }
            }
        }else if(ZkbPartOrderStep.PartOrderStep.CompletePending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                zkbPartOrderStatusVo.setCanEditFlag(true);
            }
        }else{
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<>();
            if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndRegion(selected.getCurrentStep(),selected.getRegion());
            }else if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(selected.getCurrentStep())){
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 2){
                        ZkbPartOrderUserConfig zkbPartOrderUserConfig = selected.getOnlineEngine();
                        if(zkbPartOrderUserConfig != null){
                            zkbPartOrderUserConfigs.add(zkbPartOrderUserConfig);
                        }
                    }else if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 1){
                        zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType(selected.getCurrentStep(),selected.getAssetTypeDi());
                    }
                }
            }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndOrderType(selected.getCurrentStep(),selected.getOrderType());
                }else{
                    zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndOrderTypeNull(selected.getCurrentStep());
                }
            }else if(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString().equals(selected.getCurrentStep())){
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
            }else {
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(selected.getCurrentStep());
            }
            if(zkbPartOrderUserConfigs != null && zkbPartOrderUserConfigs.size() > 0){
                for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigs){
                    if(currentUser.getLoginName().equals(c.getLoginName())){
                        zkbPartOrderStatusVo.setCanEditFlag(true);
                    }
                }
            }
        }

        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            //region 人工页面初始化
            zkbPartOrderStatusVo.setShowTech(false);
            //超声高端
            if("2".equals(selected.getArtificialType())){
                // 申请并有权限
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowManage(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    } else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }else{
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }
                    zkbPartOrderStatusVo.setShowCommercial(true);
                    if(zkbPartOrderStatusVo.isCanEditFlag()){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }
                    zkbPartOrderStatusVo.setShowCommercial(true);
                    zkbPartOrderStatusVo.setShowOrder(false);
                    zkbPartOrderStatusVo.setShowBtn("");
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                    }
                    zkbPartOrderStatusVo.setShowOrder(true);
                    zkbPartOrderStatusVo.setShowBtn("");
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                    }
                    zkbPartOrderStatusVo.setShowRejectOrder(true);
                    zkbPartOrderStatusVo.setShowBtn("");
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    zkbPartOrderStatusVo.setShowManage(true);
                    if(zkbPartOrderStatusVo.isCanEditFlag()){
                        if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                            zkbPartOrderStatusVo.setShowCommercial(true);
                            zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                        }else{
                            zkbPartOrderStatusVo.setShowOrder(true);
                            zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                        }
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    zkbPartOrderStatusVo.setShowManage(true);
                    zkbPartOrderStatusVo.setShowBtn("");
                }
            }else{
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                }else if(ZkbPartOrder.PartOrderStatus.ProductAccept.toString().equals(selected.getStatus())){
                    zkbPartOrderStatusVo.setShowProduct(true);
                    zkbPartOrderStatusVo.setShowManage(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                }else if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(selected.getStatus())){
                    zkbPartOrderStatusVo.setShowProduct(true);
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    if(zkbPartOrderStatusVo.isCanEditFlag()){
                        if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                            zkbPartOrderStatusVo.setShowCommercial(true);
                            zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                        }else{
                            zkbPartOrderStatusVo.setShowOrder(true);
                            zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                        }
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    zkbPartOrderStatusVo.setShowBtn("");;
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    zkbPartOrderStatusVo.setShowCommercial(true);
                    if(zkbPartOrderStatusVo.isCanEditFlag()){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    zkbPartOrderStatusVo.setShowCommercial(true);
                    zkbPartOrderStatusVo.setShowBtn("");;
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                    }
                    zkbPartOrderStatusVo.setShowOrder(true);
                    zkbPartOrderStatusVo.setShowBtn("");;
                }else if(ZkbPartOrder.PartOrderStatus.FourHundredCancel.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                    }
                    zkbPartOrderStatusVo.setShowOrder(true);
                    zkbPartOrderStatusVo.setShowBtn("");;
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        zkbPartOrderStatusVo.setShowProduct(true);
                    }
                    zkbPartOrderStatusVo.setShowManage(true);
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                    }
                    zkbPartOrderStatusVo.setShowRejectOrder(true);
                    zkbPartOrderStatusVo.setShowBtn("");;
                }
            }
            //endregion
        }else{
            if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && zkbPartOrderStatusVo.isCanEditFlag()){
                if(StrUtil.isNotEmpty(selected.getNotGeAsset()) && CopyByInvok.T.equals(selected.getNotGeAsset())){
                    if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowOssc(true);
                    }else{
                        zkbPartOrderStatusVo.setShowTse(true);
                    }
                }else{
                    if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowManage(true);
                    }else{
                        if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                            zkbPartOrderStatusVo.setShowManage(true);
                        }else{
                            zkbPartOrderStatusVo.setShowTech(true);
                        }
                    }
                }
                zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTech(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowManage(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    } else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowDirector(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }

                }
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTech(true);
            } else if(ZkbPartOrder.PartOrderStatus.TseAccept.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTse(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowOssc(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                }
            } else if(ZkbPartOrder.PartOrderStatus.TseReject.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTse(true);
            } else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                zkbPartOrderStatusVo.setShowManage(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowDirector(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowUnOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }
                }
            } else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                zkbPartOrderStatusVo.setShowManage(true);
            } else if(ZkbPartOrder.PartOrderStatus.OsscAccept.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTse(true);
                zkbPartOrderStatusVo.setShowOssc(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowUnOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            } else if(ZkbPartOrder.PartOrderStatus.OsscReject.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTech(true);
                zkbPartOrderStatusVo.setShowOssc(true);
            } else if(ZkbPartOrder.PartOrderStatus.DirectorAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                zkbPartOrderStatusVo.setShowDirector(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowUnOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowCommercial(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                    }
                }
            } else if(ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                zkbPartOrderStatusVo.setShowDirector(true);
            } else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                zkbPartOrderStatusVo.setShowCommercial(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowUnOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        zkbPartOrderStatusVo.setShowOrder(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                }
            }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        zkbPartOrderStatusVo.setShowTech(true);
                    }
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                zkbPartOrderStatusVo.setShowCommercial(true);
                zkbPartOrderStatusVo.setShowBtn("");;
            } else if(ZkbPartOrder.PartOrderStatus.UnOrdered.toString().equals(selected.getStatus())) {
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditerUid())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                zkbPartOrderStatusVo.setShowUnOrder(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowOrder(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            }else if(ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString().equals(selected.getStatus())) {
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditerUid())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                zkbPartOrderStatusVo.setShowRejectOrder(true);
                zkbPartOrderStatusVo.setShowBtn("");
            }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditerUid())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StrUtil.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(StrUtil.isEmpty(selected.getNotGeAsset())){
                        zkbPartOrderStatusVo.setShowReturn(true);
                    }else{
                        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                            zkbPartOrderStatusVo.setShowOsscReturn(true);
                        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                            zkbPartOrderStatusVo.setShowReturn(true);
                        }
                    }
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            } else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                zkbPartOrderStatusVo.setShowRejectOrder(true);
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
            }else if(ZkbPartOrder.PartOrderStatus.PartReturned.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditerUid())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowReturn(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }else {
                    List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                    if(zkbPartOrderUserConfigs.size() > 0){
                        zkbPartOrderStatusVo.setShowReturn(true);
                    }
                }
            }else if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus()) ){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                zkbPartOrderStatusVo.setShowReturn(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowClosed(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            } else if(ZkbPartOrder.PartOrderStatus.OsscReturned.toString().equals(selected.getStatus())){
                zkbPartOrderStatusVo.setShowTse(true);
                zkbPartOrderStatusVo.setShowOssc(true);
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                zkbPartOrderStatusVo.setShowOsscReturn(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowClosed(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            }else if(ZkbPartOrder.PartOrderStatus.Closed.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                if(StrUtil.isEmpty(selected.getNotGeAsset())){
                    zkbPartOrderStatusVo.setShowReturn(true);
                    zkbPartOrderStatusVo.setShowClosed(true);
                }else {
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowOsscReturn(true);
                    }else if (ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowReturn(true);
                        zkbPartOrderStatusVo.setShowClosed(true);
                    }
                }
                if(zkbPartOrderCloseInfo != null && StringUtils.isNotEmpty(zkbPartOrderCloseInfo.getId())){
                    zkbPartOrderStatusVo.setShowCompleteAudit(true);
                    zkbPartOrderStatusVo.setShowComplete(true);
                }
            }else if(ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                if(StrUtil.isEmpty(selected.getNotGeAsset())){
                    zkbPartOrderStatusVo.setShowReturn(true);
                    zkbPartOrderStatusVo.setShowClosed(true);
                    if(zkbPartOrderStatusVo.isCanEditFlag()){
                        zkbPartOrderStatusVo.setShowComplete(true);
                        zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                    }
                }else {
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowOsscReturn(true);
                    }else if (ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                        zkbPartOrderStatusVo.setShowReturn(true);
                        zkbPartOrderStatusVo.setShowClosed(true);
                    }
                }
            } else if(ZkbPartOrder.PartOrderStatus.CloseCommited.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                zkbPartOrderStatusVo.setShowReturn(true);
                zkbPartOrderStatusVo.setShowClosed(true);
                zkbPartOrderStatusVo.setShowComplete(true);
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    zkbPartOrderStatusVo.setShowCompleteAudit(true);
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ZERO);
                }
            }else if(ZkbPartOrder.PartOrderStatus.CloseApproved.toString().equals(selected.getStatus()) || ZkbPartOrder.PartOrderStatus.CloseFailed.toString().equals(selected.getStatus())){
                if(StrUtil.isNotEmpty(selected.getTechAuditerUid())){
                    zkbPartOrderStatusVo.setShowTech(true);
                }
                if(StrUtil.isNotEmpty(selected.getTseAuditerUid())){
                    zkbPartOrderStatusVo.setShowTse(true);
                }
                if(StrUtil.isNotEmpty(selected.getOsscAuditerUid())){
                    zkbPartOrderStatusVo.setShowOssc(true);
                }
                if(StrUtil.isNotEmpty(selected.getManageAuditer())){
                    zkbPartOrderStatusVo.setShowManage(true);
                }
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    zkbPartOrderStatusVo.setShowDirector(true);
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    zkbPartOrderStatusVo.setShowCommercial(true);
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderStatusVo.setShowUnOrder(true);
                }
                zkbPartOrderStatusVo.setShowOrder(true);
                zkbPartOrderStatusVo.setShowReturn(true);
                zkbPartOrderStatusVo.setShowClosed(true);
                zkbPartOrderStatusVo.setShowComplete(true);
                zkbPartOrderStatusVo.setShowCompleteAudit(true);
                if(ZkbPartOrder.PartOrderStatus.CloseFailed.toString().equals(selected.getStatus())){
                    zkbPartOrderStatusVo.setShowBtn(CopyByInvok.NORMAL_ONE);
                }
            }
        }
        List<ZkbPartOrderUserConfig> closeUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
        if(closeUserConfigs.size() > 0){
            zkbPartOrderStatusVo.setChoseAdmin(true);
        }

        //只有人工申请允许该申请被取消
        List<ZkbPartOrderUserConfig> orderConfigs = zkbPartOrderUserConfigRepository.findByLoginName(currentUser.getLoginName());
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())&& CollectionUtils.isNotEmpty(orderConfigs)){
            String oldStep = selected.getCurrentStep();
            if(orderConfigs.stream().filter(m -> ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(m.getConfigType())).findAny().isPresent() &&
                    oldStep.equals(ZkbPartOrderStep.PartOrderStep.Complete.toString()) &&
                    !ZkbPartOrder.PartOrderStatus.FourHundredCancel.toString().equals(selected.getStatus()) ){
                zkbPartOrderStatusVo.setShowBtnFourHundredCancel(true);
            }
        }
        for(ZkbPartOrderUserConfig c : orderConfigs){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType()) && ZkbPartOrder.OrderType.Probe.toString().equals(c.getOrderType())){
                    zkbPartOrderStatusVo.setOrder(true);
                    break;
                }
            }else{
                if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType()) && StringUtils.isEmpty(c.getOrderType())){
                    zkbPartOrderStatusVo.setOrder(true);
                    break;
                }
            }
        }

        List<ZkbPartOrderUserConfig> unOrderConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
        if(unOrderConfigs.size() > 0){
            zkbPartOrderStatusVo.setUnOrder(true);
        }
    }

    public void initRemainQty(ZkbPartOrder selected, ZkbContract zkbContract){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                selected.setRemainQty(zkbContract.getSpareQuota());
            }else{
                selected.setRemainQty(zkbContract.getSpareArtificail());
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(StringUtils.isNotEmpty(selected.getProbeQuotaType())){
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                    selected.setRemainQty(zkbContract.getProbeQuota());
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                    selected.setRemainQty(zkbContract.getProbeQuotaNormal());
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                    selected.setRemainQty(zkbContract.getProbeQuotaSpecial());
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                        selected.setRemainQty(zkbContract.getProbeQuotaNormal());
                    }
                }
            }else{
                if(StringUtils.isEmpty(selected.getHighCatFlag()) || CopyByInvok.F.equals(selected.getHighCatFlag())){
                    selected.setRemainQty(zkbContract.getProbeQuota());
                }else{
                    if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                        selected.setRemainQty(zkbContract.getProbeQuota());
                    }
                }
            }
            if(CopyByInvok.T.equals(selected.getNotGeAsset())){
                if(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString().equals(selected.getCurrentStep())){
                    selected.setTakeFlag(CopyByInvok.T);
                }else if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(selected.getCurrentStep())){
                    selected.setRepairFlag(CopyByInvok.T);
                }
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                selected.setRemainQty(zkbContract.getGeArtificialSupportTimes());
            }else{
                selected.setRemainQty(zkbContract.getSpareArtificail());
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            selected.setRemainQty(zkbContract.getSpareQuota());
        }

        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
            selected.setReturnQty(selected.getQty());
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
            selected.setNeedLetterFlag(CopyByInvok.F);
        }
    }

    public void initValues(ZkbPartOrder selected,ZkbPartOrderStatusVo zkbPartOrderStatusVo){
        if(selected.getZkbPartOrderCat() != null){
            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                zkbPartOrderStatusVo.setValueTypeTips("");
            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                zkbPartOrderStatusVo.setValueTypeTips("您的选择为高值探头，请确认合同中是否有此权益！");
            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                zkbPartOrderStatusVo.setValueTypeTips("");
            }
        }

        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            zkbPartOrderStatusVo.setShowOrderedFieldsetText("派工信息");
        }else{
            zkbPartOrderStatusVo.setShowOrderedFieldsetText("订货信息");
        }
    }

    public String normalCheck(ZkbPartOrder selected){
        StringBuilder errorLog = new StringBuilder();
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())){
                if(StrUtil.isEmpty(selected.getSrNo())){
                    errorLog.append("SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("Act#不能为空，\r\n");
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StrUtil.isEmpty(selected.getOrderNo())){
                    errorLog.append("Order#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getNotGeAsset()) && StrUtil.isEmpty(selected.getPrice())){
                    errorLog.append("价格不能为空，\r\n");
                }
                if(selected.getZkbPartOrderCat() != null && !ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType()) && StrUtil.isEmpty(selected.getNewCatFlag())){
                    errorLog.append("是否全新探头不能为空，\r\n");
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
                if(StrUtil.isEmpty(selected.getReturnType())){
                    errorLog.append("归还类型不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getWaybillNo())){
                    errorLog.append("运单号不能为空，\r\n");
                }
                if(selected.getReturnDate() == null){
                    errorLog.append("归还日期不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getNewCatSn())){
                    errorLog.append("新探头SN编号不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getUdi())){
                    errorLog.append("探头UDI不能为空，\r\n");
                }
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StrUtil.isEmpty(selected.getSrNo())){
                    errorLog.append("SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("Act#不能为空，\r\n");
                }
            }
        }
        return errorLog.toString();
    }

    public String normalCheckQuota(ZkbPartOrder selected){
        ZkbContract c = zkbContractRepository.findById(selected.getContractId());
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
           return normalCheckProbeQuota(selected, c);
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            return normalCheckPartQuota(selected, c);
        }
        return "";
    }

    public String normalCheckProbeQuota(ZkbPartOrder selected, ZkbContract c){
        if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
            if(selected.getReturnQty() > selected.getQty()){
                return "归还数量不能大于申请数量";
            }
        } else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
            if(StringUtils.isEmpty(c.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(c.getQuotaDeductType())) {
                if(!selected.getNewCatFlag().equals(selected.getTenantNewCatFlag())){
                    if (CopyByInvok.T.equals(selected.getNewCatFlag())) {
                        if(CopyByInvok.T.equals(selected.getNewCatFlag()) && StringUtils.isEmpty(selected.getProbeQuotaType())){
                            if(c.getProbeQuota() < selected.getQty()){
                                return "探头剩余名额不足，请联系供应商！";
                            }
                        }
                    }
                    if(CopyByInvok.T.equals(c.getAssetQuotaType())){
                        if (CopyByInvok.T.equals(selected.getNewCatFlag())) {
                            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(c.getId(),selected.getAssetId());
                            boolean r = zkbPartOrderService.checkQuotaHistory("order",c, selected, zkbContractAsset, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
                            if(!r){
                                if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                                    return "该合同中的高端机名额不足,请检查！";

                                }else{
                                    return "该合同中的低端机名额不足,请检查！";
                                }
                            }
                        }
                    }
                }
                if(c.getProbeQuotaApply() < selected.getQty()){
                    return "探头申请名额不足，请联系供应商！";
                }
            }else{
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                    if(c.getProbeQuotaApply() < selected.getQty()){
                        return "探头任意申请名额不足，请联系供应商！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                    if(c.getProbeQuotaNormalApply() < selected.getQty()){
                        return "探头普通申请名额不足，请联系供应商！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                    if(c.getProbeQuotaSpecialApply() < selected.getQty()){
                        return "探头特殊申请名额不足，请联系供应商！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())) {
                    if (CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())) {
                        if(c.getProbeQuotaNormalApply() < selected.getQty()){
                            return "探头高值名额不足，请联系供应商！";
                        }
                    }
                }
            }
        } else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString())){
            if(CopyByInvok.F.equals(selected.getTakeFlag())){
                if(StrUtil.isEmpty(selected.getUnTakedReason())){
                    return "拒绝原因不能为空！";
                }
            }
        }
        return "";
    }

    public boolean checkComplete(ZkbPartOrder selected, List<ZkbPartOrderCloseWorkTime> zkbPartOrderCloseWorkTimeList, List<V2_BlobObject> closeBlobObjectList, ZkbPartOrderCloseInfo zkbPartOrderCloseInfo){
        boolean r = true;
        if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getUdi())){
            WebUtil.addErrorMessage("设备UDI不能为空！",  "");
            r = false;
        }
        if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getSafetyConcern())){
            WebUtil.addErrorMessage("Safety Concern(该故障是否存在潜在伤害或已导致实际伤害)不能为空！",  "");
            r = false;
        }
        if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getPatientUse())){
            WebUtil.addErrorMessage("Patient Use(故障发生时是否有病人在使用机器)不能为空！",  "");
            r = false;
        }else {
            if("Yes".equals(zkbPartOrderCloseInfo.getPatientUse())){
                if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getRiskType())){
                    WebUtil.addErrorMessage("有病人使用，存在病患风险，请选择风险分类不能为空！",  "");
                    r = false;
                }
            }else if("No".equals(zkbPartOrderCloseInfo.getPatientUse())){
                if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getNoRiskType())){
                    WebUtil.addErrorMessage("没有病人使用，没有病患风险，请选择没有风险分类不能为空！",  "");
                    r = false;
                }
            }else if("NotReported".equals(zkbPartOrderCloseInfo.getPatientUse())){
                if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getNoReportType())){
                    WebUtil.addErrorMessage("请选择没有告知是否有病患/未报告的类别不能为空！",  "");
                    r = false;
                }
            }
        }
        if("Yes".equals(zkbPartOrderCloseInfo.getPatientUse()) || "Potential".equals(zkbPartOrderCloseInfo.getSafetyConcern()) || "Actual".equals(zkbPartOrderCloseInfo.getSafetyConcern())){
            if(zkbPartOrderCloseInfo.getEventDated() == null){
                WebUtil.addErrorMessage("Event Date(病人影响事件发生的时间)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getQualityContactPerson())){
                WebUtil.addErrorMessage("Quality Contact(质量联系人姓名)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getQualityContactPhone())){
                WebUtil.addErrorMessage("Quality Contact(质量联系人电话)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getInterventionRequired())){
                WebUtil.addErrorMessage("Intervention Required(需要的干涉)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getAdditionalDetails())){
                WebUtil.addErrorMessage("Additional Details(额外的信息)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getPatientImpactOutcome())){
                WebUtil.addErrorMessage("Patient Impact/Outcome病人影响/结果不能为空！",  "");
                r = false;
            }
        }else if("NotReported".equals(zkbPartOrderCloseInfo.getPatientUse()) && "No".equals(zkbPartOrderCloseInfo.getSafetyConcern()) ){
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getQualityContactPerson())){
                WebUtil.addErrorMessage("Quality Contact(质量联系人姓名)不能为空！",  "");
                r = false;
            }
            if(StrUtil.isEmpty(zkbPartOrderCloseInfo.getQualityContactPhone())){
                WebUtil.addErrorMessage("Quality Contact(质量联系人电话)不能为空！",  "");
                r = false;
            }
        }

        if("Actual".equals(zkbPartOrderCloseInfo.getSafetyConcern())){
            if(zkbPartOrderCloseInfo.getIncidentDate() == null){
                WebUtil.addErrorMessage("Incident date(该故障导致实际伤害事件的时间)不能为空！",  "");
                r = false;
            }
        }
        /*if(zkbPartOrderCloseInfo.getAllegedPatientConsequence() == null){
            WebUtil.addErrorMessage("病人结果不能为空！",  "");
            r = false;
        }*/

        if(zkbPartOrderCloseInfo.getProblemReason() == null){
            WebUtil.addErrorMessage("Problem(工程师服务原因)不能为空！",  "");
            r = false;
        }
        if(zkbPartOrderCloseInfo.getActionContent() == null){
            WebUtil.addErrorMessage("Action(工程师服务内容)不能为空！",  "");
            r = false;
        }
        if(zkbPartOrderCloseInfo.getTestResult() == null){
            WebUtil.addErrorMessage("Test(工程师服务结果)不能为空！",  "");
            r = false;
        }
        if(zkbPartOrderCloseWorkTimeList == null || zkbPartOrderCloseWorkTimeList.size() == 0){
            WebUtil.addErrorMessage("工作&差旅时间不能为空!","");
            r = false;
        }else{
            boolean f = false;
            StringBuilder errorMsg = new StringBuilder();
            for(ZkbPartOrderCloseWorkTime t : zkbPartOrderCloseWorkTimeList){
                if("Phone Support".equals(t.getDateType())){
                    f = true;
                }else if("onsite".equals(t.getDateType())){
                    f = true;
                }
                if(t.getStartTime().getTime() >= t.getEndTime().getTime()){
                    errorMsg.append(String.format("日期：【%tF】开始时间不能大于等于结束时间！\r", t.getWorkDated()));
                }
            }
            if(!f){
                WebUtil.addErrorMessage("必须有一个【远程支持】或【现场维修】时间类型!","");
            }
            if(StringUtils.isNotEmpty(errorMsg)){
                WebUtil.addErrorMessage(errorMsg.toString(),"");
                r = false;
            }
        }
        if(closeBlobObjectList == null || closeBlobObjectList.size() == 0){
            WebUtil.addErrorMessage("服务记录单不能为空!","");
            r = false;
        }
        if(zkbPartOrderService.checkCloseInfoBySrNum(selected.getSrNo(), zkbPartOrderCloseInfo)){
            WebUtil.addErrorMessage("srNo已经存在完成信息，不需要重复提交!","");
            r = false;
        }
        return r;
    }

    public String normalCheckPartQuota(ZkbPartOrder selected, ZkbContract c){
        if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
            boolean flag = true;
            String errorLog = "";
            Integer t= 0;
            for(ZkbPartOrderItem zkbPartOrderItem : selected.getZkbPartOrderItems()){
                if(StringUtils.isEmpty(zkbPartOrderItem.getPartNo())){
                    flag = false;
                    errorLog += "配件号不能为空,";
                }
                if(StringUtils.isEmpty(zkbPartOrderItem.getPartName())){
                    flag = false;
                    errorLog += "配件名称不能为空,";
                }
                if(StrUtil.isEmpty(selected.getNotGeAsset())){
                    if(StringUtils.isEmpty(zkbPartOrderItem.getPrice())){
                        flag = false;
                        errorLog += "价格不能为空,";
                    }
                }
                if(StringUtils.isEmpty(zkbPartOrderItem.getOrderNo())){
                    flag = false;
                    errorLog += "Order#不能为空,";
                }
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) && StringUtils.isEmpty(zkbPartOrderItem.getActualPartNo())){
                    flag = false;
                    errorLog += "实际配件号不能为空,";
                }
                if(zkbPartOrderItem.getPrePartQty() > zkbPartOrderItem.getPartQty()){
                    if(StringUtils.isEmpty(zkbPartOrderItem.getRemark())){
                        flag = false;
                        errorLog += "配件号为"+zkbPartOrderItem.getPartNo()+"的实际配件数量修改后备注不能为空,";
                    }
                }
                t += zkbPartOrderItem.getPartQty();
                ZkbPartOrderItem oldItem = zkbPartOrderService.getZkbPartOrderItemRepository().findById(zkbPartOrderItem.getId());
                if(oldItem != null){
                    if(!oldItem.getPartNo().equals(zkbPartOrderItem.getPartNo()) || !oldItem.getPartName().equals(zkbPartOrderItem.getPartName())){
                        if(StringUtils.isEmpty(zkbPartOrderItem.getRemark())){
                            flag = false;
                            errorLog += "配件号或配件名称修改后备注不能为空,";
                        }
                    }
                }
            }
            if(!flag){
                errorLog = errorLog.substring(0,errorLog.length()-1);
                return errorLog;
            }
            if(t == 0){
                return "实际配件数量不能全部为0";
            }
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(c.getId(),selected.getAssetId());
            if(ZkbContract.YOrNEnum.Y.toString().equals(c.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){

            }else {
                if (ZkbContract.ContractType.CCS.toString().equals(c.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(c.getContractType())) {
                    if(c.getSpareQuotaApply() < selected.getQty()){
                        return "备件申请名额不足，请联系供应商！";
                    }
                }else if(ZkbContract.ContractType.DI.toString().equals(c.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(c.getContractType())){
                    if(selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup().equals(1)){
                        if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                            if(c.getPartHighQuotaApply() < selected.getQty()){
                                return "高值备件申请名额不足，请联系供应商！";
                            }
                        }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                            if(c.getSpareArtificailApply() < selected.getQty()){
                                return "联合申请名额不足，请联系供应商！";
                            }
                        }else{
                            if(c.getSpareArtificailApply() < selected.getQty()){
                                return "联合申请名额不足，请联系供应商！";
                            }
                        }
                    }else{
                        if(c.getSpareArtificailApply() < selected.getQty()){
                            return "联合申请名额不足，请联系供应商！";
                        }
                    }
                }else{
                    if(c.getSpareArtificailApply() < selected.getQty()){
                        return "联合申请名额不足，请联系供应商！";
                    }
                }
            }
        }
        return "";
    }

    /**
     * 获取归还类型的下拉数据
     * 工具申请的时候则需要去除DOA、goodReturn、cancel、damagedShipment
     * @param zkbPartOrder
     * @return
     */
    public List<I18nMessage> initReturnList(ZkbPartOrder zkbPartOrder){
        List<I18nMessage> returnTypeList = i18nDao.getByMsgType("zkbPoReturnType");
        if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            List<I18nMessage> removeI18 = new ArrayList<>();
            for(I18nMessage i18nMessage : returnTypeList){
                if(!"DOA".equals(i18nMessage.getMsgKey()) &&
                        !"goodReturn".equals(i18nMessage.getMsgKey()) &&
                        !"cancel".equals(i18nMessage.getMsgKey()) &&
                        !"damagedShipment".equals(i18nMessage.getMsgKey())
                ){
                    removeI18.add(i18nMessage);
                }
            }
            if(removeI18.size() > 0){
                returnTypeList.removeAll(removeI18);
            }
        }
        return returnTypeList;
    }

    public List<HashMap<String, String>> initHighList(String contractId){
        List<HashMap<String, String>> highCatList = zkbPartOrderService.queryHighCatByContract(contractId,true);
        return highCatList;
    }

    public void saveWorkTime(ZkbPartOrderCloseWorkTime zkbPartOrderCloseWorkTime){
        zkbPartOrderCloseWorkTimeRepository.save(zkbPartOrderCloseWorkTime);
    }


    public List<ZkbPartOrderCloseWorkTime> getZkbPartOrderCloseWorkTimeList(String closeId){
        List<ZkbPartOrderCloseWorkTime> zkbPartOrderCloseWorkTimeList = zkbPartOrderCloseWorkTimeRepository.findByCloseId(closeId);
        for (int i =0; i< zkbPartOrderCloseWorkTimeList.size(); i++){
            ZkbPartOrderCloseWorkTime zkbPartOrderCloseWorkTime = zkbPartOrderCloseWorkTimeList.get(i);
            zkbPartOrderCloseWorkTime.setIndex(i);
        }
        return zkbPartOrderCloseWorkTimeList;
    }
}

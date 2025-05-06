package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import webapp.framework.web.WebUtil;

import java.util.*;

@Service
public class ZkbIntegralOrderAuditService extends MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(ZkbIntegralOrderAuditService.class);

    @Autowired
    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;

    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    @Autowired
    private ZkbPartOrderStepRepository zkbPartOrderStepRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Autowired
    private ZkbContractRepository zkbContractRepository;

    @Autowired
    private ZkbIntegralOrderApplyService zkbIntegralOrderApplyService;

    public ZkbIntegralOrder queryInitData(String id,List<ZkbContract> zkbContractList, List<HashMap<String, Object>> v2_listHashMap){
        ZkbIntegralOrder selected = zkbIntegralOrderRepository.findById(id);
        if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            ZkbContract zkbContract = zkbContractRepository.findById(selected.getContractId());
            zkbContractList.add(zkbContract);
            V2_WorkOrder v = v2_workOrderRepository.findById(selected.getWorkOrderId());
            if(v != null) {
                HashMap<String, Object> m = new HashMap<>();
                m.put("id", v.getId());
                Optional.ofNullable(v.getServiceRequest()).ifPresent(t -> {
                    m.put("srNum", v.getServiceRequest().getSrNum());
                    m.put("customerName", v.getServiceRequest().getCustomerName());
                    m.put("customerTel", v.getServiceRequest().getCustomerTel());
                    m.put("faultDescription", v.getServiceRequest().getRequestReason());
                    m.put("requestTime", v.getServiceRequest().getRequestTime());
                });
                Optional.ofNullable(v.getAssetInfo()).ifPresent(t -> {
                    m.put("name", v.getAssetInfo().getName());
                    m.put("systemId", v.getAssetInfo().getSystemId());
                    m.put("assetId", v.getAssetInfo().getUid());
                    m.put("hospitalName", v.getAssetInfo().getClinicalDeptName());
                    m.put("oneQrCode", v.getAssetInfo().getQrCode());
                    m.put("makerShortName", v.getAssetInfo().getManufacture());
                    m.put("departNum", v.getAssetInfo().getDepartNum());
                });
                v2_listHashMap.add(m);
            }
        }
        return selected;
    }

    public ZkbContract queryZkbContract(String cid){
        return zkbContractRepository.findById(cid);
    }

    public List<V2_BlobObject> queryBlobObjectByIntegralId(String id){
        return blobObjectRepository.findByBoUuid(id);
    }

    /**
     *
     * @param currentUser
     * @param zkbIntegralOrderStatusVo
     * @param selected
     */
    public void fieldRendered(UserAccount currentUser, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo, ZkbIntegralOrder selected){
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<>();
        if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndZoneContains(selected.getCurrentStep(),selected.getZone());
        }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndOrderTypeNull(selected.getCurrentStep());
        }else {
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(selected.getCurrentStep());
        }
        if(zkbPartOrderUserConfigs != null && zkbPartOrderUserConfigs.size() > 0){
            for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigs){
                if(currentUser.getLoginName().equals(c.getLoginName())){
                    zkbIntegralOrderStatusVo.setCanEditFlag(true);
                }
            }
        }

        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            if(ZkbIntegralOrder.IntegralOrderStatusEnum.Requested.toString().equals(selected.getStatus())){
                if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                    zkbIntegralOrderStatusVo.setShowEngineer(true);
                    zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                }
            }else if(ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationAccept.name().equals(selected.getStatus())){
                zkbIntegralOrderStatusVo.setShowEngineer(true);
                if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                    zkbIntegralOrderStatusVo.setShowOrder(true);
                    zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                }
            }else if(ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationReject.name().equals(selected.getStatus())){
                zkbIntegralOrderStatusVo.setShowEngineer(true);
            }else {
                zkbIntegralOrderStatusVo.setShowEngineer(true);
                zkbIntegralOrderStatusVo.setShowOrder(true);
            }
        }else{
            if(ZkbConstantUtil.NORMAL_TWO.equals(selected.getArtificialType())){
                if(ZkbIntegralOrder.IntegralOrderStatusEnum.Requested.toString().equals(selected.getStatus())  && zkbIntegralOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                    } else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        zkbIntegralOrderStatusVo.setShowCommercial(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                    }else{
                        zkbIntegralOrderStatusVo.setShowOrder(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Submit.name());
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                    }
                    zkbIntegralOrderStatusVo.setShowCommercial(true);
                    if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                        zkbIntegralOrderStatusVo.setShowOrder(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Submit.name());
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                    }
                    zkbIntegralOrderStatusVo.setShowCommercial(true);
                    zkbIntegralOrderStatusVo.setShowOrder(false);
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                    }
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbIntegralOrderStatusVo.setShowCommercial(true);
                    }
                    zkbIntegralOrderStatusVo.setShowOrder(true);
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getManageAuditer())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                    }
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbIntegralOrderStatusVo.setShowCommercial(true);
                    }
                    zkbIntegralOrderStatusVo.setShowOrder(true);
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                        if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                            zkbIntegralOrderStatusVo.setShowCommercial(true);
                            zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                        }else{
                            zkbIntegralOrderStatusVo.setShowOrder(true);
                            zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Submit.name());
                        }
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                }
            }else{
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && zkbIntegralOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        zkbIntegralOrderStatusVo.setShowManage(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                        if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                            zkbIntegralOrderStatusVo.setShowCommercial(true);
                            zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Audit.name());
                        }else{
                            zkbIntegralOrderStatusVo.setShowOrder(true);
                            zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Submit.name());
                        }
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    zkbIntegralOrderStatusVo.setShowCommercial(true);
                    if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                        zkbIntegralOrderStatusVo.setShowOrder(true);
                        zkbIntegralOrderStatusVo.setShowBtn(ZkbIntegralOrderStatusVo.BtnEnum.Submit.name());
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    zkbIntegralOrderStatusVo.setShowCommercial(true);
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbIntegralOrderStatusVo.setShowCommercial(true);
                    }
                    zkbIntegralOrderStatusVo.setShowOrder(true);
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    zkbIntegralOrderStatusVo.setShowManage(true);
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        zkbIntegralOrderStatusVo.setShowCommercial(true);
                    }
                    zkbIntegralOrderStatusVo.setShowOrder(true);
                }
            }
        }
    }

    public void initPageData(ZkbIntegralOrder selected, ZkbContract zkbContract, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo){
        if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.toString().equals(selected.getOrderType()) && "1".equals(selected.getArtificialType())){
            if(!ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                if(!ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                    zkbIntegralOrderStatusVo.setHaveTrainCourseFlag(true);
                }
            }
        }
    }

    public List<ZkbPartOrderCat> queryCatList(){
        return zkbPartOrderCatRepository.findByBusinessType(ZkbPartOrderCat.BusinessTypeEnum.ToolBox.name());
    }

    /**
     * 审批方法
     */
    public void normalNext(ZkbIntegralOrder zkbIntegralOrder,UserAccount userAccount, ZkbContract zkbContract, boolean flag){
        if(ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name().equals(zkbIntegralOrder.getCurrentStep())){
            zkbIntegralOrder.setEngineerAuditDate(new Date());
            zkbIntegralOrder.setEngineerAuditer(userAccount.getName());
            zkbIntegralOrder.setEngineerAuditerUid(userAccount.getUid());
            if(flag){
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.name());
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationAccept.name());
            }else{
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.name());
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationReject.name());
            }
        } else if(ZkbPartOrderStep.PartOrderStep.ManagePending.name().equals(zkbIntegralOrder.getCurrentStep())){
            zkbIntegralOrder.setManageAuditDate(new Date());
            zkbIntegralOrder.setManageAuditer(userAccount.getName());
            zkbIntegralOrder.setManageAuditerUid(userAccount.getUid());
            if(flag){
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.ManageAccept.name());
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.name());
            }else{
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.ManageReject.name());
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.name());
            }
           /* if("GT".equals(CopyByInvok.compareDateByYyyyMMdd(zkbIntegralOrder.getRequestDate(),zkbContract.getEndDate()))){
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.name());
            }else {*/
            //}
        }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.name().equals(zkbIntegralOrder.getCurrentStep())){
            zkbIntegralOrder.setCommercialAuditDate(new Date());
            zkbIntegralOrder.setCommercialAuditer(userAccount.getName());
            zkbIntegralOrder.setCommercialAuditerUid(userAccount.getUid());
            if(flag){
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.CommercialAccept.name());
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.name());
            }else{
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.CommercialReject.name());
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.name());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.name().equals(zkbIntegralOrder.getCurrentStep())){
            zkbIntegralOrder.setOrderedDate(new Date());
            zkbIntegralOrder.setOrderedAuditer(userAccount.getName());
            zkbIntegralOrder.setOrderedAuditerUid(userAccount.getUid());
            zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.name());
            if (flag) {
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.Ordered.name());
            } else {
                zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.OrderedReject.name());
            }
        }

        Integer dataFlag = zkbIntegralOrder.getDataFlag() == null ? 0 : zkbIntegralOrder.getDataFlag();
        dataFlag++;
        zkbIntegralOrder.setDataFlag(dataFlag);
        zkbIntegralOrderRepository.save(zkbIntegralOrder);

        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(zkbIntegralOrder.getCurrentStep());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbIntegralOrderSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbIntegralOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbIntegralOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        //如果审批不通过则需要归还积分,微信通知代理商
        if(!flag || ZkbConstantUtil.F.equals(zkbIntegralOrder.getWhetherDeduct())){
            int score = 0;
            if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
                score = 900;
            }else {
                score = 200;
            }
            zkbIntegralService.deductScore(zkbIntegralOrder.getTenantUID(),userAccount, ZkbIntegralRecord.RecordType.QuotaReturn.toString(), score);
            sendWeChatMessages(zkbIntegralOrder, UserContextService.getAccessToken());
        }else{
            //如果当前步骤是commercial则需要推送短信
            if(ZkbPartOrderStep.PartOrderStep.CommercialPending.name().equals(zkbIntegralOrder.getCurrentStep())){
                zkbIntegralOrderApplyService.sendEmail(zkbIntegralOrder, UserContextService.getAccessToken(), getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_EMAIL), WebUtil.getFieldValueMessage("zkbIntegralOrderType", zkbIntegralOrder.getOrderType()));
                zkbIntegralOrderApplyService.sendMessage(zkbIntegralOrder, UserContextService.getAccessToken(), WebUtil.getFieldValueMessage("zkbIntegralOrderType",zkbIntegralOrder.getOrderType()) + "申请");
            }else if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType()) && ZkbPartOrderStep.PartOrderStep.Complete.name().equals(zkbIntegralOrder.getCurrentStep())){
                zkbIntegralOrderApplyService.sendEmail(zkbIntegralOrder, UserContextService.getAccessToken(), getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_GE_ADMIN_EMAIL), WebUtil.getFieldValueMessage("zkbIntegralOrderType", zkbIntegralOrder.getOrderType()));
            }else if(!ZkbPartOrderStep.PartOrderStep.Complete.name().equals(zkbIntegralOrder.getCurrentStep())){
                zkbIntegralOrderApplyService.sendEmail(zkbIntegralOrder, UserContextService.getAccessToken(), getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_EMAIL), WebUtil.getFieldValueMessage("zkbIntegralOrderType", zkbIntegralOrder.getOrderType()));
            }
        }


    }

    @Async
    public void sendWeChatMessages(ZkbIntegralOrder zkbIntegralOrder,String token){
        try {
            String url = getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_WECHAT_MESSAGE).concat("?id=").concat(zkbIntegralOrder.getId());
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbIntegralOrder.getIntegralOrderNo()+" sendWeChatMessages "+response.toString());
        } catch (RestClientException e) {
            e.printStackTrace();
        }

    }
}

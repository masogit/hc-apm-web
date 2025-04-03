package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.CopyByInvok;
import com.ge.apm.service.zkb.ZkbIntegralOrderAuditService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbIntegralOrderAuditController extends GenericCRUDUUIDController<ZkbIntegralOrder> {

    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    private UserContextService userContextService;

    private ZkbIntegralOrderAuditService zkbIntegralOrderAuditService;

    private UserAccount currentUser;

    @Getter@Setter
    private String actionName;
    @Getter @Setter
    private ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo;
    @Getter @Setter
    private List<HashMap<String, Object>> v2_listHashMap = new ArrayList<>();
    @Getter @Setter
    private List<ZkbContract> contractList = new ArrayList<>();
    @Getter @Setter
    private AssetInfo assetInfo;
    @Getter @Setter
    private ZkbContract zkbContract;
    @Getter @Setter
    private List<ZkbContract> zkbContractList = new ArrayList<>();

    private boolean toolBoxAuditControlFlag = true;

    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();

    String encodeStr;

    @Getter @Setter
    List<ZkbPartOrderCat> zkbPartOrderCatList = new ArrayList<>();

    @Getter @Setter
    List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    private String queryStatus;

    @Override
    protected void init() {
        super.init();
        zkbIntegralOrderRepository = WebUtil.getBean(ZkbIntegralOrderRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbIntegralOrderAuditService = WebUtil.getBean(ZkbIntegralOrderAuditService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbIntegralOrderStatusVo = new ZkbIntegralOrderStatusVo();
        zkbContract = new ZkbContract();
        encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        queryStatus = UrlEncryptController.getValueFromMap(encodeStr, "queryStatus") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        if(StringUtils.isNotEmpty(selectedId)){
            selected = zkbIntegralOrderAuditService.queryInitData(selectedId, zkbContractList, v2_listHashMap);
            zkbContract = zkbIntegralOrderAuditService.queryZkbContract(selected.getContractId());
            if(selected == null || StrUtil.isEmpty(selected.getId())){
                WebUtil.addErrorMessage("数据不存在！");
                return;
            }

            zkbIntegralOrderAuditService.fieldRendered(currentUser, zkbIntegralOrderStatusVo, selected);
            zkbIntegralOrderAuditService.initPageData(selected, zkbContract, zkbIntegralOrderStatusVo);
            zkbPartOrderCatList = zkbIntegralOrderAuditService.queryCatList();
            if(ZkbContract.ContractType.CCS.name().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.name().equals(zkbContract.getContractType())){
                v2_blobObjectList = zkbIntegralOrderAuditService.queryBlobObjectByIntegralId(selectedId);
            }

            if(userContextService.isZkbUser()) {
                if(ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationReject.name().equals(selected.getStatus())){
                    if(CopyByInvok.F.equals(selected.getEngineerQualificationsFlag())){
                        WebUtil.addErrorMessage("工程师无资质，需有资质的工程师提交申请！","");
                    }
                }
            }
            if(ZkbPartOrderStep.PartOrderStep.OrderPending.name().equals(selected.getCurrentStep())){
                if(zkbIntegralOrderStatusVo.isCanEditFlag()){
                    if(ZkbIntegralOrder.OrderTypeEnum.EngineerOnSiteMaintenance.name().equals(selected.getOrderType())){
                        Date date1 = DateUtil.parse("2024/11/22");
                        if(date1.after(zkbContract.getCreatedDate())){
                            zkbIntegralOrderStatusVo.setMaintainTips("该合同需要联系OTR添加 PM active entitlement；");
                        }
                    }
                }
            }
        }

    }

    public String normalNext(boolean flag){
        zkbIntegralOrderAuditService.normalNext(selected, currentUser, zkbContract, flag);
        return "List?faces-redirect=true&queryStatus="+queryStatus ;
    }


    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus ;
    }

    public void engineerQualificationsFlagChange(){
        if(CopyByInvok.F.equals(selected.getEngineerQualificationsFlag())){
            zkbIntegralOrderStatusVo.setToolBoxAuditBtnFlag(false);
        }else {
            zkbIntegralOrderStatusVo.setToolBoxAuditBtnFlag(true);
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbIntegralOrder> getDAO() {
        return zkbIntegralOrderRepository;
    }
}

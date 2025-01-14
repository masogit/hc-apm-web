package com.ge.apm.view.zkb;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbIntegralOrderAuditService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbIntegralOrderAuditController extends GenericCRUDUUIDController<ZkbIntegralOrder> {

    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    private AssetInfoRepository assetInfoRepository;
    private TenantInfoRepository tenantInfoRepository;

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

    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();

    String encodeStr;

    @Getter @Setter
    List<ZkbPartOrderCat> zkbPartOrderCatList = new ArrayList<>();

    private String queryStatus;

    @Override
    protected void init() {
        super.init();
        zkbIntegralOrderRepository = WebUtil.getBean(ZkbIntegralOrderRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
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
            if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
                zkbContract = zkbContractList.get(0);
            }

            if(selected == null || StrUtil.isEmpty(selected.getId())){
                WebUtil.addErrorMessage("数据不存在！");
                return;
            }

            zkbIntegralOrderAuditService.fieldRendered(currentUser, zkbIntegralOrderStatusVo, selected);
            zkbIntegralOrderAuditService.initPageData(selected, zkbContract, zkbIntegralOrderStatusVo);
            zkbPartOrderCatList = zkbIntegralOrderAuditService.queryCatList();
        }

    }

    public String normalNext(boolean flag){
        zkbIntegralOrderAuditService.normalNext(selected, currentUser, zkbContract, flag);
        return "List?faces-redirect=true&queryStatus="+queryStatus ;
    }


    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus ;
    }


    @Override
    protected GenericRepositoryUUID<ZkbIntegralOrder> getDAO() {
        return zkbIntegralOrderRepository;
    }
}

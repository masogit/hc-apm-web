package com.ge.apm.view.zkb;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.ZkbContractCatQuotaHistoryRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.service.DbMessageSource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class ZkbContractCatQuotaHistoryController extends GenericCRUDUUIDController<ZkbContractCatQuotaHistory> {

    private ZkbContractCatQuotaHistoryRepository zkbContractCatQuotaHistoryRepository;

    private Page<ZkbContractCatQuotaHistory> zkbContractCatQuotaHistoryPage;

    private UserAccountRepository userAccountRepository;

    private ZkbPartOrderRepository zkbPartOrderRepository;

    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    @Getter@Setter
    private String createdDateFormat;

    @Getter@Setter
    private String lastModifiedDateFormat;



    @Override
    protected void init() {
        super.init();
        zkbContractCatQuotaHistoryRepository = WebUtil.getBean(ZkbContractCatQuotaHistoryRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        zkbPartOrderRepository = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
    }

    public void initReturnTYpe(){

    }

    @Override
    protected Page<ZkbContractCatQuotaHistory> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        zkbContractCatQuotaHistoryPage = zkbContractCatQuotaHistoryRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbContractCatQuotaHistoryPage;
    }

    @Override
    protected GenericRepositoryUUID<ZkbContractCatQuotaHistory> getDAO() {
        return zkbContractCatQuotaHistoryRepository;
    }

    public String returnTypeFieldRender(String partOrderNo, String partOrderId){
        if(StrUtil.isEmpty(partOrderNo)){
            return "";
        }
        if(partOrderNo.startsWith("PO")){
            ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(partOrderId);
            if(zkbPartOrder != null && StrUtil.isNotEmpty(zkbPartOrder.getReturnType())){
                String key = "zkbPoReturnType" + "-" + zkbPartOrder.getReturnType();
                return WebUtil.getMessage(key);
            }
            return zkbPartOrder.getReturnType();
        }else if(partOrderNo.startsWith("FEPO")){
            ZkbFePartOrder zkbFePartOrder = zkbFePartOrderRepository.findById(partOrderId);
            if(zkbFePartOrder != null && StrUtil.isNotEmpty(zkbFePartOrder.getReturnType())){
                String key = "zkbFePartOrder_zkbPoReturnType" + "-" + zkbFePartOrder.getReturnType();
                return WebUtil.getMessage(key);
            }
            return zkbFePartOrder.getReturnType();
        }
        return null;
    }

    public String getFieldNameByType(String type, String val){
        if("assetType".equals(type)){
            switch (val){
                case "highMachine":
                    return "高端机";
                case "lowMachine":
                    return "低端机";
                default:
                    return "";
            }
        }else if("status".equals(type)){
            switch (val){
                case "Y":
                    return "已使用";
                case "N":
                    return "已返还";
                default:
                    return "";
            }
        }else if("probeType".equals(type)){
            switch (val){
                case "OldProbe":
                    return "探头名额";
                case "Any":
                    return "探头任意名额";
                case "Normal":
                    return "探头普通名额";
                case "Special":
                    return "探头特殊名额";
                default:
                    return "";
            }
        }
        return "";
    }

    public String getUserNameByUid(String uid){
        UserAccount userAccount = userAccountRepository.getByUid(uid);
        return userAccount == null ? "" : userAccount.getName();
    }

    public void updateData(){
        zkbContractCatQuotaHistoryRepository.save(selected);
        selected = null;
    }
}

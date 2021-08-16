package com.ge.apm.view.asset;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 503202755 2020/12/14
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderUserConfigController extends GenericCRUDUUIDController<ZkbFePartOrderUserConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbFePartOrderUserConfigController.class);

    /**
     * dao
     */
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;

    /**
     * 分页数据
     */
    private Page<ZkbFePartOrderUserConfig> zkbFePartOrderUserConfig;


    /**
     * 获取用户信息
     */
    private UserAccountRepository userAccountRepository;

    /**
     * 判断用户
     */
    private UserContextService userContextService;

    /**
     * 当前用户tenantUID
     */
    private String tenantUID;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    private Boolean supplier = false;

    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
        userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
        userContextService=WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(!userContextService.isZkbAdmin())
        {
            this.tenantUID=currentUser.getTenantUID();
        }
        String loginName = currentUser.getLoginName();
        List<ZkbFePartOrderUserConfig> zkbFePartOrderUserConfigList = this.zkbFePartOrderUserConfigRepository.findByLoginNameAndConfigType(loginName,ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString());
        if(CollectionUtils.isNotEmpty(zkbFePartOrderUserConfigList))
        {
            supplier=true;
        }

    }


    @Override
    protected GenericRepositoryUUID<ZkbFePartOrderUserConfig> getDAO() {
        return this.zkbFePartOrderUserConfigRepository;
    }
    /**
     * 列表查询数据
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbFePartOrderUserConfig> loadData(PageRequest pageRequest) { List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(!userContextService.isZkbAdmin())
        {
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }
        if(userContextService.isZkbAdmin()&&StringUtils.isNotBlank(this.tenantUID))
        {
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }
        zkbFePartOrderUserConfig=zkbFePartOrderUserConfigRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbFePartOrderUserConfig;
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {

        UserAccount userAccount = this.userAccountRepository.getByLoginName(this.selected.getLoginName());
        if(userAccount==null)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameNotExist"));
            return;
        }
        if(!userContextService.isZkbAdmin())
        {

            if(userAccount.getTenantUID()==null||!userAccount.getTenantUID().equals(this.tenantUID))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameNotExist"));
                return;
            }
        }
        if(StringUtils.isBlank(this.selected.getId()))
        {
            List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.selected.getLoginName());
            if(CollectionUtils.isNotEmpty(list))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                return;
            }
        }else
            {
                List<ZkbFePartOrderUserConfig> list =this.zkbFePartOrderUserConfigRepository.findByLoginNameAndIdNot(this.selected.getLoginName(),this.selected.getId());
                if(CollectionUtils.isNotEmpty(list))
                {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                    return;
                }
            }
        this.selected.setTenantUid(userAccount.getTenantUID());
        super.save();
        this.selected=null;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        if(!userContextService.isZkbAdmin())
        {
            this.selected.setConfigType("Supplier");
        }

    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public Boolean getSupplier() {
        return supplier;
    }

    public void setSupplier(Boolean supplier) {
        this.supplier = supplier;
    }
}

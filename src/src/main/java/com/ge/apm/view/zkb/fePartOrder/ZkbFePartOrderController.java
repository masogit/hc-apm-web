package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbFePartOrderController.class);

    /**
     * 智康宝人工申请 service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 获取审批角色配置信息
     */
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbFePartOrder> zkbFePartOrderPage;

    public final static String CREATE_ACTION = "Create";

    public final static String EDIT_ACTION="Edit";

    /**
     * 查询哪个选项卡状态
     */
    private String queryStatus = MY_WORK;

    /**
     * 我的待办
     */
    private static final String MY_WORK="myWork";

    /**
     * 全部
     */
    private static final String ALL="all";

    /**
     * 没权限查看
     */
    private static final String NO_STEP="noStep";

    /**
     * 当前用户登录名
     */
    private String loginName = "";

    /**
     * 当前用户tenantUid
     */
    private String tenantUID="";

    /**
     * 当前用户在config中的配置权限
     */
    private Map<String, String> configTypeMap = new HashMap<>();

    /**
     * 判断是否400团队
     */
    private Boolean fourHundredsTeam = false;

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        this.queryStatus = WebUtil.getRequestParameter("queryStatus");
        this.tenantUID=currentUser.getTenantUID();
        if(StringUtils.isEmpty(this.queryStatus)){
            this.queryStatus = MY_WORK;
        }
        configTypeMap=this.getMapConfigByLoginName();
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            fourHundredsTeam=true;
        }
    }


    /**
     * 列表加载
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbFePartOrder> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = getSearchFilter(this.searchFilters);
        zkbFePartOrderPage=zkbFePartOrderRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbFePartOrderPage;
    }

    /**
     * 动态组装列表过滤条件
     * @param csearchFilters
     * @return
     */
    public List<SearchFilter> getSearchFilter(List<SearchFilter> csearchFilters) {
        List<SearchFilter> searchFilters = csearchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        //判断是不是我的待办选项卡
        if(MY_WORK.equals(this.queryStatus))
        {
            this.getSearchFilterByUserConfig(searchFilters);
        }else{
            this.getAllSearchFilterByUserConfig(searchFilters);
            if(StringUtils.isNotBlank(this.queryStatus)&&!ALL.equals(this.queryStatus))
            {
                searchFilters.add((new SearchFilter("currentStep", SearchFilter.Operator.EQ,queryStatus)));
            }
        }
        return searchFilters;
    }

    /**
     * 根据配置信息动态组装SearchFilter过滤条件
     * @param searchFilters
     * @return
     */
    private void getSearchFilterByUserConfig(List<SearchFilter> searchFilters)
    {
        if(!(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,NO_STEP));
            return;
        }
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingApm.toString()));
            return;
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString()));
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString()));
        }
    }

    /**
     * 根据登录账户名获取当前账户名流程配置信息
     * @return
     */
    private Map<String, String> getMapConfigByLoginName()
    {
        Map<String, String> configTypeMap = new HashMap<>();
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.loginName);
        if(CollectionUtils.isEmpty(list))
        {
            return configTypeMap;
        }
        for(ZkbFePartOrderUserConfig item:list)
        {
            if(item==null) continue;
            if(configTypeMap.containsKey(item.getConfigType())) continue;
            configTypeMap.put(item.getConfigType(),item.getConfigType());
        }
        return configTypeMap;
    }

    /**
     * 根据配置信息动态组装SearchFilter过滤条件
     * @param searchFilters
     * @return
     */
    private void getAllSearchFilterByUserConfig(List<SearchFilter> searchFilters)
    {
        if(!(this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,NO_STEP));
            return;
        }
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }
    }


    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    /**
     * 跳转新增模式
     * @return
     */
    public String intoCreateModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action="+ZkbFePartOrderController.CREATE_ACTION+"&id="+"&queryStatus="+queryStatus);
    }

    /**
     * 跳转编辑模式
     * @return
     */
    public String intoEditModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action="+ZkbFePartOrderController.EDIT_ACTION+"&id="+this.selected.getId()+"&queryStatus="+queryStatus);
    }

    public String queryListByStatus(String status){
        queryStatus = status;
        return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
    }

    public void doubleClick(SelectEvent event){
        String str = "action="+ZkbFePartOrderController.EDIT_ACTION+"&id="+this.selected.getId()+"&queryStatus="+queryStatus;
        String url = "Edit.xhtml?str="+UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public Boolean getFourHundredsTeam() {
        return fourHundredsTeam;
    }

    public void setFourHundredsTeam(Boolean fourHundredsTeam) {
        fourHundredsTeam = fourHundredsTeam;
    }
}

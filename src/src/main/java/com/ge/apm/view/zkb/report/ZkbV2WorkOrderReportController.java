package com.ge.apm.view.zkb.report;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbV2WorkOrderReportService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 报表统计
 */
@ManagedBean
@ViewScoped
public class ZkbV2WorkOrderReportController extends GenericCRUDUUIDController<V2_WorkOrder> {

    private static final Logger logger = LoggerFactory.getLogger(ZkbV2WorkOrderReportController.class);

    private Map<String,String> yearMap = new LinkedHashMap<>();

    private Map<String,String> yearMonthMap = new LinkedHashMap<>();

    private Map<String,String> monthMap = new LinkedHashMap<>();

    /**
     * 统计
     */
    private ZkbV2WorkOrderReportService zkbV2WorkOrderReportService;

    /**
     * 统计列表
     */
    private List<Map<String, Object>> zkbV2WorkOrderReportList;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * 当前用户登录名
     */
    private String loginName = "";

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        zkbV2WorkOrderReportService= WebUtil.getBean(ZkbV2WorkOrderReportService.class);
        String testLoginName = "n";
        if(this.loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            testLoginName=ZkbV2WorkOrderReportService.TEST_LOGIN_NAME;
        }
        zkbV2WorkOrderReportList=this.zkbV2WorkOrderReportService.statisticsOrderCount(yearMap,yearMonthMap,monthMap,testLoginName);
    }

    /**
     * 获取合计
     * @param item 供应商统计数据
     * @return
     */
    public Integer takeTotal(Map<String, Object> item)
    {
        Integer totalOrderCount = new Integer(0);
        for(String key:yearMap.keySet())
        {
            totalOrderCount=totalOrderCount+new Integer((Integer) item.get(key));
        }
        return totalOrderCount;
    }

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        return super.loadData(pageRequest);
    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return null;
    }

    public List<Map<String, Object>> getZkbV2WorkOrderReportList() {
        return zkbV2WorkOrderReportList;
    }

    public void setZkbV2WorkOrderReportList(List<Map<String, Object>> zkbV2WorkOrderReportList) {
        this.zkbV2WorkOrderReportList = zkbV2WorkOrderReportList;
    }
    public Map<String, String> getYearMap() {
        return yearMap;
    }

    public void setYearMap(Map<String, String> yearMap) {
        this.yearMap = yearMap;
    }

    public Map<String, String> getYearMonthMap() {
        return yearMonthMap;
    }

    public void setYearMonthMap(Map<String, String> yearMonthMap) {
        this.yearMonthMap = yearMonthMap;
    }

    public Map<String, String> getMonthMap() {
        return monthMap;
    }

    public void setMonthMap(Map<String, String> monthMap) {
        this.monthMap = monthMap;
    }
}

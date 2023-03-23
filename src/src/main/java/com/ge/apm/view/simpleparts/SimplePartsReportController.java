package com.ge.apm.view.simpleparts;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.simpleparts.SimplePartsReportVo;
import com.ge.apm.service.simpleparts.SimplePartsReportService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseUserService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

/**
 * @author by lyj 503277717
 * @date 2022/5/17.
 */
@ManagedBean
@ViewScoped
public class SimplePartsReportController extends GenericCRUDUUIDController<SimplePartsItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsReportController.class);

    private SimplePartsReportService simplePartsReportService;

    private UserContextService userContextService;

    private SimplePartsWarehouseUserService partsWarehouseUserService;

    private Date startTime;
    private Date endTime;

    @Override
    protected GenericRepositoryUUID<SimplePartsItem> getDAO() {
        return null;
    }

    private List<SimplePartsReportVo> simplePartsReportVoList;

    @Override
    protected void init() {
        simplePartsReportService = WebUtil.getBean(SimplePartsReportService.class);
        userContextService =  WebUtil.getBean(UserContextService.class);
        partsWarehouseUserService = WebUtil.getBean(SimplePartsWarehouseUserService.class);
    }

    public void previewData(){
        if(userContextService.isCountyArea()){
            TenantInfo tenantInfo = userContextService.getCurrentUserTenantInfo();
            simplePartsReportVoList = this.simplePartsReportService.querySimplePartsReportList(startTime, endTime, MapUtil.newHashMap("tenantUid", tenantInfo.getUid()));
        } else {
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            List<String> partsWarehouses = partsWarehouseUserService.findAllByPartsWarehouseByUserUid(currentUserAccount.getUid());
            simplePartsReportVoList = this.simplePartsReportService.querySimplePartsReportList(startTime, endTime, MapUtil.newHashMap("partsWarehouseId", ObjectUtil.isNotEmpty(partsWarehouses) ? partsWarehouses : ListUtil.toList("-1")));
        }
    }

    public List<SimplePartsReportVo> getSimplePartsReportVoList() {
        return simplePartsReportVoList;
    }

    public void setSimplePartsReportVoList(List<SimplePartsReportVo> simplePartsReportVoList) {
        this.simplePartsReportVoList = simplePartsReportVoList;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}

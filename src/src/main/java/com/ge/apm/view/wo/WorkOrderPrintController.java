package com.ge.apm.view.wo;

import com.ge.apm.dao.JmReportRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.domain.JmReport;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.extern.slf4j.Slf4j;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Objects;

@Slf4j
@ManagedBean
@ViewScoped
public class WorkOrderPrintController extends GenericCRUDUUIDController<V2_ServiceRequest> implements Serializable, ServerEventInterface {
    private static final long serialVersionUID = 1L;
    private ServiceRequestRepository dao;

    private JmReportRepository jmReportRepository;

    private V2_ServiceRequest selected;

    @Override
    protected void init() {
        this.dao = WebUtil.getBean(ServiceRequestRepository.class);
        this.jmReportRepository = WebUtil.getBean(JmReportRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String srId = request.getParameter("sr_id");
        selected = dao.findById(srId);
    }

    @Override
    protected GenericRepositoryUUID<V2_ServiceRequest> getDAO() {
        return dao;
    }

    /**
     * 生成工单备件打印积木报表url
     *
     * @return
     */
    public String partsPrintJmReportUrl() {
        JmReport partPrintReport = jmReportRepository.findPartPrintReport();
        if (Objects.nonNull(partPrintReport)) {
            return partPrintReport.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&sr_id=" + selected.getId();
        } else {
            WebUtil.addErrorMessage("请先配置工单备件打印报表");
            return null;
        }
    }

}
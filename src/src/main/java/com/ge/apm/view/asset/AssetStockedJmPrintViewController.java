package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.JmReportRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.JmReport;
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
public class AssetStockedJmPrintViewController extends GenericCRUDUUIDController<ArrivalListItem> implements Serializable, ServerEventInterface {
    private static final long serialVersionUID = 1L;
    private ArrivalListItemRepository dao;
    private JmReportRepository jmReportRepository;

    private String stockNo;
    private String userName;

    @Override
    protected void init() {
        this.dao = WebUtil.getBean(ArrivalListItemRepository.class);
        this.jmReportRepository = WebUtil.getBean(JmReportRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        stockNo = request.getParameter("stockNo");
        userName = request.getParameter("userName");
    }

    @Override
    protected GenericRepositoryUUID<ArrivalListItem> getDAO() {
        return dao;
    }

    /**
     * 生成工单备件打印积木报表url
     *
     * @return
     */
    public String stockPrintJmReportUrl() {
        JmReport partPrintReport;
        if (stockNo.startsWith("RK")) {
            partPrintReport = jmReportRepository.findReportByName("到货入库单");
        } else {
            partPrintReport = jmReportRepository.findReportByName("到货出库单");
        }
        if (Objects.nonNull(partPrintReport)) {
            return partPrintReport.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&inStockNo=" + stockNo + "&outStockNo=" + stockNo + "&userName=" + userName;
        } else {
            WebUtil.addErrorMessage("请先配置打印报表");
            return null;
        }
    }

}
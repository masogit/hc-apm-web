package com.ge.apm.view;

import com.ge.apm.dao.JmReportRepository;
import com.ge.apm.domain.JmReport;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
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
import java.util.Map;
import java.util.Objects;

@Slf4j
@ManagedBean
@ViewScoped
public class JmPrintViewController extends GenericCRUDUUIDController<JmReport> implements Serializable, ServerEventInterface {
    private static final long serialVersionUID = 1L;
    private JmReportRepository dao;
    private String type;

    private UserAccount loginUser;
    private HttpServletRequest request;

    @Override
    protected void init() {
        this.dao = WebUtil.getBean(JmReportRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        type = request.getParameter("type");
    }

    @Override
    protected GenericRepositoryUUID<JmReport> getDAO() {
        return dao;
    }

    /**
     * 生成工单备件打印积木报表url
     *
     * @return
     */
    public String printJmReportUrl() {
        JmReport partPrintReport = null;
        Map<String, String> param = MapUtil.newHashMap();
        switch (type) {
            case "departmentConvert":
                param.put("convertNo", request.getParameter("convertNo"));
                partPrintReport = dao.findReportByName("转科单");
                break;
            case "assetStock" :
                String stockNo = request.getParameter("stockNo");
                param.put("inStockNo", stockNo);
                param.put("outStockNo", stockNo);
                param.put("userName", request.getParameter("userName"));
                if (stockNo.startsWith("RK")) {
                    partPrintReport = dao.findReportByName("到货入库单");
                } else {
                    partPrintReport = dao.findReportByName("到货出库单");
                }
                break;
            case "workOrderDetail" :
                String woId = request.getParameter("woId");
                String srId = request.getParameter("srId");
                param.put("woId", woId);
                param.put("srId", srId);
                partPrintReport = dao.findReportByName("维修详情");
                break;
            default:
                break;
        }
        return getViewUrl(partPrintReport, param);
    }

    private String getViewUrl(JmReport partPrintReport, Map<String, String> param) {
        String viewUrl = "";
        if (ObjectUtil.isNotEmpty(partPrintReport)) {
            StringBuilder requestParam = new StringBuilder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                requestParam.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            viewUrl = partPrintReport.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + requestParam;
        } else {
            WebUtil.addErrorMessage("请先配置打印报表");
        }
        return viewUrl;
    }

}
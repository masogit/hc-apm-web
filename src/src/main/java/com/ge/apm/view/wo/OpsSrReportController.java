package com.ge.apm.view.wo;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.OpsSrReport;
import com.ge.apm.service.wo.OpsSrReportService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class OpsSrReportController extends MyCRUDController<OpsSrReport> implements Serializable {

    private OpsSrReportService opsSrReportService;

    protected void init() {
        opsSrReportService = WebUtil.getBean(OpsSrReportService.class);
    }

    @Override
    protected BaseMapper<OpsSrReport> getDAO() {
        return null;
    }

    @Override
    protected List<OpsSrReport> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        EntityWrapper<OpsSrReport> ew = new EntityWrapper<>();
        wrapSearchFilter(ew);
        lazyModel.setRowCount(opsSrReportService.selectCount(ew));
        ew.orderBy("srNumber", isAscending);
        return opsSrReportService.loadData(pageRequest, ew);
    }

}
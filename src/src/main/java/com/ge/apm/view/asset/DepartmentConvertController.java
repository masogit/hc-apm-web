package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetDepartmentConvertRepository;
import com.ge.apm.dao.SiteToSiteAllocationRepository;
import com.ge.apm.domain.AssetDepartmentConvert;
import com.ge.apm.domain.SiteToSiteAllocation;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class DepartmentConvertController extends GenericCRUDUUIDController<AssetDepartmentConvert> {

    private String oneSearchStr;
    private AssetDepartmentConvertRepository dao;
    private UaaService uuaService;
    private Date filterBeginTime;
    private Date returnAtTime;
    @Override
    protected AssetDepartmentConvertRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao=WebUtil.getBean(AssetDepartmentConvertRepository.class);
        uuaService=WebUtil.getBean(UaaService.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    protected Page<AssetDepartmentConvert> loadData(PageRequest pageRequest) {
        //        searchFilters.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,sharedList));
        setUserSearchFilters();
        if(oneSearchStr != null){
            this.updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetDeptNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
        if(filterBeginTime != null){
            searchFilters.add(new SearchFilter("transferOutAt", SearchFilter.Operator.GTE,filterBeginTime));
        }
        if(returnAtTime != null){
            searchFilters.add(new SearchFilter("transferInAt", SearchFilter.Operator.GTE,returnAtTime));
        }
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("assetName");
            this.removeFilterOnField("assetNumber");
        }
    }
    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getUserAccessibleSiteListWithClinicalDept(user.getUid()).stream()
                .filter(item -> item.getOrgType() == 4)
                .map(item -> item.getUid())
                .collect(Collectors.toList());
        this.updateSearchFilter(new SearchFilter("outDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("inDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        this.updateSearchFilter(new SearchFilter("outSiteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        this.updateSearchFilter(new SearchFilter("inSiteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));

    }

    public void postProcessXLS(Object document) {
        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow header = sheet.getRow(0);

        XSSFCellStyle cellStyle = wb.createCellStyle();//XSSFColor.HSSFColorPredefined.GREEN.getIndex()
        XSSFColor xssfColor = new XSSFColor(new Color(212, 212, 212));
        cellStyle.setFillForegroundColor(xssfColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THICK);
        cellStyle.setBorderLeft(BorderStyle.THICK);
        cellStyle.setBorderRight(BorderStyle.THICK);
        cellStyle.setBorderTop(BorderStyle.THICK);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
           Cell cell = header.getCell(i);

            cell.setCellStyle(cellStyle);
        }
    }

    public Date getFilterBeginTime() {
        return filterBeginTime;
    }

    public void setFilterBeginTime(Date filterBeginTime) {
        this.filterBeginTime = filterBeginTime;
    }

    public Date getReturnAtTime() {
        return returnAtTime;
    }

    public void setReturnAtTime(Date returnAtTime) {
        this.returnAtTime = returnAtTime;
    }

    public String getFileName(){
        String result="借用信息";
        try {
            String formatStr="yyyyMMddHHmmss";
            SimpleDateFormat format=new SimpleDateFormat(formatStr);
            String format1 = format.format(new Date());
            result+="_"+format1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

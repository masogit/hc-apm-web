package com.ge.apm.view.secondment;

import com.ge.apm.dao.SiteToSiteAllocationRepository;
import com.ge.apm.domain.SiteToSiteAllocation;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
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
public class DepartmentSecondmentController extends GenericCRUDUUIDController<SiteToSiteAllocation> {

    private String oneSearchStr;
    private SiteToSiteAllocationRepository dao;
    private UaaService uuaService;
    private Date filterBeginTime;
    private Date returnAtTime;
    @Override
    protected SiteToSiteAllocationRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao=WebUtil.getBean(SiteToSiteAllocationRepository.class);
        uuaService=WebUtil.getBean(UaaService.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    protected Page<SiteToSiteAllocation> loadData(PageRequest pageRequest) {
        //        searchFilters.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,sharedList));
        setUserSearchFilters();
        if(oneSearchStr != null){
            this.updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetDeptNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
        if(filterBeginTime != null){
            searchFilters.add(new SearchFilter("borrowedAt", SearchFilter.Operator.GTE,filterBeginTime));
        }
        if(returnAtTime != null){
            searchFilters.add(new SearchFilter("returnAt", SearchFilter.Operator.GTE,returnAtTime));
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
        this.updateSearchFilter(new SearchFilter("outSiteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("inSiteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));

    }

    public void postProcessXLS(Object document) {
        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow header = sheet.getRow(0);

        XSSFCellStyle cellStyle = wb.createCellStyle();//XSSFColor.HSSFColorPredefined.GREEN.getIndex()
        XSSFColor xssfColor = new XSSFColor(new Color(212, 212, 212));
        cellStyle.setFillForegroundColor(xssfColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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
//    public void saveDate(){
//        for(int i=0;i<100;i++){
//
//            dao.save(getOne(i));
//        }
//
//    }
//    String [] arr={
//            "722c8ebf796944c69955e5f6e795ffc4",
//            "3542446d2da14beebf3a2c2b9438f33e",
//            "4460fa2cc1ad466db350595560e21cd9",
//            "f0db6e2d3aaa4d758db050b1abd62db0",
//            "70171ae7bc80454dbc838414ae71f1d6",
//            "acc233e9194f4824b39d13e80cef3798",
//            "b53bcdf702a94e61b581d1e2ed88387a",
//            "8339d3aad54540fda2656be07c5fbf65",
//            "3d261042ef454b83934c0c396569d283",
//            "836b3624c0764a9fb9904d7c6d3ce0bc",
//            "e910eb063f344728860fc466190282d8",
//            "48f3d575e77a4305832c3f305356b185",
//            "58cc25ad221a44f1bec21691a4001018",
//            "f40c60c94df14a96aec3aa2474ea70f2"};
//    private SiteToSiteAllocation getOne(int i){
//        Random random = new Random();
//        String s = String.valueOf(i);
//        SiteToSiteAllocation siteToSiteAllocation = new SiteToSiteAllocation();
//        siteToSiteAllocation.setAssetUid(s);
//        siteToSiteAllocation.setAssetName(s);
//        siteToSiteAllocation.setReturnAt(new Date());
//        siteToSiteAllocation.setBorrowedAt(new Date());
//        siteToSiteAllocation.setInDeptUid(arr[random.nextInt(14)]);
//        siteToSiteAllocation.setOutDeptUid(arr[random.nextInt(14)]);
//        siteToSiteAllocation.setInSiteUid(arr[random.nextInt(14)]);
//        siteToSiteAllocation.setOutSiteUid(arr[random.nextInt(14)]);
//        return siteToSiteAllocation;
//    }
}

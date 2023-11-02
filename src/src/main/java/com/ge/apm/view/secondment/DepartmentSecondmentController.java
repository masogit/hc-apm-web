package com.ge.apm.view.secondment;

import com.ge.apm.dao.SiteToSiteAllocationRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.SiteToSiteAllocation;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private List<DepartmentSecondmentProcess> process;
    private UserAccountRepository userAccountRepository;
    @Override
    protected SiteToSiteAllocationRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao=WebUtil.getBean(SiteToSiteAllocationRepository.class);
        uuaService=WebUtil.getBean(UaaService.class);
        userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
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


    public void onRowSelect(SelectEvent event){
        System.out.println(event);
        process=new ArrayList<>();
        SiteToSiteAllocation target = selected;
        List<String> strings = conditionList(target);
        if(!CollectionUtils.isEmpty(strings)){
            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(strings);
            Map<String, UserAccount> collect = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, item -> item));
            addNode("借入待确认",collect.get(target.getBorrowerAck()),target.getBorrowerAckDate());
            addNode("借入待确认(主任)",collect.get(target.getBorrowerSignatureUid()),target.getBorrowerSignatureDate());
            addNode("借出待确认(主任)",collect.get(target.getLenderSignatureUid()),target.getLenderSignatureDate());
            addNode("送还待确认",collect.get(target.getReceiverUid()),target.getReceiverDate());
            addNode("送还待确认(主任)",collect.get(target.getReturnerSignatureUid()),target.getReturnerSignatureDate());
            addNode("收回待确认(主任) ",collect.get(target.getReceiverSignatureUid()),target.getReceiverSignatureDate());
        }
    }
    private List<String> conditionList(SiteToSiteAllocation target ){
        Set<String> condition = new HashSet<>();
        condition.add(target.getBorrowerAck()); // 借用确认
        condition.add(target.getLenderSignatureUid()); //借出方主任确认
        condition.add(target.getBorrowerSignatureUid()); // 接受方确认
        condition.add(target.getReturnerAck());
        condition.add(target.getReceiverSignatureUid());
        condition.add(target.getReturnerSignatureUid());
        condition.add(target.getReceiverUid());
        return condition.stream().filter(item -> StringUtils.hasText(item)).collect(Collectors.toList());
    }
    private void addNode(String nodeName,UserAccount operator, Date operatorTime){
        String name = "";
        if(operator != null){
            name = operator.getName();
        };
        DepartmentSecondmentProcess processNode=new DepartmentSecondmentProcess();
        processNode.setNodeName(nodeName);
        processNode.setOperator(name);
        processNode.setOperatorTime(operatorTime);
        process.add(processNode);
    }

    public List<DepartmentSecondmentProcess> getProcess() {
        return process;
    }

    public void setProcess(List<DepartmentSecondmentProcess> process) {
        this.process = process;
    }
}

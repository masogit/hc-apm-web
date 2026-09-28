package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetDepartmentConvertRepository;
import com.ge.apm.dao.SiteToSiteAllocationRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.DepartmentConvertService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.preview.QRPreViewerBean;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
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
public class DepartmentConvertController extends GenericCRUDUUIDController<AssetDepartmentConvert> {

    private String oneSearchStr;
    private AssetDepartmentConvertRepository dao;
    private UaaService uuaService;
    private Date filterBeginTime;
    private Date returnAtTime;

    private Integer status;
    private List<OrgInfo> clinicalDeptList;
    private String inSite;
    private String outSite;
    private List<AssetDepartmentConvert> currentPageAssets;

    //复选的设备
    private Map<String, Boolean> selectedMap;
    private  StreamedContent transferOrderFile;
    private UserAccount user;

    private Boolean isInsulate;
    private TenantInfoRepository tenantInfoRepository;

    private DepartmentConvertService departmentConvertService;

    private QRPreViewerBean qrPreViewerBean;

    @Override
    protected AssetDepartmentConvertRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetDepartmentConvertRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        this.selectedMap = new HashMap();

        user = UserContextService.getCurrentUserAccount();

        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        departmentConvertService = WebUtil.getBean(DepartmentConvertService.class);
        qrPreViewerBean = WebUtil.getBean(QRPreViewerBean.class);

        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
        loadClinicalDeptList();
    }

    @Override
    protected Page<AssetDepartmentConvert> loadData(PageRequest pageRequest) {
        //        searchFilters.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,sharedList));
        setUserSearchFilters();
        if (oneSearchStr != null) {
            this.updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetDeptNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
        if (filterBeginTime != null) {
            searchFilters.add(new SearchFilter("transferOutAt", SearchFilter.Operator.GTE, filterBeginTime));
        }
        if (returnAtTime != null) {
            searchFilters.add(new SearchFilter("transferInAt", SearchFilter.Operator.GTE, returnAtTime));
        }

        Page<AssetDepartmentConvert> res =null;
        if (this.searchFilters == null) {
            res= dao.findAll(pageRequest);
        } else {
            res= dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
        currentPageAssets = res.getContent();

        return res;
    }


    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (AssetDepartmentConvert item : currentPageAssets) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        if (oneSearchStr == null) {
            return;
        }
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
                .filter(item -> Objects.equals(item.getOrgType(), 4))
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

    public String getFileName() {
        String result = "设备转科信息";
        try {
            String formatStr = "yyyyMMddHHmmss";
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            String format1 = format.format(new Date());
            result += "_" + format1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getStatusName(Integer status) {

        String stringStatus = null;
        switch (status) {
            case -1:
                stringStatus="已取消";
                break;
            case 1:
                stringStatus="转入确认中";
                break;
            case 2:
                stringStatus="审批中";
                break;
            case 3:
            case 4:
                stringStatus="已完成";
                break;
            case 5:
                stringStatus="已拒绝";
                break;
            default:
                break;


        }
        return stringStatus;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }
    public StreamedContent getTransferOrderFile() {
        return transferOrderFile;
    }

    public void exportTransferOrderFile() {
        try {
            transferOrderFile = null;
            List<String> selectedIds = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
            transferOrderFile = departmentConvertService.export(selectedIds);
        } catch (IllegalStateException e) {
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("exportTransferOrderFile error:",e);
            WebUtil.addErrorMessage("Fail to download report");
        }
    }
    public void prepareViewQrcode(){
        List<String> selectedIds = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("id", SearchFilter.Operator.IN,selectedIds));
        List<AssetDepartmentConvert> assetDepartmentConvertList = dao.findBySearchFilter(searchFilters);
        if(CollectionUtils.isEmpty(assetDepartmentConvertList)) return;
        List<String> assetUidList = assetDepartmentConvertList.stream().filter(item-> StringUtils.hasText(item.getAssetUid())).map(AssetDepartmentConvert::getAssetUid).collect(Collectors.toList());
        List<AssetInfo> assetInfoList = departmentConvertService.getAssetInfoList(assetUidList);
        if(CollectionUtils.isEmpty(assetInfoList)) return;
        List<String> collect = assetInfoList.stream().filter(item -> StringUtils.hasText(item.getQrCode())).map(item -> item.getQrCode() + ":" + item.getSiteUID()).collect(Collectors.toList());
        qrPreViewerBean.onServerEvent("",collect);
    }

    public String openNewWindowView(String convertNo) {
//             "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=";
        return "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=departmentConvert&amp;convertNo=" + convertNo;
    }

    public String getJmNewWindowView(String convertNo) {
//             "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=";
        return "/portal/JmPrintReportView.xhtml?type=departmentConvert&convertNo=" + convertNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uuaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 3)).collect(Collectors.toList());
    }

    public List<OrgInfo> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public String getInSite() {
        return inSite;
    }

    public void setInSite(String inSite) {
        this.inSite = inSite;
    }

    public String getOutSite() {
        return outSite;
    }

    public void setOutSite(String outSite) {
        this.outSite = outSite;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }
}
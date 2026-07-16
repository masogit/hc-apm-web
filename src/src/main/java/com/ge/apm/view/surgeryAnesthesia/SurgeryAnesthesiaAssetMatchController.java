package com.ge.apm.view.surgeryAnesthesia;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentConnectRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentMatchRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetInfoDto;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentConnect;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentMatch;
import com.ge.apm.service.surgeryAnesthesia.SurgeryAnesthesiaAssetService;
import com.ge.apm.service.surgeryAnesthesia.SurgeryAnesthesiaEquipmentImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.his.HisOrgComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StreamUtils;

/**
 * 手术室绩效管理 - 设备匹配Controller
 * 用于匹配APM台账与手麻系统中的设备
 */
@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaAssetMatchController extends GenericCRUDUUIDController<SurgeryAnesthesiaEquipmentMatch> implements ServerEventInterface {

    public static final String FIELD_NAME_ORG_CODE = "orgCode";
    public static final String FIELD_NAME_SITE_UID = "siteUid";

    private OrgInfoRepository orgInfoDao;
    private SurgeryAnesthesiaEquipmentMatchRepository dao;
    private SurgeryAnesthesiaEquipmentConnectRepository connDao;
    private AssetInfoRepository assetInfoRepository;
    private SurgeryAnesthesiaAssetService surgeryAnesthesiaAssetService;
    private HisOrgComponentController orgComponentController;

    @Setter
    private DualListModel<AssetInfo> apmAvailableAssets = new DualListModel<>();

    @Setter
    private DualListModel<SurgeryAnesthesiaEquipmentConnect> connAvailableAssets = new DualListModel<>();

    @Getter
    @Setter
    private AssetInfoDto assetInfoDto;

    @Getter
    @Setter
    private SurgeryAnesthesiaEquipmentConnect surgeryAnesthesiaEquipmentConnect;

    // 初始化定义pageRequest, 提供给待匹配的页面使用
    final PageRequest pageRequest = new PageRequest(0, 100);

    @Override
    protected SurgeryAnesthesiaEquipmentMatchRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(SurgeryAnesthesiaEquipmentMatchRepository.class);
        connDao = WebUtil.getBean(SurgeryAnesthesiaEquipmentConnectRepository.class);
        surgeryAnesthesiaAssetService = WebUtil.getBean(SurgeryAnesthesiaAssetService.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);

        surgeryAnesthesiaEquipmentConnect = new SurgeryAnesthesiaEquipmentConnect();
        assetInfoDto = new AssetInfoDto();
        assetInfoDto.setPurchasePrice(500000d);

        orgComponentController.registerController(this);
    }

    /**
     * 查询APM台账设备(左侧待选设备)
     */
    public void firstQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();

        searchFiltersAsset.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        searchFiltersAsset.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        searchFiltersAsset.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        if (selectedOrg != null) {
            List<String> accessibleSites = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
            searchFiltersAsset.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
        }

        if (assetInfoDto.getPurchasePrice() != null && assetInfoDto.getPurchasePrice().compareTo(0d) > 0) {
            searchFiltersAsset.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, assetInfoDto.getPurchasePrice()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getDepartNum())) {
            searchFiltersAsset.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, assetInfoDto.getDepartNum().trim()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getName())) {
            searchFiltersAsset.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetInfoDto.getName().trim()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getFunctionType())) {
            searchFiltersAsset.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetInfoDto.getFunctionType().trim()));
        }

        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchFiltersAsset, pageRequest).getContent();
        apmAvailableAssets.setSource(assetInfos);
    }

    /**
     * 查询手麻系统设备(右侧待选设备)
     */
    public void secondQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();

        searchFiltersAsset.add(new SearchFilter("isMatched", SearchFilter.Operator.EQ, false));

        if (StringUtils.isNotBlank(surgeryAnesthesiaEquipmentConnect.getDeviceName())) {
            searchFiltersAsset.add(new SearchFilter("deviceName", SearchFilter.Operator.LIKE, surgeryAnesthesiaEquipmentConnect.getDeviceName().trim()));
        }
        if (StringUtils.isNotBlank(surgeryAnesthesiaEquipmentConnect.getDeviceCode())) {
            searchFiltersAsset.add(new SearchFilter("deviceCode", SearchFilter.Operator.LIKE, surgeryAnesthesiaEquipmentConnect.getDeviceCode().trim()));
        }
        if (StringUtils.isNotBlank(surgeryAnesthesiaEquipmentConnect.getOrgCode())) {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_ORG_CODE, SearchFilter.Operator.LIKE, surgeryAnesthesiaEquipmentConnect.getOrgCode().trim()));
        }

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        if (selectedOrg != null) {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_ORG_CODE, SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_SITE_UID, SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        } else {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_ORG_CODE, SearchFilter.Operator.IsNull, null));
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_SITE_UID, SearchFilter.Operator.IsNull, null));
        }
        searchFiltersAsset.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        List<SurgeryAnesthesiaEquipmentConnect> conns = connDao.findBySearchFilter(searchFiltersAsset, pageRequest).getContent();

        List<SurgeryAnesthesiaEquipmentConnect> targets = connAvailableAssets.getTarget();
        if (!targets.isEmpty()) {
            // 去除重新查询的数据中不包含已加入的数据
            Set<String> uidSet = targets.stream().map(SurgeryAnesthesiaEquipmentConnect::getId).collect(Collectors.toSet());
            conns.stream().filter(item -> !uidSet.contains(item.getId())).collect(Collectors.toList());
        }

        connAvailableAssets.setSource(conns);
    }

    /**
     * 提交匹配保存数据
     * APM与手麻系统设备是1对多关系
     */
    public void match() {
        List<AssetInfo> apmTargetAssets = apmAvailableAssets.getTarget();
        List<SurgeryAnesthesiaEquipmentConnect> connTargetAssets = connAvailableAssets.getTarget();

        if (apmTargetAssets.size() != 1 || connTargetAssets.isEmpty()) {
            WebUtil.addErrorMessageKey("请选中一条匹配！");
            return;
        }

        List<String> ids = connTargetAssets.stream().map(SurgeryAnesthesiaEquipmentConnect::getId).collect(Collectors.toList());
        List<SurgeryAnesthesiaEquipmentConnect> lists = connDao.findMatchedByIds(ids);
        if (!lists.isEmpty()) {
            WebUtil.addErrorMessageKey("选中的数据已经匹配，重新选择！");
            return;
        }

        surgeryAnesthesiaAssetService.match(apmTargetAssets.get(0), connTargetAssets);

        clearSelectedInPickList();
    }

    private void clearSelectedInPickList() {
        apmAvailableAssets.getTarget().clear();
        connAvailableAssets.getTarget().clear();
    }

    public void apmOnAssetsTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                if (apmAvailableAssets.getTarget().size() > 1) {
                    WebUtil.addErrorMessageKey("APM已选设备只允许选中一条！");
                    apmAvailableAssets.getTarget().remove(item);
                    apmAvailableAssets.getSource().add((AssetInfo) item);
                } else {
                    apmAvailableAssets.getSource().remove(item);
                }
            }
        }
    }

    public void connOnAssetsTransfer(TransferEvent event) {
        // no need to control
    }

    public void refreshAfterOrgChange() {
        firstQueryOrder();
        secondQueryOrder();
    }

    public DualListModel<AssetInfo> getApmAvailableAssets() {
        return apmAvailableAssets;
    }

    public DualListModel<SurgeryAnesthesiaEquipmentConnect> getConnAvailableAssets() {
        return connAvailableAssets;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        clearSelectedInPickList();
        firstQueryOrder();
        secondQueryOrder();
    }

    // ========== 设备导入相关 ==========

    private UploadedFile file;
    private List<Map<String, Object>> importMapList;
    private List<Map<String, Object>> errorList;
    private SurgeryAnesthesiaEquipmentImportService surgeryAnesthesiaEquipmentImportService;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }

    /**
     * 下载手麻系统设备导入模板
     */
    public void downloadTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(SurgeryAnesthesiaEquipmentImportService.SHEET_NAME);
            XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("device_code");
            headerRow.createCell(1).setCellValue("device_name");
            headerRow.createCell(2).setCellValue("org_code");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=SurgeryEquipment_Connect_Template.xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            StreamUtils.copy(inputStream, response.getOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            Logger.getLogger(SurgeryAnesthesiaAssetMatchController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("下载模板失败--" + e.getMessage());
        }
    }

    /**
     * 上传手麻系统设备Excel文件
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            if (surgeryAnesthesiaEquipmentImportService == null) {
                surgeryAnesthesiaEquipmentImportService = WebUtil.getBean(SurgeryAnesthesiaEquipmentImportService.class);
            }

            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int maxRowNum = SurgeryAnesthesiaEquipmentImportService.EXCEL_HEADER_ROW + 5000;
            if (doc.exceedMaxRowNum(SurgeryAnesthesiaEquipmentImportService.SHEET_NAME, maxRowNum)) {
                WebUtil.addErrorMessage("导入Excel中数据行数最多5000条，请分批导入");
                return;
            }
            importMapList = surgeryAnesthesiaEquipmentImportService.getMapFromTemplate(doc);
            surgeryAnesthesiaEquipmentImportService.importData(importMapList);
            errorList = surgeryAnesthesiaEquipmentImportService.filterErrorRecords(importMapList);

            if (!errorList.isEmpty()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "导入完成，存在失败记录",
                        "共有" + errorList.size() + "条失败记录");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                WebUtil.addSuccessMessage("导入完成");
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage("解析导入文件出错--" + e.getMessage());
            Logger.getLogger(SurgeryAnesthesiaAssetMatchController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    /**
     * 获取模板字段说明列表
     */
    public List<TemplateField> getTemplateFieldList() {
        return Arrays.asList(
                new TemplateField("device_code", "手麻系统设备识别码", true),
                new TemplateField("device_name", "手麻系统设备名称", true),
                new TemplateField("org_code", "医院机构代码", true)
        );
    }

    /**
     * 模板字段说明内部类
     */
    @Getter
    @Setter
    public static class TemplateField {
        private String name;
        private String description;
        private boolean required;

        public TemplateField(String name, String description, boolean required) {
            this.name = name;
            this.description = description;
            this.required = required;
        }
    }
}
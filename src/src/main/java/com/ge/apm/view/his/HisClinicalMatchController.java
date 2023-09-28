package com.ge.apm.view.his;

import com.ge.apm.dao.HisAdministrativeOfficesMatchRepository;
import com.ge.apm.dao.HisAdministrativeOfficesRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisAdministrativeOfficesMatch;
import com.ge.apm.service.his.HisClinicalService;
import com.ge.apm.service.his.load.HisAdministrativeOfficeImportService;
import com.ge.apm.service.his.load.HisEquipmentImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.asset.ContractImportController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@ManagedBean
@ViewScoped
public class HisClinicalMatchController extends GenericCRUDUUIDController<HisAdministrativeOfficesMatch> implements ServerEventInterface {
    private OrgInfoRepository orgInfoDao;
    private HisAdministrativeOfficesMatchRepository dao;
    private HisAdministrativeOfficesRepository connDao;
    private HisClinicalService hisClinicalService;
    private HisOrgComponentController orgComponentController;

    @Setter
    private DualListModel<OrgInfo> availableApmDepts = new DualListModel<>();

    @Setter
    private DualListModel<HisAdministrativeOffices> availableOffices = new DualListModel<>();

    @Getter
    @Setter
    private List<OrgInfo> targetApmDept = new ArrayList<>();

    @Getter
    @Setter
    private List<HisAdministrativeOffices> connTargetOffices = new ArrayList<>();

    @Setter @Getter
    private String name;
    @Setter @Getter
    private String namePyCode;
    @Setter @Getter
    private String name2;
    @Setter @Getter
    private String namePyCode2;

    private HisAdministrativeOfficeImportService  hisAdministrativeOfficeImportService;

    @Override
    protected void init() {
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(HisAdministrativeOfficesMatchRepository.class);
        connDao = WebUtil.getBean(HisAdministrativeOfficesRepository.class);
        hisClinicalService = WebUtil.getBean(HisClinicalService.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        hisAdministrativeOfficeImportService= WebUtil.getBean(HisAdministrativeOfficeImportService.class);
        orgComponentController.registerController(this);
    }

    public void firstQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();

        searchFiltersAsset.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        searchFiltersAsset.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));

        OrgInfo selectedOrg = getCurrentOrg();
        if (selectedOrg != null) {
            List<String> accessibleSites = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
            searchFiltersAsset.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
        }

        if (StringUtils.isNotBlank(namePyCode)) {
            searchFiltersAsset.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, namePyCode.trim()));
        }
        if (StringUtils.isNotBlank(name)) {
            searchFiltersAsset.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name.trim()));
        }

        targetApmDept = new ArrayList<>();
        List<OrgInfo> orgInfos = orgInfoDao.findBySearchFilter(searchFiltersAsset);
        availableApmDepts = new DualListModel<>(orgInfos, targetApmDept);
    }

    public void secondQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();
        if (StringUtils.isNotBlank(namePyCode2)) {
            searchFiltersAsset.add(new SearchFilter("hisKsid", SearchFilter.Operator.LIKE, namePyCode2.trim()));
        }
        if (StringUtils.isNotBlank(name2)) {
            searchFiltersAsset.add(new SearchFilter("hisKsname", SearchFilter.Operator.LIKE, name2.trim()));
        }

        OrgInfo selectedOrg = getCurrentOrg();
        if (selectedOrg != null) {
            searchFiltersAsset.add(new SearchFilter("hosId", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
        } else {
            searchFiltersAsset.add(new SearchFilter("hosId", SearchFilter.Operator.IsNull, null));
        }

        if (null == connTargetOffices) {
            connTargetOffices = new ArrayList<>();
        }

        //searchFiltersAsset.add(new SearchFilter("isMatched", SearchFilter.Operator.EQ, false));
        List<HisAdministrativeOffices> conns = connDao.findBySearchFilter(searchFiltersAsset);
        availableOffices = new DualListModel<>(conns, connTargetOffices);
    }

    private OrgInfo getCurrentOrg() {
        return orgComponentController.getSelectedOrg();
    }

    public void moveApmDeptByTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetApmDept.add((OrgInfo)item);
            } else {
                targetApmDept.remove(item);
            }
        }
    }

    public void moveHisOfficesByTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                connTargetOffices.add((HisAdministrativeOffices)item);
            } else {
                connTargetOffices.remove(item);
            }
        }
    }

    //提交匹配保存数据
    public void match() {

        if (targetApmDept.size() != 1 || connTargetOffices.size() != 1) {
            WebUtil.addErrorMessageKey("请选中一条匹配！");
            return;
        }

        OrgInfo source = targetApmDept.get(0);
        HisAdministrativeOffices target = connTargetOffices.get(0);
        HisAdministrativeOfficesMatch hisAdministrativeOfficesMatch = dao.findByHisAdmIdAndOrgInfoUid(target.getId(), source.getUid());
        if(null != hisAdministrativeOfficesMatch){
            WebUtil.addErrorMessageKey("该数据已存在！");
            return;
        }

        hisClinicalService.match(source, target);

        clearSelectedInPickList();

        WebUtil.addSuccessMessage("匹配成功！");

    }

    private void clearSelectedInPickList() {
        targetApmDept.clear();
        connTargetOffices.clear();
        availableApmDepts.getTarget().clear();
        availableOffices.getTarget().clear();
    }

    @Override
    protected HisAdministrativeOfficesMatchRepository getDAO() {
        return dao;
    }

    public void refreshAfterOrgChange() {
        firstQueryOrder();
        secondQueryOrder();
    }

    public DualListModel<OrgInfo> getAvailableApmDepts() {
        return availableApmDepts;
    }

    public DualListModel<HisAdministrativeOffices> getAvailableOffices() {
        return availableOffices;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        clearSelectedInPickList();

        refreshAfterOrgChange();
    }

    private UploadedFile file;
    private List<Map<String, Object>> importMapList;
    private List<Map<String, Object>> errorList;
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            //检查导入模板版本
//            if(!checkDocVersion(doc)){
//                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--"+WebUtil.getMessage("TemplateVersionIncorrect"));
//                return;
//            }

            int maxRowNum = HisAdministrativeOfficeImportService.EXCEL_CONTRACT_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(HisAdministrativeOfficeImportService.SHEET_SERVICE_CONTRACT, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            importMapList = hisAdministrativeOfficeImportService.getMapFromTemplate(doc);
            hisAdministrativeOfficeImportService.importData(importMapList);
            errorList = hisAdministrativeOfficeImportService.filterErrorRecords(importMapList);
            if(!errorList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }

    public String selectSiteName(String siteUID) {
        OrgInfo byUid = orgInfoDao.findByUid(siteUID);
        return byUid.getName();
    }
}



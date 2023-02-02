package com.ge.apm.view.zkb;

import cn.hutool.core.util.StrUtil;
import com.ge.apm.dao.ZkbHighPartInfoRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbHighPartInfo;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbHighPartService;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2022/12/27
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbHighPartController extends GenericCRUDUUIDController<ZkbHighPartInfo> {

    private ZkbHighPartInfoRepository dao;
    private Page<ZkbHighPartInfo> zkbHighPartInfoPage;
    private UploadedFile file;
    private StreamedContent exportFile;
    private List<Map<String, Object>> errorContractList = new ArrayList<>();
    private List<Map<String, Object>> importMapList;
    private ZkbHighPartService zkbHighPartService;
    private UserAccount currentUser;
    private String actionName;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbHighPartInfoRepository.class);
        zkbHighPartService = WebUtil.getBean(ZkbHighPartService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<ZkbHighPartInfo> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        zkbHighPartInfoPage = dao.findBySearchFilter(searchFilters, pageRequest);
        return zkbHighPartInfoPage;
    }


    @Override
    protected GenericRepositoryUUID<ZkbHighPartInfo> getDAO() {
        return dao;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbHighPartInfo();
        this.selected.setCreatedBy(this.currentUser.getLoginName());
        this.selected.setCreatedDate(new Date());
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());

    }

    @Override
    public void save(){
        boolean isNull = this.selected.isNull();
        if(isNull){
            WebUtil.addErrorMessage("保存失败,不能保存空数据!");
            return;
        }
        if(currentUser==null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameNotExist"));
            return;
        }
        selected.setPartNo(selected.getPartNo().trim());
        if(!checkSave()){
            return;
        }
        dao.save(selected);
        selected = null;
    }

    public boolean checkSave(){
        List<ZkbHighPartInfo> zkbHighPartInfos = dao.findByPartNo(selected.getPartNo());
        if(StrUtil.isEmpty(selected.getId())){
            if (zkbHighPartInfos != null && zkbHighPartInfos.size() > 0){
                WebUtil.addErrorMessage("备件号已存在！");
                return false;
            }
        }else{
            boolean b  = true;
            for(ZkbHighPartInfo p : zkbHighPartInfos){
                if(p.getPartNo().equals(selected.getPartNo()) &&!p.getId().equals(selected.getId())){
                    WebUtil.addErrorMessage("备件号已存在！");
                    return false;
                }
            }
        }
        return true;
    }

    public void cancel(){
        selected = null;
    }

    public String importAction(){
        return UrlEncryptController.encodeUrlParam("Import?1=1");
    }

    public void uploadDataFile(FileUploadEvent event){
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbHighPartService.EXCEL_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(0, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            importMapList = zkbHighPartService.getZkbHighPartMapFromTemplate(doc);
            if(importMapList != null && importMapList.size() > 0){
                zkbHighPartService.importData(importMapList,currentUser);
                errorContractList = zkbHighPartService.filterErrorRecords(importMapList);
                if(errorContractList ==null){
                    errorContractList = new ArrayList<>();
                }
            }else {
                errorContractList = new ArrayList<>();
            }
            if(!errorContractList.isEmpty()){
                int failCount = errorContractList.size();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+failCount+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(ZkbContractService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ZkbContractService.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    public String getTemplateName(){
        return "excel/zkb_highpart_import_template.xlsx";
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Map<String, Object>> getErrorContractList() {
        return errorContractList;
    }

    public void setErrorContractList(List<Map<String, Object>> errorContractList) {
        this.errorContractList = errorContractList;
    }
}

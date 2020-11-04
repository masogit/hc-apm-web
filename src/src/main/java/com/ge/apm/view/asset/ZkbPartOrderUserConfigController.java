package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.ZkbPartOrderUserConfig;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbPartOrderUserConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author 503202755 2020/9/25
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderUserConfigController extends GenericCRUDUUIDController<ZkbPartOrderUserConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbPartOrderUserConfigController.class);

    /**
     * service
     */
    private ZkbPartOrderUserConfigService zkbPartOrderUserConfigService;

    /**
     * dao
     */
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbPartOrderUserConfig> zkbPartOrderUserConfigPage;

    /**
     * 导入文件
     */
    private UploadedFile file;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorList;


    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbPartOrderUserConfigService = WebUtil.getBean(ZkbPartOrderUserConfigService.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrderUserConfig> getDAO() {
        return this.zkbPartOrderUserConfigRepository;
    }

    /**
     * 列表查询数据
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbPartOrderUserConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        zkbPartOrderUserConfigPage=zkbPartOrderUserConfigRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbPartOrderUserConfigPage;
    }

    /**
     * 文件上传
     * @param event  文件流
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbPartOrderUserConfigService.EXCEL_USER_CONFIG_HEADER_ROW + ZkbPartOrderUserConfigService.EXCEL_USER_CONFIG_IMPORT_MAX_ROW;
            if(doc.exceedMaxRowNum(ZkbPartOrderUserConfigService.SHEET_SERVICE_KEY_USER_CONFIG_INDEX, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            List<Map<String, Object>> importUserConfigMapList = zkbPartOrderUserConfigService.getConfigMapFromTemplate(doc);
            zkbPartOrderUserConfigService.importData(importUserConfigMapList);
            errorList = zkbPartOrderUserConfigService.filterErrorRecords(importUserConfigMapList);
            if(!errorList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }


    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "Import?faces-redirect=true";
    }

    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        return ZkbPartOrderUserConfigService.EXCEL_TEMPLATE_NAME;
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        boolean isNull = this.selected.isNull();
        if(isNull){
            WebUtil.addErrorMessage("保存失败,不能保存空数据!");
            return;
        }
        if(StringUtils.isNotBlank(this.selected.getLoginName()))
        {
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginName(this.selected.getLoginName());
            if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfigList))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                return;
            }
        }
        super.save();
        this.selected = null;
    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Map<String, Object>> errorList) {
        this.errorList = errorList;
    }
}

package com.ge.apm.view.assetTypeGroup;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.assetTypeGroup.AssetTypeService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgInfoController;
import lombok.Data;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@Data
public class AuthImportController extends GenericCRUDController<OrgInfo> implements Serializable, ServerEventInterface {

    private OrgInfoRepository dao;
    private UserAccount currentUser;
    private AssetTypeService service;
    private UploadedFile file;
    private List<Map> importFailedDataList;
    private OrgInfoController orgInfoController;
    private AssetTypeGroupController authGroupingController;
    private StreamedContent excelFile;

    private  boolean hasFailData;
    private  boolean hasExitingData;




    @Override
    protected void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
        service = WebUtil.getBean(AssetTypeService.class);
        authGroupingController=WebUtil.getBean(AssetTypeGroupController.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepository<OrgInfo> getDAO() {
        // TODO Auto-generated method stub
        return dao;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(OrgInfo object) {
        super.onBeforeNewObject(object);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        this.file = event.getFile();
        importFailedDataList = service.importAuthGroup(this.file, currentUser);

        hasFailData=service.isFailData();
        hasExitingData=service.isExistingData();

        authGroupingController.buildOrdTree();
    }

    public void exportExcel() throws IOException {

    }


    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        return super.loadData(pageRequest);
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }
}

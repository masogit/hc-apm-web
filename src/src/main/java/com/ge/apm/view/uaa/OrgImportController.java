package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
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
public class OrgImportController extends GenericCRUDController<OrgInfo> implements Serializable, ServerEventInterface {

    private OrgInfoRepository dao;
    private UserAccount currentUser;
    private OrgInfoService service;
    private UploadedFile file;
    private List<Map> importFailedDataList;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
        service = WebUtil.getBean(OrgInfoService.class);
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
        importFailedDataList = service.importOrg(this.file, currentUser);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<Map> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<Map> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        return super.loadData(pageRequest);
    }
}

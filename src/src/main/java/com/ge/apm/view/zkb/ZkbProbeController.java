package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbPartOrderCatRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbPartOrderCat;
import com.ge.apm.service.zkb.ZkbProbeService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by lyj 503277717
 * @date 2021/11/3.
 */
@ManagedBean
@ViewScoped
public class ZkbProbeController extends GenericCRUDUUIDController<ZkbPartOrderCat> implements Serializable, ServerEventInterface {

    private ZkbPartOrderCatRepository dao;

    private List<ZkbPartOrderCat> currentPageZkbPartOrderCats;

    private UploadedFile file;

    private List<ZkbPartOrderCat> errorImportList;

    private ZkbProbeService zkbProbeService;

    private UserAccount currentUser;

    @Override
    protected GenericRepositoryUUID<ZkbPartOrderCat> getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        super.init();
        currentUser = UserContextService.getCurrentUserAccount();
        zkbProbeService = WebUtil.getBean(ZkbProbeService.class);
        dao = WebUtil.getBean(ZkbPartOrderCatRepository.class);
    }

    @Override
    protected Page<ZkbPartOrderCat> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        Page<ZkbPartOrderCat> res = dao.findBySearchFilter(searchFilters, pageRequest);
        currentPageZkbPartOrderCats = res.getContent();
        return res;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException{
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        errorImportList = zkbProbeService.handleExcel(file, currentUser);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }
    @Override
    public void save() {
        super.save();
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void onBeforeNewObject(ZkbPartOrderCat object) {
        super.onBeforeNewObject(object);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    public List<ZkbPartOrderCat> getErrorImportList() {
        return errorImportList;
    }

    public void setErrorImportList(List<ZkbPartOrderCat> errorImportList) {
        this.errorImportList = errorImportList;
    }
}


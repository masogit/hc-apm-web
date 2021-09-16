package com.ge.apm.view.USProject;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.USProUpRepository;
import com.ge.apm.dao.USProjectRepository;
import com.ge.apm.domain.USProject;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

@ManagedBean
@ViewScoped
public class USProjectController extends JpaCRUDController<USProject> {

    private static final long serialVersionUID = -1L;
    private USProjectRepository dao;
    private BlobObjectRepository blobDao;
    private USProUpRepository uSProUpRepository;

    @Override
    protected USProjectRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(USProjectRepository.class);
        uSProUpRepository = WebUtil.getBean(USProUpRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<USProject> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getDetailPage(Integer id, String name) {
        String operation = "USProjectDetail.xhtml?actionName=View&selectedid=" + id.toString() + "&name=" + name;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String id, String name) {
        String operation = "USProjectCreate.xhtml?actionName=Edit&selectedid=" + id + "&name=" + name;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteUSProject(USProject imageInfo) {
        dao.delete(imageInfo);
        uSProUpRepository.deleteByName(imageInfo.getName());
        blobDao.deleteByBoUuid(imageInfo.getName());
        logger.info("delete USProject  successful. name  is:" + imageInfo.getName());
    }


}

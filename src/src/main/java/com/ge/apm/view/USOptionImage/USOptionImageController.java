package com.ge.apm.view.USOptionImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.USOptionImageRepository;
import com.ge.apm.domain.USOptionImage;
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
public class USOptionImageController extends JpaCRUDController<USOptionImage> {

    private static final long serialVersionUID = -1L;
    private USOptionImageRepository dao;
    private BlobObjectRepository blobDao;

    @Override
    protected USOptionImageRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(USOptionImageRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<USOptionImage> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getDetailPage(Integer id, String USSegment, String productName) {
        String operation = "USOptionImageDetail.xhtml?actionName=View&selectedid=" + id.toString() + "&USSegment=" + USSegment+ "&productName=" + productName;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String id, String USSegment, String productName) {
        String operation = "USOptionImageCreate.xhtml?actionName=Edit&selectedid=" + id + "&USSegment=" + USSegment+ "&productName=" + productName;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteUSOptionImage(USOptionImage imageInfo) {
        dao.delete(imageInfo);
        blobDao.deleteByBoUuid(imageInfo.getUsSegment()+"_"+imageInfo.getProductName());
        logger.info("delete IBUInfo image successful. USSegment  is:" + imageInfo.getUsSegment() + " ProductName  is: " + imageInfo.getProductName());
    }

}

package com.ge.apm.view.ProbeRecommendImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.ProbeRecommendImageRepository;
import com.ge.apm.domain.ProbeRecommendImage;
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
public class ProbeRecommendImageController extends JpaCRUDController<ProbeRecommendImage> {

    private static final long serialVersionUID = -1L;
    private ProbeRecommendImageRepository dao;
    private BlobObjectRepository blobDao;

    @Override
    protected ProbeRecommendImageRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(ProbeRecommendImageRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<ProbeRecommendImage> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getDetailPage(Integer id, String catNo) {
        String operation = "ProbeRecommendImageDetail.xhtml?actionName=View&selectedid=" + id.toString() + "&catNo=" + catNo;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String id, String catNo) {
        String operation = "ProbeRecommendImageCreate.xhtml?actionName=Edit&selectedid=" + id + "&catNo=" + catNo;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteProbeRecommendImage(ProbeRecommendImage imageInfo) {
        dao.delete(imageInfo);
        blobDao.deleteByBoUuid(imageInfo.getCatNo());
        logger.info("delete CatNo image successful. catNo  is:" + imageInfo.getCatNo());
    }

}

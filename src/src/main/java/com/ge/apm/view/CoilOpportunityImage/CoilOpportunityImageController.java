package com.ge.apm.view.CoilOpportunityImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.CoilOpportunityImageRepository;
import com.ge.apm.domain.CoilOpportunityImage;
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
public class CoilOpportunityImageController extends JpaCRUDController<CoilOpportunityImage> {

    private static final long serialVersionUID = -1L;
    private CoilOpportunityImageRepository dao;
    private BlobObjectRepository blobDao;

    @Override
    protected CoilOpportunityImageRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(CoilOpportunityImageRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<CoilOpportunityImage> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getDetailPage(Integer id, String cataLog) {
        String operation = "coilOpportunityImageDetail.xhtml?actionName=View&selectedid=" + id.toString() + "&cataLog=" + cataLog;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String id, String cataLog) {
        String operation = "coilOpportunityImageCreate.xhtml?actionName=Edit&selectedid=" + id + "&cataLog=" + cataLog;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteCoilOpportunityImage(CoilOpportunityImage imageInfo) {
        dao.delete(imageInfo);
        // 刪除货号同时删除该货号下的图片信息
        blobDao.deleteByBoUuid(imageInfo.getCataLog());
        logger.info("delete coilOpportunity image successful. cata log is:" + imageInfo.getCataLog());
    }

}

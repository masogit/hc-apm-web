package com.ge.apm.view.IBUInfoImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.IBUInfoImageRepository;
import com.ge.apm.domain.IBUInfoImage;
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
public class IBUInfoImageController extends JpaCRUDController<IBUInfoImage> {

    private static final long serialVersionUID = -1L;
    private IBUInfoImageRepository dao;
    private BlobObjectRepository blobDao;

    @Override
    protected IBUInfoImageRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(IBUInfoImageRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<IBUInfoImage> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getDetailPage(Integer id, String ibuOffer) {
        String operation = "IBUInfoImageDetail.xhtml?actionName=View&selectedid=" + id.toString() + "&ibuOffer=" + ibuOffer;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String id, String ibuOffer) {
        String operation = "IBUInfoImageCreate.xhtml?actionName=Edit&selectedid=" + id + "&ibuOffer=" + ibuOffer;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteIBUInfoImage(IBUInfoImage imageInfo) {
        dao.delete(imageInfo);
        // 刪除货号同时删除该货号下的图片信息
        blobDao.deleteByBoUuid(imageInfo.getIbuOffer());
        logger.info("delete IBUInfo image successful. IbuOffer  is:" + imageInfo.getIbuOffer());
    }

}

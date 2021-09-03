package com.ge.apm.view.USProject;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.USProUpRepository;
import com.ge.apm.dao.USProjectRepository;
import com.ge.apm.domain.USProject;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
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
        //删除文件
        if(StringUtils.isNotEmpty(imageInfo.getPdf())){
            //删除之前，先查询 是否有其他方案用此说明书,有则不删
           if(dao.countByPdf(imageInfo.getPdf()) == 0) {
               String folderNameLocal = this.getClass().getResource("").getPath().substring(0, this.getClass().getResource("").getPath().indexOf("target"))
                       + "src/main/webapp/admin/usProject/US";
               deleteFile("C:/US/", imageInfo.getPdf());
               deleteFile(folderNameLocal, imageInfo.getPdf());
           }
        }
        logger.info("delete IBUInfo image successful. name  is:" + imageInfo.getName());
    }

    public static boolean deleteFile(String path,String fileName) {
        boolean flag = false;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        File file;
            if (path.endsWith(File.separator)) {
                file = new File(path + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
            if (file.isFile()) {
                file.delete();
            }
        return flag;
    }

}

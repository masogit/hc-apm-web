package com.ge.apm.view.i18n;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.i18n.AssetGroupSecondaryImportService;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class AssetGroupSecondaryImportController extends JpaCRUDController<I18nMessage> {

    TenantInfoRepository tenantInfoDao;
    UserAccountRepository userAccountDao;
    I18nMessageRepository i18nMessageDao;

    AssetGroupSecondaryImportService importService;

    List<TenantInfo> tenantInfos;
    String tenantUid;

    private UploadedFile file;

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();

    }

    @Override
    protected GenericRepository<I18nMessage> getDAO() {
        return i18nMessageDao;
    }

    @Override
    protected void init() {
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        i18nMessageDao = WebUtil.getBean(I18nMessageRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        importService = WebUtil.getBean(AssetGroupSecondaryImportService.class);

        tenantInfos = tenantInfoDao.getAllOrderByName();
    }

    public void onSelectTenant() {

    }

    public void uploadDataFile(FileUploadEvent event) {

        this.file = event.getFile();


        try (ExcelDocumentStreamingReader excelDoc = new ExcelDocumentStreamingReader(file.getInputstream(),
                0, 1,
                null, null);)
        {

            importService.importAssetGroupSecondary(tenantUid, excelDoc);


        } catch (IOException e) {
            WebUtil.addErrorMessage("File can not be read!", null);
            logger.error(e.getMessage(),e);

        } catch (Throwable e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage(), null);
            logger.error(e.getMessage(),e);
        }
    }

    public List<TenantInfo> getTenantInfos() {
        return tenantInfos;
    }

    public void setTenantInfos(List<TenantInfo> tenantInfos) {
        this.tenantInfos = tenantInfos;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }
}
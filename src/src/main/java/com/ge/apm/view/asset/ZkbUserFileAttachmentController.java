package com.ge.apm.view.asset;

import com.ge.apm.dao.FileAttachmentOrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.FileAttachmentOrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.zkb.V2_BlobObjectService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author 212605082 on 2019/12/9.
 */
@ManagedBean
@ViewScoped
public class ZkbUserFileAttachmentController extends GenericCRUDUUIDController<FileAttachmentOrgInfo> {


    private FileAttachmentOrgInfoRepository dao;
    private TenantInfoRepository tenantInfoDao;

    private UserAccount currentUser;

    /**
     * 获取附件订单
     */
    private V2_BlobObjectService v2_BlobObjectService;

    /**
     * 主目录
     */
    private String mainDirectory;

    /**
     * 子目录
     */
    private String secondDirectory;

    /**
     * 名称
     */
    private String objectName;

    /**
     * 文件类型
     */
    private String boSubType;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 附件列表
     */
    private List<V2_BlobObject> v2_BlobObjectList;

    @Override
    protected void init() {
        dao = WebUtil.getBean(FileAttachmentOrgInfoRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        v2_BlobObjectService = WebUtil.getBean(V2_BlobObjectService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loadData();
    }

    @Override
    protected FileAttachmentOrgInfoRepository getDAO() {
        return dao;
    }

    /**
     *列表查询
     */
    public void loadData()
    {
        String tenantUID = currentUser.getTenantUID();
        if(StringUtils.isEmpty(tenantUID))
        {
            v2_BlobObjectList=null;
            return;
        }
        v2_BlobObjectList=this.v2_BlobObjectService.loadZkbOrderHistoryData(this.mainDirectory,
                this.secondDirectory,
                this.objectName,
                this.boSubType,
                this.operationType,
                tenantUID);
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    public String getSecondDirectory() {
        return secondDirectory;
    }

    public void setSecondDirectory(String secondDirectory) {
        this.secondDirectory = secondDirectory;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getBoSubType() {
        return boSubType;
    }

    public void setBoSubType(String boSubType) {
        this.boSubType = boSubType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<V2_BlobObject> getV2_BlobObjectList() {
        return v2_BlobObjectList;
    }

    public void setV2_BlobObjectList(List<V2_BlobObject> v2_BlobObjectList) {
        this.v2_BlobObjectList = v2_BlobObjectList;
    }
}

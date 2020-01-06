package com.ge.apm.service.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.FileAttachmentOrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.FileAttachmentOrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;

/**
 * @author 212605082 on 2019/12/4.
 */
@Component
public class ZkbFileAttachmentService {

    public enum ZkbMainDirectory {
        DXR, OEC, CT, ULS, Others
    }

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private BlobObjectRepository blobObjectDao;
    @Autowired
    private BlobFileService blobFileService;
    @Autowired
    private FileAttachmentOrgInfoRepository fileAttachmentOrgInfoDao;

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        searchFilters.add(new SearchFilter("testStatus", SearchFilter.Operator.NE, -1));

        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public List<TenantInfo> getTargetTenantInfo(V2_BlobObject blobObject){
        List<TenantInfo> tenantInfoList = new ArrayList<>();
        List<FileAttachmentOrgInfo> fileAttachmentOrgInfoList = fileAttachmentOrgInfoDao.findByBlobObjectUid(blobObject.getId());

        for (FileAttachmentOrgInfo fileAttachmentOrgInfo : fileAttachmentOrgInfoList) {
            tenantInfoList.add(tenantInfoDao.findByuid(fileAttachmentOrgInfo.getTenantUID()));
        }

        return tenantInfoList;
    }

    @Transactional
    public void saveZkbFileAttachment(V2_BlobObject blobObject, List<V2_BlobObject> uploadedFileList, List<TenantInfo> tenantInfoList){

        blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB);
        blobObject.setCreatedDate(new Date());
        blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());

        for (V2_BlobObject tempBlobObject: uploadedFileList) {

            tempBlobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB);
            tempBlobObject.setCreatedDate(new Date());
            tempBlobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());

            tempBlobObject.setBoSubType(blobObject.getBoSubType());
            tempBlobObject.setOperationType(blobObject.getOperationType());
            tempBlobObject.setMainDirectory(blobObject.getMainDirectory());
            tempBlobObject.setSecondDirectory(blobObject.getSecondDirectory());

            blobObjectDao.save(tempBlobObject);

            for (TenantInfo tenantInfo: tenantInfoList) {
                FileAttachmentOrgInfo tempFileAttachmentOrgInfo = new FileAttachmentOrgInfo();
                tempFileAttachmentOrgInfo.setBlobObjectUid(tempBlobObject.getId());
                tempFileAttachmentOrgInfo.setTenantUID(tenantInfo.getUid());
                fileAttachmentOrgInfoDao.save(tempFileAttachmentOrgInfo);
            }
        }
    }

    @Transactional
    public void updateZkbFileAttachment(V2_BlobObject blobObject, List<TenantInfo> tenantInfoList){

        blobObject.setLastModifiedDate(new Date());
        blobObject.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());

        fileAttachmentOrgInfoDao.deleteByBlobObjectUid(blobObject.getId());

        for (TenantInfo tenantInfo: tenantInfoList) {
            FileAttachmentOrgInfo tempFileAttachmentOrgInfo = new FileAttachmentOrgInfo();
            tempFileAttachmentOrgInfo.setBlobObjectUid(blobObject.getId());
            tempFileAttachmentOrgInfo.setTenantUID(tenantInfo.getUid());
            fileAttachmentOrgInfoDao.save(tempFileAttachmentOrgInfo);
        }

        blobObjectDao.save(blobObject);

    }

    @Transactional
    public void deleteZkbFileAttachment(String blobObjectUid){

        fileAttachmentOrgInfoDao.deleteByBlobObjectUid(blobObjectUid);

        blobFileService.deleteBlobFileById(blobObjectUid);
    }

    public V2_BlobObject fileUpload(UploadedFile file){

        V2_BlobObject blobObject = blobFileService.uploadFile(file);

        return blobObject;

    }

    public void removeUploadFile(String blobId){
        blobFileService.deleteBlobFileById(blobId);
    }

    public Map<String, List<String>> initZkbSecondDirectory(){

        Map<String, List<String>> secondDirectoryMap = new HashMap<>();

        for (ZkbMainDirectory zkbMainDirectory : ZkbMainDirectory.values()) {

            List<String> secondDirectoryList = new ArrayList<>();

            if(zkbMainDirectory.equals(ZkbMainDirectory.DXR)){
                secondDirectoryList.add("DR6000");
                secondDirectoryList.add("DR6000-2");
                secondDirectoryList.add("DR6000-3");
                secondDirectoryList.add("DR646");
                secondDirectoryList.add("DR646HD");
                secondDirectoryList.add("DR8000");
                secondDirectoryList.add("DR650");
                secondDirectoryList.add("DR656");
                secondDirectoryList.add("DR656G2");
                secondDirectoryList.add("DR656HD");
                secondDirectoryList.add("DR-F");
                secondDirectoryList.add("DR515");
                secondDirectoryList.add("DR575");
                secondDirectoryList.add("XR6000");
                secondDirectoryList.add("DR118");
                secondDirectoryList.add("DR316");
                secondDirectoryList.add("AMX4");
                secondDirectoryList.add("AMX4PLUS");
                secondDirectoryList.add("AMX220");
                secondDirectoryList.add("SENO-DS");
                secondDirectoryList.add("SENO-ESS");
                secondDirectoryList.add("SENO-CRYSTAL");
                secondDirectoryList.add("ALPHA-RT");
                secondDirectoryList.add("PS800");
                secondDirectoryList.add("PT800");
                secondDirectoryList.add("RXI");
                secondDirectoryList.add("DPX");
                secondDirectoryList.add("PRODIGY");
                secondDirectoryList.add("IDXA");
            }else if(zkbMainDirectory.equals(ZkbMainDirectory.OEC)){
                secondDirectoryList.add("OEC850");
                secondDirectoryList.add("OEC715");
                secondDirectoryList.add("OEC785");
                secondDirectoryList.add("OEC865");
                secondDirectoryList.add("OECONE");
                secondDirectoryList.add("OEC7500");
                secondDirectoryList.add("OEC7900");
                secondDirectoryList.add("OEC8800");
                secondDirectoryList.add("OEC9800");
                secondDirectoryList.add("OEC9900");
            }else if(zkbMainDirectory.equals(ZkbMainDirectory.CT)){
                secondDirectoryList.add("CTE-DUAL");
                secondDirectoryList.add("PROSPEED");
                secondDirectoryList.add("CT315");
                secondDirectoryList.add("CT325");
                secondDirectoryList.add("CT385");
                secondDirectoryList.add("ACT");
            }else if(zkbMainDirectory.equals(ZkbMainDirectory.ULS)){
                secondDirectoryList.add("Logiq-7");
                secondDirectoryList.add("Logiq-9");
                secondDirectoryList.add("Logiq-E9");
                secondDirectoryList.add("Logiq-S7");
                secondDirectoryList.add("Logiq-S8/E8");
                secondDirectoryList.add("Logiq-P7");
                secondDirectoryList.add("Logiq-P9");
                secondDirectoryList.add("Vivid-E95");
                secondDirectoryList.add("Vivid-E90");
                secondDirectoryList.add("Vivid-E80");
                secondDirectoryList.add("Vivid-E9");
                secondDirectoryList.add("Vivid-IQ");
                secondDirectoryList.add("Vivid-S5");
                secondDirectoryList.add("Vivid-S6");
                secondDirectoryList.add("Logiq-V2");
                secondDirectoryList.add("Logiq-V3");
                secondDirectoryList.add("Logiq-V5");
                secondDirectoryList.add("Voluson-E6/E8/E10");
                secondDirectoryList.add("Voluson-P6/P8");
                secondDirectoryList.add("Voluson-S6/S8");
                secondDirectoryList.add("Voluson-S10");
                secondDirectoryList.add("Voluson-730Pro");
                secondDirectoryList.add("Voluson-730EXP");
                secondDirectoryList.add("Logiq-F5F6/F8");
                secondDirectoryList.add("Logiq-C9");
                secondDirectoryList.add("Logiq-BOOK");
                secondDirectoryList.add("Venue-40");
                secondDirectoryList.add("Venue-50");
                secondDirectoryList.add("Venue-40/50");
                secondDirectoryList.add("Logiq-E");
                secondDirectoryList.add("BMD-Achilles");
            }else if(zkbMainDirectory.equals(ZkbMainDirectory.Others)){
                secondDirectoryList.add("Others");
            }

            secondDirectoryMap.put(zkbMainDirectory.name(), secondDirectoryList);

        }

        return secondDirectoryMap;
    }

    public boolean isZkbAdmin(UserAccount currentUser){
        TenantInfo tenantInfo = tenantInfoDao.findByuid(currentUser.getTenantUID());
        if(tenantInfo != null && tenantInfo.getTestStatus().equals(-1) && tenantInfo.getType().equals(3)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}

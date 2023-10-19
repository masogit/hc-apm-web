package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 212605082 on 2019/12/4.
 */
@Component
public class ZkbFileAttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFileAttachmentService.class);

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

    @Autowired
    private ZkbFileDirectoryRepository zkbFileDirectoryRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_DIRECTORY_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_DIRECTORY_IMPORT_MAX_ROW = 5000;

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_DIRECTORY_DATA_ROW = 2;

    /**
     * excel sheet 名称
     */
    public static final String SHEET_SERVICE_DIRECTORY = "DIRECTORY LIST";

    /**
     * 用户类型
     */
    public static final Integer userType = 3;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }



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
    public void saveZkbFileAttachment(V2_BlobObject blobObject, List<V2_BlobObject> uploadedFileList, List<TenantInfo> tenantInfoList,String vlink,String vlinkTitle){
        blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB);
        blobObject.setCreatedDate(new Date());
        blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
        if ("OperationVideo".equals(blobObject.getBoSubType())) {
            blobObject.setExtLink(vlink);
            blobObject.setObjectName(vlinkTitle);
            blobObjectDao.save(blobObject);
        }else{
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
    }

    @Transactional
    public void updateZkbFileAttachment(V2_BlobObject blobObject, List<TenantInfo> tenantInfoList,String vlink,String vlinkTitle) {

        blobObject.setLastModifiedDate(new Date());
        blobObject.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());

        fileAttachmentOrgInfoDao.deleteByBlobObjectUid(blobObject.getId());
        if ("OperationVideo".equals(blobObject.getBoSubType())) {
            blobObject.setExtLink(vlink);
            blobObject.setObjectName(vlinkTitle);
            blobObjectDao.save(blobObject);
        }else{
            for (TenantInfo tenantInfo : tenantInfoList) {
                FileAttachmentOrgInfo tempFileAttachmentOrgInfo = new FileAttachmentOrgInfo();
                tempFileAttachmentOrgInfo.setBlobObjectUid(blobObject.getId());
                tempFileAttachmentOrgInfo.setTenantUID(tenantInfo.getUid());
                fileAttachmentOrgInfoDao.save(tempFileAttachmentOrgInfo);
            }
            blobObjectDao.save(blobObject);
        }
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

    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getDirectoryMapFromTemplate(ExcelDocument doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_DIRECTORY_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_DIRECTORY).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_DIRECTORY, EXCEL_DIRECTORY_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            AssetInfoVO assetInfoVO = new AssetInfoVO();
            assetInfoVO.setLineNum(i);
            AssetInfoRow assetInfoRow = new AssetInfoRow(assetInfoVO,rowMap);
            assetInfoRow.setFieldStringValue(WebUtil.getMessage("mainDirectory"), "setMainDirectory");
            assetInfoRow.setFieldStringValue(WebUtil.getMessage("secondDirectory"), "setSecondDirectory");
            assetInfoRow.setFieldStringValue(WebUtil.getMessage("importDirectorySystemId"), "setSystemId");
            if(assetInfoVO.isNull()){
                break;
            }
            map.put("assetInfoVO", assetInfoVO);
            map.put("rowData", rowMap);
            map.put("status", assetInfoVO.getId()==null ? ZkbFileAttachmentService.ImportStatus.New.name() : ZkbFileAttachmentService.ImportStatus.Exist.name());
            map.put("hashCode", assetInfoVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getDirectoryMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }

    /**
     * 导入数据
     * @param importDirectoryMapList 目录信息
     */
    public void importData(List<Map<String, Object>> importDirectoryMapList) {

        LocalDateTime startTime = LocalDateTime.now();
        int row = 0;
        try {
            for (Map<String, Object> item : importDirectoryMapList) {
                ++row;
                if(checkData(item, importDirectoryMapList)) {
                    AssetInfoVO assetInfoVO = (AssetInfoVO)item.get("assetInfoVO");
                    this.assetInfoRepository.updateDirecotryInfo(assetInfoVO.getMainDirectory(),assetInfoVO.getSecondDirectory(),ZkbFileAttachmentService.userType,assetInfoVO.getSystemId());
                }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    /**
     * 检查数据是否合法
     * 1.做空值检验
     * 2.验证主目录子目录是否正确
     * 3.验证是否能根据system id 是否能匹配到信息
     * @param item
     * @param importStudentMapList
     * @return
     */
    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importStudentMapList) {

        boolean isValidateData = true;
        AssetInfoVO assetInfoVO = (AssetInfoVO)item.get("assetInfoVO");
        AssetInfoRow.clearErrMessage(item);
        if(StringUtils.isEmpty(assetInfoVO.getSystemId())){
            item.put("status", ZkbFileAttachmentService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorDirectorySystemIdIsEmpty"));
            isValidateData = false;
        }

        if(StringUtils.isEmpty(assetInfoVO.getMainDirectory())){
            item.put("status", ZkbFileAttachmentService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorDirectoryMainDirectoryIsEmpty"));
            isValidateData = false;
        }

        if(StringUtils.isEmpty(assetInfoVO.getSecondDirectory())){
            item.put("status", ZkbFileAttachmentService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorDirectorySecondDirectoryIsEmpty"));
            isValidateData = false;
        }
        if(StringUtils.isNotEmpty(assetInfoVO.getMainDirectory())&&StringUtils.isNotEmpty(assetInfoVO.getSecondDirectory()))
        {
            Integer count = zkbFileDirectoryRepository.findDirecotryCount(assetInfoVO.getMainDirectory(),assetInfoVO.getSecondDirectory(), ZkbFileDirectoryService.SUB_DIRECTORY_LEVEL_VALUE);
            if(count==0)
            {
                item.put("status", ZkbFileAttachmentService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorDirectory"));
                isValidateData = false;
            }
        }
        Integer count = this.assetInfoRepository.findAssetInfoCount(ZkbFileAttachmentService.userType,assetInfoVO.getSystemId());
        if(count==0)
        {
            item.put("status", ZkbFileAttachmentService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorCountDirectory"));
            isValidateData = false;
            return isValidateData;
        }
        return isValidateData;
    }

    /**
     * 整理错误信息
     * @param importDirectoryMapList
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importDirectoryMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importDirectoryMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbFileAttachmentService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbFileAttachmentService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }


}

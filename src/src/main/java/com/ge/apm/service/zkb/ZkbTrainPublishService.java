package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.budget.BudgetApplicationExportService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZkbTrainPublishService {

    private static Logger logger = LoggerFactory.getLogger(ZkbTrainPublishService.class);


    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;

    @Autowired
    private ZkbTrainPublishRegisterRepository zkbTrainPublishRegisterRepository;

    @Autowired
    private ZkbTrainPublishRegisterDetailRepository zkbTrainPublishRegisterDetailRepository;

    @Autowired
    private BlobObjectRepository blobObjectRepository;

    private MicroServiceUtil msService;

    @Autowired
    TenantInfoRepository tenantInfoRepository;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
    }

    public void save(ZkbTrainPublishInfo zkbTrainPublishInfo, UserAccount userAccount, ZkbTrainCourse zkbTrainCourse){
        if(StrUtil.isEmpty(zkbTrainPublishInfo.getId())){
            zkbTrainPublishInfo.setCreatedBy(userAccount.getUid());
            zkbTrainPublishInfo.setCreatedDate(new Date());
            zkbTrainPublishInfo.setCourseId(zkbTrainCourse.getId());
        }else{
            zkbTrainPublishInfo.setLastModifiedBy(userAccount.getUid());
            zkbTrainPublishInfo.setLastModifiedDate(new Date());
        }
        zkbTrainPublishRepository.save(zkbTrainPublishInfo);
    }




    public StreamedContent exportZipFile(ZkbTrainPublishInfo zkbTrainPublishInfo) throws IOException {
        List<ZkbTrainPublishRegister> registerList = zkbTrainPublishRegisterRepository.findByPid(zkbTrainPublishInfo.getId());
        if(CollectionUtils.isEmpty(registerList)){
            return null;
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);
        for(ZkbTrainPublishRegister register :registerList){
            List<ZkbTrainPublishRegisterDetail> registerDetails = zkbTrainPublishRegisterDetailRepository.findByRegisterId(register.getId());
            if(CollectionUtils.isEmpty(registerDetails)){
                continue;
            }
            for(ZkbTrainPublishRegisterDetail detail : registerDetails){
                List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(detail.getId());
                if(CollectionUtils.isEmpty(blobObjects)){
                    continue;
                }
                String fileName = detail.getEngineerName() + "-" + detail.getEngineerPhone();
                TenantInfo tenantInfo = tenantInfoRepository.findByuid(register.getTenantUid());
                String preFileName = tenantInfo.getName();
                for(V2_BlobObject b : blobObjects){
                    getFileBufferedInputStream(zout, b, preFileName, b.getObjectName());
                }
            }
        }
        zout.close();

        String base = "Train_Publish_Export" + DateUtil.format(new Date(),"yyyyMMddHHmmss");
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", base+".zip");
        return streamedContent;

    }

    public void getFileBufferedInputStream(ZipOutputStream zout,V2_BlobObject blobObject, String preFileName,String fileName) throws IOException {
        HashMap<String,Integer> nameMap = new HashMap<>();
        StreamedContent sf = null;
        try{
            sf = msService.downloadSingleFile(blobObject.getObjectStorageId());
        }catch (Exception e){
            e.printStackTrace();
            logger.error("file download fail", e);
        }
        if(StringUtils.isEmpty(preFileName)){
            zout.putNextEntry(new ZipEntry(fileName));
        }else{
            zout.putNextEntry(new ZipEntry(preFileName + "/" + fileName));
        }
        byte[] bys = new byte[1024 * 5];
        try( BufferedInputStream bufferStream = new BufferedInputStream(sf.getStream())){
            int rd = 0;
            while ((rd = bufferStream.read(bys)) != -1) {
                zout.write(bys, 0, rd);
            }
            zout.closeEntry();
        }

    }
}

package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.NotificationExecutor;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZkbTrainPublishService extends MicroServiceInvoker {

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

    @Autowired
    UserAccountRepository userAccountRepository;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
    }

    public void save(ZkbTrainPublishInfo zkbTrainPublishInfo, UserAccount userAccount, ZkbTrainCourse zkbTrainCourse){
        if (StrUtil.isEmpty(zkbTrainPublishInfo.getId())){
            zkbTrainPublishInfo.setCreatedBy(userAccount.getUid());
            zkbTrainPublishInfo.setCreatedDate(new Date());
            zkbTrainPublishInfo.setCourseId(zkbTrainCourse.getId());
            zkbTrainPublishRepository.save(zkbTrainPublishInfo);
            // 课程发布成功, 需要推送短信邮件通知给各个代理商的负责人发短信、邮件通知
            sendMessageByPublishInfo(zkbTrainPublishInfo);
        } else {
            zkbTrainPublishInfo.setLastModifiedBy(userAccount.getUid());
            zkbTrainPublishInfo.setLastModifiedDate(new Date());
            zkbTrainPublishRepository.save(zkbTrainPublishInfo);
        }
    }

    public void sendMessageByPublishInfo(ZkbTrainPublishInfo zkbTrainPublishInfo) {
        if (zkbTrainPublishInfo.getZkbTrainCourse() == null) {
            logger.info("课程发布成功，但是课程为空，不发送通知，Id = {}", zkbTrainPublishInfo.getId());
            return;
        }
        String subject = "培训管理-培训课程发布成功";
        String content = "课程 : " + zkbTrainPublishInfo.getZkbTrainCourse().getName() + " 已经发布。请登录APM系统-培训管理进行报名。课程可能会出现调整，请以APM系统显示为准。";
        String token = UserContextService.getAccessToken();
        // 查询需要短信和邮件通知的代理商负责人用户
        List<UserAccount> userAccountList = userAccountRepository.findByZkbCourseId(zkbTrainPublishInfo.getCourseId());
        if (CollectionUtils.isEmpty(userAccountList)) {
            logger.info("courseId = {}, userAccountList is empty", zkbTrainPublishInfo.getCourseId());
            return;
        }
        String courseId = zkbTrainPublishInfo.getZkbTrainCourse().getId();
        String courseName = zkbTrainPublishInfo.getZkbTrainCourse().getName();
        for (UserAccount agentAccount : userAccountList) {
            // 发送邮件
            NotificationExecutor.executeIfNotBlank("邮箱", agentAccount.getEmail(), courseId, courseName, agentAccount.getLoginName(),
                    () -> sendEmail(agentAccount.getEmail(), subject, content, token)
            );

            // 发送短信
            NotificationExecutor.executeIfNotBlank("电话", agentAccount.getTelephone(), courseId, courseName, agentAccount.getLoginName(),
                    () -> sendMessage(agentAccount.getTelephone(), subject, content, token)
            );
        }
    }

    private void sendMessage(String telephone, String subject, String content, String token) {
        try {
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("subject", subject);
            emailWrapperMap.put("content", content);
            emailWrapperMap.put("to", telephone);
            String url = getGatewayURL(GatewayURL.SEND_MESSAGE_FOR_TEXT);
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info("telephone = {}, content = {}, sendMessage result = {}", telephone, content, response.toString());
        } catch (Exception e) {
            logger.error("telephone = {}, content = {}, trainPublishSendMessage error", telephone, content, e);
        }
    }

    public void sendEmail(String email, String subject, String content, String token) {
        try {
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("subject", subject);
            emailWrapperMap.put("content", content);
            emailWrapperMap.put("to", email);
            String url = getGatewayURL(GatewayURL.SEND_EMAIL_FOR_TEXT);
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info("emailAddress = {}, content = {}, sendEmail result = {}", email, content, response.toString());
        } catch (Exception e) {
            logger.error("emailAddress = {}, content = {}, trainPublishSendEmail error", email, content, e);
        }
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

    public void delete(ZkbTrainPublishInfo zkbTrainPublishInfo) {
        zkbTrainPublishRepository.delete(zkbTrainPublishInfo);
    }

    public List<ZkbTrainPublishRegister> getRegisteredByPublishId(String publishId) {
        return zkbTrainPublishRegisterRepository.findByPid(publishId);
    }
}

package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.MeasuringCertificateDetailMapper;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.OCRTemplateService;
import com.ge.apm.service.PDFSplitFileUtil;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.llm.ApplicationType;
import com.ge.healthcare.cn.common.llm.Result;
import com.ge.healthcare.cn.common.llm.WorkflowResponse;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.healthcare.cn.common.ocr.support.BusinessType;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.validator.internal.util.StringHelper;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import com.ge.healthcare.cn.common.utils.date.DateUtil;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class MeasuringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasuringService.class);

    private final String HEADER_CERTIFICATION_NO = "证书编号";
    private final String HEADER_APPLIANCE_NAME = "器具名称";
    private final String HEADER_MANUFACTURE = "制造厂";
    private final String HEADER_MODEL = "型号";
    private final String HEADER_ASSET_SN = "设备序列号SN";
    private final String HEADER_ASSET_NO = "资产编号";
    private final String HEADER_APPLIANCE_NO = "器具编号";
    private final String HEADER_VERIFICATION_CONCLUSION = "检定结论";
    private final String HEADER_VERIFICATION_DATE = "检定日期";
    private final String HEADER_EXPIRE_DATE = "有效期至";
    private final String HEADER_VERIFICATION_UNIT = "检测单位";
    private final String HEADER_VERIFIER = "检定人员";
    private final String HEADER_ITEM = "项别";
    private final String HEADER_CATEGORY = "种别";
    private final String HEADER_CLINICAL_DEPT = "送检单位";
    private final String HEADER_REMARK = "备注";
    private final String HEADER_FILE_NO = "存档记录";
    private final String HEADER_QR_CODE = "二维码";
    private final String HEADER_CERTIFICATE_TYPE = "证书类型";
    private static final String MEASURING_STATUS= "measuringStatus";
    private int progress;

    private Pattern pattern = Pattern.compile("\\d+");
    @Autowired
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;
    @Autowired
    private MeasuringTypeConfigRepository measuringTypeConfigDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private BlobFileService blobFileService;
    @Autowired
    private MeasuringCertificateDetailMapper certificateDetailMapper;
    @Autowired
    private BlobObjectRepository blobDao;
    @Autowired
    private OCRTemplateService templateService;
    @Autowired
    private I18nMessageService i18nMessageService;
    @Autowired
    private MqUtils mqUtils;
    @Autowired
    private SystemEventAuditAopAspect systemEventAuditAopAspect;

    @Value("#{systemEnvironment['FILE_SCAN_FOLDER'] ?: urlProperties.FileScanFolder}")
    private String FILE_SCAN_FOLDER;
    @Autowired
    private ConfigItemService configItemService;

    public MeasuringCertificateDetail operateMeasuringAndAssetInfo(MeasuringCertificateDetail measuringCertificateDetail) {
        boolean flag = false;
        boolean failureFlag = false;


        AssetInfo tempAssetInfo = null;
        StringBuilder errorInfo = new StringBuilder();
        List<AssetInfo> tempAssetInfoList = null;
        List<MeasuringCertificateDetail> mcdList = null;
        if(measuringCertificateDetail.getCertificateNo() != null){
            mcdList = measuringCertificateDetailDao.findByCertificateNoAndTenantUIDOrderByCheckDateDesc( measuringCertificateDetail.getCertificateNo(), measuringCertificateDetail.getTenantUID());
            if(mcdList != null && !mcdList.isEmpty()  ) {
                MeasuringCertificateDetail measuringCertificateDetail1 = mcdList.get(0);
                if(measuringCertificateDetail1.getAssetUID()!=null){
                    boolean snMatch = measuringCertificateDetail1.getAssetSN() != null && Objects.equals(measuringCertificateDetail1.getAssetSN(), measuringCertificateDetail.getAssetSN());
                    boolean financeNoMatch = measuringCertificateDetail1.getFinanceNo() != null && Objects.equals(measuringCertificateDetail1.getFinanceNo(), measuringCertificateDetail.getFinanceNo());
                    boolean qrCodeMatch = measuringCertificateDetail1.getQrCode() != null && Objects.equals(measuringCertificateDetail1.getQrCode(), measuringCertificateDetail.getQrCode());
                    if(!snMatch && !financeNoMatch && !qrCodeMatch){
                        failureFlag=true;
                        errorInfo.append("[计量证书编号已绑定其他设备");
                    }else{
                        measuringCertificateDetail.setAssetUID(measuringCertificateDetail1.getAssetUID());
                        tempAssetInfo=assetInfoDao.findByUid(measuringCertificateDetail1.getAssetUID());
                    }
                }
            }
            if(CollectionUtils.isEmpty(mcdList) ||   mcdList.get(0).getAssetUID()==null ){
                if(ObjectUtil.isNotEmpty(measuringCertificateDetail.getAssetSN())){
                    tempAssetInfoList = this.getAssetInfoBySerialNum(measuringCertificateDetail.getAssetSN(), measuringCertificateDetail.getSiteUID());
                    if(CollectionUtils.isNotEmpty(tempAssetInfoList)  && tempAssetInfoList.size()>1 ){
                        errorInfo.append("[设备序列号SN匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        failureFlag=true;
                    }

                }
                if(ObjectUtil.isNotEmpty(measuringCertificateDetail.getFinanceNo()) && (CollectionUtils.isEmpty(tempAssetInfoList) || tempAssetInfoList.size()>1   )){
                    tempAssetInfoList = this.getAssetInfoByFinancingNum(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getSiteUID());
                    if(CollectionUtils.isNotEmpty(tempAssetInfoList)  && tempAssetInfoList.size()>1 ){
                        errorInfo.append("[资产编号匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        failureFlag=true;
                    }
                }
                // YHAAO-13151 如果资产编号和设备序列号都不能匹配到资产设备，使用二维码匹配
                if(ObjectUtil.isNotEmpty(measuringCertificateDetail.getQrCode())   && (CollectionUtils.isEmpty(tempAssetInfoList) || tempAssetInfoList.size()>1   )){
                    tempAssetInfoList = this.getAssetInfoByQrCode(measuringCertificateDetail.getQrCode(), measuringCertificateDetail.getSiteUID());
                    if(CollectionUtils.isNotEmpty(tempAssetInfoList)  && tempAssetInfoList.size()>1 ){
                        errorInfo.append("[二维码匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        failureFlag=true;
                    }

                }
            }



        }else{
            failureFlag=true;
            errorInfo.append("[计量证书编号不能为空]");
        }


        if( failureFlag  && (CollectionUtils.isEmpty(tempAssetInfoList)  ||  tempAssetInfoList.size()>1 )){
            measuringCertificateDetail.setErrorInfo(errorInfo.toString());
            return measuringCertificateDetail;
        }

        if( CollectionUtils.isNotEmpty(tempAssetInfoList) &&  tempAssetInfoList.size() == 1){
            tempAssetInfo = tempAssetInfoList.get(0);
        }

        if(tempAssetInfo != null){
            measuringCertificateDetail.setAssetUID(tempAssetInfo.getUid());
        }

        SystemEventAuditEnum eventAuditEnum = null;
        //如果查询到具体的计量记录,做更新操作，否则新增计量记录
        if(mcdList != null && !mcdList.isEmpty()) {
            MeasuringCertificateDetail tempMcd = mcdList.get(0);
            measuringCertificateDetail.setId(tempMcd.getId());
            measuringCertificateDetail.setCreatedBy(tempMcd.getCreatedBy());
            measuringCertificateDetail.setCreatedDate(tempMcd.getCreatedDate());
            measuringCertificateDetail.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            measuringCertificateDetail.setLastModifiedDate(new Date());
            eventAuditEnum =SystemEventAuditEnum.MEASURE_IMPORT_UPLOAD;
        } else {
            measuringCertificateDetail.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            measuringCertificateDetail.setCreatedDate(new Date());
            eventAuditEnum = SystemEventAuditEnum.MEASURE_IMPORT_CREATE;
        }
        measuringCertificateDetailDao.save(measuringCertificateDetail);
        if (tempAssetInfo != null) {
            //目标设备的所有计量记录
            updateMeasuringDate( tempAssetInfo);
        }
        SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, measuringCertificateDetail.getId(), Boolean.TRUE, "","measuring", JSONArray.toJSONString(measuringCertificateDetail));
        systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);

        return null;

    }
    public void sendMq(AssetInfo assetInfo,UserAccount userAccount) {
        MqContext mqContext = new MqContext();
        mqContext.setClassName("MeasuringService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("handleExcel- operateMeasuringAndAssetInfo/operateMeasuringAndAssetInfo_For_ShaoYiFu");
        mqContext.setDescribe("计量导入修改设备信息");
        mqContext.setModifySource(ModifySourceEnum.MEASURING_IMPORT.getCode());
        UserAccount currentUserAccount = userAccount;
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(assetInfo,currentUserAccount,mqContext);
    }

    public MeasuringCertificateDetail operateMeasuringAndAssetInfo_For_ShaoYiFu(MeasuringCertificateDetail measuringCertificateDetail) {


        boolean flag = false;
        boolean failureFlag = false;
        AssetInfo tempAssetInfo = null;
        StringBuilder errorInfo = new StringBuilder();
        List<AssetInfo> tempAssetInfoList = null;
        List<MeasuringCertificateDetail> mcdList = null;
        if(measuringCertificateDetail.getCertificateNo() != null){
            mcdList = measuringCertificateDetailDao.findByCertificateNoAndTenantUIDOrderByCheckDateDesc( measuringCertificateDetail.getCertificateNo(), measuringCertificateDetail.getTenantUID());
            if(mcdList != null && !mcdList.isEmpty()  ) {
                MeasuringCertificateDetail measuringCertificateDetail1 = mcdList.get(0);
                if(measuringCertificateDetail1.getAssetUID()!=null){
                    boolean snMatch = measuringCertificateDetail1.getAssetSN() != null && Objects.equals(measuringCertificateDetail1.getAssetSN(), measuringCertificateDetail.getAssetSN());
                    boolean financeNoMatch = measuringCertificateDetail1.getFinanceNo() != null && Objects.equals(measuringCertificateDetail1.getFinanceNo(), measuringCertificateDetail.getFinanceNo());
                    boolean qrCodeMatch = measuringCertificateDetail1.getQrCode() != null && Objects.equals(measuringCertificateDetail1.getQrCode(), measuringCertificateDetail.getQrCode());
                    if(!snMatch && !financeNoMatch && !qrCodeMatch){
                        failureFlag=true;
                        errorInfo.append("[计量证书编号已绑定其他设备");
                    }else{
                        measuringCertificateDetail.setAssetUID(measuringCertificateDetail1.getAssetUID());
                        tempAssetInfo=assetInfoDao.findByUid(measuringCertificateDetail1.getAssetUID());
                    }
                }
            }
            if(CollectionUtils.isEmpty(mcdList) ||   mcdList.get(0).getAssetUID()==null ){

                if(null != measuringCertificateDetail.getFinanceNo() && !"".equals(measuringCertificateDetail.getFinanceNo())){
                    tempAssetInfoList = this.getAssetInfoByFinancingNum(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getSiteUID());
                    if(tempAssetInfoList.size() == 1){
                        flag = true;
                    } else {
                        //开始匹配sn号
                        tempAssetInfoList = this.getAssetInfoBySerialNum(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getSiteUID());
                        if(tempAssetInfoList == null || tempAssetInfoList.isEmpty()){

                        }else if(tempAssetInfoList.size() == 1){
                            flag = true;
                        }else{
                            failureFlag=true;
                            errorInfo.append("[资产编号匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        }
                    }
                }
                if(!flag && (null != measuringCertificateDetail.getAssetSN() && !"".equals(measuringCertificateDetail.getAssetSN()))){
                    tempAssetInfoList = this.getAssetInfoBySerialNum(measuringCertificateDetail.getAssetSN(), measuringCertificateDetail.getSiteUID());
                    if(tempAssetInfoList == null || tempAssetInfoList.isEmpty()){

                    }else{
                        if(tempAssetInfoList.size() == 1){
                            flag = true;
                        }else{
                            failureFlag=true;
                            errorInfo.append("[设备序列号SN匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        }
                    }
                }
                // YHAAO-13151 如果资产编号和设备序列号都不能匹配到资产设备，使用二维码匹配
                if(!flag && (null != measuringCertificateDetail.getQrCode() && !"".equals(measuringCertificateDetail.getQrCode()))){
                    tempAssetInfoList = this.getAssetInfoByQrCode(measuringCertificateDetail.getQrCode(), measuringCertificateDetail.getSiteUID());
                    if(tempAssetInfoList == null || tempAssetInfoList.isEmpty()){

                    }else{
                        if(tempAssetInfoList.size() == 1){
                            flag = true;
                        }else{
                            failureFlag=true;
                            errorInfo.append("[二维码匹配到(").append(tempAssetInfoList.size()).append(")个资产设备]");
                        }
                    }
                }
                else if ((null == measuringCertificateDetail.getFinanceNo() || "".equals(measuringCertificateDetail.getFinanceNo()))
                        && (null == measuringCertificateDetail.getAssetSN() || "".equals(measuringCertificateDetail.getAssetSN()))
                        && null == measuringCertificateDetail.getQrCode() || "".equals(measuringCertificateDetail.getQrCode())){
                    errorInfo.append("[(设备序列号SN)、(资产编号)和(二维码)不能同时为空]");
                    failureFlag=true;
                }

                if(flag){
                    if(tempAssetInfoList.size() == 1){
                        tempAssetInfo = tempAssetInfoList.get(0);
                    }
                }
            }
        }else{
            failureFlag=true;
            errorInfo.append("[计量证书编号不能为空]");
        }

        if( failureFlag  && (CollectionUtils.isEmpty(tempAssetInfoList)  ||  tempAssetInfoList.size()>1 )){
            measuringCertificateDetail.setErrorInfo(errorInfo.toString());
            return measuringCertificateDetail;
        }

        if( CollectionUtils.isNotEmpty(tempAssetInfoList) &&  tempAssetInfoList.size() == 1){
            tempAssetInfo = tempAssetInfoList.get(0);
        }

        if(tempAssetInfo != null){
            measuringCertificateDetail.setAssetUID(tempAssetInfo.getUid());
        }
        SystemEventAuditEnum eventAuditEnum = null;
        //如果查询到具体的计量记录,做更新操作，否则新增计量记录
        if(mcdList != null && !mcdList.isEmpty()) {
            MeasuringCertificateDetail tempMcd = mcdList.get(0);
            measuringCertificateDetail.setId(tempMcd.getId());
            measuringCertificateDetail.setCreatedBy(tempMcd.getCreatedBy());
            measuringCertificateDetail.setCreatedDate(tempMcd.getCreatedDate());
            measuringCertificateDetail.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            measuringCertificateDetail.setLastModifiedDate(new Date());
            eventAuditEnum =SystemEventAuditEnum.MEASURE_IMPORT_UPLOAD;
        } else {
            measuringCertificateDetail.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            measuringCertificateDetail.setCreatedDate(new Date());
            eventAuditEnum = SystemEventAuditEnum.MEASURE_IMPORT_CREATE;
        }
        measuringCertificateDetailDao.save(measuringCertificateDetail);
        if (tempAssetInfo != null) {
            //目标设备的所有计量记录
            updateMeasuringDate( tempAssetInfo);
        }
        SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, measuringCertificateDetail.getId(), Boolean.TRUE, "","measuring", JSONArray.toJSONString(measuringCertificateDetail));
        systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);

        return null;
    }

    public void updateMeasuringDate( AssetInfo tempAssetInfo) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        CompletableFuture.runAsync(()->{
            //目标设备的所有计量记录
            List<MeasuringCertificateDetail> allMcdList = measuringCertificateDetailDao.findByAssetUIDAndTenantUIDOrderByCheckDateDesc(tempAssetInfo.getUid(), tempAssetInfo.getTenantUID());
            if(CollectionUtils.isEmpty(allMcdList)){
                tempAssetInfo.setNextMeteringDate(null);
                tempAssetInfo.setLastMeteringDate(null);
                tempAssetInfo.setMeteringNO(null);
                tempAssetInfo.setMeasuringType(null);
                tempAssetInfo.setLastCheckCompany(null);
                assetInfoDao.save(tempAssetInfo);
                sendMq(tempAssetInfo,currentUserAccount);
                return;
            }
            //目标设备的所有计量记录中计量日期最大的记录
            MeasuringCertificateDetail needUpdateAssetMcd = allMcdList.get(0);

            Date nextMeteringDate =  needUpdateAssetMcd.getValidDateTo();
            Date checkDate = needUpdateAssetMcd.getCheckDate();
            TenantInfo tenantInfo = tenantInfoDao.findByuid(tempAssetInfo.getTenantUID());
            if("4".equals(tenantInfo.getCustomTenant())){
                if(checkDate != null && nextMeteringDate == null && tempAssetInfo.getMeasuringItemCategory() != null){
                    MeasuringTypeConfig measuringTypeConfig = measuringTypeConfigDao.findByMeasuringItemCategoryAndSiteUID(tempAssetInfo.getMeasuringItemCategory().toString(), currentUserAccount.getSiteUID());
                    Integer measureCycle = measuringTypeConfig == null ? null:measuringTypeConfig.getMeasureCycle();
                    if(measureCycle != null && measureCycle > 0){//云南省阜外心血管病医院 下次计量日期=上次计量日期+周期（月）
                        nextMeteringDate = webapp.framework.util.DateUtil.addMonth(checkDate,measureCycle);
                    }
                }
            }
            //更新设备台账
            tempAssetInfo.setNextMeteringDate(nextMeteringDate);
            tempAssetInfo.setLastMeteringDate(checkDate);
            tempAssetInfo.setMeteringNO(needUpdateAssetMcd.getCertificateNo());
            // 上次检测单位
            tempAssetInfo.setLastCheckCompany(needUpdateAssetMcd.getCheckCompany());
            if (Objects.equals(needUpdateAssetMcd.getStatus(), 2)) {//计量不合格
                tempAssetInfo.setUsageStatus(2);//停用
                tempAssetInfo.setStatus(5);
            }

            tempAssetInfo.setMeasuringType(needUpdateAssetMcd.getType());

            assetInfoDao.save(tempAssetInfo);
            sendMq(tempAssetInfo,currentUserAccount);
        });

    }

    /**
     * 批量刷新匹配未关联台账的计量记录（按SN号/资产编号/二维码依次匹配）
     * @return int[]{matchedCount, failedCount}
     */
    public int[] refreshMatchUnpairedRecords() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<MeasuringCertificateDetail> unpairedList = measuringCertificateDetailDao
                .findBySiteUIDAndAssetUIDIsNullOrderByCheckDateDesc(currentUser.getSiteUID());

        int matched = 0;
        int failed = 0;

        for (MeasuringCertificateDetail detail : unpairedList) {
            List<AssetInfo> matchedAssets = null;

            // 1. 按设备序列号SN匹配
            if (ObjectUtil.isNotEmpty(detail.getAssetSN())) {
                matchedAssets = this.getAssetInfoBySerialNum(detail.getAssetSN(), detail.getSiteUID());
            }
            // 2. SN未匹配到，按资产编号匹配
            if ((matchedAssets == null || matchedAssets.size() != 1) && ObjectUtil.isNotEmpty(detail.getFinanceNo())) {
                matchedAssets = this.getAssetInfoByFinancingNum(detail.getFinanceNo(), detail.getSiteUID());
            }
            // 3. 资产编号未匹配到，按二维码匹配
            if ((matchedAssets == null || matchedAssets.size() != 1) && ObjectUtil.isNotEmpty(detail.getQrCode())) {
                matchedAssets = this.getAssetInfoByQrCode(detail.getQrCode(), detail.getSiteUID());
            }

            // 恰好匹配到1个资产设备，建立关联
            if (matchedAssets != null && matchedAssets.size() == 1) {
                AssetInfo asset = matchedAssets.get(0);
                detail.setAssetUID(asset.getUid());
                detail.setAssetSN(asset.getSerialNum());
                detail.setQrCode(asset.getQrCode());
                detail.setDepartNum(asset.getDepartNum());
                detail.setFinanceNo(asset.getFinancingNum());
                measuringCertificateDetailDao.save(detail);
                updateMeasuringDate(asset);
                matched++;
            } else {
                failed++;
            }
        }
        return new int[]{matched, failed};
    }


    private List<AssetInfo> getAssetInfoByFinancingNum(String financingNum, String siteUID){
        List<AssetInfo> tempAssetInfoList = null;

        if(!StringHelper.isNullOrEmptyString(financingNum)){
            //getByFinancingNumAndSiteUID 方法暂时废弃 使用getByFinancingNumAndSiteUID_ForDbTable
            tempAssetInfoList = assetInfoDao.getByFinancingNumAndSiteUID_ForDbTable(financingNum, siteUID);
        }

        return tempAssetInfoList;
    }

    private List<AssetInfo> getAssetInfoBySerialNum(String serialNum, String siteUID){
        List<AssetInfo> tempAssetInfoList = null;

        if(!StringHelper.isNullOrEmptyString(serialNum)){
            //excel 报表导出需要忽略大小写 所以使用jpa原生sql 加了ILIKE关键字( getBySerialNumAndSiteUID 方法是原本的业务，目前改为 getBySerialNumAndSiteUID_ForDbTable 业务)
            tempAssetInfoList = assetInfoDao.getBySerialNumAndSiteUID_ForDbTable(serialNum, siteUID);
        }

        return tempAssetInfoList;
    }

    private List<AssetInfo> getAssetInfoByQrCode(String qrCode, String siteUID){
        List<AssetInfo> tempAssetInfoList = null;

        if(!StringHelper.isNullOrEmptyString(qrCode)){
            //excel 报表导出需要忽略大小写 所以使用jpa原生sql 加了ILIKE关键字( getByQrCodeAndSiteUID 方法是原本的业务，目前改为 getByQrCodeAndSiteUID_ForDbTable 业务)
            tempAssetInfoList = assetInfoDao.getByQrCodeAndSiteUID_ForDbTable(qrCode, siteUID);
        }

        return tempAssetInfoList;
    }

    public List<MeasuringCertificateDetail> handleZip(UploadedFile file, UserAccount user) throws IOException {
        Wrapper<MeasuringCertificateDetail> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("id, certificate_no certificateNo");
        wrapper.eq("tenant_uid", user.getTenantUID());
        List<MeasuringCertificateDetail> measuringCertificateDetails = certificateDetailMapper.selectList(wrapper);
        Map<String, List<MeasuringCertificateDetail>> certificateDetailMap = measuringCertificateDetails.stream().filter(detail -> ObjectUtil.isNotEmpty(detail.getCertificateNo())).collect(Collectors.groupingBy(MeasuringCertificateDetail::getCertificateNo));

        List<MeasuringCertificateDetail> resultList = Lists.newArrayList();
        // 获取压缩文件中的每个子文件
        try(ZipInputStream zipInputStream = new ZipInputStream(file.getInputstream(), Charset.forName("GBK"))){
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            String certificateNo = Lists.newArrayList(fileName.split("\\.")).get(0);
            MeasuringCertificateDetail resultDetail = new MeasuringCertificateDetail();
            List<MeasuringCertificateDetail> certificateDetails = certificateDetailMap.get(certificateNo);
            String errInfo = "";
            if (ObjectUtil.isEmpty(certificateDetails)) {
                errInfo = "证书编号匹配不到计量证书";
            } else if (certificateDetails.size() > 1) {
                errInfo = "证书编号匹配到" + certificateDetails.size() + "个计量证书";
            } else {
                MeasuringCertificateDetail certificateDetail = certificateDetails.get(0);
                V2_BlobObject blobObject = blobFileService.uploadFile(zipInputStream, entry.getName());
                blobObject.setBoType(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
                blobObject.setBoUuid(certificateDetail.getId());
                List<V2_BlobObject> hasList = blobDao.findByBoUuidAndObjectName(certificateDetail.getId(), blobObject.getObjectName());
                if (ObjectUtil.isNotEmpty(hasList)) {
                    blobDao.delete(hasList);
                }
                blobDao.save(blobObject);
            }

            resultDetail.setCertificateNo(certificateNo);
            resultDetail.setErrorInfo(errInfo);
            resultList.add(resultDetail);
        }
        }catch (Exception e){
            LOGGER.error("handle zip error:{}",e.getMessage());
            throw  new RuntimeException("handleZip error",e);
        }
        return resultList;
    }
    public List<MeasuringCertificateDetail> handleZipOCR(UploadedFile file,UserAccount account,String identifyPatterns) throws IOException {
        String identifyPostProcessorRule = configItemService.getConfigItemByKeyToString("identifyPostProcessorRule",account);
        JSONObject ruleJSON = getRuleJSON(identifyPostProcessorRule);
        List<MeasuringCertificateDetail> resultList = new ArrayList<>();
        progress = 0;
        // 获取压缩文件中的每个子文件
        try(ZipInputStream zipInputStream = new ZipInputStream(file.getInputstream(), Charset.forName("GBK"))){
            List<File> zipEntry = getZipEntryFile(zipInputStream);
            int fileCount = zipEntry.size();
            int fileCountAvg = 100 / fileCount;
            for (File efile : zipEntry) {
                List<MeasuringCertificateDetail> detailList = handleOcr(efile,identifyPatterns,ruleJSON);
                resultList.addAll(detailList);
                progress += fileCountAvg;
            }
            progress = 100;
        }catch (Exception e){
            progress = 0;
            LOGGER.error("handle zip error:{}",e.getMessage());
        }
        resultList = resultList.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        return resultList;
    }

    public List<File> getZipEntryFile(ZipInputStream zipInputStream) throws IOException {
        List<File> zipEntryFileList = new ArrayList<>();
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] buffer = new byte[1024];
                    File destDir = new File(FILE_SCAN_FOLDER);
                    if(entry.isDirectory()) return null;
                    File newFile = newFile(destDir, entry);
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    zipEntryFileList.add(newFile);
                }
            }
        return zipEntryFileList;
    }
    public List<MeasuringCertificateDetail> handleOcr( File newFile, String identifyPatterns, JSONObject ruleJSON){
        List<MeasuringCertificateDetail> measuringCertificateDetailResult = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>();
            if("AI".equalsIgnoreCase(identifyPatterns)){
//                params.put("type", ApplicationType.FORM_OCR_WORKFLOWS.name());
//                JSONObject  jsonObject = templateService.getJsonObject( GatewayURL.FILES_UPLOAD_AND_WORKFLOWS_RUN,params,newFile,url->url.replace("{type}","/"+ApplicationType.FORM_OCR_WORKFLOWS.name()));
                List<String> workflowResponseListCopy = new ArrayList<>();
                List<WorkflowResponse> workflowResponseList = getWorkflowResponse(newFile);
                if(CollectionUtils.isNotEmpty(workflowResponseList)){
                    Set<String> cetificationIdList = new HashSet<>();
                    List<V2_BlobObject> blobObjects = new ArrayList<>();
                    for (WorkflowResponse item: workflowResponseList){
                        Result result = item.getData();
                        JSONObject outputs = result.getOutputs();
                        JSONObject output = outputs.getJSONObject("output");
                        JSONObject content = output.getJSONObject("content");
                        JSONObject json = new JSONObject();
                        String cetificationId = content.getString("cetification_id");
                        if(StringUtils.isEmpty(cetificationId) || !cetificationIdList.add(cetificationId) ){
                            continue;
                        }
                        postProcessorJSON(content,ruleJSON);
                        json.put("certificateNo", cetificationId);
                        json.put("assetManufacturer",content.getString("manufactory"));
                        json.put("checkPerson",content.getString("checked_by"));
                        json.put("assetModel",content.getString("pattern"));
                        json.put("assetSN",content.getString("serial_num"));
                        json.put("assetName",content.getString("device_name"));
                        json.put("checkCompany",content.getString("check_company"));
                        json.put("department",content.getString("applicant"));
                        json.put("status",content.getString("conclusion"));
                        json.put("validDateTo",content.getString("valid_util"));
                        json.put("checkDate",content.getString("verify_date"));
                        json.put("certificateType",content.getString("certification_type"));
                        MeasuringCertificateDetail measuringCertificateDetail = handleOcrResult(json, newFile,blobObjects);
                        if(measuringCertificateDetail == null) continue;
                        List<V2_BlobObject> measuringAttachList = measuringCertificateDetail.getMeasuringAttachList();
                        if(CollectionUtils.isNotEmpty(measuringAttachList)){
                            blobObjects = measuringCertificateDetail.getMeasuringAttachList();
                        }
                        measuringCertificateDetailResult.add(measuringCertificateDetail);
                    }
                    return measuringCertificateDetailResult;
                }else{
                    LOGGER.info("AI 识别 失败：{}",newFile.getName());
                }
            }else{
                params.put("type", BusinessType.MEASURE.name());
                JSONObject jsonObject = null;
                jsonObject = templateService.getJsonObject(GatewayURL.OCR_DOCUMENT, params, newFile);
                JSONArray data = jsonObject.getJSONArray("data");
                if(data != null && data.size() > 0){
                    MeasuringCertificateDetail measuringCertificateDetail = handleOcrResult(data.getJSONObject(0), newFile);
                    measuringCertificateDetailResult.add(measuringCertificateDetail);
                    return measuringCertificateDetailResult;
                }
            }


        } catch (Throwable e) {
            LOGGER.error(e.getLocalizedMessage(),e.getMessage());

        }finally {
            if(newFile != null && newFile.exists()){
                newFile.delete();
            }
        }
        return null;
    }
    public JSONObject getRuleJSON(String ruleStr){
        try {
            if(StringUtils.isEmpty(ruleStr)) return null;
            return JSONObject.parseObject(ruleStr);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return null;
        }
    }

    public void postProcessorJSON(JSONObject data,JSONObject ruleJSON){
        LOGGER.info("data:{},ruleJSON:{}",data,ruleJSON);
        if(ruleJSON == null || ruleJSON.isEmpty()) return;
        for (Map.Entry<String, Object> stringObjectEntry : ruleJSON.entrySet()) {
            String key = stringObjectEntry.getKey();
            String regex =(String) stringObjectEntry.getValue();
            String value =(String) data.get(key);
            if(StringUtils.isEmpty(value))continue;
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(value);
            if(matcher.find()){
                String group = matcher.group(1);
                data.put(key,group);
            }
        }
        LOGGER.info("data:{}",data);
    }


    public List<WorkflowResponse> getWorkflowResponse(File newFile){
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<File> files = null;
        try {
            List<CompletableFuture<WorkflowResponse>> futures = new ArrayList<>();
            files = PDFSplitFileUtil.splitPdfByPage(newFile);
            String token = UserContextService.getAccessToken();
            for (File file : files) {
                CompletableFuture<WorkflowResponse> action = CompletableFuture.supplyAsync(() -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("type", ApplicationType.FORM_OCR_WORKFLOWS.name());
                    JSONObject s = templateService.getJsonObject(GatewayURL.FILES_UPLOAD_AND_WORKFLOWS_RUN, params, file, url -> url.replace("{type}", "/" + ApplicationType.FORM_OCR_WORKFLOWS.name()),token);
                    if (s != null) {
                        WorkflowResponse workflowResponse = s.toJavaObject(WorkflowResponse.class);
                        return workflowResponse;
                    } else {
                        return new WorkflowResponse();
                    }
                }, executor).handle((result, ex) -> { // handle 同时处理正常和异常
                    if (ex != null) {
                        // 异常
                        return new WorkflowResponse();
                    } else {
                        return result;
                    }
                });
                futures.add(action);
            }
            CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            all.join();
           return  futures.stream().map(item->{
                try {
                    return item.get();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getLocalizedMessage(),e.getMessage());
                } catch (ExecutionException e) {
                    LOGGER.error(e.getLocalizedMessage(),e.getMessage());
                }
                return  null;
            }).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        } catch (Throwable e) {
            LOGGER.error(e.getLocalizedMessage(),e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if(!CollectionUtils.isEmpty(files)){
                for (File file : files) {
                    if(file != null && file.exists()){
                        file.delete();
                    }
                }

            }
            executor.shutdown();
        }

    }

    private MeasuringCertificateDetail handleOcrResult(JSONObject data, File newFile)  {
        return handleOcrResult(data,newFile,null);
    }
    private MeasuringCertificateDetail handleOcrResult(JSONObject data, File newFile,List<V2_BlobObject> v2BlobObjects) {
        MeasuringCertificateDetail result = null;
        JSONObject object= data;
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        try {
            result = new MeasuringCertificateDetail();
            String assetSN = object.getString("assetSN");

            List<AssetInfo> assetInfos=new ArrayList<>();
            if(!StringUtils.isEmpty(assetSN)) {
                String[] split = assetSN.split("/");
                if(split.length > 0){
                    List<SearchFilter> searchFilters = new ArrayList<>();
                    searchFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.IN,Arrays.asList(split)));
                    searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUserAccount.getSiteUID()));
                    searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
                    assetInfos = assetInfoDao.findBySearchFilter(searchFilters);
                }

            }

//            if(CollectionUtils.isEmpty(assetInfos)){
//                result.setCertificateNo(object.getString("certificateNo"));
//                result.setErrorInfo(assetSN + ",未匹配到设备");
//                result.setStatus(-1);
//                return result;
//            }
            if(assetInfos.size() > 1){
                result.setCertificateNo(object.getString("certificateNo"));
                result.setErrorInfo(assetSN + ",匹配到"+assetInfos.size() +"台设备");
                result.setStatus(-1);
                return result;
            }
            if(CollectionUtils.isNotEmpty(assetInfos)){
                List<MeasuringCertificateDetail> collect = assetInfos.stream().map(item -> {
                    MeasuringCertificateDetail measureDetail = new MeasuringCertificateDetail();
                    setObject(measureDetail, object);
                    measureDetail.setAssetUID(item.getUid());
                    measureDetail.setAssetSN(item.getSerialNum());
                    measureDetail.setQrCode(item.getQrCode());
                    measureDetail.setSiteUID(item.getSiteUID());
                    measureDetail.setHospitalUID(item.getHospitalUID());
                    measureDetail.setInstitutionUID(item.getInstitutionUID());
                    measureDetail.setTenantUID(item.getTenantUID());
                    measureDetail.setCreatedBy(currentUserAccount.getUid());
                    measureDetail.setCreatedDate(new Date());
                    return measureDetail;
                }).collect(Collectors.toList());
                result = collect.get(0);
            }else{
                MeasuringCertificateDetail measureDetail = new MeasuringCertificateDetail();
                setObject(measureDetail, object);
                measureDetail.setSiteUID(currentUserAccount.getSiteUID());
                measureDetail.setHospitalUID(currentUserAccount.getHospitalUID());
                measureDetail.setInstitutionUID(currentUserAccount.getInstitutionUID());
                measureDetail.setTenantUID(currentUserAccount.getTenantUID());
                measureDetail.setCreatedBy(currentUserAccount.getUid());
                measureDetail.setCreatedDate(new Date());
                result = measureDetail;
            }

            List<SearchFilter> searchFilterList = new ArrayList<>();
            searchFilterList.add(new SearchFilter("certificateNo", SearchFilter.Operator.EQ,result.getCertificateNo()));
            searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
            List<MeasuringCertificateDetail> measuringCertificateDetails = measuringCertificateDetailDao.findBySearchFilter(searchFilterList);
            SystemEventAuditEnum eventAuditEnum = null;
            if(!CollectionUtils.isEmpty(measuringCertificateDetails)){
                MeasuringCertificateDetail detail = measuringCertificateDetails.get(0);
                BeanUtils.copyProperties(result, detail,beanNullProperty(result));
                // 校准证书(certificateType=2)忽略有效期, 确保历史记录的validDateTo被清空
                if (Integer.valueOf(2).equals(detail.getCertificateType())) {
                    detail.setValidDateTo(null);
                }
                measuringCertificateDetailDao.save(detail);
                result = detail;
                eventAuditEnum = SystemEventAuditEnum.MEASURE_UPDATE;
            }else{
                measuringCertificateDetailDao.save(result);
                eventAuditEnum = SystemEventAuditEnum.MEASURE_CREATE;
            }
            if(CollectionUtils.isEmpty(v2BlobObjects)){
                V2_BlobObject v2BlobObject = templateService.saveTemplateFile(fileToByteArray(newFile), result.getCertificateNo().replaceAll("/", "-") + ".pdf", "application/pdf", V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring, result.getId());
                result.setMeasuringAttachList(Arrays.asList(v2BlobObject));
            }else{
                templateService.saveBlobObject(v2BlobObjects,result.getId());
            }
            // 更新设备计量日期
            if(CollectionUtils.isNotEmpty(assetInfos)){
                updateMeasuringDate(assetInfos.get(0));
            }
            SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, result.getId(), Boolean.TRUE, "","measuring", JSONObject.toJSONString(result,(PropertyFilter)(o,n,v)->!n.equals("assetInfo")));
            systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e.getMessage(),e);
            result.setCertificateNo(object.getString("certificateNo"));
            result.setErrorInfo(e.getMessage());
            result.setStatus(-1);
            return result;
        }
        return result;
    }

    public static byte[] fileToByteArray(File file) throws IOException {
        // 确保文件存在
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + file.getAbsolutePath());
        }
        // 初始化字节输入流
        try (FileInputStream fis = new FileInputStream(file)) {
            // 获取文件长度
            long length = file.length();

            // 如果文件长度大于 Integer.MAX_VALUE，无法直接转换为字节数组
            if (length > Integer.MAX_VALUE) {
                throw new IOException("文件过大，无法转换为字节数组");
            }
            // 创建字节数组
            byte[] bytes = new byte[(int) length];
            // 读取文件内容到字节数组中
            int offset = 0;
            int numRead = 0;
            while ((numRead = fis.read(bytes, offset, bytes.length - offset)) > 0) {
                offset += numRead;
            }
            // 确认实际读取的字节数与文件长度相同
            if (offset < bytes.length) {
                throw new IOException("未能完全读取文件：" + file.getName());
            }
            return bytes;
        }
    }
    public  String[] beanNullProperty(Object assetImport) {
        final BeanWrapper src = new BeanWrapperImpl(assetImport);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullProperty = new HashSet<>();
        for (PropertyDescriptor item : pds) {
            Object value = src.getPropertyValue(item.getName());
            if (value == null) {
                nullProperty.add(item.getName());
            }
        }
        String[] properties = new String[nullProperty.size()];
        return nullProperty.toArray(properties);
    }
    public void setObject(MeasuringCertificateDetail detail,JSONObject object){
        detail.setCertificateNo(object.getString("certificateNo"));
        detail.setCertificateType(object.getInteger("certificateType"));
        detail.setAssetManufacturer(object.getString("assetManufacturer"));
        detail.setCheckPerson(object.getString("checkPerson"));
        detail.setAssetModel(object.getString("assetModel"));
        detail.setAssetSN(object.getString("assetSN"));
        detail.setAssetName(object.getString("assetName"));
        detail.setCheckCompany(object.getString("checkCompany"));
        detail.setDepartment(object.getString("department"));
        detail.setCheckDate(new Date());
        // 校准证书(certificateType=2)不填入"有效期至"字段
        if (!Integer.valueOf(2).equals(detail.getCertificateType())) {
            detail.setValidDateTo(new Date());
        }
        String status = object.getString("status");
        if(ObjectUtil.isNotEmpty(status)){
            List<I18nMessage> byMsgType = i18nMessageService.getByMsgType(MEASURING_STATUS);
            Optional<I18nMessage> first = byMsgType.stream().filter(item -> status.contains(item.getValue())).findFirst();
            if(first.isPresent()){
                I18nMessage i18nMessage = first.get();
                detail.setStatus(Integer.valueOf(i18nMessage.getMsgKey()));
            }else{
                detail.setStatus(1);
            }
        }else{
            detail.setStatus(3);
        }

        // 校准证书(certificateType=2)忽略有效期, 不填入"有效期至"字段
        if (!Integer.valueOf(2).equals(detail.getCertificateType())) {
            try {
                String validDateToStr = "";
                String validDateToVar = object.getString("validDateTo");
                if(ObjectUtil.isNotEmpty(validDateToVar)){
                    Matcher matcher = pattern.matcher(validDateToVar);
                    while (matcher.find()) {
                        validDateToStr += matcher.group();
                    }
                    Date validDateTo = DateUtil.parse(validDateToStr, "yyyyMMdd");
                    detail.setValidDateTo(validDateTo);
                }
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(),e.getMessage());
            }
        }
        try {
            String checkDateStr = "";
            String checkDateVar = object.getString("checkDate");
            if(ObjectUtil.isNotEmpty(checkDateVar)) {
                Matcher matcher = pattern.matcher(checkDateVar);
                while (matcher.find()) {
                    checkDateStr += matcher.group();
                }
                Date checkDate = DateUtil.parse(checkDateStr, "yyyyMMdd");
                detail.setCheckDate(checkDate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e.getMessage());
        }
    }
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        String name = zipEntry.getName();
        if(name.contains("/")){
            name = name.substring(name.lastIndexOf("/"));
        }
        File destFile = new File(destinationDir, name);
//
//        String destDirPath = destinationDir.getCanonicalPath();
//        String destFilePath = destFile.getCanonicalPath();
//
//        if (!destFilePath.startsWith(destDirPath + File.separator)) {
//            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//        }

        return destFile;
    }
    public static final String IMPORT_STATUS_NEW = "New";
    public static final String IMPORT_STATUS_CHECK_PASS = "CheckPass";
    public static final String IMPORT_STATUS_CHECK_FAIL = "CheckFail";
    public static final String IMPORT_STATUS_SUCCESS = "Success";
    public static final String IMPORT_STATUS_FAILURE = "Failure";

    /**
     * 安全读取单元格字符串值，处理所有类型包括ERROR（公式错误如#N/A、#VALUE!等）
     */
    private static String getCellStringValueSafe(Cell cell) {
        if (cell == null) return null;
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return new DecimalFormat("##############.#####").format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case ERROR:
            case BLANK:
                return null;
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return new DecimalFormat("##############.#####").format(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    /**
     * 安全读取单元格日期值，处理所有类型包括ERROR
     */
    private static Date getCellDateValueSafe(Cell cell, SimpleDateFormat sdf) {
        if (cell == null) return null;
        CellType type = cell.getCellTypeEnum();
        if (type == CellType.NUMERIC) {
            return cell.getDateCellValue();
        } else if (type == CellType.STRING) {
            try {
                return sdf.parse(cell.getStringCellValue());
            } catch (ParseException e) {
                return null;
            }
        } else if (type == CellType.FORMULA) {
            try {
                return cell.getDateCellValue();
            } catch (Exception e) {
                try {
                    return sdf.parse(cell.getStringCellValue());
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 仅解析和校验Excel数据，不保存数据库。返回所有行（含校验状态）。
     */
    public List<MeasuringCertificateDetail> validateExcel(UploadedFile file, OrgInfo selectedOrg) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        List<MeasuringCertificateDetail> resultList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputstream());
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return resultList;
        }
        Map<String, Integer> header = findHeader(sheet);
        if (header.isEmpty()) {
            return resultList;
        }
        for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
            StringBuilder errorInfo = new StringBuilder();
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            // 跳过全空行
            boolean allBlank = true;
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                    String val = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : null;
                    if (val != null && !val.trim().isEmpty()) {
                        allBlank = false;
                        break;
                    }
                }
            }
            if (allBlank) continue;

            MeasuringCertificateDetail detail = new MeasuringCertificateDetail();
            detail.setTenantUID(selectedOrg.getTenantUID());
            detail.setInstitutionUID(selectedOrg.getInstitutionUID());
            detail.setHospitalUID(selectedOrg.getHospitalUID());
            detail.setSiteUID(selectedOrg.getSiteUID());
            detail.setCertificateType(1);

            if(row.getCell(header.get("MANUFACTURE_CELL")) != null){
                detail.setAssetManufacturer(getCellStringValueSafe(row.getCell(header.get("MANUFACTURE_CELL"))));
            }
            if(row.getCell(header.get("MODEL_CELL")) != null) {
                String modelVal = getCellStringValueSafe(row.getCell(header.get("MODEL_CELL")));
                detail.setAssetModel(modelVal != null && CellType.NUMERIC.equals(row.getCell(header.get("MODEL_CELL")).getCellTypeEnum()) ? String.valueOf(Double.valueOf(row.getCell(header.get("MODEL_CELL")).getNumericCellValue()).intValue()) : modelVal);
            }
            if(row.getCell(header.get("APPLIANCE_NAME_CELL")) != null){
                detail.setAssetName(getCellStringValueSafe(row.getCell(header.get("APPLIANCE_NAME_CELL"))));
            }
            if(row.getCell(header.get("APPLIANCE_NO_CELL")) != null) {
                detail.setAssetNo(getCellStringValueSafe(row.getCell(header.get("APPLIANCE_NO_CELL"))));
            }
            if(row.getCell(header.get("CERTIFICATION_NO_CELL")) != null) {
                String tempCertificateNo = getCellStringValueSafe(row.getCell(header.get("CERTIFICATION_NO_CELL")));
                if(tempCertificateNo == null || tempCertificateNo.trim().equals("")){
                    tempCertificateNo = null;
                }
                detail.setCertificateNo(tempCertificateNo);
            }
            if(row.getCell(header.get("VERIFICATION_DATE_CELL")) != null) {
                try {
                    detail.setCheckDate(getCellDateValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL")), sdf));
                }catch (Exception ex){
                    errorInfo.append("[检定日期格式不对]");
                }
                Date checkDateVal = getCellDateValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL")), sdf);
                detail.setCheckDateDisplay(checkDateVal != null ? sdf.format(checkDateVal) : getCellStringValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL"))));
            }
            if(row.getCell(header.get("EXPIRE_DATE_CELL")) != null ) {
                boolean hasValue = false;
                try {
                    Cell expireDateCell = row.getCell(header.get("EXPIRE_DATE_CELL"));
                    CellType cellType = expireDateCell.getCellTypeEnum();
                    if (CellType.NUMERIC.equals(cellType) && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(expireDateCell)) {
                        hasValue = true;
                    } else if (CellType.STRING.equals(cellType)) {
                        String strVal = expireDateCell.getStringCellValue();
                        if (!StringUtils.isEmpty(strVal)) {
                            hasValue = true;
                        }
                    }
                    if(hasValue){
                        detail.setValidDateTo(getCellDateValueSafe(expireDateCell, sdf));
                    }
                }catch (Exception ex){
                    errorInfo.append("[有效期至格式不对]");
                }
                if(hasValue){
                    Date validDateToVal = getCellDateValueSafe(row.getCell(header.get("EXPIRE_DATE_CELL")), sdf);
                    detail.setValidDateToDisplay(validDateToVal != null ? sdf.format(validDateToVal) : getCellStringValueSafe(row.getCell(header.get("EXPIRE_DATE_CELL"))));
                }
            }
            if(row.getCell(header.get("VERIFICATION_UNIT_CELL")) != null){
                detail.setCheckCompany(getCellStringValueSafe(row.getCell(header.get("VERIFICATION_UNIT_CELL"))));
            }
            if(row.getCell(header.get("VERIFIER_CELL")) != null){
                detail.setCheckPerson(getCellStringValueSafe(row.getCell(header.get("VERIFIER_CELL"))));
            }
            if(row.getCell(header.get("REMARK_CELL")) != null){
                detail.setComments(getCellStringValueSafe(row.getCell(header.get("REMARK_CELL"))));
            }
            if(row.getCell(header.get("CLINICAL_DEPT_CELL")) != null){
                detail.setDepartment(getCellStringValueSafe(row.getCell(header.get("CLINICAL_DEPT_CELL"))));
            }
            if(row.getCell(header.get("ASSET_SN_CELL")) != null) {
                String assetSn = getCellStringValueSafe(row.getCell(header.get("ASSET_SN_CELL")));
                detail.setAssetSN(org.apache.commons.lang3.StringUtils.trimToNull(assetSn));
            }
            if(row.getCell(header.get("ASSET_NO_CELL")) != null) {
                String financeNo = getCellStringValueSafe(row.getCell(header.get("ASSET_NO_CELL")));
                detail.setFinanceNo(org.apache.commons.lang3.StringUtils.trimToNull(financeNo));
            }
            if(row.getCell(header.get("ITEM_CELL")) != null){
                detail.setCheckItem(getCellStringValueSafe(row.getCell(header.get("ITEM_CELL"))));
            }
            if(row.getCell(header.get("CATEGORY_CELL")) != null){
                detail.setType(getCellStringValueSafe(row.getCell(header.get("CATEGORY_CELL"))));
            }
            if(row.getCell(header.get("FILE_NO_CELL")) != null) {
                detail.setFileNo(getCellStringValueSafe(row.getCell(header.get("FILE_NO_CELL"))));
            }
            if(row.getCell(header.get("QR_CODE_CELL")) != null) {
                String qrCode = getCellStringValueSafe(row.getCell(header.get("QR_CODE_CELL")));
                detail.setQrCode(org.apache.commons.lang3.StringUtils.trimToNull(qrCode));
            }
            String conclusionStr = getCellStringValueSafe(row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")));
            detail.setStatusDisplay(conclusionStr);
            int status = 3;
            if (conclusionStr != null) {
                switch (conclusionStr) {
                    case "A级合格":
                    case "合格":
                    case "校准合格":
                        status = 1;
                        break;
                    case "不合格":
                        status = 2;
                        break;
                    default:
                        break;
                }
            }
            detail.setStatus(status);
            Integer certificateType = null;
            if(row.getCell(header.get("CERTIFICATE_TYPE_CELL")) != null){
                String certTypeStr = getCellStringValueSafe(row.getCell(header.get("CERTIFICATE_TYPE_CELL")));
                //证书类型: 检定证书输出：1，校准证书输出：2，其他输出：-1
                if (certTypeStr != null) {
                    switch (certTypeStr) {
                    case "检定证书":
                        certificateType = 1;
                        break;
                    case "校准证书":
                        certificateType = 2;
                        break;
                    default:
                        break;
                    }
                }
            }
            detail.setCertificateType(certificateType);
            detail.setExcelIdex(j + 1);

            if(detail.getCheckDate() != null && detail.getCheckDate().after(sdf.parse("99999/12/31"))){
                errorInfo.append("[检定日期不能超过99999/12/31]");
            }
            if(detail.getValidDateTo() != null && detail.getValidDateTo().after(sdf.parse("99999/12/31"))){
                errorInfo.append("[有效期至不能超过99999/12/31]");
            }
            if (ObjectUtil.isEmpty(row.getCell(header.get("VERIFICATION_DATE_CELL")))) {
                errorInfo.append("[检定日期不能为空]");
            }
            if (ObjectUtil.isAllEmpty(row.getCell(header.get("ASSET_SN_CELL")), row.getCell(header.get("ASSET_NO_CELL")), row.getCell(header.get("QR_CODE_CELL")))) {
                errorInfo.append("[(设备序列号SN)、(资产编号)和(二维码)不能同时为空]");
            }
            if(detail.getCertificateType() == null){
                errorInfo.append("[证书类型不能为空]");
            }
            // 证书编号校验
            if (detail.getCertificateNo() == null) {
                errorInfo.append("[计量证书编号不能为空]");
            } else {
                // 检查证书编号是否已绑定其他设备
                List<MeasuringCertificateDetail> mcdList = measuringCertificateDetailDao.findByCertificateNoAndTenantUIDOrderByCheckDateDesc(detail.getCertificateNo(), detail.getTenantUID());
                if (mcdList != null && !mcdList.isEmpty() && mcdList.get(0).getAssetUID() != null) {
                    MeasuringCertificateDetail existMcd = mcdList.get(0);
                    boolean snMatch = existMcd.getAssetSN() != null && Objects.equals(existMcd.getAssetSN(), detail.getAssetSN());
                    boolean financeNoMatch = existMcd.getFinanceNo() != null && Objects.equals(existMcd.getFinanceNo(), detail.getFinanceNo());
                    boolean qrCodeMatch = existMcd.getQrCode() != null && Objects.equals(existMcd.getQrCode(), detail.getQrCode());
                    if (!snMatch && !financeNoMatch && !qrCodeMatch) {
                        errorInfo.append("[计量证书编号已绑定其他设备]");
                    }
                }
                // 尝试匹配资产设备
                if (errorInfo.length() == 0) {
                    List<AssetInfo> matchedAssets = null;
                    if (ObjectUtil.isNotEmpty(detail.getAssetSN())) {
                        matchedAssets = this.getAssetInfoBySerialNum(detail.getAssetSN(), detail.getSiteUID());
                        if (matchedAssets != null && matchedAssets.size() > 1) {
                            errorInfo.append("[设备序列号SN匹配到(").append(matchedAssets.size()).append(")个资产设备]");
                        }
                    }
                    if (ObjectUtil.isNotEmpty(detail.getFinanceNo()) && (matchedAssets == null || matchedAssets.size() != 1)) {
                        matchedAssets = this.getAssetInfoByFinancingNum(detail.getFinanceNo(), detail.getSiteUID());
                        if (matchedAssets != null && matchedAssets.size() > 1) {
                            errorInfo.append("[资产编号匹配到(").append(matchedAssets.size()).append(")个资产设备]");
                        }
                    }
                    if (ObjectUtil.isNotEmpty(detail.getQrCode()) && (matchedAssets == null || matchedAssets.size() != 1)) {
                        matchedAssets = this.getAssetInfoByQrCode(detail.getQrCode(), detail.getSiteUID());
                        if (matchedAssets != null && matchedAssets.size() > 1) {
                            errorInfo.append("[二维码匹配到(").append(matchedAssets.size()).append(")个资产设备]");
                        }
                    }
                }
            }

            if (errorInfo.length() > 0) {
                detail.setErrorInfo(errorInfo.toString());
                detail.setImportStatus(IMPORT_STATUS_CHECK_FAIL);
            } else {
                detail.setImportStatus(IMPORT_STATUS_CHECK_PASS);
            }
            resultList.add(detail);
        }
        return resultList;
    }

    /**
     * 将校验通过的数据实际保存到数据库。返回导入失败的行。
     */
    public List<MeasuringCertificateDetail> importValidatedData(List<MeasuringCertificateDetail> validatedList) {
        List<MeasuringCertificateDetail> failedList = new ArrayList<>();
        boolean isShaoYiFu = isShaoYiFu();
        for (MeasuringCertificateDetail detail : validatedList) {
            if (!IMPORT_STATUS_CHECK_PASS.equals(detail.getImportStatus())) {
                // 校验未通过的行直接保留为失败
                detail.setImportStatus(IMPORT_STATUS_FAILURE);
                failedList.add(detail);
                continue;
            }
            try {
                MeasuringCertificateDetail result;
                if (isShaoYiFu) {
                    result = operateMeasuringAndAssetInfo_For_ShaoYiFu(detail);
                } else {
                    result = operateMeasuringAndAssetInfo(detail);
                }
                if (result != null) {
                    // operateMeasuringAndAssetInfo 返回非null表示导入失败
                    result.setImportStatus(IMPORT_STATUS_FAILURE);
                    failedList.add(result);
                } else {
                    detail.setImportStatus(IMPORT_STATUS_SUCCESS);
                }
            } catch (Exception e) {
                LOGGER.error("import measuring row {} error: {}", detail.getExcelIdex(), e.getMessage(), e);
                detail.setErrorInfo("[导入异常: " + e.getMessage() + "]");
                detail.setImportStatus(IMPORT_STATUS_FAILURE);
                failedList.add(detail);
            }
        }
        return failedList;
    }

    public List<MeasuringCertificateDetail> handleExcel(UploadedFile file, OrgInfo selectedOrg) throws IOException, ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        List<MeasuringCertificateDetail> tempMCDList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputstream());
//        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputstream());
           Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return null;
            }
            Map<String, Integer> header = findHeader(sheet);
            if(header.isEmpty()) {
                return null;
            }
            for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
                StringBuilder errorInfo = new StringBuilder();
//                String errorInfo = "";

                Row row = sheet.getRow(j);

                MeasuringCertificateDetail detail = new MeasuringCertificateDetail();
                detail.setTenantUID(selectedOrg.getTenantUID());
                detail.setInstitutionUID(selectedOrg.getInstitutionUID());
                detail.setHospitalUID(selectedOrg.getHospitalUID());
                detail.setSiteUID(selectedOrg.getSiteUID());
                detail.setCertificateType(1);

                if(row.getCell(header.get("MANUFACTURE_CELL")) != null){
                    detail.setAssetManufacturer(getCellStringValueSafe(row.getCell(header.get("MANUFACTURE_CELL"))));
                }

                if(row.getCell(header.get("MODEL_CELL")) != null) {
                    String modelVal = getCellStringValueSafe(row.getCell(header.get("MODEL_CELL")));
                    detail.setAssetModel(modelVal != null && CellType.NUMERIC.equals(row.getCell(header.get("MODEL_CELL")).getCellTypeEnum()) ? String.valueOf(Double.valueOf(row.getCell(header.get("MODEL_CELL")).getNumericCellValue()).intValue()) : modelVal);
                }

                if(row.getCell(header.get("APPLIANCE_NAME_CELL")) != null){
                    detail.setAssetName(getCellStringValueSafe(row.getCell(header.get("APPLIANCE_NAME_CELL"))));
                }

                if(row.getCell(header.get("APPLIANCE_NO_CELL")) != null) {
                    detail.setAssetNo(getCellStringValueSafe(row.getCell(header.get("APPLIANCE_NO_CELL"))));
                }

                if(row.getCell(header.get("CERTIFICATION_NO_CELL")) != null) {
                    String tempCertificateNo = getCellStringValueSafe(row.getCell(header.get("CERTIFICATION_NO_CELL")));
                    if(tempCertificateNo == null || tempCertificateNo.trim().equals("")){
                        tempCertificateNo = null;
                    }
                    detail.setCertificateNo(tempCertificateNo);
                }

                if(row.getCell(header.get("VERIFICATION_DATE_CELL")) != null) {
                    try {
                        detail.setCheckDate(getCellDateValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL")), sdf));
                    }catch (Exception ex){
                        errorInfo.append("[检定日期格式不对]");
                    }

                    Date checkDateVal = getCellDateValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL")), sdf);
                    detail.setCheckDateDisplay(checkDateVal != null ? sdf.format(checkDateVal) : getCellStringValueSafe(row.getCell(header.get("VERIFICATION_DATE_CELL"))));
                }
                if(row.getCell(header.get("EXPIRE_DATE_CELL")) != null ) {
                    boolean hasValue = false;
                    try {
                        Cell expireDateCell = row.getCell(header.get("EXPIRE_DATE_CELL"));
                        CellType cellType = expireDateCell.getCellTypeEnum();

                        if (CellType.NUMERIC.equals(cellType) && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(expireDateCell)) {
                            hasValue = true;
                        }

                        else if (CellType.STRING.equals(cellType)) {
                            String strVal = expireDateCell.getStringCellValue();
                            if (!StringUtils.isEmpty(strVal)) {
                                hasValue = true;
                            }
                        }
                        if(hasValue){
                            detail.setValidDateTo(getCellDateValueSafe(expireDateCell, sdf));
                        }
                    }catch (Exception ex){
                        errorInfo.append("[有效期至格式不对]");
                    }
                    if(hasValue){
                        Date validDateToVal = getCellDateValueSafe(row.getCell(header.get("EXPIRE_DATE_CELL")), sdf);
                        detail.setValidDateToDisplay(validDateToVal != null ? sdf.format(validDateToVal) : getCellStringValueSafe(row.getCell(header.get("EXPIRE_DATE_CELL"))));
                    }

                }
                if(row.getCell(header.get("VERIFICATION_UNIT_CELL")) != null){
                    detail.setCheckCompany(getCellStringValueSafe(row.getCell(header.get("VERIFICATION_UNIT_CELL"))));
                }
                if(row.getCell(header.get("VERIFIER_CELL")) != null){
                    detail.setCheckPerson(getCellStringValueSafe(row.getCell(header.get("VERIFIER_CELL"))));
                }
                if(row.getCell(header.get("REMARK_CELL")) != null){
                    detail.setComments(getCellStringValueSafe(row.getCell(header.get("REMARK_CELL"))));
                }
                if(row.getCell(header.get("CLINICAL_DEPT_CELL")) != null){
                    detail.setDepartment(getCellStringValueSafe(row.getCell(header.get("CLINICAL_DEPT_CELL"))));
                }
                if(row.getCell(header.get("ASSET_SN_CELL")) != null) {
                    String assetSn = getCellStringValueSafe(row.getCell(header.get("ASSET_SN_CELL")));
                    detail.setAssetSN(org.apache.commons.lang3.StringUtils.trimToNull(assetSn));
                }
                if(row.getCell(header.get("ASSET_NO_CELL")) != null) {
                    String financeNo = getCellStringValueSafe(row.getCell(header.get("ASSET_NO_CELL")));
                    detail.setFinanceNo(org.apache.commons.lang3.StringUtils.trimToNull(financeNo));
                }
                if(row.getCell(header.get("ITEM_CELL")) != null){
                    detail.setCheckItem(getCellStringValueSafe(row.getCell(header.get("ITEM_CELL"))));
                }
                if(row.getCell(header.get("CATEGORY_CELL")) != null){
                    detail.setType(getCellStringValueSafe(row.getCell(header.get("CATEGORY_CELL"))));
                }
                if(row.getCell(header.get("FILE_NO_CELL")) != null) {
                    detail.setFileNo(getCellStringValueSafe(row.getCell(header.get("FILE_NO_CELL"))));
                }
                if(row.getCell(header.get("QR_CODE_CELL")) != null) {
                    String qrCode = getCellStringValueSafe(row.getCell(header.get("QR_CODE_CELL")));
                    detail.setQrCode(org.apache.commons.lang3.StringUtils.trimToNull(qrCode));
                }
                String conclusionStr = getCellStringValueSafe(row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")));
                detail.setStatusDisplay(conclusionStr);
                int status = 3;
                if (conclusionStr != null) {
                    switch (conclusionStr) {
                        case "A级合格":
                        case "合格":
                        case "校准合格":
                            status = 1;
                            break;
                        case "不合格":
                            status = 2;
                            break;
                        default:
                            break;
                    }
                }
                detail.setStatus(status);
                detail.setExcelIdex(j + 1);

                if(detail.getCheckDate() != null && detail.getCheckDate().after(sdf.parse("99999/12/31"))){
                    errorInfo.append("[检定日期不能超过99999/12/31]");
                }

                if(detail.getValidDateTo() != null && detail.getValidDateTo().after(sdf.parse("99999/12/31"))){
                    errorInfo.append("[有效期至不能超过99999/12/31]");
                }

                //检定日期不能为空，设备序列号和资产编号 二维码 不能全为空
                if (ObjectUtil.isEmpty(row.getCell(header.get("VERIFICATION_DATE_CELL")))) {
                    errorInfo.append("[检定日期不能为空]");
                }
                if (ObjectUtil.isAllEmpty(row.getCell(header.get("ASSET_SN_CELL")), row.getCell(header.get("ASSET_NO_CELL")), row.getCell(header.get("QR_CODE_CELL")))) {
                    errorInfo.append("[(设备序列号SN)、(资产编号)和(二维码)不能同时为空]");
                }
                if (errorInfo.length() > 0) {
                    detail.setErrorInfo(errorInfo.toString());
                    tempMCDList.add(detail);
                    continue;
                }
                //如果是邵逸夫医院
                if(Boolean.TRUE.equals(isShaoYiFu())){
                    if(operateMeasuringAndAssetInfo_For_ShaoYiFu(detail) != null){
                        tempMCDList.add(detail);
                    }
                }else{
                    //除邵逸夫其他的医院逻辑(其他的医院逻辑不变)
                    if(operateMeasuringAndAssetInfo(detail) != null){
                        tempMCDList.add(detail);
                    }
                }
            }
        return tempMCDList;
    }

    //判断是否是邵逸夫医院
    public Boolean isShaoYiFu(){
        TenantInfo tenantInfo = tenantInfoDao.findByuid(UserContextService.getCurrentUserAccount().getTenantUID());
        if("3".equals(tenantInfo.getCustomTenant())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Map<String, Integer> findHeader(HSSFSheet sheet) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            boolean hasCertificationNo = false;
            boolean hasApplianceName = false;
            boolean hasManufacture = false;
            boolean hasModel = false;
            boolean hasAssetSN = false;
            boolean hasAssetNo = false;
            boolean hasApplianceNo = false;
            boolean hasVerificationConclusion = false;
            boolean hasVerificationDate = false;
            boolean hasExpireDate = false;
            boolean hasVerificationUnit = false;
            boolean hasVerifier = false;
            boolean hasItem = false;
            boolean hasCategory = false;
            boolean hasClinicalDept = false;
            boolean hasRemark = false;
            boolean hasFileNo = false;
            boolean hasQrCode = false;
            boolean hasCertificateType = false;

            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    hasCertificationNo = HEADER_CERTIFICATION_NO.equals(cell.getStringCellValue()) ? true : hasCertificationNo;
                    hasApplianceName = HEADER_APPLIANCE_NAME.equals(cell.getStringCellValue()) ? true : hasApplianceName;
                    hasManufacture = HEADER_MANUFACTURE.equals(cell.getStringCellValue()) ? true : hasManufacture;
                    hasModel = HEADER_MODEL.equals(cell.getStringCellValue()) ? true : hasModel;
                    hasAssetSN = HEADER_ASSET_SN.equals(cell.getStringCellValue()) ? true : hasAssetSN;
                    hasAssetNo = HEADER_ASSET_NO.equals(cell.getStringCellValue()) ? true : hasAssetNo;
                    hasApplianceNo = HEADER_APPLIANCE_NO.equals(cell.getStringCellValue()) ? true : hasApplianceNo;
                    hasVerificationConclusion = HEADER_VERIFICATION_CONCLUSION.equals(cell.getStringCellValue()) ? true : hasVerificationConclusion;
                    hasVerificationDate = HEADER_VERIFICATION_DATE.equals(cell.getStringCellValue()) ? true : hasVerificationDate;
                    hasExpireDate = HEADER_EXPIRE_DATE.equals(cell.getStringCellValue()) ? true : hasExpireDate;
                    hasVerificationUnit = HEADER_VERIFICATION_UNIT.equals(cell.getStringCellValue()) ? true : hasVerificationUnit;
                    hasVerifier = HEADER_VERIFIER.equals(cell.getStringCellValue()) ? true : hasVerifier;
                    hasItem = HEADER_ITEM.equals(cell.getStringCellValue()) ? true : hasItem;
                    hasCategory = HEADER_CATEGORY.equals(cell.getStringCellValue()) ? true : hasCategory;
                    hasClinicalDept = HEADER_CLINICAL_DEPT.equals(cell.getStringCellValue()) ? true : hasClinicalDept;
                    hasRemark = HEADER_REMARK.equals(cell.getStringCellValue()) ? true : hasRemark;
                    hasFileNo = HEADER_FILE_NO.equals(cell.getStringCellValue()) ? true : hasFileNo;
                    hasQrCode = HEADER_QR_CODE.equals(cell.getStringCellValue()) ? true : hasQrCode;
                    hasCertificateType = HEADER_CERTIFICATE_TYPE.equals(cell.getStringCellValue()) ? true : hasCertificateType;
                    switch (cell.getStringCellValue()) {
                        case HEADER_CERTIFICATION_NO:
                            hasCertificationNo = true;
                            header.put("CERTIFICATION_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NAME:
                            hasApplianceName = true;
                            header.put("APPLIANCE_NAME_CELL", j);
                            break;
                        case HEADER_MANUFACTURE:
                            hasManufacture = true;
                            header.put("MANUFACTURE_CELL", j);
                            break;
                        case HEADER_MODEL:
                            hasModel = true;
                            header.put("MODEL_CELL", j);
                            break;
                        case HEADER_ASSET_SN:
                            hasAssetSN = true;
                            header.put("ASSET_SN_CELL", j);
                            break;
                        case HEADER_ASSET_NO:
                            hasAssetNo = true;
                            header.put("ASSET_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NO:
                            hasApplianceNo = true;
                            header.put("APPLIANCE_NO_CELL", j);
                            break;
                        case HEADER_VERIFICATION_CONCLUSION:
                            hasVerificationConclusion = true;
                            header.put("VERIFICATION_CONCLUSION_CELL", j);
                            break;
                        case HEADER_VERIFICATION_DATE:
                            hasVerificationDate = true;
                            header.put("VERIFICATION_DATE_CELL", j);
                            break;
                        case HEADER_EXPIRE_DATE:
                            hasExpireDate = true;
                            header.put("EXPIRE_DATE_CELL", j);
                            break;
                        case HEADER_VERIFICATION_UNIT:
                            hasVerificationUnit = true;
                            header.put("VERIFICATION_UNIT_CELL", j);
                            break;
                        case HEADER_VERIFIER:
                            hasVerifier = true;
                            header.put("VERIFIER_CELL", j);
                            break;
                        case HEADER_ITEM:
                            hasItem = true;
                            header.put("ITEM_CELL", j);
                            break;
                        case HEADER_CATEGORY:
                            hasCategory = true;
                            header.put("CATEGORY_CELL", j);
                            break;
                        case HEADER_CLINICAL_DEPT:
                            hasClinicalDept = true;
                            header.put("CLINICAL_DEPT_CELL", j);
                            break;
                        case HEADER_REMARK:
                            hasRemark = true;
                            header.put("REMARK_CELL", j);
                            break;
                        case HEADER_FILE_NO:
                            hasFileNo = true;
                            header.put("FILE_NO_CELL", j);
                            break;
                        case HEADER_QR_CODE:
                            hasQrCode = true;
                            header.put("QR_CODE_CELL", j);
                            break;
                        case HEADER_CERTIFICATE_TYPE:
                            hasCertificateType = true;
                            header.put("CERTIFICATE_TYPE_CELL", j);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                }
            }
            if (hasCertificationNo && hasApplianceName && hasManufacture && hasModel && hasAssetSN && hasAssetNo
                    && hasApplianceNo && hasVerificationConclusion && hasVerificationDate && hasExpireDate
                    && hasVerificationUnit && hasVerifier && hasItem && hasCategory && hasClinicalDept && hasRemark && hasFileNo && hasCertificateType) {
                header.put("HEADER_ROW", i);
                break;
            }
        }
        return header;
    }


    private Map<String, Integer> findHeader(Sheet sheet) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            boolean hasCertificationNo = false;
            boolean hasApplianceName = false;
            boolean hasManufacture = false;
            boolean hasModel = false;
            boolean hasAssetSN = false;
            boolean hasAssetNo = false;
            boolean hasApplianceNo = false;
            boolean hasVerificationConclusion = false;
            boolean hasVerificationDate = false;
            boolean hasExpireDate = false;
            boolean hasVerificationUnit = false;
            boolean hasVerifier = false;
            boolean hasItem = false;
            boolean hasCategory = false;
            boolean hasClinicalDept = false;
            boolean hasRemark = false;
            boolean hasFileNo = false;
            boolean hasQrCode = false;
            boolean hasCertificateType = false;

            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    hasCertificationNo = HEADER_CERTIFICATION_NO.equals(cell.getStringCellValue()) ? true : hasCertificationNo;
                    hasApplianceName = HEADER_APPLIANCE_NAME.equals(cell.getStringCellValue()) ? true : hasApplianceName;
                    hasManufacture = HEADER_MANUFACTURE.equals(cell.getStringCellValue()) ? true : hasManufacture;
                    hasModel = HEADER_MODEL.equals(cell.getStringCellValue()) ? true : hasModel;
                    hasAssetSN = HEADER_ASSET_SN.equals(cell.getStringCellValue()) ? true : hasAssetSN;
                    hasAssetNo = HEADER_ASSET_NO.equals(cell.getStringCellValue()) ? true : hasAssetNo;
                    hasApplianceNo = HEADER_APPLIANCE_NO.equals(cell.getStringCellValue()) ? true : hasApplianceNo;
                    hasVerificationConclusion = HEADER_VERIFICATION_CONCLUSION.equals(cell.getStringCellValue()) ? true : hasVerificationConclusion;
                    hasVerificationDate = HEADER_VERIFICATION_DATE.equals(cell.getStringCellValue()) ? true : hasVerificationDate;
                    hasExpireDate = HEADER_EXPIRE_DATE.equals(cell.getStringCellValue()) ? true : hasExpireDate;
                    hasVerificationUnit = HEADER_VERIFICATION_UNIT.equals(cell.getStringCellValue()) ? true : hasVerificationUnit;
                    hasVerifier = HEADER_VERIFIER.equals(cell.getStringCellValue()) ? true : hasVerifier;
                    hasItem = HEADER_ITEM.equals(cell.getStringCellValue()) ? true : hasItem;
                    hasCategory = HEADER_CATEGORY.equals(cell.getStringCellValue()) ? true : hasCategory;
                    hasClinicalDept = HEADER_CLINICAL_DEPT.equals(cell.getStringCellValue()) ? true : hasClinicalDept;
                    hasRemark = HEADER_REMARK.equals(cell.getStringCellValue()) ? true : hasRemark;
                    hasFileNo = HEADER_FILE_NO.equals(cell.getStringCellValue()) ? true : hasFileNo;
                    hasQrCode = HEADER_QR_CODE.equals(cell.getStringCellValue()) ? true : hasQrCode;
                    hasCertificateType = HEADER_CERTIFICATE_TYPE.equals(cell.getStringCellValue()) ? true : hasCertificateType;
                    switch (cell.getStringCellValue()) {
                        case HEADER_CERTIFICATION_NO:
                            hasCertificationNo = true;
                            header.put("CERTIFICATION_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NAME:
                            hasApplianceName = true;
                            header.put("APPLIANCE_NAME_CELL", j);
                            break;
                        case HEADER_MANUFACTURE:
                            hasManufacture = true;
                            header.put("MANUFACTURE_CELL", j);
                            break;
                        case HEADER_MODEL:
                            hasModel = true;
                            header.put("MODEL_CELL", j);
                            break;
                        case HEADER_ASSET_SN:
                            hasAssetSN = true;
                            header.put("ASSET_SN_CELL", j);
                            break;
                        case HEADER_ASSET_NO:
                            hasAssetNo = true;
                            header.put("ASSET_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NO:
                            hasApplianceNo = true;
                            header.put("APPLIANCE_NO_CELL", j);
                            break;
                        case HEADER_VERIFICATION_CONCLUSION:
                            hasVerificationConclusion = true;
                            header.put("VERIFICATION_CONCLUSION_CELL", j);
                            break;
                        case HEADER_VERIFICATION_DATE:
                            hasVerificationDate = true;
                            header.put("VERIFICATION_DATE_CELL", j);
                            break;
                        case HEADER_EXPIRE_DATE:
                            hasExpireDate = true;
                            header.put("EXPIRE_DATE_CELL", j);
                            break;
                        case HEADER_VERIFICATION_UNIT:
                            hasVerificationUnit = true;
                            header.put("VERIFICATION_UNIT_CELL", j);
                            break;
                        case HEADER_VERIFIER:
                            hasVerifier = true;
                            header.put("VERIFIER_CELL", j);
                            break;
                        case HEADER_ITEM:
                            hasItem = true;
                            header.put("ITEM_CELL", j);
                            break;
                        case HEADER_CATEGORY:
                            hasCategory = true;
                            header.put("CATEGORY_CELL", j);
                            break;
                        case HEADER_CLINICAL_DEPT:
                            hasClinicalDept = true;
                            header.put("CLINICAL_DEPT_CELL", j);
                            break;
                        case HEADER_REMARK:
                            hasRemark = true;
                            header.put("REMARK_CELL", j);
                            break;
                        case HEADER_FILE_NO:
                            hasFileNo = true;
                            header.put("FILE_NO_CELL", j);
                            break;
                        case HEADER_QR_CODE:
                            hasQrCode = true;
                            header.put("QR_CODE_CELL", j);
                            break;
                        case HEADER_CERTIFICATE_TYPE:
                            hasCertificateType = true;
                            header.put("CERTIFICATE_TYPE_CELL", j);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                }
            }
            if (hasCertificationNo && hasApplianceName && hasManufacture && hasModel && hasAssetSN && hasAssetNo
                    && hasApplianceNo && hasVerificationConclusion && hasVerificationDate && hasExpireDate
                    && hasVerificationUnit && hasVerifier && hasItem && hasCategory && hasClinicalDept && hasRemark && hasFileNo && hasCertificateType) {
                header.put("HEADER_ROW", i);
                break;
            }
        }
        return header;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public void postProcessorExcel(SystemEventAuditEnum eventAuditEnum, String eventData){
        try{
            SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, null, Boolean.TRUE, "", AuditBoTypeEnum.TABLE_DOWNLOAD.getBoType(), eventData);
            systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

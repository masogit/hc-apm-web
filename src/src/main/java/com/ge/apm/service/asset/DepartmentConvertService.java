package com.ge.apm.service.asset;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.AssetDepartmentConvertRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ge.apm.service.asset.DepartmentConvertService.ASSET_DETAIL.*;

@Service
public class DepartmentConvertService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentConvertService.class);
    protected enum ASSET_DETAIL {
        asset_name, depart_num, financing_num,serial_num , function_type, status, out_dept_name,in_dept_name,out_date,
        signature
    }

    @Autowired
    private AssetDepartmentConvertRepository assetDepartmentConvertRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;

    @Autowired
    private BlobFileService blobFileService;

    public byte[] getActiveTemplateBytes() throws IOException {
        byte[] templateBytes = null;

        FileDownloadService   fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        templateBytes=fileDownloadService.getTemplateFileByte(SystemTemplateEnum.DEPARTMENT_CONVERT.getCode(), SystemTemplateEnum.DEPARTMENT_CONVERT.getFileType());

        return templateBytes;
    }


    public StreamedContent export(List<String> selectedIds) throws IOException {
        byte[] templateBytes = getActiveTemplateBytes();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(out)) {
            selectedIds.forEach(item -> {
                AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(item);
                if (assetDepartmentConvert != null) {
                    try {
                        byte[] bytes = generate(assetDepartmentConvert, templateBytes);
                        if (bytes != null) {
                            String name = assetDepartmentConvert.getAssetName() +assetDepartmentConvert.getBusinessNumber()+ "-" + "调拨单" + ".docx";
                            zout.putNextEntry(new ZipEntry(name));
                            IOUtils.write(bytes, zout);
                            zout.closeEntry();

                        }
                    } catch (Exception e) {
                        logger.error("assetUid:{},调拨单导出失败导出异常：e", assetDepartmentConvert.getAssetUid(), e);
                    }
                }

            });
            zout.close();
            return new ApmStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "调拨单.zip");
        } catch (Exception e) {
            logger.error("调拨单导出失败 e:", e);
            return null;
        }
    }


    private byte[] generate(AssetDepartmentConvert assetDepartmentConvert, byte[] templateBytes) {
        Map<String, Object> datas = getData(assetDepartmentConvert);
        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("list", new DepartmentConvertRenderPolicy());
        return  WordExportServiceUtil.generate(datas,templateBytes, bindings);
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public Map<String, Object> getData(AssetDepartmentConvert assetDepartmentConvert) {
        Map<String, Object> map = new HashMap<>();

        AssetInfo assetInfo = assetInfoRepository.findByUid(assetDepartmentConvert.getAssetUid());

        map.put(asset_name.name(), assetInfo.getName());
        map.put(depart_num.name(), assetInfo.getDepartNum());
        map.put(financing_num.name(), assetInfo.getFinancingNum());
        map.put(function_type.name(), assetInfo.getFunctionType());
        map.put(serial_num.name(), assetInfo.getSerialNum());
        map.put(out_dept_name.name(), assetDepartmentConvert.getOutDeptName());
        map.put(in_dept_name.name(), assetDepartmentConvert.getInDeptName());
        if(assetDepartmentConvert.getTransferInAt()!=null){
            String date = SDF.format(assetDepartmentConvert.getTransferInAt());
            map.put(out_date.name(), date);
        }
        String statusName=getStatusName ("assetStatus",assetInfo.getStatus().toString());
        map.put(status.name(), statusName);
        List<MultiLevelApproval> multiLevelApprovals1 = new ArrayList<>();
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(assetDepartmentConvert.getBusinessUid());
        Map<Integer, List<MultiLevelApproval>> listMap = multiLevelApprovals.stream().filter(item -> Objects.equals(item.getApprovalStatus(), 2)).collect(Collectors.groupingBy(MultiLevelApproval::getApprovalLevel));
        listMap.forEach((id, approvalList) -> {
            List<MultiLevelApproval> collect = approvalList.stream().sorted(Comparator.comparing(MultiLevelApproval::getActualApproverDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
            multiLevelApprovals1.add(collect.get(0));
        });

        List<MultiLevelApproval> levelApprovals = multiLevelApprovals1.stream().sorted(Comparator.comparing(MultiLevelApproval::getApprovalLevel, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=1;i<=levelApprovals.size();i++){
            if(i>3){
                logger.info("审批节点大于三");
                break;
            }
            if(StringUtils.isBlank(levelApprovals.get(i-1).getAuditSignature())){
                continue;
            }
            byte[] levelSignatureBytes = getSignatureBytes(levelApprovals.get(i-1).getAuditSignature());
            map.put(signature.name()+i, new PictureRenderData(100, 80, PictureType.PNG, levelSignatureBytes));
        }

        for(MultiLevelApproval multiLevelApproval : multiLevelApprovals){
            Map<String, Object> mm = new HashMap<>();
            mm.put("approvalNumber", multiLevelApproval.getApprovalNumber());
            mm.put("businessNumber", multiLevelApproval.getBusinessNumber());
            mm.put("businessType", assetDepartmentConvert.getBusinessType() == ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT ? ApprovalConstant.APPROVAL_ASSET_DEPARTMENT_CONVERT_CHI : ApprovalConstant.APPROVAL_BATH_ASSET_DEPARTMENT_CONVERT_CHI);
            mm.put("approvalLevel", multiLevelApproval.getApprovalLevel() + "级审批人");
            mm.put("actualApproverName", multiLevelApproval.getActualApproverName());
            mm.put("approvalNodeStatus", getApprovalNodeStatus(multiLevelApproval.getApprovalNodeStatus()));
            mm.put("actualApproverDate", DateUtil.format(multiLevelApproval.getActualApproverDate(), "yyyy-MM-dd HH:mm:ss"));
            list.add(mm);
        }
        map.put("list", list);
        return map;
    }

    public String getApprovalNodeStatus(Integer approvalNodeStatus){
        if(null == approvalNodeStatus){
            return null;
        }
        switch (approvalNodeStatus){
            case 0:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME;
            case 2:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME;
            case 3:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME;
            case 4:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_RETURN_NAME;
        }
        return null;
    }

    public static String getStatusName(String fieldName, String msgKey) {
        String key = fieldName + "-" + msgKey;
        String value = WebUtil.getMessage(key);

        if (key.equals(value))
            return "";  // this means the message not found.
        else
            return value;
    }
    public byte[] getSignatureBytes(String storageId) {
        byte[] signatureBytes = null;

        try {
            signatureBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(storageId).getStream());
        } catch (IOException e) {
            logger.info("Error happens while get signature bytes", e);
        }

        return signatureBytes;
    }

    public List<AssetInfo>  getAssetInfoList(List<String> assetUid){
       return assetInfoRepository.findByUidIn(assetUid);
    }
}

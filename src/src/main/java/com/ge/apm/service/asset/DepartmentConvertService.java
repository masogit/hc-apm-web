package com.ge.apm.service.asset;


import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.ge.apm.dao.AssetDepartmentConvertRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.domain.*;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.context.LocaleHolder;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

        String  fileName = "transferorder_template.docx";
        templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/" + fileName));
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
                            String name = assetDepartmentConvert.getAssetName() + "-" + "调拨单" + ".docx";
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XWPFTemplate template = XWPFTemplate.compile(new ByteArrayInputStream(templateBytes));
        template.render(datas);
        try {
            template.write(out);
        } catch (IOException e) {
            logger.error("调拨单导出失败 generate error:", e);
            return null;
        }
        return out.toByteArray();
    }
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
        map.put(out_date.name(), assetDepartmentConvert.getTransferInAt());
        String statusName=getStatusName ("assetStatus",assetInfo.getStatus().toString());
        map.put(status.name(), statusName);
        List<MultiLevelApproval> multiLevelApprovals1 = new ArrayList<>();
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(assetDepartmentConvert.getBusinessUid());
        Map<Integer, List<MultiLevelApproval>> listMap = multiLevelApprovals.stream().filter(item -> item.getApprovalStatus() == 2).collect(Collectors.groupingBy(MultiLevelApproval::getApprovalLevel));
        listMap.forEach((id, approvalList) -> {
            List<MultiLevelApproval> collect = approvalList.stream().sorted(Comparator.comparing(MultiLevelApproval::getActualApproverDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
            multiLevelApprovals1.add(collect.get(0));
        });

        List<MultiLevelApproval> levelApprovals = multiLevelApprovals1.stream().sorted(Comparator.comparing(MultiLevelApproval::getApprovalLevel, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        for (int i=1;i<=levelApprovals.size();i++){
            if(i>3){
                logger.info("审批节点大于三");
                break;
            }
            byte[] levelSignatureBytes = getSignatureBytes(levelApprovals.get(i-1).getAuditSignature());
            map.put(signature.name()+i, new PictureRenderData(100, 80, "signature.png", levelSignatureBytes));
        }
        return map;
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
}

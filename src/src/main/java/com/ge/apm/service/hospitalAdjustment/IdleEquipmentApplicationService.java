package com.ge.apm.service.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class IdleEquipmentApplicationService {

    public static final Logger log = LoggerFactory.getLogger(IdleEquipmentApplicationService.class);

    @Autowired
    private IdleEquipmentApplicationRepository dao;
    @Autowired
    private IdleEquipmentStockRepository stockRepository;
    @Autowired
    private SystemTemplateService systemTemplateService;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Transactional
    public void idleEquipmentApproval(IdleEquipmentApplication selected, UserAccount currentUserAccount){
        AssetInfo assetInfo = assetInfoRepository.findByUid(selected.getAssetUid());
        IdleEquipmentStock idleEquipmentStock = new IdleEquipmentStock();
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setCreatedDate(new Date());
        idleEquipmentStock.setCreatedBy(currentUserAccount.getUid());
        idleEquipmentStock.setApplicationNumber(selected.getApplicationNumber());
        idleEquipmentStock.setApplicationUid(selected.getUid());
        idleEquipmentStock.setAssetUid(selected.getAssetUid());
        idleEquipmentStock.setStockSiteUid(selected.getStockSiteUid());
        idleEquipmentStock.setStockDeptUid(selected.getStockDeptUid());
        idleEquipmentStock.setStockHospitalUid(selected.getStockHospitalUid());
        idleEquipmentStock.setTenantUid(currentUserAccount.getTenantUID());
        idleEquipmentStock.setInstitutionUid(assetInfo.getInstitutionUID());
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }

    public  byte[] download(IdleEquipmentApplication selected ) throws IOException {

        IdleEquipmentApplication idleEquipmentApplication = dao.findByUid(selected.getUid());
        UserAccount user = UserContextService.getCurrentUserAccount();
        byte[] byteArray = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.IDLE_EQUIPMENT_APPLICATION, user);
        HashMap<String, Object> datas= buildDatas(idleEquipmentApplication);
        return WordExportServiceUtil.generate(datas, byteArray, null);
    }

    TextRenderData checked = new TextRenderData("R", new Style("Wingdings 2", 10.5));

    TextRenderData unchecked = new TextRenderData("\u00A3", new Style("Wingdings 2", 10.5));
    private HashMap<String, Object> buildDatas(IdleEquipmentApplication idleEquipmentApplication) {

        HashMap<String, Object> params = new HashMap<>();
//        IdleEquipmentStock idleEquipmentStock = adjustmentTransferApplication.getIdleEquipmentStock();
        AssetInfo assetInfo = assetInfoRepository.findByUid(idleEquipmentApplication.getAssetUid());
//
        params.put("ck_lead", 1==idleEquipmentApplication.getMedicalConsortiumType() ? checked : unchecked);
        params.put("ck_town", 2==idleEquipmentApplication.getMedicalConsortiumType()  ? checked : unchecked);
        params.put("ck_center", 3==idleEquipmentApplication.getMedicalConsortiumType()  ? checked : unchecked);
        params.put("ck_station", 4==idleEquipmentApplication.getMedicalConsortiumType()  ? checked : unchecked);
//        OrgInfo borrowDeptName = orgInfoRepository.getByUid(adjustmentTransferApplication.getBorrowDeptUid());
        params.put("company_name",idleEquipmentApplication.getCompanyName());
//        params.put("dept_name",borrowDeptName.getName());
        params.put("contact_person",idleEquipmentApplication.getContactPerson());
        params.put("contact_number",idleEquipmentApplication.getContactNumber());
        params.put("application_date", DateUtil.format(idleEquipmentApplication.getApplicationDate(), DatePattern.CHINESE_DATE_FORMAT));
        params.put("asset_name",assetInfo.getName());
        params.put("function_type",assetInfo.getFunctionType());
        params.put("manufacture",assetInfo.getManufacture());
        params.put("purchase_date", DateUtil.format(assetInfo.getPurchaseDate(), DatePattern.CHINESE_DATE_FORMAT));
        if(assetInfo.getPurchasePrice()!=null){
            params.put("purchase_price", BigDecimal.valueOf(assetInfo.getPurchasePrice()));

        }
        params.put("used_years",idleEquipmentApplication.getUsedYears());
        params.put("depreciation_period_years",idleEquipmentApplication.getDepreciationPeriodYears());

        params.put("over_depreciation_y", Boolean.TRUE.equals(idleEquipmentApplication.getOverDepreciation()) ? checked : unchecked);
        params.put("over_depreciation_n", !Boolean.TRUE.equals(idleEquipmentApplication.getOverDepreciation())  ? checked : unchecked);
        params.put("over_depreciation_reason",idleEquipmentApplication.getOverDepreciationReason());

        params.put("perfect", "PERFECT".equals(idleEquipmentApplication.getDeviceStatus()) ? checked : unchecked);
        params.put("repairable", "REPAIRABLE".equals(idleEquipmentApplication.getDeviceStatus())  ? checked : unchecked);
        params.put("still_usable","STILL_USABLE".equals(idleEquipmentApplication.getDeviceStatus()) ? checked : unchecked);
        params.put("maintenance_summary",idleEquipmentApplication.getMaintenanceSummary());
        params.put("purchase_voucher_no",idleEquipmentApplication.getPurchaseVoucherNo());
        params.put("state_owned", "STATE_OWNED".equals(idleEquipmentApplication.getPropertyNature()) ? checked : unchecked);
        params.put("other", "OTHER".equals(idleEquipmentApplication.getPropertyNature())  ? checked : unchecked);
        params.put("property_nature_desc",idleEquipmentApplication.getPropertyNatureDesc());

        params.put("rights_defect_y", Boolean.TRUE.equals(idleEquipmentApplication.getHasRightsDefect()) ? checked : unchecked);
        params.put("rights_defect_n", !Boolean.TRUE.equals(idleEquipmentApplication.getHasRightsDefect())  ? checked : unchecked);
        params.put("rights_defect_description",idleEquipmentApplication.getRightsDefectDescription());

        params.put("business_adjustment", "BUSINESS_ADJUSTMENT".equals(idleEquipmentApplication.getIdleReasons()) ? checked : unchecked);
        params.put("update_replacement", "UPDATE_REPLACEMENT".equals(idleEquipmentApplication.getIdleReasons())  ? checked : unchecked);
        params.put("department_merge","DEPARTMENT_MERGE".equals(idleEquipmentApplication.getIdleReasons()) ? checked : unchecked);
        params.put("idle_reasons_other","OTHER".equals(idleEquipmentApplication.getIdleReasons()) ? checked : unchecked);
        params.put("other_reason_description",idleEquipmentApplication.getOtherReasonDescription());
        params.put("expected_idle_years",idleEquipmentApplication.getExpectedIdleYears());
        params.put("expected_idle_months",idleEquipmentApplication.getExpectedIdleMonths());




        params.put("audit_result_t", Boolean.TRUE.equals(idleEquipmentApplication.getAuditResult()) ? checked : unchecked);
        params.put("audit_result_f", Boolean.FALSE.equals(idleEquipmentApplication.getAuditResult())  ? checked : unchecked);
        params.put("registration_number",idleEquipmentApplication.getRegistrationNumber());
        params.put("audit_result_reason",idleEquipmentApplication.getAuditResultReason());

        List<String> strings = JSON.parseArray(idleEquipmentApplication.getAttachments(), String.class);
        if(strings==null){
            strings=new ArrayList<>();

        }

        params.put("ContractInvoice", strings.contains("ContractInvoice")? checked : unchecked);
        params.put("MaintenanceRecord", strings.contains("MaintenanceRecord")? checked : unchecked);
        params.put("InspectionReport", strings.contains("InspectionReport")? checked : unchecked);
        params.put("PhysicalPhoto", strings.contains("PhysicalPhoto")? checked : unchecked);
        params.put("attachment_other", strings.contains("other")? checked : unchecked);
        if(strings.contains("other")){
            params.put("attachments_other_description", idleEquipmentApplication.getAttachmentsOtherDescription());

        }

        if(idleEquipmentApplication.getAuditSignature()!=null){
            byte[] handlerSignature = getSignatureBytes(idleEquipmentApplication.getAuditSignature());
            params.put("audit_signature", new PictureRenderData(100, 70, PictureType.PNG, handlerSignature));

        }
        params.put("audit_date",DateUtil.format(idleEquipmentApplication.getAuditDate(), DatePattern.CHINESE_DATE_FORMAT));

        return params;
    }

    public byte[] getSignatureBytes(String storageId) {
        byte[] signatureBytes = null;

        try {
            signatureBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(storageId).getStream());
        } catch (IOException e) {
            log.info("Error happens while get signature bytes", e);
        }

        return signatureBytes;
    }
}

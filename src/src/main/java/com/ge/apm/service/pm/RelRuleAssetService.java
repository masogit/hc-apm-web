package com.ge.apm.service.pm;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetInfoAndRelRule;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RelRuleAssetService {

    @Autowired
    private PmOrderRuleRepository pmOrderRuleRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    public StreamedContent getExcelFile(List<AssetInfoAndRelRule> assetInfoAndRelRules) throws IOException{
        HSSFWorkbook b=new HSSFWorkbook();
        CellStyle cellStyle = b.createCellStyle();
        HSSFSheet sheet= b.createSheet();
        createExcelHeader(sheet,cellStyle);
        createExcelBody(sheet,assetInfoAndRelRules);
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        for(int i=0;i<19;i++) {
            sheet.autoSizeColumn(i);
        }
        b.write(os);
        ByteArrayInputStream is=new ByteArrayInputStream(os.toByteArray());//不能直接使用 b.getBytes();
        DefaultStreamedContent ret= new DefaultStreamedContent( is );
        ret.setName("RelRuleAsset_export.xls");
        ret.setContentType("application/vnd.ms-excel");
        return ret;
    }

    private void createExcelBody(HSSFSheet sheet,List<AssetInfoAndRelRule> assetInfoAndRelRules) {
        int i = 1;
        for(AssetInfoAndRelRule assetInfoAndRelRule:assetInfoAndRelRules) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            HSSFRow row=sheet.createRow(i);
            if(assetInfoAndRelRule.getInstallDate()!=null) row.createCell(0).setCellValue(simpleDateFormat.format(assetInfoAndRelRule.getInstallDate()));
            if(assetInfoAndRelRule.getPurchaseDate()!=null) row.createCell(1).setCellValue(simpleDateFormat.format(assetInfoAndRelRule.getPurchaseDate()));
            if(assetInfoAndRelRule.getAssetName()!=null) row.createCell(2).setCellValue(assetInfoAndRelRule.getAssetName());
            if(assetInfoAndRelRule.getAssetOwnerName()!=null) row.createCell(3).setCellValue(assetInfoAndRelRule.getAssetOwnerName());
            if(assetInfoAndRelRule.getPmOwnerName()!=null) row.createCell(4).setCellValue(assetInfoAndRelRule.getPmOwnerName());
            if(assetInfoAndRelRule.getLevelOneName()!=null) row.createCell(5).setCellValue(assetInfoAndRelRule.getLevelOneName());
            if(assetInfoAndRelRule.getLevelTwoName()!=null) row.createCell(6).setCellValue(assetInfoAndRelRule.getLevelTwoName());
            if(assetInfoAndRelRule.getLevelThreeName()!=null) row.createCell(7).setCellValue(assetInfoAndRelRule.getLevelThreeName());
            if(assetInfoAndRelRule.getLevelFourName()!=null) row.createCell(8).setCellValue(assetInfoAndRelRule.getLevelFourName());
            if(assetInfoAndRelRule.getDepartNum()!=null) row.createCell(9).setCellValue(assetInfoAndRelRule.getDepartNum());
            if(assetInfoAndRelRule.getAssetGroupName()!=null) row.createCell(10).setCellValue(assetInfoAndRelRule.getAssetGroupName());
            if(assetInfoAndRelRule.getClinicalDeptName()!=null) row.createCell(11).setCellValue(assetInfoAndRelRule.getClinicalDeptName());
            if(assetInfoAndRelRule.getNationalStandardCode()!=null) row.createCell(12).setCellValue(assetInfoAndRelRule.getNationalStandardCode());
            if(assetInfoAndRelRule.getMakerShortName()!=null) row.createCell(13).setCellValue(assetInfoAndRelRule.getMakerShortName());
            if(assetInfoAndRelRule.getManufacture()!=null) row.createCell(14).setCellValue(assetInfoAndRelRule.getManufacture());
            if(assetInfoAndRelRule.getCreatedDate()!=null) row.createCell(15).setCellValue(simpleDateFormat.format(assetInfoAndRelRule.getCreatedDate()));
            if(assetInfoAndRelRule.getQrCode()!=null) row.createCell(16).setCellValue(assetInfoAndRelRule.getQrCode());
            if(assetInfoAndRelRule.getSerialNum()!=null) row.createCell(17).setCellValue(assetInfoAndRelRule.getSerialNum());
            if(assetInfoAndRelRule.getSiteName()!=null) row.createCell(18).setCellValue(assetInfoAndRelRule.getSiteName());
            i++;
        }
    }

    private void createExcelHeader(HSSFSheet sheet,CellStyle cellStyle) {
        HSSFRow row=sheet.createRow(0);
        row.createCell(0).setCellValue("启用日期");
        row.createCell(1).setCellValue("采购日期");
        row.createCell(2).setCellValue("资产名称");
        row.createCell(3).setCellValue("维修负责人");
        row.createCell(4).setCellValue("保养负责人");
        row.createCell(5).setCellValue("巡检");
        row.createCell(6).setCellValue("二级规则");
        row.createCell(7).setCellValue("三级规则");
        row.createCell(8).setCellValue("质量控制");
        row.createCell(9).setCellValue("设备编号（院方）");
        row.createCell(10).setCellValue("设备分类");
        row.createCell(11).setCellValue("所属科室");
        row.createCell(12).setCellValue("设备国标码");
        row.createCell(13).setCellValue("品牌/制造商简称");
        row.createCell(14).setCellValue("制造商");
        row.createCell(15).setCellValue("配置日期");
        row.createCell(16).setCellValue("二维码");
        row.createCell(17).setCellValue("制造商序列号");
        row.createCell(18).setCellValue("所属院区");
    }

    public List<AssetInfoAndRelRule> mergeAssetInfoAndRelRule(List<AssetInfo> assetInfos, List<RelRuleAsset> relRuleAssets) {
        List<AssetInfoAndRelRule> result = new ArrayList<>();
        Set<String> pmOrderRuleUids = new HashSet<>();
        for(RelRuleAsset relRuleAsset : relRuleAssets) {
            if(relRuleAsset.getRuleUID2()!=null) pmOrderRuleUids.add(relRuleAsset.getRuleUID2());
            if(relRuleAsset.getRuleUID()!=null) pmOrderRuleUids.add(relRuleAsset.getRuleUID());
            if(relRuleAsset.getRuleUID1()!=null) pmOrderRuleUids.add(relRuleAsset.getRuleUID1());
            if(relRuleAsset.getRuleUID4()!=null) pmOrderRuleUids.add(relRuleAsset.getRuleUID4());
        }
        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findByUidIn(new ArrayList<>(pmOrderRuleUids));
        Map<String,String> uidAndName = new HashMap<>();
        for(PmOrderRule pmOrderRule : pmOrderRules) {
            uidAndName.put(pmOrderRule.getUid(),pmOrderRule.getName());
        }
        Map<String, String> siteNameCache = new HashMap<>();
        boolean hasConfigRule = false;
        for(AssetInfo assetInfo : assetInfos) {
            AssetInfoAndRelRule assetInfoAndRelRule = new AssetInfoAndRelRule();
            String siteName = resolveSiteName(assetInfo.getSiteUID(), siteNameCache);
            for(RelRuleAsset relRuleAsset : relRuleAssets) {
                if(assetInfo.getUid().equals(relRuleAsset.getAssetUID())) {
                    assetInfoAndRelRule.setInstallDate(assetInfo.getInstallDate());
                    assetInfoAndRelRule.setPurchaseDate(assetInfo.getPurchaseDate());
                    assetInfoAndRelRule.setAssetName(assetInfo.getName());
                    assetInfoAndRelRule.setAssetOwnerName(assetInfo.getAssetOwnerName());
                    assetInfoAndRelRule.setPmOwnerName(assetInfo.getPmOwnerName());
                    assetInfoAndRelRule.setLevelOneName(uidAndName.get(relRuleAsset.getRuleUID1()));
                    assetInfoAndRelRule.setLevelTwoName(uidAndName.get(relRuleAsset.getRuleUID2()));
                    assetInfoAndRelRule.setLevelThreeName(uidAndName.get(relRuleAsset.getRuleUID()));
                    assetInfoAndRelRule.setLevelFourName(uidAndName.get(relRuleAsset.getRuleUID4()));
                    assetInfoAndRelRule.setDepartNum(assetInfo.getDepartNum());
                    assetInfoAndRelRule.setAssetGroupName(FieldValueMessageController.fieldValue("assetGroup",assetInfo.getAssetGroup().toString()));
                    assetInfoAndRelRule.setClinicalDeptName(assetInfo.getClinicalDeptName());
                    assetInfoAndRelRule.setNationalStandardCode(assetInfo.getNationalStandardCode());
                    assetInfoAndRelRule.setMakerShortName(assetInfo.getMakerShortName());
                    assetInfoAndRelRule.setCreatedDate(relRuleAsset.getCreatedDate());
                    assetInfoAndRelRule.setQrCode(assetInfo.getQrCode());
                    assetInfoAndRelRule.setSerialNum(assetInfo.getSerialNum());
                    assetInfoAndRelRule.setSiteName(siteName);
                    hasConfigRule = true;
                }
            }
            if(!hasConfigRule) {
                assetInfoAndRelRule.setInstallDate(assetInfo.getInstallDate());
                assetInfoAndRelRule.setPurchaseDate(assetInfo.getPurchaseDate());
                assetInfoAndRelRule.setAssetName(assetInfo.getName());
                assetInfoAndRelRule.setAssetOwnerName(assetInfo.getAssetOwnerName());
                assetInfoAndRelRule.setPmOwnerName(assetInfo.getPmOwnerName());
                assetInfoAndRelRule.setDepartNum(assetInfo.getDepartNum());
                assetInfoAndRelRule.setAssetGroupName(FieldValueMessageController.fieldValue("assetGroup",assetInfo.getAssetGroup().toString()));
                assetInfoAndRelRule.setClinicalDeptName(assetInfo.getClinicalDeptName());
                assetInfoAndRelRule.setNationalStandardCode(assetInfo.getNationalStandardCode());
                assetInfoAndRelRule.setMakerShortName(assetInfo.getMakerShortName());
                assetInfoAndRelRule.setQrCode(assetInfo.getQrCode());
                assetInfoAndRelRule.setSerialNum(assetInfo.getSerialNum());
                assetInfoAndRelRule.setSiteName(siteName);
            }
            hasConfigRule = false;
            result.add(assetInfoAndRelRule);
        }
        return result;
    }

    private String resolveSiteName(String siteUID, Map<String, String> cache) {
        if (siteUID == null) return null;
        if (cache.containsKey(siteUID)) return cache.get(siteUID);
        String name = null;
        try {
            OrgInfo org = orgInfoRepository.getByUid(siteUID);
            if (org != null) name = org.getName();
        } catch (Exception e) {
            // ignore
        }
        cache.put(siteUID, name);
        return name;
    }
}

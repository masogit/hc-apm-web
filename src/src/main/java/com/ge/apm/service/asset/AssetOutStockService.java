package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetOutStockDto;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.wo.doctemplate.*;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

@Service
public class AssetOutStockService {

    @Autowired
    ArrivalListItemRepository arrivalListItemRepository;

    @Autowired
    ArrivalListRepository arrivalListRepository;

    @Autowired
    AssetInfoRepository assetInfoRepository;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    private MicroServiceUtil microServiceUtil;

    @Autowired
    private SystemTemplateValueRepository systemTemplateValueRepository;
    public static final String PATH_WORD_TEMPLATE = "/portal/asset/arrivedOutStock/docTemplate/out_stock_template.docx";
    public static final String PATH_WORD_IN_STOCK_TEMPLATE = "/portal/asset/arrivedOutStock/docTemplate/in_stocked_template.docx";
    public static final String PATH_WORD_OUT_STOCK_TEMPLATE = "/portal/asset/arrivedOutStock/docTemplate/out_stocked_template.docx";



    @Transactional(rollbackFor = Exception.class)
    public void updateClinicalByOutStock(AssetOutStockDto assetOutStockDto, List<ArrivalListItem> arrivalListItemList){

        for(ArrivalListItem arrivalListItem : arrivalListItemList){
            ArrivalList arrivalList = arrivalListItem.getArrivalList();
            AssetInfo assetInfo = assetInfoRepository.findByUid(arrivalListItem.getAssetUID());
            if(assetInfo == null){
                return;
            }
            assetInfo.setClinicalDeptUID(assetOutStockDto.getStockedDeptUid());
            assetInfo.setClinicalDeptId(assetOutStockDto.getStockedDeptId());
            assetInfo.setClinicalDeptName(assetOutStockDto.getStockedDeptName());
            assetInfoRepository.save(assetInfo);

          /*  arrivalListItem.setClinicalDeptUID(assetOutStockDto.getStockedDeptUid());*/
            arrivalListItem.setUnit(assetOutStockDto.getUnit());
            arrivalListItem.setQty(assetOutStockDto.getQty());
            arrivalListItem.setOutStockNo(assetOutStockDto.getOutStockNo());
            arrivalListItem.setOutStocDate(assetOutStockDto.getOutStocDate());
            arrivalListItem.setRecruiter(assetOutStockDto.getRecruiter());
            arrivalListItem.setRecruiteDeptUid(assetOutStockDto.getStockedDeptUid());
            arrivalListItemRepository.save(arrivalListItem);

           /* arrivalList.setClinicalDeptUID(assetOutStockDto.getStockedDeptUid());
            arrivalList.setClinicalDeptName(assetOutStockDto.getStockedDeptName());
            arrivalListRepository.save(arrivalList);*/
        }
    }


    public StreamedContent exportWord(AssetOutStockDto assetOutStockDto, List<ArrivalListItem> arrivalListItemList) throws IOException {
        //1、doc模板部分
        Map<String, Object> data = createFileData(assetOutStockDto, arrivalListItemList);
        byte[] templateBytes = buildReportTemplate(PATH_WORD_TEMPLATE);

        byte[] bytes = this.renderDocx(data, templateBytes);
        return new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/zip", "out_stock_template.docx");
    }

    public StreamedContent exportWord(List<ArrivalListItem> arrivalListItemList, String sockType) throws IOException {
        //1、doc模板部分
        Map<String, Object> data = createFileData(arrivalListItemList);
        String templatePath;
        String fileName;
        String type;
        if ("inStock".equals(sockType)) {
            templatePath = PATH_WORD_IN_STOCK_TEMPLATE;
            fileName = "入库单.docx";
            type=SystemTemplate.SystemTemplateEnum.IN_STOCKED.getCode();
        } else {
            templatePath = PATH_WORD_OUT_STOCK_TEMPLATE;
            fileName = "出库单.docx";
            type=SystemTemplate.SystemTemplateEnum.OUT_STOCKED.getCode();
        }
        UserAccount user = UserContextService.getCurrentUserAccount();
        String storageId= systemTemplateValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),type,"doc");
        byte[] templateBytes = microServiceUtil.downloadSingleFileByte(storageId);

        byte[] bytes = this.renderDocx(data, templateBytes, sockType);
        return new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/zip", fileName);
    }

    public byte[] buildReportTemplate(String templatePath) throws IOException {
        byte[] templateBytes = null;
        if(templatePath != null){
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templatePath));
        }
        return templateBytes;
    }

    public Map<String, Object> createFileData(AssetOutStockDto assetOutStockDto, List<ArrivalListItem> arrivalListItemList){
        Map<String, Object> data = new HashMap<>();
        data.put("depName", assetOutStockDto.getStockedDeptName());
        data.put("date", DateUtil.format(assetOutStockDto.getOutStocDate(), "yyyy-MM-dd"));
        data.put("no", assetOutStockDto.getOutStockNo());
        data.put("lyr", assetOutStockDto.getRecruiter());

        List<Map<String, Object>> dataList = new ArrayList<>();
        for(ArrivalListItem arrivalListItem : arrivalListItemList){
            Map<String, Object> mm = new HashMap<>();
            mm.put("type", "专用设备");
            mm.put("assetName", arrivalListItem.getArrivalList().getAssetName());
            mm.put("functionType", arrivalListItem.getArrivalList().getFunctionType());
            mm.put("unit", assetOutStockDto.getUnit());
            mm.put("qty", "1");
            mm.put("purchasePrice", arrivalListItem.getPurchasePrice());
            mm.put("total", arrivalListItem.getPurchasePrice());
            mm.put("vendor", arrivalListItem.getArrivalList().getVendor());
            dataList.add(mm);
        }

        data.put("list", dataList);

        return data;
    }

    public Map<String, Object> createFileData(List<ArrivalListItem> arrivalListItemList){
        ArrivalListItem temp = arrivalListItemList.get(0);
        String deptName = Optional.ofNullable(temp.getRecruiteDeptUid()).map(orgInfoRepository::getByUid).map(OrgInfo::getName).orElse("");
        Map<String, Object> data = new HashMap<>();
        data.put("depName", deptName);
        data.put("date", DateUtil.format(new Date(), "yyyy-MM-dd"));
        data.put("inStockNo", temp.getInStockNo());
        data.put("outStockNo", temp.getOutStockNo());
        data.put("userName", UserContextService.getCurrentUserAccount().getName());
        data.put("sum", arrivalListItemList.size());
        data.put("sumPrice", arrivalListItemList.stream().mapToDouble(ArrivalListItem::getPurchasePrice).sum());
        data.put("invoiceNum", temp.getArrivalList().getInvoiceNum());
        data.put("vendor", temp.getArrivalList().getVendor());

        List<Map<String, Object>> dataList = new ArrayList<>();
        for(ArrivalListItem arrivalListItem : arrivalListItemList){
            Map<String, Object> mm = new HashMap<>();
            mm.put("type", "专用设备");
            mm.put("assetName", arrivalListItem.getArrivalList().getAssetName());
            mm.put("functionType", arrivalListItem.getArrivalList().getFunctionType());
            mm.put("unit", "台");
            mm.put("qty", "1");
            mm.put("purchasePrice", arrivalListItem.getPurchasePrice());
            mm.put("total", arrivalListItem.getPurchasePrice());
            mm.put("vendor", arrivalListItem.getArrivalList().getVendor());
            dataList.add(mm);
        }
        data.put("list", dataList);
        return data;
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {
        byte[] bytes;
//        XWPFTemplate template = null;
//        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
//            InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("list", new AssetOutStockTableRenderPolicy());
//
//
//            Configure config = Configure.newBuilder()
//                    .bind("list", new AssetOutStockTableRenderPolicy())
//                    .build();
//            template = XWPFTemplate.compile(inputStream, config);
//            template.render(data);
//            template.write(out);
//            bytes = out.toByteArray();
//        }finally {
//            if (template != null) {
//                template.close();
//            }
//        }
//        return bytes;

        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("list", new AssetOutStockTableRenderPolicy());
        return WordExportServiceUtil.generate(data, templateBytes, bindings);
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes, String stockType) throws IOException {
        byte[] bytes;
//        XWPFTemplate template = null;
//        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
//            InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("list", new AssetOutStockTableRenderPolicy(stockType));
//
//            Configure config = Configure.newBuilder()
//                    .bind("list", new AssetOutStockTableRenderPolicy(stockType))
//                    .build();
//            template = XWPFTemplate.compile(inputStream, config);
//
//            template.render(data);
//            template.write(out);
//            bytes = out.toByteArray();
//        }finally {
//            if (template != null) {
//                template.close();
//            }
//        }

        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("list", new AssetOutStockTableRenderPolicy(stockType));
        return WordExportServiceUtil.generate(data, templateBytes, bindings);

    }

    public String queryInStockNo(String preStr){
        preStr = queryPre(preStr);
        String oldMax = arrivalListItemRepository.queryInStockNo(UserContextService.getCurrentUserAccount().getTenantUID(), preStr+ "%");
        return preStr + querySerialNumber(oldMax);
    }

    public String queryOutStockNo(String preStr){
        preStr = queryPre(preStr);
        String oldMax = arrivalListItemRepository.queryOutStockNo(UserContextService.getCurrentUserAccount().getTenantUID(), preStr+ "%");
        return preStr + querySerialNumber(oldMax);
    }

    public String queryPre(String preStr){
        OrgInfo hospitalOrg = orgInfoRepository.getOrgInfoUIDByUid(UserContextService.getCurrentUserAccount().getHospitalUID());
        String orgCode = hospitalOrg.getOrgCode() == null ? "000" : hospitalOrg.getOrgCode();
        return preStr + orgCode + DateUtil.format(new Date(), "yyyyMMdd");
    }

    private String querySerialNumber(String serialNumber){
        if(StrUtil.isEmpty(serialNumber)){
            return "001";
        }else{
            int index = Integer.parseInt(serialNumber);
            index++;
            return String.format("%03d",index);
        }
    }
}

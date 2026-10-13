package com.ge.apm.service.asset;

import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetOutStockDto;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class AssetOutStockService extends MicroServiceInvoker {

    protected static final Logger logger = LoggerFactory.getLogger(MicroServiceInvoker.class);

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

    @Autowired
    private AssetStockedRepository assetStockedRepository;

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

    public StreamedContent exportWord(List<AssetStocked> assetStockedList, String sockType) throws IOException {
        //1、doc模板部分
        Map<String, Object> data = createFileData(assetStockedList);
        String fileName;
        String type;
        if ("inStock".equals(sockType)) {
            fileName = "入库单.docx";
            type = SystemTemplateEnum.IN_STOCKED.getCode();
        } else {
            fileName = "出库单.docx";
            type =SystemTemplateEnum.OUT_STOCKED.getCode();
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

    public Map<String, Object> createFileData(List<AssetStocked> assetStockedList){
        AssetStocked temp = assetStockedList.get(0);
        String deptName = Optional.ofNullable(temp.getRecruiteDeptUid()).map(orgInfoRepository::getByUid).map(OrgInfo::getName).orElse("");
        Map<String, Object> data = new HashMap<>();
        data.put("depName", deptName);
        data.put("date", DateUtil.format(new Date(), "yyyy-MM-dd"));
        data.put("inStockNo", temp.getInStockNo());
        data.put("outStockNo", temp.getOutStockNo());
        data.put("userName", UserContextService.getCurrentUserAccount().getName());
        data.put("sum", assetStockedList.size());
        data.put("sumPrice", assetStockedList.stream().map(AssetStocked::getAssetInfo).filter(t -> ObjectUtil.isNotEmpty(t.getPurchasePrice())).mapToDouble(AssetInfo::getPurchasePrice).sum());
        data.put("invoiceNum", temp.getArrivalList().getInvoiceNum());
        data.put("vendor", temp.getArrivalList().getVendor());

        List<Map<String, Object>> dataList = new ArrayList<>();
        for(AssetStocked assetStocked : assetStockedList){
            Map<String, Object> mm = new HashMap<>();
            mm.put("type", "专用设备");
            mm.put("assetName", assetStocked.getArrivalList().getAssetName());
            mm.put("functionType", assetStocked.getArrivalList().getFunctionType());
            mm.put("unit", "台");
            mm.put("qty", "1");
            mm.put("purchasePrice", assetStocked.getAssetInfo().getPurchasePrice());
            mm.put("total", assetStocked.getAssetInfo().getPurchasePrice());
            mm.put("vendor", assetStocked.getArrivalList().getVendor());
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

    public String checkAssetInfo(List<String> assetUids) {
        Map<String, Object> map = MapUtil.newHashMap();
        map.put("assetUids", assetUids);
        String token = UserContextService.getAccessToken();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_CHECK_ORDER_CONDITION);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            return String.valueOf(responseMap.get("data"));
        } else {
            logger.error("AssetOutStockService check asset error assetUids:{},result:{}", assetUids , responseMap.get("data"));
        }
        return "";
    }

    public String queryInStockNo(String preStr){
        preStr = queryPre(preStr);
        String oldMax = assetStockedRepository.queryInStockNo(UserContextService.getCurrentUserAccount().getTenantUID(), preStr+ "%");
        return preStr + querySerialNumber(oldMax);
    }

    public String queryOutStockNo(String preStr){
        preStr = queryPre(preStr);
        String oldMax = assetStockedRepository.queryOutStockNo(UserContextService.getCurrentUserAccount().getTenantUID(), preStr+ "%");
        return preStr + querySerialNumber(oldMax);
    }

    public String queryReturnedStockNo(String preStr){
        preStr = queryPre(preStr);
        String oldMax = assetStockedRepository.queryReturnedStockNo(UserContextService.getCurrentUserAccount().getTenantUID(), preStr+ "%");
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

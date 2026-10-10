package com.ge.apm.service.simpleparts;

import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.simpleparts.*;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsInventoryMapper;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsTransactionMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/8
 * @descript 收据业务
 * @package com.ge.apm.service.spareParts
 */
@Service
public class SimplePartsRequestService {
    private static final Logger log = LoggerFactory.getLogger(SimplePartsRequestService.class);

    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;

    @Autowired
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;

    @Autowired
    private SimplePartsWarehouseService partsWarehouseService;

    @Autowired
    private SimplePartsTransactionMapper partsTransactionMapper;

    @Autowired
    private SimplePartsInventoryMapper partsInventoryMapper;

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    @Autowired
    private SimplePartsRequestLineItemService partsRequestLineItemService;

    @Autowired
    private SimplePartsCodeGenerationService partsCodeGenerationService;

    @Autowired
    private SimplePartsItemRepository partsItemRepository;

    public static final String REPORT_OUT_BOUND_ORDER = "/portal/parts/template/outBoundOrderReportTemplate.docx";
    public static final String OUT_BOUND_ORDER = "出库单";

    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * @return void
     * @throws
     * @params [partsRequest]
     * @author csh
     * @createTime 2020/6/8 15:56
     * @description 保存数据
     */
    @Transactional
    public void save(SimplePartsRequest partsRequest) {
        partsRequestRepository.save(partsRequest);
    }

    /**
     * @return void
     * @params [partsRequest]
     * @author csh
     * @createTime 2020/6/12 10:39
     * @description 批量保存数据
     */
    @Transactional
    public void save(List<SimplePartsRequest> partsRequest) {
        partsRequestRepository.save(partsRequest);
    }

    /**
     * @param type   申购表单
     * @param status 状态未入库
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [type, status]
     * @author csh0
     * @createTime 2020/6/15 14:51
     * @description 申购中的备件
     */
    @Transactional(readOnly = true)
    public List<SimplePartsRequest> findByTypeAndStatus(Integer type, Integer status) {
        return partsRequestRepository.findByTypeAndStatus(type, status);
    }

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [requestCode]
     * @author csh
     * @createTime 2020/6/17 17:49
     * @description 根据订单编号查询到未审批的订单
     */
    @Transactional(readOnly = true)
    public List<SimplePartsRequest> findByRequestCode(String requestCode) {
        return partsRequestRepository.findByRequestCode(requestCode);
    }

    /**
     * get remaining count and avg price for the parts item based on the current inveontry
     * @param partsItemId
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<SimplePartsItemValue> getInventoryNumAndAvgPriceByPartsItem(String partsItemId, String partsWarehouseId) {

        List<String> warehouseIds = Lists.newArrayList();
        List<SimplePartsWarehouse> partsWarehouseList = partsWarehouseService.findWarehouseListInRequest();

        if (partsWarehouseList.size() == 0) {
            log.info("查询备件交易记录和备件库存时,无可用仓库");
            return Optional.of(new SimplePartsItemValue(0,new BigDecimal("0").setScale(2,   BigDecimal.ROUND_HALF_UP)));
        }
        if(StringUtils.isNotEmpty(partsWarehouseId)){
            warehouseIds.add(partsWarehouseId);
        } else {
            warehouseIds.addAll(partsWarehouseList.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        BigDecimal avgPrice = partsTransactionMapper.getAvgPriceByPartsItem(partsItemId, warehouseIds);
        Integer inventoryNum = partsInventoryMapper.getInventoryNumByPartsItem(partsItemId, warehouseIds);
        SimplePartsItemValue itemValue = new SimplePartsItemValue(inventoryNum == null ? 0:inventoryNum , avgPrice== null ? new BigDecimal("0").setScale(2,   BigDecimal.ROUND_HALF_UP):avgPrice.setScale(2,   BigDecimal.ROUND_HALF_UP));
        return Optional.of(itemValue);
    }

    /**
     * stock in single parts
     * @param partsRequestLineItem
     * @param partsRequest
     */
    @Transactional
    public void stockInParts(SimplePartsRequestLineItem partsRequestLineItem, SimplePartsRequest partsRequest) {

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
        partsTransaction.setSourcSupplier(partsRequestLineItem.getPartsSupplier());
        partsTransaction.setRequestCode(partsRequestLineItem.getRequestCode());
        partsTransaction.setPartsRequestId(partsRequestLineItem.getPartsRequestId());
        partsTransaction.setPartsRequestLineItemId(partsRequestLineItem.getId());
        partsTransaction.setCreatedBy(partsRequestLineItem.getCreatedBy());
        partsTransaction.setCreatedDate(new Date());
        partsTransaction.setTransactionQty(partsRequestLineItem.getRequestQty());
        partsTransaction.setPartsItemId(partsRequestLineItem.getPartsItemId());
        partsTransaction.setName(partsRequestLineItem.getName());
        partsTransaction.setUnitPrice(partsRequestLineItem.getUnitPrice());
        partsTransaction.setTotalPrice(partsRequestLineItem.getTotalPrice());
        partsTransaction.setPartsWarehouseId(partsRequestLineItem.getPartsWarehouseId());
        partsTransaction.setPartsLocatorId(partsRequestLineItem.getPartsLocatorId());

        partsTransaction.setRequestType(partsRequest.getType());
        partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);

        // 增加相对应的权限，例如租户、院区、等
        SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);

        partsTransactionRepository.save(partsTransaction);
    }

    /**
     * stock in the whole request
     * @param partsRequest
     */
    @Transactional
    public synchronized void stockInRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList) {

        Date createDate = new Date();
        // 增加相对应的权限，例如租户、院区、等
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        partsRequestLineItemList.stream().forEach(item -> {
            SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
            partsTransaction.setSourcSupplier(item.getPartsSupplier());
            partsTransaction.setRequestCode(item.getRequestCode());
            partsTransaction.setPartsRequestId(partsRequest.getId());
            partsTransaction.setPartsRequestLineItemId(item.getId());
            partsTransaction.setCreatedBy(currentUserAccount.getLoginName());
            partsTransaction.setCreatedDate(createDate);
            partsTransaction.setTransactionQty(item.getRequestQty() - item.getTransactionQty());
            partsTransaction.setPartsItemId(item.getPartsItemId());
            partsTransaction.setName(item.getName());
            partsTransaction.setUnitPrice(item.getUnitPrice());
            int qty = item.getRequestQty() - item.getTransactionQty();
            partsTransaction.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(qty)));
            partsTransaction.setPartsWarehouseId(item.getPartsWarehouseId());
            partsTransaction.setPartsLocatorId(item.getPartsLocatorId());

            partsTransaction.setRequestType(partsRequest.getType());
            partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);

            partsTransaction.setComments(item.getComments());//备注
            partsTransaction.setStorageType(item.getStorageType());//入库类型
            partsTransaction.setAddressInfo(item.getAddressInfo());//地址信息

            SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);

            partsTransactionRepository.save(partsTransaction);

            item.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
            partsRequestLineItemRepository.save(item);

            // 将数据拷贝到总库存中
            SimplePartsInventory partsInventory;

            Optional<SimplePartsInventory> partsInventoryOptional = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());
            if (partsInventoryOptional.isPresent()) {
                partsInventory = partsInventoryOptional.get();
                // 更新
                int sumNum = partsInventory.getOnhandQty() + (item.getRequestQty() - item.getTransactionQty());
                partsInventory.setOnhandQty(sumNum);
            } else {
                // 新增
                partsInventory = new SimplePartsInventory();
                partsInventory.setPartsItemId(item.getPartsItemId());
                partsInventory.setPartsLocatorId(item.getPartsLocatorId());
                partsInventory.setPartsWarehouseId(item.getPartsWarehouseId());
                partsInventory.setName(item.getName());
                partsInventory.setOnhandQty(item.getRequestQty() - item.getTransactionQty());

                SimplePartsInventoryService.addCurrentOrgInfo(partsInventory);

            }

            partsInventoryRepository.save(partsInventory);
        });

        partsRequest.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
        partsRequestRepository.save(partsRequest);
        if(partsRequest.getMaintenanceWorkOrderNumber() != null){
            partsRequest.setId(null);
            partsRequest.setCreatedDate(new Date());
            List<SimplePartsRequest> rc = this.findByRequestCode(partsRequest.getRequestCode());
            if (rc.size() > 0) {
                String requestCode = partsCodeGenerationService.getNextRequestCode(SimplePartsConstant.PARTS_REQUEST_FORM);
                partsRequest.setRequestCode(requestCode);
            }
            partsRequestLineItemList.stream().forEach(item -> {
                item.setId(null);
            });
            this.saveNewPartsRequest(partsRequest,partsRequestLineItemList,false);
        }
    }

    @Transactional(rollbackFor = SimplePartsInventoryNotEnoughException.class)
    public void stockoutRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList)
            throws SimplePartsInventoryNotEnoughException {
        // 增加相对应的权限，例如租户、院区、等
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        for (SimplePartsRequestLineItem item: partsRequestLineItemList) {
            // 更新 总的库存
            Optional<SimplePartsInventory> partsInventoryOptional = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());

            if (!partsInventoryOptional.isPresent()) {
                throw new SimplePartsInventoryNotEnoughException("no inventory");
            }

            SimplePartsInventory partsInventory = partsInventoryOptional.get();
            int newInventoryNum = partsInventory.getOnhandQty() - (item.getRequestQty() - item.getTransactionQty());

            if (newInventoryNum < 0) {
                throw new SimplePartsInventoryNotEnoughException("no inventory");
            }

            if (newInventoryNum == 0) {
                partsInventoryRepository.deleteByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());
            } else {
                partsInventory.setOnhandQty(partsInventory.getOnhandQty() -  (item.getRequestQty() - item.getTransactionQty()));
                partsInventoryRepository.save(partsInventory);
            }

            SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
            partsTransaction.setSourcSupplier(item.getPartsSupplier());
            partsTransaction.setRequestCode(item.getRequestCode());
            partsTransaction.setPartsRequestId(partsRequest.getId());
            partsTransaction.setPartsRequestLineItemId(item.getId());
            partsTransaction.setCreatedBy(currentUserAccount.getLoginName());
            partsTransaction.setCreatedDate(new Date());
            partsTransaction.setTransactionQty(item.getRequestQty() - item.getTransactionQty());
            partsTransaction.setPartsItemId(item.getPartsItemId());
            partsTransaction.setName(item.getName());
            partsTransaction.setUnitPrice(item.getUnitPrice());
            int qty = item.getRequestQty() - item.getTransactionQty();
            partsTransaction.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(qty)));
            partsTransaction.setPartsWarehouseId(item.getPartsWarehouseId());
            partsTransaction.setPartsLocatorId(item.getPartsLocatorId());
            partsTransaction.setRequestType(partsRequest.getType());
            partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK);

            partsTransaction.setTransportNo(item.getTransportNo()); //运单号
            partsTransaction.setTransportCompany(item.getTransportCompany());   //运输公司
            partsTransaction.setAddressInfo(item.getAddressInfo());//地址信息
            partsTransaction.setPartsDescription(item.getPartsDescription());//备件描述
            SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);
            partsTransactionRepository.save(partsTransaction);

            item.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
            partsRequestLineItemService.save(item);
        }

        partsRequest.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
        partsRequestRepository.save(partsRequest);
    }

    @Transactional
    public void saveNewPartsRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList, boolean isApproval) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        // 申领单类型
        partsRequest.setType(SimplePartsConstant.PARTS_REQUEST_FORM);
        // 未出库
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
        // 未审批
        partsRequest.setProposerId(currentUserAccount.getId());
        if(isApproval){
            partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
        }else {
            partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
        }

        addCurrentOrgInfo(partsRequest);

        partsRequestRepository.save(partsRequest);

        // 备件明细保存
        partsRequestLineItemList.stream().forEach(item -> {
            item.setPartsRequestId(partsRequest.getId());
            item.setRequestCode(partsRequest.getRequestCode());
            item.setCreatedBy(currentUserAccount.getLoginName());

            SimplePartsRequestLineItemService.addCurrentOrgInfo(item);

            // 未出库
            item.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
        });

        partsRequestLineItemService.save(partsRequestLineItemList);

    }

    @Transactional
    public void savePartsRequestWithProvidedData(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        //未出入库
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);

        // 未审批
        partsRequest.setCreatedBy(currentUserAccount.getLoginName());
        partsRequest.setCreatedDate(new Date());

        partsRequest.setProposerId(currentUserAccount.getId());
        partsRequest.setProposerDate(new Date());
        partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);

        addCurrentOrgInfo(partsRequest);

        partsRequestRepository.save(partsRequest);

        // 单个备件保存
        partsRequestLineItemList.stream().forEach(item -> {
            item.setPartsRequestId(partsRequest.getId());
            item.setRequestCode(partsRequest.getRequestCode());
            item.setProposerDate(new Date());

            item.setCreatedBy(currentUserAccount.getLoginName());
            item.setCreatedDate(new Date());

            SimplePartsRequestLineItemService.addCurrentOrgInfo(item);
            // 未出入库
            item.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);

        });

        partsRequestLineItemRepository.save(partsRequestLineItemList);
    }

    public static void addCurrentOrgInfo(SimplePartsRequest partsRequest) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        partsRequest.setTenantUID(currentUserAccount.getTenantUID());
        partsRequest.setInstitutionUID(currentUserAccount.getInstitutionUID());
        partsRequest.setHospitalUID(currentUserAccount.getHospitalUID());
        partsRequest.setSiteUID(currentUserAccount.getSiteUID());
        partsRequest.setClinicalDeptUID(currentUserAccount.getClinicalDeptUID());
    }

    public static void addCurrentOrgInfo(SimplePartsRequest partsRequest, UserAccount userAccountFromWorkOrder) {

        partsRequest.setTenantUID(userAccountFromWorkOrder.getTenantUID());
        partsRequest.setInstitutionUID(userAccountFromWorkOrder.getInstitutionUID());
        partsRequest.setHospitalUID(userAccountFromWorkOrder.getHospitalUID());
        partsRequest.setSiteUID(userAccountFromWorkOrder.getSiteUID());
        partsRequest.setClinicalDeptUID(userAccountFromWorkOrder.getClinicalDeptUID());
    }

    /**
     * 组装需导出的数据
     * @param simplePartsRequest
     * @param simplePartsTransactions
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> createReportData(SimplePartsRequest simplePartsRequest, List<SimplePartsTransaction> simplePartsTransactions){
        Map<String, Object> reportData = new HashedMap();
        reportData.put("request_code",simplePartsRequest.getRequestCode());//申领单编号
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;
        if(simplePartsRequest.getCreatedDate() != null){
            date = simpleDateFormat.format(simplePartsRequest.getCreatedDate());
        }
        reportData.put("created_date", date);//需求时间
        //出库单备件明细
        List<Map<String, Object>> partsLineItemList = new ArrayList<>();
        for (SimplePartsTransaction simplePartsTransaction : simplePartsTransactions) {
            Map<String, Object> partsLineItemMap = new HashMap<>();
            String itemCode = null;
            if(simplePartsTransaction.getPartsItemId() != null){
                SimplePartsItem simplePartsItem = partsItemRepository.findById(simplePartsTransaction.getPartsItemId());
                if(simplePartsItem != null){
                    itemCode = simplePartsItem.getItemCode();
                }
            }
            partsLineItemMap.put("item_code", itemCode);//料号，用备件号
            partsLineItemMap.put("transaction_qty", simplePartsTransaction.getTransactionQty());//出库数量
            SimplePartsWarehouse simplePartsWarehouse = partsWarehouseService.findWarehouseNameById(simplePartsTransaction.getPartsWarehouseId());
            String warehouseName = null;
            if(simplePartsWarehouse != null){
                warehouseName = simplePartsWarehouse.getName();
            }
            partsLineItemMap.put("warehouse_name",warehouseName);//仓库
            String warehouseLocatorName = null;
            if(simplePartsTransaction.getPartsLocatorId() != null){
                SimplePartsWareHouseLocator simplePartsWarehouseLocator = partsWarehouseService.findWarehouseLocatorNameById(simplePartsTransaction.getPartsLocatorId());
                if(simplePartsWarehouseLocator != null){
                    warehouseLocatorName = simplePartsWarehouseLocator.getName();
                }
            }
            partsLineItemMap.put("warehouse_locator_name",warehouseLocatorName);//库位
            partsLineItemMap.put("comments", simplePartsTransaction.getComments());//备注
            String addressInfo = simplePartsTransaction.getAddressInfo();
            String address = null;
            String contactPerson = null;
            String contactPhone = null;
            if(addressInfo != null){
                String[] addressInfos = addressInfo.split("，");
                for(String addressInfoTmp : addressInfos){
                    if(addressInfoTmp.startsWith("收货地址：")){
                        String[] addressArr = addressInfoTmp.split("：");
                        address = addressArr.length > 1 ? addressArr[1] : address;
                    }
                    if(addressInfoTmp.startsWith("收货人：")){
                        String[] contactPersonArr = addressInfoTmp.split("：");
                        contactPerson = contactPersonArr.length > 1 ? contactPersonArr[1] : contactPerson;
                    }
                    if(addressInfoTmp.startsWith("联系方式：")){
                        String[] contactPhoneArr = addressInfoTmp.split("：");
                        contactPhone = contactPhoneArr.length > 1 ? contactPhoneArr[1] : contactPhone;
                    }
                }
            }
            reportData.put("address", address);//收货地址
            reportData.put("contact_person", contactPerson);//收货人
            reportData.put("contact_phone", contactPhone);//联系电话
            UserAccount userAccount = userAccountRepository.getByLoginName(simplePartsTransaction.getCreatedBy());
            String userName = null;
            if(userAccount != null){
                userName = userAccount.getName();
            }
            reportData.put("out_stock_person", userName);//出库人
            String outStockDate = null;
            if(simplePartsTransaction.getCreatedDate() != null){
                outStockDate = simpleDateFormat.format(simplePartsTransaction.getCreatedDate());
            }
            reportData.put("out_stock_date", outStockDate);//出库时间
            partsLineItemList.add(partsLineItemMap);
        }
        reportData.put("parts_request_line_item_batch",partsLineItemList);
        return reportData;
    }

    /**
     * 构建导出模板
     * @return
     * @throws IOException
     */
    public byte[] buildReportTemplate() throws IOException {

//        return IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(REPORT_OUT_BOUND_ORDER));
         FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        return fileDownloadService.getTemplateFileByte(SystemTemplateEnum.PARTS_OUT_BOUND_ORDER.getCode(), SystemTemplateEnum.PARTS_OUT_BOUND_ORDER.getFileType());

    }

    //导出一条记录
    public StreamedContent exportDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(data, templateBytes))), "application/msword", this.buildFileName(data, false));

        return streamedContent;
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {
//
//        byte[] bytes;
//        XWPFTemplate template = null;
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            InputStream inputStream = new ByteArrayInputStream(templateBytes);
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("parts_request_line_item_batch", new SimplePartsRequestLineItemTableRenderPolicy());
//
//            Configure config = Configure.newBuilder()
//                    .bind("parts_request_line_item_batch", new SimplePartsRequestLineItemTableRenderPolicy())
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
        bindings.put("parts_request_line_item_batch", new SimplePartsRequestLineItemTableRenderPolicy());
        return WordExportServiceUtil.generate(data, templateBytes, bindings);
    }

    private String buildFileName(Map<String, Object> data, boolean isEncoding){

        String fileName = "-"+ data.get("request_code").toString() ;
        if(isEncoding){
            fileName = this.encoding(OUT_BOUND_ORDER) + fileName +".docx";
        }else {
            fileName = OUT_BOUND_ORDER + fileName +".docx";
        }
        return fileName;
    }

    private String encoding(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //return original name if the encode is failed.
            return fileName;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePartsRequest(String id){
        partsRequestLineItemRepository.deleteByPartsRequestId(id);
        partsRequestRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public int getTenantTotalStockNum(String partsItemId){
        List<SimplePartsInventory> partsInventoryList = partsInventoryRepository.findByPartsItemId(partsItemId);

        int totalQty = 0;
        for (SimplePartsInventory partsInventory : partsInventoryList) {
            totalQty += partsInventory.getOnhandQty();
        }

        return totalQty;
    }
}

package com.ge.apm.view.wo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.EHomeTransactionWorkOrderRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.mvstransaction.MvsTransactionWorkService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 *
 * @author lyj
 */
@ManagedBean
@ViewScoped
public class MvsTransactionWorkController extends GenericCRUDUUIDController<EHomeTransactionWorkOrder> implements Serializable, ServerEventInterface {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MvsTransactionWorkController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    OrgInfoRepository orgInfoRepository;

    private AssetFaultTypeService assetFaultTypeService;

    private String open = null;
    private String close = null;
    private String accept = null;
    private String ackSignature;


    private EHomeTransactionWorkOrderRepository dao;
    private MvsTransactionWorkService mvsTransactionWorkService;
    private FileDownloadService fileDownloadService;
    private List<I18nMessage> i18nMessageListWorkOrderType;
    private List<V2_BlobObject> attachListEhome;
    private ConfigItemValueRepository configItemValueRepository;
    private UserAccount user;
    ConfigItemValue value;

    @Override
    protected GenericRepositoryUUID<EHomeTransactionWorkOrder> getDAO() {
        return dao;
    }
    @Override
    protected void init() {
        assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(EHomeTransactionWorkOrderRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        mvsTransactionWorkService = WebUtil.getBean(MvsTransactionWorkService.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        value = configItemValueRepository.findFirstByKeyAndSiteUid("mvsLevelWorkOrder", user.getSiteUID());
        if(value == null){
            value = configItemValueRepository.findFirstByKeyAndSiteUid("mvsLevelWorkOrder", user.getHospitalUID());
            if(value == null){
                value = configItemValueRepository.findFirstByKeyAndSiteUid("mvsLevelWorkOrder", user.getInstitutionUID());
                if(value == null){
                    value = configItemValueRepository.findFirstByKeyAndSiteUid("mvsLevelWorkOrder", user.getTenantUID());
                }
            }
        }
        if(value != null&& StringUtils.isNotBlank(value.getValue())){
            i18nMessageListWorkOrderType = parseJsonToDtoList(value.getValue());
        }
    }

    public static List<I18nMessage> parseJsonToDtoList(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             List<Map<String, String>> rawList = objectMapper.readValue(
                    jsonStr,
                    new TypeReference<List<Map<String, String>>>() {}
             );
             return rawList.stream().map(entry -> {
                I18nMessage message = new I18nMessage();
                message.setMsgKey(entry.get("value"));   // JSON的"value" → msgKey
                message.setValueZh(entry.get("key"));     // JSON的"key" → valueZh
                 message.setOrderBy(0);                    // 示例排序值
                return message;
            }).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
             throw new IllegalArgumentException("JSON 解析失败: " + e.getOriginalMessage(), e);
        } catch (Exception e) {
             throw new RuntimeException("JSON 转换发生未知错误", e);
        }
    }

    @Override
    protected Page<EHomeTransactionWorkOrder> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if(value == null || StringUtils.isBlank(value.getValue())){
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        OrgInfo orgInfo = orgInfoRepository.findByUid(UserContextService.getCurrentUserAccount().getHospitalUID());
        String[] strs = Optional.ofNullable(orgInfo)
                .filter(info -> StrUtil.isNotEmpty(info.getHospitalUcmid()))
                .map(info -> new String[]{info.getHospitalUcmid()})
                .orElse(new String[0]);
        Map<String, Object> params = new HashMap<>();
        if(strs!=null && strs.length>0){
            params.put("umids",strs);
        }else{
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        String[] msgKeyArray = i18nMessageListWorkOrderType.stream()
                .map(I18nMessage::getMsgKey)   // 提取 msgKey
                .filter(Objects::nonNull)      // 过滤 null 值（可选）
                .toArray(String[]::new);
        params.put("msgKeyArray",msgKeyArray);
        processSearchFilters(params,this.searchFilters);
        List<EHomeTransactionWorkOrder> eHomeTransactionWorkOrders = mvsTransactionWorkService.loadData(params);
        if(CollectionUtils.isEmpty(eHomeTransactionWorkOrders)){
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        List<EHomeTransactionWorkOrder> processedOrders = eHomeTransactionWorkOrders.parallelStream()
                .map(order -> {
                    final String workOrderId = order.getId();
                    parseAndSet(order::getSrContext, MaintenanceOrderDTO.class, "SR_CONTEXT",
                            order::setMaintenanceOrderDTO, workOrderId);
                    parseListAndSet(order::getAttachments, AttachmentDTO.class, "ATTACHMENTS",
                            order::setAttachmentDTOS, workOrderId);
                    parseListAndSet(order::getHours, HoursContextDTO.class, "HOURS_CONTEXT",
                            order::setHoursContextData, workOrderId);
                    parseListAndSet(order::getParts, PartContextDTO.class, "PARTS_CONTEXT",
                            order::setPartsContextData, workOrderId);
                    parseAndSet(order::getWoContext, WorkOrderDTO.class, "WO_CONTEXT",
                            order::setWorkOrderDTO, workOrderId);
                    return order;
                })
                .collect(Collectors.toList());
        return new PageImpl<EHomeTransactionWorkOrder>(this.pageNum(processedOrders, pageRequest), pageRequest, processedOrders.size());
    }

    private void processSearchFilters(Map<String, Object> params, List<SearchFilter> filters) {
        for (SearchFilter filter : filters) {
             switch (filter.fieldName) {
                 case "accept": //
                     String[] dates = filter.value.toString().split("~");
                     if (dates.length > 0 && !dates[0].isEmpty()) {
                         params.put("startDate", dates[0].trim()); // 开始日期
                     }
                     if (dates.length > 1 && !dates[1].isEmpty()) {
                         params.put("endDate", dates[1].trim()); // 结束日期
                     }
                     break;
                 case "open": //
                     String[] open = filter.value.toString().split("~");
                     if (open.length > 0 && !open[0].isEmpty()) {
                         params.put("startDateOpen", open[0].trim()); // 开始日期
                     }
                     if (open.length > 1 && !open[1].isEmpty()) {
                         params.put("endDateOpen", open[1].trim()); // 结束日期
                     }
                     break;
                 case "close": //
                     String[] close = filter.value.toString().split("~");
                     if (close.length > 0 && !close[0].isEmpty()) {
                         params.put("startDateClose", close[0].trim()); // 开始日期
                     }
                     if (close.length > 1 && !close[1].isEmpty()) {
                         params.put("endDateClose", close[1].trim()); // 结束日期
                     }
                     break;
                case "typeKey":
                    params.put("typeKey", filter.value);  // 对类型字段进行业务语义映射
                    break;
                case "workOrderDTO.ownerName":
                    params.put("ownerName", filter.value);
                    break;
                case "maintenanceOrderDTO.faultDesc":
                    params.put("faultDesc", filter.value);
                    break;
                case "maintenanceOrderDTO.customerName":
                    params.put("customerName", filter.value);
                    break;
                case "workOrderDTO.workOrderNumber":
                    params.put("woNum", filter.value);
                    break;
                case "srNum":
                    params.put("srNum", filter.value);
                    break;
                case "status":
                    params.put("status", filter.value);
                    break;
                default:
                    break;
            }
        }
    }

    private <T> void parseAndSet(Supplier<String> jsonSupplier,
                                 Class<T> targetType,
                                 String fieldName,
                                 Consumer<T> setter,
                                 String workOrderId) {
        try {
            String json = jsonSupplier.get();
            if (StringUtils.isBlank(json)) {
                LOGGER.warn("[EmptyField] {} | WorkOrder: {}", fieldName, workOrderId);
                return;
            }
            T dto = objectMapper.readValue(json, targetType);
            setter.accept(dto);
        } catch (Exception e) {
            LOGGER.error("[ParseError] {} | WorkOrder: {} | Error: {}",
                    fieldName, workOrderId, e.getMessage());
        }
    }

    private <T> void parseListAndSet(Supplier<String> jsonSupplier,
                                     Class<T> elementType,
                                     String fieldName,
                                     Consumer<List<T>> setter,
                                     String workOrderId) {
        try {
            String json = jsonSupplier.get();
            if (StringUtils.isBlank(json)) {
                LOGGER.warn("[EmptyList] {} | WorkOrder: {}", fieldName, workOrderId);
                return;
            }
             JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType);
            List<T> list = objectMapper.readValue(json, type);
            setter.accept(list);
        } catch (Exception e) {
            LOGGER.error("[ListParseError] {} | WorkOrder: {} | Snippet: {}... | Error: {}",
                    fieldName, workOrderId,
                    StringUtils.left(jsonSupplier.get(), 100),
                    e.getMessage());
        }
    }
    private List<EHomeTransactionWorkOrder> pageNum(List<EHomeTransactionWorkOrder> eHomeTransactionWorkOrders, PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = eHomeTransactionWorkOrders.size();
        if(size > numEnd){
            eHomeTransactionWorkOrders = eHomeTransactionWorkOrders.subList(numStart ,numEnd);
        }else {
            eHomeTransactionWorkOrders = eHomeTransactionWorkOrders.subList(numStart ,size);
        }
        return eHomeTransactionWorkOrders;
    }

    public String getFaultTypeName(Integer id) {
        return assetFaultTypeService.getFaultName(id);
    }
    public void onSelectWorkOrder() {
        if (selected == null) return;
        attachListEhome = new ArrayList<>();
        ackSignature = new String();
        if(!CollectionUtils.isEmpty(selected.getAttachmentDTOS())){

            List<AttachmentDTO> attachmentDTOS = this.selected.getAttachmentDTOS();
            // 初始化结果集合
            Set<String> objectIds = new HashSet<>();
            for (AttachmentDTO dto : attachmentDTOS) {
                // 获取附件类型字符串（便于调试时观察具体值）
                String attachmentType = dto.getType() != null
                        ? dto.getType()
                        : null;

                // 排除类型为 "customerSignature" 的元素
                if (!"customerSignature".equals(attachmentType)) {
                    // 提取业务对象ID
                    String businessObjectId = dto.getBusinessObjectId();
                    // 将非空ID加入集合
                    if (businessObjectId != null) {
                        objectIds.add(businessObjectId);
                    }
                }
            }
            // 遍历附件列表
            for (AttachmentDTO dto : attachmentDTOS) {
                // 检查类型是否为 customerSignature
                if ("customerSignature".equals(dto.getType())) {
                    // 找到第一个匹配项时，提取ID并立即终止循环
                    ackSignature = dto.getBusinessObjectId();
                    break;
                }
            }
            List<FileInfoResult> fileInfoResults = fileDownloadService.getFileInfoForEhome(objectIds);
            List<String> imageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            if (fileInfoResults == null) {
                fileInfoResults = new ArrayList<>();
            }
            if (!CollectionUtils.isEmpty(fileInfoResults)) {
                attachListEhome = fileInfoResults.stream()
                        .filter(result -> result.getObjectId() != null)
                        .map(result -> {
                            V2_BlobObject blobObject = new V2_BlobObject();
                            blobObject.setObjectStorageId(result.getObjectId());
                            blobObject.setObjectName(result.getObjectName());
                            if (isImageFile(result.getObjectName())) {
                                blobObject.setBoType("IMG");  // 设置图片类型
                            }                            return blobObject;
                        })
                        .collect(Collectors.toList());
            }
        }
    }

    private static boolean isImageFile(String fileName) {
        if (fileName == null) return false;
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") ||
                lowerName.endsWith(".gif") ||
                lowerName.endsWith(".bmp");  // 可继续扩展其他图片格式
    }
    public List<V2_BlobObject> getAttachListEhome() {
        return attachListEhome;
    }

    public void setAttachListEhome(List<V2_BlobObject> attachListEhome) {
        this.attachListEhome = attachListEhome;
    }

    public List<I18nMessage> getI18nMessageListWorkOrderType() {
        return i18nMessageListWorkOrderType;
    }

    public void setI18nMessageListWorkOrderType(List<I18nMessage> i18nMessageListWorkOrderType) {
        this.i18nMessageListWorkOrderType = i18nMessageListWorkOrderType;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAckSignature() {
        return ackSignature;
    }

    public void setAckSignature(String ackSignature) {
        this.ackSignature = ackSignature;
    }
}


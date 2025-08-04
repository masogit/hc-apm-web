package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.ArrivalSupplierReportService;
import com.ge.apm.service.asset.AssetInstallationReportService;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.Map2XmlConverter;
import com.ge.apm.view.wo.WorkOrderReportExportController;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class AssetArrivedListController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;
    private TenantInfoRepository tenantInfoDao = null;
    private ArrivalListItemRepository itemDao;
    private AssetInstallationReportService installationReportService;
    private ArrivalSupplierReportService arrivalSupplierReportService;
    private AssetInstallationService aiService;
    private UaaService uaaService;
    private StreamedContent exportPdf;
    private StreamedContent exportDoc;
    private StreamedContent exportZip;
    private Date varArrivalDateTo = null;
    private String arrivalFormatTime = null;
    private String supplierUserBind;
    private SupplierService supplierService;
    private Integer statusFlag;
    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<ArrivalList> currentPageAssets;


    private SystemTemplateService systemTemplateService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ArrivalListRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        arrivalSupplierReportService = WebUtil.getBean(ArrivalSupplierReportService.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        installationReportService = WebUtil.getBean(AssetInstallationReportService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
        supplierService = WebUtil.getBean(SupplierService.class);
        this.selectedMap = new HashMap();
        String encodeStr = WebUtil.getRequestParameter("str");
        String statusFlagStr = null;
        if (encodeStr != null) {
            supplierUserBind = (String) UrlEncryptController.getValueFromMap(encodeStr, "supplierUserBind");
        }
    }

    @Override
    protected ArrivalListRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalList> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));
        if (!this.hasFilterOnField("siteUID")) {
            Set<String> orgUidSet = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid())
                    .stream().map(item -> item.getSiteUID()).collect(Collectors.toSet());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        }

        if (activeSupplier()) {
            if (supplierUserBind != null) {
                removeFilterOnField("supplierUserId");
                searchFilters.add(new SearchFilter("supplierUserId", supplierUserBind.equals("1") ? SearchFilter.Operator.IsNull : SearchFilter.Operator.IsNotNull, null));
            }
            if (statusFlag != null && statusFlag != 4) {
                if (statusFlag < 3) {
                    List<String> idList = null;
                    List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
                    if (statusFlag == 1) {
                        idList = dao.getArrivalListNotFinishedIds(orgUidList);
                    } else if (statusFlag == 2) {
                        idList = dao.getArrivalListPartiallyFinishedIds(orgUidList);
                    }
                    removeFilterOnField("id");
                    searchFilters.add(new SearchFilter("id", SearchFilter.Operator.IN, idList));
                }
                removeFilterOnField("status");
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusFlag == 1 ? Arrays.asList(1, 2) : Arrays.asList(statusFlag)));
            } else {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
            }
        }

        Page<ArrivalList> result = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = result.getContent();
        setCanDeleted(result);
        return result;
    }

    /**
     * 1、页面未选择下拉框status值时默认将column里status的等于2的进行判断，最终返回(1或2)
     * 2、当选择下拉框status值为1时则将column里status赋值为1。
     *
     * @param id
     * @param status
     * @return
     */
    public String getArrivalListStatus(String id, String status) {
        if (statusFlag != null) {
            if (statusFlag == 1) {
                status = "1";
            }
        } else {
            if (status.equals("2")) {
                List<ArrivalListItem> arrivalListItem = aiService.getListItem(id);
                int installNum = arrivalListItem.stream().filter(item -> ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getInstallStatus())
                        || ArrivalListItem.INSTALL_STATUS_INSTORAGE.equals(item.getInstallStatus())).collect(Collectors.toList()).size();
                status = installNum > 0 ? status : "1";
            }
        }

        return FieldValueMessageController.fieldValue("arrivalListStatus", status);
    }

    private void setCanDeleted(Page<ArrivalList> result) {
        for (ArrivalList arrivalList : result) {
            if (arrivalList.getStatus() == 1) {
                arrivalList.setCannotDeleted(false);
            } else {
                List<ArrivalListItem> items = aiService.getListItem(arrivalList.getId());
                if (items != null) {
                    arrivalList.setCannotDeleted(true);
                    boolean canNotDelete = false;
                    for (ArrivalListItem item : items) {
                        if (ArrivalListItem.INSTALL_STATUS_NEW.equals(item.getInstallStatus())) {
                            canNotDelete = true;
                            break;
                        }
                        if (StringUtils.isNotEmpty(item.getAssetUID())) {
                            //验证设备是否无效
                            if (!aiService.assetIsInvalid(item.getAssetUID())) {
                                canNotDelete = true;
                                break;
                            }
                        }
                    }
                    if (!canNotDelete) {
                        if (UserContextService.checkRole("AssetHead")) {
                            arrivalList.setCannotDeleted(false);
                        } else {
                            arrivalList.setCannotDeleted(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
        this.selectedMap.clear();
    }

    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            if (selected.isCannotDeleted() == false) {
                deleteArrivalList();
            } else {
                WebUtil.addErrorMessage("到货清单已经提交,不能删除");
            }
        }
    }

    public void deleteUnLimited() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            List<AssetInfo> assetInfos = deleteArrivalListUnLimited();
            if (assetInfos.size() > 0) {
                StringBuilder nameSb = new StringBuilder();
                StringBuilder functionTypeSb = new StringBuilder();
                assetInfos.forEach(val -> {
                    nameSb.append(val.getName()).append(",");
                    functionTypeSb.append(val.getFunctionType()).append(",");
                });
                WebUtil.addErrorMessage("删除失败,存在未报废设备 名称: " + nameSb + " 设备型号:"+ functionTypeSb +"");
            } else {
                WebUtil.addErrorMessage("删除成功");
            }
        }
    }

    @Transactional
    public void deleteArrivalList() {
        //delete phone and others
        aiService.deleteBlobObject(this.selected.getId());
        //delete items and qrCode
        aiService.deleteListItem(this.selected.getId(), this.selected.getSiteUID());
        //delete list obj
        dao.delete(selected);
        this.selected = null;
    }

    @Transactional
    public List<AssetInfo> deleteArrivalListUnLimited() {
        //delete items and qrCode
        List<AssetInfo> assetInfos = aiService.deleteListItemUnLimited(this.selected.getId(), this.selected.getSiteUID());
        if (assetInfos.size() > 0) {
            return assetInfos;
        }
        //delete phone and others
        aiService.deleteBlobObject(this.selected.getId());
        //delete list obj
        dao.delete(selected);
        this.selected = null;
        return new ArrayList<>();
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Create");
    }

    public String saveAsAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Create".concat(selected == null ? "" : "&copyFrom=".concat(selected.getId())));
    }

    public String editAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId=" + selected.getId());
    }

    public void editActionOpen() {
        String url = "action=Edit&listId=" + selected.getId();
        url = "arrivalListEdit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String batchDistribute() {
        return UrlEncryptController.encodeUrlParam("batchDistributeSupplier?supplierUserBind=1");
    }

    public void exportSelectedToPdf() {
        Map<String, Object> info = new HashedMap();
        String itemId = selected.getId();
        getBasicInfo(itemId, info);
        getDetails(itemId, info);
        doExportToPdf(info);
    }

    private void doExportToPdf(Map<String, Object> info) {
        try {
            String woXml = getWoXml(info);
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

            InputStream templateInputStream = systemTemplateService.getTemplateInputStream(SystemTemplateEnum.ARRIVAL_SUPPLIER_REPORT_PDF, currentUserAccount);


//
//            File xsl = new File(WorkOrderReportExportController.class.getClassLoader().
//                    getResource("../../portal/pdf/arrivalItem.xsl").getPath());
//
            File conf = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/fop.xml").getPath());
//            String path1 = WorkOrderReportExportController.class.getClassLoader().getResource("").getPath();
//            path1 = path1.substring(1);
//            String path2 = path1+"report/pdf/arrivalItem.xsl";
//            File xsl = new File(path2);
//
//            String path3 = WorkOrderReportExportController.class.getClassLoader().getResource("").getPath();
//            path3 = path3.substring(1);
//            String path4 = path3+"report/pdf/fop.xml";
//            File conf = new File(path4);

            FopFactory fopFactory = FopFactory.newInstance(conf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                Transformer transformer = factory.newTransformer(new StreamSource(templateInputStream));
                Source src = new StreamSource(new StringReader(woXml));
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            } finally {
                exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(out.toByteArray())), MimeConstants.MIME_PDF, "AssetArrivalList.pdf");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getWoXml(Map<String, Object> data) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new Map2XmlConverter(new DefaultMapper(XStream.class.getClassLoader())));
        xStream.alias("List", List.class);
        xStream.alias("map", HashedMap.class);

        return xStream.toXML(data);
    }

    private void sortArrivalList(List<ArrivalListItem> items) {
        Collections.sort(items, new Comparator<ArrivalListItem>() {
            /*
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(ArrivalListItem o1, ArrivalListItem o2) {
                //按序号升序排序
                if (o1.getItemNum() > o2.getItemNum()) {
                    return 1;
                }
                if (o1.getItemNum() == o2.getItemNum()) {
                    return 0;
                }
                return -1;
            }
        });

    }

    private void getDetails(String itemId, Map<String, Object> info) {
        List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
        List<ArrivalListItem> items = aiService.getListItem(itemId);
        sortArrivalList(items);
        for (ArrivalListItem item : items) {
            Map<String, Object> detail = new HashedMap();
            detail.put("序号", item.getItemNum());
            detail.put("使用科室",
                    aiService.getOrgInfoByUid(item.getClinicalDeptUID()) != null
                            ? aiService.getOrgInfoByUid(item.getClinicalDeptUID()).getName()
                            : null);
            detail.put("制造商序列号", item.getSerialNum());
            detail.put("SystemID", item.getSystemId());
            detail.put("资产编号", item.getFinancingNum());
            detail.put("注册证号", item.getRegistrationNo());
            detail.put("院方设备编号", item.getDepartNum());
            detail.put("二维码", item.getQrCode());
            detail.put("出厂日期", item.getManufactDate());
            detail.put("启用日期", item.getInstallDate());
            detail.put("维保截止日期", item.getWarrantyDate());
            detail.put("质保日期", item.getFactoryWarrantyDate());
            details.add(detail);
        }
        if (items.size() < 11) {
            for (int i = 0; i < 11 - items.size(); i++) {
                Map<String, Object> detail = new HashedMap();
                detail.put("序号", "");
                detail.put("使用科室", "");
                detail.put("制造商序列号", "");
                detail.put("资产编号", "");
                details.add(detail);
            }

        }
        info.put("到货条目", details);
    }

    private void getBasicInfo(String itemId, Map<String, Object> info) {
        ArrivalList curItem = dao.findById(itemId);
        info.put("设备名称", curItem.getAssetName());
        info.put("规格型号", curItem.getFunctionType());
        info.put("数量", curItem.getQuantity());
        info.put("生产厂家", curItem.getManufacture());
        info.put("产地", curItem.getOrigin());
        info.put("发票号", curItem.getInvoiceNum());
        if(curItem.getContractPrice()!=null){
            DecimalFormat df = new DecimalFormat("0.00");
            String price= df.format(curItem.getContractPrice());
            info.put("合同价格",price);
        }
        info.put("合同号", curItem.getContractNum());
        info.put("供应商", curItem.getVendor());
        info.put("经办人", curItem.getAgent());
        info.put("设备分类编码", curItem.getClassCode());
        info.put("到货单类型", curItem.getOrderType());// [1:医院购买,2:赠送,3:投放,4;试用,5:借用]
    }

//    public UserAccount getUserAccountByUid(String uid) {
//        return aiService.getUserAccountByUid(uid);
//    }

    public StreamedContent getExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(StreamedContent exportPdf) {
        this.exportPdf = exportPdf;
    }

    public String getArrivalFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varArrivalDateTo == null) {
            return arrivalFormatTime;
        } else {
            return "~" + sdf.format(varArrivalDateTo);
        }
    }

    public void exportArrivalSupplierReportToDoc() {
        try {
            Map<String, Object> data = arrivalSupplierReportService.createReportData(selected);
            byte[] templateBytes = arrivalSupplierReportService.buildReportTemplate();

            if (templateBytes != null) {
                exportDoc = arrivalSupplierReportService.exportDocx(data, templateBytes);
            }
        } catch (IOException e) {
            Logger.getLogger(AssetArrivedListController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("导出失败");
        }
    }

    public boolean isChecked() {
        Map<String, Boolean> map = this.selectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }

        }
        return false;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (ArrivalList item : currentPageAssets) {
            selectedMap.put(item.getId(), !isSelectedCurrentPage);
        }
    }

    public String getSupplierUserName(Integer userId) {
        if (userId != null) {
            UserAccount userAccount = uaaService.getUserById(userId);
            if (userAccount != null) {
                return userAccount.getName();
            }
        }
        return null;
    }

    /**
     *  _正常安装,_简易安装
     * @throws IOException
     */
    public void  exportArrivalSupplierReportToZip() throws IOException{
       ByteArrayOutputStream bout = new ByteArrayOutputStream();
       try(ZipOutputStream zout = new ZipOutputStream(bout)) {
           String id = selected.getId();
           String processType = selected.getProcessType();
           List<ArrivalListItem> arrivalListItems = itemDao.findByArrivalListId(id);
           if (CollectionUtils.isEmpty(arrivalListItems)) return;
           List<SystemTemplate> systemTemplates = systemTemplateService.findByKeyAndSiteForUseParent(SystemTemplateEnum.ARRIVAL_ACCEPTANCE_REPORT.getCode(), UserContextService.getCurrentUserAccount());
           if(StringUtils.isNotEmpty(processType)){
               String   templateTypeNameSuffix= processType.equalsIgnoreCase("normal")?"_正常安装":"_简易安装";
               List<SystemTemplate> collect = systemTemplates.stream().filter(item -> item.getTemplateTypeNameValue().contains(templateTypeNameSuffix)).collect(Collectors.toList());
               if(!CollectionUtils.isEmpty(collect)){
                   systemTemplates = collect;
               }
           }
           Set<String> names = new HashSet<>();
           for (ArrivalListItem item : arrivalListItems) {
               for (SystemTemplate systemTemplate : systemTemplates) {
                   StreamedContent streamedContent = installationReportService.exportReport(systemTemplate, item);
                   if (streamedContent != null) {
                       String fileName = streamedContent.getName();
                       String name =  fileName;
                       // 防止重复文件名
                       int index = 1;
                       String suffix = name.substring(name.lastIndexOf("."));
                       String prefix = name.substring(0,name.lastIndexOf("."));
                       while (!names.add(name)) {
                           name = prefix +"（"+index+"）"+suffix;
                           index ++;
                       }
                       zout.putNextEntry(new ZipEntry(name));
                       byte[] bytes = IOUtils.toByteArray(streamedContent.getStream());
                       IOUtils.write(bytes, zout);
                       zout.closeEntry();
                   }
               }
           }
       }
       exportZip = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "AssetInstallReports.zip");
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    public Date getVarArrivalDateTo() {
        return varArrivalDateTo;
    }

    public void setVarArrivalDateTo(Date varArrivalDateTo) {
        this.varArrivalDateTo = varArrivalDateTo;
    }

    public StreamedContent getExportDoc() {
        return exportDoc;
    }

    public void setExportDoc(StreamedContent exportDoc) {
        this.exportDoc = exportDoc;
    }

    public boolean activeSupplier() {
        return supplierService.activeSupplier();
    }

    public String getSupplierUserBind() {
        return supplierUserBind;
    }

    public void setSupplierUserBind(String supplierUserBind) {
        this.supplierUserBind = supplierUserBind;
    }

    public Integer getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(Integer statusFlag) {
        this.statusFlag = statusFlag;
    }

    public StreamedContent getExportZip() {
        return exportZip;
    }

    public void setExportZip(StreamedContent exportZip) {
        this.exportZip = exportZip;
    }
}

package com.ge.apm.view.asset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.map.HashedMap;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.Map2XmlConverter;
import com.ge.apm.view.wo.WorkOrderReportExportController;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

@ManagedBean
@ViewScoped
public class AssetArrivedListController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private StreamedContent exportPdf;

    private Date varArrivalDateTo = null;
    private String arrivalFormatTime = null;

    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
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
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            WebUtil.addErrorMessage("到货清单已经提交,不能删除");
        }
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Create");
    }
    
    public String saveAsAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Create".concat(selected==null?"":"&copyFrom=".concat(selected.getId())));
    }

    public String editAction() {
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId=" + selected.getId());
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

            File xsl = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/arrivalItem.xsl").getPath());

            File conf = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/fop.xml").getPath());

            FopFactory fopFactory = FopFactory.newInstance(conf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                Transformer transformer = factory.newTransformer(new StreamSource(xsl));
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

    private void sortArrivalList(List<ArrivalListItem> items)
    {
    	Collections.sort(items, new Comparator<ArrivalListItem>(){
			/*  
             * 返回负数表示：o1 小于o2，  
             * 返回0 表示：o1和o2相等，  
             * 返回正数表示：o1大于o2。  
             */  
			public int compare(ArrivalListItem o1, ArrivalListItem o2) {
				//按序号升序排序  
	            if(o1.getItemNum() > o2.getItemNum()){  
	                return 1;  
	            }  
	            if(o1.getItemNum() == o2.getItemNum()){  
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
        if(items.size() < 11)
        {
        	for(int i = 0; i < 11-items.size(); i++)
        	{
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
        info.put("合同价格", curItem.getContractPrice());
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

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    public Date getVarArrivalDateTo() {
        return varArrivalDateTo;
    }

    public void setVarArrivalDateTo(Date varArrivalDateTo) {
        this.varArrivalDateTo = varArrivalDateTo;
    }

    
}

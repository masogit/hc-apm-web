package com.ge.apm.view.asset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.service.sharedAsset.SharedAssetStockService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.wo.Map2XmlConverter;
import com.ge.apm.view.wo.WorkOrderReportExportController;
import com.google.common.base.Strings;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

@ManagedBean
@ViewScoped
public class SharedAssetStockController extends MyCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetStockController.class);

    @Getter
    @Setter
    private String searchCriteria;

    @Getter
    @Setter
    private int queryType = -1;

    @Getter
    @Setter
    private String leaseDeptName;

    private SharedAssetStockService sharedAssetStockService;
    
    @Getter
    @Setter
    private StreamedContent exportPdf;

    @Override
    protected BaseMapper<AssetInfo> getDAO() {
        return null;
    }

    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetStockService = WebUtil.getBean(SharedAssetStockService.class);
    }

    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        logger.info("thread: {}, searchCriteria: {}", Thread.currentThread().getId(), this.searchCriteria);
        Wrapper ew = new EntityWrapper();

        sharedAssetStockService.buildDefaultFilters(ew);
        sharedAssetStockService.addFiltersByQueryType(ew, this.queryType);
        if(StringUtils.isNotBlank(this.searchCriteria)) {
            sharedAssetStockService.addSearchCriteria(ew, this.searchCriteria.trim());
        }
        ew.andNew().isNotNull("shared_asset_type");
        wrapSearchFilter(ew);
        logger.info("SQL: " + ew.originalSql());
        lazyModel.setRowCount(sharedAssetStockService.getTotalRowCount(ew));
        ew.orderBy(sortField, isAscending);
        List<AssetInfo> assetList = sharedAssetStockService.loadAllSharedAssets(pageRequest, ew);
        return assetList;
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
            + selected.getId().toString()
            + "&asset_name=" + selected.getName()
            + "&terminate=" + false;
        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel() {
        return sharedAssetStockService.getAllAccessibleOrgDeptListNestedByOrgLevel();
    }

    public void prepareForceLeaseAsset(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        if(reqDetail == null) {
            reqDetail = new SharedAssetReqDetail();
            reqDetail.setChargingBeginTime(new Date());//默认计费开始时间为系统当前时间
            SharedAssetRequest leaseRequest = new SharedAssetRequest();
            leaseRequest.setType("2");//默认请求类型:补登记
            reqDetail.setLeaseRequest(leaseRequest);
        }
        asset.setSharedAssetReqDetail(reqDetail);
        super.setSelected(asset);
    }

    public void prepareForceReturnAsset(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        SharedAssetRequest leaseRequest = sharedAssetStockService.findRequestById(asset.getSharedAssetReqDetail().getBorrowId());
        if(reqDetail.getReturnRequest() == null) {
            SharedAssetRequest returnRequest = new SharedAssetRequest();
            if(leaseRequest.getType() == null) {//默认请求类型和强制调拨类型相同
                returnRequest.setType("2");//默认请求类型:补登记
            } else {
                returnRequest.setType(leaseRequest.getType());
            }
            reqDetail.setReturnRequest(returnRequest);
        }
        reqDetail.setChargingEndTime(new Date());
        asset.setSharedAssetReqDetail(reqDetail);
        super.setSelected(asset);
    }
    
    //301医院则显示打印按钮
    public boolean validate301()
    {
    	return sharedAssetStockService.validate301();
    }
    
    public void printVoucher(AssetInfo asset)
    {
    	try 
		{
    		String fileName = null;    		
    		Map<String, String> voucherInfo = sharedAssetStockService.generateVoucher(asset);
    		
    		//when no voucher_num, then take old template to print
    		String prefix = StringUtils.isBlank(voucherInfo.get("借据ID")) ? "Shared_Asset" : voucherInfo.get("借据ID");
	        if(!asset.getAssetDeptUID().equals(asset.getClinicalDeptUID()))
	        {
	            fileName = new String((prefix + "_Borrow_Info_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) 
						+ ".pdf").getBytes(),"UTF-8");
	            doExportToPdf(voucherInfo, "sharedAssetBorrowed.xsl", fileName);
	        }
	        else
	        {
				fileName = new String((prefix + "_Return_Info_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) 
						+ ".pdf").getBytes(),"UTF-8");
				doExportToPdf(voucherInfo, "sharedAssetReturned.xsl", fileName );
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
        }
    }

    private void doExportToPdf(Map<String, String> info, String xslName, String pdfName) {
        try {
            String woXml = getWoXml(info);

            File xsl = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/" + xslName).getPath());

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
                exportPdf = (new DefaultStreamedContent((new ByteArrayInputStream(out.toByteArray())), MimeConstants.MIME_PDF, pdfName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getWoXml(Map<String, String> data) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new Map2XmlConverter(new DefaultMapper(XStream.class.getClassLoader())));
        xStream.alias("List", List.class);
        xStream.alias("map", HashedMap.class);

        return xStream.toXML(data);
    }

    public void forceLeaseAsset() {
        if(this.selected != null) {
            List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
            OrgInfo assetDept = deptList.stream().filter(item -> item.getName().equals(leaseDeptName))
                                                 .findFirst()
                                                 .orElse(null);
            try {
                if(assetDept != null) {
                    selected.setAssetDeptUID(assetDept.getUid());
                    ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetStockService.forceLeaseAsset(selected);
                    LinkedHashMap<String, Object> rspBody = borrowResponse.getBody();
                    logger.info("****** force lease response ******");
                    rspBody.forEach((k, v) -> logger.info("key: " + k + ", value: " + v));
                }
            } catch (Exception ex) {
                logger.error("error: {}" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void forceReturnAsset() {
        if(this.selected != null) {
            try {
                ResponseEntity<LinkedHashMap> returnResponse = sharedAssetStockService.forceReturnAsset(selected);
                LinkedHashMap<String, Object> rspBody = returnResponse.getBody();
                logger.info("****** force return response ******");
                rspBody.forEach((k, v) -> logger.info("key: " + k + ", value: " + v));
            } catch (Exception ex) {
                logger.error("error: {}" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void onLeaseDeptNameChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            leaseDeptName = (String) ((SelectEvent) event).getObject();
        } else {
            List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
            if (!Strings.isNullOrEmpty(leaseDeptName) && !deptList.stream().anyMatch(item -> leaseDeptName.equals(item.getName()))) {
                leaseDeptName = "";
            }
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
        List<OrgInfo> res = deptList.stream().filter(item -> Strings.isNullOrEmpty(query)
                                                    || item.getName().contains(query)
                                                    || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                                             .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName())
                            .distinct()
                            .collect(Collectors.toList());
    }

}

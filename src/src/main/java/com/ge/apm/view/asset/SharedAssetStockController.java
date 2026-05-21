package com.ge.apm.view.asset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Transient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.RequestType;
import com.ge.apm.view.sysutil.UserContextService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.service.sharedasset.SharedAssetStockService;
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
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

@ManagedBean
@ViewScoped
public class SharedAssetStockController extends MyCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetStockController.class);
    private OrgInfoRepository orgInfoRepository;
    @Getter
    @Setter
    private String searchCriteria;

//    private UserAccount user;
    private AssetInfoRepository assetInfoRepository;

    private SharedAssetRequestRepository sharedAssetRequestRepository;

    private UserAccountRepository userAccountRepository;

    @Getter
    @Setter
    private int queryType = -1;

    @Getter
    @Setter
    private String leaseDeptName;

    @Getter
    @Setter
    private Date maxChargingBeginTime;

    @Getter
    @Setter
    private Date maxChargingEndTime;

    private SharedAssetStockService sharedAssetStockService;

    @Getter
    @Setter
    private StreamedContent exportPdf;

    @Getter
    @Setter
    private UserAccount borrowerUser;

    @Getter
    @Setter
    private OrgInfo borrowrOrgInfo;

//    public SharedAssetStockController(UserAccount user) {
//        this.user = user;
//    }

    @Override
    protected BaseMapper<AssetInfo> getDAO() {
        return null;
    }

    @Transient
    SharedAssetReqDetailRepository asdDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);

    @Override
    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetStockService = WebUtil.getBean(SharedAssetStockService.class);
        orgInfoRepository= WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository= WebUtil.getBean(AssetInfoRepository.class);
        sharedAssetRequestRepository=WebUtil.getBean(SharedAssetRequestRepository.class);
        userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
    }

    @Override
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
            + selected.getUid()
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
            //默认计费开始时间为系统当前时间
            reqDetail.setChargingBeginTime(new Date());
            SharedAssetRequest leaseRequest = new SharedAssetRequest();
            //默认请求类型:补登记
            leaseRequest.setType(RequestType.REQ_FOR_RECORD.getIndex());
            reqDetail.setLeaseRequest(leaseRequest);
        }
        asset.setSharedAssetReqDetail(reqDetail);
        boolean b = UserContextService.checkRole("SharedAssetMgmt");
        if(!b){
            borrowerUser = UserContextService.getCurrentUserAccount();
        }
        super.setSelected(asset);
    }

    public void prepareForceReturnAsset(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        SharedAssetRequest leaseRequest = sharedAssetStockService.findRequestById(asset.getSharedAssetReqDetail().getBorrowId());
        if(reqDetail.getReturnRequest() == null) {
            SharedAssetRequest returnRequest = new SharedAssetRequest();
            //默认请求类型和强制调拨类型相同
            if(leaseRequest.getType() == null) {
                //默认请求类型:补登记
                returnRequest.setType(RequestType.REQ_FOR_RECORD.getIndex());
            } else {
                returnRequest.setType(leaseRequest.getType());
            }
            reqDetail.setReturnRequest(returnRequest);
        }
        reqDetail.setChargingEndTime(new Date());
        asset.setSharedAssetReqDetail(reqDetail);
        super.setSelected(asset);

        //设置最迟计费开始时间和最迟计费结束时间
        maxChargingBeginTime = Date.from(Instant.now());
        maxChargingEndTime = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
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
    public void forceEditAssets() {
        AssetInfo selected = this.selected;
        SharedAssetReqDetail sharedAssetReqDetail = selected.getSharedAssetReqDetail();
        sharedAssetReqDetail=asdDao.findById(sharedAssetReqDetail.getId());
        OrgInfo orgInfo = orgInfoRepository.findByNameAndSiteUID(this.leaseDeptName, selected.getSiteUID());
        selected=assetInfoRepository.findById(selected.getId());
        selected.setAssetDeptUID(orgInfo.getUid());
        assetInfoRepository.save(selected);
        sharedAssetReqDetail.setClinicalDeptName(this.leaseDeptName);
        sharedAssetReqDetail.setClinicalDeptUid(orgInfo.getUid());
        asdDao.save(sharedAssetReqDetail);
        SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(sharedAssetReqDetail.getBorrowId());
        sharedAssetRequest.setClinicalDeptUid(orgInfo.getUid());
        sharedAssetRequest.setClinicalDeptName(this.leaseDeptName);
        if(borrowerUser != null){
            sharedAssetRequest.setApplicant(borrowerUser.getLoginName());
            sharedAssetRequest.setAccepterNumber(borrowerUser.getTelephone());
        }
        sharedAssetRequestRepository.save(sharedAssetRequest);
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
        autoCompleteBorrowerName("");
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
    public void onBorrowerName() {
//        System.out.println(borrowerUser);
    }
    public List<UserAccount>  autoCompleteBorrowerName(String query) {
        OrgInfo orgInfo = orgInfoRepository.findByNameAndSiteUID(this.leaseDeptName, selected.getSiteUID());
        if(orgInfo != null){
            List<UserAccount> byClinicalDept = userAccountRepository.getByClinicalDept(Arrays.asList(orgInfo.getUid()));
            List<UserAccount> res = byClinicalDept.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                    || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
            borrowerUser = null;
            if(CollectionUtils.isEmpty(res)){
                UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
                res = new ArrayList<>();
                res.add(currentUserAccount);
            }
            return res;
        }
      return new ArrayList<>();
    }

    public Boolean isLogPatientInfoEnabled() {
        return sharedAssetStockService.isLogPatientInfoEnabled();
    }
    
    private Boolean isValid301 = null;
            
    //301医院则显示打印按钮
    public boolean validate301()
    {
        if(isValid301 == null) {
            isValid301 = sharedAssetStockService.validate301();
        }
        return isValid301;        
    } 
    
    
    public void printVoucher(AssetInfo asset)
    {
    	try 
		{
    		String fileName = null; 
    		//get real time asset info by assetUId, as asset got from html returned by microservice may not be the latest
    		Map<String, String> voucherInfo = sharedAssetStockService.generateVoucher(asset);
    		asset = sharedAssetStockService.getRealTimeAssetInfo(asset.getUid());
    		//when no voucher_num, then take old template to print
    		String prefix = StringUtils.isBlank(voucherInfo.get("实际ID")) ? "Shared_Asset" : voucherInfo.get("实际ID");;
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
    public boolean isWhethertoenableunattended(){
       return sharedAssetStockService.isWhethertoenableunattended();
    }
}

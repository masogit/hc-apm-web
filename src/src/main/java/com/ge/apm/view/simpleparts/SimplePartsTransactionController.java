package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.simpleparts.*;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.simpleparts.SimplePartsTransactionService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author 503189828
 */
@ManagedBean
@ViewScoped
public class SimplePartsTransactionController extends GenericCRUDUUIDController<SimplePartsTransaction> {

    private static final long serialVersionUID = 8050081662275979026L;
    private SimplePartsTransactionRepository partsTransactionRepository;
    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private OrgInfoRepository orgInfoDao;
    private UserAccountRepository userDao;
    private I18nMessageRepository i18nDao;
    private I18nMessageService i18nMessageService;
    private UaaService uaaService;
    private long queryIndex = SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK;
    private SimplePartsRequestService partsRequestService;
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;
    private SimplePartsRequestRepository partsRequestRepository;
    @Getter
    @Setter
    private String oneSearchStr;

    @Getter
    @Setter
    private StreamedContent exportFile;

    public void searchList(Long queryIndex){
        this.queryIndex = queryIndex;
        this.oneSearchStr = "";
        this.searchFilters.clear();
    }

    public void searchTextList(Long queryIndex){
        this.queryIndex = queryIndex;
        this.searchFilters.clear();
    }

    @Override
    protected void init() {
        partsTransactionRepository = WebUtil.getBean(SimplePartsTransactionRepository.class);
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        i18nDao= WebUtil.getBean(I18nMessageRepository.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        partsRequestService = WebUtil.getBean(SimplePartsRequestService.class);
        partsRequestLineItemRepository = WebUtil.getBean(SimplePartsRequestLineItemRepository.class);
        partsRequestRepository = WebUtil.getBean(SimplePartsRequestRepository.class);
    }

    @Override
    protected Page<SimplePartsTransaction> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        searchFilters.removeIf(e->e.fieldName.equals("siteId"));
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(currentUserAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        setUserSearchFilters();

        //入库 or 出库
        if(queryIndex == SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK){
            this.updateSearchFilter(new SearchFilter("transactionType",SearchFilter.Operator.EQ, SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK));
        }else{
            this.updateSearchFilter(new SearchFilter("transactionType",SearchFilter.Operator.EQ, SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK));
        }
        Page<SimplePartsTransaction> inventoryList = partsTransactionRepository.findBySearchFilter(searchFilters, pageRequest);
        return inventoryList;
    }

    private void setUserSearchFilters() {
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("requestCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public List<I18nMessage> getRequestTypeList(){
        return i18nMessageService.getByMsgType(SimplePartsConstant.I18N_REQUEST_TYPE);
    }

    public String switchRequestType(int requestType){
           return i18nDao.getByTypeAndKey(SimplePartsConstant.I18N_REQUEST_TYPE,String.valueOf(requestType));
    }

    public String switchStorageType(String storageType){
        return i18nDao.getByTypeAndKey(SimplePartsConstant.I18N_STORAGE_TYPE,storageType);
    }

    public String switchSiteName(String siteUID){
        return orgInfoDao.findNameByUID(siteUID);
    }

    public String getWarehouse(String partsWarehouseUid){
        SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.findById(partsWarehouseUid);
        if (partsWarehouse != null){
            return partsWarehouse.getName();
        }
        return null;
    }

    public String getLocator(String partsLocatorUid) {

        if (SimplePartsConstant.VIRTUAL_LOCATOR_ID.equals(partsLocatorUid)) {
            return SimplePartsConstant.VIRTUAL_LOCATOR_NAME;
        }

        SimplePartsWareHouseLocator partsWareHouseLocator = partsWarehouseLocatorRepository.findById(partsLocatorUid);
        if(partsWareHouseLocator != null){
            return partsWareHouseLocator.getName();
        }
        return  null;
    }

    public void exportReportDocx() {
        List<SimplePartsTransaction> simplePartsTransactions = partsTransactionRepository.findByPartsRequestIdAndRequestCode(selected.getPartsRequestId(),selected.getRequestCode());
        SimplePartsRequest simplePartsRequest = partsRequestRepository.findById(selected.getPartsRequestId());
        try {
            Map<String, Object> data = partsRequestService.createReportData(simplePartsRequest,simplePartsTransactions);
            byte[] templateBytes = partsRequestService.buildReportTemplate();

            if (templateBytes != null) {
                exportFile = partsRequestService.exportDocx(data, templateBytes);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(SimplePartsRequestListController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("导出失败");
        }
    }

    public String switchAccountName(String loginname){
        return userDao.getNameByLoginName(loginname);
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsTransaction> getDAO() {
        return partsTransactionRepository;
    }
    public long getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(long queryIndex) {
        this.queryIndex = queryIndex;
    }
}

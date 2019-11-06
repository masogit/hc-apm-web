package com.ge.apm.view.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.service.asset.ContractInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ContractInfoController extends GenericCRUDUUIDController<ContractInfo> {

    private UaaService uaaService;
    private ContractInfoService contractService;
    private ContractInfoRepository dao = null;
    private String assetUid;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ContractInfoRepository.class);
        contractService = WebUtil.getBean(ContractInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        String encodeStr = WebUtil.getRequestParameter("str");
        assetUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetUid");
    }

    @Override
    protected ContractInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ContractInfo> loadData(PageRequest pageRequest) {

        if (!StringUtils.isEmpty(assetUid)) {
            List<ContractInfo> contractInfos = contractService.getContractListByAssetUid(assetUid);
            PageImpl page = new PageImpl(contractInfos);
            return page;
        }

        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if(!this.hasFilterOnField("contractType")){
            List<String> contractTypeList = Arrays.asList(ContractInfo.ContractType.values()).stream().map(item -> item.name()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.IN, contractTypeList, "1*"));
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

    protected long countValidContracts(ContractInfo contractInfo) {

        List<SearchFilter> localSearchFilters = new ArrayList<>();
        List<String> contractTypeList = Arrays.asList(ContractInfo.ContractType.values()).stream().map(item -> item.name()).collect(Collectors.toList());
        localSearchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.IN, contractTypeList, "1*"));

        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
        localSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        localSearchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, contractInfo.getId(), "1*"));

        Page<ContractInfo> contractInfoPage = dao.findBySearchFilter(localSearchFilters, new PageRequest(0, 1));

        return contractInfoPage.getTotalElements();
    }

    public boolean isEditable(ContractInfo contractInfo) {
        if (contractInfo == null || StringUtils.isEmpty(assetUid)) {
            return true;
        }

        long result = countValidContracts(contractInfo);

        if (result > 0) {
            return true;
        }

        return false;

    }

    public void completeContract(ContractInfo contractInfo) {
        if(contractInfo != null) {
            contractService.updateContractStatus(contractInfo.getId(), ContractInfo.ContractStatus.COMPLETE.toString());
        }
    }
    
    
    public void deleteContract(){
        if(contractService.deleteContract(selected)){
            WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
        }else{
            WebUtil.addErrorMessage(WebUtil.getMessage("CannotBeDeleted"));
        }
    }
    
    public String createAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Create");
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Edit&selectedid=".concat(selected.getId()));
    }
    public String importAction() {
        return "contractImport/ContractImport?faces-redirect=true";
    }
    public String importItemAction() {
        return "contractImport/ContractItemImport?faces-redirect=true";
    }
    public String exportAction() {
        return "contractExport/ContractExport?faces-redirect=true";
    }

}

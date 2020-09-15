package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: csh
 * @date: 2020/7/13 13:55
 * @version: 1.0
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsWarehouseListController extends GenericCRUDUUIDController<SimplePartsWarehouse> {
    private static final long serialVersionUID = 1886024138737071703L;

    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private UaaService uaaService;
    private SimplePartsWarehouseEditController partsWarehouseEditController;
    private SimplePartsWarehouseLocatorController partsWarehouseLocatorController;
    private SimplePartsWarehouseOrgController partsWarehouseOrgController;
    private SimplePartsWarehouseUserController partsWarehouseUserController;

    @Override
    protected void init() {
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        partsWarehouseEditController = WebUtil.getBean(SimplePartsWarehouseEditController.class);
        partsWarehouseLocatorController = WebUtil.getBean(SimplePartsWarehouseLocatorController.class);
        partsWarehouseOrgController = WebUtil.getBean(SimplePartsWarehouseOrgController.class);
        partsWarehouseUserController = WebUtil.getBean(SimplePartsWarehouseUserController.class);
    }

    @Override
    protected Page<SimplePartsWarehouse> loadData(PageRequest pageRequest) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        searchFilters = new ArrayList<>();
        List<String> orgUidList = uaaService.getOrgListByUser(currentUserAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        Page<SimplePartsWarehouse> partsWarehousetPageList = partsWarehouseRepository.findBySearchFilter(searchFilters, pageRequest);
        return partsWarehousetPageList;
    }

    public void onselectItem(SelectEvent event) {
        partsWarehouseEditController.onServerEvent("",selected);
        partsWarehouseLocatorController.onServerEvent("",selected);
        partsWarehouseOrgController.onServerEvent("",selected);
        partsWarehouseUserController.onServerEvent("",selected);
    }

    /**
     * @param encoded
     * @return String
     * @date 2020/7/13 18:40
     */
    public String getEncodedNameByEncoded(String encoded) {
        return null;
    }

    /**
     * @param
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWarehouse>
     * @date 2020/7/13 18:40
     * 功能描述
     */
    public List<SimplePartsWarehouse> getAllDataList() {
        List<SimplePartsWarehouse> lists = partsWarehouseService.findAll();
        return lists;
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsWarehouse> getDAO() {
        return partsWarehouseRepository;
    }

}

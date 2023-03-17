package com.ge.apm.view.SparePart;

import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SparePartController extends GenericCRUDUUIDController<SparePart> {
    private PageUtil<SparePart> pageUtil = new PageUtil();

    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private SparePartRepository dao;
    private StockRecordRepository stockRecordRepository;
    private SimplePartsWarehouseRepository warehouseRepository;

    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo currentOrg;
    @Getter @Setter
    private List<SimplePartsWarehouse> warehouses = new ArrayList<>();
    @Getter @Setter
    private String partsWarehouseId;
    @Getter@Setter
    private String filterClinicalDeptOrg;
    @Getter@Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter@Setter
    private Integer outQuantity;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SparePartRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        stockRecordRepository =  WebUtil.getBean(StockRecordRepository.class);
        warehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);

        loginUser = UserContextService.getCurrentUserAccount();
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());

        this.filterBySite = false;
        this.filterByHospital = false;

        initWarehouses(loginUser.getTenantUID());
        loadClinicalDeptList();
    }

    @Override
    protected SparePartRepository getDAO() {
        return dao;
    }

    private void initWarehouses(String tenantUid) {
        warehouses = warehouseRepository.findAllWarehouseList(tenantUid);
    }

    private void setFilter() {
        this.searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
    }

    @Override
    protected Page<SparePart> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        setFilter();
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onSelect() {

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        partsWarehouseId = selected.getPartsWarehouseId();
    }

    public void saveEdit() {
        dao.save(selected);
    }

    public void save() {
        selected.setPartsWarehouseId(partsWarehouseId);
        saveSparePartAndRecord(1);
    }

    public void outStock(SparePart sparePart) {
        selected = sparePart;
        outQuantity = null;
        filterClinicalDeptOrg = null;
    }

    public void outSave() {
        if (outQuantity > selected.getQuantity()) {
            WebUtil.addErrorMessage("出库数量不得大于备件数量");
            return;
        }
        selected.setQuantity(selected.getQuantity() - this.outQuantity);
        selected.setTotalPrice(selected.getTotalPrice() - (this.outQuantity * selected.getUnitPrice()));
        saveSparePartAndRecord(2);
    }

    @Transactional
    public void saveSparePartAndRecord(Integer recordType) {
        selected.setTenantUID(loginUser.getTenantUID());
        dao.save(selected);
        stockRecordRepository.save(buildStockRecord(recordType));
    }

    private StockRecord buildStockRecord(Integer recordType) {
        return setOutStockRecord(StockRecord.builder()
                .recordType(recordType)
                .sparePartId(selected.getId())
                .recordDate(new Date())
                .warehouseOprPerson(loginUser.getName())
                .warehouseOprPersonUid(loginUser.getUid())
                .quantity(selected.getQuantity())
                .tenantUID(loginUser.getTenantUID())
                .build());
    }

    private StockRecord setOutStockRecord(StockRecord record) {
        if (record.getRecordType() == 2) {
            OrgInfo orgInfo = clinicalDeptList.stream().filter(var -> var.getName().equals(filterClinicalDeptOrg)).findFirst().orElse(new OrgInfo());
            record.setReceivingDepartment(orgInfo.getName());
            record.setReceivingDepartmentUid(orgInfo.getUid());
            record.setQuantity(outQuantity);
        }
        return record;
    }

    public void cancel() {
        selected = null;
        partsWarehouseId = null;
    }

    public void delete() {
        dao.delete(selected);
    }

    public void totalPrice() {
        selected.setTotalPrice(Optional.ofNullable(selected.getUnitPrice()).orElse(0.0) * Optional.ofNullable(selected.getQuantity()).orElse(0));
    }

    public List<String> autoCompleteDeptName(String query) {
        return pageUtil.autoComplete(query, this.clinicalDeptList);
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
    }

}

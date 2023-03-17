package com.ge.apm.view.SparePart;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.StockRecordRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SparePartStockRecordController extends GenericCRUDUUIDController<StockRecord> {
    private PageUtil<SparePart> pageUtil = new PageUtil();

    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private StockRecordRepository stockRecordRepository;
    private SimplePartsWarehouseRepository warehouseRepository;
    private StockRecordRepository dao;

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
    private Integer filterRecordType = 1;
    @Getter@Setter
    private Date startTime;
    @Getter@Setter
    private Date endTime;

    @Override
    protected void init() {
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        stockRecordRepository =  WebUtil.getBean(StockRecordRepository.class);
        warehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        dao = WebUtil.getBean(StockRecordRepository.class);

        loginUser = UserContextService.getCurrentUserAccount();
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());

        this.filterBySite = false;
        this.filterByHospital = false;

        initWarehouses(loginUser.getTenantUID());
        loadClinicalDeptList();
    }

    @Override
    protected StockRecordRepository getDAO() {
        return stockRecordRepository;
    }

    private void initWarehouses(String tenantUid) {
        warehouses = warehouseRepository.findAllWarehouseList(tenantUid);
    }

    private void setFilter() {
        this.searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        this.searchFilters.add(new SearchFilter("recordType", SearchFilter.Operator.EQ, this.filterRecordType));
        if (this.startTime != null) {
            this.searchFilters.add(new SearchFilter("recordDate", SearchFilter.Operator.GTE, this.startTime));
            this.searchFilters.add(new SearchFilter("recordDate", SearchFilter.Operator.LTE
                    , TimeUtils.getDateFromStr(TimeUtils.getStrDate(this.endTime, "yyyyMMdd").concat("235959"), "yyyyMMddHHmmss")));
        }
    }

    @Override
    protected Page<StockRecord> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        removeFilterOnField("recordType");
        removeFilterOnField("tenantUID");
        removeFilterOnField("recordDate");
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
    }

    public void save() {
        dao.save(selected);
    }

    public void cancel() {
        selected = null;
    }

    public void delete() {
        dao.delete(selected);
    }

    public List<String> autoCompleteDeptName(String query) {
        return pageUtil.autoComplete(query, this.clinicalDeptList);
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
    }

}

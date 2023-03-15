package com.ge.apm.view.SparePart;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SparePartRepository;
import com.ge.apm.dao.StockRecordRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SparePartStockRecordController extends GenericCRUDUUIDController<StockRecord> {

    private PageUtil<JmReport> pageUtil = new PageUtil();

    private TenantInfoRepository siteDao;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private UserContextService userContextService;
    private SparePartRepository sparePartRepository;
    private StockRecordRepository stockRecordRepository;
    private SimplePartsWarehouseRepository warehouseRepository;
    private StockRecordRepository dao;

    @Getter @Setter
    private TenantInfo selectedTenant;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo currentOrg;
    @Getter @Setter
    private Integer tenantId;
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
        userContextService = WebUtil.getBean(UserContextService.class);
        sparePartRepository = WebUtil.getBean(SparePartRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        siteDao =  WebUtil.getBean(TenantInfoRepository.class);
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

    }

    @Override
    protected Page<StockRecord> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        return null;
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
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
    }

}

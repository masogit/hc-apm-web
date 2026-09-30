package com.ge.apm.view.SparePart;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SparePartRepository;
import com.ge.apm.dao.StockRecordRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.service.sparepart.SparePartStockRecordService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SparePartStockRecordController extends GenericCRUDUUIDController<StockRecord> {
    private PageUtil<StockRecord> pageUtil = new PageUtil();

    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private StockRecordRepository stockRecordRepository;
    private SimplePartsWarehouseService warehouseService;
    private SparePartStockRecordService stockRecordService;
    private StockRecordRepository dao;
    private SparePartRepository sparePartRepository;
    private BlobFileService blobService;

    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo currentOrg;
    @Getter @Setter
    private List<SimplePartsWarehouse> warehouses = new ArrayList<>();
    @Getter @Setter
    private String partsWarehouseId;
    @Getter @Setter
    private String filterClinicalDeptOrg;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private Integer filterRecordType = 1;
    @Getter @Setter
    private Date startTime;
    @Getter @Setter
    private Date endTime;
    @Getter @Setter
    private List<StockRecord> selectedItems;
    @Getter @Setter
    private StreamedContent exportDocx;
    @Getter @Setter
    private Integer rtQuantity;
    @Getter @Setter
    private List<V2_BlobObject> picList;


    @Override
    protected void init() {
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        stockRecordService = WebUtil.getBean(SparePartStockRecordService.class);
        stockRecordRepository =  WebUtil.getBean(StockRecordRepository.class);
        warehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        dao = WebUtil.getBean(StockRecordRepository.class);
        sparePartRepository = WebUtil.getBean(SparePartRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        loginUser = UserContextService.getCurrentUserAccount();
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        picList = new ArrayList<>();

        this.filterBySite = false;
        this.filterByHospital = false;

        initWarehouses();
        loadClinicalDeptList();
    }

    @Override
    protected StockRecordRepository getDAO() {
        return stockRecordRepository;
    }

    private void initWarehouses() {
        warehouses = warehouseService.findAllBySiteUidAndUserId(loginUser.getSiteUID());
    }

    private void setFilter() {
        this.searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        this.searchFilters.add(new SearchFilter("recordType", SearchFilter.Operator.EQ, this.filterRecordType));
        this.searchFilters.add(new SearchFilter("sparePart.partsWarehouseId", SearchFilter.Operator.IN, warehouses.stream().map(SimplePartsWarehouse::getId).collect(Collectors.toList())));
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
        removeFilterOnField("partsWarehouseId");
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

    public void onSelectChange() {
        this.searchFilters.clear();
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
        rtQuantity = 0;
    }

    public void delete() {
        dao.delete(selected);
    }

    public double totalPrice(double price, Integer qty) {
        return  pageUtil.formatDouble2(Optional.ofNullable(price).orElse(0.0) * Optional.ofNullable(qty).orElse(0));
    }

    public List<String> autoCompleteDeptName(String query) {
        return pageUtil.autoComplete(query, this.clinicalDeptList);
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
    }

    public void exportToDocx() {
        exportDocx = null;
        if (CollectionUtils.isEmpty(this.selectedItems)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NotSelectTheRecordsThatNeedToBeExported"));
            return;
        }
        if (checkSelectedItems()) {
            String fileName = "";
            if (1 == filterRecordType) {
                fileName = "设备配件入库单.docx";
            } else if (2 == filterRecordType) {
                fileName = "设备配件出库单.docx";
            }
            if (UserContextService.hasCurrentTenantName("")) {
                fileName = "云阜" + fileName;
            }
            try {
                byte[] templateBytes = stockRecordService.getTemplate(fileName);
                SparePartStockRecordService.RenderResult renderResult = stockRecordService.renderDocx(selectedItems, templateBytes, fileName, filterRecordType);
                exportDocx = new DefaultStreamedContent((new ByteArrayInputStream(renderResult.result)), renderResult.contentType, renderResult.fileName);
            } catch (IOException e) {
                logger.error("Fail to export part stock record", e);
                WebUtil.addErrorMessage(WebUtil.getMessage("ExportFailed"));
            }
        }
    }

    private boolean checkSelectedItems() {
        if (1 == filterRecordType || 3 == filterRecordType) {
            String supplier = null;
            for (StockRecord selectedItem : selectedItems) {
                if (StringUtils.isNotEmpty(supplier) && !supplier.equals(selectedItem.getSparePart().getSupplier())) {
                    WebUtil.addErrorMessage("选中的数据中供应商不同，无法导出！");
                    return false;
                } else if (StringUtils.isEmpty(supplier)) {
                    supplier = selectedItem.getSparePart().getSupplier();
                }
            }
        } else if (2 == filterRecordType || 4 == filterRecordType) {
            String dept = null;
            for (StockRecord selectedItem : selectedItems) {
                if (StringUtils.isNotEmpty(dept) && !dept.equals(selectedItem.getReceivingDepartmentUid())) {
                    WebUtil.addErrorMessage("选中的数据中领取科室不同，无法导出！");
                    return false;
                } else if (StringUtils.isEmpty(dept)) {
                    dept = selectedItem.getReceivingDepartmentUid();
                }
            }
        }
        return true;
    }

    public void rtStock(StockRecord record) {
        this.selected = record;
        rtQuantity = 0;
    }

    public String getTitle(Integer type, String str) {
        switch (type) {
            case 1:
                return "入库" + str;
            case 2:
                return "出库" + str;
        }
        return "退库"  + str;
    }

    public boolean canRtStock(StockRecord record) {
        return record.getRecordType() == 1 ? record.getSparePart().getQuantity() <= 0 : getValidQuantity(record) <= 0;
    }

    public Integer getValidQuantity(StockRecord record) {
        Integer quantity = record.getQuantity();
        List<StockRecord> stockRecords = dao.findListByOutboundNumber(record.getOutboundNumber());
        return stockRecords.stream().mapToInt(StockRecord::getQuantity).reduce(quantity, (o, n) -> o - n);
    }

    @Transactional
    public void saveRtStock() {
        StockRecord record = new StockRecord();
        BeanUtils.copyProperties(selected, record);
        record.setId(null);
        record.setRecordDate(new Date());
        SparePart sparePart = selected.getSparePart();
        if (selected.getRecordType() == 1) {
            record.setReturnNumber(OrderNumberGenerator.generateOrderNumber("RT"));
            record.setRecordType(3);
            if (sparePart.getQuantity() < rtQuantity) {
                WebUtil.addErrorMessage("退货数量不得大于可用数量");
                return;
            }
            sparePart.setQuantity(sparePart.getQuantity() - rtQuantity);
        }
        if (selected.getRecordType() == 2) {
            record.setReturnNumber(OrderNumberGenerator.generateOrderNumber("CT"));
            record.setRecordType(4);
            if (selected.getQuantity() < rtQuantity) {
                WebUtil.addErrorMessage("退货数量不得大于可退数量");
                return;
            }
            sparePart.setQuantity(sparePart.getQuantity() + rtQuantity);
        }
        sparePartRepository.save(sparePart);
        record.setQuantity(rtQuantity);
        dao.save(record);
    }

    public void clickSelectAttach(StockRecord record) {
        selected = record;
    }

    public boolean hasBlob(String id) {
        if (org.springframework.util.StringUtils.hasText(id)) {
            List<V2_BlobObject> blobObjects =  blobService.fetchBlobObject(id, V2_BlobObject.BO_TYPE_PART_PIC);
            return !org.springframework.util.CollectionUtils.isEmpty(blobObjects);
        }
        return false;
    }

    public List<V2_BlobObject> getPicList() {
        if (selected != null && StringUtils.isNotEmpty(selected.getSparePart().getId())) {
            picList = blobService.fetchBlobObject(selected.getSparePart().getId(), V2_BlobObject.BO_TYPE_PART_PIC);
        }
        return picList;
    }

}

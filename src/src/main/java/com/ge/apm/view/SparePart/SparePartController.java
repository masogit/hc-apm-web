package com.ge.apm.view.SparePart;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TempFileUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private SimplePartsWarehouseService warehouseService;
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
    private Integer outQuantity;
    @Getter @Setter
    private Date outDate;
    @Getter @Setter
    private String remark;
    @Getter @Setter
    private List<V2_BlobObject> picList;
    @Getter @Setter
    private List<TempFileUtil> tempFile;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SparePartRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        stockRecordRepository =  WebUtil.getBean(StockRecordRepository.class);
        warehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        loginUser = UserContextService.getCurrentUserAccount();
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());

        this.filterBySite = false;
        this.filterByHospital = false;
        picList = new ArrayList<>();
        tempFile = new ArrayList<>();
        initWarehouses();
        loadClinicalDeptList();
    }

    @Override
    protected SparePartRepository getDAO() {
        return dao;
    }

    private void initWarehouses() {
        warehouses = warehouseService.findAllBySiteUidAndUserId(loginUser.getSiteUID());
    }

    private void setFilter() {
        this.searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        this.searchFilters.add(new SearchFilter("quantity", SearchFilter.Operator.GT, 0));
        this.searchFilters.add(new SearchFilter("partsWarehouseId", SearchFilter.Operator.IN, warehouses.stream().map(SimplePartsWarehouse::getId).collect(Collectors.toList())));
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
        picList = new ArrayList<>();
        tempFile = new ArrayList<>();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        partsWarehouseId = selected.getPartsWarehouseId();
    }

    public void clickSelectAttach(SparePart sparePart) {
        selected = sparePart;
    }

    public boolean hasBlob(String id) {
        if (StringUtils.hasText(id)) {
            List<V2_BlobObject> blobObjects =  blobService.fetchBlobObject(id, V2_BlobObject.BO_TYPE_PART_PIC);
            return !CollectionUtils.isEmpty(blobObjects);
        }
        return false;
    }

    public List<V2_BlobObject> getPicList() {
        if (selected != null && StringUtils.hasText(selected.getId())) {
            picList = blobService.fetchBlobObject(selected.getId(), V2_BlobObject.BO_TYPE_PART_PIC);
        }
        return picList;
    }

    public void saveEdit() {
        dao.save(selected);
    }

    public void save() {
        selected.setPartsWarehouseId(partsWarehouseId);
        saveSparePartAndRecord(1);
        partsWarehouseId = null;
    }

    public void outStock(SparePart sparePart) {
        selected = sparePart;
        outQuantity = null;
        outDate = new Date();
        filterClinicalDeptOrg = null;
        remark = null;
    }

    public void outSave() {
        if (outQuantity > selected.getQuantity()) {
            WebUtil.addErrorMessage("出库数量不得大于备件数量");
            return;
        }
        selected.setQuantity(selected.getQuantity() - this.outQuantity);
        saveSparePartAndRecord(2);
    }

    @Transactional
    public void saveSparePartAndRecord(Integer recordType) {
        selected.setTenantUID(loginUser.getTenantUID());
        dao.save(selected);
        tempFile.forEach(this::saveTempFile);
        stockRecordRepository.save(buildStockRecord(recordType));
        tempFile = new ArrayList<>();
    }

    private StockRecord buildStockRecord(Integer recordType) {
        Date recordDate = new Date();
        if(recordType == 2){
            recordDate = outDate;
        }
        return setOutStockRecord(StockRecord.builder()
                .recordType(recordType)
                .sparePartId(selected.getId())
                .recordDate(recordDate)
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
            record.setRemark(remark);
            record.setOutboundNumber(OrderNumberGenerator.generateOrderNumber("C"));
        }
        if (record.getRecordType() == 1) {
            record.setInboundNumber(OrderNumberGenerator.generateOrderNumber("R"));
        }
        return record;
    }

    public Date getInStockDate(String sparePartId){
        StockRecord stockRecord = stockRecordRepository.findInBySparePartId(sparePartId, 1);

        if(stockRecord != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return sdf.parse(sdf.format(stockRecord.getRecordDate()));
            } catch (ParseException e) {
                return null;
            }
        }

        return null;
    }

    public boolean AreThereNnyOutboundRecords(String sparePartId) {
        return stockRecordRepository.findInBySparePartId(sparePartId, 2) != null;
    }

    public void cancel() {
        selected = null;
        partsWarehouseId = null;
        remark = null;
    }

    public void delete() {
        StockRecord stockRecord = stockRecordRepository.findInBySparePartId(selected.getId(), 1);
        if (!ObjectUtils.isEmpty(stockRecord)) {
            stockRecordRepository.delete(stockRecord);
        }
        dao.delete(selected);
    }

    public double totalPrice() {
        return pageUtil.formatDouble2(Optional.ofNullable(selected.getUnitPrice()).orElse(0.0) * Optional.ofNullable(selected.getQuantity()).orElse(0));
    }

    public double totalPrice(double price, Integer qty) {
        return pageUtil.formatDouble2(price * qty);
    }

    public List<String> autoCompleteDeptName(String query) {
        return pageUtil.autoComplete(query, this.clinicalDeptList);
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
    }

    public boolean disableUploadButton_temp() {
        return tempFile.size() > 4;
    }

    public boolean disableUploadButton() {
        return picList.size() > 4;
    }

    public void saveTempFile(TempFileUtil tempFile) {
        V2_BlobObject blobObject = blobService.uploadFile(tempFile.getInputStream(), tempFile.getFileName());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PART_PIC);
            blobObject.setBoUuid(selected.getId());
            blobService.saveBlobObject(blobObject);
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PART_PIC);
            blobObject.setBoUuid(selected.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void tempFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        tempFile.add(TempFileUtil.builder()
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .contents(file.getContents())
                .build());
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void removeTempAttachment(TempFileUtil file) {
        tempFile.remove(file);
    }

}

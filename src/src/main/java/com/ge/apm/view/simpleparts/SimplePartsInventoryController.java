package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.mapper.simpleparts.SimplePartsInventoryMapper;
import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseLocator;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件总库存Controller
 * @package com.ge.apm.view.spareparts
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsInventoryController extends GenericCRUDUUIDController<SimplePartsInventory> {

    private static final Logger logger = LoggerFactory.getLogger(SimplePartsInventoryController.class);


    private static final long serialVersionUID = 3721577362709407159L;

    private SimplePartsInventoryService partsInventoryService;
    private SimplePartsInventoryRepository partsInventoryRepository;
    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private SimplePartsWarehouseUserService partsWarehouseUserService;
    private ConfigService workflowConfigService;
    private UaaService uuaService;
    private SimplePartsConfigService partsConfigService;
    private UserContextService userContextService;
    private SimplePartsItemService partsItemService;

    private SimplePartsInventoryMapper simplePartsInventoryMapper;

    private SimplePartsItemRepository simplePartsItemRepository;

    @Getter
    @Setter
    private int queryType=0; // 0全部  1临界上 2 临界下限
    /**
     * @author csh
     * @createTime 2020/6/11 15:00
     * @description 搜索字符串
     */
    @Setter
    @Getter
    private String oneSearchStr;

    @Override
    protected void init() {
        partsInventoryRepository = WebUtil.getBean(SimplePartsInventoryRepository.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        workflowConfigService = WebUtil.getBean(ConfigService.class);
        partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        partsWarehouseUserService = WebUtil.getBean(SimplePartsWarehouseUserService.class);
        simplePartsInventoryMapper = WebUtil.getBean(SimplePartsInventoryMapper.class);
        simplePartsItemRepository = WebUtil.getBean(SimplePartsItemRepository.class);
    }

    /**
     * @param uid
     * @param type
     * @return void
     * @date 2020/7/22 15:17
     * 根据uid查询仓库名称
     */
    public SimplePartsWarehouse findWarehouseNameById(String id, String type) {
        SimplePartsWarehouse partsWarehouse = partsWarehouseService.findWarehouseNameById(id);
        return partsWarehouse;
    }

    /**
     * @param id
     * @return com.ge.apm.domain.simpleparts.PartsWareHouseLocator
     * @date 2020/7/28 11:26
     * 根据仓位的id查询
     */
    public SimplePartsWareHouseLocator findById(String id) {
        SimplePartsWareHouseLocator partsWareHouseLocator = partsWarehouseLocatorRepository.findById(id);
        return partsWareHouseLocator;
    }

    public String findLocatorName(String id) {
        if (SimplePartsConstant.VIRTUAL_LOCATOR_ID.equals(id)) {
            return SimplePartsConstant.VIRTUAL_LOCATOR_NAME;
        }
        SimplePartsWareHouseLocator partsWareHouseLocator = partsWarehouseLocatorRepository.findById(id);
        return partsWareHouseLocator.getName();
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsInventory> getDAO() {
        return partsInventoryRepository;
    }

    @Override
    protected Page<SimplePartsInventory> loadData(PageRequest pageRequest) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        searchFilters = new ArrayList<SearchFilter>();
        setUserSearchFilters();

        List<String> orgUidList = uuaService.getOrgListByUser(currentUserAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
//        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//
//        Page<SimplePartsInventory> res = partsInventoryRepository.findBySearchFilter(this.searchFilters, pageRequest);
//        return res;
        Boolean isPartsAdmin = userContextService.isPartsAdmin();
        String currentUserUid = currentUserAccount.getUid();
        Map<String, Object> map = new HashMap<>();
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            map.put("name",oneSearchStr);
        }
        if (isPartsAdmin) {
            List<String> partsWarehouses = partsWarehouseUserService.findAllByPartsWarehouseByUserUid(currentUserUid);
            Set<String> warehouses = new HashSet<>(partsWarehouses);

            if (!warehouses.isEmpty()) {
                searchFilters.add(new SearchFilter("partsWarehouseId", SearchFilter.Operator.IN, partsWarehouses));
                map.put("partsWarehouseIds",partsWarehouses);
            } else {
                searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, null));
                map.put("id",null);
            }
        } else {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, null));
            map.put("id",null);
        }
        try {
//            Page<SimplePartsInventory> resultPage = partsInventoryRepository.findBySearchFilter(this.searchFilters, pageRequest);

            map.put("queryType",queryType);
            map.put("tenantUID",currentUserAccount.getTenantUID());

            map.put("pageSize",pageRequest.getPageSize());
            map.put("offset",pageRequest.getPageNumber()*pageRequest.getPageSize());

            List<SimplePartsInventory> partsInventoryList = simplePartsInventoryMapper.findPartsInventoryList(map);
            int count = simplePartsInventoryMapper.findPartsInventoryCount(map);
            List<String> partsItemIds = partsInventoryList.stream().map(SimplePartsInventory::getPartsItemId).collect(Collectors.toList());
            List<SimplePartsItem> simplePartsItems = simplePartsItemRepository.findByIdIn(partsItemIds);
            Map<String, List<SimplePartsItem>> temMap = simplePartsItems.stream().collect(Collectors.groupingBy(SimplePartsItem::getId));
            partsInventoryList.forEach(item->{
                List<SimplePartsItem> partsItems = temMap.get(item.getPartsItemId());
                if(!CollectionUtils.isEmpty(partsItems)){
                    item.setSimplePartsItem(temMap.get(item.getPartsItemId()).get(0));
                }
            });
            setWarningStatus(partsInventoryList);
            Page<SimplePartsInventory> page = new PageImpl<>(partsInventoryList, pageRequest,count);
            return page;
        } catch (Exception e) {
            logger.error("SimplePartsInventoryController loadData error :",e);
            throw new RuntimeException("Failed to fetch parts inventory");
        }
    }

    private void setWarningStatus(List<SimplePartsInventory> simplePartsInventories){
        simplePartsInventories.forEach(item -> item.setWarningStatus());
    }

    @Override
    public void setSiteFilter() {
        if (filterBySite) {
            if (this.searchFilters == null) {
                this.searchFilters = new ArrayList<SearchFilter>();
            }
        }
    }

    private void setUserSearchFilters() {
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public void searchStr() {
        this.searchFilters.clear();
    }

    private PageRequest addRequiredSortById(PageRequest pageRequest) {
        Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
        boolean exists = false;
        while (orders.hasNext()) {
            if ("id".equals(orders.next().getProperty())) {
                exists = true;
                break;
            }
        }
        if (exists) {
            return pageRequest;
        } else {
            Sort sortWithId = new Sort(Sort.Direction.ASC, "id");
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort().and(sortWithId));
        }
        return pageRequest;
    }

    /***
     * 获取ItemCode
     * @param simplePartsInventory
     * @return
     */
    public String getPartsItemCode(SimplePartsInventory simplePartsInventory) {
        SimplePartsItem simplePartsItem = getSimplePartsItem(simplePartsInventory);
        if(simplePartsItem != null){
            return simplePartsItem.getItemCode();
        }
        return null;
    }

    public Double getPartsUnitPrice(SimplePartsInventory simplePartsInventory) {
        SimplePartsItem simplePartsItem = getSimplePartsItem(simplePartsInventory);
        if(simplePartsItem != null && simplePartsItem.getUnitPrice() != null){
            return simplePartsItem.getUnitPrice().doubleValue();
        }
        return 0.00;
    }

    public String getPartsDescription(SimplePartsInventory simplePartsInventory) {
        SimplePartsItem simplePartsItem = getSimplePartsItem(simplePartsInventory);
        if(simplePartsItem != null){
            return simplePartsItem.getPartsDescription();
        }
        return null;
    }

    public SimplePartsItem getSimplePartsItem(SimplePartsInventory simplePartsInventory) {
        if(partsItemService == null){
            partsItemService = WebUtil.getBean(SimplePartsItemService.class);
        }
        return partsItemService.getPartsItem(simplePartsInventory.getPartsItemId(),simplePartsInventory.getName());
    }
}

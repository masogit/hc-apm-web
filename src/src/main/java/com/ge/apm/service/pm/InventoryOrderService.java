package com.ge.apm.service.pm;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class InventoryOrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryOrderService.class);


	 @Autowired
	    private InventoryOrderRepository inventoryOrderDao;
	    @Autowired
	    private InventoryOrderDetailAssetInfoRepository inventoryOrderDetailDao;
//	    @Autowired
//	    private InventoryOrderIssueRepository inventoryOrderIssueRepository;
//	    @Autowired
//	    private InventoryChecklistRepository inventoryChecklistRepository;
	    @Autowired
	    private AssetInfoRepository assetInfoDao;
	    @Autowired
	    private OrgInfoRepository orgInfoDao;
	    @Autowired
	    private I18nMessageRepository i18nMessageDao;
//	    @Autowired
//	    private ObjectHubRepository objectHubRepository;

	    private final Integer INVENTORY_NOT_FINISHED = 0; // 未盘点
	    private final Integer INVENTORY_FINISHED = 1; // 已盘点色弱
	    private final Integer INVENTORY_UNDOCUMENTED = 2; // 已盘点

//	    public Boolean inventoryAssetByQrCode(String qrCode, UserAccount user) {
//	        List<InventoryOrderDetail> detailList = inventoryOrderDetailDao.findByQrCodeAndTenant(qrCode, user.getTenantUID());
//	        if (detailList == null || detailList.isEmpty()) {
//	            LOGGER.info("No asset found by QR code: " + qrCode);
//	            return false;
//	        }
//
//	        try {
//	            InventoryOrderDetail detail = detailList.get(0);
//	            detail.setStatus(INVENTORY_FINISHED);
//	            inventoryOrderDetailDao.save(detail);
//	            LOGGER.info("Inventory asset [" + qrCode + "] done.");
//	            return true;
//	        } catch (Exception e) {
//	            LOGGER.info("Inventory failed: " + qrCode);
//	            LOGGER.error(e.getMessage());
//	            return false;
//	        }
//	    }

//	    public InventoryOrder createInventoryOrder(InventoryOrderForm inventoryOrderForm, UserAccount user) {
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//	        Date planStartTime;
//	        Date planEndTime;
//
//	        try {
//	            planStartTime = format.parse(inventoryOrderForm.getPlanStartTime());
//	            planEndTime = format.parse(inventoryOrderForm.getPlanEndTime());
//	        } catch (Exception e) {
//	            LOGGER.error(e.getMessage());
//	            return null;
//	        }
//
//	        UserAccount ua = user;
//	        InventoryOrder inventoryOrder = new InventoryOrder();
//	        inventoryOrder.setSiteId(user.getSiteId());
//	        inventoryOrder.setHospitalId(user.getHospitalId());
//	        inventoryOrder.setPlanStartTime(planStartTime);
//	        inventoryOrder.setPlanEndTime(planEndTime);
//	        inventoryOrder.setCreatedBy(user.getLoginName());
//	        inventoryOrder.setLastModifiedBy(user.getLoginName());
//	        inventoryOrder.setTenantUID(ua.getTenantUID());
//	        inventoryOrder.setInstitutionUID(ua.getInstitutionUID());
//	        inventoryOrder.setHospitalUID(ua.getHospitalUID());
//	        inventoryOrder.setSiteUID(ua.getSiteUID());
//	        inventoryOrder.setIsFinished(Boolean.FALSE);
//
//	        inventoryOrder.setAssigneeOrg(JSON.toJSONString(
//	            inventoryOrderForm.getOrgSelections().stream()
//	                .filter(s -> (Boolean) s.get("selected"))
//	                .collect(Collectors.toList())));
//	        List<String> clinicalDeptUIDs = inventoryOrderForm.getOrgSelections().stream()
//	            .filter(s -> (Boolean) s.get("selected"))
//	            .map(map -> (String) map.get("uid"))
//	            .collect(Collectors.toList());
//	        List<InventoryOrderDetail> details = assetInfoDao.findAssetsToInventory(clinicalDeptUIDs)
//	            .parallelStream()
//	            .map(assetInfo -> {
//	                InventoryOrderDetail detail = new InventoryOrderDetail();
//	                detail.setAssetInfo(assetInfo);
//	                detail.setStatus(INVENTORY_NOT_FINISHED);
//	                return detail;
//	            }).collect(Collectors.toList());
//
//	        inventoryOrder.setInventoryOrderDetailList(details);
//	        return inventoryOrderDao.save(inventoryOrder);
//
//	    }

//	    public InventoryOrderDetail createInventoryOrderDetail(InventoryOrderDetailForm inventoryOrderDetailForm, UserAccount user) {
//
//	        InventoryOrder inventoryOrder = this.getLastInventoryOrder(user);
//
//	        InventoryOrderDetail inventoryOrderDetail = null;
//
//	        if (inventoryOrder != null) {
//	            inventoryOrderDetail = new InventoryOrderDetail();
//	            inventoryOrderDetail.setInventoryOrderId(inventoryOrderDao.findInventoryOrderByClinicalDept(inventoryOrderDetailForm.getOrgId()));
//	            inventoryOrderDetail.setSiteId(user.getSiteId());
//	            inventoryOrderDetail.setHospitalId(user.getHospitalId());
//	            inventoryOrderDetail.setOrgId(inventoryOrderDetailForm.getOrgId());
//	            if (inventoryOrderDetailForm.getOrgId() != null) {
//	                OrgInfo orgInfo = orgInfoDao.findById(inventoryOrderDetailForm.getOrgId());
//	                if (orgInfo != null) {
//	                    inventoryOrderDetail.setOrgName(orgInfo.getName());
//	                }
//	                inventoryOrderDetail.setClinicalDept(orgInfo);
//	            }
//	            inventoryOrderDetail.setAssetGroup(inventoryOrderDetailForm.getAssetGroup());
//	            if (inventoryOrderDetailForm.getAssetGroup() != null) {
//	                I18nMessage i18nMessage = i18nMessageDao.getI18nMessageByTypeAndKey("assetGroup", inventoryOrderDetailForm.getAssetGroup().toString());
//	                if (i18nMessage != null) {
//	                    inventoryOrderDetail.setAssetGroupName(i18nMessage.getValueZh());
//	                }
//	            }
//	            inventoryOrderDetail.setQrCode(inventoryOrderDetailForm.getQrCode());
//	            inventoryOrderDetail.setDepartNum(inventoryOrderDetailForm.getDepartNum());
//	            inventoryOrderDetail.setComment(inventoryOrderDetailForm.getComment());
//	            inventoryOrderDetail.setStatus(2);
//
//	            objectHubRepository.save(inventoryOrderDetailForm.getAttachments());
//	            inventoryOrderDetailDao.save(inventoryOrderDetail);
//	        }
//
//	        return inventoryOrderDetail;
//	    }

//	    public InventoryOrder finishInventoryOrder(InventoryOrderForm inventoryOrderForm, UserAccount user) {
//
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//	        Date startTime = null;
//	        Date endTime = null;
//
//	        try {
//	            startTime = format.parse(inventoryOrderForm.getStartTime());
//	            endTime = format.parse(inventoryOrderForm.getEndTime());
//	        } catch (Exception e) {
//	            LOGGER.error(e.getLocalizedMessage());
//	            return null;
//	        }
//	        InventoryOrder inventoryOrder = inventoryOrderDao.findById(inventoryOrderForm.getOrderId());
//
//	        inventoryOrder.setLastModifiedBy(user.getName());
//	        inventoryOrder.setStartTime(startTime);
//	        inventoryOrder.setEndTime(endTime);
//	        inventoryOrder.setIsFinished(Boolean.TRUE);
//
//	        return inventoryOrderDao.save(inventoryOrder);
//	    }

	    private void setAssetInventoryStatus(InventoryOrder inventoryOrder, Integer inventoryStatus) {
	        List assigneeOrgList = JSON.parseArray(inventoryOrderDao.findById(inventoryOrder.getId()).getAssigneeOrg());
	        List<String> clinicalDeptIdList = new ArrayList<>();
	        for (Object obj : assigneeOrgList) {
	            Map org = (Map) obj;
	            clinicalDeptIdList.add((String) org.get("uid"));
	        }
	        inventoryOrderDao.setAssetInventoryStatus(inventoryStatus, clinicalDeptIdList);
	    }

	    public Boolean isHaveValidInventoryOrder(UserAccount user) {
	        InventoryOrder inventoryOrder = this.getLastInventoryOrder(user);
	        if (inventoryOrder != null) {
	            Date startDate = inventoryOrder.getStartTime();
	            Date endDate = inventoryOrder.getEndTime();

	            Instant now = new Date().toInstant();
	            ZoneId zone = ZoneId.systemDefault();
	            LocalDateTime localDateTime = LocalDateTime.ofInstant(now, zone);
	            LocalDate localDate = localDateTime.toLocalDate();

	            Instant startDateInstant = startDate.toInstant();
	            LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDateInstant, zone);
	            LocalDate startLocalDate = startLocalDateTime.toLocalDate();

	            Instant endDateInstant = endDate.toInstant();
	            LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endDateInstant, zone);
	            LocalDate endLocalDate = endLocalDateTime.toLocalDate();

	            if (localDate.isBefore(endLocalDate.plusDays(1)) && localDate.isAfter(startLocalDate.plusDays(-1))) {
	                return Boolean.TRUE;
	            }
	        }
	        return Boolean.FALSE;
	    }

	    public Boolean isHaveNoFinishedInventoryOrder(UserAccount user) {
	        InventoryOrder inventoryOrder = this.getLastInventoryOrder(user);

	        if (inventoryOrder != null && !inventoryOrder.getIsFinished()) {
	            return Boolean.TRUE;
	        } else {
	            return Boolean.FALSE;
	        }
	    }

	    public InventoryOrder getLastInventoryOrder(UserAccount user) {
	        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
	        Pageable page = new PageRequest(0, 1, sort);
	        Page<InventoryOrder> results = inventoryOrderDao.findBySpecification((Root<InventoryOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder.and(
	            Optional.ofNullable(user).map(u -> builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())).get()
	        ), page);
	        return results.getContent().isEmpty() ? null : results.getContent().get(0);
	    }

	    public Page getLastInventoryOrderDetailPage(UserAccount user, Integer page, Integer pageSize) {

	        InventoryOrder inventoryOrder = this.getLastInventoryOrder(user);

	        Page tempPage = null;
	        if (inventoryOrder != null) {
	            tempPage = this.getInventoryOrderDetailPage(inventoryOrder.getId(), page, pageSize);
	        }

	        return tempPage;
	    }

	    public Page getInventoryOrderDetailPage(String inventoryOrderId, Integer page, Integer pageSize) {

	        PageRequest pageRequest = this.buildPageRequest(page, pageSize);
	        List<SearchFilter> searchFilters = new ArrayList<>();

	        SearchFilter hospitalFilter = new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, inventoryOrderId);
	        searchFilters.add(hospitalFilter);

	        Page tempPage = inventoryOrderDetailDao.findBySearchFilter(searchFilters, pageRequest);

	        return tempPage;
	    }

//	    public InventoryOrderDetail getInventoryOrderDetail(String inventoryOrderDetailId) {
//	        return inventoryOrderDetailDao.findById(inventoryOrderDetailId);
//	    }

	    //构建PageRequest
	    private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
	        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
	        return new PageRequest(pageNumber, pagzSize, sort);
	    }

	    public void updateAssetInventoryStatusByBatch(List<Integer> assetIds, UserAccount user) {
	        assetInfoDao.updateInventoryStatusAndIdIn(assetIds, INVENTORY_FINISHED);
	    }

//	    public InventoryIssue createInventoryIssue(InventoryIssueForm form, UserAccount user) throws Exception {
//	        InventoryIssue issue = new InventoryIssue();
//	        issue.setAssetInfo(Optional.ofNullable(form)
//	            .map(InventoryIssueForm::getAssetId)
//	            .map(assetId -> assetInfoDao.findById(assetId))
//	            .orElseThrow(() -> new Exception("资产ID无效。")));
//	        issue.setComment(Optional.ofNullable(form)
//	            .map(InventoryIssueForm::getComment)
//	            .orElse(null));
//	        issue.setInventoryOrderId(inventoryOrderDao.findInventoryOrderByAsset(form.getAssetId()));
//	        Optional.ofNullable(form)
//	            .map(InventoryIssueForm::getCheckItemIdList)
//	            .map(idList -> {
//	                issue.getInventoryCheckLists().addAll(getCheckItemsByIdList(idList));
//	                return issue;
//	            });
//	        AssetInfo asset = assetInfoDao.findById(form.getAssetId());
//	        OrgInfo clinicalDept = orgInfoDao.findByuid(asset.getClinicalDeptUID());
//	        InventoryIssue inserted = Optional.ofNullable(user)
//	            .map(UserAccount::getUserAccount)
//	            .map(u -> {
//	                issue.setHospitalUID(u.getHospitalUID());
//	                issue.setSiteUID(u.getSiteUID());
//	                issue.setInstitutionUID(u.getInstitutionUID());
//	                issue.setCreatedBy(u.getName());
//	                issue.setCreatedDate(ZonedDateTime.now());
//	                issue.setClinicalDept(clinicalDept);
//	                return issue;
//	            })
//	            .map(i -> inventoryOrderIssueRepository.save(i))
//	            .get();
//	        return inserted;
//	    }

//	    public InventoryIssue getInventoryIssueById(String id, UserAccount user) {
//	        return inventoryOrderIssueRepository.findOne((Root<InventoryIssue> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder.and(
//	            Optional.ofNullable(user).map(UserAccount::getUserAccount).map(u -> builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())).get(),
//	            Optional.of(id).map(i -> builder.equal(root.get("id"), i)).get()
//	        ));
//	    }

//	    public InventoryIssue resolveInventoryIssue(String id, UserAccount user) {
//	        InventoryIssue issue = getInventoryIssueById(id, user);
//	        issue.setIsSolved(true);
//	        issue.setLastModifiedBy(user.getName());
//	        issue.setLastModifiedDate(ZonedDateTime.now());
//	        return inventoryOrderIssueRepository.save(issue);
//	    }

//	    public Page<InventoryIssue> getInventoryIssueByPage(Integer page, Integer pageSize, UserAccount user) {
//	        PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Sort.Direction.ASC, "id"));
//	        Specification<InventoryIssue> spec = (Root<InventoryIssue> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder.and(
//	            Optional.ofNullable(user).map(UserAccount::getUserAccount).map(u -> builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())).get()
//	        );
//	        return inventoryOrderIssueRepository.findBySpecification(spec, pageRequest);
//	    }

//	    public Page<InventoryIssue> getInventoryIssues(String orderId, String clinicalDeptUID, Integer page, Integer pageSize) {
//	        PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "createdDate"));
//	        Specification<InventoryIssue> spec = (Root<InventoryIssue> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
//	            List<Predicate> predicates = new ArrayList();
//	            predicates.add(builder.equal(root.get("inventoryOrderId"), orderId));
//	            OrgInfo clinicalDept = orgInfoDao.findByuid(clinicalDeptUID);
//	            predicates.add(builder.equal(root.get("clinicalDept"), clinicalDept));
//	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
//	        };
//	        return inventoryOrderIssueRepository.findBySpecification(spec, pageRequest);
//	    }

//	    public List<InventoryCheckList> getChecklist() {
//	        return (List<InventoryCheckList>) inventoryChecklistRepository.findAll();
//	    }

//	    public List<InventoryIssue> getInventoryIssueList(UserAccount user) {
//	        return inventoryOrderIssueRepository.findBySpecification((Root<InventoryIssue> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder.and(
//	            Optional.ofNullable(user).map(UserAccount::getUserAccount).map(u -> builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())).get()
//	        ));
//	    }

//	    public List<InventoryCheckList> getCheckItemsByIdList(List<String> idList) {
//	        return inventoryChecklistRepository.findAll((Root<InventoryCheckList> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder.and(
//	            Optional.ofNullable(idList).map(i -> (root.get("id").in(i))).get()
//	        ));
//	    }

	    public List getOrgsForGrouping(UserAccount user) {
	        List<Map> userClinicalDepts = findClinicalDeptMapByUser(user);
	        for (String json : inventoryOrderDao.findByInventoryOrder(user.getTenantUID())) {
	            List<Map> orgList = JSON.parseArray(json, Map.class);
	            userClinicalDepts.removeAll(orgList);
	        }
	        return userClinicalDepts;
	    }


	    public List<InventoryOrder> getAllInventoryOrder(UserAccount user, Boolean isFinished) {
	        Sort.Order orderByFinished = new Sort.Order("isFinished");
	        Sort.Order orderByCreatedDate = new Sort.Order(Sort.Direction.DESC, "createdDate");
	        Sort sort = new Sort(orderByFinished, orderByCreatedDate);
	        List<InventoryOrder> orderListByTenant = inventoryOrderDao.findBySpecification((Root<InventoryOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            Optional.ofNullable(user).map(u -> predicates.add(builder.equal(root.get("tenantUID"), u.getTenantUID())));
	            Optional.ofNullable(isFinished).map(u -> predicates.add(builder.equal(root.get("isFinished"), isFinished)));
	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
	        }, sort);
	        List<OrgInfo> userClinicalDepts = findClinicalDeptsByUser(user);
	        List<Integer> userClinicalDeptIds = userClinicalDepts.stream().map(OrgInfo::getId).collect(Collectors.toList());
	        return orderListByTenant.parallelStream().filter(o -> {
	            List<Integer> orderClinicalDeptIds = Optional.of(o)
	                .map(InventoryOrder::getAssigneeOrg)
	                .map(json -> JSON.parseArray(json, Map.class))
	                .orElse(new ArrayList<>())
	                .stream()
	                .map(map -> (Integer) map.get("id")).collect(Collectors.toList());
	            List<Integer> temp = new ArrayList<>();
	            temp.addAll(userClinicalDeptIds);
	            temp.retainAll(orderClinicalDeptIds);
	            return !temp.isEmpty();
	        }).collect(Collectors.toList());
	    }

	    public List<OrgInfo> findClinicalDeptsByUser(UserAccount user) {
	        return orgInfoDao.findBySpecification((Root<OrgInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            Optional.ofNullable(user).map(u -> predicates.add(builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())));
	            predicates.add(builder.equal(root.get("orgType"), 4));
	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
	        });
	    }

	    public List<Map<String, String>> getOverview(String orderId) {
	        InventoryOrder order = inventoryOrderDao.findById(orderId);
	        List assigneeOrgList = JSON.parseArray(order.getAssigneeOrg());
	        List<Map<String, String>> results = new ArrayList<>();
	        for (Object obj : assigneeOrgList) {
	            Map selection = (Map) obj;
	            StringBuilder overview = new StringBuilder();
	            overview.append(" 未盘点[ ");
	            overview.append(inventoryOrderDao.countUninventoriedAsset((String) selection.get("uid"), orderId));
	            overview.append(" ]");
	            overview.append(" 已盘点[ ");
	            overview.append(inventoryOrderDao.countInventoriedAsset((String) selection.get("uid"), orderId));
	            overview.append(" ]");
	            overview.append(" 无台账[ ");
	            overview.append(inventoryOrderDao.countUndocumentedAsset((String) selection.get("uid"), orderId));
	            overview.append(" ]");
	            overview.append(" 有问题[ ");
	            overview.append(inventoryOrderDao.countInventoryIssue((String) selection.get("uid"), orderId));
	            overview.append(" ]");
	            Map<String, String> data = new HashMap<>();
	            data.put("orderId", orderId);
	            data.put("orgUID", (String) selection.get("uid"));
	            data.put("orgName", (String) selection.get("orgName"));
	            data.put("overview", overview.toString());
	            results.add(data);
	        }

	        return results;
	    }

	    public Boolean isLatestOrder(String orderId, UserAccount user) {
	        InventoryOrder newestOrder = getLastInventoryOrder(user);
	        return newestOrder == null ? true : orderId.equals(newestOrder.getId());
	    }

	    public InventoryOrder getCurrentOpenOrder(UserAccount user) {
	        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
	        List<InventoryOrder> openOrders = inventoryOrderDao.findBySpecification((Root<InventoryOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            predicates.add(builder.equal(root.get("isFinished"), false));
	            Optional.ofNullable(user).map(u -> predicates.add(builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())));
	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
	        }, sort);
	        return openOrders.isEmpty() ? null : openOrders.get(0);
	    }

//	    public Page<InventoryOrderDetail> getOrderDetails(String orderId, String clinicalDeptUID, Integer page, Integer pageSize) {
//	        PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "createdDate"));
//	        Specification<InventoryOrderDetail> spec = (Root<InventoryOrderDetail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
//	            List<Predicate> predicates = new ArrayList<>();
//	            predicates.add(builder.equal(root.get("inventoryOrderId"), orderId));
//	            predicates.add(builder.equal(root.get("orgId"), orgInfoDao.findByUid(clinicalDeptUID).getId()));
//	            predicates.add(builder.equal(root.get("status"), INVENTORY_UNDOCUMENTED));
//	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
//	        };
//	        return inventoryOrderDetailDao.findBySpecification(spec, pageRequest);
//	    }

	    public List getInventoryClinicalDepts(UserAccount user) {
	        List<Map> userClinicalDepts = findClinicalDeptMapByUser(user);
	        List<InventoryOrder> orderListByTenant = inventoryOrderDao.findBySpecification((Root<InventoryOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            Optional.ofNullable(user).map(u -> predicates.add(builder.equal(root.get("tenantUID"), u.getTenantUID())));
	            Optional.ofNullable(user).map(u -> predicates.add(builder.equal(root.get("isFinished"), false)));
	            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
	        });
	        List<Map> inventoryClinicalDepts = new ArrayList<>();
	        for (InventoryOrder order : orderListByTenant) {
	            inventoryClinicalDepts.addAll(Optional.ofNullable(order)
	                .map(InventoryOrder::getAssigneeOrg)
	                .map(json -> JSON.parseArray(json, Map.class))
	                .orElse(new ArrayList<>()));
	        }
	        userClinicalDepts.retainAll(inventoryClinicalDepts);
	        return userClinicalDepts;
	    }

//	    public InventoryOrderDetail inventoryByDepartNum(String departNum, UserAccount user) {
//	        List<InventoryOrderDetail> detailList = inventoryOrderDetailDao.findByDepartNumAndTenant(departNum, user.getTenantUID());
//	        if (detailList == null || detailList.isEmpty()) {
//	            LOGGER.info("No asset found by departNum: " + departNum);
//	            return null;
//	        }
//
//	        try {
//	            InventoryOrderDetail detail = detailList.get(0);
//	            detail.setStatus(INVENTORY_FINISHED);
//	            inventoryOrderDetailDao.save(detail);
//	            LOGGER.info("Inventory asset [" + departNum + "] done.");
//	            return detail;
//	        } catch (Exception e) {
//	            LOGGER.info("Inventory failed: " + departNum);
//	            LOGGER.error(e.getMessage());
//	            return null;
//	        }
//	    }

	    List<Map> findClinicalDeptMapByUser(UserAccount user) {
	        return findClinicalDeptsByUser(user).stream().map(orgInfo -> {
	            Map map = new HashMap();
	            map.put("id", orgInfo.getId());
	            map.put("uid", orgInfo.getUid());
	            map.put("orgName", orgInfo.getParentOrg() == null ? orgInfo.getName() : orgInfo.getParentOrg().getName() + " / " + orgInfo.getName());
	            map.put("selected", true);
	            return map;
	        }).collect(Collectors.toList());
	    }

	    public Page<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID, Integer status, Integer page, Integer pageSize) {
	        PageRequest pageRequest = new PageRequest(page, pageSize);
	        return inventoryOrderDetailDao.getInventoryAssetList(orderId, clinicalDeptUID, status, pageRequest);
	    }
	    
	    public List<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID, Integer status) {
	        return inventoryOrderDetailDao.getInventoryAssetList(orderId, clinicalDeptUID, status);
	    }
	    
	    public List<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID) {
	        return inventoryOrderDetailDao.getInventoryAssetList(orderId, clinicalDeptUID);
	    }
}

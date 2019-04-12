package com.ge.apm.service.inventory;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.PinYinComparator;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Service
public class InventoryOrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryOrderService.class);

	@Autowired
	private InventoryOrderRepository inventoryOrderDao;
	@Autowired
	private InventoryOrderDetailAssetInfoRepository inventoryOrderDetailDao;
	// @Autowired
	// private InventoryOrderIssueRepository inventoryOrderIssueRepository;
	// @Autowired
	// private InventoryChecklistRepository inventoryChecklistRepository;
	@Autowired
	private AssetInfoRepository assetInfoDao;
	@Autowired
	private OrgInfoRepository orgInfoDao;
	@Autowired
	private I18nMessageRepository i18nMessageDao;
	// @Autowired
	// private ObjectHubRepository objectHubRepository;

	private final Integer INVENTORY_NOT_FINISHED = 0; // 未盘点
	private final Integer INVENTORY_FINISHED = 1; // 已盘点色弱
	private final Integer INVENTORY_UNDOCUMENTED = 2; // 已盘点
	private static final String ORG_NAME = "orgName";
	private List<InventoryOrder> listUserInventoryOrder = new ArrayList<InventoryOrder>();

	public void init() {
		listUserInventoryOrder.clear();
	}

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
		Page<InventoryOrder> results = inventoryOrderDao.findBySpecification((Root<InventoryOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> builder
				.and(Optional.ofNullable(user).map(u -> builder.equal(root.get(u.loadOrgColumnName()), u.loadOrgUid())).get()), page);
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

	// 构建PageRequest
	private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		return new PageRequest(pageNumber, pagzSize, sort);
	}

	public void updateAssetInventoryStatusByBatch(List<Integer> assetIds, UserAccount user) {
		assetInfoDao.updateInventoryStatusAndIdIn(assetIds, INVENTORY_FINISHED);
	}

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
			List<Integer> orderClinicalDeptIds = Optional.of(o).map(InventoryOrder::getAssigneeOrg).map(json -> JSON.parseArray(json, Map.class)).orElse(new ArrayList<>()).stream()
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
			inventoryClinicalDepts.addAll(Optional.ofNullable(order).map(InventoryOrder::getAssigneeOrg).map(json -> JSON.parseArray(json, Map.class)).orElse(new ArrayList<>()));
		}
		userClinicalDepts.retainAll(inventoryClinicalDepts);
		return userClinicalDepts;
	}

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

	public Page<InventoryOrder> getPageData(PageRequest pageRequest, String tenantUid, List<SearchFilter> listSearchFilter, List<String> listAccessClinical) {
		Page<InventoryOrder> pageRe = null;
		int page = pageRequest.getPageNumber();
		int pageSize = pageRequest.getPageSize();
		List<InventoryOrder> listFilterInventoryOrder = new ArrayList<InventoryOrder>();
		List<InventoryOrder> listSmallRe = new ArrayList<InventoryOrder>();
		if (listUserInventoryOrder.size() > 0) {
		} else {
			List<InventoryOrder> listInventoryOrder = inventoryOrderDao.findByTenantUid(tenantUid);
			if (listInventoryOrder != null && listInventoryOrder.size() > 0) {
				for (int i = 0; i < listInventoryOrder.size(); ++i) {
					InventoryOrder tempOrder = listInventoryOrder.get(i);
					for (String clinicalUid : listAccessClinical) {
						if (tempOrder.getAssigneeOrg().contains(clinicalUid)) {
							listUserInventoryOrder.add(tempOrder);
							break;
						}
					}
				}
			}
		}
		try {
			if (listSearchFilter.size() > 0 && listUserInventoryOrder.size() > 0) {
				for (int i = 0; i < listUserInventoryOrder.size(); ++i) {
					InventoryOrder tempOrder = listUserInventoryOrder.get(i);
					boolean match = true;
					for (SearchFilter filter : listSearchFilter) {
						String fieldName = filter.fieldName;
						Object value = filter.value;
						if (value == null) {
							continue;
						}
						Object tempValue = getFieldValue(tempOrder, fieldName);
						if (tempValue == null) {
							match = false;
						} else if (filter.operator.equals(SearchFilter.Operator.LIKE)) {
							if (!((String) tempValue).contains((String) value)) {
								match = false;
							}
						} else if (filter.operator.equals(SearchFilter.Operator.EQ)) {
							if (!((Boolean) tempValue).equals((Boolean) value)) {
								match = false;
							}
						} else if (filter.operator.equals(SearchFilter.Operator.GTE)) {
							if (((Date) tempValue).compareTo((Date) value) < 0) {
								match = false;
							}
						} else if (filter.operator.equals(SearchFilter.Operator.LTE)) {
							if (((Date) tempValue).compareTo((Date) value) > 0) {
								match = false;
							}
						} else if (filter.operator.equals(SearchFilter.Operator.GT)) {
							if (((Date) tempValue).compareTo((Date) value) <= 0) {
								match = false;
							}
						} else if (filter.operator.equals(SearchFilter.Operator.LT)) {
							if (((Date) tempValue).compareTo((Date) value) >= 0) {
								match = false;
							}
						}
					}
					if (match) {
						listFilterInventoryOrder.add(tempOrder);
					}
				}
			} else {
				listFilterInventoryOrder = listUserInventoryOrder;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		int size = listFilterInventoryOrder.size();
		int pre = page * pageSize;
		int last = (page + 1) * pageSize;
		for (int i = pre; i < size && i < last; ++i) {
			listSmallRe.add(listFilterInventoryOrder.get(i));
		}
		pageRe = new PageImpl<InventoryOrder>(listSmallRe, pageRequest, size);
		return pageRe;
	}

	private Object getFieldValue(InventoryOrder order, String fieldName) {
		try {
			Field field = InventoryOrder.class.getDeclaredField(fieldName);
			boolean access = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(order);
			field.setAccessible(access);
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	// public List<InventoryOrder> getMoreListInventoryOrder(String
	// tenantUid,int page,int pageSize){
	// return
	// inventoryOrderDao.findByTenantUid(tenantUid,pageSize,page*pageSize);
	// }

	public List<String> getInventoryClinicalDeptUID(String orderId, boolean sort, int type) {

		List<String> listClinicalDeptName = new ArrayList<String>();
		if (StringUtils.isEmpty(orderId)) {
			return listClinicalDeptName;
		}
		InventoryOrder order = inventoryOrderDao.findById(orderId);
		String assignee_org = order.getAssigneeOrg();
		JSONArray array = new JSONArray(assignee_org);
		JSONObject obj = null;
		String temp = "";
		for (int i = 0; i < array.length(); ++i) {
			obj = array.getJSONObject(i);
			temp = obj.getString(ORG_NAME);
			if (type == 0) {
				temp = temp.substring(temp.lastIndexOf("/ ") + 1, temp.length()).trim();
			}

			if (!listClinicalDeptName.contains(temp)) {
				listClinicalDeptName.add(temp);
			}
		}
		if (sort) {
			listClinicalDeptName.sort(new PinYinComparator());
		}
		return listClinicalDeptName;
	}

}

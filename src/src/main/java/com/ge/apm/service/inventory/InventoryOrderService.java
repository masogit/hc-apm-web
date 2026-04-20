package com.ge.apm.service.inventory;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
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
	@Autowired
	private AssetInfoRepository assetInfoDao;
	@Autowired
	private OrgInfoRepository orgInfoDao;
	@Autowired
	private UserAccountRepository userAccountRepository;
	@Autowired
	private AssetTypeGroupRepository assetTypeGroupRepository;
	@Autowired
	private ConfigItemValueRepository configItemValueRepository;

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
    public List<String> getInventoryOrderData(String tenantUid, List<String> listAccessClinical,Boolean isFinished) {
        List<String> orderIdList = new ArrayList<>();
        List<InventoryOrder> listInventoryOrder = inventoryOrderDao.findByTenantUIDAndIsFinished(tenantUid,isFinished);
        if (listInventoryOrder != null && !listInventoryOrder.isEmpty()) {
			for (InventoryOrder tempOrder : listInventoryOrder) {
				for (String clinicalUid : listAccessClinical) {
					if (tempOrder.getAssigneeOrg().contains(clinicalUid)) {
						orderIdList.add(tempOrder.getId());
						break;
					}
				}
			}
        }
        return orderIdList;
    }
	public Page<InventoryOrder> getPageData(PageRequest pageRequest, UserAccount currentUser, List<SearchFilter> listSearchFilter, List<String> listAccessClinical) {
		Page<InventoryOrder> pageRe = null;
		listUserInventoryOrder.clear();
		int page = pageRequest.getPageNumber();
		int pageSize = pageRequest.getPageSize();
		List<InventoryOrder> listFilterInventoryOrder = new ArrayList<>();
		List<InventoryOrder> listSmallRe = new ArrayList<>();
		List<InventoryOrder> listInventoryOrder = inventoryOrderDao.findByTenantUid(currentUser.getTenantUID());
		if (listInventoryOrder != null && !listInventoryOrder.isEmpty()) {
			for (InventoryOrder tempOrder : listInventoryOrder) {
				for (String clinicalUid : listAccessClinical) {
					if (tempOrder.getAssigneeOrg().contains(clinicalUid)) {
						listUserInventoryOrder.add(tempOrder);
						break;
					}
				}
			}
		}
		try {
			if (!listSearchFilter.isEmpty() && !listUserInventoryOrder.isEmpty()) {
				for (InventoryOrder tempOrder : listUserInventoryOrder) {
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
			LOGGER.error(e.getMessage(), e);
		}
		if (currentUser.isEnableAssetTypeGroup()) {
			if (currentUser.getAssetTypeGroupList().isEmpty()) {
				return new PageImpl<>(listSmallRe, pageRequest, 0);
			}
			Set<String> encodeds = currentUser.getAssetTypeGroupMap().keySet();
			listFilterInventoryOrder = listFilterInventoryOrder.stream().filter(t -> ObjectUtil.isNotEmpty(t.getAssetType())).filter(t ->  {
				for (String encoded : encodeds) {
					if (encoded.startsWith(t.getAssetType())) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList());
			Set<String> assetTypes = listFilterInventoryOrder.stream().map(InventoryOrder::getAssetType).collect(Collectors.toSet());
			List<AssetTypeGroup> assetTypeGroups = assetTypeGroupRepository.findByTenantUidAndEncodedIn(currentUser.getTenantUID(), assetTypes);
			Map<String, String> assetTypeMap = assetTypeGroups.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, AssetTypeGroup::getName));
			for (InventoryOrder order : listFilterInventoryOrder) {
				List<Map> clinicalDeptList = JSON.parseArray(order.getAssigneeOrg(), Map.class);
				StringBuilder clinicalDeptNames = new StringBuilder(assetTypeMap.get(order.getAssetType()) + " ");
				for (Map map : clinicalDeptList) {
					clinicalDeptNames.append(map.get("orgName").toString().replace(assetTypeMap.get(order.getAssetType()) + " / ", "")).append(" ");
				}
				order.setClinicalDeptLabel(clinicalDeptNames.toString());
			}
		}
		int size = listFilterInventoryOrder.size();
		int pre = page * pageSize;
		int last = (page + 1) * pageSize;
		for (int i = pre; i < size && i < last; ++i) {
			listSmallRe.add(listFilterInventoryOrder.get(i));
		}
		pageRe = new PageImpl<>(listSmallRe, pageRequest, size);
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

	public List<String> getInventoryClinicalDeptUIDAll( boolean sort, int type) {

		List<String> listClinicalDeptName = new ArrayList<String>();
        List<InventoryOrder> inventoryOrderList = inventoryOrderDao.find();
		if(!inventoryOrderList .isEmpty()){
            for ( InventoryOrder order:inventoryOrderList) {
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
            }
        }

		return listClinicalDeptName;
	}

	public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
		for (SearchFilter searchFilter :searchFilters) {
			String fieldName = searchFilter.fieldName;
			switch (fieldName) {
				case "name":
					param.put("name", searchFilter.value);
					break;
				case "clinicalDeptName":
					param.put("clinicalDeptName", searchFilter.value);
					break;
				case "qrCode":
					param.put("qrCode", searchFilter.value);
					break;
				case "departNum":
					param.put("departNum", searchFilter.value);
					break;
				case "functionType":
					param.put("functionType", searchFilter.value);
					break;
				case "financingNum":
					param.put("financingNum", searchFilter.value);
					break;
				case "assetStatus":
					param.put("assetStatus", Integer.parseInt(searchFilter.value.toString()));
					break;
				case "locationName":
					param.put("locationName", searchFilter.value);
					break;
				case "isSolved":
					boolean isSolved = Boolean.parseBoolean(searchFilter.value.toString());
					if (isSolved) {
						param.put("isSolved", "是");
					} else {
						param.put("isSolved", "否");
					}
					break;
				case "lastModifiedBy":
					List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(UserContextService.getCurrentUserAccount().getTenantUID(), searchFilter.value.toString());
					List<String> loginNamList = userAccountList.stream().map(UserAccount::getLoginName).collect(Collectors.toList());
					if (ObjectUtil.isNotEmpty(loginNamList)) {
						param.put("lastModifiedBy", loginNamList);
					}
					break;
				case "status":
					param.put("status", Integer.parseInt(searchFilter.value.toString()));
					break;
				case "assetOwnerName":
					param.put("assetOwnerName", searchFilter.value);
					break;
				case "lastModifiedDate":
					if (searchFilter.operator.name().equals("GTE")) {
						param.put("startDate", searchFilter.value);
					} else {
						param.put("endDate", searchFilter.value);
					}
					break;
				default:
					break;
			}
		}
	}

	public List<Map<String, Object>> covQueryCondition(List<SearchFilter> searchFilters, List<Map<String, Object>> joinAssetList) {
		if (ObjectUtil.isEmpty(searchFilters)) {
			return joinAssetList;
		}
		List<Map<String, Object>> resultList = ListUtil.newArrayList();
		List<SearchFilter> lastModifiedByFilters = searchFilters.stream().filter(s -> "lastModifiedBy".equals(s.fieldName)).collect(Collectors.toList());
		List<String> loginNamList = ListUtil.newArrayList();
		if (lastModifiedByFilters.size() == 1) {
			List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(UserContextService.getCurrentUserAccount().getTenantUID(), lastModifiedByFilters.get(0).value.toString());
			loginNamList = userAccountList.stream().map(UserAccount::getLoginName).collect(Collectors.toList());
		}
		for (Map<String, Object> map : joinAssetList) {
			Set<Boolean> flags = new HashSet<>();
			Date start = null;
			Date end = null;
			for (SearchFilter searchFilter : searchFilters) {
				String fieldName = searchFilter.fieldName;
				switch (fieldName) {
					case "selectedOrgUid":
						flags.add(map.get("institutionuid").toString().equals(searchFilter.value) || map.get("hospitaluid").toString().equals(searchFilter.value) || map.get("siteuid").toString().equals(searchFilter.value));
						break;
					case "queryAssetUid":
						flags.add(Optional.ofNullable(map.get("queryassetuid")).map(String::valueOf).map(searchFilter.value::equals).orElse(false));
						break;
					case "name":
						flags.add(Optional.ofNullable(map.get("name")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "clinicalDeptName":
						flags.add(Optional.ofNullable(map.get("clinicaldeptname")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "qrCode":
						flags.add(Optional.ofNullable(map.get("qrcode")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "departNum":
						flags.add(Optional.ofNullable(map.get("departnum")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "functionType":
						flags.add(Optional.ofNullable(map.get("functiontype")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "financingNum":
						flags.add(Optional.ofNullable(map.get("financingnum")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "assetStatus":
						flags.add(Optional.ofNullable(map.get("assetstatus")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "locationName":
						flags.add(Optional.ofNullable(map.get("locationname")).map(String::valueOf).map(o -> o.contains((String) searchFilter.value)).orElse(false));
						break;
					case "isSolved":
						boolean isSolved = Boolean.parseBoolean(searchFilter.value.toString());
						if (isSolved) {
							flags.add(Optional.ofNullable(map.get("issolved")).map(String::valueOf).map("是"::equals).orElse(false));
						} else {
							flags.add(Optional.ofNullable(map.get("issolved")).map(String::valueOf).map("否"::equals).orElse(false));
						}
						break;
					case "lastModifiedBy":
						if (ObjectUtil.isNotEmpty(loginNamList)) {
							flags.add(Optional.ofNullable(map.get("lastmodifiedby")).map(String::valueOf).map(loginNamList::contains).orElse(false));
						} else {
							flags.add(false);
						}
						break;
					case "status":
						flags.add(Optional.ofNullable(map.get("status")).map(String::valueOf).map(searchFilter.value::equals).orElse(false));
						break;
					case "lastModifiedDate":
						if (searchFilter.operator.name().equals("GTE")) {
							start = (Date) searchFilter.value;
                        } else {
							end = (Date) searchFilter.value;
                        }
                        break;
					default:
						break;
				}
			}
            if (ObjectUtil.isAllNotEmpty(start, end)) {
                Date date = Optional.ofNullable(map.get("lastmodifieddate")).map(String::valueOf).map(DateUtil::parse).orElse(null);
                if (ObjectUtil.isEmpty(date)) {
                    flags.add(Boolean.FALSE);
                } else if (date.after(start) && date.before(end)) {
                    flags.add(Boolean.TRUE);
                } else {
                    flags.add(Boolean.TRUE);
                }
            }
			if (!flags.contains(Boolean.FALSE)) {
				resultList.add(map);
			}
		}
		return resultList;
	}

	public String getConfigValue(UserAccount currentUser, String key) {
		return configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), key);
	}

}

package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ZkbIntegralOrderListService {

    Logger logger = LoggerFactory.getLogger(ZkbIntegralOrderListService.class);

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    public void queryUserConfig(List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList,ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo, UserAccount userAccount){
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginName(userAccount.getLoginName());
        if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfigs)){
            zkbPartOrderUserConfigList.addAll(zkbPartOrderUserConfigs);
            for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                if(ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name().equals(c.getConfigType())){
                    zkbIntegralOrderStatusVo.setShowStatusBtn(false);
                }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.name().equals(c.getConfigType())){
                    zkbIntegralOrderStatusVo.setShowStatusBtn(false);
                }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.name().equals(c.getConfigType())){
                    zkbIntegralOrderStatusVo.setShowStatusBtn(true);
                }
            }
        }
    }

    public List<TenantInfo> getSourceTenantInfoList() {
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID) {
        return tenantInfoDao.findByuid(tenantUID);
    }


    public void calcPendQty(boolean isZkbUser, UserAccount currentUser, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo, List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList) {
        if (!isZkbUser) {
            Specification<ZkbIntegralOrder> specification = buildPendingSpecification(currentUser, zkbPartOrderUserConfigList);
            if (specification != null) {
                long q = zkbIntegralOrderRepository.count(specification);
                zkbIntegralOrderStatusVo.setPendingQty(q);
            }
        }
    }

    public void calcStepQty(boolean isZkbUser, UserAccount currentUser, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo){
        Specification<ZkbIntegralOrder> specification = buildRoleSpecification(currentUser, ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name(), isZkbUser);
        zkbIntegralOrderStatusVo.setPendingEngineerQualificationQty(zkbIntegralOrderRepository.count(specification));

        specification = buildRoleSpecification(currentUser, ZkbPartOrderStep.PartOrderStep.ManagePending.name(), isZkbUser);
        zkbIntegralOrderStatusVo.setPendingManageQty(zkbIntegralOrderRepository.count(specification));

        specification = buildRoleSpecification(currentUser, ZkbPartOrderStep.PartOrderStep.OrderPending.name(), isZkbUser);
        zkbIntegralOrderStatusVo.setPendingOrderQty(zkbIntegralOrderRepository.count(specification));

        specification = buildRoleSpecification(currentUser, ZkbPartOrderStep.PartOrderStep.Complete.name(), isZkbUser);
        zkbIntegralOrderStatusVo.setPendingCompleteQty(zkbIntegralOrderRepository.count(specification));
    }

    public Specification<ZkbIntegralOrder> buildPendingSpecification(UserAccount currentUser, List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList) {
        return (Root<ZkbIntegralOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (currentUser.getLoginName().contains(ZkbPartOrder.DATA_TYPE_TEST)) {
                predicates.add(builder.equal(root.get("dataType"), ZkbPartOrder.DATA_TYPE_TEST));
            } else {
                predicates.add(builder.isNull(root.get("dataType")));
            }
            if (zkbPartOrderUserConfigList != null && zkbPartOrderUserConfigList.size() > 0) {
                List<Predicate> userConfigPredicates = new ArrayList<>();
                for (ZkbPartOrderUserConfig zkbPartOrderUserConfig : zkbPartOrderUserConfigList) {
                    if (ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType())) {
                        if (!StrUtil.isEmpty(zkbPartOrderUserConfig.getZone())) {
                            List<String> strs = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                            Expression<String> exp = root.get("zone");
                            Predicate pre1 = exp.in(strs);
                            Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                            Predicate pre3 = builder.and(pre1, pre2);
                            userConfigPredicates.add(pre3);
                        } else {
                            Predicate pre1 = builder.isNull(root.get("currentStep"));
                            userConfigPredicates.add(pre1);
                        }

                    } else if (ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType())) {
                        Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                        userConfigPredicates.add(pre1);
                    } else if (ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString().equals(zkbPartOrderUserConfig.getConfigType())) {
                        Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString());
                        userConfigPredicates.add(pre1);
                    }
                }
                if (userConfigPredicates.size() > 0) {
                    predicates.add(builder.or(userConfigPredicates.toArray(new Predicate[0])));
                }else{
                    predicates.add(builder.isFalse(builder.literal(true)));
                }
            } else {
                predicates.add(builder.isFalse(builder.literal(true)));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ZkbIntegralOrder> buildRoleSpecification(UserAccount currentUser, String currentStep, boolean isZkbUser) {
        return (Root<ZkbIntegralOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(isZkbUser){
                predicates.add(builder.equal(root.get("tenantUID"), currentUser.getTenantUID()));
            }
            if (currentUser.getLoginName().contains(ZkbPartOrder.DATA_TYPE_TEST)) {
                predicates.add(builder.equal(root.get("dataType"), ZkbPartOrder.DATA_TYPE_TEST));
            } else {
                predicates.add(builder.isNull(root.get("dataType")));
            }
            predicates.add(builder.equal(root.get("currentStep"), currentStep));
            return builder.and(predicates.toArray(new Predicate[0]));
        };

    }

}

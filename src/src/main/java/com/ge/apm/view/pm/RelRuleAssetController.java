package com.ge.apm.view.pm;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.OrgInfoComparator;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class RelRuleAssetController extends GenericCRUDUUIDController<RelRuleAsset> {

    private static final long serialVersionUID = -1L;
    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private UserAccount user;
    private AssetInfoRepository assetInfoRepository;
    private QueryAsset queryAsset;
    private RelRuleAssetRepository relRuleAssetRepository;
    private String pmOrderRuleUID;
    private Integer level;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private OrgInfoRepository orgInfoRepository;
    private String siteUid;
    private BrandRepository brandRepository;
    private Boolean isInsulate;
    private Integer loginUserOrgType;
    private UserCommonService userCommonService;
    private PmOrderRule pmOrderRule;
    private String ruleUid;
    //private SupplierRepository supplierDao;
    private SupplierService supplierService;

    private TenantInfo selectedTenant;
    private OrgInfo selectedUserOrg;
    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private TenantInfoRepository tenantInfoRepository;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;

    @Override
    protected void init() {
        this.filterByHospital = false;
        this.filterBySite = false;
        user = UserContextService.getCurrentUserAccount();
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        brandRepository = WebUtil.getBean(BrandRepository.class);
        targetAssets = new ArrayList<>();
        queryAsset = new QueryAsset();
        queryAsset.setTenantUID(user.getTenantUID());
        siteUid = user.getSiteUID();
        initAssets();
        userCommonService = WebUtil.getBean(UserCommonService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        isInsulate = UserContextService.isInsulate();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        initTree(user.getTenantUID());
    }

    private void initTree(String tenantUid) {
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        if(UserContextService.isInsulate()){
            //隔离租户
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
        }else{
            //非隔离租户
            selectedTenant = tenantInfoRepository.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
        }

        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
        }
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
    }

    public void initAssets() {
        sourceAssets = new ArrayList<>();
        targetAssets.forEach((item) -> {
            if (sourceAssets.contains(item)) {
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public String getOrgName(String uid) {
        OrgInfo orgInfo = orgInfoRepository.getByUid(uid);
        if (orgInfo == null) {
            return null;
        }
        return orgInfo.getName();
    }

    public String getOrgFullName(OrgInfo org) {
        if(null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }
        return name;
    }

    public List<AssetInfo> getAssetList() {
        List<SearchFilter> assetFilters = new ArrayList<SearchFilter>();
        List<String> uids = null;
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getName())) {
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}
/*        if (null != queryAsset.getManufactureId()) {
            assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId().toString()));
        }*/
        if (null != queryAsset.getAssetGroup()) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getClinicalDeptUid())){
            Set<String> clinicalUids = orgInfoTreeService.collectChildClinicalDept(queryAsset.getClinicalDeptUid());
            List<String> results = new ArrayList<>(clinicalUids);
            if(CollectionUtils.isNotEmpty(results)){
                assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,results));
            }
        }
        if (StringUtils.isNotEmpty(queryAsset.getBrand())) {
            assetFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, queryAsset.getBrand()));
        }
        if (!assetFilters.isEmpty()) {
            if (queryAsset.getRuleStatus() != null) {
                uids = new ArrayList<>();
                if (queryAsset.getRuleStatus() == 1) {// 已配置
                    uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
                    if (CollectionUtils.isNotEmpty(uids)) {
                        assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
                    }
                } else if (queryAsset.getRuleStatus() == 2) {// 未配置
                    uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
                    assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, uids));
                }
            }
            List<SearchFilter> userFilter = getAssetFilter();
            assetFilters.addAll(userFilter);
            return assetInfoRepository.findBySearchFilter(assetFilters);
        }
        return new ArrayList<>();
    }

    public List<SearchFilter> getAssetFilter() {
        List<SearchFilter> filter = new ArrayList<>();
        if(selectedUserOrg != null){
            if(selectedUserOrg.getOrgType() == 0){
                filter.addAll(userCommonService.getSelectedOrgFilters(selectedUserOrg));
            }
        }else{
            filter.addAll(userCommonService.getUserLogicFilters(loginUserOrgType, user));
        }
        filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        return filter;
    }

    public void addFilter() {
        sourceAssets = getAssetList();
        targetAssets.forEach((item) -> {
            if (sourceAssets.contains(item)) {
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    @Override
    protected RelRuleAssetRepository getDAO() {
        return relRuleAssetRepository;
    }

    public void onAssetsTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetAssets.add((AssetInfo) item);
            } else {
                targetAssets.remove((AssetInfo) item);
            }
        }
    }

    @Transactional
    public void saveRel() {
        if (targetAssets.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
        } else {
            List<RelRuleAsset> rels = new ArrayList<>(targetAssets.size());
            targetAssets = targetAssets.stream().distinct().collect(Collectors.toList());
            for (AssetInfo ai : targetAssets) {
                RelRuleAsset rel = relRuleAssetRepository.findByAssetUID(ai.getUid());
                if (rel == null) {
                    rel = new RelRuleAsset();
                    rel.setAssetUID(ai.getUid());
                    rel.setCreatedDate(new Date());
                    rel.setTenantUID(ai.getTenantUID());
                    rel.setInstitutionUID(ai.getInstitutionUID());
                    rel.setHospitalUID(ai.getHospitalUID());
                    rel.setSiteUID(ai.getSiteUID());
                }
                if (level != null) {
                    if (level == 1) {
                        rel.setRuleUID1(pmOrderRule.getUid());
                    } else if (level == 2) {
                        rel.setRuleUID2(pmOrderRule.getUid());
                    } else if (level == 3) {
                        rel.setRuleUID(pmOrderRule.getUid());
                    }
                    rels.add(rel);
                }
                relRuleAssetRepository.save(rels);
            }
            this.level = null;
            this.ruleUid = null;
            WebUtil.addSuccessMessage("配置成功!");
        }

    }

    public List<PmOrderRule> getPmOrderRuleList() {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryAsset.getTenantUID()));
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getName())) {
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}
/*        if (null != queryAsset.getManufactureId()) {
            assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId().toString()));
        }*/
        if (queryAsset.getAssetGroup() != null) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (level != null) {
            assetFilters.add(new SearchFilter("level", SearchFilter.Operator.EQ, level));
        }
        return pmOrderRuleRepository.findBySearchFilter(assetFilters);
    }

    public List<PmOrderRule> getRuleList(String name) {
		/*		filter.add(new SearchFilter("level", SearchFilter.Operator.EQ, this.level));
		filter.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));*/
        //List<SearchFilter> filter = userCommonService.getUserLogicFilters(loginUserOrgType,user);
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            filter.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
/*		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
		}*/
        if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
            filter.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
        }
        if (queryAsset.getAssetGroup() != null) {
            filter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (level != null) {
            filter.add(new SearchFilter("level", SearchFilter.Operator.EQ, level));
        }
        if (StringUtils.isNotEmpty(name)) {
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name));
        } else if (StringUtils.isNotEmpty(queryAsset.getName())) {
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
        List<PmOrderRule> rules = pmOrderRuleRepository.findBySearchFilter(filter);
        rules.sort(new Comparator<PmOrderRule>() {
            @Override
            public int compare(PmOrderRule o1, PmOrderRule o2) {
                try {
                    String name1 = PinyinHelper.convertToPinyinString(o1.getName(), "", PinyinFormat.WITHOUT_TONE);
                    String name2 = PinyinHelper.convertToPinyinString(o2.getName(), "", PinyinFormat.WITHOUT_TONE);
                    return name1.compareTo(name2);
                } catch (PinyinException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return rules;
    }

    public void clearPmOrderRule(){
        this.ruleUid = null;
    }

    public List<String> getSupplier(String name) {
        return supplierService.getSupplier(name);
    }

    public List<OrgInfo> getClinicalDeptList(String name){
        //UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4,"1*"));
        filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
        if(StringUtils.isNotEmpty(name)){
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name.toLowerCase(),"1*2+"));
        }
        List<OrgInfo> orgInfos = orgInfoRepository.findBySearchFilter(filter);
        orgInfos.sort(new OrgInfoComparator());
        return orgInfos;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public OrgInfo getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(OrgInfo selectedUserOrg) {
        this.selectedUserOrg = selectedUserOrg;
    }

    public void setRuleId() {

    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public String getRuleUid() {
        return ruleUid;
    }

    public void setRuleUid(String ruleUid) {
        this.ruleUid = ruleUid;
    }

    public PmOrderRule getPmOrderRule() {
        return pmOrderRule;
    }

    public void setPmOrderRule(PmOrderRule pmOrderRule) {
        this.pmOrderRule = pmOrderRule;
    }

    public String getStrDate(Date lastPmDate) {
        return TimeUtils.getStrDate(lastPmDate, "yyyy-MM-dd");
    }

    public String getPmOrderRuleUID() {
        return pmOrderRuleUID;
    }

    public void setPmOrderRuleUID(String pmOrderRuleUID) {
        this.pmOrderRuleUID = pmOrderRuleUID;
    }

    public QueryAsset getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(QueryAsset queryAsset) {
        this.queryAsset = queryAsset;
    }

    public DualListModel<AssetInfo> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<AssetInfo> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<AssetInfo> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<AssetInfo> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<AssetInfo> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
    }

}

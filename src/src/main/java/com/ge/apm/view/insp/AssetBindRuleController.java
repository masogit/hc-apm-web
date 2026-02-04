package com.ge.apm.view.insp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.NurseSiteMaintainConfigMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetBindRuleController {


    @Setter@Getter
    private String assetUid;
    @Setter@Getter
    private List<NurseSiteMaintainConfig> nurseSiteMaintainConfigList;
    @Setter@Getter
    NurseSiteMaintainConfig selectedNurseSiteMaintainConfig;
    @Setter@Getter
    PmOrderRule pmOrderRule;
    @Getter@Setter
    List<NurseSiteMaintainConfigCheckItem> listCheckItems;
    @Getter@Setter
    TableDefinition tableDefinitionEs;
    @Getter@Setter
    TableDefinition tableDefinition;
    @Getter@Setter
    private List<PmOrderRuleCheckList> checkItemLists;
    @Getter @Setter
    private boolean pmLevelFour;
    @Getter @Setter
    private Map<String, PmOrderRule> ruleMap = new HashMap<>();

    private List<NurseSiteMaintainConfigCheckItem> listPairCheckItems;
    private List<NurseSiteMaintainCheckItem> listWholeCheckItems;
    @Getter@Setter
    private List<RelRuleAsset> relRuleAssetList;

    private NurseSiteMaintainConfigMapper nurseSiteMaintainConfigMapper;
    private NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
    private UserAccountRepository userAccountRepository;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private RelRuleAssetRepository relRuleAssetRepository;
    private TableDefinitionRepository templateDao;
    private PmOrderRuleCheckListRepository pmOrderRuleCheckListRepository;
    private UserAccount user;
    private ConfigService configService;
    private OrgInfoRepository orgInfoRepository;


    @PostConstruct
    protected void init() {
        nurseSiteMaintainConfigMapper = WebUtil.getBean(NurseSiteMaintainConfigMapper.class);
        nurseSiteMaintainConfigRepository = WebUtil.getBean(NurseSiteMaintainConfigRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        user = UserContextService.getCurrentUserAccount();

        WorkflowConfig workflowConfig = configService.findConfigBySite(user.getSiteUID());
        pmLevelFour = workflowConfig != null && workflowConfig.getPmRuleLevelFour();

    }

    public void loadNurseRuleList(){
        nurseSiteMaintainConfigList = nurseSiteMaintainConfigMapper.selectByAssetUid(assetUid);
        relRuleAssetList = getPmOrderRuleList(assetUid);
        initInspectionRuleMap(relRuleAssetList);

    }

    public String getUserName(String uid){
        if (StrUtil.isEmpty(uid)){
            return "";
        }
        UserAccount userAccount = userAccountRepository.findByUid(uid);
        if(userAccount == null){
            return "";
        }
        return userAccount.getName();
    }


    public void prepareNurseSiteConfig(String id){
        selectedNurseSiteMaintainConfig = nurseSiteMaintainConfigRepository.findById(id);
        listCheckItems = getListPairCheckItems();
    }


    public List<NurseSiteMaintainConfigCheckItem> getListPairCheckItems() {
        String strTemp=this.selectedNurseSiteMaintainConfig.getCheckItemsDetail();
        List<NurseSiteMaintainConfigCheckItem> list=new ArrayList<NurseSiteMaintainConfigCheckItem>();
        if(StringUtils.isNotEmpty(strTemp)){
            JSONArray jsonArray= JSONObject.parseArray(strTemp);
            for(int i=0;i<jsonArray.size();++i){
                NurseSiteMaintainCheckItem p1= JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
                NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(p1,(JSONObject) jsonArray.get(i));
                if (p.getKey()==null) {
                    p.setKey(new Date().getTime());
                }
                list.add(p);
            }
        }
        listPairCheckItems=list;
        return listPairCheckItems;
    }

    private NurseSiteMaintainConfigCheckItem initConfigCheckItem(NurseSiteMaintainCheckItem checkItem,JSONObject jsonObject){
        NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
        String id = checkItem.getId();
        p.setId(id);
        p.setName(checkItem.getName());
        p.setDefaultValue(checkItem.getDefaultValue());
        p.setDescription(checkItem.getDescription());
        p.setFieldType(checkItem.getFieldType());
        String options = checkItem.getOptions();
        p.setRequired(checkItem.getRequired());
        p.setKey(checkItem.getKey());
        if(CollectionUtils.isNotEmpty(listWholeCheckItems) && StringUtils.isNotEmpty(id)){
            Optional<String> first = listWholeCheckItems.stream().filter(item -> id.equals(item.getId()) && StringUtils.isNotEmpty(item.getGroupName())).map(NurseSiteMaintainCheckItem::getGroupName).findFirst();
            if (first.isPresent()) {
                p.setGroupName(first.get());
            }else{
                p.setGroupName(jsonObject.getString("groupName"));
            }
        }else{
            p.setGroupName(jsonObject.getString("groupName"));
        }
        if(StringUtils.isBlank(options) || options.contains("[]")){
            p.setOptions(null);
        }else{
            JSONArray objects = JSONArray.parseArray(options);
            List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
            p.setOptions(tableOptions);
        }
        return p;
    }

    public List<RelRuleAsset> getPmOrderRuleList(String assetUid) {
        List<SearchFilter> searchFilters = new ArrayList();

        searchFilters.add(new SearchFilter("assetUID", SearchFilter.Operator.EQ, assetUid));

        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        return relRuleAssetRepository.findBySearchFilter(searchFilters,sort);
    }
    private void initInspectionRuleMap(List<RelRuleAsset> relRuleAssetList) {
        List<String> ruleUidList = ListUtil.newArrayList();
        for (RelRuleAsset relRuleAsset : relRuleAssetList) {
            addRuleUidList(relRuleAsset.getRuleUIDInspection(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID2(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID4(), ruleUidList);
        }
        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findByUidIn(ruleUidList);
        ruleMap = pmOrderRules.stream().collect(Collectors.toMap(PmOrderRule::getUid, t -> t));
    }
    private void addRuleUidList(String ruleUid, List<String> ruleUidList) {
        if (ObjectUtil.isNotEmpty(ruleUid)) {
            ruleUidList.addAll(Arrays.asList(org.apache.commons.lang.StringUtils.split(ruleUid, '|')));
        }
    }


    public void preparePmRule(String uid){
        pmOrderRule = pmOrderRuleRepository.findByUid(uid);
        if(StrUtil.isNotEmpty(pmOrderRule.getSafetyReport())){
            tableDefinitionEs = templateDao.findById(pmOrderRule.getSafetyReport());
        }
        if(StrUtil.isNotEmpty(pmOrderRule.getPerformanceReport())){
            tableDefinition = templateDao.findById(pmOrderRule.getPerformanceReport());
        }
        initCheckItemLists();
    }

    public void initCheckItemLists() {
        if (pmOrderRule != null && pmOrderRule.getUid() != null) {
            checkItemLists = pmOrderRuleCheckListRepository.findAllByRuleUID(pmOrderRule.getUid());
        }
        if (CollectionUtils.isEmpty(checkItemLists)) {
            checkItemLists = new ArrayList<>();
        }
    }

    public String getRuleName(String ruleUID) {
        List<PmOrderRule> pmOrderRules = ListUtil.newArrayList();
        for (String ruleUid : StringUtils.split(ruleUID, '|')) {
            if (ObjectUtil.isNotEmpty(ruleMap.get(ruleUid))) {
                pmOrderRules.add(ruleMap.get(ruleUid));
            }
        }
        String ruleName = "";
        if (CollectionUtils.isNotEmpty(pmOrderRules)) {
            ruleName = pmOrderRules.stream().map(PmOrderRule::getName).collect(Collectors.joining("|"));
        }
        return ruleName;
    }

    public List<PmOrderRule> getRules(String ruleUID){
        List<PmOrderRule> pmOrderRules = ListUtil.newArrayList();
        for (String ruleUid : StringUtils.split(ruleUID, '|')) {
            if (ObjectUtil.isNotEmpty(ruleMap.get(ruleUid))) {
                pmOrderRules.add(ruleMap.get(ruleUid));
            }
        }
        return pmOrderRules;
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public String getSiteName(String siteUid){
        if(StringUtils.isEmpty(siteUid)){
            return "";
        }
        OrgInfo orgInfo = orgInfoRepository.findByUid(siteUid);
        if(orgInfo != null){
            return orgInfo.getName();
        }
        return "";
    }
}

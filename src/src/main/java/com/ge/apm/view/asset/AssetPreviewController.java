package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.*;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetPreviewController extends JpaCRUDController<AssetInfo> {

    private static final Logger logger = Logger.getLogger(AssetPreviewController.class);
    private static final long serialVersionUID = 1L;

    private AssetInfoRepository dao = null;
    private AssetInfoListController assetInfoListController = null;
    private UaaService uaaService;

    @Getter
    @Setter
    private String oneSearchStr;
    @Getter
    @Setter
    private List<AssetInfo> assetInfoList = new ArrayList<>();
    private Map<String, Boolean> selectedMap = new HashMap<>();
    //显示子设备，主设备，或者全部类型的设备
    @Getter
    private int queryType = 1;
    public void setQueryType(int queryType) {
        this.searchFilters.clear();
        this.queryType = queryType;
    }
    private UserAccount user;
    @Getter
    @Setter
    private OrgInfo selectSite;
    @Getter
    @Setter
    private String treeNodeNameFilter;
    @Getter
    private TreeNode selectedNode;

    protected void init() {
        user = UserContextService.getCurrentUserAccount();

        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoListController = WebUtil.getBean(AssetInfoListController.class);
        uaaService = WebUtil.getBean(UaaService.class);

        this.filterBySite = false;
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        List<AssetInfo> assetInfos = getAssetInfos();
        List<AssetInfo> content = new ArrayList<>();
        assetInfoList = assetInfos;
        if(CollectionUtils.isEmpty(assetInfos)) {
            pageRequest = new PageRequest(0,pageRequest.getPageSize(),pageRequest.getSort());
        }else if((pageRequest.getPageNumber()+1)*pageRequest.getPageSize() > assetInfos.size()){
            pageRequest = new PageRequest(assetInfos.size()%pageRequest.getPageSize() == 0 ? assetInfos.size()/pageRequest.getPageSize()-1:assetInfos.size()/pageRequest.getPageSize(),pageRequest.getPageSize(),pageRequest.getSort());
        }
        if(CollectionUtils.isNotEmpty(assetInfos)) {
            content = assetInfos.subList(pageRequest.getPageNumber()*pageRequest.getPageSize(),Math.min((pageRequest.getPageNumber()+1)*pageRequest.getPageSize(),assetInfos.size()));
        }
        return new PageImpl(content, pageRequest, assetInfos.size());
    }

    public List<AssetInfo> getAssetInfos(){
        Set<Integer> tempRootIds = getRootIds();
        if(CollectionUtils.isNotEmpty(tempRootIds)){
            Set<Integer> rootIds = removeOnlyParentAsset(tempRootIds);
            List<AssetInfo> assetInfos = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(rootIds)) {
                List<Object> assetInfoObjects = dao.findParentAndChildrenAsset1(rootIds);
                assetInfoObjects.stream().forEach(e -> {
                    convertToAssetInfo(assetInfos, (Object[]) e);
                });

                Map<Integer, AssetInfo> assetInfoMap = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getId, v -> v, (o1, o2) -> o2));
                List<SearchFilter> temp = new ArrayList<>();
                temp.add(new SearchFilter("id", SearchFilter.Operator.IN,assetInfoMap.keySet()));
                List<AssetInfo> infos = dao.findBySearchFilter(temp);
                Map<Integer, AssetInfo> assetMap = infos.stream().collect(Collectors.toMap(AssetInfo::getId, v -> v, (o1, o2) -> o2));
                Map<Integer, List<AssetInfo>> parentAssetMap = infos.stream().filter(asset -> asset.getParentAssetId() != null).collect(Collectors.groupingBy(AssetInfo::getParentAssetId));
                List<AssetInfo> removeAssets = ListUtil.newArrayList();
                infos.stream().forEach(e->{
                    if (e.getParentAssetId() != null) {
                        List<AssetInfo> childAssets = parentAssetMap.get(e.getParentAssetId());
                        List<AssetInfo> removeChildAssets = childAssets.stream().filter(asset -> assetMap.get(asset.getId()).getIsDeleted() || !assetMap.get(asset.getId()).getIsValid()).collect(Collectors.toList());
                        if (childAssets.size() == removeChildAssets.size()) {
                            removeAssets.add(assetMap.get(e.getParentAssetId()));
                        }
                        removeAssets.addAll(removeChildAssets);
                    } else if (e.getIsDeleted() || !e.getIsValid()) {
                        removeAssets.add(e);
                        List<AssetInfo> childAssets = parentAssetMap.get(e.getId());
                        removeAssets.addAll(childAssets);
                    }
                    e.setRank(assetInfoMap.get(e.getId()).getRank());
                    e.setBranchIds(assetInfoMap.get(e.getId()).getBranchIds());
                });
                infos.removeAll(removeAssets);
                if(queryType == 2){
                    infos = infos.stream().filter(a -> a.getRank() == 0).collect(Collectors.toList());
                }else if (queryType == 3){
                    infos = infos.stream().filter(a -> a.getRank() != 0).collect(Collectors.toList());
                }

                return infos.stream().sorted(Comparator.comparing(AssetInfo::getBranchIds)).collect(Collectors.toList());
            }
        }
        return Collections.EMPTY_LIST;
    }

    private Set<Integer> removeOnlyParentAsset(Set<Integer> tempRootIds) {
        List<SearchFilter> tempFilters = new ArrayList<>();
        tempFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IN,tempRootIds));
        List<AssetInfo> assetInfos = dao.findBySearchFilter(tempFilters);
        return assetInfos.stream().map(AssetInfo::getParentAssetId).collect(Collectors.toSet());
    }

    private Set<Integer> getRootIds() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.setUserSearchFilters();
        List<AssetInfo> assetInfoList = dao.findBySearchFilter(this.searchFilters);
        Set<Integer> rootIds = new HashSet<>();
        getRootIdsByRecursion(assetInfoList, rootIds);
        return rootIds;
    }



    private void getRootIdsByRecursion(List<AssetInfo> assetInfoList, Set<Integer> rootIds) {
        Set<AssetInfo> rootAssets = assetInfoList.stream().filter(e -> e.getParentAssetId() == null).collect(Collectors.toSet());
        Set<Integer> tempRootIds = rootAssets.stream().map(AssetInfo::getId).collect(Collectors.toSet());
        Set<AssetInfo> unRootAssets = assetInfoList.stream().filter(e -> e.getParentAssetId() != null).collect(Collectors.toSet());
        Set<Integer> tempunRootIds = unRootAssets.stream().map(AssetInfo::getParentAssetId).collect(Collectors.toSet());
        rootIds.addAll(tempRootIds);
        if (CollectionUtils.isNotEmpty(tempunRootIds)) {
            List<SearchFilter> temp = new ArrayList<>();
            temp.add(new SearchFilter("id", SearchFilter.Operator.IN, tempunRootIds));
            List<AssetInfo> tempAssetInfos = dao.findBySearchFilter(temp);
            getRootIdsByRecursion(tempAssetInfos, rootIds);
        }
    }

    private void convertToAssetInfo(List<AssetInfo> assetInfos, Object[] e) {
        Object[] o = e;
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setId(Integer.parseInt(checkData(o[0])));
        assetInfo.setName(checkData(o[1]));
        assetInfo.setParentAssetId(Integer.parseInt(checkData(o[2] == null ? -1 : o[2])));
        assetInfo.setBranchIds(checkData(o[3]));
        assetInfo.setRank(Integer.parseInt(checkData(o[4])));
        assetInfos.add(assetInfo);
    }

    private String checkData(Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }

    public Map<String, Boolean> initSelectMap(List<AssetInfo> assetInfos){
        selectedMap.clear();
        if(CollectionUtils.isNotEmpty(assetInfos)){
            assetInfos.stream().forEach(e->{
                selectedMap.put(e.getUid(),true);
            });
        }
        return selectedMap;
    }

    public void exportExcel() throws IOException {
        assetInfoListController.setSelectedMap(initSelectMap(assetInfoList));
        assetInfoListController.setIsOrderByForParentAndChildren(true);
        assetInfoListController.exportExcel();
    }

    public void onSelectAsset() throws IOException {
        assetInfoListController.setSelected(selected);
        assetInfoListController.onSelectAsset();
    }

    public void setSelectedNode(TreeNode selectedNode) {
        if (selectedNode == null) {
            return;
        }
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        selected = null;
        TreeNode node = event.getTreeNode();
        selectSite = (OrgInfo) node.getData();
    }

    public void setUserSearchFilters() {

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        if (!UserContextService.isMVSRole()) {
            if (!this.hasFilterOnField("siteUID")) {
                List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetTags.tag", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

        searchFilters.removeAll(searchFilters.stream().filter(item -> "parentAssetId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

        if (UserContextService.isMVSRole()) {
            this.updateSearchFilter(new SearchFilter("mvsEquip", SearchFilter.Operator.EQ, 1));
        }
    }

}

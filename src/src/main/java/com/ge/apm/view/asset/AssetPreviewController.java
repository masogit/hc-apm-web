package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.domain.*;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetPreviewController extends GenericCRUDController<AssetInfo> {

    private static final Logger logger = Logger.getLogger(AssetPreviewController.class);
    private static final long serialVersionUID = 1L;

    private AssetInfoRepository dao = null;
    private AssetInfoListController assetInfoListController = null;
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
        assetInfoListController.searchFilters.clear();
        this.queryType = queryType;
    }
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoListController = WebUtil.getBean(AssetInfoListController.class);
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
                infos.stream().forEach(e->{
                    e.setRank(assetInfoMap.get(e.getId()).getRank());
                    e.setBranchIds(assetInfoMap.get(e.getId()).getBranchIds());
                });

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
        if (assetInfoListController.searchFilters == null) {
            assetInfoListController.searchFilters = new ArrayList<SearchFilter>();
        }
        assetInfoListController.setQueryType(1);
        assetInfoListController.setOneSearchStr(oneSearchStr);
        assetInfoListController.setUserSearchFilters();
        List<AssetInfo> assetInfoList = dao.findBySearchFilter(assetInfoListController.searchFilters);
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
//        assetInfo.setFunctionType(checkData(o[5]));
//        assetInfo.setSerialNum(checkData(o[6]));
//        assetInfo.setDepartNum(checkData(o[7]));
//        assetInfo.setAssetGroup(Integer.parseInt(checkData(o[8])));
//        if (o[9] != null) {
//            assetInfo.setAssetDeptId(Integer.parseInt(checkData(o[9])));
//        }
//        assetInfo.setClinicalDeptName(checkData(o[10]));
//        assetInfo.setManufacture(checkData(o[11]));
//        assetInfo.setVendor(checkData(o[12]));
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
}

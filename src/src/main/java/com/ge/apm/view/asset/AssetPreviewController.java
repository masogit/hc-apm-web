package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
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
    private UserCommonService userCommonService;

    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;

    private UserAccount user;
    private Boolean isInsulate;
    private OrgInfo selectedUserOrg;
    private Integer loginUserOrgType;

    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;

    protected void init() {
        userCommonService = WebUtil.getBean(UserCommonService.class);

        dao = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        assetInfoListController = WebUtil.getBean(AssetInfoListController.class);

        user = UserContextService.getCurrentUserAccount();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        Set<Integer> rootIds = getRootIds();
//        rootIds.add(20);
//        rootIds.add(23);
        List<Object> assetInfoObjects = dao.findParentAndChildrenAsset1(rootIds);
        List<AssetInfo> assetInfos  = new ArrayList<>();
        assetInfoObjects.stream().forEach(e->{
                    convertToAssetInfo(assetInfos, (Object[]) e);
                }
        );
        return new PageImpl(assetInfos,pageRequest,assetInfos.size());
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
        getRootIdsByRecursion(assetInfoList,rootIds);
        return rootIds;
    }

    private void getRootIdsByRecursion(List<AssetInfo> assetInfoList, Set<Integer> rootIds) {
        Set<AssetInfo> rootAssets = assetInfoList.stream().filter(e -> e.getParentAssetId() == null).collect(Collectors.toSet());
        Set<Integer> tempRootIds = rootAssets.stream().map(AssetInfo::getId).collect(Collectors.toSet());
        Set<AssetInfo> unRootAssets = assetInfoList.stream().filter(e -> e.getParentAssetId() != null).collect(Collectors.toSet());
        Set<Integer> tempunRootIds = unRootAssets.stream().map(AssetInfo::getParentAssetId).collect(Collectors.toSet());
        rootIds.addAll(tempRootIds);
        if(CollectionUtils.isNotEmpty(tempunRootIds)) {
            List<SearchFilter> temp = new ArrayList<>();
            temp.add(new SearchFilter("id", SearchFilter.Operator.IN, tempunRootIds));
            List<AssetInfo> tempAssetInfos = dao.findBySearchFilter(temp);
            getRootIdsByRecursion(tempAssetInfos,rootIds);
        }
    }

    private void convertToAssetInfo(List<AssetInfo> assetInfos, Object[] e) {
        Object[] o = e;
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setId(Integer.parseInt(checkData(o[0])));
        assetInfo.setName(checkData(o[1]));
        assetInfo.setParentAssetId(Integer.parseInt(checkData(o[2] == null?-1:o[2])));
        assetInfo.setBranchIds(checkData(o[3]));
        assetInfo.setRank(Integer.parseInt(checkData(o[4])));
        assetInfo.setFunctionType(checkData(o[5]));
        assetInfo.setSerialNum(checkData(o[6]));
        assetInfo.setDepartNum(checkData(o[7]));
        assetInfo.setAssetGroup(Integer.parseInt(checkData(o[8])));
        if(o[9] != null) {
            assetInfo.setAssetDeptId(Integer.parseInt(checkData(o[9])));
        }
        assetInfo.setClinicalDeptName(checkData(o[10]));
        assetInfo.setManufacture(checkData(o[11]));
        assetInfo.setVendor(checkData(o[12]));
        assetInfos.add(assetInfo);
    }
    
    private String checkData(Object o){
        if(o == null){
            return "";
        }else {
            return String.valueOf(o);
        }
    }
}

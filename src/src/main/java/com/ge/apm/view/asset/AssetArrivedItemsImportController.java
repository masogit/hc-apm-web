package com.ge.apm.view.asset;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.ArrivalItemImport;
import com.ge.apm.service.asset.ArrivalItemImportHandel;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetArrivedItemsImportController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository dao;
    private UserAccount currentUser;
    private AssetInstallationService assetInstallationService;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private UploadedFile file;
    private List<ArrivalItemImport> importFailedDataList;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        assetInstallationService=WebUtil.getBean(AssetInstallationService.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<ArrivalListItem> getDAO() {
    	return dao;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        if(!Objects.equals(currentUser.getOrgLevel(), 0)){
            if(Objects.equals(currentUser.getOrgLevel(), 1)){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            }else if(Objects.equals(currentUser.getOrgLevel(), 2)){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            }else{
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        this.file = event.getFile();
        List<ArrivalItemImport> importList = ListUtil.newArrayList();
        EasyExcelFactory.read(file.getInputstream(), ArrivalItemImport.class, new ArrivalItemImportHandel(importList)).autoCloseStream(true).sheet(0).doRead();
        handleExcel(importList);
    }

    public List<ArrivalItemImport> handleExcel(List<ArrivalItemImport> importList) throws IOException {
        importFailedDataList = ListUtil.newArrayList();
        List<UserAccount> workerList = assetInstallationService.getWorkerList();
        List<OrgInfo> orgList = orgInfoRepository.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(currentUser.getSiteUID());
        Map<String, OrgInfo> orgMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getName, o -> o));
        List<AssetTypeGroup> assetTypeGroupList = assetTypeGroupRepository.listAssetTypeGroupInHierarchyByUser(currentUser.getUid());
        Set<String> clinicalDeptUidList = new HashSet<>(ListUtil.newArrayList("-1"));
        Set<String> siteUidList = new HashSet<>(ListUtil.newArrayList("-1"));
        List<ArrivalListItem> saveList = ListUtil.newArrayList();
        for (int row = 0; row < importList.size(); row++) {
            ArrivalItemImport arrivalItemImport = importList.get(row);
            StringBuilder errMsg = new StringBuilder();
            OrgInfo site = orgMap.get(arrivalItemImport.getSite());
            OrgInfo clinicalDept = orgMap.get(arrivalItemImport.getClinicalDept());
            if (ObjectUtil.isNotEmpty(site)) {
                if (ObjectUtil.isEmpty(site) || !Objects.equals(site.getOrgLevel(), 3)) {
                    errMsg.append("院区不存在;");
                } else {
                    siteUidList.add(site.getUid());
                    arrivalItemImport.setSiteUID(site.getUid());
                }
            }
             if (ObjectUtil.isNotEmpty(arrivalItemImport.getClinicalDept())) {
                 if (ObjectUtil.isNotEmpty(site)) {
                     if (ObjectUtil.isEmpty(clinicalDept) || !clinicalDept.getSiteUID().equals(site.getUid())) {
                         errMsg.append("科室不存在;");
                     } else {
                         clinicalDeptUidList.add(clinicalDept.getUid());
                         arrivalItemImport.setClinicalDeptUID(clinicalDept.getUid());
                     }
                 } else {
                     errMsg.append("院区不存在;");
                 }
            }
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getAssetOwner())) {
                List<UserAccount> userAccounts = workerList.stream().filter(u -> u.getName().equals(arrivalItemImport.getAssetOwner())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(userAccounts) || userAccounts.size() > 1) {
                    errMsg.append("维修负责人不存在/无法关联;");
                } else {
                    arrivalItemImport.setAssetOwnerUID(userAccounts.get(0).getUid());
                }
            }
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getAssetOwner2())) {
                List<UserAccount> userAccounts = workerList.stream().filter(u -> u.getName().equals(arrivalItemImport.getAssetOwner2())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(userAccounts) || userAccounts.size() > 1) {
                    errMsg.append("维修负责人2不存在/无法关联;");
                } else {
                    arrivalItemImport.setAssetOwnerUID2(userAccounts.get(0).getUid());
                }
            }
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getAssetOwner3())) {
                List<UserAccount> userAccounts = workerList.stream().filter(u -> u.getName().equals(arrivalItemImport.getAssetOwner3())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(userAccounts) || userAccounts.size() > 1) {
                    errMsg.append("维修负责人3不存在/无法关联;");
                } else {
                    arrivalItemImport.setAssetOwnerUID3(userAccounts.get(0).getUid());
                }
            }
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getAssetTypeGroup())) {
                List<AssetTypeGroup> assetTypeGroups = assetTypeGroupList.stream().filter(o -> o.getName().equals(arrivalItemImport.getAssetTypeGroup())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(assetTypeGroups) || assetTypeGroups.size() > 1) {
                    errMsg.append("资产分类不存在/无法关联;");
                } else {
                    arrivalItemImport.setEncoded(assetTypeGroups.get(0).getEncoded());
                }
            }
            if (errMsg.length() > 0) {
                arrivalItemImport.setExcelRow(row + 2);
                arrivalItemImport.setErrMsg(errMsg.toString());
                importFailedDataList.add(arrivalItemImport);
            }
        }
        List<UserAccount> clinicalOwnerList = Optional.ofNullable(assetInstallationService.getClinicalOwnerList(clinicalDeptUidList)).orElse(Collections.emptyList());
        List<SearchFilter> parentAssetSearchFilters = ListUtil.newArrayList();
        parentAssetSearchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        parentAssetSearchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        parentAssetSearchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
        parentAssetSearchFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull,null));
        parentAssetSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteUidList));
        parentAssetSearchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUidList));
        List<AssetInfo> parentAssets = assetInfoRepository.findBySearchFilter(parentAssetSearchFilters);
        Set<String> errIds = importFailedDataList.stream().map(ArrivalItemImport::getId).collect(Collectors.toSet());
        List<String> ids = importList.stream().map(ArrivalItemImport::getId).collect(Collectors.toList());
        List<ArrivalListItem> arrivalItems = dao.findByIdIn(ids);
        Map<String, ArrivalListItem> arrivalItemMap = arrivalItems.stream().collect(Collectors.toMap(ArrivalListItem::getId, t -> t));
        for (int row = 0; row < importList.size(); row++) {
            StringBuilder errMsg = new StringBuilder();
            ArrivalItemImport arrivalItemImport = importList.get(row);
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getParentAsset())) {
                List<AssetInfo> assetInfos = parentAssets.stream().filter(asset -> asset.getName().equals(arrivalItemImport.getParentAsset()) && asset.getSiteUID().equals(arrivalItemImport.getSiteUID()) && asset.getClinicalDeptUID().equals(arrivalItemImport.getClinicalDeptUID())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(assetInfos) || assetInfos.size() > 1) {
                    errMsg.append("主设备不存在/无法关联;");
                } else {
                    arrivalItemImport.setParentAssetId(assetInfos.get(0).getId());
                }
            }
            if (ObjectUtil.isNotEmpty(arrivalItemImport.getClinicalOwner())) {
                List<UserAccount> accounts = clinicalOwnerList.stream().filter(u -> u.getName().equals(arrivalItemImport.getClinicalOwner()) && u.getOrgInfoUID().equals(arrivalItemImport.getClinicalDeptUID())).collect(Collectors.toList());
                if (ObjectUtil.isEmpty(accounts) || accounts.size() > 1) {
                    errMsg.append("使用责任人不存在/无法关联;");
                } else {
                    arrivalItemImport.setClinicalOwnerUid(accounts.get(0).getUid());
                }
            }
            if (errMsg.length() > 0) {
                arrivalItemImport.setExcelRow(row + 2);
                arrivalItemImport.setErrMsg(arrivalItemImport.getErrMsg() + errMsg);
                if (!errIds.contains(arrivalItemImport.getId())) {
                    importFailedDataList.add(arrivalItemImport);
                }
            } else {
                ArrivalListItem arrivalListItem = arrivalItemMap.get(arrivalItemImport.getId());
                BeanUtils.copyProperties(arrivalItemImport, arrivalListItem, beanNullProperty(arrivalItemImport));
                saveList.add(arrivalListItem);
            }
        }
        if (ObjectUtil.isEmpty(importFailedDataList)) {
            dao.save(saveList);
            WebUtil.addSuccessMessage("导入成功！");
        } else {
            WebUtil.addErrorMessage("导入失败！");
        }
        return importFailedDataList;
    }

    public static String[] beanNullProperty(ArrivalItemImport arrivalItemImport) {
        final BeanWrapper src = new BeanWrapperImpl(arrivalItemImport);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullProperty = new HashSet<>();
        for (PropertyDescriptor item : pds) {
            if (src.getPropertyValue(item.getName()) == null) {
                nullProperty.add(item.getName());
            }
        }
        String[] properties = new String[nullProperty.size()];
        return nullProperty.toArray(properties);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<ArrivalItemImport> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<ArrivalItemImport> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}
    
}

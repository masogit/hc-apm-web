package com.ge.apm.service.sharedAsset;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.dao.mapper.SharedAssetMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedAssetStockService {

    @Autowired
    private SharedAssetMapper sharedAssetMapper;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private WorkflowConfigRepository wfCfigDao;

    public void buildDefaultFilters(Wrapper<AssetInfo> ew) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

        ew.andNew()
            .in("asset_tenant_uid", orgUidList)
            .or().in("asset_institution_uid", orgUidList)
            .or().in("asset_hospital_uid", orgUidList)
            .or().in("asset_site_uid", orgUidList)
            .or().in("clinical_dept_uid", orgUidList);
    }

    public void addFiltersByQueryType(Wrapper<AssetInfo> ew, int queryType) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());
        switch (queryType) {
            case 1 :
                ew.andNew().eq("asset_status", 1).or().eq("asset_status", 3);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            case 2:
                ew.andNew().notIn("asset_dept_uid", allocationCenterUids);
                break;
            case 3:
                ew.andNew().ne("asset_status", 1).andNew().ne("asset_status", 3);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            default:
        }
    }

    public Integer getTotalRowCount(Wrapper<AssetInfo> wrapper) {
        return sharedAssetMapper.getTotalRowCount(wrapper);
    }

    public List<AssetInfo> loadAllSharedAssets(Pagination pageRequest, Wrapper<AssetInfo> wrapper) {
        List<AssetInfo> assetList = sharedAssetMapper.loadSharedAssets(pageRequest, wrapper);
        return assetList;
    }

}

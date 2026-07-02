package com.ge.apm.view.nurseSite;

import com.ge.apm.dao.AssetOptRecordRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.AssetOptRecord;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.service.nurseSite.AssetOptRecordService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
public class AssetOptUnClosedRecordController extends GenericCRUDUUIDController<AssetOptRecord> {

    private AssetOptRecordRepository assetOptRecordRepository;
    private UserOrgInfoRepository userOrgInfoRepository;
    private UserAccountRepository userAccountRepository;
    private OrgInfoRepository orgInfoRepository;
    private AssetOptRecordService service;

    private UserAccount currentUser;

    @Override
    protected void init() {
        super.init();
        assetOptRecordRepository = WebUtil.getBean(AssetOptRecordRepository.class);
        userOrgInfoRepository = WebUtil.getBean(UserOrgInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        service = WebUtil.getBean(AssetOptRecordService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<AssetOptRecord> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<SearchFilter> searchFilters = this.searchFilters;
        Set<String> setClinicalUuids = findAccessClinicalUidListByUserUid(currentUser.getUid());
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(setClinicalUuids)));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 0));
        if (currentUser.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet()));
        }
        searchFilters.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.NE, true));
        Page<AssetOptRecord> page = assetOptRecordRepository.findBySearchFilter(searchFilters, pageRequest);
        //supplementUserName(page.getContent());
        //service.supplementCostTime(page.getContent());
        return page;
    }

    public void supplementUserName(List<AssetOptRecord> assetOptRecordList){
        for (AssetOptRecord r : assetOptRecordList){
            if(StrUtil.isNotEmpty(r.getSiteUID())){
                OrgInfo orgInfo = orgInfoRepository.findByUid(r.getSiteUID());
                if(orgInfo != null){
                    r.setAssetInfoSiteName(orgInfo.getName());
                }
            }
        }
    }

    private Set<String> findAccessClinicalUidListByUserUid(String userUid) {
        Set<String> listResult = new HashSet<>();
        List<UserOrgInfo> listUserOrgInfo = userOrgInfoRepository.findOrgUidListByUserUid(userUid);
        if (ObjectUtil.isNotEmpty(listUserOrgInfo)) {
            for (UserOrgInfo userOrgInfo : listUserOrgInfo) {
                listResult.add(userOrgInfo.getOrgUid());
            }
        }
        if (CollectionUtils.isNotEmpty(listResult)) {
            Set<String> tempResult = orgInfoRepository.findClinicalDeptOrgUidListByOrgUid(listResult);
            if (tempResult != null) {
                listResult.addAll(tempResult);
            }
        }
        return listResult;
    }

    @Override
    protected GenericRepositoryUUID<AssetOptRecord> getDAO() {
        return assetOptRecordRepository;
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        return service.fetchOrgInfoByUid(uid);
    }
}

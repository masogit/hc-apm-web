package com.ge.apm.view.his;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisEquipmentSfxmMatchRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisEquipmentSfxmMatch;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisAssetGroupListController extends JpaCRUDController<HisAssetGroupAssetMatch> implements ServerEventInterface {

    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchDao;
    private OrgInfoRepository orgInfoRepository;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;
    private HisEquipmentSfxmMatchRepository hisEquipmentSfxmMatchDao;
    private HisOrgComponentController orgComponentController;
    private HisAssetGroupEditController hisAssetGroupEditController;

    @Getter
    @Setter
    private String groupType;

    @Override
    protected void init(){
        hisAssetGroupAssetMatchDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        hisEquipmentSfxmMatchDao = WebUtil.getBean(HisEquipmentSfxmMatchRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        hisAssetGroupEditController = WebUtil.getBean(HisAssetGroupEditController.class);

        filterBySite = false;
    }

    @Override
    protected GenericRepository<HisAssetGroupAssetMatch> getDAO() {
        return null;
    }

    @Override
    protected Page<HisAssetGroupAssetMatch> loadData(PageRequest pageRequest) {
        if (null == searchFilters) {
            searchFilters = new ArrayList<>();
        }

        removeFilterOnField("orgInfoUid");
        removeFilterOnField("tenantUid");

        if (orgComponentController.getSelectedOrg() != null) {
            List<String> orgInfoUids = orgInfoRepository.getSiteOrgUidsByParentUid(orgComponentController.getSelectedOrg().getUid());
            searchFilters.add(new SearchFilter("orgInfoUid", SearchFilter.Operator.IN, orgInfoUids));
        }
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));

        Page<HisAssetGroupAssetMatch> hisAssetGroupAssetMatches = hisAssetGroupAssetMatchDao.findBySearchFilter(this.searchFilters, pageRequest);
        List<HisAssetGroupAssetMatch> content = hisAssetGroupAssetMatches.getContent();
        return hisAssetGroupAssetMatches;
    }

    @Override
    public void prepareCreate(){
        hisAssetGroupEditController.onServerEvent(null, null);

    }

    @Override
    public void prepareEdit() {
        hisAssetGroupEditController.onServerEvent(null, selected);
    }

    @Override
    public void delete(){

        List<HisEquipmentSfxmMatch> hisEquipmentSfxmMatchList = hisEquipmentSfxmMatchDao.findByHisGroupMatchUid(selected.getUuid());
        if(!hisEquipmentSfxmMatchList.isEmpty()){
            WebUtil.addErrorMessage("已绑定收费项目，请先取消匹配收费项目");
            return;
        }

        if(selected.getHisAssetGroup().getType().equals("Income")){
            String hisAssetGroupUidAndClinicalDeptUid = selected.getAssetGroupUid() + selected.getClinicalDeptUid();
            hisAssetGroupClinicalMatchDao.deleteById(hisAssetGroupUidAndClinicalDeptUid);
        }else{
            hisAssetGroupClinicalMatchDao.deleteAssetByHisAssetGroupAndOrgInfoUid(selected.getAssetGroupUid(),selected.getOrgInfoUid());
        }
        hisAssetGroupAssetMatchDao.delete(selected);
        selected = null;
    }

    public HisAssetGroupAssetMatch onSelectAssetGroupAssetMatch() {
        return selected;
    }

    public String getOrgNameByUid(String orgInfoUid){
        OrgInfo orgInfo = orgInfoRepository.findByUid(orgInfoUid);
        if(!StringUtils.isEmpty(orgInfo)){
            return orgInfo.getName();
        }
        return orgInfoUid;
    }

    public Integer getAssetCountsByGroupUidAndDeptUid(String assetGroupUid,String clinicalDeptUid){
        return hisAssetGroupClinicalMatchDao.countByGroupUidAndDeptUid(assetGroupUid+clinicalDeptUid);
    }

    public Integer getAssetCountsByGroupUidAndOrgInfoUid(String assetGroupUid,String siteUid){
        return hisAssetGroupClinicalMatchDao.countByGroupUidAndOrgInfoUid(assetGroupUid,siteUid);
    }

    public Integer getAssetCountsByhisAssetGroupAssetMatch(HisAssetGroupAssetMatch hisAssetGroupAssetMatch){
        if(hisAssetGroupAssetMatch.getHisAssetGroup()!=null){
            if(hisAssetGroupAssetMatch.getHisAssetGroup().getType().equals("Cost")){
                return    getAssetCountsByGroupUidAndOrgInfoUid(hisAssetGroupAssetMatch.getAssetGroupUid(),hisAssetGroupAssetMatch.getOrgInfoUid());
            }else{
                return    getAssetCountsByGroupUidAndDeptUid(hisAssetGroupAssetMatch.getAssetGroupUid(),hisAssetGroupAssetMatch.getClinicalDeptUid());
            }
        }
        return null;

    }

}

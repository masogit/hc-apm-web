
package com.ge.apm.view.his;

import com.ge.apm.dao.HisAdjustBusinessJobRepository;
import com.ge.apm.dao.HisEquipmentSfxmMatchRepository;
import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.his.HisEquipmentSfxmMatch;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisChargeCodeUnmatchController extends GenericCRUDUUIDController<HisEquipmentSfxmMatch> {
    private HisEquipmentSfxmMatchRepository sfxmMatchRepository;
    private HisEquipmentSfxmRepository sfxDao;
    private HisAdjustBusinessJobRepository hisAdjustBusinessJobRepository;
    private HisOrgComponentController orgComponentController;

    @Getter@Setter
    private String eqId;
    @Getter@Setter
    private String assetName;
    @Getter@Setter
    private String hisGroupName;
    @Getter@Setter
    private List<HisEquipmentSfxmMatch> currentPages;
    @Getter @Setter
    private HisAdjustBusinessJob hisAdjustBusinessJob;

    public void prepareCreateSart() {
        crudActionName = "Create";
    }

    @Override
    protected HisEquipmentSfxmMatchRepository getDAO() {
        return sfxmMatchRepository;
    }

    @Override
    protected void init() {
        sfxDao = WebUtil.getBean(HisEquipmentSfxmRepository.class);
        sfxmMatchRepository = WebUtil.getBean(HisEquipmentSfxmMatchRepository.class);
        hisAdjustBusinessJobRepository = WebUtil.getBean(HisAdjustBusinessJobRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
    }

    @Override
    protected Page<HisEquipmentSfxmMatch> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<SearchFilter>();
        if (StringUtils.isNotBlank(eqId)) {
            searchFilters.add(new SearchFilter("sfxmId", SearchFilter.Operator.EQ, eqId.trim()));
        }
        if (StringUtils.isNotBlank(assetName)) {
            searchFilters.add(new SearchFilter("sfxmName", SearchFilter.Operator.LIKE, assetName.trim()));
        }
        if (StringUtils.isNotBlank(hisGroupName)) {
            searchFilters.add(new SearchFilter("hisAssetGroup.name", SearchFilter.Operator.LIKE, hisGroupName.trim()));
        }
        if(orgComponentController.getSelectedOrg() != null){
            searchFilters.add(new SearchFilter("hosId", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getSiteUID()));
        }

        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));

        Page<HisEquipmentSfxmMatch> page = sfxmMatchRepository.findBySearchFilter(this.searchFilters, pageRequest);
        currentPages = page.getContent();
        return page;
    }

    @Override
    public List<HisEquipmentSfxmMatch> getItemList() {
        return sfxmMatchRepository.find();
    }

    public void initDate(String sfxmId){
        hisAdjustBusinessJob = new HisAdjustBusinessJob();
        hisAdjustBusinessJob.setSfxmId(sfxmId);
    }

    @Transactional
    public void onSelectCurrent(String sfxmId) {
        List<HisEquipmentSfxmMatch>  sfxmMatches =sfxmMatchRepository.findBySfxmIdAAndTenantUid(sfxmId,UserContextService.getCurrentUserAccount().getTenantUID());
        if(sfxmMatches !=null && !sfxmMatches.isEmpty()) {
            if(hisAdjustBusinessJob.getDataStartDate() == null || hisAdjustBusinessJob.getDataEndDate() == null || hisAdjustBusinessJob.getDataEndDate().compareTo(hisAdjustBusinessJob.getDataStartDate()) <= 0){
                WebUtil.addSuccessMessage("取消收费项目匹配填写开始时间或结束时间错误");
                return;
            }
            hisAdjustBusinessJob.setOrgCode(sfxmMatches.get(0).getHosId());
            hisAdjustBusinessJob.setTaskType(1);//1 取消收费代码匹配
            hisAdjustBusinessJob.setBusinessId(sfxmId);///His收费项目代码
            hisAdjustBusinessJob.setOldStrJson(JSONArray.fromObject(sfxmMatches).toString());
            hisAdjustBusinessJob.setTaskStatus(0);
            hisAdjustBusinessJob.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
            hisAdjustBusinessJob.setCreatedDate(new Date());
            hisAdjustBusinessJob.setTenantUid(sfxmMatches.get(0).getTenantUid());
            hisAdjustBusinessJob.setSiteUid(sfxmMatches.get(0).getSiteUid());
            hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);

            sfxmMatchRepository.delete(sfxmMatches);
            sfxDao.updateIsMatched(false, sfxmMatches.get(0).getHisEquipmentSfxmId());
        }
    }

    public void cancel() {
        this.selected = null;
    }

    public void prepareEditSart() {
        crudActionName = "Edit";
    }

}



package com.ge.apm.view.train;

import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.domain.TrainInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class TrainMainController extends GenericCRUDUUIDController<TrainInfo> {

    ConfigItemValueRepository configItemValueRepository;

    UserAccount userAccount;

    @Override
    protected void init() {
        super.init();
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        userAccount = UserContextService.getCurrentUserAccount();
    }

    public boolean showMvs(){
        boolean show = false;
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(userAccount.getSiteUID(), userAccount.getHospitalUID(),userAccount.getInstitutionUID(), userAccount.getTenantUID(), "trainMvsShowFlag");
        if(StrUtil.isNotEmpty(value) && "true".equals(value)){
            show = true;
        }
        return show;
    }

    @Override
    protected GenericRepositoryUUID<TrainInfo> getDAO() {
        return null;
    }
}

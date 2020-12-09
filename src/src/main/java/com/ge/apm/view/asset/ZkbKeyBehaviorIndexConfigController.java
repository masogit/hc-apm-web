package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbKeyBehaviorIndexConfigRepository;
import com.ge.apm.domain.ZkbKeyBehaviorIndexConfig;
import com.ge.apm.service.zkb.ZkbKeyBehaviorIndexConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbKeyBehaviorIndexConfigController extends GenericCRUDUUIDController<ZkbKeyBehaviorIndexConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbKeyBehaviorIndexConfigController.class);

    /**
     * 智康宝KPI config service
     */
    private ZkbKeyBehaviorIndexConfigService zkbKeyBehaviorIndexConfigService;

    /**
     * 智康宝KPI config repository
     */
    private ZkbKeyBehaviorIndexConfigRepository zkbKeyBehaviorIndexConfigRepository;

    /**
     * 租户UID
     */
    private String tenantUID;

    /**
     * 列表
     */
    private List<ZkbKeyBehaviorIndexConfig> zkbKeyBehaviorIndexConfigList;

    public final static int totalScore = 100;

    @Override
    protected void init() {
        super.init();
        zkbKeyBehaviorIndexConfigService = WebUtil.getBean(ZkbKeyBehaviorIndexConfigService.class);
        zkbKeyBehaviorIndexConfigRepository = WebUtil.getBean(ZkbKeyBehaviorIndexConfigRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbKeyBehaviorIndexConfig> getDAO() {
        return zkbKeyBehaviorIndexConfigRepository;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public List<ZkbKeyBehaviorIndexConfig> getZkbKeyBehaviorIndexConfigList() {
        return zkbKeyBehaviorIndexConfigList;
    }

    public void setZkbKeyBehaviorIndexConfigList(List<ZkbKeyBehaviorIndexConfig> zkbKeyBehaviorIndexConfigList) {
        this.zkbKeyBehaviorIndexConfigList = zkbKeyBehaviorIndexConfigList;
    }
    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        if(this.selected.getTotalFullStore()!=ZkbKeyBehaviorIndexConfigController.totalScore)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_totalFullScoreError"));
            return;
        }
        this.selected.setDefaultConfig(0);
        if(this.zkbKeyBehaviorIndexConfigService.checkTenantExist(this.selected))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_tenantExist"));
            return;
        }
        super.save();
        this.selected = null;
    }
}

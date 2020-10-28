package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.ZkbPartOrderUserConfig;
import com.ge.apm.service.zkb.ZkbPartOrderUserConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author 503202755 2020/9/25
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderUserConfigController extends GenericCRUDUUIDController<ZkbPartOrderUserConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbPartOrderUserConfigController.class);

    /**
     * service
     */
    private ZkbPartOrderUserConfigService zkbPartOrderUserConfigService;

    /**
     * dao
     */
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbPartOrderUserConfig> zkbPartOrderUserConfigPage;


    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbPartOrderUserConfigService = WebUtil.getBean(ZkbPartOrderUserConfigService.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrderUserConfig> getDAO() {
        return this.zkbPartOrderUserConfigRepository;
    }

    /**
     * 列表查询数据
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbPartOrderUserConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        zkbPartOrderUserConfigPage=zkbPartOrderUserConfigRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbPartOrderUserConfigPage;
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        super.save();
        this.selected = null;
    }
}

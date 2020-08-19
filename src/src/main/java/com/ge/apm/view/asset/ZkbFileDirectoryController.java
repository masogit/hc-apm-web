package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbFileDirectoryRepository;
import com.ge.apm.domain.ZkbFileDirectory;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.stream.Collectors;

/**
 * @author 503202755 2020/7/30
 */
@ManagedBean
@ViewScoped
public class ZkbFileDirectoryController extends GenericCRUDUUIDController<ZkbFileDirectory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbFileDirectoryController.class);


    /**
     * 目录关系service
     */
    private ZkbFileDirectoryService zkbFileDirectoryService;

    /**
     * 目录关系Repository
     */
    private ZkbFileDirectoryRepository zkbFileDirectoryRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbFileDirectory> zkbFileDirectoryPage;

    private String actionName;

    @Override
    protected void init() {
        super.init();
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        zkbFileDirectoryRepository = WebUtil.getBean(ZkbFileDirectoryRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            this.selected = zkbFileDirectoryRepository.findById(selectedId);
            prepareEdit();
        }else {
            try {
                prepareCreate();
            } catch (InstantiationException e) {
                LOGGER.info(e.getMessage());
            } catch (IllegalAccessException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    @Override
    protected Page<ZkbFileDirectory> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        zkbFileDirectoryPage=zkbFileDirectoryRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbFileDirectoryPage;
    }

    @Override
    protected GenericRepositoryUUID<ZkbFileDirectory> getDAO() {
        return zkbFileDirectoryRepository;
    }

    /**
     * 获取所有主目录信息
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory(String query) {
        List<ZkbFileDirectory> listZkbFileDirectory = zkbFileDirectoryService.findMainDirectory(this.selected.getId());
        if(CollectionUtils.isEmpty(listZkbFileDirectory))
        {
            return null;
        }
        return listZkbFileDirectory.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
    }

    /**
     * 重写delete方法，如果有子目录信息不让删除
     */
    @Override
    public void delete() {
        if (this.zkbFileDirectoryService.checkSubDirectoryExist(this.selected)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ErrorSubDirectoryExist"));
            return;
        }
        super.delete();
    }

    /**
     *
     * 重写save方法，如果发现多个相同的目录名称，不让保存
     */
    @Override
    public void save() {
        if (this.zkbFileDirectoryService.checkDirectoryNameExist(this.selected))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("ErrorDirectoryNameExist"));
            return;
        }
        if(ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE.equals(this.selected.getLevel()))
        {
            this.selected.setZkbFileDirectory(null);
        }
        super.save();
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}

package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionSpecificationRepository;
import com.ge.apm.domain.acquisition.AcquisitionSpecification;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备分类规范管理控制器
 */
@ManagedBean
@ViewScoped
public class AcquisitionSpecificationListController extends GenericCRUDUUIDController<AcquisitionSpecification> {

    private AcquisitionSpecificationRepository dao;
    private String selectedId;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionSpecificationRepository.class);
    }

    @Override
    protected Page<AcquisitionSpecification> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        
        // 添加排序
        Sort sort = new Sort(Sort.Direction.ASC, "majorCategory", "firstLevelCategory", "secondLevelCategory");
        PageRequest sortedPageRequest = new PageRequest(
            pageRequest.getPageNumber(), 
            pageRequest.getPageSize(), 
            sort
        );
        
        Page<AcquisitionSpecification> pageResult = dao.findBySearchFilter(this.searchFilters, sortedPageRequest);
        return pageResult;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        
        // 添加租户过滤
  /*      this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,
            UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));*/
    }

    @Override
    protected GenericRepositoryUUID<AcquisitionSpecification> getDAO() {
        return dao;
    }
    
    /**
     * 获取大类选项列表
     */
    public List<String> getMajorCategoryOptions() {
        List<String> options = new ArrayList<>();
        options.add("设备");
        options.add("软件");
        options.add("器械");
        return options;
    }
    
    /**
     * 获取设备等级选项列表 [{三甲,2},{二甲,1},{其他,0}]
     */
    public List<SelectItem> getDeviceGradeOptions() {
        List<SelectItem> options = new ArrayList<>();
        options.add(new SelectItem(2, "三甲"));
        options.add(new SelectItem(1, "二甲"));
        options.add(new SelectItem(0, "其他"));
        return options;
    }
    
    /**
     * 获取模态类型选项列表 [CT, MR]
     */
    public List<String> getModalityOptions() {
        List<String> options = new ArrayList<>();
        options.add("CT");
        options.add("MR");
        return options;
    }
    
    /**
     * 获取当前操作类型 (Create/Edit)
     */
    public String getAction() {
        return crudActionName;
    }
    
    /**
     * 获取选中的ID
     */
    public String getSelectedId() {
        return selectedId;
    }
    
    /**
     * 设置选中的ID
     */
    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }
    
    /**
     * 预渲染视图，加载数据
     */
    public void preRenderView() {
        // 如果是编辑模式且有selectedId，从数据库加载数据
        if ("Edit".equals(crudActionName) && selectedId != null && !selectedId.isEmpty()) {
            try {
                selected = dao.findOne(selectedId);
            } catch (Exception e) {
                WebUtil.addErrorMessage("加载数据失败: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }
}

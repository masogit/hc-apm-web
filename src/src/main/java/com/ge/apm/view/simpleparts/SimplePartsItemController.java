package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsCategoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsCategory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.service.simpleparts.SimplePartsItemService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
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
import java.util.*;

@ManagedBean
@ViewScoped
public class SimplePartsItemController extends GenericCRUDUUIDController<SimplePartsItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsItemController.class);

    private UserContextService userContextService;
    private SimplePartsItemRepository partsItemRepository;
    private SimplePartsCategoryRepository partsCategoryRepository;
    private SimplePartsItemService partsItemService;

    @Getter
    @Setter
    private TreeNode partsCategoryTree;
    @Getter
    @Setter
    private SimplePartsCategory selectedPartsCategory;
    @Override
    protected GenericRepositoryUUID<SimplePartsItem> getDAO() {
        return null;
    }
    @Getter @Setter
    private Map<String,Boolean> selectedPartsMap = new HashMap<>();
    @Getter @Setter
    private Set<String> selectedPartsIds = new HashSet<>();

    @Override
    protected void init() {
        partsItemService = WebUtil.getBean(SimplePartsItemService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        partsItemRepository = WebUtil.getBean(SimplePartsItemRepository.class);
        partsCategoryRepository = WebUtil.getBean(SimplePartsCategoryRepository.class);
    }

    public void initPartsCategoryTree() {
        if(partsCategoryTree==null) partsCategoryTree = partsItemService.buildPartsCategoryTree(userContextService.getLoginUser().getTenantUID());
    }

    public TreeNode getCurrentPartsCategoryTree() {
        return partsCategoryTree;
    }

    @Override
    protected Page<SimplePartsItem> loadData(PageRequest pageRequest) {
        if(searchFilters==null) searchFilters = new ArrayList<>();
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        Page<SimplePartsItem> res = partsItemRepository.findBySearchFilter(this.searchFilters, pageRequest);
        return res;
    }

    public SimplePartsItem onSelectPartsItem() {
        return selected;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        initPartsCategoryTree();
    }


    @Override
    public void prepareEdit() {
        super.prepareEdit();
        initPartsCategoryTree();
        if (null != selected.getCategoryCode()) {
            selectedPartsCategory = partsCategoryRepository.findByEncoded(selected.getCategoryCode(), userContextService.getLoginUser().getTenantUID());
        } else {
            selectedPartsCategory = null;
        }
    }

    @Override
    public void delete() {
        partsItemRepository.delete(selected);
    }

    public void setSelected(SimplePartsItem selectedObj) {
        if (selectedObj == null) {
            return;
        }
        selected = selectedObj;
    }

    public String getSelectedPartsCategoryName() {

        if (null == selectedPartsCategory) {
            return null;
        } else {
            return selectedPartsCategory.getName();
        }
    }

    public void onSelectPartsCategoryTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        this.selectedPartsCategory = (SimplePartsCategory) node.getData();
    }


    public void saveSelectedPartsEdit() {
        if(selectedPartsIds.size()<1) {
            WebUtil.addErrorMessage("请选择备件！");
            return;
        }
        if(null==selectedPartsCategory) {
            WebUtil.addErrorMessage("备件分类选项不能为空！");
            return;
        }
        partsItemService.saveSelectedPartsEdit(selectedPartsIds,selectedPartsCategory);
        WebUtil.addSuccessMessage("修改成功！");
        selectedPartsMap = new HashMap<>();
        selectedPartsIds = new HashSet<>();
    }

    public void savePartsItem() {
        if (null != selectedPartsCategory) {
            selected.setCategoryCode(selectedPartsCategory.getEncoded());
            selected.setCategoryName(selectedPartsCategory.getName());
        } else {
            selected.setCategoryCode(null);
            selected.setCategoryName(null);
        }
        selected.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        partsItemService.save(selected);
        selected = null;
    }

}

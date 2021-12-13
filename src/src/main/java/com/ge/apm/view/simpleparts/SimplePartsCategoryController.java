package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsCategoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsCategory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.simpleparts.SimplePartsCategoryService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@Data
public class SimplePartsCategoryController extends GenericCRUDController<SimplePartsCategory> implements Serializable, ServerEventInterface {

    private static final long serialVersionUID = -7855688925721038748L;
    private SimplePartsCategoryRepository partsCategoryDao;
    private SimplePartsCategoryService partsGroupService;
    private SimplePartsItemRepository partsItemRepository;
    private TreeNode partsGroupingTree;
    private TreeNode selectedPartsGroupNodes;
    private UserAccount loginUser;

    boolean createTopLevelNode = false;
    private String retrieval;
    private String isReadOnly = null;
    private Boolean isCreate;
    private Object object;
    private String tenantUid;
    private String parentUid;
    private String namePath;
    private String encoded;


    @Override
    protected GenericRepository<SimplePartsCategory> getDAO() {
        return null;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    protected void init() {
        loginUser = UserContextService.getCurrentUserAccount();
        partsGroupService = WebUtil.getBean(SimplePartsCategoryService.class);
        partsCategoryDao = WebUtil.getBean(SimplePartsCategoryRepository.class);
        partsItemRepository = WebUtil.getBean(SimplePartsItemRepository.class);
        tenantUid = loginUser.getTenantUID();
        buildOrdTree();
    }

    public void buildOrdTree() {

        partsGroupingTree = partsGroupService.buildPartsCategoryInTree(tenantUid);
    }


    public void filterNode() {
        partsGroupService.filterTreeNode(partsGroupingTree, retrieval, -1, -1);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        isReadOnly = null;
        createTopLevelNode = false;
        TreeNode node = event.getTreeNode();
        selected = (SimplePartsCategory) node.getData();
        if (selected.getParentUid() != null) {
            namePath = partsCategoryDao.listPartsCategoryPathInHierarchy(selected.getParentUid());
        } else {
            namePath = null;
        }
        isCreate = false;
    }

    public void prepareCreateTopLevelNode() {
        isReadOnly = "";
        createTopLevelNode = true;
        SimplePartsCategory partsGroup = new SimplePartsCategory();
        this.selected = partsGroup;
        isCreate = true;
    }

    public void prepareCreateNextNode() {
        isReadOnly = "";
        createTopLevelNode = false;
        namePath = partsCategoryDao.listPartsCategoryPathInHierarchy(selected.getId());
        parentUid = selected.getId();
        selected.setEncoded(partsCategoryDao.findById(selected.getId()).getEncoded().trim() + "-");
        selected.setParentUid(parentUid);
        selected.setName(null);
        isCreate = true;
    }

    public void saveSelectedPartsCategory() {
        if (!isCreate) {
                selected.setTenantUid(tenantUid);
                partsCategoryDao.save(selected);
                createTopLevelNode = false;
                buildOrdTree();
                WebUtil.addSuccessMessage("保存成功!");
                return;
        }
        //重复增加不被允许
        boolean isDuplicate = checkName();
        if (isDuplicate) {
            buildOrdTree();
            return;
        }
        selected.setTenantUid(tenantUid);
        SimplePartsCategory partsCategory = partsCategoryDao.findPartsCategoryByNameAndParentUidAndTenantUid(selected.getName(), selected.getParentUid(), selected.getTenantUid());
        String parentEncoded = "";
        if (selected.getParentUid() != null) {
            parentEncoded = partsCategoryDao.findById(selected.getParentUid()).getEncoded().trim();
        }
        if (partsCategory == null && selected.getEncoded().contains(parentEncoded) && selected.getEncoded().length()> parentEncoded.length()+1 && StringUtils.isEmpty(partsCategoryDao.findEncodedById(selected.getEncoded(), selected.getTenantUid()))) {
            partsCategory = selected;
            partsCategory.setId(null);
            partsCategoryDao.save(partsCategory);
            isCreate = false;
            createTopLevelNode = false;
            WebUtil.addSuccessMessage("保存成功!");
        } else if (StringUtils.isNotEmpty(partsCategoryDao.findEncodedById(selected.getEncoded(), selected.getTenantUid()))) {
            WebUtil.addErrorMessage(selected.getEncoded() + "编码已存在");
        } else if(selected.getEncoded().length()== parentEncoded.length()+ 1 || !selected.getEncoded().contains(parentEncoded)){
            WebUtil.addErrorMessage(selected.getEncoded() + "编码格式不正确，请补全生成后编码");
        }
        buildOrdTree();
    }

    public boolean checkName() {
        SimplePartsCategory partsGroup = partsCategoryDao.findPartsCategoryByNameAndParentUidAndTenantUid(selected.getName(), selected.getParentUid(), selected.getTenantUid());
        if (partsGroup != null) {
            WebUtil.addErrorMessage(selected.getName() + "已经存在");
            return true;
        }
        return false;
    }


    public void deleteSelectedPartsCategory() {
        encoded = selected.getEncoded() == null ? "" : selected.getEncoded().trim();
        List<SimplePartsItem> partsItemInfos = partsItemRepository.findByCategoryCode(encoded, UserContextService.getCurrentUserAccount().getTenantUID());
        if (partsItemInfos.size() >= 1) {
            WebUtil.addErrorMessage(selected.getName() + "已绑定备件");
            return;
        }
        List<SimplePartsCategory> childs = partsCategoryDao.findChildByParentUid(selected.getId());
        if (childs == null || childs.size() == 0) {
            partsCategoryDao.delete(selected);
        } else if (childs.size() >= 1) {
            //删除子节点
            for (int i = 0; i < childs.size(); i++) {
                partsCategoryDao.delete(childs.get(i));
            }
            //删除自己
            partsCategoryDao.delete(selected);
        }
        buildOrdTree();
        namePath = null;
        selected = null;
    }
}

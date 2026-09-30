package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsInventoryService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: csh
 * @date: 2020/7/13 13:55
 * @version: 1.0
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsWarehouseEditController extends GenericCRUDUUIDController<SimplePartsWarehouse>  implements ServerEventInterface {
    private static final long serialVersionUID = 1886024138737071702L;

    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsInventoryService partsInventoryService;
    private OrgInfoRepository orgInfoRepository;
    private UaaService uaaService;

    @Getter
    @Setter
    private String treeNodeNameFilter;

    /**
     * 当前登录人可用的最大权限的TreeNode,仓库使用
     */
    @Getter
    @Setter
    private TreeNode enableOrgTree;

    /**
     * 当前操作人选中的TreeNode,仓库使用
     */
    @Getter
    @Setter
    private TreeNode selectedNode;

    /**
     * 仓库本身使用的OrgInfo
     */
    @Getter
    @Setter
    private OrgInfo selectedUserOrg;

    @Getter
    @Setter
    public SimplePartsWarehouse partsWarehouse;

    @Override
    protected void init() {
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        this.rebuildSPTree();
        partsWarehouse = new SimplePartsWarehouse();
    }

    /**
     * @date 2020/7/13 18:17
     * 保存备件仓库
     */
    public String saveWarehouse() {

        if(selectedUserOrg.getOrgType() >= 4){
            WebUtil.addSuccessMessage("仓库所属只支持院区及院区以上级别");
            return null;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        Date currentDate = new Date();
        this.partsWarehouse.setCreatedBy(userAccount.getLoginName());
        this.partsWarehouse.setCreatedDate(currentDate);
        this.partsWarehouse.setSiteUid(this.selectedUserOrg.getSiteUID());
        this.partsWarehouse.setHospitalUid(this.selectedUserOrg.getHospitalUID());
        this.partsWarehouse.setInstitutionUid(this.selectedUserOrg.getInstitutionUID());
        this.partsWarehouse.setTenantUid(this.selectedUserOrg.getTenantUID());

        if(StringUtils.isNotEmpty(partsWarehouse.getId()) && partsWarehouse.getStatus() != null && Objects.equals(partsWarehouse.getStatus(), 0)){
            List<SimplePartsInventory> listPartsInventory = partsInventoryService.findByWarehouseId(partsWarehouse.getId());
            int inventory =listPartsInventory.stream().mapToInt(p -> p.getOnhandQty()).sum();
            if(inventory >0 ){
                WebUtil.addSuccessMessage("当前仓库存有库存,不可禁用");
                return null;
            }
        }

        partsWarehouseService.save(this.partsWarehouse);
        return goCurrentPage();
    }

    private String goCurrentPage() {
        return "PartsWarehouse?faces-redirect=true";
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selected = (SimplePartsWarehouse) eventObject;
    }

    public void preparePartsWarehouseAdd() {
        partsWarehouse = new SimplePartsWarehouse();
    }

    public void preparePartsWarehouseEdit() {
        if(selected == null){
            WebUtil.addSuccessMessage("仓库不存在");
            return;
        }
        partsWarehouse = selected;
        partsWarehouse.setStatus(partsWarehouse.getStatus() == null ? 0 : partsWarehouse.getStatus());
        selectedUserOrg = orgInfoRepository.getByUid(partsWarehouse.getSiteUid());
    }

    public void filterTreeNode() {
        uaaService.filterTreeNode(enableOrgTree, treeNodeNameFilter, -1, -1);
    }

    public void rebuildSPTree() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        //判断登录用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.checkRole(SimplePartsConstant.PARTS_ADMIN_MANAGER)) {
            enableOrgTree = uaaService.getOrgTreeBySite(userAccount.getSiteUID());
        } else {
            OrgInfo orgInfo = orgInfoRepository.getByUid(userAccount.getOrgInfoUID());
            enableOrgTree = uaaService.buildEnableOrgTree(userAccount.getUid(), userAccount.getOrgInfoUID(), orgInfo);
        }
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsWarehouse> getDAO() {
        return partsWarehouseRepository;
    }
}

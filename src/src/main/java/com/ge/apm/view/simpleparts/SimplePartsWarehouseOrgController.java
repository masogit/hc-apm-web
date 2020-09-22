package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseOrgRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseOrg;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseOrgService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: csh
 * @date: 2020/7/13 13:55
 * @version: 1.0
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsWarehouseOrgController extends GenericCRUDUUIDController<SimplePartsWarehouse>  implements ServerEventInterface {
    private static final long serialVersionUID = 1886024138737071702L;

    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseOrgService partsWarehouseOrgService;
    private SimplePartsWarehouseOrgRepository partsWarehouseOrgRepository;

    private OrgInfoRepository orgInfoRepository;

    /**
     * 仓库机构映射上使用的TreeNode
     */
    @Getter
    @Setter
    private TreeNode accessOrgTree;

    /**
     * 仓库机构映射上使用的OrgInfo
     */
    @Getter
    @Setter
    private OrgInfo orgInfoForPartsWareHouseOrg;

    @Getter
    @Setter
    public SimplePartsWareHouseOrg partsWareHouseOrg;

    @Setter
    public List<SimplePartsWareHouseOrg> partsWareHouseOrgList;

    @Override
    protected void init() {
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseOrgService = WebUtil.getBean(SimplePartsWarehouseOrgService.class);
        partsWarehouseOrgRepository = WebUtil.getBean(SimplePartsWarehouseOrgRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selected = (SimplePartsWarehouse) eventObject;
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsWarehouse> getDAO() {
        return partsWarehouseRepository;
    }

    /**
     * 初始化仓机构配置页面
     */
    public void initPartsWarehouseOrg(String uid) {
        SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.getById(uid);
        if (partsWarehouse == null) {
            WebUtil.addSuccessMessage("仓库不存在");
            return;
        }
        List<SimplePartsWareHouseOrg> list = partsWarehouseOrgService.findAllByPartsWarehouseId(partsWarehouse.getId());
        if (list.size() > 0) {
            Set<String> uidSets = list.stream().filter(item -> item.getOrgUid() != null).map(item -> item.getOrgUid()).collect(Collectors.toSet());
            List<OrgInfo> orgList = orgInfoRepository.findByUids(uidSets);
            if (orgList.size() > 0) {
                for (SimplePartsWareHouseOrg partsWareHouseOrg : list) {
                    for (OrgInfo org : orgList) {
                        if (partsWareHouseOrg.getOrgUid().equals(org.getUid())) {
                            partsWareHouseOrg.setOrgName(org.getName());
                        }
                    }
                }
            }
        }
        partsWareHouseOrgList = list;
    }

    public void onSelectOrgTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo nodeData = (OrgInfo) node.getData();
        if (nodeData == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("当前选择组织机构不存在"));
            return;
        }
        Set uidSets = partsWareHouseOrgList.stream().filter(item -> item.getOrgUid() != null).map(item -> item.getOrgUid()).collect(Collectors.toSet());
        if (uidSets.contains(nodeData.getUid())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("该机构已存在"));
            return;
        }
        orgInfoForPartsWareHouseOrg = nodeData;
        if (partsWareHouseOrg == null) {
            partsWareHouseOrg = new SimplePartsWareHouseOrg();
        }
        partsWareHouseOrg.setOrgName(orgInfoForPartsWareHouseOrg.getName());
        partsWareHouseOrg.setOrgUid(orgInfoForPartsWareHouseOrg.getUid());
    }

    public void cancelOrgTreeNode() {
        orgInfoForPartsWareHouseOrg = null;
        partsWareHouseOrg = null;
    }

    public void savePartsWarehouseOrg() {
        if (orgInfoForPartsWareHouseOrg == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("未选择组织机构"));
            return;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        partsWareHouseOrg.setPartsWarehouseId(selected.getId());
        partsWareHouseOrg.setCreatedBy(userAccount.getName());
        partsWareHouseOrg.setCreatedDate(new Date());
        partsWarehouseOrgRepository.save(partsWareHouseOrg);
        partsWareHouseOrgList.add(partsWareHouseOrg);
        cancelOrgTreeNode();
    }

    public List<SimplePartsWareHouseOrg> getPartsWareHouseOrgList() {
        if (partsWareHouseOrgList == null) {
            partsWareHouseOrgList = new ArrayList<SimplePartsWareHouseOrg>();
        }
        SimplePartsWareHouseOrg partsWareHouseOrg = new SimplePartsWareHouseOrg();
        partsWareHouseOrg.setOrgUid(SimplePartsConstant.PARTS_LOCAL_ORG_ID);
        boolean flag = false;
        for (SimplePartsWareHouseOrg info : partsWareHouseOrgList) {
            String uid = info.getOrgUid();
            if (SimplePartsConstant.PARTS_LOCAL_ORG_ID.equals(uid)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            partsWareHouseOrgList = partsWareHouseOrgList.stream()
                    .filter(item -> !SimplePartsConstant.PARTS_LOCAL_ORG_ID.equals(item.getOrgUid()))
                    .collect(Collectors.toList());
            partsWareHouseOrgList.add(partsWareHouseOrg);
        } else {
            partsWareHouseOrgList.add(partsWareHouseOrg);
        }
        return partsWareHouseOrgList;
    }

    public void preparePartsWarehouseOrgAdd() {
        TreeNode root = partsWarehouseOrgService.preparePartsWarehouseOrgAdd();
        accessOrgTree = root;
        partsWareHouseOrg = null;
    }

    public void deletePartsWarehouseOrg(String itemId) {
        SimplePartsWareHouseOrg partsWarehouseOrg = partsWarehouseOrgRepository.findById(itemId);
        if(partsWarehouseOrg == null){
            WebUtil.addSuccessMessage("机构配置不存在");
            return;
        }
        partsWarehouseOrgRepository.delete(partsWarehouseOrg);
        partsWareHouseOrgList = partsWareHouseOrgList.stream()
                .filter(item -> !itemId.equals(item.getId()))
                .collect(Collectors.toList());
    }

}

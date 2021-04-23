package com.ge.apm.view.component;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class FilterMvsSiteOrgTreeComponentController {

    public static final String FILTER_ORG_CHANGE = "FILTER_ORG_CHANGE";

    private OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
    private UaaService uaaService = WebUtil.getBean(UaaService.class);
    private AssetInfoRepository assestInfoRepository = WebUtil.getBean(AssetInfoRepository.class);

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private TreeNode selectedNode;

    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private String orgNameInputFilter;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    protected void init(){

        initMVSOrgTree();
    }

    private void initMVSOrgTree() {
        orgTree = new DefaultTreeNode("org", null, null);
        List<OrgInfo> orgList = orgDao.findSimpleAllAsList().stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        List<String> listTenantUid=assestInfoRepository.findMVSTenantUid();
        List<OrgInfo> newOrgList=new ArrayList<OrgInfo>();
        if(orgList!=null && orgList.size()>0 && listTenantUid!=null && listTenantUid.size()>0){
            for(int i=0;i<orgList.size();++i){
                OrgInfo tempOrgInfo=orgList.get(i);
                if(listTenantUid.contains(tempOrgInfo.getTenantUID())){
                    newOrgList.add(tempOrgInfo);
                }
            }
        }
        if(newOrgList.size()==0){
            newOrgList=orgList;
        }
        buildTree(newOrgList, orgTree);

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setOrgType(0);
        rootOrg.setOrgLevel(0);
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllTenants"));
        orgTree.getChildren().add(0, new DefaultTreeNode("org", rootOrg, null));
    }

    private void buildTree(List<OrgInfo> orgList, TreeNode rootNode) {
        int beginLevel = 0;
        int lastLevel = 4;
        List<OrgInfo> listAddedOrgInfo = new ArrayList<OrgInfo>();
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        while (beginLevel < lastLevel) {
            buildTree(orgList, treeMap,rootNode, listAddedOrgInfo, beginLevel);
            ++beginLevel;
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
    }

    private void buildTree(List<OrgInfo> orgList,Map<String, TreeNode> treeMap, TreeNode rootNode, List<OrgInfo> listAddedOrgInfo, int beginLevel) {
        for (OrgInfo org : orgList) {
            if (!listAddedOrgInfo.contains(org)) {
                String parentUid = org.getParentUID();
                Integer orgLevel = org.getOrgLevel();
                if (StringUtils.isEmpty(parentUid) || orgLevel == 0) {
                    rootNode.getChildren().add(treeMap.get(org.getUid()));
                    listAddedOrgInfo.add(org);
                } else if (orgLevel == beginLevel) {
                    if (treeMap.containsKey(parentUid)) {
                        treeMap.get(parentUid).getChildren().add(treeMap.get(org.getUid()));
                        listAddedOrgInfo.add(org);
                    }
                }
            }
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event) {

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        notifyController();

    }

    public void registerController(ServerEventInterface controller) {
        controllerList.add(controller);
    }

    public void notifyController() {
        for (ServerEventInterface controller: controllerList) {
            controller.onServerEvent(FILTER_ORG_CHANGE, selectedOrg);
        }
    }

    public String getSelectedOrgName() {
        return null == selectedOrg ? null : selectedOrg.getName();
    }

    public void filterTreeNodeByName() {
        uaaService.filterTreeNode(orgTree, orgNameInputFilter, 3, 3);
    }

}

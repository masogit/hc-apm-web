package com.ge.apm.view.component;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class AssetTypeGroupTreeComponentController implements ServerEventInterface {

    public static final String ASSET_TYPE_GROUP_CHANGE = "ASSET_TYPE_GROUP_CHANGE";

    private AssetTypeGroupRepository assetTypeGroupRepository;
    private AssetTypeGroupService assetTypeGroupService;

    @Getter @Setter
    private TreeNode sysAuthGroupUserAccessibleTree;

    @Getter @Setter
    private AssetTypeGroup selectedAssetTypeGroup;

    @Getter @Setter
    private TreeNode selectedNode;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    protected void init(){
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);

        initAssetTypeGroupTree();
    }

    private void initAssetTypeGroupTree() {
        sysAuthGroupUserAccessibleTree = assetTypeGroupService.buildUserAccessibleAssetTypeGroupInTreeWithAncestorIncluded();
    }

    public TreeNode getCurrentAssetTypeGroupTree() {
        return sysAuthGroupUserAccessibleTree;
    }

    public void onSelectAssetTypeGroupTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedAssetTypeGroup = (AssetTypeGroup) node.getData();

        notifyController();
    }

    public String getSelectedAssetTypeGroupName() {
        if (null == selectedAssetTypeGroup) {
            return null;
        } else {
            return selectedAssetTypeGroup.getDisplayName();
        }
    }

    public void registerController(ServerEventInterface controller) {
        controllerList.add(controller);
    }

    public void notifyController() {
        for (ServerEventInterface controller: controllerList) {
            controller.onServerEvent(ASSET_TYPE_GROUP_CHANGE, selectedAssetTypeGroup);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        String str = (String) eventObject;
        List<String> list = Arrays.asList(str.split("#"));
        if (StringUtils.isEmpty(list.get(0))) {
            selectedAssetTypeGroup = null;
        } else {
            selectedAssetTypeGroup = assetTypeGroupRepository.findByEncodedAndTenantUid(list.get(0), list.get(1));
        }
    }
}

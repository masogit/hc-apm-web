package com.ge.apm.view.component;

import com.ge.apm.dao.SysAuthGroupRepository;
import com.ge.apm.domain.SysAuthGroup;
import com.ge.apm.service.asset.AssetInfoService;
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
import java.util.List;

@ManagedBean
@ViewScoped
public class MedicalInstrumentsClassificationCompCtrl implements ServerEventInterface {

    public static final String MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE = "MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE";

    private SysAuthGroupRepository sysAuthGroupDao;
    private AssetInfoService assetInfoService;

    @Getter @Setter
    private TreeNode sysAuthGroupTree;

    @Getter @Setter
    private SysAuthGroup selectedSysAuthGroup;

    @Getter @Setter
    private TreeNode selectedNode;

    private String clinicalDeptUID;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    protected void init(){
        sysAuthGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);

        initSysAuthGroupTree();
    }

    private void initSysAuthGroupTree() {

        sysAuthGroupTree = assetInfoService.buildSysAuthGroupInTree();

    }

    public void onSelectSysAuthGroupTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedSysAuthGroup = (SysAuthGroup) node.getData();

        notifyController();
    }

    public String getSelectedAuthGroupName() {
        if (null == selectedSysAuthGroup) {
            return "";
        } else {
            return selectedSysAuthGroup.getClassificationCode() +"  "+ selectedSysAuthGroup.getName().replaceAll("\\d+","");
        }
    }

    public void registerController(ServerEventInterface controller) {
        controllerList.add(controller);
    }

    public void notifyController() {
        for (ServerEventInterface controller: controllerList) {
            controller.onServerEvent(MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, selectedSysAuthGroup);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

        if (MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE.equals(eventName)) {
            String sysAuthGroupUid = (String) eventObject;
            if (StringUtils.isEmpty(sysAuthGroupUid)) {
                selectedSysAuthGroup = null;
            } else {
                selectedSysAuthGroup = sysAuthGroupDao.findByClassificationCode(sysAuthGroupUid);
            }

        }

    }
}

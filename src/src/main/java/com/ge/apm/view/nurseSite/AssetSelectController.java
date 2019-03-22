package com.ge.apm.view.nurseSite;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.AssetSelectService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nick.liu
 * @create 2019/3/21 16:56
 */
@Data
@ManagedBean
@ViewScoped
public class AssetSelectController extends GenericCRUDUUIDController<AssetInfo> {

    private static final Logger log = LoggerFactory.getLogger(AssetSelectController.class);

    private OrgInfo selectedOrg;

    private OrgInfo selectedDept;

    private String assetName;

    private String assetGroup;

    private String functionType;

    private String departNum;

    private List<OrgInfo> deptList;

    private TreeNode orgTree;

    private TreeNode selectedNode;

    private UserAccount currentUser;

    private AssetSelectService assetSelectService;

    private List<SearchFilter> searchFilters;

    @Override
    protected GenericRepositoryUUID<AssetInfo> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        assetSelectService = WebUtil.getBean(AssetSelectService.class);
        searchFilters = new ArrayList<>(8);
    }

    public void loadData4Create() {
        this.orgTree = assetSelectService.loadOrgTree();
        this.selectedOrg = assetSelectService.loadSelectedOrg();
        this.deptList = assetSelectService.loadValidDeptList(selectedOrg);
    }

    public void loadData4Edit() {

    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (selectedOrg != null) {
            log.info("selectedOrg: " + selectedOrg.getName());
            this.deptList = assetSelectService.loadDeptListByOrg(selectedOrg);
        }
    }

    public void findAssets2Maintain() {
        log.info("findAssets2Maintain");
        this.buildSearchFilters();
        List<AssetInfo> assetInfoList = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
        log.info("{} assets has been loaded", assetInfoList.size());
    }

    private void buildSearchFilters() {
        // for access control
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getTenantUID(), "1*"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getInstitutionUID(), "1*"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getHospitalUID(), "1*"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getSiteUID(), "1*"));
        // for valid asset
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, "4", "1*"));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        // for input parameters
        if(StringUtils.isNotBlank(this.getAssetName())) {
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, this.getAssetName(), "1*3+"));
            searchFilters.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, this.getAssetName(), "1*3+"));
        }
        if(StringUtils.isNotBlank(this.getAssetGroup())) {
            searchFilters.add(new SearchFilter(assetGroup, SearchFilter.Operator.EQ, assetGroup, "1*"));
        }
        if(StringUtils.isNotBlank(this.getFunctionType())) {
            searchFilters.add(new SearchFilter(functionType, SearchFilter.Operator.LIKE, functionType, "1*"));
        }
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, this.getSelectedDept().getUid(), "1*3+"));
        searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, this.getSelectedDept().getUid(), "1*3+"));
    }

}

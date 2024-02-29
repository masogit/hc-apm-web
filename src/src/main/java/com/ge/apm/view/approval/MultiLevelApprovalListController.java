package com.ge.apm.view.approval;

import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Iterator;

import static com.ge.apm.service.constant.ApprovalConstant.APPROVAL_SEPARATOR;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalListController extends GenericCRUDUUIDController<MultiLevelApproval> {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalListController.class);

    public static final String TAB_INITIATID_BY_ME = "TAB_INITIATID_BY_ME";
    public static final String TAB_PENDING_APPROVAL_BY_ME = "TAB_PENDING_APPROVAL_BY_ME";
    public static final String TAB_APPROVED_BY_ME = "TAB_APPROVED_BY_ME";

    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private MultiLevelApprovalController multiLevelApprovalController;

    @Getter
    @Setter
    private boolean display;

    protected String currentTabId = TAB_INITIATID_BY_ME;

    @Override
    protected GenericRepositoryUUID<MultiLevelApproval> getDAO() {
        return multiLevelApprovalRepository;
    }

    @Override
    protected void init() {
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        multiLevelApprovalController =  WebUtil.getBean(MultiLevelApprovalController.class);
    }

    /**
     *  获取我发起的审批的列表
     * @return
     */
    @Override
    public Page<MultiLevelApproval> loadData(PageRequest pageRequest) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        if (TAB_INITIATID_BY_ME.equals(currentTabId)) {
            this.updateSearchFilter(new SearchFilter("initiateApproverUid", SearchFilter.Operator.EQ, user.getUid()));

        } else if (TAB_PENDING_APPROVAL_BY_ME.equals(currentTabId)) {
            this.updateSearchFilter(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING));
            this.updateSearchFilter(new SearchFilter("approvalNodeStatus", SearchFilter.Operator.EQ, ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING));
            this.updateSearchFilter(new SearchFilter("approvalUid", SearchFilter.Operator.LIKE, APPROVAL_SEPARATOR + user.getUid() + APPROVAL_SEPARATOR));

        } else {//approved by me
            this.updateSearchFilter(new SearchFilter("actualApproverUid", SearchFilter.Operator.EQ, user.getUid()));
            this.updateSearchFilter(new SearchFilter("approvalNodeStatus", SearchFilter.Operator.NE, ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING));
        }

        addRequiredSortByCreatedDate(pageRequest);
        Page<MultiLevelApproval> result = multiLevelApprovalRepository.findBySearchFilter(this.searchFilters, pageRequest);
        return result;
    }

    private PageRequest addRequiredSortByCreatedDate(PageRequest pageRequest) {
        final String FIELD_CREATD_DATE = "createdDate";
        Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
        boolean exists = false;
        while(orders.hasNext()){
            if(orders.next().getProperty().equals(FIELD_CREATD_DATE)){
                exists = true;
                break;
            }
        }
        if (exists) {
            return pageRequest;
        } else {
            Sort sort = new Sort(Sort.Direction.DESC, FIELD_CREATD_DATE);
            pageRequest= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort().and(sort));
        }
        return pageRequest;
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }

        MultiLevelApproval multiLevelApproval = (MultiLevelApproval) event.getObject();
        multiLevelApprovalController.onServerEvent(null, multiLevelApproval);
        display = true;
    }

    public void promptApproval(MultiLevelApproval multiLevelApproval) {
        display=false;
        multiLevelApprovalController.onServerEvent(null, multiLevelApproval);
    }

    public boolean isInitiatedByMe() {
        return TAB_INITIATID_BY_ME.equals(currentTabId);
    }

    public boolean isPendingApprovalByMe() {
        return TAB_PENDING_APPROVAL_BY_ME.equals(currentTabId);
    }

    public boolean isApprovedByMe() {
        return TAB_APPROVED_BY_ME.equals(currentTabId);
    }

    public void initiateApprovalByMe() {
        currentTabId = TAB_INITIATID_BY_ME;
        switchTab();
    }

    public void pendingApprovalByMe() {
        currentTabId = TAB_PENDING_APPROVAL_BY_ME;
        switchTab();
    }

    public void approvedApprovalByMe() {
        currentTabId = TAB_APPROVED_BY_ME;
        switchTab();
    }

    public void switchTab() {
        searchFilters = new ArrayList();
        multiLevelApprovalController.onServerEvent(null, null);
    }

}

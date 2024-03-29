package com.ge.apm.view.asset;

import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.view.approval.MultiLevelApprovalController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;


@ManagedBean
@ViewScoped
public class AssetApprovalController extends GenericCRUDUUIDController<MultiLevelApproval> {

    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private AssetInfoDetailController assetInfoDetailController;


    @Override
    protected GenericRepositoryUUID<MultiLevelApproval> getDAO() {
        return multiLevelApprovalRepository;
    }

    @Override
    protected void init() {
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        assetInfoDetailController =  WebUtil.getBean(AssetInfoDetailController.class);
    }

    /**
     *  获取我发起的审批的列表
     * @return
     */
    @Override
    public Page<MultiLevelApproval> loadData(PageRequest pageRequest) {
        AssetInfo assetInfo = assetInfoDetailController.getSelected();
        if(assetInfoDetailController.getSelected()!=null){
            Integer pageSize=pageRequest.getPageSize();
            Integer pageNum=pageRequest.getPageNumber()==0?pageRequest.getPageNumber():pageRequest.getPageNumber()+pageRequest.getPageSize();
            int count =multiLevelApprovalRepository.getAssetProcessRecord(assetInfo.getTenantUID(), assetInfo.getUid());
            List<MultiLevelApproval> assetApprovalProcessList=multiLevelApprovalRepository.getAssetProcessRecordPage(assetInfo.getTenantUID(), assetInfo.getUid(),pageSize,pageNum);
            Page<MultiLevelApproval> page = new PageImpl<>(assetApprovalProcessList, pageRequest, count);
            return page;
        }
        return null;

    }

}

package com.ge.apm.view.approval;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.constant.ApprovalConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalGlobalController {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalGlobalController.class);

    private MultiLevelApprovalService multiLevelApprovalService;
    private AssetInfoRepository assetInfoRepository;

    @PostConstruct
    protected void init() {
        multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
    }

    public String getAssetInfoName(String assetInfoUid){
        AssetInfo assetInfo = assetInfoRepository.findByUid(assetInfoUid);
        if (assetInfo !=null){
            return assetInfo.getName();
        }
        return null;
    }

    public String getApprovalStatus(Integer approvalStatus){
        if(null == approvalStatus){
            return null;
        }
        switch (approvalStatus){
            case 0:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME;
            case 1:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING_NAME;
            case 2:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME;
            case 3:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME;
        }
        return null;
    }

    public String swapLevelName(Integer level){
        if(level==null) return null;
        switch(level){
            case -1:
                return ApprovalConstant.APPROVAL_NAME_DISAGREE;
            case 0:
                return ApprovalConstant.APPROVAL_NAME_SUCCESS;
            default:
                return level + ApprovalConstant.APPROVAL_NAME;
        }
    }

    public String getApprovalNodeStatus(Integer approvalNodeStatus){
        if(null == approvalNodeStatus){
            return null;
        }
        switch (approvalNodeStatus){
            case 0:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME;
            case 2:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME;
            case 3:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME;
        }
        return null;
    }

    public String getBusinessType(MultiLevelApproval approval){
        if (StringUtils.isNotEmpty(approval.getBusinessTypeName())) {
            return approval.getBusinessTypeName();
        }
        return multiLevelApprovalService.getBusinessType(approval.getBusinessType());
    }
}

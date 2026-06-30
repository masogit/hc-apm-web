package com.ge.apm.domain.dto;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 503310146
 */
@Data
public class V2WorkOrderReportDto implements Serializable {
    private static final long serialVersionUID = 6569537736991577696L;

    private Date startTime;
    private Date endTime;
    private Date closedStartTime;
    private Date closedEndTime;
    private String filterParts;
    private String filterTotalPrice;
    private String currentStepId;
    private Integer assetInfoId;
    private OrgInfo selectedOrg;
    private OrgInfo clinicalDept;
    private OrgInfo assetDept;
    private UserAccount currentOwner;
    private List<Integer> statusList;
    private List<OrgInfo> clinicalDeptList;
    private Map<String,String> filterMap = MapUtil.newHashMap();
    private Set<String> assetTypeGroupEncodedSet;
    private String srNum;
    private String tenantUid;
    private String siteUid;
}

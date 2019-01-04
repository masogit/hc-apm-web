package com.ge.apm.service.wo;

import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.WorkOrderReport;
import com.ge.apm.service.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by 212605082 on 2017/12/13.
 */
@Service
public class WorkOrderStatisticsService {

    public static final String STATISTICS_TYPE_DEPART = "depart";              //按使用科室
    public static final String STATISTICS_TYPE_ASSETTYPE = "assetType";    //按设备类型
    public static final String STATISTICS_TYPE_DEPART_AND_ASSETTYPE = "departAndassetType";    //按使用科室和设备类型
    public static final String STATISTICS_TYPE_ASSETNAME = "assetName";    //按设备名称
    public static final String STATISTICS_TYPE_FAULTTYPE = "faultType";    //按故障类型
    public static final String STATISTICS_TYPE_STAFF = "staff";            //按维修工程师
    public static final String STATISTICS_TYPE_CASELOAD = "caseload";            //按科室统计工作量

    @Autowired
    private WorkOrderReportMapper worDao;

    public List<WorkOrderReport> createData(OrgInfo selectedOrg, Date startTime, Date endTime, String statisticsType){

        String tenantUID = null;
        String institutionUID = null;
        String hospitalUID = null;
        String siteUID = null;
        String startDate = null;
        String endDate = null;

        if(selectedOrg != null && selectedOrg.getOrgLevel() == 0){
            tenantUID = selectedOrg.getUid();
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 1){
            institutionUID = selectedOrg.getUid();
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 2){
            hospitalUID = selectedOrg.getUid();
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 3){
            siteUID = selectedOrg.getUid();
        }
        if(startTime != null){
            startDate = TimeUtils.getStrDate(startTime, null) + " 00:00:00";
        }
        if(endTime != null){
            endDate = TimeUtils.getStrDate(endTime, null) + " 23:59:59";
        }

        List<WorkOrderReport> woReportList = null;
        if(STATISTICS_TYPE_DEPART.equals(statisticsType)){
            woReportList = worDao.statisticsByDepart(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_ASSETTYPE.equals(statisticsType)){
            woReportList = worDao.statisticsByAssetGroup(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_DEPART_AND_ASSETTYPE.equals(statisticsType)){
            woReportList = worDao.statisticsByDepartAndAssetGroup(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_ASSETNAME.equals(statisticsType)){
            woReportList = worDao.statisticsByAssetName(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_FAULTTYPE.equals(statisticsType)){
            woReportList = worDao.statisticsByCaseType(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_STAFF.equals(statisticsType)){
            woReportList = worDao.statisticsByStaff(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }else if(STATISTICS_TYPE_CASELOAD.equals(statisticsType)){
            woReportList = worDao.statisticsCaseloadByDepartment(tenantUID, institutionUID, hospitalUID, siteUID, startDate, endDate);
        }

        return woReportList;

    }

}

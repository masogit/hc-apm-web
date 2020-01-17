package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.dao.ZkbEngineerTrackRecordRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbEngineer;
import com.ge.apm.domain.ZkbEngineerTrackRecord;
import com.ge.apm.service.utils.ExcelDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 212605082 on 2019/12/23.
 */
@Component
public class ZkbEngineerExportService {

    private static final String SHEET_ZKB_ENGINEER = WebUtil.getMessage("zkbEngineer");
    private static final String SHEET_ZKB_ENGINEERTRACKRECORD = WebUtil.getMessage("zkbEngineerTrackRecord");

    @Autowired
    private ZkbEngineerRepository zkbEngineerDao;
    @Autowired
    private ZkbEngineerTrackRecordRepository zkbEngineerTrackRecordDao;
    @Autowired
    private TenantInfoRepository tenantInfoDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public List<Map<String, Object>> createZkbEngineerData(List<ZkbEngineer> zkbEngineerList){

        List<Map<String, Object>> zkbEngineerDataList = new ArrayList<>();
        for (ZkbEngineer zkbEngineer : zkbEngineerList) {

            Map<String, Object> zkbEngineerDataMap = new HashMap<>();
            TenantInfo tempTenantInfo = tenantInfoDao.findByuid(zkbEngineer.getTenantUID());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_tenant"), tempTenantInfo != null ? tempTenantInfo.getName() : "");
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_name"), zkbEngineer.getName());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_phone"), zkbEngineer.getPhone());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_email"), zkbEngineer.getEmail());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_status"), zkbEngineer.getStatus());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_age"), zkbEngineer.getAge() != null ? String.valueOf(zkbEngineer.getAge()) : "");
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_educationalBackground"), zkbEngineer.getEducationalBackground());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_graduateSchool"), zkbEngineer.getGraduateSchool());
            zkbEngineerDataMap.put(WebUtil.getMessage("zkbEngineer_specialty"), zkbEngineer.getSpecialty());

            zkbEngineerDataList.add(zkbEngineerDataMap);

            this.putZkbEngineerTrackRecordData(zkbEngineer, zkbEngineerDataMap);
        }

        return zkbEngineerDataList;
    }

    private void putZkbEngineerTrackRecordData(ZkbEngineer zkbEnginee, Map<String, Object> zkbEngineerDataMap){

        List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList = zkbEngineerTrackRecordDao.findByZkbEngineerId(zkbEnginee.getId());

        List<Map<String, Object>> zkbEngineerTrackRecordDataList = new ArrayList<>();
        for (ZkbEngineerTrackRecord zkbEngineerTrackRecord : zkbEngineerTrackRecordList) {

            Map<String, Object> zkbEngineerTrackRecordDataMap = new HashMap<>();

            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_tenant"), zkbEngineerDataMap.get(WebUtil.getMessage("zkbEngineer_tenant")));
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_name"), zkbEngineerDataMap.get(WebUtil.getMessage("zkbEngineer_name")));
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_phone"), zkbEngineerDataMap.get(WebUtil.getMessage("zkbEngineer_phone")));
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_email"), zkbEngineerDataMap.get(WebUtil.getMessage("zkbEngineer_email")));

            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_companyName"), zkbEngineerTrackRecord.getCompanyName());
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_position"), zkbEngineerTrackRecord.getPosition());
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_hireDate"), formatDate(zkbEngineerTrackRecord.getHireDate()));
            zkbEngineerTrackRecordDataMap.put(WebUtil.getMessage("zkbEngineer_workSeniority"), zkbEngineerTrackRecord.getWorkSeniority() != null ? String.valueOf(zkbEngineerTrackRecord.getWorkSeniority()) : "");

            zkbEngineerTrackRecordDataList.add(zkbEngineerTrackRecordDataMap);
        }

        zkbEngineerDataMap.put("zkbEngineerTrackRecordDataList", zkbEngineerTrackRecordDataList);

    }

    public List<Map<String, Object>> createZkbEngineerTrackRecordData(List<Map<String, Object>> zkbEngineerDataList){

        List<Map<String, Object>> zkbEngineerTrackRecordDataList = new ArrayList<>();
        for (Map<String, Object> zkbEngineerDataMap : zkbEngineerDataList) {
            if(zkbEngineerDataMap.containsKey("zkbEngineerTrackRecordDataList")){
                zkbEngineerTrackRecordDataList.addAll((List<Map<String, Object>>)zkbEngineerDataMap.get("zkbEngineerTrackRecordDataList"));
            }
        }

        return zkbEngineerTrackRecordDataList;
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbEngineerData, List<Map<String, Object>> zkbEngineerTrackRecordData) {

        Integer keyRow_zkbEngineer = 1;
        Integer dataRow_zkbEngineer = 2;

        for (Map<String, Object> item : zkbEngineerData) {
            doc.writeRowData(SHEET_ZKB_ENGINEER, keyRow_zkbEngineer, dataRow_zkbEngineer++, item);
        }

        Integer keyRow_zkbEngineerTrackRecord = 1;
        Integer dataRow_zkbEngineerTrackRecord = 2;
        for (Map<String, Object> item : zkbEngineerTrackRecordData) {
            doc.writeRowData(SHEET_ZKB_ENGINEERTRACKRECORD, keyRow_zkbEngineerTrackRecord, dataRow_zkbEngineerTrackRecord++, item);
        }

    }

}

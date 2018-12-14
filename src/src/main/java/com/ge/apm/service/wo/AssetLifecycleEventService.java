package com.ge.apm.service.wo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.service.utils.ExcelDocument;

@Service
public class AssetLifecycleEventService {
	protected static final Logger LOGGER = LoggerFactory.getLogger(AssetLifecycleEventService.class);
	private static final String STR_ASSET_NAME = "资产名称";
	private static final String STR_WO_NO = "外部工单号";
	private static final String STR_MODIFY_DATE = "更新时间";
	private static final String STR_SYSTEMID = "System ID";
	private static final String STR_CLINICALDEPT = "所属科室";
	private static final String STR_STATUS = "状态";
	private static final String STR_SCHEDULE_DATE = "保养计划时间";
	private static final String SHEET_NAME = "datalist";
	@Autowired
	OrgInfoRepository orgInfoRepository;

	public String getMsStatus(AssetLifecycleEvent alse) {
        String strStatus = null;
        try {
            JSONObject parse = (JSONObject) JSON.parse(alse.getDescription());
            if (parse != null) {
                strStatus = parse.getString("status");
            }
        } catch (Exception e) {
        	LOGGER.error("Fail to parse description for status: " + alse.getId(), e);
        }
        return strStatus;
    }
	
	public List<Map<String, Object>> parseEventToMap(List<AssetLifecycleEvent> listEvent,int tabStatus) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listEvent != null && listEvent.size() > 0) {
			for (int i = 0; i < listEvent.size(); ++i) {
				AssetLifecycleEvent tempEvent = listEvent.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				String name = "";
				String eventUid = "";
				String updateTime = "";
				String systemId = "";
				String clinicalDeptName = "";
				String status = "";
				String reportDate="";
				try {
					name = tempEvent.getAssetName();
					eventUid = tempEvent.getEventUid();
					Date tempCreateTime=tempEvent.getCreateTime();
					if(tempCreateTime!=null){
						updateTime = sdf2.format(tempCreateTime);
					}
					systemId = tempEvent.getAssetInfo().getSystemId();
					clinicalDeptName = tempEvent.getAssetInfo().getClinicalDeptName();
					status = getMsStatus(tempEvent);
					Date scheduleDate=tempEvent.getScheduleDate();
					if(scheduleDate!=null){
						reportDate = sdf3.format(scheduleDate);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (StringUtils.isNotEmpty(name)) {

				}
				if (StringUtils.isNotEmpty(name)) {
					tempMap.put(STR_ASSET_NAME, name);
				}
				if (StringUtils.isNotEmpty(eventUid)) {
					tempMap.put(STR_WO_NO, eventUid);
				}
				if (StringUtils.isNotEmpty(updateTime)) {
					tempMap.put(STR_MODIFY_DATE, updateTime);
				}
				if (StringUtils.isNotEmpty(systemId)) {
					tempMap.put(STR_SYSTEMID, systemId);
				}
				if (StringUtils.isNotEmpty(clinicalDeptName)) {
					tempMap.put(STR_CLINICALDEPT, clinicalDeptName);
				}
				if (StringUtils.isNotEmpty(status)) {
					tempMap.put(STR_STATUS, status);
				}
				if(tabStatus==4 || tabStatus==5){
					if(StringUtils.isNotEmpty(reportDate)){
						tempMap.put(STR_SCHEDULE_DATE, reportDate);
					}
				}
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
	}

	public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {

		Integer keyRow = 1;
		Integer dataRow = 2;

		for (Map<String, Object> item : data) {
			doc.writeRowData(SHEET_NAME, keyRow, dataRow++, item);
		}

		return Boolean.TRUE;
	}
}

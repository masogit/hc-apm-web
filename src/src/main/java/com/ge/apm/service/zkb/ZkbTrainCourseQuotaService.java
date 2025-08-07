package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbTrainCourseQuotaRepository;
import com.ge.apm.dao.ZkbTrainCourseRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;

@Service
public class ZkbTrainCourseQuotaService {


    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbTrainCourseQuotaRepository zkbTrainCourseQuotaRepository;

    @Autowired
    private ZkbTrainCourseRepository zkbTrainCourseRepository;

    public static final String SHEET_NAME = "名额";

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public List<ZkbTrainCourse> queryTrainCourse(){
        return zkbTrainCourseRepository.find();
    }

    public void saveData(ZkbTrainCourseQuota zkbTrainCourseQuota, ZkbTrainCourse zkbTrainCourse, UserAccount userAccount){
        if(StrUtil.isEmpty(zkbTrainCourseQuota.getId())){
            zkbTrainCourseQuota.setCreatedBy(userAccount.getUid());
            zkbTrainCourseQuota.setCreatedDate(new Date());
            zkbTrainCourseQuota.setCourseId(zkbTrainCourse.getId());
            zkbTrainCourseQuota.setZkbTrainCourse(zkbTrainCourse);
            zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
        }else{
            zkbTrainCourseQuota.setLastModifiedBy(userAccount.getUid());
            zkbTrainCourseQuota.setLastModifiedDate(new Date());
            if(!zkbTrainCourse.getId().equals(zkbTrainCourseQuota.getZkbTrainCourse().getId())){
                zkbTrainCourseQuota.setCourseId(zkbTrainCourse.getId());
                zkbTrainCourseQuota.setZkbTrainCourse(zkbTrainCourse);
            }
            zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
        }
    }

    public static final int EXCEL_CONTRACT_DATA_ROW = 2;

    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheetAt(0).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_NAME, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            ZkbTrainCourseQuotaImportVO zkbTrainCourseQuotaImportVO = new ZkbTrainCourseQuotaImportVO();

            ZkbTrainCourseQuotaRow zkbTrainCourseQuotaRow = new ZkbTrainCourseQuotaRow(zkbTrainCourseQuotaImportVO, rowMap);
            zkbTrainCourseQuotaRow.setFieldStringValue("ContractNo", "setContractNo");
            zkbTrainCourseQuotaRow.setFieldStringValue("Dealer Name","setTenantName");
            zkbTrainCourseQuotaRow.setFieldNumValue("总数","setTotalQuota");
            zkbTrainCourseQuotaRow.setFieldNumValue("剩余名额", "setQuota");
            zkbTrainCourseQuotaRow.setFieldStringValue("课程名称", "setCourseName");
            zkbTrainCourseQuotaRow.setFieldDateValue("SVCStartDate", "setStartDate");
            zkbTrainCourseQuotaRow.setFieldDateValue("SVCEndDate", "setEndDate");
            if(zkbTrainCourseQuotaImportVO.isNull()){
                break;
            }

            map.put("vo", zkbTrainCourseQuotaImportVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId()) ? ZkbTrainCourseQuotaService.ImportStatus.New.name() : ZkbTrainCourseQuotaService.ImportStatus.Exist.name());
            map.put("hashCode", zkbTrainCourseQuotaImportVO.hashCode());

            result.add(map);
        }

        return result;
    }


    protected void importOne(ZkbTrainCourseQuotaImportVO zkbTrainCourseQuotaImportVO,UserAccount currentUser) {
        ZkbTrainCourseQuota quota = zkbTrainCourseQuotaRepository.findFirstByTenantUidAndContractNoAndCourseId(zkbTrainCourseQuotaImportVO.getTenantUid(),zkbTrainCourseQuotaImportVO.getContractNo(), zkbTrainCourseQuotaImportVO.getCourseId());
        if(quota == null){
            quota = new ZkbTrainCourseQuota();
            quota.setContractNo(zkbTrainCourseQuotaImportVO.getContractNo());
            quota.setTenantUid(zkbTrainCourseQuotaImportVO.getTenantUid());
            quota.setCourseId(zkbTrainCourseQuotaImportVO.getCourseId());
            quota.setStartDate(zkbTrainCourseQuotaImportVO.getStartDate());
            quota.setEndDate(zkbTrainCourseQuotaImportVO.getEndDate());
            quota.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
            quota.setQuota(zkbTrainCourseQuotaImportVO.getQuota());
            quota.setCreatedDate(new Date());
            quota.setCreatedBy(currentUser.getCreatedBy());
            zkbTrainCourseQuotaRepository.save(quota);
        }else{
            quota.setContractNo(zkbTrainCourseQuotaImportVO.getContractNo());
            quota.setTenantUid(zkbTrainCourseQuotaImportVO.getTenantUid());
            quota.setCourseId(zkbTrainCourseQuotaImportVO.getCourseId());
            quota.setStartDate(zkbTrainCourseQuotaImportVO.getStartDate());
            quota.setEndDate(zkbTrainCourseQuotaImportVO.getEndDate());
            quota.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
            quota.setQuota(zkbTrainCourseQuotaImportVO.getQuota());
            quota.setLastModifiedDate(new Date());
            quota.setLastModifiedBy(currentUser.getCreatedBy());
            zkbTrainCourseQuotaRepository.save(quota);
        }
    }

    public void importData(List<Map<String, Object>> importContractMapList,UserAccount currentUser) {
        try {
            for (Map<String, Object> item : importContractMapList) {
                if(checkData(item, importContractMapList)) {
                    ZkbTrainCourseQuotaImportVO vo = (ZkbTrainCourseQuotaImportVO)item.get("vo");
                    importOne(vo,currentUser);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importContractMapList) {
        boolean isValidateData = true;
        ZkbTrainCourseQuotaImportVO vo = (ZkbTrainCourseQuotaImportVO) item.get("vo");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbTrainCourseQuotaRow.clearErrMessage(item);
        if(vo.getContractNo() == null){
            item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "合同号不能为空！");
            isValidateData = false;
        }
        if(StrUtil.isEmpty(vo.getTenantName())){
            item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "租户不能为空！");
            isValidateData = false;
        }else {
            List<TenantInfo> tenantInfoList = tenantInfoDao.findByNameAndType(vo.getTenantName(),3);
            if(tenantInfoList == null || tenantInfoList.size() == 0){
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractTenantUIDExist"));
                isValidateData = false;
            }else if(tenantInfoList.size() > 1) {
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("该代理商名称存在多条数据！"));
                isValidateData = false;
            }else{
                TenantInfo tenantInfo = tenantInfoList.get(0);
                vo.setTenantUid(tenantInfo.getUid());
            }
        }
        if(StrUtil.isEmpty(vo.getCourseName())){
            item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "课程不能为空！");
            isValidateData = false;
        }else{
            List<ZkbTrainCourse> courseList = zkbTrainCourseRepository.findByName(vo.getCourseName());
            if(courseList == null || courseList.size() == 0){
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, "课程不存在！");
                isValidateData = false;
            }else if(courseList.size() > 1) {
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("该课程名称存在多条数据！"));
                isValidateData = false;
            }else{
                vo.setCourseId(courseList.get(0).getId());
            }
        }


        for (Map<String, Object> tempContractMap: importContractMapList) {
            ZkbTrainCourseQuotaImportVO tempVO = (ZkbTrainCourseQuotaImportVO)tempContractMap.get("vo");
            Integer tempHashCode = Integer.valueOf(tempContractMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && vo.getLineNum() != tempVO.getLineNum()){
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                isValidateData = false;
                break;
            }
        }
        return isValidateData;
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ZkbTrainCourseQuotaService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbTrainCourseQuotaService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }

    public static final int EXCEL_EXPORT_HEADER_ROW = 1;
    public static final int EXCEL_EXPORT_CDATA_ROW = 2;


    public Boolean exportFailData(ExcelDocument doc, List<Map<String, Object>> errorContractList) {

        int keyRowExport = EXCEL_EXPORT_HEADER_ROW;
        int dataRowExport = EXCEL_EXPORT_CDATA_ROW;
        Boolean hasFailData = false;

        //doc.clearRowsData(SHEET_SERVICE_CONTRACT, dataRowExport, keyRowExport + errorContractList.size());
        for (Map<String, Object> item : errorContractList) {
            if (item.get("status").equals(ZkbContractImportService.ImportStatus.Failure)) {
                doc.writeRowData(SHEET_NAME, keyRowExport, dataRowExport++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        return hasFailData;
    }


    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }
}

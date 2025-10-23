package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ZkbTrainCourseQuotaService {


    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbTrainCourseQuotaRepository zkbTrainCourseQuotaRepository;

    @Autowired
    private ZkbTrainCourseRepository zkbTrainCourseRepository;

    @Autowired
    private ZkbTrainCourseQuotaDetailRepository zkbTrainCourseQuotaDetailRepository;

    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;


    public static final String SHEET_NAME = "名额";

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    public void delete(ZkbTrainCourseQuota zkbTrainCourseQuota){
        //先删掉detail表
        List<ZkbTrainCourseQuotaDetail> details = zkbTrainCourseQuotaDetailRepository.findByQuotaIdOrderByUniqueIDDesc(zkbTrainCourseQuota.getId());
        if(CollectionUtils.isNotEmpty(details)){
            zkbTrainCourseQuotaDetailRepository.delete(details);
        }
        zkbTrainCourseQuotaRepository.delete(zkbTrainCourseQuota);
    }

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public List<ZkbTrainCourse> queryTrainCourse(){
        return zkbTrainCourseRepository.find();
    }

    public void saveData(ZkbTrainCourseQuota zkbTrainCourseQuota, ZkbTrainCourse zkbTrainCourse, UserAccount userAccount, List<ZkbTrainCourseQuotaDetail> zkbTrainCourseQuotaDetails){
        if(StrUtil.isEmpty(zkbTrainCourseQuota.getId())){
            zkbTrainCourseQuota.setCreatedBy(userAccount.getUid());
            zkbTrainCourseQuota.setCreatedDate(new Date());
            zkbTrainCourseQuota.setCourseId(zkbTrainCourse.getId());
            zkbTrainCourseQuota.setZkbTrainCourse(zkbTrainCourse);
            zkbTrainCourseQuota.setModality(zkbTrainCourse.getModality());
            zkbTrainCourseQuota = zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
        }else{
            zkbTrainCourseQuota.setLastModifiedBy(userAccount.getUid());
            zkbTrainCourseQuota.setLastModifiedDate(new Date());
            if (!zkbTrainCourse.getId().equals(zkbTrainCourseQuota.getCourseId())) {
                zkbTrainCourseQuota.setCourseId(zkbTrainCourse.getId());
                zkbTrainCourseQuota.setZkbTrainCourse(zkbTrainCourse);
                zkbTrainCourseQuota.setModality(zkbTrainCourse.getModality());
            }
            zkbTrainCourseQuotaRepository.save(zkbTrainCourseQuota);
        }

        if(CollectionUtils.isNotEmpty(zkbTrainCourseQuotaDetails)){
            for(ZkbTrainCourseQuotaDetail detail : zkbTrainCourseQuotaDetails){
                if(StrUtil.isEmpty(detail.getQuotaId())){
                    detail.setQuotaId(zkbTrainCourseQuota.getId());
                }
            }
            zkbTrainCourseQuotaDetailRepository.save(zkbTrainCourseQuotaDetails);
        }
    }

    public static final int EXCEL_CONTRACT_DATA_ROW = 2;

    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheetAt(0).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            ZkbTrainCourseQuotaImportVO zkbTrainCourseQuotaImportVO = new ZkbTrainCourseQuotaImportVO();

            ZkbTrainCourseQuotaRow zkbTrainCourseQuotaRow = new ZkbTrainCourseQuotaRow(zkbTrainCourseQuotaImportVO, rowMap);
            zkbTrainCourseQuotaRow.setFieldStringValue("ContractNo", "setContractNo");
            zkbTrainCourseQuotaRow.setFieldStringValue("Dealer Name","setTenantName");
            zkbTrainCourseQuotaRow.setFieldNumValue("总数","setTotalQuota");
            zkbTrainCourseQuotaRow.setFieldNumValue("剩余名额", "setQuota");
            zkbTrainCourseQuotaRow.setFieldStringValue("CatNO", "setCatNo");
            zkbTrainCourseQuotaRow.setFieldDateValue("SVCStartDate", "setStartDate");
            zkbTrainCourseQuotaRow.setFieldDateValue("SVCEndDate", "setEndDate");
            zkbTrainCourseQuotaRow.setFieldStringValue("UniqueID", "setUniqueID");
            zkbTrainCourseQuotaRow.setFieldStringValue("Modality", "setModality");
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
            quota.setModality(zkbTrainCourseQuotaImportVO.getModality());
            quota.setCreatedDate(new Date());
            quota.setCreatedBy(currentUser.getCreatedBy());
            zkbTrainCourseQuotaRepository.save(quota);

            ZkbTrainCourseQuotaDetail zkbTrainCourseQuotaDetail = new ZkbTrainCourseQuotaDetail();
            zkbTrainCourseQuotaDetail.setUniqueID(zkbTrainCourseQuotaImportVO.getUniqueID());
            zkbTrainCourseQuotaDetail.setQuotaId(quota.getId());
            zkbTrainCourseQuotaDetail.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
            zkbTrainCourseQuotaDetail.setQuota(zkbTrainCourseQuotaImportVO.getQuota());
            zkbTrainCourseQuotaDetailRepository.save(zkbTrainCourseQuotaDetail);
        }else{
            quota.setModality(zkbTrainCourseQuotaImportVO.getModality());
            quota.setStartDate(zkbTrainCourseQuotaImportVO.getStartDate());
            quota.setEndDate(zkbTrainCourseQuotaImportVO.getEndDate());
           /* quota.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
            quota.setQuota(zkbTrainCourseQuotaImportVO.getQuota());*/
            quota.setLastModifiedDate(new Date());
            quota.setLastModifiedBy(currentUser.getCreatedBy());


            List<ZkbTrainCourseQuotaDetail> details = zkbTrainCourseQuotaDetailRepository.findByUniqueID(zkbTrainCourseQuotaImportVO.getUniqueID());
            if(CollectionUtils.isNotEmpty(details)){
                ZkbTrainCourseQuotaDetail detail = details.get(0);
                int oldTotalQuota = detail.getTotalQuota();
                int oldQuota = detail.getQuota();
                if(oldQuota != zkbTrainCourseQuotaImportVO.getQuota() || oldTotalQuota != zkbTrainCourseQuotaImportVO.getTotalQuota()){
                    detail.setQuota(zkbTrainCourseQuotaImportVO.getQuota());
                    detail.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
                    zkbTrainCourseQuotaDetailRepository.save(detail);

                    int cq = zkbTrainCourseQuotaImportVO.getQuota() - oldQuota;
                    int ctq = zkbTrainCourseQuotaImportVO.getTotalQuota() - oldTotalQuota;
                    quota.setTotalQuota(quota.getTotalQuota() + cq);
                    quota.setQuota(quota.getQuota() + ctq);
                }
            }else{
                ZkbTrainCourseQuotaDetail zkbTrainCourseQuotaDetail = new ZkbTrainCourseQuotaDetail();
                zkbTrainCourseQuotaDetail.setUniqueID(zkbTrainCourseQuotaImportVO.getUniqueID());
                zkbTrainCourseQuotaDetail.setQuotaId(quota.getId());
                zkbTrainCourseQuotaDetail.setTotalQuota(zkbTrainCourseQuotaImportVO.getTotalQuota());
                zkbTrainCourseQuotaDetail.setQuota(zkbTrainCourseQuotaImportVO.getQuota());

                zkbTrainCourseQuotaDetailRepository.save(zkbTrainCourseQuotaDetail);

                quota.setTotalQuota(quota.getTotalQuota() + zkbTrainCourseQuotaDetail.getTotalQuota());
                quota.setQuota(quota.getQuota() + zkbTrainCourseQuotaDetail.getQuota());
            }
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
        if(StrUtil.isEmpty(vo.getCatNo())){
            item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "CatNO不能为空！");
            isValidateData = false;
        }else{
            List<ZkbTrainCourse> courseList = zkbTrainCourseRepository.findByCatNo(vo.getCatNo());
            if(courseList == null || courseList.size() == 0){
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, "CatNO不存在！");
                isValidateData = false;
            }else if(courseList.size() > 1) {
                item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("该课程名称存在多条数据！"));
                isValidateData = false;
            }else{
                vo.setCourseId(courseList.get(0).getId());
            }
        }
        if(StrUtil.isEmpty(vo.getModality())){
            item.put("status", ZkbTrainCourseQuotaService.ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "Modality不能为空！");
            isValidateData = false;
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

    public List<ZkbTrainCourseQuotaDetail> queryDetailByQuota(String id){
        return zkbTrainCourseQuotaDetailRepository.findByQuotaIdOrderByUniqueIDDesc(id);
    }

    public List<ZkbTrainCourseQuota> findAll() {
        return StreamSupport.stream(zkbTrainCourseQuotaRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public void adjustQuota(String tenantUid, String courseId, int delta, UserAccount operator) {
        if (delta == 0) return;

        ZkbTrainCourseQuota mainQuota = zkbTrainCourseQuotaRepository.findFirstByTenantUidAndCourseId(tenantUid, courseId);
        if (mainQuota == null) {
            throw new BusinessException("未配置该课程配额，无法调整");
        }

        int newQuota = mainQuota.getQuota() + delta;
        if (newQuota < 0) {
            throw new BusinessException("配额不足，无法完成操作，当前剩余：" + mainQuota.getQuota());
        }

        mainQuota.setQuota(newQuota);
        mainQuota.setLastModifiedBy(operator.getName());
        mainQuota.setLastModifiedDate(new Date());
        zkbTrainCourseQuotaRepository.save(mainQuota);
    }

}

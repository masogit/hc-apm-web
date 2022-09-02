package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbContactPersonRepository;
import com.ge.apm.dao.ZkbSalesTenantAssociateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @autor by lyj 503277717
 * @date 2022/8/25
*/

@Service
public class ZkbSalesPersonInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbSalesPersonInfoService.class);


    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbSalesTenantAssociateRepository zkbSalesTenantAssociateRepository;

    public List<TenantInfo> getSourceTenantInfoList(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }


    public List<HashMap<String,Object>> createTenantInfoData(List<TenantInfo> tenantInfos){
        List<HashMap<String, Object>> zkbTenantInfoDataList = new ArrayList<>();
        for(TenantInfo t : tenantInfos){
            HashMap<String, Object> m = getTenantInfoMap(t);
            zkbTenantInfoDataList.add(m);
        }
        return zkbTenantInfoDataList;
    }

    public HashMap<String, Object> getTenantInfoMap(TenantInfo t){
        HashMap<String, Object> zkbTenantInfoDataMap = new HashMap<>(50);

        zkbTenantInfoDataMap.put("租户名称", t.getName());
        zkbTenantInfoDataMap.put("租户id", t.getUid());

        return zkbTenantInfoDataMap;
    }

    public void exportData(ExcelDocument doc, List<HashMap<String, Object>> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;


        for (HashMap<String, Object> item : dataMap) {
            doc.writeRowData("租户列表", keyRow, dataRow++, item);
        }
    }

    @Transactional
    public List<HashMap<String,String>> handleExcel(UploadedFile file, UserAccount user, ZkbSalesPersonInfo zkbSalesPersonInfo) throws IOException, ParseException {
        Workbook workbook = this.createExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<HashMap<String,String>> errorList = new ArrayList<>();
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = new ArrayList<>();
        for(int j = 0 ; j < sheet.getLastRowNum(); j ++) {
            Row row = sheet.getRow(j + 1);
            HashMap<String,String> em = new HashMap<>(3);

            String tenantUid = "";
            if(row.getCell(0) != null){
                tenantUid = row.getCell(0).getStringCellValue();
            }else{
                em.put("errorInfo","Cell不存在！");
                em.put("tenantUid", tenantUid);
                errorList.add(em);
                continue;
            }
            if(StringUtils.isNotEmpty(tenantUid)){
                TenantInfo tenantInfo = tenantInfoDao.findByuid(tenantUid);
                if(tenantInfo == null){
                    em.put("errorInfo","租户不存在！");
                    em.put("tenantUid", tenantUid);
                    errorList.add(em);
                    continue;
                }else{
                    ZkbSalesTenantAssociate zkbSalesTenantAssociate = new ZkbSalesTenantAssociate();
                    zkbSalesTenantAssociate.setSalesPersonId(zkbSalesPersonInfo.getId());
                    zkbSalesTenantAssociate.setCreatedTime(new Date());
                    zkbSalesTenantAssociate.setTenantUid(tenantUid);
                    zkbSalesTenantAssociate.setCreatedBy(user.getName());
                    zkbSalesTenantAssociateList.add(zkbSalesTenantAssociate);
                }
            }
        }
        if(zkbSalesTenantAssociateList.size() > 0){
            zkbSalesTenantAssociateRepository.deleteBySalesPersonId(zkbSalesPersonInfo.getId());
            zkbSalesTenantAssociateRepository.save(zkbSalesTenantAssociateList);
        }
        return errorList;
    }


    public Workbook createExcel(UploadedFile file) throws IOException {
        Workbook workbook = null;
        if (ExcelDocument.ExcelType.XLS.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (ExcelDocument.ExcelType.XLSX.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        return workbook;
    }

}

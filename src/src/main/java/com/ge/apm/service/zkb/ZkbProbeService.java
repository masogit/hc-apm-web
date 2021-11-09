package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbPartOrderCatRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbPartOrderCat;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;


/**
 * @autor by lyj 503277717
 * @date 2021/11/4
*/

@Component
public class ZkbProbeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbProbeService.class);


    private final String[] HEADER_FIELD_CN_NAME = {"探头号","探头型号","探头类型","价值类型","是否有效"};//header名
    private final String[] HEADER_FIELD = {"catNo","catModel","catType","valueType","isVaild"};  //字段名

    private final String VALUETYPE_HIGHVALUE = "高值";
    private final String VALUETYPE_NORMALVALUE = "普通";
    private final String VALUETYPE_SPECIALVALUE = "特殊";

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    public Workbook createExcel(UploadedFile file) throws IOException {
        Workbook workbook = null;
        if (ExcelDocument.ExcelType.XLS.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (ExcelDocument.ExcelType.XLSX.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        return workbook;
    }

    public List<ZkbPartOrderCat> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
        List<ZkbPartOrderCat> tempProbeList = new ArrayList<>();
        Workbook workbook = this.createExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        for(int j = 0 ; j < sheet.getLastRowNum(); j ++){
            Row row = sheet.getRow(j+1);
            String errorInfo = "";
            ZkbPartOrderCat probe = new ZkbPartOrderCat();
            probe.setIsVaild("");
            for(int i = 0 ; i< HEADER_FIELD.length ; i++){
                String mtName = "set"+HEADER_FIELD[i].substring(0, 1).toUpperCase() + HEADER_FIELD[i].substring(1);
                if(row.getCell(i) != null &&StringUtils.isNotBlank(CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ?
                        new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue())){
                    String opt = CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue();
                    try {
                        Method method = ZkbPartOrderCat.class.getMethod(mtName,String.class);
                        method.invoke(probe, opt);
                    } catch (Exception e) {
                        LOGGER.debug("handleExcel error"+e.getMessage());
                    }
                }else{
                    errorInfo += "[请输入"+HEADER_FIELD_CN_NAME[i]+"]";
                }
            }
            probe.setExcelIdex(j + 1);
            if (StringUtils.isNotBlank(errorInfo)) {
                probe.setErrorInfo(errorInfo);
                tempProbeList.add(probe);
                continue;
            }
            zkbPartOrderCatRepository.save(this.convertDownBox(probe));
        }
        return tempProbeList;
     }

    public ZkbPartOrderCat convertDownBox(ZkbPartOrderCat probe){
        probe.setIsVaild("是".equals(probe.getIsVaild())?"Y":"N");
        /**
         * HighValue:高值
         * NormalValue:普通
         * SpecialValue:特殊
         */
        if(VALUETYPE_HIGHVALUE.equals(probe.getValueType())){
            probe.setValueType("HighValue");
        }else if (VALUETYPE_NORMALVALUE.equals(probe.getValueType())){
            probe.setValueType("NormalValue");
        }else if (VALUETYPE_SPECIALVALUE.equals(probe.getValueType())){
            probe.setValueType("SpecialValue");
        }
        return probe;
    }

}

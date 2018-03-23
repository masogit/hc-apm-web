package com.ge.apm.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.service.asset.SmallAssetPerformanceService;

public class ExcelParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelParser.class);
	private static final String HEADER_ROW="HEADER_ROW";
	public static Map<String, Integer> findHeader(HSSFSheet sheet, List<String> headers) {
        Map<String, Integer> result = new HashMap<>();
        String header = "";
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    for (int z = 0; z < headers.size(); ++z) {
                        header = headers.get(z);
                        if (cell.getStringCellValue().equals(header)) {
                            result.put(header, j);
                            break;
                        }
                    }
                } catch (Exception e) {
                	LOGGER.error(e.getMessage());
                    continue;
                }
            }
            if (result.size() == headers.size()) {
                result.put(HEADER_ROW, i);
                break;
            }
        }
        if (result.get(HEADER_ROW) == null) {
            result.clear();
        }
        return result;
    }

	public static Date getCellDateValue(HSSFRow row, int columIndex, SimpleDateFormat sdf) {
        HSSFCell cell = row.getCell(columIndex);
        String value = "";
        if (cell != null) {
            try {
                value = cell.getStringCellValue();
                if (StringUtils.isNotEmpty(value)) {
                    return sdf.parse(value);
                } else {
                    return null;
                }
            } catch (Exception e) {
                // TODO: handle exception
            	LOGGER.error("获取日期错误：" + e.getMessage());
            }
        }
        return null;
    }

    public static String getCellStringValue(HSSFRow row, int columIndex) {
        HSSFCell cell = row.getCell(columIndex);
        if (cell != null) {
            try {
                return cell.getStringCellValue();
            } catch (Exception e) {
                // TODO: handle exception
            	LOGGER.error("获取字符串错误：" + e.getMessage());
            }
        }
        return "";
    }

    public static Double getCellNumberValue(HSSFRow row, int columIndex) {
        HSSFCell cell = row.getCell(columIndex);
        if (cell != null) {
            try {
                return cell.getNumericCellValue();
            } catch (Exception e) {
                // TODO: handle exception
            	try {
            		return Double.valueOf(cell.getStringCellValue());
				} catch (Exception e2) {
					// TODO: handle exception
					LOGGER.error("获取数字错误：" + e2.getMessage());
				}
            }
        }
        return 0.0;
    }
}

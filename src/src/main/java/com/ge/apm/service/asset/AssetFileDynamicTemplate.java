/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Quan Tang
 */
public class AssetFileDynamicTemplate {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileDynamicTemplate.class);

    Workbook workBook;

    public AssetFileDynamicTemplate(String path) throws IOException {
        this(new FileInputStream(path));
    }

    public AssetFileDynamicTemplate(InputStream input) throws IOException {
        this.workBook = new SXSSFWorkbook(new XSSFWorkbook(input));
//        this.workBook = new XSSFWorkbook(input);


    }

    public void buildMetaData() {

    }

}

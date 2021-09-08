/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author 212579464
 */
@Component
public class FileScanUSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanUSService.class);

    private static Long TIME_OUT = 5000l;

    @Value("#{systemEnvironment['US_FILE_Folder_Local'] ?: urlProperties.USFileFolderLocal}")
    private String US_FILE_Folder_Local;


    public String saveUSFile(UploadedFile file) throws Exception {

        String folderNameLocal =  System.getProperty("webapp.root") +US_FILE_Folder_Local;
        LOGGER.info("USProject----------folderNameLocal"+folderNameLocal);
        return saveFile(file, folderNameLocal, file.getFileName());
    }

    public String saveFile(UploadedFile file, String folderName, String saveFilename) throws Exception {

        createFolder(folderName, false);
        String fileName = folderName.concat(File.separator).concat(saveFilename);
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            InputStream inputStream = file.getInputstream();
            int count = 0;
            long starttime = new Date().getTime();
            long endtime;
            while (count == 0) {
                count = inputStream.available();
                endtime = new Date().getTime();
                if (endtime - starttime >= TIME_OUT) {
                    throw new Exception("Save file[" + fileName + "] timeout !");
                }
            }
            byte[] buff = new byte[count];
            int bytesRead;

            while (-1 != (bytesRead = inputStream.read(buff))) {
                out.write(buff, 0, bytesRead);
            }
            out.flush();

        } catch (Exception e) {
            throw e;
        }
        LOGGER.info("USProject----------name"+fileName);
        return fileName;
    }

    public static void createFolder(String folderPath, boolean deleteExistingFiles) {
        if (deleteExistingFiles) deleteFolder(folderPath);

        File file = new File(folderPath);
        if (!file.exists()) file.mkdirs();
    }

    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFiles(folderPath);
            java.io.File myFilePath = new java.io.File(folderPath);
            myFilePath.delete();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static boolean deleteAllFiles(String path) {
        boolean flag = false;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        String[] fileList = rootFile.list();
        File file;
        for (String fileName : fileList) {
            if (path.endsWith(File.separator)) {
                file = new File(path + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                deleteAllFiles(path + "/" + fileName);
                deleteFolder(path + "/" + fileName);
                flag = true;
            }
        }
        return flag;
    }


    public  boolean deleteFile(String fileName) {
        boolean flag = false;
        String path =  System.getProperty("webapp.root") +US_FILE_Folder_Local;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        File file;
        if (path.endsWith(File.separator)) {
            file = new File(path + fileName);
        } else {
            file = new File(path + File.separator + fileName);
        }
        if (file.isFile()) {
            file.delete();
        }
        return flag;
    }

}

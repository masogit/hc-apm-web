package com.ge.apm.service.utils;

import org.apache.log4j.Logger;

import java.io.File;

public class FileUtils {

    private static final Logger log = Logger.getLogger(FileUtils.class.getName());
    
    public static void createFolder(String folderPath, boolean deleteExistingFiles) {
        if(deleteExistingFiles) deleteFolder(folderPath);
        
        File file = new File(folderPath);
        if (!file.exists()) file.mkdirs();
    }
    
    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFiles(folderPath);
            java.io.File myFilePath = new java.io.File(folderPath);
            myFilePath.delete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

}

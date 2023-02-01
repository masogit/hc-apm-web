package com.ge.apm.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wujb
 */
public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger log = Logger.getLogger(FileUtils.class.getName());

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

    /**
     * 读取文件（.json or .txt）内容到 Java
     *
     * @param t    class对象
     * @param file 需要读的文件
     * @return 读取到的实体类集合
     * @throws IOException 读取文件内容为JSON格式失败抛出
     */
    public static <T> List<T> readFile(Class<T> t, File file) throws IOException {
        List<T> list = new ArrayList<>();
        // 获取原始名字
        String fileName = file.getName();
        // 获取后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String jsonString = org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        jsonString = jsonString.replaceAll("\\r|\\n|\\s|\\t", "");
        // 如果是json或者txt文件
        if (".json".equals(suffixName) || ".txt".equals(suffixName)) {
            // 如果是集合
            if (jsonString.contains("[") || jsonString.contains("]")) {
                list = JSON.parseArray(jsonString, t);
            } else {
                JSONObject jsonObject = JSON.parseObject(jsonString);
                T t1 = JSON.toJavaObject(jsonObject, t);
                list.add(t1);
            }
        }
        return list;
    }

    /**
     * 根据 json 格式字符串写入到文件
     *
     * @param fullPath 导出完整路径
     * @param fileName 文件名称
     * @param jsonData json格式字符串
     * @return 写入的文件
     */
    public static File writeFile(String fullPath, String fileName, String jsonData) throws IOException {
        File file = new File(fullPath + fileName);

        // 如果父目录不存在，创建父目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 如果已存在,删除旧文件
        if (file.exists()) {
            file.delete();
        }
        // 创建新文件
        file.createNewFile();
        try (Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // 将格式化后的字符串写入文件
            write.write(jsonData);
            write.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}

package com.ge.apm.view.sysutil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileOperater {

    @Value("${token.path}")
    private String tokenPath;

    public String getfileinfo() {
        String rstr = "";

        try {
            FileSystemResource resource = new FileSystemResource(tokenPath);
            BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
            String str = null;
            while ((str = br.readLine()) != null) {
                rstr += str;
            }
            br.close();
        } catch (IOException e) {
            //todo loginfo
        }
        return rstr;
    }
}

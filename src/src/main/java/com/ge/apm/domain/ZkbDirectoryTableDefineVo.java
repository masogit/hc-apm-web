package com.ge.apm.domain;


import java.io.Serializable;

/**
 * @author by lyj 503277717
 * @date 2022/12/19.
 */
public class ZkbDirectoryTableDefineVo implements Serializable {

    private String name;
    /**
     * 主目录
     */
    private String mainDirectory;

    /**
     * 子目录
     */
    private String secondDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    public String getSecondDirectory() {
        return secondDirectory;
    }

    public void setSecondDirectory(String secondDirectory) {
        this.secondDirectory = secondDirectory;
    }
}

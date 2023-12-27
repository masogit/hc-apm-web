package com.ge.apm.domain.dto;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Data;

import java.io.Serializable;

@Data
public class GitProperties implements Serializable {

    private String commitId;
    private String commitDate;
    private String commitName;
    private String commitEmail;
    private String commitFull;
    private String branch;

    public  void     getGitProperties() {
        Config cfg = ConfigFactory.parseResources(this.getClass().getClassLoader(), "git.properties");
        commitId = cfg.getString("git.commit.id.abbrev");
        commitDate = cfg.getString("git.commit.time");
        commitName = cfg.getString("git.commit.user.name");
        commitEmail = cfg.getString("git.commit.user.email");
        commitFull = cfg.getString("git.commit.message.full");
        Config commonCfg = ConfigFactory.parseResources(this.getClass().getClassLoader(), "application-common-git.properties");
//        commonCommitId = commonCfg.getString("hc-apm-common-utils.commit.id.abbrev");
//        commonCommitName = commonCfg.getString("hc-apm-common-utils.commit.user.name");

    }


}

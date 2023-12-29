package com.ge.apm.web.gitproperties;

import com.ge.apm.domain.dto.GitProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/management/git/info")
public class GitPropertiesController {

    @Autowired
    private Environment environment;


    @RequestMapping(method = RequestMethod.GET ,produces = "application/json;charset=utf-8")
    public Object getGitInfo(){
        HashMap<String, Object> map = new HashMap<>();
        GitProperties gitProperties=new GitProperties();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("git", Locale.CHINA);
        String commitId=resourceBundle.getString("git.commit.id");
        String commitDate = resourceBundle.getString("git.commit.time");
        String commitName = resourceBundle.getString("git.commit.user.name");
        String commitEmail = resourceBundle.getString("git.commit.user.email");
        String commitFull = resourceBundle.getString("git.commit.message.full");
        String branch = resourceBundle.getString("git.branch");
        ResourceBundle resourceBundle1 = ResourceBundle.getBundle("application-common-git", Locale.CHINA);
        gitProperties.setCommitId(commitId);
        gitProperties.setCommitDate(commitDate);
        gitProperties.setCommitName(commitName);
        gitProperties.setCommitEmail(commitEmail);
        gitProperties.setCommitFull(commitFull);
        gitProperties.setBranch(branch);
        GitProperties commonProperties=new GitProperties();
        String commonCommitId = resourceBundle1.getString("hc-apm-common-utils.commit.id");
        String commonCommitDate = resourceBundle1.getString("hc-apm-common-utils.commit.time");
        String commonCommitName = resourceBundle1.getString("hc-apm-common-utils.commit.user.name");
        String commonCommitEmail = resourceBundle1.getString("hc-apm-common-utils.commit.user.email");
        String commonCommitFull = resourceBundle1.getString("hc-apm-common-utils.commit.message.full");
        String commonBranch = resourceBundle1.getString("hc-apm-common-utils.branch");
        commonProperties.setCommitId(commonCommitId);
        commonProperties.setCommitDate(commonCommitDate);
        commonProperties.setCommitName(commonCommitName);
        commonProperties.setCommitEmail(commonCommitEmail);
        commonProperties.setCommitFull(commonCommitFull);
        commonProperties.setBranch(commonBranch);
        HashMap<String, String> versionMap = new HashMap<>();
        versionMap.put("version",commitId);
        map.put("project",versionMap);
        map.put("hc-apm-web",gitProperties);
        map.put("hc-apm-common",commonProperties);
        return map;
    }

}

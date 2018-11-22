package com.ge.apm.service.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212547631
 */
@Component
public class MsgConfigService {
    private static final Logger logger = Logger.getLogger(MsgConfigService.class);
    public static final String ROOT = "default";
    public static final String ORG = "org";
    public static final String ASSET = "asset";
    public static final String TEMPORARY_ACCOUNT = "";
    private static Map<String, MsgConfig> msgConfighm;
    public static final String TEMP_STAFF = "仅报修权限账号";

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    UserOrgInfoRepository userOrgDao;

    @Autowired
    WorkflowConfigRepository wfcfigDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    private void loadMsgConfig(){
        if(msgConfighm!=null) return;
            
        MsgRepository msgRepository  = WebUtil.getBean(MsgRepository.class);
        List<MsgConfig> msgConfigList = msgRepository.find();

        msgConfighm = new HashMap<String, MsgConfig>();
        for(MsgConfig msgConfig: msgConfigList){
            msgConfighm.put(msgConfig.getMsgName(), msgConfig);
        }
    }
    

    


    public List<MsgTenantVO> getMsgTenantVO(String tenantUID){
        loadMsgConfig();
        TenantInfo ti = tenantInfoDao.findByuid(tenantUID);

        List<MsgTenantVO> msgTenantVOList = new ArrayList<>();
        for (MsgConfig tmpMsgConfig : msgConfighm.values()){
            MsgTenantVO msgt = new MsgTenantVO();
            msgt.setId(tmpMsgConfig.getId());
            msgt.setName(tmpMsgConfig.getMsgName());
          //  msgt.setRoleDesc(tmpMsgConfig.getRoleDesc());

           /* //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
            if(UserContextService.isOnlyLocalITAdmin()){
                if("ITAdmin".equals(tempSysRole.getName())){
                    //srVO.setDisable(Boolean.TRUE);
                    continue;
                }
            }
            //判断租户是否院区隔离
            if(ti.getInsulate() == null || !ti.getInsulate()){
                if("LocalITAdmin".equals(tempSysRole.getName())){
                    //srVO.setDisable(Boolean.TRUE);
                    continue;
                }
            }*/

            msgTenantVOList.add(msgt);
        }

        return msgTenantVOList;
    }






}

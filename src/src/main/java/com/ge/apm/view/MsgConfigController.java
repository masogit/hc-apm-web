package com.ge.apm.view;

import com.ge.apm.dao.MsgConfigTenantRepository;
import com.ge.apm.dao.MsgRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.MsgConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

import static java.util.stream.Collectors.toList;

@ManagedBean
@ViewScoped
public class MsgConfigController extends JpaCRUDController<MsgTenant> implements Serializable{
    @Getter
    @Setter
    public Map<String, Boolean> selectedMsgFields= new HashMap<String,Boolean>();;

    UserContextService userContextService;
    MsgConfigTenantRepository msgCTDao;
    @Getter
    @Setter
    private MsgConfigVO msgTenantVO;
    private Boolean isSelectedCurrentPage = false;
    @Getter
    @Setter
    private List<MsgConfigVO> msgTenantList;
    private static Map<String,MsgConfig> msgConf;
    @Getter
    @Setter
    public List<String> msgNameList;
    private MsgRepository msgRepository;
  //  public List<MsgConfigVO> msgVOList;

    @Getter
    @Setter
    private LinkedHashMap<String, String> allFields = new LinkedHashMap<String,String>();

    @Getter
    @Setter
    public List<String> msgNameListEn;
    @Getter
    @Setter
    private LinkedHashMap<String, String> allFieldsEn = new LinkedHashMap<String,String>();

    @Override
    protected void init(){
        userContextService = WebUtil.getBean(UserContextService.class);
        msgRepository  = WebUtil.getBean(MsgRepository.class);
        boolean locale=userContextService.isLocale();
        msgNameList = msgRepository.find().stream().map(item->item.getMsgName()).collect(Collectors.toList());
        msgNameListEn = msgRepository.find().stream().map(item->item.getMsgNameEn()).collect(Collectors.toList());

        msgNameList.stream().forEach(item->allFields.put(item,item));
        msgNameListEn.stream().forEach(item->allFieldsEn.put(item,item));


        if(locale) {
            List<String> selectedMsg = msgRepository.findByTenantUid(userContextService.getLoginUser().getTenantUID());
            msgNameList.stream().forEach(item -> selectedMsgFields.put(item, false));
            selectedMsg.stream().filter(item -> selectedMsgFields.containsKey(item)).filter(item -> selectedMsgFields.put(item, true)).collect(Collectors.toList());
            //  msgVOList = getMsgConfigVO(userContextService.getLoginUser().getTenantUID());
        }else{
            List<String> selectedMsgEn = msgRepository.findByTenantUidEn(userContextService.getLoginUser().getTenantUID());
            msgNameListEn.stream().forEach(item -> selectedMsgFields.put(item, false));
            selectedMsgEn.stream().filter(item -> selectedMsgFields.containsKey(item)).filter(item -> selectedMsgFields.put(item, true)).collect(Collectors.toList());

        }


        /*List<MsgConfig> selectedMsgConf = msgRepository.findByTenantUid2(userContextService.getLoginUser().getTenantUID());

        List<String> selectedMsg = msgRepository.findByTenantUid(userContextService.getLoginUser().getTenantUID());

        //List<String> selectedMsg6;

        //selectedMsgConf.stream().forEach(item->item.getMsgName().contains(selectedMsg3));
        //selectedMsgConf.stream().map(item->item.getMsgName()).forEach(item-> (selectedMsg3.contains(item) ? selectedMsg6.add(selectedMsgConf.get)));

        //personList.stream().map(p-> p.getName()).forEach(s -> System.out.println(s));

       if(locale){
            //List<String> selectedMsg2 = selectedMsg3.stream().map(item->item.equals(selectedMsg.iterator().next().getMsgName()) ).collect(Collectors.toList());
            msgNameList.stream().forEach(item->selectedMsgFields.put(item,false));
        }else{
            //msgRepository.find().stream().map(item->item.getMsgName().equals(selectedMsg.iterator().next().getMsgNameEn()) ).collect(Collectors.toList());
            msgNameList.stream().forEach(item->selectedMsgFields.put(item,false));
            //selectedMsg.stream().filter(item->selectedMsgFields.containsKey(item)).filter(item->selectedMsgFields.put(item,true)).collect(Collectors.toList());
        }*/



      //  msgVOList = getMsgConfigVO(userContextService.getLoginUser().getTenantUID());

    }

    public List<MsgConfigVO> getMsgConfigVO(String tenantUID){
        loadMsgConfigs();
        List<MsgConfigVO> msgConfigVOList =new ArrayList<>();
        for(MsgConfig mc: msgConf.values()){
           MsgConfigVO mcVO =new MsgConfigVO();
           mcVO.setMsgName(mc.getMsgName());
            msgConfigVOList.add(mcVO);
        }
            return msgConfigVOList;
    }

    @Transactional
    public void saveMsgTenant(){
        //删除原先的消息配置项(已租户级别)
        msgCTDao = WebUtil.getBean(MsgConfigTenantRepository.class);
        msgCTDao.deleteByTenantUID(userContextService.getLoginUser().getTenantUID());
       List<String> msgNameList =selectedMsgFields.entrySet().stream().filter(item->item.getValue()).map(item->item.getKey()).collect(toList());
        for(String msgName: msgNameList) {
            MsgConfig msgconfig = msgRepository.getByMsgName(msgName);
            if (msgconfig==null){
                logger.warn("Message Item not found, msgName= "+msgName);
            }else{
                MsgTenant msgT = new MsgTenant();
                msgT.setMsgConfigId(msgconfig.getId());
                msgT.setTenantUid(userContextService.getLoginUser().getTenantUID());
                msgCTDao.save(msgT);
            }
        }
        WebUtil.addSuccessMessage(WebUtil.getMessage("MessageConfigurationSaveSuccess"));
    }


    @Override
    protected GenericRepository<MsgTenant> getDAO() {
        return null;
    }

    private void loadMsgConfigs(){
        if(msgConf!=null) return;

        MsgRepository msgRepository = WebUtil.getBean(MsgRepository.class);
        List<MsgConfig> msgList = msgRepository.find();

        msgConf = new HashMap<String, MsgConfig>();
        for(MsgConfig mc: msgList){
            msgConf.put(mc.getMsgName(), mc);
        }
    }


    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
        isSelectedCurrentPage = selectedCurrentPage;
    }




}

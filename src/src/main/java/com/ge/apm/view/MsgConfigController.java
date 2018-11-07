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
    private MsgConfigVO msgTenantVO;
    private Boolean isSelectedCurrentPage = false;
    private List<MsgConfigVO> msgTenantList;
    private static Map<String,MsgConfig> msgConf;
    public List<String> msgNameList;
    private MsgRepository msgRepository;
    public List<MsgConfigVO> msgVOList;

    @Getter
    @Setter
    private LinkedHashMap<String, String> allFields = new LinkedHashMap<String,String>();




    @Override
    protected void init(){
        userContextService = WebUtil.getBean(UserContextService.class);
//        System.out.println(userContextService.getLoginUser().getTenantUID());
        msgRepository  = WebUtil.getBean(MsgRepository.class);
        msgNameList = msgRepository.find().stream().map(item->item.getMsgName()).collect(Collectors.toList());

        msgNameList.stream().forEach(item->allFields.put(item,item));

        List<String> selectedMsg = msgRepository.findByTenantUid(userContextService.getLoginUser().getTenantUID());
        msgNameList.stream().forEach(item->selectedMsgFields.put(item,false));


   selectedMsg.stream().filter(item->selectedMsgFields.containsKey(item)).filter(item->selectedMsgFields.put(item,true)).collect(Collectors.toList());
        msgVOList = getMsgConfigVO(userContextService.getLoginUser().getTenantUID());


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

    public void msgChangeListener(ValueChangeEvent e){
        if (e.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
            e.setPhaseId(PhaseId.INVOKE_APPLICATION);
            e.queue();
            return;
        }
        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);


    }

 /*   public boolean isTemporaryAccount(String mf) {
        Optional<MsgConfig> optionalSysRole = msgConf.values().stream().filter(msgcf -> msgcf.getMsgName().equals(mf) && sysRole.getId() == 9).findFirst();

        if (optionalSysRole.isPresent()) {
            return true;
        } else {
            return false;
        }

    }*/


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
        WebUtil.addSuccessMessage("消息配置保存成功");
    }


    @Override
    protected GenericRepository<MsgTenant> getDAO() {
        return null;
    }

    @Override
    protected Page<MsgTenant> loadData(PageRequest pageRequest) {

       /* Page<MsgTenant> result = dao.findAll(pageRequest);
        currentPagePmOrders = result.getContent();*/
        return null;
    }
    public void roleChangeListener(ValueChangeEvent e) {
        if (e.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
            e.setPhaseId(PhaseId.INVOKE_APPLICATION);
            e.queue();
            return;
        }

        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);


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

    /*public void onItemSelected() {
        isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
       // selectedMsgConfig = null;//
    }*/





    public MsgConfigVO getMsgTenantVO() {
        return msgTenantVO;
    }

    public void setMsgTenantVO(MsgConfigVO msgTenantVO) {
        this.msgTenantVO = msgTenantVO;
    }

    public List<MsgConfigVO> getMsgTenantList() {
        return msgTenantList;
    }

    public void setMsgTenantList(List<MsgConfigVO> msgTenantList) {
        this.msgTenantList = msgTenantList;
    }

    public static Map<String, MsgConfig> getMsgConf() {
        return msgConf;
    }

    public static void setMsgConf(Map<String, MsgConfig> msgConf) {
        MsgConfigController.msgConf = msgConf;
    }

    public MsgRepository getMsgRepository() {
        return msgRepository;
    }

    public void setMsgRepository(MsgRepository msgRepository) {
        this.msgRepository = msgRepository;
    }

    public List<MsgConfigVO> getMsgVOList() {
        return msgVOList;
    }

    public void setMsgVOList(List<MsgConfigVO> msgVOList) {
        this.msgVOList = msgVOList;
    }

    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
        isSelectedCurrentPage = selectedCurrentPage;
    }

    public List<String> getMsgNameList() {
        return msgNameList;
    }

    public void setMsgNameList(List<String> msgNameList) {
        this.msgNameList = msgNameList;
    }


}

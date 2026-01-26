package com.ge.apm.view;

import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.service.DifyShareDetailsService;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class DifyShareDetailsController {

    private DifyShareDetailsService difyShareDetailsService;

    private String encodeStr;
    private Integer userShareId;

    private List<ApmDropDownItem> messages;

    @PostConstruct
    public void init() {
        difyShareDetailsService= WebUtil.getBean(DifyShareDetailsService.class);
        encodeStr = WebUtil.getRequestParameter("str");
        String userShareId = WebUtil.getRequestParameter("userShareId");
        if(!StringUtils.isEmpty(userShareId)){
            this.userShareId = Integer.valueOf(userShareId);
        }

    }

    public String createShareMessage(){
        return difyShareDetailsService.createShareMessage(encodeStr);
    }

    public List<ApmDropDownItem>  getMessages(){
        if(messages == null){
            messages = difyShareDetailsService.getShareMessage(userShareId);
        }
        return messages;

    }
}

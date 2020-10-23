package com.ge.apm.view.sysutil;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.MicroServiceUtil;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@ManagedBean
@RequestScoped
public class LoginTelValidController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginTelValidController.class);

    private UserContextService userContextService;
    private MicroServiceUtil msUtil;
    private String telephone;
    private String encryTelephone;
    private String msmcode;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEncryTelephone() {
        return encryTelephone;
    }

    public void setEncryTelephone(String encryTelephone) {
        this.encryTelephone = encryTelephone;
    }

    public String getMsmcode() {
        return msmcode;
    }

    public void setMsmcode(String msmcode) {
        this.msmcode = msmcode;
    }

    @PostConstruct
    private void init() {
        msUtil = WebUtil.getBean(MicroServiceUtil.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.processAfterLogin();
        UserAccount user = userContextService.getLoginUser();

        telephone = user.getTelephone();

        encryTelephone = StringUtil.mobileEncrypt(telephone);


    }
    public void sendMsMCode(){
        logger.info("发送短信验证码");
        try{
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

            logger.info("sendMsMCode,telephone={}",telephone);
            Map<String,String> map = msUtil.getMsMCode(telephone);
            if(map==null){
                Boolean isSuccess = msUtil.sendMsMCode(telephone);
                if(isSuccess){
                    WebUtil.addSuccessMessage("验证码已发送,请查收短信");
                }else {
                    WebUtil.addErrorMessage("验证码发送失败,请稍后重试");
                }

            }else {
                String expire = map.get("expire");
                WebUtil.addSuccessMessage("验证码已发送,请查收短信."+expire+"分钟后可以重新发送");
            }
        }catch (Exception e){
            e.printStackTrace();
            WebUtil.addErrorMessage("验证码发送失败,请稍后重试");
        }
    }
    public void doLogin(){

        logger.info("校验验证码,进行登录");
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        logger.info("phone login: telephone={},mdmCode={}",telephone,msmcode);
        if(StringUtils.isEmpty(telephone)){
            WebUtil.addErrorMessage("手机号不能为空");
            return;
        }
        if(StringUtils.isEmpty(msmcode)){
            WebUtil.addErrorMessage("验证码不能为空");
            return;
        }
        Map<String,String> map = msUtil.getMsMCode(telephone);
        if(map == null){
            WebUtil.addErrorMessage("验证码已经过期,请重新发送");
            return;
        }
        String phone = map.get("phone");
        String code = map.get("code");
        if(phone.equals(phone) && msmcode.equals(code)){
            doTelephoneValidlogin(request, response);
            // called after user logined.
            String str = userContextService.getUserDefaultHomePage();
            logger.info("phone valid login ,userDefaultHomePage: {}",str);
            WebUtil.redirectTo(str);
        }else {
            WebUtil.addErrorMessage("验证码错误");
        }

    }

    private void doTelephoneValidlogin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute("PHONE_SMSCODE_VALID",true);
    }
}

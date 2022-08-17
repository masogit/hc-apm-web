package com.ge.apm.view.uaa;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.util.TimeUtil;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
@ManagedBean
@ViewScoped
public class ProfileController implements Serializable {
    private static final long serialVersionUID = 1L;
    Logger logger =  LoggerFactory.getLogger(getClass());
    private UserAccount myAccount;
    
    private String newPassword;
    private String newPassword2;
    private String password;
    private String originalTelephone=null;//原始
    private UserAccount loginUser;
    UserAccountService userAccountService;

    
    @PostConstruct
    public void init(){
        myAccount = UserContextService.getCurrentUserAccount();
        if(loginUser == null){
            loginUser = new UserAccount();
            loginUser.setTelephone(myAccount.getTelephone());
            loginUser.setId(myAccount.getId());
        }
        if(originalTelephone == null){
            originalTelephone = myAccount.getTelephone();
        }
        if(myAccount != null && myAccount.getWeChatId() != null){
        	myAccount.setIsUnbunding(true);
        }
        userAccountService = WebUtil.getBean(UserAccountService.class);
    }
    
    public UserAccount getMyAccount() {
        return myAccount;
    }

    public void setMyAccount(UserAccount myAccount) {
        this.myAccount = myAccount;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void saveProfile(){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        if(!myAccount.getIsUnbunding()){
        	myAccount.setWeChatId(null);
        }
        boolean isRepeated = userAccountService.whetherTheCallIsRepeated(myAccount.getTelephone(),loginUser);
        if(isRepeated){//重复
            myAccount.setTelephone(originalTelephone);
            userAccountDao.save(myAccount);
            WebUtil.addSuccessMessage(WebUtil.getMessage("ErrorDuplicatedData")+":"+WebUtil.getMessage("telephone"));
        }else{
            userAccountDao.save(myAccount);
            WebUtil.addSuccessMessage(WebUtil.getMessage("UserAccount")+WebUtil.getMessage("Updated"));
        }
    }
    
    public void resetPassword() throws NoSuchAlgorithmException, DecoderException {
        // check current password.
        if(!checkPassword()){
            WebUtil.addSuccessMessage(WebUtil.getMessage("PasswordNotMatch"));
            return;
        }

        String tempNewPassword = UserContextService.decryptData(newPassword);

    	String result = PasswordUtil.checkPwd(tempNewPassword);
    	if(!StringUtils.isEmpty(result)){
    		WebUtil.addSuccessMessage(result);
    		return;
    	}
    	if(samePwd(tempNewPassword)){
    		WebUtil.addSuccessMessage(WebUtil.getMessage("SamePwd"));
    		return;
    	}
        myAccount.setPlainPassword(tempNewPassword);
        myAccount.entryptPassword();
        myAccount.setLastModifiedDate(TimeUtil.now());
        myAccount.setLastModifiedBy(myAccount.getLoginName());
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userAccountDao.save(myAccount);

        password = "";
        newPassword = "";
        newPassword2 = "";
        
        WebUtil.addSuccessMessage(WebUtil.getMessage("Password")+WebUtil.getMessage("Updated"));
    }

	private boolean samePwd(String newPassword) {
		
		 UserContextService userContextService = WebUtil.getBean(UserContextService.class);
		 UserAccount user = userContextService.getLoginUser();
		try {
			 byte[] salt = Digests.decodeHex(user.getPwdSalt());
			 byte[] hashPassword = Digests.sha1(newPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
			 String saltedPassword = Digests.encodeHex(hashPassword);
			 if(user.getPassword().equals(saltedPassword)){
				 return true;
			 }
		} catch (DecoderException | NoSuchAlgorithmException e) {
			logger.error("samePwd method error:{}",e);
			e.printStackTrace();
		}
		return false;
	}

    /**
     * check current password.
     * @return
     * @throws DecoderException
     * @throws NoSuchAlgorithmException
     */
	public boolean checkPassword() throws DecoderException, NoSuchAlgorithmException {
        String currentPwd = UserContextService.decryptData(password);
        if (!StringUtils.isEmpty(currentPwd)) {
            byte[] salt = Digests.decodeHex(myAccount.getPwdSalt());
            byte[] hashPassword = Digests.sha1(currentPwd.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            String saltedPassword = Digests.encodeHex(hashPassword);
            if(myAccount.getPassword().equals(saltedPassword)){
                return true;
            }
        }
        return false;
    }
}

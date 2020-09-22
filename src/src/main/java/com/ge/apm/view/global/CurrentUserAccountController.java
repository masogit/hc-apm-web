package com.ge.apm.view.global;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean
@ViewScoped
public class CurrentUserAccountController {

    @Getter
    UserAccount currentUserAccount;

    @PostConstruct
    protected void init() {
        currentUserAccount = UserContextService.getCurrentUserAccount();
    }

    public Date getCurrentDateTime() {
        return new Date();
    }

}
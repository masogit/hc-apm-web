package com.ge.apm.listener;

import com.ge.apm.domain.UserAccount;

public class UserAccountThreadLocal {
    static ThreadLocal<UserAccount> userAccount=new ThreadLocal<>();



    public static void setUserAccount(UserAccount user){
        userAccount.set(user);
    }
    public  static UserAccount getUserAccount() {
        UserAccount user = userAccount.get();
        return user;
    }
    public static void remove(){
        userAccount.remove();
    }



}

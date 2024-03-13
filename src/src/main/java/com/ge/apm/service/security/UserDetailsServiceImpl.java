package com.ge.apm.service.security;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.GrantedAuthorityDiyImpl;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.TimeUtil;

/**
 * An implementation of Spring Security's UserDetailsService.
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = Logger.getLogger(UserDetailsServiceImpl.class);

    private UserAccountRepository accountRepository;

    public UserDetailsServiceImpl() {
    }

    @Autowired
    public UserDetailsServiceImpl(UserAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    private TenantInfoRepository tenantInfoRepository;


    @Autowired
    private CustomRoleRepository customRoleRepository;

    /**
     * Retrieve an account depending on its login this method is not case sensitive.<br>
     * use <code>obtainAccount</code> to match the login to either email, login or whatever is your login logic
     *
     * @param login the account login
     * @return a Spring Security userdetails object that matches the login
     * @see #obtainAccount(String)
     * @throws UsernameNotFoundException when the user could not be found
     * @throws DataAccessException when an error occured while retrieving the account
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException, DataAccessException {
        if (login == null || login.trim().isEmpty()) {
            throw new UsernameNotFoundException("Empty login");
        }

        if (log.isDebugEnabled()) {
            log.debug("Security verification for user '" + login + "'");
        }

        UserAccount account = obtainAccount(login);

        if (account == null) {
            account = obtainAccount("ge" + login);
            if (account != null) {
                log.info("Account ge" + login + " is found");
            }
        }

        if (account == null) {
            if (log.isInfoEnabled()) {
                log.info("Account " + login + " could not be found");
            }
            throw new UsernameNotFoundException("account " + login + " could not be found");
        }

        if(account.getIsLocked()!=null && account.getIsLocked().booleanValue()==true){
            if(TimeUtil.toJodaDate(account.getLockTime()).plusHours(24).isBeforeNow()){
                account.setIsLocked(false);
                account.setPasswordErrorCount(0);
                accountRepository.save(account);
            }
            else
                throw new LockedException("account locked");
        }

        Collection<GrantedAuthority> grantedAuthorities = obtainGrantedAuthorities(login);

        if (grantedAuthorities == null) {
            TenantInfo tenantInfo = tenantInfoRepository.findByuid(account.getTenantUID());
            grantedAuthorities = toGrantedAuthorities(account.getRoleNames(), tenantInfo);
            getCustomRole(account,grantedAuthorities);
        }
        String password = obtainPassword(login);

        if (password == null) {
            password = account.getPassword();
        }

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(login, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, grantedAuthorities);
    }

    /**
     * Return the account depending on the login provided by spring security.
     * @return the account if found
     */
    protected UserAccount obtainAccount(String login) {
        return accountRepository.getByLoginName(login);
    }

    /**
     * Returns null. Subclass may override it to provide their own granted authorities.
     */
    protected Collection<GrantedAuthority> obtainGrantedAuthorities(String username) {
        return null;
    }

    /**
     * Returns null. Subclass may override it to provide their own password.
     */
    protected String obtainPassword(String username) {
        return null;
    }

    public static Collection<GrantedAuthority> toGrantedAuthorities(List<String> roles, TenantInfo tenantInfo) {
        List<GrantedAuthority> result = new ArrayList<>();
        Set<String> featureList = new HashSet<>(((List<String>) JSON.parse(tenantInfo.getFeatureList())));
        Set<ApmSystemRole> apmSystemRoles = Arrays.stream(ApmSystemRole.values()).filter(r -> roles.contains(r.name())).collect(Collectors.toSet());
        Map<String, ApmSystemRole> systemRoleMap = apmSystemRoles.stream().collect(Collectors.toMap(ApmSystemRole::name, e -> e));
        List<ApmFeature> features = Arrays.stream(ApmFeature.values()).filter(feature -> featureList.contains(feature.name())).collect(Collectors.toList());
        List<ApmFunction> apmFunctionList = ListUtil.newArrayList();
        features.forEach(feature -> apmFunctionList.addAll(feature.getFunctionList()));
        for (String role : roles) {
            Set<String> roleFunctionList = new HashSet<>();
            for (ApmFunction apmFunction : apmFunctionList) {
                ApmSystemRole apmSystemRole = systemRoleMap.get(role);
                if (apmFunction.getRoleList().contains(apmSystemRole)) {
                    roleFunctionList.add(apmFunction.name());
                }
            }
            result.add(new GrantedAuthorityDiyImpl(role, roleFunctionList));
        }

        return result;
    }

    public void getCustomRole(UserAccount userAccount,Collection<GrantedAuthority> grantedAuthorities){
        if(CollectionUtils.isNotEmpty(grantedAuthorities)){
            List<CustomRole> customRolesList = customRoleRepository.findCustomRolesByUserId(userAccount.getId());
            if(CollectionUtils.isNotEmpty(customRolesList)){
                customRolesList.forEach(item->{
                    Set<String> strings = JSON.parseObject(item.getFunctionList(), new TypeReference<Set<String>>() {
                    });
                    grantedAuthorities.add(new GrantedAuthorityDiyImpl(item.getName(), strings));
                });
            }
        }
    }
}
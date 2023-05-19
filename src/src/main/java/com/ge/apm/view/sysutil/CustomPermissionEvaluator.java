package com.ge.apm.view.sysutil;

import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    public static Set<String> apmFunctionList = null;
    public static Set<ApmSystemRole> apmSystemRoles = null;
    public static Set<String> apmFeatures = null;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object functionName) {
        // 在这里实现自定义的权限验证逻辑
        // authentication：当前用户的认证信息
        // targetDomainObject：要访问的资源对象
        // permission：权限表达式，例如 "ASSET_"
        return checkPermission(functionName.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object functionName) {
        // 如果你的应用中使用了基于ID的权限验证，可以在这里实现相应的逻辑
        return false;
    }

    private boolean checkPermission(String functionName) {
        // 在这里实现具体的权限验证逻辑，例如根据 userDetails 和 targetDomainObject 判断是否具有权限
        if (CollectionUtils.isEmpty(apmFunctionList)) return false;
        return CollectionUtils.isNotEmpty(apmFunctionList.stream().filter(function -> function.startsWith(functionName)).collect(Collectors.toSet()));
    }



}

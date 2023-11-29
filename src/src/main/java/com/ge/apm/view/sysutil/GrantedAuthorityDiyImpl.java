package com.ge.apm.view.sysutil;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Set;

public class GrantedAuthorityDiyImpl implements GrantedAuthority {
    private static final long serialVersionUID = 310L;
    private final String role;
    private final Set<String> functionList;

    public GrantedAuthorityDiyImpl(String role, Set<String> functionList) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
        this.functionList = functionList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(this.role);
        } else if (obj instanceof GrantedAuthority) {
            GrantedAuthority attr = (GrantedAuthority)obj;
            return this.role.equals(attr.getAuthority());
        } else {
            return false;
        }
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public Set<String> getFunctionList() {
        return this.functionList;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}

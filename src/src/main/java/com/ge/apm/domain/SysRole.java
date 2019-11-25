/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "sys_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 32)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "role_desc")
    private String roleDesc;
    @Size(max = 255)
    @Column(name = "role_desc_en")
    private String roleDescEn;
    @Size(max = 64)
    @Column(name = "home_page")
    private String homePage;

    @Column(name = "cards")
    private String cards;

    @Column(name = "authorities")
    private String authorities;

    public String getRoleDescEn() {
        return roleDescEn;
    }

    public void setRoleDescEn(String roleDescEn) {
        this.roleDescEn = roleDescEn;
    }

    @Column(name = "authorities_ch")
    private String authoritiesCH;

    @Column(name = "cards_ch")
    private String cardsCH;
    
    @Column(name = "menus",columnDefinition="text")
    private String menus;

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public String getAuthoritiesCH() {
        return authoritiesCH;
    }

    public void setAuthoritiesCH(String authoritiesCH) {
        this.authoritiesCH = authoritiesCH;
    }

    public String getCardsCH() {
        return cardsCH;
    }

    public void setCardsCH(String cardsCH) {
        this.cardsCH = cardsCH;
    }

    public SysRole() {
    }

    public SysRole(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SysRole)) {
            return false;
        }
        SysRole other = (SysRole) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SysRole[ id=" + id + " ]";
    }
    
}

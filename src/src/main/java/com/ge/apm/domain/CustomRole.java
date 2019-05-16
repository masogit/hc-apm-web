package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "custom_role")
public class CustomRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "home_page")
    private String homePage;

    @Column(name = "role_desc")
    private String roleDesc;

    @Column(name = "authorities",columnDefinition="text")
    private String authorities;
    
    @Column(name = "web_authorities",columnDefinition="text")
    private String webAuthorities;
    
    @Column(name = "cards",columnDefinition="text")
    private String cards;
    
    @Column(name = "menus",columnDefinition="text")
    private String menus;
    
    @Column(name = "menus_ch")
    private String menusCH;

    @Column(name = "authorities_ch")
    private String authoritiesCH;

    @Column(name = "cards_ch")
    private String cardsCH;
    
    @Column(name = "web_authorities_ch")
    private String webAuthoritiesCH;

    @Column(name = "groups")
    private String groups;

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy = "system";

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date lastModifiedDate = new Date();

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
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

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
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

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public String getMenusCH() {
		return menusCH;
	}
	
	public void setMenusCH(String menusCH) {
		if(menusCH!=null && menusCH.length()>60){
			menusCH=menusCH.substring(0, 60);
			menusCH+="...";
		}
		this.menusCH = menusCH;
	}

	public String getWebAuthorities() {
		return webAuthorities;
	}

	public void setWebAuthorities(String webAuthorities) {
		this.webAuthorities = webAuthorities;
	}

	public String getWebAuthoritiesCH() {
		return webAuthoritiesCH;
	}

	public void setWebAuthoritiesCH(String webAuthoritiesCH) {
		if(webAuthoritiesCH!=null && webAuthoritiesCH.length()>60){
			webAuthoritiesCH=webAuthoritiesCH.substring(0, 60);
			webAuthoritiesCH+="...";
		}
		this.webAuthoritiesCH = webAuthoritiesCH;
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
        if (!(object instanceof CustomRole)) {
            return false;
        }
        CustomRole other = (CustomRole) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}

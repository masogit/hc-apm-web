/*
 */
package com.ge.apm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import webapp.framework.web.service.UserContext;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "i18n_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class I18nMessage implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "msg_type")
    private String msgType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "msg_key")
    private String msgKey;
    @Size(max = 128)
    @Column(name = "value_zh")
    private String valueZh;
    @Size(max = 128)
    @Column(name = "value_en")
    private String valueEn;
    @Size(max = 128)
    @Column(name = "value_tw")
    private String valueTw;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "order_by")
    private Integer orderBy;
    @Column(name = "enabled", columnDefinition = "boolean default true")
    private Boolean enabled = true;
    @Transient
    private boolean editable;
    @Transient
    private String customConfig;

    public I18nMessage() {
    }

    public I18nMessage(Integer id) {
        this.id = id;
    }

    public I18nMessage(Integer id, String msgType, Object msgKey, String valueZh, String valueEn, String valueTw, Integer siteId) {
        this.id = id;
        this.msgType = msgType;
        this.msgKey = msgKey.toString();
        this.valueZh = valueZh;
        this.valueEn = valueEn;
        this.valueTw = valueTw;
        this.siteId = siteId;
    }

    public I18nMessage(String msgType, String msgKey, String valueZh) {

        this.msgType = msgType;
        this.msgKey = msgKey;
        this.valueZh = valueZh;
        this.siteId = -1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getValueZh() {
        return valueZh;
    }

    public void setValueZh(String valueZh) {
        if(valueZh!=null){
            valueZh = valueZh.trim();
            if("".equals(valueZh)) valueZh = null;
        }
        this.valueZh = valueZh;
    }

    public String getValueEn() {
        return valueEn;
    }

    public void setValueEn(String valueEn) {
        if(valueEn!=null){
            valueEn = valueEn.trim();
            if("".equals(valueEn)) valueEn = null;
        }
        this.valueEn = valueEn;
    }

    public String getValueTw() {
        return valueTw;
    }

    public void setValueTw(String valueTw) {
        if(valueTw!=null){
            valueTw = valueTw.trim();
            if("".equals(valueTw)) valueTw = null;
        }
        this.valueTw = valueTw;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getValue(String lang){
        if("zh".equals(lang) && valueZh!=null)
            return valueZh;
        else if("en".equals(lang) && valueEn!=null)
            return valueEn;
        else if("tw".equals(lang) && valueTw!=null)
            return valueTw;
        
        //so get Zh value if no message define for specific lang
        return valueZh;
    }
    
    public String getValue(){
        try{
            String lang = UserContext.getLocale().toString().substring(0, 2);
            return this.getValue(lang);
        }
        catch(Exception ex){
            return this.getValue("zh");
        }
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
        if (!(object instanceof I18nMessage)) {
            return false;
        }
        I18nMessage other = (I18nMessage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }



    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("I18nMessage [id=");
		builder.append(id);
		builder.append(", msgType=");
		builder.append(msgType);
		builder.append(", msgKey=");
		builder.append(msgKey);
		builder.append(", valueZh=");
		builder.append(valueZh);
		builder.append(", valueEn=");
		builder.append(valueEn);
		builder.append(", valueTw=");
		builder.append(valueTw);
        builder.append(", enabled=");
        builder.append(enabled);
		builder.append(", siteId=");
		builder.append(siteId);
		builder.append("]");
		return builder.toString();
	}

	@Override
    public int compareTo(Object t) {
        I18nMessage otherMsg = (I18nMessage)t;
        try{
            Integer key1 = Integer.valueOf(this.getMsgKey());
            Integer key2 = Integer.valueOf(otherMsg.getMsgKey());
            
            return key1.compareTo(key2);
        }
        catch(Exception ex){
            return this.getMsgKey().compareTo(otherMsg.getMsgKey());
        }
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(String customConfig) {
        this.customConfig = customConfig;
    }
}

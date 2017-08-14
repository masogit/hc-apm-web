/*
 */
package com.ge.apm.domain;

import java.awt.Color;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "qr_code_template_element")
public class QrCodeTemplateElement implements Serializable {

    public enum QrCodeElementType {
        QrCode, Image, Text
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "template_Id", columnDefinition = "CHAR(32)")
    private String templateId;

    @Column(name = "element_type")
    private QrCodeElementType type;

    @Column(name = "text_value")
    private String textValue;
    
    @Column(name = "field_key")
    private String fieldKey;

    @Column(name = "position_x", columnDefinition = "int")
    private Integer positionX = 0;

    @Column(name = "position_y", columnDefinition = "int")
    private Integer positionY = 0;

    @Column(name = "size", columnDefinition = "int")
    private Integer size = 300;

    @Column(name = "font_Name")
    private String fontName = "TimesRoman";

    @Column(name = "font_size", columnDefinition = "int")
    private Integer fontSize = 16;
    
    @Column(name = "font_type", columnDefinition = "int")
    private int fontType = 1;
    
    @Column(name = "font_color", columnDefinition = "int")
    private Integer fontColor = Color.BLACK.getRGB();

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    
    @Transient
    private String textShowValue;

    public String getTextShowValue() {
        return textShowValue;
    }

    public void setTextShowValue(String textShowValue) {
        this.textShowValue = textShowValue;
    }
    
    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public QrCodeElementType getType() {
        return type;
    }

    public void setType(QrCodeElementType type) {
        this.type = type;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontType() {
        return fontType;
    }

    public void setFontType(int fontType) {
        this.fontType = fontType;
    }

    public Color getFontColor() {
        return new Color(fontColor);
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor.getRGB();
    }

    public String getColorStr(){
        Color color = getFontColor();
        String R = Integer.toHexString(color.getRed());  
        R = R.length() < 2 ? ('0' + R) : R;  
        String B = Integer.toHexString(color.getBlue());  
        B = B.length() < 2 ? ('0' + B) : B;  
        String G = Integer.toHexString(color.getGreen());  
        G = G.length() < 2 ? ('0' + G) : G;  
        return R + G + B;
    }
    
    public void setColorStr(String str){
        int i = Integer.parseInt(str, 16);
        setFontColor(new Color(i));
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
        if (!(object instanceof QrCodeTemplateElement)) {
            return false;
        }
        QrCodeTemplateElement other = (QrCodeTemplateElement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.QrCodeLib[ id=" + id + " ]";
    }

}

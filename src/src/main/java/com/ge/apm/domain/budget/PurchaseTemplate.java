package com.ge.apm.domain.budget;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lyj
 */
@Entity
@Table(name = "purchase_template")
public class PurchaseTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    // 采购单id
    @Column(name = "purchase_id")
    private String purchaseId;

    //模板id
    @Column(name = "template_id")
    private String templateId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PurchaseTemplate)) {
            return false;
        }
        PurchaseTemplate other = (PurchaseTemplate) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

}

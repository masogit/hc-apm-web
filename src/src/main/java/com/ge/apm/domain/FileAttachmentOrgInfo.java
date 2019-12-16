package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author 212605082 on 2019/12/2.
 */
@Entity
@Table(name = "file_attachment_org_info")
public class FileAttachmentOrgInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "blob_object_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String blobObjectUid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "blob_object_uid", referencedColumnName = "id")
    private V2_BlobObject blobObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlobObjectUid() {
        return blobObjectUid;
    }

    public void setBlobObjectUid(String blobObjectUid) {
        this.blobObjectUid = blobObjectUid;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public V2_BlobObject getBlobObject() {
        return blobObject;
    }

    public void setBlobObject(V2_BlobObject blobObject) {
        this.blobObject = blobObject;
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
        if (!(object instanceof FileAttachmentOrgInfo)) {
            return false;
        }
        FileAttachmentOrgInfo other = (FileAttachmentOrgInfo) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}

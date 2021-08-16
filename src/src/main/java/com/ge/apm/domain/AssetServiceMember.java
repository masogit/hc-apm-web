package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Entity
@Table(name = "asset_service_member")
public class AssetServiceMember {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "system_id")
    private String systemId;

    @Basic(optional = false)
    @Column(name = "asset_uid")
    private String assetUid;

    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    @Basic(optional = false)
    @Column(name = "user_uid")
    private String userUid;

    @JoinColumn(name = "user_uid", referencedColumnName = "uid", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.EAGER)
    private UserAccount user;

    @Column(name = "ge_user_type")
    private Integer geUserType;

    @Column(name = "import_file_id")
    private String importFileId;

    @Column(name = "import_file_type")
    private String importFileType;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public Integer getGeUserType() {
        return geUserType;
    }

    public void setGeUserType(Integer geUserType) {
        this.geUserType = geUserType;
    }

    public String getImportFileId() {
        return importFileId;
    }

    public void setImportFileId(String importFileId) {
        this.importFileId = importFileId;
    }

    public String getImportFileType() {
        return importFileType;
    }

    public void setImportFileType(String importFileType) {
        this.importFileType = importFileType;
    }
}

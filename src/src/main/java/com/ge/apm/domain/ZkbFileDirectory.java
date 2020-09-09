package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 *
 * @author 503202755
 */
@Entity
@Table(name = "zkb_file_directory")
public class ZkbFileDirectory{

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    /**
     * 目录名称
     */
    @Column(name = "name")
    private String name;


    /**
     * 目录级别
     * value 1 : 主目录
     * value 2 : 子目录
     */
    @Column(name = "level")
    private Integer level;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", unique = false, insertable = true, updatable = true)
    private ZkbFileDirectory zkbFileDirectory;

    public ZkbFileDirectory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public ZkbFileDirectory getZkbFileDirectory() {
        return zkbFileDirectory;
    }

    public void setZkbFileDirectory(ZkbFileDirectory zkbFileDirectory) {
        this.zkbFileDirectory = zkbFileDirectory;
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
        if (!(object instanceof ZkbFileDirectory)) {
            return false;
        }
        ZkbFileDirectory other = (ZkbFileDirectory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbFileDirectory[ id=" + id + " ]";
    }
}

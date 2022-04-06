package com.ge.apm.domain;


import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "nurse_site_maintain_data_history")
public class NurseSiteMaintainDataHistory {

    @Id
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name="parent_id")
    private String parentId;

    @Column(name="check_item")
    private String checkItem;

    @Column(name="check_value")
    private Boolean checkValue;

    @Column(name="index")
    private Long index;


}

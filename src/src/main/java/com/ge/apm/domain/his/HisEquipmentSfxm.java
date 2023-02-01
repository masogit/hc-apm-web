package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 503189826 on 7/26/2020.
 */

@Entity
@Table(name = "his_equipment_sfxm") //抓取收费代码
@Data
public class HisEquipmentSfxm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "sfxm_id")//His收费项目代码
    private String sfxmId;

    @Column(name = "sfxm_name")//His收费项目名称
    private String sfxmName;

    @Column(name = "sfxm_lb")//收费项目分类
    private String sfxmLb;

    @Column(name = "hos_id")//医院机构代码
    private String hosId;

    @Column(name = "is_matched")//是否匹配过,默认是false
    private Boolean isMatched;

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }

}

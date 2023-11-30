package com.ge.apm.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : 503189362
 * @date : 2023/10/24
 * @description :
 */
public class ZkbPartOrderStatusVo {

    @Getter@Setter
    private boolean showTech=false;
    @Getter@Setter
    private boolean showTse=false;
    @Getter@Setter
    private boolean showProduct = false;
    @Getter@Setter
    private boolean showManage=false;
    @Getter@Setter
    private boolean showOssc=false;
    @Getter@Setter
    private boolean showDirector = false;
    @Getter@Setter
    private boolean showCommercial = false;
    @Getter@Setter
    private boolean showUnOrder = false;
    @Getter@Setter
    private boolean showOrder=false;
    @Getter@Setter
    private boolean showReturn=false;
    @Getter@Setter
    private boolean showRejectOrder = false;
    @Getter@Setter
    private boolean showClosed=false;
    @Getter@Setter
    private boolean showOsscReturn=false;
    @Getter@Setter
    private boolean canEditFlag=false;
    @Getter@Setter
    private boolean isChoseAdmin = false;
    @Getter@Setter
    private boolean isOrder = false;
    @Getter@Setter
    private boolean isUnOrder = false;
    @Getter@Setter
    private boolean whetherDeductEdit = false;
    @Getter@Setter
    private boolean showBtnFourHundredCancel = false;
    @Getter@Setter
    private boolean showComplete = false;
    @Getter@Setter
    private boolean showCompleteAudit = false;

    /**
     *
     */
    @Getter@Setter
    private String showBtn = "";
    @Getter@Setter
    private String valueTypeTips = "";
    @Getter@Setter
    private String trainTips="";
    @Getter@Setter
    private String unrealTips = "";
    @Getter@Setter
    private String saveTips;
    @Getter@Setter
    private String letterTips;
    @Getter@Setter
    private String showOrderedFieldsetText = "";
    //1显示探头 2:显示sn列表
    @Getter@Setter
    private String showCatListFlag = "";

    @Getter@Setter
    private boolean haveTrainCourseFlag = false;


}

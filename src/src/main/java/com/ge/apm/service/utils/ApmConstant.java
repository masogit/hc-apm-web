package com.ge.apm.service.utils;

public interface ApmConstant {

    public static final int MVS_ROLE_TYPE_ADMIN = 1;
    public static final int MVS_ROLE_TYPE_FE = 2;

    public static final int MVS_ROLE_TYPE_ADMIN_2 = 3;
    public static final int MVS_ROLE_TYPE_FE_2 = 4;

    public static final int SUBSTATUS1 = 1;
    public static final int SUBSTATUS2 = 2;
    public static final int SUBSTATUS3 = 3;

    public static final String MVS_ROLE_NAME_ADMIN = "MVSAdmin";
    public static final String MVS_ROLE_NAME_FE = "MVSFE";

    //this type value is for company IDEAL
    public static final int USER_ACCOUNT_TYPE_PARTNER = -1;

    String CHILD_CLINICAL_DEPT_SEPARATOR = "-";

    int IB_TENANT_ID = 46;
    public static final String TREASURY_USE = "1";//库房领用
    public static final String MEDICAL_WORKERS_PURCHASING = "2";//医工采购

    //自定义角色类型
    static final int CUSTOME_ROLE_TYPE = 1;
    //默认site_id
    static final int DEFAULT_SITE_ID = -1;

    //static String stepNameCanceld="已取消";
    static String stepNameWaitAssign="待派工";
    static String stepNameWaitForClosed="待关单";

    String MVS_TENANT_NAME = "GE MVS";

    String DEVELOPER_USE_CONTRACT_NUM = "WBSXXXXXXXX";
}

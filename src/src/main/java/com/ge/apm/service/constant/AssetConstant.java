package com.ge.apm.service.constant;

public class AssetConstant {
    public final static int ASSET_STATUS_DELETED = -1;//已删除 isDeleted = true
    public final static int ASSET_STATUS_INVALID = -2;//报废 不可用 in_valid = false
    public final static int ASSET_STATUS_NORMAL = 1;//正常
    public final static int ASSET_STATUS_DOWN = 2;//停机
    public final static int ASSET_STATUS_ILL = 3;//限制功能使用
    public final static int ASSET_STATUS_TO_TERMINATED = 4;//待报废
    public final static int ASSET_STATUS_STOP = 5;//停用
    public final static int ASSET_STATUS_ALARM = 6;//发警报

}

package com.ge.apm.service.constant;

public class ReceiveBorrowConstant {

    private static final long serialVersionUID = 1L;
    public static final int BEHAVIOR_RENT = 1; //借用
    public static final int BEHAVIOR_RETURN = 2; //归还

    public static final int STATE_RENT_EXPIRED = 0; //借用、领用已失效
    public static final int STATE_RENT_REQUESTED = 1; //借用、领用待受理
    public static final int STATE_RENT_ACCEPTED = 2; //借用、领用待交付
    public static final int STATE_RENT_DELIVERED = 3; //借用、领用待确认
    public static final int STATE_RENT_CANCELED = 4; //借用、领用已取消
    public static final int STATE_RENT_SUSPENDED = 5; //借用、领用已搁置
    public static final int STATE_RENT_CONFIRMED = 6; //借用、领用已完成

    public static final int STATE_RETURN_REQUESTED = 7; //归还 待受理
    public static final int STATE_RETURN_ACCEPTED = 8; //归还 待交付
    public static final int STATE_RETURN_DELIVERED = 9; //归还待确认
    public static final int STATE_RETURN_CANCELED = 10; //归还已取消
    public static final int STATE_RETURN_SUSPENDED = 11; //归还已搁置
    public static final int STATE_RETURN_CONFIRMED = 12; //归还已完成
    public static final int STATE_RETURN_EXPIRED = 13; //归还已失效

}

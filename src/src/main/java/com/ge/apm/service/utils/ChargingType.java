package com.ge.apm.service.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * 定义计费类型
 * @author nick
 */
public enum ChargingType {

    /*
     * 按次收费类型
     */
    PAY_PER("次", "1"),
    /*
     * 按小时收费，step为半小时
     */
    PAY_HALF_HOUR("小时(半点)", "2"),
    /*
     * 按天收费
     */
    PAY_DAY("天", "3"),
    /*
     * 按自然时间收费，即按分钟收费
     */
    PAY_MINUTE("自然时间", "4"),
    /*
     * 按小时收费，step为小时
     */
    PAY_HOUR("小时(整点)", "5");

    @Getter
    @Setter
    private String desc;

    @Getter
    @Setter
    private String index;

    ChargingType(String desc, String index) {
        this.desc = desc;
        this.index = index;
    }

    public static ChargingType getChargingTypeByIndex(String index) {
        // 缺省按自然计费计算费用
        ChargingType rst = ChargingType.PAY_MINUTE;
        for(ChargingType chargingType : ChargingType.values()) {
            if(chargingType.index.equals(index)) {
                rst = chargingType;
            }
        }
        return rst;
    }

}

package com.ge.apm.service.utils;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import javaslang.Tuple2;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

public class SharedAssetInfoUtils {

    private static Logger log = Logger.getLogger(SharedAssetInfoUtils.class.getName());

    public static Tuple2<BigDecimal, String> getChargingUnitAndTotalTime(SharedAssetReqDetail reqDetail) {
        Tuple2<BigDecimal, String> result = null;
        LocalDateTime beginLDT;
        LocalDateTime endLDT;

        if(reqDetail.getChargingEndTime() != null) {
            endLDT = reqDetail.getChargingEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            endLDT = LocalDateTime.now();
        }

        String totalTime = "";
        BigDecimal unit = new BigDecimal(1);
        long days = 0, hours = 0, minutes = 0, rentalTimeInMinutes = 0;
        if(reqDetail.getChargingBeginTime() != null) {
            beginLDT = reqDetail.getChargingBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            rentalTimeInMinutes = ChronoUnit.MINUTES.between(beginLDT, endLDT);
            days = rentalTimeInMinutes / (24 * 60);
            hours = (rentalTimeInMinutes / 60) % 24;
            minutes = rentalTimeInMinutes % 60;
            totalTime = days + "天" + hours + "时" + minutes + "分";
        }

        switch(reqDetail.getChargingType()) {
            case "1":
                totalTime = "1次";
                unit = new BigDecimal(1);
                break;
            case "2":
                unit = new BigDecimal(rentalTimeInMinutes / 30d);
                unit = unit.setScale(0, RoundingMode.UP);
                break;
            case "3":
                unit = new BigDecimal(rentalTimeInMinutes / (60 * 24d));
                unit = unit.setScale(0, RoundingMode.UP);
                totalTime = unit.longValue() + "天";
                break;
            case "4":
                unit = new BigDecimal(rentalTimeInMinutes / 60d);
                unit = unit.setScale(2, RoundingMode.HALF_UP);
                break;
            default:
                unit = new BigDecimal(0);
        }
        result = new Tuple2<>(unit, totalTime);
        return result;
    }

    public static BigDecimal getTotalAmount(BigDecimal unit, SharedAssetReqDetail reqDetail) {
        Double unitPrice = reqDetail.getUnitPrice();
        if(unitPrice == null) {
            unitPrice = 0d;
        }
        BigDecimal totalAmount = unit.multiply(new BigDecimal(unitPrice.doubleValue()));
        if("2".equals(reqDetail.getChargingType())) {
            totalAmount = unit.multiply(new BigDecimal(unitPrice.longValue() / 2));
        }
        return totalAmount;
    }

    public static void fillData2Voucher(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        if(reqDetail.getReturnRequest() == null) {
            log.info("借时，设备处于在借状态");
            log.info("2.1 设备编号： " + asset.getDepartNum());
            log.info("2.2 设备名称： " + asset.getName());
            log.info("2.3 设备型号： " );
            log.info("2.4 日租金(单价)： " + asset.getSharedAssetType().getUnitPrice());
            log.info("2.5 借用状态： ");
            log.info("2.6 状态: ");
            log.info("2.7 附属物品: " + asset.getSharedAssetType().getAccessories());//json字符串，需额外处理
            log.info("2.8 借用时间: " + reqDetail.getChargingBeginTime());
            log.info("2.9 科室名称: " + reqDetail.getClinicalDept().getName());
            log.info("2.10 借用人: " + reqDetail.getCreateBy());
            log.info("2.11 经办人: " );
        } else {
            log.info("还时，设备处于归还状态");
            log.info("3.1 还时状态： ");
            log.info("3.2 总租用期: " + reqDetail.getRentalTime());
            log.info("3.3 归还时间: " + reqDetail.getChargingEndTime());
            log.info("3.4 总金额: " + reqDetail.getTotalAmount());
            log.info("3.5 归还人签字");
            log.info("3.6 经办人签字");
        }
    }
}

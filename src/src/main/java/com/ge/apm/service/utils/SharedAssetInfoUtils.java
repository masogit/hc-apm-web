package com.ge.apm.service.utils;

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

public class SharedAssetInfoUtils {

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
        return totalAmount;
    }
}

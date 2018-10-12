package com.ge.apm.service.utils;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.domain.SharedAssetTypeAccesssory;
import javaslang.Tuple3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocationCenterUtils {

    private static Logger logger = LoggerFactory.getLogger(AllocationCenterUtils.class.getName());

    public static Tuple3<Long, String, BigDecimal> getBillingDetail(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail) {
        Tuple3 billingDetail;

        BigDecimal totalAmount = BigDecimal.valueOf(0);
        String totalTime = "";

        Double unitPrice = reqDetail.getUnitPrice();
        if(unitPrice == null) {
            unitPrice = 0d;
        }

        LocalDateTime beginLDT;
        LocalDateTime endLDT;

        if(reqDetail.getChargingEndTime() != null) {
            endLDT = reqDetail.getChargingEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            endLDT = LocalDateTime.now();
        }

        BigDecimal unit = BigDecimal.valueOf(1);
        long days = 0, hours = 0, minutes = 0, rentalTimeInMinutes = 0, chargeTimeInMinutes = 0;
        if(reqDetail.getChargingBeginTime() != null) {
            beginLDT = reqDetail.getChargingBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            rentalTimeInMinutes = ChronoUnit.MINUTES.between(beginLDT, endLDT);//总使用分钟数
            if(rentalTimeInMinutes < 0) {
                rentalTimeInMinutes = 0;
            }
            days = rentalTimeInMinutes / (24 * 60);
            hours = (rentalTimeInMinutes / 60) % 24;
            minutes = rentalTimeInMinutes % 60;
            totalTime = days + "天" + hours + "时" + minutes + "分";
        }

        long startDurationInMinutes = 0l;
        double startPrice = 0d;

        if(reqDetail.getStartingDuration() != null && reqDetail.getStartingDuration() > 0) {
            startDurationInMinutes = (long)(reqDetail.getStartingDuration().doubleValue() * 60);
        }
        if(reqDetail.getStartingPrice() != null) {
            startPrice = reqDetail.getStartingPrice();
        }

        if(reqDetail.getStartingDuration() == null) {
            if(sharedAssetType.getStartingDuration() != null) {
                startDurationInMinutes = (long)(sharedAssetType.getStartingDuration().doubleValue() * 60);
            }
        }

        if(reqDetail.getStartingPrice() == null) {
            if(sharedAssetType.getStartingPrice() != null) {
                startPrice = sharedAssetType.getStartingPrice();
            }
        }

        chargeTimeInMinutes = rentalTimeInMinutes;
        switch(reqDetail.getChargingType()) {
            case "1":
                totalTime = "1次";
                unit = BigDecimal.valueOf(1);
                totalAmount = unit.multiply(BigDecimal.valueOf(unitPrice));
                break;
            case "2":
                //小时（半点）
                chargeTimeInMinutes = chargeTimeInMinutes - startDurationInMinutes;
                unit = BigDecimal.valueOf(chargeTimeInMinutes / 30d);
                unit = unit.setScale(0, RoundingMode.UP);
                if(chargeTimeInMinutes <= 0) {
                    totalAmount = BigDecimal.valueOf(startPrice);
                } else {
                    totalAmount = unit.divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP)
                                      .multiply(BigDecimal.valueOf(unitPrice))
                                      .add(BigDecimal.valueOf(startPrice));
                }
                break;
            case "3":
                //天
                unit = BigDecimal.valueOf(chargeTimeInMinutes / (60 * 24d));
                unit = unit.setScale(0, RoundingMode.UP);
                totalTime = unit.longValue() + "天";
                totalAmount = unit.multiply(BigDecimal.valueOf(unitPrice));
                break;
            case "4":
                //自然时间
                chargeTimeInMinutes = chargeTimeInMinutes - startDurationInMinutes;
                unit = BigDecimal.valueOf(chargeTimeInMinutes / 60d);
                unit = unit.setScale(4, RoundingMode.HALF_UP);
                if(chargeTimeInMinutes <= 0) {
                    totalAmount = BigDecimal.valueOf(startPrice);
                } else {
                    totalAmount = unit.multiply(BigDecimal.valueOf(unitPrice))
                                      .add(BigDecimal.valueOf(startPrice));
                }
                break;
            case "5":
                //小时（整点）
                chargeTimeInMinutes = chargeTimeInMinutes - startDurationInMinutes;
                unit = BigDecimal.valueOf(chargeTimeInMinutes / 60d);
                unit = unit.setScale(0, RoundingMode.UP);
                if(chargeTimeInMinutes <= 0) {
                    totalAmount = BigDecimal.valueOf(startPrice);
                } else {
                    totalAmount = unit.multiply(BigDecimal.valueOf(unitPrice))
                                      .add(BigDecimal.valueOf(startPrice));
                }
                break;
            default:
                unit = BigDecimal.valueOf(0);
                totalAmount = BigDecimal.valueOf(0);
        }

        if(logger.isInfoEnabled()) {
            logger.info("reqDetailId: {}, rentalTimeInMinutes: {}, totalAmount: {}, startDurationInMinutes: {}, startPrice: {}", reqDetail.getId(), rentalTimeInMinutes, totalAmount, startDurationInMinutes, startPrice);
        }

        billingDetail = new Tuple3(rentalTimeInMinutes, totalTime, totalAmount);
        return billingDetail;
    }

    public static String getPdfUnitPrice(SharedAssetReqDetail reqDetail)
    {
        Double price = reqDetail.getUnitPrice();
        String chgType = reqDetail.getChargingType();
        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");

        String uniPrice = null;
        //1.按次， 2.按小时(半点)/自然时间（60分钟）/按小时（整点）, 3.按天
        if(chgType.equals("1"))
        {
            uniPrice = df.format(price) + "元/次";
        }
        if(chgType.equals("2") || chgType.equals("4") || chgType.equals("5"))
        {
            BigDecimal pr = (BigDecimal.valueOf(price.doubleValue()).multiply(BigDecimal.valueOf(24)));
            uniPrice = df.format(pr) + "元/天";
        }
        if(chgType.equals("3"))
        {
            uniPrice = df.format(price) + "元/天";
        }

        return uniPrice;
    }

    public static String getPdfStatus(AssetInfo asset)
    {
        //1:正常,2:停机,3:限制功能使用,4:待报废,5:停用
        Integer status = asset.getStatus();
        String assetStatus = null;
        if(status == 1 || status == 3)
        {
            assetStatus = "已借出";
        }
        if(status == 2)
        {
            assetStatus = "停机";
        }
        if(status == 4)
        {
            assetStatus = "待报废";
        }
        if(status == 5)
        {
            assetStatus = "停用";
        }

        return assetStatus;
    }

    public static String getPdfAccessories(SharedAssetReqDetail sharedAssetReqDetail)
    {
        List<SharedAssetTypeAccesssory> accList = sharedAssetReqDetail.getAccessoryList();
        String accessories = "";

        if(accList == null)
        {
            accessories = "";
        }
        else
        {
            if(accList.size() < 1)
            {
                accessories = "";
            }

            if(accList.size() == 1)
            {
                accessories = accList.get(0).getName();
            }

            if(accList.size() > 1)
            {
                for(int i = 0; i < accList.size() - 1; i++)
                {
                    accessories += accList.get(i).getName() + '+';
                }

                accessories += accList.get(accList.size()-1 ).getName();
            }
        }
        return accessories;
    }

    public static String getPdfDatetime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Long getPdfRentTime(Date startDate, Date endDate) {
        LocalDateTime startLDT = (startDate == null) ? LocalDateTime.now() : startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endLDT = (endDate == null) ? LocalDateTime.now() : endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long rentalTimeInMinutes = ChronoUnit.MINUTES.between(startLDT, endLDT);
        return rentalTimeInMinutes;
    }
}

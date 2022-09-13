package com.ge.apm.service.sharedasset.valuation;

import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.service.utils.ChargingType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class UnattendedFee {
    // 优惠时长
    protected long  setDiscountTime(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail){
        long promotionTimeCount = 0L;
        String discountTimeStr="0";
        String chargingType = reqDetail.getChargingType();
        if(StringUtils.isBlank(chargingType) || ChargingType.PAY_PER.getIndex().equals(chargingType) || ChargingType.PAY_DAY.getIndex().equals(chargingType)){
            reqDetail.setDiscountTime(discountTimeStr);
            return 0L;
        }
        LocalDateTime sharedAssetTypeBeginTime;
        LocalDateTime sharedAssetTypeEndTime;
        LocalDateTime beginTime;
        LocalDateTime endTime;
        if((sharedAssetTypeBeginTime=getTime(reqDetail.getDiscountStartDate(),false))==null){
            reqDetail.setDiscountTime(discountTimeStr);
            return promotionTimeCount;
        }
        if((sharedAssetTypeEndTime=getTime(reqDetail.getDiscountEndDate(),false))==null){
            reqDetail.setDiscountTime(discountTimeStr);
            return promotionTimeCount;
        }
        if((beginTime=getTime(reqDetail.getChargingBeginTime(),false)) == null){
            reqDetail.setDiscountTime(discountTimeStr);
            return promotionTimeCount;
        }

        endTime=getTime(reqDetail.getChargingEndTime(),true);
        promotionTimeCount = computePromotionTime(beginTime, endTime, sharedAssetTypeBeginTime, sharedAssetTypeEndTime);
        reqDetail.setDiscountTime( getDiscountTimeStr(promotionTimeCount));
        return promotionTimeCount;
    }
    // 优惠费用
    protected void  setDiscountFee(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail,long discountTimeInt){
        BigDecimal discountFee=BigDecimal.ZERO;
        if(discountTimeInt == 0){
            reqDetail.setDiscountFee(BigDecimal.ZERO);
            return;
        }

        Double unitPrice = reqDetail.getUnitPrice();
        Double discount = reqDetail.getDiscount();

        if(unitPrice != null && discount != null){
            BigDecimal unitPriceBigDecimal = BigDecimal.valueOf(unitPrice);
            BigDecimal multiply = unitPriceBigDecimal.multiply(BigDecimal.valueOf(discountTimeInt / 60 )).multiply(BigDecimal.valueOf(discount / 100));
            discountFee=multiply;
        }
        reqDetail.setDiscountFee(discountFee);
    }
    //免除费用
    protected  void setGratisFee(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail,long discountTimeInt){

        Double gratisDuration = reqDetail.getGratisDuration();
        if(gratisDuration == null || gratisDuration<=0){
            reqDetail.setGratisFee(BigDecimal.ZERO);
            return;
        }
        BigDecimal gratisFee=null;
        Double unitPrice = reqDetail.getUnitPrice();
        if(unitPrice== null || unitPrice < 0){
            gratisFee=BigDecimal.ZERO;
        }else{
            BigDecimal unitPriceBigDecimal = BigDecimal.valueOf(unitPrice);
            BigDecimal multiply = unitPriceBigDecimal.multiply(BigDecimal.valueOf(gratisDuration));
            gratisFee=multiply;
        }
        reqDetail.setGratisFee(gratisFee);
    }

    private LocalDateTime  getTime(Date date,boolean isDefault){
        if(date == null && isDefault) {
            return LocalDateTime.now();
        }else if(date == null){
            return null;
        }else{
           return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }
    private String getDiscountTimeStr(long promotionTimeCount){
        StringBuilder formattedRentalTime = new StringBuilder();
        long days = 0L, hours = 0L, minutes = 0L;
        hours = (promotionTimeCount / 60);
        minutes = promotionTimeCount % 60;
        formattedRentalTime.append(hours).append("时").append(minutes).append("分");
        return formattedRentalTime.toString();
    }
      private long getDiscountTimeInt(LocalDateTime sharedAssetTypeBeginTime,LocalDateTime sharedAssetTypeEndTime){
          int hour = sharedAssetTypeBeginTime.getHour();
          int hour1 = sharedAssetTypeEndTime.getHour();
          if(hour >= hour1){
              sharedAssetTypeEndTime = sharedAssetTypeEndTime.plusDays(1);
          }
          return  ChronoUnit.MINUTES.between(sharedAssetTypeBeginTime, sharedAssetTypeEndTime);
      }
    protected  void setTotalAmount(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail){
        BigDecimal gratisFee = reqDetail.getGratisFee();
        BigDecimal discountFee = reqDetail.getDiscountFee();
        BigDecimal totalAmount = reqDetail.getRentalAmount();
        totalAmount = totalAmount.subtract(gratisFee).subtract(discountFee);
        reqDetail.setTotalAmount(totalAmount);
    }

    public  final  void valuation(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail){
        long discountTimeInt = setDiscountTime(sharedAssetType, reqDetail);
        setDiscountFee(sharedAssetType,reqDetail, discountTimeInt);
        setGratisFee(sharedAssetType,reqDetail,discountTimeInt);
        setTotalAmount(sharedAssetType, reqDetail);
    }

    public  long  computePromotionTime(LocalDateTime beginTime,LocalDateTime endTime,
                                              LocalDateTime sharedAssetTypeBeginTime,LocalDateTime sharedAssetTypeEndTime){
        LocalTime discountBeginTime = sharedAssetTypeBeginTime.toLocalTime();
        LocalTime discountEndTime = sharedAssetTypeEndTime.toLocalTime();
        int discountBeginTimeState = discountEndTime.getHour()-discountBeginTime.getHour();
        if(discountBeginTimeState < 1){
            sharedAssetTypeEndTime=sharedAssetTypeEndTime.plus(1,ChronoUnit.DAYS);
        }
        long OneDayMinute=60 * 24;
        long promotionTimeCount=0L;
        long countMinute=ChronoUnit.MINUTES.between(beginTime,endTime);
        long countDays = countMinute / OneDayMinute;
        long promotionTime=ChronoUnit.MINUTES.between(sharedAssetTypeBeginTime,sharedAssetTypeEndTime);
        promotionTimeCount+=countDays*promotionTime;  // 整数天数
        LocalDateTime lastDay = beginTime.plus(countDays, ChronoUnit.DAYS);

        if(discountBeginTimeState < 0){
            //优惠时间包含次日
            promotionTimeCount+= discountTimeIncludesNextDay(lastDay,endTime,discountBeginTime,discountEndTime);
            System.out.println(promotionTime);
        }else if (discountBeginTimeState > 0){
            //优惠时间不包含次日
            promotionTimeCount+= discountTimeDoesNotIncludeTheNextDay(lastDay,endTime,discountBeginTime,discountEndTime);
        }else{
            // 全天候优惠时间
            long between=ChronoUnit.MINUTES.between(lastDay,endTime);
            promotionTimeCount+=between;
        }
        return promotionTimeCount<=0?0:promotionTimeCount;
    }
    //优惠时间包含次日
    public   long  discountTimeIncludesNextDay( LocalDateTime lastDay,LocalDateTime endTime,
                                                      LocalTime discountBeginTime,LocalTime discountEndTime){

        int lastDayHour = lastDay.getHour();
        int endTimeHour = endTime.getHour();
        int lastDayDayOfYear = lastDay.getDayOfYear();
        int endDayOfYear = endTime.getDayOfYear();
        int state =endTimeHour - lastDayHour;
        int differenceDay=endDayOfYear-lastDayDayOfYear;
        long promotionTimeCount=0L;
        LocalTime DayStartTime = LocalTime.of(00, 00, 00);
        LocalTime DayEndTime = LocalTime.of(23, 59, 59);
        LocalTime lastDayLocalTime = lastDay.toLocalTime();
        LocalTime endTimeLocalTime = endTime.toLocalTime();


        if(state < 0 && differenceDay ==1){
            // 最后一天的开始时间在昨日，结束时间在今日
            if(lastDayLocalTime.compareTo(discountBeginTime) == 1 ){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,DayEndTime)+1;
            }else {
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,DayEndTime)+1;
            }
            if(endTimeLocalTime.compareTo(discountEndTime) == 1){
                promotionTimeCount+=ChronoUnit.MINUTES.between(DayStartTime,discountEndTime);
            }else {
                promotionTimeCount+=ChronoUnit.MINUTES.between(DayStartTime,endTimeLocalTime);
            }
            if(endTimeLocalTime.compareTo(discountBeginTime) > 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,endTimeLocalTime);
            }

        }else if(state == 0 && differenceDay ==1){
            LocalDateTime begin = LocalDateTime.of(lastDay.toLocalDate(), discountBeginTime);
            LocalDateTime end = LocalDateTime.of(endTime.toLocalDate(), discountEndTime);
            long countTime = ChronoUnit.MINUTES.between(begin, end);
            if((lastDayLocalTime.compareTo(discountBeginTime) > 0 && lastDayLocalTime.compareTo(DayEndTime) < 0) ||
                    (lastDayLocalTime.compareTo(DayStartTime) > 0 && lastDayLocalTime.compareTo(discountEndTime) < 0)
            ){
                long between1 = ChronoUnit.MINUTES.between(endTimeLocalTime,lastDayLocalTime);
                promotionTimeCount+=countTime-between1;
            }else{
                promotionTimeCount+=countTime;
            }

        }
        else if(state > 0 && differenceDay == 0){
            // 最后一天的开始时间在今日，结束时间在今日
            if(lastDayLocalTime.compareTo(discountBeginTime) <= 0 && endTimeLocalTime.compareTo(discountBeginTime) > 0 && endTimeLocalTime.compareTo(DayEndTime) <=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) > 0 && endTimeLocalTime.compareTo(discountBeginTime) > 0 && endTimeLocalTime.compareTo(DayEndTime) <=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }

            if(lastDayLocalTime.compareTo(DayStartTime) >= 0 && lastDayLocalTime.compareTo(discountEndTime) < 0 && endTimeLocalTime.compareTo(discountEndTime) <=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(DayStartTime) >= 0 && lastDayLocalTime.compareTo(discountEndTime) < 0 && endTimeLocalTime.compareTo(discountEndTime) >= 0 ){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,discountEndTime);
            }

        }else if(state == 0 && differenceDay == 0){
            // 最后一天的开始时间和结束时间在同一个小时
            if(lastDayLocalTime.compareTo(discountBeginTime) ==-1  && endTimeLocalTime.compareTo(discountBeginTime) ==1 && endTimeLocalTime.compareTo(DayEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) == 0 && endTimeLocalTime.compareTo(discountBeginTime) ==1 &&  endTimeLocalTime.compareTo(DayEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) == 1 &&  endTimeLocalTime.compareTo(discountBeginTime) ==1 &&  endTimeLocalTime.compareTo(DayEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }

            if(lastDayLocalTime.compareTo(DayStartTime) >=0 && endTimeLocalTime.compareTo(discountEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }
        }
        return promotionTimeCount;
    }
    //优惠时间不包含次日
    public   long  discountTimeDoesNotIncludeTheNextDay( LocalDateTime lastDay,LocalDateTime endTime,
                                                               LocalTime discountBeginTime,LocalTime discountEndTime){

        int lastDayHour = lastDay.getHour();
        int endTimeHour = endTime.getHour();
        int lastDayDayOfYear = lastDay.getDayOfYear();
        int endDayOfYear = endTime.getDayOfYear();
        int state =endTimeHour - lastDayHour;
        int differenceDay=endDayOfYear-lastDayDayOfYear;
        long promotionTimeCount=0L;
        LocalTime lastDayLocalTime = lastDay.toLocalTime();
        LocalTime endTimeLocalTime = endTime.toLocalTime();
        if(state <= 0 && differenceDay==1 ){
            if(lastDayLocalTime.compareTo(discountBeginTime) >= 0 && lastDayLocalTime.compareTo(discountEndTime) < 0 ){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,discountEndTime);
            }

            if(endTimeLocalTime.compareTo(discountBeginTime) > 0  && endTimeLocalTime.compareTo(discountEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,endTimeLocalTime);
            }else if(endTimeLocalTime.compareTo(discountBeginTime) > 0  && endTimeLocalTime.compareTo(discountEndTime) > 0) {
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,discountEndTime);
            }

        }else if(state > 0 && differenceDay==0){
            if(lastDayLocalTime.compareTo(discountBeginTime) <= 0 && endTimeLocalTime.compareTo(discountEndTime) >=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,discountEndTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) <= 0 && endTimeLocalTime.compareTo(discountEndTime) <=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(discountBeginTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) >=0 && lastDayLocalTime.compareTo(discountEndTime) < 0 && endTimeLocalTime.compareTo(discountEndTime) <=0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }else if(lastDayLocalTime.compareTo(discountBeginTime) >=0 && lastDayLocalTime.compareTo(discountEndTime) < 0 && endTimeLocalTime.compareTo(discountEndTime) >0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,discountEndTime);
            }
        }else if(state == 0 && differenceDay==0){
            if(lastDayLocalTime.compareTo(discountBeginTime) >= 0 && endTimeLocalTime.compareTo(discountBeginTime) > 0 && endTimeLocalTime.compareTo(discountEndTime) <= 0){
                promotionTimeCount+=ChronoUnit.MINUTES.between(lastDayLocalTime,endTimeLocalTime);
            }
        }
        return promotionTimeCount;
    }
}

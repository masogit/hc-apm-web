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
        long discountTimeInt = getDiscountTimeInt(sharedAssetTypeBeginTime, sharedAssetTypeEndTime);
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
        if(reqDetail.getRentalTimeInMins() < discountTimeInt){
            reqDetail.setDiscountFee(BigDecimal.ZERO);
            return;
        }
        Double unitPrice = reqDetail.getUnitPrice();
        Double discount = reqDetail.getDiscount();

        if(unitPrice != null && discount != null){
            BigDecimal unitPriceBigDecimal = BigDecimal.valueOf(unitPrice);
            BigDecimal multiply = unitPriceBigDecimal.multiply(BigDecimal.valueOf(discountTimeInt / 60 * (discount / 100)));
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
    private boolean isBefore(LocalDateTime beginTime,LocalDateTime sharedAssetTypeBeginTime){
        if(beginTime.getHour() <= sharedAssetTypeBeginTime.getHour() && beginTime.getMinute() <= sharedAssetTypeBeginTime.getMinute() && beginTime.getSecond() <= sharedAssetTypeBeginTime.getSecond()){
            return true;
        }
      return false;
    }

    private boolean isAfter(LocalDateTime endTime,LocalDateTime sharedAssetTypeEndTime){
        if(endTime.getHour() >= sharedAssetTypeEndTime.getHour() && endTime.getMinute() >= sharedAssetTypeEndTime.getMinute() && endTime.getSecond() >= sharedAssetTypeEndTime.getSecond()){
            return true;
        }
        return false;
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
        boolean flag=false;
        if(sharedAssetTypeBeginTime.getHour() >= sharedAssetTypeEndTime.getHour() ){
            sharedAssetTypeEndTime=sharedAssetTypeEndTime.plus(1,ChronoUnit.DAYS);
            flag=true;
        }
        LocalTime beginLocalTime = sharedAssetTypeBeginTime.toLocalTime();
        LocalTime endLocalTime = sharedAssetTypeEndTime.toLocalTime();
        long OneDayMinute=60 * 24;
        long promotionTimeCount=0L;
        long countMinute=ChronoUnit.MINUTES.between(beginTime,endTime);
        long countDays = countMinute / OneDayMinute;
        long promotionTime=ChronoUnit.MINUTES.between(sharedAssetTypeBeginTime,sharedAssetTypeEndTime);
        promotionTimeCount+=countDays*promotionTime;
        LocalDateTime lastDay = beginTime.plus(countDays, ChronoUnit.DAYS);
        LocalDateTime startPromotionTimeOfLastDay = LocalDateTime.of(lastDay.toLocalDate(), beginLocalTime);
        LocalDateTime endPromotionTimeOfLastDay;
        if(flag){
            endPromotionTimeOfLastDay=LocalDateTime.of(startPromotionTimeOfLastDay.plus(1,ChronoUnit.DAYS).toLocalDate(),endLocalTime);
        }else{
            endPromotionTimeOfLastDay=LocalDateTime.of(startPromotionTimeOfLastDay.toLocalDate(), endLocalTime);
        }
        LocalDateTime plus=startPromotionTimeOfLastDay;
        List<Integer> arryList=new ArrayList<>();
        do{
            arryList.add(plus.getHour());
        }
        while (( plus = plus.plus(1, ChronoUnit.HOURS)).compareTo(endPromotionTimeOfLastDay) !=1);
        long between = ChronoUnit.HOURS.between(lastDay, endTime);
        int i1 = lastDay.get(ChronoField.MINUTE_OF_HOUR);
        if(i1 != 0){
            promotionTimeCount-=i1;
        }
        for (int i = 0; i <=between ; i++) {
            LocalDateTime plus1 = lastDay.plus(i, ChronoUnit.HOURS);
            int hour = plus1.getHour();
            if(arryList.contains(hour) ){
                promotionTimeCount+=60;
            }
        }
        promotionTimeCount-=60;
        int hour = endTime.getHour();
        if(arryList.contains(hour) && hour != arryList.get(arryList.size() -1) ){
            LocalDateTime plus1 = lastDay.plus(between, ChronoUnit.HOURS);
            long between1 = ChronoUnit.MINUTES.between(plus1, endTime);
            promotionTimeCount+=between1;
        }
        return promotionTimeCount<=0?0:promotionTimeCount;
    }
}

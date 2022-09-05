package com.ge.apm.service.sharedasset.valuation;

import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.service.utils.ChargingType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public abstract class UnattendedFee {
    // 优惠时长
    protected long  setDiscountTime(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail){
        long rentalTimeInDays = 0L;
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
            return rentalTimeInDays;
        }
        if((sharedAssetTypeEndTime=getTime(reqDetail.getDiscountEndDate(),false))==null){
            reqDetail.setDiscountTime(discountTimeStr);
            return rentalTimeInDays;
        }
        long discountTimeInt = getDiscountTimeInt(sharedAssetTypeBeginTime, sharedAssetTypeEndTime);
        if((beginTime=getTime(reqDetail.getChargingBeginTime(),false)) == null){
            reqDetail.setDiscountTime(discountTimeStr);
            return rentalTimeInDays;
        }
        if(reqDetail.getRentalTimeInMins() <= discountTimeInt){
            reqDetail.setDiscountTime(discountTimeStr);
            return rentalTimeInDays;
        }
        endTime=getTime(reqDetail.getChargingEndTime(),true);
        rentalTimeInDays = ChronoUnit.DAYS.between(beginTime, endTime);


        if(rentalTimeInDays == 0 && isBefore(beginTime,sharedAssetTypeBeginTime) && isAfter(endTime,sharedAssetTypeEndTime)){
             discountTimeStr = getDiscountTimeStr(1, discountTimeInt, sharedAssetType);
        }else if(rentalTimeInDays > 0){
            if(isAfter(beginTime,sharedAssetTypeBeginTime)){
                rentalTimeInDays-=1;
            }
            if(rentalTimeInDays !=0 && isAfter(endTime,sharedAssetTypeEndTime)){
                rentalTimeInDays+=1;
            }
            discountTimeStr = getDiscountTimeStr(rentalTimeInDays, discountTimeInt, sharedAssetType);
        }
        reqDetail.setDiscountTime(discountTimeStr);
        return discountTimeInt*rentalTimeInDays;
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
            BigDecimal multiply = unitPriceBigDecimal.multiply(BigDecimal.valueOf(discountTimeInt / 60 * discount));
            discountFee=multiply;
        }
        reqDetail.setDiscountFee(discountFee);
    }
    //免除费用
    protected  void setGratisFee(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail,long discountTimeInt){

        Double gratisDuration = reqDetail.getGratisDuration();
        if(gratisDuration == null || gratisDuration==0){
            reqDetail.setGratisFee(BigDecimal.ZERO);
            return;
        }
        if(reqDetail.getRentalTimeInMins() <= gratisDuration*60){
            reqDetail.setGratisFee(BigDecimal.ZERO);
            return;
        }
        BigDecimal gratisFee=null;
        Double unitPrice = reqDetail.getUnitPrice();
        if(gratisDuration == null || unitPrice== null){
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
    private String getDiscountTimeStr(long rentalTimeInDays,long discountTimeInt,SharedAssetType sharedAssetType){
        discountTimeInt=rentalTimeInDays * discountTimeInt;
        StringBuilder formattedRentalTime = new StringBuilder();
        long days = 0L, hours = 0L, minutes = 0L;
        days = discountTimeInt / (24 * 60);
        hours = (discountTimeInt / 60) % 24;
        minutes = discountTimeInt % 60;
        formattedRentalTime.append(days).append("天").append(hours).append("时").append(minutes).append("分");
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
}

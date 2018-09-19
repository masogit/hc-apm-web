package com.ge.apm.service.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetTypeAccesssory;

import javaslang.Tuple2;

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
            if(rentalTimeInMinutes < 0) {
                rentalTimeInMinutes = 0;
            }
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
            totalAmount = unit.multiply(new BigDecimal(unitPrice)).divide(new BigDecimal(2));
        }
        return totalAmount;
    }

    public static void fillData2Voucher(AssetInfo asset, Map<String, Object> info) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        if(reqDetail.getReturnRequest() == null) {
            Date date = reqDetail.getChargingBeginTime();

            String year = getSplittedTime(date, "yyyy");
            String month = getSplittedTime(date, "MM");
            String day = getSplittedTime(date, "dd");
            String hour = getSplittedTime(date, "HH");
            String minute = getSplittedTime(date, "mm");
            String second = getSplittedTime(date, "ss");

            String rcpID = year.substring(2,4) + "-" + month + "-" + day + "/" + hour + ":"
            		+ minute + ":" + second;

            info.put("借据ID", rcpID);
            info.put("设备编号", asset.getDepartNum());
            info.put("设备名称", asset.getName());
            info.put("设备型号", asset.getFunctionType());
            info.put("设备分类", asset.getSharedAssetType() == null ? null : asset.getSharedAssetType().getSwapName());
            info.put("租金", getPdfUnitPrice(reqDetail));
            info.put("状态", getPdfStatus(asset));
            info.put("借用时间", year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
            info.put("附属物品", getPdfAccessories(asset));
            info.put("科室名称", reqDetail.getClinicalDept() == null ? null : reqDetail.getClinicalDept().getName());
        }
        else
        {
            LocalDateTime beginLDT;
            LocalDateTime endLDT;
            beginLDT = reqDetail.getChargingBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if(reqDetail.getChargingEndTime() != null) {
                endLDT = reqDetail.getChargingEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            else {
                endLDT = LocalDateTime.now();
            }

            long rentalTimeInMinutes = ChronoUnit.MINUTES.between(beginLDT, endLDT);

            DecimalFormat df = new DecimalFormat("0.00"); //保留两位小数
            String borrowDays = rentalTimeInMinutes < 360 ? "0.25" : df.format((double)rentalTimeInMinutes/(24*60));

            Double compenAmount = reqDetail.getCompensationAmount();
            Double rentFee = getPdfTotalAmount(reqDetail, rentalTimeInMinutes);
            if(compenAmount == null || compenAmount == 0)
            {
            	info.put("赔偿费用", "/");
            	info.put("总计费用", rentFee);
            }
            else
            {
            	info.put("赔偿费用", compenAmount);
            	info.put("总计费用", rentFee + compenAmount);
            }
            
            info.put("归还时间", reqDetail.getChargingEndTime() == null ? null :
                    getSplittedTime(reqDetail.getChargingEndTime(), "yyyy-MM-dd HH:mm:ss"));
            info.put("总租用期", rentalTimeInMinutes < 360 ? 0.25 : borrowDays);
            info.put("租赁费用", rentFee);
            info.put("备注", reqDetail.getReqComment());
            //is_intact为true或者null时默认为完好
            info.put("还时状态", (reqDetail.getIntact() == Boolean.FALSE ? "损坏" : "完好"));
        }
    }

    public static Double getPdfTotalAmount(SharedAssetReqDetail reqDetail, long minutes)
    {
    	Double totalAmount = 0.0;
        //chgType: 1.按次， 2.按小时/自然时间（60分钟）,3. 按天 , 4. 自然时间
        //request.type: 1.维修, 2.补登记 , 3. NULL的时候正常计费
        if("1".equals(reqDetail.getReturnRequest().getType()))
        {
            totalAmount = 0.0;
        }
        else
        {
            //按自然时间计费时，若小于6小时，按6小时计算保底价
            if(reqDetail.getChargingType().equals("4") && minutes < 6*60)
            {
                totalAmount = 6 * reqDetail.getUnitPrice();
            }
            else
            {
                totalAmount = reqDetail.getTotalAmount();
            }
        }

        return totalAmount;
    }

    public static Date addHourOfDate(Date date,int i)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, i);
        Date newDate = c.getTime();
        return newDate;
    }

    private static String getSplittedTime(Date date, String timeUnit)
    {
        SimpleDateFormat format = new SimpleDateFormat(timeUnit);
        return format.format(date);
    }

    private static String getPdfUnitPrice(SharedAssetReqDetail reqDetail)
    {
        Double price = reqDetail.getUnitPrice();
        String chgType = reqDetail.getChargingType();

        String uniPrice = null;
        //1.按次， 2.按小时/自然时间（60分钟）,3. 按天
        if(chgType.equals("1"))
        {
            uniPrice = price.toString() + "元/次";
        }
        if(chgType.equals("2") || chgType.equals("4"))
        {
            Double pr = price.doubleValue() * 24;
            uniPrice = pr.toString() + "元/天";
        }
        if(chgType.equals("3"))
        {
            uniPrice = price.toString() + "元/天";
        }

        return uniPrice;
    }

    private static String getPdfStatus(AssetInfo asset)
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

    private static String getPdfAccessories(AssetInfo asset)
    {
        List<SharedAssetTypeAccesssory> accList = asset.getSharedAssetType().getAccessoryList();
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
}

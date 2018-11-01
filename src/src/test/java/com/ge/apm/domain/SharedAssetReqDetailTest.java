package com.ge.apm.domain;

import com.ge.apm.service.utils.ChargingType;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Test case for SharedAssetReqDetail
 * @author nick
 */
public class SharedAssetReqDetailTest {

    private SharedAssetReqDetail sharedAssetReqDetail;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Before
    public void setUp() {
        sharedAssetReqDetail = new SharedAssetReqDetail();
        sharedAssetReqDetail.setUnitPrice(0.3d);
    }

    @Test
    public void testPayPer() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_PER.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        assertEquals(sharedAssetReqDetail.getUnitPrice().doubleValue(), sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        assertEquals("1次", sharedAssetReqDetail.getFormattedRentalTime());
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour1() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:40:00"));
        assertEquals(1.65d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour2() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:55:00"));
        assertEquals(1.8d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour3() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:40:00"));
        sharedAssetReqDetail.setStartingDuration(3d);
        sharedAssetReqDetail.setStartingPrice(20d);
        assertEquals(20.75d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour4() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:55:00"));
        sharedAssetReqDetail.setStartingDuration(3.0d);
        sharedAssetReqDetail.setStartingPrice(20d);
        assertEquals(20.9d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour5() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:40:00"));
        sharedAssetReqDetail.setStartingDuration(3.5d);
        sharedAssetReqDetail.setStartingPrice(20d);
        assertEquals(20.6d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    /**
     * Test pay half hour without starting configuration
     * @throws Exception
     */
    @Test
    public void testPayHalfHour6() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HALF_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 15:55:00"));
        sharedAssetReqDetail.setStartingDuration(3.5d);
        sharedAssetReqDetail.setStartingPrice(20d);
        assertEquals(20.75d, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    @Test
    public void testPayDay() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_DAY.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-19 05:55:00"));
        assertEquals(3.0, sharedAssetReqDetail.getRentalAmount().doubleValue(), 0.01d);
        assertEquals("10天", sharedAssetReqDetail.getFormattedRentalTime());
        System.out.println("Rental Time: " + sharedAssetReqDetail.getFormattedRentalTime());
    }

    @Test
    public void testPayMinutes() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_MINUTE.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 05:55:00"));
    }

    @Test
    public void testPayHour() throws Exception {
        sharedAssetReqDetail.setChargingType(ChargingType.PAY_HOUR.getIndex());
        sharedAssetReqDetail.setChargingBeginTime(sdf.parse("2018-10-09 10:20:00"));
        sharedAssetReqDetail.setChargingEndTime(sdf.parse("2018-10-09 05:55:00"));
    }

}

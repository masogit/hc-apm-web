package com.ge.apm.service.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    @Test
    public void testGetDateFormat() {

        String result = TimeUtils.getDateFormat("2011/11/20");

        assertEquals("yyyy/MM/dd", result);

        result = TimeUtils.getDateFormat("2011-11-20");

        assertEquals("yyyy-MM-dd", result);

        result = TimeUtils.getDateFormat("11/20/2017");

        assertEquals("MM/dd/yyyy", result);

        result = TimeUtils.getDateFormat("11-20-2017");

        assertEquals("MM-dd-yyyy", result);
    }

}
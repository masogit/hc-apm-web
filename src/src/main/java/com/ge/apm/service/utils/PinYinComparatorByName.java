package com.ge.apm.service.utils;

import java.util.Comparator;

public class PinYinComparatorByName implements Comparator<ChineseName> {

    @Override
    public int compare(ChineseName chineseName1, ChineseName chineseName2) {
        try {
        	String name1 = chineseName1.getPinYin() == null ? "" : chineseName1.getPinYin();
            String name2 = chineseName2.getPinYin() == null ? "" : chineseName2.getPinYin();
            return name1.compareTo(name2);

        }catch(Exception e) {
            return 0;
        }
    }

}

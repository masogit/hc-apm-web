package com.ge.apm.service.utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.Comparator;

public class PinYinComparator implements Comparator<String> {

    @Override
    public int compare(String org1, String org2) {
        try {
        	String name1 = PinyinHelper.convertToPinyinString(org1, "", PinyinFormat.WITHOUT_TONE);
            String name2 = PinyinHelper.convertToPinyinString(org2, "", PinyinFormat.WITHOUT_TONE);
            return name1.compareTo(name2);

        }catch(Exception e) {
            return 0;
        }
    }

}

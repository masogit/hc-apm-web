package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintainItem;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class StringComparator implements Comparator<String> {

    @Override
    public int compare(String arg0, String arg1) {
    	String pinyin0=PinyinUtil.getShortPinyin(arg0).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
    	String pinyin1=PinyinUtil.getShortPinyin(arg1).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
		return pinyin0.compareTo(pinyin1);
    }
}

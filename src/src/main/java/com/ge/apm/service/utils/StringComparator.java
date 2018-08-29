package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintainItem;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class StringComparator implements Comparator<String> {

    @Override
    public int compare(String arg0, String arg1) {
		return arg0.compareTo(arg1);
    }
}

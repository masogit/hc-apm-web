package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintainItem;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class NurseSiteMaintainItemComparator implements Comparator<NurseSiteMaintainItem> {

    @Override
    public int compare(NurseSiteMaintainItem org1, NurseSiteMaintainItem org2) {
        try {
        	return org1.getId().compareTo(org2.getId());
        }catch(Exception e) {
            return 0;
        }
    }

}

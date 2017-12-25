package com.ge.apm.service.utils;

import com.ge.apm.domain.OrgInfo;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.Comparator;

public class OrgInfoComparator implements Comparator<OrgInfo> {

    @Override
    public int compare(OrgInfo org1, OrgInfo org2) {
        try {
            if (org1.getOrgType().equals(org2.getOrgType())) {
                String name1 = PinyinHelper.convertToPinyinString(org1.getName(), "", PinyinFormat.WITHOUT_TONE);
                String name2 = PinyinHelper.convertToPinyinString(org2.getName(), "", PinyinFormat.WITHOUT_TONE);
                return name1.compareTo(name2);
            } else {
                return org1.getOrgType().compareTo(org2.getOrgType());

            }

        }catch(Exception e) {
            return 0;
        }
    }

}

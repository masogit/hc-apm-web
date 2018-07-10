package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintain;

public class NurseSiteMaintainComparator implements Comparator<NurseSiteMaintain> {

    @Override
    public int compare(NurseSiteMaintain org1, NurseSiteMaintain org2) {
        try {
        	return org1.getScheduleDate().getTime()>org2.getScheduleDate().getTime()?1:0;
        }catch(Exception e) {
            return 0;
        }
    }

}

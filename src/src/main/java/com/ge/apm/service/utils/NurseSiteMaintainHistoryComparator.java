package com.ge.apm.service.utils;

import com.ge.apm.domain.NurseSiteMaintainHistory;

import java.util.Comparator;

public class NurseSiteMaintainHistoryComparator implements Comparator<NurseSiteMaintainHistory> {

    @Override
    public int compare(NurseSiteMaintainHistory org1, NurseSiteMaintainHistory org2) {
        try {
        	int result=org1.getScheduleDate().compareTo(org2.getScheduleDate());
        	if(result==0){
        		return org1.getSequence().compareTo(org2.getSequence());
        	}
        	else{
        		return result;
        	}
        	
        }catch(Exception e) {
            return 0;
        }
    }
}

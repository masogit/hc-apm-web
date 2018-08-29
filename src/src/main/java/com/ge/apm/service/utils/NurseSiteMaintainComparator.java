package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintain;

public class NurseSiteMaintainComparator implements Comparator<NurseSiteMaintain> {

    @Override
    public int compare(NurseSiteMaintain org1, NurseSiteMaintain org2) {
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

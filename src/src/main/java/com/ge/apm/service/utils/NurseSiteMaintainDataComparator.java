package com.ge.apm.service.utils;

import java.util.Comparator;

import com.ge.apm.domain.NurseSiteMaintainData;

public class NurseSiteMaintainDataComparator implements Comparator<NurseSiteMaintainData> {

    @Override
    public int compare(NurseSiteMaintainData org1, NurseSiteMaintainData org2) {
        try {
        	return org1.getCheckItem().compareTo(org2.getCheckItem());
        }catch(Exception e) {
            return 0;
        }
    }

}

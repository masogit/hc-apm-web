package com.ge.apm.service.utils;

import java.util.Comparator;

import org.primefaces.model.ScheduleEvent;

import com.ge.apm.domain.NurseSiteMaintain;

public class ScheduleEventComparator implements Comparator<ScheduleEvent> {

    @Override
    public int compare(ScheduleEvent org1, ScheduleEvent org2) {
        try {
        	return ((NurseSiteMaintain)org1.getData()).getScheduleDate().getTime()>((NurseSiteMaintain)org2.getData()).getScheduleDate().getTime()?1:0;
        }catch(Exception e) {
            return 0;
        }
    }

}

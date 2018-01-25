package com.ge.apm.service.schedule;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserScheduleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by 212605082 on 2017/11/13.
 */
@Service
public class UserScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(UserScheduleService.class);

    @Autowired
    private UserScheduleRepository userScheduleDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;
    @Autowired
    private UserScheduleRequestApiService requestApiService;

    //休假
    private final String SCHEDULE_TYPE_VACATION = "V";
    //排班
    private final String SCHEDULE_TYPE_WORK = "W";

    private final String SCHEDULE_STAGE_ALL="ALL";
    private final String SCHEDULE_STAGE_AM="AM";
    private final String SCHEDULE_STAGE_PM="PM";


    public void saveWork(List<UserAccount> userAccountList, Date startDate, Date endDate, UserSchedule userSchedule, boolean isBatch){

        Instant startDateInstant = startDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDateInstant, zone);
        LocalDate startLocalDate = startLocalDateTime.toLocalDate();

        Instant endDateInstant = endDate.toInstant();
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endDateInstant, zone);
        LocalDate endLocalDate = endLocalDateTime.toLocalDate();

        long daysDiff = ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1;

        LocalDate scheduleDate;
        Instant scheduleDateInstant;
        for(int i = 0 ; i < daysDiff ; i++){

            scheduleDateInstant = startLocalDate.plus(i, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();

            if(isBatch){
                scheduleDate = startLocalDate.plus(i, ChronoUnit.DAYS).atStartOfDay().toLocalDate();
                if(scheduleDate.get(ChronoField.DAY_OF_WEEK) != DayOfWeek.SATURDAY.getValue() && scheduleDate.get(ChronoField.DAY_OF_WEEK) != DayOfWeek.SUNDAY.getValue()){
                    continue;
                }

            }

            for (UserAccount ua : userAccountList) {
                UserSchedule tempUserSchedule = userScheduleDao.findByTypeAndUserUIDAndScheduleDate(SCHEDULE_TYPE_WORK, ua.getUid(), Date.from(scheduleDateInstant));
                if(tempUserSchedule == null){
                    userSchedule.setId(null);
                    userSchedule.setScheduleDate(Date.from(scheduleDateInstant));
                    userSchedule.setUserUID(ua.getUid());
                    userScheduleDao.save(userSchedule);
                }
            }
        }
    }

    public List<LinkedHashMap<String, Date>> saveVacation(Date startDate, Date endDate){

        String token = UserContextService.getAccessToken();
        UserScheduleForm usForm = new UserScheduleForm();
        usForm.setStartDate(startDate);
        usForm.setEndDate(endDate);
        usForm.setScheduleDateStage(SCHEDULE_STAGE_ALL);
        usForm.setType(SCHEDULE_TYPE_VACATION);

        List<LinkedHashMap<String, Date>> resultList = requestApiService.createUserVacationRequest(token, usForm);

        return resultList;

    }

    public void deleteUserSchedule(UserSchedule us){
        userScheduleDao.delete(us);
    }

}

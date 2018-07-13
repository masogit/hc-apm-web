package com.ge.apm.service.schedule;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserScheduleRepository;
import com.ge.apm.dao.UserScheduleStageConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.domain.UserScheduleStageConfig;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
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
    @Autowired
    private UserScheduleStageConfigRepository userScheduleStageConfigDao;

    //休假
    public final static String SCHEDULE_TYPE_VACATION = "V";
    //排班
    public final static String SCHEDULE_TYPE_WORK = "W";

    public final static String SCHEDULE_STAGE_ALL="ALL"; //全天
    public final static String SCHEDULE_STAGE_AM="AM";   //白班
    public final static String SCHEDULE_STAGE_PM="PM";   //晚班

    public final static String BATCH_TYPE_WEEKDAY="WeekDay";   //工作日
    public final static String BATCH_TYPE_WEEKEND="WeekEnd";   //周末

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

    @Transactional
    public void batchSaveWorkSchedule(List<UserAccount> userAccountList, Date startDate, Date endDate, String batchType, UserAccount userAccount){
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
        for(int i = 0 ; i < daysDiff ; i++) {

            scheduleDateInstant = startLocalDate.plus(i, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();
            scheduleDate = startLocalDate.plus(i, ChronoUnit.DAYS).atStartOfDay().toLocalDate();

            boolean isWeekend = false;

            if(scheduleDate.get(ChronoField.DAY_OF_WEEK) == DayOfWeek.SATURDAY.getValue() || scheduleDate.get(ChronoField.DAY_OF_WEEK) == DayOfWeek.SUNDAY.getValue()){
                isWeekend = true;
            }

            if(BATCH_TYPE_WEEKDAY.equals(batchType)){
                if(isWeekend){
                    continue;
                }else {
                    userScheduleDao.deleteByTypeAndScheduleDateAndSiteUID(SCHEDULE_TYPE_WORK, Date.from(scheduleDateInstant), userAccount.getSiteUID());
                }
            }else if(BATCH_TYPE_WEEKEND.equals(batchType)){
                if(!isWeekend){
                    continue;
                }else {
                    userScheduleDao.deleteByTypeAndScheduleDateAndSiteUID(SCHEDULE_TYPE_WORK, Date.from(scheduleDateInstant), userAccount.getSiteUID());
                }
            }

            for (UserAccount ua : userAccountList) {
                UserSchedule tempUserSchedule = new UserSchedule();
                tempUserSchedule.setTenantUID(userAccount.getTenantUID());
                tempUserSchedule.setInstitutionUID(userAccount.getInstitutionUID());
                tempUserSchedule.setHospitalUID(userAccount.getHospitalUID());
                tempUserSchedule.setSiteUID(userAccount.getSiteUID());
                tempUserSchedule.setType("W");
                tempUserSchedule.setUserUID(ua.getUid());
                tempUserSchedule.setScheduleDate(Date.from(scheduleDateInstant));
                tempUserSchedule.setUserAccount(ua);

                if(BATCH_TYPE_WEEKDAY.equals(batchType)) {
                    tempUserSchedule.setScheduleDateStage(SCHEDULE_STAGE_AM);
                }else if(BATCH_TYPE_WEEKEND.equals(batchType)) {
                    tempUserSchedule.setScheduleDateStage(SCHEDULE_STAGE_ALL);
                }

                userScheduleDao.save(tempUserSchedule);

                if(BATCH_TYPE_WEEKDAY.equals(batchType)) {
                    UserSchedule tempUserSchedule1 = new UserSchedule();
                    tempUserSchedule1.setTenantUID(userAccount.getTenantUID());
                    tempUserSchedule1.setInstitutionUID(userAccount.getInstitutionUID());
                    tempUserSchedule1.setHospitalUID(userAccount.getHospitalUID());
                    tempUserSchedule1.setSiteUID(userAccount.getSiteUID());
                    tempUserSchedule1.setType("W");
                    tempUserSchedule1.setUserUID(ua.getUid());
                    tempUserSchedule1.setScheduleDate(Date.from(scheduleDateInstant));
                    tempUserSchedule1.setUserAccount(ua);
                    tempUserSchedule1.setScheduleDateStage(SCHEDULE_STAGE_PM);
                    userScheduleDao.save(tempUserSchedule1);
                }

            }
        }
    }

    public void saveScheduleStageConfig(Date startTime_all, Date endTime_all, Date startTime_am, Date endTime_am, Date startTime_pm, Date endTime_pm, UserAccount userAccount){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(int i = 0 ; i < 3 ; i++){
            UserScheduleStageConfig ussc = new UserScheduleStageConfig();
            ussc.setTenantUID(userAccount.getTenantUID());
            ussc.setInstitutionUID(userAccount.getInstitutionUID());
            ussc.setHospitalUID(userAccount.getHospitalUID());
            ussc.setSiteUID(userAccount.getSiteUID());
            if(i == 0){
                ussc.setScheduleDateStage(UserScheduleService.SCHEDULE_STAGE_ALL);
                List<UserScheduleStageConfig> usscList = userScheduleStageConfigDao.findByScheduleDateStageAndSiteUID(UserScheduleService.SCHEDULE_STAGE_ALL, userAccount.getSiteUID());
                if(!usscList.isEmpty()){
                    ussc = usscList.get(0);
                }
                ussc.setStartTime(sdf.format(startTime_all).split(" ")[1]);
                ussc.setEndTime(sdf.format(endTime_all).split(" ")[1]);
            }else if(i == 1){
                ussc.setScheduleDateStage(UserScheduleService.SCHEDULE_STAGE_AM);
                List<UserScheduleStageConfig> usscList = userScheduleStageConfigDao.findByScheduleDateStageAndSiteUID(UserScheduleService.SCHEDULE_STAGE_AM, userAccount.getSiteUID());
                if(!usscList.isEmpty()){
                    ussc = usscList.get(0);
                }
                ussc.setStartTime(sdf.format(startTime_am).split(" ")[1]);
                ussc.setEndTime(sdf.format(endTime_am).split(" ")[1]);
            }else if(i == 2){
                ussc.setScheduleDateStage(UserScheduleService.SCHEDULE_STAGE_PM);
                List<UserScheduleStageConfig> usscList = userScheduleStageConfigDao.findByScheduleDateStageAndSiteUID(UserScheduleService.SCHEDULE_STAGE_PM, userAccount.getSiteUID());
                if(!usscList.isEmpty()){
                    ussc = usscList.get(0);
                }
                ussc.setStartTime(sdf.format(startTime_pm).split(" ")[1]);
                ussc.setEndTime(sdf.format(endTime_pm).split(" ")[1]);
            }

            userScheduleStageConfigDao.save(ussc);
        }

    }

    public Map<String,UserScheduleStageConfig> loadScheduleStageConfig(UserAccount ua){
        Map<String,UserScheduleStageConfig> scheduleStageConfigMap = new HashedMap();
        scheduleStageConfigMap.put(SCHEDULE_STAGE_ALL, null);
        scheduleStageConfigMap.put(SCHEDULE_STAGE_AM, null);
        scheduleStageConfigMap.put(SCHEDULE_STAGE_PM, null);

        List<UserScheduleStageConfig> usscList = userScheduleStageConfigDao.findBySiteUID(ua.getSiteUID());

        for (UserScheduleStageConfig ussc : usscList) {
            if(SCHEDULE_STAGE_ALL.equals(ussc.getScheduleDateStage())){
                scheduleStageConfigMap.put(SCHEDULE_STAGE_ALL, ussc);
            }else if(SCHEDULE_STAGE_AM.equals(ussc.getScheduleDateStage())){
                scheduleStageConfigMap.put(SCHEDULE_STAGE_AM, ussc);
            }else if(SCHEDULE_STAGE_PM.equals(ussc.getScheduleDateStage())){
                scheduleStageConfigMap.put(SCHEDULE_STAGE_PM, ussc);
            }
        }

        return scheduleStageConfigMap;
    }

    public void deleteUserSchedule(UserSchedule us){
        userScheduleDao.delete(us);
    }

    public boolean isBeforeTagetDate(Date sourceDate, Date targetDate){
        Instant sourceDateInstant = sourceDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime sourceLocalDateTime = LocalDateTime.ofInstant(sourceDateInstant, zone);
        LocalDate sourceLocalDate = sourceLocalDateTime.toLocalDate();

        Instant targetDateInstant = targetDate.toInstant();
        LocalDateTime targetLocalDateTime = LocalDateTime.ofInstant(targetDateInstant, zone);
        LocalDate targetLocalDate = targetLocalDateTime.toLocalDate();

        return sourceLocalDate.isBefore(targetLocalDate);
    }

    public boolean isValidateWorkSchedule(Date scheduleDate, String scheduleDateStage, UserAccount userAccount){

        boolean flag = true;

        List<UserSchedule> userScheduleList = userScheduleDao.findByTypeAndScheduleDateAndSiteUID(SCHEDULE_TYPE_WORK, scheduleDate, userAccount.getSiteUID());

        int count_all = 0;
        int count_am = 0;
        int count_pm = 0;

        for (UserSchedule tempUserSchedule : userScheduleList) {

            if(SCHEDULE_STAGE_ALL.equals(tempUserSchedule.getScheduleDateStage())){
                count_all++;
            }else if(SCHEDULE_STAGE_AM.equals(tempUserSchedule.getScheduleDateStage())){
                count_am++;
            }else if(SCHEDULE_STAGE_PM.equals(tempUserSchedule.getScheduleDateStage())){
                count_pm++;
            }
        }

        if(count_all > 0){
            if(!SCHEDULE_STAGE_ALL.equals(scheduleDateStage)){
                flag = false;
            }
        }

        if(count_am > 0 || count_pm > 0){
            if(SCHEDULE_STAGE_ALL.equals(scheduleDateStage)){
                flag = false;
            }
        }

        return flag;
    }

    public boolean isValidateUser(Date scheduleDate, String userUID, String scheduleDateStage, String userScheduleID){

        List<UserSchedule> usList = userScheduleDao.findByTypeAndUserUIDAndScheduleDateAndScheduleDateStage(SCHEDULE_TYPE_WORK, userUID, scheduleDate, scheduleDateStage);

        if(!usList.isEmpty()){
            if(userScheduleID != null && userScheduleID.equals(usList.get(0).getId())){
                return true;
            }
            return false;
        }else {
            return true;
        }

    }

}

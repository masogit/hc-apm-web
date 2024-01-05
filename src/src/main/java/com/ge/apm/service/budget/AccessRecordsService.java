package com.ge.apm.service.budget;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


//@ManagedBean(name = "accessRecordsService")
//@SessionScoped
@Service
public class AccessRecordsService  implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(AccessRecordsService.class);

    static Map<String,HashMap<Integer,HashMap<String, HashMap<String,Integer>>>> map1=new HashMap<String,HashMap<Integer,HashMap<String, HashMap<String,Integer>>>>();

//    static HashMap<String, HashMap<String,Integer>> map=new HashMap<String, HashMap<String,Integer>>();


    public static void clear(String key){
        map1.remove(key);
    }


    @Async
    public void  accessRecords(String featureName, String businessType, UserAccount userAccount) {
        String dateString = DateUtil.getFormatStrByDate(new Date(), "yyyyMMdd");
        HashMap<Integer, HashMap<String, HashMap<String, Integer>>> dateMap = map1.get(dateString);

        if(map1.get(dateString)==null){
            map1.clear();
            dateMap=new HashMap<Integer, HashMap<String, HashMap<String, Integer>>>();
            map1.put(dateString,dateMap);
        }
        HashMap<String, HashMap<String, Integer>> userMap = dateMap.get(userAccount.getId());
        if(dateMap.get(userAccount.getId())==null){
            userMap =new  HashMap<String, HashMap<String, Integer>>();
            dateMap.put(userAccount.getId(),userMap);
        }


//        Integer integer = Optional.ofNullable(map.get(featureName)).orElse(new HashMap<String,Integer>()).get(businessType);

        Integer integer = Optional.ofNullable(userMap.get(featureName)).orElse(new HashMap<String,Integer>()).get(businessType);


        int count=0;
        if(integer==null){
            Optional.ofNullable(userMap.get(featureName)).map(m->m.put(businessType,1));
            if(userMap.get(featureName)==null){
                HashMap<String, Integer> m = new HashMap<>();
                m.put(businessType ,1);
                userMap.put(featureName,m);
            }
        }else{
            count=integer;
            HashMap<String, Integer> m = userMap.get(featureName);
            m.put(businessType,count+1);
            userMap.put(featureName ,m);

        }

        HashMap<String, Integer> hashMap = userMap.get(featureName);
        int finalCount = count;

        List<Integer> collect = hashMap.values().stream().filter(i -> i > finalCount).collect(Collectors.toList());
        if((!CollectionUtils.isEmpty(collect)  &&   collect.size()==1) || hashMap.size()==1 ){
//                UserAccount userAccount = UserContextService.getCurrentUserAccount();
                MqUtils mqUtils = WebUtil.getBean(MqUtils.class);
                mqUtils.sendUserAccessRecords(userAccount,businessType,featureName);
        }





    }
}

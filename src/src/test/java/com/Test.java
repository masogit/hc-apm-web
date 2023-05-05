package com;

import com.alibaba.fastjson.JSONObject;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.api.AssetsService;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.service.uaa.AdminConsoleService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;

import javax.xml.crypto.Data;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:config/springmvc-parent.xml")
public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @org.junit.Test
    public void to(){

        HashMap<Object, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","张三");
        map.put("password","123456");
        MqContext mqContext = new MqContext();
        mqContext.setTime(new Date());
        mqContext.setType("asset");
//        mqContext.setContext(map);
        mqContext.setRoutingKey("asset.delete");
        mqContext.setExChange("assetChange");
        mqUtils.sendToMq(mqContext);
    }

    @org.junit.Test
    public void toAsset(){
//        AssetInfo assetInfo = assetInfoRepository.findByUid("aaabaaaaappaaaabbbb");
        AssetInfo assetInfo = assetInfoRepository.findByUid("840c7b62bbf84fb984622347a8e5c5f6");
//        assetInfo.setId(null);
//        assetInfo.setUid("aaabooaapobbb");
//        assetInfo.setQrCode("jjjbobbbopkkj");
//        assetInfo.setSiteUID("sbbbbokopss");
//        assetInfoRepository.save(assetInfo);
//        assetInfo.setId(null);

        assetInfo.setDeviceStatus(22);
        assetInfo.setStatus(32);
        assetInfo.setAssetDeptUID("abdeea5a968a462cb464fff33c2c5d26");
        assetInfo.setClinicalDeptUID("5c5b52fd74054998a92bd410d54a3c55");
        assetInfo.setAssetDeptName("df翻云覆雨");
        assetInfo.setClinicalDeptName("他依然天涯");
//        AssetInfo oldAsset = dao.findByUid(selected.getUid());

        MqContext mqContext = new MqContext();
//        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(selected);

        mqContext.setTime(new Date());
        mqContext.setType("asset");
        mqContext.setRoutingKey("asset.update");
        mqContext.setExChange("assetChange");
        UserAccount currentUserAccount = new UserAccount();
        currentUserAccount.setName("ssss");
        currentUserAccount.setSiteUID("14");
        currentUserAccount.setSiteId(14);
        JSONObject jsonObject =new JSONObject();
        assetInfo.setHospital(null);
        assetInfo.setQrCodeLib(null);
        assetInfo.setAssetTags(null);
        assetInfo.setBrand(null);
        //gson序列化只与属性(字段)有关，与get开头的方法无关
        Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String str = g.toJson(assetInfo);
        jsonObject.put("assetInfo",str);
//        jsonObject.put("oldAssetInfo",oldAsset);
        jsonObject.put("currentUserAccount",currentUserAccount);
        mqContext.setJsonObject(jsonObject);
        mqUtils.sendToMq(mqContext);
//        assetHistoryService.saveAssetHistoryIfUpdated(oldAsset, selected);
    }


    @Autowired
    private AdminConsoleService adminConsoleService;

    @org.junit.Test
    public void AssetHostory(){
        adminConsoleService.batchUpdateAssetHistoryEvent();
    }


    @Autowired
    private ElasticService elasticService;
    @Autowired
    private AssetHistoryService assetHistoryService;
    @org.junit.Test
    public void loadData() {

        String s="clinicalDeptName";
        String s1 = s.toLowerCase();
        List<SearchFilter> searchFilters=null;

        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        String oneSearchStr="55";
        String[] b=new String[] {"06d1c46be51444fea9f6ace11efc74c6"};

        List<String> orgUidList = Arrays.asList(b);
        searchFilters.add(new SearchFilter("tenant_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("institution_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospital_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("site_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        CustomConfigObject  customConfig = customConfigService.getCurrentCustomConfig(userAccount.getSiteUID());
        String[] a=new String[] {"clinicalDeptName","qrCode","name","installDate","assetDeptName","departNum","financingNum","locationNotes"};
        List<String> assetsImportantFields=Arrays.asList(a);
        searchFilters.add(new SearchFilter("jsonData", SearchFilter.Operator.CONTAINS, assetsImportantFields,"1*"));
//        if(!StringUtils.isEmpty(oneSearchStr)) {
//            searchFilters.add(new SearchFilter("jsonData", SearchFilter.Operator.LIKE, oneSearchStr));
//        }
        String gte="2021-09-29 17:18:22";
        Date dateByFormatStr = DateUtil.getDateByFormatStr(gte, "yyyy-MM-dd HH:mm:ss");

        searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, dateByFormatStr,"1*"));

        //convert to json data page

        Page<AssetHistory> assetHistoryPage = elasticService.searchAssetHistory(searchFilters, "123", 0, 20);
        List<JSONObject> dataLists = assetHistoryService.parseFieldsByJson(assetHistoryPage.getContent());
        PageRequest pageRequest= new PageRequest(0, 20, null);

        Page<JSONObject> jsonPage = new PageImpl<>(dataLists, pageRequest, assetHistoryPage.getTotalElements());
        searchFilters = new ArrayList<SearchFilter>();
        logger.info("assetHistoryPage size:{}",dataLists.size());
        logger.info("assetHistoryPage :{}",dataLists.toArray());
        searchFilters = new ArrayList<SearchFilter>();
    }
}

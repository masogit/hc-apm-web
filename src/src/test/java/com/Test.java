package com;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.callback.MqContext;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.job.AssetHistoryJob;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:config/springmvc-parent.xml")
public class Test {
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

        AssetInfo assetInfo = assetInfoRepository.findByUid("d4714eda47fe4b9f9156304d7d9f3ff8");
//        assetInfo.setId(null);
//        assetInfo.setUid("hfhffjjfjfkfkfkfkfkfkfkf");
//        assetInfoRepository.save(assetInfo);
        assetInfo.setDeviceStatus(22);
        assetInfo.setStatus(32);
        assetInfo.setAssetDeptUID("abdeea5a968a462cb464fff33c2c5d26");
        assetInfo.setClinicalDeptUID("5c5b52fd74054998a92bd410d54a3c55");
        assetInfo.setAssetDeptName("再发士大夫地方");
        assetInfo.setClinicalDeptName("发射点发射点今天又");
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
        jsonObject.put("assetInfo",assetInfo);
//        jsonObject.put("oldAssetInfo",oldAsset);
        jsonObject.put("currentUserAccount",currentUserAccount);
        mqContext.setJsonObject(jsonObject);
        MqUtils.sendToMq(mqContext);
//        assetHistoryService.saveAssetHistoryIfUpdated(oldAsset, selected);
    }

    @Autowired
    private AssetHistoryJob assetHistoryJob;

    @org.junit.Test
    public void assetHistoryJobTest(){

        assetHistoryJob.execute();

    }

}

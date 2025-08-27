package com.ge.apm.aop.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("nurse_site_maintain_config_item")
public class NurseSiteMaintainConfigItemHandler implements AuditHandler {
    @Override
    public String getBoType() {
        return "nurse_site_maintain_config_item";
    }

    @Override
    public String generateEventData(Object[] args) {
        try {
            Object entity = args[0];
            if(entity instanceof List){
              List<NurseSiteMaintainConfigItem>  data= (List<NurseSiteMaintainConfigItem>)entity;
                List<Map<String, String>> collect = data.stream().map(item -> {
                    Map<String, String> eventData = new HashMap<>();
                    eventData.put("assetUid", item.getAssetUid());
                    eventData.put("configId", item.getParentId());
                    return eventData;
                }).collect(Collectors.toList());

                return JSON.toJSONString(collect);
            }else if(entity instanceof NurseSiteMaintainConfigItem){
                NurseSiteMaintainConfigItem configItem= (NurseSiteMaintainConfigItem) entity;
                Map<String, String> eventData = new HashMap<>();
                eventData.put("assetUid", configItem.getAssetUid());
                eventData.put("configId", configItem.getParentId());
                return JSON.toJSONString(eventData);
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

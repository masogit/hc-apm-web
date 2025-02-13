package com.ge.apm.service.LargeScreen;

import com.ge.apm.dao.LargeScreenConfigRepository;
import com.ge.apm.dao.LargeScreenItemRepository;
import com.ge.apm.domain.LargeScreenConfig;
import com.ge.apm.domain.LargeScreenItem;
import com.ge.apm.domain.UserAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Service
public class LargeScreenConfigService {
    @Autowired
    private LargeScreenConfigRepository largeScreenConfigRepository;

    @Autowired
    private LargeScreenItemRepository largeScreenItemRepository;
    @Transactional
    public LargeScreenConfig cloneLargeScreenConfig(LargeScreenConfig config, UserAccount userAccount){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String formatDate = format.format(date);
        LargeScreenConfig screenConfig = new LargeScreenConfig();
        BeanUtils.copyProperties(config,screenConfig,"id","usingDate");
        String name = config.getName();
        name=name.replaceAll("-\\d+","");
        screenConfig.setName(name+"-"+formatDate);
        screenConfig.setCreatedBy(userAccount.getUid());
        screenConfig.setCreatedDate(date);
        screenConfig.setStatus(0);
        screenConfig = largeScreenConfigRepository.save(screenConfig);
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("largeScreenConfigId", SearchFilter.Operator.EQ,config.getId()));
        List<LargeScreenItem> largeScreenItems = largeScreenItemRepository.findBySearchFilter(searchFilters);
        List<LargeScreenItem> result = new ArrayList<>();
        for (LargeScreenItem largeScreenItem : largeScreenItems) {
            LargeScreenItem item = makeItemObject(screenConfig, largeScreenItem,userAccount);
            result.add(item);
        }
        largeScreenItemRepository.save(result);
        return screenConfig;
    }
    public LargeScreenItem  makeItemObject(LargeScreenConfig largeScreenConfig,LargeScreenItem largeScreenItem,UserAccount userAccount){
        LargeScreenItem item = new LargeScreenItem();
        item.setLargeScreenConfigId(largeScreenConfig.getId());
        item.setCreatedBy(userAccount.getUid());
        item.setCreatedDate(new Date());
        item.setValue(largeScreenItem.getValue());
        item.setKey(largeScreenItem.getKey());
        return item;
    }
    @Transactional
    public void deleteLargeScreenConfig(LargeScreenConfig config, UserAccount currentUser) {
        Integer status = config.getStatus();
        if(Integer.valueOf(1).equals(status)){
            throw new RuntimeException("配置启用中无法删除，请停用配置后再试");
        }
        largeScreenItemRepository.deleteByLargeScreenConfigId(config.getId());
        largeScreenConfigRepository.delete(config.getId());
    }
}

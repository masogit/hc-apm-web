package com.ge.apm.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    public void evictSiteConfigByUids(Collection<String> siteUids) {
        if (siteUids == null || siteUids.isEmpty()) return;

        Cache cache = cacheManager.getCache("springCache");
        if (cache != null) {
            siteUids.forEach(uid ->
                    cache.evict("CustomConfigObject_SiteUid." + uid)
            );
        }
    }
}
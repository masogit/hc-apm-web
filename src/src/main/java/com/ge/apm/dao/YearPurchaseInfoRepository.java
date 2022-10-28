package com.ge.apm.dao;

import com.ge.apm.domain.YearPurchaseInfo;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface YearPurchaseInfoRepository extends GenericRepository<YearPurchaseInfo> {
    public YearPurchaseInfo findByUid(String uid);

    public List<YearPurchaseInfo> findByUidIn(List<String> assetUids, Sort sort);
}
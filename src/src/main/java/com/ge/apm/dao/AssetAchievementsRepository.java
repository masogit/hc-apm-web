package com.ge.apm.dao;

import com.ge.apm.domain.AssetAchievements;
import webapp.framework.dao.GenericRepository;

public interface AssetAchievementsRepository extends GenericRepository<AssetAchievements> {
    public AssetAchievements findByUid(String uid);
}

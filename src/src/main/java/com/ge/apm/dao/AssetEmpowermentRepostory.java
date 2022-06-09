package com.ge.apm.dao;

import com.ge.apm.domain.AssetEmpowerment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetEmpowermentRepostory extends JpaRepository<AssetEmpowerment,Integer> {

}

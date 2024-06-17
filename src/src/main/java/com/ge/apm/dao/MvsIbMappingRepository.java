package com.ge.apm.dao;
  
import com.ge.apm.domain.MvsIbMapping;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MvsIbMappingRepository extends JpaRepository<MvsIbMapping, String> {
}
package com.ge.apm.dao;

import com.ge.apm.domain.AttrValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttrValueRepository extends JpaRepository<AttrValue, String> {

    List<AttrValue> findByValue(String value);

}

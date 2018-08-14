package com.ge.apm.dao;

import com.ge.apm.domain.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, String> {

    List<Attribute> findByName(String name);

}

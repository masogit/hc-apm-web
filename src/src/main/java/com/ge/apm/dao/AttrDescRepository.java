package com.ge.apm.dao;

import com.ge.apm.domain.AttrDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttrDescRepository extends JpaRepository<AttrDesc, String> {

    List<AttrDesc> findByName(String name);

}

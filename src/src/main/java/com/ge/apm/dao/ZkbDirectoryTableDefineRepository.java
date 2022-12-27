/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;


import com.ge.apm.domain.ZkbDirectoryTableDefine;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @autor by lyj 503277717
 * @date 2022/12/19
*/

public interface ZkbDirectoryTableDefineRepository extends GenericRepositoryUUID<ZkbDirectoryTableDefine> {



    @Query(value = "select t.* from zkb_directory_tabledefine t    WHERE status = '0' ",nativeQuery = true)
    List<ZkbDirectoryTableDefine> queryAllDirectory();



}

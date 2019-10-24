/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ImportFiles;
import com.ge.apm.domain.ImportFilesVO;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 *
 * @author 212579464
 */
public interface ImportFilesRepository extends GenericRepositoryUUID<ImportFiles> {
    @Query("select new com.ge.apm.domain.ImportFilesVO(imf.id,imf.fileName,imf.filePath,imf.fileType,imf.uploadedTime,imf.processStartTime,imf.processEndTime,imf.status,imf.creatorUID) from ImportFiles imf order by imf.uploadedTime desc")
    List<ImportFilesVO> findAllImportFilesVO();
}

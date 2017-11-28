/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.ge.apm.service.utils.MicroServiceInvoker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author 212579464
 */

public class ReportTableApiService extends MicroServiceInvoker{
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Value("#{urlProperties.url_unstructureData}")
    private String url_unstructureData;
    
    
    
}

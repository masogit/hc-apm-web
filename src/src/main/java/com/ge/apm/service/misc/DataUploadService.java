package com.ge.apm.service.misc;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "ClinicalDataService", targetNamespace = "com.ge.dh.apm")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface DataUploadService {

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetClinicalRecord", action = "uploadAssetClinicalRecord")
    String uploadAssetClinicalRecord(ClinicalDataUploadRequest dataUploadRequest) throws Exception;

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetPerformanceRecord", action = "uploadAssetPerformanceRecord")
    String uploadAssetPerformanceRecord(AssetPerformanceUploadRequest request) throws Exception;

}

package com.ge.apm.edgeserver.dataupload.fromsite;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.ge.apm.edgeserver.dataupload.entity.AssetClinicalRecordDelta;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;

import javax.jws.WebParam;

@WebService(name="ClinicalDataService", targetNamespace="com.ge.dh.apm")
@SOAPBinding(style=SOAPBinding.Style.RPC,use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface DataUploadService {

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetClinicalRecord", action = "uploadAssetClinicalRecord")
    SoapResult uploadAssetClinicalRecord(@WebParam(name = "assetClinicalRecord") AssetClinicalRecordDelta assetClinicalRecordDelta);

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetPerformanceRecord", action = "uploadAssetPerformanceRecord")
    String uploadAssetPerformanceRecord(@WebParam(name="assetPerformanceRecord") AssetPerformanceRecordDelta asset) throws Exception;
    
}

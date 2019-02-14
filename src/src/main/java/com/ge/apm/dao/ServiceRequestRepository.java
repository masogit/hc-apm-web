package com.ge.apm.dao;

import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.pure.ServiceRequestProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ServiceRequestRepository extends GenericRepositoryUUID<V2_ServiceRequest> {

    V2_ServiceRequest findByIdAndHospitalId(String id, Integer hospitalId);

    public List<V2_ServiceRequest> findByAssetId(int assetId);

    public Page<V2_ServiceRequest> findByAssetIdAndHospitalIdAndStatusOrderByRequestTimeDesc(int assetId, int hospitalId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdAndHospitalIdOrderByRequestTimeDesc(int assetId, int hospitalId, Pageable pageable);

    public Page<V2_ServiceRequest> findByRequestorIdAndStatusOrderByRequestTimeDesc(Integer userId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByFromDeptIdAndStatusOrderByRequestTimeDesc(Integer orgId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdAndStatusOrderByRequestTimeDesc(int assetId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdOrderByRequestTimeDesc(int assetId, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetId(Integer assetId, Pageable pr);

    public List<V2_ServiceRequest> findByAssetIdAndStatus(int assetId, int srStatus);

    Page<V2_ServiceRequest> findById(String id, Pageable pageRequest);

    @Query(nativeQuery = true, value = "SELECT clinical_dept_name FROM asset_info WHERE id=?1")
    String getSrClinicalDeptName(Integer assetId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT clinical_dept_name FROM asset_info ai WHERE EXISTS (SELECT * FROM v2_service_request sr WHERE sr.asset_id=ai.id and sr.status=1) ORDER BY clinical_dept_name")
    List<String> getSrClinicalDeptNameList();

    @Query(nativeQuery = true, value = "SELECT id FROM asset_info WHERE clinical_dept_name=?1 and hospital_id=?2 and site_id=?3")
    List<Integer> getAssetInfoByClinicalDeptName(String clinicalDeptName, Integer hospitalId, Integer siteId);

    Page<ServiceRequestProjection> findProjectionByAssetId(Integer assetId, Pageable pr);
}

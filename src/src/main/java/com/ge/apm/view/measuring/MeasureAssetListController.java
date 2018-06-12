package com.ge.apm.view.measuring;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2018/6/4.
 */
@ManagedBean
@ViewScoped
public class MeasureAssetListController extends JpaCRUDController<AssetInfo> {

    private UserAccount currentUser;

    private AssetInfoRepository dao;
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;

    private List<MeasuringCertificateDetail> measuringCertificateList;

    private BlobFileService blobService;
    private BlobObjectRepository blobDao;

    private UaaService uaaService;

    private List<V2_BlobObject> attachements;

    private OrgInfo fileterOrg;

    @Override
    protected void init() {

        dao = WebUtil.getBean(AssetInfoRepository.class);
        measuringCertificateDetailDao = WebUtil.getBean(MeasuringCertificateDetailRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        attachements = new ArrayList<>();
    }

    @Override
    protected AssetInfoRepository getDAO(){
        return dao;
    }

    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if(!this.hasFilterOnField("measureGrade")){
            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
        }else{
            boolean hasNullMeasureGrade = false;
            Integer measureGradeValue = null;
            for (SearchFilter sf : this.searchFilters) {
                //判断是否有计量等级的过滤条件
                if(sf.fieldName.equals("measureGrade")){
                    if(sf.value == null){
                        hasNullMeasureGrade = true;
                    }else {
                        measureGradeValue = Integer.valueOf(sf.value.toString());
                    }
                }
            }

            //判断计量等级过滤条件是否为空
            if(hasNullMeasureGrade){
                this.removeFilterOnField("measureGrade");
                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
            }else {
                this.updateSearchFilter(new SearchFilter("measureGrade", SearchFilter.Operator.EQ, measureGradeValue, "1*"));
            }
        }

        if(null != fileterOrg){
            /*if(!this.hasFilterOnField("clinicalDeptUID")){
                searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
            }else{*/
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
            //}
        }else{
            if(this.hasFilterOnField("clinicalDeptUID")){
                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);

        /*Specification<AssetInfo> specification=new Specification<AssetInfo>() {
            @Override
            public Predicate toPredicate(Root<AssetInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate p1=cb.equal(root.get("tenantUID").as(String.class), currentUser.getTenantUID());
                Predicate p2=cb.notEqual(root.get("measureGrade").as(Integer.class), 4);
                Predicate p3=cb.isNull(root.get("measureGrade").as(Integer.class));

                Predicate p = cb.and(p1,cb.or(p2,p3));

                return p;
            }

        };

        return dao.findBySpecification(specification, pageRequest);*/
    }

    public void onSelectMeasureAsset(){
        measuringCertificateList = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
            blobObject.setBoUuid(selected.getUid());
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public List<V2_BlobObject> getAttachList() {
        List<V2_BlobObject> tempAttachList = new ArrayList<>();
        if(this.selected != null){
            tempAttachList = blobDao.findByBoUuidAndBoSubType(this.selected.getUid(), V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
        }

        return tempAttachList;
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {

        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (deptOrg.getOrgType() == 4) {
                fileterOrg = deptOrg;
                return;
            }
        }
        fileterOrg = null;
    }

    public List<MeasuringCertificateDetail> getMeasuringCertificateList() {
        return measuringCertificateList;
    }

    public void setMeasuringCertificateList(List<MeasuringCertificateDetail> measuringCertificateList) {
        this.measuringCertificateList = measuringCertificateList;
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }

    public OrgInfo getFileterOrg() {
        return fileterOrg;
    }

    public void setFileterOrg(OrgInfo fileterOrg) {
        this.fileterOrg = fileterOrg;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }
}

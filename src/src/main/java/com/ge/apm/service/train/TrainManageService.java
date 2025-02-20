package com.ge.apm.service.train;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.domain.dto.TrainRoleDto;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainManageService extends MicroServiceInvoker {


    @Autowired
    private TrainInfoRepository trainInfoRepository;

    @Autowired
    private TrainDetailRepository trainDetailRepository;

    @Autowired
    private BlobObjectRepository blobObjectRepository;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private TrainDetailSignRepository trainDetailSignRepository;

    public List<OrgDto> onCreateInventory(){

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(createHttpHeaders(UserContextService.getAccessToken()));
        String url = getGatewayURL(GatewayURL.BEFORE_CREATE_INVENTORY_ORDER);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET,requestEntity,LinkedHashMap.class);
        return JSONObject.parseObject(JSONObject.toJSONString(response.getBody().get("data")), new TypeReference<List<OrgDto>>() {});
    }

    public List<TrainRoleDto> queryTrainRole(UserAccount userAccount){
        List<TrainRoleDto> trainRoleDtoList = new ArrayList<>();
        List<CustomRole> customRoles = customRoleRepository.findByTenantUID(userAccount.getTenantUID());
        customRoles.stream()
                .filter(customRole -> customRole.getFunctionList() != null)
                .filter(customRole -> customRole.getFunctionList().contains("TRAIN_MANAGE_VIEW"))
                .forEach(customRole -> {
                    TrainRoleDto trainRoleDto = new TrainRoleDto();
                    trainRoleDto.setId(customRole.getId().toString());
                    trainRoleDto.setRoleName(customRole.getName());
                    trainRoleDtoList.add(trainRoleDto);
                });

        return trainRoleDtoList;
    }

    public boolean saveCheck(TrainInfo trainInfo, List<TrainDetail> trainDetailList){
        boolean rs = true;
        if(CollectionUtils.isEmpty(trainDetailList)){
            //WebUtil.addErrorMessage("培训课程不能为空！");
        }

        return rs;
    }

    /**
     * 保存方法
     * @param trainInfo
    b * @param userAccount
     */
    @Transactional
    public void save(TrainInfo trainInfo, UserAccount userAccount, List<String> selectedOrgUid, List<String> selectedRoleId){
        if(StrUtil.isEmpty(trainInfo.getId())){
            trainInfo.setCreatedDate(new Date());
            trainInfo.setCreatedUid(userAccount.getUid());
            trainInfo.setCreatedBy(userAccount.getName());
            trainInfo.setCourseNo(generateCourseNo());
            trainInfo.setTenantUID(userAccount.getTenantUID());
            trainInfo.setInstitutionUID(userAccount.getInstitutionUID());
            trainInfo.setHospitalUID(userAccount.getHospitalUID());
            trainInfo.setSiteUID(userAccount.getSiteUID());
            trainInfo.setStatus(TrainDetail.Status.Planned.name());
        }
        if(CollectionUtils.isNotEmpty(selectedOrgUid)){
            trainInfo.setTrainingDeptUid(selectedOrgUid.stream().collect(Collectors.joining(",")));
            trainInfo.setTrainingDeptCount(selectedOrgUid.size());
        }
        if(CollectionUtils.isNotEmpty(selectedRoleId)){
            trainInfo.setTrainingRoleUid(selectedRoleId.stream().collect(Collectors.joining(",")));
        }
        trainInfo.setLastModifiedDate(new Date());
        trainInfo.setLastModifiedBy(userAccount.getName());
        trainInfoRepository.save(trainInfo);
        /*for(TrainDetail trainDetail : trainDetailList){
            if(StrUtil.isEmpty(trainDetail.getTrainId())){
                trainDetail.setTrainId(trainInfo.getId());
                trainDetail.setId(null);
                trainDetailRepository.save(trainDetail);
                for(V2_BlobObject blobObject : trainDetail.getBlobObjectList()){
                    blobObject.setBoUuid(trainDetail.getId());
                }
                blobObjectRepository.save(trainDetail.getBlobObjectList());
            }else{
                if(!TrainDetail.DataStatus.Original.name().equals(trainDetail.getDataStatus())){
                    //如果是新增的数据，把临时的id去除
                    if(TrainDetail.DataStatus.Create.name().equals(trainDetail.getDataStatus())){
                        trainDetail.setId(null);
                    }
                    trainDetail.setTrainId(trainInfo.getId());
                    trainDetailRepository.save(trainDetail);
                    for(V2_BlobObject blobObject : trainDetail.getBlobObjectList()){
                        if(StrUtil.isEmpty(blobObject.getBoUuid())){
                            blobObject.setBoUuid(trainDetail.getId());
                            blobObjectRepository.save(blobObject);
                        }
                    }
                }
            }
        }*/
    }

    @Transactional
    public List<TrainDetail> saveDetail(TrainDetail trainDetail,String detailActionName, List<V2_BlobObject> blobObjects, TrainInfo trainInfo){
        if("Create".equals(detailActionName)){
            trainDetail.setCreatedDate(new Date());
        }else{
            trainDetail.setLastModifiedDate(new Date());
        }
        trainDetailRepository.save(trainDetail);
        for(V2_BlobObject blobObject : blobObjects){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObjects);
            }
        }

        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        if("Create".equals(detailActionName)){
            trainInfo.setStatus(TrainDetail.Status.Planned.name());//已计划
            trainInfo.setTrainQty(trainDetailList.size());
            trainInfoRepository.save(trainInfo);
        }
        return trainDetailList;
    }

    public void completeDetail(TrainDetail trainDetail, List<V2_BlobObject> blobObjects, TrainInfo trainInfo, UserAccount userAccount){
        trainDetail.setStatus(TrainDetail.Status.Completed.name());
        trainDetail.setLastModifiedDate(new Date());
        trainDetailRepository.save(trainDetail);
        for(V2_BlobObject blobObject : blobObjects){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObjects);
            }
        }
        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        boolean allComplete = true;
        for(TrainDetail td : trainDetailList){
            if (!TrainDetail.Status.Completed.name().equals(td.getStatus())) {
                allComplete = false;
            }
        }
        if(allComplete){
            trainInfo.setStatus(TrainDetail.Status.Completed.name());
            trainInfo.setLastModifiedDate(new Date());
            trainInfo.setLastModifiedBy(userAccount.getName());
            trainInfoRepository.save(trainInfo);
        }
    }

    @Transactional
    public void cancelDetail(TrainDetail trainDetail, TrainInfo trainInfo, UserAccount userAccount){
        trainDetail.setStatus(TrainDetail.Status.Canceled.name());
        trainDetail.setLastModifiedDate(new Date());
        trainDetailRepository.save(trainDetail);

        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        boolean allCancel = true;
        for(TrainDetail td : trainDetailList){
            if (!TrainDetail.Status.Canceled.name().equals(td.getStatus())) {
                allCancel = false;
            }
        }
        if(allCancel){
            trainInfo.setStatus(TrainDetail.Status.Canceled.name());
            trainInfo.setLastModifiedDate(new Date());
            trainInfo.setLastModifiedBy(userAccount.getName());
            trainInfoRepository.save(trainInfo);
        }
    }


    public String generateCourseNo(){
        StringBuilder sb = new StringBuilder();
        String pre = DateUtil.format(new Date(), "yyyyMMdd");
        sb.append(pre);
        Object object = trainInfoRepository.queryMaxCourseNo(pre);
        if(object == null){
            sb.append("001");
        }else{
            String current = object.toString().replace(pre, "");
            if(StrUtil.isNotEmpty(current)){
                int currentIndex = Integer.parseInt(current);
                currentIndex++;
                sb.append(String.format("%03d",currentIndex));
            }else{
                sb.append("001");
            }
        }

        return sb.toString();
    }

    public List<V2_BlobObject> queryBlobByDetailId(String detailId){
        return blobObjectRepository.findByBoUuid(detailId);
    }

    public void removeBlob(V2_BlobObject v2BlobObject){
        blobFileService.deleteBlobFile(v2BlobObject);
    }

    public V2_BlobObject handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobFileService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_TRAIN_DETAIL);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_TRAIN_DETAIL);
            v2_blobObject.setBoUuid(null);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
        return v2_blobObject;
    }

    public List<TrainDetail> queryDetailByTrainId(String trainId){
        return trainDetailRepository.findByTrainId(trainId);
    }

    public void saveSign(TrainDetailSign trainDetailSign){
        if(StrUtil.isEmpty(trainDetailSign.getId())){
            Object index = trainDetailSignRepository.queryMaxOrderByDetailId(trainDetailSign.getDetailId());
            if(index != null){
                int i = Integer.parseInt(index.toString());
                i++;
                trainDetailSign.setSignOrder(i);
            }else{
                trainDetailSign.setSignOrder(1);
            }
        }
        trainDetailSignRepository.save(trainDetailSign);
    }

    public List<TrainDetailSign> queryDetailSignByDetailId(String detailId){
        return trainDetailSignRepository.findByDetailId(detailId);
    }

}

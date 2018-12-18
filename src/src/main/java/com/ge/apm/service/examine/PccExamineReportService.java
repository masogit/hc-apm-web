package com.ge.apm.service.examine;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PccExamineReportRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Service
public class PccExamineReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportService.class);
    private static final String SHEET_Maintain = "Maintain List";
    @Autowired
    AssetInfoRepository assetInfoRepository;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    UserOrgInfoRepository userOrgInfoRepository;
    
    @Autowired
    PccExamineReportRepository pccExamineReportRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private UaaService uaaService;
    private static List<ApmDropDownItem> lineStatus;
    private static List<ApmDropDownItem> mirrorStatus;
    private static List<ApmDropDownItem> shellStatus;
    private static List<ApmDropDownItem> linkerStatus;
    private static List<ApmDropDownItem> oilStatus;
    private static List<ApmDropDownItem> tipStatus;
    private static List<ApmDropDownItem> electricLeakageStatus;
    private static List<ApmDropDownItem> firstSeenStatus;
    private static List<ApmDropDownItem> firstTipStatus;
    private static List<ApmDropDownItem> resultStatus;
    private static List<ApmDropDownItem> recommandStatus;

    
    
    public List<PccExamineReport> fetchPccExamineReportBySite(String siteUid){
        return pccExamineReportRepository.fetchPccExamineReportBySiteUid(siteUid);
    }
    @Transactional
    public V2_BlobObject uploadFile(FileUploadEvent event) {
        return blobFileService.uploadFile(event.getFile());
    }
    
    public boolean saveFile(List<ApmStreamedContent> list,String boUuid){
        boolean result=false;
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();++i){
                result=saveFile(list.get(i),boUuid);
                if(!result){
                    return result;
                }
            }
        }
        return result;
    }
    @Transactional
    public boolean savePccReport(PccExamineReport report,List<ApmStreamedContent> listContent){
        pccExamineReportRepository.save(report);
        String id=report.getId();
        if(StringUtils.isNotEmpty(id)){
            saveFile(listContent, id);
        }
        return true;
    }
    
    public List<ApmStreamedContent> parseBlobObject(String boUuid){
        List<V2_BlobObject> listObj=getFile(boUuid);
        List<ApmStreamedContent> listResult=new ArrayList<ApmStreamedContent>();
        if(listObj!=null&& listObj.size()>0){
            for(int i=0;i<listObj.size();++i){
                V2_BlobObject obj=listObj.get(i);
                String storageId=obj.getObjectStorageId();
                if(StringUtils.isNotEmpty(storageId)){
                    ApmStreamedContent tempContent=new ApmStreamedContent(blobFileService.getBlobObjectByStorageId(storageId).getStream(), obj.getObjectType(), obj.getObjectName());
                    listResult.add(tempContent);
                }
            }
        }
        return listResult;
    }
    
    @Transactional
    public boolean saveFile(ApmStreamedContent content,String boUuid){
        if(content!=null){
            V2_BlobObject blobObj=blobFileService.uploadFile(content.getStream(), content.getName());
            blobObj.setBoType(V2_BlobObject.BO_TYPE_PCCReport);
            blobObj.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObj.setBoUuid(boUuid);
            blobFileService.saveBlobObject(blobObj);
        }
        return true;
    }
    
    public List<V2_BlobObject> getFile(String boUuid){
        return blobFileService.findBlobObjectsByBoUid(boUuid);
    }
    
    public Boolean deleteFile(List<V2_BlobObject> objs){
        Boolean result=true;
        if(objs!=null && objs.size()>0){
            for(int i=0;i<objs.size();++i){
                result=deleteFile(objs.get(i));
                if(result==null ||result==false){
                    return result;
                }
            }
        }
        return result;
    }
    @Transactional
    public Boolean deleteFile(V2_BlobObject obj){
        return blobFileService.deleteBlobFile(obj);
    }
    
    public static List<ApmDropDownItem> fetchLineStatus(){
        if(lineStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"破损（老化）\"},{\"key\":\"3\",\"label\":\"断裂\"}]";
            lineStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return lineStatus;
    }
    public static List<ApmDropDownItem> fetchMirrorStatus(){
        if(mirrorStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"起泡\"},{\"key\":\"3\",\"label\":\"断裂\"}]";
            mirrorStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return mirrorStatus;
    }
    public static List<ApmDropDownItem> fetchShellStatus(){
        if(shellStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"开裂\"}]";
            shellStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return shellStatus;
    }
    public static List<ApmDropDownItem> fetchLinkerStatus(){
        if(linkerStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"锁扣脱落\"},{\"key\":\"3\",\"label\":\"外壳破损\"},{\"key\":\"4\",\"label\":\"针脚损坏\"}]";
            linkerStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return linkerStatus;
    }
    public static List<ApmDropDownItem> fetchOilStatus(){
        if(oilStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"无\"},{\"key\":\"2\",\"label\":\"良好\"},{\"key\":\"3\",\"label\":\"漏油\"},{\"key\":\"4\",\"label\":\"破损\"}]";
            oilStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return oilStatus;
    }
    public static List<ApmDropDownItem> fetchTipStatus(){
        if(tipStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"外观良好,通过测试\"},{\"key\":\"2\",\"label\":\"外观存在问题,未通过测试\"}]";
            tipStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return tipStatus;
    }
    public static List<ApmDropDownItem> fetchElectricLeakageStatus(){
        if(electricLeakageStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"未测试\"},{\"key\":\"2\",\"label\":\"通过\"},{\"key\":\"3\",\"label\":\"超标\"}]";
            electricLeakageStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return electricLeakageStatus;
    }
    public static List<ApmDropDownItem> fetchFirstSeenStatus(){
        if(firstSeenStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"因探头外观存在风险，未执行检测\"},{\"key\":\"2\",\"label\":\"通道灵敏度测试值在正常范围内，无信号缺失通道\"},{\"key\":\"3\",\"label\":\"部分通道有短路\"},{\"key\":\"4\",\"label\":\"通道灵敏度缺失/衰弱\"}]";
            firstSeenStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return firstSeenStatus;
    }
    public static List<ApmDropDownItem> fetchFirstTipStatus(){
        if(firstTipStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"未执行FIRSTCALL检测，建议测试其他环节综合判断\"},{\"key\":\"2\",\"label\":\"测试值良好，无功能故障\"},{\"key\":\"3\",\"label\":\"探头功能故障,未通过测试\"}]";
            firstTipStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return firstTipStatus;
    }
    public static List<ApmDropDownItem> fetchResultStatus(){
        if(resultStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"探头状态良好\"},{\"key\":\"2\",\"label\":\"图像有干扰\"},{\"key\":\"3\",\"label\":\"图像远场衰减\"},{\"key\":\"4\",\"label\":\"图像缺波\"},{\"key\":\"5\",\"label\":\"探头角度不能旋转\"},{\"key\":\"6\",\"label\":\"马达不能转\"},{\"key\":\"7\",\"label\":\"无法校准\"},{\"key\":\"8\",\"label\":\"温度报警\"},{\"key\":\"9\",\"label\":\"探头无法识别\"},{\"key\":\"10\",\"label\":\"连接探头报错/导致重启\"},{\"key\":\"11\",\"label\":\"通道短路\"},{\"key\":\"12\",\"label\":\"通道灵敏度减弱\"},{\"key\":\"13\",\"label\":\"漏电流超标\"},{\"key\":\"14\",\"label\":\"漏油\"},{\"key\":\"15\",\"label\":\"油囊破损\"},{\"key\":\"16\",\"label\":\"线缆破损\"},{\"key\":\"17\",\"label\":\"线缆断裂\"},{\"key\":\"18\",\"label\":\"透镜起泡\"},{\"key\":\"19\",\"label\":\"透镜断裂\"},{\"key\":\"20\",\"label\":\"外壳开裂\"},{\"key\":\"21\",\"label\":\"护套脱落或破损\"},{\"key\":\"22\",\"label\":\"连接器锁扣脱落\"},{\"key\":\"23\",\"label\":\"连接器外壳破损\"},{\"key\":\"24\",\"label\":\"连接针脚损坏\"}]";
            resultStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return resultStatus;
    }
    public static List<ApmDropDownItem> fetchRecommandStatus(){
        if(recommandStatus==null){
            String str="[{\"key\":\"1\",\"label\":\"建议定期检测\"},{\"key\":\"2\",\"label\":\"建议更换\"}]";
            recommandStatus=(List<ApmDropDownItem>)JSONObject.parseArray(str, ApmDropDownItem.class);
        }
        return recommandStatus;
    }

}

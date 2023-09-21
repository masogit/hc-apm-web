
package com.ge.apm.view.his;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisEquipmentSfxmMatchRepository;
import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.domain.his.HisEquipmentSfxmMatch;
import com.ge.apm.service.his.load.HisEquipmentImportService;
import com.ge.apm.service.his.load.HisEquipmentSfxmImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.asset.ContractImportController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisChargeCodeMatchController extends GenericCRUDUUIDController<HisAssetGroupAssetMatch> implements ServerEventInterface {
    private OrgInfoRepository orgInfoDao;
    private HisEquipmentSfxmRepository sfxDao;
    private HisEquipmentSfxmMatchRepository  sfxmMatchDao;
    private HisAssetGroupAssetMatchRepository groupAssetMatchRepository;
    private HisOrgComponentController orgComponentController;
    @Getter@Setter
    private DualListModel<HisEquipmentSfxm> availableAssets;
    @Getter@Setter
    private List<HisEquipmentSfxm> sourceAssets;
    @Getter@Setter
    private List<HisEquipmentSfxm> targetAssets;
    @Getter@Setter
    private String name; //设备名称
    @Getter@Setter
    private String departNum;//设备编号
    @Getter@Setter
    private String sfxmId;  //过滤His收费代码
    @Getter@Setter
    private String sfxmName;//过滤His收费名称
    @Getter@Setter
    private Boolean subEquipment = false;//过滤子设备
    private List<HisAssetGroupAssetMatch> currentPagePmOrders;

    private HisEquipmentSfxmImportService hisEquipmentSfxmImportService;

    //初始化定义pageRequest,提供给待匹配的页面使用
    @Getter@Setter
    PageRequest pageRequest;

    @Getter@Setter
    List<HisAssetGroupAssetMatch> startList = new ArrayList<>();
    //复选的设备
    @Getter@Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();

    List<String> pmIdList = new ArrayList<>(); //选中的

    public void prepareCreateSart() {
        crudActionName = "Create";
    }

    @Override
    protected HisAssetGroupAssetMatchRepository getDAO() {
        return groupAssetMatchRepository;
    }

    public void prepareEditSart() {
        crudActionName = "Edit";
    }

    @Override
    protected void init() {
        sfxmMatchDao = WebUtil.getBean(HisEquipmentSfxmMatchRepository.class);
        groupAssetMatchRepository = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        sfxDao = WebUtil.getBean(HisEquipmentSfxmRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        hisEquipmentSfxmImportService = WebUtil.getBean(HisEquipmentSfxmImportService.class);
        orgComponentController.registerController(this);
        pageRequest =  new PageRequest(0 ,200);
        initAssets();//初始化 availableAssets
    }

    @Override
    protected Page<HisAssetGroupAssetMatch> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<SearchFilter>();

        //  sfxmId、 sfxmName 取值不到
        if (StringUtils.isNotBlank(sfxmId)) {//所属科室
            searchFilters.add(new SearchFilter("clinicalDeptName", SearchFilter.Operator.EQ, sfxmId.trim()));
        }
        if (StringUtils.isNotBlank(sfxmName)) {// 设备分类名称
            searchFilters.add(new SearchFilter("assetGroupName", SearchFilter.Operator.LIKE, sfxmName.trim()));
        }
        if (Boolean.TRUE.equals(subEquipment)) {// 是否子设备
            searchFilters.add(new SearchFilter("isSubEquipment", SearchFilter.Operator.EQ, Boolean.TRUE));
        }
        if(orgComponentController.getSelectedOrg() != null){
            List<String> siteOrgUidsByParentUid = orgInfoDao.getSiteOrgUidsByParentUid(orgComponentController.getSelectedOrg().getUid());
            if(!siteOrgUidsByParentUid.isEmpty()){
                searchFilters.add(new SearchFilter("orgInfoUid", SearchFilter.Operator.IN, siteOrgUidsByParentUid));
            }
        }

        Page<HisAssetGroupAssetMatch> page = groupAssetMatchRepository.findBySearchFilter(searchFilters,pageRequest);
        currentPagePmOrders = page.getContent();
        return page;
    }

    @Override
    public List<HisAssetGroupAssetMatch> getItemList() {
        return groupAssetMatchRepository.find();
    }

    public void onDeleteSelectedGroup(String id) {
        if(selectedMap !=null && selectedMap.size() >0 ) {
            selectedMap.remove(id);
            startList = new ArrayList<>();
            pmIdList = new ArrayList<>();
            selectedMap.forEach((k, v) -> {
                if (Boolean.TRUE.equals(v))
                    pmIdList.add(k);
            });
            if(!pmIdList.isEmpty()) {
                List<HisAssetGroupAssetMatch> results = groupAssetMatchRepository.findByIds(pmIdList);
                startList.addAll(results);
            }
        }
    }

    public void initAssets() {
        sourceAssets = new ArrayList<>();
        targetAssets = new ArrayList<>();
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public void firstQueryOrder() {
        searchFilters = new ArrayList<SearchFilter>();
        if (StringUtils.isNotBlank(name)) {
            searchFilters.add(new SearchFilter("sfxmName", SearchFilter.Operator.LIKE, name.trim()));
        }
        if (StringUtils.isNotBlank(departNum)) {
            searchFilters.add(new SearchFilter("sfxmId", SearchFilter.Operator.LIKE, departNum.trim()));
        }

        if (startList !=null && !startList.isEmpty()) {
            Set<String> clinicalDeptUids = startList.stream().map(HisAssetGroupAssetMatch::getClinicalDeptUid).collect(Collectors.toSet());
            List<String> sfxmIdList = sfxmMatchDao.findByClinicalDeptUids(clinicalDeptUids);
            searchFilters.add(new SearchFilter("sfxmId", SearchFilter.Operator.NotIN, sfxmIdList));
        }

        if(orgComponentController.getSelectedOrg() != null){
            searchFilters.add(new SearchFilter("hosId", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
        }

        List<HisEquipmentSfxm> bySearchFilter = sfxDao.findBySearchFilter(this.searchFilters,pageRequest).getContent();
        if(!targetAssets.isEmpty()){
            Set<String> set = targetAssets.stream().map(HisEquipmentSfxm::getSfxmId).collect(Collectors.toSet());
            bySearchFilter = bySearchFilter.stream().filter(item -> StringUtils.isNotEmpty(item.getSfxmId()) && !set.contains(item.getSfxmId())).collect(Collectors.toList());
        }
        availableAssets = new DualListModel<>(bySearchFilter, targetAssets);
    }

    //提交匹配保存数据
    @Transactional
    public void submmitOrder() {
        if(startList == null || startList.isEmpty()){
            WebUtil.addErrorMessageKey("未添加设备分组");
            return;
        }

        if(targetAssets.isEmpty()){
            WebUtil.addErrorMessageKey("未添加收费项目！");
            return;
        }
        Map<String,List<HisAssetGroupAssetMatch>> map = startList.stream().collect(Collectors.groupingBy(HisAssetGroupAssetMatch::getAssetGroupName));
        if(map.size()>1){
            Double rate = startList.stream().mapToDouble(HisAssetGroupAssetMatch::getGroupRate).sum();
            if(rate != 100.0){
                WebUtil.addErrorMessageKey("分摊必须是100%！");
                return;
            }
        }else{
            List<HisAssetGroupAssetMatch> collect = startList.stream().filter(item -> item.getGroupRate() != 100.0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(collect)){
                WebUtil.addErrorMessageKey("多设备分组必须都是100%！");
                return;
            }
        }

        Set<String> sfxmIdList = targetAssets.stream().filter(item -> StringUtils.isNotEmpty(item.getSfxmId())).map(HisEquipmentSfxm::getSfxmId).collect(Collectors.toSet());
        if(sfxmIdList.isEmpty()){
            WebUtil.addErrorMessageKey("收费项目的收费代码不存在！");
            return;
        }

        //targetAssets去重
        targetAssets =  targetAssets.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(HisEquipmentSfxm::getSfxmId))), ArrayList::new)
        );
        Set<String> groupClinicalDeptUids = startList.stream().filter(item -> StringUtils.isNotEmpty(item.getClinicalDeptUid())).map(HisAssetGroupAssetMatch::getClinicalDeptUid).collect(Collectors.toSet());
        List<HisEquipmentSfxmMatch> bySfxmIdsAndClinicalDeptUids = sfxmMatchDao.findBySfxmIdsAndClinicalDeptUids(sfxmIdList, groupClinicalDeptUids);
        if(!bySfxmIdsAndClinicalDeptUids.isEmpty()){
            WebUtil.addErrorMessageKey("存在收费项目已与科室进行过匹配！");
            return;
        }

        List<String> sfxmClinicalDeptUidList = sfxmMatchDao.findBySfxmIds(sfxmIdList);
        if(!sfxmClinicalDeptUidList.isEmpty()){
            Set<String> set = Sets.newHashSet();
            set.addAll(groupClinicalDeptUids);
            set.retainAll(sfxmClinicalDeptUidList);
            if(!set.isEmpty()){
                WebUtil.addErrorMessageKey("存在收费项目已匹配科室！");
                return;
            }
        }
        UserAccount user = UserContextService.getCurrentUserAccount();
        for (HisEquipmentSfxm sfxm : targetAssets) {
            HisEquipmentSfxm temp = sfxDao.findById(sfxm.getId());
            if(temp == null){
                continue;
            }
            List<HisEquipmentSfxmMatch> matches = new ArrayList<>();
            Date date = new Date();
            for(HisAssetGroupAssetMatch group : startList){
                HisEquipmentSfxmMatch hisEquipmentSfxmMatch = new HisEquipmentSfxmMatch();
                hisEquipmentSfxmMatch.setHisEquipmentSfxmId(sfxm.getId());
                hisEquipmentSfxmMatch.setSfxmId(sfxm.getSfxmId());
                hisEquipmentSfxmMatch.setSfxmName(sfxm.getSfxmName());
                hisEquipmentSfxmMatch.setSfxmLb(sfxm.getSfxmLb());
                hisEquipmentSfxmMatch.setHosId(sfxm.getHosId());
                hisEquipmentSfxmMatch.setHisGroupMatchUid(group.getUuid());
                hisEquipmentSfxmMatch.setAssetGroupUid(group.getAssetGroupUid());
                hisEquipmentSfxmMatch.setClinicalDeptUid(group.getClinicalDeptUid());// 所属科室
                hisEquipmentSfxmMatch.setClinicalDeptName(group.getClinicalDeptName());
                hisEquipmentSfxmMatch.setAssetGroupName(group.getAssetGroupName());
                hisEquipmentSfxmMatch.setGroupRate(group.getGroupRate());
                hisEquipmentSfxmMatch.setCreateDate(date);
                hisEquipmentSfxmMatch.setCreateBy(user.getName());
                hisEquipmentSfxmMatch.setClinicalDeptName(group.getClinicalDeptName());
                matches.add(hisEquipmentSfxmMatch);
            }
            List<String> ids = Lists.newArrayList();
            ids.add(sfxm.getId());
            sfxmMatchDao.save(matches);
            sfxDao.updateIsMatched(true,sfxm.getId());
        }
        targetAssets = Lists.newArrayList();
        startList = Lists.newArrayList();
        pmIdList = Lists.newArrayList();
        selectedMap = new HashMap<>();
        firstQueryOrder();
        WebUtil.addErrorMessageKey("匹配成功");
    }

    public void groupTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetAssets.add((HisEquipmentSfxm)item);
            } else {
                targetAssets.remove(item);
            }
        }
    }

    public void pick(){
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (Boolean.TRUE.equals(v))
                pmIdList.add(k);
        });
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getUuid()) || !selectedMap.get(item.getUuid()));
        for (HisAssetGroupAssetMatch item : currentPagePmOrders) {
            selectedMap.put(item.getUuid(), !isSelectedAll);
        }
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (Boolean.TRUE.equals(v))
                pmIdList.add(k);

        });
    }

    //初始化
    public List<HisAssetGroupAssetMatch> getSartList() {
        return startList;
    }

    //选中确认
    public void confirm(){
        if(CollectionUtils.isNotEmpty(pmIdList)) {
            startList = new ArrayList<>();
            List<HisAssetGroupAssetMatch> results = groupAssetMatchRepository.findByIds(pmIdList);
            startList.addAll(results);
        }
        firstQueryOrder();
    }

    public void cancel() {
        this.selected = null;
    }

    public void refreshAfterOrgChange() {
        targetAssets = Lists.newArrayList();
        startList = Lists.newArrayList();
        pmIdList = Lists.newArrayList();
        selectedMap = new HashMap<>();
        firstQueryOrder();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        refreshAfterOrgChange();
    }




    private List<Map<String, Object>> importMapList;
    private List<Map<String, Object>> errorList;
    private UploadedFile file;
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            //检查导入模板版本
//            if(!checkDocVersion(doc)){
//                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--"+WebUtil.getMessage("TemplateVersionIncorrect"));
//                return;
//            }

            int maxRowNum = HisEquipmentSfxmImportService.EXCEL_CONTRACT_HEADER_ROW + 30000;
            if(doc.exceedMaxRowNum(HisEquipmentSfxmImportService.SHEET_SERVICE_CONTRACT, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            importMapList = hisEquipmentSfxmImportService.getMapFromTemplate(doc);
            hisEquipmentSfxmImportService.importData(importMapList);
            errorList = hisEquipmentSfxmImportService.filterErrorRecords(importMapList);
            if(!errorList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }


    @Getter @Setter
    private List<HisAssetGroupAssetMatch> assetGroupByGroupName=new ArrayList<>();

    public void hisGroupByName() {
        assetGroupByGroupName.clear();
        if(CollectionUtils.isNotEmpty(startList)){
            HisAssetGroupAssetMatch hisAssetGroupAssetMatch = startList.get(0);
            String assetGroupName = hisAssetGroupAssetMatch.getAssetGroupName();
            List<SearchFilter> searchFilters=new ArrayList<>();
            searchFilters.add(new SearchFilter("assetGroupName", SearchFilter.Operator.EQ,assetGroupName));
            List<HisAssetGroupAssetMatch> bySearchFilter = groupAssetMatchRepository.findBySearchFilter(searchFilters);
            if(CollectionUtils.isNotEmpty(bySearchFilter)){
                assetGroupByGroupName.addAll(bySearchFilter);

            }
        }
    }
}



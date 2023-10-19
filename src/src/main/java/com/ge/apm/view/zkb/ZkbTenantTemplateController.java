package com.ge.apm.view.zkb;

import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.ZkbDirectoryTableDefineRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by lyj 503277717
 * @date 2023/2/22.
 */
@ManagedBean
@ViewScoped
public class ZkbTenantTemplateController extends GenericCRUDUUIDController<TableDefinition> {

    private TableDefinitionRepository dao;

    private String queryTenantUID = "";

    private List<TenantInfo> tenantInfoList;

    private List<TableDefinition> tableDefinitionList;

    private ZkbPartOrderService zkbPartOrderService;

    private ZkbDirectoryTableDefineRepository zkbDirectoryTableDefineRepository;

    private List<String> tableDefineIds = new ArrayList<>();

    private List<String> tenantInfoUids = new ArrayList<>();

    private boolean allTenant = false;

    private String queryTenantUIDS = "";

    /**
     * 目录关系分页数据
     */
    private Page<TableDefinition> zkbTableDefinitionPage;

    @Override
    protected TableDefinitionRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(TableDefinitionRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        zkbDirectoryTableDefineRepository = WebUtil.getBean(ZkbDirectoryTableDefineRepository.class);
        tenantInfoList = zkbPartOrderService.getSourceTenantInfoList();
        tableDefinitionList = getTableDefinitionListByTenantUID();
    }

    public List<TableDefinition> getTableDefinitionListByTenantUID(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, StringUtils.isBlank(this.queryTenantUIDS)?"1=1":this.queryTenantUIDS));
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    protected Page<TableDefinition> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (CollectionUtils.isEmpty(searchFilters)) {
            searchFilters = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(this.queryTenantUID)) {
            searchFilters.add((new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.queryTenantUID)));
        }
        zkbTableDefinitionPage = dao.findBySearchFilter(searchFilters, pageRequest);
         handDirectory(zkbTableDefinitionPage.getContent());
        return zkbTableDefinitionPage;
    }

    public void handDirectory(List<TableDefinition> tableDefinitionList){
        if(CollectionUtils.isNotEmpty(tableDefinitionList)){
            List<ZkbDirectoryTableDefine> zkbDirectorys = zkbDirectoryTableDefineRepository.queryAllDirectory();
            Map<String, ZkbDirectoryTableDefineVo> stringZkbDirectoryTableDefineVoMap = new Hashtable<String,ZkbDirectoryTableDefineVo>();
            for(ZkbDirectoryTableDefine zkbDirectoryW:zkbDirectorys){
                if(stringZkbDirectoryTableDefineVoMap.containsKey(zkbDirectoryW.getName()))
                    continue;
                StringBuilder stringBuilderSecond = new StringBuilder();
                ZkbDirectoryTableDefineVo zkbDirectoryTableDefineVo = new ZkbDirectoryTableDefineVo();
                for(ZkbDirectoryTableDefine zkbDirectoryN:zkbDirectorys){
                    if(zkbDirectoryW.getName().equals(zkbDirectoryN.getName())){
                        stringBuilderSecond.append(zkbDirectoryN.getSecondDirectory()+",");
                    }
                }
                zkbDirectoryTableDefineVo.setMainDirectory(zkbDirectoryW.getMainDirectory());
                zkbDirectoryTableDefineVo.setSecondDirectory(stringBuilderSecond.toString().length()>0?(stringBuilderSecond.toString().substring(0,stringBuilderSecond.toString().length()-1)):"");
                stringZkbDirectoryTableDefineVoMap.put(zkbDirectoryW.getName(),zkbDirectoryTableDefineVo);
            }
            tableDefinitionList.stream().forEach(t ->{
                ZkbDirectoryTableDefineVo vo =  stringZkbDirectoryTableDefineVoMap.get(t.getName());
                if (vo!=null){
                    t.setMainDirectory(vo.getMainDirectory());
                    t.setSecondDirectory(vo.getSecondDirectory());
                }
            });
        }
    }

    public void importTemplate() {
        try {
            if(CollectionUtils.isEmpty(this.tableDefineIds)){
                WebUtil.addErrorMessage(WebUtil.getMessage("请勾选源模板！"));
            }
            if(CollectionUtils.isEmpty(this.tenantInfoUids)&&!this.allTenant){
                WebUtil.addErrorMessage(WebUtil.getMessage("请勾选目标租户或所有租户！！"));
            }
            for(String tableDefineId : tableDefineIds){
                TableDefinition tableDefinition = dao.findById(tableDefineId);
                if(this.allTenant){
                    for(TenantInfo tenantInfo : tenantInfoList){
                        List<TableDefinition> tableDefinitionList = dao.findAllByTenantUID(tenantInfo.getUid());
                        boolean existsOr = tableDefinitionList.stream().filter(tdf -> tdf.getName().equals(tableDefinition.getName())).findAny().isPresent();
                        if(!existsOr){
                            this.handTableDefinition(new TableDefinition(),tableDefinition,tenantInfo.getUid());
                        }
                    }
                }else{
                    for(String tenantInfoUid : tenantInfoUids){
                        List<TableDefinition> tableDefinitionList = dao.findAllByTenantUID(tenantInfoUid);
                        boolean existsOr = tableDefinitionList.stream().filter(tdf -> tdf.getName().equals(tableDefinition.getName())).findAny().isPresent();
                        if(!existsOr){
                            this.handTableDefinition(new TableDefinition(),tableDefinition,tenantInfoUid);
                        }
                    }
                }
            }
            WebUtil.addSuccessMessage(WebUtil.getMessage("ConfigurationSuccess"));

        } catch (Exception e) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("FAILED"));
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void handTableDefinition(TableDefinition tableDefinition1,TableDefinition td,String tenantInfoUid) throws Exception {
        tableDefinition1.setName(td.getName());
        dao.save(tableDefinition1);
        tableDefinition1.setBaseId(tableDefinition1.getId());
        tableDefinition1.setHospitalUID(tenantInfoUid);
        tableDefinition1.setInstitutionUID(tenantInfoUid);
        tableDefinition1.setSiteUID(tenantInfoUid);
        tableDefinition1.setTenantUID(tenantInfoUid);
        tableDefinition1.setAssetGroup(td.getAssetGroup());
        tableDefinition1.setDescription(td.getDescription());
        tableDefinition1.setSchema(td.getSchema());
        tableDefinition1.setStatus("Released");
        tableDefinition1.setTemplateType("Maintain");
        tableDefinition1.setCreatedBy(td.getCreatedBy());
        tableDefinition1.setCreatedDate(new Date());
        tableDefinition1.setLastModifiedBy(td.getCreatedBy());
        tableDefinition1.setLastModifiedDate(new Date());
        tableDefinition1.setExcelStorageId(td.getExcelStorageId());
        tableDefinition1.setType(td.getType());
        dao.save(tableDefinition1);
    }

    public void updateTemplate() {
        try {
            if(CollectionUtils.isEmpty(this.tableDefineIds)){
                WebUtil.addErrorMessage(WebUtil.getMessage("请勾选源模板！"));
            }
            if(CollectionUtils.isEmpty(this.tenantInfoUids)&&!this.allTenant){
                WebUtil.addErrorMessage(WebUtil.getMessage("请勾选目标租户或所有租户！！"));
            }
            for(String tableDefineId : tableDefineIds){
                TableDefinition tableDefinition = dao.findById(tableDefineId);
                if(this.allTenant) {
                    for(TenantInfo tenantInfo : tenantInfoList){
                        List<TableDefinition> tableDefinitionList =  dao.findAllByTenantUID(tenantInfo.getUid()).stream().filter(tdf -> tdf.getName().equals(tableDefinition.getName())).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(tableDefinitionList)){
                            this.handTableDefinition(tableDefinitionList.get(0),tableDefinition,tenantInfo.getUid());
                        }
                    }
                }else {
                    for(String tenantInfoUid : tenantInfoUids){
                        List<TableDefinition> tableDefinitionList =  dao.findAllByTenantUID(tenantInfoUid).stream().filter(tdf -> tdf.getName().equals(tableDefinition.getName())).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(tableDefinitionList)){
                            this.handTableDefinition(tableDefinitionList.get(0),tableDefinition,tenantInfoUid);
                        }
                    }
                }
            }
            WebUtil.addSuccessMessage(WebUtil.getMessage("ConfigurationSuccess"));
        } catch (Exception e) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("FAILED"));
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public String importAction() {
        return "templateOperate.xhtml";
    }

    public String backList() {
        return "templateList.xhtml";
    }
    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<TableDefinition> getTableDefinitionList() {
        return tableDefinitionList;
    }

    public void setTableDefinitionList(List<TableDefinition> tableDefinitionList) {
        this.tableDefinitionList = tableDefinitionList;
    }

    public List<String> getTableDefineIds() {
        return tableDefineIds;
    }

    public void setTableDefineIds(List<String> tableDefineIds) {
        this.tableDefineIds = tableDefineIds;
    }

    public List<String> getTenantInfoUids() {
        return tenantInfoUids;
    }

    public void setTenantInfoUids(List<String> tenantInfoUids) {
        this.tenantInfoUids = tenantInfoUids;
    }

    public String getQueryTenantUID() {
        return queryTenantUID;
    }

    public void setQueryTenantUID(String queryTenantUID) {
        this.queryTenantUID = queryTenantUID;
    }

    public boolean isAllTenant() {
        return allTenant;
    }

    public void setAllTenant(boolean allTenant) {
        this.allTenant = allTenant;
    }

    public String getQueryTenantUIDS() {
        return queryTenantUIDS;
    }

    public void setQueryTenantUIDS(String queryTenantUIDS) {
        this.queryTenantUIDS = queryTenantUIDS;
    }
}

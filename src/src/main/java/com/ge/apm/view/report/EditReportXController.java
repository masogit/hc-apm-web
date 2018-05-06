package com.ge.apm.view.report;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.report.TableDefinitionService;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ManagedBean
@ViewScoped
public class EditReportXController extends GenericCRUDUUIDController<V2_PmOrder> implements Serializable, ServerEventInterface {
    private TableDefinitionRepository tableDao;
    private TableSchema table;

    private Map<String, Object> tableValue;

    private TableDefinitionService tableService;

    private PmOrderRuleRepository pmOrderRuleRepository;
    private PmOrderRuleCheckListRepository pmckLstDao;
    private UnstructuredDataCollection report;

    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return null;
    }

    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        String reportId = WebUtil.getRequestParameter("reportId");
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        pmckLstDao = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);

      //  report = tableService.getReportById(reportId);
        /*table = JSONObject.parseObject(tableService.getTableSchemaByTemplateId(report.getSchemaId()), TableSchema.class);*/
        /*table = JSONObject.parseObject(tableService.getTableSchema(report.getId()), TableSchema.class);

        tableValue = new HashMap<String, Object>();
        List<UnstructuredData> dataList = report.getData();
        dataList.forEach(item -> {
            try{
                tableValue.put(item.getKey(),item.getValue());
            }catch(ClassNotFoundException e){
                tableValue.put(item.getKey(),item.getValueSerialized());
            }
        });*/
    }

    public String getEditLinkUrlX(TableDefinition td) {
        // TableDefinition table = tableDao.findById(report.getSchemaId());
        if (null != td.getExcelStorageId()) {
            return "/portal/reporter/EditExcelReport.xhtml?faces-redirect=true&reportId=" + td.getId();
        }else{
            return "/portal/reporter/EditReport.xhtml?faces-redirect=true&reportId=" + td.getId();
        }
    }
    protected void init_backup() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        String reportId = WebUtil.getRequestParameter("reportId");

        report = tableService.getReportById(reportId);
        table = JSONObject.parseObject(tableService.getTableSchemaByTemplateId(report.getSchemaId()), TableSchema.class);
        tableValue = new HashMap<String, Object>();
        List<UnstructuredData> dataList = report.getData();
        dataList.forEach(item -> {
            try{
                tableValue.put(item.getKey(),item.getValue());
            }catch(ClassNotFoundException e){
                tableValue.put(item.getKey(),item.getValueSerialized());
            }
        });
    }
    public void save() {
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
        tableService.saveTableDate(report,tableValue);
        WebUtil.addSuccessMessage("已保存");
    }
    
    public void submit(){
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
        tableService.saveTableDate(report,tableValue);
        tableService.submitReport(report);
         WebUtil.addSuccessMessage("已提交");
    }

    public Map<String, Object> getTableValue() {
        return tableValue;
    }
    public void saveReport(V2_PmOrder v2PmOrder){
       /* rpList = reportDao.findByBoUuid(v2PmOrder.getId());

        UnstructuredDataCollection unData=null;
        if(!rpList.isEmpty()){
            for(int i=0;i<rpList.size();i++){
                unData = (UnstructuredDataCollection) rpList.get(i);
                //   unData.get

            }


        }else{
            //  rpList =new UnstructuredDataCollection();
           // rpList.setBoUuid(v2PmOrder.getId());
        } */

    }
    public TableDefinition getPerformReport(V2_PmOrder  v2PmOrder){
        if(v2PmOrder==null){
            return null;
        }
        PmOrderRule pmOrderRule= new PmOrderRule();
        int level =  v2PmOrder.getRuleLevel();
        switch (level){
            case 3: {
                pmOrderRule= pmOrderRuleRepository.findPmOrderRuleLevel3(v2PmOrder.getAssetUid());
            }
            case 2:{
                pmOrderRule = pmOrderRuleRepository.findPmOrderRuleLevel2(v2PmOrder.getAssetUid());
            }
        }
        TableDefinition performReport =tableDao.findById(pmOrderRule.getPerformanceReport());
        return performReport;
    }

    public TableDefinition getSaftyReport(V2_PmOrder  v2PmOrder){
        if(v2PmOrder==null){
            return null;
        }
        PmOrderRule pmOrderRule= new PmOrderRule();
        int level =  v2PmOrder.getRuleLevel();
        switch (level){
            case 3: {
                pmOrderRule= pmOrderRuleRepository.findPmOrderRuleLevel3(v2PmOrder.getAssetUid());
            }
            case 2:{
                pmOrderRule = pmOrderRuleRepository.findPmOrderRuleLevel2(v2PmOrder.getAssetUid());
            }
        }

        TableDefinition  safty =tableDao.findById(pmOrderRule.getSafetyReport());
        return safty;

    }
    public  List<PmOrderRuleCheckList> acPmOrderUleCheckList(V2_PmOrder v2PmOrder){
        if(v2PmOrder ==null)
            return null;
        PmOrderRule pmOrderRule= new PmOrderRule();
        int level =  v2PmOrder.getRuleLevel();
        switch (level){
            case 3: {
                pmOrderRule= pmOrderRuleRepository.findPmOrderRuleLevel3(v2PmOrder.getAssetUid());
            }
            case 2:{
                pmOrderRule = pmOrderRuleRepository.findPmOrderRuleLevel2(v2PmOrder.getAssetUid());
            }
        }
        if(pmOrderRule==null){
            return null;
        }
        return pmckLstDao.findAllByRuleUID(pmOrderRule.getUid());
    }


    List<UnstructuredDataCollection> checkCollection ; //load  on dialog on
    List<UnstructuredDataCollection> unstructuredDataCollection ; //load  on dialog on
    // UnstructuredDataCollectionRepository

    HashMap<String,UnstructuredData> hmCheck = new HashMap<String,UnstructuredData>();
    private UnstructuredDataCollectionRepository reportDao;
    public void loadHmCheck(V2_PmOrder v2PmOrder){
        checkCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(),"pm-checklist-report");
        List<PmOrderRuleCheckList> pmCheckList= acPmOrderUleCheckList(v2PmOrder);

        UnstructuredData unData=null;

        for(int i=0;i<pmCheckList.size();i++){
            PmOrderRuleCheckList pmOrderRuleCheckListData = (PmOrderRuleCheckList)pmCheckList.get(i);
            UnstructuredDataCollection unCollection =  (UnstructuredDataCollection)checkCollection.get(0);
            List<UnstructuredData> dataList = (List<UnstructuredData>)unCollection.getData();
            for(int k =0;k<dataList.size();k++ ){
                 UnstructuredData unsData = (UnstructuredData)dataList.get(k);
                if(unsData.getKey().equalsIgnoreCase(pmOrderRuleCheckListData.getUid()))
                     {
                hmCheck.put(pmOrderRuleCheckListData.getUid(),unsData);
                }
            }
        }
        System.out.println();
//List<UnstructuredDataCollection> checkCollection
      /* pmCheckList.stream().forEach(item->{
                   unstructuredDataCollection.stream().forEach(itemU->{
                       itemU.
                   }).findAny().orelse(null);
                   hmCheck.put(item.getUid(),)
               }*/

        /*if(!rpList.isEmpty()){
            for(int i=0;i<rpList.size();i++){
                unData = (UnstructuredDataCollection) rpList.get(i);
                //   unData.get
            }


        }else{
            //  rpList =new UnstructuredDataCollection();
            // rpList.setBoUuid(v2PmOrder.getId());
        }*/

    }
    //map : item.uid(pmorderrulecheck.uid) value: unstrundata

    public void setTableValue(Map<String, Object> tableValue) {
        this.tableValue = tableValue;
    }

    public TableSchema getTable() {
        return table;
    }

    public void setTable(TableSchema table) {
        this.table = table;
    }

    public UnstructuredDataCollection getReport() {
        return report;
    }

    public HashMap<String, UnstructuredData> getHmCheck() {
        return hmCheck;
    }

    public void setHmCheck(HashMap<String, UnstructuredData> hmCheck) {
        this.hmCheck = hmCheck;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }
}

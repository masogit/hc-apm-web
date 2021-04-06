package com.ge.apm.view;

import com.ge.apm.dao.SqlExportQueueRepostitory;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.view.sysutil.AppContextService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.broker.SiBroker;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.FieldInfo;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhaokai
 * @create 2021-02-02-16:56
 */
@ManagedBean
@ViewScoped
public class SqlExportController extends JpaCRUDController<SqlExportQueue>{

    private static final long serialVersionUID = -1L;
    protected static final Logger logger = Logger.getLogger(SqlExportController.class);
    // final String excelExportDestfilepath = "C:/zk/test/export/excel/";
     final String excelExportDestfilepath = "/tmp/apm/export";
    ConfigUtils configUtils;

    //@Value("#{urlProperties.excelExportDestfilepath}")
    //private String excelExportDestfilepath;

    private SqlExportQueue selectSqlExportQueue;
    SqlExportQueueRepostitory sqlDao = null;
    private SqlExportQueue selectedProduct;
    private List<SqlExportQueue> selectedProducts;
    private UserAccount loginUser;
    private String sql;
    private String countSql;
    private String errorMsg = null;
    Page<Map<String, Object>> selectList = null;
    private String sqlColumns = "";
    private List<FieldInfo> fieldList;
    private StreamedContent exportTemplate;
    private int exportTemplateDisabled = 0;
    private Long selectTotalPages = 0L;
    private int selectPageSize = 0;
    private int  gotoPageNum =0;
    private static final Integer LX_USER_TYPE = -1; // lixinag usertype
    private boolean isShow = true;

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    @Override
    public String getColumnNameList() {
        logger.error("=========SqlExportController.getColumnNameList");
        return sqlColumns;
    }
    protected void updateTableColumns() {
        String colNameList = getColumnNameList();
        if(colNameList==null){
            logger.error("******* Dynamic Column error: getColumnNameList() returns null");
            return;
        }

        if(fieldList==null) fieldList = new ArrayList<FieldInfo>();
        fieldList.clear();

        String[] columnNames = colNameList.split(" ");
        for(String columnName : columnNames) {
            fieldList.add(new FieldInfo(columnName.trim()));
        }
    }

    public void onToggleColumn(ToggleEvent e) {
        int i = (Integer) e.getData();
        if(i>=fieldList.size()){
            logger.error("******** DynaColumn onToggleColumn error, index out of range: "+i);
            return;
        }

        fieldList.get(i).setIsVisible(e.getVisibility() == Visibility.VISIBLE);
    }
    @Override
    protected void init() {
        logger.info("=========SqlExportController.init start===========");
        sqlDao = WebUtil.getBean(SqlExportQueueRepostitory.class);
        configUtils = WebUtil.getBean(ConfigUtils.class);
        loginUser = UserContextService.getCurrentUserAccount();

        //判断是否是理想用户
        if(!loginUser.getIsSuperAdmin()){
            logger.info("=========SqlExportController.init this.isShow = FALSE===========");
            this.isShow = Boolean.FALSE;
        }else {
            logger.info("=========SqlExportController.init this.isShow = TRUE===========");
            this.isShow = Boolean.TRUE;
        }
    }
    @Override
    protected SqlExportQueueRepostitory getDAO() {
        return sqlDao;
    }
    @Override
    protected Page<SqlExportQueue> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        pageRequest=new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),new Sort(Sort.Direction.DESC,"creationTime"));
        //Page<SqlExportQueue> res = sqlDao.findBySearchFilter(this.searchFilters, pageRequest);
        Page<SqlExportQueue> res = sqlDao.findAll(pageRequest);

        return res;
    }



    @Override
    public void setSelected(SqlExportQueue sqlExportQueue) {
        if (sqlExportQueue == null) {
            return;
        }

        this.selected = sqlExportQueue;
        System.out.println(this.selected);
    }

    public void onSelectExportQueue() {
        System.out.println(selected);
        System.out.println(selected);
    }

    public void onRowSelect(SelectEvent event) {
        SqlExportQueue sqlExportQueue = (SqlExportQueue) event.getObject();
        FacesMessage msg = new FacesMessage("Product Selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Product Unselected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void executeSql(){
        if(sql==null) return;
        sql = sql.trim();
        System.out.println(sql);
    }
    //
    public void addSelectSql(){
        try {
            //1.判断是否有数据返回
            logger.info("=========SqlExportController.addSelectSql start===========");
            Map<String, Object> params = new HashMap();
            this.countSql = "select count(*) from ("+sql+ ") tmp";
            //String theCountSQL = (countSql == null) ? "select count(*) from (" + sql + ") _count" : countSql;
            logger.info("=========SqlExportController.addSelectSql theCountSQL={"+countSql+"}===========");
            //theCountSQL = theCountSQL.replace(":#searchFilter", "");
            params.put("_sql", countSql);
            params.put("_pageStart", 0);
            params.put("_pageSize", 20);
            List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
            logger.info("=========SqlExportController.addSelectSql result.size={"+result.size()+"}===========");
            //int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());
            //logger.info("=========SqlExportController.addSelectSql rowCount={"+rowCount+"}===========");
            //int sqlExportQueueCount = sqlDao.findSqlExportQueueByExecuteStatement(sql);
            //logger.info("=========SqlExportController.addSelectSql sqlExportQueueCount={"+sqlExportQueueCount+"}===========");
            //if(rowCount > 0 && sqlExportQueueCount == 0 ){
                //2.插入任务列表
                logger.info("=========SqlExportController.addSelectSql sqlDao.save(sqlExportQueue) start===========");
                SqlExportQueue sqlExportQueue = new SqlExportQueue();
                sqlExportQueue.setTaskStatus(0);
                sqlExportQueue.setCreater(loginUser.getId());
                sqlExportQueue.setCreaterName(loginUser.getLoginName());
                sqlExportQueue.setCreationTime(new Date());
                sqlExportQueue.setExecuteStatement(sql);
                sqlDao.save(sqlExportQueue);
                logger.info("=========SqlExportController.addSelectSql sqlDao.save(sqlExportQueue) end===========");
            //}else{
            //    if(rowCount == 0){
            //        WebUtil.addSuccessMessage(WebUtil.getMessage("NoCorrespondingDataFound"));//未查询到数据
            //    }else{
            //        WebUtil.addSuccessMessage(WebUtil.getMessage("ErrorDuplicatedData")+":"+"语句");//重复的语句
            //    }
            //}
        } catch (Exception e) {
            logger.error("=========SqlExportController.addSelectSql error={"+e.getMessage()+"}==========");
            SqlExportQueue sqlExportQueue = new SqlExportQueue();
            sqlExportQueue.setTaskStatus(5);//未生成:0，生成中:1,生成成功:2，生成失败:3，已删除:4 ,sql执行失败：5
            sqlExportQueue.setCreater(loginUser.getId());
            sqlExportQueue.setCreaterName(loginUser.getLoginName());
            sqlExportQueue.setCreationTime(new Date());
            sqlExportQueue.setExecuteStatement(sql);
            sqlDao.save(sqlExportQueue);
            e.printStackTrace();
        }

    }
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public SqlExportQueue getSelectSqlExportQueue() {
        return selectSqlExportQueue;
    }

    public void setSelectSqlExportQueue(SqlExportQueue selectSqlExportQueue) {
        this.selectSqlExportQueue = selectSqlExportQueue;
    }

    public SqlExportQueue getSelectedProduct() {
        return selectedProduct;
    }

    public void downLoadExcel(SqlExportQueue selectedProduct){

    }
    public void setSelectedDownProduct(SqlExportQueue selectedProduct){
        this.selectedProduct = selectedProduct;
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        String fileDir = configUtils.fetchProperties("excelExportDestfilepath")+dateNowStr+"/"+selectedProduct.getId()+".xlsx";
        File f= new File(fileDir) ;    // 声明File对象
        //InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(fileDir);
        // 第2步、通过子类实例化父类对象
        InputStream input = null ;    // 准备好一个输入的对象
        try {
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        exportTemplate = new DefaultStreamedContent(input, "application/vnd.ms-excel", selectedProduct.getId()+".xlsx");
        System.out.println(selectedProduct);
    }
    public void exportTemplate2(SqlExportQueue selectedProduct){
        logger.info("=========SqlExportController.exportTemplate2  start updateTime={"+selectedProduct.getUpdateTime()+"}==========");
        Date d = selectedProduct.getUpdateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        String fileDir = configUtils.fetchProperties("excelExportDestfilepath")+dateNowStr+"/"+selectedProduct.getId()+".xlsx";
        logger.info("=========SqlExportController.exportTemplate2 fileDir={"+fileDir+"}==========");
        File f= new File(fileDir) ;    // 声明File对象
        logger.info("=========SqlExportController.exportTemplate2 file={"+f.toString()+"}==========");
        //InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(fileDir);
        // 第2步、通过子类实例化父类对象
        InputStream input = null ;    // 准备好一个输入的对象
        try {
            logger.info("=========SqlExportController.exportTemplate2 input = new FileInputStream(f)==========");
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            logger.error("=========SqlExportController.exportTemplate2 input = new FileInputStream(f) Error==========");
            e.printStackTrace();
        }
        logger.info("=========SqlExportController.exportTemplate2 exportTemplate==========");
        exportTemplate = new DefaultStreamedContent(input, "application/vnd.ms-excel", selectedProduct.getId()+".xlsx");
        logger.info("=========SqlExportController.exportTemplate2 exportTemplate end==========");
    }

    public void exportGenerate(SqlExportQueue selectedProduct){
        selectedProduct.setTaskStatus(0);
        selectedProduct.setUpdateTime(new Date());
        sqlDao.save(selectedProduct);
    }

    public void setSelectedProduct(SqlExportQueue selectedProduct) {
        logger.info("=========SqlExportController.setSelectedProduct  start===========");
        this.selectedProduct = selectedProduct;
        getPreviewData(1);
        logger.info("=========SqlExportController.setSelectedProduct  end===========");
    }
    public void gotoPageNumMethod() {
        logger.info("=========SqlExportController.gotoPageNumMethod  start===========");
        if(gotoPageNum<0){
            logger.info("=========SqlExportController.gotoPageNumMethod gotoPageNum<0  ===========");
            gotoPageNum = 1;
        }else if(gotoPageNum>selectPageSize){
            logger.info("=========SqlExportController.gotoPageNumMethod gotoPageNum>0  ===========");
            gotoPageNum = selectPageSize;
        }
        getPreviewData(gotoPageNum);
        logger.info("=========SqlExportController.gotoPageNumMethod  end===========");
    }
    private void getPreviewData(int gotoPageNum){
        logger.info("=========SqlExportController.getPreviewData  start===========");
        if(selectedProduct.getExecuteStatement()==null){
            logger.info("=========SqlExportController.getPreviewData  selectedProduct.getExecuteStatement()==null===========");
            return;
        }
        sql = selectedProduct.getExecuteStatement();
        sql = sql.trim();
        logger.info("=========SqlExportController.getPreviewData sql={"+sql+"}");
        if("".equals(sql)) return;

        // remove the last ; if exists
        if(sql.endsWith(";")) sql = sql.substring(0, sql.length()-1);
        if(!sql.toLowerCase().startsWith("select ")){
            executeSql();
            return;
        }

        this.countSql = "select count(*) from ("+sql+ ") tmp";

        String theSQL = sql.replace(":#searchFilter", "").replace(":#orderBy", "");

        //update countSQL: to fill in :#searchFilter part. there should be no :#orderBy in countSQL
        String theCountSQL = (countSql == null) ? "select count(*) from (" + theSQL + ") _count" : countSql;
        theCountSQL = theCountSQL.replace(":#searchFilter", "");
        Map<String, Object> params = new HashMap<>();
        params.put("_sql", theCountSQL);

        //get count of rows: execute the countSQL
        List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());

        // add Page limit to the sql
        theSQL = "select * from (" + theSQL + ") _count" + AppContextService.getSqlPagingTemplate();
        params.put("_sql", theSQL);
        params.put("_pageStart", (gotoPageNum-1) * 20);
        params.put("_pageSize", 20);
        result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        PageRequest pageRequest = new PageRequest(gotoPageNum-1,20);

        logger.info("=========SqlExportController.getPreviewData PageImpl start");
        selectList = new PageImpl<Map<String, Object>>(result, pageRequest, rowCount);
        logger.info("=========SqlExportController.getPreviewData PageImpl end");
        // updateTableColumns

        sqlColumns = "";
        List<Map<String, Object>> list = selectList.getContent();
        selectTotalPages =  selectList.getTotalElements();
        selectPageSize = selectList.getTotalPages();
        logger.info("=========SqlExportController.getPreviewData selectTotalPages={"+selectTotalPages+"}  selectPageSize={"+selectPageSize+"}");
        if(list.size()>0){
            Map<String, Object> row = list.get(0);

            for(Object field: row.keySet().toArray()){
                sqlColumns = sqlColumns + " " + field.toString();
            }
        }
        sqlColumns = sqlColumns.trim();
        logger.info("=========SqlExportController.getPreviewData updateTableColumns start and sqlColumns={"+sqlColumns+"}");
        updateTableColumns();
        logger.info("=========SqlExportController.getPreviewData  updateTableColumns end");
    }
    public String fieldValue(String msgKey) {
        switch(msgKey)
        {
            case "0" :
                return "未生成";
            case "1" :
                return "生成中";
            case "2" :
                return "生成成功";
            case "3" :
                return "生成失败";
            case "4" :
                return "已删除";
            case "5" :
                return "sql执行失败";
            default :
                return "未知状态";
        }
    }
    //下载文件
    public void downLoadFile(){
        System.out.println(111);
    }

    public List<SqlExportQueue> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(List<SqlExportQueue> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }
    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Page<Map<String, Object>> getSelectList() {
        return selectList;
    }

    public void setSelectList(Page<Map<String, Object>> selectList) {
        this.selectList = selectList;
    }

    public StreamedContent getExportTemplate() {
        return exportTemplate;
    }

    public void setExportTemplate(StreamedContent exportTemplate) {
        this.exportTemplate = exportTemplate;
    }

    public int getExportTemplateDisabled() {
        return exportTemplateDisabled;
    }

    public void setExportTemplateDisabled(int exportTemplateDisabled) {
        this.exportTemplateDisabled = exportTemplateDisabled;
    }

    public Long getSelectTotalPages() {
        return selectTotalPages;
    }

    public void setSelectTotalPages(Long selectTotalPages) {
        this.selectTotalPages = selectTotalPages;
    }

    public int getSelectPageSize() {
        return selectPageSize;
    }

    public void setSelectPageSize(int selectPageSize) {
        this.selectPageSize = selectPageSize;
    }

    public int getGotoPageNum() {
        return gotoPageNum;
    }

    public void setGotoPageNum(int gotoPageNum) {
        this.gotoPageNum = gotoPageNum;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}

package com.ge.apm.view;

import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class SqlDynaColumnController extends SqlDataController {

    private String sqlColumns = "";
    private boolean isShow = true;
    private static final Integer LX_USER_TYPE = -1; // lixinag usertype

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    public void init() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        //判断是否是理想用户
        if(LX_USER_TYPE.equals(loginUser.getType())){
            this.isShow = Boolean.FALSE;
        }else {
            this.isShow = Boolean.TRUE;
        }
    }

    @Override
    public String getColumnNameList() {
        return sqlColumns;
    }
    
    public void resetSqlResult(){
        sqlColumns = "";
        updateTableColumns();
        selected = null;
    }

    private String errorMsg = null;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    @Override
    protected Page<Map<String, Object>> loadData(PageRequest pageRequest, String searchFilterSQL, String orderBySQL, Map<String, Object> params) {
        resetSqlResult();
        
        if(sql==null) return emptyPage;
        sql = sql.trim();
        if("".equals(sql)) return emptyPage;
        
        // remove the last ; if exists
        if(sql.endsWith(";")) sql = sql.substring(0, sql.length()-1);
        
        try{
            if(!sql.toLowerCase().startsWith("select ")){
                executeSql();
                return emptyPage;
            }

            this.countSql = "select count(*) from ("+sql+ ") tmp";

            Page<Map<String, Object>> data = super.loadData(pageRequest, searchFilterSQL, orderBySQL, params);

            // updateTableColumns
            sqlColumns = "";
            List<Map<String, Object>> list = data.getContent();
            if(list.size()>0){
                Map<String, Object> row = list.get(0);
                
                for(Object field: row.keySet().toArray()){
                    sqlColumns = sqlColumns + " " + field.toString();
                }
            }
            sqlColumns = sqlColumns.trim();
            updateTableColumns();
            
            return data;
        }
        catch(Exception ex){
            errorMsg = ex.getCause().getMessage();
            WebUtil.addErrorMessage("SQL Error: "+errorMsg);
            return emptyPage;
        }
   }
    
    public String getSelectedRowData(){
        if(selected==null) return "";
        String result = "";
        
        for(Map.Entry<String, Object> field: selected.entrySet()){
            result = result + field.getKey()+"="+field.getValue() + "\n";
        }
        return result;
    }
    
    public void executeSql(){
        if(sql==null) return;
        
        sql = sql.trim();
        if("".equals(sql)) return;
 
        //support multiple SQL statements seperated by ;
        String[] sqlList = sql.split(";");
        for (String sqlLine : sqlList) {
            sqlLine = sqlLine.replaceAll("\n", " ").replaceAll("\r", " ").trim();
            if("".equals(sqlLine)) continue;
            
            NativeSqlUtil.execute(sqlLine, null);
        }
    }
}
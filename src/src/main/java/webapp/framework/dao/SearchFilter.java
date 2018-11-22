package webapp.framework.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.util.HttpRequestUtil;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

/**
 *
 * @author wujianb
 */
public class SearchFilter {
    public static final String searchFilterPrefix = ":search_";

    public enum Operator {
        EQ, LIKE, GT, LT, GTE, LTE, NE, IN,IsNull, IsNotNull, NotIN
    }

    public String tableAlias;
    public String fieldName;
    public Object value;
    public Operator operator;
    
    /**
     *  conditionPath : the path build conditions with 'and' 'or'.
     *  start with a node name and end with "*"(AND) and "+"(OR). 
     *  such as     A and B         => (A, 1*), (B,1*)              ==> (A, AB*)(B,AB*)
     *              A or B          => (A, 1+), (B,1+)              ==> (A, AB+)(B,AB+)
     *              A and B or C    => (A, 1+2*),(B,1+2*),(C,1+)    ==> (A, ABC+AB*)(B,ABC+AB*)(C,ABC+)
     *              A and (B or C)  => (A, 1*),(B,1*2+),(C,1*2+)    ==> (A, ABC*),(B,ABC*BC+),(C,ABC*BC+)
    */
    public String conditionPath;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.fieldName);
        hash = 29 * hash + Objects.hashCode(this.operator);
        hash = 29 * hash + Objects.hashCode(this.conditionPath);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchFilter other = (SearchFilter) obj;
        if (!Objects.equals(this.fieldName, other.fieldName)) {
            return false;
        }
        if (this.operator != other.operator) {
            return false;
        }
        if (!Objects.equals(this.conditionPath, other.conditionPath)) {
            return false;
        }
        return true;
    }

    public SearchFilter(String fieldName, Operator operator, Object value) {
        this(fieldName,operator,value,"1*");
    }
    
    public SearchFilter(String fieldName, Operator operator, Object value,String conditionPath) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
        this.conditionPath = conditionPath;
        parseFieldName();
    }

    private void parseFieldName() {
        String[] nameParts = fieldName.split("-");
        if (nameParts.length > 1) {
            this.tableAlias = nameParts[0];
            this.fieldName = nameParts[1];
        }
    }

    /**
     * searchParams: format is OPERATOR_FIELDNAME
     */
    private static List<SearchFilter> parse(Map<String, Object> searchParams) {
        List<SearchFilter> filters = new ArrayList<SearchFilter>();

        for (Entry<String, Object> entry : searchParams.entrySet()) {
            String key = entry.getKey();

            Object value = entry.getValue();
            if (StringUtils.isBlank((String) value))  continue;

            int indexFieldName = key.indexOf("_");
            if(indexFieldName<2) {
                WebUtil.addErrorMessage(WebUtil.getMessage("InvalidSearchFilterName")+": "+key);
                continue;                
            }

            Operator operator = Operator.valueOf(key.substring(0, indexFieldName));
            
            String fieldName;
            if(key.endsWith("BOOLEAN")){
                fieldName = key.substring(indexFieldName+1, key.length()-8);
                value = value.equals("true");
            }
            else if(key.contains("_DATETIME")){
                int posDataType = key.indexOf("_DATETIME");
                fieldName = key.substring(indexFieldName+1, posDataType);

                try{
                    value = TimeUtil.fromString(value.toString()).toDate();
                }
                catch(Exception ex){
                    WebUtil.addErrorMessage(WebUtil.getMessage("InvalidDateFormat")+": " + entry.getValue());
                    continue;
                }
            }
            else{
                fieldName = key.substring(indexFieldName+1, key.length());
            }
            
            if(fieldName.endsWith("_input"))
                fieldName = fieldName.substring(0, fieldName.length()-6);
            
            SearchFilter filter;
            if (operator.equals(Operator.IN) || operator.equals(Operator.NotIN)) {
                String[] intList = StringUtils.split((String) value, ",");
                List<Integer> valueList = new ArrayList<Integer>();
                for (String intValue : intList) {
                    try {
                        valueList.add(Integer.valueOf(intValue));
                    } catch (Exception ex) {
                        valueList.add(-1);
                    }
                }

                filter = new SearchFilter(fieldName, operator, valueList);
            } else {
                filter = new SearchFilter(fieldName, operator, value);
            }

            filters.add(filter);
        }

        return filters;
    }

    private static Set<SearchFilter> parseToParamSet(Map<String, Object> searchParams) {
        Set<SearchFilter> filters = new HashSet<SearchFilter>();

        for (Entry<String, Object> entry : searchParams.entrySet()) {
            String key = entry.getKey();

            Object value = entry.getValue();
            if (StringUtils.isBlank((String) value))  continue;

            int indexFieldName = key.indexOf("_");
            if(indexFieldName<2) {
                WebUtil.addErrorMessage(WebUtil.getMessage("InvalidSearchFilterName")+": "+key);
                continue;                
            }

            Operator operator = Operator.valueOf(key.substring(0, indexFieldName));
            
            String fieldName;
            if(key.endsWith("BOOLEAN")){
                fieldName = key.substring(indexFieldName+1, key.length()-8);
                value = value.equals("true");
            }
            else if(key.contains("_DATETIME")){
                int posDataType = key.indexOf("_DATETIME");
                fieldName = key.substring(indexFieldName+1, posDataType);

                try{
                    value = TimeUtil.fromString(value.toString()).toDate();
                }
                catch(Exception ex){
                    WebUtil.addErrorMessage(WebUtil.getMessage("InvalidDateFormat")+": " + entry.getValue());
                    continue;
                }
            }
            else{
                fieldName = key.substring(indexFieldName+1, key.length());
            }
            
            if(fieldName.endsWith("_input"))
                fieldName = fieldName.substring(0, fieldName.length()-6);
            
            SearchFilter filter;
            if (operator.equals(Operator.IN) || operator.equals(Operator.NotIN)) {
                String[] intList = StringUtils.split((String) value, ",");
                List<Integer> valueList = new ArrayList<Integer>();
                for (String intValue : intList) {
                    try {
                        valueList.add(Integer.valueOf(intValue));
                    } catch (Exception ex) {
                        valueList.add(-1);
                    }
                }

                filter = new SearchFilter(fieldName, operator, valueList);
            } else {
                filter = new SearchFilter(fieldName, operator, value);
            }

            filters.add(filter);
        }

        return filters;
    }

    public static List<SearchFilter> getSearchFiltersFromHttpRequest(String prefix) {
        Map<String, Object> params = HttpRequestUtil.getParametersStartingWith(prefix);
        return parse(params);
    }
    public static Set<SearchFilter> getSearchFilterSetFromHttpRequest(String prefix) {
        Map<String, Object> params = HttpRequestUtil.getParametersStartingWith(prefix);
        return parseToParamSet(params);
    }

        
    public String getSqlParamName(){
        String sqlOperator = this.operator.toString();

        String fName = fieldName;
        if(fName.contains("\""))
            fName = fName.replaceAll("\"", "");
        
        if(this.tableAlias==null)
            return String.format("%s%s", fName, sqlOperator);
        else
            return String.format("%s_%s%s", this.tableAlias, fName, sqlOperator);
    }

    public String getConditionSql(){
        String sqlFieldName;
        if(this.tableAlias==null)
            sqlFieldName = this.fieldName;
        else
            sqlFieldName = String.format("%s.%s", this.tableAlias,this.fieldName);

        String paramName = this.getSqlParamName();
        if(paramName.contains("\""))
            paramName = paramName.replaceAll("\"", "");
                
        String where = null;
        switch (this.operator) {
            case EQ:
                where = sqlFieldName + "=:#" + paramName;
                break;
            case LIKE:
                where = sqlFieldName +  " like :#" + paramName;
                break;
            case GT:
                where = sqlFieldName +  ">:#" + paramName;
                break;
            case GTE:
                where = sqlFieldName +  ">=:#" + paramName;
                break;
            case LT:
                where = sqlFieldName + "<:#" + paramName;
                break;
            case LTE:
                where = sqlFieldName + "<=:#" + paramName;
                break;
            case NE:
                where = sqlFieldName + "!=:#" + paramName;
                break;
            case IN:
                where = sqlFieldName + " in (:#" + paramName + ")";
                break;
            case NotIN:
                where = sqlFieldName + " not in (:#" + paramName + ")";
                break;
            case IsNull:
                where = sqlFieldName + " is null";
                break;
            case IsNotNull:
                where = sqlFieldName + " is not null";
                break;
        }
    
        return where;
    }
    
    public static String buildSearchFilterSql(List<SearchFilter> searchFilterList){
        String sqlWhere = "";
        for (SearchFilter filter : searchFilterList) {
            sqlWhere = sqlWhere + " and " + filter.getConditionSql();
        }

        return sqlWhere;
    }
    public static Map<String, Object> buildSearchFilterSqlParam(List<SearchFilter> searchFilterList) {
        Map<String, Object> params = new HashMap<String, Object>();

        for (SearchFilter filter : searchFilterList) {
            if (filter.operator.equals(SearchFilter.Operator.LIKE)) {
                params.put(filter.getSqlParamName(), "%" + filter.value + "%");
            } else {
                params.put(filter.getSqlParamName(), filter.value);
            }
        }
        return params;
    }
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return fieldName+" "+operator+" "+value+" "+tableAlias;
    }
}

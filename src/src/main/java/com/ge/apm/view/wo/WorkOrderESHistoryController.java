package com.ge.apm.view.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrderESHistory;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean
@ViewScoped
public class WorkOrderESHistoryController extends GenericCRUDUUIDController<WorkOrderESHistory>  {

    private UserAccount currentUser;

    private ElasticService elasticService;
    private String accessToken ;

    private static final String WORK_ORDER_HISTORY_INDEX="work_order_history";

    private Map<String,Field> fieldMap;


    private String preTags = "<span style='background-color: yellow'>";
    private String postTags = "</span>";

    @Override
    protected GenericRepositoryUUID<WorkOrderESHistory> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        searchFilters = new ArrayList<>();
        currentUser = UserContextService.getCurrentUserAccount();
        accessToken = UserContextService.getAccessToken();
        elasticService = WebUtil.getBean(ElasticService.class);
        fieldMap = Stream.of(WorkOrderESHistory.class.getDeclaredFields()).collect(Collectors.toMap(Field::getName,item->item));
    }

    @Override
    protected Page<WorkOrderESHistory> loadData(PageRequest pageRequest) {
        Page<Object> result = elasticService.searchHistory(searchFilters,accessToken,pageRequest.getPageNumber(),pageRequest.getPageSize(),WORK_ORDER_HISTORY_INDEX, null, null);
        List<Object> content = result.getContent();
        if(CollectionUtils.isEmpty(content)){
            return new PageImpl<WorkOrderESHistory>(new ArrayList<>());
        }else{
            SerializeConfig config = new SerializeConfig();
            config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
            List<WorkOrderESHistory> data = content.stream().map(item ->initObject(item)).collect(Collectors.toList());
            highlightField(data);
            return new PageImpl<WorkOrderESHistory>(data,pageRequest,result.getTotalElements());
        }
    }

    @Override
    protected void addFilter(String fieldName, String matchMode, Object filterValue) {
        super.addFilter(fieldName, matchMode, filterValue);
        if("RETRIEVAL".equals(matchMode)){
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.RETRIEVAL, filterValue));
        }
    }

    private void highlightField(List<WorkOrderESHistory> data){
        if(CollectionUtils.isEmpty(searchFilters)) return;
        if(CollectionUtils.isEmpty(data)) return;
        Map<Field, Object> fieldObjectMap = searchFilters.stream().collect(Collectors.toMap(item -> fieldMap.get(item.fieldName), item -> item.value));
        data.forEach(item->{
            doHighlight(item,fieldObjectMap);
        });
    }

    /**
     *  高亮检索字段
     * @param data
     * @param fieldObjectMap
     */
    private void doHighlight(WorkOrderESHistory data,Map<Field, Object> fieldObjectMap){

        for (Map.Entry<Field, Object> fieldObjectEntry : fieldObjectMap.entrySet()) {
            try {
                Field field = fieldObjectEntry.getKey();
                Object search = fieldObjectEntry.getValue();
                field.setAccessible(true);
                Object o = field.get(data);
                if(o instanceof String){
                    String value =(String) o;
                    value=value.replaceAll((String) search,preTags + search + postTags);
                    field.set(data,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    public void onSelectOrder() throws IOException {
        String url = "actionName=View&selectedid="
                + selected.getId().toString()
                + "&queryIndex=8";
        url = "workOrderList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    private WorkOrderESHistory initObject(Object o){
        JSONObject jsonObjectNew = new JSONObject();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            jsonObjectNew.put(underlineToCamel(stringObjectEntry.getKey()),stringObjectEntry.getValue());
        }
        WorkOrderESHistory result = JSON.toJavaObject(jsonObjectNew, WorkOrderESHistory.class);
        return result;
    }

    private static String underlineToCamel(String underlineName){
        StringBuilder camelCaseStr = new StringBuilder();
        String[] split = underlineName.split("_");
        camelCaseStr.append(split[0].toLowerCase());
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            if("uid".equalsIgnoreCase(s)){
                camelCaseStr.append(s.toUpperCase());
            }else{
                char[] charArray = s.toCharArray();
                charArray[0] = Character.toUpperCase(charArray[0]);
                camelCaseStr.append(charArray);
            }
        }
        return camelCaseStr.toString();
    }

}

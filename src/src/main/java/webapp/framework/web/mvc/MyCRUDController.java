package webapp.framework.web.mvc;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public abstract class MyCRUDController<E> implements Serializable, ServerEventInterface {

    private static final long serialVersionUID = 1L;

    protected boolean filterBySite = true;
    protected boolean filterByHospital = false;
    protected boolean filterByLoginUser = false;

    protected static final String searchFilterPrefix = ":search_";
    protected static final Logger logger = Logger.getLogger(GenericCRUDController.class);

    abstract protected BaseMapper<E> getDAO();

    public Map<String, String> filterMatchModes;
    public Set<SearchFilter> searchFilters;
    protected boolean warnOnEmptySearchFilters = false;
    public String crudActionName;


    public boolean isWarnOnEmptySearchFilters() {
        return warnOnEmptySearchFilters;
    }

    public void setWarnOnEmptySearchFilters(boolean warnOnEmptySearchFilters) {
        this.warnOnEmptySearchFilters = warnOnEmptySearchFilters;
    }

    public Class<E> getBeanClass() {
        return (Class<E>) ((ParameterizedType) (getClass().getGenericSuperclass()))
                .getActualTypeArguments()[0];
    }

    private Map<String, Boolean> dateTimeFields = null;

    protected boolean isDateTimeField(String fieldName) {
        if (dateTimeFields == null) {
            dateTimeFields = new HashMap<String, Boolean>();
            Class a = getBeanClass();
            for (Field f : a.getDeclaredFields()) {
                if (f.getType().toString().contains("Date")) {
                    dateTimeFields.put(f.getName(), Boolean.TRUE);
                }
            }
        }
        return dateTimeFields.get(fieldName) != null;
    }

    protected void getFilterMatchModes(DataTable table) {
        if (filterMatchModes == null) {
            filterMatchModes = new HashMap<String, String>();
            for (FilterMeta a : (List<FilterMeta>) table.getFilterMetadata()) {
                String fieldName = a.getFilterByVE().getExpressionString();
                if (fieldName == null) {
                    continue;
                }

                fieldName = fieldName.replace("}", "").substring(fieldName.indexOf(".") + 1);
                filterMatchModes.put(fieldName, a.getColumn().getFilterMatchMode());
            }
        }
    }

    protected void addFilter(String fieldName, String matchMode, Object filterValue) {
        matchMode = matchMode.toUpperCase();

        if ("EQ".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.EQ, filterValue));
        } else if ("LIKE".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LIKE, filterValue));
        } else if ("LT".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LT, filterValue));
        } else if ("LTE".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, filterValue));
        } else if ("GT".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GT, filterValue));
        } else if ("GTE".equals(matchMode)) {
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, filterValue));
        } else {
            logger.warn("**** filterMatchMode not supported: " + matchMode);
        }
    }

    public void onFilter(FilterEvent event) {
        if (searchFilters == null) {
            searchFilters = new HashSet<>();
        }
        searchFilters.clear();

        getFilterMatchModes((DataTable) event.getComponent());
        for (Map.Entry<String, Object> filter : event.getFilters().entrySet()) {
            String fieldName = filter.getKey();
            Object filterValue = filter.getValue();
            if (filterValue == null) {
                continue;
            }

            if (isDateTimeField(fieldName)) {
                // support lte & gte for datetime field
                String[] values = filterValue.toString().split("~");
                if (values.length > 0) {
                    filterValue = TimeUtil.fromString(values[0]);

                    if (filterValue != null) {
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, ((DateTime) filterValue).toDate()));
                    }
                }

                if (values.length > 1) {
                    filterValue = TimeUtil.fromString(values[1]);
                    if (filterValue != null) {
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, ((DateTime) filterValue).toDate()));
                    }
                }
                continue;
            }

            String matchMode = filterMatchModes.get(fieldName);
            addFilter(fieldName, matchMode, filterValue);
        }
    }

    protected void wrapSearchFilter(Wrapper ew){
        if(searchFilters==null) return;
        if(searchFilters.size()<1) return;
        
        for(SearchFilter filter: searchFilters){
            switch(filter.operator){
                case EQ:
                    ew.eq(filter.fieldName, filter.value);
                    break;
                case LIKE:
                    if(filter.value!=null){
                        ew.like("lower("+filter.getFieldFullName()+")", filter.value.toString().toLowerCase());
                    }
                    break;
                case LT :
                    ew.lt(filter.fieldName, filter.value);
                    break;
                case LTE:
                    ew.le(filter.fieldName, filter.value);
                    break;
                case GT:
                    ew.gt(filter.fieldName, filter.value);
                    break;
                case GTE:
                    ew.ge(filter.fieldName, filter.value);
                    break;
            }
        }
    }
    
    protected List<E> loadData(Pagination  pageRequest, String sortField, boolean isAscending){
        //build SQL where
        EntityWrapper ew= new EntityWrapper();
        wrapSearchFilter(ew);
        
        //first get total row count
        lazyModel.setRowCount(getDAO().selectCount(ew));

        //then get and return the row data
        ew.orderBy(sortField, isAscending);
        return getDAO().selectPage(pageRequest, ew);
    }

    protected LazyDataModel lazyModel;
    protected E selected;

    protected void init() {
    }
    
    @PostConstruct
    private void doPostConstruct() {
        init();

        lazyModel = new MyCRUDDataTableModel<E>() {
            @Override
            protected List<E> load(Pagination  pageRequest, String sortField, boolean isAscending, Map<String, Object> filters){
                return loadData(pageRequest, sortField, isAscending);
            }
        };
    }

    public LazyDataModel getLazyModel() {
        return lazyModel;
    }

    public boolean isDeleteAction() {
        return "Delete".equals(crudActionName);
    }

    public String getCRUDActionName() {
        return WebUtil.getMessage(crudActionName);
    }

    public List<E> getItemList() {
        return getDAO().selectList(null); 
    }

    public E getSelected() {
        return selected;
    }

    public void setSelected(E selectedObj) {
        if (selectedObj == null) {
            return;
        }
        selected = selectedObj;
    }

    protected Class<E> getEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected String getActionMessage(String actionName) {
        return WebUtil.getMessage(getEntityClass().getSimpleName()) + " " + WebUtil.getMessage(actionName);
    }

    public void onBeforeNewObject(E object) {}
    public void onAfterNewObject(E object, boolean isOK) {}
    public void onBeforeUpdateObject(E object) {}
    public void onAfterUpdateObject(E object, boolean isOK) {}
    public void onBeforeDeleteObject(E object) {}
    public void onAfterDeleteObject(E object, boolean isOK) {}
    public void onBeforeSave(E object) {}
    public void onAfterDataChanged() {}
    @Override
    public void onServerEvent(String eventName, Object eventObject) {}

    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";

        Class<E> entityClass = getEntityClass();
        selected = entityClass.newInstance();
        setSelected(selected);

        onBeforeNewObject(selected);
    }

    public void prepareEdit() {
        crudActionName = "Edit";
    }

    public void prepareView() {
        crudActionName = "View";
    }

    public void prepareDelete() {
        crudActionName = "Delete";
    }

    public void setSelected(int id) {
        selected = getDAO().selectById(id);
        setSelected(selected);
    }

    public void create() {
        boolean isOK = persist(WebUtil.PersistAction.CREATE, getActionMessage("Created"));
        onAfterNewObject(selected, isOK);
    }

    public void save() {
        onBeforeSave(selected);
        if (selected.hashCode() == 0) {
            create();
        } else {
            update();
        }
    }

    public void update() {
        onBeforeUpdateObject(selected);
        boolean isOK = persist(WebUtil.PersistAction.UPDATE, getActionMessage("Updated"));
        onAfterUpdateObject(selected, isOK);
    }

    public void delete(E obj) {
        this.selected = obj;
        delete();
    }

    public void delete() {
        onBeforeDeleteObject(selected);
        boolean isOK = persist(WebUtil.PersistAction.DELETE, getActionMessage("Deleted"));
        onAfterDeleteObject(selected, isOK);

        if (!WebUtil.isValidationFailed()) {
            selected = null; // Remove selection
        }
    }

    public String getErrorMessageForDuplicateKey(E object) {
        return null;
    }

    public String getKeyFieldNameValue(E object) {
        return "";
    }

    protected boolean onPersistException(Exception ex, E object) {
        return false;
    }

    protected Serializable getObjectIdValue(E object){
        if(object==null) return null;
        try { 
            Method idMethod = object.getClass().getDeclaredMethod("getId");
            return (Serializable)idMethod.invoke(object);
        } catch (Exception ex) {
        }
        
        try { 
            Method idMethod = object.getClass().getDeclaredMethod("getUid");
            return (Serializable)idMethod.invoke(object);
        } catch (Exception ex) {
        }

        return null;
    }
    
    protected boolean persist(WebUtil.PersistAction persistAction, String successMessage) {
        if (selected == null)  return false;
        try {
            switch (persistAction) {
                case DELETE:
                    getDAO().deleteById(getObjectIdValue(selected));
                    break;
                case UPDATE:
                    getDAO().updateById(selected);
                    break;
                default:
                    getDAO().insert(selected);
                    break;
            }

            onAfterDataChanged();
            WebUtil.addSuccessMessage(successMessage);
            return true;
        } catch (Exception ex) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);

            if (onPersistException(ex, selected)) {
                return false;
            }

            if (ex.getClass().equals(DataIntegrityViolationException.class)) {
                DataIntegrityViolationException exDuplicateKey = (DataIntegrityViolationException) ex;
                if (exDuplicateKey.getRootCause().getMessage().toLowerCase().contains("duplicate ")) {
                    String errMessage = getErrorMessageForDuplicateKey(selected);

                    if (errMessage == null) {
                        errMessage = getKeyFieldNameValue(selected) + ": " + WebUtil.getMessage("PersistenceErrorDuplicateKey");
                    }

                    WebUtil.addErrorMessage(errMessage);
                    return false;
                }
            }

            String msg = "";
            Throwable cause = ex.getCause();
            if (cause != null) {
                msg = cause.getLocalizedMessage();
            }
            if (msg.length() > 0) {
                WebUtil.addErrorMessage(msg);
            } else {
                WebUtil.addErrorMessage(ex, WebUtil.getMessage("PersistenceErrorOccured"));
            }
        }

        return false;
    }

    public void setSearchFilter() {
        searchFilters = SearchFilter.getSearchFilterSetFromHttpRequest(searchFilterPrefix);

        if (warnOnEmptySearchFilters && (searchFilters == null || searchFilters.isEmpty())) {
            WebUtil.addErrorMessageKey("noSearchCondition");
        }
    }
}

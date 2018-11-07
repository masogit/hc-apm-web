package webapp.framework.web.mvc;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public abstract class MyCRUDDataTableModel<T> extends LazyDataModel<T> {
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(MyCRUDDataTableModel.class);
    
    private boolean resetPaginator = true;
    abstract protected List<T> load(Pagination  pageRequest, String sortField, boolean isAscending, Map<String, Object> filters);

    //Automatically called by PrimeFaces component.
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        return load(toPagination(first, pageSize), sortField, sortOrder.equals(SortOrder.ASCENDING), filters);
    }

    protected Pagination  toPagination (int first, int pageSize) {
        int start = resetPaginator ? 0 : (first/pageSize+1);
        resetPaginator = false;

        return new Page(start,pageSize);
    }

    @Override
    public String getRowKey(T item) {
        return "" + item.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getRowData(String rowKey) {
        try{
            for (T item : ((List<T>) getWrappedData())) {
            	if (rowKey.equals("" + item.hashCode()) || rowKey.hashCode()==item.hashCode()) {
                    return item;
                }
            }
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
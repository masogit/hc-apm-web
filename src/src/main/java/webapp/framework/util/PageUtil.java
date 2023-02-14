package webapp.framework.util;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.List;

public class PageUtil<T> {
    //将list转化为分页集合
    public Page<T> pageNum(List<T> list, PageRequest pageRequest) {
        int countSize = list.size();
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = list.size();
        if (size > numEnd && CollectionUtils.isNotEmpty(list)) {
            list = list.subList(numStart, numEnd);
        } else {
            if (size > numStart) {
                list = list.subList(numStart ,size);
            } else {
                list = new ArrayList<>();
            }
        }
        return new PageImpl<>(list, pageRequest, countSize);
    }
}

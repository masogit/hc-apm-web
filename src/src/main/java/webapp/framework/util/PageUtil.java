package webapp.framework.util;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.google.common.base.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> autoComplete(String query, List<OrgInfo> clinicalDeptList) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public double formatDouble2(double d) {
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }
}

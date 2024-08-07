package com.ge.apm.view.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrderESHistory;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class WorkOrderESHistoryController extends GenericCRUDUUIDController<WorkOrderESHistory> implements Serializable {

    private UserAccount currentUser;

    private ElasticService elasticService;
    private String accessToken ;

    private static final String WORK_ORDER_HISTORY_INDEX="work_order_history";

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
    }

    @Override
    protected Page<WorkOrderESHistory> loadData(PageRequest pageRequest) {
        Page<Object> result = elasticService.searchHistory(searchFilters,accessToken,pageRequest.getPageNumber(),pageRequest.getPageSize(),WORK_ORDER_HISTORY_INDEX);
        List<Object> content = result.getContent();
        if(CollectionUtils.isEmpty(content)){
            return new PageImpl<WorkOrderESHistory>(new ArrayList<>());
        }else{
            SerializeConfig config = new SerializeConfig();
            config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
            List<WorkOrderESHistory> data = content.stream().map(item -> {
                return JSON.parseObject(JSON.toJSONString(item, config), new TypeReference<WorkOrderESHistory>() {
                });
            }).collect(Collectors.toList());
            return new PageImpl<WorkOrderESHistory>(data,pageRequest,result.getTotalElements());
        }
    }


}

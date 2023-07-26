package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbUnrealRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.domain.ZkbUnreal;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2023/6/30
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbUnrealController extends GenericCRUDUUIDController<ZkbUnreal> {

    private ZkbPartOrderService zkbPartOrderService;
    private ZkbUnrealRepository zkbUnrealRepository;
    private UserAccount currentUser;
    private PageUtil<ZkbUnreal> pageUtil = new PageUtil<>();
    private List<String> testName ;

    @Override
    protected void init() {
        super.init();
        zkbUnrealRepository = WebUtil.getBean(ZkbUnrealRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        testName = zkbPartOrderService.getZkbTestName();
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<ZkbUnreal> getDAO() {
        return zkbUnrealRepository;
    }

    @Override
    protected Page<ZkbUnreal> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<ZkbUnreal> resultPage = zkbUnrealRepository.findBySearchFilter(searchFilters);
        List<ZkbUnreal> resultList = new ArrayList<ZkbUnreal>();
        if(CollectionUtils.isNotEmpty(resultPage)){
            resultList = resultPage.stream().filter(zkbUnreal ->
                    (StringUtils.isNotBlank(zkbUnreal.getCreatedBy())&& !testName.contains(zkbUnreal.getCreatedBy()))).collect(Collectors.toList());
        }
        return (currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1) ?
                pageUtil.pageNum(resultPage, pageRequest): pageUtil.pageNum(resultList, pageRequest);
    }



}

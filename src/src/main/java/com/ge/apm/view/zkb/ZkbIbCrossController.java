package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/6/30
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbIbCrossController extends GenericCRUDUUIDController<ZkbIbCrossRelationShipLog> {

    private ZkbIbCrossUserRepository zkbIbCrossUserRepository;
    private ZkbIbCrossUserStatusRepository zkbIbCrossUserStatusRepository;
    private ZkbIbCrossRelationShipRepository zkbIbCrossRelationShipRepository;
    private ZkbIbCrossRelationShipLogRepository zkbIbCrossRelationShipLogRepository;
    private TenantInfoRepository tenantInfoDao;
    private List<ZkbIbCrossUser> zkbIbCrossUserList = new ArrayList<>();
    private List<ZkbIbCrossUserStatus> zkbIbCrossUserStatusList = new ArrayList<>();
    private List<ZkbIbCrossRelationShip> zkbIbCrossRelationShipList = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        zkbIbCrossRelationShipLogRepository = WebUtil.getBean(ZkbIbCrossRelationShipLogRepository.class);
        zkbIbCrossUserRepository = WebUtil.getBean(ZkbIbCrossUserRepository.class);
        zkbIbCrossUserStatusRepository = WebUtil.getBean(ZkbIbCrossUserStatusRepository.class);
        zkbIbCrossRelationShipRepository = WebUtil.getBean(ZkbIbCrossRelationShipRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        zkbIbCrossUserList = zkbIbCrossUserRepository.find();
        zkbIbCrossUserStatusList = zkbIbCrossUserStatusRepository.find();
        zkbIbCrossRelationShipList = zkbIbCrossRelationShipRepository.find();

    }

    @Override
    protected Page<ZkbIbCrossRelationShipLog> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }


        return zkbIbCrossRelationShipLogRepository.findBySearchFilter(searchFilters, pageRequest);
    }


    @Override
    protected GenericRepositoryUUID<ZkbIbCrossRelationShipLog> getDAO() {
        return zkbIbCrossRelationShipLogRepository;
    }


    public TenantInfo getTenantInfo(String tenantInfoUID){
        return tenantInfoDao.findByuid(tenantInfoUID);
    }

    public List<ZkbIbCrossUser> getZkbIbCrossUserList() {
        return zkbIbCrossUserList;
    }

    public void setZkbIbCrossUserList(List<ZkbIbCrossUser> zkbIbCrossUserList) {
        this.zkbIbCrossUserList = zkbIbCrossUserList;
    }

    public List<ZkbIbCrossUserStatus> getZkbIbCrossUserStatusList() {
        return zkbIbCrossUserStatusList;
    }

    public void setZkbIbCrossUserStatusList(List<ZkbIbCrossUserStatus> zkbIbCrossUserStatusList) {
        this.zkbIbCrossUserStatusList = zkbIbCrossUserStatusList;
    }

    public List<ZkbIbCrossRelationShip> getZkbIbCrossRelationShipList() {
        return zkbIbCrossRelationShipList;
    }

    public void setZkbIbCrossRelationShipList(List<ZkbIbCrossRelationShip> zkbIbCrossRelationShipList) {
        this.zkbIbCrossRelationShipList = zkbIbCrossRelationShipList;
    }
}

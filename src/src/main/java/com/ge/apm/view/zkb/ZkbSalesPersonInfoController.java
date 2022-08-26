package com.ge.apm.view.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbSalesPersonInfoRepository;
import com.ge.apm.dao.ZkbSalesTenantAssociateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbSalesPersonInfoService;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class ZkbSalesPersonInfoController extends GenericCRUDUUIDController<ZkbSalesPersonInfo> {

    /**
     *
     */
    private static final long serialVersionUID = -1L;

    private ZkbSalesPersonInfoService zkbSalesPersonInfoService;

    private ZkbSalesPersonInfoRepository dao;

    private TenantInfoRepository tenantInfoDao;

    private ZkbSalesTenantAssociateRepository zkbSalesTenantAssociateRepository;

    private List<TenantInfo> tenantInfoList;

    private List<String> tenantInfoUids = new ArrayList<>();

    private String tenantInfoUid;

    @Override
    protected GenericRepositoryUUID<ZkbSalesPersonInfo> getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbSalesPersonInfoRepository.class);
        zkbSalesTenantAssociateRepository =  WebUtil.getBean(ZkbSalesTenantAssociateRepository.class);
        zkbSalesPersonInfoService = WebUtil.getBean(ZkbSalesPersonInfoService.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        tenantInfoList = zkbSalesPersonInfoService.getSourceTenantInfoList();

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.selected.setSparePartsSelection(true);
        this.selected.setProbeSelection(true);
    }

    public void prepareAssociateTenant() {
        tenantInfoUids = new ArrayList<String>();
        if (selected == null) {
            return;
        }
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = this.selected.getZkbSalesTenantAssociateList();// zkbSalesTenantAssociateRepository.getBySalesPersonId(selected.getId());
        zkbSalesTenantAssociateList.stream().forEach(a ->{
            tenantInfoUids.add(a.getTenantUid());
        });
    }

    public void saveSalesTenantAssociate() {
        if (selected == null) {
            return;
        }
        zkbSalesTenantAssociateRepository.deleteBySalesPersonId(this.selected.getId());
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociatesList = new ArrayList<ZkbSalesTenantAssociate>();
        tenantInfoUids.stream().forEach(uid -> {
            ZkbSalesTenantAssociate zkbSalesTenantAssociates = new ZkbSalesTenantAssociate();
            zkbSalesTenantAssociates.setTenantUid(uid);
            zkbSalesTenantAssociates.setZkbSalesPersonInfo(this.selected);
            zkbSalesTenantAssociates.setCreatedTime(new Date());
            zkbSalesTenantAssociates.setSalesPersonId(this.selected.getId());
            zkbSalesTenantAssociatesList.add(zkbSalesTenantAssociates);
         });
        zkbSalesTenantAssociateRepository.save(zkbSalesTenantAssociatesList);

    }
    @Override
    public void onAfterDeleteObject(ZkbSalesPersonInfo zkbSalesPersonInfo, boolean isOK) {
        /*if(isOK){
             List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList =  new ArrayList<>(); //zkbSalesTenantAssociateRepository.getBySalesPersonId(zkbSalesPersonInfo.getId());
            zkbSalesTenantAssociateRepository.delete(zkbSalesTenantAssociateList);
        }*/
    }

    public String getAssociateTenant(String salesPersonId){
        StringBuilder sb = new StringBuilder();
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList =  zkbSalesTenantAssociateRepository.findBySalesPersonId(salesPersonId); //zkbSalesTenantAssociateRepository.getBySalesPersonId(salesPersonId);
        Set<String> uidSet = new HashSet<>();
        zkbSalesTenantAssociateList.stream().forEach(a ->{
            uidSet.add(a.getTenantUid());
        });
        List<TenantInfo> tenantInfoList = tenantInfoDao.findByUidIn(uidSet);
        tenantInfoList.stream().forEach(t ->{
            sb.append(t.getName()).append(" ");
        });
        return sb.toString();
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<String> getTenantInfoUids() {
        return tenantInfoUids;
    }

    public String getTenantInfoUid() {
        return tenantInfoUid;
    }

    public void setTenantInfoUid(String tenantInfoUid) {
        this.tenantInfoUid = tenantInfoUid;
    }

    public void setTenantInfoUids(List<String> tenantInfoUids) {
        this.tenantInfoUids = tenantInfoUids;
    }
}

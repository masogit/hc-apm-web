package com.ge.apm.view.iot;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.RccEtlDeviceRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.RccEtlDevice;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 503266962
 * 物联绩效设备绑定
 */
@ManagedBean
@ViewScoped
public class IotDeviceBindController extends GenericCRUDController<RccEtlDevice> {


    private RccEtlDeviceRepository dao;
    private AssetInfoRepository assetInfoRepository;

    private String assetUid;

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(RccEtlDeviceRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
    }


    @Override
    protected GenericRepository<RccEtlDevice> getDAO() {
        return dao;
    }

    @Override
    protected Page<RccEtlDevice> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        Page<RccEtlDevice> list = dao.findBySearchFilter(searchFilters, pageRequest);
        return list;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    public List<AssetInfo> getAssetList() {
        if(this.selected == null){
            return new ArrayList<>();
        }
        return assetInfoRepository.getBySiteUid(this.selected.getOrgInfo().getSiteUID());
    }

    public void setIotAssetUid(){
        if(this.selected.getAssetInfo() == null){
            this.assetUid = null;
        }else{
            this.assetUid = this.selected.getAssetInfo().getUid();
        }
    }

    public void updateIotAsset(){
        if(StringUtils.isEmpty(this.assetUid)){
            WebUtil.addErrorMessage("请选择设备！");
            return;
        }
        dao.updateAssetById(this.selected.getId(), this.assetUid);
        this.selected = null;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }
}

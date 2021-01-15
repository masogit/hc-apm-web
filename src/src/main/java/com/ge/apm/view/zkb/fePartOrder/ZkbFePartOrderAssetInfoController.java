package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbFePartOrderAssetInfoRepository;
import com.ge.apm.domain.ZkbFePartOrderAssetInfo;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


/**
 * GEFE维修申请detail
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderAssetInfoController extends GenericCRUDUUIDController<ZkbFePartOrderAssetInfo> {


    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderAssetInfoRepository zkbFePartOrderAssetInfoRepository;

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderAssetInfoRepository = WebUtil.getBean(ZkbFePartOrderAssetInfoRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbFePartOrderAssetInfo> getDAO() {
        return zkbFePartOrderAssetInfoRepository;
    }
}

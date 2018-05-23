package com.ge.apm.view.inventory;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;

import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class InventoryAssetInfoController extends GenericCRUDController<AssetInfo> implements Serializable, ServerEventInterface {

	private AssetInfoRepository dao;
	private Integer assetInfoID;
	private AssetInfo assetInfo;

	@Override
	protected void init() {
		dao = WebUtil.getBean(AssetInfoRepository.class);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		assetInfoID = Integer.valueOf(request.getParameter("assetInfoID"));
	}

	@Override
	protected GenericRepository<AssetInfo> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<AssetInfo> loadData(PageRequest pageRequest) {
		return null;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {

	}

	public AssetInfo getAssetInfo() {
		if (assetInfo == null) {
			assetInfo = dao.findById(assetInfoID);
		}
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

}

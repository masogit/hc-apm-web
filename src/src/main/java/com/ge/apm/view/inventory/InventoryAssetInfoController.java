package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

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

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private static final String UID = "uid";
	private Integer assetInfoID;
	private AssetInfo assetInfo;
	private List<AssetInfo> listAssetInfo;

	@Override
	protected void init() {
		System.out.println("xxx init InventoryAssetInfoController");
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
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

	public List<AssetInfo> getListAssetInfo() {
		if (listAssetInfo == null) {
			listAssetInfo = new ArrayList<AssetInfo>();
			listAssetInfo.add(getAssetInfo());
		}
		return listAssetInfo;
	}

	public void setListAssetInfo(List<AssetInfo> listAssetInfo) {
		this.listAssetInfo = listAssetInfo;
	}

}

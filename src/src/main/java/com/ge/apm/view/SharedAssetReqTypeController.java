/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SharedAssetTypeRepository;
import com.ge.apm.domain.*;
import com.ge.apm.dao.SharedAssetReqTypeRepository;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.RowEditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetReqTypeController extends GenericCRUDUUIDController<SharedAssetReqType>{
    
    SharedAssetReqTypeRepository dao = null;
    SharedAssetTypeRepository typeDao = null;
    OrgInfoRepository orgInfoDao = null;
    private String sharedId;
    private Integer stateId = -1;
    
    @Override
    protected void init() {
        typeDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        dao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        sharedId = (String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if ("Create".equalsIgnoreCase(actionName)) {
            stateId = -2 ;
        }    
    }
    
    @Override
    protected SharedAssetReqTypeRepository getDAO() {
        return dao;
    }
    
    @Override
    protected Page<SharedAssetReqType> loadData(PageRequest pageRequest) {
        
        return dao.findBySharedId(pageRequest, sharedId);
    }
    
    public void onIouCreate(){
       
        stateId =-2;
    };
    
    public void cancelDialog(){
        showDialog = false;
    }
    
    
    private boolean showDialog = true;
    public boolean isShowDialog() {
        return showDialog;
    }
    
    public void creates(){
        
       showDialog = true;
    }
    
    public void onChanged(){
        stateId =-1;
        
        SharedAssetRequestV2Controller sharedController = WebUtil.getBean(SharedAssetRequestV2Controller.class);
        if(sharedController.getSelected()!=null){
                    
        }
       
    }
    
    @Override
    public List<SharedAssetReqType> getItemList() {
        return dao.find();
    }
    
    @Override
    public void onBeforeSave(SharedAssetReqType object) {
        SharedAssetRequestV2Controller sharedController = WebUtil.getBean(SharedAssetRequestV2Controller.class);
        
        object.setSharedId(sharedId);
        
        sharedController.save();
        String createId = sharedController.getSelected().getId();
        
        if(stateId == -2||stateId == 0){
            sharedId = createId;
        }
        object.setSharedId(sharedId);
        
        String sharedAssetTypeId = object.getSharedAssetTypeId();
        
        SharedAssetType vo = new SharedAssetType();
        vo = typeDao.findById(sharedAssetTypeId);
        object.setSwapName(vo.getSwapName());
        object.setExceptQty(0);
    }
    
    private String qtyId;
    private int returnqty;
    private int exceptqty;
        
    public void onCellEditRegist(RowEditEvent event) {
       qtyId=((SharedAssetReqType) event.getObject()).getId();
       returnqty=((SharedAssetReqType) event.getObject()).getReturnQty();
       exceptqty = ((SharedAssetReqType) event.getObject()).getExceptQty();

       SharedAssetReqType vo = new SharedAssetReqType();
       vo = dao.findById(qtyId);
       vo.setReturnQty(returnqty);
       vo.setExceptQty(exceptqty);
       
       dao.save(vo);
 
    }
    
    public int getSwapQty(){
        String Uid = this.selected.getSharedAssetTypeId();
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return 0;
        }else{
            List<AssetInfo> objectList = new ArrayList<AssetInfo>();
            UserAccount user = UserContextService.getCurrentUserAccount();
            objectList = typeDao.findbyBorrowQty(user.getOrgInfoUID(),Uid);
            return objectList.size();
        }
    }
    
    
}

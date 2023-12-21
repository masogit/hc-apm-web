package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private SupplierUserRepository supplierUserDao;
    @Autowired
    private UserAccountRepository userAccountDao;
    @Autowired
    private SupplierApiService supplierApiService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private SupplierQrCodeRepository supplierQrCodeDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;

    public List<String> getSupplier(String name) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> filter = new ArrayList<>();

        filter.add(new SearchFilter("manufacturerT", SearchFilter.Operator.EQ, true,"1*"));
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(),"1*"));
        if(StringUtils.isNotEmpty(name)){
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name.toLowerCase(),"1*2+"));
        }
        //return supplierDao.findByManufacturerTAndTenantUID(true,user.getTenantUID());
        List<Supplier> suppliers = supplierDao.findBySearchFilter(filter);
        List<String> names = new ArrayList<>(suppliers.size());
        suppliers.stream().forEach( s -> names.add(s.getName()));
        names.sort(new PinYinComparator());
        return names;
    }

    public List<UserAccount> getTempStaffList(String tenantUID){
        return userAccountDao.findByTenantUIDAndTempStaffRole(tenantUID);
    }

    public List<SupplierUser> getSupplierUserList(Integer supplierId, String status, String queryStr){

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        List<SearchFilter> searchFilterList = new ArrayList<>();

        searchFilterList.add(new SearchFilter("supplier.tenantUID", SearchFilter.Operator.EQ, currentUserAccount.getTenantUID(),"1*"));

        if(supplierId != null){
            searchFilterList.add(new SearchFilter("supplierID", SearchFilter.Operator.EQ, supplierId,"1*"));
        }

        if(StringUtils.isNotEmpty(status)){
            searchFilterList.add(new SearchFilter("status", SearchFilter.Operator.EQ, status,"1*"));
        }

        if(StringUtils.isNotEmpty(queryStr)){
            searchFilterList.add(new SearchFilter("supplier.name", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
            searchFilterList.add(new SearchFilter("userAccount.name", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
            searchFilterList.add(new SearchFilter("userAccount.telephone", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
        }

        return supplierUserDao.findBySearchFilter(searchFilterList);
    }

    public List<UserAccount> getApprovedUserList(Integer supplierId){
        List<SupplierUser> supplierUserList = supplierUserDao.findBySupplierIDAndStatus(supplierId, SupplierUser.Status.Approved.name());

        List<UserAccount> approvedUserList = new ArrayList<>();
        for (SupplierUser supplierUser : supplierUserList) {
            approvedUserList.add(supplierUser.getUserAccount());
        }

        return approvedUserList;
    }

    public void approve(SupplierUser supplierUser){
        supplierUser.setStatus(SupplierUser.Status.Approved.name());
        supplierUserDao.save(supplierUser);
    }

    public void approveByID(String id){
        SupplierUser supplierUser = supplierUserDao.findById(id);
        supplierUser.setStatus(SupplierUser.Status.Approved.name());
        supplierUserDao.save(supplierUser);
    }

    public void deleteByID(String id){
        supplierUserDao.delete(id);
    }

    @Transactional
    public void refuseByID(String id){
        SupplierUser supplierUser = supplierUserDao.findById(id);
        supplierUserDao.delete(id);
        //supplierUserDao.deleteUserWxApp(supplierUser.getUserAccount().getId());
        //userAccountDao.delete(supplierUser.getUserAccount());
    }

    @Transactional
    public void updateSupplierUserList(Supplier supplier, List<UserAccount> approvedUserList){

        supplierUserDao.deleteBySupplierIDAndStatus(supplier.getId(), SupplierUser.Status.Approved.name());

        List<Integer> userIDList = new ArrayList<>();
        for (UserAccount userAccount : approvedUserList) {
            SupplierUser supplierUser = new SupplierUser();
            supplierUser.setSupplierID(supplier.getId());
            supplierUser.setSupplierUID(supplier.getUid());
            supplierUser.setUserID(userAccount.getId());
            supplierUser.setUserUID(userAccount.getUid());
            supplierUser.setStatus(SupplierUser.Status.Approved.name());
            supplierUserDao.save(supplierUser);

            userIDList.add(userAccount.getId());
        }

        supplierUserDao.deleteBySupplierIDAndStatusAndUserIDIn(supplier.getId(), SupplierUser.Status.PendingApproval.name(), userIDList);

    }

    @Transactional
    public void saveSupplier(Supplier supplier) {
        supplierDao.save(supplier);

        assetInfoDao.updateSupplierName(supplier.getName(), supplier.getId(), supplier.getTenantUID());
        assetInfoDao.updateMaintainerName(supplier.getName(), String.valueOf(supplier.getId()), supplier.getTenantUID());
        assetInfoDao.updateManufactureName(supplier.getName(), String.valueOf(supplier.getId()), supplier.getTenantUID());

    }

    public byte[] getWxaCodeImage(){

        UserAccount user = UserContextService.getCurrentUserAccount();
        TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());

        SupplierQrCode supplierQrCode = supplierQrCodeDao.findFirstByTenantUIDAndType(tenantInfo.getUid(), SupplierQrCode.Type.Wxa.name());

        SupplierQrCode tempSupplierQrCode = new SupplierQrCode();

        if(supplierQrCode != null){

            if(supplierQrCode.getQrCode() != null){
                return supplierQrCode.getQrCode();
            }

            tempSupplierQrCode.setId(supplierQrCode.getId());

        }

        String token = UserContextService.getAccessToken();
        String path = "pages/index/index?tenantName="+ tenantInfo.getName() +"&tenantUID=" + tenantInfo.getUid();
        String appName ="hc-apm-supplier";

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("path", path);
        payloadMap.put("appName", appName);

        JSONObject payload = new JSONObject(payloadMap);

        byte [] wxaCodeByte = supplierApiService.getWxaCodeImage(token ,payload);

        if(this.isValidPic(wxaCodeByte)){

            tempSupplierQrCode.setTenantUID(tenantInfo.getUid());
            tempSupplierQrCode.setQrCode(wxaCodeByte);
            tempSupplierQrCode.setType(SupplierQrCode.Type.Wxa.name());

            supplierQrCodeDao.save(tempSupplierQrCode);

        }else {
            wxaCodeByte = null;
        }

        return wxaCodeByte;
    }

    public byte[] getWeChatCodeImage(){

        UserAccount user = UserContextService.getCurrentUserAccount();
        TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());

        SupplierQrCode supplierQrCode = supplierQrCodeDao.findFirstByTenantUIDAndType(tenantInfo.getUid(), SupplierQrCode.Type.WeChat.name());

        SupplierQrCode tempSupplierQrCode = new SupplierQrCode();

        if(supplierQrCode != null){

            if(supplierQrCode.getQrCode() != null){
                return supplierQrCode.getQrCode();
            }

            tempSupplierQrCode.setId(supplierQrCode.getId());

        }

        String token = UserContextService.getAccessToken();
        byte [] weChatCodeByte = supplierApiService.getWeChatQrCodeImage(token);

        if(this.isValidPic(weChatCodeByte)){

            tempSupplierQrCode.setTenantUID(tenantInfo.getUid());
            tempSupplierQrCode.setQrCode(weChatCodeByte);
            tempSupplierQrCode.setType(SupplierQrCode.Type.WeChat.name());

            supplierQrCodeDao.save(tempSupplierQrCode);

        }else {
            weChatCodeByte = null;
        }


        return weChatCodeByte;

    }

    public boolean
    activeSupplier(){
        UserAccount user = UserContextService.getCurrentUserAccount();

        WorkflowConfig configBySite = configService.findConfigBySite(user.getSiteUID());
        if(null != configBySite){
           return configBySite.getActiveSupplier() != null && configBySite.getActiveSupplier();
        }

        return false;

    }

    public boolean validateSupplierCode(Supplier supplier){

        UserAccount user = UserContextService.getCurrentUserAccount();

        List<Supplier> supplierList = new ArrayList<>();

        if (StringUtils.isNotEmpty(supplier.getSupplierCode())) {
            supplierList = supplierDao.findBySupplierCodeAndTenantUIDAndIdNot(supplier.getSupplierCode(), user.getTenantUID(), supplier.getId());
        }

        if(supplierList.isEmpty()){
            return true;
        }

        return false;
    }

    public boolean validateSupplierRegistrationCode(Supplier supplier){

        UserAccount user = UserContextService.getCurrentUserAccount();

        List<Supplier> supplierList = new ArrayList<>();

        if (StringUtils.isNotEmpty(supplier.getProductRegistrationCode())) {
            supplierList = supplierDao.findByProductRegistrationCodeAndTenantUIDAndIdNot(supplier.getProductRegistrationCode(), user.getTenantUID(), supplier.getId());
        }

        if(supplierList.isEmpty()){
            return true;
        }

        return false;
    }

    public boolean activeSupplierCertificateManage(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        WorkflowConfig configBySite = configService.findConfigBySite(user.getSiteUID());
        if(null != configBySite){
            return configBySite.getSupplierCertificateManage() != null && configBySite.getSupplierCertificateManage();
        }
        return false;
    }

    public boolean isValidPic(byte[] bts){

        if (bts == null || bts.length < 4){
            return false;
        }

        int len = bts.length;

        byte[] jpg = new byte[4];

        jpg[0] = -1;
        jpg[1] = -40;
        jpg[2] = -1;
        jpg[3] = -39;

        try{

            if (bts[0] == jpg[0] && bts[1] == jpg[1] && bts[len - 2] == jpg[2] && bts[len - 1] == jpg[3]){
                return true;
            }else {
                return false;
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

}

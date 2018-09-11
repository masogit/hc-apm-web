/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.google.common.base.Strings;

/**
 *
 * @author 212579464
 */
@Component
public class AutoCompleteService {

    
    public List<Supplier> autoCompleteVendor(String query,List<Supplier> supplierList) {
        List<Supplier> res = supplierList.stream().filter(item -> item.getSupplierT() != null && item.getSupplierT()).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<Supplier> autoCompleteMaintainer(String query,List<Supplier> supplierList) {

        List<Supplier> res = supplierList.stream().filter(item -> item.getMaintainerT() != null && item.getMaintainerT()).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public List<Supplier> autoCompleteManufacture(String query,List<Supplier> supplierList) {

        List<Supplier> res = supplierList.stream().filter(item -> item.getManufacturerT() != null && item.getManufacturerT()).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public List<UserAccount> completeOwnerList(String query,List<UserAccount> ownerList) {

    	if (ownerList == null) return Collections.emptyList();

        List<UserAccount> res = ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        //already in specified order, no need to order by pinyin
//        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public List<UserAccount> completeClinicalOwnerList(String query,List<UserAccount> clinicalOwnerList) {

        if (clinicalOwnerList == null) return Collections.emptyList();;

        List<UserAccount> res = clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    //for parent asset
    public List<AssetInfo> completeAssetInfoList(String query,List<AssetInfo> assetInfoList){
    	if(assetInfoList==null){
    		return Collections.emptyList();
    	}
    	List<AssetInfo> res=assetInfoList.stream().filter(item->Strings.isNullOrEmpty(query) || item.getName().contains(query) 
                || (item.getNamePyCode()==null?false:item.getNamePyCode().contains(query.toLowerCase())) 
                || item.getQrCode() != null && item.getQrCode().contains(query)
                || item.getFinancingNum() != null && item.getFinancingNum().contains(query)
                || (item.getDepartNum()!=null?item.getDepartNum().contains(query):true)).collect(Collectors.toList());
    	res.sort(new PinYinComparatorByName());
    	return res;
    }

}

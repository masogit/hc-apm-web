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

        List<Supplier> res = supplierList.stream().filter(item -> Arrays.asList("1","3").contains(item.getSupplierType())).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        Supplier obj=new Supplier();
        obj.setName("请选择");
        obj.setId(null);
        res.add(0,obj);
        return res;
    }

    public List<Supplier> autoCompleteMaintainer(String query,List<Supplier> supplierList) {

        List<Supplier> res = supplierList.stream().filter(item -> Arrays.asList("2","3").contains(item.getSupplierType())).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        Supplier obj=new Supplier();
        obj.setName("请选择");
        obj.setId(null);
        res.add(0,obj);
        return res;
    }
    
    public List<UserAccount> completeOwnerList(String query,List<UserAccount> ownerList) {

    	if (ownerList == null) return Collections.emptyList();

        List<UserAccount> res = ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        UserAccount user=new UserAccount();
        user.setName("请选择");
        user.setId(null);
        res.add(0, user);
        //already in specified order, no need to order by pinyin
//        res.sort(new PinYinComparatorByName());
        return res;
    }

}

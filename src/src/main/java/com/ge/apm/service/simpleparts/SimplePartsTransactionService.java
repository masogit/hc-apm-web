package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsTransactionRepository;
import com.ge.apm.domain.simpleparts.SimplePartsTransaction;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件库存的Service
 * @package com.ge.apm.service.spareparts
 */
@Service
public class SimplePartsTransactionService {

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    public static void addCurrentOrgInfo(SimplePartsTransaction partsTransaction) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        partsTransaction.setTenantUID(currentUserAccount.getTenantUID());
        partsTransaction.setInstitutionUID(currentUserAccount.getInstitutionUID());
        partsTransaction.setHospitalUID(currentUserAccount.getHospitalUID());
        partsTransaction.setSiteUID(currentUserAccount.getSiteUID());
        partsTransaction.setClinicalDeptUid(currentUserAccount.getClinicalDeptUID());
    }
}

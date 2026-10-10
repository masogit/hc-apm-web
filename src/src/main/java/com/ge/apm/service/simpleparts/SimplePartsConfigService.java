package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsConfigRepository;
import com.ge.apm.domain.simpleparts.SimplePartsConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SimplePartsConfigService {

    @Autowired
    private SimplePartsConfigRepository partsConfigRepository;

    @Transactional(readOnly = true)
    public boolean isEnableWarehouseLocator() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Optional<SimplePartsConfig> partsConfigOptional = partsConfigRepository.findByTenantUid(currentUserAccount.getTenantUID());

        if (!partsConfigOptional.isPresent()) {
            return false;
        }

        return partsConfigOptional.get().isEnableWarehouseLocator();
    }

    @Transactional(readOnly = true)
    public boolean isEnableWarehouseSelectionInRequest() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Optional<SimplePartsConfig> partsConfigOptional = partsConfigRepository.findByTenantUid(currentUserAccount.getTenantUID());

        if (!partsConfigOptional.isPresent()) {
            return false;
        }

        return partsConfigOptional.get().isEnableWarehouseSelectionInRequest() != null ? partsConfigOptional.get().isEnableWarehouseSelectionInRequest() : false;
    }

    @Transactional(readOnly = true)
    public boolean isEnableIntegrationWithWorkOrder() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Optional<SimplePartsConfig> partsConfigOptional = partsConfigRepository.findByTenantUid(currentUserAccount.getTenantUID());

        if (!partsConfigOptional.isPresent()) {
            return false;
        }

        return partsConfigOptional.get().isEnableIntegrationWithWorkOrder();
    }

    @Transactional(readOnly = true)
    public boolean isEnableTenantLocal() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Optional<SimplePartsConfig> partsConfigOptional = partsConfigRepository.findByTenantUid(currentUserAccount.getTenantUID());

        if (!partsConfigOptional.isPresent()) {
            return false;
        }

        return partsConfigOptional.get().isEnableTenantLocal();
    }
}

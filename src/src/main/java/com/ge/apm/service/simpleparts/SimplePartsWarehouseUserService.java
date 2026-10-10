package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseUserRepository;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseUser;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: kwg
 * @date: 2020/7/13 13:51
 * @version: 1.0
 */
@Service
public class SimplePartsWarehouseUserService {

    private static final Logger logger = Logger.getLogger(SimplePartsWarehouseUserService.class);

    @Autowired
    private SimplePartsWarehouseUserRepository partsWarehouseUserRepository;

    @Autowired
    private SimplePartsWarehouseRepository partsWarehouseRepository;

    @Transactional(readOnly = true)
    public List<String> findAllByPartsWarehouseByUserUid(String userUid) {
        return partsWarehouseUserRepository.findAllByPartsWarehouseByUserUid(userUid);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> queryByUserUid() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        if (userAccount == null) {
            logger.info("当用登录用户信息为空");
            return null;
        }
        List<SimplePartsWareHouseUser> list = partsWarehouseUserRepository.findAllByUserUid(userAccount.getUid());
        if (list != null && list.size() > 0) {
            Set<String> setIds = list.stream().filter(item -> item.getPartsWarehouseId() != null).map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());
            if (setIds != null && setIds.size() > 0) {
                UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
                return partsWarehouseRepository.getByIds(currentUserAccount.getTenantUID(),setIds);
            }
        }
        logger.info("当前登录用户未查询到管理的仓库");
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void savePartsWarehouseUser(String id, List<UserAccount> selectUserAccountList) {
        partsWarehouseUserRepository.deleteByPartsWarehouseId(id);
        if (selectUserAccountList != null && selectUserAccountList.size() > 0) {
            List<SimplePartsWareHouseUser> saveList = Lists.newArrayList();
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            for (UserAccount user : selectUserAccountList) {
                SimplePartsWareHouseUser entity = new SimplePartsWareHouseUser();
                entity.setUserUid(user.getUid());
                entity.setPartsWarehouseId(id);
                entity.setCreatedDate(new Date());
                entity.setCreatedBy(userAccount.getName());
                saveList.add(entity);
            }
            partsWarehouseUserRepository.save(saveList);
        }
    }
}

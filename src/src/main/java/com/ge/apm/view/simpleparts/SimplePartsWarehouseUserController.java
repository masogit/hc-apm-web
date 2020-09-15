package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseUserRepository;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseUser;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseUserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: csh
 * @date: 2020/7/13 13:55
 * @version: 1.0
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsWarehouseUserController extends GenericCRUDUUIDController<SimplePartsWarehouse>  implements ServerEventInterface {
    private static final long serialVersionUID = 1886024138737071702L;

    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseUserRepository partsWarehouseUserRepository;
    private SimplePartsWarehouseUserService partsWarehouseUserService;
    private SysRoleRepository roleDao;
    private UserAccountRepository userDao;
    private UserRoleRepository userRoleRepository;

    @Getter
    @Setter
    private List<UserAccount> userAccountList;

    @Getter
    @Setter
    private List<UserAccount> selectUserAccountList;

    @Getter
    @Setter
    public SimplePartsWareHouseUser partsWareHouseUser;

    @Getter
    @Setter
    public List<SimplePartsWareHouseUser> partsWareHouseUserList;

    @Override
    protected void init() {
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseUserService = WebUtil.getBean(SimplePartsWarehouseUserService.class);
        partsWarehouseUserRepository = WebUtil.getBean(SimplePartsWarehouseUserRepository.class);
        userRoleRepository = WebUtil.getBean(UserRoleRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        roleDao = WebUtil.getBean(SysRoleRepository.class);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selected = (SimplePartsWarehouse) eventObject;
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsWarehouse> getDAO() {
        return partsWarehouseRepository;
    }

    /**
     * 初始化新增仓库管理员页面
     */
    public void initAddPartsWarehouseUser(String uid) {
        try {
            SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.getById(uid);
            if (partsWarehouse == null) {
                WebUtil.addSuccessMessage("仓库不存在");
                return;
            }

            selected = partsWarehouse;
            partsWareHouseUserList = partsWarehouseUserRepository.findAllByPartsWarehouseId(partsWarehouse.getId());

            Set<String> uidSets = partsWareHouseUserList.stream().filter(item -> item.getUserUid() != null).map(item -> item.getUserUid()).collect(Collectors.toSet());
            if (uidSets != null && uidSets.size() != 0) {
                selectUserAccountList = userDao.getByUids(uidSets);
            } else {
                selectUserAccountList = Lists.newArrayList();
            }
            SysRole role = roleDao.getByName(SimplePartsConstant.PARTS_ADMIN_MANAGER);
            if (role != null) {
                Set set = Sets.newHashSet();
                set.add(role.getId());
                Set<Integer> userIds = userRoleRepository.findUserIdByRoleIds(set);
                if (userIds != null && userIds.size() > 0) {
                    userAccountList = userDao.getByIds(userIds);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    /**
     * 新增仓库管理员页面
     */

    public void savePartsWarehouseUser(String uid) {
        SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.getById(uid);
        if (partsWarehouse == null) {
            WebUtil.addSuccessMessage("仓库不存在");
            return;
        }
        partsWarehouseUserService.savePartsWarehouseUser(partsWarehouse.getId(),selectUserAccountList);
    }

}

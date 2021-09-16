package com.ge.apm.view.sysutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class WorkOrderUpdateController extends GenericCRUDUUIDController<AssetLifecycleEvent> {

	private static final long serialVersionUID = 1L;

	AssetLifecycleEventRepository lifecycleDao;

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return lifecycleDao;
    }

    @Override
	protected void init() {
        lifecycleDao = WebUtil.getBean(AssetLifecycleEventRepository.class);
	}

    public void updateStatus() {

        // 查询数据不一致的工单
        List<AssetLifecycleEvent> incorrectedEvents = lifecycleDao.getIncorrectedEvent();

        for (int i = 0; i < incorrectedEvents.size(); i++) {
            AssetLifecycleEvent assetLifecycleEvent = incorrectedEvents.get(i);
            String description = assetLifecycleEvent.getDescription();
            JSONObject jsonObj = JSON.parseObject(description);
            jsonObj.replace("title","待关单","已关单");
            jsonObj.replace("status",6,7);
            String s = JSON.toJSONString(jsonObj);
            assetLifecycleEvent.setDescription(s);
            assetLifecycleEvent.setStatus(2);
        }
        lifecycleDao.save(incorrectedEvents);
    }

}
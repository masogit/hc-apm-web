package com.ge.apm.view.pm;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.tiefaces.components.websheet.TieWebSheetBean;

import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.service.pm.InventoryOrderService;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class InventoryOrderController extends TieWebSheetBean {

	private InventoryOrderService service;
	public InventoryOrderController() {

	}

	@Override
	public void initialLoad() {
		service = WebUtil.getBean(InventoryOrderService.class);
		System.out.println("xxx service");
		System.out.println(service);
		List<InventoryOrder> listInventoryOrder = service.findSomeTestRecords();
		System.out.println("listInventoryOrder");
		System.out.println(listInventoryOrder.toString());
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("listInventoryOrder", listInventoryOrder);
		try {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("inventoryOrder.xlsx");
			loadWebSheet(stream, context);
		} catch (Exception e) {
			// TODO: handle exception
			WebUtil.addErrorMessage(e, "读取模版失败");
		}
	}
}

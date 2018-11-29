package com.ge.apm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;

public class TenantInfoConfig {
	private String edition;
	private List<TenantInfoConfigItem> menuList;
	private TreeNode root;
	private static List<TenantInfoConfigItem> listOriginVersionInfo;
	private static List<String> listVersionName;
	public static final String VERSION_FUNCTION = "business";
	public static final String VERSION_COMMON = "common";
	public static final String VERSION_ULTRASOUND = "ultrasound";
	public static final String TREE_TYPE = "tenantConfigTree";
	private static Map<String, String> mapVersion;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(root==null){
			return "{\"edition\":\"" + edition + "\",\"menuList\":" + parseNodeToList().toString() + "}";
		}
		else{
			StringBuilder sb=new StringBuilder();
			String result=readTreeNode(root, sb);
			if(result.length()>0){
				result=result.substring(0, result.length()-1);
			}
			return "{\"edition\":\"" + edition + "\",\"menuList\":[" + result + "]}";
		}
	}

	private TenantInfoConfig() {
	}
	private String readTreeNode(TreeNode root,StringBuilder sb){
		if(root.isSelected()){
			TenantInfoConfigItem tempItem=(TenantInfoConfigItem)root.getData();
			sb.append("\""+tempItem.getKey()+"\",");
		}
		for(TreeNode node:root.getChildren()){
			readTreeNode(node, sb);
		}
		return sb.toString();
	}
	private List<TenantInfoConfigItem> parseNodeToList(){
		List<TenantInfoConfigItem> tempList=new ArrayList<TenantInfoConfigItem>();
		for(int i=0;i<menuList.size();++i){
			TenantInfoConfigItem tempItem=menuList.get(i);
			if(tempItem.isValue()){
				tempList.add(tempItem);
			}
		}
		return tempList;
	}

	private void resetVersion() {
		if (StringUtils.isEmpty(edition)) {
			return;
		}
		for (int i = 0; i < menuList.size(); ++i) {
			TenantInfoConfigItem tempItem = menuList.get(i);
			tempItem.setValue(true);
			String key = tempItem.getKey();
			if (edition.equals(VERSION_COMMON)) {
			} else if (edition.equals(VERSION_ULTRASOUND) || edition.equals(VERSION_FUNCTION)) {
				if (key.equals("repair.workSchedule")) {
					tempItem.setValue(false);
				}
				if (key.equals("assetAdjust")) {
					tempItem.setValue(false);
				}
				if (key.contains("assetAdjust.")) {
					tempItem.setValue(false);
				}
				if (key.equals("nurseSite")) {
					tempItem.setValue(false);
				}
				if (key.contains("nurseSite.")) {
					tempItem.setValue(false);
				}
			}
		}
		if(root!=null){
			buildRoot();
		}
	}
	private void buildRoot() {
		List<TenantInfoConfigItem> tempMenuList=new ArrayList<TenantInfoConfigItem>(menuList);
		root = new DefaultTreeNode("root",null, null);
		buildChildren(root,tempMenuList);
	}
	
	private void buildChildren(TreeNode root,List<TenantInfoConfigItem> tempMenuList){
		List<String> headers=new ArrayList<String>();
		Map<String,DefaultTreeNode> headerItem=new HashMap<String,DefaultTreeNode>();
		for(int i=0;i>-1 && i<tempMenuList.size();++i){
			TenantInfoConfigItem tempItem = tempMenuList.get(i);
			String childKey=tempItem.getKey();
			if(!childKey.contains(".")){
				DefaultTreeNode tempNode=new DefaultTreeNode(TREE_TYPE,tempItem,root);
				tempNode.setExpanded(true);
				if(tempItem.isValue()){
					tempNode.setSelected(true);
				}
				headerItem.put(childKey, tempNode);
				headers.add(childKey);
				tempMenuList.remove(i);
				--i;
			}
		}
		for(int i=0;i>-1 && i<tempMenuList.size();++i){
			TenantInfoConfigItem tempItem = tempMenuList.get(i);
			String childKey=tempItem.getKey();
			int index=childKey.lastIndexOf(".");
			if(index>-1){
				String parentKey=childKey.substring(0,index);
				childKey=childKey.substring(index+1, childKey.length());
				if(headers.contains(parentKey)){
					DefaultTreeNode tempNode=new DefaultTreeNode(TREE_TYPE,tempItem,headerItem.get(parentKey));
					if(tempItem.isValue()){
						tempNode.setSelected(true);
					}
				}
				tempMenuList.remove(i);
				--i;
			}
		}
	}
	
	

	public static TenantInfoConfig build() {
		TenantInfoConfig tempVersionInfo = new TenantInfoConfig();
		tempVersionInfo.menuList = getListVersions();
		tempVersionInfo.edition = VERSION_COMMON;
		return tempVersionInfo;
	}

//	 public static void main(String[] args) {
//	 TenantInfoConfig tempConfig = TenantInfoConfig.build();
//	 System.out.println(tempConfig.getJSONStr());
//	 tempConfig.setEdition(VERSION_FUNCTION);
//	 System.out.println(tempConfig.getJSONStr());
//	 tempConfig=TenantInfoConfig.parseStr(tempConfig.getJSONStr());
//	 System.out.println(tempConfig.getJSONStr());
//	 }

	public static TenantInfoConfig parseStr(String str) {
		if (StringUtils.isEmpty(str)) {
			return build();
		}
		TenantInfoConfig tempVersionInfo = new TenantInfoConfig();
		JSONObject jsonObj = JSONObject.parseObject(str);
		tempVersionInfo.edition = jsonObj.getString("edition");
		JSONArray arr = jsonObj.getJSONArray("menuList");
		List<TenantInfoConfigItem> tempListVersionItem = new ArrayList<TenantInfoConfigItem>();
		Map<String, String> mapKeyLabel = getMapVersionKeyLabel();
		List<String> listItemKey = new ArrayList<String>();
		try {
			for (int i = 0; i < arr.size(); ++i) {
				String key = arr.getString(i);
				String label = mapKeyLabel.get(key);
				if (StringUtils.isEmpty(label)) {
					continue;
				}
				listItemKey.add(key);
				TenantInfoConfigItem tempItem = new TenantInfoConfigItem(label, key, true);
				tempListVersionItem.add(tempItem);
			}
			Iterator<String> it = mapKeyLabel.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String label = mapKeyLabel.get(key);
				if (!listItemKey.contains(key)) {
					TenantInfoConfigItem tempItem = new TenantInfoConfigItem(label, key, false);
					tempListVersionItem.add(tempItem);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			tempListVersionItem = getListVersions();
		}
		tempVersionInfo.menuList = tempListVersionItem;
		return tempVersionInfo;
	}

	public String getJSONStr() {
		return toString();
	}
	
	public void onSelectTenantConfig(NodeSelectEvent event){
		setTreeNodes(event.getTreeNode(), true);
	}
	
	public void onUnSelectTenantConfig(NodeUnselectEvent event){
        setTreeNodes(event.getTreeNode(), false);
	}
	private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }
	

	public static List<String> getListVersionName() {
		if (listVersionName == null) {
			listVersionName = new ArrayList<String>();
			listVersionName.add(VERSION_FUNCTION);
			listVersionName.add(VERSION_COMMON);
			listVersionName.add(VERSION_ULTRASOUND);
		}
		return listVersionName;
	}

	private static List<TenantInfoConfigItem> getListVersions() {
		Map<String, String> mapKeyLabel = getMapVersionKeyLabel();
		if (listOriginVersionInfo == null) {
			listOriginVersionInfo = new ArrayList<TenantInfoConfigItem>();
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("homePage"), "homePage", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset"), "asset", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.info"), "asset.info", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.analysis"), "asset.analysis", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.mgt"), "asset.mgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.supplierMgt"), "asset.supplierMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.scrap"), "asset.scrap", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.create"), "asset.create", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.audit"), "asset.audit", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.batchImportData"), "asset.batchImportData", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("asset.inventory"), "asset.inventory", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust"), "assetAdjust", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.borrowApply"), "assetAdjust.borrowApply", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.borrowAccept"), "assetAdjust.borrowAccept", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.returnApply"), "assetAdjust.returnApply", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.returnAccept"), "assetAdjust.returnAccept", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.batch"), "assetAdjust.batch", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.cost"), "assetAdjust.cost", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.stock"), "assetAdjust.stock", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.config"), "assetAdjust.config", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("assetAdjust.nameConfig"), "assetAdjust.nameConfig", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("install"), "install", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("install.list"), "install.list", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("install.store"), "install.store", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("install.check"), "install.check", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair"), "repair", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.workbench"), "repair.workbench", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.message"), "repair.message", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.report"), "repair.report", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.workSchedule"), "repair.workSchedule", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.vacationMgt"), "repair.vacationMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("repair.reportMgt"), "repair.reportMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain"), "maintain", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.workbench"), "maintain.workbench", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.rule"), "maintain.rule", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.assetMgt"), "maintain.assetMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.electricDataImport"), "maintain.electricDataImport", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.reportMgt"), "maintain.reportMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("maintain.reportMgt"), "maintain.reportMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("measure"), "measure", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("measure.categoryConfig"), "measure.categoryConfig", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("measure.info"), "measure.info", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("nurseSite"), "nurseSite", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("nurseSite.maintain"), "nurseSite.maintain", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("nurseSite.config"), "nurseSite.config", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("nurseSite.report"), "nurseSite.report", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("knowledge"), "knowledge", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("knowledge.mgt"), "knowledge.mgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance"), "performance", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance.scan"), "performance.scan", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance.CT"), "performance.CT", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance.config"), "performance.config", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance.import"), "performance.import", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("performance.BI"), "performance.BI", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config"), "config", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.tenant"), "config.tenant", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.org"), "config.org", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.user"), "config.user", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.batchImportData"), "config.batchImportData", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.assetMessage"), "config.assetMessage", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.qrCode"), "config.qrCode", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.qrCodeMgt"), "config.qrCodeMgt", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.repairOwner"), "config.repairOwner", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.assetMark"), "config.assetMark", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.engineerGroup"), "config.engineerGroup", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.workFlow"), "config.workFlow", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.custom"), "config.custom", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.attribute"), "config.attribute", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.repairMessage"), "config.repairMessage", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.captureDataMessage"), "config.captureDataMessage", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.mvs"), "config.mvs", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.info"), "config.info", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("config.purchase"), "config.purchase", true));
		}
		return listOriginVersionInfo;
	}

	private static Map<String, String> getMapVersionKeyLabel() {
		if (mapVersion == null) {
			mapVersion = new HashMap<String, String>();
			mapVersion.put("homePage", "我的主页");
			mapVersion.put("asset", "设备档案");
			mapVersion.put("asset.info", "基本信息");
			mapVersion.put("asset.analysis", "设备分布分析");
			mapVersion.put("asset.mgt", "合同管理");
			mapVersion.put("asset.supplierMgt", "供应商管理");
			mapVersion.put("asset.scrap", "报废资产");
			mapVersion.put("asset.create", "新增资产建档");
			mapVersion.put("asset.audit", "资产审核");
			mapVersion.put("asset.batchImportData", "批量导入资产数据");
			mapVersion.put("asset.inventory", "盘点工单");
			mapVersion.put("assetAdjust", "设备调剂");
			mapVersion.put("assetAdjust.borrowApply", "借用申请");
			mapVersion.put("assetAdjust.borrowAccept", "借用受理");
			mapVersion.put("assetAdjust.returnApply", "归还申请");
			mapVersion.put("assetAdjust.returnAccept", "归还受理");
			mapVersion.put("assetAdjust.batch", "设备批量调剂");
			mapVersion.put("assetAdjust.cost", "设备计费情况");
			mapVersion.put("assetAdjust.stock", "设备库存情况");
			mapVersion.put("assetAdjust.config", "调剂设备配置");
			mapVersion.put("assetAdjust.nameConfig", "调剂名称设置");
			mapVersion.put("install", "到货安装");
			mapVersion.put("install.list", "到货清单");
			mapVersion.put("install.store", "安装入库");
			mapVersion.put("install.check", "核对入库");
			mapVersion.put("repair", "维修流程");
			mapVersion.put("repair.workbench", "维修工作台");
			mapVersion.put("repair.message", "设备消息订阅");
			mapVersion.put("repair.report", "维修工单报表");
			mapVersion.put("repair.workSchedule", "排班管理");
			mapVersion.put("repair.vacationMgt", "休假管理");
			mapVersion.put("repair.reportMgt", "报表模版管理");
			mapVersion.put("maintain", "预防维护");
			mapVersion.put("maintain.workbench", "保养工作台");
			mapVersion.put("maintain.rule", "保养规则");
			mapVersion.put("maintain.assetMgt", "资产规则管理");
			mapVersion.put("maintain.electricDataImport", "电气安全数据导入");
			mapVersion.put("maintain.reportMgt", "报告模版管理");
			mapVersion.put("maintain.reportMgt", "报表模版管理");
			mapVersion.put("measure", "计量管理");
			mapVersion.put("measure.categoryConfig", "计量类别设置");
			mapVersion.put("measure.info", "计量信息");
			mapVersion.put("nurseSite", "护士站");
			mapVersion.put("nurseSite.maintain", "日常维护");
			mapVersion.put("nurseSite.config", "日常维护设置");
			mapVersion.put("nurseSite.report", "日常维护报表");
			mapVersion.put("knowledge", "知识文档");
			mapVersion.put("knowledge.mgt", "文档管理");
			mapVersion.put("performance", "绩效分析");
			mapVersion.put("performance.scan", "状态监控");
			mapVersion.put("performance.CT", "CT球管预测");
			mapVersion.put("performance.config", "设备绩效配置");
			mapVersion.put("performance.import", "设备绩效导入");
			mapVersion.put("performance.BI", "BI绩效分析平台");
			mapVersion.put("config", "系统配置");
			mapVersion.put("config.tenant", "租户管理");
			mapVersion.put("config.org", "组织机构管理");
			mapVersion.put("config.user", "用户管理");
			mapVersion.put("config.batchImportData", "批量数据导入");
			mapVersion.put("config.assetMessage", "设备消息订阅");
			mapVersion.put("config.qrCode", "二维码管理");
			mapVersion.put("config.qrCodeMgt", "二维码模版管理");
			mapVersion.put("config.repairOwner", "维修责任人设置");
			mapVersion.put("config.assetMark", "设备标签");
			mapVersion.put("config.engineerGroup", "维修工程师群组");
			mapVersion.put("config.workFlow", "流程配置项");
			mapVersion.put("config.custom", "自定义配置");
			mapVersion.put("config.attribute", "属性配置");
			mapVersion.put("config.repairMessage", "维修消息配置");
			mapVersion.put("config.captureDataMessage", "小屏消息配置");
			mapVersion.put("config.mvs", "MVS设备管理");
			mapVersion.put("config.info", "基本信息");
			mapVersion.put("config.purchase", "采购管理");
		}
		return mapVersion;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		if(!edition.equals(this.edition)){
			this.edition = edition;
			resetVersion();
		}
	}

	public TreeNode getRoot() {
		if(root==null){
			buildRoot();
		}
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

}
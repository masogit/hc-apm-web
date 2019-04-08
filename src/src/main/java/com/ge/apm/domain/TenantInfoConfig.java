package com.ge.apm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	// private static Map<String, String> mapVersion;
	private static List<String> listKey;
	private static List<String> listLabel;
	private TreeNode[] selectedNodes;
	private static Object lockObj = new Object();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (root == null) {
			return "{\"edition\":\"" + edition + "\",\"menuList\":" + parseNodeToList().toString() + "}";
		} else {
			StringBuilder sb = new StringBuilder();
			readTreeNode(root, sb);
			String result = sb.toString();
			if (result.length() > 0) {
				result = result.substring(0, result.length() - 1);
			}
			return "{\"edition\":\"" + edition + "\",\"menuList\":[" + result + "]}";
		}
	}

	private TenantInfoConfig() {
	}

	private void readTreeNode(TreeNode root, StringBuilder sb) {
		if (root.isSelected()) {
			TenantInfoConfigItem tempItem = (TenantInfoConfigItem) root.getData();
			if (tempItem != null) {
				sb.append("\"" + tempItem.getKey() + "\",");
			}
		}
		for (TreeNode node : root.getChildren()) {
			readTreeNode(node, sb);
		}
	}

	private String readTreeNode(StringBuilder sb) {
		if (selectedNodes != null && selectedNodes.length > 0) {
			for (int i = 0; i < selectedNodes.length; ++i) {
				TreeNode tempNode = selectedNodes[i];
				TenantInfoConfigItem tempItem = (TenantInfoConfigItem) tempNode.getData();
				sb.append("\"" + tempItem.getKey() + "\",");
			}
		}
		return sb.toString();
	}

	private List<TenantInfoConfigItem> parseNodeToList() {
		List<TenantInfoConfigItem> tempList = new ArrayList<TenantInfoConfigItem>();
		for (int i = 0; i < menuList.size(); ++i) {
			TenantInfoConfigItem tempItem = menuList.get(i);
			if (tempItem.isValue()) {
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
			} else if (edition.equals(VERSION_ULTRASOUND)) {
				if (key.equals("repair.workSchedule")) {
					tempItem.setValue(false);
				} else if (key.equals("assetAdjust")) {
					tempItem.setValue(false);
				} else if (key.contains("assetAdjust.")) {
					tempItem.setValue(false);
				} else if (key.equals("nurseSite")) {
					tempItem.setValue(false);
				} else if (key.contains("nurseSite.")) {
					tempItem.setValue(false);
				}
			} else if (edition.equals(VERSION_FUNCTION)) {
				if (key.equals("repair.workSchedule")) {
					tempItem.setValue(false);
				} else if (key.equals("assetAdjust")) {
					tempItem.setValue(false);
				} else if (key.contains("assetAdjust.")) {
					tempItem.setValue(false);
				} else if (key.equals("nurseSite")) {
					tempItem.setValue(false);
				} else if (key.contains("nurseSite.")) {
					tempItem.setValue(false);
				} else if (key.equals("repair.vacationMgt")) {
					tempItem.setValue(false);
				} else if (key.equals("maintain.electricDataImport")) {
					tempItem.setValue(false);
				}
			}
			if (key.equals("examineMgt")) {
				tempItem.setValue(false);
			} else if (key.contains("examineMgt.")) {
				tempItem.setValue(false);
			}
		}
		if (root != null) {
			buildRoot();
		}
	}

	private void buildRoot() {
		List<TenantInfoConfigItem> tempMenuList = new ArrayList<TenantInfoConfigItem>(menuList);
		root = new DefaultTreeNode("root", null, null);
		buildChildren(root, tempMenuList);
	}

	private void buildChildren(TreeNode root, List<TenantInfoConfigItem> tempMenuList) {
		List<String> headers = new ArrayList<String>();
		Map<String, DefaultTreeNode> headerItem = new HashMap<String, DefaultTreeNode>();
		for (int i = 0; i > -1 && i < tempMenuList.size(); ++i) {
			TenantInfoConfigItem tempItem = tempMenuList.get(i);
			String childKey = tempItem.getKey();
			if (!childKey.contains(".")) {
				DefaultTreeNode tempNode = new DefaultTreeNode(TREE_TYPE, tempItem, root);
				tempNode.setExpanded(true);
				tempNode.setSelectable(true);
				if (tempItem.isValue()) {
					tempNode.setSelected(true);
				} else {
					tempNode.setSelected(false);
				}
				headerItem.put(childKey, tempNode);
				headers.add(childKey);
				tempMenuList.remove(i);
				--i;
			}
		}
		for (int i = 0; i > -1 && i < tempMenuList.size(); ++i) {
			TenantInfoConfigItem tempItem = tempMenuList.get(i);
			String childKey = tempItem.getKey();
			int index = childKey.lastIndexOf(".");
			if (index > -1) {
				String parentKey = childKey.substring(0, index);
				childKey = childKey.substring(index + 1, childKey.length());
				if (headers.contains(parentKey)) {
					DefaultTreeNode tempNode = new DefaultTreeNode(TREE_TYPE, tempItem, headerItem.get(parentKey));
					if (tempItem.isValue()) {
						tempNode.setSelected(true);
					} else {
						tempNode.setSelected(false);
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
		tempVersionInfo.setEdition(VERSION_COMMON);
		return tempVersionInfo;
	}

	// public static void main(String[] args) {
	// TenantInfoConfig tempConfig = TenantInfoConfig.build();
	// tempConfig.setEdition(VERSION_FUNCTION);
	// tempConfig=TenantInfoConfig.parseStr(tempConfig.getJSONStr());
	// }
	public static TenantInfoConfig parseStr(String str) {
		if(listKey==null){
			initKeyLabel();
		}
		if (StringUtils.isEmpty(str)) {
			return build();
		}
		TenantInfoConfig tempVersionInfo = new TenantInfoConfig();
		JSONObject jsonObj = JSONObject.parseObject(str);
		tempVersionInfo.edition = jsonObj.getString("edition");
		JSONArray arr = jsonObj.getJSONArray("menuList");
		List<TenantInfoConfigItem> tempListVersionItem = new ArrayList<TenantInfoConfigItem>();
		Set<String> setItemKey = new HashSet<String>();
		try {
			for (int i = 0; i < arr.size(); ++i) {
				String key = arr.getString(i);
				if (!listKey.contains(key)) {
					continue;
				}
				setItemKey.add(key);
			}
			for(int i=0;i<listKey.size();++i){
				String key=listKey.get(i);
				String label=listLabel.get(i);
				if (!setItemKey.contains(key)) {
					TenantInfoConfigItem tempItem = new TenantInfoConfigItem(label, key, false);
					tempListVersionItem.add(tempItem);
				} else {
					TenantInfoConfigItem tempItem = new TenantInfoConfigItem(label, key, true);
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

	private void updateParentNode(TreeNode parentNode) {
		if (parentNode == null) {
			return;
		}
		boolean result = false;
		for (TreeNode item : parentNode.getChildren()) {
			result = item.isSelected();
			if (result) {
				break;
			}
		}
		parentNode.setSelected(result);
	}

	public void onSelectTenantConfig(NodeSelectEvent event) {
		TreeNode tempNode = event.getTreeNode();
		setTreeNodes(tempNode, true);
		updateParentNode(tempNode.getParent());
	}

	public void onUnSelectTenantConfig(NodeUnselectEvent event) {
		TreeNode tempNode = event.getTreeNode();
		setTreeNodes(tempNode, false);
		updateParentNode(tempNode.getParent());
	}

	private void setTreeNodes(TreeNode root, Boolean isSelected) {
		if (root == null) {
			return;
		}
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
		if(listKey==null){
			initKeyLabel();
		}
		if (listOriginVersionInfo == null) {
			listOriginVersionInfo = new ArrayList<TenantInfoConfigItem>();
			for(int i=0;i<listKey.size();++i){
				String key=listKey.get(i);
				String label=listLabel.get(i);
				listOriginVersionInfo.add(new TenantInfoConfigItem(label, key, true));
			}
		}
		return listOriginVersionInfo;
	}

	private static void initKeyLabel() {
			synchronized (lockObj) {
				if (listKey == null) {
				listKey = new ArrayList<String>();
				listLabel = new ArrayList<String>();
				listKey.add("homePage");
				listLabel.add("我的主页");
				listKey.add("asset");
				listLabel.add("设备档案");
				listKey.add("asset.info");
				listLabel.add("基本信息");
				listKey.add("asset.analysis");
				listLabel.add("设备分布分析");
				listKey.add("asset.mgt");
				listLabel.add("合同管理");
				listKey.add("asset.supplierMgt");
				listLabel.add("供应商管理");
				listKey.add("asset.scrap");
				listLabel.add("报废资产");
				listKey.add("asset.create");
				listLabel.add("新增资产建档");
				listKey.add("asset.audit");
				listLabel.add("资产审核");
				listKey.add("asset.batchImportData");
				listLabel.add("批量导入资产数据");
				listKey.add("asset.inventory");
				listLabel.add("盘点工单");
				listKey.add("assetAdjust");
				listLabel.add("设备调剂");
				listKey.add("assetAdjust.borrowApply");
				listLabel.add("借用申请");
				listKey.add("assetAdjust.borrowAccept");
				listLabel.add("借用受理");
				listKey.add("assetAdjust.returnApply");
				listLabel.add("归还申请");
				listKey.add("assetAdjust.returnAccept");
				listLabel.add("归还受理");
				listKey.add("assetAdjust.batch");
				listLabel.add("设备批量调剂");
				listKey.add("assetAdjust.cost");
				listLabel.add("设备计费情况");
				listKey.add("assetAdjust.stock");
				listLabel.add("设备库存情况");
				listKey.add("assetAdjust.config");
				listLabel.add("调剂设备配置");
				listKey.add("assetAdjust.nameConfig");
				listLabel.add("调剂名称设置");
				listKey.add("install");
				listLabel.add("到货安装");
				listKey.add("install.list");
				listLabel.add("到货清单");
				listKey.add("install.store");
				listLabel.add("安装入库");
				listKey.add("install.check");
				listLabel.add("核对入库");
				listKey.add("repair");
				listLabel.add("维修流程");
				listKey.add("repair.workbench");
				listLabel.add("维修工作台");
				listKey.add("repair.count");
				listLabel.add("设备维修统计");
				listKey.add("repair.message");
				listLabel.add("设备消息订阅");
				listKey.add("repair.report");
				listLabel.add("维修工单报表");
				listKey.add("repair.workSchedule");
				listLabel.add("排班管理");
				listKey.add("repair.vacationMgt");
				listLabel.add("休假管理");
				listKey.add("repair.reportMgt");
				listLabel.add("报表模版管理");
				listKey.add("maintain");
				listLabel.add("预防维护");
				listKey.add("maintain.workbench");
				listLabel.add("保养工作台");
				listKey.add("maintain.rule");
				listLabel.add("保养规则");
				listKey.add("maintain.assetMgt");
				listLabel.add("资产规则管理");
				listKey.add("maintain.electricDataImport");
				listLabel.add("电气安全数据导入");
				listKey.add("maintain.reportMgt");
				listLabel.add("报告模版管理");
				listKey.add("maintain.reportMgt");
				listLabel.add("报表模版管理");
				listKey.add("measure");
				listLabel.add("计量管理");
				listKey.add("measure.categoryConfig");
				listLabel.add("计量类别设置");
				listKey.add("measure.info");
				listLabel.add("计量信息");
				listKey.add("nurseSite");
				listLabel.add("护士站");
				listKey.add("nurseSite.maintain");
				listLabel.add("日常维护");
				listKey.add("nurseSite.config");
				listLabel.add("日常维护设置");
				listKey.add("nurseSite.report");
				listLabel.add("日常维护报表");
				listKey.add("nurseSite.optRecord");
				listLabel.add("开关机检查");
				listKey.add("knowledge");
				listLabel.add("知识文档");
				listKey.add("knowledge.mgt");
				listLabel.add("文档管理");
				listKey.add("performance");
				listLabel.add("绩效分析");
				listKey.add("performance.scan");
				listLabel.add("状态监控");
				listKey.add("performance.CT");
				listLabel.add("CT球管预测");
				listKey.add("performance.config");
				listLabel.add("设备绩效配置");
				listKey.add("performance.import");
				listLabel.add("设备绩效导入");
				listKey.add("performance.BI");
				listLabel.add("BI绩效分析平台");
				listKey.add("config");
				listLabel.add("系统配置");
				listKey.add("config.org");
				listLabel.add("组织机构管理");
				listKey.add("config.user");
				listLabel.add("用户管理");
				listKey.add("config.customRole");
				listLabel.add("自定义角色");
				listKey.add("config.assetMessage");
				listLabel.add("设备消息订阅");
				listKey.add("config.qrCode");
				listLabel.add("二维码管理");
				listKey.add("config.qrCodeMgt");
				listLabel.add("二维码模版管理");
				listKey.add("config.repairOwner");
				listLabel.add("维修责任人设置");
				listKey.add("config.assetMark");
				listLabel.add("设备标签");
				listKey.add("config.engineerGroup");
				listLabel.add("维修工程师群组");
				listKey.add("config.workFlow");
				listLabel.add("流程配置项");
				listKey.add("config.custom");
				listLabel.add("自定义配置");
				listKey.add("config.attribute");
				listLabel.add("属性配置");
				listKey.add("config.repairMessage");
				listLabel.add("维修消息配置");
				listKey.add("examineMgt");
				listLabel.add("检测管理");
				listKey.add("examineMgt.pcc");
				listLabel.add("探头检测");
				listKey.add("examineMgt.pccConfig");
				listLabel.add("探头检测配置");
				listKey.add("examineMgt.ssoImp");
				listLabel.add("SSO用户导入");
			}
		}
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		if (!edition.equals(this.edition)) {
			this.edition = edition;
			resetVersion();
		}
	}

	public TreeNode getRoot() {
		if (root == null) {
			buildRoot();
		}
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	public List<String> getListLabel(){
		return this.listLabel;
	}
	public List<String> getListKey(){
		return this.listKey;
	}
}

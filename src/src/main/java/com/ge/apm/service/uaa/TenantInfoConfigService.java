package com.ge.apm.service.uaa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.TenantInfoConfigItem;

/**
 *
 * @author 503035627
 */
@Component
public class TenantInfoConfigService {
	private static final Logger logger = Logger.getLogger(TenantInfoConfigService.class);
	public static final String VERSION_FUNCTION = "business";
	public static final String VERSION_COMMON = "common";
	public static final String VERSION_ULTRASOUND = "ultrasound";
	public static final String TREE_TYPE = "tenantConfigTree";
	public static final String MSG_TYPE = "tenantVersionMenu";
	private String edition;
	private List<TenantInfoConfigItem> menuList;
	private TreeNode root;
	private List<TenantInfoConfigItem> listOriginVersionInfo;
	private List<String> listVersionName;
	// private static Map<String, String> mapVersion;
	private List<String> listKey;
	private List<String> listLabel;
	private TreeNode[] selectedNodes;

	@Autowired
	I18nMessageRepository i18nRepository;
	
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

	public void init() {
		initKeyLabel();
		menuList = getListVersions();
		setEdition(VERSION_COMMON);
	}

	// public static void main(String[] args) {
	// TenantInfoConfig tempConfig = TenantInfoConfig.build();
	// tempConfig.setEdition(VERSION_FUNCTION);
	// tempConfig=TenantInfoConfig.parseStr(tempConfig.getJSONStr());
	// }
	public void parseStr(String str) {
		initKeyLabel();
		if (StringUtils.isEmpty(str)) {
			init();
			return;
		}
		JSONObject jsonObj = JSONObject.parseObject(str);
		edition = jsonObj.getString("edition");
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
			for (int i = 0; i < listKey.size(); ++i) {
				String key = listKey.get(i);
				String label = listLabel.get(i);
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
		menuList = tempListVersionItem;
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

	public List<String> getListVersionName() {
		if (listVersionName == null) {
			listVersionName = new ArrayList<String>();
			listVersionName.add(VERSION_FUNCTION);
			listVersionName.add(VERSION_COMMON);
			listVersionName.add(VERSION_ULTRASOUND);
		}
		return listVersionName;
	}

	private List<TenantInfoConfigItem> getListVersions() {
		if (listOriginVersionInfo == null) {
			listOriginVersionInfo = new ArrayList<TenantInfoConfigItem>();
			for (int i = 0; i < listKey.size(); ++i) {
				String key = listKey.get(i);
				String label = listLabel.get(i);
				listOriginVersionInfo.add(new TenantInfoConfigItem(label, key, true));
			}
		}
		return listOriginVersionInfo;
	}

	private void initKeyLabel() {
		if (listKey == null) {
			listKey = new ArrayList<String>();
			listLabel = new ArrayList<String>();
			List<I18nMessage> list18 = i18nRepository.findI18nByType(MSG_TYPE);
			if (list18 != null && list18.size() > 0) {
				for (int i = 0; i < list18.size(); ++i) {
					I18nMessage msg = list18.get(i);
					listKey.add(msg.getMsgKey());
					listLabel.add(msg.getValueZh());
				}
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

	public List<String> getListLabel() {
		return this.listLabel;
	}

	public List<String> getListKey() {
		return this.listKey;
	}
}

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
	public static final String VERSION_FUNCTION = "business";//业务板
	public static final String VERSION_COMMON = "common";//通用版
	public static final String VERSION_ULTRASOUND = "ultrasound";//超声科室版
	public static final String VERSION_ASSETMGT="assetMgt";//设备运营管理
	public static final String EXCELLENT_ENJOYMENT_A="excellentEnjoymentA";//优享A
	public static final String EXCELLENT_ENJOYMENT_B="excellentEnjoymentB";//优享B
	public static final String OPS_VERSION ="opsVersion";//OPS版
	public static final String ZKB_VERSION ="ZKB";//智康宝版
	public static final String TREE_TYPE = "tenantConfigTree";
	private static final String MSG_TYPE = "tenantVersionMenu";
	private String edition;
	private List<TenantInfoConfigItem> menuList;
	private TreeNode root;
	private List<TenantInfoConfigItem> listOriginVersionInfo;
	// private static Map<String, String> mapVersion;
	private List<String> listKey;
	private List<String> listLabel;
	private TreeNode[] selectedNodes;
	private String cpmURL;
	

	@Autowired
	I18nMessageRepository i18nRepository;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject jsonResult=new JSONObject();
		jsonResult.put("edition", edition);
		if (root == null) {
			jsonResult.put("menuList", parseNodeToJSONArray());
		} else {
			JSONArray jsonArr=new JSONArray();
			readTreeNode(root, jsonArr);
			jsonResult.put("menuList", jsonArr);
			if(cpmURL!=null){
				jsonResult.put("cpmURL", cpmURL);
			}
		}
		return jsonResult.toJSONString();
	}
	
	public String toMenusStr(){
		if (root == null) {
			return null;
		} else {
			JSONArray jsonArr=new JSONArray();
			readTreeNode(root, jsonArr);
			StringBuilder sb=new StringBuilder();
			if(jsonArr!=null && jsonArr.size()>0){
				for(int i=0;i<jsonArr.size();++i){
					sb.append(jsonArr.get(i));
					sb.append(",");
				}
			}
			String result=sb.toString();
			if(StringUtils.isNotEmpty(result) && result.length()>1){
				result=result.substring(0, result.length()-1);
			}
			return result;
		}
	}
	
	public String toMenusLabelStr(){
		if (root == null) {
			return null;
		} else {
			JSONArray jsonArr=new JSONArray();
			readTreeNodeLabel(root, jsonArr);
			StringBuilder sb=new StringBuilder();
			if(jsonArr!=null && jsonArr.size()>0){
				for(int i=0;i<jsonArr.size();++i){
					sb.append(jsonArr.get(i));
					sb.append(",");
				}
			}
			String result=sb.toString();
			if(StringUtils.isNotEmpty(result) && result.length()>1){
				result=result.substring(0, result.length()-1);
			}
			return result;
		}
	}

	private void readTreeNode(TreeNode root, JSONArray jsonArr) {
		if (root.isSelected()) {
			TenantInfoConfigItem tempItem = (TenantInfoConfigItem) root.getData();
			if (tempItem != null) {
				jsonArr.add((tempItem.getKey()));
			}
		}
		for (TreeNode node : root.getChildren()) {
			readTreeNode(node, jsonArr);
		}
	}
	private void readTreeNodeLabel(TreeNode root, JSONArray jsonArr) {
		if (root.isSelected()) {
			TenantInfoConfigItem tempItem = (TenantInfoConfigItem) root.getData();
			if (tempItem != null) {
				jsonArr.add((tempItem.getLabel()));
			}
		}
		for (TreeNode node : root.getChildren()) {
			readTreeNodeLabel(node, jsonArr);
		}
	}

	private JSONArray parseNodeToJSONArray() {
		JSONArray jsonArr=new JSONArray();
		for (int i = 0; i < menuList.size(); ++i) {
			TenantInfoConfigItem tempItem = menuList.get(i);
			if (tempItem.isValue()) {
				jsonArr.add(tempItem.getKey());
			}
		}
		return jsonArr;
	}

	public void resetVersion() {
		if (StringUtils.isEmpty(edition)) {
			return;
		}
		for (int i = 0; i < menuList.size(); ++i) {
			TenantInfoConfigItem tempItem = menuList.get(i);
			tempItem.setValue(true);
			String key = tempItem.getKey();
			if (edition.equals(VERSION_COMMON)) {
				//do nothing
			}else if(edition.equals(EXCELLENT_ENJOYMENT_A)){
				if(key.equals("measure.templateMgt")){
					tempItem.setValue(false);
				}
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
			}else if (edition.equals(VERSION_FUNCTION)) {
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
			}else if(edition.equals(EXCELLENT_ENJOYMENT_B)){
				if(key.contains("assetAdjust")) {
					tempItem.setValue(false);
				}else if (key.equals("repair.workSchedule")) {
					tempItem.setValue(false);
				}else if(key.equals("repair.vacationMgt")){
					tempItem.setValue(false);
				}else if(key.contains("nurseSite")){
					tempItem.setValue(false);
				}else if(key.equals("measure.templateMgt")){
					tempItem.setValue(false);
				}

			}else if(edition.equals(OPS_VERSION)){
				if(key.equals("asset.analysis")){
					tempItem.setValue(false);
				}else if(key.equals("asset.mgt")){
					tempItem.setValue(false);
				}else if(key.equals("asset.supplierMgt")){
					tempItem.setValue(false);
				}else if(key.equals("asset.scrapTempMgt")){
					tempItem.setValue(false);
				}else if(key.equals("asset.audit")){
					tempItem.setValue(false);
				}else if(key.equals("assetAdjust")) {
					tempItem.setValue(false);
				} else if(key.contains("assetAdjust.")) {
					tempItem.setValue(false);
				}else if(key.equals("repair.count")) {
					tempItem.setValue(false);
				}else if(key.equals("repair.message")) {
					tempItem.setValue(false);
				}else if (key.equals("repair.workSchedule")) {
					tempItem.setValue(false);
				}else if(key.equals("repair.vacationMgt")){
					tempItem.setValue(false);
				}else if(key.contains("measure")){
					tempItem.setValue(false);
				}else if(key.contains("nurseSite")){
					tempItem.setValue(false);
				}else if(key.contains("install")){
					tempItem.setValue(false);
				}
			}
			if (key.equals("examineMgt")) {
				tempItem.setValue(false);
			} else if (key.contains("examineMgt.")) {
				tempItem.setValue(false);
			}
			if(key.equals("performance.bigScreen")){
				tempItem.setValue(false);
			}
			if(key.equals("measure.console")){
				tempItem.setValue(false);
			}
			if(key.equals("measure.rule")){
				tempItem.setValue(false);
			}
			if(key.equals("measure.ruleMgt")){
				tempItem.setValue(false);
			}
			if(key.equals("link")){
				tempItem.setValue(false);
			}
			if(key.equals("link.CPM")){
				tempItem.setValue(false);
			}
			if(key.equals("knowledge.zkb")){
				tempItem.setValue(false);
			}
			if (edition.equals(VERSION_ASSETMGT)) {
				if(key.equals("link")){
					tempItem.setValue(true);
				}
				if(key.equals("link.CPM")){
					tempItem.setValue(true);
				}
			}
			if(edition.equals(ZKB_VERSION)){
				if(key.equals("knowledge.zkb")){
					tempItem.setValue(true);
				}
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

	//init method
	public void init() {
		initKeyLabel();
		this.cpmURL="";
		menuList = getListVersions();
		setEdition(VERSION_COMMON);
	}

	// public static void main(String[] args) {
	// TenantInfoConfig tempConfig = TenantInfoConfig.build();
	// tempConfig.setEdition(VERSION_FUNCTION);
	// tempConfig=TenantInfoConfig.parseStr(tempConfig.getJSONStr());
	// }
	
	public void parseMenuList(String str){
		initKeyLabel();
		if (StringUtils.isEmpty(str)) {
			menuList.clear();
		}
		else{
			String [] arr=str.split(",");
			List<TenantInfoConfigItem> tempListVersionItem = new ArrayList<TenantInfoConfigItem>();
			Set<String> setItemKey = new HashSet<String>();
			try {
				for (int i = 0; i < arr.length; ++i) {
					String key = arr[i];
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
		if (root != null) {
			buildRoot();
		}
	}
	//init method
	public void parseStr(String str) {
		initKeyLabel();
		if (StringUtils.isEmpty(str)) {
			init();
			return;
		}
		JSONObject jsonObj = JSONObject.parseObject(str);
		edition = jsonObj.getString("edition");
		this.cpmURL=jsonObj.getString("cpmURL");
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
		if (root != null) {
			buildRoot();
		}
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

	public String getCpmURL() {
		return cpmURL;
	}

	public void setCpmURL(String cpmURL) {
		this.cpmURL = cpmURL;
	}
	
}

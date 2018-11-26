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
	public static final String VERSION_FUNCTION = "业务版";
	public static final String VERSION_COMMON = "通用版";
	public static final String VERSION_ULTRASOUND = "超声科室版";
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
				if (key.equals("维修流程.排班管理")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.借用受理")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.归还受理")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.设备批量调剂")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.设备计费情况")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.设备库存情况")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.调剂设备配置")) {
					tempItem.setValue(false);
				}
				if (key.equals("设备调剂.调剂名称设置")) {
					tempItem.setValue(false);
				}
				if (key.equals("护士站")) {
					tempItem.setValue(false);
				}
				if (key.equals("护士站.日常维护")) {
					tempItem.setValue(false);
				}
				if (key.equals("护士站.日常维护设置")) {
					tempItem.setValue(false);
				}
				if (key.equals("护士站.日常维护报表")) {
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
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("我的主页"), "我的主页", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案"), "设备档案", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.基本信息"), "设备档案.基本信息", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.设备分布分析"), "设备档案.设备分布分析", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.合同管理"), "设备档案.合同管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.供应商管理"), "设备档案.供应商管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.报废资产"), "设备档案.报废资产", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.新增资产建档"), "设备档案.新增资产建档", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.资产审核"), "设备档案.资产审核", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.批量导入资产数据"), "设备档案.批量导入资产数据", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备档案.盘点工单"), "设备档案.盘点工单", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂"), "设备调剂", true));
			//add
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.借用申请"), "设备调剂.借用申请", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.借用受理"), "设备调剂.借用受理", true));
			//add
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.归还申请"), "设备调剂.归还申请", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.归还受理"), "设备调剂.归还受理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.设备批量调剂"), "设备调剂.设备批量调剂", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.设备计费情况"), "设备调剂.设备计费情况", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.设备库存情况"), "设备调剂.设备库存情况", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.调剂设备配置"), "设备调剂.调剂设备配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("设备调剂.调剂名称设置"), "设备调剂.调剂名称设置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("到货安装"), "到货安装", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("到货安装.到货清单"), "到货安装.到货清单", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("到货安装.安装入库"), "到货安装.安装入库", true));
			//add
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("到货安装.核对入库"), "到货安装.核对入库", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程"), "维修流程", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.维修工作台"), "维修流程.维修工作台", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.设备消息订阅"), "维修流程.设备消息订阅", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.维修工单报表"), "维修流程.维修工单报表", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.排班管理"), "维修流程.排班管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.休假管理"), "维修流程.休假管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("维修流程.报表模版管理"), "维修流程.报表模版管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护"), "预防维护", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.保养工作台"), "预防维护.保养工作台", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.保养规则"), "预防维护.保养规则", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.资产规则管理"), "预防维护.资产规则管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.电气安全数据导入"), "预防维护.电气安全数据导入", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.报告模版管理"), "预防维护.报告模版管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("预防维护.报表模版管理"), "预防维护.报表模版管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("计量管理"), "计量管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("计量管理.计量类别设置"), "计量管理.计量类别设置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("计量管理.计量信息"), "计量管理.计量信息", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("护士站"), "护士站", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("护士站.日常维护"), "护士站.日常维护", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("护士站.日常维护设置"), "护士站.日常维护设置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("护士站.日常维护报表"), "护士站.日常维护报表", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("知识文档"), "知识文档", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("知识文档.文档管理"), "知识文档.文档管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析"), "绩效分析", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析.状态监控"), "绩效分析.状态监控", true));
			//add
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析.CT球管预测"), "绩效分析.CT球管预测", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析.设备绩效配置"), "绩效分析.设备绩效配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析.设备绩效导入"), "绩效分析.设备绩效导入", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("绩效分析.BI绩效分析平台"), "绩效分析.BI绩效分析平台", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置"), "系统配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.租户管理"), "系统配置.租户管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.组织机构管理"), "系统配置.组织机构管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.用户管理"), "系统配置.用户管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.批量数据导入"), "系统配置.批量数据导入", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.设备消息订阅"), "系统配置.设备消息订阅", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.二维码管理"), "系统配置.二维码管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.二维码模版管理"), "系统配置.二维码模版管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.维修责任人设置"), "系统配置.维修责任人设置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.设备标签"), "系统配置.设备标签", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.维修工程师群组"), "系统配置.维修工程师群组", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.流程配置项"), "系统配置.流程配置项", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.自定义配置"), "系统配置.自定义配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.属性配置"), "系统配置.属性配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.维修消息配置"), "系统配置.维修消息配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("系统配置.小屏消息配置"), "系统配置.小屏消息配置", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("MVS设备管理"), "MVS设备管理", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("MVS设备管理.基本信息"), "MVS设备管理.基本信息", true));
			listOriginVersionInfo.add(new TenantInfoConfigItem(mapKeyLabel.get("MVS设备管理.采购管理"), "MVS设备管理.采购管理", true));
		}
		return listOriginVersionInfo;
	}

	private static Map<String, String> getMapVersionKeyLabel() {
		if (mapVersion == null) {
			mapVersion = new HashMap<String, String>();
			mapVersion.put("我的主页", "我的主页");
			mapVersion.put("设备档案", "设备档案");
			mapVersion.put("设备档案.基本信息", "基本信息");
			mapVersion.put("设备档案.设备分布分析", "设备分布分析");
			mapVersion.put("设备档案.合同管理", "合同管理");
			mapVersion.put("设备档案.供应商管理", "供应商管理");
			mapVersion.put("设备档案.报废资产", "报废资产");
			mapVersion.put("设备档案.新增资产建档", "新增资产建档");
			mapVersion.put("设备档案.资产审核", "资产审核");
			mapVersion.put("设备档案.批量导入资产数据", "批量导入资产数据");
			mapVersion.put("设备档案.盘点工单", "盘点工单");
			mapVersion.put("设备调剂", "设备调剂");
			//add
			mapVersion.put("设备调剂.借用申请", "借用申请");
			mapVersion.put("设备调剂.借用受理", "借用受理");
			//add
			mapVersion.put("设备调剂.归还申请", "归还申请");
			mapVersion.put("设备调剂.归还受理", "归还受理");
			mapVersion.put("设备调剂.设备批量调剂", "设备批量调剂");
			mapVersion.put("设备调剂.设备计费情况", "设备计费情况");
			mapVersion.put("设备调剂.设备库存情况", "设备库存情况");
			mapVersion.put("设备调剂.调剂设备配置", "调剂设备配置");
			mapVersion.put("设备调剂.调剂名称设置", "调剂名称设置");
			mapVersion.put("到货安装", "到货安装");
			mapVersion.put("到货安装.到货清单", "到货清单");
			mapVersion.put("到货安装.安装入库", "安装入库");
			//add
			mapVersion.put("到货安装.核对入库", "核对入库");
			mapVersion.put("维修流程", "维修流程");
			mapVersion.put("维修流程.维修工作台", "维修工作台");
			mapVersion.put("维修流程.设备消息订阅", "设备消息订阅");
			mapVersion.put("维修流程.维修工单报表", "维修工单报表");
			mapVersion.put("维修流程.排班管理", "排班管理");
			mapVersion.put("维修流程.休假管理", "休假管理");
			mapVersion.put("维修流程.报表模版管理", "报表模版管理");
			mapVersion.put("预防维护", "预防维护");
			mapVersion.put("预防维护.保养工作台", "保养工作台");
			mapVersion.put("预防维护.保养规则", "保养规则");
			mapVersion.put("预防维护.资产规则管理", "资产规则管理");
			mapVersion.put("预防维护.电气安全数据导入", "电气安全数据导入");
			mapVersion.put("预防维护.报告模版管理", "报告模版管理");
			mapVersion.put("预防维护.报表模版管理", "报表模版管理");
			mapVersion.put("计量管理", "计量管理");
			mapVersion.put("计量管理.计量类别设置", "计量类别设置");
			mapVersion.put("计量管理.计量信息", "计量信息");
			mapVersion.put("护士站", "护士站");
			mapVersion.put("护士站.日常维护", "日常维护");
			mapVersion.put("护士站.日常维护设置", "日常维护设置");
			mapVersion.put("护士站.日常维护报表", "日常维护报表");
			mapVersion.put("知识文档", "知识文档");
			mapVersion.put("知识文档.文档管理", "文档管理");
			mapVersion.put("绩效分析", "绩效分析");
			mapVersion.put("绩效分析.状态监控", "状态监控");
			//add
			mapVersion.put("绩效分析.CT球管预测", "CT球管预测");
			mapVersion.put("绩效分析.设备绩效配置", "设备绩效配置");
			mapVersion.put("绩效分析.设备绩效导入", "设备绩效导入");
			mapVersion.put("绩效分析.BI绩效分析平台", "BI绩效分析平台");
			mapVersion.put("系统配置", "系统配置");
			mapVersion.put("系统配置.租户管理", "租户管理");
			mapVersion.put("系统配置.组织机构管理", "组织机构管理");
			mapVersion.put("系统配置.用户管理", "用户管理");
			mapVersion.put("系统配置.批量数据导入", "批量数据导入");
			mapVersion.put("系统配置.设备消息订阅", "设备消息订阅");
			mapVersion.put("系统配置.二维码管理", "二维码管理");
			mapVersion.put("系统配置.二维码模版管理", "二维码模版管理");
			mapVersion.put("系统配置.维修责任人设置", "维修责任人设置");
			mapVersion.put("系统配置.设备标签", "设备标签");
			mapVersion.put("系统配置.维修工程师群组", "维修工程师群组");
			mapVersion.put("系统配置.流程配置项", "流程配置项");
			mapVersion.put("系统配置.自定义配置", "自定义配置");
			mapVersion.put("系统配置.属性配置", "属性配置");
			mapVersion.put("系统配置.维修消息配置", "维修消息配置");
			mapVersion.put("系统配置.小屏消息配置", "小屏消息配置");
			mapVersion.put("MVS设备管理", "MVS设备管理");
			mapVersion.put("MVS设备管理.基本信息", "基本信息");
			mapVersion.put("MVS设备管理.采购管理", "采购管理");
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
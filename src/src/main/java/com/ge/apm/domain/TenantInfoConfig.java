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
	private TreeNode[] selectedNodes;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(root==null){
			return "{\"edition\":\"" + edition + "\",\"menuList\":" + parseNodeToList().toString() + "}";
		}
		else{
			StringBuilder sb=new StringBuilder();
			String result=readTreeNode(root,sb);
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
	private String readTreeNode(StringBuilder sb){
		if(selectedNodes!=null && selectedNodes.length>0){
			for(int i=0;i<selectedNodes.length;++i){
				TreeNode tempNode=selectedNodes[i];
				TenantInfoConfigItem tempItem=(TenantInfoConfigItem)tempNode.getData();
				sb.append("\""+tempItem.getKey()+"\",");
			}
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
				else if (key.equals("assetAdjust")) {
					tempItem.setValue(false);
				}
				else if (key.contains("assetAdjust.")) {
					tempItem.setValue(false);
				}
				else if (key.equals("nurseSite")) {
					tempItem.setValue(false);
				}
				else if (key.contains("nurseSite.")) {
					tempItem.setValue(false);
				}
			}
			if(key.equals("examineMgt")){
				tempItem.setValue(false);
			}
			else if(key.contains("examineMgt.")){
				tempItem.setValue(false);
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
				tempNode.setSelectable(true);
				if(tempItem.isValue()){
					tempNode.setSelected(true);
				}
				else{
					tempNode.setSelected(false);
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
					else{
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
//	 public static void main(String[] args) {
//	 TenantInfoConfig tempConfig = TenantInfoConfig.build();
//	 tempConfig.setEdition(VERSION_FUNCTION);
//	 tempConfig=TenantInfoConfig.parseStr(tempConfig.getJSONStr());
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
		if(root==null){
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
		Map<String, String> mapKeyLabel = getMapVersionKeyLabel();
		if (listOriginVersionInfo == null) {
			listOriginVersionInfo = new ArrayList<TenantInfoConfigItem>();
			Iterator<String> it=mapKeyLabel.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				String label=mapKeyLabel.get(key);
				listOriginVersionInfo.add(new TenantInfoConfigItem(label, key, true));
			}
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
			mapVersion.put("config.org", "组织机构管理");
			mapVersion.put("config.user", "用户管理");
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
			mapVersion.put("examineMgt", "检测管理");
			mapVersion.put("examineMgt.pcc", "探头检测");
			mapVersion.put("examineMgt.pccConfig", "探头检测配置");
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
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
}

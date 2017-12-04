package com.ge.apm.service.asset;

import com.ge.apm.dao.GbCodeRepository;
import com.ge.apm.domain.GbCode;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.web.GbForm;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lsg on 30/11/2017.
 */
@Component
public class GbCodeService {
	@Autowired
	GbCodeRepository gbDao;
	/*public List<GbCode> getSubCode(Integer code){
		//获取当前gbcode的级别
		//如果当前是一级目录,就去获取二级目录的设备
		if(code%10000==0){
			return gbDao.findGbByRange(code,code+9999,1);
		}else if(code%100 ==0){ //当前是二级目录，取三级目录
			return gbDao.findGbByRange(code+1,code+99,2);
		}
     return new ArrayList<GbCode>();
	}*/

/*	public TreeNode getOrgTreeByParentIdWithSelectedChildren(String parentOrgId, String userUid) {

		List<GbCode> orgInfoFullList = gbDao.find();
		List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteList(userUid);

		TreeNode rootNode = buildOrgTreeByParentIdWithSelectedChildren(orgInfoFullList, orgInfoSelectableList);

		return rootNode;
	}*/

	public  TreeNode buildGbTree(){
		List<GbCode> gbList = gbDao.find();
		Map<String, TreeNode> treeMap = new LinkedHashMap<>();

		for(GbCode gb: gbList){
			TreeNode node = new DefaultTreeNode("gb", gb, null);
			node.setSelectable(false);
			node.setRowKey("gb_"+gb.getId());
			treeMap.put(gb.getCode(), node);
		}
		TreeNode rootNode = null;
		for(GbCode org: gbList){
			TreeNode node = treeMap.get(org.getCode());

			if (node == null) continue;
			node.setSelectable(true);

			GbCode current = org;
			while(current.getParentCode()!=null){
				TreeNode currentNode = treeMap.get(current.getCode());
				TreeNode parentNode = treeMap.get(current.getParentCode());
				if (parentNode == null) break;
				parentNode.getChildren().add(currentNode);
				current = (GbCode)parentNode.getData();
			}

			if (rootNode == null) {
				if (current.getParentCode() == null) {
					rootNode = treeMap.get(current.getCode());
				} else if (treeMap.get(current.getParentCode()) == null){
					rootNode = treeMap.get(current.getCode());
				}
			}

		}

		return rootNode;

	}

	/////////////
	public TreeNode getGbCodeTree(){
		//OrgInfo rootOrg = orgInfoDao.getByUid(user.getSiteUID());

		TreeNode root = new DefaultTreeNode("root", null, null);

		List<GbCode> userScopeList = gbDao.find();
		/*for (GbCode item : userScopeList) {
			if (item.getLevel() == 0) {
				new DefaultTreeNode("gb", item, root);
			} else {
				root.getChildren().add(getOrgTreeBySite().getChildren().get(0));
			}
		}*/
		root.getChildren().add(getOrgTreeBySite().getChildren().get(0));
		return root;
	}
	public TreeNode getOrgTreeBySite(){
		List<GbCode> orgList = null;
		orgList = gbDao.find();
		TreeNode root  = new DefaultTreeNode("root", null, null);;

		Map<String, TreeNode> treeMap = new LinkedHashMap<>();

		for(GbCode gb: orgList){
			TreeNode node = new DefaultTreeNode("gb", gb, null);
			node.setRowKey("gb_"+gb.getId());
			treeMap.put(gb.getCode(), node);
		}

		for(Map.Entry<String, TreeNode> nodeItem: treeMap.entrySet()){
			TreeNode node = nodeItem.getValue();
			GbCode gbCode = (GbCode)node.getData();
			if(gbCode.getParentCode()!=null){
				TreeNode parentNode = treeMap.get(gbCode.getParentCode());
				if(parentNode!=null){
					parentNode.getChildren().add(node);
				}
			}
		}
		List<GbCode> subGb = gbDao.getSubGb(0);
		root.getChildren().add(treeMap.get(subGb.get(0).getCode())); //yinggai jia level =0 dejiedian
		return root; //在此应该拿到第一和第二级目录
	}

	public List<GbForm> tansferGbForm(){
		List<GbCode> all = gbDao.find();
		List<GbForm> gbFormList = new ArrayList<GbForm>();
		for(GbCode gb:all){
			GbForm gbForm = new GbForm();
              if(gb.getLevel()==0){
				  gbForm.setL1Code(gb.getCode().toString());
				  gbForm.setL1Name(gb.getName());
				  gbFormList.add(gbForm);
			  }
			  if(gb.getLevel()==1){
				  gbForm.setL2Code(gb.getCode().toString());
				  gbForm.setL2Name(gb.getName());
				  gbFormList.add(gbForm);
              }
			if(gb.getLevel()==2){
				gbForm.setL3Code(gb.getCode().toString());
				gbForm.setL3Name(gb.getName());
				gbFormList.add(gbForm);
			}
		}
		return gbFormList;
	}
}

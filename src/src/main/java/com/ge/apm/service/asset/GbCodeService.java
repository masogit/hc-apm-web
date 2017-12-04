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

}

package com.ge.apm.service.assetTypeGroup;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import lombok.Data;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
@Data
public class AssetTypeService {

    @Autowired
    AssetTypeGroupRepository assetTypeGroupDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;


    private  boolean failData;
    private boolean existingData;


    public TreeNode buildSysAuthGroupInTree(String tenantUid) {

        List<AssetTypeGroup> resultList = assetTypeGroupDao.listAssetTypeGroupInHierarchy(tenantUid);

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup assetTypeGroup: resultList) {
            TreeNode node = new DefaultTreeNode("sysAuthGroup", assetTypeGroup, null);
            node.setRowKey(assetTypeGroup.getUid());
            treeNodeMap.put(assetTypeGroup.getUid(), node);
        }

        TreeNode root  = new DefaultTreeNode("root", null, null);

        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            AssetTypeGroup currentData = (AssetTypeGroup) currentNode.getData();

            if (null == currentData.getParentUid()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUid());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                }
            }

        }

        sortSysAuthGroupByEncoded(root);

        return root;

    }


    public void sortSysAuthGroupByEncoded(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    AssetTypeGroup sag1 = (AssetTypeGroup) o1.getData();
            AssetTypeGroup sag2 = (AssetTypeGroup) o2.getData();
                    return sag1.getEncoded().compareTo(sag2.getEncoded());
                }
        );

        for (TreeNode node: root.getChildren()) {
            sortSysAuthGroupByEncoded(node);
        }

    }

    public void filterTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if(filterKeyword==null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doFilterTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item->{
            item.setType("sysAuthGroup");
            if(!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }


    private void doFilterTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doFilterTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);

            item.setType("hidden");
            AssetTypeGroup org = (AssetTypeGroup)item.getData();
            if(!org.getName().contains(filterKeyword)) return;
            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }

    @Transactional
    public List<Map> importAuthGroup(UploadedFile file, UserAccount user) throws IOException {

        List<Map> resultList = new ArrayList<>();
        //TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());
        ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

        String message = "已经存在";
        Map<String, String> result = null;
        /*int keyRow = 1;
        int dataRow = 2;*/
        /*
        nameList存储所有数据
        * */
        List<String> nameList=new ArrayList<String>();
        Map<String,List> nameMap=new HashMap<>();
        List<List> dataList= new ArrayList<>();
        /*
        定义一个list,存储每个sheet的开头第一行的的列值，将这个值与下面每一行的值进行比较，如果不同，就进行替换
        校验的规则是每个sheet所有行的列数不同超过第一行的列数
        **/
        List<String> rowNameList=new ArrayList<String>();
        //获取sheet的页数
        int sheets=doc.getWorkBook().getNumberOfSheets();

        //校验sheet名称,以及列数是否正常，sheet名称要以01,02这样的数字为开头
        for(int i=0; i<sheets; i++){
            String sheetNameCode=doc.getWorkBook().getSheetAt(i).getSheetName().substring(0,2);
            char[] nameCode= sheetNameCode.toCharArray();
            for(int a=0; a<nameCode.length; a++){
                if(nameCode[a]>57 || nameCode[a]<48){
                    result = new HashMap<>();
                    result.put("index",doc.getWorkBook().getSheetAt(i).getSheetName()+" sheet名称不符合格式,开头两位为0-9的数字");
                    resultList.add(result);
                    return  resultList;
                }
            }
            int collums0=doc.getWorkBook().getSheetAt(i).getRow(0).getPhysicalNumberOfCells();
            if(collums0 % 2!=0){
                result = new HashMap<>();
                result.put("index",doc.getWorkBook().getSheetAt(i).getSheetName()+" 请检查是否存在编码列");
                resultList.add(result);
                return  resultList;
            }
        }

        for(int i=0; i<sheets; i++){

            int collums0=doc.getWorkBook().getSheetAt(i).getRow(0).getPhysicalNumberOfCells();

            for(int x=0; x<collums0; x++){
                rowNameList.add(getCellValue(doc,i,1,x));
            }

            int aa=doc.getWorkBook().getSheetAt(i).getLastRowNum();

            for(int j=1; j<doc.getWorkBook().getSheetAt(i).getLastRowNum()+1; j++){

                //int collums=doc.getWorkBook().getSheetAt(i).getRow(j).getPhysicalNumberOfCells();
                int collums=doc.getWorkBook().getSheetAt(i).getRow(j)== null ? 0 :doc.getWorkBook().getSheetAt(i).getRow(j).getLastCellNum();

                boolean isNull=false;
                if(collums==0)
                    continue;
                for(int d=0;d<doc.getWorkBook().getSheetAt(i).getRow(j).getLastCellNum(); d++){
                    if(!getCellValue(doc,i,j,d).equals("")){
                        isNull=true;
                    }
                }

                //去除空白行
                if(doc.getWorkBook().getSheetAt(i).getRow(j)==null || !isNull) {
                    continue;
                }


                String names=null;
                List<String> list= new ArrayList<String>();

                list.add(doc.getWorkBook().getSheetAt(i).getSheetName().substring(2));
                list.add(doc.getWorkBook().getSheetAt(i).getSheetName().substring(0,2));

                for(int k=0;k<collums; k++){
                    if(!rowNameList.get(k).equals(getCellValue(doc,i,j,k))
                            && !getCellValue(doc,i,j,k).equals("")){
                        rowNameList.set(k,getCellValue(doc,i,j,k));
                    }else if(getCellValue(doc,i,j,k).equals("")){
                        names=rowNameList.get(k);
                        list.add(names);
                    }
                    String names2=getCellValue(doc,i,j,k);
                    if(!names2.equals("")){
                        list.add(names2);
                    }
                }

                names=list.toString().replace(",","-").substring(1,list.toString().length()-1);

                List<String> tmpdata=new ArrayList<>();

                //重新编码
                for(int a=0; a<list.size();a++){
                    if(tmpdata.size()>0){
                        tmpdata.add(tmpdata.get(tmpdata.size()-1)+"-"+list.get(a+1));
                    }else{
                        tmpdata.add(list.get(a+1));
                    }
                    a=a+1;
                }
                for(int a=0;a<tmpdata.size();a++){
                    list.set(a*2+1,tmpdata.get(a));
                }
                dataList.add(Arrays.asList(list.toArray()));
                list.clear();
                nameList.add(names);
            }
            rowNameList.clear();
        }

//        Integer maxId = assetTypeGroupDao.getMaxId();
//        if (null == maxId) {
//            maxId = 0;
//        }

        for(List lis1: dataList){

            List<String> data=lis1;

            try {
                String parentUid=null;
                for(int i=0; i<data.size()-1; i++){
                    String name = data.get(i).trim();
                    String encoded = data.get(i + 1).trim();
//                    String classificationCode=encoded.contains("-") ? data.get(i+1).trim().substring(encoded.lastIndexOf("-")+1, encoded.length()) : encoded;
                    String classificationCode=encoded.replaceAll("-", "");
                    AssetTypeGroup sysAuthGroup=assetTypeGroupDao.findByEncodedAndTenantUid(encoded,user.getTenantUID());
                    //通过每行的最后一个cell判断这条数据是否存在
                    if(i==data.size()-2){
                        if(sysAuthGroup!=null){
                            result = new HashMap<>();
                            result.put("index",name+" "+encoded+"编码已存在,导入失败!!!!!!");
                            resultList.add(result);
                        }

                    }
                    if(sysAuthGroup==null && parentUid==null){
                        sysAuthGroup= new AssetTypeGroup();
//                        sysAuthGroup.setId(++maxId);
                        sysAuthGroup.setName(name);
                        sysAuthGroup.setClassificationCode(classificationCode);
                        sysAuthGroup.setEncoded(encoded);
//                        sysAuthGroup.onSave();
                        sysAuthGroup.setTenantUid(user.getTenantUID());
                        assetTypeGroupDao.save(sysAuthGroup);
                        result = new HashMap<>();
                        result.put("index","资产"+name+" "+encoded+"导入成功");
                        resultList.add(result);
                    }else if(sysAuthGroup==null && parentUid!=null){
                        sysAuthGroup= new AssetTypeGroup();
//                        sysAuthGroup.setId(++maxId);
                        sysAuthGroup.setName(name);
                        sysAuthGroup.setClassificationCode(classificationCode);
                        sysAuthGroup.setEncoded(encoded);
                        AssetTypeGroup parent = assetTypeGroupDao.findByUid(parentUid);
                        sysAuthGroup.setParentAuthGroup(parent);
                        sysAuthGroup.setParentUid(parentUid);
//                        sysAuthGroup.onSave();
                        sysAuthGroup.setTenantUid(user.getTenantUID());
                        assetTypeGroupDao.save(sysAuthGroup);
                        result = new HashMap<>();
                        result.put("index","资产"+name+" "+encoded+"导入成功");
                        resultList.add(result);
                    }
                    AssetTypeGroup sysAuthGroup2=assetTypeGroupDao.findByEncodedAndTenantUid(encoded,user.getTenantUID());
                    parentUid=sysAuthGroup2.getUid();
                    i++;
                }

            } catch(Exception e){
                result.put("status", e.getMessage());
            } finally {
                if (result != null) {
                    //resultList.add(result);

                }
            }
        }
        return resultList;
    }

    //获取第i个sheet中第j行第k列的数值
    public String getCellValue(ExcelDocument doc, int i, int j, int k){
        String data=null;
        if(doc.getWorkBook().getSheetAt(i).getRow(j).getCell(k)==null){
            data="";
        }else{
            if(doc.getWorkBook().getSheetAt(i).getRow(j).getCell(k).getStringCellValue() instanceof  String){
                data=doc.getWorkBook().getSheetAt(i).getRow(j).getCell(k).getStringCellValue();
            }else {
                data="";
            }
        }
        return data;
    }

    public void checkRowData(String authGroupName) throws Exception {

        if (StringUtils.isEmpty(authGroupName)) {
            throw new Exception("分组不能为空!");
        }
    }


    private boolean getLastRow(String authGroupName)  {
        if (StringUtils.isEmpty(authGroupName)) {
            return true;
        }
        return false;
    }
}

package com.ge.apm.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MaintainDataPair {
	private String key;
	private String strValue;
	private Boolean blnValue;
	private Integer intValue;
	private List<MaintainDataPair> listSimplePairValue;
	private List<String> listStrValue;
	private List<Boolean> listBlnValue;
	//0：未检查,1:通过，2有问题
	private Integer status;
	
	private final String COMMONKEY="result";
	
	public MaintainDataPair() {
		this.key=COMMONKEY;
	}
	
	public MaintainDataPair(List<MaintainDataPair> listSimplePairValue){
		this.key=COMMONKEY;
		this.listSimplePairValue=listSimplePairValue;
	}
	
	public MaintainDataPair(String strValue){
		this.key=COMMONKEY;
		this.strValue=strValue;
	}
	
	public MaintainDataPair(String key,Integer intValue){
		this.key=key;
		this.intValue=intValue;
	}
	
	public MaintainDataPair(String key,String strValue){
		this.key=key;
		this.strValue=strValue;
	}
	
	public MaintainDataPair(String key,Boolean blnValue){
		this.key=key;
		this.blnValue=blnValue;
	}
	
	public MaintainDataPair(String key,List<MaintainDataPair> listSimplePairValue){
		this.key=key;
		this.listSimplePairValue=listSimplePairValue;
	}
	
	public void put(String key,String strValue){
		this.key=key;
		this.strValue=strValue;
	}
	
	public void put(String key,Boolean blnValue){
		this.key=key;
		this.blnValue=blnValue;
	}
	
	public void put(String key,List<MaintainDataPair> listSimplePairValue){
		this.key=key;
		this.listSimplePairValue=listSimplePairValue;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public Boolean getBlnValue() {
		return blnValue;
	}

	public void setBlnValue(Boolean blnValue) {
		this.blnValue = blnValue;
	}

	public List<MaintainDataPair> getListSimplePairValue() {
		return listSimplePairValue;
	}

	public void setListSimplePairValue(List<MaintainDataPair> listSimplePairValue) {
		this.listSimplePairValue = listSimplePairValue;
	}

	public List<String> getListStrValue() {
		return listStrValue;
	}

	public void setListStrValue(List<String> listStrValue) {
		this.listStrValue = listStrValue;
	}

	public List<Boolean> getListBlnValue() {
		return listBlnValue;
	}

	public void setListBlnValue(List<Boolean> listBlnValue) {
		this.listBlnValue = listBlnValue;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getJsonStr(){
		if(status==null){
			status=0;
		}
		List<MaintainDataPair> listCheckItems=this.getListSimplePairValue();
		if(listCheckItems!=null &&listCheckItems.size()>0){
			String str="{\""+COMMONKEY+"\":[";
			for (int i = 0; i < listCheckItems.size(); ++i) {
				MaintainDataPair p = listCheckItems.get(i);
				List<MaintainDataPair> listInner=p.getListSimplePairValue();
				if(listInner!=null && listInner.size()>0){
					str+="{\""+p.getKey()+"\":[";
					for(int j=0;j<listInner.size();++j){
						MaintainDataPair temp=listInner.get(j);
						Boolean bln=temp.getBlnValue();
						if(bln!=null){
							if(status==1||status==2){
								if(status==3){
								}
								else{
									status=2;
									if(bln==false){
										status=3;
									}
								}
							}
						}
						str+="{\""+temp.getKey()+"\":"+""+bln+"},";
					}
					str=str.substring(0,str.length()-1);
					str+="]},";
				}
			}
			str=str.substring(0, str.length()-1);
			str+="]}";
			return str;
		}
		return "";
	}
	
	public void parseStr(List<MaintainDataPair> listP,String content,Object defaultValue){
		if(listP==null){
			new ArrayList<MaintainDataPair>();
		}
		else{
			listP.clear();
		}
		if(StringUtils.isNotEmpty(content)){
			JSONObject obj=JSONObject.parseObject(content);
			JSONArray array=(JSONArray) obj.get(COMMONKEY);
			for(int i=0;i<array.size();++i){
				JSONObject objInner=array.getJSONObject(i);
				Set<String> setKey=objInner.keySet();
				if(objInner!=null && setKey.size()>0){
					Iterator<String> it=setKey.iterator();
					while(it.hasNext()){
						String key=it.next();
						JSONArray checkItemsArr=objInner.getJSONArray(key);
						List<MaintainDataPair> listInnerP=new ArrayList<MaintainDataPair>();
						for(int j=0;j<checkItemsArr.size();++j){
							JSONObject checkItem=checkItemsArr.getJSONObject(j);
							String itemKey=checkItem.keySet().iterator().next();
							Object itemValue=checkItem.get(itemKey);
							MaintainDataPair p;
							if(itemValue!=null){
								p=new MaintainDataPair(itemKey,(Boolean)itemValue);
							}
							else{
								p=new MaintainDataPair(itemKey,(Boolean)defaultValue);
							}
							listInnerP.add(p);
						}
						MaintainDataPair p=new MaintainDataPair(key,listInnerP);
						listP.add(p);
					}
				}
			}
		}
	}
	
	public static void replaceWithDefaultValue(List<MaintainDataPair> list,Object defaultValue){
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				List<MaintainDataPair> listInner=list.get(i).getListSimplePairValue();
				if(listInner!=null && listInner.size()>0){
					for(int j=0;j<listInner.size();++j){
						MaintainDataPair p=listInner.get(j);
						p.setBlnValue((Boolean)defaultValue);
					}
				}
			}
		}
	}
	
	public static void copyCheckItems(List<MaintainDataPair> source, List<MaintainDataPair> dest){
		if(dest!=null && dest.size()>0 && source!=null && source.size()>0){
			for(int i=0;i<source.size();++i){
				MaintainDataPair p=source.get(i);
				for(int j=0;j<dest.size();++j){
					MaintainDataPair p2=dest.get(j);
					if(p.getKey().equals(p2.getKey())){
						copyCheckItemsValue(source.get(i).getListSimplePairValue(), dest.get(j).getListSimplePairValue());
					}
				}
			}
		}
	}
	
	public static void copyCheckItemsValue(List<MaintainDataPair> listinnerSource, List<MaintainDataPair> listInnerDest){
		for(int y=0;y<listinnerSource.size();++y){
			String innerSourceKey=listinnerSource.get(y).getKey();
			Boolean bln=listinnerSource.get(y).getBlnValue();
			for(int x=0;x<listInnerDest.size();++x){
				String innerDestKey=listInnerDest.get(x).getKey();
				if(innerDestKey.equals(innerSourceKey)){
					if(bln!=null){
						listInnerDest.get(x).setBlnValue(listinnerSource.get(y).getBlnValue());
					}
					else{
						listInnerDest.get(x).setBlnValue(false);
					}
				}
			}
		}
	}
	
	public static void mergeCheckItemsValue(List<MaintainDataPair> listinnerSource, List<MaintainDataPair> listInnerDest){
		for(int y=0;y<listinnerSource.size();++y){
			String innerSourceKey=listinnerSource.get(y).getKey();
			boolean contains=false;
			for(int x=0;x<listInnerDest.size();++x){
				String innerDestKey=listInnerDest.get(x).getKey();
				if(innerDestKey.equals(innerSourceKey)){
					contains=true;
				}
			}
			if(!contains){
				listInnerDest.add(listinnerSource.get(y));
			}
		}
	}
	
	public static void print(MaintainDataPair p){
		System.out.println(p.getKey());
		System.out.println(p.getBlnValue());
	}
	public static void print(List<MaintainDataPair> list){
		for(int i=0;i<list.size();++i){
			List<MaintainDataPair> innerList=list.get(i).getListSimplePairValue();
			System.out.println(list.get(i).getKey());
			for(int j=0;j<innerList.size();++j){
				print(innerList.get(j));
			}
		}
	}
}

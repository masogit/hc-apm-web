package com.ge.apm.domain;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SimplePair {
	private String key;
	private String strValue;
	private Boolean blnValue;
	private Integer intValue;
	private List<SimplePair> listSimplePairValue;
	private List<String> listStrValue;
	private List<Boolean> listBlnValue;
	//0：未检查,1:通过，2有问题
	private Integer status;
	
	private final String COMMONKEY="result";
	
	public SimplePair() {
		this.key=COMMONKEY;
	}
	
	public SimplePair(List<SimplePair> listSimplePairValue){
		this.key=COMMONKEY;
		this.listSimplePairValue=listSimplePairValue;
	}
	
	public SimplePair(String strValue){
		this.key=COMMONKEY;
		this.strValue=strValue;
	}
	
	public SimplePair(String key,Integer intValue){
		this.key=key;
		this.intValue=intValue;
	}
	
	public SimplePair(String key,String strValue){
		this.key=key;
		this.strValue=strValue;
	}
	
	public void put(String key,String strValue){
		this.key=key;
		this.strValue=strValue;
	}
	
	public void put(String key,Boolean blnValue){
		this.key=key;
		this.blnValue=blnValue;
	}
	
	public void put(String key,List<SimplePair> listSimplePairValue){
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

	public List<SimplePair> getListSimplePairValue() {
		return listSimplePairValue;
	}

	public void setListSimplePairValue(List<SimplePair> listSimplePairValue) {
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		status=0;
		List<SimplePair> listCheckItems=this.getListSimplePairValue();
		if(listCheckItems.size()>0){
			String str="{\""+COMMONKEY+"\":[";
			for (int i = 0; i < listCheckItems.size(); ++i) {
				SimplePair p = listCheckItems.get(i);
				List<SimplePair> listInner=p.getListSimplePairValue();
				if(listInner.size()>0){
					str+="{\""+p.getKey()+"\":[";
					for(int j=0;j<listInner.size();++j){
						SimplePair temp=listInner.get(j);
						Boolean bln=temp.getBlnValue();
						if(bln!=null){
							if(status==2){
							}
							else{
								status=1;
								if(bln==false){
									status=2;
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
	
	public void parseStr(List<SimplePair> listP,String content,Object defaultValue){
		status=0;
		if(StringUtils.isNotEmpty(content)){
			JSONObject obj=JSONObject.parseObject(content);
			JSONArray array=(JSONArray) obj.get(COMMONKEY);
			for(int j=0;j<listP.size();++j){
				SimplePair innerP=listP.get(j);
				for(int i=0;i<array.size();++i){
					JSONObject objInner=array.getJSONObject(i);
					if(innerP.getKey().equals(objInner.keySet().iterator().next())){
						List<SimplePair> pValue=innerP.getListSimplePairValue();
						JSONArray innerArr=objInner.getJSONArray(innerP.getKey());
						for(int z=0;z<pValue.size();++z){
							SimplePair pLast=pValue.get(z);
							for(int y=0;y<innerArr.size();++y){
								JSONObject objLast=innerArr.getJSONObject(y);
								String lastKey=objLast.keySet().iterator().next();
								if(pLast.getKey().equals(lastKey)){
									if(objLast.get(lastKey)!=null){
										Boolean bln=(Boolean)objLast.get(lastKey);
										if(bln!=null){
											if(status==2){
											}
											else{
												status=1;
												if(bln==false){
													status=2;
												}
											}
										}
										pLast.setBlnValue(bln);
									}
									else{
										pLast.setBlnValue((Boolean)defaultValue);
									}
								}
							}
						}
					}
				}
			}
		}
		else{
			for(int j=0;j<listP.size();++j){
				SimplePair innerP=listP.get(j);
				List<SimplePair> pValue=innerP.getListSimplePairValue();
				for(int z=0;z<pValue.size();++z){
					SimplePair pLast=pValue.get(z);
					pLast.setBlnValue((Boolean)defaultValue);
				}
			}
		}
	}
}

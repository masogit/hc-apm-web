package com.ge.apm.view.sysutil;

import com.ge.apm.domain.UnstructuredData;

import java.io.Serializable;

/**
 * Created by lsg on 21/4/2018.
 */
public class CheckItem implements Serializable {
	String key;
	UnstructuredData ud;
	String zhValue;

	public CheckItem(String key, UnstructuredData ud){
		this.key = key;
		this.ud = ud;
		try{
			switch (ud.getValue().toString()){
				case "pass":zhValue="通过";break;
				case "n/a":zhValue="不适用";break;
				case "unpass":zhValue="不通过";break;
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}

	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public UnstructuredData getUd() {
		return ud;
	}

	public void setUd(UnstructuredData ud) {
		this.ud = ud;
	}

	public UnstructuredData getItemUd(String key){
		return ud;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (key != null ? key.hashCode() : 0);
		return hash;
	}

	public String getZhValue() {
		return zhValue;
	}

	public void setZhValue(String zhValue) {
		this.zhValue = zhValue;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CheckItem)) {
			return false;
		}
		CheckItem other = (CheckItem) object;
		return !((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key)));
	}
}
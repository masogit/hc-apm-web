package webapp.framework.web.mvc;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.CustomRole;

/**
 *
 * @author 503035627
 */
public interface IAuthCtrl {
	List<CustomRole> getCustomRole();
	String getAuthKey();
	default boolean authOpe(int type){
		List<CustomRole> listCustomRole=getCustomRole();
		String authKey=getAuthKey();
		if(authKey!=null && listCustomRole!=null && listCustomRole.size()>0){
			for(int i=0;i<listCustomRole.size();++i){
				CustomRole customRole=listCustomRole.get(i);
				String jsonStr=customRole.getJsonAuthorities();
				if(StringUtils.isNotEmpty(jsonStr)){
					JSONArray jsonArr=JSONArray.parseArray(jsonStr);
					for(int j=0;j<jsonArr.size();++j){
						JSONObject jsonObj=jsonArr.getJSONObject(j);
						String key=jsonObj.getString("k");
						if(authKey.equals(key)){
							switch (type){
							case 1:
								return jsonObj.getBooleanValue("a");
							case 2:
								return jsonObj.getBooleanValue("d");
							case 3:
								return jsonObj.getBooleanValue("m");
							case 4:
								return jsonObj.getBooleanValue("s");
							case 5:
								return jsonObj.getBooleanValue("i");
							case 6:
								return jsonObj.getBooleanValue("o");
							}
							
						}
					}
				}
				
			}
		}
		return true;
	}
	default boolean authCreate(){
		return authOpe(1);
	}
	default boolean authDelete(){
		return authOpe(2);
	}
	default boolean authModify(){
		return authOpe(3);
	}
	default boolean authSearch(){
		return authOpe(4);
	}
	default boolean authImport(){
		return authOpe(5);
	}
	default boolean authExport(){
		return authOpe(6);
	}
	default boolean authSave(){
		return authCreate()||authModify();
	}
}

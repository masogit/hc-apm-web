package com.ge.apm.service.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ElectricSafetyDataRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ElectricSafetyData;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.view.sysutil.UserContextService;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class ElectricSafetyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElectricSafetyService.class);
    
//    private Properties properties;
    private final String UPDATE="UPDATE";
    private final String INSERT="INSERT";
    private UserAccount user;
    
    @Autowired
    AssetInfoRepository assetInfoRepository;
    
    @Autowired
    V2_PmOrderRepository v2_PmOrderRepository;
    
    @Autowired
    ElectricSafetyDataRepository electricSafetyDataRepository;
    
    
    public List<ElectricSafetyData> handlerPsFile(UploadedFile file, UserAccount user) throws IOException, ParseException {
		String[] strArr=readFileByLine(file);
		this.user=user;
		return analysis(strArr,file.getFileName());
    }
    
   
    public void insertUpdateData(List<ElectricSafetyData> list){
    	String financingNum="";
    	for(ElectricSafetyData detail: list){
    		detail.setTenantUID(user.getTenantUID());
    		detail.setHospitalUID(user.getHospitalUID());
    		detail.setSiteUID(user.getSiteUID());
    		detail.setInstitutionUID(user.getInstitutionUID());
    		financingNum=detail.getFinancingNum();
    		if(financingNum!=null){
        		AssetInfo info=assetInfoRepository.findByFinancingNum(financingNum);
        		if(info!=null){
        			String uid=info.getUid();
            		String id=v2_PmOrderRepository.findByAssetUid(uid);
//            		detail.setAssetId(info.getId());
            		detail.setAssetUid(uid);
            		detail.setV2_PmOrderId(id);
        		}
        		try {
        			ElectricSafetyData dbData=electricSafetyDataRepository.findByFinancingNumAndCheckDate(detail.getFinancingNum(),detail.getCheckDate());
        			if(dbData!=null){
        				detail.setId(dbData.getId());
        				setDefaultValue(detail,UPDATE);
        			}
        			else{
        				setDefaultValue(detail,INSERT);
        			}
        			electricSafetyDataRepository.save(detail);
        			detail.setWarnMessage("import success");
				} catch (Exception e) {
					// TODO: handle exception
					LOGGER.info("insert electric safety data failed. Reason is "+e.getMessage());
					detail.setWarnMessage("import failed");
				}
    		}
    		else{
    			detail.setWarnMessage("import failed");
    		}
    	}
    }
    
    
    private void setDefaultValue(ElectricSafetyData detail,String type){
    	
    	if(type.equals(INSERT)){
    		detail.setCreatedBy(UserContextService.getCurrentUserAccount().getId());
        	detail.setCreatedDate(new Date());
    	}
    	detail.setLastModifiedBy(UserContextService.getCurrentUserAccount().getId());
		detail.setLastModifiedDate(new Date());
    }
	
    private List<ElectricSafetyData> analysis(String[] arrStr,String filename){
//		getProperties();
		List<ElectricSafetyData> listData=new ArrayList<ElectricSafetyData>();
		for(int i=0;i<arrStr.length;++i){
			ElectricSafetyData data=new ElectricSafetyData();
			data.setFileName(filename);
			data.setLineIndex(i+1);
			parseValues(arrStr[i],data);
			if(data.getValues()!=null){
				listData.add(data);
			}
		}
		return listData;
	}
	
	
    private String[] readFileByLine(UploadedFile file){
		try{
			String str=new String(file.getContents());
			String [] strArr=str.split("\n");
			return strArr;
		}
		catch(Exception e){
			LOGGER.info(e.getMessage());
		}
		return null;
	}
	
	private Map<String,Object> parseValues(String strLine,ElectricSafetyData data){
		strLine=" "+strLine;
		Map<String,Object> map=new HashMap<String,Object>();
		String regex="[^0-9a-zA-Z][0-9]+,";
		Pattern p=Pattern.compile(regex);
		String preKey="";
		int currentEnd=0;
		int nextStart=0;
		try {
			Matcher matcher=p.matcher(strLine);
			while(matcher.find()){
				if(currentEnd==0){
					currentEnd=matcher.end();
					preKey=getKey(matcher);
				}
				else{
					nextStart=matcher.start();
					map.put(preKey, strLine.substring(currentEnd,nextStart));
					currentEnd=matcher.end();
					preKey=getKey(matcher);
				}
			}
			map.put(preKey, strLine.substring(currentEnd,strLine.length()));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info(e.getMessage());
		}
		if(StringUtils.isNotEmpty(preKey)){
			convertValues(map,data);
			setCheckType(map.get("2").toString(), data);
//			data.setValues(convertKeys(map));
			data.setValues(map);
		}
		return map;
	}
	
	private String getKey(Matcher matcher){
		String key=matcher.group().trim().replace(",", "");
		return key;
	}
	
//	private Map<String,Object> convertKeys(Map<String,Object> map){
//		Map<String,Object> newMap=new HashMap<String,Object>();
//		String newKey="";
//		for(String key:map.keySet()){
//			if(properties.get(key)==null){
//				newKey=key;
//			}
//			else{
//				newKey=(String)properties.get(key);
//			}
//			newMap.put(newKey, map.get(key));
//		}
//		return newMap;
//	}
	
	private Map<String,Object> convertValues(Map<String,Object> map,ElectricSafetyData data){
		String strValue="";
		for(String key:map.keySet()){
			strValue=map.get(key).toString();
			//60,设备编号
			if(!StringUtils.equals(key, "60")){
				if(needRemoveTailer(strValue)){
					strValue=getNumber(strValue);
				}
			}
			//5,测试仪日期（格式：日-月-年）22.09.17 6,测试仪时间（格式：H:M:S）13:24:09
			if(StringUtils.equals(key, "5")){
				Date date=null;
				if(map.get("6")!=null){
					date=convertStringDate("dd.MM.yy H:mm:ss", strValue+" "+map.get("6").toString());
				}
				else{
					date=convertStringDate("dd.MM.yy", strValue);
				}
				data.setStorageDate(date);
			}
			//55,存储器日期（格式：日-月-年）30.10.18 56,存储器时间（格式：H:M）13:20
			else if(StringUtils.equals(key, "55")){
				Date date=null;
				if(map.get("56")!=null){
					date=convertStringDate("dd.MM.yy H:mm", strValue+" "+map.get("56").toString());
				}
				else{
					date=convertStringDate("dd.MM.yy", strValue);
				}
				data.setCheckDate(date);
			}
			//60,设备编号
			else if(StringUtils.equals(key, "60")){
				strValue=strValue.replaceAll(" ", "");
				data.setFinancingNum(strValue);
				data.setAssetInfo(assetInfoRepository.findByFinancingNum(strValue));
			}
			
//			7，——》保护接地阻抗（单位：Ω）
			else if(StringUtils.equals(key, "7")){
				data.setProtectGroundImpedance(strValue);
			}
//			8，——》保护接地阻抗参考值（<0.2Ω）
//			else if(StringUtils.equals(key, "8")){
//				data.setProtectGroundImpedanceRefVal(strValue);
//			}
//			23，——》患者漏电流（应用部分加电）（单位：μA）
			else if(StringUtils.equals(key, "23")){
				data.setPatientElectricLeakage_app(strValue);
			}
//			24，——》患者漏电流（应用部分加电）参考值（<50.0μA）
//			else if(StringUtils.equals(key, "24")){
//				data.setPatientElectricLeakageRefVal_app(strValue);
//			}
//			25，——》对地漏电流（正常状态）（单位：mA）
			else if(StringUtils.equals(key, "25")){
				data.setGroundElectricLeakage_normal(strValue);
			}
//			26，——》对地漏电流（正常状态）参考值（<0.5mA）
//			else if(StringUtils.equals(key, "26")){
//				data.setGroundElectricLeakageRefVal_normal(strValue);
//			}
//			27，——》对地漏电流（单一故障状态）（单位：mA）
//			else if(StringUtils.equals(key, "27")){
//				data.setGroundElectricLeakage_singleError(strValue);
//			}
//			28，——》对地漏电流（单一故障状态）参考值（<1.0mA）
//			else if(StringUtils.equals(key, "28")){
//				data.setGroundElectricLeakageRefVal_singleError(strValue);
//			}
//			29，——》外壳漏电流（正常状态）（单位：μA）
			else if(StringUtils.equals(key, "29")){
				data.setShellElectricLeakage_normal(strValue);
			}
//			30，——》外壳漏电流（正常状态）参考值（<100μA）
//			else if(StringUtils.equals(key, "30")){
//				data.setShellElectricLeakageRefVal_normal(strValue);
//			}
//			31，——》外壳漏电流（单一故障状态）（单位：μA）
//			else if(StringUtils.equals(key, "31")){
//				data.setShellElectricLeakage_singleError(strValue);
//			}
//			32，——》外壳漏电流（单一故障状态）参考值（<0.5mA）
//			else if(StringUtils.equals(key, "32")){
//				data.setShellElectricLeakageRefVal_singleError(strValue);
//			}
//			33，——》患者漏电流AC（正常状态）（单位：μA）
//			else if(StringUtils.equals(key, "33")){
//				data.setPatientElectricLeakageAC_normal(strValue);
//			}
//			34，——》患者漏电流AC（正常状态）参考值（<10.0μA）
//			else if(StringUtils.equals(key, "34")){
//				data.setPatientElectricLeakageRefValAC_normal(strValue);
//			}
//			35，——》患者漏电流AC（单一故障状态）（单位：μA）
//			else if(StringUtils.equals(key, "35")){
//				data.setShellElectricLeakageAC_singleError(strValue);
//			}
//			36，——》患者漏电流AC（单一故障状态）参考值（<50.0μA）
//			else if(StringUtils.equals(key, "36")){
//				data.setShellElectricLeakageRefValAC_singleError(strValue);
//			}
//			37，——》患者漏电流DC（正常状态）（单位：μA）
//			else if(StringUtils.equals(key, "37")){
//				data.setPatientElectricLeakageDC_normal(strValue);
//			}
//			38，——》患者漏电流DC（正常状态）参考值（<10.0μA）
//			else if(StringUtils.equals(key, "38")){
//				data.setPatientElectricLeakageRefValDC_normal(strValue);
//			}
//			39，——》患者漏电流DC（单一故障状态）（单位：μA）
//			else if(StringUtils.equals(key, "39")){
//				data.setShellElectricLeakageDC_singleError(strValue);
//			}
//			40，——》患者漏电流DC（单一故障状态）参考值（<50.0μA）
//			else if(StringUtils.equals(key, "40")){
//				data.setShellElectricLeakageRefValDC_singleError(strValue);
//			}
//			41，——》患者辅助电流AC（正常状态）（单位：μA）
//			else if(StringUtils.equals(key, "41")){
//				data.setPatientAssisElectricAC_normal(strValue);
//			}
//			42，——》患者辅助电流AC（正常状态）参考值（<10.0μA）
//			else if(StringUtils.equals(key, "42")){
//				data.setPatientAssisElectricRefValAC_normal(strValue);
//			}
//			43，——》患者辅助电流AC（单一故障状态）（单位：μA）
//			else if(StringUtils.equals(key, "43")){
//				data.setPatientAssisElectricAC_singleError(strValue);
//			}
//			44，——》患者辅助电流AC（单一故障状态）参考值（<50.0μA）
//			else if(StringUtils.equals(key, "44")){
//				data.setPatientAssisElectricRefValAC_singleError(strValue);
//			}
//			45，——》患者辅助电流DC（正常状态）（单位：μA）
//			else if(StringUtils.equals(key, "45")){
//				data.setPatientAssisElectricDC_normal(strValue);
//			}
//			46，——》患者辅助电流DC（正常状态）参考值（<10.0μA）
//			else if(StringUtils.equals(key, "46")){
//				data.setPatientAssisElectricRefValDC_normal(strValue);
//			}
//			47，——》患者辅助电流DC（单一故障状态）（单位：μA）
//			else if(StringUtils.equals(key, "47")){
//				data.setPatientAssisElectricDC_singleError(strValue);
//			}
//			48，——》患者辅助电流DC（单一故障状态）参考值（<50.0μA）
//			else if(StringUtils.equals(key, "48")){
//				data.setPatientAssisElectricRefValDC_singleError(strValue);
//			}
//			51，——》市电电压实际值
//			else if(StringUtils.equals(key, "51")){
//				data.setCityVoltage(strValue);
//			}
//			52，——》市电电压参考值（230V）
//			else if(StringUtils.equals(key, "52")){
//				data.setCityVoltageRefVal(strValue);
//			}
			map.put(key, strValue);
		}
		return map;
	}
	
	private Date convertStringDate(String pattern,String value){
		//yyMMdd=180130
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		Date date=new Date();
		try {
			date = format.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e.getMessage());
		}
		return date;
	}
	
	private String getNumber(String str){
		Pattern p=Pattern.compile("[^,]*[0-9]+");
		Matcher m=p.matcher(str);
		if(m.find()){
			return str.substring(m.start(), m.end());
		}
		return str;
	}
	
	private boolean needRemoveTailer(String str){
		Pattern p=Pattern.compile("[^,]*[0-9]+[^0-9]+");
		Matcher m=p.matcher(str);
		if(str.length()<40){
			if(m.find()){
				return true;
			}
		}
		return false;
	}
	
//	private void getProperties(){
//		if(properties!=null)
//			return;
//		properties=new Properties();
//		try {
//			String filePath=URLDecoder.decode(ElectricSafetyService.class.getResource("/").getPath(),"UTF-8")+"electricSafetyMapping.properties";
//			InputStream input=new FileInputStream(new File(filePath));
//			properties.load(input);
//			input.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//			LOGGER.info(e.getMessage());
//		}
//	}
	
	public void parseTableValue(Map<String, Object> tableValue,String jsonStrInstrumentData,String schemaStr,String boUuid){
		TableSchema table=null;
		if(StringUtils.isNotEmpty(schemaStr)){
			table = JSONObject.parseObject(schemaStr, TableSchema.class);
		}
		if(StringUtils.isNotEmpty(jsonStrInstrumentData)&& StringUtils.isNotEmpty(boUuid)){
			JSONObject obj=JSONObject.parseObject(jsonStrInstrumentData);
			if(obj!=null){
				ElectricSafetyData electricSafetyData=electricSafetyDataRepository.findByPmOrderId(boUuid);
				if(electricSafetyData!=null){
					for(String key:obj.keySet()){
//						if(tableValue.containsKey(key)){
//							tableValue.put(key, getInstrumentValue(obj.getString(key), electricSafetyData));
//						}
						if(table!=null){
							List<TableGroup> listTableGroup=table.getGroups();
							if(listTableGroup!=null && listTableGroup.size()>0){
								for(int i=0;i<listTableGroup.size();++i){
									List<TableItem> listTableItem=listTableGroup.get(i).getItems();
									if(listTableItem!=null && listTableItem.size()>0){
										for(int j=0;j<listTableItem.size();++j){
											TableItem temp=listTableItem.get(j);
											if(temp.getKey().equals(key)){
												tableValue.put(key, getInstrumentValue(obj.getString(key), electricSafetyData));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private Object getInstrumentValue(String attrName, ElectricSafetyData data) {
		Object result = null;
		Field[] fields = ElectricSafetyData.class.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(attrName)) {
				boolean access = field.isAccessible();
				field.setAccessible(true);
				try {
					result = field.get(data);
					field.setAccessible(access);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage());
				}
				break;
			}
		}
		return result;
	}
	
	private void setCheckType(String str,ElectricSafetyData data){
		if(str.length()==40){
			String a=str.substring(3,4);
			String b=str.substring(4,5);
			String c=a+b;
			
			if(StringUtils.equals(c, "12")){
				data.setCheckType("I B");
			}
			else if(StringUtils.equals(c, "1A")){
				data.setCheckType("I BF");
			}
			else if(StringUtils.equals(c, "52")){
				data.setCheckType("I CF");
			}
			else if(StringUtils.equals(c, "02")){
				data.setCheckType("II B");
			}
			else if(StringUtils.equals(c, "0A")){
				data.setCheckType("II BF");
			}
			else if(StringUtils.equals(c, "42")){
				data.setCheckType("II CF");
			}
			a=str.substring(18,19);
			b=str.substring(31,32);
			c=a+b;
			if(StringUtils.equals(c, "51")){
				data.setCheckType("I B");
			}
			else if(StringUtils.equals(c, "52")){
				data.setCheckType("I BF");
			}
			else if(StringUtils.equals(c, "53")){
				data.setCheckType("I CF");
			}
			else if(StringUtils.equals(c, "61")){
				data.setCheckType("II B");
			}
			else if(StringUtils.equals(c, "62")){
				data.setCheckType("II BF");
			}
			else if(StringUtils.equals(c, "63")){
				data.setCheckType("II CF");
			}
			
//			c=str.substring(33,34);
//			if(StringUtils.equals(c, "0")){
//				data.setGroupType("0组应用");
//			}
//			else if(StringUtils.equals(c, "2")){
//				data.setGroupType("2组应用");
//			}
//			else if(StringUtils.equals(c, "5")){
//				data.setGroupType("5组应用");
//			}
//			else if(StringUtils.equals(c, "10")){
//				data.setGroupType("10组应用");
//			}
		}
	}
}

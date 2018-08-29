package com.ge.apm.service.utils;

import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ImageUtils {
    private static final Logger log = Logger.getLogger(ImageUtils.class.getName());

	public static String getBase64(InputStream input){
		Base64.Encoder encoder = Base64.getEncoder();
		try {
			if(input!=null){
				byte[] b = new byte[input.available()];
				input.read(b);
				input.close();
				return encoder.encodeToString(b);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		return null;
	}
	
	public static byte[] convertPrimefacesBase64ToByte(String str){
		if(StringUtils.isNotEmpty(str)){
			int index=str.indexOf(",");
			if(index>0){
				str=str.substring(index+1, str.length());
				Decoder decoder=Base64.getDecoder();
				return decoder.decode(str);
			}
		}
		return null;
	}
}

package com.ge.apm.service.utils;

import java.io.InputStream;
import java.util.Base64;

import org.apache.log4j.Logger;

public class ImageUtils {
    private static final Logger log = Logger.getLogger(ImageUtils.class.getName());

	public static String getBase64(InputStream input){
		Base64.Encoder encoder = Base64.getEncoder();
		try {
			if(input!=null){
				byte[] b = new byte[input.available()];
				String temp = "<img src=\"data:image/jpeg;base64,";
				input.read(b);
				temp += encoder.encodeToString(b);
				temp+="\">";
				return temp;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		return null;
	}
}

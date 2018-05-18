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
				input.read(b);
				input.close();
				return encoder.encodeToString(b);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		System.out.println("xxx end getBase64");
		return null;
	}
}

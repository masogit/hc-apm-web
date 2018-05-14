package com.ge.apm.service.utils;

import java.io.InputStream;
import java.util.Base64;

import org.apache.log4j.Logger;

public class ImageUtils {
    private static final Logger log = Logger.getLogger(ImageUtils.class.getName());

	public static String getBase64(InputStream input){
		Base64.Encoder encoder = Base64.getEncoder();
		System.out.println("xxx begin getBase64");
		try {
			if(input!=null){
				byte[] b = new byte[input.available()];
				System.out.println(input.available());
				input.read(b);
				String result=encoder.encodeToString(b);
				System.out.println(result);
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		System.out.println("xxx end getBase64");
		return null;
	}
}

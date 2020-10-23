package com.ge.apm.service.zkb;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/10/23
 * @description :
 */
public class CopyByInvok {

    public static <T> T translateField(Object souce, Class<T> target) throws Exception {
        T tarObj = target.newInstance();
        Class tempClass = souce.getClass();
        List<Field> fieldList = new ArrayList();
        fieldList = Arrays.asList(tempClass.getDeclaredFields());
        for (Field f : fieldList) {
            String name = f.getName();
            if("serialVersionUID".equals(name)){
                continue;
            }
            f.setAccessible(true);
            f.set(tarObj, f.get(souce));
        }
        return tarObj;
    }
}

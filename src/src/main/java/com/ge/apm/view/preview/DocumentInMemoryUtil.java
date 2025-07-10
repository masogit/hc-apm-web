package com.ge.apm.view.preview;

import com.ge.apm.domain.dto.FileInfoResult;

import java.util.WeakHashMap;

public class DocumentInMemoryUtil {
    // 弱引用对象集合, JVM GC clean
    private static final WeakHashMap<String, FileInfoResult> weakHashMap = new WeakHashMap<>();


    public static synchronized void put(String key, FileInfoResult value){
        weakHashMap.put(new String(key), value);
    }

    public static synchronized FileInfoResult get(String key){
       return weakHashMap.get(key);
    }

    public static synchronized FileInfoResult remove(String key){
        return weakHashMap.remove(key);
    }

}

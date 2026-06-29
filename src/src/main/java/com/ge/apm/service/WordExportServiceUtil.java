package com.ge.apm.service;


import com.deepoove.poi.policy.RenderPolicy;
import com.ge.healthcare.cn.common.utils.util.WordExportServiceUtils;

import java.util.Map;

public class WordExportServiceUtil {



    public static byte[] generate(Map<String, Object> datas, byte[] templateBytes, Map<String, RenderPolicy> bindings) {
        return WordExportServiceUtils.generate(datas, templateBytes, bindings);

    }

    public static byte[] generate(Map<String, Object> datas, byte[] templateBytes, Map<String, RenderPolicy> bindings,Map<Character, RenderPolicy> plugins) {
        return WordExportServiceUtils.generate(datas, templateBytes, bindings,plugins);

    }
}

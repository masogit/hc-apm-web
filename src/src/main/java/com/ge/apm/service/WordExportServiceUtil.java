package com.ge.apm.service;


import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.service.asset.ExportTerminatedAppraisalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordExportServiceUtil {



    private static final Logger logger = LoggerFactory.getLogger(ExportTerminatedAppraisalService.class);


    /**
     * 公共的模板导出方法
     * @param datas 要渲染的数据
     * @param templateBytes 模板文件的字节数组
     * @param bindings 自定义绑定策略的映射
     * @return 导出文件的字节数组
     */
    public static byte[] generate(Map<String, Object> datas, byte[] templateBytes, Map<String, RenderPolicy> bindings) {
        if (datas == null) {
            datas = new HashMap<>();
        }
        if (bindings == null) {
            bindings = new HashMap<>();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XWPFTemplate template = compileTemplate(templateBytes, bindings)) {
            template.render(datas);
            template.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("WordExportServiceUtil error:",e);

            return new byte[0];
        }
    }

    /**
     * 编译模板
     * @param templateBytes 模板文件的字节数组
     * @param bindings 自定义绑定策略的映射
     * @return 编译后的模板对象
     * @throws IOException 读取模板文件时可能抛出的IO异常
     */
    private static XWPFTemplate compileTemplate(byte[] templateBytes, Map<String, RenderPolicy> bindings) throws IOException {
        ConfigureBuilder configureBuilder = Configure.newBuilder();
        for (Map.Entry<String, RenderPolicy> entry : bindings.entrySet()) {
            configureBuilder.bind(entry.getKey(), entry.getValue());
        }
        Configure config = configureBuilder.build();
        return XWPFTemplate.compile(new ByteArrayInputStream(templateBytes), config);
    }
}

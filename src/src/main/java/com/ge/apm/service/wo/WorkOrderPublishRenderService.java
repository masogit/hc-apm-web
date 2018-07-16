package com.ge.apm.service.wo;

import com.ge.apm.service.wo.doctemplate.WorkOrderDocxRenderService;
import com.ge.apm.view.wo.Map2XmlConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 212688574
 */

@Component
public class WorkOrderPublishRenderService {
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderPublishRenderService.class);

    @Autowired
    WorkOrderDocxRenderService workOrderDocxRenderService;

    public RenderResult render(List<Map<String, Object>> data, byte[] templateBytes) throws IOException {
        logger.info("Export work orders: " + data.size());
        return data.size() > 1 ? renderZip(data, templateBytes) : renderDocx(data, templateBytes);
    }

    private RenderResult renderDocx(List<Map<String, Object>> data, byte[] templateBytes) throws IOException {
        RenderResult renderResult = null;
        for (Map<String, Object> map : data) {
            byte[] bytes = workOrderDocxRenderService.renderDocx(map, templateBytes);
            renderResult = new RenderResult(bytes, "application/msword", encoding(getFileName(map, 1)));
        }
        return renderResult;
    }

    private RenderResult renderZip(List<Map<String, Object>> data, byte[] templateBytes) throws IOException {
        RenderResult renderResult;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(bout)) {
            int index = 1;
            for (Map<String, Object> map : data) {
                String fileName = getFileName(map, index++);
                zout.putNextEntry(new ZipEntry(fileName));
                logger.info("Adding zip entry for: " + fileName);
                byte[] bytes = workOrderDocxRenderService.renderDocx(map, templateBytes);
                IOUtils.write(bytes, zout);
                zout.closeEntry();
            }
            zout.close();
            renderResult = new RenderResult(bout.toByteArray(), "application/zip", "WorkOrderDetails.zip");
            return renderResult;
        }
    }

    private String getFileName(Map<String, Object> map, int index) {
        return "WO" + index + "-" + map.get("设备名称") + "-" + map.get("报修单号") + ".docx";
    }

    private String encoding(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //return original name if the encode is failed.
            return fileName;
        }
    }


    public class RenderResult {
        RenderResult(byte[] result, String contentType, String fileName) {
            this.result = result;
            this.contentType = contentType;
            this.fileName = fileName;
        }

        public byte[] result;
        public String contentType;
        public String fileName;
    }

    public RenderResult renderPdf(List<Map<String, Object>> data) {
        RenderResult renderResult = null;
        try {
            String woXml = getWoXml(data);

            File xsl = new File(getClass().getClassLoader().
                    getResource("../../portal/pdf/wo2fo.xsl").getPath());

            File conf = new File(getClass().getClassLoader().
                    getResource("../../portal/pdf/fop.xml").getPath());

            FopFactory fopFactory = FopFactory.newInstance(conf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                Transformer transformer = factory.newTransformer(new StreamSource(xsl));
                Source src = new StreamSource(new StringReader(woXml));
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            } finally {
                renderResult = new RenderResult(out.toByteArray(), MimeConstants.MIME_PDF, "WorkOrderDetails.pdf");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return renderResult;
    }

    private String getWoXml(List<Map<String, Object>> data) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new Map2XmlConverter(new DefaultMapper(XStream.class.getClassLoader())));
        xStream.alias("List", List.class);
        xStream.alias("map", HashedMap.class);
        return xStream.toXML(data);
    }
}

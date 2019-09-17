package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EsbUnmarshalTool {

    private static final Logger logger = Logger.getLogger(EsbUnmarshalTool.class);

    public List<AssetPerformanceRecordDelta> unmarshal(String xml) {
        try {
            return byJDom(xml);
        } catch (Exception e) {
            logger.error("Fail to unmarshal response: " + xml, e);
            throw new IllegalStateException("Fail to unmarshal response.");
        }
    }

    public List<AssetPerformanceRecordDelta> byJDom(String xml) throws JDOMException, IOException, JAXBException, XMLStreamException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document doc = saxBuilder.build(new InputSource(new ByteArrayInputStream(xml.getBytes())));
        Element rootElement = doc.getRootElement();
        List<Content> content = rootElement.getContent();
        if (content.size() > 0) {
            doc = saxBuilder.build(new ByteArrayInputStream(content.get(0).getValue().getBytes()));
            String result = doc.getRootElement().getValue();
            return byJaxb(result);
        }
        return null;
    }

    private List<AssetPerformanceRecordDelta> byJaxb(String xml) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(AssetPerformanceRecordDelta.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();

        XMLStreamReader xsr = xif.createXMLStreamReader((new ByteArrayInputStream(xml.getBytes())), "UTF-8");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        List<AssetPerformanceRecordDelta> list = new ArrayList<>();
        while (xsr.hasNext()) {
            while (xsr.isStartElement() && "Table".equals(xsr.getLocalName())) {
                list.add((AssetPerformanceRecordDelta) unmarshaller.unmarshal(xsr));
            }
            xsr.next();
        }
        for (Object object : list) {
            logger.info(object.toString());
        }
        return list;
    }
}

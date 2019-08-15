package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class EsbUnmarshalTool {

    private static final Logger logger = Logger.getLogger(EsbUnmarshalTool.class);

    public List<AssetPerformanceRecordDelta> unmarshal(String xml) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(AssetPerformanceRecordDelta.class);
        String businessResponse = null;
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes()));
        while (xsr.hasNext()) {
            if (xsr.isStartElement() && "BussinessResponse".equals(xsr.getLocalName())) {
                businessResponse = xsr.getElementText();
                break;
            }
            xsr.next();
        }

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        List<AssetPerformanceRecordDelta> list = new ArrayList<>();
        xsr = xif.createXMLStreamReader(new ByteArrayInputStream(businessResponse.getBytes()));
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

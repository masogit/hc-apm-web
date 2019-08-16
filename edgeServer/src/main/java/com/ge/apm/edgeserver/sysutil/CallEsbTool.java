package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.dao.AssetPerformanceRecordDeltaRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.camel.Body;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.cxf.helpers.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class CallEsbTool {

    @Autowired
    AssetPerformanceRecordDeltaRepository repository;

    public String callEsb() throws IOException {
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod("url");
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        int statusCode = 0;
        try {
            statusCode = client.executeMethod(postMethod);
        } catch (IOException e) {
            throw e;
        }
        System.out.println(statusCode);
        try {
            return postMethod.getResponseBodyAsString();
        } catch (IOException e) {
            throw e;
        }
    }

    public String esbResponse() throws IOException {
        return IOUtils.toString(new FileInputStream("d:\\prod-response"));
    }

    public void save(@Body String body) throws JAXBException, XMLStreamException {
        List<AssetPerformanceRecordDelta> deltas = unmarshal(body);
        repository.save(deltas);
    }

    protected List<AssetPerformanceRecordDelta> unmarshal(String body) throws JAXBException, XMLStreamException {
        EsbUnmarshalTool esbUnmarshalTool = new EsbUnmarshalTool();
        List<AssetPerformanceRecordDelta> list = esbUnmarshalTool.unmarshal(body);
        return list;
    }

}

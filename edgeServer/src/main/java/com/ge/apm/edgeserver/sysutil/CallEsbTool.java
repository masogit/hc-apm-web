package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.dao.AssetPerformanceRecordDeltaRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.cxf.helpers.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CallEsbTool {

    @Autowired
    AssetPerformanceRecordDeltaRepository repository;

    public void setQueryDate(@Headers Map<String, Object> header) {
        return;
    }

    public class esbRequestParams {
        String appCode;
        String password;
        Date startDate;
        Date endDate;
        Date time;

        public String getAppCode() {
            return appCode;
        }

        public void setAppCode(String appCode) {
            this.appCode = appCode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
    }

    public esbRequestParams getRequest(@Headers Map<String, Object> headers) {
        esbRequestParams params = new esbRequestParams();
        params.setAppCode("APP644");
        params.setPassword("d98M4lUh");
        params.setTime(new Date());
        params.setStartDate((Date) headers.get("queryDate"));
        params.setEndDate((Date) headers.get("queryDate"));
        return params;
    }

    public String mockResponse() throws IOException {
        return IOUtils.toString(new FileInputStream("d:\\prod-response"));
    }

    public void save(@Body String body, @Headers Map<String, Object> headers) throws JAXBException, XMLStreamException {
        Date queryDate = (Date) headers.get("queryDate");
        List<AssetPerformanceRecordDelta> deltas = unmarshal(body, queryDate);
        repository.save(deltas);
    }

    protected List<AssetPerformanceRecordDelta> unmarshal(String body, Date queryDate) throws JAXBException, XMLStreamException {
        EsbUnmarshalTool esbUnmarshalTool = new EsbUnmarshalTool();
        List<AssetPerformanceRecordDelta> list = esbUnmarshalTool.unmarshal(body);
        list.forEach(i -> i.setHappenDate(queryDate));
        return list;
    }

}

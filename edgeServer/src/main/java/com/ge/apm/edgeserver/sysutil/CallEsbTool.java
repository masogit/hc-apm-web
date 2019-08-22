package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.dao.AssetPerformanceRecordDeltaRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.camel.Body;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.helpers.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class CallEsbTool {

    @Autowired
    AssetPerformanceRecordDeltaRepository repository;

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

    public esbRequestParams getRequest() {
        esbRequestParams params = new esbRequestParams();
        params.setAppCode("APP564");
        params.setPassword("JZnsfqOO");
        params.setTime(new Date());
        params.setStartDate(new Date());
        params.setEndDate(DateUtils.addDays(new Date(), -3));
        return params;
    }

    public String mockResponse() throws IOException {
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

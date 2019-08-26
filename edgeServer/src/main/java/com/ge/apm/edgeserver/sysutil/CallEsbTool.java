package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.dao.AssetPerformanceRecordDeltaRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.helpers.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CallEsbTool {

    private final String HEADER_QUERY_DATE = "queryDate";

    @Autowired
    AssetPerformanceRecordDeltaRepository repository;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public void setQueryDate(@Headers Map<String, Object> headers) {
        try {
            String queryDateStr = FileUtils.readFileToString(new File("mdm_queryDate.txt"), "utf-8");
            Date lastUpdatedDate = format.parse(queryDateStr);
            headers.put(HEADER_QUERY_DATE, DateUtils.addDays(lastUpdatedDate, 1));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public esbRequestParams getRequest(@Headers Map<String, Object> headers) {
        esbRequestParams params = new esbRequestParams();
        params.setAppCode("APP644");
        params.setPassword("d98M4lUh");
        params.setTime(new Date());
        params.setStartDate((Date) headers.get(HEADER_QUERY_DATE));
        params.setEndDate((Date) headers.get(HEADER_QUERY_DATE));
        return params;
    }

    public String mockResponse() throws IOException {
        return IOUtils.toString(new FileInputStream("d:\\prod-response"));
    }

    public void save(@Body String body, @Headers Map<String, Object> headers) throws JAXBException, XMLStreamException {
        Date queryDate = (Date) headers.get(this.HEADER_QUERY_DATE);
        List<AssetPerformanceRecordDelta> deltas = unmarshal(body, queryDate);
        repository.save(deltas);
        try {
            FileUtils.writeStringToFile(new File("mdm_queryDate.txt"), format.format(queryDate), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<AssetPerformanceRecordDelta> unmarshal(String body, Date queryDate) throws JAXBException, XMLStreamException {
        EsbUnmarshalTool esbUnmarshalTool = new EsbUnmarshalTool();
        List<AssetPerformanceRecordDelta> list = esbUnmarshalTool.unmarshal(body);
        list.forEach(i -> {
            i.setHappenDate(queryDate);
            if (StringUtils.contains(i.getHospitalName(), "南部")) {
                i.setSiteUID("6f32b4f4ae0e40f3a1db627e854cece1");
            } else if (StringUtils.contains(i.getHospitalName(), "北部")) {
                i.setSiteUID("5c563184623311e7aeb25254001a1c14");
            }
        });
        return list;
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

}

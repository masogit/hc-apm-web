package com.ge.apm.edgeserver.sysutil;

import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EsbUnmarshalToolTest {

    EsbUnmarshalTool esbUnmarshalTool = new EsbUnmarshalTool();

    @Test
    public void testUnmarshal() throws IOException {
        String content = IOUtils.toString(new FileInputStream("src\\test\\resource\\mdm\\prod-response.xml"), "UTF-8");
        List<AssetPerformanceRecordDelta> unmarshal = esbUnmarshalTool.unmarshal(content);
        assertEquals(122, unmarshal.size());
    }

}
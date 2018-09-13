package com.ge.apm.service.asset;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class AssetFileDynamicTemplateTest {

    @Test
    public void buildMetaData() throws IOException {

        InputStream inputStream = AssetFileDynamicTemplateTest.class.getClassLoader().getResourceAsStream("asset/template/APM_Asset_Dynamic_Template.xlsx");

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(inputStream);
        template.buildMetaData();

    }
}
package com.ge.apm.service.asset;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VlinkParserServiceTest {

    VlinkParserService controller = new VlinkParserService();

    String YOUKU_BROWSER_LINK = "https://v.youku.com/v_show/id_XNDI4NDMzNDAwMA==.html?spm=user.playlsit.page.1";

    String YOUKU_EMBEDDED_LINK = "<iframe height=498 width=510 src='http://player.youku.com/embed/XNDI4NDMzNDAwMA==' frameborder=0 'allowfullscreen'></iframe>";

    String YOUKU_EXPECTED_LINK = "http://player.youku.com/embed/XNDI4NDMzNDAwMA==";

    String BILIBILI_LINK = "https://www.bilibili.com/video/av66394962";

    String BILIBILI_EXPECTED_LINK = "https://www.bilibili.com/video/av66394962";

    @Test
    public void testLinkParser() {
        assertEquals(null, controller.parseVlink(null));
        assertEquals(null, controller.parseVlink("http://test.com"));
        assertEquals(YOUKU_EXPECTED_LINK, controller.parseVlink(YOUKU_BROWSER_LINK));
        assertEquals(YOUKU_EXPECTED_LINK, controller.parseVlink(YOUKU_EMBEDDED_LINK));
        assertEquals(BILIBILI_EXPECTED_LINK, controller.parseVlink(BILIBILI_LINK));
    }
}
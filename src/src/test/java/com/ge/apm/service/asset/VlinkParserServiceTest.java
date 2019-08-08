package com.ge.apm.service.asset;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VlinkParserServiceTest {

    VlinkParserService controller = new VlinkParserService();

    String BROWSER_LINK = "https://v.youku.com/v_show/id_XNDI4NDMzNDAwMA==.html?spm=user.playlsit.page.1";

    String EMBEDDED_LINK = "<iframe height=498 width=510 src='http://player.youku.com/embed/XNDI4NDMzNDAwMA==' frameborder=0 'allowfullscreen'></iframe>";

    String EXPECTED_LINK = "http://player.youku.com/embed/XNDI4NDMzNDAwMA==";

    @Test
    public void testLinkParser() {
        assertEquals(null, controller.parseVlink(null));
        assertEquals(null, controller.parseVlink("http://test.com"));
        assertEquals(EXPECTED_LINK, controller.parseVlink(BROWSER_LINK));
        assertEquals(EXPECTED_LINK, controller.parseVlink(EMBEDDED_LINK));
    }
}
package com.ge.apm.service.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class VlinkParserService {
    private static final Logger log = LoggerFactory.getLogger(VlinkParserService.class);
    public String parseVlink(String vlink) {
        try {
            log.info("Parsing vlink: {}", vlink);
            if (vlink == null) {
                return null;
            }
            if (vlink.startsWith("https://v.youku.com/")) {
                return parseYoukulink(vlink);
            } else if (vlink.startsWith("https://www.bilibili.com/")) {
                return parseBilibililink(vlink);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }

    }

    private String parseYoukulink(String vlink) {
        String code = StringUtils.substringAfter(vlink, "https://v.youku.com/v_show/id_");
        if (StringUtils.isEmpty(code)) {
            code = StringUtils.substringAfter(vlink, "https://v.youku.com/v_show/id_");
        }
        if (StringUtils.isNotEmpty(code)) {
            code = StringUtils.substringBefore(code, ".html");
        }
        if (StringUtils.isEmpty(code)) {
            if (StringUtils.contains(vlink, "http://player.youku.com/embed/")) {
                code = StringUtils.substringAfter(vlink, "http://player.youku.com/embed/");
                code = StringUtils.substringBefore(code, "'");
            }
        }
        if (StringUtils.isNotEmpty(code)) {
            return "http://player.youku.com/embed/" + code;
        } else {
            return null;
        }
    }

    private String parseBilibililink(String vlink) {
        return vlink;
    }
}

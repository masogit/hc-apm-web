package com.ge.apm.service.asset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class VlinkParserService {

    public String parseVlink(String vlink) {
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
}

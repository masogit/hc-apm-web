package com.ge.apm.service.i18n;

import com.ge.apm.domain.I18nMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class I18nService {

    public String findMatchValueByMsgKey(Integer item, List<I18nMessage> msgList) {

        String msgKey = String.valueOf(item);
        for (I18nMessage msg: msgList) {
            if (msg.getMsgKey().equals(msgKey)) {
                return msg.getValueZh();
            }
        }

        return null;
    }
}

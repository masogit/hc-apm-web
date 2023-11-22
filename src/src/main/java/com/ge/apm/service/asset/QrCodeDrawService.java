/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.healthcare.cn.common.qrcode.QrCodeSupportUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author 212579464
 */
@Service
public class QrCodeDrawService {

    @Autowired
    private QrCodeLibRepository qrCodeLibRepository;

    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;

    private static final Pattern PATTERN_DIGITAL = Pattern.compile("[0-9]*");
    // LOGO路径
//    private static final String LOGO_PATH = "/logo.jpg";
//    private static final String BACKGROUND_PATH = "/background.png";

    @Value("#{wxProperties.suscribeLink}")
    private String suscribeLink;

    @Value("#{wxProperties.qrcode_web_context_url}")
    private String webContextUrl;


    public String getImageBase64(BufferedImage src) {
        Base64 encoder = new Base64();
        String res = "data:image/jpeg;base64,";
        try (ByteArrayOutputStream byteout = new ByteArrayOutputStream()) {
            ImageIO.write(src, FORMAT_NAME, byteout);
            try (ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray())) {
                byte[] bytes = new byte[bytein.available()];
                bytein.read(bytes);
                res += encoder.encodeToString(bytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(QrCodeDrawService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public BufferedImage createQrCodeTag(JSONObject qrcodeTemplate, String siteUid) throws Exception {
        String url = buildQrCodeUrl( qrcodeTemplate.getJSONObject("QrCodeElements"),siteUid);
        return QrCodeSupportUtil.createQrCodeTag(qrcodeTemplate,siteUid,url);
    }

    private String buildQrCodeUrl(JSONObject qrcodeElement, String siteUid) {
        String url = suscribeLink;
        String encodingMethod = qrcodeElement.getString("EncodingMethod");
        String code = qrcodeElement.getString("TextValue");
        if(StringUtils.isNotBlank(encodingMethod) && encodingMethod.contains("FuncPage")) {
            String qrCodeId = code;
            if(StringUtils.isNotBlank(siteUid) && StringUtils.isNotBlank(code)) {
                QrCodeLib qrCodeLib = qrCodeLibRepository.findByQrCode(code, siteUid);
                if (null == qrCodeLib) {
                    return url;
                }
                qrCodeId = qrCodeLib.getId().toString();
            }
            url = webContextUrl.concat("/q/").concat(qrCodeId);
        } else {
            url = url.concat("?").concat(code);
        }
        return url;
    }

    public BufferedImage createImageWithLogo(String content, String imgPath, boolean needCompress) throws Exception {
        BufferedImage image = QrCodeSupportUtil.createBitMatrixImage(content,QRCODE_SIZE);
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入Logo图片
        QrCodeSupportUtil.insertImage(image, imgPath, needCompress);
        return image;
    }


    public BufferedImage getQrCodeImageWithGeLogo(QrCodeLib qrCodeLib) throws Exception{
        String domain = webContextUrl;
        if(domain.indexOf("geapm.gechs.cn")>-1){
            domain = "http://apm.gehealthcloud.cn";
        }
        String url = domain.concat("/r/").concat(qrCodeLib.getId().toString());
        String logoPath = this.getClass().getClassLoader().getResource("/image/logo.jpg").getPath(); ;
        return createImageWithLogo(url, logoPath, true);
    }

    public static void main(String[] args) throws Exception {

        QrCodeTemplateElement element = new QrCodeTemplateElement();
        element.setType(QrCodeTemplateElement.QrCodeElementType.QrCode);
        element.setSize(300);
        element.setPositionX(20);
        element.setPositionY(100);
        element.setTextValue("1234123412341234");

        QrCodeTemplateElement elementText = new QrCodeTemplateElement();
        elementText.setType(QrCodeTemplateElement.QrCodeElementType.Text);
        elementText.setFontColor(Color.yellow);
        elementText.setTextValue("上海XX医院");
        elementText.setPositionX(20);
        elementText.setPositionY(5);
        elementText.setFontSize(50);

        List<QrCodeTemplateElement> eleList = new ArrayList();
        eleList.add(element);
        eleList.add(elementText);
        QrCodeDrawService service = new QrCodeDrawService();
//        BufferedImage res = service.createQrCodeTag(500, 500, eleList);

//        ImageIO.write(res, FORMAT_NAME, new File("c:/Temp/testService.jpg"));
    }
}

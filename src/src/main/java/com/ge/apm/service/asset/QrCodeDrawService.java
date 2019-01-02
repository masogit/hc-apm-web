/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.QrCodeTemplateElement;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;

/**
 *
 * @author 212579464
 */
@Service
public class QrCodeDrawService {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 78;
    // LOGO高度
    private static final int HEIGHT = 78;

    private static final Pattern PATTERN_DIGITAL = Pattern.compile("[0-9]*");
    // LOGO路径
//    private static final String LOGO_PATH = "/logo.jpg";
//    private static final String BACKGROUND_PATH = "/background.png";

    @Value("#{wxProperties.suscribeLink}")
    private String suscribeLink;

    private BufferedImage createBitMatrixImage(String content, Integer size) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size,
                hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

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

    private BufferedImage drawQrcodeWithText(String prifix, String code, int fontSize, int textPositionX, String fontName) throws Exception {
        BufferedImage image = new BufferedImage(QRCODE_SIZE, QRCODE_SIZE * 11 / 10, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(createBitMatrixImage(prifix.concat("?").concat(code), QRCODE_SIZE), 0, 0, null);

        StringBuffer textSB = new StringBuffer(code);
        if (code != null && code.length() == 16 && PATTERN_DIGITAL.matcher(code).matches()) {
            textSB.insert(4, "-");
            textSB.insert(9, "-");
            textSB.insert(14, "-");
        }
        String os = System.getProperty("os.name").toLowerCase();
        int textX = os.indexOf("windows") != -1 ? 23 : 0;
        int textY = 310;
        textX = textPositionX;
        Font font = new Font(Optional.ofNullable(fontName).orElse("TimesRoman"), Font.BOLD, fontSize);

        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawChars(textSB.toString().toCharArray(), 0, textSB.length(), textX, textY);
        g.dispose();
        return image;
    }


    public BufferedImage createQrCodeTag(JSONObject qrcodeTemplate) throws Exception {
        Integer width = qrcodeTemplate.getInteger("Width");
        Integer height = qrcodeTemplate.getInteger("Height");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        JSONObject qrcodeElement = qrcodeTemplate.getJSONObject("QrCodeElements");
        g.drawImage(
                drawQrcodeWithText(suscribeLink, qrcodeElement.getString("TextValue"), qrcodeElement.getInteger("FontSize"), qrcodeElement.getInteger("FontPositionX"), qrcodeElement.getString("FontName"))
                        .getScaledInstance(qrcodeElement.getInteger("Size"), image.getHeight() * qrcodeElement.getInteger("Size") / QRCODE_SIZE, Image.SCALE_SMOOTH),
                qrcodeElement.getInteger("PositionX"),
                qrcodeElement.getInteger("PositionY"), null
        );

        //其它文字
        qrcodeTemplate.getJSONArray("Elements").forEach(item -> {
            JSONObject object = (JSONObject) item;
            if (Strings.isNotEmpty(object.getString("TextShowValue"))) {
                Font font = new Font(object.getString("FontName"), object.getInteger("FontType"), object.getInteger("FontSize"));
                g.setFont(font);
                g.setColor(new Color(Integer.parseInt(object.getString("ColorStr"), 16)));
                g.drawChars(object.getString("TextShowValue").toCharArray(), 0, object.getString("TextShowValue").length(), object.getIntValue("PositionX"), object.getIntValue("PositionY") + object.getIntValue("FontSize"));
            }
        });

        g.dispose();
        return image;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

//    private BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
//
//        BufferedImage image = createBitMatrixImage(content, QRCODE_SIZE);
//        int width = image.getWidth();
//        int height = image.getHeight();
//        if (imgPath == null || "".equals(imgPath)) {
//            return image;
//        }
    // 插入Logo图片
//        InputStream is = this.getClass().getResourceAsStream(LOGO_PATH);
//        if (null != is) {
//            Image logoSrc = ImageIO.read(is);
//            int srcWidth = logoSrc.getWidth(null);
//            int srcHeight = logoSrc.getHeight(null);
//            if (needCompress) { // 压缩LOGO
//                if (width > WIDTH) {
//                    width = WIDTH;
//                }
//                if (height > HEIGHT) {
//                    height = HEIGHT;
//                }
//                Image compressedImage = logoSrc.getScaledInstance(srcWidth, srcHeight, Image.SCALE_SMOOTH);
//                BufferedImage tag = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
//                Graphics g = tag.getGraphics();
//                g.drawImage(compressedImage, 0, 0, null); // 绘制缩小后的图
//                g.dispose();
//                logoSrc = compressedImage;
//            }
//            // 插入LOGO
//            Graphics2D graph = image.createGraphics();
//            int x = (QRCODE_SIZE - width) / 2;
//            int y = (QRCODE_SIZE - height) / 2;
//            graph.drawImage(logoSrc, x, y, width, height, null);
//            graph.setStroke(new BasicStroke(3f));
//            graph.dispose();
//        }
//        return image;
//    }
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

    public BufferedImage createQrCodeTag(Integer width, Integer height, List<QrCodeTemplateElement> elements) throws Exception {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        QrCodeTemplateElement qrCodeItem = elements.stream().filter(item-> QrCodeTemplateElement.QrCodeElementType.QrCode.equals(item.getType())).findAny().get();
        drawQrcodeElement(g, suscribeLink, qrCodeItem);
        for (QrCodeTemplateElement item : elements) {
            if (!QrCodeTemplateElement.QrCodeElementType.QrCode.equals(item.getType())) {
                drawTextElement(g, item);
            }
        }

        g.dispose();
        return image;
    }

    public BufferedImage drawQrcodeWithText(String prifix, QrCodeTemplateElement element) throws Exception {

        String code = element.getTextValue();
        BufferedImage image = new BufferedImage(QRCODE_SIZE, QRCODE_SIZE * 11 / 10, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(createBitMatrixImage(prifix.concat("?").concat(code), QRCODE_SIZE), 0, 0, null);

        StringBuffer textSB = new StringBuffer(code);
        if (code != null && code.length() == 16) {
            textSB.insert(4, "-");
            textSB.insert(9, "-");
            textSB.insert(14, "-");
        }
        String os = System.getProperty("os.name").toLowerCase();
        int textX = os.indexOf("windows") != -1 ? 23 : 0;
        int textY = 310;
        int fontSize = element.getFontSize();
        textX = element.getFontType();
//        textX = 0 ;
        Font font = new Font(Optional.ofNullable(element.getFontName()).orElse("TimesRoman"), Font.BOLD, fontSize);
        
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawChars(textSB.toString().toCharArray(), 0, textSB.length(), textX, textY);
        g.dispose();
        return image;
    }

    public void drawTextElement(Graphics graphics, QrCodeTemplateElement element) throws Exception {
        if (null != element.getTextShowValue() && !element.getTextShowValue().isEmpty()) {
            Font font = new Font(element.getFontName(), element.getFontType(), element.getFontSize());
            graphics.setFont(font);
            graphics.setColor(element.getFontColor());
            graphics.drawChars(element.getTextShowValue().toCharArray(), 0, element.getTextShowValue().length(), element.getPositionX(), element.getPositionY() + element.getFontSize());
        }
    }

    public void drawQrcodeElement(Graphics graphics, String prifix, QrCodeTemplateElement element) throws Exception {
        BufferedImage image = drawQrcodeWithText(prifix, element);
        graphics.drawImage(
                image.getScaledInstance(element.getSize(), image.getHeight() * element.getSize() / QRCODE_SIZE, Image.SCALE_SMOOTH),
                element.getPositionX(),
                element.getPositionY(), null
        );
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
        BufferedImage res = service.createQrCodeTag(500, 500, eleList);

        ImageIO.write(res, FORMAT_NAME, new File("c:/Temp/testService.jpg"));
    }
}

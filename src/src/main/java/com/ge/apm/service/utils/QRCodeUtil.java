/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

//import java.util.UUID;

/**
 * @author 212579464
 */
@Component
public class QRCodeUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 78;
    // LOGO高度
    private static final int HEIGHT = 78;
    // LOGO路径
    private static final String LOGO_PATH = QRCodeUtil.class.getResource("/").getFile().concat("logo.jpg");
    private static final String BACKGROUND_PATH = QRCodeUtil.class.getResource("/").getFile().concat("background.png");
    private static final String LOGO_NYD = QRCodeUtil.class.getResource("/").getFile().concat("nyd_logo.png");
    private static final int NYD_SITE_ID = 17;
    private static String suscribeLink;

    //    private static QRCodeUtil qrCodeUtil;
//    private static String suscribeLink1;
    String wxSuscribeLink;

    static {
        Properties properties = new Properties();
        try {
            properties.load(QRCodeUtil.class.getResourceAsStream("/wx.properties"));
            suscribeLink = properties.getProperty("suscribeLink");
        } catch (IOException ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
                hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入Logo图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
//        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
//        graph.draw(shape);
        graph.dispose();
    }

    public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999) + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
    }

    public static String getQRCodeImageBase64(String code) {
        return getQRCodeImageBase64(code, QRCODE_SIZE);
    }

    public static String getQRCodeImageBase64(String code, int size) {
        Base64 encoder = new Base64();
        String res = "data:image/jpeg;base64,";
        try (ByteArrayOutputStream byteout = new ByteArrayOutputStream()) {
            QRCodeUtil.encodeWithBackground(code, LOGO_PATH, byteout, true, size);
            try (ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray())) {
                byte[] bytes = new byte[bytein.available()];
                bytein.read(bytes);
                res += encoder.encodeToString(bytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static void encode(String content, String imgPath, String destPath) throws Exception {
        QRCodeUtil.encode(content, imgPath, destPath, false);
    }

    public static void encode(String content, String destPath, boolean needCompress) throws Exception {
        QRCodeUtil.encode(content, null, destPath, needCompress);
    }

    public static void encode(String content, String destPath) throws Exception {
        QRCodeUtil.encode(content, null, destPath, false);
    }

    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    public static void encodeWithBackground(String content, String imgPath, OutputStream output, boolean needCompress, int size)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(suscribeLink.concat("?").concat(content), imgPath, needCompress);
        BufferedImage outimg = addBackground(BACKGROUND_PATH, image, size, content);
        ImageIO.write(outimg, FORMAT_NAME, output);
    }

    public static void encode(String content, OutputStream output) throws Exception {
        QRCodeUtil.encode(content, null, output, false);
    }

    public static void main(String[] args) throws Exception {
        //batchCode();
        insertSql();
//        String logoFile = "c:/Temp/logo.jpg";//GE logo
//        String backGroundPath = "c:/Temp/background.png";//图片背景
//        String prefix = "http://weixin.qq.com/r/nCnx6dbEDItzrZKK93yO"; //nCnx6dbEDItzrZKK93yO 公众号
//        String text = "1703307223655922";//qrCode
//		QRCodeUtil.encode(text, logoFile, "c:/Temp", true);

        //    批量生成
//        String srcFolder = "c:/Temp/180419";
//        String[] files = new File(srcFolder).list();
//        for (String item : files) {
//            System.out.println(item);
//            qrCodeBatchJob(srcFolder + File.separator + item, logoFile, backGroundPath, prefix,1000);
//        }
//        BufferedImage image = QRCodeUtil.createImage(prefix + "1703307223655922", logoFile, true);
//        BufferedImage img = addBackground(backGroundPath, image, 472, "1703307223655922");
//        ImageIO.write(img, FORMAT_NAME, new File("c:/Temp/" + text + ".jpg"));
//        
//        
    }

    public static void insertSql() throws Exception {
        String fileName = "c:/Temp/qrCode_lib.sql";
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            String srcFolder = "c:/Temp/qrCode-lib";
            String[] files = new File(srcFolder).list();
            for (String item : files) {
                String qrCode = StringUtils.substringBeforeLast(item, ".jpg");
                Integer id = Integer.parseInt(StringUtils.substring(qrCode, 5, qrCode.length()));
                String uid = UUID.randomUUID().toString().replace("-", "");
                StringBuffer sbf = new StringBuffer("INSERT INTO \"public\".\"qr_code_lib\"(\"id\",\"qr_code\", \"issue_date\", \"status\", " +
                        "\"uid\", \"tenant_uid\", \"institution_uid\", \"hospital_uid\", \"site_uid\", \"type\") VALUES (");
                sbf.append(id).append(",");
                sbf.append("'").append(qrCode).append("',");
                sbf.append("'").append("2020-10-26").append("',");
                sbf.append(1).append(",");
                sbf.append("'").append(uid).append("',");
                sbf.append("'").append("26acd70242fd42f8b807cfcbf24a1877").append("',");
                sbf.append("'").append("ba2439b62edf40dfaca0fdfd7a222ceb").append("',");
                sbf.append("'").append("ba2439b62edf40dfaca0fdfd7a222ceb").append("',");
                sbf.append("'").append("ba2439b62edf40dfaca0fdfd7a222ceb").append("',");
                sbf.append(1).append(");");
                System.out.println(sbf.toString());
                out.write(sbf.toString());
                out.newLine();
            }
        } catch (Exception e) {
            logger.error("qrcode util insertSql:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void batchCode() {
        String logoFile = "c:/Temp/logo2.jpg";//GE logo
        String prefix = "http://apm.gehealthcloud.cn/r/"; //nCnx6dbEDItzrZKK93yO 公众号
        String qrCodeFix = "202010";

        int cycleTimes = 1000;
        int numLength = 10;

        Stream.iterate(1000080600, item -> item + 1).limit(cycleTimes).map(item -> String.valueOf(item)).map(item -> {
            while (item.length() < numLength) {
                item = "0" + item;
            }
            return item;
        }).forEach(x -> {
            try {
                String qrCode = qrCodeFix + x;
                BufferedImage image = QRCodeUtil.createImage(prefix + x, logoFile, true);
                ImageIO.write(image, FORMAT_NAME, new File("c:/Temp/1000/" + qrCode + ".jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static BufferedImage addBackground(String bgPath, BufferedImage innerImg, int size, String text) throws IOException {

        StringBuffer textSB = new StringBuffer(text);
        textSB.insert(4, "-");
        textSB.insert(9, "-");
        textSB.insert(14, "-");

        String os = System.getProperty("os.name").toLowerCase();
        int textX = os.indexOf("windows") != -1 ? 85 * size / 472 : 58 * size / 472;
        int textY = 425 * size / 472;

        int sizeOfBackGround = size;
        int sizeOfCode = 320 * size / 472;
        int margin = sizeOfCode / 24;

//        int textX =  (size/2-65)*size / 472;
        BufferedImage src = ImageIO.read(new File(bgPath));
        Image background = src.getScaledInstance(sizeOfBackGround, sizeOfBackGround, Image.SCALE_SMOOTH);
        Image resizedCode = innerImg.getScaledInstance(sizeOfCode, sizeOfCode, Image.SCALE_SMOOTH);

        BufferedImage bufImg = new BufferedImage(sizeOfBackGround, sizeOfBackGround, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufImg.createGraphics();
        g.drawImage(background, 0, 0, null);

        int x = (sizeOfBackGround - sizeOfCode) / 2 + margin;
        int y = (sizeOfBackGround - sizeOfCode) / 2 + margin + 4 * margin / 4;

        int fontSize = 30 * size / 472;
        Font font = new Font("TimesRoman", Font.BOLD, fontSize);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawChars(textSB.toString().toCharArray(), 0, textSB.length(), textX, textY);

        g.drawImage(resizedCode, x, y, x + sizeOfCode - 2 * margin, y + sizeOfCode - 2 * margin, margin, margin, sizeOfCode - margin, sizeOfCode - margin, null);
        g.dispose();

        return bufImg;

    }

    public static void qrCodeBatchJob(String srcfile, String logoFile, String backGroundPath, String prefix, Integer smallCount) throws Exception {
        File src = new File(srcfile);
        if (src.isFile() && src.exists()) {
            String outputFolder = src.getAbsolutePath();
            outputFolder = outputFolder.substring(0, outputFolder.indexOf("."));
            mkdirs(outputFolder.concat("472"));
            mkdirs(outputFolder.concat("709"));
            try (InputStreamReader read = new InputStreamReader(new FileInputStream(src));
                 BufferedReader bufferedReader = new BufferedReader(read)) {
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    System.out.println(lineTxt);
//                String prefix = UUID.randomUUID().toString();
                    BufferedImage image = QRCodeUtil.createImage(prefix + "?" + lineTxt, logoFile, true);
                    if (smallCount > 0) {
                        BufferedImage imageWithbg = addBackground(backGroundPath, image, 472, lineTxt);
                        ImageIO.write(imageWithbg, FORMAT_NAME, new File(outputFolder + "472/" + lineTxt + ".jpg"));
                    } else {
                        BufferedImage imageWithbg790 = addBackground(backGroundPath, image, 709, lineTxt);
                        ImageIO.write(imageWithbg790, FORMAT_NAME, new File(outputFolder + "709/" + lineTxt + ".jpg"));
                    }
                    smallCount--;
                }
            } catch (Exception e) {
                logger.error("qrCodeBatchJob error:{}", e.getMessage());
            }
        } else {
            System.out.println("找不到指定的文件");
        }

    }

    public static String generateQrCodes(List<String> codes, Integer small, Integer big, Integer siteId) throws Exception {
        String logo = null;
        if (siteId == NYD_SITE_ID) {
            logo = LOGO_NYD;
        } else {
            logo = LOGO_PATH;
        }
        String foldName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + new Random().nextInt(1000);
        String codeExistsFileFold = System.getProperty("user.home") + "/qrCode" + foldName;
        File src = new File(codeExistsFileFold);
        String codeOutputFolder = src.getAbsolutePath() + System.getProperty("file.separator");
        System.out.println("codeOutputFolder is " + codeOutputFolder);
        mkdirs(codeOutputFolder.concat("472"));
        mkdirs(codeOutputFolder.concat("709"));
        int num = small + big;
        for (int i = 0; i < num; i++) {
            String code = codes.get(i);
            BufferedImage image = QRCodeUtil.createImage(suscribeLink + "?" + code, logo, true);
            if (i < small) {
                BufferedImage imageWithbg = addBackground(BACKGROUND_PATH, image, 472, code);
                ImageIO.write(imageWithbg, FORMAT_NAME, new File(codeOutputFolder + "472/" + code + ".jpg"));
            } else {
                BufferedImage imageWithbg790 = addBackground(BACKGROUND_PATH, image, 709, code);
                ImageIO.write(imageWithbg790, FORMAT_NAME, new File(codeOutputFolder + "709/" + code + ".jpg"));
            }
        }

        String zipPosition = System.getProperty("user.home") + System.getProperty("file.separator");
        String zipName = zipPosition + foldName + "code.zip";
        ZipCompressor zc = new ZipCompressor(zipName);
        zc.compress(codeOutputFolder.concat("472"), codeOutputFolder.concat("709"));
        return zipName;
    }

//	public static void main(String[] args) throws Exception {
//		List<String> codes = new ArrayList<String>();
//		codes.add(UUID.randomUUID().toString());
//		codes.add(UUID.randomUUID().toString());
//		codes.add(UUID.randomUUID().toString());
//		codes.add(UUID.randomUUID().toString());
//		codes.add(UUID.randomUUID().toString());
//		codes.add(UUID.randomUUID().toString());
//		generateQrCodes(codes, 1, 2,1);
//	}

}

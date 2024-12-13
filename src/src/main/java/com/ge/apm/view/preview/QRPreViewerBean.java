package com.ge.apm.view.preview;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class QRPreViewerBean implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(QRPreViewerBean.class);


    private QrCodeService qrCodeService;

    private UserAccount currentUser;

    private List<String> itemList = new ArrayList<>();

    private Map<String,StreamedContent> cacheStreamedContent = new HashMap<>();

    @PostConstruct
    public void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        qrCodeService = WebUtil.getBean(QrCodeService.class);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        itemList.clear();
        if(eventObject instanceof String){
            String qrCode =(String) eventObject;
            itemList.add(qrCode);
            return;
        }
        if(eventObject instanceof List){
            List<String> qrCode =(List<String>) eventObject;
            itemList.addAll(qrCode);
        }
    }
    public StreamedContent getImg(String code) throws IOException {
        return  getImg(code, currentUser.getSiteUID());
    }
    public StreamedContent getImg(String code, String siteUid) throws IOException {
        StreamedContent streamedContent = cacheStreamedContent.get(code);
        if(streamedContent == null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BufferedImage qrCodeImage = qrCodeService.getQrCodeImage(code, siteUid);
            ImageIO.write(qrCodeImage, "png", os);
            streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/jpeg", "qrCodes.jpeg");
            cacheStreamedContent.put(code,streamedContent);
        }else{
            ((ByteArrayInputStream)streamedContent.getStream()).reset();
        }
        return  streamedContent;
    }

    public List<String> getItemList() {
        return itemList;
    }

    public void setItemList(List<String> itemList) {
        this.itemList = itemList;
    }
}
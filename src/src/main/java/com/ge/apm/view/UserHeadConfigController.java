package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.*;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class UserHeadConfigController {
    private Logger log = Logger.getLogger(UserHeadConfigController.class.getName());

    /*接口类*/
    private OrgInfoRepository orgInfoRepository;
    private ConfigItemRepository configItemRepository;
    private ConfigItemValueRepository configItemValueRepository;

    /*属性类*/
    @Getter@Setter
    private UserAccount user;
    @Getter@Setter
    private UserHeadConfig selected;
    @Getter@Setter
    private ConfigItemValue configItemValue;
    @Getter@Setter
    private OrgInfo selectSite; //选定的所属院区

    @Getter@Setter
    private UploadedFile file;

    private CroppedImage croppedImage;

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    @Getter@Setter
    private String fileName;
    @Getter@Setter
    private String tempImageContent;
    @Getter@Setter
    private String newImageName;


    @PostConstruct
    protected void init() {
        /*接口类*/
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        configItemRepository = WebUtil.getBean(ConfigItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);

        /*属性类*/
        user = UserContextService.getCurrentUserAccount();
        selectSite = orgInfoRepository.findByUid(user.getOrgInfoUID());
        /*初始化方法*/
        loadData();
    }

    public void loadData() {
        selected = new UserHeadConfig();
        ConfigItemValue configItemHead;
        ConfigItemValue configItemLogo;
        Integer orgType = selectSite.getOrgType();
        switch (orgType) {
            case 4:
            case 3:
                //如果用户是院区及以下级别 则取选区标题配置
                configItemHead = configItemValueRepository.findFirstByKeyAndSiteUid("systemHead" ,selectSite.getSiteUID());
                configItemLogo = configItemValueRepository.findFirstByKeyAndSiteUid("systemLogo" ,selectSite.getSiteUID());
                break;
            default:
                //如果用户是院区以上级别 则取租户标题配置
                configItemHead = configItemValueRepository.findFirstByKeyAndSiteUid("systemHead" ,selectSite.getTenantUID());
                configItemLogo = configItemValueRepository.findFirstByKeyAndSiteUid("systemLogo" ,selectSite.getSiteUID());
                break;
        }
        if (configItemHead == null) {
            //如果没有则取平台默认配置
            ConfigItem configItem = configItemRepository.findByDefaultValueForKeyAndCategory("systemHead", 13);
            selected.setHeadTitle(configItem.getDefaultValue());
        } else {
            selected.setHeadTitle(configItemHead.getValue());
        }
        if (configItemLogo == null) {
            //如果没有则取平台默认配置
            ConfigItem configItem = configItemRepository.findByDefaultValueForKeyAndCategory("systemLogo", 13);
            selected.setHeadLogo(configItem.getDefaultValue());
        } else {
            selected.setHeadLogo(configItemLogo.getValue());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.file = null;
        this.croppedImage = null;
        UploadedFile file = event.getFile();
        if (file != null && file.getContents() != null && file.getContents().length > 0 && file.getFileName() != null) {
            this.file = file;
            FacesMessage msg = new FacesMessage("Successful", this.file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public String crop() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = servletContext.getRealPath("") + File.separator +
                "ui" + File.separator + "barca" + File.separator+ croppedImage.getOriginalFilename() + "cropped.jpg";

        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(croppedImage.getBytes(), 0,
                    croppedImage.getBytes().length);
            imageOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getImage() throws IOException {
        StreamedContent content = new DefaultStreamedContent(new ByteArrayInputStream(file.getContents()), file.getContentType(), file.getFileName());
        byte[] bytes = IOUtils.toByteArray(content.getStream());
        org.apache.commons.codec.binary.Base64 encoder = new Base64();
        String res = "data:" + file.getContentType() + ";base64,";
        res += encoder.encodeToString(bytes);
        return res;
    }

    public StreamedContent getCropped() {
        return new DefaultStreamedContent(new ByteArrayInputStream(this.croppedImage.getBytes())
                , file.getContentType()
        );
    }
}

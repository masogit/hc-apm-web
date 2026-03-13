package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.AdverseEventsRepository;
import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.domain.AdverseEventsFile;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.TempFileUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdverseEventBaseController extends JpaCRUDController<AdverseEvents> {

    private AdverseEventsRepository adverseEventsRepository;
    public BlobFileService blobService;
    @Getter
    @Setter
    public List<TempFileUtil> injuryPerformance;
    @Getter@Setter
    public List<TempFileUtil> mechanicalFailure;

    @Override
    protected void init() {
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        injuryPerformance = new ArrayList<>();
        mechanicalFailure = new ArrayList<>();
    }

    public AdverseEventsFile saveTempFile(TempFileUtil tempFile) {
        V2_BlobObject blobObject = blobService.uploadFile(tempFile.getInputStream(), tempFile.getFileName());
        if(blobObject == null){
            return null;
        }
        AdverseEventsFile file = new AdverseEventsFile();
        file.setCreatedDate(new Date());
        if(blobObject != null){
            file.setObjectId(blobObject.getObjectStorageId());
            file.setObjectName(blobObject.getObjectName());
            file.setObjectType(blobObject.getObjectType());
            file.setObjectSize(blobObject.getObjectSize());
        }
        return file;
    }

    public void processFile(AdverseEvents selected) {
        if (!CollectionUtils.isEmpty(injuryPerformance)) {
            List<AdverseEventsFile> harmPerformFile = CollectionUtils.isEmpty(selected.getHarmPerformFile())?new ArrayList<>():selected.getHarmPerformFile();
            injuryPerformance.forEach(v -> {
                AdverseEventsFile adverseEventsFile = saveTempFile(v);
                if(adverseEventsFile != null){
                    harmPerformFile.add(adverseEventsFile);
                }
            });
            selected.setHarmPerformFile(harmPerformFile);
        }
        if (!CollectionUtils.isEmpty(mechanicalFailure)) {
            List<AdverseEventsFile> deviceFaultPerformFile = CollectionUtils.isEmpty(selected.getDeviceFaultPerformFile())?new ArrayList<>():selected.getDeviceFaultPerformFile();
            mechanicalFailure.forEach(v -> {
                AdverseEventsFile adverseEventsFile = saveTempFile(v);
                if(adverseEventsFile != null){
                    deviceFaultPerformFile.add(adverseEventsFile);
                }
            });
            selected.setDeviceFaultPerformFile(deviceFaultPerformFile);
        }
    }

    public void exportFile(TempFileUtil tempFileUtil) {
        try {
            InputStream inputStream = tempFileUtil.getInputStream();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + tempFileUtil.getFileName());
            StreamUtils.copy(inputStream, response.getOutputStream());
            facesContext.responseComplete();
            facesContext.renderResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportFiled(AdverseEventsFile adverseEventsFile) throws IOException {
        InputStream inputStream = blobService.getBlobObjectByStorageId(adverseEventsFile.getObjectId()).getStream();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=" + adverseEventsFile.getObjectName());
        StreamUtils.copy(inputStream, response.getOutputStream());
        facesContext.responseComplete();
        facesContext.renderResponse();
    }

    public void tempInjuryPerformanceFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        injuryPerformance.add(TempFileUtil.builder()
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .contents(file.getContents())
                .build());
    }

    public void tempMechanicalFailureFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        mechanicalFailure.add(TempFileUtil.builder()
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .contents(file.getContents())
                .build());
    }

    public void removeTempHarmPerform(TempFileUtil file) {
        injuryPerformance.remove(file);
    }

    public void removeTempDeviceFaultPerform(TempFileUtil file) {
        mechanicalFailure.remove(file);
    }

    public void removeHarmPerformFile(AdverseEventsFile file) {
        selected.getHarmPerformFile().remove(file);
    }

    public void removeDeviceFaultPerformFile(AdverseEventsFile file) {
        selected.getDeviceFaultPerformFile().remove(file);
    }

    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }
}

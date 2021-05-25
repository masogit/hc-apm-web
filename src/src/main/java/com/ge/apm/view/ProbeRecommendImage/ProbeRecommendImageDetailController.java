package com.ge.apm.view.ProbeRecommendImage;

import com.ge.apm.dao.ProbeRecommendImageRepository;
import com.ge.apm.domain.ProbeRecommendImage;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ProbeRecommendImageDetailController {

    private BlobFileService blobService;
    private ProbeRecommendImageRepository dao;
    private List<V2_BlobObject> attachements;
    @Setter
    @Getter
    private ProbeRecommendImage selected;


    @PostConstruct
    protected void init() {
        blobService = WebUtil.getBean(BlobFileService.class);
        dao = WebUtil.getBean(ProbeRecommendImageRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        selected = dao.findById(Integer.valueOf(selectedId));
    }

    public List<V2_BlobObject> getAttachList(String catNo) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(catNo);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(V2_BlobObject.BO_SUB_TYPE_IMG)).collect(Collectors.toList());
        return collect;
    }

}

package com.ge.apm.view.IBUInfoImage;

import com.ge.apm.dao.IBUInfoImageRepository;
import com.ge.apm.domain.IBUInfoImage;
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
public class IBUInfoImageDetailController {

    private BlobFileService blobService;
    private IBUInfoImageRepository dao;
    private List<V2_BlobObject> attachements;
    @Setter
    @Getter
    private IBUInfoImage selected;


    @PostConstruct
    protected void init() {
        blobService = WebUtil.getBean(BlobFileService.class);
        dao = WebUtil.getBean(IBUInfoImageRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        selected = dao.findById(Integer.valueOf(selectedId));
    }

    public List<V2_BlobObject> getAttachList(String ibuOffer) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(ibuOffer);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(V2_BlobObject.BO_SUB_TYPE_IMG)  && item.getBoType().equals(V2_BlobObject.BO_TYPE_IBU_INFO) ).collect(Collectors.toList());
        return collect;
    }

}

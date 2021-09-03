package com.ge.apm.view.USProject;

import com.ge.apm.dao.USProjectRepository;
import com.ge.apm.domain.USProject;
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
public class USProjectDetailController {

    private BlobFileService blobService;
    private USProjectRepository dao;
    private List<V2_BlobObject> attachements;
    @Setter
    @Getter
    private USProject selected;


    @PostConstruct
    protected void init() {
        blobService = WebUtil.getBean(BlobFileService.class);
        dao = WebUtil.getBean(USProjectRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        selected = dao.findById(Integer.valueOf(selectedId));
    }

    public List<V2_BlobObject> getAttachList(String name) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(name);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(V2_BlobObject.BO_SUB_TYPE_IMG) && item.getBoType().equals(V2_BlobObject.BO_TYPE_US_Option) ).collect(Collectors.toList());
        return collect;
    }

}

package com.ge.apm.view.train;

import com.ge.apm.dao.TrainDetailRepository;
import com.ge.apm.dao.TrainInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.domain.dto.TrainRoleDto;
import com.ge.apm.service.asset.QrCodeDrawService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.train.TrainManageService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.qrcode.QrCodeSupportUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TrainInSiteController extends GenericCRUDUUIDController<TrainInfo> {

    private TrainInfoRepository dao;

    private TrainDetailRepository trainDetailRepository;

    private UserAccount loginUser;

    private UaaService uaaService;

    @Getter @Setter
    private StreamedContent exportFile;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(TrainInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        trainDetailRepository = WebUtil.getBean(TrainDetailRepository.class);
    }

    @Override
    protected Page<TrainInfo> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        Page<TrainInfo> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        return pageResult;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("orgUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    public String editForward() {
        return UrlEncryptController.encodeUrlParam("trainEdit?faces-redirect=true&actionName=Edit".concat("&selectedid=").concat(selected.getId()));
    }

    public void delete(){
        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(selected.getId());
        if(CollectionUtils.isNotEmpty(trainDetailList)){
            WebUtil.addErrorMessage("该主课程下仍包含子课程，必须先删除所有子课程后才能删除主课程。请先移除子课程，再尝试删除。");
            return;
        }
        dao.delete(selected);
        WebUtil.addSuccessMessage("删除成功");
    }

    @Override
    protected GenericRepositoryUUID<TrainInfo> getDAO() {
        return dao;
    }
}

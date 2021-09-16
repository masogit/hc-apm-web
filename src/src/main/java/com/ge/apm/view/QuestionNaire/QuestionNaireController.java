package com.ge.apm.view.QuestionNaire;

import com.ge.apm.dao.QuestionNaireRepository;
import com.ge.apm.domain.QuestionNaire;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

@ManagedBean
@ViewScoped
public class QuestionNaireController extends JpaCRUDController<QuestionNaire> {

    private static final long serialVersionUID = -1L;
    private QuestionNaireRepository dao;

    @Override
    protected QuestionNaireRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(QuestionNaireRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected Page<QuestionNaire> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }


    public String getEditPage(String id) {
        String operation = "QuestionNaireCreate.xhtml?actionName=Edit&selectedid=" + id;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }

    @Transactional
    public void deleteQuestionNaire(QuestionNaire imageInfo) {
        dao.delete(imageInfo);
        logger.info("delete QuestionNaire  successful. name  is:" + imageInfo.getName());
    }

}

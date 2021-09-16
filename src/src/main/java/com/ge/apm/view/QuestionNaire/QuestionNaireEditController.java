package com.ge.apm.view.QuestionNaire;

import com.ge.apm.dao.QuestionNaireRepository;
import com.ge.apm.domain.QuestionNaire;
import com.ge.apm.view.asset.AssetInfoEditController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


@ManagedBean
@ViewScoped
public class QuestionNaireEditController extends JpaCRUDController<QuestionNaire> {

    private QuestionNaireRepository dao;


    @Override
    protected void init() {
        dao = WebUtil.getBean(QuestionNaireRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(Integer.parseInt(selectedId));
            prepareEdit();
        }
        if (selected == null) {
            try {
                prepareCreate();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected QuestionNaireRepository getDAO() {
        return dao;
    }



    public String getListPageLink() {
        return "QuestionNaireList?faces-redirect=true";
    }

    /**
     * 新增
     */
    @Transactional
    public String applyChange() {
            selected.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
            selected.setCreateTime(new Date());
            this.create();
            return "QuestionNaireList?faces-redirect=true";
        }

    /**
     * 修改
     */
    @Transactional
    public String updateChange() {
        selected.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
        selected.setCreateTime(new Date());
        this.update();
        return "QuestionNaireList?faces-redirect=true";
    }



    }


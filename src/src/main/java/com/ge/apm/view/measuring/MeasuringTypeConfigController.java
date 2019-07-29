package com.ge.apm.view.measuring;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MeasuringTypeConfigRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.MeasuringTypeConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasuringTypeConfigController extends GenericCRUDUUIDController<MeasuringTypeConfig> implements Serializable, ServerEventInterface {

    private MeasuringTypeConfigRepository dao;
    private I18nMessageRepository i18nDao;

    private UserAccount currentUser;
    private String measuringItemCategory;
    private String measureGrade;
    private Integer measureCycle;


    @Override
    protected void init() {
        dao = WebUtil.getBean(MeasuringTypeConfigRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<MeasuringTypeConfig> getDAO() {
        return dao;
    }

    @Override
    protected Page<MeasuringTypeConfig> loadData(PageRequest pageRequest) {
        //this.selected = null;
        this.setSearchFilter();
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(MeasuringTypeConfig object) {
        super.onBeforeNewObject(object);

        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());

    }

    public List<I18nMessage> getmeasuringItemCategory(){
        List<I18nMessage> micList = i18nDao.findByMsgTypeOrderById("measuringItemCategory");
        if(this.selected.getMeasuringItemCategory() != null){
            I18nMessage tempData = i18nDao.findByMsgTypeAndMsgKeyOrderById("measuringItemCategory", this.selected.getMeasuringItemCategory());
            micList.add(tempData);
        }

        return micList;
    }

    public String getMeasuringItemCategory() {
        return measuringItemCategory;
    }

    public void setMeasuringItemCategory(String measuringItemCategory) {
        this.measuringItemCategory = measuringItemCategory;
    }

    @Override
    public void save() {
        super.save();
        this.selected=null;
    }

    public String getMeasureGrade() {
        return measureGrade;
    }

    public void setMeasureGrade(String measureGrade) {
        this.measureGrade = measureGrade;
    }

    public Integer getMeasureCycle() {
        return measureCycle;
    }

    public void setMeasureCycle(Integer measureCycle) {
        this.measureCycle = measureCycle;
    }
}

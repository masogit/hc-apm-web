package com.ge.apm.view.measuring;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MeasuringTypeConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.MeasuringTypeConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasuringTypeConfigController extends GenericCRUDUUIDController<MeasuringTypeConfig> implements Serializable, ServerEventInterface {

    private MeasuringTypeConfigRepository dao;
    private I18nMessageRepository i18nDao;
    private UserAccountRepository userDao;
    private UserCommonService userCommonService;

    private UserAccount currentUser;
    private String measuringItemCategory;
    private String measureGrade;
    private Integer measureCycle;
    private List<I18nMessage> measuringItemCategoryList;
    private I18nMessage i18nMessage;

    @Override
    protected void init() {
        dao = WebUtil.getBean(MeasuringTypeConfigRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);

        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<MeasuringTypeConfig> getDAO() {
        return dao;
    }

    @Override
    protected Page<MeasuringTypeConfig> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
        return dao.findBySearchFilter(searchFilters, pageRequest);

    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(MeasuringTypeConfig object) {
        super.onBeforeNewObject(object);

        object.setMeasureCycle(0);
        object.setMeasureGrade("1");
        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());

    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        this.initMeasuringItemCategoryList();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.i18nMessage = null;

        this.initMeasuringItemCategoryList();
    }

    public void initMeasuringItemCategoryList(){

        measuringItemCategoryList = FieldValueMessageController.getFieldValueList("measuringItemCategory",currentUser.getSiteId());

        List<MeasuringTypeConfig> measuringTypeConfigList = dao.findBySiteUID(currentUser.getSiteUID());
        for (MeasuringTypeConfig mtc : measuringTypeConfigList) {
            for (I18nMessage i18n : measuringItemCategoryList) {
                if(mtc.getMeasuringItemCategory().equals(i18n.getMsgKey())){
                    measuringItemCategoryList.remove(i18n);
                    break;
                }
            }
        }

        if(this.selected.getMeasuringItemCategory() != null){
            i18nMessage = i18nDao.getByMsgTypeAndMsgKeyAndSiteId("measuringItemCategory", this.selected.getMeasuringItemCategory(), currentUser.getSiteId());
            if(i18nMessage == null){
                i18nMessage = i18nDao.getByMsgTypeAndMsgKeyAndSiteId("measuringItemCategory", this.selected.getMeasuringItemCategory(), -1);
            }

            measuringItemCategoryList.add(i18nMessage);

        }

    }

    public UserAccount getUserAccount(String userUID){
        return userDao.getByUid(userUID);
    }

    public List<I18nMessage> autoCompleteMeasuringItemCategory(String query){
        List<I18nMessage> res = measuringItemCategoryList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getValueZh().contains(query)).collect(Collectors.toList());

        return res;
    }

    public List<UserAccount> autoCompleteOwner(String query) {

        List<UserAccount> userAccountList = userCommonService.getWorkerList(null,null);

        if (userAccountList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = userAccountList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());

        return res;
    }

    public String saveMeasuringTypeConfig(){

        if(selected.getLastMeteringDate() != null && selected.getNextMeteringDate() != null){
            if(selected.getNextMeteringDate().before(selected.getLastMeteringDate())){
                WebUtil.addErrorMessage("下次计量日期不能早于最后计量日期");
                return "";
            }
        }

        this.selected.setMeasuringItemCategory(this.i18nMessage.getMsgKey());
        this.selected.setMeasuringItemCategoryName(this.i18nMessage.getValue());
        this.selected.setMeasureOwnerUid(this.selected.getMeasureOwner() != null ? this.selected.getMeasureOwner().getUid() : null);
        this.selected.setMeasureOwnerId(this.selected.getMeasureOwner() != null ? this.selected.getMeasureOwner().getId() : null);

        dao.save(this.selected);
        this.selected=null;

        return "measuringTypeConfigList?faces-redirect=true";
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

    public List<I18nMessage> getMeasuringItemCategoryList() {
        return measuringItemCategoryList;
    }

    public void setMeasuringItemCategoryList(List<I18nMessage> measuringItemCategoryList) {
        this.measuringItemCategoryList = measuringItemCategoryList;
    }

    public I18nMessage getI18nMessage() {
        return i18nMessage;
    }

    public void setI18nMessage(I18nMessage i18nMessage) {
        this.i18nMessage = i18nMessage;
    }

    public String getMeasuringItemCategory() {
        return measuringItemCategory;
    }

    public void setMeasuringItemCategory(String measuringItemCategory) {
        this.measuringItemCategory = measuringItemCategory;
    }
}

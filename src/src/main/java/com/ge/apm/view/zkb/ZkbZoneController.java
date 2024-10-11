package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbZoneRepository;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbZone;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class ZkbZoneController  extends GenericCRUDUUIDController<ZkbZone> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbZoneController.class);

    private ZkbZoneRepository zkbZoneRepository;

    private String actionName;

    @Getter@Setter
    private List<ZkbZone> importFailedDataList;

    @Getter@Setter
    private UploadedFile file;

    private Map<String, String> zoneImportMap = new HashMap<>();

    private final String HEADER_REGION = "区域";
    private final String HEADER_ZONE = "Zone";

    @Override
    protected void init() {
        super.init();
        zkbZoneRepository = WebUtil.getBean(ZkbZoneRepository.class);
    }

    @Override
    protected Page<ZkbZone> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return zkbZoneRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    protected GenericRepositoryUUID<ZkbZone> getDAO() {
        return zkbZoneRepository;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        actionName = "Create";
    }


    @Override
    public void prepareEdit() {
        super.prepareEdit();
        actionName = "Edit";
    }


    public void saveData(){
        if(!saveCheck()){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("区域(Zone)已存在！","");
            return;
        }else{
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }

        zkbZoneRepository.save(selected);
    }

    public boolean saveCheck(){
        List<ZkbZone> zkbZoneList = new ArrayList<>();
        if("Edit".equals(actionName)){
            zkbZoneList = zkbZoneRepository.findByRegionAndZoneAndIdNot(selected.getRegion(), selected.getZone(), selected.getId());
        }else{
            zkbZoneList = zkbZoneRepository.findByRegionAndZone(selected.getRegion(), selected.getZone());
        }
        if(zkbZoneList != null && zkbZoneList.size() > 0){
            return false;
        }
        return true;
    }


    public void deleteData(){
        super.delete();
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        this.file = event.getFile();
        importFailedDataList = handleExcel(file, null);
        if(importFailedDataList.size()==0){
            WebUtil.addSuccessMessage("导入完成！");
        }
        else{
            WebUtil.addErrorMessage("导入失败！请修改错误数据");
        }
    }

    public List<ZkbZone> handleExcel(UploadedFile file, UserAccount user) throws IOException {
        List<ZkbZone> importFailedDataList = new ArrayList<>();

        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<String> headers = new ArrayList<>();
        headers.add(HEADER_REGION);
        headers.add(HEADER_ZONE);
        Map<String, Integer> header = ExcelParser.findHeader(sheet, headers);
        if (header.isEmpty()) {
            return null;
        }
        String region;
        String zone;

        int regionIdx = header.get(HEADER_REGION);
        int zoneIdx = header.get(HEADER_ZONE);
        List<ZkbZone> listRe = new ArrayList<ZkbZone>();
        int len = sheet.getLastRowNum();
        for (int j = header.get("HEADER_ROW") + 1; j <= len; j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            ZkbZone detail = new ZkbZone();
            try {
                detail.setExcelIdex(j + 1);
                region = StringUtils.trim(ExcelParser.getCellStringValue(row, regionIdx));
                zone = StringUtils.trim(ExcelParser.getCellStringValue(row, zoneIdx));
                if(StrUtil.isEmpty(region)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "区域不能为空；");
                    pass = false;
                }

                if (StrUtil.isEmpty(zone)) {
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "zone不能为空；");
                    pass = false;
                }

                if(!StrUtil.isEmpty(region) && !StrUtil.isEmpty(zone)){
                    List<ZkbZone> zkbZones = zkbZoneRepository.findByRegionAndZone(region,zone);
                    if(!zkbZones.isEmpty()){
                        detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "区域Zone已存在；");
                        pass = false;
                    }
                }
                detail.setRegion(region);
                detail.setZone(zone);

                if (!pass) {
                    importFailedDataList.add(detail);
                }else {
                    if(zoneImportMap.containsKey(region+"|"+zone)){
                        continue;
                    }
                    listRe.add(detail);
                    zoneImportMap.put(region + "|" + zone,region);
                }
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                importFailedDataList.add(detail);
            }
        }

        if (importFailedDataList.size() == 0) {
            zkbZoneRepository.save(listRe);
        }
        return importFailedDataList;
    }
}

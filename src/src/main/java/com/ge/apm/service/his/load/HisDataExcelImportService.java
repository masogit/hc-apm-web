package com.ge.apm.service.his.load;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.utils.ExcelDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HisDataExcelImportService {

    @Autowired
    private HisAssetGroupImportService hisAssetGroupImportService;
    @Autowired
    private HisAdministrativeOfficeImportService hisAdministrativeOfficeImportService;
    @Autowired
    private HisEquipmentImportService hisEquipmentImportService;
    @Autowired
    private HisEquipmentSfxmImportService hisEquipmentSfxmImportService;
    @Autowired
    private HisOrgInfoImportService hisOrgInfoImportService;

//    @Transactional
    public int importExcel(String fileName, ExcelDocument excelDoc, UserAccount currentUser){

        int result = 0;

        if (fileName.startsWith("his_administrative_offices")) {
            List<HisAdministrativeOffices> hisAdministrativeOffices = hisAdministrativeOfficeImportService.parse(excelDoc);
            hisAdministrativeOfficeImportService.saveAll(hisAdministrativeOffices);
            result = hisAdministrativeOffices.size();

        } else if (fileName.startsWith("his_asset_group")) {
            List<HisAssetGroup> hisAssetGroups = hisAssetGroupImportService.parse(excelDoc);
            hisAssetGroupImportService.saveAll(hisAssetGroups);
            result = hisAssetGroups.size();
        } else if (fileName.startsWith("his_equipment_connect")) {
            List<HisEquipmentConnect> hisEquipmentConnects = hisEquipmentImportService.parse(excelDoc);
            hisEquipmentImportService.saveAll(hisEquipmentConnects);
            result = hisEquipmentConnects.size();
        } else if (fileName.startsWith("his_equipment_sfxm")) {
            List<HisEquipmentSfxm> hisEquipmentSfxms = hisEquipmentSfxmImportService.parse(excelDoc);
            hisEquipmentSfxmImportService.saveAll(hisEquipmentSfxms);
            result = hisEquipmentSfxms.size();
        } else if (fileName.startsWith("org_info")) {
            List<OrgInfo> orgInfos = hisOrgInfoImportService.parse(excelDoc, currentUser);
            hisOrgInfoImportService.saveAll(orgInfos);
            result = orgInfos.size();
        }

        return result;
    }

}

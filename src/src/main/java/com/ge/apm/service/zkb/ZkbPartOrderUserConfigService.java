package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.ZkbPartOrderUserConfig;
import com.ge.apm.domain.ZkbPartOrderUserConfigVO;
import com.ge.apm.service.asset.ZkbPartOrderUserConfigRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 503202755 2020/9/25
 */
@Component
public class ZkbPartOrderUserConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbPartOrderUserConfigService.class);

    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_USER_CONFIG_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_USER_CONFIG_IMPORT_MAX_ROW = 5000;

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_USER_CONFIG_DATA_ROW = 2;

    /**
     * excel模板名称
     */
    public static final String EXCEL_TEMPLATE_NAME="Part_Order_User_Config_Template.xlsx";

    /**
     * 目录地址
     */
    public static final String USER_CONFIG_PATH="/portal/zkb/partOrderUserConfig/";

    /**
     * excel sheet  KPI项
     */
    public static final String SHEET_SERVICE_KEY_USER_CONFIG_INDEX = "part_order_user_config_list";

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public static List<I18nMessage> listI18nMessage = FieldValueMessageController.getFieldValueListByMsgType("zkbPoStepsRole");



    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getConfigMapFromTemplate(ExcelDocument doc) throws Exception {

        Map<String, String> listI18nMessageMap =
                ZkbPartOrderUserConfigService.listI18nMessage.stream().collect(Collectors.toMap(I18nMessage::getValueZh,
                        I18nMessage::getMsgKey));
        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_USER_CONFIG_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_KEY_USER_CONFIG_INDEX).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_KEY_USER_CONFIG_INDEX, EXCEL_USER_CONFIG_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbPartOrderUserConfigVO zkbPartOrderUserConfigVO = new ZkbPartOrderUserConfigVO();
            zkbPartOrderUserConfigVO.setLineNum(i);
            ZkbPartOrderUserConfigRow zkbPartOrderUserConfigRow = new ZkbPartOrderUserConfigRow(zkbPartOrderUserConfigVO,rowMap);
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_systemId"), "setId");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_apm"), "setLoginName");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_name"), "setName");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_email"), "setEmail");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_phone"), "setPhone");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_sso"), "setSso");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_region"), "setRegion");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_type"), "setConfigType");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_propertyType"), "setAssetType");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_remark"), "setRemark");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_orderType"), "setOrderType");
            zkbPartOrderUserConfigRow.setFieldStringValue(WebUtil.getMessage("zkbKeyUserConfig_holiday"), "setIsVacation");
            zkbPartOrderUserConfigRow.setFieldStringValue("是否删除", "setDelFlag");
            if(zkbPartOrderUserConfigVO.isNull()){
                break;
            }
            if(StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getConfigType()))
            {
                if(listI18nMessageMap.containsKey(zkbPartOrderUserConfigVO.getConfigType()))
                {
                    zkbPartOrderUserConfigVO.setConfigType(listI18nMessageMap.get(zkbPartOrderUserConfigVO.getConfigType()));
                }else
                    {
                        zkbPartOrderUserConfigVO.setConfigType("error");
                    }
            }
            if("是".equals(zkbPartOrderUserConfigVO.getIsVacation()))zkbPartOrderUserConfigVO.setIsVacation("t");
            if("否".equals(zkbPartOrderUserConfigVO.getIsVacation()))zkbPartOrderUserConfigVO.setIsVacation("f");
            map.put("zkbPartOrderUserConfigVO", zkbPartOrderUserConfigVO);
            map.put("rowData", rowMap);
            map.put("status", zkbPartOrderUserConfigVO.getId()==null ? ZkbPartOrderUserConfigService.ImportStatus.New.name() : ZkbPartOrderUserConfigService.ImportStatus.Exist.name());
            map.put("hashCode", zkbPartOrderUserConfigVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getKbiMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }
    /**
     * 导入数据
     * @param importUserConfigMapList 用户配置信息
     */
    public void importData(List<Map<String, Object>> importUserConfigMapList) {
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.now();
        boolean allDateRight = true;
        int row = 0;
        try {
            for (Map<String, Object> item : importUserConfigMapList) {
                ++row;
                if(checkData(item)) {
                    ZkbPartOrderUserConfigVO zkbPartOrderUserConfigVO = (ZkbPartOrderUserConfigVO)item.get("zkbPartOrderUserConfigVO");
                    ZkbPartOrderUserConfig zkbPartOrderUserConfig = new ZkbPartOrderUserConfig();
                    BeanUtils.copyProperties(zkbPartOrderUserConfigVO, zkbPartOrderUserConfig);
                    zkbPartOrderUserConfigList.add(zkbPartOrderUserConfig);
                }else
                    {
                        allDateRight=false;
                    }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
        if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfigList)&&allDateRight)
        {
            for(ZkbPartOrderUserConfig item:zkbPartOrderUserConfigList)
            {
                if(StringUtils.isNotBlank(item.getId())&&"是".equals(item.getDelFlag()))
                {
                    this.zkbPartOrderUserConfigRepository.delete(item.getId());
                    continue;
                }
                if(StringUtils.isBlank(item.getId())&&"是".equals(item.getDelFlag()))
                    continue;
                if(StringUtils.isNotBlank(item.getId()))
                {
                    ZkbPartOrderUserConfig temple = this.zkbPartOrderUserConfigRepository.findById(item.getId());
                    temple.setUsBackUpId(temple.getUsBackUpId());
                    temple.setUsBackUpName(temple.getUsBackUpName());
                }
                this.zkbPartOrderUserConfigRepository.save(item);
            }
        }
    }

    public List<Map<String, Object>> createExportPartOrderUserConfigData(List<ZkbPartOrderUserConfig> zkbFePartOrderList){
        Map<String, String> listI18nMessageMap =
                ZkbPartOrderUserConfigService.listI18nMessage.stream().collect(Collectors.toMap(I18nMessage::getMsgKey,
                        I18nMessage::getValueZh));
        List<Map<String, Object>> zkbPartOrderList = new ArrayList<>();
        for (ZkbPartOrderUserConfig zkbPartOrderUserConfig : zkbFePartOrderList) {
            Map<String, Object> orderHistoryMap = new HashMap<>();
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_systemId"),zkbPartOrderUserConfig.getId());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_apm"),zkbPartOrderUserConfig.getLoginName());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_name"),zkbPartOrderUserConfig.getName());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_email"),zkbPartOrderUserConfig.getEmail());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_phone"),zkbPartOrderUserConfig.getPhone());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_sso"),zkbPartOrderUserConfig.getSso());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_region"),zkbPartOrderUserConfig.getRegion());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_type"),listI18nMessageMap.get(zkbPartOrderUserConfig.getConfigType()));
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_propertyType"),zkbPartOrderUserConfig.getAssetType());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_usBackUp"),zkbPartOrderUserConfig.getUsBackUpName());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_remark"),zkbPartOrderUserConfig.getRemark());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_orderType"),zkbPartOrderUserConfig.getOrderType());
            if("t".equals(zkbPartOrderUserConfig.getIsVacation()))
            {
                orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_holiday"),"是");
            }else
            {
                orderHistoryMap.put(WebUtil.getMessage("zkbKeyUserConfig_holiday"),"否");
            }
            orderHistoryMap.put("是否删除","否");
            zkbPartOrderList.add(orderHistoryMap);
        }
        return zkbPartOrderList;
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> orderHistoryMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : orderHistoryMapList) {
            doc.writeRowData(SHEET_SERVICE_KEY_USER_CONFIG_INDEX, 1, dataRow++, item);
        }
    }

    /**
     * 检查数据是否合法
     * 1.loginName 登录账号不能为空
     * 2.email 邮箱不能为空
     * 3.configType 是否正确
     * 4.assetType 是否正确
     * @param item
     * @return
     */
    public boolean checkData(Map<String, Object> item) {
        boolean isValidateData = true;
        ZkbPartOrderUserConfigVO zkbPartOrderUserConfigVO = (ZkbPartOrderUserConfigVO)item.get("zkbPartOrderUserConfigVO");
        ZkbPartOrderUserConfigRow.clearErrMessage(item);
        boolean checkExistName = true;
        if(StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getId()))
        {
            ZkbPartOrderUserConfig zkbPartOrderUserConfig = this.zkbPartOrderUserConfigRepository.findById(zkbPartOrderUserConfigVO.getId());
            if(zkbPartOrderUserConfig==null)
            {
                item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
                ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_systemIdError"));
                isValidateData = false;
                checkExistName=false;
            }
        }
        if(StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getLoginName()))
        {
            if(!("OrderExtraPending".equals(zkbPartOrderUserConfigVO.getConfigType())||"OrderPending".equals(zkbPartOrderUserConfigVO.getConfigType())))
            {
                if(checkExistName) {
                    List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = null;
                    if (StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getId())) {
                        zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginNameAndIdNot(zkbPartOrderUserConfigVO.getLoginName(), zkbPartOrderUserConfigVO.getId());
                    } else {
                        zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginName(zkbPartOrderUserConfigVO.getLoginName());
                    }
                    if (CollectionUtils.isNotEmpty(zkbPartOrderUserConfigList)) {
                        item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
                        ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                        isValidateData = false;
                    }
                }
            }
        }else
            {
                item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
                ZkbPartOrderUserConfigRow.addErrMessage(item, "APM用户名"+WebUtil.getMessage("ValidationRequire"));
                isValidateData = false;

            }
        Map<String, String> listI18nMessageMap =
                ZkbPartOrderUserConfigService.listI18nMessage.stream().collect(Collectors.toMap(I18nMessage::getMsgKey,
                        I18nMessage::getValueZh));
        if(StringUtils.isBlank(zkbPartOrderUserConfigVO.getConfigType()))
        {
            item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
            ZkbPartOrderUserConfigRow.addErrMessage(item, "类型"+WebUtil.getMessage("ValidationRequire"));
            isValidateData = false;
        }else if(!listI18nMessageMap.containsKey(zkbPartOrderUserConfigVO.getConfigType()))
        {
            item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
            ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_configTypeError"));
            isValidateData = false;
        }
        Map<String,String> assetTypeMap = new HashMap<>();
        assetTypeMap.put("US","US");
        assetTypeMap.put("CT","CT");
        assetTypeMap.put("XR","XR");
        if(StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getAssetType())&&!assetTypeMap.containsKey(zkbPartOrderUserConfigVO.getAssetType()))
        {
            item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
            ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_assetTypeError"));
            isValidateData = false;
        }
        return isValidateData;
    }
    /**
     * 整理错误信息
     * @param importUserConfigMapList
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importUserConfigMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importUserConfigMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbPartOrderUserConfigService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbPartOrderUserConfigService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }
}

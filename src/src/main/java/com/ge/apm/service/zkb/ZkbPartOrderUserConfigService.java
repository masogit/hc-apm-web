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

    public static List<I18nMessage> listI18nMessage = FieldValueMessageController.getFieldValueListByMsgType("zkbPoSteps");



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
        listI18nMessageMap.put("400公用邮箱","OrderCommon");
        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_USER_CONFIG_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_KEY_USER_CONFIG_INDEX).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_KEY_USER_CONFIG_INDEX, EXCEL_USER_CONFIG_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbPartOrderUserConfigVO zkbPartOrderUserConfigVO = new ZkbPartOrderUserConfigVO();
            zkbPartOrderUserConfigVO.setLineNum(i);
            ZkbPartOrderUserConfigRow zkbPartOrderUserConfigRow = new ZkbPartOrderUserConfigRow(zkbPartOrderUserConfigVO,rowMap);
            zkbPartOrderUserConfigRow.setFieldStringValue("账号", "setLoginName");
            zkbPartOrderUserConfigRow.setFieldStringValue("名称", "setName");
            zkbPartOrderUserConfigRow.setFieldStringValue("邮箱", "setEmail");
            zkbPartOrderUserConfigRow.setFieldStringValue("电话", "setPhone");
            zkbPartOrderUserConfigRow.setFieldStringValue("用户SSO", "setSso");
            zkbPartOrderUserConfigRow.setFieldStringValue("区域", "setRegion");
            zkbPartOrderUserConfigRow.setFieldStringValue("类型", "setConfigType");
            zkbPartOrderUserConfigRow.setFieldStringValue("资产类型", "setAssetType");
            zkbPartOrderUserConfigRow.setFieldStringValue("备注", "setRemark");
            zkbPartOrderUserConfigRow.setFieldStringValue("订单类型", "setOrderType");
            zkbPartOrderUserConfigRow.setFieldStringValue("是否休假", "setIsVacation");
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
        LocalDateTime startTime = LocalDateTime.now();
        int row = 0;
        try {
            for (Map<String, Object> item : importUserConfigMapList) {
                ++row;
                if(checkData(item)) {
                    ZkbPartOrderUserConfigVO zkbPartOrderUserConfigVO = (ZkbPartOrderUserConfigVO)item.get("zkbPartOrderUserConfigVO");
                    ZkbPartOrderUserConfig zkbPartOrderUserConfig = new ZkbPartOrderUserConfig();
                    BeanUtils.copyProperties(zkbPartOrderUserConfigVO, zkbPartOrderUserConfig);
                    this.zkbPartOrderUserConfigRepository.save(zkbPartOrderUserConfig);
                }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
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
        if(StringUtils.isNotBlank(zkbPartOrderUserConfigVO.getLoginName()))
        {
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginName(zkbPartOrderUserConfigVO.getLoginName());
            if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfigList))
            {
                item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
                ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                isValidateData = false;
            }
        }
        if(StringUtils.isBlank(zkbPartOrderUserConfigVO.getEmail()))
        {
            item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
            ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_emailError"));
            isValidateData = false;
        }
        Map<String, String> listI18nMessageMap =
                ZkbPartOrderUserConfigService.listI18nMessage.stream().collect(Collectors.toMap(I18nMessage::getMsgKey,
                        I18nMessage::getValueZh));
        listI18nMessageMap.put("OrderCommon","400公用邮箱");
        if(!listI18nMessageMap.containsKey(zkbPartOrderUserConfigVO.getConfigType()))
        {
            item.put("status", ZkbPartOrderUserConfigService.ImportStatus.Failure);
            ZkbPartOrderUserConfigRow.addErrMessage(item, WebUtil.getMessage("zkbKeyUserConfig_configTypeError"));
            isValidateData = false;
        }
        Map<String,String> assetTypeMap = new HashMap<>();
        assetTypeMap.put("CS","CS");
        assetTypeMap.put("CT","CT");
        assetTypeMap.put("XR","XR");
        if(!assetTypeMap.containsKey(zkbPartOrderUserConfigVO.getAssetType()))
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

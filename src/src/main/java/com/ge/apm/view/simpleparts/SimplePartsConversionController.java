package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.util.ArrayList;
import java.util.List;

import static com.ge.apm.service.constant.SimplePartsConstant.*;

@ManagedBean
@ViewScoped
public class SimplePartsConversionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsConversionController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private SimplePartsWarehouseRepository partsWarehouseRepository;

    @PostConstruct
    protected void init()  {
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
    }

    public String getWareHouseName(String wareHouseId){
        if(wareHouseId.isEmpty()){
            return "";
        }else{
            SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.findById(wareHouseId);
            return partsWarehouse.getName();
        }
    }

    public String partsFormStatus(int status, String type) {
        if (StringUtils.equals(type, "stockIn")) {
            if (status == PARTS_TRANSACTIONED) {
                return PARTS_STATUS_IN_STOCK;
            }
            if (status == PARTS_NOT_TRANSACTIONED) {
                return PARTS_STATUS_NOT_IN_STOCK;
            }
        }
        if (StringUtils.equals(type, "stockOut")) {
            if (status == PARTS_TRANSACTIONED) {
                return PARTS_STATUS_OUT_STOCK;
            }
            if (status == PARTS_NOT_TRANSACTIONED) {
                return PARTS_STATUS_NOT_OUT_STOCK;
            }
        }
        if (StringUtils.equals(type, "approve")) {
            if (status == ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE) {
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME;
            }
            if (status == ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE) {
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME;
            }
            if (status == ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING) {
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING_NAME;
            }
        }
        return "";
    }

    public String getWarehouseStatusName(Integer status) {
        if (null == status) {
            return null;
        }

        if (WAREHOUSE_DISABLED == status) {
            return WAREHOUSE_DISABLED_NAME;
        } else {
            return WAREHOUSE_ENABLED_NAME;
        }

    }

    public List<String> getRequestTypeNameList() {
        List<String> resultList = new ArrayList<>();
        resultList.add(SimplePartsConstant.PARTS_REQUEST_FORM_NAME);
        resultList.add(SimplePartsConstant.PARTS_PURCHASE_FORM_NAME);
        resultList.add(SimplePartsConstant.PARTS_TRANSFER_FORM_NAME);
        resultList.add(SimplePartsConstant.PARTS_PHYSICAL_COUNT_FORM_NAME);
        resultList.add(SimplePartsConstant.PARTS_RETURN_FORM_NAME);

        return resultList;
    }

}

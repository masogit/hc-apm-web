package com.ge.apm.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 503310146
 */
@Data
public class ArrivalItemImport implements Serializable {
    private static final long serialVersionUID = -1647877151547239554L;

    private Integer excelRow;

    private String errMsg;

    @ExcelProperty("UUID")
    private String id;

    @ExcelProperty("到货单名称/编号")
    private String name;

    @ExcelProperty("编号")
    private String itemNum;

    @ExcelProperty("资产名称")
    private String assetName;

    @ExcelProperty("安装工时")
    private String workHours;

    @ExcelProperty("所属院区")
    private String site;

    private String siteUID;

    @ExcelProperty("所属科室")
    private String clinicalDept;

    private String clinicalDeptUID;

    @ExcelProperty("当前状态")
    private String installStatus;

    @ExcelProperty("主设备")
    private String parentAsset;

    private Integer parentAssetId;

    @ExcelProperty("设备别名")
    private String aliasName;

    @ExcelProperty("制造商序列号")
    private String serialNum;

    @ExcelProperty("设备编号（院方）")
    private String departNum;

    @ExcelProperty("资产编号")
    private String financingNum;

    @ExcelProperty("二维码")
    private String qrCode;

    @ExcelProperty("SystemID/厂商报修ID")
    private String systemId;

    @ExcelProperty("条码")
    private String barCode;

    @ExcelProperty("院内IT系统编号")
    private String modalityId;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("出厂日期")
    private Date manufactDate;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("启用日期")
    private Date installDate;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("质保截止日期")
    private Date factoryWarrantyDate;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("维保截止日期")
    private Date warrantyDate;

    @ExcelProperty("维修负责人")
    private String assetOwner;

    private String assetOwnerUID;

    @ExcelProperty("维修负责人2")
    private String assetOwner2;

    private String assetOwnerUID2;

    @ExcelProperty("维修负责人3")
    private String assetOwner3;

    private String assetOwnerUID3;

    @ExcelProperty("使用责任人")
    private String clinicalOwner;

    private String clinicalOwnerUid;

    @ExcelProperty("安装位置编码")
    private String locationCode;

    @ExcelProperty("安装位置")
    private String locationName;

    @ExcelProperty("备注")
    private String comments;

    @ExcelProperty("资产分类")
    private String assetGroup;

}

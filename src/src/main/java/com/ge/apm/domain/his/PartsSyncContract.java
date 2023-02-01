package com.ge.apm.domain.his;

import com.ge.apm.service.utils.ApmConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 同步环球的备件合同表
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartsSyncContract implements Serializable {

    private static final long serialVersionUID = -3219107586626148069L;

    private String contractNum;

    private String customerName;


    public static final PartsSyncContract developerUsedContract =
            new PartsSyncContract(ApmConstant.DEVELOPER_USE_CONTRACT_NUM,"开发用客户");

}

package com.ge.apm.dao.mapper;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 手术室绩效设备MyBatis Mapper
 * 关联查询 surgery_anesthesia_equipment_info + asset_info + surgery_anesthesia_data_result
 */
public interface SurgeryAnesthesiaEquipmentInfoMapper {

    List<SurgeryAnesthesiaEquipmentInfo> selectList(Map<String, Object> param);

    Long selectListCount(Map<String, Object> param);

    int updateBatch(@Param("uids") List<String> uids,
                    @Param("displayName") String displayName,
                    @Param("positiveRate") BigDecimal positiveRate);
}

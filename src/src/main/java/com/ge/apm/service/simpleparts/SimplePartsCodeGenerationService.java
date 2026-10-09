package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/16
 * @descript 备件明细Service
 * @package com.ge.apm.service.spareparts
 */
@Service
public class SimplePartsCodeGenerationService {

    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;

    /**
     * @param
     * @return java.lang.String
     * @date 2020/7/15 9:57
     * 调用微服务接口初始化编号
     */
    @Transactional(readOnly = true)
    public String getNextRequestCode(Integer type) {
        try {
            DateTime now = new DateTime();
            String prefix = (type==1) ? "SL" : "SG" ;
            String year = now.toString("yyyyMM");
            int max= 999999;

            String maxFinacingNum = "";//当前最大值的str
            synchronized (this){
                maxFinacingNum = partsRequestRepository.fetchMaxAssetFinacingNum(prefix+year,type);
                if(StringUtils.isEmpty(maxFinacingNum)){
                    return  prefix+year + "000001";
                }
                int finacingNum = 0;//当前最大数值
                if(finacingNum > max){
                    return "error:无法生成资产编号，已达到上限。";
                }
                finacingNum = Integer.parseInt(maxFinacingNum.substring(8));
                String num = String.format("%06d", ++finacingNum);
                return prefix+year+num;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

package webapp.framework.util;

import com.ge.apm.dao.AssetDepartmentConvertRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class GenerateOrderNumUtil {
    public static final String  TRANSFER_OUT_BOUND_ORDER_NUM_PREFIX  = "DB";

    @Autowired
    private AssetDepartmentConvertRepository assetDepartmentConvertRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    //生成转科出库单号
    public String autoGenerateOutBoundOrderNum(String orgUid) {
        OrgInfo byUid = orgInfoRepository.getByUid(orgUid);
        String hospitalUID = byUid.getHospitalUID();
        OrgInfo hospitalOrg = orgInfoRepository.getByUid(hospitalUID);
        DateTime now = new DateTime();
        String prefix = TRANSFER_OUT_BOUND_ORDER_NUM_PREFIX;
        String orgCOde=hospitalOrg.getOrgCode()!=null?hospitalOrg.getOrgCode():"002";
        String year = now.toString("yyyyMMdd");
        synchronized (this) {
            int count = assetDepartmentConvertRepository.selectCurrentFinishCount(prefix + orgCOde + year);
            if (count == 0) {
                return prefix + orgCOde + year + "001";
            }
            count=count+1;
            DecimalFormat df = new DecimalFormat("000");
            String num = df.format(count);
            return prefix + orgCOde + year + num;
        }
    }

}

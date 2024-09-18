package com.ge.apm.domain.pure;

import java.util.Date;

/**
 * Created by lindai on 2019/1/30.
 */
public interface ServiceRequestProjection {
    String getId();
    Integer getCasePriority();
    Date getRequestTime();
    String getRequestReason();
    String getRequestorName();
}

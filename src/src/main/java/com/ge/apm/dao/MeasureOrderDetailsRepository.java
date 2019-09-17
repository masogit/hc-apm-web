package com.ge.apm.dao;

import com.ge.apm.domain.MeasureOrderRuleItem;
import com.ge.apm.domain.MeasuringOrderDetail;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MeasureOrderDetailsRepository extends GenericRepository<MeasuringOrderDetail> {

	public MeasuringOrderDetail getById(String id);

}

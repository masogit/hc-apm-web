package com.ge.apm.service.mvstransaction;

import com.ge.apm.dao.mapper.MvsTransactionWorkMapper;
import com.ge.apm.domain.EHomeTransactionWorkOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MvsTransactionWorkService {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MvsTransactionWorkMapper mvsTransactionWorkMapper;
    public List<EHomeTransactionWorkOrder> loadData(Map<String, Object> params ) {
        return mvsTransactionWorkMapper.loadData(params);
    }


}

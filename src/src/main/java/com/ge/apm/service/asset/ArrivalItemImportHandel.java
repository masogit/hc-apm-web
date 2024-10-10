package com.ge.apm.service.asset;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ge.apm.domain.dto.ArrivalItemImport;

import java.util.List;

/**
 * @author 503310146
 */
public class ArrivalItemImportHandel implements ReadListener<ArrivalItemImport> {

    List<ArrivalItemImport> importList;

    public ArrivalItemImportHandel (List<ArrivalItemImport> importList) {
        this.importList = importList;
    }

    @Override
    public void invoke(ArrivalItemImport arrivalItemImport, AnalysisContext analysisContext) {
        importList.add(arrivalItemImport);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}

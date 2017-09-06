package com.ge.apm.web.chart;

import com.ge.apm.domain.BatchAssetExam;
import com.ge.apm.service.analysis.AssetExamDataAggregator;
import com.ge.apm.service.analysis.AssetExamDataService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Query;

/**
 * Created by lsg on 7/3/2017.
 *
 */
@Controller
@RequestMapping("/assetExam")
public class AssetExamController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AssetExamDataService assetExamDataService;
    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;

    @RequestMapping(value = "/assetSummitFromTo", method = RequestMethod.PUT)
    @ResponseBody
    public String aggreFromTo(@RequestBody BatchAssetExam batchAssetExam) {

        return  assetExamDataService.aggregateAssetSummitRangeDay(batchAssetExam);
    }

    @RequestMapping(value = "/examSummitFromTo", method = RequestMethod.PUT)
    @ResponseBody
    public String aggreExamFromTo(@RequestBody BatchAssetExam batchAssetExam) {

        // assetExamDataAggregator.aggregateForAssetSumit(currentDate);
        return  assetExamDataService.aggregateExamSumitRangeDay(batchAssetExam);
    }

    @RequestMapping(value = "/assetSummitByDay", method = RequestMethod.PUT)
    @ResponseBody
    public String aggreByDay(@RequestBody BatchAssetExam batchAssetExam) {
        logger.info("----aggreFromTo--batchAssetExam--bODY->"+ batchAssetExam.getCalDay());
        return  assetExamDataService.assetAssetSumitAggregatorByday(batchAssetExam);
    }

    @RequestMapping(value = "/rating", method = RequestMethod.PUT)
    @ResponseBody
    public String calRating() throws Exception {
        return  assetExamDataService.calRating();

    }
    // entry for pro  http...geapm/web/assetExam/assetSummitFromToLinks?from=2017-08-17&to=2017-09-05
    @RequestMapping(value = "/assetSummitFromToLinks", method = RequestMethod.GET )
    public @ResponseBody void getData(@RequestParam(name="from",required = true) @DateTimeFormat(pattern="yyyy-mm-dd") String from, @RequestParam(name="to",required = true) @DateTimeFormat(pattern="yyyy-mm-dd") String to) {
        BatchAssetExam ba = new BatchAssetExam();
        ba.setFrom(from);
        ba.setTo(to);
        assetExamDataService.aggregateAssetSummitRangeDay(ba);
    }



}
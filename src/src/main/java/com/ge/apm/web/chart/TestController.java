package com.ge.apm.web.chart;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.AssetFinacingNum;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.wo.WorkflowService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import webapp.framework.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/asset")
public class TestController {

	@Autowired
	private Environment env;

	@Autowired
	ConfigUtils configUtils;

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	WorkflowService workflowService;

	
	@Autowired
	private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

	private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET )
    @ResponseBody
    public List<UserAccount> getData(@PathVariable String id) {
//    	logger.info("id is {}",id);
//        return userAccountService.getUserAccount();
    	return null;
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET )
    @ResponseBody
    public List<UserAccount> getUsers() {
    	return null;
//        return userAccountService.getUserAccount();
    }



    @RequestMapping(value = "/isExists", method = RequestMethod.GET )
    public String isExists() {
    		String _openId = "otf1us5X8vbPlgpSK2C7aXKPxu6Q111";
    		System.out.println(userAccountService.isExists(_openId));
    		return "success";
    }
    
    @RequestMapping(value = "/auto", method = RequestMethod.POST )
	public String autoGenerateFinacingNum(@RequestBody AssetFinacingNum model) {
		String result = assetFinacingNumGenerateService.autoGenerateFinacingNum(
				model.getAssetId(), model.getNationalStardCode(), model.getUid(), model.getMajorAssetId(), model.getPrefix());
		return result;
	}
}

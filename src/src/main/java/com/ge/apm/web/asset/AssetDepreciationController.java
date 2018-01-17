package com.ge.apm.web.asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ge.apm.service.asset.AssetDepreciationService;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;

@Controller
@RequestMapping("/assetDepreciation")
public class AssetDepreciationController {

    @Autowired
    private AssetDepreciationService assetDepreciationService;
    
    @Autowired
    AssetFinacingNumGenerateService assetFinacingNumGenerateService;

	@RequestMapping(value = "/init_data", method = RequestMethod.POST)
	@ResponseBody
	public String excuteTask() {
		return assetDepreciationService.initAssetDepreciation();
	}
	
//	@RequestMapping(value = "/{assetId}", method = RequestMethod.GET)
//	@ResponseBody
//	public String generateFinacingNum(@PathVariable("assetId") Integer assetId) {
//		return assetFinacingNumGenerateService.generateFinacingNum(assetId, null, null, null);
//	}

}

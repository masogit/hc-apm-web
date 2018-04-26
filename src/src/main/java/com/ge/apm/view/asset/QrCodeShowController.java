/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.asset.QrCodeService;
import com.google.common.base.Strings;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class QrCodeShowController {

    private QrCodeService qrCodeService;

    @PostConstruct
    protected void init() {
        qrCodeService = WebUtil.getBean(QrCodeService.class);
    }

    public String getQrCodeImageBase64(String code, String siteUid) {
        return qrCodeService.getQrCodeImageBase64(code, siteUid);
    }

    public String getQrCodeImageWithTemplate(String code, String templateId,String siteUid) {
        return qrCodeService.getQrCodeImageBase64WithTemplate(code, templateId, siteUid);
    }

    public String getQrCodeImageBase64ByAsset(AssetInfo asset) {
        return qrCodeService.getQrCodeImageBase64(asset);
    }

}

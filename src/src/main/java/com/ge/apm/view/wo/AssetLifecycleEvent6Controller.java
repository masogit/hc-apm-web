package com.ge.apm.view.wo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;

import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.AssetLifecycleEventService;
import com.ge.apm.view.sysutil.FileDownloadService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEvent6Controller extends AssetLifecycleEventController {

	
	protected void init() {
		super.init();
        tabStatus=6;
    }
}
package com.ge.apm.domain;

/**
 *
 * @author 503035627
 */
public class UserAuth {
	private String authKey;
	private boolean authCreate=false;
	private boolean authModify=false;
	private boolean authDelete=false;
	private boolean authSearch=false;
	private boolean authSave=false;
	private boolean authImport=false;
	private boolean authExport=false;
	
	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	public Boolean getAuthCreate() {
		return authCreate;
	}
	public void setAuthCreate(Boolean authCreate) {
		this.authCreate = authCreate;
	}
	public Boolean getAuthModify() {
		return authModify;
	}
	public void setAuthModify(Boolean authModify) {
		this.authModify = authModify;
	}
	public Boolean getAuthDelete() {
		return authDelete;
	}
	public void setAuthDelete(Boolean authDelete) {
		this.authDelete = authDelete;
	}
	public Boolean getAuthSearch() {
		return authSearch;
	}
	public void setAuthSearch(Boolean authSearch) {
		this.authSearch = authSearch;
	}
	public Boolean getAuthSave() {
		return authSave;
	}
	public void setAuthSave(Boolean authSave) {
		this.authSave = authSave;
	}
	public Boolean getAuthImport() {
		return authImport;
	}
	public void setAuthImport(Boolean authImport) {
		this.authImport = authImport;
	}
	public Boolean getAuthExport() {
		return authExport;
	}
	public void setAuthExport(Boolean authExport) {
		this.authExport = authExport;
	}
	public void setAllAuth(String authKey,boolean value){
		this.setAuthKey(authKey);
		this.setAuthCreate(value);
		this.setAuthDelete(value);
		this.setAuthModify(value);
		this.setAuthSearch(value);
		this.setAuthImport(value);
		this.setAuthExport(value);
	}
}

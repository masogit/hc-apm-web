package com.ge.apm.domain;

public class ApmDropDownItem  {
	
	private String key;
	private String label;
	public ApmDropDownItem(){
		
	}
	public ApmDropDownItem(String key, String label) {
		super();
		this.key = key;
		this.label = label;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

package com.ge.apm.domain;

import java.util.UUID;

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
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the key fields are
        // not set
        if (!(object instanceof ApmDropDownItem)) {
            return false;
        }
        ApmDropDownItem other = (ApmDropDownItem) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ApmDropDownItem [ key=" + key + " ]";
    }
}

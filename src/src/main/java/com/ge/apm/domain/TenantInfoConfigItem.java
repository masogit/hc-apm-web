package com.ge.apm.domain;

public class TenantInfoConfigItem{
	private String label;
	private String key;
	private boolean value;
	public TenantInfoConfigItem(String label, String key, boolean value) {
		this.label = label;
		this.key = key;
		this.value = value;
	}
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TenantInfoConfigItem)) {
            return false;
        }
        TenantInfoConfigItem other = (TenantInfoConfigItem) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\""+key+"\"";
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	
}
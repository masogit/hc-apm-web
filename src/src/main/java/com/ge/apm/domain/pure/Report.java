package com.ge.apm.domain.pure;

import java.util.List;

public class Report {

	private String title;
	private List<Object> groups;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Object> getGroups() {
		return groups;
	}
	public void setGroups(List<Object> groups) {
		this.groups = groups;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [title=");
		builder.append(title);
		builder.append(", groups=");
		builder.append(groups);
		builder.append("]");
		return builder.toString();
	}
	
}

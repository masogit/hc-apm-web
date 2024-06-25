package com.ge.apm.domain.dto;

public class FileInfoResult {

	private String objectId;
	private String objectName;
	private String objectType;
	private Long objectSize;
	private byte[] bytes;


	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Long getObjectSize() {
		return objectSize;
	}

	public void setObjectSize(Long objectSize) {
		this.objectSize = objectSize;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}

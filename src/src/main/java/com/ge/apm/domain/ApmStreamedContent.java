package com.ge.apm.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.poi.util.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.DynamicContentSrcBuilder;


/**
 * Default implementation of a StreamedContent
 */
public class ApmStreamedContent implements StreamedContent {
	
	private InputStream stream;
	
	private String id;
	
	private String contentType;
    
	private String name;
    
    private String contentEncoding;
    
    private Integer contentLenght;
    
    private byte[] tBytes;
	
	public ApmStreamedContent() {}
	
    public ApmStreamedContent(InputStream stream) {
		this.setStream(stream);
	}
	
	public ApmStreamedContent(InputStream stream, String contentType) {
        this(stream);
		this.contentType = contentType;
	}
	
	public ApmStreamedContent(InputStream stream, String contentType, String name) {
		this(stream, contentType);
		this.name = name;
	}
    
    public ApmStreamedContent(InputStream stream, String contentType, String name, String contentEncoding) {
		this(stream, contentType, name);
        this.contentEncoding = contentEncoding;
	}    

    public ApmStreamedContent(InputStream stream, String contentType, String name, Integer contentLenght) {
    	this(stream, contentType, name);
		this.contentLenght = contentLenght;
	}    

	public ApmStreamedContent(InputStream stream, String contentType, String name, String contentEncoding, Integer contentLenght) {
		this(stream, contentType, name);
		this.contentEncoding = contentEncoding;
		this.contentLenght = contentLenght;
	}

	public Integer getContentLenght() {
		return contentLenght;
	}

	public void setContentLenght(Integer contentLenght) {
		this.contentLenght = contentLenght;
	}

	@Override
	public InputStream getStream() {
		if(tBytes==null){
			return null;
		}
		return new ByteArrayInputStream(tBytes);
	}

	public void setStream(InputStream stream) {
		if(stream==null){
			return;
		}
		try {
//			this.tBytes=IOUtils.toByteArray(stream);
			this.stream=stream;
			this.tBytes=DynamicContentSrcBuilder.toByteArray(stream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}
	
    @Override
	public String getContentType() {
		return contentType;
	}
    
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
    @Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }
    
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

	public Integer getContentLength() {
		return contentLenght;
	}
	public byte[] gettBytes() {
		return tBytes;
	}

	public void settBytes(byte[] tBytes) {
		this.tBytes = tBytes;
	}

	public String getId() {
		if(id==null){
			id=UUID.randomUUID().toString();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}    
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ApmStreamedContent)) {
			return false;
		}
		ApmStreamedContent other = (ApmStreamedContent) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ApmStreamedContent [ id=" + id + " ]";
	}
}
package com.ge.apm.domain;

import javax.persistence.*;

/**
 * Created by lsg on 30/11/2017.
 */

@Entity
@Table(name = "gb_code")
public class GbCode {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@Basic(optional = false)
	@Column(name = "code")
	private String code;
	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Column(name = "level")
	private int level;

	@Column(name = "parent_Code")
	private String parentCode ;


	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(Integer code) {
		code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		if(code.contains("/"))
		{
			this.code= code.split("/")[0];
		}else {
			this.code = code;
		}
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(code);
	//	builder.append("/");
	//	builder.append(name);
	/*	builder.append("GbCode [id=");
		builder.append(id);
		builder.append(", code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", code=");
		builder.append(code);

		builder.append("]");*/
		return builder.toString();
	}
}

package com.ge.apm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserHeadConfig implements Serializable{
	private static final long serialVersionUID = -1L;

	private String headTitle;  //头部标题

	private String headLogo;  //头部logo
}

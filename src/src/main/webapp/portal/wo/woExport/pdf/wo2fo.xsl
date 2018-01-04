<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/">
    <fo:root font-family="SimSun">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4-portrait"
              page-height="29.7cm" page-width="21.0cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="A4-portrait">
	  
        <fo:flow flow-name="xsl-region-body">	
		<fo:block>
			<xsl:for-each select="List/map">
			<fo:block margin-top="1.2cm">
			</fo:block>
			<fo:block>				
				<fo:external-graphic overflow="hidden" src="./ge.jpg" left="2cm">		
				</fo:external-graphic>				
			</fo:block>
			<fo:block font-family="SimSun" font-weight="bold" font-size="11px" margin-left="75%" space-after="0.8cm">
				<xsl:value-of select="院区"/>
			</fo:block>
			<fo:block font-family="SimSun" font-weight="bold" font-size="17px" text-align="center" space-after="0.8cm">
				医疗设备维修工单
			</fo:block>
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center" space-after="0.2cm">
				设备信息
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="35%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="35%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								设备名称
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备名称"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								所属科室
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="科室"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								设备型号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备型号"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								生产厂家
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="制造商"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								序列号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备序列号"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								系统编号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="系统编号"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								二维码
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="二维码"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								当前位置
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="当前位置"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>						
					</fo:table-body>					
				</fo:table>
			</fo:block>
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center" space-after="0.2cm">
				报修信息
			</fo:block>
			
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed">
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="35%"/>					
					<fo:table-column column-width="49%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								报修单号：
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="报修单号"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" number-rows-spanned="6" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm">
								故障现象：<xsl:value-of select="故障描述"/>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								报 修 人：
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="报修人"/>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								联系电话:
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="联系电话"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								报修时间：
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="报修时间"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								报修状态：
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="报修状态"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
								停机时间：
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm">
									<xsl:value-of select="停机时间"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>						
					</fo:table-body>					
				</fo:table>
			</fo:block>
			
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center" space-after="0.2cm">
				维修信息
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="11%"/>
					<fo:table-column column-width="11%"/>
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="16%"/>
					<fo:table-column column-width="16%"/>
					<fo:table-column column-width="16%"/>
					<fo:table-column column-width="17%"/>			
										
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block margin-left="-1cm" margin-right="-1cm" font-weight="bold" text-align="center">
								当前状态
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="工单状态"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
								总工时
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="维修工时"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
								总费用(元)
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="维修总费用"/>
								</fo:block>
							</fo:table-cell>													
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								签到时间
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="签到时间"/>
								</fo:block>
							</fo:table-cell>		
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								工单类型
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="工单类型"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								故障类型
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="故障类型"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								处理人
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:value-of select="当前责任人"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								工时：<xsl:value-of select="工时"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:choose>
										<xsl:when test="协作者1">
											协作者：<xsl:value-of select="协作者1"/>
										</xsl:when>
										<xsl:otherwise>	
											协作者:
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:choose>
										<xsl:when test="工时1">
											工时：<xsl:value-of select="工时1"/>
										</xsl:when>
										<xsl:otherwise>	
											工时：
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:choose>
										<xsl:when test="协作者2">
											协作者：<xsl:value-of select="协作者2"/>
										</xsl:when>
										<xsl:otherwise>	
											协作者：
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
									<xsl:choose>
										<xsl:when test="工时2">
											工时：<xsl:value-of select="工时2"/>
										</xsl:when>
										<xsl:otherwise>	
											工时：
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" text-align="center">
								故障原因描述
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" text-align="center">
								解决方案
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" text-align="center">		
								交付前检测
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
						
						<fo:table-row>
							<fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm" text-align="left">
									&#160;<xsl:value-of select="原因描述"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm" text-align="left">
									&#160;<xsl:value-of select="解决方案"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.6cm" text-align="left">
									&#160;<xsl:value-of select="交付前检测"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>							
					</fo:table-body>					
				</fo:table>
			</fo:block>
			
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center" space-after="0.2cm">
				备件信息
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed" >
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>					
					
					<fo:table-header border-style="solid" border-width="0.8px">
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								备件
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								数量
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								单价（元）
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								类型
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								序列号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								质保期
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								到货
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-header>
					
					<fo:table-body>
						<xsl:choose>
							<xsl:when test="备件信息/map">
								<xsl:apply-templates select="备件信息"/>
							</xsl:when>
							<xsl:otherwise>						
								<fo:table-row>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>											
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>									
								</fo:table-row>
							</xsl:otherwise>	
						</xsl:choose>
					</fo:table-body>
					
				</fo:table>
			</fo:block>
			
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center" space-after="0.2cm">
				维修流程
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed">
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="21%"/>
					<fo:table-column column-width="21%"/>
					<fo:table-column column-width="32%"/>
									
					
					<fo:table-header  border-style="solid" border-width="0.8px">
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								流程步骤
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								责任人
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								开始时间
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								结束时间
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1cm" margin-right="-1cm" text-align="center">
								处理描述
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
					</fo:table-header>
					
					<fo:table-body>
						<xsl:choose>
							<xsl:when test="维修流程/map">
								<xsl:apply-templates select="维修流程"/>
							</xsl:when>
							<xsl:otherwise>						
								<fo:table-row>									
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>											
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>										
										</fo:block>
									</fo:table-cell>									
								</fo:table-row>
							</xsl:otherwise>	
						</xsl:choose>						
					</fo:table-body>
				</fo:table>
				</fo:block>
			<xsl:if test="last()&gt;position()">
				<fo:block break-before="page" />
			</xsl:if>
			</xsl:for-each >
			</fo:block>
			</fo:flow>
		</fo:page-sequence>
    </fo:root>
  </xsl:template>
  <!-- 备件详情 -->
  <xsl:template match="备件信息/map">
    <fo:table-row>      
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="备件名"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="数量"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="单价"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="类型"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="序列号"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="质保期"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="到货"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>
  
  <!-- 维修流程 -->
  <xsl:template match="维修流程/map">
    <fo:table-row>      
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="步骤名称"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="责任人"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="开始时间"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="结束时间"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell border-style="solid" border-width="0.5px">
        <fo:block margin-left="-1cm" margin-right="-1cm" text-align="center">
          <xsl:value-of select="处理描述"/>
        </fo:block>
      </fo:table-cell>	  
    </fo:table-row>
  </xsl:template>
  
  
  
</xsl:stylesheet>
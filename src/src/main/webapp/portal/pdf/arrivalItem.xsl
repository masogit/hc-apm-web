<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/map">
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
						
			<fo:block font-weight="bold" font-size="17px" text-align="center" space-after="1cm" margin-top="2cm">
				医疗设备到货情况详单
			</fo:block>								
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" margin-left="1.3cm" margin-right="1.3cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="22%"/>
					<fo:table-column column-width="11%"/>
					<fo:table-column column-width="1%"/>
					<fo:table-column column-width="36%"/>
							
					<fo:table-body>
						<fo:table-row height="20px" line-height="20px">
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								设备名称
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="设备名称"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								规格型号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="规格型号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						<fo:table-row height="20px" line-height="20px">
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								数量
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="数量"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" wrap-option="wrap">
								生产厂家
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="生产厂家"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="20px" line-height="20px">	
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								产地
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="产地"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								发票号
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="发票号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="20px" line-height="20px">
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								合同价格
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="合同价格"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								合同号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="合同号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="20px" line-height="20px">
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								供应商
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="供应商"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								经办人
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="经办人"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="20px" line-height="20px">
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								设备分类编码
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="5" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="设备分类编码"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row line-height="30px" height="30px">
							<fo:table-cell number-columns-spanned="6" border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" font-size="13px" text-align="center">
								设备详细
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						
						<xsl:choose>
							<xsl:when test="到货条目/map">
								<xsl:apply-templates select="到货条目"/>
							</xsl:when>
							<xsl:otherwise>						
								<fo:table-row>
									<fo:table-cell number-columns-spanned="6" line-height="430px" border-style="solid" border-width="0.5px">
										<fo:block>
											&#160;
										</fo:block>
									</fo:table-cell>							
								</fo:table-row>	
							</xsl:otherwise>	
						</xsl:choose>
						
					</fo:table-body>					
				</fo:table>
			</fo:block>
			
					
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
    </fo:root>
  </xsl:template>
  <!-- 到货条目 -->
  <xsl:template match="到货条目/map">
    <fo:table-row height="18px" line-height="18px">      
      <fo:table-cell border-style="solid" border-width="0.3px" background-color="#ccc" number-columns-spanned="6">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          编号：<xsl:value-of select="序号"/>
        </fo:block>
      </fo:table-cell>
	</fo:table-row>
	<fo:table-row height="18px" line-height="18px">
      <fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          使用科室：<xsl:value-of select="使用科室"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          制造商序列号：
		  <xsl:call-template name="processText">
			<xsl:with-param name="text" select="制造商序列号" />
			<xsl:with-param name="colWidth" select="number(28)"/>
		  </xsl:call-template>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell number-columns-spanned="1" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          SystemID/厂商报修ID：
		  <xsl:call-template name="processText">
			<xsl:with-param name="text" select="SystemID" />
			<xsl:with-param name="colWidth" select="number(32)"/>
		  </xsl:call-template>
        </fo:block>
      </fo:table-cell>	  
    </fo:table-row>
	<fo:table-row height="18px" line-height="18px">
      <fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          资产编号：
		   <xsl:call-template name="processText">
			<xsl:with-param name="text" select="资产编号" />
			<xsl:with-param name="colWidth" select="number(26)"/>
		   </xsl:call-template>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          注册证号：<xsl:value-of select="注册证号"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell number-columns-spanned="1" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          设备编号(院方)：
		  <xsl:call-template name="processText">
			<xsl:with-param name="text" select="院方设备编号" />
			<xsl:with-param name="colWidth" select="number(32)"/>
		  </xsl:call-template>
        </fo:block>
      </fo:table-cell>	  
    </fo:table-row>	
	<fo:table-row height="18px" line-height="18px">
      <fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          二维码：
		  <xsl:call-template name="processText">
			<xsl:with-param name="text" select="二维码" />
			<xsl:with-param name="colWidth" select="number(26)"/>
		  </xsl:call-template>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          出厂日期：<xsl:value-of select="出厂日期"/>
        </fo:block>
      </fo:table-cell>
	  <fo:table-cell number-columns-spanned="1" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          启用日期：<xsl:value-of select="启用日期"/>
        </fo:block>
      </fo:table-cell>	  
    </fo:table-row>	
	<fo:table-row height="18px" line-height="18px">
      <fo:table-cell number-columns-spanned="2" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          质保日期：<xsl:value-of select="质保日期"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell number-columns-spanned="3" border-style="dotted" border-width="0.5px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          维保截止日期：<xsl:value-of select="维保截止日期"/>
        </fo:block>
      </fo:table-cell>	  	  
    </fo:table-row>
  </xsl:template>
  
  <xsl:template name="processText">
	<xsl:param name="text" />
	<xsl:param name="colWidth" />
	<xsl:choose>
		<xsl:when test="string-length($text) > $colWidth">
			<xsl:value-of select="substring($text,1,$colWidth)"/>&#8203;
			<xsl:call-template name="processText">
				<xsl:with-param name="text" select="substring($text,$colWidth+1)" />
				<xsl:with-param name="colWidth" select="number($colWidth)"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$text" />
		</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
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
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								设备名称
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="设备名称"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								规格型号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="规格型号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								数量
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="数量"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" wrap-option="wrap">
								生产厂家
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="生产厂家"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="30px" line-height="30px">	
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								产地
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="产地"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								发票号
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="发票号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								合同价格
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="合同价格"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								合同号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:call-template name="processText">
										<xsl:with-param name="text" select="合同号" />
										<xsl:with-param name="colWidth" select="number(34)"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								供应商
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="供应商"/>
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								经办人
								</fo:block>
							</fo:table-cell>						
							<fo:table-cell number-columns-spanned="2" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="经办人"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm">
								设备分类编码
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="5" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.2cm" margin-right="-1.4cm">
									<xsl:value-of select="设备分类编码"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row line-height="30px" height="30px">
							<fo:table-cell number-columns-spanned="6" border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" font-size="13px" text-align="center">
								设备分布情况
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>					
						
					</fo:table-body>					
				</fo:table>
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" margin-left="1.3cm" margin-right="1.3cm">
				<fo:table table-layout="fixed" >
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="26%"/>
					<fo:table-column column-width="33%"/>
					<fo:table-column column-width="33%"/>
									
					
					<fo:table-header border-style="solid" border-width="0.8px">
						<fo:table-row height="30px" line-height="30px">
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
								序号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
								设备所属科室
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
								设备财产编号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
								主机序列号
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
					</fo:table-header>
					
					<fo:table-body>
						<xsl:choose>
							<xsl:when test="到货条目/map">
								<xsl:apply-templates select="到货条目"/>
							</xsl:when>
							<xsl:otherwise>						
								<fo:table-row>
									<fo:table-cell number-columns-spanned="4" line-height="430px" border-style="solid" border-width="0.8px">
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
    <fo:table-row height="30px" line-height="30px">      
      <fo:table-cell border-style="solid" border-width="0.8px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          <xsl:value-of select="序号"/>
        </fo:block>
      </fo:table-cell>
	  
	  <fo:table-cell border-style="solid" border-width="0.8px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          <xsl:value-of select="使用科室"/>
        </fo:block>
      </fo:table-cell>
	  
	  <fo:table-cell border-style="solid" border-width="0.8px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          <xsl:call-template name="processText">
			<xsl:with-param name="text" select="资产编号" />
			<xsl:with-param name="colWidth" select="number(26)"/>
		  </xsl:call-template>
        </fo:block>
      </fo:table-cell>
	  
	  <fo:table-cell border-style="solid" border-width="0.8px">
        <fo:block margin-left="-1.1cm" margin-right="-1.1cm" text-align="left">
          <xsl:call-template name="processText">
			<xsl:with-param name="text" select="制造商序列号" />
			<xsl:with-param name="colWidth" select="number(26)"/>
		  </xsl:call-template>
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
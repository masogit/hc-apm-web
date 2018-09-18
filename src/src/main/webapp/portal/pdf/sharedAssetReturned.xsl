<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/map">
    <fo:root font-family="SimSun">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4-portrait"
              page-height="14cm" page-width="24.0cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="A4-portrait">
	  
        <fo:flow flow-name="xsl-region-body">	
		<fo:block>	
						
			<fo:block font-weight="bold" font-size="12px" margin-top="8cm" margin-left="1.2cm"  text-align="left" space-after="0.1cm">
				还  时
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="1cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px" space-after="0.4cm">
					<fo:table-column column-width="18%"/>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="30%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								是否完好  
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="dotted" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:choose>
										<xsl:when test="还时状态/text()='完好'">
											<fo:external-graphic content-width="10pt" content-height="10pt" 
											overflow="hidden" src="./checkbox.png" left="2cm">		
											</fo:external-graphic>
										</xsl:when>
										<xsl:otherwise>	
											□
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
												
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								总租用期（天）
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="总租用期"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								归还时间
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="归还时间"/>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								租赁费用（元）
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="租赁费用"/>
								</fo:block>
							</fo:table-cell>						
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								赔偿费用（元）
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="赔偿费用"/>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								总计费用（元）
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="总计费用"/>
								</fo:block>
							</fo:table-cell>						
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								归还人签字
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								经办人签字
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
												
					</fo:table-body>					
				</fo:table>
								
				<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="98%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="solid hidden hidden solid" border-width="0.8px" >
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.4cm">
								设备如有丢失、损坏，按院有关规定赔偿  
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid hidden hidden solid" border-width="0.8px" >
								<fo:block font-weight="bold" margin-left="-0.6cm" margin-right="-0.4cm">
									<xsl:value-of select="备注"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>												
					</fo:table-body>	
				</fo:table>
				
			</fo:block>

			
			<fo:block font-weight="bolder" font-size="8px" text-align="center">
				第1联：中心留存 &#160;第2联：卫生经济科记账  &#160;第3联：租用科室凭据
				<fo:leader leader-length="200mm"
               			   leader-pattern="rule"
                           rule-style="solid"
                           rule-thickness="0.1mm"
                           color="black">
                </fo:leader>
			</fo:block>
		</fo:block>
		</fo:flow>
		</fo:page-sequence>
    </fo:root>
  </xsl:template>

  
</xsl:stylesheet>

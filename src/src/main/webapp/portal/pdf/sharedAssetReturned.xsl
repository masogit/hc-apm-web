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
						
			<fo:block font-weight="bold" font-size="11px" margin-top="8cm" margin-left="1.2cm"  text-align="left" space-after="0.1cm">
				还  时
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="18%"/>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="30%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="dotted" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								还时状况 ：  
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="dotted" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								 □ 
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
								总金额
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="dotted" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="总金额"/>
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
			</fo:block>
		
		</fo:block>
		
		</fo:flow>
		</fo:page-sequence>
    </fo:root>
  </xsl:template>

  
</xsl:stylesheet>

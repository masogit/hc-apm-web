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
			
			<fo:block font-weight="bold" font-size="10px" text-align="center" margin-top="1cm">
				<xsl:value-of select="抬头"/>
			</fo:block>	
			
			
			<fo:block font-family="SimSun" font-weight="bold" font-size="13px" text-align="center"  space-after="-0.2cm">
				设备租赁凭据
			</fo:block>
						
			<fo:block wrap-option="no-wrap" overflow="visible" margin-left="1cm" margin-right="1cm">
     			<fo:leader leader-length="220mm"
               			   leader-pattern="rule"
                           rule-style="solid"
                           rule-thickness="0.1mm"
                           color="black">
                </fo:leader>
   			</fo:block>
			
			<fo:block font-weight="bold" font-size="11px" margin-left="1.2cm"  text-align="left" space-after="0.1cm">
				借据ID： <xsl:value-of select="借据ID"/>				
			</fo:block>
			
			<fo:block font-weight="bold" font-size="11px" margin-left="1.2cm"  text-align="left" space-after="0.1cm">
				借  时
			</fo:block>
			
			<fo:block wrap-option="wrap" font-size="11px" language="zh" text-align="left" margin-left="1cm" margin-right="1cm" space-after="0.6cm">
			<fo:table table-layout="fixed" border-style="solid" border-width="0.8px">
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="22%"/>
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="22%"/>
					<fo:table-column column-width="13%"/>
					<fo:table-column column-width="17%"/>
					
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								设备编号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备编号"/>
								</fo:block>
							</fo:table-cell>
							
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
								设备型号
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备型号"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								设备分类
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="设备分类"/>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								租&#160;&#160;金
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="租金"/>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="solid" number-columns-spanned="2" border-width="0.8px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								借时状况：□  下送：□
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								状&#160;&#160;态
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="状态"/>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								借用时间
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" number-columns-spanned="3" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="借用时间"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								附属物品
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" number-columns-spanned="5"  border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="附属物品"/>
								</fo:block>
							</fo:table-cell>							
						</fo:table-row>

						<fo:table-row>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								科室名称
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="科室名称"/>
								</fo:block>
							</fo:table-cell>

							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								借用人
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
								经办人
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-style="solid" border-width="0.5px">
								<fo:block font-weight="bold" margin-left="-0.4cm" margin-right="-0.4cm">
									<xsl:value-of select="经办人"/>
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

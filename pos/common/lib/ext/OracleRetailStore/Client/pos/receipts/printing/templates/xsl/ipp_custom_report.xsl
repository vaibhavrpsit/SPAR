<!--
/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/deploy/client/receipts/printing/templates/xsl/ipp_custom_report.xsl /main/2 2012/06/12 09:04:04 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  06/11/12 - IPP xsl template fix
 *    acadar    12/15/09 - reorganize printing files
 *    acadar    12/15/09 - reorganize printing files
 *    acadar    12/07/09 - changes to files location
 *    acadar    12/07/09 - templates for network printing
 *
 * ===========================================================================
 */
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:template match="/">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="master-pages-for-all" page-height="8.5in" page-width="11in" margin-top="0.25in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
          <fo:region-body margin-top="0.5in" margin-bottom="0.5in" margin-left="1.0in" margin-right="1.0in"/>
          <fo:region-before extent="0.25in"/>
          <fo:region-after extent="0.25in"/>
          <fo:region-start extent="0.5in" background-color="#A3BED8" reference-orientation="270"/> 
          <fo:region-end  extent="0.5in" background-color="#A3BED8" reference-orientation="90"/> 
        </fo:simple-page-master>
        <fo:page-sequence-master master-name="sequence-of-pages">
          <fo:repeatable-page-master-reference master-reference="master-pages-for-all"/>
        </fo:page-sequence-master>
      </fo:layout-master-set>      
      <fo:page-sequence master-reference="sequence-of-pages" initial-page-number="1">        
          <fo:static-content flow-name="xsl-region-before">
          <fo:block/>
          </fo:static-content>  
          <fo:static-content flow-name="xsl-region-after">
            <fo:block font-family="Helvetica" font-size="10pt" text-align="center">-<fo:page-number/>-</fo:block>
          </fo:static-content>  
          <fo:static-content flow-name="xsl-region-start">
             <xsl:apply-templates select="/document/page/region/left-sidebar"/>
          </fo:static-content>          
           <fo:static-content flow-name="xsl-region-end">
             <xsl:apply-templates select="/document/page/region/right-sidebar"/>
          </fo:static-content>  
          <fo:flow flow-name="xsl-region-body">
            <fo:block-container>
             <xsl:apply-templates select="/document/receipt/group"/>
            </fo:block-container>
          </fo:flow>        
      </fo:page-sequence>
    </fo:root>
  </xsl:template>    
    <xsl:template match="border-text">
          <fo:block-container position="absolute" width="100%" display-align="center">
          <fo:block font-family="Courier" font-size="24pt" color="#343434" text-align="center">
                 <xsl:value-of select="current()"/>
                 <xsl:text>&#xA;</xsl:text>
          </fo:block>
        </fo:block-container>    
   </xsl:template>  
  <xsl:template match="text">
    <fo:block text-align="left" font-family="Courier" font-size="10pt" linefeed-treatment="preserve" white-space-collapse="false" white-space-treatment="preserve" wrap-option="no-wrap">
      <xsl:value-of select="current()"/>
      <xsl:text>&#xA;</xsl:text>
    </fo:block>
  </xsl:template>
  <xsl:template match="image">
    <fo:block text-align="center">
     <fo:external-graphic scaling="uniform" content-width="1.0in" src="url(&quot;{@url}&quot;)"/> 
    </fo:block>
  </xsl:template>
  <xsl:template match="barcode">
    <fo:block text-align="left" linefeed-treatment="preserve" white-space-collapse="false" white-space-treatment="preserve" wrap-option="no-wrap"/>
    <fo:block text-align="left" linefeed-treatment="preserve" white-space-collapse="false" white-space-treatment="preserve" wrap-option="no-wrap">
      <fo:instream-foreign-object width="3.53in" text-align="center">
        <bc:barcode xmlns:bc="http://barcode4j.krysalis.org/ns" message="{.}">        
          <xsl:choose>
            <xsl:when test="@type='code39'">
              <bc:code39>
                <bc:module-width>0.4mm</bc:module-width>
                <bc:height>1.5cm</bc:height>
                <bc:human-readable>bottom</bc:human-readable>
              </bc:code39>
            </xsl:when>
            <xsl:otherwise>
              <bc:code128>
                <bc:module-width>0.4mm</bc:module-width>
                <bc:height>1.5cm</bc:height>
                <bc:human-readable>bottom</bc:human-readable>
              </bc:code128>
            </xsl:otherwise>
          </xsl:choose>
        </bc:barcode>
      </fo:instream-foreign-object>
    </fo:block>
  </xsl:template>
</xsl:stylesheet>
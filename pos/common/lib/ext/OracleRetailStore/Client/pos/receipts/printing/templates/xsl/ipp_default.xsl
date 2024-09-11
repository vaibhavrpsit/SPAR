<!--
/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/deploy/client/receipts/printing/templates/xsl/ipp_default.xsl /main/5 2014/03/13 18:02:02 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  03/13/14 - added keep-together=always so that group of data is
 *                         always kept together & are not split
 *    cgreene   11/08/13 - Removed keep-together=always from group block
 *                         because extra large groups like in ICCDetails report
 *                         wound up on unnecessary second page.
 *    abhineek  09/24/12 - Made the changes for header and barcode alignment
 *                         for various line size,table width needs to be
 *                         adjusted accordingly
 *    acadar    12/15/09 - reorganize printing files
 *    acadar    12/15/09 - reorganize printing files
 *    acadar    12/08/09 - changed the paper format from auto
 *    acadar    12/07/09 - changes to files location
 *    acadar    12/07/09 - fop template
 *
 * ===========================================================================
 */
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="master-pages-for-all" page-height="11in"
                    page-width="8.5in" margin-top="0.25in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
                    <fo:region-body margin-top="0.5in" margin-bottom="0.5in" margin-left="0.25in"
                        margin-right="0.25in" />
                    <fo:region-before extent="0.25in" />
                    <fo:region-after extent="0.25in" />
                </fo:simple-page-master>
                <!-- Page with header -->
                <fo:simple-page-master master-name="pages-with-header" page-height="11in"
                    page-width="8.5in" margin-top="0.25in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
                    <fo:region-body margin-top="3.5in" margin-bottom="0.5in" margin-left="0.25in"
                        margin-right="0.25in" />
                    <fo:region-before margin-left="0.25in" margin-right="0.25in" extent="3.5in" />
                    <fo:region-after extent="0.25in" />
                </fo:simple-page-master>
                <!-- Page with header and Footer -->
                <fo:simple-page-master master-name="pages-with-header-footer" page-height="11in"
                    page-width="8.5in" margin-top="0.25in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
                    <fo:region-body margin-top="3.5in" margin-bottom="3in" margin-left="0.25in"
                        margin-right="0.25in" />
                    <fo:region-before margin-left="0.25in" margin-right="0.25in" extent="3.5in" />
                    <fo:region-after margin-left="0.25in" margin-right="0.25in" extent="3in" />
                </fo:simple-page-master>
                <fo:page-sequence-master master-name="sequence-of-pages">
                    <fo:repeatable-page-master-reference master-reference="master-pages-for-all" />
                </fo:page-sequence-master>
                
                <fo:page-sequence-master master-name="repeating-header-footer-pages">
                    <fo:repeatable-page-master-reference master-reference="pages-with-header-footer" />
                </fo:page-sequence-master>
                
                <fo:page-sequence-master master-name="non-repeating-footer-pages">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference page-position="last"
                            master-reference="pages-with-header-footer" />
                        <fo:conditional-page-master-reference page-position="any"
                            master-reference="pages-with-header" />
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>
            </fo:layout-master-set>
            <xsl:choose>
                <xsl:when test="/document/receipt/@fixed_length ='false'">                    
                     <fo:page-sequence master-reference="sequence-of-pages" initial-page-number="1">
                        <fo:static-content flow-name="xsl-region-before">
                            <fo:block />
                        </fo:static-content>
                        <fo:static-content flow-name="xsl-region-after">
                            <fo:block font-family="Helvetica" font-size="10pt" text-align="center">
                                -
                                <fo:page-number />
                                -
                            </fo:block>
                        </fo:static-content>
                        <fo:flow flow-name="xsl-region-body">
                            <fo:block-container width="3.75in" start-indent="0.1in" end-indent="0.1in">
                                <xsl:apply-templates select="/document/receipt/group" />
                            </fo:block-container>
                        </fo:flow>
                    </fo:page-sequence>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="/document/receipt/footer/@repeat ='true'">
                         <!-- Print header and footer information on all receipt pages -->
                            <fo:page-sequence master-reference="repeating-header-footer-pages" initial-page-number="1">
                                <fo:static-content flow-name="xsl-region-before">
                                    <fo:block-container start-indent="0.21in" end-indent="0.21in">
                                        <xsl:apply-templates select="/document/receipt/header" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:static-content flow-name="xsl-region-after">
                                    <fo:block font-family="Helvetica" font-size="10pt" text-align="center"
                                        start-indent="0.4in" end-indent="0.4in">
                                        <fo:block-container width="3.75in" start-indent="0.21in"
                                            end-indent="0.21in">
                                            <xsl:apply-templates select="/document/receipt/footer" />
                                        </fo:block-container>
                                        -
                                        <fo:page-number />
                                        -
                                    </fo:block>
                                </fo:static-content>
                                <fo:flow flow-name="xsl-region-body">
                                    <fo:block-container width="3.75in" start-indent="0.1in"
                                        end-indent="0.1in">
                                        <xsl:apply-templates select="/document/receipt/group" />
                                    </fo:block-container>
                                </fo:flow>
                            </fo:page-sequence>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- Print header on each page, and print footer only in the last page -->
                            <fo:page-sequence master-reference="non-repeating-footer-pages"
                                initial-page-number="1">
                                <fo:static-content flow-name="xsl-region-before">
                                    <fo:block-container width="6.9in" start-indent="0.21in"
                                        end-indent="0.21in">
                                        <xsl:apply-templates select="/document/receipt/header" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:static-content flow-name="xsl-region-after">
                                    <fo:block font-family="Helvetica" font-size="10pt" text-align="center"
                                        start-indent="0.4in" end-indent="0.4in" />
                                    <fo:block-container width="3.75in" start-indent="0.21in"
                                        end-indent="0.21in">
                                        <xsl:apply-templates select="/document/receipt/footer" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:flow flow-name="xsl-region-body">
                                    <fo:block-container width="8.5in" start-indent="0.1in"
                                        end-indent="0.1in">
                                        <xsl:apply-templates select="/document/receipt/group" />
                                    </fo:block-container>
                                </fo:flow>
                            </fo:page-sequence>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </fo:root>
    </xsl:template>
    <xsl:template match="group">
        <fo:block keep-together="always">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>
    <xsl:template match="footer">
        <fo:block>
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>
    <xsl:template match="header">
        <fo:block>
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>
    <xsl:template match="text">
        <fo:block text-align="left" font-family="Courier" font-size="10pt" linefeed-treatment="preserve"
            white-space-collapse="false" white-space-treatment="preserve" wrap-option="no-wrap">
            <xsl:value-of select="current()" />
            <xsl:text>&#xA;</xsl:text>
        </fo:block>
    </xsl:template>
    <xsl:template match="image">
     <fo:table table-layout="fixed" width="100%">
		<fo:table-body> 
			<fo:table-row>
				<fo:table-cell>
        			<fo:block text-align="center">
            			<fo:external-graphic scaling="uniform" content-width="1.0in" src="url(&quot;{@url}&quot;)" />
        			</fo:block>
        		</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	 </fo:table>
    </xsl:template>
    <xsl:template match="barcode">
     <fo:table table-layout="fixed" width="100%">
		<fo:table-body> 
			<fo:table-row> 
				<fo:table-cell>
			        <fo:block text-align="center" linefeed-treatment="preserve" white-space-collapse="false"
			            white-space-treatment="preserve" wrap-option="no-wrap" />
			        <fo:block text-align="center" linefeed-treatment="preserve" white-space-collapse="false"
			            white-space-treatment="preserve" wrap-option="no-wrap">
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
        		</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	 </fo:table>
    </xsl:template>
</xsl:stylesheet>
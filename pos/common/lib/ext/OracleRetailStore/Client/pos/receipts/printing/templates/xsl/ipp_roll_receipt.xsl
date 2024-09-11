<!--
/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/deploy/client/receipts/printing/templates/xsl/ipp_roll_receipt.xsl /main/1 2014/02/18 17:13:24 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/12/14 - Support added for a network, non-JPOS, receipt
 *                         printer install.
 *    icole     02/11/14 - Initial revision for 80mm wide receipt printing.
 *
 * ===========================================================================
 */
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="master-pages-for-all" page-height="1677mm"
                    page-width="80mm" margin-top="0mm" margin-bottom="0mm" margin-left="0mm" margin-right="0mm">
                    <fo:region-body margin-top="0mm" margin-bottom="0mm" margin-left="0mm"
                        margin-right="0mm" />
                    <fo:region-before extent="0mm" />
                    <fo:region-after extent="0mm" />
                </fo:simple-page-master>
                <!-- Page with header -->
                <fo:simple-page-master master-name="pages-with-header" page-height="1677mm"
                    page-width="80mm" margin-top="0mm" margin-bottom="0mm" margin-left="0mm" margin-right="0mm">
                    <fo:region-body margin-top="0mm" margin-bottom="0mm" margin-left="0mm"
                        margin-right="0mm" />
                    <fo:region-before margin-left="0mm" margin-right="0mm" extent="00mm" />
                    <fo:region-after extent="0mm" />
                </fo:simple-page-master>
                <!-- Page with header and Footer -->
                <fo:simple-page-master master-name="pages-with-header-footer" page-height="1677mm"
                    page-width="80mm" margin-top="0mm" margin-bottom="0mm" margin-left="0mm" margin-right="0mm">
                    <fo:region-body margin-top="0mm" margin-bottom="0mm" margin-left="0mm"
                        margin-right="0mm" />
                    <fo:region-before margin-left="0mm" margin-right="0mm" extent="0mm" />
                    <fo:region-after margin-left="0mm" margin-right="0mm" extent="0mm" />
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
                            <fo:block font-family="Helvetica" font-size="9pt" font-weight="normal" text-align="center">
                                -
                                <fo:page-number />
                                -
                            </fo:block>
                        </fo:static-content>
                        <fo:flow flow-name="xsl-region-body">
                            <fo:block-container width="80mm" start-indent="0mm" end-indent="0mm">
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
                                    <fo:block-container start-indent="0mm" end-indent="0mm">
                                        <xsl:apply-templates select="/document/receipt/header" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:static-content flow-name="xsl-region-after">
                                    <fo:block font-family="Helvetica" font-size="9pt" font-weight="normal" text-align="center"
                                        start-indent="0mm" end-indent="0mm">
                                        <fo:block-container width="80mm" start-indent="0mm"
                                            end-indent="0mm">
                                            <xsl:apply-templates select="/document/receipt/footer" />
                                        </fo:block-container>
                                        -
                                        <fo:page-number />
                                        -
                                    </fo:block>
                                </fo:static-content>
                                <fo:flow flow-name="xsl-region-body">
                                    <fo:block-container width="80mm" start-indent="0mm"
                                        end-indent="0mm">
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
                                    <fo:block-container width="80mm" start-indent="0mm"
                                        end-indent="0mm">
                                        <xsl:apply-templates select="/document/receipt/header" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:static-content flow-name="xsl-region-after">
                                    <fo:block font-family="Helvetica" font-size="9pt" font-weight="normal" text-align="center"
                                        start-indent="0mm" end-indent="0mm" />
                                    <fo:block-container width="80mm" start-indent="0mm"
                                        end-indent="0mm">
                                        <xsl:apply-templates select="/document/receipt/footer" />
                                    </fo:block-container>
                                </fo:static-content>
                                <fo:flow flow-name="xsl-region-body">
                                    <fo:block-container width="80mm" start-indent="0mm"
                                        end-indent="0mm">
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
        <fo:block>
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
        <fo:block text-align="left" font-family="Courier" font-size="9pt" font-weight="normal" linefeed-treatment="preserve"
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
                        <fo:external-graphic scaling="uniform" content-width="50mm" src="url(&quot;{@url}&quot;)" />
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
                        <fo:instream-foreign-object width="72mm" text-align="center">
                            <bc:barcode xmlns:bc="http://barcode4j.krysalis.org/ns" message="{.}">
                                <xsl:choose>
                                    <xsl:when test="@type='code39'">
                                        <bc:code39>
                                            <bc:module-width>0.5mm</bc:module-width>
                                            <bc:height>1.5cm</bc:height>
                                            <bc:human-readable>bottom</bc:human-readable>
                                        </bc:code39>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <bc:code128>
                                            <bc:module-width>0.5mm</bc:module-width>
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
/* ===========================================================================
* Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 */
This folder contains FO-XSL style sheets for styling receipt/report documents 
generated for eReceipt and/or IPP printing. 

ipp_default.xsl       - Basic page formatting.

ipp_image_receipt.xsl - Formats an 8-1/2in x 11in portrait-oriented page where text 
                        is printed on an image background.

ipp_swan_receipt.xsl  - Formats an 8-1/2in x 11in portrait-oriented page where text 
                        is printed on a colored background.

ipp_custom_report.xsl - Formats an 8-1/2in x 11in landscape-oriented page with colored borders on right and
                        left margins.  This style sheet demonstrates how using a template like 
                        ../xml/ipp_custom_report.xml can be used to include customized static content into
                        receipts and reports.  By using the ipp_custom_report.xml  as the template for report
                        information, report data can be formatted so each page contains a "confidential" 
                        warning in the colored side-bars.

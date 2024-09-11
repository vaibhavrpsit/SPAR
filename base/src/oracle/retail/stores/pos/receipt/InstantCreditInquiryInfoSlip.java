/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/InstantCreditInquiryInfoSlip.java /main/16 2011/02/27 20:37:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    8    360Commerce 1.7         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    7    360Commerce 1.6         6/12/2007 8:48:04 PM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    6    360Commerce 1.5         6/11/2007 11:46:33 AM  Anda D. Cadar   SCR
 *         27206: removed the $ sign from the House Account receipt
 *    5    360Commerce 1.4         5/23/2007 3:23:26 PM   Mathews Kochummen
 *         format for locales
 *    4    360Commerce 1.3         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
 * sign from the House Account receipt
 *    5    360Commerce1.4         5/23/2007 3:23:26 PM   Mathews Kochummen
 *         format for locales
 *    4    360Commerce1.3         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    3    360Commerce1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:25 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/22 18:57:38  pkillick
 *   @scr 4106 -Removed code that was printing customer's masked SIN number.
 *
 *   Revision 1.3  2004/02/12 16:48:43  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:34:39  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Jan 09 2004 14:37:28   nrao
 * Fix for SCR 3696. Part of rework for House Account Inquiry. Printing masked SSN when House Account Inquiry is performed using SSN.
 * Resolution for 3696: House Account Inquiry Recept is not Displaying the SS# if the search was done by SS#
 *
 *    Rev 1.3   Dec 04 2003 17:27:12   nrao
 * Added proper masking of account number.
 *
 *    Rev 1.2   Nov 21 2003 11:31:10   nrao
 * Changed printing of last 4 digits of account number.
 *
 *    Rev 1.1   Nov 20 2003 18:19:42   nrao
 * Removed social security number and changed receipt heading.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import oracle.retail.stores.domain.utility.InstantCreditIfc;

/**
 * This site prints out the info from Instant Credit Inquiry
 * 
 * @version $Revision: /main/16 $
 */
public class InstantCreditInquiryInfoSlip extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = 2968881565931827913L;

    // instant credit card object
    protected InstantCreditIfc card = null;

    /**
     * Constructor
     */
    public InstantCreditInquiryInfoSlip()
    {
        this(null);
    }

    /**
     * Constructor
     */
    public InstantCreditInquiryInfoSlip(InstantCreditIfc card)
    {
        this.card = card;
        setDocumentType(ReceiptTypeConstantsIfc.INSTANTCREDIT_INQUIRY);
    }

    /**
     * Set the instant credit card.
     *
     * @param card
     */
    public void setInstantCredit(InstantCreditIfc card)
    {
        this.card = card;
    }

    /**
     * Returns the instance of the instant credit being reported on.
     *
     * @return
     */
    public InstantCreditIfc getInstantCredit()
    {
        return card;
    }
}
/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/NoSaleSlip.java /rgbustores_13.4x_generic_branch/2 2011/10/25 09:48:30 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  10/25/11 - Fixed Reasoncode not printed in nosale slip
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    mdecama   11/04/08 - I18N - Fixed way to retrieve a Locale
 *    mdecama   10/22/08 - Added I18N changes for ReasonCodes
 *
 * $Log:
 *  6    360Commerce 1.5         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 *  5    360Commerce 1.4         5/12/2006 5:25:28 PM   Charles D. Baker
 *       Merging with v1_0_0_53 of Returns Managament
 *  4    360Commerce 1.3         1/25/2006 4:11:32 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse
 * $:
 *  5    .v700     1.2.1.1     1/4/2006 11:55:16      Deepanshu       CR 6155:
 *       Remove extra line spacing before the header in the receipt
 *  4    .v700     1.2.1.0     10/26/2005 13:50:38    Deepanshu       CR 6122:
 *       Retreived value from properties file to be printed on receipt
 *  3    360Commerce1.2         3/31/2005 15:29:09     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:23:42     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:45     Robert Pearse
 * $
 * Revision 1.6  2004/07/22 15:05:58  awilliam
 * @scr 4465 no sale receipt print control transaction header and barcode are missing
 *
 * Revision 1.5  2004/04/09 16:55:59  cdb
 * @scr 4302 Removed double semicolon warnings.
 *
 * Revision 1.4  2004/03/02 18:58:26  awilliam
 * @scr 3928 No Sale printing slip defects
 *
 * Revision 1.3  2004/02/12 16:48:43  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:34:38  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 10 2004 14:38:52   Tim Fritz
 * Added the new CaptureReasonCodeForNoSale parameter for the new No Sale requirments.
 *
 *    Rev 1.0   Aug 29 2003 15:51:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 09 2003 13:30:34   KLL
 * clean-up
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.0   Apr 09 2003 13:20:12   KLL
 * Initial revision.
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.receipt;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;

/**
 * This class represents the balance slip that is printed when doing a gift card
 * inquiry. The requirements are found in the printing functional requirements.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 * @since 5.2.0
 */
public class NoSaleSlip extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = 8964401432526618242L;

    /**
     * hold the transaction
     */
    protected NoSaleTransactionIfc noSaleTransaction = null;

    /**
     * Constructor
     */
    public NoSaleSlip ()
    {
        this (null);
    }

    /**
     * Constructor
     * @param noSale
     */
    public NoSaleSlip (NoSaleTransactionIfc noSale)
    {
        noSaleTransaction = noSale;
        setDocumentType(ReceiptTypeConstantsIfc.NO_SALE);
    }

    /**
     * @return the reasonCode
     */
    public int getReasonCode()
    {
        return Integer.valueOf(noSaleTransaction.getLocalizedReasonCode().getCode());
    }
    
    /**
     * Retrieves a Reason Code String
     * 
     * @return
     */
    public String getReasonString()
    {
        Locale receiptLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
        return getReasonString(receiptLocale);
    }

    /**
     * Retrieves a localized Reason Code String
     * @param lcl
     * @return
     */
    public String getReasonString (Locale lcl)
    {
        String reasonString = null;
        Locale bestMatch = LocaleMap.getBestMatch (lcl);
        reasonString = noSaleTransaction.getLocalizedReasonCode(bestMatch);
        return reasonString;
    }

    /**
     * @return the noSaleTransaction
     */
    public NoSaleTransactionIfc getNoSaleTransaction()
    {
        return noSaleTransaction;
    }
    
    /**
     * @param noSaleTransaction
     */
    public void setNoSaleTransaction(NoSaleTransactionIfc noSaleTransaction)
    {
        this.noSaleTransaction = noSaleTransaction;
    }

    public boolean isTrainingMode()
    {
    	return this.noSaleTransaction.isTrainingMode();
    }
}

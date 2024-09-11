/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/CreateStoreCreditNumberActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:44 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.8  2004/06/16 18:07:41  bwf
 *   @scr 5000 Back out changes, change to req happening.
 *
 *   Revision 1.7  2004/06/15 22:57:01  bwf
 *   @scr 5000 Check to see if customer was captured before asking again.
 *
 *   Revision 1.6  2004/06/04 21:27:18  bwf
 *   @scr 5205 Fixed change due options and store credit flow for undo 
 *   and cancel during change and refund.
 *
 *   Revision 1.5  2004/06/02 04:05:19  blj
 *   @scr 4529 - resolution to customer id printing issues
 *
 *   Revision 1.4  2004/05/11 16:08:47  blj
 *   @scr 4476 - more rework for store credit tender.
 *
 *   Revision 1.3  2004/04/13 16:30:07  bwf
 *   @scr 4263 Decomposition of store credit.
 *
 *   Revision 1.2  2004/03/04 23:28:07  nrao
 *   Code Review Changes for Issue Store Credit.
 *
 *   Revision 1.1  2004/02/17 17:56:50  nrao
 *   New site for Issue Store Credit
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//----------------------------------------------------------------------------
/**
 *  This site creates a new store credit using a unique number
 * 
 *  $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//----------------------------------------------------------------------------
public class CreateStoreCreditNumberActionSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    // Static strings
    public static final String FAILURE_LETTER = "Cancel";
    public static final String STORE_CREDIT_MINIMUM = "StoreCreditMinimum";
    public static final String OVERTENDER_IN_A_RETURN = "OvertenderInAReturn";
    public static final String VALID_LETTER = "Valid";
    
    //------------------------------------------------------------------------
    /* 
     * @param bus  The bus arriving at this site.
     */
    //------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO) cargo.getTenderADO();
        
        // create storeCredit with unique store credit number
        StoreCreditIfc storeCredit = storeCreditTender.createUniqueStoreCredit();
       
        // retrieve transaction
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        
        LetterIfc letter = null;
        
        // retrieve amount to be used for store credit
        CurrencyIfc tenderAmount = null;
        tenderAmount = txnADO.issueStoreCreditAmount((String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT));
        
        // determine what type of store credit to be issued
        TenderStoreCreditIfc tscIssue = txnADO.unusedStoreCreditReissued(storeCredit, tenderAmount);
        storeCreditTender.fromLegacy(tscIssue);
        
        try
        {
            // try to add the tender to the transaction
            txnADO.addTender(storeCreditTender);
            cargo.setLineDisplayTender(storeCreditTender);
        }
        catch (TenderException e)
        {
            cargo.setTenderADO(storeCreditTender);
            logger.error("Error adding Issue Store Credit Tender", e);      
        }
        
        try
        {
            // determine whether store credit is of type ISSUE
            storeCreditTender.determineState();
        }
        
        catch(TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            // if issue store credit tender
            if (error == TenderErrorCodeEnum.ISSUE_STORE_CREDIT)
            {
                letter = new Letter(VALID_LETTER);
            }
            // if not issue store credit tender
            else if (error == TenderErrorCodeEnum.NOT_ISSUE_STORE_CREDIT)
            {
                letter = new Letter(FAILURE_LETTER);
            }
            else
            {
                letter = new Letter(FAILURE_LETTER);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}

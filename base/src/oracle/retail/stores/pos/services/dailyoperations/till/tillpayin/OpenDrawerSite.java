/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/OpenDrawerSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 04 2003 16:01:56   RSachdeva
 * String constant defined for tag
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Feb 20 2003 14:43:32   RSachdeva
 * Clean Up for Code Conversion as per Coding Standards
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 27 2002 15:01:26   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:26:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Nov 2001 14:27:34   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:19:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

/**
 * Attempts to open the cash drawer to add PayIn tender by calling
 * TillCashDrawer.tillOpenCashDrawer
 * 
 * @version $Revision: /main/11 $
 * @see TillCashDrawer
 */
@SuppressWarnings("serial")
public class OpenDrawerSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
     * Cash Drawer Retry Continue Cancel payin tag
     */
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PAYIN_TAG = "CashDrawerRetryContinueCancel.payin";
    /**
     * Cash Drawer Retry Continue Cancel payin default text
     */
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PAYIN_TEXT = "the till pay-in";

    /**
     * openDrawerSite
     */
    public static final String SITENAME = "OpenDrawerSite";

    /**
     * Calls TillCashDrawer.tillOpenCashDrawer to open the cash drawer.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayInCargo cargo = (TillPayInCargo) bus.getCargo();

        // Create the Till Pickup Transaction
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TillAdjustmentTransactionIfc transaction =
          DomainGateway.getFactory().getTillAdjustmentTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_PAYIN_TILL);
        utility.initializeTransaction(transaction, -1);
        
        cargo.setTransaction(transaction);
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String tillPayIn = util.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_PAYIN_TAG,
                                                      CASH_DRAWER_RETRY_CONT_CANCEL_PAYIN_TEXT);
        TillCashDrawer.tillOpenCashDrawer(bus, tillPayIn);
    }

 }

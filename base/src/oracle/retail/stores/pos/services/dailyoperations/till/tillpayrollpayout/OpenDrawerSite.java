/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/OpenDrawerSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
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
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/03/12 18:19:23  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.3  2004/02/12 16:50:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 04 2003 16:39:48   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 27 2002 14:37:18   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:26:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Nov 2001 14:27:36   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:19:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

/**
 * Used by the payroll pay out use case. Attempts to open the
 *         drawer.
 * 
 * @author khassen
 */
@SuppressWarnings("serial")
public class OpenDrawerSite extends PosSiteActionAdapter
{
    public static final String revisionNumber = "$Revision: /main/11 $";
    public static final String SITENAME = "OpenDrawerSite";

    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PAYROLL_PAYOUT_TAG = "CashDrawerRetryContinueCancel.payrollpayout";
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PAYROLL_PAYOUT_TEXT = "payroll payout";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayrollPayOutCargo cargo = (TillPayrollPayOutCargo) bus.getCargo();

        // Create the Till Pickup Transaction
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TillAdjustmentTransactionIfc transaction =
          DomainGateway.getFactory().getTillAdjustmentTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL);
        utility.initializeTransaction(transaction, -1);
        
        cargo.setTransaction(transaction);
        UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        TillCashDrawer.
        tillOpenCashDrawer(bus, util.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_PAYROLL_PAYOUT_TAG,
                                                          CASH_DRAWER_RETRY_CONT_CANCEL_PAYROLL_PAYOUT_TEXT));
     }

    /**
     * returns a string representation of this object.
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  OpenDrawerSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    /**
     * returns the revision number.
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
 }

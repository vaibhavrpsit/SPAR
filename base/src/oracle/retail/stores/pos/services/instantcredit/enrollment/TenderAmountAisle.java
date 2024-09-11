/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/TenderAmountAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
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
 * 7    360Commerce 1.6         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 6    360Commerce 1.5         12/7/2007 4:27:45 PM   Alan N. Sinton  CR
 *      29598: Modifications per code review.
 * 5    360Commerce 1.4         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *      29661: Encrypting, masking and hashing account numbers for House
 *      Account.
 * 4    360Commerce 1.3         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *      merge
 *      
 * 3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:25:52 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:14:47 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 19 2004 16:39:26   nrao
 * Fix for Special Order and Layaway Transaction. If not split final tender or final tender, then get tender amount from cargo.
 * 
 *    Rev 1.1   Nov 24 2003 19:48:48   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderAmountAisle extends LaneActionAdapter
{
    /** The logger to which log messages will be sent */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.instantcredit.enrollment.TenderAmountAisle.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        CurrencyIfc amount = (CurrencyIfc) cargo.getTenderAmount();
        if (cargo.getTransaction() != null)
        {
            SaleReturnTransaction srt = (SaleReturnTransaction)cargo.getTransaction();
            if (srt.getTransactionTotals() != null &&
                srt.getTransactionTotals().getBalanceDue() != null
                && (cargo.isFinalTender() || cargo.isSplitFinalTender()))
            {
                amount = srt.getTransactionTotals().getBalanceDue();
            }
        }
        InstantCreditIfc instantCredit = (InstantCreditIfc) cargo.getInstantCredit();
        String[] args = {amount.toString(), "" };
        if(instantCredit != null && instantCredit.getEncipheredCardData() != null)
        {
            args[1] =  instantCredit.getEncipheredCardData().getTruncatedAcctNumber();
        }
        
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InstantCreditTenderAmount",
                                    args, "Yes");
    }
}

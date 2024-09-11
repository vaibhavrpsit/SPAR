/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/cancel/SpecialOrderRefundRoad.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:08 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:52:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:02:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Dec 10 2001 16:35:14   dfh
 * set trans type to order cancel
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 07 2001 16:40:06   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.cancel;

// foundation imports
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

//------------------------------------------------------------------------------
/**
    This class negates the prior special order deposit to become a refund,
    prior to calling the tender service. <P>
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class SpecialOrderRefundRoad extends PosLaneActionAdapter
{                                       // begin class DepositEnteredRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "SpecialOrderRefundRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";

    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        the special order deposit is negated to become a refund, prior
        to entering the tender service. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();

        // setup new payment (refund) - negate the original deposit
        PaymentIfc deposit = orderTransaction.getPayment();
        // if deposit is not 0 then negate amount
        if (deposit.getPaymentAmount().signum() == CurrencyIfc.POSITIVE)
        {
            deposit.setPaymentAmount(deposit.getPaymentAmount().negate());
        }
        deposit.setDescription("Special Order Refund");
        orderTransaction.setPayment(deposit);
// set type to cancel
        orderTransaction.setTransactionType(TransactionIfc.TYPE_ORDER_CANCEL);

        // journal refund
        StringBuffer sb = new StringBuffer();
        String depositString = deposit.getPaymentAmount().toFormattedString();


        Object dataObject[]={depositString};


        String depositPaid = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SPECIAL_ORDER_DEPOSIT_PAID, dataObject);


        sb.append(Util.EOL)
        .append(depositPaid);

        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        jmi.journal(orderTransaction.getCashier().getEmployeeID(),
                    orderTransaction.getTransactionID(), sb.toString());
    }                           // end traverse()
}                           // end class SpecialOrderRefundRoad

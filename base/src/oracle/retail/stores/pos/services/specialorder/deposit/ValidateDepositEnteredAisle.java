/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/ValidateDepositEnteredAisle.java /main/11 2012/05/11 14:47:09 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/11/12 - add more comments
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/7/2007 4:45:03 PM    Maisa De Camargo
 *         Updated type of SpecialOrderDepositBeanModel.depositAmountValue
 *         field from String to CurrencyIfc.
 *    4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:52:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:01:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Dec 04 2001 16:09:00   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 15:11:22   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.SpecialOrderDepositBeanModel;

//--------------------------------------------------------------------------
/**
    Displays error message indicating deposit is invalid or sets the deposit.
    <P>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class ValidateDepositEnteredAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       Special Order Deposit Minimum string
    **/
    public static final String DEPOSIT_MINIMUM = "MinimumDepositRequired";
    /**
       Invalid Special Order Deposit string
    **/
    public static final String INVALID_DEPOSIT = "InvalidSpecialOrderDeposit";

    //----------------------------------------------------------------------
    /**
       Displays error message indicating deposit is invalid. If deposit is
       below minimum, displays the MinimumDepositRequired error screen. If deposit
       is greater than the balance due, then displays the InvalidSpecialOrderDeposit
       error screen.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo) bus.getCargo();
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        SpecialOrderDepositBeanModel beanModel =
            (SpecialOrderDepositBeanModel) ui.getModel(POSUIManagerIfc.SPECIAL_ORDER_DEPOSIT);

        // initializes values from model
        CurrencyIfc depositAmount       = beanModel.getDepositAmountValue();
        CurrencyIfc minimumDeposit      = orderTransaction.getOrderStatus().getMinimumDepositAmount();
        CurrencyIfc grandTotal          = orderTransaction.getOrderStatus().getTotal();

        // Calculate deposit
        TransactionTotalsIfc totals = orderTransaction.getTransactionTotals();

        // If initial deposit is less than minimum deposit...
        if (depositAmount.compareTo(minimumDeposit) < 0 )
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(DEPOSIT_MINIMUM);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (depositAmount.compareTo(grandTotal) > 0) // deposit > total
        {
            String[] args = new String[1];
            args[0] = grandTotal.toFormattedString();
            DialogBeanModel model = new DialogBeanModel();
            model.setArgs(args);
            model.setResourceID(INVALID_DEPOSIT);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            // The deposit is valid and a deposit is created and set to the cargo
            // No need to set xchannel deposit amount here since a xchannel order is always
            // fully funded, and this site is not invoked for a xchannel order.
            orderTransaction.getOrderStatus().setStoreOrderDepositAmount(depositAmount);

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}

/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/SpecialOrderDepositSite.java /main/13 2012/05/11 14:47:08 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/09/12 - separate minimum deposit amount into xchannel part
 *                         and store order part
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         8/7/2007 4:45:03 PM    Maisa De Camargo
 *         Updated type of SpecialOrderDepositBeanModel.depositAmountValue
 *         field from String to CurrencyIfc.
 *    5    360Commerce 1.4         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:45:21 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 08 2003 10:11:36   bwf
 * Undo comes back to this site and then continues.  Check to see if need to send undo letter again if percentage due is 100.
 * Resolution for 2396: Set Special Order Deposit Percent to 100, undo button has unexpected action on Tender Options screen
 *
 *    Rev 1.0   Apr 29 2002 15:01:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:20   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:43:30   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   Dec 04 2001 16:08:58   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 15:11:20   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SpecialOrderDepositBeanModel;
import java.math.BigDecimal;
import oracle.retail.stores.domain.order.OrderConstantsIfc;

// ------------------------------------------------------------------------------
/**
 * Displays Special Order Deposit detail screen for making a deposit on a
 * SpecialOrder.
 * <P>
 *
 * @version: $Revision: /main/13 $
 */
// ------------------------------------------------------------------------------
public class SpecialOrderDepositSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -3995972163490713083L;

    /**
     * class name constant
     */
    public static final String SITENAME = "SpecialOrderDepositSite";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * string constant for the special order depositpercent parameter
     */
    public static final String DEPOSIT_PERCENT = "SpecialOrderDepositPercent";

    /**
     * string constant for the special order deposit
     */
    public static final String SPECIAL_ORDER_DEPOSIT = "Special Order Deposit";

    // --------------------------------------------------------------------------
    /**
     * Creates the special order transaction and displays the SpecialOrder
     * deposit detail screen.
     * <P>
     *
     * @param bus the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        boolean mailLetter = false;
        Integer depositPercent = new Integer(25); // default value
        String undoString = "Undo";
        boolean mailUndo = false;

        // Get special order parameter - depositpercent
        try
        {
            depositPercent = pm.getIntegerValue(DEPOSIT_PERCENT);
        }
        catch (ParameterException e)
        {
            logger.error(Util.throwableToString(e) + " Using default value [25]");
        }
        // Calculate deposit
        TransactionTotalsIfc transactionTotals = orderTransaction.getTransactionTotals();
        TransactionTotalsIfc tenderTotals = orderTransaction.getTenderTransactionTotals();
        CurrencyIfc grandTotal = transactionTotals.getGrandTotal();
        CurrencyIfc xcGrandTotal = orderTransaction.getXChannelGrandTotal();
        CurrencyIfc stGrandTotal = grandTotal.subtract(xcGrandTotal);
        CurrencyIfc minimumDeposit = DomainGateway.getBaseCurrencyInstance("0.00");

        orderTransaction.getOrderStatus().setXChannelTotal(xcGrandTotal);
        orderTransaction.getOrderStatus().setStoreOrderTotal(stGrandTotal);

        // determine if they need to pay 100% of the total or not
        if (depositPercent.intValue() < 100)
        {
            minimumDeposit = grandTotal.multiply(new BigDecimal(depositPercent.floatValue() / 100));
        }
        else
        // parameter is 100 %
        {
            minimumDeposit = grandTotal;
        }

        // if special order deposit percent == 100, set cargo, send Success
        // letter
        if ((minimumDeposit.signum() == CurrencyIfc.POSITIVE && minimumDeposit.compareTo(grandTotal) == 0)
                || (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND))
        {
            mailLetter = true;
            orderTransaction.getOrderStatus().setXChannelMinimumDepositAmount(xcGrandTotal);
            orderTransaction.getOrderStatus().setStoreOrderMinimumDepositAmount(stGrandTotal);
            orderTransaction.getOrderStatus().setXChannelDepositAmount(xcGrandTotal);
            orderTransaction.getOrderStatus().setStoreOrderDepositAmount(stGrandTotal);
            // if undoing from tender options instead of deposit site
            if (bus.getCurrentLetter().getName().equals(undoString))
            {
                mailUndo = true;
            }
        }
        else
        {
            //No need to set xchannel mininum deposit amount here since this branch only handles
            //special order, not pick/delivery orders. A special order can never contain any
            //xchannel items.
            orderTransaction.getOrderStatus().setStoreOrderMinimumDepositAmount(minimumDeposit);

            // Retrieve bean model to initialize its data and flags
            SpecialOrderDepositBeanModel model = new SpecialOrderDepositBeanModel();

            // Gets the customer
            CustomerIfc customer = orderTransaction.getCustomer();
            StringBuffer sb = new StringBuffer();
            sb.append(customer.getFirstLastName());

            // Set the model with special order Transactionn details
            model.setCustomerValue(sb.toString());
            model.setSpecialOrderNumberValue(orderTransaction.getOrderID());

            model.setBalanceDueValue(grandTotal.toFormattedString());
            model.setMinimumDepositValue(minimumDeposit.toFormattedString());

            model.setDepositAmountValue(minimumDeposit);

            // display the special order deposit screen
            ui.showScreen(POSUIManagerIfc.SPECIAL_ORDER_DEPOSIT, model);
        }
        if (mailLetter)
        {
            Letter newLetter = null;
            if (mailUndo)
            {
                newLetter = new Letter(CommonLetterIfc.UNDO);
            }
            else
            {
                newLetter = new Letter(CommonLetterIfc.SUCCESS);
            }
            bus.mail(newLetter, BusIfc.CURRENT);
        }
    }
}

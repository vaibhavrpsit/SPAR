/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.specialorder.deposit;

// java imports
import java.math.BigDecimal;

import max.retail.stores.domain.order.MAXOrderStatusIfc;
import max.retail.stores.pos.ui.beans.MAXSpecialOrderDepositBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
    Displays Special Order Deposit detail screen for making a deposit on a SpecialOrder.
    <P>
    @version: $Revision: 6$
**/
//------------------------------------------------------------------------------

public class MAXSpecialOrderDepositSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4409303351550011821L;
	/**
        class name constant
    **/
    public static final String SITENAME = "SpecialOrderDepositSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 6$";
    /**
        string constant for the special order depositpercent parameter
    **/
    public static final String DEPOSIT_PERCENT = "SpecialOrderDepositPercent";
    /**
        string constant for the special order deposit
    **/
    public static final String SPECIAL_ORDER_DEPOSIT = "Special Order Deposit";

    //--------------------------------------------------------------------------
    /**
        Creates the special order transaction and displays the SpecialOrder
        deposit detail screen.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
                (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        boolean mailLetter = false;
        Integer depositPercent = new Integer(0); // default value
        String undoString = "Undo";
        boolean mailUndo = false;
        
        //Get special order parameter - depositpercent
        try
        {
            depositPercent = pm.getIntegerValue(DEPOSIT_PERCENT);
        }
        catch (ParameterException e)
        {
            logger.error( Util.throwableToString(e) +
                " Using default value [25]");
        }
        // Calculate deposit
        TransactionTotalsIfc transactionTotals = orderTransaction.getTransactionTotals();
        TransactionTotalsIfc tenderTotals = orderTransaction.getTenderTransactionTotals();
        CurrencyIfc grandTotal = transactionTotals.getGrandTotal();
        CurrencyIfc minimumDeposit = DomainGateway.getBaseCurrencyInstance("0.00");

        ((MAXOrderStatusIfc) orderTransaction.getOrderStatus()).setTotal(grandTotal);

        // determine if they need to pay 100% of the total or not
        if (depositPercent.intValue() < 100)
        {
            minimumDeposit = grandTotal.multiply(new BigDecimal
                (depositPercent.floatValue()/100));
        }
        else  // parameter is 100 %
        {
            minimumDeposit = grandTotal;
        }
        ((MAXOrderStatusIfc) orderTransaction.getOrderStatus()).setMinimumDepositAmount(minimumDeposit);

        // if special order deposit percent == 100, set cargo, send Success letter
        if (minimumDeposit.signum() == CurrencyIfc.POSITIVE &&
            minimumDeposit.compareTo(grandTotal) == 0)
        {
            mailLetter = true;
            ((MAXOrderStatusIfc) orderTransaction.getOrderStatus()).setMinimumDepositAmount(grandTotal);
            ((MAXOrderStatusIfc) orderTransaction.getOrderStatus()).setDepositAmount(grandTotal);
            tenderTotals.updateTransactionTotalsForPayment(minimumDeposit);
            // if undoing from tender options instead of deposit site
            if(bus.getCurrentLetter().getName().equals(undoString))
            {
                mailUndo = true;
            }
        }
        else
        {
            // Retrieve bean model to initialize its data and flags
            MAXSpecialOrderDepositBeanModel model = new MAXSpecialOrderDepositBeanModel();

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
            model.setExpectedDeliveryDate(new EYSDate());
            model.setExpectedDeliveryTime(new EYSTime());

            // display the special order deposit screen
            ui.showScreen(POSUIManagerIfc.SPECIAL_ORDER_DEPOSIT, model);
        }
        if (mailLetter)
        {
            Letter newLetter = null;
            if(mailUndo)
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

/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/EvaluateBalanceSite.java /main/16 2014/02/10 15:44:38 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/10/14 - reworked flow for gift card activation error
 *                         scenarios
 *    icole     08/01/13 - Discard swipe ahead if balance is negative per
 *                         functional requirement
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    blarsen   06/14/11 - Integrate changes to isSwipeAhead() interface.
 *    blarsen   06/09/11 - Added logic to bypass TenderOptionsUI and go to
 *                         AuthorizationStation when a swipe ahead is detected.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    03/09/09 - added comments from code review performed by Jack
 *                         Swan
 *    acadar    03/09/09 - do not display capture customer when balance is 0
 *    acadar    03/06/09 - do not display the Capture Customer screen if
 *                         balance due is 0.00 for a LayawayDelete
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         11/2/2006 10:43:32 AM  Keith L. Lesikar
 *         OracleCustomer parameter update.
 *    4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:56 PM  Robert Pearse
 *
 *   Revision 1.10  2004/07/29 13:00:44  khassen
 *   @scr 5002 added another condition for calling the CCI screen.
 *
 *   Revision 1.9  2004/07/14 21:21:55  jriggins
 *   @scr 4401 Added logic to determine when to capture customer info during tender
 *
 *   Revision 1.8  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.7  2004/07/13 18:18:14  jriggins
 *   @scr 4401 Added check for a null transaction
 *
 *   Revision 1.6  2004/07/12 20:58:26  jriggins
 *   @scr 4401 Incorporated logic in EvaluateBalanceSite to determine when to capture customer information based on the NegativeAmtDue parameter. Removed this check from RefundOptionsDueUISite because it is now performed at a more generic level.
 *
 *   Revision 1.5  2004/04/28 15:46:37  blj
 *   @scr 4603 - Fix gift card change due defects.
 *
 *   Revision 1.4  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.3  2004/03/22 17:26:43  blj
 *   @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Nov 04 2003 11:17:42   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:29:48   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:06:44   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.ado.transaction.LayawayTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.ado.transaction.TenderStateEnum;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site checks the balance due on the transaction
 * for the purpose of making a flow control decision
 */
@SuppressWarnings("serial")
public class EvaluateBalanceSite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	System.out.println("Arrive in EvaluateBalanceSite");
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // reset cargo for new tenders
        cargo.setTenderADO(null);
        cargo.resetTenderAttributes();
        cargo.setOverrideOperator(null);

        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        String letter = "";

        // Check to see if we need to capture customer info
        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        boolean isLayawayDelete =  false;
        if (txnADO instanceof LayawayTransactionADO )
        {
            isLayawayDelete = ((LayawayTransactionADO)txnADO).isLayawayDelete();
        }


        //do not display CaptureCustomer screen if LayawayDelete and no balance due
        if (isLayawayDelete)
        {
            CurrencyIfc totalTenderAmount = ((AbstractRetailTransactionADO)txnADO).getAmountTender();
            if ((totalTenderAmount.compareTo(DomainGateway.getBaseCurrencyInstance()) != CurrencyIfc.EQUALS) && txnADO.getCaptureCustomer() == null)
            {
                bus.mail(new Letter("CaptureCustomer"), BusIfc.CURRENT);
            }
            else
            {
                TenderStateEnum tenderState = txnADO.evaluateTenderState();

                letter = tenderState.toString();
                bus.mail(new Letter(letter), BusIfc.CURRENT);
            }

        }

        else if (isCaptureCustomerNeeded(bus, utility, txnADO))
        {
            bus.mail(new Letter("CaptureCustomer"), BusIfc.CURRENT);
        }
        else if (txnADO.capturePATCustomer())
        {
            bus.mail(new Letter("CaptureIRSCustomer"), BusIfc.CURRENT);
        }
        else
        {
            // Evaluate the balance
            TenderStateEnum tenderState = txnADO.evaluateTenderState();
            letter = tenderState.toString();
            // Discard a swiped ahead card if the balance is negative per functional specs
            if (cargo.getCurrentTransactionADO().getBalanceDue().signum() == CurrencyIfc.NEGATIVE)
            {
                PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
                paymentManager.clearSwipeAheadData(cargo.getRegister().getWorkstation());
            }
            // bypass going to tender options if the customer has already swiped a card
            if (TenderStateEnum.TENDER_OPTIONS.equals(tenderState))
            {
                // isSwipeAhead via PaymentManager
                PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
                boolean isSwipeAhead = paymentManager.isSwipeAhead(cargo.getRegister().getWorkstation());
                if (isSwipeAhead)
                {
                    String balanceDue = cargo.getCurrentTransactionADO().getBalanceDue().toFormattedString();
                    Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                    String amount = LocaleUtilities.parseCurrency(balanceDue, defaultLocale).toString();
                    cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);

                    letter = "SwipeAhead";
                }
            }
            else if (TenderStateEnum.CHANGE_DUE.equals(tenderState) &&
                    cargo.isGiftCardActivationsCanceled())
            {
                letter = "RefundOptions";
            }

            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

    /**
     * Determines whether or not customer information needs to be captured based on factors such as whether or not
     * the info has already been captured, transaction type/conditions (redeems, returns, exchanges, sends,
     * price adjustments) and statuses of the Customer.360Customer and Customer.NegativeAmtDue parameters
     *
     * @param utility Utility class
     * @param txnADO current transaction
     */
    protected boolean isCaptureCustomerNeeded(BusIfc bus, UtilityIfc utility, RetailTransactionADOIfc txnADO)
    {
        boolean isCaptureCustomerNeeded = false;



        // Make sure the customer info hasn't already been captured and that we
        // aren't set to use OracleCustomer
        if (txnADO != null && txnADO.getCaptureCustomer() == null &&
                "N".equals(utility.getParameterValue("OracleCustomer", "N")) )
        {
            // If the transaction is a redeem we know we need to capture customer info
            TransactionPrototypeEnum transactionType = txnADO.getTransactionType();
            TenderStateEnum tenderState = txnADO.evaluateTenderState();
            if (TransactionPrototypeEnum.REDEEM.equals(transactionType))
            {
                isCaptureCustomerNeeded = true;
            }
            // If there are any return line items that were not associated with
            // a retrieved return transaction and there is a zero balance, then
            // we must call the capture customer info use case.
            else if ((TenderStateEnum.PAID_UP.equals(tenderState)) &&
                     (TransactionPrototypeEnum.SALE_RETURN.equals(transactionType)))
            {
                SaleReturnTransactionADO saleReturnADO = (SaleReturnTransactionADO) txnADO;
                if (saleReturnADO.containsNonRetrievedReturnItems())
                {
                    isCaptureCustomerNeeded = true;
                }
            }
            // Otherwise, check to see if we need to capture customer info if there are return items in the
            // transaction but regardless of balance
            else if ("N".equals(utility.getParameterValue("NegativeAmtDue", "N")) )
            {
                if (TransactionPrototypeEnum.SALE_RETURN.equals(transactionType)
                    && (txnADO.containsReturnItems() || txnADO.containsSendItems()) )
                {
                    isCaptureCustomerNeeded = true;
                }
            }

            // Otherwise, if RM is not used, only capture customer info when there is a refund due
            // Note: When RM is used, customer is captured before sending request to RM server.
            else if ("N".equals(utility.getParameterValue("UseOracleRetailReturnManagement", "N")))
            {
                isCaptureCustomerNeeded = TenderStateEnum.REFUND_DUE.equals(tenderState);
            }
        }

        return isCaptureCustomerNeeded;
    }
}

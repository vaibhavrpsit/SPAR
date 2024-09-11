/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/ProcessPaymentSite.java /main/17 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/01/13 - Fixed failure to cancel House Account Transaction
 *                         when cancel button pressed or timout occurs.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    ohorne    08/19/11 - set masked ha account number as ref.number for ej
 *                         inclusion
 *    cgreene   07/26/11 - repacked into houseaccount.payment
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    ohorne    05/27/11 - cleanup
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         6/18/2007 5:07:49 PM   Anda D. Cadar
 *         cleanup
 *    8    360Commerce 1.7         6/14/2007 7:03:51 PM   Anda D. Cadar   fixed
 *          a crash while running with fr_CA
 *    7    360Commerce 1.6         5/21/2007 9:16:22 AM   Anda D. Cadar   EJ
 *         changes
 *    6    360Commerce 1.5         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         3/29/2007 6:48:35 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         5    .v8x      1.3.1.0     3/3/2007 1:56:48 PM    Maisa De Camargo
 *         Replaced "Sub-Total" to "Subtotal" to match receipt
 *    4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:29  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 01 2003 18:48:00   nrao
 * Added changes for House Account Payment.
 *
 *    Rev 1.0   Aug 29 2003 16:04:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:11:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:42:10   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:43:00   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   16 Jan 2002 18:03:52   vxs
 * Now get payment from bean model instead of ui.getinput()
 * Resolution for POS SCR-527: Account Info screen is missing a CurrentTextField for Payment Amount
 *
 *    Rev 1.0   Sep 21 2001 11:32:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceException;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Creates a new payment transaction and sets all attributes on it.
 * 
 */
@SuppressWarnings("serial")
public class ProcessPaymentSite extends PosSiteActionAdapter
{
    public static final int LENTH_OF_CURRENCY_DISPLAY_LINE = 38;
    public static final String SUB_TOTAL_STRING = "Subtotal";
    public static final String TOTAL_STRING = "Total";

    /**
     * lane name constant
     */
    public static final String LANENAME = "PaymentEnteredAisle";

    /**
     * @param bus the bus traversing this lane
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the user input
        PayHouseAccountCargo cargo = (PayHouseAccountCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        //get the entered payment amount from bean model
        String amt = ui.getInput();
        // convert the amount into a BigDecimal by calling the CurrencyService
        BigDecimal amount = BigDecimalConstants.ZERO_AMOUNT;
        try
        {
            amount = CurrencyServiceLocator.getCurrencyService().parseCurrency(amt, LocaleMap.getLocale(LocaleMap.DEFAULT));

        }
        catch (CurrencyServiceException ce)
        {
            logger.error("Amount is not parseable; setting the amount to null ", ce) ;
        }

        cargo.setPaymentAmount(amount);

        if (amount != null)
        {
            if (logger.isInfoEnabled()) logger.info("beanModel.getPaymentAmout()=" + amount.toString() + "");
        }
        //If a transaction number has not been designated then initialize a new transaction.
        PaymentTransactionIfc transaction = cargo.getTransaction();
        if (transaction == null)
        {
            transaction = DomainGateway.getFactory().getPaymentTransactionInstance();
            transaction.setTransactionType(TransactionIfc.TYPE_HOUSE_PAYMENT);
            transaction.setCashier(cargo.getCashier());
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.initializeTransaction(transaction);
        }
        transaction.getPayment().setEncipheredCardData(cargo.getEncipheredCardData());
        CurrencyIfc paymentAmount = DomainGateway.getBaseCurrencyInstance(amount);
        transaction.setPaymentAmount(paymentAmount);
        transaction.getPayment().setPaymentAccountType(PaymentConstantsIfc.ACCOUNT_TYPE_HOUSE_ACCOUNT);
        transaction.getPayment().setReferenceNumber(cargo.getEncipheredCardData().getMaskedAcctNumber());
        cargo.setTransaction(transaction);
        
        //ejournal payment info including payment amount, sub-total total.
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if(journal != null)
        {
            TransactionTotalsIfc totals = transaction.getTransactionTotals();
            
            String totalsJournalString = buildJournalString(totals);
            String paymentJournalString = transaction.journalLineItems(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL))+totalsJournalString;
            journal.journal(transaction.getCashier().getLoginID(),
                            transaction.getTransactionID(),
                            paymentJournalString);
        }
        else
        {
            logger.error( "No JournalManager found");
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Build Totals Journal String
     * @param totals the totals
     * @return the string
     */
    private String buildJournalString(TransactionTotalsIfc totals)
    {
        StringBuilder sb = new StringBuilder();
        String subtotalString =
                totals.getSubtotal()
                .subtract(totals.getDiscountTotal()).toGroupFormattedString();
        String totalString = totals.getGrandTotal().toGroupFormattedString();
   
        Object subTotalDataObject[]={subtotalString};
        String subTotal = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUB_TOTAL, subTotalDataObject);
   
        Object totalDataObject[]={totalString};
        String total = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL, totalDataObject);
   
        sb.append(Util.EOL).append(Util.EOL)
          .append(subTotal)
          .append(Util.EOL).append(Util.EOL)
          .append(total);
        return sb.toString();
    }

}

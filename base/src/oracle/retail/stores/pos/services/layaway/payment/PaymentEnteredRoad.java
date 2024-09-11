/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    nkgautam  05/14/09 - fix for journalling location instead of location
 *                         code
 *    acadar    04/20/09 - display location in EJ
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    nkgautam  03/17/09 - EJ fix for getting location journaled in Journal
 *                         Locale
 *    deghosh   01/16/09 - EJ i18n defect fixes
 *    deghosh   12/02/08 - EJ i18n changes
 *    deghosh   11/27/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    14   360Commerce 1.13        7/20/2007 3:11:33 AM   Anda D. Cadar   More
 *         alignment changes
 *    13   360Commerce 1.12        7/11/2007 3:02:27 AM   Alan N. Sinton  CR
 *         27623 - Modified calls to SaleReturnTransaction.journalLineItems()
 *         to use the JournalFormatterManager instead.
 *    12   360Commerce 1.11        7/10/2007 12:26:18 AM  Mathews Kochummen use
 *          locale format date
 *    11   360Commerce 1.10        6/27/2007 5:16:05 PM   Manikandan Chellapan
 *         CR#27265 Adjust numSpaces for Mimimum Down payment, Payment Total
 *    10   360Commerce 1.9         6/13/2007 7:18:17 AM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    9    360Commerce 1.8         5/24/2007 2:05:57 AM   Mathews Kochummen
 *         format for locales
 *    8    360Commerce 1.7         5/21/2007 7:46:20 PM   Anda D. Cadar   EJ
 *         changes
 *    7    360Commerce 1.6         5/9/2007 3:52:00 AM    Alan N. Sinton  CR
 *         26486 - Refactor of some EJournal code.
 *    6    360Commerce 1.5         3/30/2007 5:02:03 AM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         CR# 22742: EJournal should have "Subtotal' field header to be
 *         consistent with          receipt for Layway Payment and Layway
 *         Pickup
 *    5    360Commerce 1.4         12/6/2006 9:51:34 PM   Charles D. Baker CR
 *         21233 - Updated to log layaway location to EJournal during new
 *         layaway and layaway payment.
 *    4    360Commerce 1.3         2/25/2006 12:53:01 AM  Brett J. Larsen CR
 *         10575 - incorrect tax amount in e-journal for tax exempt
 *         transactions
 *
 *         replaced faulty code w/ new helper method in JournalUtilities
 *
 *    3    360Commerce 1.2         4/1/2005 2:59:19 AM    Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 9:54:01 PM   Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 11:43:01 PM  Robert Pearse
 *
 *   Revision 1.8.2.2  2004/10/18 18:20:54  jdeleau
 *   @scr 7381 Correct printing of tax in the e-journal for when printItemTax
 *   is turned off.
 *
 *   Revision 1.8.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.9  2004/10/07 20:32:11  bwf
 *   @scr 7317 Uncomment out fee line of pickup.
 *
 *   Revision 1.8  2004/09/30 20:21:52  jdeleau
 *   @scr 7263 Make printItemTax apply to e-journal as well as receipts.
 *
 *   Revision 1.7  2004/09/30 18:08:46  cdb
 *   @scr 7248 Cleaned up inventory location and state in LayawayTransaction object.
 *
 *   Revision 1.6  2004/07/22 20:39:43  kll
 *   @scr 5978: set the SalesAssociate during a Layaway Payment
 *
 *   Revision 1.5  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 11 2003 10:41:22   bwf
 * Deprecation Fix
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.0   Apr 29 2002 15:20:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.find.FindLayawayCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This class retrieves the input from the payment detail screen and places it
 * in the cargo.
 */
public class PaymentEnteredRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 6928301931504750402L;

    /**
     * lane name constant
     */
    public static final String LANENAME = "PaymentEnteredRoad";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/25 $";

    /**
     * sequence number constant
     */
    protected static final long GENERATE_SEQUENCE_NUMBER = -1;

    /**
     * date format for journal string
     */
    public static final String dateFormat = "MM/dd/yyyy";


    /**
     * Performs the traversal functionality for the aisle. In this case, the
     * payment information is retrieved from the ui and placed in the cargo.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        PaymentDetailBeanModel model =
            (PaymentDetailBeanModel) ui.getModel(POSUIManagerIfc.PAYMENT_DETAIL);

        // Gets layaway from cargo... set in the LayawayPaymentSite
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();
        LayawayIfc layaway = layawayCargo.getLayaway();
        PaymentIfc payment = layawayCargo.getPayment();

        // Get changes from ui
        layaway.setBalanceDue(model.getBalanceDue());

        // Get the journal manager and prepare for journalling
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);


        // Initialize variables for journaling
        String location = "";
        if(layaway.getLocationCode() != null)
        {
            location = layaway.getLocationCode().getText(locale);
        }
        String feeString = "";
        String totalString = "";
        String minPaymentString = "";
        String curPaymentString = "";

        if (layaway.getBalanceDue().equals(payment.getPaymentAmount()))
        {
            layawayCargo.setFinalPayment(true);
        }
        else
        {
            layawayCargo.setFinalPayment(false);
        }



        // The initial layaway...
        if(layaway.getStatus() == LayawayConstantsIfc.STATUS_UNDEFINED)
        {
            LayawayTransactionIfc layawayTransaction =
                        (LayawayTransactionIfc)layawayCargo.getTransaction();
            layawayTransaction.setPayment(payment);

            // Sets the layaways' total including the create fee
            layaway.setTotal(model.getBalanceDue());

            // Sets the grandTotal including the creation fee
            layawayTransaction.modifyFee(model.getLayawayFee());


            feeString = layaway.getCreationFee().toGroupFormattedString();
            totalString = layaway.getTotal().toGroupFormattedString();
            minPaymentString = layaway.getMinimumDownPayment().add(layaway.getCreationFee()).toFormattedString();
            curPaymentString = payment.getPaymentAmount().toFormattedString();
            journalInitialPayment(journal, location, feeString, totalString, minPaymentString,
                            curPaymentString, layawayTransaction, bus.getServiceName());
        }
        else
        if (layaway.getStatus() == LayawayConstantsIfc.STATUS_ACTIVE ||
            layaway.getStatus() == LayawayConstantsIfc.STATUS_NEW)
        {
            // The final payment...
            if (layawayCargo.isFinalPayment() ||
                layawayCargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_PICKUP)
            {
                LayawayTransactionIfc layawayTransaction = DomainGateway.getFactory()
                                                            .getLayawayTransactionInstance();

                // This new layaway transaction is initilaized using the
                // seed transaction and initialLayawayTransaction from the cargo.
                layawayTransaction.initialize(layawayCargo.getSeedLayawayTransaction(),
                                             layawayCargo.getInitialLayawayTransaction());
                layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_COMPLETE);

                // Add layaway and payment to transaction
                layawayTransaction.setPayment(payment);
                layawayTransaction.setLayaway(layaway);
                layawayTransaction.modifyFee(model.getLayawayFee());
                layawayTransaction.setReentryMode(layawayCargo.getRegister().getWorkstation().isTransReentryMode());
                // update cargo with payment transaction
                layawayCargo.setTenderableTransaction(layawayTransaction);

                // journal this last payment
                feeString = layaway.getCreationFee().toFormattedString();
                totalString = layaway.getTotal().toFormattedString();
                minPaymentString = layaway.getMinimumDownPayment().add(layaway.getCreationFee()).toFormattedString();
                curPaymentString = payment.getPaymentAmount().toFormattedString();
                journalPickup(journal,
                              layawayTransaction,
                              bus.getServiceName(), pm);
            }
            else
            // Not the first or the last payment...
            {
                // Create payment transaction
                LayawayPaymentTransactionIfc paymentTransaction =
                    DomainGateway.getFactory().getLayawayPaymentTransactionInstance();
                paymentTransaction.initialize(layawayCargo.getSeedLayawayTransaction());

                // Set layaway and payment in transaction
                paymentTransaction.setLayaway(layaway);
                paymentTransaction.setPayment(payment);
                paymentTransaction.setCustomer(layaway.getCustomer());
                if(layawayCargo.getSalesAssociate() != null)
                {
                    paymentTransaction.setSalesAssociate(layawayCargo.getSalesAssociate());
                }
                paymentTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_PAYMENT);
                paymentTransaction.setReentryMode(layawayCargo.getRegister().getWorkstation().isTransReentryMode());
                layawayCargo.setTenderableTransaction(paymentTransaction);
                //location = layaway.getLocationCode().getCode();
                if(layaway.getLocationCode() != null)
                {
                    location = layaway.getLocationCode().getText(locale);
                }
                journalPayment(journal,
                               location,
                               paymentTransaction,
                               bus.getServiceName());
            }
        }
    }

    /**
     * Journals the initial layaway payment information.
     * <P>
     *
     * @param JournalManagerIfc journal
     * @param String fee layaway fee
     * @param String total layaway total
     * @param String minPayment minimum payment amount
     * @param String curPayment current payment amount
     * @param TenderableTransactionIfc trans layaway transaction
     * @param serviceName service name used in log
     */
    protected void journalInitialPayment(JournalManagerIfc journal, String location, String fee, String total,
                                    String minPayment, String curPayment,
                                    TenderableTransactionIfc trans,
                                    String serviceName)
    {
        StringBuilder sb = new StringBuilder();
        Object[] dataArgs = new Object[2];

        // journal initial payment info
        sb.append(Util.EOL);

        dataArgs[0] = location;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LOCATION_LABEL, dataArgs))
           .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = fee;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_FEE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = total;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = minPayment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MINIMUM_DOWN_PAYMENT_LABEL, dataArgs))

            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = curPayment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAYMENT_TOTAL_LABEL, dataArgs));

        if (journal != null)
        {
            journal.journal(trans.getCashier().getLoginID(),
                            trans.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.error( "No JournalManager found");
        }
    }

    /**
     * Journals the layaway payment information.
     *
     * @param JournalManagerIfc journal
     * @param TenderableTransactionIfc trans payment transaction
     * @param serviceName service name used in log
     */
    protected void journalPayment(JournalManagerIfc journal,
                                  String location,
                                  TenderableTransactionIfc trans,
                                  String serviceName)
    {
        StringBuilder sb = new StringBuilder();
        Object[] dataArgs = new Object[2];

        String number = ((LayawayPaymentTransactionIfc)trans).getLayaway().getLayawayID();
        String expire = ((LayawayPaymentTransactionIfc)trans).getLayaway().getExpirationDate().toFormattedString();
        String balanceDue = ((LayawayPaymentTransactionIfc)trans).getLayaway().getBalanceDue().toGroupFormattedString();
        String payment = ((LayawayPaymentTransactionIfc)trans).getPayment().getPaymentAmount().toGroupFormattedString();
        String newBalance = ((LayawayPaymentTransactionIfc)trans).getLayaway().getBalanceDue().subtract(((LayawayPaymentTransactionIfc)trans).getPayment().getPaymentAmount()).toGroupFormattedString();

        // journal initial payment info

        sb.append(Util.EOL)

            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_PAYMENT_LABEL, null));
        sb.append(Util.EOL).append(Util.EOL);

        dataArgs[0] =  number;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_NUMBER_LABEL, dataArgs));
        sb.append(Util.EOL);

        dataArgs[0] =  expire;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXPIRATION_DATE_LABEL, dataArgs));
        sb.append(Util.EOL);

        dataArgs[0] = location;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LOCATION_LABEL, dataArgs));
        sb.append(Util.EOL).append(Util.EOL);

        dataArgs[0] = balanceDue;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BALANCE_DUE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAYMENT_AMOUNT_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = newBalance;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_BALANCE_DUE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUBTOTAL_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);

        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_LABEL, dataArgs));

        if (journal != null)
        {
            journal.journal(trans.getCashier().getLoginID(),
                            trans.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.error(
                         "No JournalManager found");
        }
    }

    /**
     * Journals the layaway pickup information.
     *
     * @param journal JournalManagerIfc journal
     * @param tenderTrans TenderableTransactionIfc trans layaway complete
     *            transaction
     * @param serviceName service name used in log
     */
    protected void journalPickup(JournalManagerIfc journal,
                                 TenderableTransactionIfc tenderTrans,
                                 String serviceName)
    {
        journalPickup(journal, tenderTrans, serviceName, null);
    }

    /**
     * Journals the layaway pickup information.
     *
     * @param journal JournalManagerIfc journal
     * @param tenderTrans TenderableTransactionIfc trans layaway complete
     *            transaction
     * @param serviceName service name used in log
     * @param pm Parameter Manager
     */
    protected void journalPickup(JournalManagerIfc journal,
                                 TenderableTransactionIfc tenderTrans,
                                 String serviceName,
                                 ParameterManagerIfc pm)
    {
        LayawayTransactionIfc trans = (LayawayTransactionIfc)tenderTrans;
        StringBuffer sb = new StringBuffer();
        Object[] dataArgs = new Object[2];
        Locale locale =  LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

        String number = trans.getLayaway().getLayawayID();
        String expire = trans.getLayaway().getExpirationDate().toFormattedString();
        String balanceDue = trans.getLayaway().getBalanceDue().toGroupFormattedString();
        String payment = trans.getPayment().getPaymentAmount().toGroupFormattedString();
        TransactionTotalsIfc totals = trans.getTransactionTotals();
        String subtotalString = totals.getSubtotal()
                .subtract(totals.getDiscountTotal()).toGroupFormattedString();
        String fee = trans.getLayaway().getCreationFee().toGroupFormattedString();
        String zero = DomainGateway.getBaseCurrencyInstance("0.00").toGroupFormattedString();
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        // journal initial payment info
        sb.append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_PICKUP_LABEL, null));
        sb.append(Util.EOL).append(Util.EOL);
        dataArgs[0] = number;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_NUMBER_LABEL, dataArgs));
        dataArgs[0] = expire;
        sb.append(Util.EOL)
            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXPIRATION_DATE_LABEL, dataArgs));

        TransactionDiscountStrategyIfc[] discounts = ((SaleReturnTransaction)trans).getTransactionDiscounts();
        if (discounts != null &&
            discounts.length > 0)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                sb.append(discounts[i].toJournalString(locale));
            }
        }

        if (((SaleReturnTransaction)trans).getTransactionTax().getTaxMode() != TaxIfc.TAX_MODE_STANDARD)
        {
            sb.append(((SaleReturnTransaction)trans).getTransactionTax().toJournalString(locale));
        }
        if (((SaleReturnTransaction)trans).getDefaultRegistry() != null)
        {
            sb.append(Util.EOL)
              .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_GIFT_REG_LABEL, null))
              .append(Util.EOL);
            dataArgs[0] = ((SaleReturnTransaction)trans).getDefaultRegistry().getID();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG_LABEL, dataArgs));
        }

        sb.append(Util.EOL);
        sb.append(formatter.journalLineItems(trans));

        String totalString = totals.getGrandTotal().toGroupFormattedString();

        sb.append(Util.EOL).append(Util.EOL);
        dataArgs[0] = subtotalString;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUBTOTAL_LABEL, dataArgs))
            .append(Util.EOL);


        TransactionTaxIfc transactionTax = ((SaleReturnTransaction) tenderTrans).getTransactionTax();
        formatter.formatTotals(trans, sb, totals, transactionTax, pm);


        dataArgs[0] = fee;
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_FEE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = totalString;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = balanceDue;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BALANCE_DUE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAYMENT_AMOUNT_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = zero;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_BALANCE_DUE_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUBTOTAL_LABEL, dataArgs))
            .append(Util.EOL).append(Util.EOL);
        dataArgs[0] = payment;
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_LABEL, dataArgs));
        if (journal != null)
        {
            journal.journal(trans.getCashier().getLoginID(),
                            trans.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.error( "No JournalManager found");
        }
    }
}

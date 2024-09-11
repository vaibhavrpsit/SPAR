/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/OfflinePaymentRoad.java /main/14 2012/09/12 11:57:10 blarsen Exp $
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
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/21/2007 9:34:33 PM   Mathews Kochummen use
 *          locale format
 *    4    360Commerce 1.3         5/21/2007 9:16:20 AM   Anda D. Cadar   EJ
 *         changes
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:00:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   09 Jul 2003 23:29:38   baa
 * modify screen to get customer name
 *
 *    Rev 1.1   Sep 03 2002 11:09:30   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:32   msg
 * Initial revision.
 *
 *    Rev 1.1   13 Feb 2002 15:20:34   jbp
 * changed journaling for offline layaway payment
 * Resolution for POS SCR-1228: Layaway Offline payment EJ entry format errors
 *
 *    Rev 1.0   Sep 21 2001 11:21:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;


import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;

/**
 * This class retrieves the input from the layaway offline screen and places it
 * in the cargo.
 * 
 * @version $Revision: /main/14 $
 */
public class OfflinePaymentRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 7898932479066364470L;
    /**
     * lane name constant
     */
    public static final String LANENAME = "OfflinePaymentRoad";
    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/14 $";
    /**
     * date format for journal string
     */
    public static final String dateFormat = "MM/dd/yyyy";

    /**
     * Performs the traversal functionality for the aisle. In this case, the
     * system is offline and the payment information is retrieved from the ui
     * and placed in the cargo.
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        OfflinePaymentBeanModel beanModel =
            (OfflinePaymentBeanModel) ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);

        // Get the journal manager and prepare for journalling
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        // Gets layaway from cargo... set in the LayawayPaymentSite
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();

        // Initializes the layaway
        LayawayIfc layaway = DomainGateway.getFactory().getLayawayInstance();

        // Sets layaway values from bean model
        layaway.setLayawayID(beanModel.getLayawayNumber());
        layaway.setBalanceDue(beanModel.getBalanceDue());
        layaway.setExpirationDate(beanModel.getExpirationDate());
        layaway.setStatus(LayawayConstantsIfc.STATUS_ACTIVE);
        // Builds the customer and sets to the layaway
        String customerName = beanModel.getCustomerName();
        CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
        customer.setCustomerName(customerName);
        layaway.setCustomer(customer);

        // Initializes the payment
        PaymentIfc payment = DomainGateway.getFactory().getPaymentInstance();

        // Sets payment values from bean model
        payment.setPaymentAmount(beanModel.getPaymentAmount());
        payment.setReferenceNumber(beanModel.getLayawayNumber());

        // Create payment transaction
        LayawayPaymentTransactionIfc paymentTransaction =
            DomainGateway.getFactory().getLayawayPaymentTransactionInstance();
        paymentTransaction.initialize(layawayCargo.getSeedLayawayTransaction());

        // Set layaway and payment to the transaction
        paymentTransaction.setLayaway(layaway);
        paymentTransaction.setPayment(payment);
        paymentTransaction.setCustomer(customer);
        paymentTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_PAYMENT);

        // Set items to the cargo
        layawayCargo.setLayaway(layaway);
        layawayCargo.setPayment(payment);
        layawayCargo.setTenderableTransaction(paymentTransaction);

        ui.customerNameChanged(customerName);

        // Journal Payment
        journalPayment(journal, paymentTransaction, bus.getServiceName());
    }

    /**
     * Journals the layaway payment information.
     * 
     * @param JournalManagerIfc journal
     * @param TenderableTransactionIfc trans payment transaction
     * @param serviceName service name for log
     */
    protected void journalPayment(JournalManagerIfc journal,
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
        String customerName =   ((LayawayPaymentTransactionIfc)trans).getCustomer().getCustomerName();

        dataArgs[0] = number;
		sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.LAYAWAY_NUMBER_LABEL, dataArgs));

		dataArgs[0] = balanceDue;
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.BALANCE_DUE_LABEL, dataArgs));

		dataArgs[0] = payment;
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.PAYMENT_AMOUNT_LABEL, dataArgs));

		dataArgs[0] = newBalance;
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.NEW_BALANCE_LABEL, dataArgs));

		dataArgs[0] = expire;
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.EXPIRATION_DATE_LABEL, dataArgs));

		dataArgs[0] = customerName;
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.CUSTOMER_NAME_LABEL, dataArgs));

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
}
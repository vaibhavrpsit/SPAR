/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/ConfirmSelectionSite.java /main/22 2014/05/09 13:15:49 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - set deprecation revision so we will know when to remove.
 *    yiqzhao   05/02/14 - Deprecate this class. Replaced by
 *                         CreateOrderTransactionSite.
 *    abhinavs  09/10/13 - Fix to set business customer name
 *    asinton   12/19/12 - changed argument to writeToJournal method to take
 *                         OrderCargo which is the super class of
 *                         PickupOrderCargo
 *    sgu       10/30/12 - add new file
 *    sgu       10/24/12 - refactor order view and cancel flow
 *    sgu       10/16/12 - set ordered amount for each order line item
 *    sgu       10/16/12 - move transaction sequence generation to the very end
 *    sgu       10/15/12 - refactor order pickup flow to support partial pickup
 *    sgu       10/15/12 - add ordered amount
 *    sgu       10/09/12 - use new createOrderTransaction from UI flow
 *    sgu       05/11/12 - check customer null pointer and set status bean even
 *                         when customer does not exist
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    nkgautam  10/12/10 - fixed missing discount amount totals
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         7/12/2007 3:11:11 PM   Anda D. Cadar   call
 *         toFormattedString(locale)
 *    5    360Commerce 1.4         4/25/2007 8:52:19 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/22/2006 11:45:14 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:22 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 26 2002 11:05:20   jriggins
 * Now make calls to CurrencyIfc.toFormattedString() instead of CurrencyIfc.toString().  Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:11:50   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:04   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.0   Mar 18 2002 11:41:38   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 06 2002 05:46:38   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   05 Feb 2002 18:02:16   cir
 * Set the totals in the totals bean
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   29 Jan 2002 15:06:34   sfl
 * Clone the whole order to support the ESC in the
 * Tender Options screen during doing the tender
 * for a special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   26 Jan 2002 18:49:56   cir
 * Clone the order line items and save them in the cargo
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   15 Jan 2002 18:49:48   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:52:08   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:18   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Displays the "CONFIRM_SELECTION" screen.
 * 
 * @deprecated As of 14.1, this site is no longer used.
 */
@Deprecated
public class ConfirmSelectionSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4367502582348105887L;

    /** class name constant */
    public static final String SITENAME = "EditItemStatusSite";

    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/22 $";

     /**
      * Customer name bundle tag
      */
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";

     /**
      * Customer name default text
      */
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * Visual presentation for the Edit Item Status screen.
     * </p>
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        //Initialize Variables
        PickupOrderCargo    cargo       = (PickupOrderCargo)bus.getCargo();
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TransactionUtilityManagerIfc transUtility =
            (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        LineItemsModel      model       = new LineItemsModel();
        StatusBeanModel     sbModel     = new StatusBeanModel();

        OrderTransactionIfc orderTransaction = cargo.getTransaction();
        if (orderTransaction == null)
        {
            OrderIfc order = cargo.getOrder();
            TransactionTaxIfc transactionTax = transUtility.getInitialTransactionTax();
            orderTransaction = order.createOrderTransaction(transactionTax);

            orderTransaction.setSalesAssociate(cargo.getOperator());
            orderTransaction.getPayment().setBusinessDate(cargo.getRegister().getBusinessDate());
            transUtility.initializeTransaction(orderTransaction);
            cargo.setTransaction(orderTransaction);

            /*
             * In the case of canceled items where a refund might be given then in order to make
             * the refund options work the original tenders need to be available in the order transaction.
             */
            if(orderTransaction.containsCanceledOrderLineItems())
            {
                SaleReturnTransactionIfc originalTransaction = (SaleReturnTransactionIfc)order.getOriginalTransaction();
                orderTransaction.setReturnTenderElements(getOriginalTenders(originalTransaction.getTenderLineItems()));
            }

            // journal the newly created order pickup/cancel transaction
            writeToJournal(cargo, orderTransaction, bus);
        }

        //StatusBeanModel Configure
        CustomerIfc customer = cargo.getOrder().getCustomer();
        if (customer != null)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            if(customer.isBusinessCustomer())
            {
                parms[0]=customer.getLastName();
                parms[1]="";
            }
            String pattern =
                utility.retrieveText("CustomerAddressSpec",
                        BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        CUSTOMER_NAME_TAG,
                        CUSTOMER_NAME_TEXT);
            String customerName =
                LocaleUtilities.formatComplexMessage(pattern, parms);

            sbModel.setCustomerName(customerName);
        }

        //LineItemsModel Configure
        model.setStatusBeanModel(sbModel);

        model.setLineItems(orderTransaction.getLineItems());
        //Display Screen
        ui.showScreen(POSUIManagerIfc.CONFIRM_SELECTION, model);
    }

    /**
     * Retrieve tenders from original transaction
     *
     * @param tenderList
     * @return ReturnTenderDataElement[] list of tenders
     */
    protected ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc[] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i = 0; i < tenderList.length; i++)
        {
            tenders[i] = DomainGateway.getFactory()
                    .getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }

    /**
     * Writes a transaction containing Pickup Order items to the Journal.
     *
     * @param cargo The pickup order cargo.
     * @param transaction The newly create sale return transaction containing.
     * @param bus The bus. order line items
     */
    public void writeToJournal(OrderCargoIfc cargo,
                               OrderTransactionIfc transaction,
                               BusIfc bus)
    {
        OrderIfc order = cargo.getOrder();
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().
                getManager(JournalFormatterManagerIfc.TYPE);
        if(journal != null && formatter != null)
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            journal.journal(transaction.getCashier().getLoginID(),
                    transaction.getTransactionID(),
                    formatter.journalOrder(transaction, order, pm));
        }
    }
}
/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/LookupReturnOrderSite.java /main/7 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/11/14 - avoid set null to transaction id
 *    jswan     11/10/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    yiqzhao   11/01/13 - Set transaction id for order original transaction.
 *    jswan     06/04/13 - Modified to display a more appropriate message when
 *                         the tour fails to retrieve an associated order for
 *                         returns.
 *    sgu       03/18/13 - remove check for cross currency
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    mkutiana  11/13/12 - With xc the customer is not required in an order
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

//java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;




/**
 * Retrieves an OrderIfc based upon the OrderSummaryEntryIfc reference.
 */
@SuppressWarnings("serial")
public class LookupReturnOrderSite extends PosSiteActionAdapter
{
    /**
     * class name constant
     */
    public static final String SITENAME = "LookupOrderSite";

    /**
     * Retrieves the order detail object based upon the order summary set
     * in cargo.
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
    	// get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        AbstractFindTransactionCargo cargo = (AbstractFindTransactionCargo)bus.getCargo();

        Letter  result  = new Letter (CommonLetterIfc.SUCCESS);
        boolean mailLetter = true;
        try
        {
            //lookup order
            OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
            OrderIfc order = orderManager.getOrder(
                    cargo.getSelectedTransactionOrderID(),
                    utility.getRequestLocales(),
                    cargo.getRegister().getWorkstation().isTrainingMode());
            
            if ( order.getOriginalTransaction() != null  &&
                 StringUtils.isBlank(order.getOriginalTransaction().getTransactionIdentifier().getTransactionIDString()) &&
                 cargo.getOriginalTransactionId() != null)       
            {
               order.getOriginalTransaction().setTransactionIdentifier(cargo.getOriginalTransactionId());
            }
            
            CustomerIfc customer = null;
            if(order.getOriginalTransaction() instanceof TenderableTransactionIfc &&
                    StringUtils.isNotBlank(((TenderableTransactionIfc)order.getOriginalTransaction()).getCustomerId()))
            {
                OrderTransactionIfc transaction = (OrderTransactionIfc)order.getOriginalTransaction();
                CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);

                CustomerSearchCriteriaIfc customerCriteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID,transaction.getCustomerId(), utility.getRequestLocales());
                Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                customerCriteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                customerCriteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                customerCriteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                customerCriteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                try
                {
                    customer = customerManager.getCustomer(customerCriteria);
                }
                catch (DataException de)
                {
                    throw new DataException(DataException.CUSTOMER_INFO_NOT_FOUND_ERROR, "Customer Info Not Found");
                }
                if (customer == null)
                {
                    throw new DataException(DataException.CUSTOMER_INFO_NOT_FOUND_ERROR, "Customer Info Not Found");
                }
                transaction.setCustomer(customer);
                transaction.setTransactionType(TransactionIfc.TYPE_ORDER_PARTIAL);
                cargo.moveTransactionToOriginal(transaction);
            }
            else if (order.getOriginalTransaction() instanceof TenderableTransactionIfc)
            {
                OrderTransactionIfc transaction = (OrderTransactionIfc)order.getOriginalTransaction();
                transaction.setTransactionType(TransactionIfc.TYPE_ORDER_PARTIAL);
                cargo.moveTransactionToOriginal(transaction);
            }
            
            // Use customer locale preferrences for the
            // pole display and receipt  subsystems
            if (customer != null)
            {
                Locale customerLocale = customer.getPreferredLocale();

                if (customerLocale != null)
                {
                    if (!customerLocale.equals(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT)))
                    {
                        //Do not print date format on the receipt based on the customer locale.
                        //Print the date format as per the store server locale for the picklist order.
                        //LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, customerLocale);
                        UIUtilities.setUILocaleForCustomer(customerLocale);
                    }
                }
            }
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                displayNoTransactionsForNumber(bus);
                mailLetter = false;
            }
            else
            {
                result = new Letter(CommonLetterIfc.DB_ERROR);
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                logger.error( " DB error: " + de.getMessage());
            }
        }
        
        if (mailLetter)
        {
            bus.mail(result,BusIfc.CURRENT);
        }
    }

    /*
     * Show the NO TRANSACTIONS FOR NUMBER ERROR SCREEN
     */
    protected void displayNoTransactionsForNumber(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("RetrieveTransactionNotFound");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,"Retry");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,"TransactionNotFound");
        // display the screen
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}

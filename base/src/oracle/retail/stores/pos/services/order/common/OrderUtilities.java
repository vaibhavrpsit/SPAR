/* ===========================================================================
* Copyright (c) 2007, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/OrderUtilities.java /main/39 2014/03/10 14:11:41 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    03/05/14 - OrderUtilities.assignNewOrderID no longer calls
 *                         OrderTransaction.setUniqueID(..)
 *    mkutiana  05/01/13 - moved the journaling to after the order number is
 *                         created
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    yiqzhao   03/07/13 - Journal order id when customer is not linked.
 *    sgu       01/07/13 - journal order location for order fill
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       06/22/12 - added utility functions to cancel new order ids
 *    sgu       06/21/12 - move xc order manager interface to domain module
 *    sgu       06/20/12 - refactor get order id
 *    sgu       05/22/12 - remove order filled status
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    sgu       05/11/12 - check order customer null pointer
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
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
 *    abondala  01/03/10 - update header date
 *    nganesh   03/14/09 - Formatted Special Order EJ
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *    vchengeg  02/17/09 - to format the EJournal for a Cancel Special Order
 *    deghosh   02/04/09 - EJ i18n defect fixes
 *    deghosh   01/22/09 - EJ i18n defect fixes
 *    nkgautam  12/30/08 - EJ Changes for extra space b/w price and Tax
 *    vchengeg  12/05/08 - Formatted Ejournal entry for HPQC bug : 990
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         4/25/2007 8:52:20 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    9    360Commerce 1.8         3/29/2007 6:46:31 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         10   .v8x      1.7.1.1     3/3/2007 2:22:37 PM    Maisa De Camargo
 *         Replaced "Sub-Total" to "Subtotal" to match receipt
 *         9    .v8x      1.7.1.0     3/3/2007 2:00:58 PM    Maisa De Camargo
 *         Replaced "Sub-Total" to "Subtotal" to match receipt
 *    8    360Commerce 1.7         11/27/2006 5:38:36 PM  Charles D. Baker CR
 *         21362 - Reintroducing previously deleted funcationlity for special
 *         order location behavior previously removed by inventory
 *    7    360Commerce 1.6         8/9/2006 6:11:40 PM    Brett J. Larsen CR
 *         19562 - special order has previous order in poslog
 *
 *         CR 4263 - invalid "deposit applied" line
 *
 *         v7x->360commerce merge
 *    6    360Commerce 1.5         5/12/2006 5:25:30 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         5/4/2006 5:11:51 PM    Brendan W. Farrell
 *         Remove inventory.
 *    4    360Commerce 1.3         2/24/2006 2:06:52 PM   Brett J. Larsen CR
 *         10575 - incorrect tax amount in e-journal for tax exempt
 *         transactions
 *
 *         replaced faulty code w/ new helper method in JournalUtilities
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:54 PM  Robert Pearse
 *
 *
 *    6    .v7x      1.3.1.1     7/20/2006 7:49:57 AM   Deepanshu       CR
 *         19562: Defect fixed, item status is corrected for special order
 *         line items in PosLog
 *    5    .v7x      1.3.1.0     7/7/2006 7:31:54 AM    Dinesh Gautam   CR
 *         4263: "Deposit applied" should not be in receipt and e.journal
 *
 *   Revision 1.11.2.2  2004/10/18 18:20:54  jdeleau
 *   @scr 7381 Correct printing of tax in the e-journal for when printItemTax
 *   is turned off.
 *
 *   Revision 1.11.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.15  2004/10/12 22:24:32  mweis
 *   @scr 7012 Honor the Order's channel when determining a default inventory location.
 *
 *   Revision 1.14  2004/10/12 18:53:52  mweis
 *   @scr 7012 Consolodate inventory UI model work under InventoryBeanModelIfc.
 *
 *   Revision 1.13  2004/10/12 16:38:51  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.12  2004/10/11 21:35:12  mweis
 *   @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.
 *
 *   Revision 1.11  2004/09/30 20:21:52  jdeleau
 *   @scr 7263 Make printItemTax apply to e-journal as well as receipts.
 *
 *   Revision 1.10  2004/09/29 20:46:07  mweis
 *   @scr 7012 Elaborate 'Special' as part of the Order params.
 *
 *   Revision 1.9  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "order list" (and fixes for SCR 7243).
 *
 *   Revision 1.8  2004/09/23 21:17:59  mweis
 *   @scr 7012 Special Order and Web Order parameters for POS Inventory
 *
 *   Revision 1.7  2004/08/26 16:23:01  mweis
 *   @scr 7012 Clump all "Inventory" related params into a hidden group.  Make necessary code changes to honor this new group.
 *
 *   Revision 1.6  2004/06/29 22:03:32  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.5.2.2  2004/06/17 21:57:00  jeffp
 *   Removed HEAD modifications since merged_rediron_POSInvIntegration.
 *
 *   Revision 1.5.2.1  2004/06/14 17:48:09  aachinfiev
 *   Inventory location/state related modifications
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jun 05 2003 11:10:36   bwf
 * In printOrderInformation, checked phone before appending it to StringBuilder.  Business customer has no home phone, only work.
 * Resolution for 2652: Cancel business customer's Sp. Order, POS crashes
 * Resolution for 2656: Pickup business customer's sp. order, POS crashes.
 *
 *    Rev 1.1   Aug 28 2002 10:08:22   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:13:10   msg
 * Initial revision.
 *
 *    Rev 1.1   02 Apr 2002 15:14:10   dfh
 * updates to better journal discounts for order pickup, order cancel, and return by customer (no receipt)
 * Resolution for POS SCR-1567: Picked up Orders/ return of picked up orders missing discounts in EJ
 *
 *    Rev 1.0   Mar 18 2002 11:41:10   msg
 * Initial revision.
 *
 *    Rev 1.9   Mar 07 2002 10:52:08   dfh
 * updates to better save the order location
 * Resolution for POS SCR-1522: Location for a Filled Special Order does not update correctly
 *
 *    Rev 1.8   04 Feb 2002 17:10:54   vxs
 * Journal formatting inside printOrderTotalsInformation()
 *
 *    Rev 1.7   01 Feb 2002 17:24:28   vxs
 * added variable balanceDue inside printOrderTotalsInformation()
 * Resolution for POS SCR-985: Special Order - EJournal completion
 *
 *    Rev 1.6   01 Feb 2002 13:17:00   vxs
 * Ejournal updates, added printOrderTotalsInformation()
 * Resolution for POS SCR-985: Special Order - EJournal completion
 *
 *    Rev 1.4   Jan 28 2002 14:07:00   dfh
 * fixing journalling again ......
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 28 2002 11:48:42   dfh
 * fix undefined status
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 27 2002 21:24:56   dfh
 * code cleanup, use new methods
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:52:06   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:00:12   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

// java imports
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.InventoryBeanModelIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
//------------------------------------------------------------------------------
/**
    The OrderUtilites contains methods that are shared by more than one
    Order service.
    @version $Revision: /main/39 $
**/
//------------------------------------------------------------------------------
public class OrderUtilities
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.order.common.OrderUtilities.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/39 $";

    /**
       retail transaction
    **/
    public RetailTransactionIfc transaction = null;

    /**
       standard line length in characters
    **/
    public static final int LINE_LENGTH = 40;

    /**
       constant for subtotal string length
    **/
    public static final int SUBTOTAL_LENGTH = 8;

    /**
       constant for total string length
    **/
    public static final int TOTAL_LENGTH = 5;

    /**
     * Constant used for defaulting the inventory status.
     */
    public static final int SALES_FLOOR = 0;

    /** System default location id for "Sales Floor". */
    protected int SYSTEM_DEFAULT_SALES_FLOOR_LOCATION = SALES_FLOOR; // == 0;

    /**
       Sales Associate ID
    **/
    public String employeeID = "";

    /**
       order line item
    **/
    public OrderLineItemIfc[] item = null;

    /**
       concatenate journal text
    **/
    public StringBuilder journalText = new StringBuilder();

    /**
     * Register business date
     * Used when order status changes to reflect item status change date
     */
    private  EYSDate businessDate =null;

    // for journal formatting purposes
    /** @deprecated As of release 12.0, replaced by {@link Util#SPACES} **/
    public static final String     SPACES = Util.SPACES;
    public static final int        ITEM_PRICE_LENGTH = 12;
    public static final int        ITEM_NUMBER_LENGTH = 20;
    public static final String     ITEM_STRING = "ITEM: ";

    //++ CR 27545
    /**
     * Locale
     */
    protected static Locale journalLocale = null;

    protected static Locale defaultLocale = null;


    public static final String JOURNAL_PREFIX="JournalEntry";

    /**
     * Gets the locale used for Journaling
     * @return
     */
    public static Locale getJournalLocale()
    {
        // attempt to get instance
        if (journalLocale == null)
        {
            journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        }
        return journalLocale;
    }
    //-- CR 27545
    /**
     * Gets the locale used for Journaling
     * @return
     */
    public static Locale getDefaultLocale()
    {
        // attempt to get instance
        if(defaultLocale == null)
        {
            defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        }
        return defaultLocale;
    }
    
    
    /**
     * Journals the order creation
     * @param bus
     * @param OrderTransactionIfc orderTransaction
     */
    public static void journalOrderCreation(BusIfc bus, OrderTransactionIfc orderTransaction)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(Util.EOL);
        Object dataObject[] = { orderTransaction.getOrderID() };
        String pickupDeliveryOrderHeader = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.PDO_ORDER_HEADER, dataObject);
        String specialOrderNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.SPECIAL_ORDER_NUMBER, dataObject);
        sb.append(pickupDeliveryOrderHeader).append(Util.EOL).append(Util.EOL).append(specialOrderNumber);
        JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        jmgr.journal(orderTransaction.getCashier().getEmployeeID(), orderTransaction.getTransactionID(), sb
                .toString());

    }

    /**
     * journalOrder is used for PrintOrder, FillOrder, CancelOrder and
     * PickupOrder services. A journal entry is made with minor modifications
     * depending on the type of calling service.
     *
     * @param order the order object
     * @param transaction the retail transaction
     * @param serviceType the type of the calling service
     * @param cargo the services cargo
     */
    public void journalOrder(OrderIfc order,
                             String transactionID, int serviceType,
                             AbstractFinancialCargo cargo,
                             BusIfc bus)
    {
        // set the text to be written to the journal depending on the Calling Lane

        // Since the Journal feature is not yet going through I18N changes, we
        // should pull out the English text for display.
        String serviceName = "UNKNOWN_SERVICE_TYPE";
        if (serviceType != OrderCargoIfc.SERVICE_TYPE_NOT_SET)
            serviceName = OrderCargoIfc.SERVICE_NAME_TEXT_LIST[serviceType];
        else
            logger.warn(
                        "unknown service type " + new Object[] { new Integer(serviceType) } + "!!!");

        //String serviceNameCAPS = serviceName.toUpperCase() + " ORDER";

        String serviceNameCAPS = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.serviceorder."+serviceName,null);
        // the date, time and cashier (operator)
        if (serviceType != OrderCargoIfc.SERVICE_PICKUP_TYPE)
        {
        	Date date = new Date();
            Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
            String dateTimeString = dateTimeService.formatDate(date, journalLocale, DateFormat.SHORT) +  " " +
                                        dateTimeService.formatTime(date, journalLocale, DateFormat.SHORT);
            journalText.append(dateTimeString);
            journalText.append(getOperatorID(cargo));
        }
        else
        {
            journalText.append(Util.EOL);
        }


        journalText.append(Util.EOL).append(Util.EOL);
        journalText.append(serviceNameCAPS);

        // journal the order header (order #, customer name, order status)
        journalText.append(printOrderInformation(order));

        // If called from Fill Order, journal the order location
        // Otherwise, skip this piece of code
        if (serviceType == OrderCargoIfc.SERVICE_FILL_TYPE)
        {
            CodeEntryIfc codeEntry = ((OrderCargo)cargo).getOrderLocationsList().findListEntryByCode(order.getStatus().getLocation());
            Object dataObject[]={codeEntry.getText(getJournalLocale())};

            String itemLocation = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_ITEM_LOC,dataObject);

            journalText.append(Util.EOL);
            journalText.append(itemLocation);

            businessDate = cargo.getRegister().getBusinessDate();
        }

        // journal the order detail (item #, item description, quantity and price)
        item = order.getOrderLineItems();
        for (int i=0; i < item.length; i++)
        {
        	journalText.append(Util.EOL);
        	journalText.append(printLineItem(item[i]));
        }

        if ((serviceType == OrderCargoIfc.SERVICE_PICKUP_TYPE) ||
            (serviceType == OrderCargoIfc.SERVICE_PRINT_TYPE) ||
            (serviceType == OrderCargoIfc.SERVICE_CANCEL_TYPE))
        {
            JournalFormatterManagerIfc formatter =
                (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(
                        JournalFormatterManagerIfc.TYPE);
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            journalText.append(formatter.journalOrderTotals(order, serviceType, pm));
        }

        JournalManagerIfc jmi =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        // Journal the entry.
        if (jmi != null)
        {
            jmi.journal(employeeID, transactionID, journalText.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }

    //---------------------------------------------------------------------
    /**
       This method is used to acquire the Sales Associate ID for journalling <p>
       @param order the order item
       @return StringBuilder sb is the string to append to the journal
    **/
    //--------------------------------------------------------------------------
    public StringBuilder getOperatorID(AbstractFinancialCargo cargo)
    {
        StringBuilder sb = new StringBuilder();
        String operatorID = "";
        // journal the employee id
        try
        {
            operatorID = cargo.getOperator().getEmployeeID();
        }
        catch (NullPointerException e)
        {
            /*
             * Don't worry if there is no sales associate,
             * not all transactions have one.
             */
            operatorID = "";
        }

//        sb.append("\nCashier: ")
//            .append(operatorID)
//            .append("\n\n\t");


        Object DataObject[]={operatorID};


        String cashier = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_CASHIER_ID,DataObject);


        sb.append(Util.EOL)
          .append(cashier)
          .append(Util.EOL)
          .append(Util.EOL);

        return(sb);
    }
    //---------------------------------------------------------------------
    /**
       This method is used to print information related to
       the order number, customer and order status
       @param order the order item
       @return StringBuilder sb is the string to append to the journal
    **/
    //--------------------------------------------------------------------------
    public StringBuilder printOrderInformation(OrderIfc order)
    {
        StringBuilder sb       = new StringBuilder();
        CustomerIfc orderCustomer = order.getCustomer();
        String previousStatus = "";
        int prevStatus = order.getStatus().getStatus().getPreviousStatus();

        // check for previous status undefined, status of -1
        if (prevStatus < OrderConstantsIfc.ORDER_STATUS_UNDEFINED)
        {
            previousStatus = "Undefined";
        }
        else
        {
            previousStatus = order.getStatus().getStatus().getDescriptors()[prevStatus];
        }

//        sb.append("\n\nOrder Number: ")
//          .append(order.getOrderID())
//          .append("\n  Customer: ")
//          .append(orderCustomer.getLastName())
//          .append(", ").append(orderCustomer.getFirstName());


        Object dataObject[]={order.getOrderID()};


        String orderNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_NUMBER,dataObject);
        
        sb.append(Util.EOL).append(orderNumber);

        if (orderCustomer != null)
        {
            Object customerDataObject[]={orderCustomer.getLastName(),orderCustomer.getFirstName()};
            String customerName=null;

            if(orderCustomer.getFirstName()==null || orderCustomer.getFirstName().length()==0)
            {
                customerName =  I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_TAG_LABEL,customerDataObject);
            }
            else
            {
                customerName = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_CUST_NAME,customerDataObject);
            }

            sb.append(Util.EOL).append(customerName);

            // make sure there is a phoneByType 0
            if(orderCustomer.getPhoneByType(0) != null)
            {
                sb.append("\n").append(orderCustomer.getPhoneByType(0).toFormattedString());
            }
            else if(orderCustomer.getPhoneByType(1) != null)  // business customers phone numbers are type 1
            {
                sb.append("\n").append(orderCustomer.getPhoneByType(1).toFormattedString());
            }
        }

        String newStatusDesc= order.getStatus().getStatus().statusToString();
        Object prevStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+previousStatus ,null)};

        String oldStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_PREVIOUS_STATUS,prevStatusDataObject);

        Object newStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+newStatusDesc ,null)};


        String newStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_NEW_STATUS,newStatusDataObject);


        sb.append(Util.EOL);
        sb.append(oldStatus);
        sb.append(Util.EOL);
        sb.append(newStatus);
        sb.append(Util.EOL);


        return sb;
    }

    //---------------------------------------------------------------------
    /**
       This method is used to print information related to
       the order line items
       @param item the order line item
       @return StringBuilder sb is the string to append to the journal
    **/
    //--------------------------------------------------------------------------
    public StringBuilder printLineItem(OrderLineItemIfc item)
    {
        StringBuilder sb           = new StringBuilder();
        Date date                 = new Date();
        String itemID             =   item.getItemID();
        ItemPriceIfc ip           =   item.getItemPrice();
        String sellingPrice       =   ip.getSellingPrice().toFormattedString();
        String extendedPrice      =   item.getExtendedSellingPrice().toGroupFormattedString();
        String itemDescription    =   item.getItemDescription(getJournalLocale());
        String qtyOrdered         =   item.getQuantityOrderedDecimal().toString();
        qtyOrdered = Integer.toString(item.getQuantityOrderedDecimal().intValue()); //CR 27545
        int prevStatus = item.getOrderItemStatus().getStatus().getPreviousStatus();
        int status =  item.getOrderItemStatus().getStatus().getStatus();

        String taxModeDesc = null;

        // if cancelled item, then qty and extended price are journalled as negatives
        if (status == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED)
        {
            qtyOrdered    = "(" + qtyOrdered + ")";
            extendedPrice = item.getExtendedSellingPrice().negate().toGroupFormattedString();
        }

        // Tax Mode
        int taxMode = ip.getItemTax().getTaxMode();
        String taxFlag = new String ("T");

        if (taxMode == TaxIfc.TAX_MODE_STANDARD
            && item.getTaxable() == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
            taxModeDesc=TaxIfc.TAX_MODE_DESCRIPTOR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
            taxModeDesc = TaxIfc.TAX_MODE_DESCRIPTOR[taxMode];
        }

        String previousStatus = "";
        if (prevStatus == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
        {
            previousStatus = "Undefined";
        }
        else
        {
            previousStatus = item.getOrderItemStatus().getStatus().getDescriptors()[prevStatus];
        }

        Object dataObject[]={itemID,extendedPrice.trim(),I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JOURNAL_PREFIX+".taxflag."+taxModeDesc,null)};

        String journalItem = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_ITEM,dataObject);


        Object qtyDataObject[]={qtyOrdered,sellingPrice};


        String itemQty = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_ITEM_QTY,qtyDataObject);


        sb.append(Util.EOL);
        sb.append(journalItem);
        sb.append(Util.EOL);

        // This needs to be changed
        sb.append(itemDescription);
        sb.append(Util.EOL);
        sb.append(itemQty);

        if (status != OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED && status != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
        {
        //++ CR 27545
        CurrencyIfc depositAmountCurrencyIfc = item.getOrderItemStatus().getDepositAmount();
        String depositAmount = depositAmountCurrencyIfc.toGroupFormattedString();


        Object DataObject[]={depositAmount};


        String deposit = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_DEPOSIT_APPLIED,DataObject);

        sb.append(Util.EOL);
        sb.append(deposit);

        //-- CR 27545
        }


        String newStatusDesc= item.getOrderItemStatus().getStatus().getDescriptors()[status];
        Object prevStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+previousStatus ,null)};


        String oldStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_PREVIOUS_STATUS,prevStatusDataObject);

        Object newStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+newStatusDesc ,null)};


        String newStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_NEW_STATUS,newStatusDataObject);


        sb.append(Util.EOL);
        sb.append(oldStatus);
        sb.append(Util.EOL);
        sb.append(newStatus);


        if(businessDate==null)
        {
            sb.append(" " + item.getLastStatusChange().toFormattedString(getDefaultLocale()));
        }
        else
        {
            sb.append(" " + businessDate.toFormattedString(getDefaultLocale()));
        }
        sb.append(Util.EOL);
        return sb;
    }

    //---------------------------------------------------------------------
    /**
       This method is used to print information related to
       the pickup order line items
       @param item the order line item
       @return StringBuilder sb is the string to append to the journal
    **/
    //--------------------------------------------------------------------------
    public StringBuilder printPickupLineItem(OrderLineItemIfc item)
    {
        StringBuilder sb           = new StringBuilder();
        Date date                 = new Date();
        String itemID             =   item.getItemID();
        ItemPriceIfc ip           =   item.getItemPrice();
        String sellingPrice       =   ip.getSellingPrice().toFormattedString();
        String extendedPrice      =   item.getExtendedSellingPrice().toGroupFormattedString();
        String itemDescription    =   item.getItemDescription(getJournalLocale());
        String qtyOrdered         =   item.getQuantityOrderedDecimal().toString();
        // We assume that the quantity orderded is not UOM?????
        qtyOrdered = Integer.toString(item.getQuantityOrderedDecimal().intValue()); //CR 27545
        int prevStatus = item.getOrderItemStatus().getStatus().getPreviousStatus();

        int status =  item.getOrderItemStatus().getStatus().getStatus();
        if(prevStatus==-1)prevStatus = status;

        // if cancelled item, then qty and extended price are journalled as negatives
        if (status == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED)
        {
            qtyOrdered    = "(" + qtyOrdered + ")";
            extendedPrice = item.getExtendedSellingPrice().negate().toGroupFormattedString();;
        }

        // Tax Mode
        int taxMode = ip.getItemTax().getTaxMode();
        String taxFlag = new String ("T");

        String taxModeDesc = null;



        if (taxMode == TaxIfc.TAX_MODE_STANDARD
            && item.getTaxable() == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
            taxModeDesc = TaxIfc.TAX_MODE_DESCRIPTOR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
            taxModeDesc = TaxIfc.TAX_MODE_DESCRIPTOR[taxMode];
        }

        String previousStatus = "";
        if (prevStatus == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
        {
            previousStatus = "Undefined";
        }
        else
        {
            previousStatus = item.getOrderItemStatus().getStatus().getDescriptors()[prevStatus];
        }




        Object dataObject[]={itemID,extendedPrice.trim(),I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JOURNAL_PREFIX+".taxflag."+taxModeDesc,null)};

        String journalItem = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_ITEM,dataObject);


        Object qtyDataObject[]={qtyOrdered,sellingPrice};


        String itemQty = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_ITEM_QTY,qtyDataObject);


        sb.append(Util.EOL);
        sb.append(journalItem);
        sb.append(Util.EOL);

        // This needs to be changed
        sb.append(itemDescription);
        sb.append(Util.EOL);
        sb.append(itemQty);

        if (status != OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED && status != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
        {
        //++ CR 27545
        CurrencyIfc depositAmountCurrencyIfc = item.getOrderItemStatus().getDepositAmount();
        String depositAmount = depositAmountCurrencyIfc.toGroupFormattedString();
        //sb.append("\n  Deposit applied: ").append(depositAmount);
        Object DataObject[]={depositAmount};


        String deposit = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_DEPOSIT_APPLIED,DataObject);


        sb.append(Util.EOL);
        sb.append(deposit);


        //-- CR 27545
        }


        String newStatusDesc = item.getOrderItemStatus().getStatus().getDescriptors()[status];


        Object prevStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+previousStatus ,null)};


        String oldStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_PREVIOUS_STATUS,prevStatusDataObject);

        Object newStatusDataObject[]={I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+JournalConstantsIfc.ORDER_STATUS_PREFIX+newStatusDesc ,null)};


        String newStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_NEW_STATUS,newStatusDataObject);


        sb.append(Util.EOL);
        sb.append(oldStatus);
        sb.append(Util.EOL);
        sb.append(newStatus);


        sb.append(" " + item.getLastStatusChange().toFormattedString(getDefaultLocale()));
        sb.append(Util.EOL);
        return sb;
    }


    /**
    *  This method is used to print information related to
    *  the subtotal, tax and total of order
    *  @param order the order item
    *  @param serviceType of the calling service
    *  @return StringBuilder sb is the string to append to the journal
    *  @deprecated Since 12.0, use JournalFormatter instead.
    */
    public StringBuilder printOrderTotalsInformation(OrderIfc order, int serviceType)
    {
        return printOrderTotalsInformation(order, serviceType, null);
    }

    /**
    *  This method is used to print information related to
    *  the subtotal, tax and total of order
    *  @param order the order item
    *  @param serviceType of the calling service
    *  @param pm Parameter Manager
    *  @return StringBuilder sb is the string to append to the journal
    *  @deprecated Since 12.0, use JournalFormatter instead.
    */
    public StringBuilder printOrderTotalsInformation(OrderIfc order, int serviceType, ParameterManagerIfc pm)
    {
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        return new StringBuilder(formatter.journalOrderTotals(order, serviceType, pm));
    }

    /**
     * Loads the order inventory location information for the model.
     *
     * @param order       The order.
     * @param storeNumber Store number.
     * @param pm          Parameter manager.
     * @param model       The model.
     */
    public void loadInventoryLocations(OrderIfc order, String storeNumber, ParameterManagerIfc pm, InventoryBeanModelIfc model)
    {
        // Set default to Special Order location
        Integer preSelectLocation = new Integer(getSpecialOrderDefaultLocation(pm));

        // If we actually have a Web Order, use that location instead.
        if (order != null && order.getStatus().getInitiatingChannel() == OrderConstantsIfc.ORDER_CHANNEL_WEB)
        {
            preSelectLocation = new Integer(getWebOrderDefaultLocation(pm));
        }


        // Spike the model
        UIUtilities.loadInventoryLocations(model, storeNumber, preSelectLocation);
    }

    /**
     * Returns the Order location to use for a new Special Order.
     * @param pm The ParameterManager.
     *           Can be <code>null</code> to force the system default location.
     * @return The Order location to use for a new Special Order.
     *
     * @see #getSpecialOrderDefaultStatus(ParameterManagerIfc)
     */
    public int getSpecialOrderDefaultLocation(ParameterManagerIfc pm)
    {
        int location = Integer.parseInt(getParameterValue(pm, "SpecialOrderDefaultInventoryLocation", Integer.toString(SYSTEM_DEFAULT_SALES_FLOOR_LOCATION)));
        return location;
    }

    /**
     * Returns the Order location to use for a new Web Order.
     * @param pm The ParameterManager.
     *           Can be <code>null</code> to force the system default location.
     * @return The Order location to use for a new Web Order.
     *
     * @see #getWebOrderDefaultStatus(ParameterManagerIfc)
     */
    public int getWebOrderDefaultLocation(ParameterManagerIfc pm)
    {
        int location = Integer.parseInt(getParameterValue(pm, "WebOrderDefaultInventoryLocation", Integer.toString(SYSTEM_DEFAULT_SALES_FLOOR_LOCATION)));
        return location;
    }

    protected String getParameterValue(ParameterManagerIfc pm, String param, String defValue)
    {
        String value = defValue;

        try
        {
            value = pm.getStringValue(param);
        } catch (Exception ignored) {}

        return value;
    }

    /**
     * Returns the parameterized form of the location passed in.
     * The parameterized form is expected to be one from the "OrderLocation" list.
     * If no matches are found, the location passed in is simply returned.
     *
     * @param  location The human-readable form of the location.
     * @param  bus      The bus.
     *
     * @return The parameterized form of the location passed in.
     */
    public String translateOrderLocationToParam(String location, BusIfc bus)
    {
        // Defense.
        if (location == null || bus == null)
        {
            return location;
        }

        // Worst case, we will simply return what we received.
        String answer = location;

        // Attempt to translate the human string back into the parameter one.
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        boolean found = false;
        try
        {
            String[] tempLocations = pm.getStringValues("OrderLocation");
            int i = 0;
            int index = 0;
            while(!found && (i < tempLocations.length))
            {
                String tempStr = tempLocations[i];
                if(utility.retrieveCommonText(tempStr).equals(location))
                {
                    found = true;
                    answer = tempLocations[i];
                }
                i++;
            }
        }
        catch (ParameterException ignore)
        {
            // keep our "answer" variable as is.
        }

        return answer;
    }

    /**
     * Method toItemJournal.
     *
     * @return Journal formatted string for the item number
     */
    protected String toItemJournal(String itemID)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Util.EOL);
        buffer.append(ITEM_STRING);
        buffer.append(itemID);
        buffer.append(Util.SPACES.substring(
                itemID.length(),
                ITEM_NUMBER_LENGTH));
        return buffer.toString();
    }

    /**
     * Set a new order ID to an order transaction
     * @param orderTransaction
     * @param orderMgr
     * @param register
     * @throws DataException
     */
    public static void assignNewOrderID(OrderTransactionIfc orderTransaction, OrderManagerIfc orderMgr,
            RegisterIfc register) throws DataException
    {
        String orderID;
        
        boolean xchannelOrderID = orderTransaction.containsXChannelOrderLineItem();
        if (xchannelOrderID)
        {
            oracle.retail.stores.domain.manager.xchannel.order.OrderManagerIfc xChannelOrderMgr =
                (oracle.retail.stores.domain.manager.xchannel.order.OrderManagerIfc)orderMgr;
            orderID = xChannelOrderMgr.getNewXChannelOrderID(register);
        }
        else
        {
            orderID = orderMgr.getNewOrderID(register);
        }
        orderTransaction.assignNewOrderID(orderID, xchannelOrderID);
    }

    /**
     * Cancel new order ids that have been assigned to the transaction but are no longer used
     * @param orderTransaction
     * @param orderMgr
     */
    public static void cancelUnusedNewOrderIDs(OrderTransactionIfc orderTransaction, OrderManagerIfc orderMgr)
    {
        String newStoreOrderId = orderTransaction.getNewStoreOrderIDAssigned();
        String newXCOrderId = orderTransaction.getNewXChannelOrderIDAssigned();
        String orderId = orderTransaction.getOrderID();

        if (!Util.isObjectEqual(newStoreOrderId, orderId))
        {
            cancelOrderID(newStoreOrderId, false, orderMgr); // Cancel store order ID
        }
        if (!Util.isObjectEqual(newXCOrderId, orderId))
        {
            cancelOrderID(newXCOrderId, true, orderMgr);     // Cancel xchannel order ID a
        }
    }

    /**
     * Cancel new order ids that have been assigned to the transaction
     * @param orderTransaction the transaction
     * @param orderMgr the order manager
     */
    public static void cancelNewOrderIDs(OrderTransactionIfc orderTransaction, OrderManagerIfc orderMgr)
    {
        String newStoreOrderId = orderTransaction.getNewStoreOrderIDAssigned();
        String newXCOrderId = orderTransaction.getNewXChannelOrderIDAssigned();

        cancelOrderID(newStoreOrderId, false, orderMgr);    // Cancel store order ID
        cancelOrderID(newXCOrderId, true, orderMgr);        // Cancel xchannel order ID
    }

    /**
     * Cancel an order id
     * @param orderID the order id
     * @param xchannelOrderID a boolean flag indicating if the order id is a cross channel one
     * @param orderMgr the order manager
     */
    protected static void cancelOrderID(String orderID, boolean xchannelOrderID, OrderManagerIfc orderMgr)
    {
        if (!StringUtils.isBlank(orderID))
        {
            if (xchannelOrderID)
            {
                try
                {
                    ((oracle.retail.stores.domain.manager.xchannel.order.OrderManagerIfc)orderMgr).
                    cancelNewXChannelOrderID(orderID);
                }
                catch (DataException de)
                {
                    logger.warn("Failed to cancel xchannel order id " + orderID, de);
                }
            }
            else
            {
                orderMgr.cancelNewOrderID(orderID);
            }
        }
    }
    
    /**
     * Format customer name for the order
     * @param order the order
     * @return the formatted order customer name
     */
    public static String formatCustomerName(OrderSummaryEntryIfc order)
    {
        String customerName = order.getCustomerCompanyName();
        if (StringUtils.isBlank(customerName))
        {
            String customerFirstName = "", customerLastName = "";
            if (!StringUtils.isBlank(order.getCustomerFirstName()))
            {
                customerFirstName = order.getCustomerFirstName();
            }
            if (!StringUtils.isBlank(order.getCustomerLastName()))
            {
                customerLastName = order.getCustomerLastName();
            }
            Object[] parms = {customerFirstName, customerLastName};
            String pattern = UIUtilities.retrieveText("Common", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME, 
                    TagConstantsIfc.CUSTOMER_NAME_TAG, TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
            customerName = LocaleUtilities.formatComplexMessage(pattern, parms);
        }
     
        return customerName;
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.   <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                               // begin toString()
        // result string
        String strResult = new String("Class:  OrderUtilities (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                               // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}

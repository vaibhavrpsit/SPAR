/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/AutomaticEmailSite.java /main/25 2013/01/10 14:04:07 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sgu    01/07/13 - add order fill flow
 *    sgu    11/13/12 - remove mix match for saved and current order
 *    sgu    10/24/12 - refactor order view and cancel flow
 *    sgu    05/22/12 - remove order filled status
 *    sgu    05/16/12 - check in changes after merge
 *    sgu    05/15/12 - remove column LN_ITM_REF from order line item tables
 *    sgu    05/14/12 - check order customer nullpointer
 *    sgu    05/11/12 - check order customer null pointer
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    ohorne 03/13/09 - properties updates for email messages
 *    deghos 02/18/09 - Removed the parameters
 *                      'EmailReplyURL','EmailFromAddress' and
 *                      'EmailMaximumMatches'
 *    kulu   10/30/08 - Update due to merge
 *    kulu   10/29/08 - Retrieve Email.COLUMN_SIZE from applicaiton.properties
 *
 * ===========================================================================

     $Log:
      6    360Commerce 1.5         4/21/2008 6:58:55 AM   Anil Kandru
           Reviewed the code and removed few print statements.
      5    360Commerce 1.4         4/21/2008 6:36:00 AM   Naveen Ganesh   Added
            Header and Footer for email format
      4    360Commerce 1.3         1/25/2006 4:10:49 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:27:15 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/8/2005 15:23:45     Rohit Sachdeva  6606:
           EMessage Text
      3    360Commerce1.2         3/31/2005 15:27:15     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:47     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:33     Robert Pearse
     $
     Revision 1.6  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.5  2004/04/19 14:43:01  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:09  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:22  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:45  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:03:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Aug 23 2002 15:57:44   jriggins
 * Replaced "Common" as the "spec" for grabbing the COLUMN_SIZE from the bundles to "Email."
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   23 May 2002 17:44:06   vxs
 * Removed unneccessary concatenations in logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:12:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:50   msg
 * Initial revision.
 *
 *    Rev 1.5   08 Mar 2002 16:59:18   pdd
 * Renamed pickup email parameter.
 * Resolution for POS SCR-175: CR/Order, E-mail parameter group needs to be updated
 *
 *    Rev 1.4   Feb 20 2002 16:42:54   dfh
 * better email to include all items
 * Resolution for POS SCR-1301: E-Mail needs to be updated due to new changes in crossreach for special order
 *
 *    Rev 1.3   Jan 17 2002 21:09:42   dfh
 * cleanup using new domain
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Dec 26 2001 13:54:44   dfh
 * does not send email if customer does not have an email
 * address, minor cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:52:06   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:06   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:30   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.order.common;

// java imports
import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EMessageWriteDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

//------------------------------------------------------------------------------
/**
    This site is used to send automatic emails for order services.
    @version $Revision: /main/25 $
**/
//------------------------------------------------------------------------------
public class AutomaticEmailSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4616498500301210494L;

    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/25 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "AutomaticEmailSite";
    /**
       column size bundle tag
    **/
    public static final String COLUMN_SIZE_TAG = "COLUMN_SIZE";
    /**
       default string length size for email contents
    **/
    public static final String    COLUMN_SIZE = "10";

    /**
       order partial pickup parameter
    **/
    public static final String ORDERPARTIALPICKUP = "OrderPartialPickup";

    /**
       email filled order parameter header
    **/
    public static final String EMAILFILLEDORDERHEADER = "AutomaticEmailFilledOrderHeader";

    /**
       email partial order parameter header
    **/
    public static final String EMAILPARTIALORDERHEADER = "AutomaticEmailPartialOrderHeader";
    /**
       email completed order parameter header
    **/
    public static final String EMAILPICKEDUPORDERHEADER = "AutomaticEmailPickedUpOrderHeader";
    /**
       email canceled order parameter header
    **/
    public static final String EMAILCANCELEDORDERHEADER = "AutomaticEmailCanceledOrderHeader";

    /**
       email filled order parameter footer
    **/
    public static final String EMAILFILLEDORDERFOOTER = "AutomaticEmailFilledOrderFooter";

    /**
       email partial order parameter footer
    **/
    public static final String EMAILPARTIALORDERFOOTER = "AutomaticEmailPartialOrderFooter";
    /**
       email completed order parameter footer
    **/
    public static final String EMAILPICKEDUPORDERFOOTER = "AutomaticEmailPickedUpOrderFooter";
    /**
       email canceled order parameter footer
    **/
    public static final String EMAILCANCELEDORDERFOOTER = "AutomaticEmailCanceledOrderFooter";


    /**
        subject bundle tag
    **/
    public static final String SUBJECT_TAG = "Subject";
    /**
        subject default text
    **/
    public static final String SUBJECT_TEXT = "Order Number: {0}";
    /**
        Customer name bundle tag
    **/
    protected static final String CUSTOMER_NAME_TAG = "CustomerName";
    /**
        Customer name default text
    **/
    protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";
    /**
        email item bundle tag
    **/
    protected static final String EMAIL_ITEM_TAG = "Item";
    /**
        email item default text
    **/
    protected static final String EMAIL_ITEM_TEXT = "Item";
    /**
        email item number bundle tag
    **/
    protected static final String EMAIL_ITEM_NUM_TAG = "ItemNumber";
    /**
        email item number default text
    **/
    protected static final String EMAIL_ITEM_NUM_TEXT = "ItemNumber";
    /**
        price bundle tag
    **/
    protected static final String EMAIL_PRICE_TAG="Price";
    /**
        price default text
    **/
    protected static final String EMAIL_PRICE_TEXT = "Price";
    /**
        item status bundle tag
    **/
    protected static final String EMAIL_ITEM_STATUS_TAG="ItemStatus";
    /**
        item status default text
    **/
    protected static final String EMAIL_ITEM_STATUS_TEXT="ItemStatus";
    /**
        message text bundle tag
    **/
    protected static final String MESSAGE_TAG="MessageText";
    /**
        message text default text
    **/
    protected static final String MESSAGE_TEXT="{0}Order Number: {1}            Order Date:   {2}\n{3}{4}{5}{6}\n{7}{8}{9}{10}";
    /**
        message details bundle tag
    **/
    protected static final String MESSAGE_DETAILS_TAG="Details";
    /**
        message details default text
    **/
    protected static final String MESSAGE_DETAILS_TEXT="{0}{1}{2}{3}\n";

    //--------------------------------------------------------------------------
    /**
       Sends automatic emails for order services: Fill, Pickup, and Cancel.
       Sends email for partial-filled orders based upon the value of the
       OrderPartialPickup parameter. Email text is based upon the order status
       and the value of email parameters: AutomaticEmailFilledOrder,
       AutomaticEmailPartialOrder, AutomaticEmailPickedUpOrder,
       and AutomaticEmailCanceledOrder.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	Letter          letter   = null;
        OrderCargo      cargo    = (OrderCargo) bus.getCargo();

        String emailAddress = null;
        CustomerIfc customer = cargo.getOrder().getCustomer();
        if (customer != null)
        {
            emailAddress = customer.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME).getEmailAddress();
        }

        UtilityManagerIfc utility =
            (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // customer has email address
        if (emailAddress != null && emailAddress.length() > 0)
        {
            ParameterManagerIfc paramManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            OrderIfc        order    = cargo.getOrder();
            StoreIfc        store    = cargo.getStoreStatus().getStore();
            boolean         partialPickup   =   true; //default param value is defined as Y in requirements
            boolean         sendEmail = true; // can send email

            // need to revist when Customer supports Locales
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

            // create emessage and populate its fields
            EMessageIfc message = DomainGateway.getFactory().getEMessageInstance();
            message.setMessageID("");    //set by the EMessageWriteDataTransaction??
            message.setShipToStoreID(store.getStoreID());
            message.setShipToLocationName(store.getLocationName(locale));
            message.setOrderID(order.getOrderID());
            message.setCustomerID(customer.getCustomerID());

            // Get customer name from the bundle.
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String pattern =
              utility.retrieveText("Common",
                                   BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                   CUSTOMER_NAME_TAG,
                                   CUSTOMER_NAME_TEXT,
                                   locale);
            String customerName =
              LocaleUtilities.formatComplexMessage(pattern, parms);
            message.setCustomerName(customerName);

            message.setTimestampBegin();

            // Create string from the bundle
            parms = new Object[] { order.getOrderID() };
            pattern =
                utility.retrieveText("Email",
                                     BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                     SUBJECT_TAG,
                                     SUBJECT_TEXT,
                                     locale);
            String subject =
                LocaleUtilities.formatComplexMessage(pattern, parms);
            message.setSubject(subject);

            message.addRecipient(emailAddress);

            String[]    messageTextHeader    = {""};
            String[]    messageTextFooter    = {""};
            int         orderStatus     = order.getOrderStatus();

            //use different e-mail text depending on order status
            switch (orderStatus)
            {
                // filled -  but may have picked up item(s)
                case OrderConstantsIfc.ORDER_STATUS_FILLED:
                    try
                    {
                        messageTextHeader = paramManager.getStringValues(EMAILFILLEDORDERHEADER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn(
                                    "Error retrieving AutomaticEmailFilledOrderHeader parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }
                    try
                    {
                        messageTextFooter = paramManager.getStringValues(EMAILFILLEDORDERFOOTER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn(
                                    "Error retrieving AutomaticEmailFilledOrderFooter parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }
                    break;

                // completed
                case OrderConstantsIfc.ORDER_STATUS_COMPLETED:
                    try
                    {
                        messageTextHeader = paramManager.getStringValues(EMAILPICKEDUPORDERHEADER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn(
                                    "Error retrieving " + EMAILPICKEDUPORDERHEADER + " parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }

                    try
                    {
                        messageTextFooter = paramManager.getStringValues(EMAILPICKEDUPORDERFOOTER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn(
                                    "Error retrieving " + EMAILPICKEDUPORDERFOOTER + " parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }
                    break;

                // canceled
                case OrderConstantsIfc.ORDER_STATUS_CANCELED:
                    try
                    {
                        messageTextHeader = paramManager.getStringValues(EMAILCANCELEDORDERHEADER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn( "Error retrieving AutomaticEmailCanceledOrderHeader parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }
                    try
                    {
                        messageTextFooter = paramManager.getStringValues(EMAILCANCELEDORDERFOOTER);
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn( "Error retrieving AutomaticEmailCanceledOrderFooter parameter e-message will contain no text.\n" + pe.getMessage() + "");
                    }
                    break;

                // partial
                case OrderConstantsIfc.ORDER_STATUS_PARTIAL:
                    try
                    {
                        partialPickup = paramManager.getStringValue(ORDERPARTIALPICKUP).equalsIgnoreCase("Y");
                    }
                    catch(ParameterException pe)
                    {
                        logger.warn( "Error retrieving OrderPartialPickup parameter using default parameter value of Y.\n" + pe.getMessage() + "");
                    }
                    if (partialPickup)  // partial pickups allowed ?
                    {
                        try
                        {
                            messageTextHeader = paramManager.getStringValues(EMAILPARTIALORDERHEADER);
                        }
                        catch(ParameterException pe)
                        {
                            logger.warn(
                                        "Error retrieving AutomaticEmailPartialOrderHeader parameter e-message will contain no text.\n" + pe.getMessage() + "");
                        }
                        try
                        {
                            messageTextFooter = paramManager.getStringValues(EMAILPARTIALORDERFOOTER);
                        }
                        catch(ParameterException pe)
                        {
                            logger.warn(
                                        "Error retrieving AutomaticEmailPartialOrderFooter parameter e-message will contain no text.\n" + pe.getMessage() + "");
                        }
                    }
                    else
                    {
                        sendEmail = false;
                        logger.warn( "Cannot pickup partial order, do not send email");
                    }
                    break;
            } // switch

            if (sendEmail) //attempt to send the message
            {
                // MessageFormat parameter array
                Object messageTextParms[] = new Object[9];

                // Attempt to pull the column size from the bundle.
                String columnSizeStr =
                    Gateway.getProperty("application", "Email.COLUMN_SIZE", "10");

                int columnSize =
                    Integer.parseInt(columnSizeStr);

                StringBuffer temp = new StringBuffer();
                for (int i = 0; i < messageTextHeader.length; i++ )
                {
                    //do we need formatting here?
                    //could strip or add line feeds if necesary
                    temp.append(messageTextHeader[i] + "\n");
                }
                messageTextParms[0] = temp.toString();
                temp = null;

                messageTextParms[1] = order.getOrderID();
                messageTextParms[2] = order.getTimestampCreated().toFormattedString(locale);

                messageTextParms[3] =
                    pad(utility.retrieveText("Email",
                                             BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                             EMAIL_ITEM_TAG,
                                             EMAIL_ITEM_TEXT,
                                             locale),
                                             columnSize);

                messageTextParms[4] =
                    pad(utility.retrieveText("Email",
                                             BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                             EMAIL_ITEM_NUM_TAG,
                                             EMAIL_ITEM_NUM_TEXT,
                                             locale),
                                             columnSize);

                messageTextParms[5] =
                    pad(utility.retrieveText("Email",
                                             BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                             EMAIL_PRICE_TAG,
                                             EMAIL_PRICE_TEXT,
                                             locale),
                                             columnSize);

                messageTextParms[6] =
                    pad(utility.retrieveText("Email",
                                             BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                             EMAIL_ITEM_STATUS_TAG,
                                             EMAIL_ITEM_STATUS_TEXT,
                                             locale),
                                             columnSize);

                AbstractTransactionLineItemIfc[] lineItems = order.getLineItems();
                StringBuffer messageDetailsStr = new StringBuffer(200);
                for (AbstractTransactionLineItemIfc lineItem : lineItems)
                {
                    SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItem;

                    // Format the details of the message from the bundle.
                    Object messageDetailsParms[] = new Object[4];
                    messageDetailsParms[0] = pad(item.getPLUItem().getDescription(locale),
                                                 columnSize);
                    messageDetailsParms[1] = pad(item.getItemID(),
                                                 columnSize);
                    messageDetailsParms[2] = pad(item.getItemPrice().getExtendedDiscountedSellingPrice().toString(),
                                                 columnSize);
                    messageDetailsParms[3] = pad(item.getOrderItemStatus().getStatus().statusToString(),
                                                 columnSize);

                    String messageDetailsPattern =
                        utility.retrieveText("Email",
                                             BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                             MESSAGE_DETAILS_TAG,
                                             MESSAGE_DETAILS_TEXT,
                                             locale);
                    messageDetailsStr
                        .append(LocaleUtilities.formatComplexMessage(messageDetailsPattern,
                                                                     messageDetailsParms));
                }
                // Add the formatted details to the message text.
                messageTextParms[7] = messageDetailsStr.toString();

                StringBuffer footer = new StringBuffer();
                for (int i = 0; i < messageTextFooter.length; i++ )
                {
                    //do we need formatting here?
                    //could strip or add line feeds if necesary
                    footer.append(messageTextFooter[i] + "\n");
                }
                messageTextParms[8] = footer.toString();
                footer = null;

                //add the text to the message
                String messageTextPattern =
                    utility.retrieveText("Email",
                                         BundleConstantsIfc.ORDER_BUNDLE_NAME,
                                         MESSAGE_TAG,
                                         MESSAGE_TEXT,
                                         locale);

                String messageTextStr =
                    LocaleUtilities.formatComplexMessage(messageTextPattern, messageTextParms);


                message.setMessageText(messageTextStr);
                //give it a timestamp
                message.setTimestampSent(); //should this be done further down the line?

                EMessageWriteDataTransaction writeDataTransaction = null;

                writeDataTransaction = (EMessageWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.EMESSAGE_WRITE_DATA_TRANSACTION);

                // send the message
                try
                {
                    writeDataTransaction.sendEMessage(message);
                }
                catch (DataException de)
                {
                    letter = new Letter(CommonLetterIfc.DB_ERROR);
                    logger.error( " DB error: " + de.getMessage() + "");
                }
                if (logger.isInfoEnabled()) logger.info( "...EMessage sent...\n" + message + "");
            } // send email
        } // has email address
        else if (customer != null)
        {
            //customer does NOT have email address, log warning
            logger.warn( "Customer: " + customer.getFirstLastName() + " does NOT have an email address do not send email");
        }
        else
        {
            //No customer is linked to the order
            logger.warn( "No customer is linked to the order. No customer notification email is sent.");
        }

        letter = new Letter(CommonLetterIfc.SUCCESS);
        bus.mail(letter, BusIfc.CURRENT);

    } // end arrive

    //--------------------------------------------------------------------------
    /**
       Adds spaces to or truncates argument so that the length of the
       String returned is equal to columnSize.
       <P>
       @param value  the string to pad
    **/
    //--------------------------------------------------------------------------
    String pad(String value, int columnSize)
    {
        int length = value.length();

        if (length < columnSize)
        {
            for (int i=0; i < (columnSize - length); i++)
            {
                value += " ";
            }
            return value;
        }
        else
        {
            return value.substring(0,columnSize);
        }
    }
} // AutomaticEmailSite


/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderListRenderer.java /main/23 2013/07/09 14:16:35 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/09/13 - Fix to set correct order type in ordersearch results
 *    sgu       05/07/12 - add xchannel support in order summary
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    acadar    02/10/09 - use default locale for date/time display
 *    acadar    02/09/09 - use default locale for display of date and time
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    ddbaker   11/14/08 - Updates due to merge.
 *    ddbaker   11/13/08 - Updated to eliminate clipping of the order list and
 *                         buttons for longer languages.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         7/17/2007 2:36:32 PM   Anda D. Cadar   do
 *         not display the ISO currencyCode for base currency
 *    7    360Commerce 1.6         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *         changes for CR 27494: POS 1st initialization when Server is offline
 *    6    360Commerce 1.5         6/12/2007 8:48:23 PM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    5    360Commerce 1.4         5/11/2007 4:10:46 PM   Mathews Kochummen use
 *          locale's date format
 *    4    360Commerce 1.3         5/8/2007 11:32:28 AM   Anda D. Cadar
 *         currency changes for I18N
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 06 2002 17:25:28   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:38   msg
 * Initial revision.
 *
 *    Rev 1.1   18 Feb 2002 11:30:00   baa
 * ui changes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   16 Feb 2002 17:56:42   baa
 * Initial revision.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DateFormat;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;
//-------------------------------------------------------------------------
/**
   This is the renderer for the Email list.
  $Revision: /main/23 $
*/
//----------------------------------------------------------------------------
public class OrderListRenderer extends AbstractListRenderer
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 6754215253039610924L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/23 $";
    
    /**
      The property for the currency format.
      @deprecated as of release Bahamas java DecimalFormat for Currency is being used
    **/
    public static final String CURRENCY_FORMAT = "#,##0.00;(#,##0.00)";
    /**
       The property for the currency format.
       @deprecated as of release Bahamas java DecimalFormat for Currency is being used
    **/
    protected String currencyFormat = CURRENCY_FORMAT;

    /** the default weights that layout the first display line */
    public static int[] DEFAULT_WEIGHTS = {100};

    /** the default weights that layout the second display line */
    public static int[] DEFAULT_WEIGHTS2 = {24,23,19,17,17}; //{30,10,20,15,15,10};

    /** the default weights that layout the first display line */
    public static int[] DEFAULT_WIDTHS = {5};

    /** the default weights that layout the second display line */
    public static int[] DEFAULT_WIDTHS2 = {1,1,1,1,1};

    public static int CUSTOMER   = 0;

    public static int ORDER_NO   = 1;
    public static int DATE       = 2;
    public static int STATUS     = 3;
    public static int TYPE       = 4;
    public static int TOTAL      = 5;
    public static int MAX_FIELDS = 6;

    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.OrderListRenderer.class);


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public OrderListRenderer()
    {
        super();
        setName("OrderListRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;
        secondLineWeights = DEFAULT_WEIGHTS2;
        firstLineWidths = DEFAULT_WIDTHS;
        secondLineWidths = DEFAULT_WIDTHS2;
        // look up the label weights
        setFirstLineWeights("orderListRendererWeights");
        setSecondLineWeights("orderListRendererWeights2");
        setFirstLineWidths("orderListRendererWidths");
        setSecondLineWidths("orderListRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak  = CUSTOMER;
        secondLineBreak = TOTAL;

        initialize();
    }

     //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {
        labels[ORDER_NO].setHorizontalAlignment(JLabel.LEFT);
        labels[CUSTOMER].setHorizontalAlignment(JLabel.LEFT);
        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[STATUS].setHorizontalAlignment(JLabel.LEFT);
        labels[TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[TOTAL].setHorizontalAlignment(JLabel.RIGHT);

    }
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        OrderSummaryEntryIfc order = (OrderSummaryEntryIfc)value;
        String status = order.statusToString(order.getOrderStatus());

        labels[ORDER_NO].setText(order.getOrderID());
        
        String customerName = OrderUtilities.formatCustomerName(order);
        labels[CUSTOMER].setText(customerName);
        
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String orderDate = dateTimeService.formatDate(order.getTimestampCreated().dateValue(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
        labels[DATE].setText(orderDate);
        labels[STATUS].setText(UIUtilities.retrieveCommonText(status,status));
        if(order.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            String orderTypePDO=OrderConstantsIfc.ORDER_CHANNEL_DESCRIPTORS[order.getOrderType()];
            labels[TYPE].setText(UIUtilities.retrieveCommonText(orderTypePDO,orderTypePDO));
        }
        else
        {
            String orderType = OrderConstantsIfc.ORDER_CHANNEL_DESCRIPTORS[order.getOrderType()];
            labels[TYPE].setText(UIUtilities.retrieveCommonText(orderType,orderType));
        }
        CurrencyIfc total = order.getOrderTotal();
        String totalString = total.toFormattedString();
        String baseCurrencyCode = "";
        try
        {
            baseCurrencyCode = DomainGateway.getBaseCurrencyType().getCurrencyCode();
            if(!total.getType().getCurrencyCode().equals(baseCurrencyCode))
            {
                totalString = total.toISOFormattedString();
            }
        }
        catch(Exception e)
        {
            logger.error("Currency Information not retrieved; store Server/database connectivity issues");
        }


        labels[TOTAL].setText(totalString);

   }
   //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields()  { }
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        this.props = props;
    }
   //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency.
       @deprecated as of Bahamas no longer in use
    */
    //---------------------------------------------------------------------
    protected void setCurrencyFormat()
    {

    }

    //---------------------------------------------------------------------
    /**
       Formats an EYSDate value for the JTable display
       @param date an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    protected String getDateString(EYSDate date)
    {
          return date.toFormattedString(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }


    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        String dummy = "dummy";
        OrderSummaryEntryIfc order = DomainGateway.getFactory().getOrderSummaryEntryInstance();

        order.setOrderID("111222222");
        order.setCustomerFirstName("first name");
        order.setCustomerLastName("last name");
        order.setTimestampCreated(new EYSDate(2001, 4, 16));
        order.setStoreOrderStatus(0);
        order.setInitiatingChannel(0);
        order.setStoreOrderTotal(DomainGateway.getBaseCurrencyInstance());


        return(order);
    }


    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        OrderListRenderer bean = new OrderListRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}

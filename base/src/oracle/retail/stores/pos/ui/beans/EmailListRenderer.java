/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailListRenderer.java /main/17 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/11/2007 4:14:33 PM   Mathews Kochummen use
 *          locale's date format
 *    3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:10:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Sep 18 2002 17:15:30   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 06 2002 17:25:24   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:34   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:51:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:54:16   msg
 * Initial revision.
 *
 *    Rev 1.0   16 Feb 2002 17:55:58   baa
 * Initial revision.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.text.DateFormat;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.UIUtilities;
//-------------------------------------------------------------------------
/**
   This is the renderer for the Email list.
  $Revision: /main/17 $
*/
//----------------------------------------------------------------------------
public class EmailListRenderer extends AbstractListRenderer

{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
       The property for the currency format.
    **/

    public static int ORDER_NO    = 0;
    public static int CUSTOMER    = 1;
    public static int DATE    = 2;
    public static int STATUS    = 3;
    public static int MAX_FIELDS    = 4;

    public static int[] DEFAULT_WEIGHTS = {30,25,20,25};


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public EmailListRenderer()
    {
        super();
        setName("EmailListRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;

        setFirstLineWeights("EmailListRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak  = MAX_FIELDS;
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

    }
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {

        EMessageIfc  eMessage = (EMessageIfc) value;

        labels[ORDER_NO].setText(eMessage.getOrderID());
        labels[CUSTOMER].setText(eMessage.getCustomerName());
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String dateString = dateTimeService.formatDate(eMessage.getTimestampSent().dateValue(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
        labels[DATE].setText(dateString);
        labels[STATUS].setText(getStatusString(eMessage.getMessageStatus()));

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
       Formats an EYSDate value for the JTable display
       @param date an EYSDate value
       @return String to be used by the JTable
       @deprecated as of release 5.5 use format from eysdate object
     */
    //---------------------------------------------------------------------
    protected static String getDateString(EYSDate date)
    {
         return date.toFormattedString(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

    //---------------------------------------------------------------------
    /**
       Formats the int status value for the JTable display
       @param status an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    protected static String getStatusString(int status)
    {
        String eStatus = EMessageIfc.MESSAGE_STATUS_DESCRIPTORS[status];
        return UIUtilities.retrieveCommonText(eStatus,eStatus);
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


        EMessageIfc  eMessage = DomainGateway.getFactory().getEMessageInstance();

        eMessage.setOrderID("111222222");
        eMessage.setCustomerName("test name");
        eMessage.setTimestampSent(new EYSDate(2001, 4, 16));
        eMessage.setMessageStatus(0);

        return(eMessage);
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

        EmailListRenderer bean = new EmailListRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}

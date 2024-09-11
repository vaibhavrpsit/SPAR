/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ExternalOrderSelectRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    abondala  06/09/10 - change the locale to default
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/19/10 - search flow update
 *    abondala  05/19/10 - Display list of external orders flow
 *    abondala  05/18/10 - renderer class to display external orders
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.text.DateFormat;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AbstractListRenderer;

//-------------------------------------------------------------------------
/**
   This is the renderer for the ExternalOrder list.
   $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class ExternalOrderSelectRenderer extends AbstractListRenderer

{
	private static final long serialVersionUID = -4379386801172856856L;

	/**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** the default weights that layout the first display line */
    public static int[] DEFAULT_WEIGHTS = {24,23,19,17,17};

    /** the default weights that layout the first display line */
    public static int[] DEFAULT_WIDTHS = {1,1,1,1,1};

    public static int ORDER_NUMBER    = 0;
    public static int DATE            = 1;
    public static int ACCOUNT         = 2;
    public static int LAST_NAME       = 3;
    public static int TOTAL           = 4;
    public static int MAX_FIELDS      = 5;


    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ExternalOrderSelectRenderer.class);


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public ExternalOrderSelectRenderer()
    {
        super();
        setName("ExternalOrderSelectRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;
        firstLineWidths = DEFAULT_WIDTHS;
        // look up the label weights
        setFirstLineWeights("externalOrderSelectRendererWeights");
        setFirstLineWidths("externalOrderSelectRendererWidths");

        fieldCount = MAX_FIELDS;
        lineBreak  = TOTAL;

        initialize();
    }

     //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {
        labels[ORDER_NUMBER].setHorizontalAlignment(JLabel.LEFT);
        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[ACCOUNT].setHorizontalAlignment(JLabel.LEFT);
        labels[LAST_NAME].setHorizontalAlignment(JLabel.LEFT);
        labels[TOTAL].setHorizontalAlignment(JLabel.RIGHT);

    }
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
    	ExternalOrderIfc order = (ExternalOrderIfc)value;

        labels[ORDER_NUMBER].setText(order.getNumber());
        if(order.getCreationDate() != null)
        {
	        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
	        String orderDate = dateTimeService.formatDate(order.getCreationDate(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
	        labels[DATE].setText(orderDate);
        }
        labels[ACCOUNT].setText(order.getAccount());
        labels[LAST_NAME].setText(order.getLastName());

        CurrencyIfc total = order.getTotal();
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
        @param props the properties object
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
    	 return null;
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

        ExternalOrderSelectRenderer bean = new ExternalOrderSelectRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}

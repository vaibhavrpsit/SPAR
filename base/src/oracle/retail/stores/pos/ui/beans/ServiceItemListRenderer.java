/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemListRenderer.java /main/29 2014/01/09 16:23:22 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *   cgreene   08/13/14 - Initial revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 * This is the renderer for the "Non-Merchandise" screen that uses the
 * {@link ServiceItemListBean}. It shows two columns, description and price
 * of the ItemSearchResults.
 * 
 * $Revision: /main/29 $
 */
public class ServiceItemListRenderer extends AbstractListRenderer<ItemSearchResult>
{
    private static final long serialVersionUID = -9053211115630876737L;
    
    protected static final Logger logger = Logger.getLogger(ServiceItemListRenderer.class); 

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/29 $";

    public static final int DESCRIPTION  = 0;
    public static final int PRICE        = 1;
    public static final int MAX_FIELDS   = 2;

    // setting the height of the rows to display
    public static int MAX_HEIGHT = 28;

    public static final int[] DEFAULT_WEIGHTS = { 70 ,30 };

    /**
     * Constructor
     */
    public ServiceItemListRenderer()
    {
        setName("ServiceItemListRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;

        setFirstLineWeights("ServiceItemRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak  = MAX_FIELDS;
        initialize();
    }

    /**
     * Initializes the optional components.
     */
    @Override
    protected void initOptions()
    {
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);
    }

    /**
     * Builds each line item to be displayed.
     */
    @Override
    public void setData(Object value)
    {
        ItemSearchResult itemSearchResult = (ItemSearchResult) value;

        labels[DESCRIPTION].setText(itemSearchResult.getItemDescription());
        CurrencyIfc cPrice = DomainGateway.getBaseCurrencyInstance(itemSearchResult.getPrice());
        labels[PRICE].setText(cPrice.toFormattedString());
    }

    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        ItemSearchResult item = new ItemSearchResult();
        item.setItemID("This is description is 37 chars long.");
        item.setPrice(new BigDecimal("120.00"));
        return item ;
    }

    /**
     *  Update the fields based on the properties
     */
    protected void setPropertyFields()  { }

    /**
     *  Set the properties to be used by this bean
     *  @param props the properties object
     */
    public void setProps(Properties props)
    {
        this.props = props;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = "Class:  ServiceItemListRenderer (Revision " + getRevisionNumber() + ")"
                + hashCode();
        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        ServiceItemListRenderer bean = new ServiceItemListRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }

}

/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AvailableToPromiseInventoryLineItemRenderer.java /main/2 2012/05/18 14:20:08 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.util.Properties;
import java.util.Vector;

import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;
//------------------------------------------------------------------------------
/**
 *    This is the renderer for the Customer list. It formats
 *    customer objects for display as a list entry.
 *    @version $Revision: /main/2 $
**/
//------------------------------------------------------------------------------
public class AvailableToPromiseInventoryLineItemRenderer extends AbstractListRenderer
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7170275324200384302L;

    /** revision number supplied by Team Connection */
    public static String revisionNumber = "$Revision: /main/2 $";

    public static int STORE_NUMBER = 0;
    public static int STORE_NAME   = 1;
    public static int ADDRESS      = 2;
    public static int STORE_PHONE  = 3;
    public static int QUANTITY     = 4;
    public static int DATE         = 5;
    public static int EMPTY1       = 6;
    public static int ENPTY2       = 7;
    public static int CITYSTATE    = 8;
    public static int MAX_FIELDS   = 9;

    public static int[] STORE_INVENTORY_WEIGHTS = {15,21,23,14,11,16};
    public static String AVAILABLE_TO_PROMISE_INVENTORY_WEIGHTS = "AvailableToPromiseInventoryLineItemRenderer";

    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Default Constructor.
     */
    //---------------------------------------------------------------------
    public AvailableToPromiseInventoryLineItemRenderer()
    {
        super();
        setName("StoreInventoryLineItemRenderer");
        // set default in case lookup fails
        firstLineWeights = STORE_INVENTORY_WEIGHTS;
        // look up the label weights
        setFirstLineWeights(AVAILABLE_TO_PROMISE_INVENTORY_WEIGHTS);
        secondLineWeights = firstLineWeights;

        fieldCount = MAX_FIELDS;
        lineBreak = DATE;
        secondLineBreak = MAX_FIELDS + 1;

        //initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
     protected void initOptions()
     {
        labels[STORE_NUMBER].setHorizontalAlignment(JLabel.LEFT);
        labels[STORE_NAME].setHorizontalAlignment(JLabel.CENTER);
        labels[ADDRESS].setHorizontalAlignment(JLabel.LEFT);
        labels[STORE_PHONE].setHorizontalAlignment(JLabel.CENTER);
        labels[QUANTITY].setHorizontalAlignment(JLabel.CENTER);
        labels[DATE].setHorizontalAlignment(JLabel.RIGHT);
        labels[EMPTY1].setHorizontalAlignment(JLabel.LEFT);
        labels[ENPTY2].setHorizontalAlignment(JLabel.CENTER);
        labels[CITYSTATE].setHorizontalAlignment(JLabel.LEFT);
     }

    //---------------------------------------------------------------------
    /**
     * This sets the fields of this ListCellRenderer.
     * @param customer oracle.retail.stores.domain.customer.Customer
     */
    //---------------------------------------------------------------------
     @SuppressWarnings("unchecked")
    public void setData(Object data)
    {
        if(data != null)
        {
            AvailableToPromiseInventoryLineItemModel model = 
                (AvailableToPromiseInventoryLineItemModel)data;

            labels[STORE_NUMBER].setText(model.getStoreID());
            labels[STORE_NAME].setText(model.getStoreName());
            labels[QUANTITY].setText(model.getQuantityAvailable());
            labels[DATE].setText(model.getDateAvailable());
            labels[EMPTY1].setText("  ");
            labels[ENPTY2].setText("  ");
            
            StringBuffer cityState=new StringBuffer();
            String countryCode = null;
            if(model.getAddress() != null)
            {
                AddressIfc addr = model.getAddress();

                if(!addr.getCity().equals(""))
                {
                    cityState.append(addr.getCity()).append(",");
                }

                if(!Util.isEmpty(addr.getState()))
                {
                    cityState.append(addr.getState()).append(" ");
                }
                cityState.append(addr.getPostalCode());
                
                labels[CITYSTATE].setText(cityState.toString());

                Vector vectAddrLn = addr.getLines();
                if(vectAddrLn != null && !vectAddrLn.isEmpty())
                {
                    labels[ADDRESS].setText(vectAddrLn.elementAt(0).toString());
                }
                countryCode = addr.getCountry();
            }
            else
            {
                labels[CITYSTATE].setText("");
                labels[ADDRESS].setText("");
            }

            PhoneIfc phone = model.getStorePhone();

            if(phone!=null)
            {
            	if (phone.getPhoneNumber() != null && !phone.getPhoneNumber().equals("") && countryCode !=null)
            	{
                    UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            		String formattedPhoneNumber = util.getFormattedNumber(phone.getPhoneNumber(), countryCode);
            		labels[STORE_PHONE].setText(formattedPhoneNumber);
            	}
               else
               {
                  labels[STORE_PHONE].setText("");
               }
            }
            else
            {
                labels[STORE_PHONE].setText("");
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields() { }

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
        Creates the prototype cell to speed updates.
        @return Customer the prototype renderer
     */
    //---------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Object createPrototype()
    {
        AvailableToPromiseInventoryLineItemModel model = new AvailableToPromiseInventoryLineItemModel();

        model.setDateAvailable("xx/xx/xx");
        model.setQuantityAvailable("999.99");
        model.setStoreID("11111");
        model.setStoreName("XXXXXXXXXXXXXX XXXX");

        AddressIfc addr = DomainGateway.getFactory().getAddressInstance();

        addr.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_OTHER);
        addr.setCity("XXXXXXXXXXXXXXXXXXXX");
        addr.setState("XX");

        Vector lineVect = new Vector();
        String addrLine = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        lineVect.addElement(addrLine);
        addr.setLines(lineVect);
        model.setAddress(addr);

        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.setPhoneNumber("521-555-1234");
        model.setStorePhone(phone);

        return(model);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        AvailableToPromiseInventoryLineItemRenderer bean = new AvailableToPromiseInventoryLineItemRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}

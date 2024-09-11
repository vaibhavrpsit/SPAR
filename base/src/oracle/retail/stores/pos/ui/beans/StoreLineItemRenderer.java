/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreLineItemRenderer.java /main/1 2012/06/21 12:42:41 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/07/12 - Creation
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
 *    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------
public class StoreLineItemRenderer extends AbstractListRenderer
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** revision number supplied by Team Connection */
    public static String revisionNumber = "$Revision: /main/1 $";

    public static int STORE_NUMBER = 0;
    public static int STORE_NAME   = 1;
    public static int ADDRESS      = 2;
    public static int STORE_PHONE  = 3;
    public static int EMPTY1       = 4;
    public static int ENPTY2       = 5;
    public static int CITYSTATE    = 6;
    public static int MAX_FIELDS   = 7;

    public static int[] STORE_WEIGHTS = {15,21,50,14};
    public static String STORE_LINE_ITEM_RENDERER = "StoreLineItemRenderer";

    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Default Constructor.
     */
    //---------------------------------------------------------------------
    public StoreLineItemRenderer()
    {
        super();
        setName("StoreLineItemRenderer");
        // set default in case lookup fails
        firstLineWeights = STORE_WEIGHTS;
        // look up the label weights
        setFirstLineWeights(STORE_LINE_ITEM_RENDERER);
        secondLineWeights = firstLineWeights;

        fieldCount = MAX_FIELDS;
        lineBreak = STORE_PHONE;
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
            StoreLineItemModel model = 
                (StoreLineItemModel)data;

            labels[STORE_NUMBER].setText(model.getStoreID());
            labels[STORE_NAME].setText(model.getStoreName());
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
        StoreLineItemModel model = new StoreLineItemModel();

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

        StoreLineItemRenderer bean = new StoreLineItemRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}
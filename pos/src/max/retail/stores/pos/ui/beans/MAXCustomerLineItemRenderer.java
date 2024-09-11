/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerLineItemRenderer.java /main/24 2013/05/28 15:06:02 abondala Exp $
 * ==========================================================================
 * Rev 1.0	Aug 30,2016	Ashish Yadav	Changes done for code merging
 *
 * ===========================================================================
 */
package max.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JLabel;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerLineItemRenderer;


/**
 * This is the renderer for the Customer list. It formats customer objects for
 * display as a list entry.
 *
 * @version $Revision: /main/24 $
 */
public class MAXCustomerLineItemRenderer extends CustomerLineItemRenderer
{
    private static final long serialVersionUID = 2546641048938377714L;

    /** revision number supplied by Team Connection */
    public static String revisionNumber = "$Revision: /main/24 $";

    public static int NAME       = 0;
    public static int ADDRESS    = 1;
    public static int HOMEPHONE  = 2;
    public static int ID         = 3;
    public static int CITYSTATE  = 4;
    public static int MAX_FIELDS = 5;

    public static int[] CUSTOMER_WEIGHTS = {30,40,30};

   /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Default Constructor.
     */
    //---------------------------------------------------------------------
    public MAXCustomerLineItemRenderer()
    {
    	super();
        setName("CustomerLineItemRenderer");
        // set default in case lookup fails
        firstLineWeights =  CUSTOMER_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("labelWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = HOMEPHONE;

        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
     protected void initOptions()
     {
        labels[NAME].setHorizontalAlignment(JLabel.LEFT);
        labels[ADDRESS].setHorizontalAlignment(JLabel.LEFT);
        labels[HOMEPHONE].setHorizontalAlignment(JLabel.CENTER);

        labels[ID].setHorizontalAlignment(JLabel.LEFT);
        labels[CITYSTATE].setHorizontalAlignment(JLabel.LEFT);

        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        constraints.gridy = 1;
        constraints.weightx = 0.0;
        add(labels[ID], constraints);
        add(labels[CITYSTATE], constraints);
     }

    //---------------------------------------------------------------------
    /**
     * This sets the fields of this ListCellRenderer.
     * @param customer oracle.retail.stores.domain.customer.Customer
     */
    //---------------------------------------------------------------------
    public void setData(Object data)
    {
        if(data != null)
        {
            CustomerIfc customer    = (CustomerIfc)data;

            String customerName     = UIUtilities.retrieveText(BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                                               "Common",
                                                               "CustomerName");
            Object[] values = new Object[2];
            if(!customer.isBusinessCustomer())
            {
                  values[0] = customer.getFirstName();
                  values[1] = customer.getLastName();
            }
            else
            {
                  values[0] = customer.getLastName();
                  values[1] = " ";
            }

            // fill in the message format with the customer's name
            customerName = LocaleUtilities.formatComplexMessage(customerName, values);

            labels[NAME].setText(customerName);
            labels[ID].setText(customer.getCustomerID());

            StringBuffer cityState=new StringBuffer();
            List<AddressIfc> addressList = customer.getAddressList();
            String countryCode = null;


            if(addressList != null && !addressList.isEmpty())
            {
                AddressIfc addr = (AddressIfc) addressList.get(0);

                if(!Util.isEmpty(addr.getCity()))
                {
                    cityState.append(addr.getCity()).append(",");
                }

                if(!Util.isEmpty(addr.getState()))
                {
                    cityState.append(addr.getState()).append(" ");
                }
                String postalCode = addr.getPostalCode();
                if (postalCode == null)
                {
                    postalCode = "";
                }
                cityState.append(postalCode);

                labels[CITYSTATE].setText(cityState.toString());

                Vector vectAddrLn = addr.getLines();
                if(vectAddrLn != null && !vectAddrLn.isEmpty())
                {
                    if (!Util.isEmpty(((String)vectAddrLn.elementAt(0))))
                    {
                        labels[ADDRESS].setText(vectAddrLn.elementAt(0).toString());    
                    }
                    else
                    {
                        labels[ADDRESS].setText("");   
                    }
                }
                countryCode = addr.getCountry();
            }
            else
            {
                labels[CITYSTATE].setText("");
                labels[ADDRESS].setText("");
            }
	// Changes starts for rev 1.0
            //PhoneIfc custPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_HOME);
			PhoneIfc custPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_MOBILE);
	// Chnages ends for Rev 1.0
            if (custPhone == null) //business customer
            {
                custPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_WORK);
            }

            if(custPhone!=null)
            {
                if (custPhone.getPhoneNumber() != null && !custPhone.getPhoneNumber().equals("") && countryCode !=null)
                {
                    UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
                    String formattedPhoneNumber = util.getFormattedNumber(custPhone.getPhoneNumber(), countryCode);
                    labels[HOMEPHONE].setText(formattedPhoneNumber);
                }
				//Changes starts for Rev 1.0
               else if (custPhone.getPhoneNumber() != null && !custPhone.getPhoneNumber().equals(""))
               {
                    labels[HOMEPHONE].setText(custPhone.getPhoneNumber());
               }
			   //Changes ends for Rev 1.0
               else
               {
                  labels[HOMEPHONE].setText("");
               }
            }
            else
            {
                labels[HOMEPHONE].setText("");
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
    public Object createPrototype()
    {
        CustomerIfc cust = DomainGateway.getFactory().getCustomerInstance();

        cust.setFirstName("XXXXXXXXXXXXXXXX");
        cust.setMiddleName("XXXXXXXXXXXXXXXX");
        cust.setLastName("XXXXXXXXXXXXXXXXXXXX");
        cust.setCustomerID("12345678901234");

        AddressIfc addr = DomainGateway.getFactory().getAddressInstance();

        addr.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
        addr.setCity("XXXXXXXXXXXXXXXXXXXX");
        addr.setState("XX");

        Vector<String> lineVect = new Vector<String>();
        String addrLine = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        lineVect.addElement(addrLine);
        addr.setLines(lineVect);
        List<AddressIfc> addrList = new ArrayList<AddressIfc>();
        addrList.add(addr);
        cust.setAddressList(addrList);

        PhoneIfc homePhone = DomainGateway.getFactory().getPhoneInstance();

        homePhone.setPhoneNumber("555-1234");

        List<PhoneIfc> phoneList = new ArrayList<PhoneIfc>();
        phoneList.add(homePhone);
        cust.setPhoneList(phoneList);

        return(cust);
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

        CustomerLineItemRenderer bean = new CustomerLineItemRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }

}

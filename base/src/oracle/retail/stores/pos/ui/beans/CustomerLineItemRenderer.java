/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerLineItemRenderer.java /main/24 2013/05/28 15:06:02 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  05/28/13 - if the address is null, set the text to empty string
 *                         for the UI display
 *    abondala  12/17/12 - fixed the NPE issues for the non-required fields
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    hyin      05/23/12 - retrieve workphone for business customer
 *    hyin      05/21/12 - postal code null check
 *    cgreene   04/03/12 - removed deprecated methods
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mkochumm  12/17/08 - format phone number
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:09:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jun 25 2003 16:00:12   baa
 * remove default state setting for customer info lookup
 *
 *    Rev 1.3   Jun 24 2003 14:05:08   baa
 * show customer name instead of tags on customer select screen
 *
 *    Rev 1.2   Sep 06 2002 17:25:22   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:52   msg
 * Initial revision.
 *
 *    Rev 1.8   21 Feb 2002 14:00:30   jbp
 * added null check when setting data to renderer.
 * Resolution for POS SCR-1372: Selecting Customer to Delete on Customer Select when search done by Emp ID screen hangs application
 *
 *    Rev 1.7   28 Jan 2002 16:00:32   baa
 * fixing dual list
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.6   28 Jan 2002 10:39:34   baa
 * working on ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

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


/**
 * This is the renderer for the Customer list. It formats customer objects for
 * display as a list entry.
 *
 * @version $Revision: /main/24 $
 */
public class CustomerLineItemRenderer extends AbstractListRenderer
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
    public CustomerLineItemRenderer()
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

            PhoneIfc custPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_HOME);
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

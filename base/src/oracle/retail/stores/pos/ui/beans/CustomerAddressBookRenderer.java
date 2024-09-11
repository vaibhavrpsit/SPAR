/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerAddressBookRenderer.java /main/2 2013/05/13 10:44:53 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    05/10/13 - Fix to set and display correct address type
* yiqzhao     06/11/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.ui.beans;

import java.util.Properties;

import javax.swing.JLabel;


import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *  Renderer for SaleReturnLineItems.
 */
public class CustomerAddressBookRenderer extends  AbstractListRenderer
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
 
    /** the ADDRESS_TYPE column */
    public static int ADDRESS_TYPE = 0;
    /** the NAME column */
    public static int NAME = 1;
    /** the ADDRESS line column */
    public static int ADDRESS = 2;
    /** the PHONE column */
    public static int PHONE = 3;
    
    public static int EMPTY1       = 4;
    public static int ENPTY2       = 5;
    public static int CITYSTATE    = 6;
    /** the maximum number of fields */
    public static int MAX_FIELDS = 7;

    public static int[] CUSTOMER_ADDRESS_BOOK_WEIGHTS = {15,20,50,15};
    public static String CUSTOMER_ADDRESS_BOOK_RENDERER = "CustomerAddressBookRenderer";

    /** Properties **/
    protected Properties props = null;

    /**
     *  Default constructor.
     */
    public CustomerAddressBookRenderer()
    {
        super();
        setName("CustomerAddressBookRenderer");

        // set default in case lookup fails
        firstLineWeights = CUSTOMER_ADDRESS_BOOK_WEIGHTS;
        // look up the label weights
        setFirstLineWeights(CUSTOMER_ADDRESS_BOOK_RENDERER);
        secondLineWeights = firstLineWeights;

        fieldCount = MAX_FIELDS;
        lineBreak = PHONE;
        secondLineBreak = MAX_FIELDS + 1;

        initialize();
    }

    /**
     * Over ride to add new Label.
     * This is done to show the Item Message on the screen
     */
    @Override
    protected void initLabels()
    {
        super.initLabels();
        labels[ADDRESS_TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[NAME].setHorizontalAlignment(JLabel.LEFT);
        labels[ADDRESS].setHorizontalAlignment(JLabel.LEFT);
        labels[PHONE].setHorizontalAlignment(JLabel.RIGHT);
        labels[EMPTY1].setHorizontalAlignment(JLabel.LEFT);
        labels[ENPTY2].setHorizontalAlignment(JLabel.CENTER);
        labels[CITYSTATE].setHorizontalAlignment(JLabel.LEFT);
    }



    /**
     *  sets the visual components of the cell
     *  @param value Object
     */
    public void setData(Object data)
    {
    	if(data != null)
    	{
    		CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)data;

    		labels[ADDRESS_TYPE].setText(model.getAddressType());

    		StringBuffer nameBuffer = new StringBuffer(model.getFirstName());
    		labels[NAME].setText(nameBuffer.append(" ").append(model.getLastName()).toString());

    		StringBuffer addrBuffer = new StringBuffer(model.getAddressLine1());
    		if( model.getAddressLine2() != null)
    		{
    			addrBuffer.append(" ").append(model.getAddressLine2());
    		}
    		labels[ADDRESS].setText(addrBuffer.toString());

    		labels[PHONE].setText(model.getPhoneNumber());

    		labels[EMPTY1].setText("  ");
    		labels[ENPTY2].setText("  ");

    		StringBuffer cityState = new StringBuffer();
    		if(model.getAddressLine1() != null)
    		{
    			if(!model.getCity().equals(""))
    			{
    				cityState.append(model.getCity()).append(",");
    			}
    			StringBuffer state = new StringBuffer();
    			if ( model.getStateNames().length>0 )
    			{
    				state.append( model.getStateNames()[0] );
    				cityState.append(state.append(" "));
    			}

    			cityState.append(model.getCountryNames()[0]).append(" ");
    			cityState.append(model.getPostalCode());
    			labels[CITYSTATE].setText(cityState.toString());
    		}
    		else
    		{
    			labels[CITYSTATE].setText("");
    			labels[ADDRESS].setText("");
    		}
    	}
    }
    //--------------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number. <P>
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

	@Override
	protected void setPropertyFields() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object createPrototype() {
		// TODO Auto-generated method stub
		CaptureCustomerInfoBeanModel model = new CaptureCustomerInfoBeanModel();
		model.setAddressLine1("7000 Loop 1");
		model.setAddressLine2("Suite 250");
		PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
		phone.setPhoneNumber("1234567890");
		PhoneIfc phones[] = new PhoneIfc[1];
		phones[0] = phone;
		model.setPhoneList( phones);
		model.setCity("Austin");
		model.setStateNames(new String[] {"TX"});
		model.setCountryNames(new String[] {"US"});
		model.setPostalCode("78759");

        return(model);		
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

        CustomerAddressBookRenderer bean = new CustomerAddressBookRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}

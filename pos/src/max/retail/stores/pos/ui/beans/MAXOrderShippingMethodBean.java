/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

// Java imports
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Shipping Method Information
   $Revision: 4$
*/
//----------------------------------------------------------------------------
public class MAXOrderShippingMethodBean extends ValidatingBean
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 182129889873154981L;

	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 4$";

    // The bean model
    protected MAXOrderShippingMethodBeanModel beanModel = null;

    // First, last name field
    protected JLabel   nameField     = null;   
 
    /**
       business name label
    **/
    protected JLabel businessName = null;  
    
    /**
       business name string used to set name for this field
    **/
    public static final String BUSINESS_NAME = "BusinessName";

    // Address line 1 field
    protected JLabel  addressLine1Field  = null;

    // Address line 2 field
    protected JLabel   addressLine2Field  = null;

    // Address line 3 field
    protected JLabel   addressLine3Field  = null;

    // city, state, zip, country field
    protected JLabel   cityStateZipField  = null;

    // labels for fields
    protected JLabel shipToLabel  = null;

    // Shipping Method Label
    protected JLabel shipViaLabel = null;

    // Shipping Charge label
    protected JLabel shipChargeLabel  = null;

    // empty label
    protected JLabel emptyLabel   = null;

    // ship to label tag
    protected static String SHIP_TO_LABEL = "ShipToLabel";

    // ship via label tag
    protected static String SHIP_VIA_LABEL = "ShipViaLabel";

    // ship charges label tag
    protected static String SHIP_CHARGE_LABEL = "ShipChargeLabel";

    // instructions label tag
    protected static String INSTRUCTIONS_LABEL = "InstructionsLabel";

    //  Container of method lists
    protected JList shipViaList = null;

    //  Scroll long list of departments
    protected JScrollPane shipViaScrollPane = null;

    // shipping charge
    protected CurrencyTextField     shipChargeField = null;


    //----------------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
     */
    //----------------------------------------------------------------------------
    public MAXOrderShippingMethodBean()
    {
        super();
        initialize();
    }


    //----------------------------------------------------------------------------
    /**
     * Initialize the class and its screen members.
     */
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        setName("ShippingMethodBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeLabels();
        initializeFields();
        layoutComponents();
    }

    //----------------------------------------------------------------------------
    /**
     * Initialize the labels on this screen
     */
    //----------------------------------------------------------------------------
    protected void initializeLabels()
    {
         shipToLabel = uiFactory.createLabel("shipToLabel", null, UI_LABEL);
         emptyLabel = uiFactory.createLabel("",null,UI_LABEL);

         nameField = uiFactory.createDisplayField("nameField");
         businessName = uiFactory.createDisplayField(BUSINESS_NAME);
         addressLine1Field = uiFactory.createDisplayField("addressLine1Field");
         addressLine2Field = uiFactory.createDisplayField("addressLine2Field");
         addressLine3Field = uiFactory.createDisplayField("addressLine3Field");
         cityStateZipField = uiFactory.createDisplayField("cityStateZipField");

         shipViaLabel = uiFactory.createLabel("shipToLabel", null, UI_LABEL);
         shipChargeLabel = uiFactory.createLabel("shipChargeLabel", null, UI_LABEL);
    }

    //----------------------------------------------------------------------------
    /**
     * Initialize the fields on this screens.
     */
    //----------------------------------------------------------------------------
    protected void initializeFields()
    {
        shipViaScrollPane = uiFactory.createSelectionList("shipViaList", "large");
        shipViaList = (JList) shipViaScrollPane.getViewport().getView();

        shipChargeField = uiFactory.createCurrencyField("shipChargeField",
                                                        "false",
                                                        "false",
                                                        "false");
    }

    //----------------------------------------------------------------------------
    /**
     * Layout the components on this screen
     */
    //----------------------------------------------------------------------------
    protected void layoutComponents()
    {
        JLabel[] labels =
        {
            shipToLabel,  emptyLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel,
            shipViaLabel, shipChargeLabel
        };

        JComponent[] components =
        {
            nameField, businessName,
            addressLine1Field, addressLine2Field, addressLine3Field,
            cityStateZipField, shipViaScrollPane, shipChargeField
        };

        setLayout(new GridBagLayout());
        UIUtilities.layoutDataPanel(this, labels,components);
    }




    //--------------------------------------------------------------------------
    /**
     *     Overrides the inherited setVisible().
     *    @param value boolean
     */
    public void setVisible(boolean value)
    {
        super.setVisible(value);
        
        if (value && !errorFound())
        {
            setCurrentFocus(shipViaList);
        }
    }


    //-----------------------------------------------------------------------
    /**
    *   Gets the POSBaseBeanModel for validation of the current settings of the bean.
    *   @return the POSBaseBeanModel for the current values.
    */
    //------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }


    //------------------------------------------------------------------------
    /**
     *  Calls setchangeStateValue to determine if any of the fields were changed.
        Updates the model with the newest values from the bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setSelectedShipMethod(shipViaList.getSelectedIndex());
        if (Util.isEmpty(shipChargeField.getText()))
        {
           beanModel.setShippingCharge(null);
        }
        else
        {
            beanModel.setShippingCharge(shipChargeField.getCurrencyValue());
        }

    }

    //------------------------------------------------------------------------
    /**
     *  Sets the model for the current settings of this bean.
     *  @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ShippingMethodBean model to null");
        }
        else
        {
           if (model instanceof MAXOrderShippingMethodBeanModel)
            {
                beanModel = (MAXOrderShippingMethodBeanModel) model;
                updateBean();
            }
        }
    }

    //------------------------------------------------------------------------
    /**
     *  Updates the bean and changes fields based upon whether a layaway is in progress
    */
    //------------------------------------------------------------------------
    public void updateBean()
    {

        nameField.setText(beanModel.getFirstName() + " " + beanModel.getLastName());
        businessName.setText(beanModel.getOrgName());
        addressLine1Field.setText(beanModel.getAddressLine1());
        addressLine2Field.setText(beanModel.getAddressLine2());

        String cityState = beanModel.getCity();


        if (beanModel.getCountryInfo() != null)
        {
            if (beanModel.getStateInfo() != null)
            {
                cityState = cityState + beanModel.getCountryInfo().getAddressDelimiter() +" "+ beanModel.getState();
            }

            cityState = cityState +" "+ beanModel.getPostalCode();
           /* if (beanModel.getExtPostalCode() != null && beanModel.getExtPostalCode().length() > 0)
            {
              cityState = cityState + beanModel.getCountryInfo().getPostalCodeDelimiter() + beanModel.getExtPostalCode();
            }
*/
            cityState = cityState +" "+ beanModel.getCountryInfo().getPostalCodeDelimiter() +" "+ beanModel.getCountryInfo().getCountryName();
        }


        cityStateZipField.setText(cityState);

       ShippingMethodIfc methods[] = beanModel.getShipMethodsList();

       if (methods != null)
       {

          String methodDesc[] = new String[methods.length];
          for (int i=0; i < methods.length; i++)
          {
              methodDesc[i] = methods[i].getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
          }
          shipViaList.setListData(methodDesc);

       }
       ShippingMethodIfc selection = beanModel.getSelectedShipMethod();
       shipViaList.setSelectedValue(selection.getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)),true);

       CurrencyIfc charge = beanModel.getShippingCharge();
       shipChargeField.setValue(charge);
       setCurrentFocus(shipViaList);

    }

    //----------------------------------------------------------------------------
    /**
     * The framework calls this method just before display
     */
    //----------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        shipViaList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                //we are interested in the final state of the selection
                if(!e.getValueIsAdjusting())
                {
                    updateShippingCharge(e);
                }
            }
        });
        shipViaList.addFocusListener(this);
        updateModel();
    }

    //----------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        shipViaList.removeFocusListener(this);
    }

    //----------------------------------------------------------------------------
    /**
     * updateShippingCharge
     */
    //----------------------------------------------------------------------------
    public void updateShippingCharge(ListSelectionEvent e)
    {
        int indx = shipViaList.getSelectedIndex();
        if ( indx == -1 )
        {
             indx = 0;
        }

        beanModel.setSelectedShipMethod(indx);

        CurrencyIfc charge = beanModel.getShippingCharge();
        shipChargeField.setValue(charge);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if (shipToLabel != null)
        {
            shipToLabel.setText(retrieveText(SHIP_TO_LABEL,
                                             shipToLabel.getText()));
        }
        if (shipViaLabel != null)
        {
            shipViaLabel.setText(retrieveText(SHIP_VIA_LABEL,
                                              shipViaLabel.getText()));
        }
        if (shipChargeLabel != null)
        {
            shipChargeLabel.setText(retrieveText(SHIP_CHARGE_LABEL,
                                                 shipChargeLabel.getText()));
        }
        shipChargeField.setLabel(shipChargeLabel);
    }                                   // end updatePropertyFields()

    //--------------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args java.lang.String[]
     */
    //--------------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        javax.swing.JFrame frame = new
          javax.swing.JFrame("CheckReferralBean");

        MAXOrderShippingMethodBean bean =  new  MAXOrderShippingMethodBean();

        MAXOrderShippingMethodBeanModel beanModel = new   MAXOrderShippingMethodBeanModel();

        beanModel.setFirstName("John");
        beanModel.setLastName("Doe");
        beanModel.setAddressLine1("1 main street");
        beanModel.setAddressLine2("Suite 1b");
        beanModel.setCity("Austin");
        bean.setModel(beanModel);
        bean.activate();

        frame.setSize(530, 290);
        frame.getContentPane().add(bean);
        frame.show();
    }

}

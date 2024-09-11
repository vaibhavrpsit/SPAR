/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ExternalOrderSearchBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    09/02/10 - set the properties bundle
 *    abondala  06/01/10 - Siebel integration changes
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/20/10 - updated search flow
 *    abondala  05/12/10 - Search external orders flow
 *    abondala  05/12/10 - New class to dsiplay advanced search criteria for
 *                         retrieving external orders
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BooleanComboModel;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.ValidatingFormattedTextField;
import oracle.retail.stores.pos.ui.beans.YesNoComboBox;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Looking up the External Orders.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class ExternalOrderSearchBean extends ValidatingBean
{
    private static final long serialVersionUID = -4390278031886595250L;

    // Logger
    protected static final Logger logger = Logger.getLogger(ExternalOrderSearchBean.class);

    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** First Name field and label ID */
    protected static final int FIRST_NAME = 0;

    /** Last Name field and label ID */
    protected static final int LAST_NAME = 1;

    /** Account field and label ID */
    protected static final int ACCOUNT = 2;

    /** Country field and label ID */
    protected static final int COUNTRY = 3;

    /** Phone Number field and label ID */
    protected static final int PHONE_NUMBER = 4;

    /** Order Number field and label ID */
    protected static final int ORDER_NUMBER = 5;

    /** Include All Stores field and label ID */
    protected static final int INCLUDE_ALL_STORES = 6;


    /** Label Text array */
    protected static String labelText[] = {"First Name:", "Last Name:", "Account:", "Country:", "Telephone No.:", "Order Number:", "Include All Stores:"};

    /** Label Text array */
    protected static String labelTags[] = {"FirstNameLabel", "LastNameLabel", "AccountLabel", "CountryLabel", "TelephoneNumberLabel", "OrderNumberLabel", "IncludeAllStoresLabel" };

    /** Label components */
    protected JLabel[] fieldLabels = new JLabel[labelText.length];

    /** First Name component */
    protected ConstrainedTextField firstNameField  = null;

    /** Last Name component */
    protected ConstrainedTextField lastNameField   = null;

    /** account number field */
    protected ConstrainedTextField accountField = null;

    /** country field */
    protected ValidatingComboBox countryField = null;

    /** telephone number field */
    protected ValidatingFormattedTextField telephoneField = null;

    /** order number field */
    protected ConstrainedTextField orderNumberField;

    /** include all stores checkbox  field */
    protected YesNoComboBox includeAllStoresField = null;
    /** Boolean Model */
    protected BooleanComboModel includeAllStoresModel = null;


    /** Indicates if the bean should be updated */
    protected boolean dirtyModel                   = false;

    //----------------------------------------------------------------------------
    /***
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public ExternalOrderSearchBean()
    {
        super();
        initialize();
    }

    //----------------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        setName("ExternalOrderSearchBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();

    }

    /**
     * Initialize this bean's components.
     */
    protected void initComponents()
    {
        initLabels();
        initFields();
    }


    /**
     * Initializes the setting for the field labels.
     */
    protected void initLabels()
    {
        for(int i=0; i<labelText.length; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelTags[i], labelTags[i], null, UI_LABEL);
        }
    }

    /**
     * Initialize the setting for the data fields.
     */
    protected void initFields()
    {
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "50", "20");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "50", "20");
        accountField  = uiFactory.createConstrainedField("accountField", "1", "100", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
        orderNumberField  = uiFactory.createConstrainedField("orderNumberField", "1", "30", "20");
        includeAllStoresField = uiFactory.createYesNoComboBox("includeAllStoresField", 10);

    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        // create component array with null placeholder for divider
        JComponent[] fields = {firstNameField, lastNameField, accountField, countryField, telephoneField, orderNumberField, includeAllStoresField};
        UIUtilities.layoutDataPanel(this, fieldLabels, fields);
    }

    //------------------------------------------------------------------------
    /**
     * Overrides the inherited setVisible() to set the focus on the reply area.
       @param value boolean
     */
    //------------------------------------------------------------------------
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(firstNameField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Activates this bean.
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        countryField.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == countryField)
        {
            int selectedCountryIndex = countryField.getSelectedIndex() ;
            updatePhoneFormat(selectedCountryIndex);
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    private void updatePhoneFormat(int selectedCountryIndex)
    {
        int countryIndex = ((ExternalOrderSearchBeanModel)beanModel).getCountryIndex();
        String countryCode = ((ExternalOrderSearchBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (((ExternalOrderSearchBeanModel)beanModel).getTelephoneNumber() != null)
        {
            if ((selectedCountryIndex != countryIndex)
                    || (((ExternalOrderSearchBeanModel)beanModel).getTelephoneNumber().equals("")))
            {
                String phoneFormat = null;
                String phoneValidationRegexp = null;
                phoneFormat = util.getPhoneFormat(countryCode);
                telephoneField.setFormat(phoneFormat);
                phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
                telephoneField.setValidationRegexp(phoneValidationRegexp);
                ((ExternalOrderSearchBeanModel)beanModel).setTelephoneNumber("");
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Deactivates this bean.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        countryField.removeActionListener(this);
    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        if (beanModel instanceof ExternalOrderSearchBeanModel)
        {
               ExternalOrderSearchBeanModel model = (ExternalOrderSearchBeanModel)beanModel;
               model.setFirstName(firstNameField.getText());
               model.setLastName(lastNameField.getText());
               model.setAccount(accountField.getText());

               model.setCountryIndex(countryField.getSelectedIndex());
               model.setTelephoneNumber(telephoneField.getFieldValue());

               model.setOrderNumber(orderNumberField.getText());
               model.setIncludeAllStores(getIncludeAllStoresModel().valueOf((String)includeAllStoresField.getSelectedItem()));
        }
    }

    /**
     * Returns the dataModel being used by the IncludeAllStoresField, which is a
     * YesNoComboBox.
     *
     * @return BooleanComboModel
     */
    protected BooleanComboModel getIncludeAllStoresModel()
    {
        // explicit cast from ComboBoxModel to BooleanComboModel
        if (includeAllStoresModel == null)
        {
            includeAllStoresModel = (BooleanComboModel) includeAllStoresField.getModel();
        }

        return includeAllStoresModel;
    }

//  ---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        super.setProps(props);
        getIncludeAllStoresModel().setProps(props);
        updatePropertyFields();
    }


    //------------------------------------------------------------------------
    /**
     * Sets the model for the current settings of this bean.
     * @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set ExternalOrderSearchBeanModel" +
                                           " to null");
        }
        if (model instanceof ExternalOrderSearchBeanModel)
        {
            beanModel = (ExternalOrderSearchBeanModel)model;
            dirtyModel = true;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if (beanModel instanceof ExternalOrderSearchBeanModel)
        {
            if(dirtyModel)
            {
                ExternalOrderSearchBeanModel model = (ExternalOrderSearchBeanModel)beanModel;
                firstNameField.setText(model.getFirstName());
                lastNameField.setText(model.getLastName());
                accountField.setText(model.getAccount());

                // Retrieve countries and update combo box
                setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
                int countryIndex = model.getCountryIndex();
                String countryCode = model.getCountry(countryIndex).getCountryCode();
                UtilityManagerIfc util = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
                String phoneFormat = util.getPhoneFormat(countryCode);
                telephoneField.setFormat(phoneFormat);
                String phoneValidationRegexp = null;
                phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
                telephoneField.setValidationRegexp(phoneValidationRegexp);
                telephoneField.setValue(model.getTelephoneNumber());
                telephoneField.setEmptyAllowed(true);

                orderNumberField.setText(model.getOrderNumber());

                String includeAllStoresSetting = getIncludeAllStoresModel().valueOf(model.getIncludeAllStores());
                includeAllStoresField.setModel(new ValidatingComboBoxModel(getIncludeAllStoresModel().getValues()));
                includeAllStoresField.setSelectedItem(includeAllStoresSetting);

                dirtyModel = false;
            }
        }

    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        for (int i = 0; i < labelTags.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                fieldLabels[i]));
        }
        lastNameField.setLabel(fieldLabels[LAST_NAME]);
        firstNameField.setLabel(fieldLabels[FIRST_NAME]);
        accountField.setLabel(fieldLabels[ACCOUNT]);
        countryField.setLabel(fieldLabels[COUNTRY]);
        telephoneField.setLabel(fieldLabels[PHONE_NUMBER]);
        orderNumberField.setLabel(fieldLabels[ORDER_NUMBER]);
        includeAllStoresField.setLabel(fieldLabels[INCLUDE_ALL_STORES]);

    }

    //------------------------------------------------------------------------
    /**
     * Gets the model for error screen processing.
     * @return POSBaseBeanModel
    */
    //------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        updateModel();
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ExternalOrderSearchBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
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

    //--------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    //--------------------------------------------------------------------------
    public static void main(String[] args)
    {

        UIUtilities.setUpTest();

        ExternalOrderSearchBeanModel beanModel = new ExternalOrderSearchBeanModel();
        beanModel.setFirstName("JAMES");
        beanModel.setLastName("BOND");
        beanModel.setAccount("7777");
        beanModel.setTelephoneNumber("7777777777");
        beanModel.setOrderNumber("SIEBEL_ORD");
        beanModel.setIncludeAllStores(true);

        ExternalOrderSearchBean bean = new ExternalOrderSearchBean();

        bean.configure();
        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}

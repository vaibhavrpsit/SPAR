/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderCustomerInfoBean.java /main/5 2013/05/29 18:20:51 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abondala    05/28/13 - add business name field to the order search screen
* abhinavs    04/16/13 - Fix to prevent Clear action on countryComboBox
* yiqzhao     12/17/12 - Make first name, last name and phone number fields
*                        required.
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;


import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;
//import oracle.retail.stores.foundation.manager.ui.jfc.ConstrainedTextField;

/**
 * Work panel bean for credit card info entry
 */
public class OrderCustomerInfoBean extends ValidatingBean
{
    private static final long serialVersionUID = 1L;

    // Revision number
    public static final String revisionNumber = "$Revision: /main/5 $";

    /**
     * fields and labels for customer ID data
     */
    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel businessNameLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel telephoneLabel = null;
    
    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField businessNameField = null;
    protected ValidatingComboBox countryField = null;
    protected ValidatingFormattedTextField telephoneField = null;
    
    // The bean model
    protected OrderCustomerInfoBeanModel beanModel = new OrderCustomerInfoBeanModel();

    /**
     * Constructor
     */
    public OrderCustomerInfoBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("OrderCustomerInfoBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
        firstNameLabel     = uiFactory.createLabel("FirstNameLabel", "FirstNameLabel", null, UI_LABEL);
        lastNameLabel      = uiFactory.createLabel("LastNameLabel", "LastNameLabel", null, UI_LABEL);
        businessNameLabel  = uiFactory.createLabel("businessNameLabel", "businessNameLabel", null, UI_LABEL);
        
        countryLabel        = uiFactory.createLabel("CountryLabel", "CountryLabel", null, UI_LABEL);
        telephoneLabel      = uiFactory.createLabel("TelephoneLabel", "PhoneNumberLabel", null, UI_LABEL);

        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        //createConstrainedField(name, minLength, maxLength, columns, true);
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        businessNameField = uiFactory.createConstrainedField("businessNameField", "2", "30", "30");
        countryField =  uiFactory.createValidatingComboBox("countryField", "false", "15");
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneLabel", "", "30", "20");
    }

    /**
     * Lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{firstNameLabel, lastNameLabel, businessNameLabel, countryLabel, telephoneLabel},
                                    new JComponent[]{firstNameField, lastNameField, businessNameField, countryField, telephoneField});
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        lastNameField.addFocusListener(this);
        businessNameField.addFocusListener(this);
        countryField.addActionListener(this);
        telephoneField.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        lastNameField.removeFocusListener(this);
        businessNameField.removeFocusListener(this);
        countryField.removeActionListener(this);
        telephoneField.removeActionListener(this);
    }

    /**
     * Updates the model property
     */
    public void updateModel()
    {
        beanModel.setFirstName(firstNameField.getText());
        beanModel.setLastName(lastNameField.getText());
        beanModel.setBusinessName(businessNameField.getText());
        beanModel.setCountryIndex(countryField.getSelectedIndex());
        beanModel.setTelephone(telephoneField.getText());
        

    }

    /**
     * Sets the model property
     * 
     * @param model UIModelIfc
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ChargeBean model to null");
        }
        
        if (model instanceof OrderCustomerInfoBeanModel)
        {
            beanModel = (OrderCustomerInfoBeanModel) model;
            updateBean();
        }
    }

    /**
     * Do actual updating of bean from the model
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof OrderCustomerInfoBeanModel)
        {
        	OrderCustomerInfoBeanModel model = (OrderCustomerInfoBeanModel)beanModel;
            firstNameField.setText(model.getFirstName());
            lastNameField.setText(model.getLastName());
            businessNameField.setText(model.getBusinessName());
            
            // Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            
            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManagerIfc util = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            telephoneField.setText(model.getTelephoneNumber());
            
            String phoneValidationRegexp = null;
            phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneField.setValidationRegexp(phoneValidationRegexp);
            
            telephoneField.setValue(model.getTelephoneNumber());
            telephoneField.setEditable(true);
            telephoneField.setEmptyAllowed(false);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == countryField)
        {
            int selectedCountryIndex = countryField.getSelectedIndex();
            //int countryIndex = ((OrderCustomerInfoBeanModel)beanModel).getCountryIndex();
            updatePhoneFormat(selectedCountryIndex);
        }
        if (event.getSource() != telephoneField && event.getSource() != countryField)
        {
        	super.actionPerformed(event);
        }
    }
    /**
     * Updates the phone format for the selected country index.
     * @param selectedCountryIndex
     */
    private void updatePhoneFormat(int selectedCountryIndex)
    {
        int countryIndex = ((OrderCustomerInfoBeanModel)beanModel).getCountryIndex();
        String countryCode = ((OrderCustomerInfoBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (((OrderCustomerInfoBeanModel)beanModel).getTelephoneNumber() != null)
        {
            if ((selectedCountryIndex != countryIndex)
                    || (((OrderCustomerInfoBeanModel)beanModel).getTelephoneNumber().equals("")))
            {
                String phoneFormat = null;
                String phoneValidationRegexp = null;
                phoneFormat = util.getPhoneFormat(countryCode);
                telephoneField.setFormat(phoneFormat);
                phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
                telephoneField.setValidationRegexp(phoneValidationRegexp);
                ((OrderCustomerInfoBeanModel)beanModel).setTelephone("");
            }
        }
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     * 
     * @return the POSBaseBeanModel associated with this bean.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel",
        			firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel",
        		lastNameLabel));
        businessNameLabel.setText(retrieveText("BusinessNameLabel",
                businessNameLabel));
        countryLabel.setText(retrieveText("CountryLabel",
        		countryLabel));
        telephoneLabel.setText(retrieveText("TelephoneLabel",
        		telephoneLabel));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    public String toString()
    {
        String strResult = new String("Class: OrderCustomerInfoBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        OrderCustomerInfoBean bean = new OrderCustomerInfoBean();

        UIUtilities.doBeanTest(bean);
    }
}

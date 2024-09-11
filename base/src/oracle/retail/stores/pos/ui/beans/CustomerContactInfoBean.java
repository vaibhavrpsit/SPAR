
/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerContactInfoBean.java /main/5 2013/10/01 10:39:55 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/01/13 - Remove the phone number if it is invalid.
 *    yiqzhao   07/08/13 - Populate phone number field if the number can be
 *                         found.

 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//import oracle.retail.stores.foundation.manager.ui.jfc.ConstrainedTextField;

/**
 * This bean is used for displaying the Customer information screen based on the
 * data from the CustomerInfoBeanModel.
 *
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBeanModel
 */
public class CustomerContactInfoBean extends DataInputBean
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** first name */
    protected ConstrainedTextField firstNameField = null;
    
    /** last name */
    protected ConstrainedTextField lastNameField = null;
    
    /** telephoneField */
    protected ValidatingFormattedTextField telephoneField = null;
    /** countryField */
    protected ValidatingComboBox countryField = null;
    /** Current country code */
    protected String currentCountryCode = "";
    /** Country model */
    protected CountryModel countryModel = null;
    /** phone validation pattern */
    String phoneValidationRegexp = null;

    /**
     * Default Constructor.
     */
    public CustomerContactInfoBean()
    {
    }

    /**
     * activate any settings made by this bean to external entities
     */
    @Override
    public void activate()
    {
        super.activate();
        countryField.addActionListener(this);
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        countryField.removeActionListener(this);
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        super.updateBean();
        
        if (beanModel instanceof DataInputBeanModel)
        {
            // Get the country model and save it for later use, then replace the 
            // the country model with combo box model that contains only the list
            // of valid countries.
            DataInputBeanModel model = (DataInputBeanModel)beanModel;
            // When Cancel button, and then No button selected from the dialog, it may not return CountryModel  
            if ( model.getValue(POSUIManagerIfc.COUNTRY_FIELD) instanceof CountryModel)
            {
                countryModel = (CountryModel)model.getValue(POSUIManagerIfc.COUNTRY_FIELD);
                ValidatingComboBoxModel vcbm = new ValidatingComboBoxModel(countryModel.getCountryNames());
                vcbm.setSelectedItem(countryModel.getCountryName());            
                model.setValue(POSUIManagerIfc.COUNTRY_FIELD, vcbm);
            }
            
            // Call the super method.
            super.updateBean();
            
            // Perform additional local updates to the bean.
            telephoneField  = (ValidatingFormattedTextField)getComponentByName("telephoneNumberField");
            if ( telephoneField != null )
            {
                setFieldRequired(telephoneField, true);
            }
            if (countryField == null)
            {
                countryField    = (ValidatingComboBox)getComponentByName("countryField");
            }
            int index = countryField.getSelectedIndex();
            String countryCode = countryModel.getCountry(index).getCountryCode();
            currentCountryCode = countryCode;
            
            updatePhoneFormat(countryCode);
            if ( telephoneField != null )
            {
                telephoneField.setValue(model.getValue("telephoneNumberField"));
                //make sure the phone number is set based on phoneValidationRegexp
                String phoneNumber = telephoneField.getText();
                //Check the phone number is valid. If not, set the initial value.
                if ( !isValid(phoneNumber) )
                {
                    //clear the phone number if it is not valid
                    telephoneField.setValue("");
                }
            }

            
            firstNameField = (ConstrainedTextField)getComponentByName("firstNameField");
            if ( firstNameField != null )
            {
                setFieldRequired(firstNameField, true);
            }
            lastNameField = (ConstrainedTextField)getComponentByName("lastNameField");
            if ( lastNameField != null )
            {
                setFieldRequired(lastNameField, true);
            }
        }
    }

    /**
     * Get the component associated with the name parameter.
     * @param name
     * @return the associated component
     */
    protected JComponent getComponentByName(String name)
    {
        JComponent retValue = null;
        for(JComponent component: components)
        {
            if (component.getName().equals(name))
            {
                retValue = component;
                break;
            }
        }
        
        return retValue;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == countryField)
        {
            int index = countryField.getSelectedIndex();
            String countryCode = countryModel.getCountry(index).getCountryCode();
            updatePhoneFormat(countryCode);
            currentCountryCode = countryCode;
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Updates the phone format for the selected country index.
     * @param selectedCountryIndex
     */
    private void updatePhoneFormat(String countryCode)
    {
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String phoneFormat = null;

        phoneFormat = util.getPhoneFormat(countryCode);
        telephoneField.setFormat(phoneFormat);
        phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
        telephoneField.setValidationRegexp(phoneValidationRegexp);
    }
    
    /**
     * Check the phone number is valid or not
     * @param phoneNumber
     * @return
     */
    protected boolean isValid(String phoneNumber)
    {
        boolean isValid = true;
        if ( phoneNumber != null && phoneNumber.length() > 0 )
        {
            if (phoneValidationRegexp != null && (phoneValidationRegexp.length() > 0))
            {
                isValid = Pattern.matches(phoneValidationRegexp, phoneNumber);
            }
        }
        else
        {
            isValid = false;
        }
        return isValid;     
    }
}
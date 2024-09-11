/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreSearchInfoBean.java /main/3 2012/11/12 11:27:27 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/09/12 - Enable cancel and delete buttons on global
 *                         navigation panel.
 *    jswan     05/14/12 - Modified to fix issue with split of multi-quantity
 *                         line items.
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * This bean is used to enter the store search criteria information.
 *
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBeanModel
 */
public class StoreSearchInfoBean extends DataInputBean
{
    /** serialVersionUID */
    private static final long serialVersionUID = -7514845934931593927L;
    /** postalCodeField */
    protected ValidatingFormattedTextField postalCodeField = null;
    /** countryField */
    protected ValidatingComboBox countryField = null;
    /** stateField */
    protected ValidatingComboBox stateField = null;
    /** Combo box model */
    protected ValidatingComboBoxModel countryComboBoxModel = null; 
    /** Combo box model */
    protected ValidatingComboBoxModel stateModel = null; 
    /** Current country code */
    protected String currentCountryCode = "";
    /** the country model */
    protected CountryModel countryModel = null;

    /**
     * Default Constructor.
     */
    public StoreSearchInfoBean()
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
            if (postalCodeField == null)
            {
                postalCodeField = (ValidatingFormattedTextField)getComponentByName(POSUIManagerIfc.POSTAL_CODE_FIELD);
            }
            if (stateField == null)
            {
                stateField      = (ValidatingComboBox)getComponentByName(POSUIManagerIfc.STATE_FIELD);
            }
            if (countryField == null)
            {
                countryField    = (ValidatingComboBox)getComponentByName(POSUIManagerIfc.COUNTRY_FIELD);
            }
            
            // Retrieve countries and update combo box
            int index = countryField.getSelectedIndex();
            String countryCode = countryModel.getCountry(index).getCountryCode();
            updatePostalFormat(countryCode);
            currentCountryCode = countryCode;
        }
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        super.updateModel();

        // The state and country combo boxes hold the names, but tour class is
        // expecting the the codes.  Do the conversion here.
        if (beanModel instanceof DataInputBeanModel)
        {
            DataInputBeanModel model = (DataInputBeanModel)beanModel;

            countryModel.setStateIndex(stateField.getSelectedIndex());
            model.setValue(POSUIManagerIfc.STATE_CODE_FIELD, countryModel.getState());

            countryModel.setCountryIndex(countryField.getSelectedIndex());
            model.setValue(POSUIManagerIfc.COUNTRY_CODE_FIELD, countryModel.getCountry());
        }
    }

    /**
     * Get component by name
     * @param name
     * @return JComponent
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
            if (!currentCountryCode.equals(countryCode))
            {
                updatePostalFormat(countryCode);
                updateStates();
                currentCountryCode = countryCode;
            }
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Updates the postal code format given the selected country index and 
     * @param selectedCountryIndex
     * @param countryIndex
     */
    protected void updatePostalFormat(String countryCode)
    {
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String postalCodeFormat = null;
        String postalCodeValidationRegexp = null;
        postalCodeFormat = util.getPostalCodeFormat(countryCode);
        postalCodeField.setFormat(postalCodeFormat);
        postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
        postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
    }

    /**
     * Update states as country selection changes
     */
    protected void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if (countryIndx < 0)
        {
            countryIndx = 0;
        }
        
        String[] stateList = countryModel.getStateNames(countryIndx);
        stateList = LocaleUtilities.sort(stateList, getLocale());
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);
        stateField.setModel(stateModel);
        stateField.setSelectedIndex(0);
    }
}
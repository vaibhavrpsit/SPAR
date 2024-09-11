/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DeliveryAddressInfoBean.java /main/19 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    ohorne    03/12/09 - Min length of firstName and lastName fields is now 1
 *    abondala  03/05/09 - updated related to reason codes
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    vchengeg  12/12/08 - EJ defect fixes
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/17/08 - Pickup Delivery order
 *    aphulamb  11/14/08 - check in for Pickup Delivery Order functionality
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Delivery Address Info bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used for displaying the Customer information screen based on the
 * data from the DeliveryAddressBeanModel.
 * 
 * @see oracle.retail.stores.pos;ui.beans.DeliveryAddressBeanModel
 */
public class DeliveryAddressInfoBean extends ValidatingBean
{
    private static final long serialVersionUID = 4202988926212052632L;

    // instructions label tag
    protected static final String INSTRUCTIONS_LABEL = "InstructionsLabel";

    //Fields and labels that contain customer data
    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel addressLine3Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel postalCodeLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel telephoneLabel = null;
    protected JLabel postalDelim = null;
    protected JLabel customerNameLabel = null;
    protected JLabel phoneTypeLabel = null;
    protected JLabel reasonCodeLabel;
    protected JLabel extPostalCodeLabel = null;
    protected JLabel instrLabel = null;
    protected ValidatingComboBox reasonCodeField;
    protected ValidatingTextField customerNameField = null;
    protected ValidatingComboBox phoneTypeField = null;
    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField addressLine1Field = null;
    protected ConstrainedTextField addressLine2Field = null;
    protected ConstrainedTextField addressLine3Field = null;
    protected ConstrainedTextField cityField = null;
    protected ValidatingFormattedTextField postalCodeField = null;
    protected NumericTextField extPostalCodeField = null;
    protected ValidatingFormattedTextField telephoneField = null;
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    // special instructions field
    protected ConstrainedTextAreaField instrField = null;
    //  Scroll long  text area  of departments
    protected JScrollPane instrScrollPane = null;

    protected boolean customerLookup = false;
    protected boolean businessCustomer = false;

    protected int initialCountryIndex;

    /**
     * editable indicator
     */
    protected boolean editableFields = true;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * Default Constructor.
     */
    public DeliveryAddressInfoBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();
    }

    /**
     * Initialize the layout.
     */
    protected void initLayout()
    {
        JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField, extPostalCodeField, postalDelim);

        JLabel[] labels = { firstNameLabel, lastNameLabel, customerNameLabel, addressLine1Label, addressLine2Label,
                addressLine3Label, cityLabel, countryLabel, stateLabel, postalCodeLabel, phoneTypeLabel,
                telephoneLabel, reasonCodeLabel, instrLabel, };
        JComponent[] components = { firstNameField, lastNameField, customerNameField, addressLine1Field,
                addressLine2Field, addressLine3Field, cityField, countryField, stateField, postalPanel, phoneTypeField,
                telephoneField, reasonCodeField, instrScrollPane };

        setLayout(new GridBagLayout());

        int xValue = 0;

        for (int i = 0; i < labels.length; i++)
        {
            UIUtilities.layoutComponent(this, labels[i], components[i], 0, xValue, false);
            xValue++;
        }
        // init labels for fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "30");
        addressLine3Field = uiFactory.createConstrainedField("addressLine3Field", "1", "30");
        cityField = uiFactory.createConstrainedField("cityField", "1", "20");
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "20");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");

        extPostalCodeField = uiFactory.createNumericField("extpostalCodeField", "4", "4");
        extPostalCodeField.setColumns(10);
        stateField = uiFactory.createValidatingComboBox("stateField");
        countryField = uiFactory.createValidatingComboBox("countryField");
        //
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
        phoneTypeField = uiFactory.createValidatingComboBox("phoneTypeField");

        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30");
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField");

        instrScrollPane = uiFactory.createConstrainedTextAreaFieldPane("instrViaScrollPane", "0", "300", "100", "true",
                "true", JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instrField = (ConstrainedTextAreaField)instrScrollPane.getViewport().getView();
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        addressLine1Label = uiFactory.createLabel("addressLine1Label", "addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", "addressLine2Label", null, UI_LABEL);
        addressLine3Label = uiFactory.createLabel("addressLine3Label", "addressLine3Label", null, UI_LABEL);
        cityLabel = uiFactory.createLabel("cityLabel", "cityLabel", null, UI_LABEL);

        firstNameLabel = uiFactory.createLabel("firstNameLabel", "firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        telephoneLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        postalDelim = uiFactory.createLabel("postalDelim", "", null, UI_LABEL);

        customerNameLabel = uiFactory.createLabel("customerNameLabel", "customerNameLabel", null, UI_LABEL);
        phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", "phoneTypeLabel", null, UI_LABEL);

        reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", "reasonCodeLabel", null, UI_LABEL);

        extPostalCodeLabel = uiFactory.createLabel("extPostalCodeLabel", "extPostalCodeLabel", null, UI_LABEL);
        instrLabel = uiFactory.createLabel("instrLabel", "instrLabel", null, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        setChangeStateValue();
        setJournalStringUpdates();
        if (beanModel instanceof DeliveryAddressBeanModel)
        {
            DeliveryAddressBeanModel model = (DeliveryAddressBeanModel)beanModel;

            if (isBusinessCustomer() || model.isBusinessCustomer())
            {
                model.setOrgName(customerNameField.getText());
                model.setLastName(customerNameField.getText());

                if (!getCustomerLookup())
                {
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
                model.setTelephoneNumber(telephoneField.getFieldValue());
            }
            else
            {
                if (model instanceof MailBankCheckInfoDeliveryBeanModel)
                {
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                //For shipping to customer we can have business name
                //along with fist and last name
                boolean shippingToCustomer = model instanceof ShippingMethodDeliveryBeanModel;
                if (shippingToCustomer && !Util.isEmpty(customerNameField.getText()))
                {
                    model.setOrgName(customerNameField.getText());
                }

                model.setFirstName(firstNameField.getText());
                model.setLastName(lastNameField.getText());
                model.setTelephoneType(phoneTypeField.getSelectedIndex());

                int indx = phoneTypeField.getSelectedIndex();
                if (indx == -1)
                {
                    indx = 0;
                }
                model.setTelephoneType(indx);

                if (shippingToCustomer)
                {
                    // remove previously defined phone number.
                    // there is only phone number allowed for shipping customer.
                    // One of the reason is that the first available phone number will be loaded in updateBean()
                    if (model.getPhoneList() != null)
                    {
                        for (int i = 0; i < model.getPhoneList().length; i++)
                        {
                            model.setTelephoneNumber("", i);
                        }
                    }
                }
                model.setTelephoneNumber(telephoneField.getFieldValue(), indx);
            }

            model.setAddressLine1(addressLine1Field.getText());
            model.setPostalCode(postalCodeField.getFieldValue());
            if (extPostalCodeField.isVisible())
            {
                model.setExtPostalCode(extPostalCodeField.getText());
            }

            if (!getCustomerLookup())
            {

                model.setAddressLine2(addressLine2Field.getText());
                model.setAddressLine3(addressLine3Field.getText());
                model.setCity(cityField.getText());

                model.setCountryIndex(countryField.getSelectedIndex());
                model.setStateIndex(stateField.getSelectedIndex());
                model.setInstructions(instrField.getText());
            }
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void updateBean()
    {
        if (beanModel instanceof DeliveryAddressBeanModel)
        {
            // get model
            DeliveryAddressBeanModel model = (DeliveryAddressBeanModel)beanModel;

            // set edit mode
            boolean editMode = model.getEditableFields();

            if (model.isBusinessCustomer())
            {
                businessCustomer = true;
            }
            else
            {
                businessCustomer = false;
            }

            //business Customer fields to show
            // only for add/update screen
            boolean isBusinessCustomerUpdate = (businessCustomer && !customerLookup && !model.isContactInfoOnly());

            /**
             * @since 09AUG06 - CR17720 Do not make the field editable since this is a database primary key. - CMG
             */
            //
            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            setupComponent(firstNameField, editMode, !businessCustomer);
            firstNameField.setRequired(!businessCustomer);
            setFieldRequired(firstNameField, !businessCustomer);

            lastNameField.setText(model.getLastName());
            setupComponent(lastNameField, editMode, !businessCustomer);
            lastNameField.setRequired(!businessCustomer);
            setFieldRequired(lastNameField, !businessCustomer);

            if (!customerLookup && !model.isContactInfoOnly())
            {
                // set up preferred customer field
                String[] discountStrings = model.getCustomerGroupStrings();
                // if strings exist, set them in field
                if (discountStrings != null)
                {
                    if (discountStrings.length > 0)
                    {
                        discountStrings[0] = retrieveText("NoneLabel", "None");
                    }
                    // if index valid, set it
                    model.getSelectedCustomerGroupIndex();
                }
            }

            addressLine3Field.setText(model.getAddressLine3());
            setupComponent(addressLine3Field, editMode, (!customerLookup && model.is3LineAddress()));

            addressLine1Field.setText(model.getAddressLine1());
            addressLine1Field.setEditable(editMode);

            addressLine2Field.setText(model.getAddressLine2());
            setupComponent(addressLine2Field, editMode, !customerLookup);

            cityField.setText(model.getCity());
            setupComponent(cityField, editMode, !customerLookup);

            //Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            countryField.setFocusable(editMode);
            countryField.setEnabled(editMode);

            boolean shippingToCustomer = model instanceof ShippingMethodDeliveryBeanModel;
            if (shippingToCustomer)
            {
                countryField.setRequired(true);
            }

            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
            setupComponent(stateField, editMode, !customerLookup);
            stateField.setFocusable(editMode);
            stateField.setEnabled(editMode);
            if (shippingToCustomer)
            {
                stateField.setRequired(true);
            }

            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            postalCodeField.setValue(model.getPostalCode());
            postalCodeField.setEditable(editMode);
            postalCodeField.setValidationRegexp(util.getPostalCodeValidationRegexp(countryCode));

            extPostalCodeField.setText(model.getExtPostalCode());
            extPostalCodeField.setEditable(editMode);

            // update the phone
            int index = model.getTelephoneIntType();
            if (index < 0)
            {
                index = 0;
            }
            // update the phone type list
            setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
            setupComponent(phoneTypeField, editMode, !businessCustomer && !(model.isMailBankCheck()));
            for (int i = 0; i < phoneTypeField.getItemCount(); i++)
            {
                String phoneNumber = model.getTelephoneNumber(i);
                if (!Util.isEmpty(phoneNumber))
                {
                    // make first available phone the default
                    index = i;
                    i = phoneTypeField.getItemCount();
                }
            }
            phoneTypeField.setFocusable(editMode);
            phoneTypeField.setEnabled(editMode);

            String phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            telephoneField.setValue(model.getTelephoneNumber(index));
            telephoneField.setEditable(editMode);
            telephoneField.setValidationRegexp(util.getPhoneValidationRegexp(countryCode));

            setupComponent(telephoneField, editMode, true); // added - brian j.

            if (!businessCustomer)
            {
                phoneTypeField.setSelectedIndex(index);
            }

            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }
            initialCountryIndex = countryIndx;
            String[] stateList = model.getStateNames();

            // update the state combo box with the new list of states
            ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

            stateField.setModel(stateModel);
            int stateIndx = model.getStateIndex();
            if (stateIndx == -1)
            {
                stateIndx = 0;
            }
            stateField.setSelectedIndex(stateIndx);

            setPostalFields();

            // these are business customer specific fields
            customerNameField.setText(model.getOrgName());
            //This field is displayed for businessCustomer as well as shippingToCustomer
            //This field is optional for shippingToCustomer
            setupComponent(customerNameField, editMode, businessCustomer || shippingToCustomer);

            customerNameField.setRequired(businessCustomer);
            setFieldRequired(customerNameField, businessCustomer);

            if (model.getReasonCodes() != null)
            {
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(model.getReasonCodes());
                reasonCodeField.setModel(listModel);

                if (model.isSelected())
                {
                    reasonCodeField.setSelectedItem(model.getSelectedReason());
                }
                else
                {
                    reasonCodeField.setSelectedItem(model.getDefaultValue());
                }
            }
            setupComponent(reasonCodeField, editMode, isBusinessCustomerUpdate || model.isMailBankCheck());
            reasonCodeField.setFocusable(editMode);
            reasonCodeField.setEnabled(editMode);

            instrField.setText(model.getInstructions());

            if (model instanceof MailBankCheckInfoDeliveryBeanModel)
            {
                reasonCodeLabel.setText(retrieveText("IdTypeLabel", reasonCodeLabel));
            }
            updatePhoneList(null);
        }
    }

    /**
     * Convenience method to populate a comboBox
     * 
     * @param data the data to be display in the combo box
     * @param field the actual combo box field receiving the data
     * @param selected index the default selected value
     */
    protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex)
    {
        if (data != null)
        {
            ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);

            field.setModel(model);

            field.setSelectedIndex(selectedIndex);
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc) field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField) field).setEditable(isEditable);
        }
        field.setFocusable(isEditable);
        // field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        customerNameField.addFocusListener(this);
        instrField.addFocusListener(this);

        countryField.addActionListener(this);
        phoneTypeField.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        customerNameField.removeFocusListener(this);
        instrField.removeFocusListener(this);

        countryField.removeActionListener(this);
        phoneTypeField.removeActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == countryField)
        {
            updateStates();
            setPostalFields();
        }
        else if (event.getSource() == phoneTypeField)
        {
            updatePhoneList(event);
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Requests focus on parameter value name field if visible is true.
     * 
     * @param visible true if setting visible, false otherwise
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            if (isBusinessCustomer())
            {
                setCurrentFocus(customerNameField);
            }
            else
            {
                setCurrentFocus(firstNameField);
            }

        }
    }

    /**
     * Indicates whether this screens is used for a lookup. True indicates it
     * will be used for a lookup, false otherwise.
     * 
     * @param propValue customer lookup indicator
     */
    public void setCustomerLookup(String propValue)
    {
        customerLookup = (new Boolean(propValue)).booleanValue();
    }

    /**
     * Retrieves the customer lookup indicator. True indicates it will be used
     * for a lookup, false otherwise.
     * 
     * @param propValue customer lookup indicator
     */
    public boolean getCustomerLookup()
    {
        return (customerLookup);
    }

    /**
     * Indicates whether this is a business customer scenario or not.
     * 
     * @param propValue the business customer indicator
     */
    public void setBusinessCustomer(String propValue)
    {
        businessCustomer = (new Boolean(propValue)).booleanValue();
    }

    /**
     * Retrieves the business customer indicator. True indicates it is a
     * business customer, false it is not.
     * 
     * @param propValue business customer indicator
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    /**
     * Updates shipping charge base on the shipping method selected
     * 
     * @param ListSelectionEvent the listSelection event
     */
    public void updatePhoneList(ActionEvent e)
    {
        int indx = phoneTypeField.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }

        ((DeliveryAddressBeanModel)beanModel).setTelephoneType(indx);
        telephoneField.setValue(((DeliveryAddressBeanModel)beanModel).getTelephoneNumber(indx));
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {

        customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        addressLine3Label.setText(retrieveText("AddressLine3Label", addressLine3Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        postalDelim.setText(retrieveText("ExtPostalCode", "-"));

        extPostalCodeLabel.setText(retrieveText("ExtendedPostalCode", "Extended Postal Code"));

        telephoneLabel.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));
        phoneTypeLabel.setText(retrieveText("PhoneTypeLabel", phoneTypeLabel));

        customerNameField.setLabel(customerNameLabel);
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);

        reasonCodeField.setLabel(reasonCodeLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        addressLine3Field.setLabel(addressLine3Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        extPostalCodeField.setLabel(extPostalCodeLabel);
        postalCodeField.setLabel(postalCodeLabel);

        // phone properties
        telephoneField.setLabel(telephoneLabel);
        phoneTypeField.setLabel(phoneTypeLabel);
        if (instrLabel != null)
        {
            instrLabel.setText(retrieveText(INSTRUCTIONS_LABEL, instrLabel.getText()));
        }
    }

    /**
     * Update states as country selection changes
     * 
     * @param e a document event
     */
    public void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if (countryIndx < 0)
        {
            countryIndx = 0;
        }
        String[] stateList = ((CountryModel)beanModel).getStateNames(countryIndx);

        stateList = LocaleUtilities.sort(stateList, getLocale());

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        stateField.setModel(stateModel);
        //select 1st element of the list for the current country
        stateField.setSelectedIndex(0);
    }

    /**
     * Determine what postal fields should be enable/required.
     */
    protected void setPostalFields()
    {
        CountryModel countryModel = (CountryModel)beanModel;
        setFieldRequired(postalCodeField, countryModel.isPostalCodeRequired(countryField.getSelectedIndex()));
        setFieldRequired(extPostalCodeField, false);
        if (countryModel.isExtPostalCodeRequired(countryField.getSelectedIndex()))
        {
            extPostalCodeField.setVisible(true);
        }
        else
        {
            extPostalCodeField.setVisible(false);
            extPostalCodeField.setText("");
        }
        String extPostalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getExtPostalCodeFormat();
        String postalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getPostalCodeFormat();
        if (extPostalFormat != null)
        {
            extPostalCodeField.setMinLength(extPostalFormat.length());
            extPostalCodeField.setMaxLength(extPostalFormat.length());
        }
        else
        {
            extPostalCodeField.setMinLength(0);
        }
        telephoneField.setFormat(countryModel.getCountry(countryField.getSelectedIndex()).getPhoneFormat());

        postalDelim.setText(countryModel.getPostalCodeDelim(countryField.getSelectedIndex()));
        postalDelim.repaint();
    }

    protected int getMinLength(String value)
    {
        int minLen = 0;
        for (int i = 0; i < value.length(); i++)
        {
            //parse string to ignoring empty spaces
            // NOTE: the assumption that spaces are optional
            // characters
            if (!Character.isSpaceChar(value.charAt(i)))
            {
                minLen++;
            }
        }
        return minLen;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: CustomerInfoBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        if (beanModel != null)
        {

            strResult += "\n\nbeanModel = ";
            strResult += beanModel.toString();

        }
        else
        {
            strResult += "\nbeanModel = null\n";
        }

        if (firstNameField != null)
        {

            strResult += "\nfirstNameField text = ";
            strResult += firstNameField.getText();
            strResult += ", min length = ";
            strResult += firstNameField.getMinLength();
            strResult += ", max length = ";
            strResult += firstNameField.getMaxLength();

        }
        else
        {
            strResult += "\nfirstNameField = null\n";
        }

        if (lastNameField != null)
        {

            strResult += "\nlastNameField text = ";
            strResult += lastNameField.getText();
            strResult += ", min length = ";
            strResult += lastNameField.getMinLength();
            strResult += ", max length = ";
            strResult += lastNameField.getMaxLength();

        }
        else
        {
            strResult += "\nlastNameField = null\n";
        }

        if (addressLine1Field != null)
        {

            strResult += "\naddressLine1Field text = ";
            strResult += addressLine1Field.getText();
            strResult += ", min length = ";
            strResult += addressLine1Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine1Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine1Field = null\n";
        }

        if (addressLine2Field != null)
        {

            strResult += "\naddressLine2Field text = ";
            strResult += addressLine2Field.getText();
            strResult += ", min length = ";
            strResult += addressLine2Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine2Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine2Field = null\n";
        }

        if (cityField != null)
        {

            strResult += "\ncityField text = ";
            strResult += cityField.getText();
            strResult += ", min length = ";
            strResult += cityField.getMinLength();
            strResult += ", max length = ";
            strResult += cityField.getMaxLength();

        }
        else
        {
            strResult += "\ncityField = null\n";
        }

        if (postalCodeField != null)
        {

            strResult += "\npostalCodeField text = ";
            strResult += postalCodeField.getText();

        }
        else
        {
            strResult += "\npostalCodeField = null\n";
        }

        if (extPostalCodeField != null)
        {

            strResult += "\nExtpostalCodeField text = ";
            strResult += extPostalCodeField.getText();
            strResult += ", min length = ";
            strResult += extPostalCodeField.getMinLength();
            strResult += ", max length = ";
            strResult += extPostalCodeField.getMaxLength();

        }
        else
        {
            strResult += "\nExtpostalCodeField = null\n";
        }
        strResult += "\neditableFields =" + editableFields + "\n";

        // pass back result
        return (strResult);
    }

    /**
     * Tests each data field to determine if user has entered or updated data.
     * If data has changed then set the set change status to true, otherwise set
     * it to false.
     */
    protected void setChangeStateValue()
    {
        // convert the telephoneField to String for comparison
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString(telephoneField.getFieldValue());

        if (beanModel instanceof MailBankCheckInfoDeliveryBeanModel)
        {
            MailBankCheckInfoDeliveryBeanModel model = (MailBankCheckInfoDeliveryBeanModel)beanModel;
            if ((!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getFirstName(), firstNameField
                    .getText()) != 0))
                    || (!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getLastName(),
                            lastNameField.getText()) != 0))
                    || (model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getOrgName(),
                            customerNameField.getText()) != 0))
                    || (LocaleUtilities.compareValues(model.getPostalCode(), postalCodeField.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getAddressLine1(), addressLine1Field.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getAddressLine2(), addressLine2Field.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getAddressLine3(), addressLine3Field.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getCity(), cityField.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getExtPostalCode(), extPostalCodeField.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getTelephoneNumber(phoneTypeField.getSelectedIndex()),
                            phone.getPhoneNumber()) != 0)
                    || (model.getSelectedIndex() != reasonCodeField.getSelectedIndex())
                    || (model.getStateIndex() != stateField.getSelectedIndex())
                    || (initialCountryIndex != countryField.getSelectedIndex()))
            {
                model.setChangeState(true); // user changed data
            }
            else
            {
                model.setChangeState(false); // same data as in the model
            }
        }
    }

    /**
     * Tests each data field to determine if user has entered or updated data.
     * If data has changed then construct the journalString with the old and new
     * values for this field.
     */
    protected void setJournalStringUpdates()
    {
        Object [] dataArgs = null;
    	if (beanModel instanceof MailBankCheckInfoDeliveryBeanModel)
        {
            MailBankCheckInfoDeliveryBeanModel model = (MailBankCheckInfoDeliveryBeanModel)beanModel;
            // convert the telephoneField to String for comparison
            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
            phone.parseString(telephoneField.getFieldValue());

            if (model.isBusinessCustomer())
            {
                if (model.getCustomerName().compareTo(customerNameField.getText()) != 0)
                {
            	  dataArgs = new Object[]{model.getCustomerName()};
            	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_BUSINESS_NAME_LABEL, dataArgs);
            	  dataArgs = new Object[]{customerNameField.getText()};
            	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_BUSINESS_NAME_LABEL, dataArgs);
            	  model.setJournalString( model.getJournalString()
                          +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
              }
            }
            else
            {
                // test every field and set journal string for those that have changed
                if (model.getFirstName().compareTo(firstNameField.getText()) != 0)
                {
            	  dataArgs = new Object[]{model.getFirstName()};
            	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_FIRST_NAME_LABEL, dataArgs);
            	  dataArgs = new Object[]{firstNameField.getText()};
            	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_FIRST_NAME_LABEL, dataArgs);
            	  model.setJournalString( model.getJournalString()
                          +Util.EOL
                              + oldValue
                              +Util.EOL
                              + newValue);
              }
                if (model.getLastName().compareTo(lastNameField.getText()) != 0)
                {
            	  dataArgs = new Object[]{model.getLastName()};
            	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_LAST_NAME_LABEL, dataArgs);
            	  dataArgs = new Object[]{lastNameField.getText()};
            	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_LAST_NAME_LABEL, dataArgs);
            	  model.setJournalString( model.getJournalString()
                          +Util.EOL
                              + oldValue
                              +Util.EOL
                              + newValue);
              }
            }
            if (model.getAddressLine1().compareTo(addressLine1Field.getText()) != 0)
            {
        	  dataArgs = new Object[]{model.getAddressLine1()};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ADDRESS_LINE_1_LABEL, dataArgs);
        	  dataArgs = new Object[]{addressLine1Field.getText()};
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ADDRESS_LINE_1_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
          }
            if (model.getAddressLine2().compareTo(addressLine2Field.getText()) != 0)
            {
        	  dataArgs = new Object[]{model.getAddressLine2()};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ADDRESS_LINE_2_LABEL, dataArgs);
        	  dataArgs = new Object[]{addressLine2Field.getText()};
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ADDRESS_LINE_2_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
          }

            if (model.getAddressLine3().compareTo(addressLine3Field.getText()) != 0)
            {

          	  dataArgs = new Object[]{model.getAddressLine3()};
          	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ADDRESS_LINE_3_LABEL, dataArgs);
          	  dataArgs = new Object[]{addressLine3Field.getText()};
          	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ADDRESS_LINE_3_LABEL, dataArgs);
          	  model.setJournalString( model.getJournalString()
                        +Util.EOL
                            + oldValue
                            +Util.EOL
                            + newValue);
            }
            if (model.getCity().compareTo(cityField.getText()) != 0)
            {
        	  dataArgs = new Object[]{model.getCity()};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_CITY_LABEL, dataArgs);
        	  dataArgs = new Object[]{cityField.getText()};
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_CITY_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
            }
            if (model.getStateIndex() != stateField.getSelectedIndex())
            {
        	  dataArgs = new Object[]{model.getState()};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_STATE_LABEL, dataArgs);
        	  dataArgs = new Object[]{(String)stateField.getSelectedItem()};
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_STATE_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
            }
            if (model.getCountryIndex() != countryField.getSelectedIndex())
            {
        	  dataArgs = new Object[]{model.getCountry()};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_COUNTRY_LABEL, dataArgs);
        	  dataArgs = new Object[]{(String) countryField.getSelectedItem()};
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_COUNTRY_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
            }
            if ((model.getPostalCode().compareTo(postalCodeField.getText()) != 0)
                    || (model.getExtPostalCode().compareTo(extPostalCodeField.getText()) != 0))
            {

          	  dataArgs = new Object[]{model.getExtPostalCode()};
          	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_POSTAL_CODE_LABEL, dataArgs);
          	  dataArgs = new Object[]{extPostalCodeField.getText()};
          	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_POSTAL_CODE_LABEL, dataArgs);
          	  model.setJournalString( model.getJournalString()
                        +Util.EOL
                            + oldValue
                            +Util.EOL
                            + newValue);
            }
            if (model.getTelephoneNumber(model.getTelephoneIntType()).compareTo(phone.getPhoneNumber()) != 0)
            {
        	  dataArgs = new Object[]{model.getTelephoneNumber(model.getTelephoneIntType())};
        	  String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_PHONE_NUMBER_LABEL, dataArgs);
        	  dataArgs = new Object[]{ phone.getPhoneNumber() };
        	  String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_PHONE_NUMBER_LABEL, dataArgs);
        	  model.setJournalString( model.getJournalString()
                      +Util.EOL
                          + oldValue
                          +Util.EOL
                          + newValue);
        	}

        }
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    // ---------------------------------------------------------------------
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

        CustomerInfoBean bean = new CustomerInfoBean();
        bean.telephoneField.setValue("4043865851");
        System.out.println("1: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("4(512)555-1212");
        System.out.println("2: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("(512)555-1212");
        System.out.println("3: " + bean.telephoneField.getFieldValue());

        UIUtilities.doBeanTest(bean);
    }
}

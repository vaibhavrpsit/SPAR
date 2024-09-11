/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerAddBean.java /main/51 2014/01/07 16:26:14 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  01/07/14 - Fix to save/update customer phone numbers correctly
 *                         by updating them at one go
 *    abananan  10/07/13 - pricingGroupField width changed from 10 to 25 in
 *                         CustomerInfoBean and CustomerAddBean
 *    subrdey   06/03/13 - Change email field length to 60
 *    arabalas  03/07/13 - changed the maximum allowed length of Email from 64
 *                         to 30
 *    vbongu    03/01/13 - Change tax cert field length to 30 as in CO
 *    abhineek  01/31/13 - Added preference field to CusotmerInfo screen
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *    asinton   09/12/12 - prevent null pointer by getting ParameterManagerIfc
 *                         from Gateway instead of ADOContext.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   08/29/12 - widened email field because it was too short in
 *                         customer demos
 *    acadar    07/18/12 - updates for XC Customer
 *    acadar    05/31/12 - code review comments
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    vtemker   03/29/12 - Merged conflicts
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    rabhawsa  12/23/11 - defect183 email field length increased to 64
 *                         characters
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    ohorne    03/30/09 - added email validation
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    mkochumm  02/19/09 - set country always so that we know what format the
 *                         phone no. is in
 *    glwang    02/02/09 - set max length of first name and last name fields as
 *                         1
 *    glwang    01/29/09 - set max length of user id as 10
 *    mahising  01/21/09 - fixed issue when adding duplicate customer
 *    mahising  12/31/08 - fix QA issue
 *    vchengeg  12/12/08 - EJ defect fixes
 *    mkochumm  12/09/08 - fix screen layout
 *    mahising  12/05/08 - base rework
 *    mahising  11/26/08 - fixed merge issue
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         3/13/2008 11:14:37 AM  Mathews Kochummen
 *       forward port v12x to trunk. reviewed by michael
 *  8    I18N_P2    1.3.1.3     2/6/2008 2:03:42 PM    Sandy Gu        Changed
 *       the employeeID field to allow multi byptes
 *  7    I18N_P2    1.3.1.2     1/8/2008 2:56:48 PM    Sandy Gu        Set max
 *       length of constraied text field.
 *  6    I18N_P2    1.3.1.1     1/7/2008 3:41:03 PM    Maisa De Camargo 29826 -
 *        Setting the size of the combo boxes. This change was necessary
 *       because the width of the combo boxes used to grow according to the
 *       length of the longest content. By setting the size, we allow the
 *       width of the combo box to be set independently     from the width of
 *       the dropdown menu.
 *  5    I18N_P2    1.3.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *       alphanumerice fields for I18N purpose
 *  4    360Commerce 1.3         10/11/2007 12:31:18 PM Peter J. Fierro Changes
 *        to define popup widths independently of max columns
 *  3    360Commerce 1.2         10/10/2007 1:02:00 PM  Anda D. Cadar   Changes
 *        to not allow double byte chars
 *  2    360Commerce 1.1         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *       changes to not allow double bytes chars in some cases
 *  1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This bean is used for displaying the Customer information screen based on the
 * data from the CustomerInfoBeanModel.
 *
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBeanModel
 */
public class CustomerAddBean extends ValidatingBean
{
    private static final long serialVersionUID = -1285669032589267370L;

    // Fields and labels that contain customer data

    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel postalCodeLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel telephoneLabel = null;
    protected JLabel emailLabel = null;
    protected JLabel customerNameLabel = null;
    protected JLabel phoneTypeLabel = null;
    protected JLabel discountLabel = null;
    protected JLabel taxCertificateLabel;
    protected JLabel reasonCodeLabel;
    protected JLabel custTaxIDLabel = null;
    protected JLabel pricingGroupLabel = null;
    protected JLabel preferredReceiptModeLabel = null;
    protected ConstrainedTextField custTaxIDField ;
    protected ValidatingComboBox pricingGroupField;
    protected AlphaNumericTextField taxCertificateField;
    protected ValidatingComboBox reasonCodeField;
    protected ValidatingTextField customerNameField = null;
    protected ValidatingComboBox phoneTypeField = null;
    protected ValidatingFormattedTextField emailField = null;

    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField addressLine1Field = null;
    protected ConstrainedTextField addressLine2Field = null;
    protected ConstrainedTextField cityField = null;
    protected ValidatingFormattedTextField postalCodeField = null;
    protected ValidatingFormattedTextField telephoneField = null;
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    protected ValidatingComboBox discountField = null;
    protected ValidatingComboBox preferredReceiptModeField = null;
    
    protected boolean customerLookup = false;
    protected boolean businessCustomer = false;

    protected int initialCountryIndex;

    /**
     * editable indicator
     */
    protected boolean editableFields = true;
    
    /**
     * telephone type selected 
     */
    private int fieldTelephoneTypeSelected = PhoneConstantsIfc.PHONE_TYPE_HOME;
    /**
     * Default Constructor.
     */
    
 
    public CustomerAddBean()
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
        JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField);
        JPanel panel1 = createPanel(discountField, pricingGroupLabel, pricingGroupField);
        JPanel panel2 = createPanel(taxCertificateField, reasonCodeLabel, reasonCodeField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(17);
        orderableFields.add(new OrderableField(firstNameLabel, firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(lastNameLabel, lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(customerNameLabel, customerNameField, AddressField.BUSINESS_NAME));
        orderableFields.add(new OrderableField(addressLine1Label, addressLine1Field, AddressField.ADDRESS_LINE_1));
        orderableFields.add(new OrderableField(addressLine2Label, addressLine2Field, AddressField.ADDRESS_LINE_2));
        orderableFields.add(new OrderableField(cityLabel, cityField, AddressField.CITY));
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(stateLabel, stateField, AddressField.STATE));
        orderableFields.add(new OrderableField(postalCodeLabel, postalPanel, AddressField.POSTAL_CODE));
        orderableFields.add(new OrderableField(phoneTypeLabel, phoneTypeField, AddressField.TELEPHONE_TYPE));
        orderableFields.add(new OrderableField(telephoneLabel, telephoneField, AddressField.TELEPHONE));
        orderableFields.add(new OrderableField(emailLabel, emailField));
        orderableFields.add(new OrderableField(preferredReceiptModeLabel, preferredReceiptModeField));
        orderableFields.add(new OrderableField(discountLabel, panel1));
        orderableFields.add(new OrderableField(taxCertificateLabel, panel2));
        orderableFields.add(new OrderableField(custTaxIDLabel, custTaxIDField));
        
        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        setLayout(new GridBagLayout());

        int xValue = 0;
       /* if (!getCustomerLookup()) // if customer info lookup do not layout the customer id
        {
            UIUtilities.layoutComponent(this, employeeIDLabel, employeeIDField, 0, 0, false);

            employeeIDField.setLabel(employeeIDLabel);
            xValue = 1;
        } */
        
        for (OrderableField orderedField : orderedFields)
        {
            UIUtilities.layoutComponent(this, orderedField.getLabel(), orderedField.getField(), 0, xValue, false);
            xValue++;
        }
        
        // init labels for fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);
        preferredReceiptModeField.setLabel(preferredReceiptModeLabel);
    }

    JPanel createPanel(JComponent component1, JComponent component2, JComponent component3)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIManager.getColor("beanBackground"));
        panel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        panel.add(component1, constraints);

        constraints.insets = new Insets(0, 10, 0, 0);
        panel.add(component2);
        panel.add(component3, constraints);

        return panel;
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "60", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "60", "30");
        cityField = uiFactory.createConstrainedField("cityField", "1", "30", "20");
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");
        stateField = uiFactory.createValidatingComboBox("stateField", "false", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        emailField = uiFactory.createValidatingFormattedTextField("emailField", "", "60", "30");
        preferredReceiptModeField = uiFactory.createValidatingComboBox("preferredReceiptModeField", "false", "20");
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
        phoneTypeField = uiFactory.createValidatingComboBox("phoneTypeField", "false", "10");
        discountField = uiFactory.createValidatingComboBox("discountField", "false", "12");
        discountField.setEditable(false);
        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30", "30");
        taxCertificateField = uiFactory.createAlphaNumericField("taxCertificateField", "1", "30", "15", false);
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField", "false", "10");
        custTaxIDField = uiFactory.createAlphaNumericField("custTaxIDField", "1", "15");
        pricingGroupField = uiFactory.createValidatingComboBox("pricingGroupField", "false", "25");
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        addressLine1Label = uiFactory.createLabel("addressLine1Label", "addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", "addressLine2Label", null, UI_LABEL);
        cityLabel = uiFactory.createLabel("cityLabel", "cityLabel", null, UI_LABEL);

        firstNameLabel = uiFactory.createLabel("firstNameLabel", "firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        telephoneLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        emailLabel = uiFactory.createLabel("emailLabel", "emailLabel", null, UI_LABEL);
        preferredReceiptModeLabel = uiFactory.createLabel("preferredReceiptModeLabel", "preferredReceiptModeLabel", null, UI_LABEL);
        customerNameLabel = uiFactory.createLabel("customerNameLabel", "customerNameLabel", null, UI_LABEL);
        phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", "phoneTypeLabel", null, UI_LABEL);
        discountLabel = uiFactory.createLabel("discountLabel", "discountLabel", null, UI_LABEL);
        taxCertificateLabel = uiFactory.createLabel("taxCertificateLabel", "taxCertificateLabel", null, UI_LABEL);

        reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", "reasonCodeLabel", null, UI_LABEL);
        custTaxIDLabel = uiFactory.createLabel("custTaxIDLabel", "custTaxIDLabel", null, UI_LABEL);
        pricingGroupLabel = uiFactory.createLabel("pricingGroupLabel", "pricingGroupLabel", null, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        EncipheredDataIfc customerTaxCertificate = null;
        EncipheredDataIfc customerTaxID = null;
        setChangeStateValue();
        setJournalStringUpdates();
        if (beanModel instanceof CustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;

            if (isBusinessCustomer() || model.isBusinessCustomer())
            {
                model.setOrgName(customerNameField.getText());
                model.setLastName(customerNameField.getText());

                if (!getCustomerLookup())
                {
                    try
                    {
                        customerTaxCertificate = FoundationObjectFactory.getFactory()
                                .createEncipheredDataInstance(taxCertificateField.getText().getBytes());
                    }
                    catch (EncryptionServiceException e)
                    {
                        logger.error("Could not encrypt text" + e.getLocalizedMessage());
                    }
                    model.setEncipheredTaxCertificate(customerTaxCertificate);
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
                model.setCountryIndex(countryField.getSelectedIndex());
                model.setTelephoneNumber(telephoneField.getFieldValue());
                model.setSelectedReceiptMode(preferredReceiptModeField.getSelectedIndex());
            }
            else
            {
                if (model instanceof MailBankCheckInfoBeanModel)
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
                boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
                if (shippingToCustomer &&
                    !Util.isEmpty(customerNameField.getText()))
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

                if ( shippingToCustomer )
                {
                    // remove previously defined phone number.
                    // there is only phone number allowed for shipping customer.
                    // One of the reason is that the first available phone number will be loaded in updateBean()
                    if ( model.getPhoneList() != null )
                    {
                        for ( int i = 0; i < model.getPhoneList().length; i++ )
                        {
                            model.setTelephoneNumber("", i);
                        }
                    }
                }
                model.setCountryIndex(countryField.getSelectedIndex());
                model.setTelephoneNumber(telephoneField.getFieldValue(), indx);
                model.setSelectedReceiptMode(preferredReceiptModeField.getSelectedIndex());
            }

            model.setAddressLine1(addressLine1Field.getText());
            model.setPostalCode(postalCodeField.getFieldValue());

            if (!getCustomerLookup())
            {
                if (!model.isContactInfoOnly())
                {
                    model.setSelectedCustomerGroupIndex(discountField.getSelectedIndex());
                }
                if (!(model instanceof MailBankCheckInfoBeanModel))
                {
                    model.setEmail(emailField.getText());
                }
                model.setAddressLine2(addressLine2Field.getText());
                model.setCity(cityField.getText());

                model.setCountryIndex(countryField.getSelectedIndex());
                model.setStateIndex(stateField.getSelectedIndex());

            }
            if (!Util.isEmpty(custTaxIDField.getText()))
            {
                try
                {
                    customerTaxID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                            custTaxIDField.getText().getBytes());
                    model.setTaxID(customerTaxID);
                }
                catch (EncryptionServiceException e)
                {
                    logger.error("Could not encrypt text.", e);
                }
            }
            model.setSelectedCustomerPricingGroup(pricingGroupField.getSelectedIndex());
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof CustomerInfoBeanModel)
        {
            // get model
            CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;
            
                      
            if (model.selectedReceiptMode == ReceiptConstantsIfc.EMAIL_RECEIPT
                    || model.selectedReceiptMode == ReceiptConstantsIfc.PRINT_AND_EMAIL_RECEIPT)
            {
                setRequiredValidatingFields("emailField");
            }

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
                    discountField.setModel(new ValidatingComboBoxModel(discountStrings));
                    // if index valid, set it
                    int index = model.getSelectedCustomerGroupIndex();
                    if (index > -1)
                    {
                        discountField.setSelectedIndex(index);
                    }
                }
            }
            setupComponent(discountField, editMode, !customerLookup && !model.isContactInfoOnly());
            discountField.setFocusable(editMode);
            discountField.setEnabled(editMode);

            addressLine1Field.setText(model.getAddressLine1());
            addressLine1Field.setEditable(editMode);

            addressLine2Field.setText(model.getAddressLine2());
            setupComponent(addressLine2Field, editMode, !customerLookup);

            cityField.setText(model.getCity());
            setupComponent(cityField, editMode, !customerLookup);

            //Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManagerIfc util = (UtilityManagerIfc) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            String phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneField.setValidationRegexp(phoneValidationRegexp);

            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            setupComponent(countryField, editMode, !customerLookup);
            countryField.setFocusable(editMode);
            countryField.setEnabled(editMode);

            boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
            if ( shippingToCustomer )
            {
                countryField.setRequired(true);
            }

            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
            setupComponent(stateField, editMode, !customerLookup);
            stateField.setFocusable(editMode);
            stateField.setEnabled(editMode);
            if ( shippingToCustomer )
            {
                stateField.setRequired(true);
            }

            postalCodeField.setValue(model.getPostalCode());
            postalCodeField.setEditable(editMode);

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


            telephoneField.setValue(model.getTelephoneNumber(index));
            telephoneField.setEditable(editMode);
            setupComponent(telephoneField, editMode, true); // added - brian j.

            if (!businessCustomer)
            {
                phoneTypeField.setSelectedIndex(index);
            }

            emailField.setText(model.getEmail());
            emailField.setValidationRegexp(util.getEmailValidationRegexp());
            setupComponent(emailField, editMode, !customerLookup && !(model instanceof MailBankCheckInfoBeanModel));
            
            if (model.getReceiptModes() != null)
            {
                 preferredReceiptModeField.setModel(new ValidatingComboBoxModel(model.getReceiptModes()));
                 preferredReceiptModeField.setSelectedIndex(model.getSelectedReceiptMode());
            }
            
            // show receipt preference field in customer master for business customer
            
            setupComponent(preferredReceiptModeField, editMode, !customerLookup);
            preferredReceiptModeField.getLabel().setVisible(!customerLookup);
           
            
            // set email field mandatory if receipt option is email/print and email
            if (model.getSelectedReceiptMode() == ReceiptConstantsIfc.EMAIL_RECEIPT
                    || model.getSelectedReceiptMode() == ReceiptConstantsIfc.PRINT_AND_EMAIL_RECEIPT)
            {
                setFieldRequired(emailField, true);
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
           
            
            //
            setPostalFields();

            // these are business customer specific fields
            customerNameField.setText(model.getOrgName());
            //This field is displayed for businessCustomer as well as shippingToCustomer
            //This field is optional for shippingToCustomer
            setupComponent(customerNameField, editMode, businessCustomer || shippingToCustomer);

            customerNameField.setRequired(businessCustomer);
            setFieldRequired(customerNameField, businessCustomer);
            if (model.getEncipheredTaxCertificate() != null)
            {
                byte[] taxCertificate = null;
                try
                {
                    taxCertificate = model.getEncipheredTaxCertificate().getDecryptedNumber();
                    taxCertificateField.setText(new String(taxCertificate));
                }
                finally
                {
                    Util.flushByteArray(taxCertificate);
                }
            }
            else
            {
                taxCertificateField.setText("");
            }
            setupComponent(taxCertificateField, editMode, isBusinessCustomerUpdate);

            if (model.getReasonCodes() != null)
            {
                reasonCodeField.setModel(new ValidatingComboBoxModel (model.getReasonCodes()));
                if (model.isSelected())
                {
                    reasonCodeField.setSelectedItem(model.getSelectedReason());
                }
            }
            setupComponent(reasonCodeField, editMode, isBusinessCustomerUpdate || model.isMailBankCheck());
            reasonCodeField.setFocusable(editMode);
            reasonCodeField.setEnabled(editMode);


            if (model instanceof MailBankCheckInfoBeanModel)
            {
                reasonCodeLabel.setText(retrieveText("IdTypeLabel", reasonCodeLabel));
            }

            if(model.getTaxID()!=null)
            {
                byte[] taxID = null;
                try
                {
                    taxID = model.getTaxID().getDecryptedNumber();
                    custTaxIDField.setText(new String(taxID));
                }
                finally
                {
                    Util.flushByteArray(taxID);
                }
            }
            else
            {
                custTaxIDField.setText("");
            }
            String[] pricingStrings = model.getPricingGroups();

            if (pricingStrings != null)
            {
                if (pricingStrings.length > 0)
                {
                    pricingStrings[0] = retrieveText("NoneLabel", "None");
                }
                pricingGroupField.setModel(new ValidatingComboBoxModel(pricingStrings));
                // if index valid, set it
                int pricingIndex = model.getSelectedCustomerPricingGroup();
                if (pricingIndex > -1)
                {
                    pricingGroupField.setSelectedIndex(pricingIndex);
                }
            }
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

        field.setRequestFocusEnabled(isVisible);
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

        countryField.addActionListener(this);
        phoneTypeField.addActionListener(this);
        
        // disabling pricing group label and field
        // if parameter CustomerSpecificPricing is false
        ParameterManagerIfc pm = (ParameterManagerIfc)Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        try
        {

            if (!(pm.getBooleanValue("CustomerSpecificPricing")))
            {

                pricingGroupField.setVisible(false);
                pricingGroupLabel.setVisible(false);
            }

        }
        catch (ParameterException e)
        {
            logger.error("parameter not found for CustomerSpecificPricing");
        }
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
            int selectedCountryIndex = countryField.getSelectedIndex() ;
            updatePhoneFormat(selectedCountryIndex);
            updatePostalFormat(selectedCountryIndex);
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

    private void updatePhoneFormat(int selectedCountryIndex)
    {
        int countryIndex = ((CustomerInfoBeanModel) beanModel).getCountryIndex();
        String countryCode = ((CustomerInfoBeanModel) beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (((CustomerInfoBeanModel) beanModel).getTelephoneNumber() != null)
        {
            if ((selectedCountryIndex != countryIndex)
                    || (((CustomerInfoBeanModel) beanModel).getTelephoneNumber().equals("")))
            {
                String phoneFormat = null;
                String phoneValidationRegexp = null;
                phoneFormat = util.getPhoneFormat(countryCode);
                telephoneField.setFormat(phoneFormat);
                phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
                telephoneField.setValidationRegexp(phoneValidationRegexp);
                ((CustomerInfoBeanModel) beanModel).setCountryIndex(selectedCountryIndex);
                ((CustomerInfoBeanModel) beanModel).setTelephoneNumber("");
            }
        }
    }

    private void updatePostalFormat(int selectedCountryIndex)
    {
        int countryIndex = ((CustomerInfoBeanModel)beanModel).getCountryIndex();
        String countryCode = ((CustomerInfoBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if ( (selectedCountryIndex != countryIndex)
              || (((CustomerInfoBeanModel)beanModel).getPostalCode().equals("")) )
        {
            String postalCodeFormat = null;
            String postalCodeValidationRegexp = null;
            postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            ((CustomerInfoBeanModel)beanModel).setCountryIndex(selectedCountryIndex);
            ((CustomerInfoBeanModel)beanModel).setPostalCode("");
        }
    }

    /**
     * Requests focus on parameter value name field if visible is true.
     *
     * @param visible true if setting visible, false otherwise
     */
    @Override
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
     *  Retrieves the business customer indicator. True indicates it is
     *  a business customer, false it is not.
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    /**
     * Update the Phone number in phone number text field , based on the selected phone type
     * 
     * @param e the listSelection event
     */
    public void updatePhoneList(ActionEvent e)
    {
        int indx = phoneTypeField.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }
        String phonenumber=telephoneField.getFieldValue().trim();
        PhoneIfc phone = null;
        if(((CustomerInfoBeanModel)beanModel).getPhoneList()!= null)
        {
            phone = ((CustomerInfoBeanModel)beanModel).getPhoneList()[fieldTelephoneTypeSelected];
        }
        if (phone != null)
        {
            if (oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
            {
                phone.setStatusCode(PhoneConstantsIfc.STATUS_INACTIVE);
            }
            else
            {
                phone.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
                phone.setPhoneNumber(phonenumber);
            }
        }
        else
        {
            if(!oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
            {

                ((CustomerInfoBeanModel)beanModel).setTelephoneNumber(phonenumber, fieldTelephoneTypeSelected);
            }
        }
        fieldTelephoneTypeSelected = indx;
        ((CustomerInfoBeanModel)beanModel).setTelephoneType(indx);
        telephoneField.setValue(((CustomerInfoBeanModel)beanModel).getTelephoneNumber(indx));
    }

    /**
     *  Update property fields.
     */
    protected void updatePropertyFields()
    {

        customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        discountLabel.setText(retrieveText("DiscountLabelWithColon", discountLabel));
        taxCertificateLabel.setText(retrieveText("TaxCertificateLabel", taxCertificateLabel));

        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        emailLabel.setText(retrieveText("EmailLabel", emailLabel));
        preferredReceiptModeLabel.setText(retrieveText("ReceiptPreferenceLabel", preferredReceiptModeLabel));
        
        telephoneLabel.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));
        phoneTypeLabel.setText(retrieveText("PhoneTypeLabel", phoneTypeLabel));

        custTaxIDLabel.setText(retrieveText("TaxIDLabel", custTaxIDLabel));
        pricingGroupLabel.setText(retrieveText("PricingGroupLabel", pricingGroupLabel));

        // customer info
        customerNameField.setLabel(customerNameLabel);
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);

        discountField.setLabel(discountLabel);
        taxCertificateField.setLabel(taxCertificateLabel);
        reasonCodeField.setLabel(reasonCodeLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        postalCodeField.setLabel(postalCodeLabel);
        emailField.setLabel(emailLabel);
        
        // phone properties
        telephoneField.setLabel(telephoneLabel);
        phoneTypeField.setLabel(phoneTypeLabel);

         // for tax ID and pricing group
        custTaxIDField.setLabel(custTaxIDLabel);
        pricingGroupField.setLabel(pricingGroupLabel);
    }

    /**
     * Update states as country selection changes
     */
    public void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if (countryIndx < 0)
        {
            countryIndx = 0;
        }
        String[] stateList = ((CountryModel) beanModel).getStateNames(countryIndx);

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
        setFieldRequired(postalCodeField, false);
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
        String strResult = new String("Class: CustomerAddBean (Revision " + getRevisionNumber() + ") @" + hashCode());
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
        String phoneNumber = new String(phone.getPhoneNumber());

        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel) beanModel;
            if ((!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getFirstName(), firstNameField.getText()) != 0))
                || (!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getLastName(), lastNameField.getText()) != 0))
                || (model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getOrgName(), customerNameField.getText()) != 0))
                || (LocaleUtilities.compareValues(model.getPostalCode(), postalCodeField.getFieldValue()) != 0)
                || (LocaleUtilities.compareValues(model.getAddressLine1(), addressLine1Field.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getAddressLine2(), addressLine2Field.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getCity(), cityField.getText()) != 0)
                || (LocaleUtilities
                    .compareValues(model.getTelephoneNumber(phoneTypeField.getSelectedIndex()), phoneNumber)
                    != 0)
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
        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel) beanModel;
            // convert the telephoneField to String for comparison
            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
            phone.parseString(telephoneField.getFieldValue());
            String phoneNumber = new String(phone.getPhoneNumber());

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
            if ((model.getPostalCode().compareTo(postalCodeField.getFieldValue()) != 0))
            {
                dataArgs = new Object[]{model.getPostalCode()};
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_POSTAL_CODE_LABEL, dataArgs);
                dataArgs = new Object[]{postalCodeField.getFieldValue()};
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_POSTAL_CODE_LABEL, dataArgs);
                model.setJournalString( model.getJournalString()
                        +Util.EOL
                            + oldValue
                            +Util.EOL
                            + newValue);
            }
            if (model.getTelephoneNumber(model.getTelephoneIntType()).compareTo(phoneNumber) != 0)
            {
                dataArgs = new Object[]{model.getTelephoneNumber(model.getTelephoneIntType())};
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_PHONE_NUMBER_LABEL, dataArgs);
                dataArgs = new Object[]{phoneNumber};
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_PHONE_NUMBER_LABEL, dataArgs);
                model.setJournalString( model.getJournalString()
                        +Util.EOL
                            + oldValue
                            +Util.EOL
                            + newValue);
              }

            if (model.getEmail().compareTo(emailField.getText()) != 0)
            {
                dataArgs = new Object[]{model.getEmail()};
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_EMAIL_LABEL, dataArgs);
                dataArgs = new Object[]{emailField.getText()};
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_EMAIL_LABEL, dataArgs);
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

        CustomerAddBean bean = new CustomerAddBean();
        bean.telephoneField.setValue("4043865851");
        System.out.println("1: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setText("4(512)555-1212");
        System.out.println("2: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("(512)555-1212");
        System.out.println("3: " + bean.telephoneField.getFieldValue());

        UIUtilities.doBeanTest(bean);
    }
}

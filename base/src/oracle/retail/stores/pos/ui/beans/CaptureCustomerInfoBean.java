/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CaptureCustomerInfoBean.java /main/34 2013/01/18 14:54:20 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  01/14/13 - fix the issue of return transaction linked with a
 *                         business custome
 *    abondala  12/20/12 - fix the NPE
 *    yiqzhao   11/09/12 - Enable cancel and delete buttons on global
 *                         navigation panel.
 *    yiqzhao   07/30/12 - remove id type from shipping customer info screen
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    cgreene   06/18/09 - fix actionlistener to be removed in deactivate
 *                         method
 *    ohorne    03/12/09 - Min length of firstName and lastName fields is now 1
 *    cgreene   03/01/09 - change method calls from deprecated setPhone methods
 *                         to new ones
 *    mahising  02/21/09 - Fixed phonetype issue for mailcheck tender
 *    mahising  02/19/09 - Resolved Bug Id:2211
 *    mahising  02/17/09 - fixed personal id business customer issue
 *    npoola    02/11/09 - fix issue for send customer info
 *    deghosh   12/08/08 - EJ i18n changes
 *    mdecama   12/03/08 - Updates per code review
 *    mdecama   12/03/08 - Updated logic when setting the personalID
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    ebthorne  10/30/08 - Post merge updates.
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes
 * ===========================================================================
     $Log:
      7    .v8x      1.3.1.2     3/11/2007 3:53:55 PM   Brett J. Larsen CR 4530
            - default reason code not being displayed in list

           added support for default reason code (IDType for this screen)
      6    .v8x      1.3.1.1     3/6/2007 2:54:15 PM    Brett J. Larsen CR 4530
            - reverting to prior version - misunderstood requirements
      5    .v8x      1.3.1.0     3/5/2007 4:45:12 PM    Brett J. Larsen CR 4530
            - id type should not have a default value - previous value being
           saved in control - setting index to "-1" always
      4    360Commerce1.3         7/28/2006 6:03:57 PM   Brett J. Larsen CR
           4530 - default reason code fix
           v7x->360Commerce merge
      3    360Commerce1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $

      6    .v7x      1.2.1.2     7/17/2006 10:55:23 AM  Michael Wisbauer
           changed code to set default to none and also commented out code
           that was causing cotrol to use db values instead of from bundles
      5    .v7x      1.2.1.1     7/15/2006 8:02:05 PM   Michael Wisbauer
           Removed setting model for id type
      4    .v7x      1.2.1.0     6/23/2006 4:45:46 AM   Dinesh Gautam   CR
           4530: Fix for reason code

     Revision 1.24  2004/09/30 15:46:01  lzhao
     @scr 7224: remove setRequired for  postCodeField and extPostCodeField.

     Revision 1.23  2004/09/28 18:17:25  lzhao
     @scr 7224: set postalCode and mask for some countries.

     Revision 1.22  2004/09/23 17:46:05  rsachdeva
     @scr 7188 Capture Customer Journal

     Revision 1.21  2004/07/29 20:43:25  jdeleau
     @scr 6594 backout changes until further requirements are known

     Revision 1.19  2004/07/29 17:15:00  aachinfiev
     @scr 6597 - Fixed typo and alphabetical order problems in State/Region field

     Revision 1.18  2004/07/23 16:25:57  aachinfiev
     @scr 5008 - Added journalling of captured customer information

     Revision 1.17  2004/07/21 02:10:22  aschenk
     @scr 6041 - ID type is now prepopulated on the customer Info screen.

     Revision 1.16  2004/07/17 19:21:23  jdeleau
     @scr 5624 Make sure errors are focused on the beans, if an error is found
     during validation.

     Revision 1.15  2004/07/17 18:22:29  jdeleau
     @scr 5624 On validation error, when going back to the
     original screen, make the field with the error have focus.

     Revision 1.14  2004/07/13 17:32:46  khassen
     @scr 6212 - Fixed code list mappings.

     Revision 1.13  2004/07/13 14:15:59  awilliam
     @scr 6005 japan states not in alphabetical order

     Revision 1.12  2004/07/08 14:44:13  khassen
     @scr 6039 - updated validation methods/fields/functionality for the postal code.

     Revision 1.11  2004/07/02 19:42:51  khassen
     @scr 5752 - updated required fields.

     Revision 1.10  2004/07/02 19:08:54  khassen
     @scr 5642 - update the bean to reflect changes in customer info.

     Revision 1.9  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.8  2004/03/16 18:30:41  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.7  2004/03/16 17:15:22  build
     Forcing head revision

     Revision 1.6  2004/03/16 17:15:16  build
     Forcing head revision

     Revision 1.5  2004/02/27 19:20:49  khassen
     @scr 0 Capture Customer Info use-case
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This bean is used for displaying the Customer information screen based on the
 * data from the CaptureCustomerInfoBeanModel.
 * <P>
 * Developed for explicit use with the capture customer info use case.
 */
public class CaptureCustomerInfoBean extends ValidatingBean
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = -4936149167429887184L;

    /**
     * Revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/34 $";

    /** Fields and labels that contain customer data */
    protected JLabel customerIDLabel = null;
    protected JLabel customerNameLabel = null;
    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel postalCodeLabel = null;
    protected JLabel telephoneLabel = null;
    protected JLabel phoneTypeLabel = null;
    protected JLabel idTypeLabel = null;

    protected ValidatingTextField customerNameField = null;
    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField addressLine1Field = null;
    protected ConstrainedTextField addressLine2Field = null;
    protected ConstrainedTextField cityField = null;
    protected ValidatingFormattedTextField postalCodeField = null;
    protected ValidatingFormattedTextField telephoneField = null;
    protected ValidatingComboBox phoneTypeField = null;
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    protected ValidatingComboBox idTypeField;
    protected JPanel postalPanel = null;

    protected int initialCountryIndex;
    /** editable indicator */
    protected boolean editableFields = true;

    /**
     * Default Constructor. Calls {@link #initialize()}
     */
    public CaptureCustomerInfoBean()
    {
        initialize();
    }

    /**
     * Initialize this widget by configuring via the {@link UIFactoryIfc} and
     * initializing the fields, labels and layout.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();
    }

    /**
     *    Initialize the layout.
     */
    protected void initLayout()
    {
        postalPanel = uiFactory.createPostalPanel(postalCodeField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(11);
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
        orderableFields.add(new OrderableField(idTypeLabel, idTypeField));

        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        setLayout(new GridBagLayout());

        int xValue = 0;

        for (OrderableField orderedField : orderedFields)
        {
            UIUtilities.layoutComponent(this, orderedField.getLabel(), orderedField.getField(), 0, xValue, false);
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
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30", "30");
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "60", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "60", "30");
        cityField = uiFactory.createConstrainedField("cityField", "1", "30", "20");
        stateField = uiFactory.createValidatingComboBox("stateField", "false", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");
        phoneTypeField = uiFactory.createValidatingComboBox("phoneTypeField", "false", "10");
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
        idTypeField = uiFactory.createValidatingComboBox("idTypeField", "false", "15");
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
        customerNameLabel = uiFactory.createLabel("customerNameLabel", "customerNameLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        telephoneLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", "phoneTypeLabel", null, UI_LABEL);
        idTypeLabel = uiFactory.createLabel("idTypeLabel", "idTypeLabel", null, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        setJournalStringUpdates();
        if (beanModel instanceof CaptureCustomerInfoBeanModel)
        {
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) beanModel;

            if(model.isBusinessCustomer())
            {
                 model.setOrgName(customerNameField.getText());
                 model.setLastName(customerNameField.getText());
            }
            else
            {
                model.setFirstName(firstNameField.getText());
                model.setLastName(lastNameField.getText());
            }
            model.setPhoneType(phoneTypeField.getSelectedIndex());
            model.setPhoneNumber(telephoneField.getFieldValue(), model.getPhoneType());
            model.setAddressLine1(addressLine1Field.getText());
            model.setAddressLine2(addressLine2Field.getText());
            model.setPostalCode(postalCodeField.getFieldValue());
            model.setCity(cityField.getText());

            model.setCountryIndex(countryField.getSelectedIndex());
            model.setStateIndex(stateField.getSelectedIndex());
            model.setSelectedIDType(idTypeField.getSelectedIndex());
            model.setPostalCodeRequired(postalCodeField.isRequired());
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof CaptureCustomerInfoBeanModel)
        {
            // get model
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) beanModel;

            // set edit mode
            boolean editMode = model.getEditableFields();

            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            setupComponent(firstNameField, editMode, !model.isBusinessCustomer());
            firstNameField.setRequired(!model.isBusinessCustomer());
            setFieldRequired(firstNameField, !model.isBusinessCustomer());

            lastNameField.setText(model.getLastName());
            setupComponent(lastNameField, editMode, !model.isBusinessCustomer());
            lastNameField.setRequired(!model.isBusinessCustomer());
            setFieldRequired(lastNameField, !model.isBusinessCustomer());
            addressLine1Field.setText(model.getAddressLine1());

            addressLine2Field.setText(model.getAddressLine2());

            cityField.setText(model.getCity());

            if (model.getCountryIndex() < 0)
            {
                model.setCountryIndex(0);
            }
            if (model.getStateIndex() < 0)
            {
                model.setStateIndex(0);
            }
            //Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            String phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneField.setValidationRegexp(phoneValidationRegexp);

            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            postalCodeField.setRequired(true);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());

            postalCodeField.setValue(model.getPostalCode());

            // update the phone
            int index = model.getPhoneType();
            if (index < 0)
            {
                index = 0;
            }
            // update the phone type list
            setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
            setupComponent(phoneTypeField, editMode, (!model.isBusinessCustomer() && !model.isMailCheck()));
            for (int i = 0; i < phoneTypeField.getItemCount(); i++)
            {
                String phoneNumber = model.getPhoneNumber(i);
                if (!Util.isEmpty(phoneNumber))
                {
                    // make first available phone the default
                    index = i;
                    i = phoneTypeField.getItemCount();
                }
            }
            phoneTypeField.setFocusable(editMode);
            phoneTypeField.setEnabled(editMode);

            telephoneField.setValue(model.getPhoneNumber(index));
            telephoneField.setEditable(editMode);
            telephoneField.setEmptyAllowed(false);
            telephoneField.setRequired(true);
            setupComponent(telephoneField, editMode, true);
            phoneTypeField.setSelectedIndex(index);
            // these are business customer specific fields
            customerNameField.setText(model.getOrgName());
            setupComponent(customerNameField, editMode, model.isBusinessCustomer());

            customerNameField.setRequired(model.isBusinessCustomer());
            setFieldRequired(customerNameField, model.isBusinessCustomer() && editMode);

            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }
            setPostalFields();
            
            if ( model.isXchannelShipping() )
            {
                //set idTypeLabel and idTypeField not visible
                setupComponent(idTypeField, editMode, false);
            }
            else
            {
                Vector<String> idTypes = model.getIDTypes();
    
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(idTypes);
                idTypeField.setModel(listModel);
                if (model.getCurrentIDType() > -1)
                {
                    idTypeField.setSelectedIndex(idTypes.indexOf(model.getSelectedIDType()));
                }
                else
                {
                    idTypeField.setSelectedIndex(idTypes.indexOf(model.getDefaultIDType()));
                }
            }

            updatePhoneList(null);
        }
    }

    /**
     * Convenience method to populate a comboBox
     * 
     * @param data the data to be display in the combo box
     * @param field the actual combo box field receiving the data
     * @param selectedIndex the default selected value
     */
    @Override
    protected void setComboBoxModel(Vector data, ValidatingComboBox field, int selectedIndex)
    {
        if (data != null)
        {
            ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);

            field.setModel(model);
            field.setSelectedIndex(selectedIndex);
        }
    }

    /**
     * Convenience method to populate a comboBox
     * 
     * @param data the data to be display in the combobox
     * @param field the actual combobox field receiving the data
     * @param selectedIndex index the default selected value
     */
    @Override
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

        field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }

    /**
     * Deactivates this bean.
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

    /**
     * The framework calls this method just before display. Any listeners added
     * here should be removed in {@link #deactivate()}.
     */
    @Override
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        customerNameField.addFocusListener(this);
        countryField.addActionListener(this);
        phoneTypeField.addActionListener(this);
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
        CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)beanModel;
        int countryIndex = model.getCountryIndex();
        String countryCode = model.getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (selectedCountryIndex != countryIndex || model.getPhoneNumber().equals(""))
        {
            String phoneFormat = null;
            String phoneValidationRegexp = null;
            phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneField.setValidationRegexp(phoneValidationRegexp);
            model.setPhoneNumber("", model.getPhoneType());
        }
    }

    private void updatePostalFormat(int selectedCountryIndex)
    {
        int countryIndex = ((CaptureCustomerInfoBeanModel)beanModel).getCountryIndex();
        String countryCode = ((CaptureCustomerInfoBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if ( (selectedCountryIndex != countryIndex)
                  || (((CaptureCustomerInfoBeanModel)beanModel).getPostalCode().equals("")) )
          {
              String postalCodeFormat = null;
              String postalCodeValidationRegexp = null;
              postalCodeFormat = util.getPostalCodeFormat(countryCode);
              postalCodeField.setFormat(postalCodeFormat);
              postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
              postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
              ((CaptureCustomerInfoBeanModel)beanModel).setPostalCode("");
          }
    }

    /**
     * Updates shipping charge base on the shipping method selected
     * @param e the listSelection event
     */
    public void updatePhoneList(ActionEvent e)
    {
        int indx = phoneTypeField.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }

        ((CaptureCustomerInfoBeanModel) beanModel).setPhoneType(indx);
        telephoneField.setValue(((CaptureCustomerInfoBeanModel) beanModel).getPhoneNumber(indx));
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));
        customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
        idTypeLabel.setText(retrieveText("IdTypeLabel", idTypeLabel));
        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        phoneTypeLabel.setText(retrieveText("PhoneTypeLabel", phoneTypeLabel));
        telephoneLabel.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));

        // customer info
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);
        idTypeField.setLabel(idTypeLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        postalCodeField.setLabel(postalCodeLabel);

        // phone properties
        telephoneField.setLabel(telephoneLabel);
        phoneTypeField.setLabel(phoneTypeLabel);
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
        CountryModel countryModel = (CountryModel) beanModel;
        if ( countryModel.isPostalCodeRequired(countryField.getSelectedIndex()) )
        {
            postalCodeField.setVisible(true);
        }
        else
        {
            postalCodeField.setVisible(false);
        }

        telephoneField.setFormat(countryModel.getCountry(countryField.getSelectedIndex()).getPhoneFormat());
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
        StringBuilder strResult = new StringBuilder(250);
        strResult.append("Class: CustomerInfoBean (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
        if (beanModel != null)
        {
            strResult.append("\n\nbeanModel = ");
            strResult.append(beanModel);
        }
        else
        {
            strResult.append("\nbeanModel = null\n");
        }

        if (firstNameField != null)
        {
            strResult.append("\nfirstNameField text = ");
            strResult.append(firstNameField.getText());
            strResult.append(", min length = ");
            strResult.append(firstNameField.getMinLength());
            strResult.append(", max length = ");
            strResult.append(firstNameField.getMaxLength());
        }
        else
        {
            strResult.append("\nfirstNameField = null\n");
        }

        if (lastNameField != null)
        {
            strResult.append("\nlastNameField text = ");
            strResult.append(lastNameField.getText());
            strResult.append(", min length = ");
            strResult.append(lastNameField.getMinLength());
            strResult.append(", max length = ");
            strResult.append(lastNameField.getMaxLength());
        }
        else
        {
            strResult.append("\nlastNameField = null\n");
        }

        if (addressLine1Field != null)
        {
            strResult.append("\naddressLine1Field text = ");
            strResult.append(addressLine1Field.getText());
            strResult.append(", min length = ");
            strResult.append(addressLine1Field.getMinLength());
            strResult.append(", max length = ");
            strResult.append(addressLine1Field.getMaxLength());
        }
        else
        {
            strResult.append("\naddressLine1Field = null\n");
        }

        if (addressLine2Field != null)
        {
            strResult.append("\naddressLine2Field text = ");
            strResult.append(addressLine2Field.getText());
            strResult.append(", min length = ");
            strResult.append(addressLine2Field.getMinLength());
            strResult.append(", max length = ");
            strResult.append(addressLine2Field.getMaxLength());
        }
        else
        {
            strResult.append("\naddressLine2Field = null\n");
        }

        if (cityField != null)
        {
            strResult.append("\ncityField text = ");
            strResult.append(cityField.getText());
            strResult.append(", min length = ");
            strResult.append(cityField.getMinLength());
            strResult.append(", max length = ");
            strResult.append(cityField.getMaxLength());
        }
        else
        {
            strResult.append("\ncityField = null\n");
        }

        if (postalCodeField != null)
        {
            strResult.append("\npostalCodeField text = ");
            strResult.append(postalCodeField.getText());
        }
        else
        {
            strResult.append("\npostalCodeField = null\n");
        }

        strResult.append("\neditableFields =").append(editableFields).append("\n");

        // pass back result
        return strResult.toString();
    }

    /**
     * Tests each data field to determine if user has entered or updated data.
     * If data has changed then construct the journalString with the old and new
     * values for this field.
     */
    protected void setJournalStringUpdates()
    {
        if (beanModel instanceof CaptureCustomerInfoBeanModel)
        {
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) beanModel;
            // convert the telephoneField to String for comparison
            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
            phone.parseString(telephoneField.getFieldValue());
            String telephoneFieldPhoneNumber = new String(phone.getPhoneNumber());
            String modelPhoneNumber = model.getPhoneNumber();
            Object[] dataArgs = new Object[2];
            if (model.isBusinessCustomer())
            {
                if (model.getOrgName().compareTo(customerNameField.getText()) != 0)
                {
                  dataArgs = new Object[]{model.getOrgName()};
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

                if (model.getFirstName().compareTo(firstNameField.getText()) != 0)
                {
                    dataArgs[0] = model.getFirstName();
                    StringBuffer journalString = new StringBuffer();
                    journalString.append(model.getJournalString()).append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.OLD_FIRST_NAME_LABEL, dataArgs));
                    dataArgs[0] = firstNameField.getText();
                    journalString.append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.NEW_FIRST_NAME_LABEL, dataArgs));
                    model.setJournalString(journalString.toString());
                }
                if (model.getLastName().compareTo(lastNameField.getText()) != 0)
                {
                    dataArgs[0] = model.getLastName();
                    StringBuffer journalString = new StringBuffer();
                    journalString.append(model.getJournalString()).append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.OLD_LAST_NAME_LABEL, dataArgs));
                    dataArgs[0] = lastNameField.getText();
                    journalString.append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.NEW_LAST_NAME_LABEL, dataArgs));
                    model.setJournalString(journalString.toString());
                }
            }
            if (model.getAddressLine1() != null && model.getAddressLine1().compareTo(addressLine1Field.getText()) != 0)
            {
                dataArgs[0] = model.getAddressLine1();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ADDRESS_LINE_1_LABEL, dataArgs));
                dataArgs[0] = addressLine1Field.getText();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ADDRESS_LINE_1_LABEL, dataArgs));
                model.setJournalString(
                        journalString.toString());
            }
            if (model.getAddressLine2() != null && model.getAddressLine2().compareTo(addressLine2Field.getText()) != 0)
            {
                dataArgs[0] = model.getAddressLine2();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ADDRESS_LINE_2_LABEL, dataArgs));
                dataArgs[0] = addressLine2Field.getText();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ADDRESS_LINE_2_LABEL, dataArgs));
                model.setJournalString(
                        journalString.toString());
            }
            if ((model.getCity() != null && model.getCity().compareTo(cityField.getText()) != 0) 
                    || (cityField.getText().length() > 0))
            {
                dataArgs[0] = model.getCity();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_CITY_LABEL, dataArgs));
                dataArgs[0] = cityField.getText();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_CITY_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
            }
            if ((model.getPostalCode() != null && model.getPostalCode().compareTo(postalCodeField.getFieldValue()) != 0) 
                    || (postalCodeField.getFieldValue().length() > 0))
            {
                dataArgs[0] = model.getPostalCode();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_POSTAL_CODE_LABEL, dataArgs));
                dataArgs[0] = postalCodeField.getFieldValue();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_POSTAL_CODE_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString()) ;
            }

            if (modelPhoneNumber != null && modelPhoneNumber.compareTo(telephoneFieldPhoneNumber) != 0)
            {
                dataArgs[0] = modelPhoneNumber;
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_PHONE_NUMBER_LABEL, dataArgs));
                dataArgs[0] = telephoneFieldPhoneNumber;
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_PHONE_NUMBER_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
            }
            if (model.getPhoneType() != phoneTypeField.getSelectedIndex())
            {
                dataArgs[0] = model.getPhoneTypes()[model.getPhoneType()];
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_PHONE_TYPE_LABEL, dataArgs));
                dataArgs[0] = phoneTypeField.getSelectedItem();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_PHONE_TYPE_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
            }
            if (model.getStateIndex() != stateField.getSelectedIndex())
            {
                dataArgs[0] = model.getState();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_STATE_LABEL, dataArgs));
                dataArgs[0] = stateField.getSelectedItem();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_STATE_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
            }
            if (model.getCountryIndex() != countryField.getSelectedIndex())
            {
                dataArgs[0] = model.getCountry();
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_COUNTRY_LABEL, dataArgs));
                dataArgs[0] = countryField.getSelectedItem();
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_COUNTRY_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
            }

            if (model.getCurrentIDType() != idTypeField.getSelectedIndex())
            {
                String modelIDType = model.getIDType(model.getCurrentIDType());
                String fieldIDType = model.getIDType(idTypeField.getSelectedIndex());
                dataArgs[0] = modelIDType;
                StringBuffer journalString = new StringBuffer();
                journalString
                        .append(model.getJournalString())
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_ID_TYPE_LABEL, dataArgs));
                dataArgs[0] = fieldIDType;
                journalString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_ID_TYPE_LABEL, dataArgs));

                model.setJournalString(
                        journalString.toString());
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

        CaptureCustomerInfoBean bean = new CaptureCustomerInfoBean();

        bean.telephoneField.setValue("4043865851");
        System.out.println("1: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("4(512)555-1212");
        System.out.println("2: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("(512)555-1212");
        System.out.println("3: " + bean.telephoneField.getFieldValue());

        UIUtilities.doBeanTest(bean);
    }
}

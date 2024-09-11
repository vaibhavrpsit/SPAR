/* ===========================================================================
* Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CaptureIRSCustomerBean.java /main/25 2012/05/08 12:57:37 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   05/08/12 - Fixed NPE issue in Taxpayer ID field (Budb ID:
 *                         14021736)
 *    vtemker   04/04/12 - Added create method for NumericByteTextField
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    acadar    04/22/09 - translate date/time labels
 *    ohorne    03/17/09 - changed min len of first and last name to 1
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    mkochumm  02/23/09 - remove hard coded date label
 *    mahising  02/21/09 - Fixed birthdate field issue for PATCustomer
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *    6    I18N_P2    1.3.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    5    I18N_P2    1.3.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    4    360Commerce 1.3         10/10/2007 1:02:52 PM  Anda D. Cadar
 *         changes to not allow double byte chars
 *    3    360Commerce 1.2         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *         changes to not allow double bytes chars in some cases
 *    2    360Commerce 1.1         5/21/2007 1:43:39 PM   Mathews Kochummen fix
 *          label
 *    1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;

/**
 * This bean is used for displaying the IRS Customer information screen based on
 * the data from the CaptureIRSCustomerBeanModel.
 * <P>
 * Developed for explicit use with the Patriot Act use case.
 * 
 * @version $Revision: /main/25 $
 */
public class CaptureIRSCustomerBean extends ValidatingBean
{
    private static final long serialVersionUID = -4516917839656698951L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /main/25 $";

    //Fields and labels that contain customer data
    protected JLabel firstNameLabel = null;
    protected JLabel middleInitialLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel dateOfBirthLabel = null;
    protected JLabel taxpayerIDLabel = null;
    protected JLabel occupationLabel = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel postalCodeLabel = null;

    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField middleInitialField = null;
    protected ConstrainedTextField lastNameField = null;
    protected EYSDateField dateOfBirthField = null;
    protected NumericByteTextField taxpayerIDField = null;
    protected ConstrainedTextField occupationField = null;
    protected ConstrainedTextField addressLine1Field = null;
    protected ConstrainedTextField addressLine2Field = null;
    protected ConstrainedTextField cityField = null;
    protected ValidatingFormattedTextField postalCodeField = null;
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    protected JPanel postalPanel = null;

    protected int initialCountryIndex;

    /**
     * Default Constructor.
     */
    public CaptureIRSCustomerBean()
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
        postalPanel = uiFactory.createPostalPanel(postalCodeField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(12);
        orderableFields.add(new OrderableField(firstNameLabel, firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(middleInitialLabel, middleInitialField, AddressField.MIDDLE_NAME));
        orderableFields.add(new OrderableField(lastNameLabel, lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(dateOfBirthLabel, dateOfBirthField));
        orderableFields.add(new OrderableField(taxpayerIDLabel, taxpayerIDField));
        orderableFields.add(new OrderableField(occupationLabel, occupationField));
        orderableFields.add(new OrderableField(addressLine1Label, addressLine1Field, AddressField.ADDRESS_LINE_1));
        orderableFields.add(new OrderableField(addressLine2Label, addressLine2Field, AddressField.ADDRESS_LINE_2));
        orderableFields.add(new OrderableField(cityLabel, cityField, AddressField.CITY));
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(stateLabel, stateField, AddressField.STATE));
        orderableFields.add(new OrderableField(postalCodeLabel, postalPanel, AddressField.POSTAL_CODE));

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
        middleInitialField.setLabel(middleInitialLabel);
        dateOfBirthField.setLabel(dateOfBirthLabel);
        taxpayerIDField.setLabel(taxpayerIDLabel);
        occupationField.setLabel(occupationLabel);
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "20");
        middleInitialField = uiFactory.createConstrainedField("middleInitialField", "1", "1");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        dateOfBirthField = uiFactory.createEYSDateField("dateOfBirthField");
        taxpayerIDField = uiFactory.createNumericByteTextField("taxpayerIDField", "9", "20", "20");
        occupationField = uiFactory.createConstrainedField("occupationField", "2", "30", "30");
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "2", "60", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "60", "30");
        cityField = uiFactory.createConstrainedField("cityField", "2", "30", "20");
        stateField = uiFactory.createValidatingComboBox("stateField", "false", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");
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
        middleInitialLabel = uiFactory.createLabel("middleInitialLabel", "middleInitialLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        dateOfBirthLabel = uiFactory.createLabel("dateOfBirthLabel", "dateOfBirthLabel", null, UI_LABEL);
        taxpayerIDLabel = uiFactory.createLabel("taxpayerIDLabel", "taxpayerIDLabel", null, UI_LABEL);
        occupationLabel = uiFactory.createLabel("occupationLabel", "occupationLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        EncipheredDataIfc taxPayerID=null;
        if (beanModel instanceof CaptureIRSCustomerBeanModel)
        {
            CaptureIRSCustomerBeanModel model = (CaptureIRSCustomerBeanModel) beanModel;

            model.setFirstName(firstNameField.getText());
            model.setLastName(lastNameField.getText());
            model.setMiddleInitial(middleInitialField.getText());
            model.setDateOfBirth(dateOfBirthField.getDate());
            try
            {
                taxPayerID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                        taxpayerIDField.getTextBytes());
                model.setTaxPayerID(taxPayerID);
            }
            catch (EncryptionServiceException e)
            {
                logger.error("Could not encrypt text" + e.getLocalizedMessage());
            }
            model.setOccupation(occupationField.getText());
            model.setAddressLine1(addressLine1Field.getText());
            model.setAddressLine2(addressLine2Field.getText());
            model.setPostalCode(postalCodeField.getFieldValue());
            model.setCity(cityField.getText());
            model.setCountryIndex(countryField.getSelectedIndex());
            model.setStateIndex(stateField.getSelectedIndex());
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof CaptureIRSCustomerBeanModel)
        {
            // get model
            CaptureIRSCustomerBeanModel model = (CaptureIRSCustomerBeanModel) beanModel;

            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            lastNameField.setText(model.getLastName());
            middleInitialField.setText(model.getMiddleInitial());

            dateOfBirthField.setDate(model.getDateOfBirth());
            if (model.getTaxPayerID() != null)
            {
                byte[] taxPayerID = null;
                try
                {
                    taxPayerID = model.getTaxPayerID().getDecryptedNumber();
                    taxpayerIDField.clearTextBytes();
                    taxpayerIDField.setTextBytes(taxPayerID);
                }
                finally
                {
                    Util.flushByteArray(taxPayerID);
                }
                
            }
            else
            {
                taxpayerIDField.setTextBytes(new byte[0]);
            }

            occupationField.setText(model.getOccupation());
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

            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());

            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            postalCodeField.setValue(model.getPostalCode());

            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }

            setPostalFields();
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
        countryField.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        countryField.removeActionListener(this);
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
            updatePostalFormat(selectedCountryIndex);
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    private void updatePostalFormat(int selectedCountryIndex)
    {
    	int countryIndex = ((CaptureIRSCustomerBeanModel)beanModel).getCountryIndex();
    	String countryCode = ((CaptureIRSCustomerBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
    	UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
    	if ( (selectedCountryIndex != countryIndex)
    			|| (((CaptureIRSCustomerBeanModel)beanModel).getPostalCode().equals("")) )
    	{
    		String postalCodeFormat = null;
    		String postalCodeValidationRegexp = null;
    		postalCodeFormat = util.getPostalCodeFormat(countryCode);
    		postalCodeField.setFormat(postalCodeFormat);
    		postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
    		postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
    		((CaptureIRSCustomerBeanModel)beanModel).setPostalCode("");
    	}
    }

    /**
     *  Update property fields.
     */
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        middleInitialLabel.setText(retrieveText("MiddleInitialLabel", middleInitialLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        dateOfBirthLabel.setText(retrieveText("DateOfBirth", dateOfBirthLabel));
        String translatedLabel = getTranslatedDatePattern();
        dateOfBirthLabel.setText(LocaleUtilities.formatComplexMessage(dateOfBirthLabel.getText(), translatedLabel));

        taxpayerIDLabel.setText(retrieveText("TaxPayerIDLabel", taxpayerIDLabel));
        occupationLabel.setText(retrieveText("OccupationLabel", occupationLabel));

        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));

        // customer info
        firstNameField.setLabel(firstNameLabel);
        middleInitialField.setLabel(middleInitialLabel);
        lastNameField.setLabel(lastNameLabel);
        dateOfBirthField.setLabel(dateOfBirthLabel);
        taxpayerIDField.setLabel(taxpayerIDLabel);
        occupationField.setLabel(occupationLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        postalCodeField.setLabel(postalCodeLabel);
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
        // select 1st element of the list for the current country
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
    }

    /**
     * Parse string to ignoring empty spaces NOTE: the assumption that spaces
     * are optional characters
     * 
     * @param value
     * @return minimum length
     */
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
        StringBuilder strResult = new StringBuilder("Class: CaptureIRSCustomerBean (Revision ")
            .append(getRevisionNumber()).append(") @").append(Integer.toString(hashCode()));
        if (beanModel != null)
        {
            strResult.append("\n\nbeanModel = ").append(beanModel.toString());
        }
        else
        {
            strResult.append("\nbeanModel = null\n");
        }

        if (firstNameField != null)
        {
            strResult.append("\nfirstNameField text = ").append(firstNameField.getText());
            strResult.append(", min length = ").append(Integer.toString(firstNameField.getMinLength()));
            strResult.append(", max length = ").append(Integer.toString(firstNameField.getMaxLength()));
        }
        else
        {
            strResult.append("\nfirstNameField = null\n");
        }

        if (lastNameField != null)
        {
            strResult.append("\nlastNameField text = ").append(lastNameField.getText());
            strResult.append(", min length = ").append(Integer.toString(lastNameField.getMinLength()));
            strResult.append(", max length = ").append(Integer.toString(lastNameField.getMaxLength()));
        }
        else
        {
            strResult.append("\nlastNameField = null\n");
        }

        if (addressLine1Field != null)
        {
            strResult.append("\naddressLine1Field text = ").append(addressLine1Field.getText());
            strResult.append(", min length = ").append(Integer.toString(addressLine1Field.getMinLength()));
            strResult.append(", max length = ").append(Integer.toString(addressLine1Field.getMaxLength()));
        }
        else
        {
            strResult.append("\naddressLine1Field = null\n");
        }

        if (addressLine2Field != null)
        {
            strResult.append("\naddressLine2Field text = ").append(addressLine2Field.getText());
            strResult.append(", min length = ").append(Integer.toString(addressLine2Field.getMinLength()));
            strResult.append(", max length = ").append(Integer.toString(addressLine2Field.getMaxLength()));
        }
        else
        {
            strResult.append("\naddressLine2Field = null\n");
        }

        if (cityField != null)
        {
            strResult.append("\ncityField text = ").append(cityField.getText());
            strResult.append(", min length = ").append(Integer.toString(cityField.getMinLength()));
            strResult.append(", max length = ").append(Integer.toString(cityField.getMaxLength()));
        }
        else
        {
            strResult.append("\ncityField = null\n");
        }

        if (postalCodeField != null)
        {
            strResult.append("\npostalCodeField text = ").append(postalCodeField.getText());
        }
        else
        {
            strResult.append("\npostalCodeField = null\n");
        }

        // pass back result
        return (strResult.toString());
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
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CaptureIRSCustomerBean bean = new CaptureIRSCustomerBean();

        UIUtilities.doBeanTest(bean);
    }
}

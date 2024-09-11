/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditCustomerBean.java /main/33 2013/10/15 14:16:20 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    mchellap  04/24/13 - Splitting DOB to birth date and birth year
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    hyin      10/06/11 - use correct property name
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    acadar    04/22/09 - translate date/time labels
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    mahising  02/27/09 - Fixed message issue for credit cust info screen
 *    mkochumm  02/23/09 - remove hard coded date label
 *    mkochumm  02/22/09 - set country
 *    kulu      01/16/09 - Fix the bug that customer can not process instant
 *                         credit tender a second time - client freezes
 *    kulu      01/15/09 - Fix the bug that customer can not process instant
 *                         credit tender a second time - client freezes
 *    kulu      01/15/09 - Fix the bug that customer can not process instant
 *                         credit tender a second time - client freezes
 *    mkochumm  12/09/08 - fix screen layout
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         6/2/2008 5:23:40 PM    Deepti Sharma
 *       CR-31672. Corrected spelling and changed width.
 *  6    360Commerce 1.5         5/29/2008 4:07:26 PM   Deepti Sharma
 *       CR-31672 changes for instant credit enrollment. Code reviewed by Alan
 *        Sinton
 *  7    I18N_P2    1.4.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set max
 *       length of constraied text field.
 *  6    I18N_P2    1.4.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *       29826 - Setting the size of the combo boxes. This change was
 *       necessary because the width of the combo boxes used to grow according
 *       to the length of the longest content. By setting the size, we allow
 *       the width of the combo box to be set independently from the width of
 *       the dropdown menu.
 *  5    360Commerce 1.4         7/19/2007 3:23:03 PM   Mathews Kochummen use
 *       locale formatted date
 *  4    360Commerce 1.3         5/21/2007 1:52:13 PM   Mathews Kochummen fix
 *       label
 *  3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
 * $
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;

import org.apache.log4j.Logger;

/**
 * This bean captures customer information for instant credit enroll.
 */
public class InstantCreditCustomerBean extends ValidatingBean
{
    private static final long serialVersionUID = 3482142744378866590L;

    /** Store instance of logger here **/
    protected static final Logger logger = Logger.getLogger(InstantCreditCustomerBean.class);

    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel streetNumber1Label = null;
    protected JLabel streetNumber2Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel zipCodeLabel = null;
    protected JLabel birthdateLabel = null;
    protected JLabel birthyearLabel = null;


    protected JLabel governmentIdLabel = null;
    protected JLabel homeTelLabel = null;
    protected JLabel yearlyIncomeLabel = null;
    protected JLabel appReferenceNumberLabel = null;
    protected JLabel appSignedLabel = null;

    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField street1Field = null;
    protected ConstrainedTextField street2Field = null;
    protected ConstrainedTextField cityField = null;
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    protected ValidatingFormattedTextField zipCodeField = null;
    protected EYSDateField birthdateField = null;
    protected NumericTextField birthyearField = null;
    protected GovernmentIdField governmentIdField = null;
    protected ValidatingFormattedTextField homeTelField = null;
    protected NumericTextField yearlyIncomeField = null;
    protected ValidatingComboBox appSignedField = null;
    protected ConstrainedTextField appReferenceNumberField = null;

    // The bean model
    protected InstantCreditCustomerBeanModel beanModel = null;

    /**
     * Constructor
     */
    public InstantCreditCustomerBean ()
    {
    }

    /**
     * Configures the class.
     */
    public void configure()
    {
        setName("InstantCreditCustomerBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize the components in this bean.
     */
    protected void initComponents()
    {
        firstNameLabel = uiFactory.createLabel("firstNameLabel", "firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        streetNumber1Label = uiFactory.createLabel("streetNumber1Label", "streetNumber1Label", null, UI_LABEL);
        streetNumber2Label = uiFactory.createLabel("streetNumber2Label", "streetNumber2Label", null, UI_LABEL);
        cityLabel = uiFactory.createLabel("cityLabel", "cityLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        zipCodeLabel = uiFactory.createLabel("zipCodeLabel", "zipCodeLabel", null, UI_LABEL);
        birthdateLabel = uiFactory.createLabel("Birthday", "Birthday ({0}):", null, UI_LABEL);
        birthyearLabel = uiFactory.createLabel("Birth Year", "Birth Year (YYYY):", null, UI_LABEL);
        governmentIdLabel = uiFactory.createLabel("governmentIdLabel", "governmentIdLabel", null, UI_LABEL);
        homeTelLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        yearlyIncomeLabel = uiFactory.createLabel("yearlyIncomeLabel", "yearlyIncomeLabel", null, UI_LABEL);
        appSignedLabel = uiFactory.createLabel("appSignedLabel", "appSignedLabel", null, UI_LABEL);
        appReferenceNumberLabel = uiFactory.createLabel("appReferenceNumberLabel", "appReferenceNumberLabel", null, UI_LABEL);

        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        firstNameField.setErrorMessage("First Name");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        lastNameField.setErrorMessage("Last Name");
        street1Field = uiFactory.createConstrainedField("street1Field","1", "60", "30");
        street1Field.setErrorMessage("Street1");
        street2Field = uiFactory.createConstrainedField("street2Field","1", "60", "30");
        street2Field.setErrorMessage("Street2");
        cityField = uiFactory.createConstrainedField("cityField", "1", "30", "20");
        cityField.setErrorMessage("City");
        stateField = uiFactory.createValidatingComboBox("stateField", "false", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        zipCodeField = uiFactory.createValidatingFormattedTextField("zipCodeField", "", true, "20", "15");
        zipCodeField.setErrorMessage("Zip Code");
        
        birthdateField = uiFactory.createEYSDateField("birthdateField");
        birthdateField.setFormat(DateDocument.MONTH_DAY);
        birthdateField.setColumns(15);
        birthdateField.setErrorMessage("Birth Date");
        birthyearField = uiFactory.createNumericField("birthyearField", "4", "4");
        birthyearField.setColumns(15);
        birthyearField.setErrorMessage("Birth Year");
        
        governmentIdField = new GovernmentIdField();
        governmentIdField.setName("governmentIdField");
        governmentIdField.setColumns(15);
        governmentIdField.setMinLength(9);
        governmentIdField.setMinimumSize(15);
        governmentIdField.setErrorMessage("Government Id FieldGovernmentIdField");
        homeTelField = uiFactory.createValidatingFormattedTextField("homeTelField", "", "30", "20");
        homeTelField.setErrorMessage("Home Telephone");
        yearlyIncomeField = uiFactory.createNumericField("yearlyIncomeField", "1", "8");
        yearlyIncomeField.setColumns(5);
        yearlyIncomeField.setRequired(true);
        yearlyIncomeField.setErrorMessage("Yearly Income");

        appReferenceNumberField = uiFactory.createAlphaNumericField("appReferenceNumberField", "1", "6");
        appReferenceNumberField.setColumns(12);
        appReferenceNumberField.setRequired(true);
        appReferenceNumberField.setErrorMessage("Application Ref. Number");

        appSignedField = uiFactory.createValidatingComboBox("appSignedField", "false", "2");
        appSignedField.setColumns(2);
    }

    /**
     * Create this bean's layout and layout the components.
     */
    protected void initLayout()
    {
        JPanel postalPanel =
            uiFactory.createPostalPanel(zipCodeField);

        JPanel panel1 = createPanel(yearlyIncomeField, appSignedLabel, appSignedField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(14);
        orderableFields.add(new OrderableField(firstNameLabel, firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(lastNameLabel, lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(streetNumber1Label, street1Field, AddressField.ADDRESS_LINE_1));
        orderableFields.add(new OrderableField(streetNumber2Label, street2Field, AddressField.ADDRESS_LINE_2));
        orderableFields.add(new OrderableField(cityLabel, cityField, AddressField.CITY));
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(stateLabel, stateField, AddressField.STATE));
        orderableFields.add(new OrderableField(zipCodeLabel, postalPanel, AddressField.POSTAL_CODE));
        orderableFields.add(new OrderableField(homeTelLabel, homeTelField, AddressField.TELEPHONE));
        orderableFields.add(new OrderableField(birthdateLabel, birthdateField));
        orderableFields.add(new OrderableField(birthyearLabel, birthyearField));
        orderableFields.add(new OrderableField(governmentIdLabel, governmentIdField));
        orderableFields.add(new OrderableField(yearlyIncomeLabel, panel1));
        orderableFields.add(new OrderableField(appReferenceNumberLabel, appReferenceNumberField));

        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        // build arrays to pass to layoutDataPanel
        JLabel[] labels = new JLabel[orderedFields.size()];
        JComponent[] components = new JComponent[orderedFields.size()];
        int i=0;
        for (OrderableField orderedField : orderedFields)
        {
            labels[i] = orderedField.getLabel();
            components[i] = orderedField.getField();
            i++;
        }

        UIUtilities.layoutDataPanel(this, labels, components);
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

        constraints.insets = new Insets(0, 20, 0, 0);
        panel.add(component2, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        panel.add(component3, constraints);

        return panel;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        countryField.addActionListener(this);
    }

    /*(non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
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
            int selectedCountryIndex = countryField.getSelectedIndex();
            updatePhoneFormat(selectedCountryIndex);
            updatePostalFormat(selectedCountryIndex);
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    private void updatePhoneFormat(int selectedCountryIndex)
    {
    	int countryIndex = beanModel.getCountryIndex();
    	String countryCode = beanModel.getCountry(selectedCountryIndex).getCountryCode();
    	UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
    	if (selectedCountryIndex != countryIndex || Util.isEmpty(beanModel.getHomePhone()))
    	{
    		String phoneFormat = null;
    		String phoneValidationRegexp = null;
    		phoneFormat = util.getPhoneFormat(countryCode);
    		homeTelField.setFormat(phoneFormat);
    		phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
    		homeTelField.setValidationRegexp(phoneValidationRegexp);
    		beanModel.setCountryIndex(countryField.getSelectedIndex());
    		beanModel.setHomePhone("");
    	}
    }

    private void updatePostalFormat(int selectedCountryIndex)
    {
    	int countryIndex = beanModel.getCountryIndex();
    	String countryCode = beanModel.getCountry(selectedCountryIndex).getCountryCode();
    	UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
    	if (selectedCountryIndex != countryIndex || Util.isEmpty(beanModel.getZipCode()) )
    	{
    		String postalCodeFormat = null;
    		String postalCodeValidationRegexp = null;
    		postalCodeFormat = util.getPostalCodeFormat(countryCode);
    		zipCodeField.setFormat(postalCodeFormat);
    		postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
    		zipCodeField.setValidationRegexp(postalCodeValidationRegexp);
    		beanModel.setCountryIndex(countryField.getSelectedIndex());
    		beanModel.setZipCode("");
    	}
    }

    /**
     * Updates the model associated with the current screen information.
     */
    @Override
    public void updateModel()
    {
        beanModel.setFirstName(firstNameField.getText());
        beanModel.setLastName(lastNameField.getText());
        beanModel.setStreet1(street1Field.getText());
        beanModel.setStreet2(street2Field.getText());
        beanModel.setCity(cityField.getText());
        beanModel.setZipCode(zipCodeField.getFieldValue());

        //    update the state and country combo boxes with the new list of states and countries
        beanModel.setCountryIndex(countryField.getSelectedIndex());
        beanModel.setStateIndex(stateField.getSelectedIndex());

        EYSDate DOBDate = null;
        if (birthdateField.isValid())
        {
            if (!Util.isEmpty(birthyearField.getText()))
            {
                int year = (int)(birthyearField.getLongValue());
                DateDocument bdayDoc = (DateDocument)(birthdateField.getDocument());
                bdayDoc.setSeparateYear(year);
            }
            beanModel.setBirthdate(birthdateField.getDate());
            DOBDate = birthdateField.getDate();
        }

        if (!Util.isEmpty(birthyearField.getText()))
        {
            beanModel.setBirthYear(birthyearField.getLongValue());
            if (DOBDate != null)
            {
                DOBDate.setYear(Integer.parseInt(birthyearField.getText()));
            }
        }
        beanModel.setDateOfBirth(DOBDate);

        EncipheredDataIfc governmentId = null;
        byte[] bytes = null;
        try
        {
            bytes = governmentIdField.getGovernmentIdNumber().getBytes();
            governmentId = FoundationObjectFactory.getFactory().createEncipheredDataInstance(bytes);
        }
        catch (EncryptionServiceException e)
        {
            logger.warn("Could not encrypt government ID field");
        }
        finally
        {
            Util.flushByteArray(bytes);
        }
        beanModel.setGovernmentId(governmentId);
        beanModel.setCountryIndex(countryField.getSelectedIndex());
        beanModel.setHomePhone(homeTelField.getFieldValue());
        beanModel.setYearlyIncome(yearlyIncomeField.getText());
        if(appSignedField.getSelectedItem() != null)
        {
            int selectedOption = appSignedField.getSelectedIndex();
            beanModel.setAppSigned(String.valueOf(selectedOption));
        }
        beanModel.setAppReference(appReferenceNumberField.getText());
    }

    /**
     * Sets the model property
     * @param model UIModelIfc
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set InstantCreditCustomerBeanModel model to null");
        }

        if (model instanceof InstantCreditCustomerBeanModel)
        {
            beanModel = (InstantCreditCustomerBeanModel) model;
            updateBean();
        }
    }

    /**
     * Updates the current screen information with the associated model.
     */
    @Override
    public void updateBean()
    {
        DefaultComboBoxModel listModel = new DefaultComboBoxModel();
        listModel.add(0, retrieveText("No"));
        listModel.add(1, retrieveText("Yes"));
        appSignedField.setModel(listModel);
        appSignedField.setSelectedIndex(0);

        if(beanModel.isFirstRun())
        {
            firstNameField.setText(beanModel.getFirstName());
            lastNameField.setText(beanModel.getLastName());
            if(beanModel.getStreet1() != null)
            {
                street1Field.setText(beanModel.getStreet1());
            }
            else
            {
                street1Field.setText("");
            }

            if(beanModel.getStreet2() != null)
            {
                street2Field.setText(beanModel.getStreet2());
            }
            else
            {
                street2Field.setText("");
            }

            cityField.setText(beanModel.getCity());

           
            if (beanModel.getBirthdate() != null && beanModel.getBirthdate().isValid())
            {

                birthdateField.setDate(beanModel.getBirthdate());
            }
            else
            {
                birthdateField.setText("");
            }

            if (beanModel.isBirthYearValid())
            {
                birthyearField.setLongValue(beanModel.getBirthYear());
            }
            else
            {
                birthyearField.setText("");
            }

            if(beanModel.getGovernmentId() == null)
            {
                governmentIdField.setText("");
            }
            else
            {
                governmentIdField.setText(new String(beanModel.getGovernmentId().getDecryptedNumber()));
            }

            //Retrieve countries and update combo box
            setComboBoxModel(beanModel.getCountryNames(), countryField, beanModel.getCountryIndex());

            int countryIndex = beanModel.getCountryIndex();
            String countryCode = beanModel.getCountry(countryIndex).getCountryCode();
            UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String phoneFormat = util.getPhoneFormat(countryCode);
            homeTelField.setFormat(phoneFormat);
            String phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            homeTelField.setValidationRegexp(phoneValidationRegexp);

            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            zipCodeField.setFormat(postalCodeFormat);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            zipCodeField.setValidationRegexp(postalCodeValidationRegexp);
            if(beanModel.getHomePhone() != null)
            {
                homeTelField.setValue(beanModel.getHomePhone());
            }
            else
            {
                homeTelField.setValue("");
            }

            // update the state combo box with the new list of states
            setComboBoxModel(beanModel.getStateNames(), stateField, beanModel.getStateIndex());

            zipCodeField.setValue(beanModel.getZipCode());
            if(beanModel.getYearlyIncome() != null)
            {
            	yearlyIncomeField.setText(beanModel.getYearlyIncome());
            }
            else
            {
            	yearlyIncomeField.setText("");
            }
            if(beanModel.getAppReference() != null)
            {
            	appReferenceNumberField.setText(beanModel.getAppReference());
            }
            else
            {
            	appReferenceNumberField.setText("");
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
     * Sets the field to display with proper focus
     * 
     * @param visible flag
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            if (firstNameField.getText() == null || firstNameField.getText().equals(""))
            {
                firstNameField.requestFocus();
            }
            else if(lastNameField.getText() == null || lastNameField.getText().equals(""))
            {
                lastNameField.requestFocus();
            }
            else
            {
                street1Field.requestFocus();
            }
        }
    }

    /**
     * Update states as country selection changes
     */
    public void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if ( countryIndx < 0)
        {
             countryIndx = 0;
        }
        String[] stateList = ((CountryModel)beanModel).getStateNames(countryIndx);


        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        stateField.setModel(stateModel);
        //select 1st element of the list for the current country
        stateField.setSelectedIndex(0);
    }

    /**
     * Gets the min length of postal field
     * 
     * @param value Postal Format
     * @return int minLen
     */
    protected int getMinLength(String value)
     {
        int minLen = 0;
        for (int i=0; i < value.length(); i++)
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

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        streetNumber1Label.setText(retrieveText("AddressLine1Label", streetNumber1Label));
        streetNumber2Label.setText(retrieveText("AddressLine2Label", streetNumber2Label));

        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));

        zipCodeLabel.setText(retrieveText("PostalCodeLabel", zipCodeLabel));

        DateDocument doc = (DateDocument)birthdateField.getDocument();
        String dobLabel = retrieveText("BirthdateLabel", birthdateLabel);
        String yearLabel = retrieveText("BirthYearLabel", birthyearLabel);

        // Retrieve the localized pattern for the Full year
        SimpleDateFormat dateFormat = DomainGateway.getSimpleDateFormat(getDefaultLocale(),
                LocaleConstantsIfc.DEFAULT_YEAR_FORMAT);
        String translatedLabel = getTranslatedDatePattern(dateFormat.toPattern());

        birthyearLabel.setText(LocaleUtilities.formatComplexMessage(yearLabel, translatedLabel));

        // Retrieve bundle text for month/day label
        String monthDayPatternChars = ((SimpleDateFormat)(doc.getDateFormat())).toPattern();

        translatedLabel = getTranslatedDatePattern(monthDayPatternChars);
        birthdateLabel.setText(LocaleUtilities.formatComplexMessage(dobLabel, translatedLabel));

        governmentIdLabel.setText(retrieveText("GovernmentIdNumberLabel", governmentIdLabel));
        homeTelLabel.setText(retrieveText("TelephoneNumberLabel", homeTelLabel));
        yearlyIncomeLabel.setText(retrieveText("YearlyIncome", yearlyIncomeLabel));
        appSignedLabel.setText(retrieveText("AppSigned", appSignedLabel));
        appReferenceNumberLabel.setText(retrieveText("ReferenceNumberLabel", appReferenceNumberLabel));

        // customer info
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);

        // address properties
        street1Field.setLabel(streetNumber1Label);
        street2Field.setLabel(streetNumber2Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        zipCodeField.setLabel(zipCodeLabel);
        governmentIdField.setLabel(governmentIdLabel);
        homeTelField.setLabel(homeTelLabel);

        yearlyIncomeField.setLabel(yearlyIncomeLabel);
        appSignedField.setLabel(appSignedLabel);
        appReferenceNumberField.setLabel(appReferenceNumberLabel);
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc)field).getLabel().setVisible(isVisible);
        }

        field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: InstantCreditCustomerBeanModel (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        InstantCreditCustomerBean bean = new InstantCreditCustomerBean();

        UIUtilities.doBeanTest(bean);
    }
}

/*===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditInquiryCriteriaBean.java /main/3 2013/10/15 14:16:21 asinton Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     10/10/13 - removed references to social security number and
*                        replaced with locale agnostic government id
* tksharma    12/10/12 - commons-lang update 3.1
* cgreene     08/29/11 - set focus
* sgu         05/20/11 - refactor instant credit inquiry flow
* sgu         05/18/11 - add new class
* sgu         05/18/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This bean captures customer information for instant credit enroll.
 */
public class InstantCreditInquiryCriteriaBean extends ValidatingBean
{
    private static final long serialVersionUID = 3482142744378866590L;

    /** Store instance of logger here **/
    protected static final Logger logger = Logger.getLogger(InstantCreditCustomerBean.class);

    protected JLabel countryLabel = null;
    protected JLabel postalCodeLabel = null;
    protected JLabel governmentIdLabel = null;
    protected JLabel telephoneNumberLabel = null;
    protected JLabel referenceNumberLabel = null;

    protected ValidatingComboBox countryField = null;
    protected ValidatingFormattedTextField postalCodeField = null;
    protected GovernmentIdField governmentIdField = null;
    protected ValidatingFormattedTextField telephoneNumberField = null;
    protected ConstrainedTextField referenceNumberField = null;

    // The bean model
    protected InstantCreditInquiryCriteriaBeanModel beanModel = null;

    /**
     * Constructor
     */
    public InstantCreditInquiryCriteriaBean ()
    {
    }

    /**
     * Configures the class.
     */
    public void configure()
    {
        setName("InstantCreditInquiryCriteriaBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize the components in this bean.
     */
    protected void initComponents()
    {
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        governmentIdLabel = uiFactory.createLabel("governmentId", "governmentId", null, UI_LABEL);
        telephoneNumberLabel = uiFactory.createLabel("telephoneNumberLabel", "telephoneNumberLabel", null, UI_LABEL);
        referenceNumberLabel = uiFactory.createLabel("referenceNumberLabel", "referenceNumberLabel", null, UI_LABEL);

        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");
        postalCodeField.setErrorMessage("Postal Code");
        governmentIdField = new GovernmentIdField();
        governmentIdField.setName("governmentIdField");
        governmentIdField.setColumns(15);
        governmentIdField.setMinLength(9);
        governmentIdField.setMinimumSize(15);
        governmentIdField.setErrorMessage("Government ID Number");
        telephoneNumberField = uiFactory.createValidatingFormattedTextField("homeTelField", "", "30", "15");
        telephoneNumberField.setErrorMessage("Telephone Number");

        referenceNumberField = uiFactory.createAlphaNumericField("referenceNumberField", "1", "6");
        referenceNumberField.setColumns(15);
        referenceNumberField.setRequired(true);
        referenceNumberField.setErrorMessage("Reference Number");
    }

    /**
     * Create this bean's layout and layout the components.
     */
    protected void initLayout()
    {
        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(5);
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(referenceNumberLabel, referenceNumberField));
        orderableFields.add(new OrderableField(postalCodeLabel, postalCodeField, AddressField.POSTAL_CODE));
        orderableFields.add(new OrderableField(telephoneNumberLabel, telephoneNumberField, AddressField.TELEPHONE));
        orderableFields.add(new OrderableField(governmentIdLabel, governmentIdField));

        // build arrays to pass to layoutDataPanel
        JLabel[] labels = new JLabel[orderableFields.size()];
        JComponent[] components = new JComponent[orderableFields.size()];
        int i=0;
        for (OrderableField orderedField : orderableFields)
        {
            labels[i] = orderedField.getLabel();
            components[i] = orderedField.getField();
            i++;
        }

        UIUtilities.layoutDataPanel(this, labels, components);
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
            telephoneNumberField.setFormat(phoneFormat);
            phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneNumberField.setValidationRegexp(phoneValidationRegexp);
            beanModel.setCountryIndex(countryField.getSelectedIndex());
            beanModel.setHomePhone("");
        }
    }

    private void updatePostalFormat(int selectedCountryIndex)
    {
        int countryIndex = beanModel.getCountryIndex();
        String countryCode = beanModel.getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (selectedCountryIndex != countryIndex || Util.isEmpty(beanModel.getPostalCode()) )
        {
            String postalCodeFormat = null;
            String postalCodeValidationRegexp = null;
            postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            beanModel.setCountryIndex(countryField.getSelectedIndex());
            beanModel.setPostalCode("");
        }
    }

    /**
     * Updates the model associated with the current screen information.
     */
    @Override
    public void updateModel()
    {
        beanModel.setPostalCode(postalCodeField.getFieldValue());
        beanModel.setCountryIndex(countryField.getSelectedIndex());
        EncipheredDataIfc governmentId = null;
        try
        {
            governmentId = FoundationObjectFactory.getFactory().createEncipheredDataInstance(governmentIdField.getGovernmentIdNumber().getBytes());
        }
        catch (EncryptionServiceException e)
        {
            logger.warn("Could not encrypt government ID field");
            e.printStackTrace();
        }
        beanModel.setGovernmentId(governmentId);
        beanModel.setHomePhone(telephoneNumberField.getFieldValue());
        beanModel.setReferenceNumber(referenceNumberField.getText());
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
            throw new NullPointerException("Attempt to set InstantCreditInquiryCriteriaBeanModel model to null");
        }

        if (model instanceof InstantCreditInquiryCriteriaBeanModel)
        {
            beanModel = (InstantCreditInquiryCriteriaBeanModel) model;
            updateBean();
        }
    }

    /**
     * Updates the current screen information with the associated model.
     */
    @Override
    public void updateBean()
    {
        if (beanModel.isFirstRun())
        {
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
            telephoneNumberField.setFormat(phoneFormat);
            String phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneNumberField.setValidationRegexp(phoneValidationRegexp);

            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            if(beanModel.getHomePhone() != null)
            {
                telephoneNumberField.setValue(beanModel.getHomePhone());
            }
            else
            {
                telephoneNumberField.setValue("");
            }

            postalCodeField.setValue(beanModel.getPostalCode());

            if(beanModel.getReferenceNumber() != null)
            {
                referenceNumberField.setText(beanModel.getReferenceNumber());
            }
            else
            {
                referenceNumberField.setText("");
            }

            boolean visible = beanModel.isReferenceNumberSearch();
            referenceNumberLabel.setVisible(visible);
            referenceNumberField.setVisible(visible);
            setFieldRequired(referenceNumberField,visible);
        }
    }

    /**
     * Overridden to provide focus in country field.
     *
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible)
    {
        if (visible)
        {
            // if the postal code and telephone have already been entered,
            // then set the focus on the ssn field
            if (!StringUtils.isEmpty(postalCodeField.getFieldValue()) &&
                !StringUtils.isEmpty(telephoneNumberField.getFieldValue()))
            {
                governmentIdField.requestFocusInWindow();
            }
            else
            {
                countryField.requestFocusInWindow();
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
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        governmentIdLabel.setText(retrieveText("GovernmentIdNumberLabel", governmentIdLabel));
        telephoneNumberLabel.setText(retrieveText("TelephoneNumberLabel", telephoneNumberLabel));
        referenceNumberLabel.setText(retrieveText("ReferenceNumberLabel", referenceNumberLabel));

        countryField.setLabel(countryLabel);
        postalCodeField.setLabel(postalCodeLabel);
        governmentIdField.setLabel(governmentIdLabel);
        telephoneNumberField.setLabel(telephoneNumberLabel);
        referenceNumberField.setLabel(referenceNumberLabel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: InstantCreditInquiryCriteriaBean (Revision " +
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

        InstantCreditInquiryCriteriaBean bean = new InstantCreditInquiryCriteriaBean();

        UIUtilities.doBeanTest(bean);
    }
}
/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PersonalIDEntryBean.java /main/24 2012/03/29 15:26:23 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    ohorne    03/12/09 - Min length of firstName and lastName fields is now 1
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    mahising  02/27/09 - Fixed personal id issue for business customer
 *    mahising  02/17/09 - fixed personal id business customer issue
 *    mdecama   12/03/08 - Added firstName and lastName
 *
 * ===========================================================================
 * $Log:
 *   5    I18N_P2    1.2.1.1     1/7/2008 3:45:32 PM    Maisa De Camargo 29826
 *        - Setting the size of the combo boxes. This change was necessary
 *        because the width of the combo boxes used to grow according to the
 *        length of the longest content. By setting the size, we allow the
 *        width of the combo box to be set independently     from the width of
 *         the dropdown menu.
 *   4    I18N_P2    1.2.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *        alphanumerice fields for I18N purpose
 *   3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
 *
 *  Revision 1.6  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.5  2004/06/15 16:54:08  mweis
 *  @scr 5620 "ID State:" label appearing as "ID Country:" label
 *
 *  Revision 1.4  2004/05/24 18:40:11  mweis
 *  @scr 4898 Need to list State after Country for Returns' PERSONAL_ID
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:52:58   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 03 2003 10:26:06   jgs
 * Added code to support Driver's Licence validation.
 * Resolution for 1874: Add Driver's License Validation to Return Prompt for ID
 *
 *    Rev 1.4   Apr 10 2003 13:33:50   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Feb 20 2003 15:02:22   HDyer
 * Fixed typos.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Feb 18 2003 12:40:12   HDyer
 * Use the utility manager to get the localized text for the entry type.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Feb 05 2003 11:23:10   HDyer
 * Display the localized strings for ID types rather than the key/tag value.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Dec 16 2002 10:21:02   HDyer
 * Initial revision.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for PersonalID Entry Information
 *
 * @version $Revision: /main/24 $
 */
public class PersonalIDEntryBean extends ValidatingBean implements DocumentListener, ItemListener
{
    /**
     * Generated SerialVersion UID
     */
    private static final long serialVersionUID = 2428232797369286985L;

    // Revision number
    public static final String revisionNumber = "$Revision: /main/24 $";

    // Logger
    protected static final Logger logger = Logger.getLogger(PersonalIDEntryBean.class);

    // Constant field indices
    protected static final int FIRST_NAME = 0;
    protected static final int LAST_NAME = FIRST_NAME + 1;
    protected static final int BUSINESS_NAME = LAST_NAME + 1;
    protected static final int ID_NUMBER = BUSINESS_NAME + 1;
    protected static final int ID_TYPE = ID_NUMBER + 1;
    protected static final int ID_COUNTRY = ID_TYPE + 1;
    protected static final int ID_STATE = ID_COUNTRY + 1;
    protected static final int MAX_FIELDS = ID_STATE + 1; // add one because of 0 index!

    // Top panel label text aray
    protected static final String labelText[] =
    {
        "First Name:", "Last Name:", "Business Name:",
        "ID Number:", "ID Type:",
        "ID Country:", "ID State:",
    };

    // Top panel label text aray
    protected static final String labelTags[] =
    {
        "FirstName", "LastName", "BusinessName",
        "IDNumberLabel", "IDTypeLabel",
        "IDCountryLabel", "IDStateLabel",
    };

    // label array
    protected JLabel[] fieldLabels = null;
    protected JLabel countryLabel = null;
    /** First Name **/
    protected ConstrainedTextField firstNameField = null;
    /** Last Name **/
    protected ConstrainedTextField lastNameField = null;
    /** ID number field */
    protected AlphaNumericTextField idNumberField = null;
    /** ID type list */
    protected ValidatingComboBox idTypeField = null;
    /** State list */
    protected ValidatingComboBox idStateField = null;
    /** Country list */
    protected ValidatingComboBox idCountryField = null;
    /** Business Name Field **/
    protected ValidatingTextField customerNameField = null;
    /** Business Name Label **/
    protected JLabel customerNameLabel = null;
    /** Business Customer Flag **/
    protected boolean isBusinessCustomer = false;

    /**
     * Default Constructor.
     */
    public PersonalIDEntryBean()
    {
        super();
        initialize();
    }

    /**
     * Configures the class.
     */
    public void initialize()
    {
        setName("PersonalIDEntryBean");
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
     * Initialize the setting for the data fields.
     */
    protected void initFields()
    {
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "20");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30", "20");
        idNumberField = uiFactory.createAlphaNumericField("idNumberField", "1", "20", false);
        idTypeField = uiFactory.createValidatingComboBox("idTypeField", "false", "15");
        idCountryField = uiFactory.createValidatingComboBox("idCountryField", "false", "15");
        idStateField = uiFactory.createValidatingComboBox("idStateField", "false", "20");
    }

    /**
     * Initializes the setting for the field labels.
     */
    protected void initLabels()
    {

        fieldLabels = new JLabel[MAX_FIELDS];

        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelText[i], labelText[i], null, UI_LABEL);
        }
        countryLabel = fieldLabels[ID_COUNTRY];
        customerNameLabel = fieldLabels[BUSINESS_NAME];
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(7);
        orderableFields.add(new OrderableField(fieldLabels[FIRST_NAME], firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(fieldLabels[LAST_NAME], lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(fieldLabels[BUSINESS_NAME], customerNameField,
                AddressField.BUSINESS_NAME));
        orderableFields.add(new OrderableField(fieldLabels[ID_NUMBER], idNumberField));
        orderableFields.add(new OrderableField(fieldLabels[ID_TYPE], idTypeField));
        orderableFields.add(new OrderableField(fieldLabels[ID_COUNTRY], idCountryField));
        orderableFields.add(new OrderableField(fieldLabels[ID_STATE], idStateField));

        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        setLayout(new GridBagLayout());

        int xValue = 0;

        JComponent[] orderedComponents = new JComponent[orderedFields.size()];
        JLabel[] orderedLabels = new JLabel[orderableFields.size()];
        for (OrderableField orderedField : orderedFields)
        {
            orderedComponents[xValue] = orderedField.getField();
            orderedLabels[xValue] = orderedField.getLabel();
            xValue++;
        }

        UIUtilities.layoutDataPanel(this, orderedLabels, orderedComponents, false);
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
        idNumberField.getDocument().addDocumentListener(this);
        idNumberField.addFocusListener(this);
        idCountryField.addItemListener(this);
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
        idNumberField.getDocument().removeDocumentListener(this);
        idNumberField.removeFocusListener(this);
        idCountryField.removeItemListener(this);
    }

    /**
     * Overrides the inherited setVisible().
     *
     * @param visible boolean
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible && !errorFound())
        {
            setFocusToFirst();
        }
    }

    /**
     * Updates the model with the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
       EncipheredDataIfc personalIDNumber = null;

        if (beanModel instanceof PersonalIDEntryBeanModel)
        {
            PersonalIDEntryBeanModel model = (PersonalIDEntryBeanModel)beanModel;

            // if business customer
            if (model.isBusinessCustomer())
            {
                model.setOrgName(customerNameField.getText());
                setBusinessCustomer(true);
            }
            else
            {
                model.setFirstName(firstNameField.getText());
                model.setLastName(lastNameField.getText());
            }

            try
            {
                personalIDNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(idNumberField.getText().getBytes());
                model.setPersonalID(personalIDNumber);
            }
            catch (EncryptionServiceException e)
            {
                logger.error("Could not encrypt text" + e.getLocalizedMessage());
            }
            model.setSelectedIDType(idTypeField.getSelectedIndex());
            if (idStateField.getSelectedIndex() >= 0)
            {
                model.setStateIndex(idStateField.getSelectedIndex());
            }
            if (idCountryField.getSelectedIndex() >= 0)
            {
                model.setCountryIndex(idCountryField.getSelectedIndex());
            }
            else
            {
                model.setCountryIndex(0);
            }
        }
    }

    /**
     * Update the bean if the model has changed
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof PersonalIDEntryBeanModel)
        {
            PersonalIDEntryBeanModel model = (PersonalIDEntryBeanModel)beanModel;
            
            byte[] personalID = null;
            try
            {
                personalID = model.getPersonalID().getDecryptedNumber();
                idNumberField.setText(new String(personalID));    
            }
            finally
            {
                Util.flushByteArray(personalID);
            }
            setFieldRequired(idNumberField, true);

            // Retrieve countries and update combo box
            String[] countryList = model.getCountryNames();
            ValidatingComboBoxModel countryModel = new ValidatingComboBoxModel(countryList);

            idCountryField.setModel(countryModel);
            idCountryField.setSelectedIndex(model.getCountryIndex());

            // update the state combo box with the new list of states
            String[] stateList = model.getStateNames();
            ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

            idStateField.setModel(stateModel);
            idStateField.setSelectedIndex(model.getStateIndex());

            // Update the combo box with the ID Type localized strings

            Vector idTypes = model.getIDTypes();
            if (idTypes != null)
            {
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(idTypes);
                idTypeField.setModel(listModel);
            }

            if (model.getSelectedIDType() > -1)
            {
                idTypeField.setSelectedIndex(model.getSelectedIDType());
            }
            // Update Name
            boolean editMode = model.getEditableFields();
            // if business customer
            if (model.isBusinessCustomer())
            {
                customerNameField.setText(model.getOrgName());
                setBusinessCustomer(true);
            }
            else
            {
                firstNameField.setText(model.getFirstName());
                lastNameField.setText(model.getLastName());
            }

            setupComponent(firstNameField, editMode, !model.isBusinessCustomer());
            setupComponent(lastNameField, editMode, !model.isBusinessCustomer());
            setupComponent(customerNameField, editMode, model.isBusinessCustomer());

            setFieldRequired(firstNameField, !model.isBusinessCustomer());
            setFieldRequired(lastNameField, !model.isBusinessCustomer());
            setFieldRequired(customerNameField, model.isBusinessCustomer() && editMode);

        }
    }

    /**
     * Return business customer flag
     *
     * @return boolean isBusinessCustomer
     */
    public boolean isBusinessCustomer()
    {
        return isBusinessCustomer;
    }

    /**
     * Set business customer flag
     *
     * @param boolean isBusinessCustomer
     */
    public void setBusinessCustomer(boolean isBusinessCustomer)
    {
        this.isBusinessCustomer = isBusinessCustomer;
    }

    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void changedUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Implementation of ItemListener interface.
     *
     * @param e a document event
     */
    public void changedUpdate(ItemEvent e)
    {
    }

    /**
     * Implementation of ItemListener interface.
     *
     * @param e a document event
     */
    public void valueChanged(ItemEvent e)
    {
    }

    /**
     * Implementation of ItemListener interface.
     *
     * @param e a document event
     */
    public void itemStateChanged(ItemEvent e)
    {
        updateStates(e);
    }

    /**
     * Update states as country selection changes
     *
     * @param e a document event
     */
    public void updateStates(ItemEvent e)
    {
        int countryIndx = idCountryField.getSelectedIndex();
        if (countryIndx == -1)
        {
            countryIndx = 0;
        }
        PersonalIDEntryBeanModel model = (PersonalIDEntryBeanModel)beanModel;
        model.setCountryIndex(countryIndx);
        String[] stateList = model.getStateNames();

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        idStateField.setModel(stateModel);
        idStateField.setSelectedIndex(0); // select 1st element of the list
    }

    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void insertUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void removeUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Determine what fields should be required. This is not always the same for
     * PersonalID Entry. If the user only enters something in the top panel,
     * then all fields are required in the top panel, and vice versa.
     */
    protected void setRequiredFields()
    {
        // if business customer
        if (isBusinessCustomer())
        {
            setFieldRequired(customerNameField, true);
            setFieldRequired(firstNameField, false);
        }
        else
        {
            setFieldRequired(firstNameField, true);
            setFieldRequired(customerNameField, false);
        }
        setFieldRequired(idNumberField, true);
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc)field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField)field).setEditable(isEditable);
        }

        field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i], fieldLabels[i]));
        }
        firstNameField.setLabel(fieldLabels[FIRST_NAME]);
        lastNameField.setLabel(fieldLabels[LAST_NAME]);
        idNumberField.setLabel(fieldLabels[ID_NUMBER]);
        countryLabel.setText(retrieveText("IDCountryLabel", "ID Country:"));
        idCountryField.setLabel(countryLabel);
        customerNameField.setLabel(fieldLabels[BUSINESS_NAME]);

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass())
                + "(Revision " + getRevisionNumber()
                + ") @" + hashCode());

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
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        String[] countryList = { "US", "Canada" };
        String[] stateList = { "TX", "CA", "FL" };
        PersonalIDEntryBeanModel beanModel = new PersonalIDEntryBeanModel();
        beanModel.setIDNumber("12345");
        Vector<String> idTypesVector = new Vector<String>(); // =
        // cargo.getIDTypes
        // ();
        idTypesVector.add(new String("Drivers License"));
        idTypesVector.add(new String("Eye Scan"));
        idTypesVector.add(new String("Blood Test"));
        idTypesVector.add(new String("DNA"));
        beanModel.setCountryNames(countryList);
        beanModel.setStateNames(stateList);
        beanModel.setStateIndex(0);
        beanModel.setCountryIndex(0);
        beanModel.setIDTypes(idTypesVector);
        beanModel.setFirstName("Joe");
        beanModel.setLastName("Doe");

        PersonalIDEntryBean bean = new PersonalIDEntryBean();
        bean.configure();
        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}

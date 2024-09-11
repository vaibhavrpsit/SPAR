/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeMasterBean.java /main/26 2013/05/22 11:33:44 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  05/22/13 - set daysValid value only if it is not empty
 *    subrdey   04/05/13 - Change combobox to textfield for temp employee valid
 *                         days.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   10/25/11 - added a renderer to the EmployeeMasterBean for
 *                         renderering Locales instead of dealing with Strings
 *                         directly.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    sgu       03/11/09 - change names from alphanumerice to text fields
 *    kkhirema  02/26/09 - modified activate method to select the language
 *                         based on the user locale or UI locale
 *    glwang    02/02/09 - set max length of first name and last name fields as
 *                         1
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *
 * ===========================================================================
 * $Log:
 *     6    360Commerce 1.5         3/5/2008 2:54:53 PM    Anil Bondalapati
 *          updated to fix the display of storeID on the backoffice
 *     6    I18N_P2    1.4.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *          alphanumerice fields for I18N purpose
 *     5    360Commerce 1.4         9/26/2006 9:14:46 AM   Christian Greene
 *          moving password fields to new site
 *     4    360Commerce 1.3         4/2/2006 11:56:19 PM   Dinesh Gautam
 *          Added code for new fields �Employee login Id� & �Verify Password�
 *     3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse
 *
 *    Revision 1.11.2.2  2005/01/27 15:41:41  rsachdeva
 *    @scr 7908 Enabling Scanning and Swiping for Employee Id field in Employee Master Screen
 *
 *    Revision 1.11.2.1  2005/01/17 23:01:15  rsachdeva
 *    @scr 7908 Enabling Scanning and Swiping for Employee Id field in Employee Master Screen
 *
 *    Revision 1.11  2004/08/18 16:36:58  jriggins
 *    @scr 3846 Set the min=1 and max=5 of the store ID field.
 *
 *    Revision 1.10  2004/08/04 18:12:34  blj
 *    @scr 0 - no change.
 *
 *    Revision 1.9  2004/07/16 16:00:38  aschenk
 *    @scr 6099 - Fixed the days value field to display the saved value if there is one.  It was always showing 1.
 *
 *    Revision 1.8  2004/07/05 16:38:20  aachinfiev
 *    @scr 5215 - Password accepting non-alphanumeric characters
 *
 *    Revision 1.7  2004/06/25 16:22:58  jeffp
 *    @scr 5738 - set daysValidField to default to index 0 when displaying combo box
 *
 *    Revision 1.6  2004/05/27 17:13:09  awilliam
 *    @scr 5266
 *
 *    Revision 1.5  2004/05/01 16:47:26  tfritz
 *    @scr 3904 Preferred Language now shows the one saved for the specific employee
 *
 *    Revision 1.4  2004/04/01 00:11:33  cdb
 *    @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.
 *
 *
 * Rev 1.6 Dec 22 2003 17:20:26 jriggins Renamed certain methods and members
 * Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.5 Dec 17 2003 08:50:46 jriggins Added check to see whether we need to
 * mask the password field. Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.4 Dec 16 2003 15:28:30 jriggins Added support for the add temporary
 * employee usecase. Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.3 Sep 24 2003 06:54:54 baa Address scr 2947 Resolution for 2947:
 * Employee Master accepts characters other than alphanumeric.
 *
 * Rev 1.2 Sep 19 2003 10:45:16 baa add Mask to password field Resolution for
 * 3369: Feature Enhancement: Mask Employee Password
 *
 * Rev 1.1 Sep 16 2003 17:52:32 dcobb Migrate to JVM 1.4.1 Resolution for 3361:
 * New Feature: JVM 1.4.1_03 (Windows) Migration
 *
 * Rev 1.0 Aug 29 2003 16:10:26 CSchellenger Initial revision.
 *
 * Rev 1.3 Apr 16 2003 19:18:18 baa add status field Resolution for POS
 * SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer
 * screen
 *
 * Rev 1.2 Dec 18 2002 17:40:20 baa add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.1 Aug 14 2002 18:17:36 baa format currency Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.0 Apr 29 2002 14:51:32 msg Initial revision.
 *
 * Rev 1.1 15 Apr 2002 09:34:28 baa make call to setLabel() from the
 * updatePropertyFields() method Resolution for POS SCR-1599: Field name labels
 * on dialog screens use default text instead of text from bundles
 *
 * Rev 1.0 Mar 18 2002 11:54:24 msg Initial revision.
 *
 * Rev 1.3 Feb 23 2002 15:04:14 mpm Re-started internationalization initiative.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes Resolution for POS
 * SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Check Entry Information
 * 
 * @version $Revision: /main/26 $
 */
public class EmployeeMasterBean extends ValidatingBean implements DocumentListener
{
    private static final long serialVersionUID = -2950347615403944711L;
    /**
     * Revision number
     */
    public static String revisionNumber = "$Revision: /main/26 $";
    /**
     * First Name field and label index
     */
    protected static final int FIRST_NAME = 0;
    /**
     * Middle Name field and label index
     */
    protected static final int MIDDLE_NAME = FIRST_NAME + 1;
    /**
     * Last Name field and label index
     */
    protected static final int LAST_NAME = MIDDLE_NAME + 1;
    /**
     * Id field and label index
     */
    protected static final int ID_NUMBER = LAST_NAME + 1;
    /**
     * Login Id field and label index
     */
    protected static final int LOGIN_ID_NUMBER = ID_NUMBER + 1;
    /**
     * Role field and label index
     */
    protected static final int ROLE = LOGIN_ID_NUMBER + 1;

    /**
     * Status field and label index
     */
    protected static final int STATUS = ROLE + 1;
    /**
     * Preferred language field and label index
     */
    protected static final int LANGUAGE = STATUS + 1;
    /**
     * Preferred language field and label index
     */
    protected static final int DAYS_VALID = LANGUAGE + 1;
    /**
     * Highest field and label index
     */
    protected static final int MAX_FIELDS = DAYS_VALID + 1;
    // add one because of 0 index!
    /**
     * Array of labels
     */
    protected static String labelText[] =
        {
            "First Name",
            "Middle Name",
            "Last Name",
            "Employee ID",
            "Employee Login ID",
            "Role",
            "Status",
            "Preferred Language",
            "Days Valid" };

    /**
     * Array of labels
     */
    protected static String labelTags[] =
        {
            "FirstNameLabel",
            "MiddleNameLabel",
            "LastNameLabel",
            "EmployeeIDLabel",
            "EmployeeLoginIDLabel",
            "RoleLabel",
            "StatusLabel",
            "PreferredLanguageLabel",
            "DaysValidLabel" };

    /***************************************************************************
     * fieldLabels is an array of JLabels
     */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];

    /***************************************************************************
     * beanModel is the EmployeeMaster bean model
     */
    protected EmployeeMasterBeanModel beanModel = new EmployeeMasterBeanModel();

    /**
     * firstNameField is used for displaying and entering the first name
     */
    protected ConstrainedTextField firstNameField = null;

    /**
     * middleNameField is used for displaying and entering the middle name
     */
    protected ConstrainedTextField middleNameField = null;

    /**
     * lastNameField is used for displaying and entering the last name
     */
    protected ConstrainedTextField lastNameField = null;

    /**
     * idNumberField is used for displaying and entering the employee ID number
     */
    protected AlphaNumericTextField idNumberField = null;

    /**
     * idNumberField is used for displaying and entering the employee ID number
     */
    protected AlphaNumericTextField loginIdNumberField = null;

    /**
     * rolesrField is used for displaying and entering the employee role
     */
    protected ValidatingComboBox rolesField = null;

    /**
     * status is used for displaying and entering the employee status
     */
    protected ValidatingComboBox statusField = null;

    /**
     * languagesField is used for displaying and entering the employee role
     */
    protected ValidatingComboBox languageField = null;
    /**
     * daysValid is used for displaying and entering the temporary employee days
     * valid
     */
    protected NumericTextField daysValidField = null;
    /**
     * dirty Model is a flag indicating whether or not the model has been
     * updated.
     */
    protected boolean dirtyModel = false;

    /**
     * editableIDNumber is a flag indicating whether or not the idNumberField
     * can be edited.
     */
    protected boolean editableIDNumber = true;

    /**
     * employeeType is a typesafe enum which type of employee is being
     * viewed/modified
     */
    protected EmployeeTypeEnum employeeType = EmployeeTypeEnum.STANDARD;

    /**
     * Default Constructor
     */
    public EmployeeMasterBean()
    {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("EmployeeMasterBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelTags[i], labelTags[i], null, UI_LABEL);
        }
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        middleNameField = uiFactory.createConstrainedField("middleNameField", "1", "30", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");

        idNumberField = uiFactory.createAlphaNumericField("idNumberField", "1", "10", false);
        loginIdNumberField = uiFactory.createAlphaNumericField("loginIdNumberField", "1", "10", "10", false);
        rolesField = uiFactory.createValidatingComboBox("rolesField");

        statusField = uiFactory.createValidatingComboBox("statusField");

        languageField = uiFactory.createValidatingComboBox("languageField");
        languageField.setRenderer(new PreferredLanguagesRenderer());
        daysValidField = uiFactory.createNumericField("daysValidField", "1", "3");
    }

    /**
     * Initializes the layout.
     */
    protected void initLayout()
    {
        JComponent[] fields =
            {
                firstNameField,
                middleNameField,
                lastNameField,
                idNumberField,
                loginIdNumberField,
                rolesField,
                statusField,
                languageField,
                daysValidField };
        UIUtilities.layoutDataPanel(this, fieldLabels, fields);
    }

    /**
     * The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();

        firstNameField.addFocusListener(this);
        setCurrentFocus(firstNameField);
        idNumberField.setEditable(false); // make the ID ineditable
        idNumberField.setFocusable(false);

        // Only show these fields for temporary employees
        if (EmployeeTypeEnum.TEMPORARY.equals(employeeType))
        {

            daysValidField.setVisible(true);
            fieldLabels[DAYS_VALID].setText(retrieveText(labelTags[DAYS_VALID], fieldLabels[DAYS_VALID]));
        }
        else
        {

            daysValidField.setVisible(false);
            fieldLabels[DAYS_VALID].setText("");
        }
        languageField.setSelectedIndex(beanModel.getSelectedLanguage());
    }

    /**
     * The framework calls this method just before removing the screen
     */
    public void deactivate()
    {
        super.deactivate();
        if (editableIDNumber)
        {
            idNumberField.getDocument().removeDocumentListener(this);
            loginIdNumberField.getDocument().removeDocumentListener(this);
        }

        firstNameField.removeFocusListener(this);
    }

    /**
     * Updates the model for the current settings of this bean.
     */
    public void updateModel()
    {
        beanModel.setFirstName(firstNameField.getText());
        beanModel.setMiddleName(middleNameField.getText());
        beanModel.setLastName(lastNameField.getText());
        beanModel.setIDNumber(idNumberField.getText());
        beanModel.setLoginIDNumber(loginIdNumberField.getText());
        beanModel.setSelectedRole(rolesField.getSelectedIndex());
        beanModel.setEditableIDNumber(editableIDNumber);
        beanModel.setSelectedStatus(statusField.getSelectedIndex());
        beanModel.setSelectedLanguage(languageField.getSelectedIndex());

        // These only apply to temporary employees
        if (EmployeeTypeEnum.TEMPORARY.equals(employeeType))
        {
            String daysValid = (String)daysValidField.getText();
            if(!daysValid.isEmpty())
            {
                beanModel.setDaysValidValue(Integer.valueOf(daysValid).intValue());
            }
        }
    }

    /**
     * Sets the model for the current settings of this bean.
     * 
     * @param model the model for the current values of this bean
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set EmployeeMasterBeanModel to null");
        }
        if (model instanceof EmployeeMasterBeanModel)
        {
            beanModel = (EmployeeMasterBeanModel)model;
            updateBean();
        }
    }

    /**
     * Update the bean if the model's been changed
     */
    public void updateBean()
    {
        firstNameField.setText(beanModel.getFirstName());
        middleNameField.setText(beanModel.getMiddleName());
        lastNameField.setText(beanModel.getLastName());
        idNumberField.setText(beanModel.getIDNumber());
        loginIdNumberField.setText(beanModel.getLoginIDNumber());

        if (beanModel.getRoles() != null)
        {
            rolesField.setModel(new DefaultComboBoxModel(beanModel.getRoles()));
            if (beanModel.getSelectedRole() > -1)
            {
                rolesField.setSelectedIndex(beanModel.getSelectedRole());
            }
        }

        if (beanModel.getStatusValues() != null)
        {
            statusField.setModel(new DefaultComboBoxModel(beanModel.getStatusValues()));
            if (beanModel.getSelectedStatus() > -1)
            {
                statusField.setSelectedIndex(beanModel.getSelectedStatus());
            }
        }
        if (beanModel.getSupportedLanguages() != null)
        {
            languageField.setModel(new DefaultComboBoxModel(beanModel.getSupportedLanguages()));
            beanModel.setPreferredLanguage(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            languageField.setSelectedIndex(beanModel.getSelectedLanguage());
        }

        editableIDNumber = beanModel.getEditableIDNumber();

        employeeType = beanModel.getEmployeeType();
        if (EmployeeTypeEnum.TEMPORARY.equals(employeeType))
        {
            daysValidField.setText(new Integer(beanModel.getDaysValidValue()).toString());
        }

    }

    /**
     * Update property fields.
     */
    public void updatePropertyFields()
    {
        for (int i = 0; i < labelTags.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i], fieldLabels[i]));
        }

        // update label text
        firstNameField.setLabel(fieldLabels[FIRST_NAME]);
        middleNameField.setLabel(fieldLabels[MIDDLE_NAME]);
        lastNameField.setLabel(fieldLabels[LAST_NAME]);
        idNumberField.setLabel(fieldLabels[ID_NUMBER]);
        loginIdNumberField.setLabel(fieldLabels[LOGIN_ID_NUMBER]);
        rolesField.setLabel(fieldLabels[ROLE]);
        statusField.setLabel(fieldLabels[STATUS]);
        languageField.setLabel(fieldLabels[LANGUAGE]);
        daysValidField.setLabel(fieldLabels[DAYS_VALID]);
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
     * Receive scanner data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     **/
    public void setScannerData(DeviceModelIfc data)
    {
        ScannerModel scannerModel = (ScannerModel)data;

        String temp = new String(scannerModel.getScanLabelData());

        idNumberField.setText(temp);

        updateModel();
        setCurrentFocus(rolesField);
    }

    /**
     * Receive MSR data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     **/
    public void setMSRDataEmployee(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received MSR data: " + data);

        MSRModel msrModel = (MSRModel)data;
        String employeeID = null;
        if (msrModel.getAccountNumber() != null && msrModel.getAccountNumber().length() > 10)
        {
            employeeID = msrModel.getAccountNumber().substring(0, 10);
        }
        else
        {
            employeeID = msrModel.getAccountNumber();
        }
        idNumberField.setText(employeeID);

        updateModel();
        setCurrentFocus(rolesField);

    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     **/
    public void changedUpdate(DocumentEvent e)
    {

    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     **/
    public void insertUpdate(DocumentEvent e)
    {

    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     **/
    public void removeUpdate(DocumentEvent de)
    {
        if (idNumberField.getText().equals(""))
        {
            try
            {
                DeviceTechnicianIfc dt = (DeviceTechnicianIfc)Gateway.getDispatcher().getLocalTechnician(
                        DeviceTechnicianIfc.TYPE);
                if (dt != null)
                {
                    try
                    {
                        String sessionName = ScannerSession.TYPE;
                        ScannerSession scannerSession = (ScannerSession)dt.getDeviceSession(sessionName);
                        scannerSession.setEnabled(true);
                    }
                    catch (DeviceException e)
                    {
                        logger.error("setScannerData: deviceException", e);
                    }
                }
            }
            catch (TechnicianNotFoundException e)
            {
                logger.error("setScannerData: can't get deviceTechnician", e);
            }
            catch (Exception e)
            {
                logger.error("setScannerData: can't get deviceTechnician", e);
            }
        }
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
      // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Main entry point for testing.
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EmployeeMasterBean());
    }

    // ------------------------------------------------------------------------------
    /**
     * Inner class to display the locale objects correctly in the list.
     */
    @SuppressWarnings("serial")
    private static class PreferredLanguagesRenderer extends DefaultListCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus)
        {
            // This method may be a lit of work to do in a renderer implementation.
            // I considered getting the UI locale and UtilityManager in a constructor,
            // but it is possible for the UI locale to change.
            Locale supportedLocale = (Locale)value;
            Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String defaultValue = supportedLocale.getDisplayName(uiLocale);
            UtilityManagerIfc utility = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            value = utility.getLocaleDisplayName(supportedLocale.toString(), defaultValue, uiLocale);
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}

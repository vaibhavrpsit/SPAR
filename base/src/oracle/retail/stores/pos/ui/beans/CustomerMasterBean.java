/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerMasterBean.java /main/28 2013/11/23 11:31:53 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/23/13 - Add setText for employeeIDLabel.
 *    mchellap  11/30/12 - Added customer receipt preference
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *    acadar    07/27/12 - changes for XC
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    icole     03/28/12 - Forward port of changes to not allow a birth year
 *                         greater than the current year or earlier than the
 *                         current year minus 140. This contains forward ports
 *                         of vkaruppu_bug-13028525_13.1x and
 *                         vkaruppu_bug-13523646_13.1x.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - refactoring
 *    acadar    04/22/09 - refactoring changes
 *    acadar    04/22/09 - translate date/time labels
 *    nkgautam  02/16/09 - Changed fullName Constrained Field to a JLabel
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    8    I18N_P2    1.5.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    7    I18N_P2    1.5.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    6    360Commerce 1.5         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *         changes to not allow double bytes chars in some cases
 *    5    360Commerce 1.4         8/3/2007 5:35:14 PM    Mathews Kochummen
 *         validate date when year is on separate field
 *    4    360Commerce 1.3         5/21/2007 10:04:16 PM  Mathews Kochummen
 *         format label
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/16 18:05:42  bvanschyndel
 *   @scr 5995 made the model Validating for yes and no combo boxes
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 01 2003 14:01:32   baa
 * yes/no combo box issues
 * Resolution for 3468: Drop down boxes display incorrect data on Customer Details during Customer Search
 *
 *    Rev 1.0.1.1   Dec 01 2003 13:55:16   baa
 * cleanup system outs
 *
 *    Rev 1.0   Aug 29 2003 16:10:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Aug 27 2003 17:20:54   baa
 * remove call to apply pattern
 * Resolution for 3330: Out of Memory Error- App Crashes.
 *
 *    Rev 1.6   May 09 2003 12:50:48   baa
 * more fixes to business customer
 * Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 *
 *    Rev 1.5   Apr 02 2003 17:50:46   baa
 * customer and screen changes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Mar 24 2003 10:08:18   baa
 * remove reference to foundation.util.EMPTY_STRING
 * Resolution for POS SCR-2101: Remove uses of  foundation constant  EMPTY_STRING
 *
 *    Rev 1.3   Mar 20 2003 18:18:58   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.2   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   21 May 2002 17:29:54   baa
 * ils
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   13 May 2002 14:12:00   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   15 Apr 2002 09:33:38   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:52:54   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 28 2002 19:21:06   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//java imports
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
/**
 * This is the class that display main Customer information.
 * It is used with the CustomerMasterBeanModel class.
 */
public class CustomerMasterBean extends ValidatingBean
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1387485184334784513L;
    /** birthdateLabel */
    protected JLabel birthdateLabel = null;
    /** birthYearLabel */
    protected JLabel birthYearLabel = null;
    /** customerIdLabel */
    protected JLabel customerIdLabel = null;
    /** emailLabel */
    protected JLabel emailLabel = null;
    /** employeeIDLabel */
    protected JLabel employeeIDLabel = null;
    /** birthdateLabel */

    /** fullNameLabel */
    protected JLabel fullNameLabel = null;
    /** salutationLabel */
    protected JLabel salutationLabel = null;

    /** genderLabel */
    protected JLabel genderLabel = null;
    /** mailLabel */
    protected JLabel mailLabel = null;
    /** privacyIssuesLabel */
    protected JLabel privacyIssuesLabel = null;

    /** telephoneLabel */
    protected JLabel telephoneLabel = null;
    /** preferredLanguageLabel */
    protected JLabel preferredLanguageLabel = null;
    
    /** CustomerIdField */
    protected JLabel customerIdField = null;
    /** birthdateField */
    protected EYSDateField birthdateField = null;
    /** birthYearField */
    protected NumericTextField birthYearField = null;
    /** mailField */
    protected YesNoComboBox mailField = null;
    /** telephoneField */
    protected YesNoComboBox telephoneField = null;
    /** emailField */
    protected YesNoComboBox emailField = null;
  
    /** employeeIDField */
    protected AlphaNumericTextField employeeIDField = null;
    /** LanguageField */

    // general purpose dataModel object reference for the YesNoComboBoxes
    /** mailModel */
    protected BooleanComboModel mailModel = null;
    /** telephoneModel */
    protected BooleanComboModel telephoneModel = null;
    /** emailModel */
    protected BooleanComboModel emailModel = null;
    /** FullNameField */
    protected JLabel fullNameField = null;
    /** salutationField */
    protected ConstrainedTextField salutationField = null;
    /** genderField */
    protected ValidatingComboBox genderField = null;

    /** PreferredLanguageField */
    protected ValidatingComboBox preferredLanguageField = null;
    
    /** FirstNameField */
    protected ConstrainedTextField firstNameField = null;
    /** LastNameField */
    protected ConstrainedTextField lastNameField = null;
    /** MiddleNameField */
    protected ConstrainedTextField middleNameField = null;
    /** SuffixField */
    protected ConstrainedTextField suffixField = null;
    /** FirstNameLabel */
    protected JLabel firstNameLabel = null;
    /** LastNameLabel */
    protected JLabel lastNameLabel = null;
    /** SuffixLabel */
    protected JLabel suffixLabel = null;
    /** MiddleNameLabel */
    protected JLabel middleNameLabel = null;

    /**
     * Constructor. Call setTabOrder() to override the default focus manager
     * where needed.  This allows the bean to control which field receives
     * the focus each time the TAB key is pressed.
     */
    public CustomerMasterBean()
    {
        super();
        initialize();
    }


    /**
     * Returns the base bean model
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Initializes the fields
     */
    protected void initializeFields()
    {

        birthdateField = uiFactory.createEYSDateField("birthdateField");
        birthdateField.setFormat(DateDocument.MONTH_DAY);
        birthdateField.setColumns(15);

        birthYearField = uiFactory.createNumericField("birthYearField", "0", "4");
        birthYearField.setColumns(15);
        customerIdField = uiFactory.createLabel("customerIdField", "", null, UI_LABEL);
        fullNameField = uiFactory.createLabel("fullNameField", "Full Name:", null, UI_LABEL);
        genderField = uiFactory.createValidatingComboBox("genderField", "false", "10");
        salutationField = uiFactory.createConstrainedField("salutationField", "2", "30", "15");
        preferredLanguageField = uiFactory.createValidatingComboBox("PreferredLanguageField", "false", "20");
        preferredLanguageField.setEditable(false);
        mailField = uiFactory.createYesNoComboBox("mailField", 10);
        mailField.setEditable(false);
        telephoneField = uiFactory.createYesNoComboBox("telephoneField", 10);
        telephoneField.setEditable(false);
        emailField = uiFactory.createYesNoComboBox("emailField", 10);
        emailField.setEditable(false);
        employeeIDField = uiFactory.createAlphaNumericField("employeeIDField", "0", "10", "10");
    }

    /**
     * Initializes the labels
     */
    protected void initializeLabels()
    {
        birthdateLabel         = uiFactory.createLabel("birthdateLabel", "Birthday ({0}):", null, UI_LABEL);
        birthYearLabel         = uiFactory.createLabel("birthYearLabel", "Birth Year (YYYY):", null, UI_LABEL);
        customerIdLabel        = uiFactory.createLabel("customerIdLabel", "Customer ID:", null, UI_LABEL);
        fullNameLabel          = uiFactory.createLabel("fullNameLabel", "Full Name:", null, UI_LABEL);
        genderLabel            = uiFactory.createLabel("genderLabel", "Gender:", null, UI_LABEL);

        privacyIssuesLabel     = uiFactory.createLabel("privacyIssuesLabel", "Privacy Issues", null, UI_LABEL);
        salutationLabel        = uiFactory.createLabel("salutationLabel", "Salutation:", null, UI_LABEL);
        mailLabel              = uiFactory.createLabel("mailLabel", "Mail:", null, UI_LABEL);
        telephoneLabel         = uiFactory.createLabel("telephoneLabel", "Telephone:", null, UI_LABEL);
        emailLabel             = uiFactory.createLabel("emailLabel", "E-Mail:", null, UI_LABEL);
        preferredLanguageLabel = uiFactory.createLabel("preferredLanguageLabel", "Language:", null, UI_LABEL);
        employeeIDLabel        = uiFactory.createLabel("employeeIDLabel", "Employee ID:", null, UI_LABEL);
    }

    /**
     * Return the dataModel being used by the mailField which is a YesNoComboBox
     * @return BooleanComboModel
     */
     protected BooleanComboModel getMailModel()
     {
        // explicit cast from MailComboModel to BooleanComboModel
        if (mailModel == null)
        {
            mailModel = (BooleanComboModel) mailField.getModel();
        }

        return mailModel;
     }

    /**
     * Return the dataModel being used by the telephoneField which is a YesNoComboBox
     * @return BooleanComboModel
     */
     protected BooleanComboModel getTelephoneModel()
     {
        // explicit cast from ComboBoxModel to BooleanComboModel
        if (telephoneModel == null)
        {
            telephoneModel = (BooleanComboModel) telephoneField.getModel();
        }

        return telephoneModel;

     }

    /**
     * Return the dataModel being used by the emailField which is a YesNoComboBox
     * @return BooleanComboModel
     */
     protected BooleanComboModel getEmailModel()
     {
        // explicit cast from ComboBoxModel to BooleanComboModel
        if (emailModel == null)
        {
            emailModel = (BooleanComboModel) emailField.getModel();
        }

        return emailModel;
     }

    /**
     *    Initialize the class.
     */
    protected void initialize()
    {
        setName("CustomerMasterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();

    }

    /**
     *    Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        UIUtilities.layoutComponent(this,customerIdLabel,customerIdField,0,0,false);
        UIUtilities.layoutComponent(this,fullNameLabel,fullNameField,0,1,false);
        UIUtilities.layoutComponent(this,salutationLabel,salutationField,0,2,false);

        UIUtilities.layoutComponent(this,birthdateLabel,birthdateField,0,3,false);
        UIUtilities.layoutComponent(this,birthYearLabel,birthYearField,0,4,false);
        UIUtilities.layoutComponent(this,genderLabel,genderField,0,5,false);
        UIUtilities.layoutComponent(this,privacyIssuesLabel,null,0,7,false);

        UIUtilities.layoutComponent(this,mailLabel,mailField,0,8,false);
        UIUtilities.layoutComponent(this,telephoneLabel,telephoneField,0,9,false);
        UIUtilities.layoutComponent(this,emailLabel,emailField,0,10,false);
        UIUtilities.layoutComponent(this,preferredLanguageLabel,preferredLanguageField,0,11,false);
        UIUtilities.layoutComponent(this,employeeIDLabel,employeeIDField,0,13,false);
    }

    /**
     * Updates the model for the current settings of this bean.
     */
    public void updateModel()
    {
        if (beanModel instanceof CustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;
            if (birthdateField.isValid())
            {
              if(!Util.isEmpty(birthYearField.getText()))
              {
            	  //since year occurs in a separate ui field, set value in DateDocument
            	  int year = (int)(birthYearField.getLongValue());
                  DateDocument bdayDoc = (DateDocument)(birthdateField.getDocument());
                  bdayDoc.setSeparateYear(year);
              }
              model.setBirthdate(birthdateField.getDate());
            }
            if(!Util.isEmpty(birthYearField.getText()))
            {
                model.setBirthYear(birthYearField.getLongValue());
            }
            else
            {
                model.setBirthYear(PersonConstantsIfc.YEAR_OF_BIRTH_UNSPECIFIED);
            }
            model.setMailPrivacy(getMailModel().valueOf((String)mailField.getSelectedItem()));
            model.setTelephonePrivacy(getTelephoneModel().valueOf((String)telephoneField.getSelectedItem()));
            model.setEmailPrivacy(getEmailModel().valueOf((String)emailField.getSelectedItem()));
            model.setCustomerName(fullNameField.getText());
            model.setGenderIndex(genderField.getSelectedIndex());
            model.setSalutation(salutationField.getText());
            model.setSelectedLanguage(preferredLanguageField.getSelectedIndex());
            model.setEmployeeID(employeeIDField.getText());
        }
    }


    /**
     * validate fields
     */
    protected boolean validateFields()
    {
        boolean valid = true;
        if(!Util.isEmpty(birthYearField.getText()))
        {
      	    int year = (int)(birthYearField.getLongValue());
      	    valid = validateYear(year);
            DateDocument bdayDoc = (DateDocument)(birthdateField.getDocument());
            bdayDoc.setSeparateYear(year);
        }
        if(!valid)
        {
            return valid;
        }
        else
        {
            return (super.validateFields());
        }
    }

    /**
     * validate year
     */
    protected boolean validateYear(int year)
    {
        boolean valid = true;
        EYSDate now = new EYSDate();
        for (int i = 0; i < MAX_ERROR_MESSAGES; i++)
        {
            errorMessage[i] = "";
        }
        int errorCount = 0;
        // Assume that no one over 140 will be added.  May need to be increased when the fountain of youth is discovered.
        if (year < (now.getYear() - 140) || year > now.getYear())
        {
            String msg = null;
            Object[] data = null;
            msg = UIUtilities.retrieveText("DialogSpec", BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.InvalidYear", "{0} is not a valid calendar year.");
            data = new Object[1];
            data[0] = getFieldName(birthYearField);
            msg = LocaleUtilities.formatComplexMessage(msg, data, getLocale());
            errorMessage[errorCount] = msg;
            showErrorScreen();
            valid = false;
        }
        return valid;
    }

    /**
     * Update the bean if It's been changed
     */
    protected void updateBean()
    {
       if (beanModel instanceof CustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;
            if (model.isBirthdateValid())
            {

                birthdateField.setDate(model.getBirthMonthAndDay());
            }
            else
            {
                birthdateField.setText("");
            }

            if (model.isBirthYearValid())
            {
                birthYearField.setLongValue(model.getBirthYear());
            }
            else
            {
                birthYearField.setText("");
            }

            genderField.setModel(new ValidatingComboBoxModel (model.getGenderTypes()));
            genderField.setSelectedIndex(model.getGenderIndex());

            customerIdField.setText(model.getCustomerID());

            employeeIDField.setText(model.getEmployeeID());

            String mailSetting = getMailModel().valueOf(model.getMailPrivacy());
            mailField.setModel(new ValidatingComboBoxModel(getMailModel().getValues()));
            mailField.setSelectedItem(mailSetting);

            String phoneSetting = getTelephoneModel().valueOf(model.getTelephonePrivacy());
            telephoneField.setModel(new ValidatingComboBoxModel(getTelephoneModel().getValues()));
            telephoneField.setSelectedItem(phoneSetting);

            String emailSetting = getEmailModel().valueOf(model.getEmailPrivacy());
            emailField.setModel(new ValidatingComboBoxModel(getEmailModel().getValues()));
            emailField.setSelectedItem(emailSetting);

            fullNameField.setText(model.getCustomerName());

            salutationField.setText(model.getSalutation());

            if (model.getLanguages() != null)
            {
                 preferredLanguageField.setModel(new ValidatingComboBoxModel(model.getLanguages()));
                 preferredLanguageField.setSelectedIndex(model.getSelectedLanguage());
            }
            
            
        }
    }

    /**
     *  Set the properties to be used by this bean
     *  @param props the propeties object
     */
    public void setProps(Properties props)
    {
        super.setProps(props);
        getMailModel().setProps(props);
        getTelephoneModel().setProps(props);
        getEmailModel().setProps(props);
        updatePropertyFields();
    }

    /**
     *  Update property fields.
     */
    protected void updatePropertyFields()
    {
        DateDocument    doc         =  (DateDocument)birthdateField.getDocument();
        String          dobLabel    =  retrieveText("BirthdateLabel", birthdateLabel);
        String          yearLabel   =  retrieveText("BirthYearLabel", birthYearLabel);

        //Retrieve the localized pattern for the Full year
        SimpleDateFormat dateFormat = DomainGateway.getSimpleDateFormat(getDefaultLocale(), LocaleConstantsIfc.DEFAULT_YEAR_FORMAT);
        String translatedLabel = getTranslatedDatePattern(dateFormat.toPattern());

        birthYearLabel.setText(LocaleUtilities.formatComplexMessage(yearLabel,translatedLabel));

        // Retrieve bundle text for month/day label
        String monthDayPatternChars = ((SimpleDateFormat)(doc.getDateFormat())).toPattern();

        translatedLabel = getTranslatedDatePattern(monthDayPatternChars);
        birthdateLabel.setText(LocaleUtilities.formatComplexMessage(dobLabel, translatedLabel));

        customerIdLabel.setText(retrieveText("CustomerIDLabel",customerIdLabel));
        fullNameLabel.setText(retrieveText("FullNameLabel",  fullNameLabel));
        genderLabel.setText(retrieveText("GenderLabel", genderLabel));
        privacyIssuesLabel.setText(retrieveText("PrivacyIssuesLabel",privacyIssuesLabel));
        salutationLabel.setText(retrieveText("SalutationLabel", salutationLabel));
        employeeIDLabel.setText(retrieveText("EmployeeIDLabel", employeeIDLabel));

        mailLabel.setText(retrieveText("MailLabel", mailLabel));
        telephoneLabel.setText(retrieveText("TelephoneLabel", telephoneLabel));
        emailLabel.setText(retrieveText("EmailLabel", emailLabel));
        preferredLanguageLabel.setText(retrieveText("PreferredLanguageLabel", preferredLanguageLabel));
        // Associate labels with fields
        salutationField.setLabel(salutationLabel);

        genderField.setLabel(genderLabel);
        birthdateField.setLabel(birthdateLabel);
        birthYearField.setLabel(birthYearLabel);

        mailField.setLabel(mailLabel);
        telephoneField.setLabel(telephoneLabel);
        emailField.setLabel(emailLabel);
    }

    /**
     * Returns default display string.
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: CustomerMasterBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * @return String representation of revision number
    */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }


    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CustomerMasterBean bean = new CustomerMasterBean();

        UIUtilities.doBeanTest(bean);
    }
}

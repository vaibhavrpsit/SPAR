/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerMasterBean.java /main/28 2013/11/23 11:31:53 yiqzhao Exp $
 * ===========================================================================
 * Rev 1.0	Aug 30,2016	Ashish Yadav	Changes for code merging
 * ===========================================================================
 */
package max.retail.stores.pos.ui.beans;

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
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
import oracle.retail.stores.pos.ui.beans.BooleanComboModel;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.CustomerMasterBean;
import oracle.retail.stores.pos.ui.beans.DateDocument;
// Changes ends for rev 1.0
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.YesNoComboBox;
/**
 * This is the class that display main Customer information.
 * It is used with the CustomerMasterBeanModel class.
 */
public class MAXCustomerMasterBean extends CustomerMasterBean
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
    // Changes starts for code merging(commeting below line)
   // protected JLabel fullNameField = null;
    protected ConstrainedTextField fullNameField = null;
 // Changes starts for code merging(commeting below line)
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
    public MAXCustomerMasterBean()
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
        // Chnages starts for code merging
        fullNameField = uiFactory.createConstrainedField("fullNameField", "Full Name:", null, UI_LABEL);
        //  Changes ends for code merging
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
                  MAXDateDocument bdayDoc = (MAXDateDocument)(birthdateField.getDocument());
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
            MAXDateDocument bdayDoc = (MAXDateDocument)(birthdateField.getDocument());
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
//Changes starts for Rev 1.0
            if(model.getEditableFields()==false)
            {
            	birthdateField.setEditable(false); 
         	  birthYearField.setEditable(false);
         	 genderField.setEnabled(false);
         	mailField.setEnabled(false);
         	telephoneField.setEnabled(false);
         	emailField.setEnabled(false);
         	preferredLanguageField.setEnabled(false);
         	fullNameField.setEditable(false);
         	salutationField.setEditable(false);
            }
            else
            {
         	   birthdateField.setEditable(true); 
         	  birthYearField.setEditable(true);
         	 genderField.setEnabled(true);
         	mailField.setEnabled(true);
         	telephoneField.setEnabled(true);
         	emailField.setEnabled(true);
         	preferredLanguageField.setEnabled(true);
         	fullNameField.setEditable(true);
         	salutationField.setEditable(true);
            }
//Changes ends for rev 1.0
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

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-16-2015
 * Rev 1.0  Jan 25, 2011 5:58:09 PM Vaishali.Kumari
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.3
 * Bean for the CRM_CUSTOMER_DETAILS
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
import oracle.retail.stores.pos.ui.beans.BooleanComboModel;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.CustomerMasterBean;
import oracle.retail.stores.pos.ui.beans.DateDocument;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.ValidatingFieldIfc;
import oracle.retail.stores.pos.ui.beans.YesNoComboBox;

public class MAXCRMCustomerMasterBean extends ValidatingBean {

	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 **/
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = -4907692034919039382L;

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	/**
	 * Fields and labels
	 **/
	protected JLabel BirthdateLabel = null;
	protected JLabel BirthYearLabel = null;
	protected JLabel CustomerIdLabel = null;
	protected JLabel EmailLabel = null;
	protected JLabel EmployeeNoLabel = null;

	protected JLabel FullNameLabel = null;
	protected JLabel SalutationLabel = null;

	protected JLabel GenderLabel = null;
	protected JLabel MailLabel = null;
	protected JLabel PrivacyIssuesLabel = null;

	protected JLabel TelephoneLabel = null;
	/** @deprecated as of release 6.0 move to customerInfoBean **/
	@Deprecated
	protected JLabel PreferredCustomerLabel = null;
	protected JLabel PreferredLanguageLabel = null;
	protected JLabel CustomerIdField = null;
	protected EYSDateField BirthdateField = null;
	protected NumericTextField BirthYearField = null;
	protected YesNoComboBox MailField = null;
	protected YesNoComboBox TelephoneField = null;
	protected YesNoComboBox EmailField = null;
	protected JLabel LanguageField = null;

	// general purpose dataModel object reference for the YesNoComboBoxes
	protected BooleanComboModel mailModel = null;
	protected BooleanComboModel telephoneModel = null;
	protected BooleanComboModel emailModel = null;
	protected ConstrainedTextField FullNameField = null;
	protected ConstrainedTextField SalutationField = null;
	protected ValidatingComboBox GenderField = null;

	protected ValidatingComboBox PreferredLanguageField = null;

	/** @deprecated as of release 6.0 removed from this screen **/
	@Deprecated
	protected AlphaNumericTextField EmployeeNoField = null;
	protected ConstrainedTextField FirstNameField = null;
	protected ConstrainedTextField LastNameField = null;
	protected ConstrainedTextField MiddleNameField = null;
	protected ConstrainedTextField SuffixField = null;
	protected JLabel FirstNameLabel = null;
	protected JLabel LastNameLabel = null;
	protected JLabel SuffixLabel = null;
	protected JLabel MiddleNameLabel = null;
	protected ValidatingComboBox PreferredCustomerField = null;

	protected int separateYear = -1;
	/**
	 * flag that indicates if the bean model has been changed
	 *
	 * @deprecated as of release 5.5 dead code
	 **/
	@Deprecated
	protected boolean dirtyModel = true;

	// ----------------------------------------------------------------------------
	/**
	 * Constructor. Call setTabOrder() to override the default focus manager
	 * where needed. This allows the bean to control which field receives the
	 * focus each time the TAB key is pressed.
	 */
	// ----------------------------------------------------------------------------
	public MAXCRMCustomerMasterBean() {
		super();
		initialize();
		setTabOrder();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns the base bean model
	 */
	// ----------------------------------------------------------------------------
	@Override
	public POSBaseBeanModel getPOSBaseBeanModel() {
		return beanModel;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initializes the fields
	 */
	// ----------------------------------------------------------------------------
	protected void initializeFields() {

		BirthdateField = uiFactory.createEYSDateField("BirthdateField");
		BirthdateField.setFormat(DateDocument.MONTH_DAY);
		BirthdateField.setColumns(15);

		BirthYearField = uiFactory.createNumericField("BirthYearField", "0", "4");
		BirthYearField.setColumns(15);
		CustomerIdField = uiFactory.createLabel("", null, UI_LABEL);

		FullNameField = uiFactory.createConstrainedField("FirstNameField", "2", "30");

		GenderField = uiFactory.createValidatingComboBox("GenderField");

		SalutationField = uiFactory.createConstrainedField("SalutationField", "2", "30");

		PreferredLanguageField = uiFactory.createValidatingComboBox("PreferredLanguageField");
		PreferredLanguageField.setEditable(false);

		MailField = uiFactory.createYesNoComboBox("MailField");
		MailField.setEditable(false);

		TelephoneField = uiFactory.createYesNoComboBox("TelephoneField");
		TelephoneField.setEditable(false);

		EmailField = uiFactory.createYesNoComboBox("EMailField");
		EmailField.setEditable(false);

	}

	// ----------------------------------------------------------------------------
	/**
	 * Initializes the labels
	 */
	// ----------------------------------------------------------------------------
	protected void initializeLabels() {
		BirthdateLabel = uiFactory.createLabel("Birthday ({0}):", null, UI_LABEL);
		BirthYearLabel = uiFactory.createLabel("Birth Year (YYYY):", null, UI_LABEL);
		CustomerIdLabel = uiFactory.createLabel("Customer ID:", null, UI_LABEL);
		FullNameLabel = uiFactory.createLabel("Full Name:", null, UI_LABEL);
		GenderLabel = uiFactory.createLabel("Gender:", null, UI_LABEL);

		PrivacyIssuesLabel = uiFactory.createLabel("Privacy Issues", null, UI_LABEL);
		SalutationLabel = uiFactory.createLabel("Salutation:", null, UI_LABEL);
		MailLabel = uiFactory.createLabel("Mail:", null, UI_LABEL);
		TelephoneLabel = uiFactory.createLabel("Telephone:", null, UI_LABEL);
		EmailLabel = uiFactory.createLabel("E-Mail:", null, UI_LABEL);
		PreferredLanguageLabel = uiFactory.createLabel("Language:", null, UI_LABEL);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Return the dataModel being used by the MailField which is a YesNoComboBox
	 *
	 * @return BooleanComboModel
	 */
	// ----------------------------------------------------------------------------
	protected BooleanComboModel getMailModel() {
		// explicit cast from MailComboModel to BooleanComboModel
		if (mailModel == null) {
			mailModel = (BooleanComboModel) MailField.getModel();
		}

		return mailModel;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Return the dataModel being used by the TelephoneField which is a
	 * YesNoComboBox
	 *
	 * @return BooleanComboModel
	 */
	// ----------------------------------------------------------------------------
	protected BooleanComboModel getTelephoneModel() {
		// explicit cast from ComboBoxModel to BooleanComboModel
		if (telephoneModel == null) {
			telephoneModel = (BooleanComboModel) TelephoneField.getModel();
		}

		return telephoneModel;

	}

	// ----------------------------------------------------------------------------
	/**
	 * Return the dataModel being used by the EmailField which is a
	 * YesNoComboBox
	 *
	 * @return BooleanComboModel
	 */
	// ----------------------------------------------------------------------------
	protected BooleanComboModel getEmailModel() {
		// explicit cast from ComboBoxModel to BooleanComboModel
		if (emailModel == null) {
			emailModel = (BooleanComboModel) EmailField.getModel();
		}

		return emailModel;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Override the tab key ordering scheme of the default focus manager where
	 * appropriate. The default is to move in a zig-zag pattern from left to
	 * right across the screen. In some cases, however, it makes more sense to
	 * move down column one on the screen then start at the top of column 2.
	 *
	 * @deprecated as of release 6.0 no longer needed
	 */
	// ----------------------------------------------------------------------------
	@Deprecated
	protected void setTabOrder() {
	} // end method setTabOrder

	// --------------------------------------------------------------------------
	/**
	 * Initialize the class.
	 */
	protected void initialize() {
		setName("CustomerMasterBean");
		uiFactory.configureUIComponent(this, UI_PREFIX);

		initializeFields();
		initializeLabels();
		initLayout();

	}

	// --------------------------------------------------------------------------
	/**
	 * Initializes the layout and lays out the components.
	 */
	protected void initLayout() {
		setLayout(new GridBagLayout());

		UIUtilities.layoutComponent(this, CustomerIdLabel, CustomerIdField, 0, 0, false);
		UIUtilities.layoutComponent(this, FullNameLabel, FullNameField, 0, 1, false);
		UIUtilities.layoutComponent(this, SalutationLabel, SalutationField, 0, 2, false);

		UIUtilities.layoutComponent(this, BirthdateLabel, BirthdateField, 0, 3, false);
		UIUtilities.layoutComponent(this, BirthYearLabel, BirthYearField, 0, 4, false);
		UIUtilities.layoutComponent(this, GenderLabel, GenderField, 0, 5, false);
		UIUtilities.layoutComponent(this, PrivacyIssuesLabel, null, 0, 7, false);

		UIUtilities.layoutComponent(this, MailLabel, MailField, 0, 8, false);
		UIUtilities.layoutComponent(this, TelephoneLabel, TelephoneField, 0, 9, false);
		UIUtilities.layoutComponent(this, EmailLabel, EmailField, 0, 10, false);
		UIUtilities.layoutComponent(this, PreferredLanguageLabel, PreferredLanguageField, 0, 11, false);
	}

	// ------------------------------------------------------------------------
	/**
	 * Updates the model for the current settings of this bean.
	 */
	// ------------------------------------------------------------------------
	@Override
	public void updateModel() {
		if (beanModel instanceof CustomerInfoBeanModel) {
			CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;
			if (BirthdateField.isValid()) {
				if (!Util.isEmpty(BirthYearField.getText())) {
					BirthYearField.getLongValue();
					BirthdateField.getDocument();
				}
				model.setBirthdate(BirthdateField.getDate());
			}

			if (!Util.isEmpty(BirthYearField.getText())) {
				model.setBirthYear(BirthYearField.getLongValue());
			}

			model.setMailPrivacy(getMailModel().valueOf((String) MailField.getSelectedItem()));
			model.setTelephonePrivacy(getTelephoneModel().valueOf((String) TelephoneField.getSelectedItem()));
			model.setEmailPrivacy(getEmailModel().valueOf((String) EmailField.getSelectedItem()));

			model.setCustomerName(FullNameField.getText());

			model.setGenderIndex(GenderField.getSelectedIndex());
			model.setSalutation(SalutationField.getText());

			model.setSelectedLanguage(PreferredLanguageField.getSelectedIndex());
		}
	}

	/**
	 * validate fields
	 */
	@Override
	protected boolean validateFields() {
		if (!Util.isEmpty(BirthYearField.getText())) {
			BirthYearField.getLongValue();
			DateDocument bdayDoc = (DateDocument) (BirthdateField.getDocument());
		}
		return (super.validateFields());
	}

	// ---------------------------------------------------------------------
	/**
	 * Update the bean if It's been changed
	 */
	// ---------------------------------------------------------------------
	@Override
	protected void updateBean() {
		if (beanModel instanceof CustomerInfoBeanModel) {
			CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;
			// set edit mode
			boolean editMode = model.getEditableFields();

			if (model.isBirthdateValid()) {

				BirthdateField.setDate(model.getBirthMonthAndDay());
			} else {
				BirthdateField.setText("");
			}
			setupComponent(BirthdateField, editMode, true);
			if (model.isBirthYearValid()) {
				BirthYearField.setLongValue(model.getBirthYear());
			} else {
				BirthYearField.setText("");
			}
			setupComponent(BirthYearField, editMode, true);

			setComboBoxModel(model.getGenderTypes(), GenderField, model.getGenderIndex());
			setupComponent(GenderField, editMode, true);
			GenderField.setFocusable(editMode);
			GenderField.setEnabled(editMode);
			// GenderField.setEditable(editMode);

			CustomerIdField.setText(model.getCustomerID());
			setupComponent(CustomerIdField, editMode, true);
			String mailSetting = getMailModel().valueOf(model.getMailPrivacy());

			MailField.setModel(new ValidatingComboBoxModel(new BooleanComboModel().getValues()));
			MailField.setSelectedItem(mailSetting);
			MailField.setEditable(false);
			MailField.setEnabled(false);
			/*
			 * MailField.setFocusable(editMode); MailField.setEnabled(editMode);
			 */
			/*
			 * MailField.setModel(new ValidatingComboBoxModel(new
			 * BooleanComboModel().getValues()));
			 * MailField.setSelectedItem(mailSetting); setupComponent(MailField,
			 * false, true); MailField.setEditable(false);
			 */
			String phoneSetting = getTelephoneModel().valueOf(model.getTelephonePrivacy());
			TelephoneField.setModel(new ValidatingComboBoxModel(new BooleanComboModel().getValues()));
			TelephoneField.setSelectedItem(phoneSetting);
			TelephoneField.setEnabled(false);
			setupComponent(TelephoneField, false, true);
			String emailSetting = getEmailModel().valueOf(model.getEmailPrivacy());
			EmailField.setModel(new ValidatingComboBoxModel(new BooleanComboModel().getValues()));
			EmailField.setSelectedItem(emailSetting);
			EmailField.setEnabled(false);
			setupComponent(EmailField, editMode, true);
			FullNameField.setText(model.getCustomerName());
			setupComponent(FullNameField, editMode, true);
			SalutationField.setText(model.getSalutation());
			setupComponent(SalutationField, editMode, true);
			if (model.getLanguages() != null) {
				PreferredLanguageField.setModel(new DefaultComboBoxModel(model.getLanguages()));
				PreferredLanguageField.setSelectedIndex(model.getSelectedLanguage());
				PreferredLanguageField.setEnabled(false);
			}

		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Set the properties to be used by this bean
	 *
	 * @param props
	 *            the propeties object
	 */
	// ---------------------------------------------------------------------
	@Override
	public void setProps(Properties props) {
		super.setProps(props);
		getMailModel().setProps(props);
		getTelephoneModel().setProps(props);
		getEmailModel().setProps(props);
		updatePropertyFields();
	}

	// ---------------------------------------------------------------------------
	/**
	 * Update property fields.
	 */
	// ---------------------------------------------------------------------------
	@Override
	protected void updatePropertyFields() {
		DateDocument doc = (DateDocument) BirthdateField.getDocument();
		String dobLabel = retrieveText("BirthdateLabel", BirthdateLabel);
		String yearLabel = retrieveText("BirthYearLabel", BirthYearLabel);

		// Retrieve the localized pattern for the Full year
		SimpleDateFormat dateFormat = DomainGateway.getSimpleDateFormat(getLocale(), LocaleConstantsIfc.DEFAULT_YEAR_FORMAT);

		// Retrieve bundle text for labels
		String label = ((SimpleDateFormat) (doc.getDateFormat())).toLocalizedPattern().toUpperCase(getLocale());
		BirthdateLabel.setText(LocaleUtilities.formatComplexMessage(dobLabel, label));
		BirthYearLabel.setText(LocaleUtilities.formatComplexMessage(yearLabel, dateFormat.toLocalizedPattern().toUpperCase(getLocale())));
		CustomerIdLabel.setText(retrieveText("CustomerIDLabel", CustomerIdLabel));
		FullNameLabel.setText(retrieveText("FullNameLabel", FullNameLabel));
		GenderLabel.setText(retrieveText("GenderLabel", GenderLabel));
		PrivacyIssuesLabel.setText(retrieveText("PrivacyIssuesLabel", PrivacyIssuesLabel));
		SalutationLabel.setText(retrieveText("SalutationLabel", SalutationLabel));

		MailLabel.setText(retrieveText("MailLabel", MailLabel));
		TelephoneLabel.setText(retrieveText("TelephoneLabel", TelephoneLabel));
		EmailLabel.setText(retrieveText("EmailLabel", EmailLabel));
		PreferredLanguageLabel.setText(retrieveText("PreferredLanguageLabel", PreferredLanguageLabel));
		// Associate labels with fields
		SalutationField.setLabel(SalutationLabel);
		FullNameField.setLabel(FirstNameLabel);

		GenderField.setLabel(GenderLabel);

		BirthdateField.setLabel(BirthdateLabel);
		BirthYearField.setLabel(BirthYearLabel);

		MailField.setLabel(MailLabel);
		TelephoneField.setLabel(TelephoneLabel);
		EmailField.setLabel(EmailLabel);
		PreferredLanguageField.setLabel(PreferredCustomerLabel);
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates the information displayed on the screen's if the model's been
	 * changed.
	 */
	// ---------------------------------------------------------------------
	protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible) {
		if (field instanceof ValidatingFieldIfc) {
			((ValidatingFieldIfc) field).getLabel().setVisible(isVisible);
		}

		if (field instanceof JTextField) {
			((JTextField) field).setEditable(isEditable);
		}
		field.setFocusable(isEditable);
		// field.setRequestFocusEnabled(isVisible);
		field.setVisible(isVisible);
	}

	// ---------------------------------------------------------------------
	/**
	 * Convinience method to populate a comboBox
	 *
	 * @param data
	 *            the data to be display in the combo box
	 * @param field
	 *            the actual combo box field receiving the data
	 * @param selected
	 *            index the default selected value
	 */
	// ---------------------------------------------------------------------
	@Override
	protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex) {
		if (data != null) {
			ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);

			field.setModel(model);

			field.setSelectedIndex(selectedIndex);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns default display string.
	 * <P>
	 *
	 * @return String representation of object
	 */
	// ---------------------------------------------------------------------
	@Override
	public String toString() {
		String strResult = new String("Class: CustomerMasterBean (Revision " + getRevisionNumber() + ") @" + hashCode());
		return (strResult);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <P>
	 *
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	@Override
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}

	// --------------------------------------------------------------------------
	/**
	 * main entrypoint - starts the part when it is run as an application
	 *
	 * @param args
	 *            java.lang.String[]
	 */
	// --------------------------------------------------------------------------
	public static void main(java.lang.String[] args) {
		UIUtilities.setUpTest();

		CustomerMasterBean bean = new CustomerMasterBean();

		UIUtilities.doBeanTest(bean);
	}

	protected int getSeparateYear() {
		return separateYear;
	}

	/**
	 * sets the year value when the year occurs in a ui field by itself and is
	 * not part of the day/month field
	 *
	 * @param val
	 */
	protected void setSeparateYear(int val) {
		separateYear = val;
	}

	/*
	 * protected void setYesNoComboBoxModel(String[] data, YesNoComboBox field,
	 * String selectedItem) { if (data != null) { YesNoComboBox model = new
	 * YesNoComboBox();
	 *
	 * field.setModel((ComboBoxModel) model);
	 * field.setSelectedItem(selectedItem); //
	 * field.setSelectedIndex(selectedIndex); } }
	 */
}

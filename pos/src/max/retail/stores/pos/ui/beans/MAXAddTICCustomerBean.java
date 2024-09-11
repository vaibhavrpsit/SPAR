/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *  
 *	Rev 1.3     SEP 13,2017        	Karni Singh			PIN code enable.
 *	Rev 1.2     July 04,2017        Nayya Gupta			Expect only characters in first name and last name
 *	Rev 1.1     Nov 08, 2016		Ashish Yadav		Cardless Loyalty FES
 *	Rev 1.0     Oct 18, 2016		Ashish Yadav		Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.text.MaskFormatter;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXTICCustomerDataTransaction;
import max.retail.stores.domain.ticcustomer.MAXTICCustomerConfig;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.DateDocument;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;

public class MAXAddTICCustomerBean extends ValidatingBean {

	private static final long serialVersionUID = -328908385678741052L;
	/** revision number **/
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	/** customer First Name Label **/
	protected JLabel firstNameLabel = null;

	// changes for Rev1.2 starts
	/** customer First Name field **/
	protected MAXOnlyLettersWithoutSpaceTextField firstNameField = null;

	/** customer Last name Label **/
	protected JLabel lastNameLabel = null;

	/** customer Last name field **/
	protected MAXOnlyLettersWithoutSpaceTextField lastNameField = null;
	// changes for Rev1.2 starts
	/** mobNo Label **/
	protected JLabel mobNoLabel = null;

	/** Mob No field **/
	protected NumericTextField mobNoField = null;

	/** Dob Label **/
	// protected JLabel dobLabel = null;
	// Changes start for rev 1.1 (Cardless Loyalty)
	/** Dob Label **/
	protected JLabel birthDateLabel = null;

	/** Dob field **/
	protected EYSDateField birthDateField = null;

	protected JLabel birthYearLabel = null;

	protected NumericTextField birthYearField = null;
	// Changes end for rev 1.1 (Cardless Loyalty)

	/** Gender Label **/
	protected JLabel genderLabel = null;

	/** Gender field **/
	protected ValidatingComboBox genderField = null;

	/** pinCode Label **/
	protected JLabel pinCodeLabel = null;

	/** pinCode field **/
	protected NumericTextField pinCodeField = null;

	/** Email Label **/
	protected JLabel emailLabel = null;

	/** Email field **/
	protected ConstrainedTextField emailField = null;

	boolean isTicVisibleDB = false;

	boolean isTicMandatoryDB = false;

	List configList = null;

	/**
	 * Class constructor
	 */
	public MAXAddTICCustomerBean() {
		initialize();
	}

	/**
	 * Initializes the class.
	 */
	protected void initialize() {

		setName("MAXCreditCardBean");
		uiFactory.configureUIComponent(this, UI_PREFIX);
		checkVisiblility();
		initComponents();
		initLayout();
	}

	// this function will check the value in the database table and set the
	private void checkVisiblility() {

		MAXTICCustomerDataTransaction customerDataTrans = null;
		try {
			customerDataTrans = (MAXTICCustomerDataTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.TIC_CUSTOMER_CONFIG_TRANSACTION);
			configList = customerDataTrans.getTICCustomerConfig();
		} catch (DataException de) {
			logger.error("Unable to read password policy  for " + configList,
					de);
			// throw de;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (configList != null && configList.size() > 0) {
			MAXTICCustomerConfig ticCustomer = (MAXTICCustomerConfig) configList
					.get(0);

			if (ticCustomer != null
					&& (ticCustomer.getDisplayField().equalsIgnoreCase("Y") || ticCustomer
							.getDisplayField().equalsIgnoreCase("T"))) {
				isTicVisibleDB = true;
			} else {
				isTicVisibleDB = false;
			}
			if (ticCustomer != null
					&& (ticCustomer.getMandatoryField().equalsIgnoreCase("Y") || ticCustomer
							.getMandatoryField().equalsIgnoreCase("T"))) {
				isTicMandatoryDB = true;
			} else {
				isTicMandatoryDB = false;
			}

		}

	}

	/**
	 * Initializes the components.
	 */
	protected void initComponents() {

		firstNameLabel = uiFactory.createLabel("firstNamelabel",
				"firstNamelabel", null, UI_LABEL);
		lastNameLabel = uiFactory.createLabel("lastNamelabel", "lastName",
				null, UI_LABEL);
		mobNoLabel = uiFactory.createLabel("mobNolabel", "mobNo", null,
				UI_LABEL);

		genderLabel = uiFactory.createLabel("genderlabel", "gender", null,
				UI_LABEL);
		pinCodeLabel = uiFactory.createLabel("pinCodelabel", "pinCode", null,
				UI_LABEL);
		// Changes start for rev 1.1 (Cardless Loyalty)
		// dobLabel = uiFactory.createLabel("doblabel", "dob", null, UI_LABEL);
		birthDateLabel = uiFactory.createLabel("birthDateLabel", "birthdate",
				null, UI_LABEL);
		birthYearLabel = uiFactory.createLabel("birthYearLabel", "birthyear",
				null, UI_LABEL);
		// Changes ends for rev 1.1 (Cardless Loyalty)
		emailLabel = uiFactory.createLabel("emaillabel", "email", null,
				UI_LABEL);
		// changes for Rev1.2 starts
		// firstNameField =
		// uiFactory.createValidatingFormattedTextField("firstNameField","LLLLLLLLLLLLLLL","15",
		// "1");
		firstNameField = new MAXOnlyLettersWithoutSpaceTextField("", 1, 15);
		firstNameField.setColumns(13);
		lastNameField = new MAXOnlyLettersWithoutSpaceTextField("", 1, 15);
		lastNameField.setColumns(13);
		// changes for Rev1.2 ends

		mobNoField = uiFactory.createNumericField("mobNoField", "10", "10");

		genderField = uiFactory.createValidatingComboBox("genderField");
		pinCodeField = uiFactory.createNonZeroNumericField("pinCodeField", "6", "6");
		// dobField = uiFactory.createEYSDateField("dobField");
		// Changes start ofr code merging(commenting below line)
		birthDateField = uiFactory.createEYSDateField("birthDateField");
		birthDateField.setFormat(DateDocument.MONTH_DAY);
		birthYearField = uiFactory.createNumericField("birthYearField", "4",
				"4");
		birthYearField.setColumns(7);
		// Chnages ends for code merging
		emailField = uiFactory.createConstrainedField("emailField", "6", "30");
		firstNameField.setRequired(true);
		lastNameField.setRequired(true);
		mobNoField.setRequired(true);
		pinCodeField.setRequired(true); // Rev 1.3 changes
	}

	protected static MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
	}

	/**
	 * Update property fields.
	 */

	protected void updatePropertyFields() {

		firstNameLabel.setText(retrieveText("firstNamelabel", firstNameLabel));
		lastNameLabel.setText(retrieveText("lastNamelabel", lastNameLabel));
		mobNoLabel.setText(retrieveText("mobNolabel", mobNoLabel));
		pinCodeLabel.setText(retrieveText("pinCodelabel", pinCodeLabel)); // Rev
																			// 1.3
																			// changes
		emailLabel.setText(retrieveText("emaillabel", emailLabel)); // Rev 1.3
																	// changes
		if (isTicVisibleDB) {
			genderLabel.setText(retrieveText("genderlabel", genderLabel));
			pinCodeLabel.setText(retrieveText("pinCodelabel", pinCodeLabel));
			// Changes start for Rev 1.1 (Cardless Loyalty)
			// dobLabel.setText(retrieveText("doblabel", dobLabel));
			birthDateLabel.setText(retrieveText("birthDateLabel",
					birthDateLabel));
			birthYearLabel.setText(retrieveText("birthYearLabel",
					birthYearLabel));
			// Chnages ends for Rev 1.1 (Cardless Loyalty)
			emailLabel.setText(retrieveText("emaillabel", emailLabel));
		}

	}

	/**
	 * Initializes the layout and lays out the components.
	 */
	protected void initLayout() {

		setLayout(new GridBagLayout());

		int xValue = 0;

		UIUtilities.layoutComponent(this, firstNameLabel, firstNameField, 0,
				xValue, false);
		xValue++;
		UIUtilities.layoutComponent(this, lastNameLabel, lastNameField, 0,
				xValue, false);
		xValue++;
		UIUtilities.layoutComponent(this, mobNoLabel, mobNoField, 0, xValue,
				false);
		xValue++;

		UIUtilities.layoutComponent(this, genderLabel, genderField, 0, xValue,
				false);
		xValue++;
		UIUtilities.layoutComponent(this, pinCodeLabel, pinCodeField, 0,
				xValue, false);
		xValue++;
		// Chnages start for Rev 1.1 (Cardless Loyalty)
		UIUtilities.layoutComponent(this, birthDateLabel, birthDateField, 0,
				xValue, false);
		xValue++;
		UIUtilities.layoutComponent(this, birthYearLabel, birthYearField, 0,
				xValue, false);
		xValue++;
		// Changes ends for Rev 1.1 (Cardless Loyalty)
		UIUtilities.layoutComponent(this, emailLabel, emailField, 0, xValue,
				false);
		xValue++;

	}

	/**
	 * Updates the information displayed on the screen's if the model's been
	 * changed.
	 */

	protected void updateBean() {
		checkVisiblility();
		// /for the visibility True/False depending on the database value
		if (isTicVisibleDB) {
			genderLabel.setVisible(true);
			pinCodeLabel.setVisible(true);
			// dobLabel.setVisible(true);
			emailLabel.setVisible(true);
			genderField.setVisible(true);
			pinCodeField.setVisible(true);
			// Changes start for Rev 1.1 (Cardless Loyalty)
			birthDateLabel.setVisible(true);
			birthDateField.setVisible(true);
			birthYearLabel.setVisible(true);
			birthYearField.setVisible(true);
			// Changes ends for Rev 1.1 (Cardless Loyalty)
			emailField.setVisible(true);

		} else {
			genderLabel.setVisible(false);
			// pinCodeLabel.setVisible(false); // Rev 1.3 changes
			// dobLabel.setVisible(false);
			// emailLabel.setVisible(false); // Rev 1.3 changes

			genderField.setVisible(false);
			// pinCodeField.setVisible(false); // Rev 1.3 changes
			// Changes start for Rev 1.1 (Cardless Loyalty)
			birthDateLabel.setVisible(false);
			birthDateField.setVisible(false);
			birthYearLabel.setVisible(false);
			birthYearField.setVisible(false);
			// Changes ends for Rev 1.1 (Cardless Loyalty)
			// emailField.setVisible(false); // Rev 1.3 changes

		}
		// /for the mandatory True/False depending on the database value
		if (isTicVisibleDB && isTicMandatoryDB) {
			genderField.setRequired(true);
			pinCodeField.setRequired(true);
			// Changes starts for Rev 1.1 (Cardless Loyalty)
			birthDateField.setRequired(true);
			birthYearField.setRequired(true);
			// Changes ends for Rev 1.1 (Cardless Loyalty)

		} else {
			genderField.setRequired(false);
			// pinCodeField.setRequired(false); // Rev 1.3 changes
			// Changes starts for Rev 1.1 (Cardless Loyalty)
			birthDateField.setRequired(false);
			birthYearField.setRequired(false);
			// Changes ends for Rev 1.1 (Cardless Loyalty)
		}

		if (beanModel instanceof MAXTICCustomerModel) {

			MAXTICCustomerModel model = (MAXTICCustomerModel) beanModel;
			if (model.getFirstName() != null)
				firstNameField.setText(model.getFirstName());
			if (model.getLastName() != null)
				lastNameField.setText(model.getLastName());
			mobNoField.setText(model.getMobile());
			pinCodeField.setText(model.getPinCode()); // Rev 1.3 changes
			emailField.setText(model.getEmail()); // Rev 1.3 changes

			if (isTicVisibleDB) {
				ValidatingComboBoxModel valBoxModel = new ValidatingComboBoxModel(
						model.getGenderTypes());
				genderField.setModel(valBoxModel);

				emailField.setText(model.getEmail());
				// Changes starts for Rev 1.1 (Cardless Loyalty)
				birthDateField.setText(model.getBirthDate());

				birthYearField.setText(model.getBirthYear());
				// Changes ends for Rev 1.1 (Cardless Loyalty)
				pinCodeField.setText(model.getPinCode());

				if (model.getGender() != null
						&& !model.getGender().equalsIgnoreCase("")) {

					genderField.setSelectedItem(model.getGender());

				} else {
					genderField.setSelectedIndex(Gender.GENDER_UNSPECIFIED);

				}
			}

		}
	}

	public void updateModel() {
		if (beanModel instanceof MAXTICCustomerModel) {
			MAXTICCustomerModel model = (MAXTICCustomerModel) beanModel;

			model.setFirstName(firstNameField.getText());
			model.setLastName(lastNameField.getText());
			model.setMobile(mobNoField.getText());
			model.setPinCode(pinCodeField.getText()); // Rev 1.3 changes
			model.setEmail(emailField.getText()); // Rev 1.3 changes
			if (isTicVisibleDB) {

				if (genderField.getSelectedItem() != null
						&& !genderField.getSelectedItem().toString()
								.equalsIgnoreCase("")) {
					model.setGender((String) genderField.getSelectedItem());
				}

				model.setPinCode(pinCodeField.getText());
				// Changes starts for Rev 1.1 (Cardless Loyalty)
				model.setBirthDate(birthDateField.getText());
				model.setBirthYear(birthYearField.getText());
				// Changes ends for Rev 1.1 (Cardless Loyalty)
				model.setEmail(emailField.getText());
				model.setConfigList(configList);

			}

		}

	}
}

/* *****************************************************************************************
 * rev 1.0       Akhilesh kumar       splitting dob field to dob date and year  end
 * Copyright (c) 2010   Lifestyle India Pvt. Ltd..    All Rights Reserved.
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-14-2015
 * *******************************************************************************************/

package max.retail.stores.pos.services.customer.tic;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXTICCustomerDataTransaction;
import max.retail.stores.domain.ticcustomer.MAXTICCustomerConfig;
import max.retail.stores.pos.ui.beans.MAXTICCustomerModel;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
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

	/** customer First Name field **/
	protected AlphaNumericTextField firstNameField = null;

	/** customer Last name Label **/
	protected JLabel lastNameLabel = null;

	/** customer Last name field **/
	protected AlphaNumericTextField lastNameField = null;

	/** mobNo Label **/
	protected JLabel mobNoLabel = null;

	/** Mob No field **/
	protected NumericTextField mobNoField = null;

	
	
	 ///rev 1.0 start
	/** Dob Label **/
	protected JLabel birthDateLabel = null;

	/** Dob field **/
	protected EYSDateField birthDateField = null;
	
	
	protected JLabel birthYearLabel = null;
	
	
    protected NumericTextField birthYearField = null;
    
    
    ///start 1.0 end
    
    
    

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

		setName("CreditCardBean");
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
		birthDateLabel = uiFactory.createLabel("birthDateLabel", "birthdate", null, UI_LABEL);
		birthYearLabel = uiFactory.createLabel("birthYearLabel", "birthyear", null, UI_LABEL);
		
		emailLabel = uiFactory.createLabel("emaillabel", "email", null,
				UI_LABEL);

		firstNameField = uiFactory.createAlphaNumericField("firstNameField",
				"1", "15");
		lastNameField = uiFactory.createAlphaNumericField("lastNameField", "1",
				"15");
		mobNoField = uiFactory.createNumericField("mobNoField", "10", "10");

		genderField = uiFactory.createValidatingComboBox("genderField");
		pinCodeField = uiFactory.createNumericField("pinCodeField", "6", "6");
		
		
		//start rev 1.0
		birthDateField = uiFactory.createEYSDateField("birthDateField");
		birthDateField.setFormat(DateDocument.MONTH_DAY);
		birthYearField = uiFactory.createNumericField("birthYearField", "4", "4");
	    birthYearField.setColumns(7);	    
	    //end rev 1.0
	        
	        
		emailField = uiFactory.createConstrainedField("emailField", "6", "30");

		firstNameField.setRequired(true);
		lastNameField.setRequired(true);
		mobNoField.setRequired(true);

	}

	/**
	 * Update property fields.
	 */

	@Override
	protected void updatePropertyFields() {

		firstNameLabel.setText(retrieveText("firstNamelabel", firstNameLabel));
		lastNameLabel.setText(retrieveText("lastNamelabel", lastNameLabel));
		mobNoLabel.setText(retrieveText("mobNolabel", mobNoLabel));

		if (isTicVisibleDB) {
			genderLabel.setText(retrieveText("genderlabel", genderLabel));
			pinCodeLabel.setText(retrieveText("pinCodelabel", pinCodeLabel));
			
			// rev start 1.0
			birthDateLabel.setText(retrieveText("birthDateLabel", birthDateLabel));
			birthYearLabel.setText(retrieveText("birthYearLabel", birthYearLabel));
			// rev end 1.0 
			
			
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
		
		
		
		
		///rev 1.0 start
		UIUtilities.layoutComponent(this, birthDateLabel, birthDateField, 0, xValue, false);
		xValue++;
		UIUtilities.layoutComponent(this, birthYearLabel, birthYearField, 0, xValue, false);
		xValue++;
		///rev 1.0 end
		
		
		UIUtilities.layoutComponent(this, emailLabel, emailField, 0, xValue,
				false);
		xValue++;

	}

	/**
	 * Updates the information displayed on the screen's if the model's been
	 * changed.
	 */

	@Override
	protected void updateBean() {
		checkVisiblility();


		///for the visibility True/False depending on the database value
		if (isTicVisibleDB) {
			genderLabel.setVisible(true);
			pinCodeLabel.setVisible(true);
			///rev 1.0 start
			birthDateLabel.setVisible(true);
			birthDateField.setVisible(true);
			birthYearLabel.setVisible(true);
			birthYearField.setVisible(true);
			///rev 1.0 end
			emailLabel.setVisible(true);
			genderField.setVisible(true);
			pinCodeField.setVisible(true);
		
			emailField.setVisible(true);

		} else {
			genderLabel.setVisible(false);
			pinCodeLabel.setVisible(false);
			///rev 1.0 start
			birthDateLabel.setVisible(false);
			birthDateField.setVisible(false);
			birthYearLabel.setVisible(false);
			birthYearField.setVisible(false);
			///rev 1.0 end
			emailLabel.setVisible(false);

			genderField.setVisible(false);
			pinCodeField.setVisible(false);
			
			emailField.setVisible(false);

		}
		///for the mandatory True/False depending on the database value
		if (isTicVisibleDB && isTicMandatoryDB) {
			genderField.setRequired(true);
			pinCodeField.setRequired(true);
			///rev 1.0 start
			birthDateField.setRequired(true);
			birthYearField.setRequired(true);
			///rev 1.0 end
			

		} else {
			genderField.setRequired(false);
			pinCodeField.setRequired(false);
			
			///rev 1.0 start
			birthDateField.setRequired(false);
			birthYearField.setRequired(false);
			///rev 1.0 end
		}

		if (beanModel instanceof MAXTICCustomerModel) {

			MAXTICCustomerModel model = (MAXTICCustomerModel) beanModel;

			firstNameField.setText(model.getFirstName());
			lastNameField.setText(model.getLastName());
			mobNoField.setText(model.getMobile());

			if (isTicVisibleDB) {
				ValidatingComboBoxModel valBoxModel = new ValidatingComboBoxModel(
						model.getGenderTypes());
				genderField.setModel(valBoxModel);

				emailField.setText(model.getEmail());
				
			//	rev 1.0 start
				birthDateField.setText(model.getBirthDate());
				
				birthYearField.setText(model.getBirthYear());
				
			//	rev 1.0 end
				
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

	@Override
	public void updateModel() {
		if (beanModel instanceof MAXTICCustomerModel) {
			MAXTICCustomerModel model = (MAXTICCustomerModel) beanModel;

			model.setFirstName(firstNameField.getText());
			model.setLastName(lastNameField.getText());
			model.setMobile(mobNoField.getText());

			if (isTicVisibleDB) {

				if (genderField.getSelectedItem() != null
						&& !genderField.getSelectedItem().toString()
						.equalsIgnoreCase("")) {
					model.setGender((String) genderField.getSelectedItem());
				}

				model.setPinCode(pinCodeField.getText());
				
				model.setBirthDate(birthDateField.getText());
				model.setBirthYear(birthYearField.getText());
					
				model.setEmail(emailField.getText());
				model.setConfigList(configList);

			}

		}

	}

}

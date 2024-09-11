/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.1 07/June/2013  Jyoti   Bug 6275 - Credit/Debit Tender- Offline Credit/Debit Card Detail Date Label should be change to mm/yyyy
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.DateDocument;
import oracle.retail.stores.pos.ui.beans.DiscPercentEntryBean;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.ValidatingTextField;

// --------------------------------------------------------------------------
/**
 * This Bean displays a Discount percent field and a Reason Code Form.
 * <P>
 * It uses the DecimalWithReasonBeanModel. This bean supports an editable combo
 * box. To make the combo box editable, add an EditableList bean property with
 * property value true in the workpanel of the corresponding OVERLAYSCREEN in
 * the appropriate uicfg.xml file. By default, it is not editable.
 *
 * @see com.extendyourstore.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: 1.1 $
 */
// --------------------------------------------------------------------------
public class MAXCreditCardBean extends ValidatingBean
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4747456719610757135L;

	public static String CARD_NUMBER = "Card Number:";

	public static String BANK = "Bank:";

	public static String AUTH_CODE = "Authorization Code:";

	public static String EXPIRY_DATE = "Expiry Date:";

	public static String DATE_FORMAT = "MM/YYYY";    //Rev 1.2 changes

	
	/** label for the Card Number */
	protected JLabel cardNumberLabel;

	/** label for the bank list */
	protected JLabel bankLabel = null;

	/** text for the label */
	protected String labelText = "Reason Code:";

	/** tag for the label */
	protected String labelTags = "ReasonCodeColonLabel";

	protected NumericTextField cardNumberField;

	protected ValidatingTextField test;

	protected ConstrainedTextField test1;

	protected JLabel expiryDateLabel;

	protected JLabel authCodeLabel;

	protected JLabel dateFormatLabel;

	protected EYSDateField expiryDateField;

	protected ConstrainedTextField authCodeField;

	protected ValidatingTextField dateFormatField;

	/** the list of bank names */
	protected JList choiceList;

	protected ValidatingComboBox bankList = null;

	/** vector with list of tags for bank names * */
	protected Vector tag_list = null;

	/** vector with list of tag ids for bank names * */
	protected Vector tag_list_ids = null;

	/** Indicates if code id should prepend code description in list * */
	protected boolean prependCodeID = false;

	protected JScrollPane scrollPane = null;
	
//	protected MAXCreditCardBeanModel beanModel = null;

	// --------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	// -------------------------------------------------------------------------
	public MAXCreditCardBean()
	{
		super();
		


	}

	// --------------------------------------------------------------------------
	/**
	 * Initialize the class.
	 */
	// -------------------------------------------------------------------------
	public void configure()
	{
		uiFactory.configureUIComponent(this, UI_PREFIX);

		initComponents();
		initLayout();

		setName("MAXCreditCardBean");
		setLabelText("Sample Label 1");
		setLabelTags("Sample Label 2");
	}

	// --------------------------------------------------------------------------
	/**
	 * Initialize the display components.
	 */
	// -------------------------------------------------------------------------
	protected void initComponents()
	{

		bankList = uiFactory.createValidatingComboBox("bankListField");

		cardNumberLabel = uiFactory.createLabel(CARD_NUMBER, null, UI_LABEL);


		//cardNumberField = uiFactory.createNumericField("cardNumberField", "16", "16");
		// Commented Above line for making Card Number field to minimum required length to 1 digit
		cardNumberField = uiFactory.createNumericField("cardNumberField", "1", "16");
		// END Change Code By Mahendra

		cardNumberField.setHorizontalAlignment(SwingConstants.LEFT);
		cardNumberField.setColumns(16);

		expiryDateField = new EYSDateField();
		// changes added for bug no 1052 : roshana
		expiryDateField.setFormat(DateDocument.MONTH_YEAR);
		/* expiryDateField.setFormat(DateDocument.CREDITCARD_MONTH_YEAR);*/
		expiryDateField.setName("expiryDateField");
		expiryDateField.setColumns(5);
		expiryDateField.setRequired(true);
		expiryDateField.setMinLength(4);
		expiryDateField.enable(true);
		//Added to make the expiry date field mandatory.
		setFieldRequired(expiryDateField,true);


		dateFormatField = new ValidatingTextField("dateFormatField");
		dateFormatField.setVisible(false);
		dateFormatField.setName("dateFormatField");

		bankList = uiFactory.createValidatingComboBox("BankListField");
		bankLabel = uiFactory.createLabel(BANK, null, UI_LABEL);
		expiryDateLabel = uiFactory.createLabel(EXPIRY_DATE, null, UI_LABEL);
		dateFormatLabel = uiFactory.createLabel(DATE_FORMAT, null, UI_LABEL);
		authCodeLabel = uiFactory.createLabel(AUTH_CODE, null, UI_LABEL);

		// START Commented Below Line and Changed the minimum length to 1

		// authCodeField = uiFactory.createConstrainedField("AuthCodeField",
		// "4", "8");

		authCodeField = uiFactory.createConstrainedField("AuthCodeField", "1",
		"10");
		// END Commented Below Line and Changed the minimum length to 1

		// authCodeField.setName("AuthCodeField");
		authCodeField.setHorizontalAlignment(SwingConstants.LEFT);
		authCodeField.setColumns(16);

		scrollPane = uiFactory.createSelectionList("bankList", "large");
		scrollPane.setSize(100, 200);
		choiceList = (JList)scrollPane.getViewport().getView();
		choiceList.setBorder(BorderFactory.createLineBorder(Color.gray));
		choiceList.setSize(15, 5);
	}

	// --------------------------------------------------------------------------
	/**
	 * Overrides JPanel setVisible() method to request focus.
	 *
	 * @param aFlag True to make this component visible
	 */
	// --------------------------------------------------------------------------
	/*
	 * public void setVisible(boolean aFlag) { System.out.println("Test");
	 * super.setVisible(aFlag); if (aFlag && !errorFound()) {
	 * setCurrentFocus(cardNumberField); } }
	 */

	// --------------------------------------------------------------------------
	/**
	 * Activates this bean.
	 */
	// -------------------------------------------------------------------------
	public void activate()
	{
		super.activate();
		cardNumberField.addFocusListener(this);
		choiceList.addFocusListener(this);
		authCodeField.addFocusListener(this);
		expiryDateField.addFocusListener(this);
	}

	// --------------------------------------------------------------------------
	/**
	 * Deactivates this bean.
	 */
	// -------------------------------------------------------------------------
	public void deactivate()
	{
		super.deactivate();
		cardNumberField.removeFocusListener(this);
		choiceList.removeFocusListener(this);
		authCodeField.removeFocusListener(this);
		expiryDateField.addFocusListener(this);
	
	}

	/**
	 * Initializes the layout and lays out the components.
	 */
	// -------------------------------------------------------------------------
	protected void initLayout()
	{
		JLabel[] labels = { cardNumberLabel, expiryDateLabel, dateFormatLabel, bankLabel,authCodeLabel};
		JComponent[] components = { cardNumberField, expiryDateField, dateFormatField, bankList,authCodeField };
		UIUtilities.layoutDataPanel(this, labels, components);
	}

	// --------------------------------------------------------------------------
	/**
	 * This updates the local bean model.
	 *
	 * @see com.extendyourstore.pos.ui.beans.DecimalWithReasonBeanModel
	 */
	// -------------------------------------------------------------------------
	public void updateModel()
	{
		MAXCreditCardBeanModel myModel = (MAXCreditCardBeanModel)beanModel;
		myModel.setAuthCode(this.authCodeField.getText());
		myModel.setCardNumber(this.cardNumberField.getText());
		String date = this.expiryDateField.getText();
		if (!date.equalsIgnoreCase(""))
		{
			myModel.setExpirationDate(date);
		}
		else
		{
			myModel.setExpirationDate(this.expiryDateField.getText());
		}
		myModel.setSelectedBank(this.bankList.getSelectedIndex());
		String bankName = String.valueOf(this.bankList.getSelectedItem());
		myModel.setSelectedBankName(bankName);
		myModel.setBankName(String.valueOf(""));
	}

	protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex)
	{
		if (data != null)
		{
			ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);
			field.setModel(model);
			field.setSelectedIndex(selectedIndex);
		}
	}
//	public void updateBean() {
//
//		String coupons[] = beanModel.getCouponList();
//
//		if (coupons != null) {
//
//			couponList.setListData(coupons);
//
//		}
//		String selection = beanModel.getSelectedCoupon();
//		couponList.setSelectedValue(selection, true);
//
//		setCurrentFocus(couponList);
//
//	}


	// --------------------------------------------------------------------------
	/**
	 * This method updates the Bean if when changed by setModel
	 *
	 * @see #setModel(com.extendyourstore.foundation.manager.gui.UIModelIfc
	 *      model)
	 */
	// -------------------------------------------------------------------------
	protected void updateBean()
	{
		MAXCreditCardBeanModel myModel = (MAXCreditCardBeanModel)beanModel;
		String[] choices = null;


		expiryDateField.setRequired(true);
		expiryDateField.setMinLength(4);
		expiryDateField.enable(true);
			choices = new String[myModel.getBankDes().length];
			choices = myModel.getBankDes();
			if (choices != null) {

				choiceList.setListData(choices);

			}

		choiceList.setListData(choices);
		choiceList.setSelectedIndex(myModel.getSelectedBank());
		this.authCodeField.setText(myModel.getAuthCode());
		this.cardNumberField.setText(myModel.getCardNumber());
		this.expiryDateField.setText(myModel.getExpirationDate());
		setComboBoxModel(choices, bankList, myModel.getSelectedBank());
		if (myModel.getExpirationDate().equalsIgnoreCase(""))
		{
			setCurrentFocus(expiryDateField);
			expiryDateField.requestFocus();
		}
		else
		{
			setCurrentFocus(authCodeField);
		}

	}


	protected void setFocusToFirst()
	{
		MAXCreditCardBeanModel myModel = (MAXCreditCardBeanModel)beanModel;
		if(myModel != null)
		{
			if (myModel.getCardNumber().equalsIgnoreCase(""))
			{
				setCurrentFocus(cardNumberField);
				cardNumberField.requestFocus();
			}

			else
			{
				setCurrentFocus(expiryDateField);
			}

		}
		else
		{
			super.setFocusToFirst();
		}
	}


	// --------------------------------------------------------------------------
	/**
	 * Provides uniform way to display choices
	 *
	 * @param tag The internationalizable reason code description key
	 * @return list entry corresponding to a given reason code
	 */
	// -------------------------------------------------------------------------
	/*
	 * public String getDisplayChoice(String tag) {
	 *
	 * String displayChoice = retrieveText(tag,tag); if (!Util.isEmpty(tag)) {
	 * retrieveText(tag,tag); if (prependCodeID) { int selectedCodeIndex =
	 * tag_list.indexOf(tag); if (!(selectedCodeIndex == -1)) { displayChoice =
	 * tag_list_ids.elementAt(selectedCodeIndex) + CHOICE_SEPARATOR +
	 * displayChoice; } } } return displayChoice; }
	 */

	// --------------------------------------------------------------------------
	/**
	 * Sets the label for the selection list field.
	 *
	 * @param text the label
	 */
	// -------------------------------------------------------------------------
	public void setLabelText(String text)
	{
		labelText = text;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the label tag for the selection list field.
	 *
	 * @param text the label
	 */
	// -------------------------------------------------------------------------
	public void setLabelTags(String text)
	{
		labelTags = text;
		updatePropertyFields();
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates property-based fields.
	 */
	// ---------------------------------------------------------------------
	protected void updatePropertyFields()
	{
		super.updatePropertyFields();

		cardNumberLabel.setText(retrieveText(CARD_NUMBER, CARD_NUMBER));
		cardNumberField.setLabel(cardNumberLabel);
		bankLabel.setText(retrieveText(BANK, BANK));
		// choiceList.setLabel(bankLabel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns default display string.
	 *
	 * @return String representation of object
	 */
	// -------------------------------------------------------------------------
	public String toString()
	{
		return new String("Class: " + Util.getSimpleClassName(this.getClass()) + "(Revision " + getRevisionNumber()
				+ ") @" + hashCode());
	}

	// -------------------------------------------------------------------------
	/**
	 * main entrypoint - starts the part when it is run as an application
	 *
	 * @param args java.lang.String[]
	 */
	// -------------------------------------------------------------------------
	public static void main(java.lang.String[] args)
	{
		UIUtilities.setUpTest();

		DiscPercentEntryBean bean = new DiscPercentEntryBean();
		bean.activate();

		UIUtilities.doBeanTest(bean);
	}
}

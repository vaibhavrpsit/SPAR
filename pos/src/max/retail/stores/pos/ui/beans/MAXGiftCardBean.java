/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.ui.beans;

// java imports
import java.math.BigDecimal;

import javax.swing.JLabel;

import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//---------------------------------------------------------------------
/**
 * 
 This bean is used to display information about Gift Cards. No user input.
 * 
 * @version $Revision: 6$ $EKW;
 */
// ---------------------------------------------------------------------
public class MAXGiftCardBean extends BaseBeanAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9060223009630043180L;

	/** version number from revision system */
	public static final String revisionNumber = "$Revision: 6$ $EKW;";

	// label and field placeholder constants
	public static final int CARD_NUMBER = 0;
	public static final int INIT_BALANCE = 1;
	public static final int CARD_AMOUNT = 2;
	public static final int MAX_FIELDS = 3;

	public static final String NOT_AVAILABLE = "not available";
	public static final String NOT_AVAILABLE_TAG = "NotAvailableLabel";
	public static final String NOT_APPLICABLE_TAG = "NotApplicable";

	public static final String LabelTagRemainingBalance = "RemainingBalance";
	public static final String LabelTextRemainingBalance = "Remaining Balance:";

	public static final String[] labelText = { "Gift Card Number:", "Initial Balance:", "Gift Card Amount:" };

	public static final String[] labelTags = { "GiftCardNumberLabel", "InitialBalanceLabel", "GiftCardAmountLabel" };

	/** array of labels */
	protected JLabel[] labels = null;

	/** array of display fields */
	protected JLabel[] fields = null;

	/** the bean model */
	protected GiftCardBeanModel beanModel = null;

	/**
	 * flag indicating the model has changed
	 * 
	 * @deprecated as of release 5.5 obsolete
	 */
	protected boolean dirtyModel = true;

	// --------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public MAXGiftCardBean() {
		super();
		initialize();
	}

	// --------------------------------------------------------------------------
	/**
	 * Initialize the class.
	 */
	protected void initialize() {
		setName("GiftCardInquiryBean");
		uiFactory.configureUIComponent(this, UI_PREFIX);

		initComponents();
		initLayout();

	}

	// --------------------------------------------------------------------------
	/**
	 * Initialize the display components.
	 */
	protected void initComponents() {

		labels = new JLabel[MAX_FIELDS];
		fields = new JLabel[MAX_FIELDS];

		for (int i = 0; i < MAX_FIELDS; i++) {
			labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
			fields[i] = uiFactory.createLabel(NOT_AVAILABLE, null, UI_LABEL);
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Layout the components.
	 */
	public void initLayout() {
		UIUtilities.layoutDataPanel(this, labels, fields);
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the information to be shown by this bean.
	 * 
	 * @param model
	 *            UIModelIfc
	 */
	public void setModel(UIModelIfc model) {
		if (model == null) {
			throw new NullPointerException("Attempt to set GiftCardBean model to null");
		}
		if (model instanceof GiftCardBeanModel) {
			beanModel = (GiftCardBeanModel) model;
			dirtyModel = true;
			updateBean();
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Update the bean if the model has changed.
	 */
	protected void updateBean() {
		labels[CARD_NUMBER].setText("");
		labels[INIT_BALANCE].setText("");
		labels[CARD_AMOUNT].setText("");

		fields[CARD_NUMBER].setText("");
		fields[INIT_BALANCE].setText("");
		fields[CARD_AMOUNT].setText("");

		if ((beanModel.getGiftCardStatus() != null)) {
			if (beanModel.getGiftCardStatus().equals(MAXTenderAuthConstantsIfc.RELOAD) || beanModel.getGiftCardStatus().equals(MAXTenderAuthConstantsIfc.ACTIVE)) {
				if (beanModel.getGiftCardAmount() != null) {
					// I18N change - remove ISO code
					String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(), getLocale());
					fields[CARD_AMOUNT].setText(amount);
					labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT], labelText[CARD_AMOUNT]));
				}
			} else // inquiry, return any status, if fail, the status is empty
					// string
			{
				if (beanModel.getGiftCardNumber() != null) {
					labels[CARD_NUMBER].setText(retrieveText(labelTags[CARD_NUMBER], labelText[CARD_NUMBER]));
					fields[CARD_NUMBER].setText(beanModel.getGiftCardNumber());
				}
				// Retrieve currency format
				if (beanModel.getGiftCardInitialBalance() != null) {
					labels[INIT_BALANCE].setText(retrieveText(labelTags[INIT_BALANCE], labelText[INIT_BALANCE]));
					if (beanModel.isValidInquriy()) {
						String balance = getCurrencyService().formatCurrency(beanModel.getGiftCardInitialBalance(), getLocale());
						fields[INIT_BALANCE].setText(balance);
					} else {
						fields[INIT_BALANCE].setText("");
					}
				}
				if (beanModel.getGiftCardAmount() != null) {
					labels[CARD_AMOUNT].setText(retrieveText(LabelTagRemainingBalance, LabelTextRemainingBalance));
					if (beanModel.isValidInquriy()) {
						String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(), getLocale());
						fields[CARD_AMOUNT].setText(amount);
					} else {
						fields[CARD_AMOUNT].setText("");
					}
				}
			}
		} else {
			// activation fail, request reenter gift card number.
			// show gift card amount at this time.
			if (beanModel.getGiftCardAmount() != null) {
				String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(), getLocale());
				fields[CARD_AMOUNT].setText(amount);
				labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT], labelText[CARD_AMOUNT]));
			}
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Gets the POSBaseBeanModel associated with this bean.
	 * 
	 * @return the POSBaseBeanModel associated with this bean.
	 */
	// -----------------------------------------------------------------------
	public POSBaseBeanModel getPOSBaseBeanModel() {
		return beanModel;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Update property fields.
	 */
	// ---------------------------------------------------------------------------
	protected void updatePropertyFields() {
		for (int i = 0; i < MAX_FIELDS; i++) {
			labels[i].setText(retrieveText(labelTags[i], labels[i]));

			// check for not available
			if (Util.isObjectEqual(fields[i].getText(), NOT_AVAILABLE)) {
				fields[i].setText(retrieveText(NOT_AVAILABLE_TAG, fields[i]));
			}
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
	public String toString() {
		String strResult = new String("Class: GiftCardBean (Revision " + getRevisionNumber() + ") @" + hashCode());
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
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}

	// ---------------------------------------------------------------------
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	// ---------------------------------------------------------------------
	public static void main(java.lang.String[] args) {
		UIUtilities.setUpTest();

		MAXGiftCardBeanModel model = new MAXGiftCardBeanModel();
		model.setGiftCardNumber("20020012");
		model.setGiftCardInitialBalance(new BigDecimal("100.00"));
		model.setGiftCardAmount(new BigDecimal("49.99"));
		model.setGiftCardStatus("Active");

		MAXGiftCardBean bean = new MAXGiftCardBean();
		bean.setModel(model);

		UIUtilities.doBeanTest(bean);

	}
}

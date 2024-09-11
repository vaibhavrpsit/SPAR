package max.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

public class MAXUserDefinedCouponBean extends ValidatingBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5309240759181890689L;

	public static final String revisionNumber = "$Revision: 4$";

	// The bean model
	protected MAXUserDefinedCouponBeanModel beanModel = null;

	// Shipping Method Label
	protected JLabel quantityLabel = null;
	protected JLabel amountLabel = null;

	// ship via label tag
	protected static String QUANTITY_LABEL = "QuantityLabel";

	protected static String AMOUNT_LABEL = "AmountLabel";

	protected NumericTextField quantityField = null;

	protected CurrencyTextField amountField = null;

	public MAXUserDefinedCouponBean() {
		super();
		initialize();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initialize the class and its screen members.
	 */
	// ----------------------------------------------------------------------------
	protected void initialize() {
		setName("MAXUserDefinedCouponBean");
		uiFactory.configureUIComponent(this, UI_PREFIX);

		initializeLabels();
		initializeFields();
		layoutComponents();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initialize the labels on this screen
	 */
	// ----------------------------------------------------------------------------
	protected void initializeLabels() {

		quantityLabel = uiFactory.createLabel("quantityLabel", null, UI_LABEL);
		amountLabel = uiFactory.createLabel("amountLabel", null, UI_LABEL);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initialize the fields on this screens.
	 */
	// ----------------------------------------------------------------------------
	protected void initializeFields() {

		quantityField = uiFactory.createNumericField("quantityField",
				"1", "2");
		amountField = uiFactory.createCurrencyField("amountField", "false",
				"false", "false");

	}

	// ----------------------------------------------------------------------------
	/**
	 * Layout the components on this screen
	 */
	// ----------------------------------------------------------------------------
	protected void layoutComponents() {
		JLabel[] labels = { amountLabel, quantityLabel };

		JComponent[] components = { amountField, quantityField };

		setLayout(new GridBagLayout());
		UIUtilities.layoutDataPanel(this, labels, components);
	}

	// --------------------------------------------------------------------------
	/**
	 * Overrides the inherited setVisible().
	 * 
	 * @param value
	 *            boolean
	 */
	public void setVisible(boolean value) {
		

		if (value && !errorFound()) {
			setCurrentFocus(amountField);
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Gets the POSBaseBeanModel for validation of the current settings of the
	 * bean.
	 * 
	 * @return the POSBaseBeanModel for the current values.
	 */
	// ------------------------------------------------------------------------
	public POSBaseBeanModel getPOSBaseBeanModel() {
		return beanModel;
	}

	// ------------------------------------------------------------------------
	/**
	 * Calls setchangeStateValue to determine if any of the fields were changed.
	 * Updates the model with the newest values from the bean.
	 */
	// ------------------------------------------------------------------------
	public void updateModel() {

		if (beanModel != null) {
			if (!quantityField.getText().equals(""))
				beanModel
						.setQuantity(Integer.parseInt(quantityField.getText()));
			else
				beanModel.setQuantity(0);
			if (!amountField.getText().equals(""))
				beanModel.setAmount(DomainGateway
						.getBaseCurrencyInstance(amountField.getText()));
			else
				beanModel.setAmount(DomainGateway.getBaseCurrencyInstance());
		}
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the model for the current settings of this bean.
	 * 
	 * @param model
	 *            the model for the current values of this bean
	 */
	// ------------------------------------------------------------------------
	public void setModel(UIModelIfc model) {
		if (model == null) {
			throw new NullPointerException(
					"Attempt to set MAXUserDefinedCouponBean model to null");
		} else {
			if (model instanceof MAXUserDefinedCouponBeanModel) {
				beanModel = (MAXUserDefinedCouponBeanModel) model;
				updateBean();
			}
		}
	}

	// ------------------------------------------------------------------------
	/**
	 * Updates the bean and changes fields based upon whether a layaway is in
	 * progress
	 */
	// ------------------------------------------------------------------------
	public void updateBean() {
		if (beanModel != null)

		{
			if (beanModel.getQuantity() != 0)
				quantityField.setText(beanModel.getQuantity() + "");
			if (beanModel.getAmount() != null)
				amountField.setText(beanModel.getAmount().getStringValue());
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * The framework calls this method just before display
	 */
	// ----------------------------------------------------------------------------
	public void activate() {
		super.activate();
		updateModel();
	}

	// ----------------------------------------------------------------------------
	/**
	 * deactivate any settings made by this bean to external entities
	 */
	// ----------------------------------------------------------------------------
	public void deactivate() {
		super.deactivate();
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates property-based fields.
	 **/
	// ---------------------------------------------------------------------
	protected void updatePropertyFields() { // begin updatePropertyFields()
		if (quantityLabel != null) {
			quantityLabel.setText("Coupon Quantiity:");
		}
		if (amountLabel != null) {
			amountLabel.setText(retrieveText(AMOUNT_LABEL,
					amountLabel.getText()));
		}
	} // end updatePropertyFields()

}
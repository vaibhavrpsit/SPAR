
package max.retail.stores.pos.ui.beans;

// Java imports
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBean;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

//----------------------------------------------------------------------------
/**
 * Contains the visual presentation for Shipping Method Information $Revision:
 * 4$
 */
// ----------------------------------------------------------------------------
public class MAXCouponBean extends ValidatingBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3886044570289586569L;

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 4$";

	// The bean model
	protected MAXCouponBeanModel beanModel = null;

	// Shipping Method Label
	protected JLabel couponListLabel = null;

	// ship via label tag
	protected static String COUPON_LIST_LABEL = "CouponListLabel";

	// Container of method lists
	protected JList couponList = null;

	// Scroll long list of departments
	protected JScrollPane couponListScrollPane = null;

	// ----------------------------------------------------------------------------
	/**
	 * Default class Constructor and initializes its components.
	 */
	// ----------------------------------------------------------------------------
	public MAXCouponBean() {
		super();
		initialize();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initialize the class and its screen members.
	 */
	// ----------------------------------------------------------------------------
	protected void initialize() {
		setName("MAXCouponBean");
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

		couponListLabel = uiFactory.createLabel("couponListLabel", null,
				UI_LABEL);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Initialize the fields on this screens.
	 */
	// ----------------------------------------------------------------------------
	protected void initializeFields() {
		couponListScrollPane = uiFactory.createSelectionList("couponList",
				"large");
		couponList = (JList) couponListScrollPane.getViewport().getView();

	}

	// ----------------------------------------------------------------------------
	/**
	 * Layout the components on this screen
	 */
	// ----------------------------------------------------------------------------
	protected void layoutComponents() {
		JLabel[] labels = { couponListLabel };

		JComponent[] components = { couponListScrollPane };

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
		super.setVisible(value);

		if (value && !errorFound()) {
			setCurrentFocus(couponList);
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
		
		beanModel.setSelectedCouponIndex(couponList.getSelectedIndex());

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
					"Attempt to set MAXCouponBean model to null");
		} else {
			if (model instanceof MAXCouponBeanModel) {
				beanModel = (MAXCouponBeanModel) model;
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

		String coupons[] = beanModel.getCouponList();

		if (coupons != null) {

			couponList.setListData(coupons);

		}
		String selection = beanModel.getSelectedCoupon();
		couponList.setSelectedValue(selection, true);

		setCurrentFocus(couponList);

	}

	// ----------------------------------------------------------------------------
	/**
	 * The framework calls this method just before display
	 */
	// ----------------------------------------------------------------------------
	public void activate() {
		super.activate();
		couponList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// we are interested in the final state of the selection
				if (!e.getValueIsAdjusting()) {
					updateShippingCharge(e);
				}
			}
		});
		couponList.addFocusListener(this);
		updateModel();
	}

	// ----------------------------------------------------------------------------
	/**
	 * deactivate any settings made by this bean to external entities
	 */
	// ----------------------------------------------------------------------------
	public void deactivate() {
		super.deactivate();
		couponList.removeFocusListener(this);
	}

	// ----------------------------------------------------------------------------
	/**
	 * updateShippingCharge
	 */
	// ----------------------------------------------------------------------------
	public void updateShippingCharge(ListSelectionEvent e) {
		int indx = couponList.getSelectedIndex();
		if (indx == -1) {
			indx = 0;
		}

		beanModel.setSelectedCouponIndex(indx);

	}

	// ---------------------------------------------------------------------
	/**
	 * Updates property-based fields.
	 **/
	// ---------------------------------------------------------------------
	protected void updatePropertyFields() { // begin updatePropertyFields()
		if (couponListLabel != null) {
			couponListLabel.setText(retrieveText(COUPON_LIST_LABEL,
					couponListLabel.getText()));
		}
	} // end updatePropertyFields()

	// --------------------------------------------------------------------------
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	// --------------------------------------------------------------------------
	public static void main(java.lang.String[] args) {
		javax.swing.JFrame frame = new javax.swing.JFrame("CheckReferralBean");

		ShippingMethodBean bean = new ShippingMethodBean();

		ShippingMethodBeanModel beanModel = new ShippingMethodBeanModel();

		beanModel.setFirstName("John");
		beanModel.setLastName("Doe");
		beanModel.setAddressLine1("1 main street");
		beanModel.setAddressLine2("Suite 1b");
		beanModel.setCity("Austin");
		bean.setModel(beanModel);
		bean.activate();

		frame.setSize(530, 290);
		frame.getContentPane().add(bean);
		frame.show();
	}

}


package max.retail.stores.pos.ui.beans;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//----------------------------------------------------------------------------
/**
 * Class description.
 * <P>
 * 
 * @version $Revision: 5$
 **/
// ----------------------------------------------------------------------------
public class MAXCouponDenominationBeanModel extends POSBaseBeanModel { // begin
																		/**
	 * 
	 */
	private static final long serialVersionUID = -4410394391957649101L;
	// class
																		// SummaryTenderMenuBeanModel
	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 5$";
	/**
	 * holds description and expected amount for each tender or charge
	 **/
	protected ArrayList couponDenominationCountBeanModel = null;
	
	protected int couponType = -1;
	
	protected String couponName = null;
	
	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	protected CurrencyIfc total=null;

	public int getCouponType() {
		return couponType;
	}

	public void setCouponType(int couponType) {
		this.couponType = couponType;
	}

	public CurrencyIfc getTotal() {
		return total;
	}

	public void setTotal(CurrencyIfc total) {
		this.total = total;
	}

	/** Store instance of logger here **/
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.ui.beans.MAXCouponDenominationBeanModel.class);

	// ---------------------------------------------------------------------
	/**
	 * Constructs SummaryTenderMenuBeanModel object.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXCouponDenominationBeanModel() {

	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves description and expected amount for each tender or charge.
	 * <P>
	 * 
	 * @return holds description and expected amount for each tender or charge
	 **/
	// ----------------------------------------------------------------------------
	public ArrayList getCouponDenominationCountBeanModel() { // begin
																// getSummaryCountBeanModel[]()
		return (couponDenominationCountBeanModel);
	} // end getSummaryCountBeanModel[]()

	// ----------------------------------------------------------------------------
	/**
	 * Sets description and expected amount for each tender or charge.
	 * <P>
	 * 
	 * @param value
	 *            holds description and expected amount for each tender or
	 *            charge
	 **/
	// ----------------------------------------------------------------------------
	public void setCouponDenominationCountBeanModel(ArrayList value) { // begin
																			// setSummaryCountBeanModel[]()
		couponDenominationCountBeanModel = value;
	} // end setSummaryCountBeanModel[]()



	// ---------------------------------------------------------------------
	/**
	 * Retrieves the source-code-control system revision number.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() { // begin getRevisionNumber()
		// return string
		return (Util.parseRevisionNumber(revisionNumber));
	} // end getRevisionNumber()

	// ---------------------------------------------------------------------
	/**
	 * SummaryMenuBeanModel main method.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>none
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>toString() output
	 * </UL>
	 * 
	 * @param args
	 *            [] command-line parameters
	 **/
	// ---------------------------------------------------------------------
	public static void main(String args[]) { // begin main()
		// instantiate class
		MAXCouponDenominationBeanModel c = new MAXCouponDenominationBeanModel();
		// output toString()
		System.out.println(c.toString());
	} // end main()
} // end class SummaryTenderMenuBeanModel

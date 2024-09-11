package max.retail.stores.domain.event;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEvent;

public class MAXItemPriceMaintenanceEvent extends ItemPriceMaintenanceEvent
		implements MAXItemPriceMaintenanceEventIfc {

	static final long serialVersionUID = 4132320245651645114L;
	public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	protected String appliedOn = "RE";

	public MAXItemPriceMaintenanceEvent() {
		super();
	}

	/**
	 * Returns the PriceChangeEvent applied on.
	 * 
	 * The possible values are MRP and RE
	 * 
	 * @return String
	 */
	public String getAppliedOn() {
		return appliedOn;
	}

	/**
	 * Sets the PriceChangeEvent applied on.
	 * 
	 * @param appliedOn
	 */
	public void setAppliedOn(String appliedOn) {
		this.appliedOn = appliedOn;
	}

	public Object clone() {
		MAXItemPriceMaintenanceEvent c = new MAXItemPriceMaintenanceEvent();
		setCloneAttributes(c);
		return c;
	}

	public void setCloneAttributes(MAXItemPriceMaintenanceEvent newClass) {

		super.setCloneAttributes(newClass);
		newClass.setAppliedOn(this.appliedOn);

	}

	public String toString() {
		StringBuilder strResult = Util.classToStringHeader(
				"MAXItemPriceMaintenanceEvent", revisionNumber, hashCode());

		strResult
				.append(Util.formatToStringEntry("saleUnitAmount",
						getSaleUnitAmount()))
				.append(Util.formatToStringEntry("applicationCode",
						applicationCodeToString()))
				.append(Util.formatToStringEntry("priority", getPriority()))
				.append(Util.formatToStringEntry("lastPriceDigit",
						getLastPriceDigit()))
				.append(Util.formatToStringEntry("appliedOn", getAppliedOn()));

		if (getItems() == null) {
			strResult.append("items:                              [null]")
					.append(Util.EOL);
		} else {
			strResult.append(getItems().toString());
		}
		strResult.append(super.toString()).append(Util.EOL);

		return strResult.toString();
	}

}

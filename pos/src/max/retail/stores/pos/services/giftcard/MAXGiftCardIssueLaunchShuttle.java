
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;


public class MAXGiftCardIssueLaunchShuttle implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -2886329241731780814L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.giftcard.MAXGiftCardIssueLaunchShuttle.class);

	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	public static final String promoType = "promoGiftCard";

	/**
	 * Gift card cargo
	 */
	protected GiftCardCargo giftCardCargo = null;

	// ----------------------------------------------------------------------
	/**
	 * Loads cargo from POS service.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>Cargo will contain the retail transaction
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>
	 * </UL>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) { // begin load()
		giftCardCargo = (GiftCardCargo) bus.getCargo();
	} // end load()

	// ----------------------------------------------------------------------
	/**
	 * Loads data into GiftCardAReload service.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>Cargo will contain the retail transaction
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>
	 * </UL>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) { // begin unload()
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();

		cargo.setStoreStatus(giftCardCargo.getStoreStatus());
		cargo.setRegister(giftCardCargo.getRegister());
		cargo.setOperator(giftCardCargo.getOperator());
		cargo.setCustomerInfo(giftCardCargo.getCustomerInfo());
		cargo.setTenderLimits(giftCardCargo.getTenderLimits());
		cargo.setTransaction(giftCardCargo.getTransaction());
		cargo.setPLUItem(giftCardCargo.getPLUItem());
		cargo.setGiftCardAmount(giftCardCargo.getGiftCardAmount());
		cargo.setSalesAssociate(giftCardCargo.getSalesAssociate());

	} // end unload()

	// ----------------------------------------------------------------------
	/**
	 * Returns a string representation of this object.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() { // begin toString()
		// result string
		String strResult = new String("Class:  GiftCardIssueLaunchShuttle (Revision " + revisionNumber + ") @" + hashCode());
		// pass back result
		return (strResult);
	} // end toString()

} // end class GiftCardIssueLaunchShuttle

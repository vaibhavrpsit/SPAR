/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		25/04/2013		Initial Draft: Changes for Gift Card
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the cargo used in the Sale service to
 * the cargo used in the Gift Card service.
 * <p>
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXGiftOptionsLaunchShuttle implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 2456373953929130875L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXGiftOptionsLaunchShuttle.class);

	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	/**
	 * The sale cargo
	 **/
	protected SaleCargoIfc saleCargo = null;

	// ----------------------------------------------------------------------
	/**
	 * Loads cargo from Sale service.
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
		saleCargo = (SaleCargoIfc) bus.getCargo();
	} // end load()

	// ----------------------------------------------------------------------
	/**
	 * Loads data into GiftCard service.
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
		// super.unload(bus);
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		cargo.setStoreStatus(saleCargo.getStoreStatus());
		cargo.setRegister(saleCargo.getRegister());
		cargo.setOperator(saleCargo.getOperator());
		cargo.setCustomerInfo(saleCargo.getCustomerInfo());
		cargo.setTenderLimits(saleCargo.getTenderLimits());
		cargo.setTransaction(saleCargo.getTransaction());
		cargo.setSalesAssociate(saleCargo.getSalesAssociate());
		SaleReturnTransactionIfc transaction = saleCargo.getTransaction();

		cargo.setTransaction(transaction);
		if (transaction != null) {
			// Get the selected lines items from the sale cargo
			
			ArrayList itemList = new ArrayList();

			AbstractTransactionLineItemIfc[] cargoItems = transaction.getItemContainerProxy().getLineItems();
			if (cargoItems != null) {
				for (int i = 0; i < cargoItems.length; i++) {

					itemList.add(cargoItems[i]);

				}
			}
			SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[]) itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);

			if (items != null) {

				if (items.length > 0) {
					cargo.setLineItems(items);

				}
			}

		}
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
		String strResult = new String("Class:  GiftCardLaunchShuttle (Revision " + revisionNumber + ") @" + hashCode());
		// pass back result
		return (strResult);
	} // end toString()

} // end class GiftOptionsLaunchShuttle

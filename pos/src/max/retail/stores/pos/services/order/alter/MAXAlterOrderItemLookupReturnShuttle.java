/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import java.math.BigDecimal;

import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

// --------------------------------------------------------------------------
/**
 * This shuttle copies information from the Item Inquiry service back to the
 * Modify Item service. $Revision: /rgbustores_12.0.9in_branch/1 $
 */
// --------------------------------------------------------------------------
public class MAXAlterOrderItemLookupReturnShuttle implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -5054526421608617025L;

	/**
	 * revision number
	 */
	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	/**
	 * Child service's cargo
	 */
	protected ItemInquiryCargo inquiryCargo = null;

	// ----------------------------------------------------------------------
	/**
	 * This shuttle copies information from the Item Inquiry service back to the
	 * Modify Item service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus to copy cargo from.
	 */
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {
		inquiryCargo = (ItemInquiryCargo) bus.getCargo();
	}

	// ----------------------------------------------------------------------
	/**
	 * Copies the new item to the cargo for the Modify Item service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus to copy cargo to.
	 */
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {
		MAXOrderCargoIfc cargo = (MAXOrderCargoIfc) bus.getCargo();
		if (inquiryCargo.getModifiedFlag()) {
			PLUItemIfc pluItem = inquiryCargo.getPLUItem();
			BigDecimal itemQuantity = inquiryCargo.getItemQuantity();

			if (pluItem != null) {
				

				cargo.setPLUItem(pluItem);
				cargo.setItemQuantity(itemQuantity);
				cargo.setItemSerial(inquiryCargo.getItemSerial());
				OrderLineItemIfc lineItem = DomainGateway.getFactory().getOrderLineItemInstance();
				lineItem.setPLUItem(pluItem);
				//lineItem.getItemPrice().setExtendedSellingPrice(pluItem.getPrice().multiply(itemQuantity));
				
				//lineItem.getItemPrice().
				lineItem.setItemQuantity(itemQuantity);
				lineItem.setItemSerial(inquiryCargo.getItemSerial());
				lineItem.getItemPrice().setSellingPrice(pluItem.getPrice());
				lineItem.getItemPrice().setItemQuantity(itemQuantity);
				lineItem.calculateLineItemPrice();
				cargo.setLineItem(lineItem);
				
			}
		}

		
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a string representation of this object.
	 * <P>
	 * 
	 * @return String representation of object
	 */
	// ----------------------------------------------------------------------
	public String toString() { // begin toString()
		// result string
		String strResult = new String(
				"Class:  InquiryOptionsReturnShuttle (Revision "
						+ getRevisionNumber() + ")" + hashCode());

		// pass back result
		return (strResult);
	} // end toString()

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 */
	// ----------------------------------------------------------------------
	public String getRevisionNumber() { // begin getRevisionNumber()
		// return string
		return (Util.parseRevisionNumber(revisionNumber));
	} // end getRevisionNumber()

	// ----------------------------------------------------------------------
	/**
	 * Main to run a test..
	 * <P>
	 * 
	 * @param args
	 *            Command line parameters
	 */
	// ----------------------------------------------------------------------
	public static void main(String args[]) { // begin main()
		// instantiate class
		MAXAlterOrderItemLookupReturnShuttle obj = new MAXAlterOrderItemLookupReturnShuttle();

		// output toString()
		System.out.println(obj.toString());
	} // end main()
}

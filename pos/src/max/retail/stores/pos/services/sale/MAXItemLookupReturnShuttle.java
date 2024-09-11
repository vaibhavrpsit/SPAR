/* ===========================================================================
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemLookupReturnShuttle.java /main/19 2013/04/17 16:44:39 yiqzhao Exp $
 * ===========================================================================
 
 *    Rev 1.1   Nov 07 2003 12:37:18   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * ===========================================================================
 */
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.lineitem.MAXItemContainerProxyIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.sale.ItemLookupReturnShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the Item Inquiry service back to the
 * Modify Item service. $Revision: /main/19 $
 **/
// --------------------------------------------------------------------------
public class MAXItemLookupReturnShuttle extends ItemLookupReturnShuttle {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -5054526421608617025L;

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: /main/19 $";


	public void load(BusIfc bus) {
        inquiryCargo = (MAXItemInquiryCargo) bus.getCargo();
	}

	// ----------------------------------------------------------------------
	/**
	 * Copies the new item to the cargo for the Modify Item service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus to copy cargo to.
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		if (inquiryCargo.getModifiedFlag()) {
			MAXPLUItemIfc pluItem = (MAXPLUItemIfc) inquiryCargo.getPLUItem();
			BigDecimal itemQuantity = inquiryCargo.getItemQuantity();

			if (pluItem != null) {
				cargo.setTransaction((SaleReturnTransactionIfc) inquiryCargo
						.getTransaction());

				if (cargo.getTransaction() == null) {
					cargo.initializeTransaction(bus);
				}

				cargo.setPLUItem(pluItem);
				cargo.setItemQuantity(itemQuantity);
				cargo.setItemSerial(inquiryCargo.getItemSerial());
				SaleReturnLineItemIfc srli = ((MAXSaleReturnTransaction)cargo.getTransaction()).addPLUItem(
						cargo.getPLUItem(), cargo.getItemQuantity());
						
						
						

						
				((MAXSaleReturnLineItemIfc)srli).setExcList(pluItem.getItemExclusionGroupList());// Gaurav
																		// for
																		// phase
																		// 3
																		// exclusion
																		// List
				try {
					// if(cargo.getTransaction() instanceof
					// SaleReturnTransactionIfc )
					// {
					((MAXSaleReturnTransaction) cargo.getTransaction())
							.setTaxTypeLegal(false);
					((MAXItemContainerProxyIfc)((MAXSaleReturnTransaction) cargo.getTransaction())
							.getItemContainerProxy()).setTaxApplied(false);
					// }
				} catch (ClassCastException e) {

				}
		

						
						
				srli.setPluDataFromCrossChannelSource(inquiryCargo
						.isItemFromWebStore());

				// Sets the Send enabled flag to true for transaction level send
				// items
				if (cargo.getTransaction().isTransactionLevelSendAssigned()) {
					Vector<AbstractTransactionLineItemIfc> lineItemsVector = null;
					lineItemsVector = cargo.getTransaction()
							.getItemContainerProxy().getLineItemsVector();

					SendManagerIfc sendMgr = null;
					try {
						sendMgr = (SendManagerIfc) ManagerFactory
								.create(SendManagerIfc.MANAGER_NAME);
					} catch (ManagerException e) {
						// default to product version
						sendMgr = new SendManager();
					}

					for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector
							.iterator(); i.hasNext();) {
						srli = (SaleReturnLineItemIfc) i.next();
						if (sendMgr.checkValidSendItem(srli)) {
							srli.setItemSendFlag(true);
							// this value is always 1 since multiple sends are
							// not allowed
							srli.setSendLabelCount(1);
						}

					}

				}

				// Do not take account of serial number for Layaway initiate
				// when item IMEI is scanned instead of Item Number.
				if (cargo.getTransaction() instanceof LayawayTransaction
						&& cargo.getTransaction().getTransactionStatus() == LayawayConstantsIfc.STATUS_NEW) {
					srli.setItemSerial(null);
					srli.setItemIMEINumber(null);
				} else {
					srli.setItemSerial(cargo.getItemSerial());
					srli.setItemIMEINumber(inquiryCargo.getItemIMEINumber());
				}

				if (pluItem.isAlterationItem()) {
					srli.setAlterationItemFlag(true);
				}
				cargo.setLineItem(srli);
			}

		}
		// this case is used when transaction sequence number is generated, but
		// then Esc is used
		else if (inquiryCargo.getPLUItem() == null
				&& inquiryCargo.getTransaction() != null) {
			cargo.setTransaction((SaleReturnTransactionIfc) inquiryCargo
					.getTransaction());
		}

		// get the transaction if it was created for the date.
		if (inquiryCargo.getTransaction() != null
				&& ((SaleReturnTransactionIfc) inquiryCargo.getTransaction())
						.getAgeRestrictedDOB() != null) {
			cargo.setTransaction((SaleReturnTransactionIfc) inquiryCargo
					.getTransaction());
		}

		if (cargo.getTransaction() != null) {
			List<SaleReturnLineItemIfc> splittedLineItems = getSplittedLineItems(cargo
					.getTransaction().getLineItemsVector());
			if (splittedLineItems.size() > 1) {
				cargo.setSplittedLineItems(splittedLineItems
						.toArray(new SaleReturnLineItemIfc[splittedLineItems
								.size()]));
			} else {
				cargo.setSplittedLineItems(null);
			}
		}
	}

	protected List<SaleReturnLineItemIfc> getSplittedLineItems(
			Vector<AbstractTransactionLineItemIfc> lineItems) {
		List<SaleReturnLineItemIfc> srlis = new ArrayList<SaleReturnLineItemIfc>();
		for (AbstractTransactionLineItemIfc lineItem : lineItems) {
			SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
			if (srli.isSelectedForItemSplit()) {
				srlis.add(srli);
				srli.setSelectedForItemSplit(false);
			}
		}
		return srlis;
	}

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
	 **/
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
	 **/
	// ----------------------------------------------------------------------
	public static void main(String args[]) { // begin main()
		// instantiate class
		ItemLookupReturnShuttle obj = new ItemLookupReturnShuttle();

		// output toString()
		System.out.println(obj.toString());
	} // end main()
}

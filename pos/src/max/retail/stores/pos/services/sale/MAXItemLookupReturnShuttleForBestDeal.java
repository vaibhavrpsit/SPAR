/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

 $Log:
 5    360Commerce 1.4         1/22/06 11:45:01 AM CSTRon W. Haight
 removed references to com.ibm.math.BigDecimal
 4    360Commerce 1.3         12/13/05 4:42:34 PM CSTBarry A. Pape
 Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/05 3:28:32 PM CST Robert Pearse   
 2    360Commerce 1.1         3/10/05 10:22:28 AM CSTRobert Pearse   
 1    360Commerce 1.0         2/11/05 12:11:38 PM CSTRobert Pearse   
 $
 Revision 1.11  2004/09/23 00:07:11  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

 Revision 1.10  2004/08/05 22:17:03  dcobb
 @scr 6655 Remove letter checks from shuttles.
 Backed out lwalters changes for SCR 1665 and aschenk changes for SCR 3959.
 Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.

 Revision 1.9  2004/07/06 15:49:31  rsachdeva
 @scr 5963 CPOI Line Items Update

 Revision 1.8  2004/06/29 16:42:46  lwalters
 @scr 1665

 Cancel was removing all items in the transaction.
 Changed to check the Cancel letter in addition to the Undo.

 Revision 1.7  2004/05/14 20:55:52  dfierling
 @scr 3830 -  Modification for Alteration printing instructions

 Revision 1.6  2004/03/23 22:22:03  aschenk
 @scr 3959 - Item (with no price) is no longer added to the Sell Item screen when select Undo in Price Entry screen.

 Revision 1.5  2004/02/25 15:50:41  epd
 @scr 3561 Updated to repair the addiing of items to a txn

 Revision 1.4  2004/02/20 21:08:20  epd
 @scr 3561 fixed adding of items to transaction

 Revision 1.3  2004/02/12 16:48:17  mcs
 Forcing head revision

 Revision 1.2  2004/02/11 21:22:50  rhafernik
 @scr 0 Log4J conversion and code cleanup

 Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.3   Nov 17 2003 09:56:28   jriggins
 * Setting the SaleReturnLineItemIfc instance into the SaleCargo in unload().
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 13 2003 11:09:56   baa
 * sale refactoring
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   Nov 07 2003 12:37:18   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 * Rev 1.12  06/Jun/2013 Karandeep Singh  Change for TIC Preview Sale requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;

import max.retail.stores.domain.lineitem.MAXItemContainerProxy;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.services.sale.ItemLookupReturnShuttle;

// --------------------------------------------------------------------------
/**
 * This shuttle copies information from the Item Inquiry service back to the
 * Modify Item service. $Revision: 1.4 $
 */
// --------------------------------------------------------------------------
public class MAXItemLookupReturnShuttleForBestDeal implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -5054526421608617025L;

	/**
	 * revision number
	 */
	public static final String revisionNumber = "$Revision: 1.4 $";

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
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		if (cargo.getTransaction().getTransactionType() == 2) // Sakshi
		{
			bus.mail(new Letter("TenderHome"), BusIfc.CURRENT);
		} else {
			if (inquiryCargo.getModifiedFlag()) {
				PLUItemIfc pluItem = inquiryCargo.getPLUItem();
				BigDecimal itemQuantity = inquiryCargo.getItemQuantity();

				if (pluItem != null) {
					cargo.setTransaction((SaleReturnTransactionIfc) inquiryCargo.getTransaction());

					if (cargo.getTransaction() == null) {
						cargo.initializeTransaction(bus);
					}

					cargo.setPLUItem(pluItem);
					// cargo.setItemQuantity(itemQuantity);
					// cargo.setItemSerial(inquiryCargo.getItemSerial());
					/* India Localization- Rounding related Changes starts here */
					// cargo.getTransaction().getTransactionTotals().setRounding(
					// cargo.getRounding());
					// cargo.getTransaction().getTransactionTotals()
					// .setRoundingDenominations(
					// cargo.getRoundingDenominations());
					cargo.setApplyBestDeal(true);
					/* India Localization- Rounding related Changes ends here */

					/*
					 * if(cargo.getPLUItem() != null)
					 * cargo.setPLUItem(pluItem);
					 */
					SaleReturnLineItemIfc srli = null;
					if (cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc)
					{
					   srli = ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).addPLUItem(cargo.getPLUItem(),cargo.getItemQuantity(),cargo.isApplyBestDeal());
					}
					else if (cargo.getTransaction() instanceof MAXLayawayTransaction)
					{
					   srli = ((MAXLayawayTransaction)cargo.getTransaction()).addPLUItem(cargo.getPLUItem(),cargo.getItemQuantity(),cargo.isApplyBestDeal());
					}

					try {
						((MAXSaleReturnTransaction) cargo.getTransaction()).setTaxTypeLegal(true);
					//	((MAXSaleReturnTransaction) cargo.getTransaction()).getItemContainerProxy().setTaxApplied(true);
						ItemContainerProxyIfc itemProxy= (ItemContainerProxyIfc)cargo.getTransaction().getItemContainerProxy();
						((MAXItemContainerProxy)itemProxy).setTaxApplied(true);
					} catch (ClassCastException e) {

					}
					if (pluItem.isAlterationItem()) {
						srli.setAlterationItemFlag(true);
					}

					// cargo.setLineItem(srli);
				}
			}
		}

		// get the transaction if it was created for the date.
		if (inquiryCargo.getTransaction() != null && ((SaleReturnTransactionIfc) inquiryCargo.getTransaction()).getAgeRestrictedDOB() != null) {
			cargo.setTransaction((SaleReturnTransactionIfc) inquiryCargo.getTransaction());
		}
		//condition for Invalid error on scanning of GC.start	
		
		if((("Tender").equals(cargo.getInitialOriginStationLetter())) && (("Invalid").equals(bus.getCurrentLetter().getName())) && 
				(cargo.getPLUItem()!= null && (cargo.getPLUItem() instanceof GiftCardPLUItem)))
		{
			bus.mail(new Letter("TenderHome"), BusIfc.CURRENT);
		}
		else
		{
			if(("Tender").equals(cargo.getInitialOriginStationLetter()) && cargo.getPLUItem()!= null)
				bus.mail(new Letter("TenderHome"), BusIfc.CURRENT);

		}
		
		
		//condition for Invalid error on scanning of GC.ends
		
		
		 /* if(inquiryCargo.getTransaction() != null &&   inquiryCargo.getTransaction().getSuspendReasonCode()!= 0 && 
				  ("Tender").equals(((MAXItemInquiryCargo)inquiryCargo).getInitialOriginLetter()) &&
				  ("Invalid").equals(bus.getCurrentLetter().getName())) 
		  { 
			  bus.mail(new
					  Letter("TenderHome"), BusIfc.CURRENT); 
		}*/
		 
		// bus.mail(new Letter("Undo"), BusIfc.CURRENT);
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
		String strResult = new String("Class:  InquiryOptionsReturnShuttle (Revision " + getRevisionNumber() + ")" + hashCode());

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
		ItemLookupReturnShuttle obj = new ItemLookupReturnShuttle();

		// output toString()
		System.out.println(obj.toString());
	} // end main()
}

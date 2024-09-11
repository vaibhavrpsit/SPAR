/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:05 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     10/31/2005 11:49:26    Deepanshu       CR
           6092: Set the Sales Associate in the ItemInquiryCargo
      3    360Commerce1.2         3/31/2005 15:28:32     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:28     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:39     Robert Pearse
     $
     Revision 1.6  2004/09/23 00:07:11  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.5  2004/08/23 16:15:57  cdb
     @scr 4204 Removed tab characters

     Revision 1.4  2004/05/27 17:12:48  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.3  2004/02/12 16:48:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.4   Dec 12 2003 14:05:54   lzhao
 * get store, registration information from sale cargo.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Nov 13 2003 13:08:48   jriggins
 * assigning SCR
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 13 2003 13:06:44   jriggins
 * refactoring the item inquiry service so that plu lookups can be performed without having to go through the entire item inquiry flow
 *
 *    Rev 1.1   Nov 07 2003 12:37:16   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;


//--------------------------------------------------------------------------
/**
 * $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXItemLookupLaunchShuttleForBestDeal extends FinancialCargoShuttle implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 286725333388100988L;

	public static final String APPLY_BEST_DEAL = "ApplyDiscounts";
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	// Calling service's cargo
	protected SaleCargoIfc saleCargo = null;
	//protected MAXSaleCargo saleCargo1=null;
	

	// ----------------------------------------------------------------------
	/**
	 * Loads the item cargo.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus to copy cargo from.
	 **/
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {
		// load the financial cargo
		super.load(bus);

		saleCargo = (SaleCargoIfc) bus.getCargo();
		//saleCargo1 = (MAXSaleCargo)saleCargo;
		((MAXSaleCargo)saleCargo).setInitialOriginStationLetter(bus.getCurrentLetter().getName());
	}

	// ----------------------------------------------------------------------
	/**
	 * Transfers the item cargo to the item inquiry cargo for the item inquiry
	 * service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus to copy cargo to.
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {
		// unload the financial cargo
		super.unload(bus);
        
		//ItemInquiryCargo inquiryCargo = (ItemInquiryCargo) bus.getCargo();
		MAXItemInquiryCargo inquiryCargo1=(MAXItemInquiryCargo) bus.getCargo();
		inquiryCargo1.setRegister(saleCargo.getRegister());
		inquiryCargo1.setTransaction(saleCargo.getTransaction());
		inquiryCargo1.setModifiedFlag(true);
		inquiryCargo1.setStoreStatus(saleCargo.getStoreStatus());
		inquiryCargo1.setRegister(saleCargo.getRegister());
		inquiryCargo1.setOperator(saleCargo.getOperator());
		inquiryCargo1.setCustomerInfo(saleCargo.getCustomerInfo());
		inquiryCargo1.setTenderLimits(saleCargo.getTenderLimits());
		inquiryCargo1.setPLUItem(saleCargo.getPLUItem());
		inquiryCargo1.setSalesAssociate(saleCargo.getEmployee());
		//putting flag in item inquiry cargo to check if best deal button is selected
		inquiryCargo1.setInitialOriginLetter(((MAXSaleCargo)saleCargo).getInitialOriginStationLetter()); 
		if(inquiryCargo1.getInitialOriginLetter().equalsIgnoreCase(APPLY_BEST_DEAL) || ((MAXSaleCargo)saleCargo).isApplyBestDeal())
		{
			inquiryCargo1.setApplyBestDeal(true);
			//logger.info("Prateek: Setting changes for best deal, in if block: "+inquiryCargo1.isApplyBestDeal());
		}
		else
		{
			inquiryCargo1.setApplyBestDeal(false); 	// Sakshi..for apply best deal button on click apply best deal button
			
		}

		String geoCode = null;
		if (saleCargo.getStoreStatus() != null && saleCargo.getStoreStatus().getStore() != null) {
			geoCode = saleCargo.getStoreStatus().getStore().getGeoCode();
		}

		/** Changes for Return Tender crash : Starts **/
		if (saleCargo != null && saleCargo.getPLUItem() != null && saleCargo.getPLUItem().getItemID() != null)
			inquiryCargo1.setInquiry(saleCargo.getPLUItem().getItemID(), "", "", geoCode);
		else if(saleCargo != null && saleCargo.getPLUItemID() != null)
			inquiryCargo1.setInquiry(saleCargo.getPLUItemID(), "", "", geoCode);
		else
			inquiryCargo1.setInquiry("70071000", "", "", geoCode);
			/** Changes for Return Tender crash : Ends **/
		//inquiryCargo1.setInquiry(saleCargo.getPLUItemID(), "", "", geoCode);
		TransactionDiscountStrategyIfc[] trsDis = (inquiryCargo1.getTransaction()).getTransactionDiscounts();
		inquiryCargo1.setIsRequestForItemLookup(true);
		if (((SaleReturnTransaction) inquiryCargo1.getTransaction()).getEmployeeDiscountID() != null
				&& (inquiryCargo1.getTransaction()).getTransactionDiscounts() != null && trsDis[0].getDiscountEmployee() != null
				&& trsDis[0].getDiscountEmployee().getEmployeeID() != null) {

		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a string representation of this object.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() {
		return "Class:  InquiryOptionsLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode();
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}
}

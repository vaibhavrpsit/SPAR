/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * Rev 1.0  May 04, 2011 01:06:12 PM Tarun.Gupta
 * Initial revision.
 * Resolution for Defect MAX-211:After layaway initialization, it is not appearing after searching with provided criteria
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.layaway.create;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
 * Determines if a customer is already linked to the transaction and mails the
 * appropriate letter to proceed.
 * <p>
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------

public class MAXLayawayCustomerRequiredSite extends PosSiteActionAdapter {
	/** 
    class name constant 
	 **/ 
	public static final String SITENAME = "LayawayCustomerRequiredSite"; 
	/**
    revision number
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	//----------------------------------------------------------------------
	/**
    Determines if the customer is currencly linked to the
    transaction. Mails Continue letter is customer is linked or
    Link if customer is not linked.
    <P>
    @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public void arrive(BusIfc bus)
	{
		// need to determine if customer is already linked to this transaction
		// if not linked then call customer service
		// if linked, then mail continue letter to customer layaway screen
		LayawayCargo cargo = (LayawayCargo)bus.getCargo();

		/*if (cargo.getSaleTransaction() != null &&
				cargo.getSaleTransaction().getCustomer() != null)
		{*/
		//akhilesh changes for tic cr  condition check for the customer id null check START
		if (cargo.getSaleTransaction() != null && cargo.getSaleTransaction().getCustomer() != null && !cargo.getSaleTransaction().getCustomer().getCustomerID().equalsIgnoreCase("")) {
		///akhilesh changes for tic cr  condition check for the customer id END
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		}
		else
		{
			bus.mail(new Letter(CommonLetterIfc.LINK), BusIfc.CURRENT);   
		}     
	}
}

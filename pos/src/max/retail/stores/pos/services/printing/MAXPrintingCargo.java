/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.

	Rev 1.0  17/June/2013	Jyoti Rawal, Initial Draft: Fix for Bug 6394 Credit Charge Slip is not getting printed
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.printing;

// Java imports
import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

//------------------------------------------------------------------------------
/**
 * Data and methods common to the sites in Printing Service.
 *
 * @version $Revision: 1.1 $
 **/
// ------------------------------------------------------------------------------
public class MAXPrintingCargo extends PrintingCargo {

	protected HashMap tenderattributes = null;
	protected String transactionId = null;

	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public HashMap getTenderattributes() {
		return tenderattributes;
	}


	public void setTenderattributes(HashMap tenderattributes) {
		this.tenderattributes = tenderattributes;
	}


	//----------------------------------------------------------------------
	/**
        Determines whether store credit is being redeemed during the transaction.
        <P>
        @return the boolean flag
	 **/
	//----------------------------------------------------------------------
	public void addTenderForFranking(TenderLineItemIfc tli)
    {
        if (tendersToFrank == null)
        {
            tendersToFrank = new Vector<TenderLineItemIfc>();
        }
        tendersToFrank.addElement(tli);
    }
	

}

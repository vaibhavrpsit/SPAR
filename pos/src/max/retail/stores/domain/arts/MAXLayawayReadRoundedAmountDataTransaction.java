/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log:
      5    360Commerce 1.4         11/9/2006 7:28:30 PM   Jack G. Swan
           Modifided for XML Data Replication and CTR.
      4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
     $
     Revision 1.7  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:33:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:41:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:50:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:59:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:30   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * This data transaction was constructed to test the layaway data operations.
 * 
 * @version $Revision: 5$
 **/
// -------------------------------------------------------------------------
public class MAXLayawayReadRoundedAmountDataTransaction extends DataTransaction implements DataTransactionIfc { // begin
																												// class
																												// LayawayDataTransaction
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -7316419398844270522L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXLayawayReadRoundedAmountDataTransaction.class);
	/**
	 * revision number of this class
	 **/
	public static String revisionNumber = "$Revision: 5$";
	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXLayawayReadRoundedAmountDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXLayawayReadRoundedAmountDataTransaction() {
		super(dataCommandName);
	}

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 * 
	 * @param name
	 *            transaction name
	 **/
	// ---------------------------------------------------------------------
	public MAXLayawayReadRoundedAmountDataTransaction(String name) {
		super(name);
	}

	public String maxLayawayReadRoundedAmountDataTransaction(LayawayIfc layaway) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXLayawayReadRoundedAmountDataTransaction.maxLayawayReadRoundedAmountDataTransaction");

		// set data actions and execute
		// ArrayList listReadPrintedItemFreeDiscountRule = new ArrayList();
		String roundedAmount = null;
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("MAXLayawayReadRoundedAmount");
		// da.setDataObject();
		dataActions[0] = da;
		da.setDataObject(layaway);
		setDataActions(dataActions);
		roundedAmount = (String) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXLayawayReadRoundedAmountDataTransaction.maxLayawayReadRoundedAmountDataTransaction");

		return (roundedAmount);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the revision number of this class.
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (revisionNumber);
	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		// result string
		StringBuilder strResult = Util.classToStringHeader("MAXLayawayReadRoundedAmountDataTransaction",
				getRevisionNumber(), hashCode());
		return (strResult.toString());
	}
}

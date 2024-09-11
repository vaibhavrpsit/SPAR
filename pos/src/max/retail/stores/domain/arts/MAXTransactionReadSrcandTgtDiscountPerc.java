/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Discount Rule
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * The DataTransaction to perform persistent read operations on the POS
 * Transaction object.
 * 
 * @version $Revision: 1.1 $
 * @see com.extendyourstore.domain.arts.TransactionWriteDataTransaction
 * @see com.extendyourstore.domain.arts.UpdateReturnedItemsDataTransaction
 * @see com.extendyourstore.domain.arts.TransactionHistoryDataTransaction
 **/
// -------------------------------------------------------------------------
public class MAXTransactionReadSrcandTgtDiscountPerc extends DataTransaction implements DataTransactionIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -3159317257797343146L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXTransactionReadSrcandTgtDiscountPerc.class);

	/**
	 * revision number of this class
	 **/
	public static String revisionNumber = "$Revision: 1.1 $";
	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXTransactionReadSrcandTgtDiscountPerc";

	/**
	 * The name that reads the tax history.
	 */
	// public static final String READ_TAX_HISTORY = "ReadTaxHistory";

	/**
	 * layaway reference
	 **/
	// protected LayawayIfc layaway = null;

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXTransactionReadSrcandTgtDiscountPerc() {
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
	public MAXTransactionReadSrcandTgtDiscountPerc(String name) {
		super(name);
	}

	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}

	public String toString() {
		StringBuilder strResult = Util.classToStringHeader(getClass().getName(), getRevisionNumber(), hashCode());
		return (strResult.toString());
	}

	public String readSrcandTgtDiscountPerc(String promoID) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadSrcandTgtDiscountPerc.readSrcandTgtDiscountPerc");

		// set data actions and execute
		String readSrcandTgtDiscountPerc = null;
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("ReadSrcandTgtDiscountPerc");
		StringBuffer sb = new StringBuffer();
		sb.append(promoID);
		da.setDataObject(sb);
		dataActions[0] = da;
		setDataActions(dataActions);
		readSrcandTgtDiscountPerc = (String) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadSrcandTgtDiscountPerc.readSrcandTgtDiscountPerc");

		return (readSrcandTgtDiscountPerc);
	}

}

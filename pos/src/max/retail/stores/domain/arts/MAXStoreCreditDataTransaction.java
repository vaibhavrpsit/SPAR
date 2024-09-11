/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
 * @author Himanshu
 * 
 **/
// -------------------------------------------------------------------------
public class MAXStoreCreditDataTransaction extends DataTransaction implements DataTransactionIfc {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6425518018875259115L;
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXStoreCreditDataTransaction.class);

	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXStoreCreditDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 */
	// ---------------------------------------------------------------------
	public MAXStoreCreditDataTransaction() {
		super(dataCommandName);
	}

	/**
	 * MAX Customizations Check if store credit has been redeemed already and if
	 * not redeemed, whether it exists in Store credit master table in CO
	 * 
	 * Added by Himanshu
	 */
	public TenderStoreCreditIfc lookUpStoreCredit(TenderStoreCreditIfc storeCredit) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MGCreditNoteDataTransaction.lookUpStoreCredit");

		// set data actions and executeR
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("LookupStoreCredit");
		da.setDataObject(storeCredit);
		dataActions[0] = da;
		setDataActions(dataActions);

		TenderStoreCreditIfc strCrdt = (TenderStoreCreditIfc) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MGCreditNoteDataTransaction.lookUpStoreCredit");

		return strCrdt;
	}

}

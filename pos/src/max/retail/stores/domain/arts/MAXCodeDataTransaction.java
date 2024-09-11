/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:28 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:17 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:03 PM  Robert Pearse   
     $
     Revision 1.5  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.4  2004/02/17 16:18:46  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:29:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jun 27 2003 13:47:26   bwf
 * Be able to internationalize getCodeListMapForUpdate.
 * Resolution for 2269: Tags instead of names displaying in the Reason Code List Screen
 * 
 *    Rev 1.3   Jun 25 2003 13:39:20   bwf
 * Added getCodeListMapEntryText to get internationalized text.
 * Resolution for 2458: Receipt displays "tags" around discount reason code
 * Resolution for 2470: Receipt displays "tags" around customer discount type
 * Resolution for 2874: Greater Than, Less Than signs appear around the customer discount type on the receipt.
 * 
 *    Rev 1.2   Jan 30 2003 15:51:56   adc
 * Changes for BackOffice 2.0
 * Resolution for 1846: Advanced Pricing Updates
 * 
 *    Rev 1.1   Jan 21 2003 10:13:18   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 * 
 *    Rev 1.0   Jun 03 2002 16:34:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:57:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:04   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import max.retail.stores.domain.utility.MAXCodeListMapIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * This class handles the DataTransaction behavior for code list data requests.
 * 
 * @version $Revision: 3$
 * @see com.extendyourstore.domain.utility.CodeListMapIfc
 **/
// -------------------------------------------------------------------------
public class MAXCodeDataTransaction extends DataTransaction { // begin class
																// CodeDataTransaction
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXCodeDataTransaction.class);

	/**
	 * revision number of this class
	 **/
	public static String revisionNumber = "$Revision: 3$";
	/**
	 * The name that links this transaction to a command within DataScript.
	 **/
	public static String dataCommandName = "MAXCodeDataTransaction";
	/**
	 * the name linking this transaction to a command within datascript for a
	 * local data transaction
	 **/
	public static String LocalDataCommandName = "LocalCodeDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeDataTransaction() { // begin CodeDataTransaction()
		super(dataCommandName);
	} // end CodeDataTransaction()

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * 
	 * @param name
	 *            data command name
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeDataTransaction(String name) { // begin CodeDataTransaction()
		super(name);
	} // end CodeDataTransaction()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves code list map for a given store.
	 * <P>
	 * This is different from retrieval for update, because there are items in
	 * the code list map which shouldn't be updated by the code list facility in
	 * POS, such as the preferred customer discount reason codes. These codes
	 * are, however, required for operating POS.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeListMapIfc retrieveCodeListMap(String storeID) throws DataException { // begin
																						// retrieveCodeListMap()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.retrieveCodeListMap");

		MAXCodeListMapIfc listMap = null;
		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("RetrieveCodeListMap");
		da.setDataObject(storeID);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		listMap = (MAXCodeListMapIfc) getDataManager().execute(this);

		if (listMap == null) {
			throw new DataException(DataException.NO_DATA, "No Code List Map was returned to CodeDataTransaction.");
		}

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.retrieveCodeListMap" + "");

		return (listMap);
	} // end retrieveCodeListMap()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves code list map for a given store/locale as set in
	 * SearchCriteriaIfc.
	 * <P>
	 * This is different from retrieval for update, because there are items in
	 * the code list map which shouldn't be updated by the code list facility in
	 * POS, such as the preferred customer discount reason codes. These codes
	 * are, however, required for operating POS. SearchCriteria has a new field
	 * codeMapName that is used only when a specific reason code group needs to
	 * be retrieved from the database, not all of them.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeListMapIfc retrieveCodeListMap(SearchCriteriaIfc inquiry) throws DataException { // begin
																								// retrieveCodeListMap()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.retrieveCodeListMap");

		MAXCodeListMapIfc listMap = null;
		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("RetrieveCodeListMap");
		da.setDataObject(inquiry);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		listMap = (MAXCodeListMapIfc) getDataManager().execute(this);

		if (listMap == null) {
			throw new DataException(DataException.NO_DATA, "No Code List Map was returned to CodeDataTransaction.");
		}

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.retrieveCodeListMap" + "");

		return (listMap);
	} // end retrieveCodeListMap()
		// ---------------------------------------------------------------------

	/**
	 * Retrieves code list map for a given store for updating.
	 * <P>
	 * This is different from the standard retrieval, because there are items in
	 * the code list map which shouldn't be updated by the code list facility in
	 * POS, such as the preferred customer discount reason codes. These codes
	 * are, however, required for operating POS.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeListMapIfc retrieveCodeListMapForUpdate(String storeID) throws DataException { // begin
																								// retrieveCodeListMapForUpdate()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.retrieveCodeListMapForUpdate");

		MAXCodeListMapIfc listMap = null;

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("RetrieveCodeListMapForUpdate");
		da.setDataObject(storeID);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		listMap = (MAXCodeListMapIfc) getDataManager().execute(this);

		if (listMap == null) {
			throw new DataException(DataException.NO_DATA, "No Code List Map was returned to CodeDataTransaction.");
		}

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.retrieveCodeListMapForUpdate" + "");

		return (listMap);
	} // end retrieveCodeListMapForUpdate()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves code list map for a given store for updating.
	 * <P>
	 * This is different from the standard retrieval, because there are items in
	 * the code list map which shouldn't be updated by the code list facility in
	 * POS, such as the preferred customer discount reason codes. These codes
	 * are, however, required for operating POS.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public MAXCodeListMapIfc retrieveCodeListMapForUpdate(SearchCriteriaIfc inquiry) throws DataException { // begin
																											// retrieveCodeListMapForUpdate()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.retrieveCodeListMapForUpdate");

		MAXCodeListMapIfc listMap = null;

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("RetrieveCodeListMapForUpdate");
		da.setDataObject(inquiry);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		listMap = (MAXCodeListMapIfc) getDataManager().execute(this);

		if (listMap == null) {
			throw new DataException(DataException.NO_DATA, "No Code List Map was returned to CodeDataTransaction.");
		}

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.retrieveCodeListMapForUpdate" + "");

		return (listMap);
	} // end retrieveCodeListMapForUpdate()
		// ---------------------------------------------------------------------

	/**
	 * Saves a code list. Previously saved entries for this store are replaced.
	 * <P>
	 * 
	 * @param map
	 *            CodeListIfc object to be updated
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public void saveCodeList(CodeListIfc list) throws DataException { // begin
																		// saveCodeList()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.saveCodeList; Name = " + getTransactionName());

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("SaveCodeList");
		da.setDataObject(list);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.saveCodeList" + "");

	} // end saveCodeList()

	// ---------------------------------------------------------------------
	/**
	 * Saves a code list map. Previously saved entries for this store are
	 * replaced.
	 * <P>
	 * 
	 * @param map
	 *            CodeListMapIfc object to be saved
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public void saveCodeListMap(MAXCodeListMapIfc map) throws DataException { // begin
																			// saveCodeListMap()
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.getCodeListMapEntryText; Name = " + getTransactionName());

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("SaveCodeListMap");
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);

		// execute data request
		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.saveCodeListMap" + "");

	} // end saveCodeListMap()

	// ---------------------------------------------------------------------
	/**
	 * Get correct internationalized text.
	 * <P>
	 * 
	 * @param SearchCriteriaIfc
	 *            inquiry
	 * @exception DataException
	 *                when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public String getCodeListMapEntryText(SearchCriteriaIfc inquiry) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("CodeDataTransaction.getCodeListMapEntryText");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("GetCodeListMapEntryText");
		da.setDataObject(inquiry);
		dataActions[0] = da;
		setDataActions(dataActions);

		String newText = null;
		// execute data request
		newText = (String) getDataManager().execute(this);
		if (newText == null) {
			throw new DataException(DataException.NO_DATA,
					"No Code List Map Entry Text was returned to CodeDataTransaction.");
		}

		if (logger.isDebugEnabled())
			logger.debug("" + "CodeDataTransaction.getCodeListMapEntryText" + "");

		return (newText);
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
	 * Returns the string representation of this object.
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		return (Util.classToStringHeader("CodeDataTransaction", getRevisionNumber(), hashCode()).toString());
	}
} // end class CodeDataTransaction

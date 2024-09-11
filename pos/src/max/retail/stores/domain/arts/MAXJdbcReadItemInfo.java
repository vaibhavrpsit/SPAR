/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *  Rev 1.2     Mar 08,2017         Nitika Arora    Changes for Defect-If we seach the item wih Adv search and complet the tender, tax is printing zero in the receipt.
 *	Rev 1.1     Feb 28,2016         Abhishek Goyal  Changes for Item Search Slow
 *	Rev 1.0		Dec 20, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.arts.JdbcReadItemInfo;
import oracle.retail.stores.domain.arts.JdbcReadNewTaxRules;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MAXJdbcReadItemInfo extends MAXJdbcPLUOperation implements MAXARTSDatabaseIfc {
	private static final long serialVersionUID = 453278324824538585L;

	/**
	 * The logger to which log messages will be sent.
	 */
	protected static final Logger logger = Logger.getLogger(JdbcReadItemInfo.class);

	/**
	 * The default selected value .
	 */
	protected static String DEFAULT_SELECTED_VALUE = "-1";

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("Entering JdbcReadItemInfo.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		SearchCriteriaIfc itemInfo = (SearchCriteriaIfc) action.getDataObject();

		PLUItemIfc[] items = readItemInfo(connection, itemInfo);

		if (itemInfo.getGeoCode() == null) {
			JdbcReadNewTaxRules taxReader = new JdbcReadNewTaxRules();
			GeoCodeVO geoCodeVO = taxReader.readGeoCodeFromStoreId(connection, itemInfo.getStoreNumber());
			assignTaxRules(connection, items, geoCodeVO.getGeoCode());
		} else {
			assignTaxRules(connection, items, itemInfo.getGeoCode());
		}

		// Search Item by any method, This call retrieves corresponding Item
		// Messages and updates Item Object
		getItemMessages(connection, items);
		/* India Localization - Tax related changes starts here */
		if (items != null) {
			assignTaxAssignments(connection, items);
		}

		dataTransaction.setResult(items);

		if (logger.isDebugEnabled())
			logger.debug("Exiting JdbcReadItemInfo.execute");
	}

	public PLUItemIfc[] readItemInfo(JdbcDataConnection dataConnection, SearchCriteriaIfc info) throws DataException {
		String itemDesc = info.getDescription();
		String itemTypeCode = info.getItemTypeCode();
		String itemUOMCode = info.getItemUOMCode();
		String itemStyleCode = info.getItemStyleCode();
		String itemColorCode = info.getItemColorCode();
		String itemSizeCode = info.getItemSizeCode();

		LocaleRequestor localeRequestor = info.getLocaleRequestor();

		if (itemDesc != null) {
			itemDesc = protectString(itemDesc);
		}
		String itemDept = info.getDepartmentID();
		String storeID = info.getStoreNumber();
		int maxMatches = info.getMaximumMatches();
		String itemManufacurer = info.getManufacturer();
		if (itemManufacurer != null) {
			itemManufacurer = protectString(itemManufacurer);
		}
                //Changes for Rev 1.1 Starts
		//String qualifier = "1=1";
		String qualifier = null;
                //Changes for Rev 1.1 Ends
		// keep track of just searching by an item identifier (e.g. itemID,
		// posItemID, or both) and store id
		boolean searchingByItemAndStore = false;

		if (info.isSearchItemByItemNumber() && !StringUtils.isEmpty(info.getItemNumber())) {
			String itemNo = protectString(info.getItemNumber()); // protect any
																	// single
																	// quotation
																	// marks
			if (itemNo.indexOf('%') > -1) {
				qualifier = "(" + ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID + " LIKE UPPER(" + inQuotes(itemNo) + ")"
						+ " OR " + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " LIKE UPPER(" + inQuotes(itemNo)
						+ "))";
			} else {
				qualifier = "(" + ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID + " = " + inQuotes(itemNo)
						+ ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(itemNo) + ")";
				searchingByItemAndStore = true;
			}
		} else if (info.isSearchItemByPosItemID() && !StringUtils.isEmpty(info.getPosItemID())) {
			String posItemID = protectString(info.getPosItemID()); // protect
																	// any
																	// single
																	// quotation
																	// marks
			if (posItemID.indexOf('%') > -1) {
				qualifier = "UPPER(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + ")" + " LIKE " + "UPPER("
						+ inQuotes(posItemID) + ")";
			} else {
				qualifier = ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(posItemID);
				searchingByItemAndStore = true;
			}
		} else if (info.isSearchItemByItemID() && !StringUtils.isEmpty(info.getItemID())) {
			String itemID = protectString(info.getItemID()); // protect any
																// single
																// quotation
																// marks
			if (itemID.indexOf('%') > -1) {
				qualifier = "UPPER(" + ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + ")" + " LIKE " + "UPPER("
						+ inQuotes(itemID) + ")";
			} else {
				qualifier = ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID + " = " + inQuotes(itemID);
				searchingByItemAndStore = true;
			}
		}

		if (itemDesc != null) {
			Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());
			String descLocaleQualifier = null;
			
			//Changes for Rev 1.1 Starts
//			if (itemDesc.indexOf('%') > -1) {
//				descLocaleQualifier = "UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE "
//						+ "UPPER('" + itemDesc + "')";
//			} else {
//				descLocaleQualifier = "UPPER(" + ALIAS_ITEM_I8 + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE "
//						+ "UPPER('%" + itemDesc + "%')";
//			}
			/**if (itemDesc.indexOf('%') > -1) {
				descLocaleQualifier = "UPPER(" + ALIAS_ITEM + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE "
						+ "UPPER('" + itemDesc + "')";
			} else {
				descLocaleQualifier = "UPPER(" + ALIAS_ITEM + "." + FIELD_ITEM_DESCRIPTION + ")" + " LIKE "
						+ "UPPER('%" + itemDesc + "%')";
			}*/
		
			if (itemDesc.indexOf('%') > -1) {
				descLocaleQualifier = "" + ALIAS_ITEM + "." + FIELD_ITEM_DESCRIPTION + "" + " LIKE "
						+ "'" + itemDesc.toUpperCase() + "'";
			} else {
				descLocaleQualifier = "" + ALIAS_ITEM + "." + FIELD_ITEM_DESCRIPTION + "" + " LIKE "
						+ "'%" + itemDesc.toUpperCase() + "%'";
			}
			
//			if (info.getSearchLocale() != null) {
//				descLocaleQualifier = descLocaleQualifier + " AND " + ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " = '"
//						+ LocaleMap.getBestMatch(info.getSearchLocale()).toString() + "'";
//			} else {
//				descLocaleQualifier = descLocaleQualifier + " AND " + ALIAS_ITEM_I8 + "." + FIELD_LOCALE + " "
//						+ JdbcDataOperation.buildINClauseString(bestMatches);
//			}
			//Changes for Rev 1.1 Ends

			if (qualifier != null) {
				// Using Locale Table to search description
				qualifier = qualifier + " AND " + descLocaleQualifier;
			} else {
				// Using Locale Table to search description
				qualifier = descLocaleQualifier;
			}
		}

		// search by manufacturer
		if (itemManufacurer != null) {
			Set<Locale> bestMatches = LocaleMap.getBestMatch("", localeRequestor.getLocales());
			String manufLocaleQualifier = null;
			if (itemManufacurer.indexOf('%') > -1) {
				manufLocaleQualifier = "UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
						+ ")" + " LIKE " + "UPPER('" + itemManufacurer + "')";
			} else {
				manufLocaleQualifier = "UPPER(" + ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_NAME
						+ ")" + " LIKE " + "UPPER('%" + itemManufacurer + "%')";
			}
			if (info.getSearchLocale() != null) {
				manufLocaleQualifier = manufLocaleQualifier + " AND " + ALIAS_ITEM_MANUFACTURER_I18N + "."
						+ FIELD_LOCALE + " = '" + LocaleMap.getBestMatch(info.getSearchLocale()).toString() + "'";
			} else {
				manufLocaleQualifier = manufLocaleQualifier + " AND " + ALIAS_ITEM_MANUFACTURER_I18N + "."
						+ FIELD_LOCALE + " " + JdbcDataOperation.buildINClauseString(bestMatches);
			}
			if (qualifier != null) {
				// Using Locale Table to search manufacturer name
				qualifier = qualifier + " AND " + manufLocaleQualifier;
			} else {
				// Using Locale Table to search manufacturer name
				qualifier = manufLocaleQualifier;
			}
		}
		// End addition
		if (itemDept != null && !itemDept.equals("-1")) {
			qualifier = qualifier + " AND " + ALIAS_ITEM + "." + FIELD_POS_DEPARTMENT_ID + " = " + inQuotes(itemDept);
		}

		if (itemTypeCode != null && !itemTypeCode.equals(DEFAULT_SELECTED_VALUE)) {
			qualifier = qualifier + " AND " + ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + " = " + inQuotes(itemTypeCode);
		}

		if (itemUOMCode != null && !itemUOMCode.equals(DEFAULT_SELECTED_VALUE)) {
			qualifier = qualifier + " AND " + ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE + " = "
					+ inQuotes(itemUOMCode);
		}
		if (itemStyleCode != null && !itemStyleCode.equals(DEFAULT_SELECTED_VALUE)) {
			qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_STYLE_CODE + " = "
					+ inQuotes(itemStyleCode);
		}
		if (itemColorCode != null && !itemColorCode.equals(DEFAULT_SELECTED_VALUE)) {
			qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_COLOR_CODE + " = "
					+ inQuotes(itemColorCode);
		}
		if (itemSizeCode != null && !itemSizeCode.equals(DEFAULT_SELECTED_VALUE)) {
			qualifier = qualifier + " AND " + ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE + " = " + inQuotes(itemSizeCode);
		}

		if (storeID != null) {
//			qualifier = qualifier + " AND " + ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID + " = "
//					+ makeSafeString(storeID);
			searchingByItemAndStore = searchingByItemAndStore & true;
		} else {
			searchingByItemAndStore = false;
		}

		PLUItemIfc[] items = null;
		boolean usePlanogramID = info.isUsePlanogramID();

		// construct a PLU requestor
		PLURequestor pluRequestor = new PLURequestor();
		if (itemManufacurer == null) {
			pluRequestor.removeRequestType(PLURequestor.RequestType.Manufacturer);
		}
		if (!usePlanogramID) {
			pluRequestor.removeRequestType(PLURequestor.RequestType.Planogram);
		}

		if (searchingByItemAndStore) {
			if (info.isSearchItemByItemNumber()) {
				items = readPLUItemByItemNumber(dataConnection, info.getItemNumber(), storeID, false, pluRequestor,
						localeRequestor);
			} else if (info.isSearchItemByItemID()) {
				items = readPLUItemByItemID(dataConnection, info.getItemID(), storeID, false, pluRequestor,
						localeRequestor);
			} else if (info.isSearchItemByPosItemID()) {
				items = readPLUItemByPosItemID(dataConnection, info.getPosItemID(), storeID, false, pluRequestor,
						localeRequestor);
			}
		} else {
//                      Changes for Rev 1.1 Starts
			items = selectItemInfo(dataConnection, qualifier, maxMatches, pluRequestor, localeRequestor, storeID);
//                      Changes for Rev 1.1 Ends
		}

		items = readRelatedItems(dataConnection, items, storeID);

		return items;
	}
//Changes for Rev 1.1 Starts
	public PLUItemIfc[] selectItemInfo(JdbcDataConnection dataConnection, String qualifier, int maxMatches,
			PLURequestor pluRequestor, LocaleRequestor localeRequestor, String storeNo) throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
//		sql.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
		sql.addTable(TABLE_ITEM, ALIAS_ITEM);
		// add table for manufacturer
		boolean isManufacturerSearch = (qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1);
		if (isManufacturerSearch) {
			sql.addTable(TABLE_ITEM_MANUFACTURER, ALIAS_ITEM_MANUFACTURER);

		}

		// Set distinct flag to true
		//Changes for Rev 1.1 Starts
		//sql.setDistinctFlag(true);
		sql.setDistinctFlag(false);
		// add columns
		//sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID);// FIELD_ITEM_ID);
		//sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_ID);
                
		// add qualifiers
//		if (isManufacturerSearch) {
//			// need pos identities which are manufactured by this manu
//			sql.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_MANUFACTURER_ID + " = " + ALIAS_ITEM_MANUFACTURER
//					+ "." + FIELD_ITEM_MANUFACTURER_ID);
//		} else {
//			sql.addQualifier(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID + " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID);
//		}
                //Changes for Rev 1.1 Ends
		if (qualifier.indexOf(FIELD_STYLE_CODE) > -1 || qualifier.indexOf(FIELD_COLOR_CODE) > -1
				|| qualifier.indexOf(FIELD_SIZE_CODE) > -1 || qualifier.indexOf(FIELD_UNIT_OF_MEASURE_CODE) > -1) {
			sql.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);
			sql.addTable(TABLE_UNIT_OF_MEASURE, ALIAS_UNIT_OF_MEASURE);
			sql.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_ITEM_ID + " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID + " AND "
					+ ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE + " = "
					+ ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE);
		}

		// Using Locale Table for description search
		//Changes for Rev 1.1 Starts
		//sql.addTable(TABLE_ITEM_I8, ALIAS_ITEM_I8);
		//Changes for Rev 1.1 Ends
		
		if (qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1) {
			sql.addTable(TABLE_ITEM_MANUFACTURER_I18N, ALIAS_ITEM_MANUFACTURER_I18N);
		}

		// Add qualifier for Locale Table
		//Changes for Rev 1.1 Starts
		//sql.addQualifier(ALIAS_ITEM_I8 + "." + FIELD_ITEM_ID + " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID);
		//Changes for Rev 1.1 Ends
		if (qualifier.indexOf(FIELD_ITEM_MANUFACTURER_NAME) > -1) {
			sql.addQualifier(ALIAS_ITEM_MANUFACTURER_I18N + "." + FIELD_ITEM_MANUFACTURER_ID + " = "
					+ ALIAS_ITEM_MANUFACTURER + "." + FIELD_ITEM_MANUFACTURER_ID);
		}

		// use the parameterized qualifier as well
		sql.addQualifier(qualifier);
	       //	sql.addOrdering(ALIAS_POS_IDENTITY, FIELD_POS_ITEM_ID);

		// perform the query
		ArrayList<String> results = new ArrayList<String>();
		try {
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {
				int index = 0;
				String itemID = getSafeString(rs, ++index);
                        //Changes for Rev 1.1 Starts
			//	String storeID = getSafeString(rs, ++index);
			//	String result = itemID + "," + storeID;
                                String result = itemID + "," + storeNo;
                         //Changes for Rev 1.1 Ends
				results.add(result);
			}
			rs.close();
		} catch (DataException de) {
			logger.warn(de);
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "ReadItemInfo");
			throw new DataException(DataException.SQL_ERROR, "ReadItemInfo", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "ReadItemInfo", e);
		}

		if (results.isEmpty()) {
			throw new DataException(DataException.NO_DATA,
					"No PLU was found processing the result set in JdbcReadItemInfo.");
		}

		// see if data read exceeds maximum matches parameter
		if ((maxMatches > 0) && (results.size() > maxMatches)) {
			throw new DataException(DataException.RESULT_SET_SIZE,
					"Too many records were found processing the result set in JdbcReadItemInfo.");
		}

		// for each selected item id, read the PLUItem information
		ArrayList<PLUItemIfc> items = new ArrayList<PLUItemIfc>();
		Iterator<String> i = results.iterator();
		while (i.hasNext()) {
			String result = i.next();
			String itemID = null;
			String storeID = null;
			StringTokenizer strTk = new StringTokenizer(result, ",");
			while (strTk.hasMoreTokens()) {
				itemID = strTk.nextToken();
				storeID = strTk.nextToken();
			}
                        //Changes for Rev 1.1 Starts
		//	PLUItemIfc item = readPLUItem(dataConnection, itemID, storeID, false, pluRequestor, localeRequestor)[0];
			PLUItemIfc item = readPLUItemByDescSearch(dataConnection, itemID, storeID, false, pluRequestor, localeRequestor)[0];
                 	//Changes for Rev 1.1 Ends	
  	                items.add(item);
		}

		// convert results to array and return
		PLUItemIfc[] itemArray = new PLUItemIfc[items.size()];
		items.toArray(itemArray);
		return (itemArray);
	}
         //Changes for Rev 1.1 Ends
	/**
	 * Searches the protectString for a single quote then adds another single
	 * quote to protect it.
	 * 
	 * @param protectString
	 *            the string to protect
	 * @return the string with any single quotation marks protected
	 */
	static public String protectString(String protectString) {
		StringBuilder buf = new StringBuilder(protectString);
		int count = 0;
		for (int i = 0; i < buf.length(); ++i) {
			switch (buf.charAt(i)) {
			case '\'': // Single Quote
				buf.insert(i, '\''); // add another
				i++;
				break;

			case '\\': // backslash character
				// Escape the backslash character
				count = i++;
				buf = jdbcHelperClass.backSlashChar(count, buf);
				break;

			}
		}
		return (buf.toString());
	}

	/**
	 * Retrieves Item Messages per Item from the DB and sets it into the PLU
	 * Item Object
	 * 
	 * @return void
	 * @param connection
	 * @param items
	 * @throws DataException
	 */
	public void getItemMessages(JdbcDataConnection connection, PLUItemIfc[] items) {
		if (items != null) {
			for (int itemCtr = 0; itemCtr < items.length; itemCtr++) {
				PLUItemIfc item = items[itemCtr];
				getItemLevelMessages(connection, item);
			}
		}
	}

	/**
	 * Method which gets the ILRM Message for a Given Item
	 * 
	 * The Catch block simply prints the exception caused during execution as
	 * the requirement is to just print the error not propogate it
	 * 
	 * @param dataConnection
	 * @param item
	 * @throws DataException
	 */
	public void getItemLevelMessages(JdbcDataConnection dataConnection, PLUItemIfc item) {
		if (item != null) {
			SQLSelectStatement sql = new SQLSelectStatement();
			MessageDTO mdto = new MessageDTO();
			List<MessageDTO> messageList = new ArrayList<MessageDTO>();
			Map<String, List<MessageDTO>> messagesMap = new HashMap<String, List<MessageDTO>>(1);

			// add tables
			sql.addTable(TABLE_ITEM_MESSAGE_ASSOCIATION);
			sql.addTable(TABLE_ASSET_MESSAGES);
			sql.addTable(TABLE_ASSET_MESSAGES_I18N);

			sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TYPE);
			sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID);
			sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);
			sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_LOCALE);
			sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_MESSAGE_DESCRIPTION);
			// add columns from related item association

			// add qualifiers //TODO change ITEM_ID below to the IFC name
			sql.addQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_ITEM_ID, "'" + item.getItemID() + "'");
			sql.addJoinQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES,
					FIELD_MESSAGE_CODE_ID);
			sql.addJoinQualifier(TABLE_ASSET_MESSAGES, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES_I18N,
					FIELD_MESSAGE_CODE_ID);
			// price info exists in the store server.

			sql.addOrdering(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);

			try {
				String str = sql.getSQLString();
				String transactionType = null;
				String messageType = null;
				logger.debug(str);
				// execute the query and get the result set
				dataConnection.execute(sql.getSQLString());
				ResultSet rs = (ResultSet) dataConnection.getResult();

				while (rs.next()) {
					if (transactionType != null
							&& !transactionType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE))) {
						messageList.add(mdto);
						messagesMap.put(transactionType, messageList);
						messageList = null;
						messageType = null;
						messageList = new ArrayList<MessageDTO>();
					}

					if (messageType != null && messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE))) {
						mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)),
								rs.getString(FIELD_MESSAGE_DESCRIPTION));
						continue;
					} else if (messageType != null && !messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE))) {
						messageList.add(mdto);
					}

					messageType = rs.getString(FIELD_MESSAGE_TYPE);

					mdto = new MessageDTO();
					mdto.setDefaultItemMessage(rs.getString(FIELD_MESSAGE_DESCRIPTION));
					mdto.setItemMessageCodeID(rs.getString(FIELD_MESSAGE_CODE_ID));
					mdto.setItemMessageTransactionType(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE));
					mdto.setItemMessageType(messageType);
					mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)),
							rs.getString(FIELD_MESSAGE_DESCRIPTION));

					logger.info(mdto.toString());
					transactionType = rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE);
				}
				messageList.add(mdto);
				messagesMap.put(transactionType, messageList);
				item.setAllItemLevelMessages(messagesMap);
			} catch (DataException de) {
				logger.error(de.toString());
			} catch (SQLException se) {
				logger.error(se);
			} catch (Exception e) {
				logger.error("Unexpected exception in readItemMessage " + e);
			}
		}
	}
}

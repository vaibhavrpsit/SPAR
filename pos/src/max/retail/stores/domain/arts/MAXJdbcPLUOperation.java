/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.  
 *
 *  Rev 1.13    Sep 06, 2022		Kamlesh Pant	CapLimit Enforcement for Liquor
 *  Rev 1.12    May 04, 2017		Kritica Agarwal GST Changes
 *	Rev 1.11	Mar 29, 2017		hitesh dua		changes to get nm_ru_prdv (ru_prdv) from DB
 *	Rev 1.10	Mar 28, 2017		Mansi Goel		Changes to resolve database communication error for source/target based rules
 *  Rev 1.9     Mar 27, 2017        Abhishek Goyal  Item Scanning Taking Time if there are mutiple barcodes of same item
 *	Rev 1.8		Mar 27, 2017		Mansi Goel		Changes to resolve brand promotions are not working
 *	Rev 1.7		Mar 10, 2017		Mansi Goel		Changes to resolve Manual Discount Screen
 *  Rev 1.6     03 Mar, 2017        Abhishek Goyal  Item Advance Search Changes
 *	Rev 1.5     28 Feb, 2017        Abhishek Goyal  Item Scanning Taking More Time
 *	Rev 1.4		20 Feb,2017			Nadia Arora		MMRP Changes
 *	Rev 1.3		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *	Rev 1.2		Dec 20, 1016		Mansi Goel		Changes for Gift Card FES
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev 1.0     Oct 19, 2016		Ashish Yadav	Initial Draft Food Totals requirement.
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLParameterIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.arts.JdbcItemClassification;
import oracle.retail.stores.domain.arts.JdbcPLUOperation;
import oracle.retail.stores.domain.arts.JdbcSCLUOperation;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.StringSearchCriteria;
import oracle.retail.stores.domain.data.AbstractDBUtils;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountListIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKit;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

public class MAXJdbcPLUOperation extends JdbcPLUOperation implements MAXARTSDatabaseIfc, MAXDiscountRuleConstantsIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The logger to which log messages will be sent.
	 */
	protected static final Logger logger = Logger.getLogger(MAXJdbcPLUOperation.class);
	boolean isGstEnabled;

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.execute");
		// Change for Rev 1.12 : Starts
		isGstEnabled = Gateway.getBooleanProperty("application", "GSTEnabled", true);

		// Change for Rev 1.12 : Ends
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// send back the selected data (or lack thereof)
		PLUItemIfc[] items = null;
		Object dataObject = action.getDataObject();
		if (dataObject instanceof SearchCriteriaIfc) {
			((SearchCriteriaIfc) dataObject).setSearchItemByItemID(false);
			((SearchCriteriaIfc) dataObject).setSearchItemByItemNumber(true);
			items = getPluItems(connection, (SearchCriteriaIfc) dataObject);
		}
		// Retrieves item excluding store coupons
		else if (dataObject instanceof StringSearchCriteria) {
			StringSearchCriteria inquiry = (StringSearchCriteria) dataObject;
			String pluNumber = inquiry.getIdentifier();
			items = readPLUItem(connection, pluNumber, inquiry.getLocaleRequestor(), null);
			items = readRelatedItems(connection, items, null);
			getItemMessages(connection, items);
		}
		// Retrieves item excluding store coupons
		else {
			String pluNumber = (String) action.getDataObject();
			items = readPLUItem(connection, pluNumber, getRequestLocales(LocaleMap.getLocale(LocaleMap.DEFAULT)), null);
			items = readRelatedItems(connection, items, null);
			getItemMessages(connection, items);
		}
		/* India Localization - Tax related changes starts here */
		if (items != null) {
			if (((MAXSearchCriteriaIfc) dataObject).getInterStateDelivery()) {
				assignTaxAssignments(connection, items, ((MAXSearchCriteriaIfc) dataObject).getFromRegion(),
						((MAXSearchCriteriaIfc) dataObject).getToRegion());
			} else {
				assignTaxAssignments(connection, items);
			}

		}

		/* India Localization - Tax related changes stops here */

		dataTransaction.setResult(items);

		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.execute");
	}

	/**
	 * Assign the TaxAssignments for a given Tax Category of an Item for India
	 * Localization.
	 * 
	 * @param dataConnection DB connection
	 * @param items          An array of PLUItems with the Tax Category populated
	 * 
	 * @throws DataException India Localization Changes
	 */
	protected void assignTaxAssignments(JdbcDataConnection dataConnection, PLUItemIfc[] items) throws DataException {
		for (int i = 0; i < items.length; i++) {
			// Get the Tax Assignments for each PLUItem
			getTaxAssignments(dataConnection, items[i]);
		}

	}

	// ----------------------------------------------------------------------
	/**
	 * Reads Multiple TaxAssignments for a given Tax Category for an Item.
	 * <P>
	 * 
	 * @param dataConnection a connection to the database
	 * @param A              PLUItem with the TaxCategory already prepopulated.
	 * @return
	 * @exception DataException thrown when an error occurs executing the SQL
	 *                          against the DataConnection, or when processing the
	 *                          ResultSet
	 * @since 12.0.1 India Localization Changes related to Tax
	 **/
	// ----------------------------------------------------------------------
	public void getTaxAssignments(JdbcDataConnection dataConnection, PLUItemIfc item) throws DataException {

		ResultSet resultSet = null;
		ArrayList taxAssignmentList = new ArrayList();

		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql.addTable(TABLE_TAX_ASSIGNMENT, ALIAS_TAX_ASSIGNMENT);

			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_ID_CTGY_TX);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD_DSCR);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_RT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_FCT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TXBL_FCT);
			sql.addQualifier(
					ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = " + ((MAXPLUItemIfc) item).getTaxCategory());

			// If Item has TaxCategory as -1, it is not a valid Tax Category
			// Change for Rev 1.12 : Starts
			// if(isGstEnabled){
			if (Gateway.getBooleanProperty("application", "GSTEnabled", true)) {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_TO_REGION + " = "
						+ ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_FROM_REGION);
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_DIFF + " !='I' ");
			} else {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_DIFF + "  is null ");
			}
			// Change for Rev 1.12 : Ends
			// System.out.println("TAX : " +sql.getSQLString());
			dataConnection.execute(sql.getSQLString(), true);
			resultSet = (ResultSet) dataConnection.getResult();

			if (resultSet != null) {
				MAXTaxAssignment taxAssignment = null;
				while (resultSet.next()) {
					taxAssignment = new MAXTaxAssignment();

					int taxCtgy = resultSet.getInt(FIELD_ID_CTGY_TX);
					String taxCode = resultSet.getString(FIELD_TX_CD);

					String taxCodeDesc = resultSet.getString(FIELD_TX_CD_DSCR);
					BigDecimal taxRate = resultSet.getBigDecimal(FIELD_TX_RT);
					BigDecimal taxAmtFactor = resultSet.getBigDecimal(FIELD_TX_FCT);
					BigDecimal taxableAmtFactor = resultSet.getBigDecimal(FIELD_TXBL_FCT);

					taxAssignment.setTaxCode(taxCode);
					taxAssignment.setTaxCodeDescription(taxCodeDesc);
					taxAssignment.setTaxRate(taxRate);
					taxAssignment.setTaxableAmountFactor(taxableAmtFactor);
					taxAssignment.setTaxAmountFactor(taxAmtFactor);
					taxAssignment.setTaxCategory(taxCtgy);
					taxAssignmentList.add(taxAssignment);

				}
			}
			((MAXPLUItemIfc) item).addTaxAssignments(
					(MAXTaxAssignment[]) taxAssignmentList.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
		}

		catch (SQLException sqlException) {
			dataConnection.logSQLException(sqlException, " Tax Assignment lookup -- Error Retrieving Tax Assignments");
			throw new DataException(DataException.SQL_ERROR, "Tax Assignment lookup", sqlException);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException se) {
					dataConnection.logSQLException(se, "Tax Assignment lookup -- Could not close result handle");
				}
			}
		}

	}

	protected AdvancedPricingRuleIfc readSourceAmount(ResultSet rs, AdvancedPricingRuleIfc rule) throws SQLException {
		String sourceID;
		CurrencyIfc thrAmount;
		for (; rs.next(); rule.getSourceList().addEntry(sourceID, thrAmount)) {
			int index = 0;
			sourceID = JdbcDataOperation.getSafeString(rs, ++index);
			thrAmount = JdbcDataOperation.getCurrencyFromDecimal(rs, ++index);
		}

		return rule;
	}

	// Changes for Rev 1.1 : Starts
	public static String getItemGroups(PLUItemIfc pluItem, JdbcDataConnection dataConnection) {
		SQLSelectStatement sql = new SQLSelectStatement();
		ResultSet rs;
		String itemGroups = new String();
		String merchHierarchy = pluItem.getItemClassification().getMerchandiseHierarchyGroup();
		sql.setDistinctFlag(true);
		// add tables
		sql.addTable(TABLE_GROUP_ITEM_LIST, ALIAS_TABLE_GROUP_ITEM_LIST);
		sql.addColumn(FIELD_ITEM_GROUP_ID);

		sql.addQualifier("(" + FIELD_ITEM_ID + " = " + makeSafeString(pluItem.getItemID()) + " OR " + FIELD_ITEM_ID
				+ " = " + makeSafeString(MAXUtils.getDepartmentID(pluItem.getDepartmentID())) + " OR " + FIELD_ITEM_ID
				+ " = "
				+ makeSafeString(
						MAXUtils.getMerchandiseClass(pluItem.getItemClassification().getMerchandiseHierarchyGroup()))
				+ " OR " + FIELD_ITEM_ID + " = "
				+ makeSafeString(pluItem.getItemClassification().getMerchandiseHierarchyGroup()) + " OR "
				+ FIELD_ITEM_ID + " = " + makeSafeString(MAXUtils.getBrand(pluItem)) + ")");

		StringBuffer itemGroup = new StringBuffer();
		int index = 1;
		try {
			rs = execute(dataConnection, sql);
			while (rs.next()) {
				String itmgrp = getSafeString(rs, index);
				itemGroup.append(makeSafeString(itmgrp));
				itemGroup.append(",");
			}

			if (itemGroup.length() < 1) {
				while (rs.next()) {
					String itmgrp = getSafeString(rs, index);
					itemGroup.append(makeSafeString(itmgrp));
					itemGroup.append(",");
				}
				if (itemGroup.length() < 1) {
					itemGroup.append("'NO'");
				}
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}

		} catch (DataException e) {
			if (itemGroup.length() < 1) {
				itemGroup.append("");
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}
			e.printStackTrace();
		} catch (SQLException e) {
			if (itemGroup.length() < 1) {
				itemGroup.append("");
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}
		}
		return itemGroup.toString();
	}

	public String getItemGroupsExclusionGroup(String itemID, JdbcDataConnection dataConnection) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setDistinctFlag(true);
		sql.addTable(TABLE_GROUP_ITEM_LIST, ALIAS_TABLE_GROUP_ITEM_LIST);
		sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
		sql.addColumn(ALIAS_TABLE_GROUP_ITEM_LIST, FIELD_ITEM_GROUP_ID);
		sql.addQualifier(FIELD_ID_ITM + " = " + makeSafeString(itemID));
		sql.addQualifier(ALIAS_TABLE_GROUP_ITEM_LIST + "." + FIELD_ITEM_GROUP_ID + " = " + ALIAS_TABLE_ITEM_GROUP_PRDV
				+ "." + FIELD_ITEM_GROUP_ID);
		sql.addQualifier(ALIAS_TABLE_ITEM_GROUP_PRDV + "." + FIELD_ITEM_GROUP_TYPE + " != ExclusionList");
		StringBuffer itemGroup = new StringBuffer();
		int index = 1;
		try {
			for (ResultSet rs = execute(dataConnection, sql); rs.next(); itemGroup.append(",")) {
				String itmgrp = getSafeString(rs, index);
				itemGroup.append(makeSafeString(itmgrp));
			}

			if (itemGroup.length() < 1)
				itemGroup.append("");
			else
				itemGroup.deleteCharAt(itemGroup.length() - 1);
		} catch (DataException e) {
			if (itemGroup.length() < 1)
				itemGroup.append("");
			else
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			e.printStackTrace();
		} catch (SQLException e) {
			if (itemGroup.length() < 1)
				itemGroup.append("");
			else
				itemGroup.deleteCharAt(itemGroup.length() - 1);
		}
		return itemGroup.toString();
	}

	protected SQLSelectStatement buildItemGroupSQL(int id) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
		sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID);
		sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_THR_QUANTITY);
		sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID + " = " + id);
		sql.addQualifier(FIELD_ITEM_GROUP_TYPE + " != 'ExclusionList'");
		return sql;
	}

	// Changes for Rev 1.1 : Starts
	protected SQLSelectStatement buildRuleSQL(int comparisonBasis, boolean deal, Integer ruleID, PLUItemIfc pluItem,
			boolean storeLevelDisc) {
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
		// add columns
		addCommonColumns(sql);
		// changes for rev 1.11
		sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_NAME);
		if (deal) {
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_MIX_MATCH_PRICE_REDUCTION_MONETARY_AMOUNT);
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_MIX_MATCH_PRICE_REDUCTION_PERCENT);
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_MIX_MATCH_PRICE_POINT_REDUCTION);
		} else {
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_MONETARY_AMOUNT);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_PERCENT);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_PRICE_POINT);
		}
		// add comparison specific columns and joins
		switch (comparisonBasis) {
		case COMPARISON_BASIS_ITEM_ID:
			// add columns
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID);

			// add qualifiers
			if (ruleID != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID,
						pluItem.getItemID()));
			} else if (storeLevelDisc) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID, "*"));
			}

			// add joins
			sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + " ON "
					+ ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_RETAIL_STORE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
			break;

		case COMPARISON_BASIS_DEPARTMENT_ID:
			// add columns
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_POS_DEPARTMENT_ID);

			// add qualifiers
			if (ruleID != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_POS_DEPARTMENT_ID, MAXUtils.getDepartmentID(pluItem.getDepartmentID())));
			} else if (storeLevelDisc) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID, "*"));
			}

			// add joins
			sql.addOuterJoinQualifier(" JOIN " + TABLE_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + " ON "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_RETAIL_STORE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
			break;

		case COMPARISON_BASIS_MERCHANDISE_CLASS:
			// add columns
			sql.addColumn(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY,
					FIELD_MERCHANDISE_CLASSIFICATION_CODE);

			// add qualifiers
			if (ruleID != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {

				sql.addQualifier(new SQLParameterValue(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_MERCHANDISE_CLASSIFICATION_CODE,
						MAXUtils.getMerchandiseClass(pluItem.getItemClassification().getMerchandiseHierarchyGroup())));
			} else if (storeLevelDisc) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID, "*"));
			}

			// add joins
			sql.addOuterJoinQualifier(" JOIN " + TABLE_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY + " ON "
					+ ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY + "."
					+ FIELD_PRICE_DERIVATION_RULE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "."
					+ FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_RETAIL_STORE_ID
					+ " = " + ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
			break;

		case COMPARISON_BASIS_SUBCLASS:
			// add columns
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_POS_DEPARTMENT_ID);

			// add qualifiers
			if (ruleID != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_POS_DEPARTMENT_ID, pluItem.getItemClassification().getMerchandiseHierarchyGroup()));
			} else if (storeLevelDisc) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID, "*"));
			}

			// add joins
			sql.addOuterJoinQualifier(" JOIN " + TABLE_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + " ON "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_RETAIL_STORE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
			break;

		case COMPARISON_BASIS_BRAND:
			// add columns
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_BRAND_NAME);

			// add qualifiers
			if (ruleID != null) {
				sql.addQualifier(new SQLParameterValue(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
						FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_TABLE_BRAND_PRDV, FIELD_BRAND_NAME, MAXUtils.getBrand(pluItem)));
			} else if (storeLevelDisc) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID, "*"));
			}

			// add joins
			sql.addOuterJoinQualifier(" JOIN " + TABLE_BRAND_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_TABLE_BRAND_PRDV + " ON " + ALIAS_TABLE_BRAND_PRDV + "." + FIELD_PRICE_DERIVATION_RULE_ID
					+ " = " + ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_TABLE_BRAND_PRDV + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "."
					+ FIELD_RETAIL_STORE_ID);
			break;

		case COMPARISON_BASIS_ITEM_GROUP:
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_THR_AMOUNT);
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_THR_QUANTITY);
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID);

			if (ruleID != null) {
				sql.addQualifier(
						new SQLParameterValue(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_PRICE_DERIVATION_RULE_ID, ruleID));
			}

			if (pluItem != null) {
				logger.debug("Item Groups are : " + ((MAXPLUItemIfc) pluItem).getItemGroups());
				String itemgroups = ((MAXPLUItemIfc) pluItem).getItemGroups();
				if (itemgroups.length() > 1) {
					sql.addQualifier(
							ALIAS_TABLE_ITEM_GROUP_PRDV + "." + FIELD_ITEM_GROUP_ID + " IN  (" + itemgroups + ")");
				}
			} else
				sql.addQualifier(ALIAS_TABLE_ITEM_GROUP_PRDV + "." + FIELD_ITEM_GROUP_ID + " = ''");

			sql.addQualifier(ALIAS_TABLE_ITEM_GROUP_PRDV + "." + FIELD_ITEM_GROUP_ID + "!= 'ExclusionList'");

			sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY + " "
					+ ALIAS_TABLE_ITEM_GROUP_PRDV + " ON " + ALIAS_TABLE_ITEM_GROUP_PRDV + "."
					+ FIELD_PRICE_DERIVATION_RULE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "."
					+ FIELD_PRICE_DERIVATION_RULE_ID + " AND " + ALIAS_TABLE_ITEM_GROUP_PRDV + "."
					+ FIELD_RETAIL_STORE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
			break;
		} // switch

		// add dates for all discounts
		sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE);
		sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE);
		sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_CUSTOMER_PRICING_GROUP_ID);

		if (deal) {
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_MIX_AND_MATCH_LIMIT_COUNT);
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_PROMOTIONAL_PRODUCT_ID);
			sql.addColumn(ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM, FIELD_COMPARISON_BASIS_CODE);

			sql.addOuterJoinQualifier(" JOIN " + TABLE_MIX_AND_MATCH_PRICE_DERIVATION_ITEM + " "
					+ ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM + " ON " + ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM
					+ "." + FIELD_PRICE_DERIVATION_RULE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "."
					+ FIELD_PRICE_DERIVATION_RULE_ID + " AND " + ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_ITEM + "."
					+ FIELD_RETAIL_STORE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID);
		} else {
			// Changes for Rev 1.1 : Starts
			sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_RULE_QUANTITY);
			// Changes for Rev 1.1 : Ends
			sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_DERIVATION + " " + ALIAS_ITEM_PRICE_DERIVATION
					+ " ON " + ALIAS_ITEM_PRICE_DERIVATION + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = "
					+ ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " AND "
					+ ALIAS_ITEM_PRICE_DERIVATION + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_PRICE_DERIVATION_RULE
					+ "." + FIELD_RETAIL_STORE_ID);
		}
		sql.addQualifier(currentTimestampRangeCheckingString(
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE,
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE));

		// add qualifier for the status
		String statusExpiredString = STATUS_DESCRIPTORS[STATUS_EXPIRED];
		sql.addQualifier(new SQLParameterValue(
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_STATUS_CODE + " != ?",
				statusExpiredString));

		// limit search to type of item
		if (pluItem != null) {
			int itemType = ItemClassificationConstantsIfc.TYPE_UNKNOWN;
			if (pluItem.getItemClassification() != null) {
				itemType = pluItem.getItemClassification().getItemType();
			}

			switch (itemType) {
			case ItemClassificationConstantsIfc.TYPE_STORE_COUPON:
				sql.addQualifier(new SQLParameterValue(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
						ASSIGNMENT_STORE_COUPON));
				break;

			default:
				sql.addQualifier(
						new SQLParameterValue(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE, ASSIGNMENT_ITEM));
			}
		}

		return sql;
	}

	public ArrayList<MAXAdvancedPricingRuleIfc> selectInvoiceDiscounts(JdbcDataConnection dataConnection,
			PLUItemIfc pluItem) {

		ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules = new ArrayList<MAXAdvancedPricingRuleIfc>();
		ResultSet rs = null;
		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql = buildInvoiceRuleSql(pluItem);
			sql.getSQLString();
			rs = execute(dataConnection, sql);
			while (rs.next()) {
				AdvancedPricingRuleIfc rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();
				int index = 0;
				rule.setRuleID(JdbcDataOperation.getSafeString(rs, ++index));
				rule.setStoreID(JdbcDataOperation.getSafeString(rs, ++index));
				index = readBasicAdvancedPricingRulesForInvoiceRules(rs, rule, dataConnection);

				invoiceRules.add((MAXAdvancedPricingRuleIfc) rule);
			}
		} catch (DataException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return invoiceRules;
	}

	protected SQLSelectStatement buildInvoiceRuleSql(PLUItemIfc pluItem) {

		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
		// add columns
		addCommonColumns(sql);
		sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_MONETARY_AMOUNT);
		sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_PERCENT);
		sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_PRICE_POINT);

		sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_NAME);

		sql.addTable(TABLE_PRICE_DERIVATION_RULE_TYPE, ALIAS_PRICE_DERIVATION_RULE_TYPE);
		sql.addTable(TABLE_ITEM_PRICE_DERIVATION, ALIAS_ITEM_PRICE_DERIVATION);
		sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_DESCRIPTION,
				ALIAS_PRICE_DERIVATION_RULE_TYPE, FIELD_RULE_TYPE_NAME);
		sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_RETAIL_STORE_ID, ALIAS_PRICE_DERIVATION_RULE_TYPE,
				FIELD_RETAIL_STORE_ID);

		sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID, ALIAS_ITEM_PRICE_DERIVATION,
				FIELD_PRICE_DERIVATION_RULE_ID);
		sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_RETAIL_STORE_ID, ALIAS_ITEM_PRICE_DERIVATION,
				FIELD_RETAIL_STORE_ID);
		// Changes for Rev 1.7 : Starts
		sql.addQualifier(new SQLParameterValue(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_ITM_DISC_TYPE_CODE + " = 9"));
		// Changes for Rev 1.7 : Ends
		sql.addQualifier(currentTimestampRangeCheckingString(
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE,
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE));
		String statusExpiredString = STATUS_DESCRIPTORS[STATUS_EXPIRED];
		sql.addQualifier(new SQLParameterValue(
				ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_STATUS_CODE + " != ?",
				statusExpiredString));
		/* Change for Rev 1.25 : Start */
		sql.addQualifier(new SQLParameterValue(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_RETAIL_STORE_ID + " = ?",
				pluItem.getStoreID()));

		// sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE_TYPE+ "." + FIELD_RULE_TYPE_CODE
		// + " = 34");
		// sql.addORQualifier(ALIAS_PRICE_DERIVATION_RULE_TYPE+ "." +
		// FIELD_RULE_TYPE_CODE , 33);

		// sql.addTable(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY,
		// MAXARTSDatabaseIfc.ALIAS_PRICE_DERIVATION_RULE_ELIGIBILITY);

		System.out.println(sql.toString());
		return sql;
	}

	protected int readBasicAdvancedPricingRulesForInvoiceRules(ResultSet rs, AdvancedPricingRuleIfc rule,
			JdbcDataConnection dataConnection) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcPLUOperation.readBasicAdvancedPricingRulesForInvoiceRules begins.");

		int index = 2;
		String appliedWhen = null;
		String status = null;
		String inBestDeal = null;

		appliedWhen = getSafeString(rs, ++index);
		status = getSafeString(rs, ++index);
		inBestDeal = getSafeString(rs, ++index);

		rule.setDescription(getSafeString(rs, ++index));

		Float reasonCodeF = new Float(rs.getFloat(++index));
		int reasonCode = reasonCodeF.intValue();
		String reasonCodeString = CodeConstantsIfc.CODE_UNDEFINED;
		try {
			reasonCodeString = Integer.toString(reasonCode);
		} catch (Exception e) {
			// do nothing, code already initialized to UNDEFINED
		}
		LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
		reason.setCode(reasonCodeString);
		rule.setReason(reason);
		rule.setReasonCode(reasonCode);
		
		Float discountScopeF = new Float(rs.getFloat(++index));
		int discountScope = discountScopeF.intValue();
		rule.setDiscountScope(discountScope);
		Float discountMethodF = new Float(rs.getFloat(++index));
		int discountMethod = discountMethodF.intValue();
		rule.setDiscountMethod(discountMethod);
		Float argumentBasisF = new Float(rs.getFloat(++index));
		int argumentBasis = argumentBasisF.intValue();
		rule.setAssignmentBasis(argumentBasis);

		Float applicationLimitF = new Float(rs.getFloat(++index));
		int applicationLimit = applicationLimitF.intValue();
		rule.setApplicationLimit(applicationLimit);

		rule.setSourceItemPriceCategory(getSafeString(rs, ++index));
		rule.setTargetItemPriceCategory(getSafeString(rs, ++index));

		rule.setSourceThreshold(getCurrencyFromDecimal(rs, ++index));
		rule.setSourceLimit(getCurrencyFromDecimal(rs, ++index));
		rule.setTargetThreshold(getCurrencyFromDecimal(rs, ++index));
		rule.setTargetLimit(getCurrencyFromDecimal(rs, ++index));
		rule.setSourceAnyQuantity(rs.getInt(++index));
		rule.setTargetAnyQuantity(rs.getInt(++index));
		Float thresholdTypeF = new Float(rs.getFloat(++index));
		int thresholdType = thresholdTypeF.intValue();
		rule.setThresholdTypeCode(thresholdType);
		rule.setAccountingMethod(rs.getInt(++index));
		boolean flag = getBooleanFromString(rs, ++index);
		rule.setDealDistribution(flag);
		flag = getBooleanFromString(rs, ++index);
		rule.setAllowRepeatingSources(flag);
		rule.setCalcDiscOnItemType(rs.getInt(++index));

		// Add the Promotion Fields here
		try {
			rule.setPromotionId(rs.getInt(++index));
		} catch (Exception e) {
			rule.setPromotionId(0);
		}
		rule.setPromotionComponentId(rs.getInt(++index));
		rule.setPromotionComponentDetailId(rs.getInt(++index));

		rule.getSourceList().setItemThreshold(rule.getSourceThreshold());

		CurrencyIfc discountAmount = getCurrencyFromDecimal(rs, ++index);

		rule.setDiscountRate(getPercentage(rs, ++index));

		// identify if its a FixedPrice Discount
		CurrencyIfc fixedPrice = getCurrencyFromDecimal(rs, ++index);
		if (fixedPrice.signum() != 0) {
			rule.setFixedPrice(fixedPrice);
		} else {
			rule.setDiscountAmount(discountAmount);
		}

		String localizedName = JdbcDataOperation.getSafeString(rs, ++index);
		Locale lcl = LocaleMap.getLocale(LocaleMap.DEFAULT);
		rule.setName(lcl, localizedName);
		rule.getReason().setText(rule.getLocalizedNames());
		setDiscountRuleValues(rule, appliedWhen, status, inBestDeal);
		// added by Vaibhav for getting targetitemslist
		if (rule.getReason().getCode().equalsIgnoreCase("33") || rule.getReason().getCode().equalsIgnoreCase("34")) {
			List<String> itemList = readTargetItemsList(rs, rule, dataConnection);

			if (itemList.size() > 0) {
				((MAXAdvancedPricingRuleIfc)rule).setItemList(itemList);
				
			}
		}

		// ((MAXAdvancedPricingRuleIfc)rule).setTargetItemId(rs.getString(34));
		// ((MAXAdvancedPricingRuleIfc)rule).getTargetItemId();
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcPLUOperation.readBasicAdvancedPricingRulesForInvoiceRules() ends.");
		return index;
	}

	protected SQLSelectStatement buildBrandSQL(int id) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_BRAND_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_BRAND_PRDV);
		sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_BRAND_NAME);
		sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_THR_QUANTITY);
		sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID + " = " + id);
		return sql;
	}

	protected SQLSelectStatement buildSelectPLUItemExclusionListSQL(String qualifier, String fieldsToInclude) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setDistinctFlag(true);
		sql.addTable(TABLE_GROUP_ITEM_LIST, ALIAS_TABLE_GROUP_ITEM_LIST);
		sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
		sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_PRICE_DERIVATION_SUMMARY_RULE_ID);
		sql.addColumn(ALIAS_TABLE_GROUP_ITEM_LIST, FIELD_ITEM_GROUP_ID);
		sql.addColumn(ALIAS_TABLE_GROUP_ITEM_LIST, FIELD_ITEM_ID);
		sql.addQualifier(ALIAS_TABLE_ITEM_GROUP_PRDV, "ITEM_GRP_TYPE", "'" + fieldsToInclude + "'");
		sql.addJoinQualifier(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID, ALIAS_TABLE_ITEM_GROUP_PRDV,
				FIELD_ITEM_GROUP_ID);
		return sql;
	}

	// Changes for Rev 1.1 : Ends

	private String getLtrimString(String orgString) {
		int k = orgString.length();
		for (int i = 0; i <= k; k--) {
			if (orgString.startsWith("0")) {
				orgString = orgString.replaceFirst("0", "");
			} else {
				return orgString;
			}
		}
		return orgString;
	}

	public PLUItemIfc readRelatedItems(JdbcDataConnection dataConnection, PLUItemIfc item, String storeID)
			throws DataException {
		if (item != null) {
			HashMap<String, RelatedItemGroupIfc> relatedItemContainer = new HashMap<String, RelatedItemGroupIfc>(1);
			item.setRelatedItemContainer(relatedItemContainer);

			SQLSelectStatement sql = new SQLSelectStatement();

			// add tables
			sql.addTable(TABLE_RELATED_ITEM_ASSOCIATION);

			// add columns from related item association
			sql.addColumn(FIELD_RELATED_ITEM_ID);
			sql.addColumn(FIELD_POS_ITEM_ID);
			sql.addColumn(FIELD_RELATED_ITEM_TYPE_CODE);
			sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG);
			sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG);
			// add columns from item
			sql.addColumn(FIELD_POS_DEPARTMENT_ID);

			sql.addOuterJoinQualifier(" JOIN " + TABLE_POS_IDENTITY + " ON " + TABLE_POS_IDENTITY + "." + FIELD_ITEM_ID
					+ " = " + TABLE_RELATED_ITEM_ASSOCIATION + "." + FIELD_RELATED_ITEM_ID);
			sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM + " ON " + TABLE_ITEM + "." + FIELD_ITEM_ID + " = "
					+ TABLE_RELATED_ITEM_ASSOCIATION + "." + FIELD_ITEM_ID);
			sql.addOuterJoinQualifier(" JOIN " + TABLE_RETAIL_STORE_ITEM + " ON " + TABLE_RETAIL_STORE_ITEM + "."
					+ FIELD_ITEM_ID + " = " + TABLE_ITEM + "." + FIELD_ITEM_ID);

			// add qualifiers
			sql.addQualifier(new SQLParameterValue(TABLE_RETAIL_STORE_ITEM, FIELD_ITEM_ID, item.getItemID()));
			// If no storeID is given, then the data needs to be setup such that
			// only one store's
			// price info exists in the store server.
			if (storeID != null && storeID.length() > 0) {
				sql.addQualifier(new SQLParameterValue(TABLE_RETAIL_STORE_ITEM, FIELD_RETAIL_STORE_ID, storeID));
			}

			try {
				// execute the query and get the result set
				ResultSet rs = execute(dataConnection, sql);
				List<RelatedItemIfc> relatedItems = new ArrayList<RelatedItemIfc>();
				while (rs.next()) {
					// parse result set and create domain objects as necessary
					int index = 0;
					String relatedItemID = getSafeString(rs, ++index);
					String posItemID = getSafeString(rs, ++index);
					String typeCode = getSafeString(rs, ++index);
					boolean deleteable = getBooleanFromString(rs, ++index);
					boolean returnable = getBooleanFromString(rs, ++index);
					String departmentID = getSafeString(rs, ++index);

					RelatedItemSummaryIfc relatedItemSummary = DomainGateway.getFactory()
							.getRelatedItemSummaryInstance();
					// relatedItemSummary.setLocalizedDescriptions(item.getLocalizedDescriptions());
					Locale sqlLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
					LocaleRequestor localeRequestor = getRequestLocales(sqlLocale);
					relatedItemSummary.setLocalizedDescriptions(
							applyLocaleDependentDescriptions(dataConnection, relatedItemID, localeRequestor));
					relatedItemSummary.setDepartmentID(departmentID);
					relatedItemSummary.setItemID(relatedItemID);
					relatedItemSummary.setPosItemID(posItemID);

					RelatedItemIfc relatedItem = DomainGateway.getFactory().getRelatedItemInstance();
					relatedItem.setDeleteable(deleteable);
					relatedItem.setReturnable(returnable);
					relatedItem.setRelatedItemSummary(relatedItemSummary);
					RelatedItemGroupIfc relatedItemGroup = item.getRelatedItemContainer().get(typeCode);
					if (relatedItemGroup == null) {
						relatedItemGroup = DomainGateway.getFactory().getRelatedItemGroupInstance();
						item.getRelatedItemContainer().put(typeCode, relatedItemGroup);
					}
					relatedItemGroup.addRelatedItem(relatedItem);
					relatedItems.add(relatedItem);
				}

				MAXJdbcSelectPriceChange selectPriceChange = new MAXJdbcSelectPriceChange();
				PLUItemIfc transientPLUItem = instantiatePLUItem();
				transientPLUItem.setStoreID(storeID);
				Calendar now = Calendar.getInstance();
				for (int i = 0; i < relatedItems.size(); i++) {
					RelatedItemIfc relatedItem = relatedItems.get(i);
					RelatedItemSummaryIfc relatedItemSummary = relatedItem.getRelatedItemSummary();
					transientPLUItem.setItemID(relatedItemSummary.getItemID());

					PriceChangeIfc[] changes = selectPriceChange.readPermanentPriceChanges(dataConnection,
							transientPLUItem, now); // this is not
													// timezone safe
					transientPLUItem.setPermanentPriceChanges(changes);
					// Populate the maximumRetailPriceChanges
					/*
					 * MAXMaximumRetailPriceChangeIfc[] activeMRPChanges = selectPriceChange
					 * .readEffectiveUniqueMaximumRetailPriceChanges (dataConnection,
					 * Calendar.getInstance(), transientPLUItem.getStoreID(),
					 * transientPLUItem.getItemID(), true,
					 * transientPLUItem.getPermanentPriceChanges());
					 * 
					 * if (transientPLUItem instanceof MAXPLUItemIfc) { ((MAXPLUItemIfc)
					 * transientPLUItem).setActiveMaximumRetailPriceChanges (activeMRPChanges); }
					 * else if (transientPLUItem instanceof MAXGiftCardPLUItemIfc) {
					 * //((MAXGiftCardPLUItemIfc)
					 * transientPLUItem).setActiveMaximumRetailPriceChanges (activeMRPChanges); }
					 * 
					 * // Populate the Inactive maximumRetailPriceChanges
					 * MAXMaximumRetailPriceChangeIfc[] inActiveMRPChanges = selectPriceChange
					 * .readEffectiveUniqueMaximumRetailPriceChanges (dataConnection,
					 * Calendar.getInstance(), transientPLUItem.getStoreID(),
					 * transientPLUItem.getItemID(), false, null);
					 * 
					 * if (transientPLUItem instanceof MAXPLUItemIfc) { ((MAXPLUItemIfc)
					 * transientPLUItem).setInActiveMaximumRetailPriceChanges (inActiveMRPChanges);
					 * } else if (transientPLUItem instanceof MAXGiftCardPLUItem) {
					 * //((MAXGiftCardPLUItem) transientPLUItem
					 * ).setInActiveMaximumRetailPriceChanges(inActiveMRPChanges ); }
					 */
					changes = selectPriceChange.readAllTemporaryPriceChanges(dataConnection, transientPLUItem, now); // this
																														// is
																														// not
																														// timezone
																														// safe
					transientPLUItem.setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(changes);

					changes = selectPriceChange.readClearancePriceChanges(dataConnection, transientPLUItem,
							Calendar.getInstance()); // this not timezone
														// safe

					transientPLUItem.setClearancePriceChangesAndClearancePriceChangesForReturns(changes);

					relatedItemSummary.setPrice(transientPLUItem.getPrice());

					applyItemImages(dataConnection, transientPLUItem, null);
					relatedItemSummary.setItemImage(transientPLUItem.getItemImage());
				}
			} catch (DataException de) {
				logger.warn(de.toString());
				throw de;
			} catch (SQLException se) {
				dataConnection.logSQLException(se, "readRelatedItems");
				throw new DataException(DataException.SQL_ERROR, "readRelatedItems", se);
			} catch (Exception e) {
				logger.error("Unexpected exception in readRelatedItems " + e);
				throw new DataException(DataException.UNKNOWN, "readRelatedItems", e);
			}
		}
		return item;
	}

	protected PLUItemIfc[] readPLUItemByKey(JdbcDataConnection dataConnection, SQLParameterValue key, String storeID,
			boolean retrieveStoreCoupons, PLURequestor pluRequestor, LocaleRequestor sqlLocale) throws DataException {
		List<SQLParameterIfc> qualifiers = new ArrayList<SQLParameterIfc>(2);
		qualifiers.add(key);
		// 1.2 start here....//+"OR as_itm.ID_STRC_MR_CD0="+getPOSItemID(key);
		// Changes for Rev 1.6 starts
//		String sql = "SELECT DISTINCT ITM.Id_itm from ID_IDN_PS POSID JOIN AS_ITM ITM on POSID.ID_ITM=ITM.ID_ITM where POSID.ID_ITM_POS="
//				+ getPOSItemID(key.getValue().toString()); // Karni
//		ResultSet rs;
//		try {
//			dataConnection.execute(sql);
//
//			String posItemID;
//			for (rs = (ResultSet) dataConnection.getResult(); rs.next();) {
//
//				posItemID = rs.getString(1);
//
//				qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, "ID_ITM", posItemID));
//			}
//
//			rs.close();
//		} catch (DataException de) {
//			logger.warn(de);
//			throw de;
//		} catch (SQLException se) {
//			dataConnection.logSQLException(se, "PLUItem lookup");
//			throw new DataException(1, "PLUItem lookup", se);
//		} catch (Exception e) {
//			throw new DataException(0, "PLUItem lookup", e);
//		}
//		if (storeID != null) {
//			qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_RETAIL_STORE_ID, storeID));
//		}
		// Changes for Rev 1.6 ends
		PLUItemIfc[] items = selectPLUItem(dataConnection, qualifiers, pluRequestor, sqlLocale);

		// Process the result to filter out coupons if necessary. Do not use the
		// StoreCouponQualifier to weed out
		// store coupons. This may cause ItemNotFoundExceptions during the join
		// which will make any transactions
		// containing store coupons irretrievable for returns.

		/*
		 * 27SEP07 Filtering out coupons does not allow the client to know when coupons
		 * are part of a return transaction, or reject them when coupons are attempted
		 * to be returned during a no-receipt return. Coupon plus should be made
		 * ProhibitReturnFlag = true, if the domain logic prefers them not to be
		 * returned. - CMG
		 */

		// if (retrieveStoreCoupons == false && items.length > 0)
		// {
		// ArrayList itemList = new ArrayList();
		// for(int i=0; i<items.length; i++)
		// {
		// if(items[i].getItemClassification().getItemType() !=
		// ItemClassificationConstantsIfc.TYPE_STORE_COUPON)
		// {
		// itemList.add(items[i]);
		// }
		// }
		// int size = itemList.size();
		// // if there are no items left, throw a data exception
		// if (size == 0)
		// {
		// throw new DataException(DataException.WARNING,
		// "Store coupons can not be included in this lookup.");
		// }
		// items = (PLUItemIfc[]) itemList.toArray(new PLUItemIfc[size]);
		// }
		// since we are not filtering out coupons, configure any coupons we have
		for (int i = items.length - 1; i >= 0; i--) {
			if (items[i].isStoreCoupon())
				JdbcSCLUOperation.configureStoreCouponRules(items[i]);

		}

		return items;
	}

	public PLUItemIfc[] selectPLUItem(JdbcDataConnection dataConnection, List<SQLParameterIfc> andQualifiers,
			List<SQLParameterIfc> orQualifiers, PLURequestor pluRequestor, LocaleRequestor sqlLocale)
			throws DataException {

		if (usesWildcards(andQualifiers) || usesWildcards(orQualifiers)) {
			// wildcards were used in the query
			// call selectPLUItems() to retrieve all the matching items
			return selectPLUItems(dataConnection, andQualifiers, orQualifiers, pluRequestor, sqlLocale);
		}

		SQLSelectStatement sql = buildSelectPLUItemSQL(andQualifiers, orQualifiers, pluRequestor, sqlLocale);
		// System.out.println("Sql : "+sql.getSQLString());
		sql.getSQLString();
		// create a reference for the item
		ArrayList<PLUItemIfc> list = new ArrayList<PLUItemIfc>();
		// Changes for Rev 1.9 Starts
		HashMap<String, PLUItemIfc> hm = new HashMap<String, PLUItemIfc>();
		// Changes for Rev 1.9 Ends
		try {
			// execute the query and get the result set
			ResultSet rs = execute(dataConnection, sql);

			while (rs.next()) {
				// change for Rev 1.12 : Starts
				if (isGstEnabled && (rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6) == null
						|| rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6).isEmpty())) {
					throw new DataException(DataException.NO_DATA, "HSN number not found");
				}
				// change for Rev 1.12 : Ends
				PLUItemIfc pluItem = createPLUItem(rs);
				// Changes for Rev 1.9 Starts
				// list.add(pluItem);
				hm.put(pluItem.getItem().getItemID(), pluItem);
				// Changes for Rev 1.9 Ends
			}
			// Changes for Rev 1.9 Starts
			for (Iterator<String> iterator = hm.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				list.add(hm.get(key));
			}
			// Changes for Rev 1.9 Ends
			// Changes for Rev 1.3 : Starts
			updateItemProperties(list, dataConnection);
			// Changes for Rev 1.3 : Ends
			// getting classifications

			JdbcItemClassification itemClassification = new JdbcItemClassification();
			MAXJdbcSelectPriceChange selectPriceChange = new MAXJdbcSelectPriceChange();

			for (Iterator<PLUItemIfc> iter = list.iterator(); iter.hasNext();) {
				PLUItemIfc pluItem = iter.next();
				// Chnages start for rev 1.0 (Food Totals : commenting below
				// piece of code)
				/*
				 * List<MerchandiseClassificationIfc> itemClassifications = itemClassification
				 * .getClassifications(dataConnection, pluItem); ItemClassificationIfc ic =
				 * pluItem.getItemClassification();
				 * ic.setMerchandiseClassifications(itemClassifications);
				 */
				// Chnages end for rev 1.0 (Food Totals : commenting below piece
				// of code)
				// populate price changes
				if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.Price)) {

					// Changes Starts by Kamlesh Pant for SpecialEmpDiscount
					pluItem.setEmpID(pluRequestor.isEmpID());
					// Changes Ends for SpecialEmpDiscount
					PriceChangeIfc[] changes = selectPriceChange.readPermanentPriceChanges(dataConnection, pluItem,
							Calendar.getInstance()); // this is not timezone // safe
					pluItem.setPermanentPriceChanges(changes);
					/* Rev 1.4 changes starts */
					MAXMaximumRetailPriceChangeIfc[] activeMRPChanges = selectPriceChange
							.readEffectiveUniqueMaximumRetailPriceChanges(dataConnection, Calendar.getInstance(),
									pluItem.getStoreID(), pluItem.getItemID(), true,
									pluItem.getPermanentPriceChanges());
					/* Rev 1.5 start */
					if (pluItem instanceof MAXPLUItem) {
						((MAXPLUItem) pluItem).setActiveMaximumRetailPriceChanges(activeMRPChanges);
					} else if (pluItem instanceof MAXGiftCardPLUItem) {
						// ((MAXGiftCardPLUItem)
						// pluItem).setActiveMaximumRetailPriceChanges(activeMRPChanges);
					}

					// Populate the Inactive maximumRetailPriceChanges
					MAXMaximumRetailPriceChangeIfc[] inActiveMRPChanges = selectPriceChange
							.readEffectiveUniqueMaximumRetailPriceChanges(dataConnection, Calendar.getInstance(),
									pluItem.getStoreID(), pluItem.getItemID(), false, null);

					if (pluItem instanceof MAXPLUItem) {
						((MAXPLUItem) pluItem).setInActiveMaximumRetailPriceChanges(inActiveMRPChanges);
					} else if (pluItem instanceof MAXGiftCardPLUItem) {
						// ((MAXGiftCardPLUItem)
						// pluItem).setInActiveMaximumRetailPriceChanges(inActiveMRPChanges);
					}
					/* Rev changes ends */
					changes = selectPriceChange.readAllTemporaryPriceChanges(dataConnection, pluItem,
							Calendar.getInstance()); // this is not timezone
														// safe
					pluItem.setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(changes);
					changes = selectPriceChange.readClearancePriceChanges(dataConnection, pluItem,
							Calendar.getInstance()); // this not timezone
														// safe

					pluItem.setClearancePriceChangesAndClearancePriceChangesForReturns(changes);
				}

				// update gift card amounts
				if (pluItem instanceof GiftCardPLUItemIfc) {
					GiftCardPLUItemIfc gci = (GiftCardPLUItemIfc) pluItem;
					gci.getGiftCard().setReqestedAmount(pluItem.getSellingPrice());
				}

				if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.Planogram)) {
					applyPlanogramIDs(dataConnection, pluItem);
				}

				if (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_STOCK) {
					if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.StockItem)) {
						selectStockItem(dataConnection, pluItem.getItemID(), pluItem, sqlLocale);
					}
				}
				if (pluRequestor == null
						|| pluRequestor.containsRequestType(PLURequestor.RequestType.LocalizedDescription)) {
					applyLocaleDependentDescriptions(dataConnection, pluItem, sqlLocale);
				}
				if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.ItemImage)) {
					applyItemImages(dataConnection, pluItem, sqlLocale);
				}

				// this method is called for regular item search from the sale
				// screen
				applyManufacturer(dataConnection, pluItem, andQualifiers, sqlLocale);

				// Changes for Rev 1.1 : Starts
				// Changes for Rev 1.2 : Starts
				if (pluItem instanceof MAXPLUItem)
					((MAXPLUItemIfc) pluItem).setItemGroups(getItemGroups(pluItem, dataConnection));
				// Changes for Rev 1.1 : Ends

				if (pluRequestor == null
						|| pluRequestor.containsRequestType(PLURequestor.RequestType.AdvancedPricingRules)
								&& !(pluItem instanceof MAXGiftCardPLUItem))

				{
					applyAdvancedPricingRules(dataConnection, pluItem, sqlLocale);
				}
				// Changes for Rev 1.2 : Ends
				// and components to the ItemKit
				if (pluItem.isKitHeader()) {
					if (pluRequestor == null
							|| pluRequestor.containsRequestType(PLURequestor.RequestType.KitComponents)) {
						KitComponentIfc[] kitComps = selectKitComponents(dataConnection, pluItem.getItemID(),
								sqlLocale);
						((ItemKitIfc) pluItem).addComponentItems(kitComps);
					}
				}

				// 27192
				if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.POSDepartment)) {
					getDepartmentByDeptID(dataConnection, pluItem, sqlLocale);
				}
				SQLSelectStatement itemExclusionsql = buildSelectPLUItemExclusionListSQL(pluItem.getItemID(),
						"ExclusionList");
				try {
					dataConnection.execute(itemExclusionsql.getSQLString());
					ResultSet rs1 = (ResultSet) dataConnection.getResult();
					List exclusionGroupListMap = new ArrayList();
					int inc = 0;
					int index;
					for (; rs1.next(); exclusionGroupListMap
							.add(rs1.getString(++index) + "_" + rs1.getString(++index + 1)))
						index = 0;

					((MAXPLUItemIfc) pluItem).setItemExclusionGroupList(exclusionGroupListMap);
				} catch (Exception exception) {
				}
			}
		} catch (DataException de) {
			logger.warn(de.toString());
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "PLUItem lookup");
			throw new DataException(DataException.SQL_ERROR, "PLUItem lookup", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "PLUItem lookup", e);
		}

		// Changes for Rev 1.5 : Starts
//		if (list.size() == 0) {
//			throw new DataException(DataException.NO_DATA,
//					"No PLU was found processing the result set in JdbcPLUOperation.");
//		}
		// Changes for Rev 1.5 : Ends
		return list.toArray(new PLUItemIfc[list.size()]);
	}

	public PLUItemIfc[] selectPLUItems(JdbcDataConnection dataConnection, List<SQLParameterIfc> andQualifiers,
			List<SQLParameterIfc> orQualifiers, PLURequestor pluRequestor, LocaleRequestor locale)
			throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
		sql.addTable(TABLE_ITEM, ALIAS_ITEM);

		// add columns
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID);

		// add qualifiers
		sql.addJoinQualifier(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, ALIAS_ITEM, FIELD_ITEM_ID);

		// add AND qualifiers
		sql.addQualifiers(andQualifiers);

		// add OR qualifiers
		sql.addOrQualifiers(orQualifiers);

		// perform the query
		ArrayList<String> results = new ArrayList<String>();
		try {
			ResultSet rs = execute(dataConnection, sql);

			while (rs.next()) {
				int index = 0;
				String posItemID = getSafeString(rs, ++index);
				getSafeString(rs, ++index); // itemID
				results.add(posItemID);
			}
			rs.close();
		} catch (DataException de) {
			logger.warn(de.toString());
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "PLUItem lookup");
			throw new DataException(DataException.SQL_ERROR, "PLUItem lookup", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "PLUItem lookup", e);
		}

		if (results.isEmpty()) {
			throw new DataException(DataException.NO_DATA,
					"No PLU was found processing the result set in JdbcPLUOperation.");
		}

		// for each selected item id, read the PLUItem information
		ArrayList<PLUItemIfc> items = new ArrayList<PLUItemIfc>();
		Iterator<String> i = results.iterator();

		// make sure locale is there
		if (locale == null) {
			locale = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
		}

		while (i.hasNext()) {
			items.add(readPLUItem(dataConnection, i.next(), pluRequestor, locale)[0]);
		}

		// convert results to array and return
		MAXPLUItemIfc[] itemArray = new MAXPLUItemIfc[items.size()];
		items.toArray(itemArray);
		return (itemArray);
	}

	protected SQLSelectStatement buildSelectPLUItemSQL(List<SQLParameterIfc> andQualifiers,
			List<SQLParameterIfc> orQualifiers, PLURequestor pluRequestor, LocaleRequestor sqlLocale) {
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_POS_IDENTITY, ALIAS_POS_IDENTITY);
		// sql.addTable(TABLE_ITEM_IMAGE,ALIAS_IMAGE);

		// add columns
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_ID);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID);
		sql.addColumn(ALIAS_ITEM + "." + "ID_CTGY_TX");
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_QUANTITY_KEY_PROHIBIT_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_PROHIBIT_RETURN_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_MINIMUM_SALE_UNIT_COUNT);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_POS_ITEM_MAXIMUM_SALE_UNIT_COUNT);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ALLOW_COUPON_MULTIPLY_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ELECTRONIC_COUPON_FLAG);
		// sql.addColumn("POSID.FL_CPN_RST");
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_COUPON_RESTRICTED_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_PRICE_MODIFIABLE_FLAG);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_SPECIAL_ORDER_ELIGIBLE);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_ITEM_EMPLOYEE_DISCOUNT_ALLOWED_FLAG);
		sql.addColumn(ALIAS_RETAIL_STORE_ITEM + "." + FIELD_COMPARE_AT_SALE_UNIT_RETAIL_PRICE_AMOUNT);
		sql.addColumn(ALIAS_RETAIL_STORE_ITEM + "." + FIELD_SALE_AGE_RESTRICTION_ID);
		sql.addColumn("ITM.NM_BRN");
		sql.addColumn(ALIAS_ITEM + "." + FIELD_TAX_GROUP_ID);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_TAX_EXEMPT_CODE);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_SIZE_REQUIRED_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_POS_DEPARTMENT_ID);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_REGISTRY_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_MERCHANDISE_HIERARCHY_LEVEL_CODE);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_AUTHORIZED_FOR_SALE_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_KIT_SET_CODE);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_SUBSTITUTE_IDENTIFIED_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_MERCHANDISE_HIERARCHY_GROUP_ID);
		sql.addColumn("SUBSTR(CLASS1.ID_MRHRC_GP_PRNT,3,12)");
		sql.addColumn("SUBSTR(DEPT1.ID_MRHRC_GP_PRNT,3,12)");
		sql.addColumn("SUBSTR(GROUP1.ID_MRHRC_GP_PRNT,3,12)");
		sql.addColumn("SUBSTR(DIVISION1.ID_MRHRC_GP_PRNT,3,12)");
		// sql.addColumn(TABLE_ITEM_MANUFACTURER + "." + "NM_MF");
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_MANUFACTURER_UPC_ITEM_ID);
		sql.addColumn(ALIAS_POS_IDENTITY + "." + FIELD_RETAIL_STORE_ID);

		// sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_IMAGE_LOCATION);
		// sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_FULL_IMAGE_NAME);
		// sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_THUMB_BLOB);
		// sql.addColumn(ALIAS_IMAGE + "." + FIELD_ITEM_FULL_BLOB);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE0);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE1);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE2);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE3);

		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE4);
		// changes start for rev 1.0 : Food totals
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE5);
		// Changes ends for rev 1.0
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE7);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE8);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE9);
		/* Rev 1.4 changes starts */
		sql.addColumn(ALIAS_RETAIL_STORE_ITEM + "." + FIELD_MAXIMUM_RETAIL_PRICE);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_RETAIL_LESS_THAN_MRP_FLAG);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_MULTIPLE_MRP_FLAG);
		/* rev 1.4 changes ends */
		// add joins
		sql.addOuterJoinQualifier(" JOIN " + TABLE_RETAIL_STORE_ITEM + " " + ALIAS_RETAIL_STORE_ITEM + " ON "
				+ ALIAS_RETAIL_STORE_ITEM + "." + FIELD_ITEM_ID + " = " + ALIAS_POS_IDENTITY + "." + FIELD_ITEM_ID
				+ " AND " + ALIAS_RETAIL_STORE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_POS_IDENTITY + "."
				+ FIELD_RETAIL_STORE_ID);

		// join POS Identity and Item tables using the given qualifier
		sql.addOuterJoinQualifier("", /* JOIN */TABLE_ITEM + " " + ALIAS_ITEM, /* ON */ALIAS_ITEM, FIELD_ITEM_ID,
				/* = */ALIAS_POS_IDENTITY, FIELD_ITEM_ID);
		sql.addOuterJoinQualifier("", "ST_ASCTN_MRHRC CLASS1", "ITM", "ID_MRHRC_GP", "CLASS1", "ID_MRHRC_GP_CHLD");
		sql.addOuterJoinQualifier("", "ST_ASCTN_MRHRC DEPT1", "CLASS1", "ID_MRHRC_GP_PRNT", "DEPT1",
				"ID_MRHRC_GP_CHLD");
		sql.addOuterJoinQualifier("", "ST_ASCTN_MRHRC GROUP1", "DEPT1", "ID_MRHRC_GP_PRNT", "GROUP1",
				"ID_MRHRC_GP_CHLD");
		sql.addOuterJoinQualifier("", "ST_ASCTN_MRHRC DIVISION1", "GROUP1", "ID_MRHRC_GP_PRNT", "DIVISION1",
				"ID_MRHRC_GP_CHLD");
		// sql.addOuterJoinQualifier("", "PA_MF", "ITM", "ID_MF", "PA_MF",
		// "ID_MF");
		sql.addQualifiers(andQualifiers);
		sql.addOrQualifiers(orQualifiers);
		return sql;
	}

	protected PLUItemIfc createPLUItem(ResultSet rs) throws SQLException, DataException {
		// parse result set and create domain objects as necessary
		int index = 0;
		String posItemID = getSafeString(rs, ++index);
		String itemID = getSafeString(rs, ++index);
		int taxCategory = rs.getInt(++index);
		boolean disableQuantityKey = getBooleanFromString(rs, ++index);
		boolean prohibitReturn = getBooleanFromString(rs, ++index);
		BigDecimal minimumSaleQuantity = getBigDecimal(rs, ++index);
		BigDecimal maximumSaleQuantity = getBigDecimal(rs, ++index);
		boolean multipleCouponsAllowed = getBooleanFromString(rs, ++index);
		boolean priceEntryRequired = getBooleanFromString(rs, ++index);
		boolean electronicCouponAvailable = getBooleanFromString(rs, ++index);
		boolean couponRestricted = getBooleanFromString(rs, ++index);
		boolean priceModifiable = getBooleanFromString(rs, ++index);
		boolean specialOrderEligible = getBooleanFromString(rs, ++index);
		boolean employeeDiscountAllowed = getBooleanFromString(rs, ++index);
		CurrencyIfc compareAtPrice = getCurrencyFromDecimal(rs, ++index);
		int restrictedAge = rs.getInt(++index);
		String itemBrand = getSafeString(rs, ++index);
		int taxGroupID = rs.getInt(++index);
		boolean taxable = getBooleanFromString(rs, ++index);
		boolean sizeRequired = getBooleanFromString(rs, ++index);
		String deptID = getSafeString(rs, ++index);
		boolean discountable = getBooleanFromString(rs, ++index);
		boolean damageDiscountable = getBooleanFromString(rs, ++index);
		boolean registryEligible = getBooleanFromString(rs, ++index);
		String productGroupID = rs.getString(++index);
		boolean saleable = getBooleanFromString(rs, ++index);
		Float kitCodeF = new Float(rs.getFloat(++index));
		int kitCode = kitCodeF.intValue();
		boolean substituteAvailable = getBooleanFromString(rs, ++index);
		String itemType = rs.getString(++index);
		String merchandiseHierarchyGroupId = rs.getString(++index);
		String classid = rs.getString(++index);
		String deptid = rs.getString(++index);
		String groupid = rs.getString(++index);
		String divisionid = rs.getString(++index);
		// int manufacturerID = rs.getInt(++index);
		String manufacturerItemUPC = getSafeString(rs, ++index);
		String storeID = rs.getString(++index);
		// Changes for Rev 1.8 : Starts
		String brandName = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE4);
		// Changes for Rev 1.8 : Ends
		/* Rev 1.4 starts */
		String retailLessThanMRPFlag = null;
		retailLessThanMRPFlag = rs.getString(FIELD_RETAIL_LESS_THAN_MRP_FLAG);
		String multipleMRPFlag = null;
		multipleMRPFlag = rs.getString(FIELD_MULTIPLE_MRP_FLAG);
		/* Rev 1.4 ends */
		/* Rev 1.12 changes starts */
		String hsnNum = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6);
		/* Rev 1.12 changes ends */

		/* Rev 1.13 changes starts */
		// String liquom = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE7);
		// String liqcat = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE8);
		/* Rev 1.13 changes ends */
		PLUItemIfc pluItem = null;
		// create the appropriate PLUItemIfc type
		if (productGroupID != null && productGroupID.equals(PRODUCT_GROUP_GIFT_CARD)) {
			// create a GiftCardPLUItemIfc
			pluItem = instantiateGiftCardPLUItem(priceEntryRequired);
		}
		// Added for alterations functionality
		else if (productGroupID != null && productGroupID.equals(PRODUCT_GROUP_ALTERATION)) {
			pluItem = instantiateAlterationPLUItem();
		} else {
			switch (kitCode) {
			case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
				// create an ItemKitIfc
				pluItem = instantiateItemKit();
				break;
			case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
				// create an ItemKitIfc
				pluItem = instantiateKitComponent();
				break;
			default:
				pluItem = instantiatePLUItem();
			}
		}

		// initialize common attributes
		pluItem.setItemID(itemID);
		pluItem.setPosItemID(posItemID);
		((MAXPLUItemIfc) pluItem).setTaxCategory(taxCategory);
		pluItem.setTaxable(taxable);
		pluItem.setTaxGroupID(taxGroupID);
		pluItem.setDepartmentID(deptID);
		pluItem.setItemSizeRequired(sizeRequired);
		// pluItem.setPlanogramID(planogramID);
		// pluItem.setManufacturerID(manufacturerID);
		pluItem.setManufacturerItemUPC(manufacturerItemUPC);
		pluItem.setStoreID(storeID);

		// pluItem.setliqcat(liqcat);
		// pluItem.setliquom(liquom);
		// if(pluItem instanceof MAXGiftCardPLUItemIfc){
		if (groupid != null)
			((MAXPLUItemIfc) pluItem).setItemGroup(getLtrimString(groupid));
		if (divisionid != null)
			((MAXPLUItemIfc) pluItem).setItemDivision(getLtrimString(divisionid));
		if (deptid != null)
			((MAXPLUItemIfc) pluItem).setItemDepartmentId(getLtrimString(deptid));
		if (classid != null)
			((MAXPLUItemIfc) pluItem).setItemClassId(getLtrimString(classid));
		if (merchandiseHierarchyGroupId != null)
			((MAXPLUItemIfc) pluItem).setSubClass(getLtrimString(merchandiseHierarchyGroupId));
		if (itemBrand != null)
			((MAXPLUItemIfc) pluItem).setBrandName(brandName);
		// }
		// Rev 1.4 starts
		if (pluItem instanceof MAXPLUItem) {

			((MAXPLUItem) pluItem)
					.setRetailLessThanMRPFlag(AbstractDBUtils.TRUE.equals(retailLessThanMRPFlag) ? true : false);
			((MAXPLUItem) pluItem)
					.setMultipleMaximumRetailPriceFlag(AbstractDBUtils.TRUE.equals(multipleMRPFlag) ? true : false);
			// Change for Rev 1.12 : Starts
			((MAXPLUItem) pluItem).setHsnNum(hsnNum);
			// Change for Rev 1.12 : Ends
			// ((MAXPLUItem) pluItem).setliquom(liquom);
			// ((MAXPLUItem) pluItem).setliqcat(liqcat);
		}

		// Rev 1.4 ends
		if (compareAtPrice == null) {
			compareAtPrice = DomainGateway.getBaseCurrencyInstance();
		}
		pluItem.setCompareAtPrice(compareAtPrice);
		pluItem.setRestrictiveAge(restrictedAge);

		ItemClassificationIfc ic = pluItem.getItemClassification();

		// set the remaining ItemClassification attributes for the PLUItem
		ic.setQuantityModifiable(!disableQuantityKey);
		ic.setReturnEligible(!prohibitReturn);
		ic.setPriceOverridable(priceModifiable);
		ic.setMinimumSaleQuantity(minimumSaleQuantity);
		ic.setMaximumSaleQuantity(maximumSaleQuantity);
		ic.setMultipleCouponEligible(multipleCouponsAllowed);
		ic.setPriceEntryRequired(priceEntryRequired);
		ic.setElectronicCouponAvailable(electronicCouponAvailable);
		ic.setCouponRestricted(couponRestricted);
		ic.setDiscountEligible(discountable);
		ic.setDamageDiscountEligible(damageDiscountable);
		ic.setRegistryEligible(registryEligible);
		ic.setAuthorizedForSale(saleable);
		ic.setItemKitSetCode(kitCode);
		ic.setSubstituteItemAvailable(substituteAvailable);
		ic.setSpecialOrderEligible(specialOrderEligible);
		ic.setEmployeeDiscountAllowedFlag(employeeDiscountAllowed);
		ic.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupId);

		String merchandiseCode = null;
		// Changes for rev 1.0 : start(Food totals
		ArrayList<MerchandiseClassificationIfc> classificationList = new ArrayList<MerchandiseClassificationIfc>();
		MerchandiseClassificationIfc merchandiseClassification = null;
		for (int i = 0; i < 10; i++) {
			merchandiseCode = getSafeString(rs, ++index);
			if (!Util.isEmpty(merchandiseCode)) {
				merchandiseClassification = DomainGateway.getFactory().getMerchandiseClassificationInstance();
				merchandiseClassification.setIdentifier(merchandiseCode);
				classificationList.add(merchandiseClassification);
			} else {
				merchandiseClassification = DomainGateway.getFactory().getMerchandiseClassificationInstance();
				merchandiseClassification.setIdentifier("");
				classificationList.add(merchandiseClassification);
			}
		}

		ic.setMerchandiseClassifications(classificationList);

		// Changes for rev 1.0 : end(Food totals

		if (itemType.equals("SRVC"))
			ic.setItemType(2);
		else if (itemType.equals("STCK"))
			ic.setItemType(1);
		else if (itemType.equals("SCPN"))
			ic.setItemType(3);

		// create product group and set ID if necessary
		ProductGroupIfc pg = DomainGateway.getFactory().getProductGroupInstance();
		if (productGroupID != null) {
			pg.setGroupID(productGroupID);
			pg.setDescription(itemBrand);
		}
		ic.setGroup(pg);

		// ic.setExternalSystemCreateUIN(true); //TODO
		// ic.setSerialEntryTime("StoreReceiving"); //TODO
		return pluItem;
	}

	protected AdvancedPricingRuleIfc[] selectGroupDiscounts(JdbcDataConnection dataConnection, PLUItemIfc pluItem,
			LocaleRequestor sqlLocale) throws DataException {

		ArrayList<AdvancedPricingRuleIfc> list = new ArrayList<AdvancedPricingRuleIfc>();
		ResultSet rs = null;
		SQLSelectStatement sql = new SQLSelectStatement();
		try {

			sql = buildGroupSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID, pluItem);
			sql.getSQLString();
			rs = execute(dataConnection, sql);
			// do a lookup for all the items that belong to the same rule
			Collection<AdvancedPricingRuleIfc> itemComparisonRules = readGroupAdvancedPricingRules(rs,
					DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID);
			for (AdvancedPricingRuleIfc advancedPricingRuleIfc : itemComparisonRules) {
				((MAXAdvancedPricingRuleIfc) advancedPricingRuleIfc).getSourceThreshold();
			}

			for (AdvancedPricingRuleIfc rule : itemComparisonRules) {
				getLocalizedRuleData(dataConnection, rule, sqlLocale);
				// ExcludedItemsRead
				rule = selectExcludedSourceItems(dataConnection, rule);
				// Thresholds
				rule = selectThresholds(dataConnection, rule);
				if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
					sql = buildItemSQL(Integer.parseInt(rule.getRuleID()));
					rs = execute(dataConnection, sql);
					rule = readSources(rs, rule);
				} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
						.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
					sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
							DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID);
					rs = execute(dataConnection, sql);
					rule = readSourceAmount(rs, rule);
				}
			}
			list.addAll(itemComparisonRules);

			String merchClassCodes = pluItem.getItemClassification().getMerchandiseHierarchyGroup();
			if (merchClassCodes != null && !"''".equals(merchClassCodes)) {
				sql = buildGroupSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS, pluItem);
				rs = execute(dataConnection, sql);
				Collection<AdvancedPricingRuleIfc> classComparisonRules = readGroupAdvancedPricingRules(rs,
						DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS);
				for (AdvancedPricingRuleIfc rule : classComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildClassSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
							.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
				list.addAll(classComparisonRules);
			}

			if (pluItem.getDepartmentID() != null) {
				sql = buildGroupSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID, pluItem);
				rs = execute(dataConnection, sql);
				Collection<AdvancedPricingRuleIfc> depComparisonRules = readGroupAdvancedPricingRules(rs,
						DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID);
				for (AdvancedPricingRuleIfc rule : depComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildDepartmentSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
							.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
				list.addAll(depComparisonRules);
			}

			String subClass = pluItem.getItemClassification().getMerchandiseHierarchyGroup();
			if (subClass != null) {
				sql = buildGroupSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS, pluItem);
				rs = execute(dataConnection, sql);
				Collection<AdvancedPricingRuleIfc> classComparisonRules = readGroupAdvancedPricingRules(rs,
						MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS);
				for (AdvancedPricingRuleIfc rule : classComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildDepartmentSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
							.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
				list.addAll(classComparisonRules);
			}

			if (((MAXPLUItemIfc) pluItem).getItemGroups() != null
					&& !((MAXPLUItemIfc) pluItem).getItemGroups().equalsIgnoreCase("'NO'")) {
				// Changes for Rev 1.1 : Starts
				sql = buildGroupSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP, pluItem);
				rs = execute(dataConnection, sql);
				Collection<AdvancedPricingRuleIfc> itemGroupComparisonRules = readGroupAdvancedPricingRules(rs,
						MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP);
				// Changes for Rev 1.1 : Ends
				for (AdvancedPricingRuleIfc rule : itemGroupComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildItemGroupSQL(Integer.parseInt(rule.getRuleID()));
						sql.getSQLString();
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
							.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
							|| Integer.parseInt(rule.getReason()
									.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
				list.addAll(itemGroupComparisonRules);
			}
			logger.debug("AFTER ITEM GROUP : GROUP DISCOUNT)");

			sql = buildGroupSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND, pluItem);
			rs = execute(dataConnection, sql);
			Collection<AdvancedPricingRuleIfc> brandComparisonRules = readGroupAdvancedPricingRules(rs,
					MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND);
			for (AdvancedPricingRuleIfc rule : brandComparisonRules) {
				getLocalizedRuleData(dataConnection, rule, sqlLocale);
				if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
					sql = buildBrandSQL(Integer.parseInt(rule.getRuleID()));
					sql.getSQLString();
					rs = execute(dataConnection, sql);
					rule = readSources(rs, rule);
				} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT && (Integer
						.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster
						|| Integer.parseInt(rule.getReason()
								.getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster)) {
					sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
							MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND);
					rs = execute(dataConnection, sql);
					rule = readSourceAmount(rs, rule);
				}
			}
			list.addAll(brandComparisonRules);

		} catch (SQLException se) {
			dataConnection.logSQLException(se, "AdvancedPricing lookup");
			throw new DataException(DataException.SQL_ERROR, "Advanced Pricing lookup", se);
		} finally {
			if (rs != null) {
				try {
					rs.close();

				} catch (SQLException se)

				{
					dataConnection.logSQLException(se, "Advanced Pricing lookup -- Could not close result handle");

				}

			}

		}
		AdvancedPricingRuleIfc[] rules = new AdvancedPricingRuleIfc[list.size()];
		list.toArray(rules);
		return rules;
	}

	public void applyAdvancedPricingRules(JdbcDataConnection dataConnection, PLUItemIfc pluItem,
			LocaleRequestor sqlLocale) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcPLUOperation.applyAdvancedPricingRules() begins.");
		AdvancedPricingRuleIfc[] rules = selectGroupDiscounts(dataConnection, pluItem, sqlLocale);
		pluItem.addAdvancedPricingRules(rules);
		// Changes for Rev 1.1 : Starts
		ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts = selectInvoiceDiscounts(dataConnection, pluItem);
		if (!(pluItem instanceof GiftCardPLUItemIfc)) {
			((MAXPLUItemIfc) pluItem).setInvoiceDiscounts(invoiceDiscounts);
		}
		// Changes for Rev 1.1 : Ends
		// organize existing rules for next deal discount.
		Hashtable<String, AdvancedPricingRuleIfc> ruleMap = new Hashtable<String, AdvancedPricingRuleIfc>(1);
		for (int i = 0; i < rules.length; i++) {
			ruleMap.put(rules[i].getRuleID(), rules[i]);
		}

		pluItem.addAdvancedPricingRules(selectDealDiscounts(dataConnection, pluItem, ruleMap, sqlLocale));

		pluItem.addAdvancedPricingRules(selectStoreLevelDiscounts(dataConnection, pluItem, ruleMap, sqlLocale));

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcPLUOperation.applyAdvancedPricingRules() ends");
	}

	protected AdvancedPricingRuleIfc[] selectDealDiscounts(JdbcDataConnection dataConnection, PLUItemIfc pluItem,
			Hashtable<String, AdvancedPricingRuleIfc> existingRules, LocaleRequestor sqlLocale) throws DataException {
		ResultSet rs = null;
		SQLSelectStatement sql = new SQLSelectStatement();
		try {

			sql = buildDealSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID, pluItem);
			rs = execute(dataConnection, sql);
			List<AdvancedPricingRuleIfc> itemComparisonRules = readDealAdvancedPricingRules(rs,
					DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID, existingRules);
			for (AdvancedPricingRuleIfc rule : itemComparisonRules) {
				getLocalizedRuleData(dataConnection, rule, sqlLocale);
				// ExcludedItemsRead
				rule = selectExcludedSourceItems(dataConnection, rule);
				// Thresholds
				rule = selectThresholds(dataConnection, rule);
				if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
					sql = buildItemSQL(Integer.parseInt(rule.getRuleID()));
					rs = execute(dataConnection, sql);
					rule = readSources(rs, rule);
				} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
					if (Integer.parseInt(rule.getReason().getCode()) != 23
							&& Integer.parseInt(rule.getReason().getCode()) != 24)
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID);
					else
						sql = MAXUtils.buildSqlForBuyRsNOrMoreOfXGetYatZpctOffTieredWithoutMM(
								Integer.parseInt(rule.getRuleID()));
					rs = execute(dataConnection, sql);
					rule = readSourceAmount(rs, rule);
				}
			}

			String merchClassCodes = pluItem.getItemClassification().getMerchandiseHierarchyGroup();
			if (merchClassCodes != null && !"''".equals(merchClassCodes)) {
				sql = buildDealSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS, pluItem);
				rs = execute(dataConnection, sql);
				List<AdvancedPricingRuleIfc> classComparisonRules = readDealAdvancedPricingRules(rs,
						DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS, existingRules);
				for (AdvancedPricingRuleIfc rule : classComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildClassSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS);
						sql.getSQLString();
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
			}

			// department type rules
			if (pluItem.getDepartmentID() != null) {
				sql = buildDealSQL(DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID, pluItem);
				rs = execute(dataConnection, sql);
				List<AdvancedPricingRuleIfc> subClassComparisonRules = readDealAdvancedPricingRules(rs,
						DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID, existingRules);
				for (AdvancedPricingRuleIfc rule : subClassComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildDepartmentSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
			}

			if (merchClassCodes != null) {
				sql = buildDealSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS, pluItem);
				rs = execute(dataConnection, sql);
				List<AdvancedPricingRuleIfc> classComparisonRules = readDealAdvancedPricingRules(rs,
						MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS, existingRules);
				for (AdvancedPricingRuleIfc rule : classComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildDepartmentSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
						sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
								MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_SUBCLASS);
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
			}

			if (((MAXPLUItemIfc) pluItem).getItemGroups() != null
					&& !((MAXPLUItemIfc) pluItem).getItemGroups().equalsIgnoreCase("'NO'")) {
				sql = buildDealSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP, pluItem);
				rs = execute(dataConnection, sql);
				List<AdvancedPricingRuleIfc> itemGroupComparisonRules = readDealAdvancedPricingRules(rs,
						MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP, existingRules);
				for (AdvancedPricingRuleIfc rule : itemGroupComparisonRules) {
					getLocalizedRuleData(dataConnection, rule, sqlLocale);
					// ExcludedItemsRead
					rule = selectExcludedSourceItems(dataConnection, rule);
					// Thresholds
					rule = selectThresholds(dataConnection, rule);
					if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
						sql = buildItemGroupSQL(Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSources(rs, rule);
					} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
						if (Integer.parseInt(rule.getReason().getCode()) != 23
								&& Integer.parseInt(rule.getReason().getCode()) != 24)
							sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
									MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP);
						else
							sql = MAXUtils.buildSqlForBuyRsNOrMoreOfXGetYatZpctOffTieredWithoutMM(
									Integer.parseInt(rule.getRuleID()));
						rs = execute(dataConnection, sql);
						rule = readSourceAmount(rs, rule);
					}
				}
			}

			sql = buildDealSQL(MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND, pluItem);
			rs = execute(dataConnection, sql);
			List<AdvancedPricingRuleIfc> brandComparisonRules = readDealAdvancedPricingRules(rs,
					MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND, existingRules);
			for (AdvancedPricingRuleIfc rule : brandComparisonRules) {
				getLocalizedRuleData(dataConnection, rule, sqlLocale);
				// ExcludedItemsRead
				rule = selectExcludedSourceItems(dataConnection, rule);
				// Thresholds
				rule = selectThresholds(dataConnection, rule);
				if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY) {
					sql = buildBrandSQL(Integer.parseInt(rule.getRuleID()));
					rs = execute(dataConnection, sql);
					rule = readSources(rs, rule);
				} else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT) {
					sql = MAXUtils.buildSourceSqlForAmount(Integer.parseInt(rule.getRuleID()),
							MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_BRAND);
					rs = execute(dataConnection, sql);
					rule = readSourceAmount(rs, rule);
				}
			}
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "AdvancedPricing lookup");
			throw new DataException(DataException.SQL_ERROR, "Advanced Pricing lookup", se);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					dataConnection.logSQLException(se, "Advanced Pricing lookup -- Could not close result handle");
				}
			}

		}

		return existingRules.values().toArray(new AdvancedPricingRuleIfc[existingRules.size()]);
	}

	protected ArrayList<AdvancedPricingRuleIfc> readGroupAdvancedPricingRules(ResultSet rs, int comparisonBasis)
			throws SQLException

	{
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readGroupAdvancedPricingRules() begins.");
		ArrayList<AdvancedPricingRuleIfc> discountsArray = new ArrayList<AdvancedPricingRuleIfc>();
		while (rs.next()) {
			AdvancedPricingRuleIfc rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();
			int index = 0;
			rule.setRuleID(JdbcDataOperation.getSafeString(rs, ++index));
			rule.setStoreID(JdbcDataOperation.getSafeString(rs, ++index));

			index = readBasicAdvancedPricingRules(rs, rule);

			if (comparisonBasis > -1) {
				// threshold for amount
				CurrencyIfc thrAmount = JdbcDataOperation.getCurrencyFromDecimal(rs, ++index);

				// get around decimal index because Postgres' getInt doesn't
				// handle decimals indexes as integers.
				// int sourceQuantity = rs.getInt(++index); //original code
				Float sourceQuantityF = new Float(rs.getFloat(++index));
				BigDecimal roundfinalQunatity = new BigDecimal(0.0D);
				// Changes for Rev 1.1 : Starts
				try {
					roundfinalQunatity = (new BigDecimal(sourceQuantityF.doubleValue())).setScale(3,
							BigDecimal.ROUND_HALF_UP);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String entry = JdbcDataOperation.getSafeString(rs, ++index);

				// if(rule.get)
				// ((MAXAdvancedPricingRuleIfc)rule).setTargetItemId(entry);
				// ((MAXAdvancedPricingRuleIfc)rule).getTargetItemId();
				switch (rule.getThresholdTypeCode()) {
				case DiscountRuleConstantsIfc.THRESHOLD_QUANTITY:
					rule.getSourceList().addEntry(entry, roundfinalQunatity.intValue());
					break;

				case DiscountRuleConstantsIfc.THRESHOLD_AMOUNT:
					rule.getSourceList().addEntry(entry, thrAmount);
					break;
				default:
					logger.warn("Invalid thresholdTypeCode read in JDBCPLUOperation.");
					break;
				}
				// Changes for Rev 1.1 : Ends

				EYSDate date = JdbcDataOperation.dateToEYSDate(rs, ++index);
				rule.setEffectiveDate(date);
				rule.setEffectiveTime(new EYSTime(date));

				date = JdbcDataOperation.dateToEYSDate(rs, ++index);
				rule.setExpirationDate(date);
				rule.setExpirationTime(new EYSTime(date));
				// checking the null validation of pricingGroupID and inserting
				// in to AdvancePricingRuleIfc object if it is not null
				String pricingGroup = JdbcDataOperation.getSafeString(rs, ++index);
				if (!"".equals(pricingGroup)) {
					rule.setPricingGroupID(Integer.parseInt(pricingGroup));

				} // end if
				String mValue = null;
				try {
					mValue = JdbcDataOperation.getSafeString(rs, ++index);
				} catch (Exception exception) {
				}
				if (mValue != null && (rule instanceof MAXAdvancedPricingRuleIfc))
					((MAXAdvancedPricingRuleIfc) rule).setmValue(Integer.parseInt(mValue));
			}

			rule.setSourceComparisonBasis(comparisonBasis);
			rule.setSourcesAreTargets(true);
			if (rule.getReasonCode() != 25 && rule.getReasonCode() != 24 && rule.getReasonCode() != 33)
				discountsArray.add(rule);
		}
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readGroupAdvancedPricingRules() ends.");
		return discountsArray;
	}

	protected ArrayList<AdvancedPricingRuleIfc> readDealAdvancedPricingRules(ResultSet rs, int comparisonBasis,
			Hashtable<String, AdvancedPricingRuleIfc> existingRules) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readDealAdvancedPricingRules() begins.");
		ArrayList<AdvancedPricingRuleIfc> discountsArray = new ArrayList<AdvancedPricingRuleIfc>();
		List<String> ruleIDArray = new ArrayList<String>();
		while (rs.next()) {
			int index = 0;
			String ruleID = JdbcDataOperation.getSafeString(rs, ++index);
			String storeID = JdbcDataOperation.getSafeString(rs, ++index);

			AdvancedPricingRuleIfc rule = existingRules.get(ruleID);
			if (rule == null) {
				rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();
				rule.setRuleID(ruleID);
				rule.setStoreID(storeID);
				existingRules.put(ruleID, rule);

			}
			if (!ruleIDArray.contains(ruleID)) {
				index = readBasicAdvancedPricingRules(rs, rule);

				if (comparisonBasis > -1) {
					// threshold for amount
					CurrencyIfc thrAmount = JdbcDataOperation.getCurrencyFromDecimal(rs, ++index);

					// get around decimal index because Postgres' getInt doesn't
					// handle decimals indexes as integers.
					// int sourceQuantity = rs.getInt(++index);
					Float sourceQuantityF = new Float(rs.getFloat(++index));
					int sourceQuantity = sourceQuantityF.intValue();
					String sourceEntry = JdbcDataOperation.getSafeString(rs, ++index);

					switch (rule.getThresholdTypeCode()) {
					case DiscountRuleConstantsIfc.THRESHOLD_QUANTITY:
						rule.getSourceList().addEntry(sourceEntry, sourceQuantity);
						break;
					case DiscountRuleConstantsIfc.THRESHOLD_AMOUNT:
						rule.getSourceList().addEntry(sourceEntry, thrAmount);
						break;
					default:
						logger.warn("Invalid thresholdTypeCode read in JDBCPLUOperation.");
						break;
					}
				}

				EYSDate date = JdbcDataOperation.dateToEYSDate(rs, ++index);
				rule.setEffectiveDate(date);
				rule.setEffectiveTime(new EYSTime(date));

				date = JdbcDataOperation.dateToEYSDate(rs, ++index);
				rule.setExpirationDate(date);
				rule.setExpirationTime(new EYSTime(date));

				rule.setPricingGroupID(rs.getInt(++index));

				// get around decimal index because Postgres' getInt doesn't
				// handle decimals indexes as integers.
				// int targetQuantity = rs.getInt(++index);
				Float targetQuantityF = new Float(rs.getFloat(++index));
				int targetQuantity = targetQuantityF.intValue();
				String targetEntry = JdbcDataOperation.getSafeString(rs, ++index);

				Float targetCompBasisF = new Float(rs.getFloat(++index));
				int targetCompBasis = targetCompBasisF.intValue();
				rule.setTargetComparisonBasis(targetCompBasis);

				rule.setSourceComparisonBasis(comparisonBasis);
				rule.getTargetList().addEntry(targetEntry, targetQuantity);
				rule.setSourcesAreTargets(false);
				ruleIDArray.add(ruleID);
			} else {
				// set the column number for target cquantity
				// it varies based on whether there is comparisonBasis.
				if (comparisonBasis > -1) {
					index = 34;
				} else {
					index = 31;
				}
				// Changes for Rev 1.10 : Starts
				Float targetQuantityF = new Float(rs.getFloat(FIELD_MIX_AND_MATCH_LIMIT_COUNT));
				int targetQuantity = targetQuantityF.intValue();
				String targetEntry = rs.getString(FIELD_PROMOTIONAL_PRODUCT_ID);
				rule.getTargetList().addEntry(targetEntry, targetQuantity);
				// Changes for Rev 1.10 : Ends
			}
		}
		discountsArray.addAll(existingRules.values());
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readDealAdvancedPricingRules() ends.");
		return discountsArray;
	}

	protected String getPOSItemID(String itemID) {
		return ("'" + itemID + "'");
	}

	// Changes for Rev 1.3 : Starts
	public void updateItemProperties(ArrayList<PLUItemIfc> list, JdbcDataConnection dataConnection) {
		updateItemUOM(list, dataConnection);
		updateItemSize(list, dataConnection);
	}

	public void updateItemSize(ArrayList<PLUItemIfc> list, JdbcDataConnection dataConnection) {
		SQLSelectStatement sql = new SQLSelectStatement();
		ResultSet rs = null;
		Object[] pluItemArray = list.toArray();

		for (Object pluItemObject : pluItemArray) {
			PLUItemIfc pluItem = (PLUItemIfc) pluItemObject;
			sql.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);
			sql.addTable(TABLE_SIZE, ALIAS_SIZE);
			sql.addColumn(ALIAS_SIZE, FIELD_ACTUAL_SIZE_TYPE_DESCRIPTION);
			sql.addJoinQualifier(ALIAS_SIZE + "." + FIELD_SIZE_CODE, ALIAS_STOCK_ITEM + "." + FIELD_SIZE_CODE);
			sql.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_ITEM_ID, makeSafeString(pluItem.getItemID()));
			try {
				sql.getSQLString();
				rs = execute(dataConnection, sql);
				if (rs.next()) {
					if (pluItem instanceof MAXPLUItem)
						((MAXPLUItem) pluItem).setItemSizeDesc(rs.getString(FIELD_ACTUAL_SIZE_TYPE_DESCRIPTION));
				}
			} catch (DataException | SQLException exception) {
				exception.printStackTrace();
			}
		}
	}

	public void updateItemUOM(ArrayList<PLUItemIfc> list, JdbcDataConnection dataConnection) {
		SQLSelectStatement sql = new SQLSelectStatement();
		ResultSet rs = null;
		Object[] pluItemArray = list.toArray();

		for (Object pluItemObject : pluItemArray) {
			UnitOfMeasureIfc uom = DomainGateway.getFactory().getUnitOfMeasureInstance();
			PLUItemIfc pluItem = null;
			if (pluItemObject instanceof MAXPLUItem) {
				pluItem = (MAXPLUItem) pluItemObject;
			} else if (pluItemObject instanceof MAXGiftCardPLUItem) {
				pluItem = (MAXGiftCardPLUItem) pluItemObject;
			} else if (pluItemObject instanceof ItemKit)
				pluItem = (ItemKit) pluItemObject;

			sql.addTable(TABLE_UNIT_OF_MEASURE, ALIAS_UNIT_OF_MEASURE);
			sql.addTable(TABLE_STOCK_ITEM, ALIAS_STOCK_ITEM);

			sql.addColumn(ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_NAME);
			sql.addJoinQualifier(ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE,
					ALIAS_STOCK_ITEM + "." + FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE);
			sql.addQualifier(ALIAS_STOCK_ITEM + "." + FIELD_ITEM_ID, makeSafeString(pluItem.getItemID()));
			try {
				rs = execute(dataConnection, sql);
				if (rs.next())
					uom.setName(rs.getString(FIELD_UNIT_OF_MEASURE_NAME));
			} catch (DataException | SQLException exception) {
				exception.printStackTrace();
			}
			pluItem.setUnitOfMeasure(uom);
		}
	}
	// Changes for Rev 1.3 : Ends

	// Changes for Rev 1.5 : Starts
	public PLUItemIfc[] readPLUItemByItemNumber(JdbcDataConnection dataConnection, String key, String storeID,
			boolean retrieveStoreCoupons, PLURequestor pluRequestor, LocaleRequestor sqlLocale) throws DataException {
		List orQualifiers = new ArrayList(1);
		orQualifiers.add(new SQLParameterValue("POSID", "ID_ITM", key));
		// orQualifiers.add(new SQLParameterValue("POSID", "ID_ITM_POS", key));

		List andQualifiers = new ArrayList(1);
		if (storeID != null) {
			andQualifiers.add(new SQLParameterValue("POSID", "ID_STR_RT", storeID));
		}

		PLUItemIfc[] items = selectPLUItem(dataConnection, andQualifiers, orQualifiers, pluRequestor, sqlLocale);

		if (items.length == 0) {
			List orQualifiersSecond = new ArrayList(1);
			orQualifiersSecond.add(new SQLParameterValue("POSID", "ID_ITM_POS", key));
			items = selectPLUItem(dataConnection, andQualifiers, orQualifiersSecond, pluRequestor, sqlLocale);
			if (items.length == 0) {
				throw new DataException(DataException.NO_DATA,
						"No PLU was found processing the result set in JdbcPLUOperation.");
			}
		}

		for (int i = items.length - 1; i >= 0; --i) {
			if (items[i].isStoreCoupon()) {
				JdbcSCLUOperation.configureStoreCouponRules(items[i]);
			}
		}
		return items;
	}
	// Changes for Rev 1.5 : Ends

	// Changes for Rev 1.6 : Starts
	public PLUItemIfc[] readPLUItemByDescSearch(JdbcDataConnection dataConnection, String key, String storeID,
			boolean retrieveStoreCoupons, PLURequestor pluRequestor, LocaleRequestor sqlLocale) throws DataException {
		return readPLUItemByPosItemIDByDescSearch(dataConnection, key, storeID, retrieveStoreCoupons, pluRequestor,
				sqlLocale);
	}

	public PLUItemIfc[] readPLUItemByPosItemIDByDescSearch(JdbcDataConnection dataConnection, String key,
			String storeID, boolean retrieveStoreCoupons, PLURequestor pluRequestor, LocaleRequestor sqlLocale)
			throws DataException {
		SQLParameterValue sqlParamVal = new SQLParameterValue("ITM", "ID_ITM", key);
		return readPLUItemByKey(dataConnection, sqlParamVal, storeID, retrieveStoreCoupons, pluRequestor, sqlLocale);
	}
	// Changes for Rev 1.6 : Ends

	// added for rev 1.11 start
	protected int readBasicAdvancedPricingRules(ResultSet rs, AdvancedPricingRuleIfc rule) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readBasicAdvancedPricingRules() begins.");

		int index = 2;
		String appliedWhen = null;
		String status = null;
		String inBestDeal = null;

		appliedWhen = getSafeString(rs, ++index);
		status = getSafeString(rs, ++index);
		inBestDeal = getSafeString(rs, ++index);

		rule.setDescription(getSafeString(rs, ++index));

		Float reasonCodeF = new Float(rs.getFloat(++index));
		int reasonCode = reasonCodeF.intValue();
		String reasonCodeString = CodeConstantsIfc.CODE_UNDEFINED;
		try {
			reasonCodeString = Integer.toString(reasonCode);
		} catch (Exception e) {
			// do nothing, code already initialized to UNDEFINED
		}
		LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
		reason.setCode(reasonCodeString);
		rule.setReason(reason);
		Float discountScopeF = new Float(rs.getFloat(++index));
		int discountScope = discountScopeF.intValue();
		rule.setDiscountScope(discountScope);
		Float discountMethodF = new Float(rs.getFloat(++index));
		int discountMethod = discountMethodF.intValue();
		rule.setDiscountMethod(discountMethod);
		Float argumentBasisF = new Float(rs.getFloat(++index));
		int argumentBasis = argumentBasisF.intValue();
		rule.setAssignmentBasis(argumentBasis);

		Float applicationLimitF = new Float(rs.getFloat(++index));
		int applicationLimit = applicationLimitF.intValue();
		rule.setApplicationLimit(applicationLimit);
		// source Item Price Category
		rule.setSourceItemPriceCategory(getSafeString(rs, ++index));
		// Target Item Price Category
		rule.setTargetItemPriceCategory(getSafeString(rs, ++index));
		rule.setSourceThreshold(getCurrencyFromDecimal(rs, ++index));
		rule.setSourceLimit(getCurrencyFromDecimal(rs, ++index));
		rule.setTargetThreshold(getCurrencyFromDecimal(rs, ++index));
		rule.setTargetLimit(getCurrencyFromDecimal(rs, ++index));
		rule.setSourceAnyQuantity(rs.getInt(++index));
		rule.setTargetAnyQuantity(rs.getInt(++index));
		Float thresholdTypeF = new Float(rs.getFloat(++index));
		int thresholdType = thresholdTypeF.intValue();
		rule.setThresholdTypeCode(thresholdType);
		rule.setAccountingMethod(rs.getInt(++index));
		boolean flag = getBooleanFromString(rs, ++index);
		rule.setDealDistribution(flag);
		flag = getBooleanFromString(rs, ++index);
		rule.setAllowRepeatingSources(flag);
		rule.setCalcDiscOnItemType(rs.getInt(++index));

		// Add the Promotion Fields here
		try {
			rule.setPromotionId(rs.getInt(++index));
		} catch (Exception e) {
			rule.setPromotionId(0);
		}
		rule.setPromotionComponentId(rs.getInt(++index));
		rule.setPromotionComponentDetailId(rs.getInt(++index));

		// changes for rev 1.11 start
		String localizedName = JdbcDataOperation.getSafeString(rs, ++index);
		Locale lcl = LocaleMap.getLocale(LocaleMap.DEFAULT);
		rule.setName(lcl, localizedName);
		// changes for rev 1.11 end

		CurrencyIfc discountAmount = getCurrencyFromDecimal(rs, ++index);

		rule.setDiscountRate(getPercentage(rs, ++index));

		// identify if its a FixedPrice Discount
		CurrencyIfc fixedPrice = getCurrencyFromDecimal(rs, ++index);
		if (fixedPrice.signum() != 0) {
			rule.setFixedPrice(fixedPrice);
		} else {
			rule.setDiscountAmount(discountAmount);
		}

		// translate values
		setDiscountRuleValues(rule, appliedWhen, status, inBestDeal);

		if (logger.isDebugEnabled())
			logger.debug("JdbcPLUOperation.readBasicAdvancedPricingRules() ends.");
		return index;
	}

	private ArrayList<String> readTargetItemsList(ResultSet rs, AdvancedPricingRuleIfc rule,
			JdbcDataConnection dataConnection) {

		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setTable(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_PRICE_DERIVATION_RULE_ELIGIBILITY);
		sql.addColumn(FIELD_ITEM_IMAGE_ID);
		// sql.addColumn(FIELD_PRICE_DERIVATION_SUMMARY_RULE_ID);
		sql.addQualifier(new SQLParameterValue(ALIAS_PRICE_DERIVATION_RULE_ELIGIBILITY + "."
				+ FIELD_PRICE_DERIVATION_SUMMARY_RULE_ID + "=" + rule.getRuleID()));
		ArrayList<String> itemList = new ArrayList<String>();

		try {
			rs = execute(dataConnection, sql);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			int i = 0;
			while (rs.next()) {
				
				itemList.add(i, rs.getString(FIELD_ITEM_IMAGE_ID));
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return itemList;
		// TODO Auto-generated method stub

	}

	public PLUItemIfc[] getPluItemsByItemNumber(JdbcDataConnection connection, SearchCriteriaIfc inquiry)
			throws DataException {
		PLUItemIfc[] items;
		LocaleRequestor localeRequestor = inquiry.getLocaleRequestor();
		String key = null;
		// Rev 1.2 Changes starts
		if (inquiry.getItemNumber() != null && !inquiry.getItemNumber().equals(""))
			key = inquiry.getItemNumber();
		else if (inquiry.getPosItemID() != null && !inquiry.getPosItemID().equals(""))
			key = inquiry.getPosItemID();
		// Rev 1.2 Changes Ends
		if (key == null) {
			key = inquiry.getItemID();
		}
		String storeID = inquiry.getStoreNumber();
		String geoCode = inquiry.getGeoCode();
		PLURequestor pluRequestor = inquiry.getPLURequestor();

		items = readPLUItemByItemNumber(connection, key, storeID, false, pluRequestor, localeRequestor);

		items = decoratePluItems(connection, items, storeID, geoCode, pluRequestor);
		return items;

	}

	// Change for Rev 1.12:Starts
	protected void assignTaxAssignments(JdbcDataConnection dataConnection, PLUItemIfc[] items, String fromRegion,
			String toRegion) throws DataException {
		for (int i = 0; i < items.length; i++) {
			// Get the Tax Assignments for each PLUItem
			getInterStateTaxAssignments(dataConnection, items[i], fromRegion, toRegion);
		}
	}

	protected void assignTaxAssignments(JdbcDataConnection dataConnection, AbstractTransactionLineItemIfc items,
			String fromRegion, String toRegion) throws DataException {
		// Get the Tax Assignments for each PLUItem
		if (items instanceof PLUItemIfc)
			getInterStateReCalculateTaxAssignments(dataConnection, items, fromRegion, toRegion);
	}

	public void getInterStateReCalculateTaxAssignments(JdbcDataConnection dataConnection,
			AbstractTransactionLineItemIfc item, String fromRegion, String toRegion) throws DataException {

		ResultSet resultSet = null;
		ArrayList taxAssignmentList = new ArrayList();

		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql.addTable(TABLE_TAX_ASSIGNMENT, ALIAS_TAX_ASSIGNMENT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_ID_CTGY_TX);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD_DSCR);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_RT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_FCT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TXBL_FCT);

			if (item instanceof MAXPLUItem) {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = "
						+ ((MAXPLUItemIfc) item).getTaxCategory());

			} else if (item instanceof MAXGiftCardPLUItem) {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = "
						+ ((MAXGiftCardPLUItem) item).getTaxCategory());
			}
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_STORE_ID + " = " + "'" + 7302 + "'");
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_DIFF + " = " + "'I'");
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_TO_REGION + " in "
					+ "(select gst_reg_code from ls_gst_reg_map where upper(gst_reg_desc)=upper('" + toRegion + "'))");
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_FROM_REGION + " in "
					+ "(select gst_reg_code from ls_gst_reg_map where upper(gst_reg_desc)=upper('" + fromRegion
					+ "'))");

			// If Item has TaxCategory as -1, it is not a valid Tax Category
			dataConnection.execute(sql.getSQLString());
			resultSet = (ResultSet) dataConnection.getResult();

			if (resultSet != null) {
				MAXTaxAssignment taxAssignment = null;
				while (resultSet.next()) {
					taxAssignment = new MAXTaxAssignment();

					int taxCtgy = resultSet.getInt(FIELD_ID_CTGY_TX);
					String taxCode = resultSet.getString(FIELD_TX_CD);

					String taxCodeDesc = resultSet.getString(FIELD_TX_CD_DSCR);
					BigDecimal taxRate = resultSet.getBigDecimal(FIELD_TX_RT);
					BigDecimal taxAmtFactor = resultSet.getBigDecimal(FIELD_TX_FCT);
					BigDecimal taxableAmtFactor = resultSet.getBigDecimal(FIELD_TXBL_FCT);

					taxAssignment.setTaxCode(taxCode);
					taxAssignment.setTaxCodeDescription(taxCodeDesc);
					taxAssignment.setTaxRate(taxRate);
					taxAssignment.setTaxableAmountFactor(taxableAmtFactor);
					taxAssignment.setTaxAmountFactor(taxAmtFactor);
					taxAssignment.setTaxCategory(taxCtgy);
					taxAssignmentList.add(taxAssignment);

				}
			}

			if (item instanceof MAXPLUItemIfc) {
				((MAXPLUItemIfc) item).addTaxAssignments(
						(MAXTaxAssignment[]) taxAssignmentList.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
			} else if (item instanceof MAXGiftCardPLUItem) {
				((MAXGiftCardPLUItem) item).addTaxAssignments(
						(MAXTaxAssignment[]) taxAssignmentList.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
			}
		}

		catch (SQLException sqlException) {
			dataConnection.logSQLException(sqlException, " Tax Assignment lookup -- Error Retrieving Tax Assignments");
			throw new DataException(DataException.SQL_ERROR, "Tax Assignment lookup", sqlException);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException se) {
					dataConnection.logSQLException(se, "Tax Assignment lookup -- Could not close result handle");
				}
			}
		}

	}

	public void getInterStateTaxAssignments(JdbcDataConnection dataConnection, PLUItemIfc item, String fromRegion,
			String toRegion) throws DataException {

		ResultSet resultSet = null;
		ArrayList taxAssignmentList = new ArrayList();

		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql.addTable(TABLE_TAX_ASSIGNMENT, ALIAS_TAX_ASSIGNMENT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_ID_CTGY_TX);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD_DSCR);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_RT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_FCT);
			sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TXBL_FCT);

			if (item instanceof MAXPLUItem) {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = "
						+ ((MAXPLUItemIfc) item).getTaxCategory());

			} else if (item instanceof MAXGiftCardPLUItem) {
				sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = "
						+ ((MAXGiftCardPLUItem) item).getTaxCategory());
			}

			/*
			 * sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." +
			 * MAXARTSDatabaseIfc.FIELD_STORE_ID + " = " + "'" + item.getStoreID() + "'");
			 */
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_DIFF + " = " + "'I'");
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_TO_REGION + " in "
					+ "(select gst_reg_code from max_gst_reg_map where upper(gst_reg_desc)=upper('" + toRegion + "'))");
			sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_TAX_FROM_REGION + " in "
					+ "(select gst_reg_code from max_gst_reg_map where upper(gst_reg_desc)=upper('" + fromRegion
					+ "'))");

			// If Item has TaxCategory as -1, it is not a valid Tax Category
			dataConnection.execute(sql.getSQLString());
			resultSet = (ResultSet) dataConnection.getResult();

			if (resultSet != null) {
				MAXTaxAssignment taxAssignment = null;
				while (resultSet.next()) {
					taxAssignment = new MAXTaxAssignment();

					int taxCtgy = resultSet.getInt(FIELD_ID_CTGY_TX);
					String taxCode = resultSet.getString(FIELD_TX_CD);

					String taxCodeDesc = resultSet.getString(FIELD_TX_CD_DSCR);
					BigDecimal taxRate = resultSet.getBigDecimal(FIELD_TX_RT);
					BigDecimal taxAmtFactor = resultSet.getBigDecimal(FIELD_TX_FCT);
					BigDecimal taxableAmtFactor = resultSet.getBigDecimal(FIELD_TXBL_FCT);

					taxAssignment.setTaxCode(taxCode);
					taxAssignment.setTaxCodeDescription(taxCodeDesc);
					taxAssignment.setTaxRate(taxRate);
					taxAssignment.setTaxableAmountFactor(taxableAmtFactor);
					taxAssignment.setTaxAmountFactor(taxAmtFactor);
					taxAssignment.setTaxCategory(taxCtgy);
					taxAssignmentList.add(taxAssignment);

				}
			}

			if (item instanceof MAXPLUItemIfc) {
				((MAXPLUItemIfc) item).addTaxAssignments(
						(MAXTaxAssignment[]) taxAssignmentList.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
			} else if (item instanceof MAXGiftCardPLUItem) {
				((MAXGiftCardPLUItem) item).addTaxAssignments(
						(MAXTaxAssignment[]) taxAssignmentList.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
			}
		}

		catch (SQLException sqlException) {
			dataConnection.logSQLException(sqlException, " Tax Assignment lookup -- Error Retrieving Tax Assignments");
			throw new DataException(DataException.SQL_ERROR, "Tax Assignment lookup", sqlException);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException se) {
					dataConnection.logSQLException(se, "Tax Assignment lookup -- Could not close result handle");
				}
			}
		}

	}

	// Change for Rev 1.12: Ends

}
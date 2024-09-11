/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.1		Mar 27, 2017		Mansi Goel		Changes to resolve brand promotions are not working
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.MAXUtils;

import java.util.List;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

public class MAXUtils extends JdbcUtilities implements MAXDiscountRuleConstantsIfc, MAXARTSDatabaseIfc {

	public static boolean isTieredDiscount(int ruleReasonCode) {
		switch (ruleReasonCode) {
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered:
			return true;
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZPctoffTiered:
			return true;
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered:
			return true;
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered:
			return true;
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXatZPctoffTiered:
			return true;
			
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster:
			return true;
		case MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster:
			return true;
		default:
			return false;
		}
	}
	
	public static SQLSelectStatement buildSourceSqlForAmount(int ruleId, int comparisonBasisId) {
		SQLSelectStatement sql = new SQLSelectStatement();

		switch (comparisonBasisId) {
		case COMPARISON_BASIS_ITEM_ID:
			sql.addTable(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);

			break;
		case COMPARISON_BASIS_ITEM_GROUP:
			sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID);
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_THR_AMOUNT);
			break;

		case COMPARISON_BASIS_BRAND:
			sql.addTable(TABLE_BRAND_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_BRAND_PRDV);
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_BRAND_NAME);
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_THR_AMOUNT);
			break;

		case COMPARISON_BASIS_DEPARTMENT_ID:
		case COMPARISON_BASIS_SUBCLASS:
			sql.addTable(TABLE_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
					ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_POS_DEPARTMENT_ID);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			break;
			
		case COMPARISON_BASIS_MERCHANDISE_CLASS:
			sql.addTable(TABLE_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY,
					ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY);
			sql.addColumn(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY,
					FIELD_MERCHANDISE_CLASSIFICATION_CODE);
			sql.addColumn(ALIAS_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			break;
		default:
			break;
		}
		sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID + " = " + ruleId);
		return sql;

	}

	public static SQLSelectStatement buildSqlForBuyRsNOrMoreOfXGetYatZpctOffTiered(int ruleId) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
		sql.addTable("RU_PRDV", "RUPRDV");
		// add columns
		sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID);
		sql.addColumn("RUPRDV", "MO_TH_SRC");
		sql.addQualifier("ruprdv.id_ru_prdv =itm_grp_prdv.id_ru_prdv");

		sql.addQualifier("itm_grp_prdv.id_ru_prdv" + " = " + ruleId);

		return sql;

	}

	public static SQLSelectStatement buildSqlForBuyRsNOrMoreOfXGetYatZpctOffTieredWithoutMM(int ruleId) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable("co_el_prdv_itm", "ipdre");
		sql.addTable("RU_PRDV", "RUPRDV");
		// add columns
		sql.addColumn("ipdre", "id_itm");
		sql.addColumn("RUPRDV", "MO_TH_SRC");
		sql.addQualifier("ruprdv.id_ru_prdv =ipdre.id_ru_prdv");

		sql.addQualifier("ipdre.id_ru_prdv" + " = " + ruleId);

		return sql;

	}

	public static SQLSelectStatement buildSqlForBuyRsNOrMoreOfXGetYatZpctOffTieredWithoutMMForDept(int ruleId) {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable("CO_EL_PRDV_DPT", "PRDVDPT");
		sql.addTable("RU_PRDV", "RUPRDV");
		// add columns
		sql.addColumn("PRDVDPT", "ID_DPT_POS");
		sql.addColumn("RUPRDV", "MO_TH_SRC");
		sql.addQualifier("ruprdv.id_ru_prdv =PRDVDPT.id_ru_prdv");

		sql.addQualifier("PRDVDPT.id_ru_prdv" + " = " + ruleId);

		return sql;

	}

	public static SQLSelectStatement buildSourceSqlForAmountForBuyRsNOrMoreOfXGetYatZRsTiered(int ruleId,
			int comparisonBasisId) {
		SQLSelectStatement sql = new SQLSelectStatement();

		switch (comparisonBasisId) {
		case COMPARISON_BASIS_ITEM_ID:

			sql.addTable(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY);
			// add columns
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_ITEM_ID);
			sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);

			break;
		case COMPARISON_BASIS_ITEM_GROUP:
			sql.addTable(TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_ITEM_GROUP_PRDV);
			// add columns
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_ITEM_GROUP_ID);
			sql.addColumn(ALIAS_TABLE_ITEM_GROUP_PRDV, FIELD_THR_AMOUNT);
			break;

		case COMPARISON_BASIS_BRAND:
			sql.addTable(TABLE_BRAND_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TABLE_BRAND_PRDV);
			// add columns
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_BRAND_ID);
			sql.addColumn(ALIAS_TABLE_BRAND_PRDV, FIELD_THR_AMOUNT);
			break;

		case COMPARISON_BASIS_DEPARTMENT_ID:
		case COMPARISON_BASIS_SUBCLASS:

			sql.addTable(TABLE_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY,
					ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY);
			// add columns
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_POS_DEPARTMENT_ID);
			sql.addColumn(ALIAS_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_THR_AMOUNT);
			break;

		default:
			break;
		}

		sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID + " = " + ruleId);
		sql.addQualifier("ITM_GRP_PRDV.MO_TH <> 0");
		return sql;
	}
	
	//Changes for Rev 1.0 : Starts
	public static String getDepartmentID(String dept) {
		String departmentID = null;
		if (dept.length() == 4)
			departmentID = "3:" + dept;
		if (dept.length() == 3)
			departmentID = "3:0" + dept;
		if (dept.length() == 2)
			departmentID = "3:00" + dept;
		if (dept.length() == 1)
			departmentID = "3:000" + dept;		
		return departmentID;
	}
	
	public static String getMerchandiseClass(String merchClass){
		String merchandiseClass = null;
		int index = merchClass.lastIndexOf(":");
		merchClass = merchClass.substring(index + 1);
		merchandiseClass = "4:" + merchClass.substring(0, 8);
		return merchandiseClass;
	}
	
	public static String getBrand(PLUItemIfc pluItem){
		String brandName = null;
		String dept = pluItem.getDepartmentID();
		if (pluItem.getDepartmentID().length() == 3)
			dept = "0" + dept;
		if (pluItem.getDepartmentID().length() == 2)
			dept = "00" + dept;
		if (pluItem.getDepartmentID().length() == 1)
			dept = "000" + dept;
		if (pluItem.getItemClassification().getMerchandiseClassifications().size() > 0) {
			List<MerchandiseClassificationIfc> mci = (List<MerchandiseClassificationIfc>) pluItem.getItemClassification()
					.getMerchandiseClassifications();
			//Changes for Rev 1.1 : Starts
			MerchandiseClassificationIfc merchClassification = mci.get(4);
			//Changes for Rev 1.1 : Ends
			brandName = merchClassification.getIdentifier() + " : " + dept;
		}
		return brandName;
	}
	//Changes for Rev 1.0 : Ends 
}
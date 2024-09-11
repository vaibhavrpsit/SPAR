/********************************************************************************
 * Copyright (c) 2018 MAX Hyper Market Inc.    All Rights Reserved.
 * 
 * Rev 1.3      July03,2017			Nayya Gupta			issue 62 Comma needs to be removed to 
 * 														adjust the space issue on receipt 
 * Rev 1.2		Jun 19, 2017		Jyoti Yadav 		EJ is saving with VAT as tax type
 * Rev 1.1		Jun 16, 2017		Jyoti Yadav 		Tax codes in tax breakup for void trx
 * Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 * 
 * ***************************************************************************************/
package max.retail.stores.common.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXVoidTransaction;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;

public class MAXTAXUtils extends JdbcUtilities implements
		MAXDiscountRuleConstantsIfc, MAXARTSDatabaseIfc {

	// Change for Rev 1.1 :Starts
	public static String getItemTaxType(RetailTransactionIfc transaction) {
		/* Change for Rev 1.6: Start */
		/*
		 * String[] taxType = { "A", "B", "C", "D", "E", "F", "G", "I", "J",
		 * "K", "L", "M", "N", "O", "P" };
		 */
		String[] taxType = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z", "A1", "B1", "C1", "D1", "E1", "F1", "G1",
				"H1", "I1", "J1", "K1", "L1", "M1", "N1", "O1", "P1", "Q1",
				"R1", "S1", "T1", "U1", "V1", "W1", "X1", "Y1", "Z1" };
		/* Change for Rev 1.6: End */
		Map<String, String> taxCodeMap = new HashMap<String, String>();
		int index = 0;
		String itemTaxType = null;
		String taxCode;
		boolean isExists = false;
		for (Enumeration e = ((RetailTransactionIfc) transaction)
				.getLineItemsVector().elements(); e.hasMoreElements();) {
			MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) e
					.nextElement();
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = null;
			lineItemTaxBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice()
					.getItemTax())).getLineItemTaxBreakUpDetail();
			itemTaxType = null;
			if (lineItemTaxBreakUpDetails != null) {
				for (int j = 0; j < lineItemTaxBreakUpDetails.length; j++) {
					String taxTypes[] = new String[lineItemTaxBreakUpDetails.length];

					taxCode = lineItemTaxBreakUpDetails[j].getTaxAssignment()
							.getTaxCode();

					if (!taxCode.equals("empty")) {
						while (index < taxType.length) {
							if (taxCodeMap.containsValue(taxType[index])
									&& !taxCodeMap.containsKey(taxCode)) {
								taxTypes[j] = taxType[index + 1];
								isExists = false;
								index++;
							} else if (taxCodeMap.containsKey(taxCode)) {
								taxTypes[j] = taxCodeMap.get(taxCode);
								if (!isExists) {
									/* Change for Rev 1.5: Start */
									// index--;
									/* Change for Rev 1.5: End */
									isExists = true;
								}
							} else {
								taxTypes[j] = taxType[index];
								isExists = false;
								index++;
							}
							taxCodeMap.put(taxCode, taxTypes[j]);
							/* Change for Rev 1.1: Start */
							if (transaction instanceof MAXVoidTransaction) {
								((MAXVoidTransaction) transaction)
										.setTaxCode(taxCodeMap);
							}
							/* Change for Rev 1.1: End */
							else {
								((MAXSaleReturnTransactionIfc) transaction)
										.setTaxCode(taxCodeMap);

							}
							if (itemTaxType != null && itemTaxType != ""
									&& !itemTaxType.isEmpty()) {
								if (taxTypes[j].compareTo(itemTaxType) > 0) {
									// Rev 1.3 changes
									itemTaxType = itemTaxType.concat("")
											.concat(taxTypes[j]);
								} else {
									// Rev 1.3 changes
									itemTaxType = taxTypes[j].concat("")
											.concat(itemTaxType);
								}
							} else {
								itemTaxType = taxTypes[j];
							}
							lineItemTaxBreakUpDetails[j].getTaxAssignment()
									.setTaxType(taxTypes[j]);
							break;
						}
					}

				}
			}
			srli.setTaxType(itemTaxType);
		}
		return itemTaxType;
	}

	// Change for Rev 1.1 : Ends

	/* Change for Rev 1.2:Start */
	public static String getLineItemTaxType(MAXSaleReturnLineItemIfc srli) {
		String[] taxType = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z", "A1", "B1", "C1", "D1", "E1", "F1", "G1",
				"H1", "I1", "J1", "K1", "L1", "M1", "N1", "O1", "P1", "Q1",
				"R1", "S1", "T1", "U1", "V1", "W1", "X1", "Y1", "Z1" };
		/* Change for Rev 1.6: End */
		Map<String, String> taxCodeMap = new HashMap<String, String>();
		int index = 0;
		String itemTaxType = null;
		String taxCode;
		boolean isExists = false;

		MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = null;
		lineItemTaxBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice()
				.getItemTax())).getLineItemTaxBreakUpDetail();
		itemTaxType = null;
		if (lineItemTaxBreakUpDetails != null) {
			for (int j = 0; j < lineItemTaxBreakUpDetails.length; j++) {
				String taxTypes[] = new String[lineItemTaxBreakUpDetails.length];

				taxCode = lineItemTaxBreakUpDetails[j].getTaxAssignment()
						.getTaxCode();

				if (!taxCode.equals("empty")) {
					while (index < taxType.length) {
						if (taxCodeMap.containsValue(taxType[index])
								&& !taxCodeMap.containsKey(taxCode)) {
							taxTypes[j] = taxType[index + 1];
							isExists = false;
							index++;
						} else if (taxCodeMap.containsKey(taxCode)) {
							taxTypes[j] = taxCodeMap.get(taxCode);
							if (!isExists) {
								isExists = true;
							}
						} else {
							taxTypes[j] = taxType[index];
							isExists = false;
							index++;
						}
						taxCodeMap.put(taxCode, taxTypes[j]);
						if (itemTaxType != null && itemTaxType != ""
								&& !itemTaxType.isEmpty()) {
							if (taxTypes[j].compareTo(itemTaxType) > 0) {
								// Rev 1.3 changes
								itemTaxType = itemTaxType.concat("").concat(
										taxTypes[j]);
							} else {
								// Rev 1.3 changes
								itemTaxType = taxTypes[j].concat("").concat(
										itemTaxType);
							}
						} else {
							itemTaxType = taxTypes[j];
						}
						lineItemTaxBreakUpDetails[j].getTaxAssignment()
								.setTaxType(taxTypes[j]);
						break;
					}
				}

			}
		}
		srli.setTaxType(itemTaxType);

		return itemTaxType;
	}
	/* Change for Rev 1.2:End */
}
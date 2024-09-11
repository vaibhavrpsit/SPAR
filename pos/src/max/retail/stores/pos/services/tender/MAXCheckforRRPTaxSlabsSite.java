/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jan 02, 2019		Purushotham Reddy 	Changes for RRP Tax Slabs
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender;

/**
@author Purushotham Reddy Sirison
**/

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import max.retail.stores.domain.MAXUtils.MAXIGSTTax;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXReadTaxOnPLUItem;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NHelper;

public class MAXCheckforRRPTaxSlabsSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1L;
	
	String   letter  = "Continue";
	
	public void arrive(BusIfc bus) {

		TenderCargo cargo = (TenderCargo) bus.getCargo();
		boolean isRRPBasedGSTEnabled= false;
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try{
        	isRRPBasedGSTEnabled = pm.getBooleanValue("IsRRPBasedGSTEnabled").booleanValue();
        }
        catch (ParameterException e){
          logger.warn("IsRRPBasedGSTEnabled Parameter does not exist in application.xml file");
        }
		MAXSaleReturnTransaction transaction = (MAXSaleReturnTransaction)cargo.getTransaction();
		
		if (isRRPBasedGSTEnabled && transaction.getTransactionType() == TransactionIfc.TYPE_SALE  ) {
			checkForRRPTaxSlabs(transaction, cargo, bus);
		}
		else{
			bus.mail(new Letter(letter) , BusIfc.CURRENT);
		}
	}

	private void checkForRRPTaxSlabs(MAXSaleReturnTransaction transaction,
			TenderCargo cargo, BusIfc bus) {
		AbstractTransactionLineItemIfc[] lines = null;
		String updatedTaxCategory = null;
		String footwareCategoryThresold = null;
		String apparelCategoryThresold = null;
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		try {
			 footwareCategoryThresold = parameterManager
					.getStringValue("FootwareCategoryThreshold");
			 apparelCategoryThresold = parameterManager
					.getStringValue("ApparelCategoryThreshold");
		} catch (ParameterException pe) {
			logger.warn("Couldn't retrieve parameter: " , pe);
		}
		if (cargo.getTransaction() instanceof SaleReturnTransaction) {
			lines = ((SaleReturnTransaction) cargo.getTransaction())
					.getItemContainerProxy().getLineItems();
			for (int i = 0; i < lines.length; i++) {

				MAXSaleReturnLineItemIfc srli = ((MAXSaleReturnLineItemIfc) lines[i]);
				MAXPLUItemIfc item = (MAXPLUItemIfc) srli.getPLUItem();
				int initialTaxCategory = item.getTaxCategory();
				
				BigDecimal itemPrice = new BigDecimal(srli
						.getExtendedDiscountedSellingPrice().getDoubleValue());
				BigDecimal itemQty = srli.getItemQuantityDecimal();
				if(itemQty.compareTo(BigDecimal.ONE)== 1){
					itemPrice = itemPrice.divide(itemQty, 4, BigDecimal.ROUND_HALF_UP);
				}
				
				try {
					updatedTaxCategory = checkforSlabs(bus, initialTaxCategory);
				} catch (ParameterException e) {
					logger.warn("Couldn't retrieve parameter: " , e);
				}
				if (updatedTaxCategory != null) {
					
					//changes by kamlesh for rrp tax Starts
					
					String updatedTaxCatagory = null;
					String taxCategory =String.valueOf(initialTaxCategory);
					String taxcategoryV = "";
					String parameter = null;
					ParameterManagerIfc parameterManager1 = (ParameterManagerIfc) bus
							.getManager(ParameterManagerIfc.TYPE);
					parameter = "GSTTaxSlabList";
					Vector<String> v = null;
					String[] tax;
					try {
						String[] taxSlab = parameterManager1.getStringValues(parameter);
						v = new Vector<String>(Arrays.asList(taxSlab));
						for (int j = 0; j < v.size(); j++) {
							tax = v.get(j).split("_");
							Vector<String> taxVector = new Vector<String>(Arrays.asList(tax));
							taxcategoryV = taxVector.get(0).toString();
							if (taxCategory.equalsIgnoreCase(taxcategoryV)) {
								taxcategoryV = taxVector.get(1).toString();
								if (taxcategoryV.startsWith("Footware") && 
										(itemPrice.compareTo(new BigDecimal(footwareCategoryThresold)) == -1)) {
									changeItemTaxSlab(transaction, srli, bus,updatedTaxCategory);
							
								}
								else if (taxcategoryV.startsWith("Apparel") &&
										(itemPrice.compareTo(new BigDecimal(apparelCategoryThresold)) == -1)) 
								{
									changeItemTaxSlab(transaction, srli, bus,updatedTaxCategory);
								}
								else{
									changeItemTaxSlab(transaction, srli, bus,String.valueOf(initialTaxCategory));
									
								}
							}}
							} catch (ParameterException pe) {
							logger.warn("Couldn't retrieve parameter: " + parameter, pe);
						}
					//ends
					/*
					 * if ((itemPrice.compareTo(new BigDecimal(footwareCategoryThresold)) == -1) ||
					 * (itemPrice.compareTo(new BigDecimal(apparelCategoryThresold)) == -1)) {
					 * changeItemTaxSlab(transaction, srli, bus,String.valueOf(initialTaxCategory));
					 * 
					 * } else{ changeItemTaxSlab(transaction, srli, bus,updatedTaxCategory);
					 * 
					 * }
					 */
				}
			}
		}

		transaction.setTransactionTotals(DomainGateway.getFactory()
				.getTransactionTotalsInstance());
		transaction.getTransactionTotals().updateTransactionTotals(
				transaction.getItemContainerProxy().getLineItems(),
				transaction.getItemContainerProxy().getTransactionDiscounts(),
				transaction.getItemContainerProxy().getTransactionTax());
		bus.mail(new Letter(letter), BusIfc.CURRENT);
	}
	private String checkforSlabs(BusIfc bus, int initialTaxCategory) throws ParameterException {
		
		String updatedTaxCatagory = null;
		String taxCategory =String.valueOf(initialTaxCategory);
		String taxcategoryV = "";
		String parameter = null;
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		parameter = "GSTTaxSlabList";
		Vector<String> v = null;
		String[] tax;
		try {
			String[] taxSlab = parameterManager.getStringValues(parameter);
			v = new Vector<String>(Arrays.asList(taxSlab));
			for (int i = 0; i < v.size(); i++) {
				tax = v.get(i).split("_");
				Vector<String> taxVector = new Vector<String>(Arrays.asList(tax));
				taxcategoryV = taxVector.get(0).toString();
				if (taxCategory.equalsIgnoreCase(taxcategoryV)) {
					taxcategoryV = taxVector.get(1).toString();
					if (taxcategoryV.startsWith("Footware")) {
						parameter = "FootwareCategory";
						String footwareParam = parameterManager.getStringValue(parameter);
						v = new Vector<String>(Arrays.asList(footwareParam));
						updatedTaxCatagory = footwareParam;
					}

					else if (taxcategoryV.startsWith("Apparel")) {
						parameter = "ApparelCategory";
						String apprealParam = parameterManager.getStringValue(parameter);
						v = new Vector<String>(Arrays.asList(apprealParam));
						updatedTaxCatagory = apprealParam;
					}
				}
			}
		} catch (ParameterException pe) {
			logger.warn("Couldn't retrieve parameter: " + parameter, pe);
		}
		return updatedTaxCatagory;
	}


	private void changeItemTaxSlab(MAXSaleReturnTransaction transaction,
			MAXSaleReturnLineItemIfc srli, BusIfc bus, String updatedTaxCategory) {
		ArrayList<MAXTaxAssignment> taxAssignmentList = null;

		((MAXPLUItemIfc) srli.getPLUItem()).setTaxAssignments(null);

		MAXIGSTTax igstTax = new MAXIGSTTax();
		igstTax.setTaxCategory(updatedTaxCategory);
		igstTax.setRrpTaxEnabled(true);
		igstTax.setStoreId(((MAXSaleReturnTransactionIfc) transaction)
				.getTransactionIdentifier().getStoreID());
		
		MAXReadTaxOnPLUItem tax = new MAXReadTaxOnPLUItem();

		tax = (MAXReadTaxOnPLUItem) DataTransactionFactory
				.create(MAXDataTransactionKeys.ReadIGSTTaxTransactions);

		try {
			taxAssignmentList = tax.readTax(igstTax);
		} catch (DataException e) {
			logger.error("Error accesssing the Database", e);
		}
		if (taxAssignmentList != null) {
			((MAXPLUItemIfc) srli.getPLUItem()).setTaxAssignments((MAXTaxAssignment[]) taxAssignmentList
							.toArray(new MAXTaxAssignment[taxAssignmentList.size()]));
			
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = ((MAXSaleReturnLineItemIfc) srli)
									.getLineItemTaxBreakUpDetails();
			
			CurrencyIfc taxinclusiveSellingPrice = srli.getItemPrice().getExtendedDiscountedSellingPrice();
		
			int i = 0 ;
 			for(MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUpDetail : lineItemTaxBreakUpDetails){
				  lineItemTaxBreakUpDetail.setTaxableAmount(taxinclusiveSellingPrice.
						  multiply(((MAXTaxAssignmentIfc)taxAssignmentList.get(i)).getTaxableAmountFactor()));
				  lineItemTaxBreakUpDetail.setTaxAmount(taxinclusiveSellingPrice.
						  multiply(((MAXTaxAssignmentIfc)taxAssignmentList.get(i)).getTaxAmountFactor()));
				  lineItemTaxBreakUpDetail.setTaxRate(((MAXTaxAssignmentIfc) taxAssignmentList.get(i)).getTaxRate());
				  lineItemTaxBreakUpDetail.setTaxAssignment(((MAXTaxAssignmentIfc)taxAssignmentList.get(i)));
				  lineItemTaxBreakUpDetail.getTaxAssignment().setTaxAmountFactor
					(taxAssignmentList.get(i).getTaxAmountFactor());
				  //KAMLESH CHANGES STARTS
				  
				  StringBuffer buffer = new StringBuffer();
					Object[] dataArgs = null;
					dataArgs = new Object[] { lineItemTaxBreakUpDetail.getTaxRate().toString() };
				    buffer.append(Util.EOL);
				    // dataArgs = new Object[] { rateCode, tax.toGroupFormattedString() };
			       buffer.append(I18NHelper.getString("EJournal", "JournalEntry.RRPRuleLabel",dataArgs));
				  
				  //ENDS
				i++;
			}
			
			((MAXSaleReturnLineItemIfc) srli).setLineItemTaxBreakUpDetails(lineItemTaxBreakUpDetails);
		}
		else {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("RRPGstTaxSlabsError");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.OK);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
	}
}
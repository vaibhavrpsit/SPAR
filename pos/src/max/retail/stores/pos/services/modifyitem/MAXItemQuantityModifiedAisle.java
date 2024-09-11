/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.2   Kamlesh Pant  Sep 18, 2022		CapLimit Enforcement for Liquor
  Rev 1.1	Tanmaya		  11/06/2013		Bug 6093 - Incorrect marking of the not send status.
  Rev 1.0	Prateek		  23/03/2013		Initial Draft: Changes for Quantity Button
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.modifyitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.modifyitem.ItemQuantityModifiedAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXItemQuantityModifiedAisle extends ItemQuantityModifiedAisle {

	// ----------------------------------------------------------------------
	/**
	 * ##COMMENT-TRAVERSE##
	 * <P>
	 * 
	 * @param bus Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		boolean continueFlow = true;
	//	System.out.println("Kamlesh Pant");
		BigDecimal newQuantity; // new quantity to set the line item to
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String quantity = ui.getInput();
		BigDecimal tmp = null;
		float a = 0;
		JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher()
				.getManager(JournalFormatterManagerIfc.TYPE);

		try {
			tmp = new BigDecimal(quantity);
			if (tmp.scale() == 0) {
				tmp = tmp.multiply(new BigDecimal("1.00"));
			} else if (tmp.scale() == 1) {
				tmp = tmp.multiply(Util.I_BIG_DECIMAL_ONE);
			}
		} catch (Exception e) {
			// Changes for taking wrong value in quantity : Start
			continueFlow = false;
			// tmp = new BigDecimal("1.00");//what do we do when invalid???
			DialogBeanModel dModel = new DialogBeanModel();
			dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dModel.setResourceID("QuantityCannotBeDot");
			dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			// Changes for taking wrong value in quantity : End
		}
		if (continueFlow) {
			// if quantity entered is zero, reenter quantity.
			if (tmp.compareTo(BigDecimalConstants.ZERO_AMOUNT) == 0) {
				DialogBeanModel dModel = new DialogBeanModel();
				dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dModel.setResourceID("QuantityCannotBeZero");
				dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			} else {
				// check if this item is a sale or return
				ItemCargo cargo = (ItemCargo) bus.getCargo();
				//System.out.println("cargo.getTransaction().getLineItemsVector().size() :"+cargo.getTransaction().getLineItemsVector().size());
				if (cargo.getItem().getItemQuantityDecimal().signum() >= 0) {
					newQuantity = tmp;
				} else {
					newQuantity = tmp.negate();
				}

				//float oldquantity = Float.parseFloat(cargo.getItem().getItemQuantity().toString());

				// save original item in stringbuffer for journal
				StringBuffer sb = new StringBuffer();
				SaleReturnLineItemIfc item = cargo.getItem();
				sb.append(formatter.toJournalRemoveString(item));
				ItemDiscountStrategyIfc[] itemDiscounts = item.getItemPrice().getItemDiscounts();
				if ((itemDiscounts != null) && (itemDiscounts.length > 0)) {
					for (int i = 0; i < itemDiscounts.length; i++) {
						if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit)) {
							sb.append(Util.EOL);
							sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i], true));
						}
					}
				}

				// set the quantity of the line item
				item.modifyItemQuantity(newQuantity);

				// CR28143: For VAT the line item's tax amounts are reported in the EJournal.
				// In order to retrieve the right values, they must be recalculated. The
				// actual update to the transaction doesn't occur until leaving
				// ModifyItemReturnShuttle.
				// To fix this calculate the tax forthe price overriden items in a clone of the
				// transaction.
				// This fix is copied from ItemPriceModifiedAisle - Mani

				RetailTransactionIfc transaction = (RetailTransactionIfc) cargo.getTransaction();
				if (transaction instanceof SaleReturnTransactionIfc) {
					SaleReturnTransactionIfc srTransaction = (SaleReturnTransactionIfc) transaction.clone();
					srTransaction.replaceLineItem(item, item.getLineNumber());
					srTransaction.updateTransactionTotals();
					MAXSaleReturnLineItem itmCargo = null;
					SaleReturnLineItemIfc[] itemlist = (SaleReturnLineItemIfc[]) (cargo.getItems());
					MAXSaleReturnTransactionIfc transaction1 = (MAXSaleReturnTransactionIfc) cargo.getTransaction();

					for (int i = 0; i < itemlist.length; i++) {
						// Rev 1.2 starts for liquor
						float beertot = 0;
						float InLiqtot = 0;
						float frnLiqtot = 0;
						float liquorTotal = 0;
						itmCargo = (MAXSaleReturnLineItem) itemlist[i];
						Iterator itr = (((SaleReturnTransactionIfc) transaction).getItemContainerProxy()
								.getLineItemsVector()).iterator();
						//float a1 = ((MAXPLUItemIfc)itmCargo.getPLUItem());
						while (itr.hasNext()) {
							MAXSaleReturnLineItem itm = (MAXSaleReturnLineItem) (itr.next());
							if (itmCargo.getPLUItemID().equals(itm.getPLUItemID())) {
								((MAXSaleReturnLineItem) itemlist[i])
										.setScansheetCategoryID(itm.getScansheetCategoryID());
								((MAXSaleReturnLineItem) itemlist[i])
										.setScansheetCategoryDesc(itm.getScansheetCategoryDesc());
								cargo.setItems(itemlist);
							}
						}
						
						DialogBeanModel model = new DialogBeanModel();
						POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
						MAXPLUItemIfc  pluItem= ((MAXPLUItemIfc)itmCargo.getPLUItem());
						// PromptAndResponseModel beanModel = new PromptAndResponseModel();
						MaxLiquorDetails liquorDetail = null;
						MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
								.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
						try {
							liquorDetail = ((MAXHotKeysTransaction) hotKeysTransaction)
									.getLiquorUMAndCategory(pluItem.getItemID());
						} catch (DataException e) {
							logger.warn(e.getMessage());
						}
						
						if (liquorDetail.getDepartment() != null && liquorDetail.getDepartment().equals("41")) {
							try {
								//String category = cat.toString();
								ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
										.getManager(ParameterManagerIfc.TYPE);
								float indLiq = Float
										.parseFloat(parameterManager.getStringValue("IndianLiqureTotal"));
								float beer = Float.parseFloat(parameterManager.getStringValue("BeerLiqureTotal"));
								float frnLiq = Float
										.parseFloat(parameterManager.getStringValue("ForeigenLiqureTotal"));
								float liqTotal = Float
										.parseFloat(parameterManager.getStringValue("OverallLiqureTotal"));
									
								//float value = Float.parseFloat(liquorDetail.getLiqUMinLtr());
								//float quant = Float.parseFloat(quantity);							
								
								transaction1.setInLiqtot(0);
								//System.out.println("cargo.getTransaction().getLineItemsVector().size() ::"+cargo.getTransaction().getLineItemsVector().size());
								for(int j=0;j<cargo.getTransaction().getLineItemsVector().size();j++)
								{
									//((SaleReturnLineItem)cargo.getTransaction().getLineItemsVector().get(j)).setItemQuantity(newQuantity);
									
								//	System.out.println("Inside while");
								//	System.out.println("j202 ::"+j);
									String category = ((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemClassification().getMerchandiseClassifications().get(8).getIdentifier();
									if(category.equalsIgnoreCase("BEER"))
									{
										String value1 =((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemClassification().getMerchandiseClassifications().get(7).getIdentifier();
										float value = Float.parseFloat(value1);
										//String quan = ((SaleReturnLineItem)cargo.getTransaction().getLineItemsVector().get(j)).getItemQuantity().toString();
										float quant = Float.parseFloat(cargo.getTransaction().getLineItemsVector().get(j).getItemQuantity().toString());
										if(((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemID().equalsIgnoreCase(cargo.getItem().getPLUItem().getItemID()))
										{
											quant = Float.parseFloat(cargo.getItem().getItemQuantityDecimal().toString());
										}
								//		System.out.println("Quantity 223:"+quant);
								//		System.out.println("value 224:"+value);
										beertot= beertot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
										
										if(beertot>beer) 
										{
											BigDecimal temp = new BigDecimal(1);
											item.modifyItemQuantity(temp);
											String[] msg = new String[4];
											msg[0] = String.valueOf(beertot); 
											msg[1] = "BEER"; 
											msg[2] =parameterManager.getStringValue("BeerLiqureTotal"); 
											msg[3] ="Press Enter to Change the Quantity."; 
											model.setArgs(msg);
											model.setResourceID("LiquorQuantityError");
											model.setType(DialogScreensIfc.ERROR);
											model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
											uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
											return;
										}
									}
									else if(category.equalsIgnoreCase("INDN LIQR"))
									{
										
										String value1 =((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemClassification().getMerchandiseClassifications().get(7).getIdentifier();
										float value = Float.parseFloat(value1);
										float quant = Float.parseFloat(cargo.getTransaction().getLineItemsVector().get(j).getItemQuantity().toString());
										
										if(((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemID().equalsIgnoreCase(cargo.getItem().getPLUItem().getItemID()))
										{
											quant = Float.parseFloat(cargo.getItem().getItemQuantityDecimal().toString());
										}
										//String quan = ((SaleReturnLineItem)cargo.getTransaction().getLineItemsVector().get(j)).getItemQuantity().toString();
										
									//	System.out.println("Quantity 223:"+quant);
									//	System.out.println("value 224:"+value);
										InLiqtot= InLiqtot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
								//		System.out.println("244"+InLiqtot);
									
										
									//	System.out.println("InLiqtot ::"+InLiqtot);
										if(InLiqtot>indLiq) {
											BigDecimal temp = new BigDecimal(1);
											item.modifyItemQuantity(temp);
											//System.out.println("Indian error 258::"+item.getItemQuantity());
											String[] msg = new String[4];
											msg[0] = String.valueOf(InLiqtot); 
											msg[1] = "INDN LIQR"; 
											msg[2] =parameterManager.getStringValue("IndianLiqureTotal"); 
											msg[3] ="Press Enter to Change the Quantity."; 
											model.setArgs(msg);
											model.setResourceID("LiquorQuantityError");
											model.setType(DialogScreensIfc.ERROR);
											model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
											uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
											return;
										}
									}
									else if(category.equalsIgnoreCase("FORN LIQR"))
									{
										String value1 =((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemClassification().getMerchandiseClassifications().get(7).getIdentifier();
										float value = Float.parseFloat(value1);
										float quant = Float.parseFloat(cargo.getTransaction().getLineItemsVector().get(j).getItemQuantity().toString());
										if(((MAXSaleReturnLineItem) cargo.getTransaction().getLineItemsVector().get(j)).getPLUItem().getItemID().equalsIgnoreCase(cargo.getItem().getPLUItem().getItemID()))
										{
											quant = Float.parseFloat(cargo.getItem().getItemQuantityDecimal().toString());
										}
									//	System.out.println("Quantity 223:"+quant);
									//	System.out.println("value 224:"+value);
										frnLiqtot = frnLiqtot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
										
										if(frnLiqtot>frnLiq) 
										{
											BigDecimal temp = new BigDecimal(1);
											item.modifyItemQuantity(temp);
											String[] msg = new String[4];
											msg[0] = String.valueOf(frnLiqtot); 
											msg[1] = "FRN LIQR"; 
											msg[2] =parameterManager.getStringValue("ForeigenLiqureTotal"); 
											msg[3] ="Press Enter to Change the Quantity."; 
											model.setArgs(msg);
											model.setResourceID("LiquorQuantityError");
											model.setType(DialogScreensIfc.ERROR);
											model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
											uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
											return;
										}
									}
																
								if(transaction1.getliquortot()<=liqTotal)
								{
									liquorTotal = beertot + InLiqtot + frnLiqtot;
									if(liquorTotal>liqTotal) 
									{
										String[] msg = new String[4];
										msg[0] = String.valueOf(liquorTotal); 
										msg[1] = "Total"; 
										msg[2] =parameterManager.getStringValue("ForeigenLiqureTotal"); 
										msg[3] ="Press Enter to Change the Quantity."; 
										model.setArgs(msg);
										model.setResourceID("LiquorQuantityError");
										model.setType(DialogScreensIfc.ERROR);
										model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
										uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
										return;
									}
								}
							}
							}
							catch (Exception e) {
								logger.warn(e.getMessage());
							}
						}

						transaction1.setBeertot(beertot);
						transaction1.setInLiqtot(InLiqtot);
						transaction1.setfrnLiqtot(frnLiqtot);
						transaction1.setliquortot(liquorTotal);
						logger.info("After while loop getBeertot ::" + transaction1.getBeertot());
						logger.info("After while loop getInLiqtot ::" + transaction1.getInLiqtot());
						logger.info("After while loop getfrnLiqtot ::" + transaction1.getfrnLiqtot());
						logger.info("After while loop getliquortot ::" + transaction1.getliquortot());

						//System.out.println("385 InLiqtot ::" + transaction1.getInLiqtot());
						//System.out.println("386 Beertot ::" + transaction1.getBeertot());
						//System.out.println("386 frnLiqtot ::" + transaction1.getfrnLiqtot());
						//System.out.println("386 liquortot ::" + transaction1.getliquortot());

						// Rev 1.2 Ends
					}
				}

				// journal it here
				JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher()
						.getManager(JournalManagerIfc.TYPE);

				if (journal != null) {
					// save new item info in stringbuffer for journal
					sb.append(Util.EOL);
					sb.append(formatter.toJournalString(cargo.getItem(), null, null));
					itemDiscounts = item.getItemPrice().getItemDiscounts();
					if ((itemDiscounts != null) && (itemDiscounts.length > 0)) {
						for (int i = 0; i < itemDiscounts.length; ++i) {
							if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit)) {
								sb.append(Util.EOL);
								sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i], false));
							}
						}
					}

					// write the journal
					journal.journal(cargo.getCashier().getEmployeeID(), cargo.getTransactionID(), sb.toString());
				} else {
					logger.warn("No journal manager found!");
				}
				// Changes starts ofr code merging(commenting below condition as per MAX)
				// if (item.getSendLabelCount() <= 0 ||(cargo.getTransaction() != null &&
				// cargo.getTransaction().getTransactionTotals().isTransactionLevelSendAssigned()))
				if (item.getSendLabelCount() <= 0 || (cargo.getTransaction() != null
						&& ((SaleReturnTransactionIfc) cargo.getTransaction()).isTransactionLevelSendAssigned()))
				// Changes ends for code merging
				{
					// Done modifying quantity, mail a final letter.
					bus.mail(new FinalLetter("Done"), BusIfc.CURRENT);
				} else {
					cargo.setItemQuantity(newQuantity);
					bus.mail(new Letter("ShippingMethod"), BusIfc.CURRENT);
				}
			}
		}

	}
}

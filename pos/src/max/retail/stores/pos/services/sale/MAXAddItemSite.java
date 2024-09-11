/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   	Rev 1.5  Sep 18, 2022   Kamlesh Pant					CapLimit Enforcement for Liquor
    Rev 1.4	 17/Feb/2017	Nadia Arora						Changes for Advanced Search - item not getting added and for exception
   	Rev 1.3  5/Jan/2015  	Akanksha Chauhan\Bhanu Priya,  	Changes for Gift Card Scanned Functionality 
	Rev 1.2  03/June/2013	Jyoti							Bug 6145 - GC issue/Reload :- incorrect message is showing
	Rev 1.1  20/May/2013	Tanmaya							Bug 5779 - Item is not showing as "Not Send item", in POS at the time of Home delivery send.
  	Rev 1.0  23/Apr/2013	Jyoti Rawal						Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
// foundation imports
import java.util.HashMap;
import java.util.Iterator;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.domain.utility.MAXEntryMethodConstantsIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.modifyitem.MAXLiquorItemQuantitySite;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import org.apache.log4j.Logger;
import oracle.retail.stores.domain.utility.EntryMethod;

//--------------------------------------------------------------------------
/**
 * This site adds an item to the transaction.
 * 
 * @version $Revision: 3$
 **/
//--------------------------------------------------------------------------
public class MAXAddItemSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2663281567359377269L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";
	private static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.sale.MAXAddItemSite.class);

	// ----------------------------------------------------------------------
	/**
       Adds the item to the transaction. Mails Continue letter is special order to not
       ask for serial numbers, else mails GetSerialNumbers letter to possibly ask for
       serial numbers.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings("null")
	public void arrive(BusIfc bus)
    {
        // Grab the item from the cargo
    	
        MAXSaleCargo cargo = (MAXSaleCargo)bus.getCargo();
    	
        
        String letter = CommonLetterIfc.CONTINUE;
        SaleReturnTransactionIfc transaction1 = cargo.getTransaction();
  // changes done for code merging(commenting below line)
        //SaleReturnLineItemIfc item = cargo.getLineItem();
        MAXSaleReturnLineItem item = (MAXSaleReturnLineItem) cargo.getLineItem();
        PLUItem pluItem = null;
		/*
		 * Object pluc = cargo.getEmpID(); if(pluc != null ) { cargo.setEmpID(true); }
		 */
     
       //System.out.println("101 ::"+cargo.getEmpID());
       
      //  System.out.println("102 ::"+empID);
    //   PLUItemIfc emp = null;
       // cargo.getEmployee().getEmployeeID(empID);
		
		  //cargo.setEmployee(employee); 
		/*
		 * PLUItemIfc pluemp =(PLUItemIfc)cargo.getEmployee(); if(pluemp!=null) {
		 * pluemp.setEmpID(true); }
		 */
		
		 // System.out.println("105 ::"+pluc.getEmpID());
		 
        
        if(cargo.getPLUItem() instanceof MAXPLUItem) {
        	 pluItem = (MAXPLUItem) cargo.getPLUItem();
        	//pluItem.setEmpID(((PLUItem) pluc).getEmpID());
        	//pluItem.setEmpID(((PLUItem) pluc).getEmpID());
        	pluItem.setEmpID(cargo.getEmpID());
           // System.out.println("126 ::"+ cargo.getPLUItem().getEmpID());
            //System.out.println("127 ::"+ pluItem.getEmpID());
        }
        else
        	pluItem = (MAXPLUItem) cargo.getPLUItem();
       
        
        //System.out.println("133 ::"+ cargo.getPLUItem().getEmpID());
        //System.out.println("134 ::"+ pluItem.getEmpID());
//Rev 1.5 Starts for liquor
        MaxLiquorDetails liquorDetail = null;
        DialogBeanModel model = new DialogBeanModel();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
		try {
			liquorDetail = ((MAXHotKeysTransaction) hotKeysTransaction)
					.getLiquorUMAndCategory(pluItem.getItemID());
		} catch (DataException e) {
			logger.warn(e.getMessage());
		}
		//System.out.println("liquorDetail :"+liquorDetail.toString());
        
        //String letter = CommonLetterIfc.CONTINUE;
        MAXSaleReturnTransactionIfc transaction = cargo.getTransaction();
        //MAXSaleReturnLineItem item = (MAXSaleReturnLineItem) cargo.getLineItem();
        
		if(liquorDetail.getDepartment()!=null && liquorDetail.getDepartment().equals("41"))
		{							  
			try { 
				ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
						.getManager(ParameterManagerIfc.TYPE);
				float indLiq =  Float.parseFloat(parameterManager.getStringValue("IndianLiqureTotal"));
				float beer = Float.parseFloat(parameterManager.getStringValue("BeerLiqureTotal")); 
				float frnLiq =  Float.parseFloat(parameterManager.getStringValue("ForeigenLiqureTotal")); 
				float liquorTotal =  Float.parseFloat(parameterManager.getStringValue("OverallLiqureTotal"));
				float value =  Float.parseFloat(liquorDetail.getLiqUMinLtr()); 
				float beertot = transaction.getBeertot();
				float inLiqtot = transaction.getInLiqtot();
				float frnLiqtot = transaction.getfrnLiqtot();
				float liquortot = transaction.getliquortot();
				
				float quant = Float.parseFloat(item.getItemQuantityDecimal().toString());
				if(liquorDetail.getLiquorCategory().equalsIgnoreCase("BEER"))
				{
					beertot= beertot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
				}
				else if(liquorDetail.getLiquorCategory().equalsIgnoreCase("INDN LIQR"))
				{
					inLiqtot= inLiqtot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
				}
				else if(liquorDetail.getLiquorCategory().equalsIgnoreCase("FORN LIQR"))
				{
					frnLiqtot = frnLiqtot + MAXLiquorItemQuantitySite.liquorLimit(quant,value);
				}
				 
				transaction.setBeertot(beertot);
				transaction.setInLiqtot(inLiqtot);
				transaction.setfrnLiqtot(frnLiqtot);
				//System.out.println("Before AddItem Error msg ::"+transaction.getInLiqtot());
				
				if(transaction.getBeertot()>beer) 
				{
					//System.out.println("Inside AddItem Error msg");
					String[] msg = new String[4];
					msg[0] = String.valueOf(transaction.getBeertot()); 
					msg[1] = "BEER"; 
					msg[2] =parameterManager.getStringValue("BeerLiqureTotal"); 
					msg[3] ="Press Enter to Return Previous Screen."; 
					model.setArgs(msg);
					model.setResourceID("LiquorQuantityErrormsg");
					model.setType(DialogScreensIfc.ERROR);
					model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
					beertot = beertot - value;
					transaction.setBeertot(beertot);
					return;
				}
				
				if(transaction.getInLiqtot()>indLiq) 
				{
					//System.out.println("Inside AddItem Error msg");
					String[] msg = new String[4];
					msg[0] = String.valueOf(transaction.getInLiqtot()); 
					msg[1] = "INDN LIQR"; 
					msg[2] =parameterManager.getStringValue("IndianLiqureTotal"); 
					msg[3] ="Press Enter to Return Previous Screen."; 
					model.setArgs(msg);
					model.setResourceID("LiquorQuantityErrormsg");
					model.setType(DialogScreensIfc.ERROR);
					model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
					inLiqtot = inLiqtot - value;
					transaction.setInLiqtot(inLiqtot);
					return;
				}
				
				if(transaction.getfrnLiqtot()>frnLiq) 
				{
					//System.out.println("Inside AddItem Error msg");
					String[] msg = new String[4];
					msg[0] = String.valueOf(transaction.getfrnLiqtot()); 
					msg[1] = "FRN LIQR"; 
					msg[2] =parameterManager.getStringValue("ForeigenLiqureTotal"); 
					msg[3] ="Press Enter to Return Previous Screen."; 
					model.setArgs(msg);
					model.setResourceID("LiquorQuantityErrormsg");
					model.setType(DialogScreensIfc.ERROR);
					model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
					frnLiqtot = frnLiqtot - value;
					transaction.setfrnLiqtot(frnLiqtot);
					return;
				}
				
				if(transaction.getliquortot()<=liquorTotal)
				{
					liquortot = transaction.getBeertot() + transaction.getInLiqtot() + transaction.getfrnLiqtot();
					if(transaction.getliquortot()>liquorTotal)
					{
						//System.out.println("Inside AddItem Error msg");
						String[] msg = new String[4];
						msg[0] = String.valueOf(transaction.getliquortot()); 
						msg[1] = "TOTAL LIQR"; 
						msg[2] =parameterManager.getStringValue("OverallLiqureTotal"); 
						msg[3] ="Press Enter to Return Previous Screen."; 
						model.setArgs(msg);
						model.setResourceID("LiquorQuantityErrormsg");
						model.setType(DialogScreensIfc.ERROR);
						model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
						transaction.setliquortot(0);
						return;
					}
				}
				
				transaction.setBeertot(beertot);
				transaction.setInLiqtot(inLiqtot);
				transaction.setfrnLiqtot(frnLiqtot);
				transaction.setliquortot(liquortot);
				
			//	System.out.println("getBeertot ::"+transaction.getBeertot());
			//	System.out.println("getInLiqtot ::"+transaction.getInLiqtot());
			//	System.out.println("getfrnLiqtot ::"+transaction.getfrnLiqtot());
			//	System.out.println("getliquortot ::"+transaction.getliquortot());
			
			}
			catch(Exception e) {
				logger.warn(e.getMessage());
			}
		}
	// Rev 1.5 Ends

	// changes ends for code merging
	/**
	 * Rev 1.0 changes start here
	 */
	if(transaction!=null)

	{
		Iterator itr = cargo.getTransaction().getLineItemsIterator();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		while (itr.hasNext()) {
			SaleReturnLineItemIfc itemObj = (SaleReturnLineItemIfc) itr.next();
			if (itemObj.getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD)
					&& (!(item.getPLUItemID().equals("70071000")))) {
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("ITEM_NOT_ALLOWED"); // Rev 1.2 changes
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
		}
		/**
		 * Rev 1.0 changes start here
		 */
	}/* Rev 1.4 changes starts */
	else
	{
		cargo.initializeTransaction(bus);
		transaction = cargo.getTransaction();
		if (item == null) {
			item = (MAXSaleReturnLineItem) (cargo.getTransaction().addPLUItem(pluItem, cargo.getItemQuantity()));
		}
	}
	/* Rev 1.4 changes ends */

	// Rev 1.3
	if(transaction!=null&&((MAXTransactionTotalsIfc)transaction.getTransactionTotals()).isTransactionLevelSendAssigned())
	{
		SendManagerIfc sendMgr = null;
		try {
			sendMgr = (SendManagerIfc) ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
		} catch (ManagerException e) {
			// default to product version
			sendMgr = new SendManager();
		}
		if (sendMgr.checkValidSendItem(item)) {
			item.setItemSendFlag(true);
			// this value is always 1 since multiple sends are not allowed
			item.setSendLabelCount(1);
		}
		/**
		 * Rev 1.1 changes start here
		 */
		else {
			item.setSendLabelCount(-1);
		}
		/**
		 * Rev 1.1 changes end hereMAXAddItemSite.java
		 */
	}

	// Add a gift receipt to the line item if one is assigned to the whole
	// transaction,\
	// Rev 1.3
	if(transaction!=null&&transaction.isTransactionGiftReceiptAssigned()&&!item.hasDamageDiscount())item.setGiftReceiptItem(true);

	// If special order then skip serial number entry.
	// (For special orders we just have the *desire* to get the item --
	// we do not have the actual item yet.)
	if(transaction!=null&&transaction.getTransactionType()!=TransactionIfc.TYPE_ORDER_INITIATE)
	{
		// check for serial item if serialized item or kit header
		if (item.isSerializedItem() || item.isKitHeader()) {
			letter = "GetSerialNumbers";
		}
	}else // we do have a special order
	{
		// If it is an item that is not eligible for special order
		if (transaction != null && cargo.getPLUItem().isSpecialOrderEligible() == false) {
			letter = "NotValid";
		}
	}

	if(letter!="NotValid")
	{

		// set issue gift card items to gift receipt
		// Rev 1.3 start
		if (item != null) {
			if (item.isGiftCardIssue() || item.isGiftCardReload()) {
				try {
					ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
					boolean autoPrintGiftReceiptGiftCardIssue = pm
							.getStringValue("AutoPrintGiftReceiptForGiftCardIssue").equalsIgnoreCase("Y");
					if (autoPrintGiftReceiptGiftCardIssue) {
						item.setGiftReceiptItem(true);
					}
				}

				catch (ParameterException pe) {
					logger.warn("Unable to retrieve parameter" + pe.getMessage());
				}

			}

			// Indicate whether the item ID was scanned or typed
			if (cargo.isItemScanned()) {
// Changes starts ofr code merging(commenting below line)
				// item.setEntryMethod(EntryMethodConstantsIfc.ENTRY_METHOD_SCANNED);
				item.setEntryMethod(EntryMethod.getEntryMethod(1));

// Changes ends for code merging
				// reset scanning flag
				cargo.setItemScanned(false);
			}

			if ((cargo.getCategoryIDScanSheet()) != null && (cargo.getCategoryDescripionScanSheet()) != null) {
				item.setScansheetCategoryID(cargo.getCategoryIDScanSheet());
				item.setScansheetCategoryDesc(cargo.getCategoryDescripionScanSheet());
			} else {
				String defaultCategory = Gateway.getProperty("application", "DefaultCategory", "");
				item.setScansheetCategoryID("0");
				item.setScansheetCategoryDesc(defaultCategory);
			}
			cargo.setLineItem(item);

			// save size in line item, if we have it
			if (cargo.getItemSizeCode() != null) {
				item.setItemSizeCode(cargo.getItemSizeCode());
				// reset size now
				cargo.setItemSizeCode(null);
			}

			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
			try {
				pda.lineDisplayItem(item);
			} catch (DeviceException e) {
				logger.warn("Unable to use Line Display: " + e.getMessage() + "");
			}
			try {
				// Changes starts for code merging(added string ItemScreen at below line instead
				// of MAXIngenicoItems.SCREEN_NAME)
				// CIDAction action = new CIDAction(MAXIngenicoItems.SCREEN_NAME,
				// CIDAction.ADD_ITEM);
				CIDAction action = new CIDAction("ItemScreen", CIDAction.ADD_ITEM);
				// Chnages ends for code merging
				action.setLineItem(item);
				pda.cidScreenPerformAction(action);
			} catch (DeviceException e) {
				logger.warn("Unable to use CPOI: " + e.getMessage() + "");
			}
		}
	}
	// Rev 1.3 End
	cargo.setCategoryIDScanSheet(null);
	cargo.setCategoryDescripionScanSheet(null);
	bus.mail(new Letter(letter),BusIfc.CURRENT);
}

}

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016-2017 MAXHyperMarkets, Inc.    All Rights Reserved. 
  Rev 1.0   Sep 12, 2022	Kamlesh Pant	CapLimit Enforcement for Liquor
  Rev 1.1   Nitika Arora    27 Feb 2017     Changes for barcode length check in isHotKeyValid method for Bug(Not scanning the weighted items)
  Rev 1.0	Nadia Arora		24 Oct 2016		Initial Draft:	HotKeys FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import java.math.BigDecimal; 
import java.util.ArrayList;
import java.util.Locale;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.sale.MAXSaleConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
// java imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKit;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This site validates the item number stored in the cargo.
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/2 $
 **/
// --------------------------------------------------------------------------
public class MAXValidateItemInfoSite extends PosSiteActionAdapter {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8244911946621506566L;
	/**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/21 $";
    /**
     * item number field
     */
    public static final int ITEM_NUMBER_FIELD = 1;
    /**
     * item description field
     */
    public static final int ITEM_DESC_FIELD = 2;
    /**
     * item manufacturer field
     */
    public static final int ITEM_MANUFAC_FIELD = 3;
    
    /**
	 * Letter for multiple items found
	 */
	public static final String LETTER_MULTIPLE_ITEMS_FOUND = "MultipleItemsFound";
	
	/**
	 * Letter for one item found
	 */
	public static final String LETTER_ONE_ITEM_FOUND = "OneItemFound";

	public static final String LETTER_REGULAR_ITEM_NOT_FOUND = "RegularItemNotFound";
	
	/**
	 * constant for parameter name
	 **/
	public static final String ITEM_MAXIMUM_MATCHES = "ItemMaximumMatches";

    /**
     * Validate the item info stored in the cargo( number, desc and dept). If
     * the item is found, a Success letter is sent. Otherwise, a Failure letter
     * is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	//System.out.println("MAXValidateItemInfoSite :");
    	// <!-- MAX Rev 1.1 Change : Start -->    	
   	 	POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
        UtilityIfc utility;
       // String letter = "";
        boolean offline = false;
		boolean showDialog = false;
	// <!-- MAX Rev 1.1 Change : end -->
		
        // letter to be sent
        String letter = null;

        // get item inquiry from cargo
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) cargo.getInquiry();
       // MAXPLUItemIfc plu  = (MAXPLUItemIfc)bus.getCargo();
     //   System.out.println("129:"+cargo.getLiqcat());
        //System.out.println(((MAXPLUItemIfc) pluItem).getliqcat());
        cargo.resetInvalidFieldCounter();
        //inquiry.getItemNumber();
        cargo.setNecBarCode(inquiry.getItemNumber());
        
        
        if(inquiry.getItemNumber().startsWith("3") && cargo.getNecBarCode().length()==18)
        {
        	//inquiry.setItemID(inquiry.getItemNumber());
        	inquiry.setItemNumber(inquiry.getItemNumber().substring(1,10));
        	System.out.println(inquiry.getItemNumber());
        	//inquiry.getItemNumber();
        	
        }
        

        // <!-- MAX Rev 1.1 Change : Start -->
        
        
		MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
		
    	try {
			utility = Utility.createInstance();
		
		offline = isSystemOffline(utility);
		FinancialCountIfc fci = cargo.getRegister().getCurrentTill().getTotals()
				.getCombinedCount().getExpected();
		FinancialCountTenderItemIfc[] fctis = fci.getTenderItems();
		String  tillFloat="0.00";
		for (int i = 0; i < fctis.length; i++) {
			if(fctis[i].getDescription().equalsIgnoreCase("CASH")){
				tillFloat=fctis[i].getAmountTotal().toString();
			}
				
		}

		String limitallowed = utility.getParameterValue(
				"CashThresholdAmount", "50000.00");
		double tf = Double.parseDouble(tillFloat);
		double cta = Double.parseDouble(limitallowed);
		if (tf >= cta)
			showDialog = true;
		
		if (showDialog && !offline) {

				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("cashthresholdamounterror");
				model.setType(DialogScreensIfc.ERROR);

				 model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.OK);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		else {
	
        // validate input fields prior to execute transaction
        if (inquiry.getDescription() == null &&
                inquiry.getItemID() == null &&
                inquiry.getPosItemID() == null &&
                inquiry.getItemNumber() == null &&
                inquiry.getManufacturer() == null)
        {
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            cargo.setInvalidField(ITEM_DESC_FIELD);
            cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else if ( !(inquiry.isSearchItemByItemID() && isValidItemNumber(inquiry.getItemID()) ||
                   (inquiry.isSearchItemByPosItemID() && isValidItemNumber(inquiry.getPosItemID())) ||
                   (inquiry.isSearchItemByItemNumber()&& isValidItemNumber(inquiry.getItemNumber()))) )
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            if (!isValidField(inquiry.getDescription()))
            {
                // check if the search critera is valid
                cargo.setInvalidField(ITEM_DESC_FIELD);
            }
            if (!isValidField(inquiry.getManufacturer()))
            {
                // check if the search critera is valid
                cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            }
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getDescription()))
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_DESC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getManufacturer()))
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else
        {
            // retrieve item with USER_INTERFACE locale only
            inquiry.setLocaleRequestor(LocaleMap.getSupportedLocaleRequestor());
            inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
            inquiry.setPLURequestor(new PLURequestor());
            /*letter = CommonLetterIfc.SEARCH;*/
            if(isHotKeyValid(inquiry.getItemNumber()))
            	letter = getItems(bus,cargo, inquiry);
            else
            	letter = CommonLetterIfc.SEARCH;
        }

        // Proceed to the next site
				if (letter != null) {
					bus.mail(new Letter(letter), BusIfc.CURRENT);
				}
			}
		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance::";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
    	
    	// <!-- MAX Rev 1.1 Change : end -->
    }

    /**
     * Returns a boolean, validates the field
     * 
     * @param the string data
     * @return boolean
     */
    public boolean isValidField(String data)
    {
        // isValid returns false if only a wild character is sent as data
        // true other wise.
        boolean isValid = true;

        // when using wild card search at least one character must be used
        // with the wildcard.This is to narrow the search
        if (data != null && data.equals("%"))
        {
            isValid = false;
        }
        return (isValid);
    }

    /**
     * Returns a boolean, validates the item number field
     * 
     * @param the string data
     * @return boolean
     */
    public boolean isValidItemNumber(String data)
    {
        // isValid returns false if only a wild character is sent as data
        // or only spaces were given in the item number field, true other wise.
        boolean isValid = true;

        if (data == null || data.equals("%"))
        {
            isValid = false;
        }
        return (isValid);
    }
    
 // <!-- MAX Rev 1.1 Change : Start -->
 	protected boolean isSystemOffline(UtilityIfc utility) {
 		DispatcherIfc d = Gateway.getDispatcher();
 		DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
 		boolean offline = true;
 		try {
 			if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
 					|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
 				offline = false;
 			}
 		} catch (DataException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		return offline;

 	}
 	// <!-- MAX Rev 1.1 Change : end -->
 	
 	/**
	 * Check the kit item is authorized for sale
	 * <p>
	 * 
	 * @param itemKit
	 *            ItemKit
	 * @return boolean true if it is authorized, otherwise return false
	 */
	// ----------------------------------------------------------------------
	protected boolean isItemKitAuthForSale(ItemKit itemKit) {
		KitComponentIfc kitComponents[] = itemKit.getComponentItems();
		if (kitComponents != null) {
			for (int i = 0; i < kitComponents.length; i++) {
				if (kitComponents[i].isKitHeader()) {
					if (!isItemKitAuthForSale((ItemKit) kitComponents[i])) {
						return false;
					}
				} else {
					if (!isItemAuthForSale(kitComponents[i])) {
						return false;
					}
				}
			}
		}
		return true;
	}
 	
	/**
	 * Returns a flag indicating whether item has active MRP's
	 * 
	 * @param pluItem
	 * @return boolean
	 * @since 12.0.9IN
	 */
	// ----------------------------------------------------------------------
	/*protected boolean hasActiveMRPs(PLUItemIfc pluItem) {
		boolean hasActiveMRPs = false;
		if (pluItem.getActiveMaximumRetailPriceChanges() != null) {
			hasActiveMRPs = pluItem.getActiveMaximumRetailPriceChanges().length > 0 ? true
					: false;
		}
		return hasActiveMRPs;

	}*/
	
	/**
	 * Check the item is authorized for sale
	 * <p>
	 * 
	 * @param pluItem
	 *            PLUItemIfc
	 * @return boolean true if it is authorized, otherwise return false
	 */
	// ----------------------------------------------------------------------
	protected boolean isItemAuthForSale(PLUItemIfc pluItem) {
		ItemClassificationIfc classification = pluItem.getItemClassification();
		if (classification != null && !classification.isAuthorizedForSale()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a list of items matching the search criteria
	 * 
	 * @param bus
	 * @param cargo
	 * @param inquiry
	 *            the inquiry search criteria
	 * @return String The letter to be sent
	 **/
	// ----------------------------------------------------------------------
	public String getItems(BusIfc bus, MAXItemInquiryCargo cargo,
			SearchCriteriaIfc inquiry) {
		// letter to be sent
		String letter = null;
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

		boolean hotKeysFlag = false;
		try {
			//System.out.println("384 ::"+cargo.getEmpID());
			PLUTransaction pluTransaction = null;
			pluTransaction = (PLUTransaction) DataTransactionFactory
					.create(DataTransactionKeys.PLU_TRANSACTION);
			// get list of items matching search criteria from database
			//System.out.println("390 ::");
			inquiry.setMaximumMatches(getMaximumMatches(bus));
			Locale locale = LocaleMap
					.getLocale(LocaleConstantsIfc.USER_INTERFACE);
			// commented as setLocale method is not present in 14 version of POS
			//inquiry.setLocale(locale);
			// set storeID
			inquiry.setStoreNumber(cargo.getRegister().getWorkstation()
					.getStoreID());

			// <!-- MAX Rev 1.0 Change : Start -->
			boolean isNotUPCItem = false;
			PLUItemIfc[] pluItems = null;
			PLUItemIfc[] isUPCItem = null;
			BigDecimal decimalWeight = null;
			//Commenting as in Version 14 item id is saved in item number and not in item id
			//String barItemID = inquiry.getItemID();
			String barItemID = inquiry.getItemNumber();
			try {
				isUPCItem = pluTransaction.getPLUItems((MAXSearchCriteriaIfc) inquiry);
				pluItems = isUPCItem;
			}

			catch (DataException de) {
				isNotUPCItem = true;
			}
			ParameterManagerIfc pm = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			int barCodeLength = 0;
			
			try {
				barCodeLength = pm.getIntegerValue(
						MAXSaleConstantsIfc.WEIGHT_BARCODE_LENGTH).intValue();
			} catch (ParameterException e1) {
				logger.error("" + Util.throwableToString(e1) + "");
			}

			if (barItemID.length() != barCodeLength && isNotUPCItem) {
				if (isHotKeyValid(barItemID))
					hotKeysFlag = true;
				else {
					showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null);
					letter = CommonLetterIfc.INVALID;
					return null;
				}

			}

			try {
				if (barCodeLength != 0
						&& isNotUPCItem
						&& barItemID.length() == pm.getIntegerValue(
								MAXSaleConstantsIfc.WEIGHT_BARCODE_LENGTH)
								.intValue()) {
					String itemNo = "";
					String itemWeight = "";

					try {
						itemNo = barItemID.substring(
								pm.getIntegerValue(
										MAXSaleConstantsIfc.SKU_START_POSITION)
										.intValue() - 1,
								pm.getIntegerValue(
										MAXSaleConstantsIfc.SKU_END_POSITION)
										.intValue());
						itemWeight = barItemID
								.substring(
										pm.getIntegerValue(
												MAXSaleConstantsIfc.WEIGHT_START_POSITION)
												.intValue() - 1,
										pm.getIntegerValue(
												MAXSaleConstantsIfc.WEIGHT_END_POSITION)
												.intValue());

					} catch (ParameterException e) {
						logger.error("" + Util.throwableToString(e) + "");
					}

					try {
						decimalWeight = new BigDecimal(itemWeight);
						decimalWeight = decimalWeight.movePointLeft(3);
					} catch (NumberFormatException e) {
						logger.info("NUMBER FORMAT EXCEPTION DURING ITEM VALIDATION");
						showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null);

					}
					inquiry.setItemNumber(itemNo);
					pluItems = pluTransaction.getPLUItems((MAXSearchCriteriaIfc) inquiry);
					CurrencyIfc currency = DomainGateway
							.getBaseCurrencyInstance();

					if (decimalWeight != null) {
						pluItems[0].setItemWeight(decimalWeight);
						((MAXPLUItemIfc) pluItems[0]).setWeightedBarCode(true);

					}
					if (pluItems.length <= 0) {
						throw new DataException("INFO_NOT_FOUND_ERROR");
						
					}
				}
				// <!-- MAX Rev 1.0 Change : end -->
				else {

					pluItems = isUPCItem;
				}
			} catch (ParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList list = new ArrayList();
			ArrayList errors = new ArrayList();
			boolean inActiveMRP = false;
			// Ensure the item is actually authorized for sale. Per "Sale" func
			// req 3/2004.
			if (pluItems != null) {
				for (int i = 0; i < pluItems.length; i++) {
					// if related item, we dont care about nonsaleable
					if (cargo.isRelatedItem()) {
						list.add(pluItems[i]);
					} else if (pluItems[i].isKitHeader()) {
						if (!isItemKitAuthForSale((ItemKit) pluItems[i])) {
							errors.add(pluItems[i]);
						}
						/* 12.0.9IN:MMRP related changes starts here */
						// code commented for code merging(commenting below line as getActiveMaximumRetailPriceChanges() for pluitem used in method hasActiveMRPs() is removed in base 14)
						/*else if (!hasActiveMRPs(pluItems[i])) {
							errors.add(pluItems[i]);
							inActiveMRP = true;
						}*/
						/* 12.0.9IN:MMRP related changes ends here */
						else {
							list.add(pluItems[i]);
						}
					} else if (!isItemAuthForSale(pluItems[i])) {
						errors.add(pluItems[i]);
					} else {
						list.add(pluItems[i]);
					}
				}
			}
			// If no good items were found, show an error dialog.
			if (errors.size() > 0 && list.size() < 1) {
				showErrorDialog(bus, false);
			}

			else {

				if (list.size() == 1) {
					cargo.setPLUItem((PLUItemIfc) list.get(0));
					cargo.setItemList(null);
					letter = LETTER_ONE_ITEM_FOUND;
				} else if (list.size() == 0) {
					// If the regular item lookup did return result,
					// still need to do store coupon lookup.
					// letter = CommonLetterIfc.RETRY;
					letter = LETTER_REGULAR_ITEM_NOT_FOUND;
					cargo.setPLUItem(null);
					cargo.setItemList(null);
				} else {
					letter = LETTER_MULTIPLE_ITEMS_FOUND;
					cargo.setItemList((PLUItemIfc[]) list
							.toArray(new PLUItemIfc[0]));
					cargo.setPLUItem(null);
				}
				
				
			}
			
		} 
		
		catch (DataException de) {

			int errorCode = de.getErrorCode();

			logger.warn("ItemNo: " + inquiry.getItemID() + " \nItem Desc: "
					+ inquiry.getDescription() + " \nItem Dept: "
					+ inquiry.getDepartmentID() + "");

			logger.warn("Error: " + de.getMessage() + " \n " + de + "");

			cargo.setDataExceptionErrorCode(errorCode);

			// Don't think that args is used, but...
			String args[] = new String[1];

			// Set the appropriate letter to mail.
			// If the regular item lookup did return result,
			// still need to do store coupon lookup.
			// letter = CommonLetterIfc.RETRY;

			/* India Localization Changes - Item Creation Changes */
			// For Indian Scenario Item Creations hould be disabled in
			// ORPOS.
			showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null);
			/* India Localization Changes - Item Creation Changes */
		}
		if (hotKeysFlag)
			letter = getHotKeySItems(bus, cargo, inquiry);

		return (letter);
	}
 	
	private boolean isHotKeyValid(String hotkey) {
		boolean result = false;
		try {
			Double.parseDouble(hotkey);
			//version 14 upgrade for hot keys
			/*if (hotkey.length() != 2) */
			if (hotkey.length() == 2 || hotkey.length() == 13) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;

	}
	
 	public String getHotKeySItems(BusIfc bus, MAXItemInquiryCargo cargo,
			SearchCriteriaIfc inquiry) {
		// letter to be sent
		String letter = null;
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

		/*try {*/

			MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
			String itemId = null;
			try {
				//COMMENTING AS IN BASE 14 ITEM IS COMING UNDER ITEM NUMBER
				/*itemId = ((MAXHotKeysTransaction) hotKeysTransaction)
						.getItemIdFromHotKey(inquiry.getItemID());*/
				itemId = ((MAXHotKeysTransaction) hotKeysTransaction)
						.getItemIdFromHotKey(inquiry.getItemNumber());
			} catch (DataException e) {
				// TODO Auto-generated catch block
				/**MAX Rev 1.2 Change : Start**/
//				e.printStackTrace();
//				System.out.println(e.toString());
				String rscid = "HotKeyLookupFail";
				showErrorDialog(bus, rscid, null);
				/**MAX Rev 1.2 Change : End**/
			}
			if (itemId != null && ! itemId.equals(""))
			{
				String arr[]= itemId.split(";");
				if(arr.length==1)
				{
					inquiry.setItemNumber(arr[0]);
					letter = CommonLetterIfc.SEARCH;
					//Commented as per base 14 item search mwthod is changed
					
					/*	
					inquiry.setItemID(arr[0]);
					PLUTransaction pluTransaction = null;

					pluTransaction = (PLUTransaction) DataTransactionFactory
							.create(DataTransactionKeys.PLU_TRANSACTION);

					// get list of items matching search criteria from database
					inquiry.setMaximumMatches(getMaximumMatches(bus));
					Locale locale = LocaleMap
							.getLocale(LocaleConstantsIfc.USER_INTERFACE);
					// changes starts for code merging(commenting below line as setLocale() method is removed in base 14)   
					//inquiry.setLocale(locale);
					// set storeID
					inquiry.setStoreNumber(cargo.getRegister().getWorkstation()
							.getStoreID());

					PLUItemIfc[] pluItems = pluTransaction.getPLUItems(inquiry);
					ArrayList list = new ArrayList();
					ArrayList errors = new ArrayList();
					boolean inActiveMRP = false;
					// Ensure the item is actually authorized for sale. Per "Sale" func
					// req 3/2004.
					if (pluItems != null) {
						for (int i = 0; i < pluItems.length; i++) {
							// if related item, we dont care about nonsaleable
							if (cargo.isRelatedItem()) {
								list.add(pluItems[i]);
							} else if (pluItems[i].isKitHeader()) {
								if (!isItemKitAuthForSale((ItemKit) pluItems[i])) {
									errors.add(pluItems[i]);
								}
								 12.0.9IN:MMRP related changes starts here 
								// code commented for code merging(commenting below line as getActiveMaximumRetailPriceChanges() for pluitem used in method hasActiveMRPs() is removed in base 14)   
								else if (!hasActiveMRPs(pluItems[i])) {
									errors.add(pluItems[i]);
									inActiveMRP = true;
								}
								 12.0.9IN:MMRP related changes ends here 
								else {
									list.add(pluItems[i]);
								}
							} else if (!isItemAuthForSale(pluItems[i])) {
								errors.add(pluItems[i]);
							} else {
								list.add(pluItems[i]);
							}
						}
					}
					// If no good items were found, show an error dialog.
					if (errors.size() > 0 && list.size() < 1) {
						showErrorDialog(bus, false);
					}

					else {

						if (list.size() == 1) {
							cargo.setPLUItem((PLUItemIfc) list.get(0));
							cargo.setItemList(null);
							letter = LETTER_ONE_ITEM_FOUND;
						} else if (list.size() == 0) {
							// If the regular item lookup did return result,
							// still need to do store coupon lookup.
							// letter = CommonLetterIfc.RETRY;
							letter = LETTER_REGULAR_ITEM_NOT_FOUND;
							cargo.setPLUItem(null);
							cargo.setItemList(null);
						} else {
							letter = LETTER_MULTIPLE_ITEMS_FOUND;
							cargo.setItemList((PLUItemIfc[]) list
									.toArray(new PLUItemIfc[0]));
							cargo.setPLUItem(null);
						}
					}
				*/}
				else
				{
					showErrorDialog( bus,  "MultipleHotKeyError", null);
				}
			}
			else
			{
				showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null);
			}
			

			
		/*}*/
		//comment catch block as search method is changed in 14 version
		/*catch (DataException de) {
			int errorCode = de.getErrorCode();
			logger.warn("ItemNo: " + inquiry.getItemID() + " \nItem Desc: "
					+ inquiry.getDescription() + " \nItem Dept: "
					+ inquiry.getDepartmentID() + "");

			logger.warn("Error: " + de.getMessage() + " \n " + de + "");

			cargo.setDataExceptionErrorCode(errorCode);

			// Don't think that args is used, but...
			String args[] = new String[1];

			// Set the appropriate letter to mail.
			// If the regular item lookup did return result,
			// still need to do store coupon lookup.
			// letter = CommonLetterIfc.RETRY;

			 India Localization Changes - Item Creation Changes 
			// For Indian Scenario Item Creations hould be disabled in ORPOS.
			showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null);
			 India Localization Changes - Item Creation Changes 
		}*/

		return (letter);
	}
 	
 	/**
	 * Displays error Dialog
	 * 
	 * @param bus
	 */
	// ----------------------------------------------------------------------
	private void showErrorDialog(BusIfc bus, boolean inActiveMRP) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		// Using "generic dialog bean". display the error dialog
		DialogBeanModel model = new DialogBeanModel();
		/* 12.0.9IN:Changes related to India L10N starts here */
		if (!inActiveMRP) {
			model.setResourceID("ItemNotAuthForSale");
		} else {
			model.setResourceID("ItemNoActiveMRPForSale");
		}
		/* 12.0.9IN:Changes related to India L10N ends here */
		model.setType(DialogScreensIfc.ERROR);
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
 	
 	/**
	 * Displays error Dialog
	 * 
	 * @param bus
	 */
	// ----------------------------------------------------------------------
	private void showErrorDialog(BusIfc bus, String id, String[] args) {
		POSUIManagerIfc ui;
		ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// Using "generic dialog bean". display the error dialog

		DialogBeanModel model = new DialogBeanModel();

		model.setResourceID(id);
		if (args != null) {
			model.setArgs(args);
		}
		model.setType(DialogScreensIfc.ERROR);
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
	
	/**
	 * Returns an Integer, the maximum matches allowed from the parameter file.
	 * <P>
	 * 
	 * @param bus
	 * @return int maximum matches allowed as Integer
	 */
	// ----------------------------------------------------------------------
	private int getMaximumMatches(BusIfc bus) {
		// get paramenter manager
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);

		// maximum number of matches allowed
		Integer maximum = new Integer("100"); // default
		try {
			String s = pm.getStringValue(ITEM_MAXIMUM_MATCHES);
			s.trim();
			maximum = new Integer(s);
			if (logger.isInfoEnabled())
				logger.info("Parameter read: " + ITEM_MAXIMUM_MATCHES + " = ["
						+ maximum + "]");
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		return (maximum.intValue());
	}
	
	

}

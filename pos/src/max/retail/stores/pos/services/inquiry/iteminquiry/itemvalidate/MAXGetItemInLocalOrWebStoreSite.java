/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * Rev 1.1   May 04, 2017	        Kritica Agarwal  GST Changes
 * Rev 1.0   Feb 07,2017    		Ashish Yadav     Changes for Item not found bug
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import java.util.ArrayList;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveLiquidationTransaction;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.liquidationreport.MAXLiquidationReport;
import max.retail.stores.domain.manager.item.MAXItemManagerIfc;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKit;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXGetItemInLocalOrWebStoreSite extends PosSiteActionAdapter
{
    
    private static final long serialVersionUID = -686797107976490489L;

    /**
     * Letter for one item found
     */
    public static final String LETTER_ONE_ITEM_FOUND = "OneItemFound";
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    public static String barCodeNo = null;

    public void arrive(BusIfc bus)
    {
        // letter to be sent
        String   letter  = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
       
        // get item inquiry from cargo
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        //System.out.println("MAXItemInquiryCargo 56:"+cargo.toString());
        MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) cargo.getInquiry();
        
        cargo.resetInvalidFieldCounter();
        //System.out.println("MAXSearchCriteriaIfc 59:"+inquiry.toString());
        ArrayList<PLUItemIfc> errors = new ArrayList<PLUItemIfc>();
        //System.out.println("MAXGetItemInLocalOrWebStoreSite 59 ::"+cargo.getEmpID());
        //MAXSaleCargo cargo1 = (MAXSaleCargo)bus.getCargo(); 
        //System.out.println("MAXGetItemInLocalOrWebStoreSite :"+cargo.getEmpID());

        try
        {
        	//Change for Rev 1.1 : Starts
        	MAXItemManagerIfc mgr = (MAXItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
        
			/*
			 * if(cargo.getEmpID()) { System.out.println("69 insideIf ::");
			 * cargo.getPLUItem().setEmpID(cargo.getEmpID());
			 * System.out.println("71 insideIf :"+cargo.getPLUItem().getEmpID()); }
			 */
          //  MAXItemManagerIfc mgr = (MAXItemManagerIfc)bus.getManager(MAXItemManagerIfc.TYPE);
          //Change for Rev 1.1 : Ends
        	
            boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalPLULookup", false);
            inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
            //Change for Rev 1.1 : Starts
        	inquiry.setInterStateDelivery(((MAXItemInquiryCargo)cargo).getInterStateDelivery());
        	if(((MAXItemInquiryCargo)cargo).getInterStateDelivery()){
        		inquiry.setFromRegion(((MAXItemInquiryCargo)cargo).getFromRegion());
        		inquiry.setToRegion(((MAXItemInquiryCargo)cargo).getToRegion());
        	}
        	//System.out.println("============+90::"+inquiry.getEmpID());
        	inquiry.setEmpID(cargo.getEmpID());
        	//System.out.println("============+92::"+inquiry.getEmpID());
        	MAXPLUItemIfc pluItem = new MAXPLUItem();
			/*
			 * if(cargo.getNecBarCode().startsWith("3")) {
			 * pluItem.setBarCodePriceFlag(true); inquiry.setBarCodePriceFlag(true);
			 * 
			 * }
			 */
        	//System.out.println(cargo.getLiqBarCode());
        	if(barCodeNo == null || barCodeNo=="") {
        		barCodeNo = cargo.getNecBarCode();
        	}
        	String barCodeSUbString=null;;
        	if(barCodeNo.length()>10 && barCodeNo.length()==18) {
        		barCodeSUbString = barCodeNo.substring(1, 10);
        	}else {
        		System.out.println("barCodeSUbString 115:"+barCodeNo);
        		//System.out.println("barCodeSUbString 116::"+barCodeNo);
        		barCodeSUbString = barCodeNo;
        		
        	}
        		if(barCodeSUbString.equalsIgnoreCase(inquiry.getItemNumber())) {
        	if(barCodeNo.startsWith("3") && barCodeNo.length()==18)
        			
        	{
        		if(cargo.getNecBarCode().startsWith("3") && cargo.getNecBarCode().length()==18) {
        		pluItem = (MAXPLUItemIfc) mgr.getPluItem(inquiry);
        		
        		String newPrice = null;
        		String decimal = null;
        		
        		
        			newPrice = cargo.getNecBarCode().substring(10,15);
        			System.out.println("newPrice :" +newPrice);
        			decimal =cargo.getNecBarCode().substring(16,18);
        			System.out.println("decimal :" +decimal);
        	
        		CurrencyIfc ovrPrice = DomainGateway.getBaseCurrencyInstance(newPrice+"."+decimal);
        		System.out.println("ovrPrice :" +ovrPrice);	
        		pluItem.setPrice(ovrPrice);
            	pluItem.clearAdvancedPricingRules();
            	//added for liquidation barcode report by Vaibhav
            	MAXLiquidationReport liquidationReport= new MAXLiquidationReport();
            	liquidationReport.setLiquidationbarcode(cargo.getNecBarCode().toString());
            	liquidationReport.setBusinessDay(cargo.getRegister().getBusinessDate());
            	liquidationReport.setItemprice(ovrPrice.toString());
            	liquidationReport.setStoreID(cargo.getRegister().getWorkstation().getStoreID());
            	liquidationReport.setTransactionID(Integer.toString(cargo.getRegister().getLastTransactionSequenceNumber()));
            	liquidationReport.setWsID(cargo.getRegister().getWorkstation().getWorkstationID());
            	System.out.println("line 153"+cargo.getRegister().getWorkstation().getWorkstationID());
            	liquidationReport.setItemId(cargo.getNecBarCode().substring(1,10));
            	
            	MAXSaveLiquidationTransaction dbTrans = null;
            	dbTrans = (MAXSaveLiquidationTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_LIQUIDATION_REPORT_TRANSACTION);
            	

            	
            	try {
            		dbTrans.saveLiquidationItem(liquidationReport);
            	} catch (Exception e) {
            		// TODO Auto-generated catch block
            		e.printStackTrace();
            	
            	}
					/*
					 * 
					 * else { pluItem = (MAXPLUItemIfc) mgr.getPluItem(inquiry); }
					 */
						 
        		}
        	else if(!(cargo.getNecBarCode().startsWith("3")) || cargo.getNecBarCode().length()!=18)
        	{
        		pluItem = (MAXPLUItemIfc) mgr.getPluItem(inquiry);
        	}
        }else {
        	barCodeNo = cargo.getNecBarCode();
        	if(barCodeNo.startsWith("3") && barCodeNo.length()==18)
    	{
    		pluItem = (MAXPLUItemIfc) mgr.getPluItem(inquiry);
    		String newPrice = null;
    		String decimal = null;
    			newPrice = cargo.getNecBarCode().substring(10,15);
    			System.out.println("newPrice :" +newPrice);
    			decimal =cargo.getNecBarCode().substring(16,18);
    			System.out.println("decimal :" +decimal);
    		CurrencyIfc ovrPrice = DomainGateway.getBaseCurrencyInstance(newPrice+"."+decimal);
    		System.out.println("ovrPrice :" +ovrPrice);	
    		pluItem.setPrice(ovrPrice);
        	pluItem.clearAdvancedPricingRules();
        	//added for liquidation barcode report by Vaibhav
        	MAXLiquidationReport liquidationReport= new MAXLiquidationReport();
        	liquidationReport.setLiquidationbarcode(cargo.getNecBarCode().toString());
        	liquidationReport.setBusinessDay(cargo.getRegister().getBusinessDate());
        	liquidationReport.setItemprice(ovrPrice.toString());
        	liquidationReport.setStoreID(cargo.getRegister().getWorkstation().getStoreID());
        	liquidationReport.setTransactionID(Integer.toString(cargo.getRegister().getLastTransactionSequenceNumber()));
        	liquidationReport.setWsID(cargo.getRegister().getWorkstation().getWorkstationID());
        	System.out.println("line 203"+cargo.getRegister().getWorkstation().getWorkstationID());
        	liquidationReport.setItemId(cargo.getNecBarCode().substring(1,10));
        	
        	MAXSaveLiquidationTransaction dbTrans = null;
        	dbTrans = (MAXSaveLiquidationTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_LIQUIDATION_REPORT_TRANSACTION);
        	

        	
        	try {
        		dbTrans.saveLiquidationItem(liquidationReport);
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	
        	}
    	}
    	else if(!(cargo.getNecBarCode().startsWith("3")) || cargo.getNecBarCode().length()!=18)
    	{
    		pluItem = (MAXPLUItemIfc) mgr.getPluItem(inquiry);
    	}
        }
            // If this tour was launched to retrieve a related item, do not make
            // the check for saleable status 
        	//cargo.getNecBarCode();
        	System.out.println(cargo.getNecBarCode());        	
			
        	System.out.println(pluItem.getPrice());
            if (!cargo.isRelatedItem())
            {
                if (pluItem.isKitHeader())
                {
                    if (!isItemKitAuthForSale((ItemKit)pluItem))
                    	
                    {
                        errors.add(pluItem);
                    }
                }
                else if (!isItemAuthForSale(pluItem))
                {
                    errors.add(pluItem);
                }
            }
            
            // If there are errors, display the dialog
            if (errors.size() > 0)
            {
                showErrorDialog(bus);
                barCodeNo =null;
            }
            else
            {
                cargo.setItemFromWebStore(!inquiry.isRetrieveFromStore());
                cargo.setPLUItem(pluItem);
                cargo.setItemList(null);
                letter = LETTER_ONE_ITEM_FOUND;
                barCodeNo =null;
            }
        }
        }
        catch (DataException de)
        {
            logger.warn("ItemNo: "    + inquiry.getItemNumber() + 
                     " \nItem Desc: " + inquiry.getDescription() + 
                     " \nItem Dept: " + inquiry.getDepartmentID() + "", de);

            cargo.setDataExceptionErrorCode(de.getErrorCode());
            barCodeNo =null;
            //Change for Rev 1.1 :Starts
            if(de.getMessage().equalsIgnoreCase("HSN number not found")){
            	letter =  "HSNnotFound";
            }else{
            //Change for Rev 1.1 :Ends
            switch(de.getErrorCode())
            {
                case DataException.ADVANCED_PRICING_INFO_NOT_FOUND_ERROR:
                    letter = CommonLetterIfc.DATERANGE;
                    break;
                    
                case DataException.NO_DATA:
                    
                    // If no item was found determine the next step.
                    boolean isOffLine    = isOffLine(de.getErrorCodeExtended());
                    boolean isXChannel   = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "XChannelEnabled", false);
                    boolean isSimEnabled = isSimLookupEnabled(bus);
                    barCodeNo =null;
                    // If cross channel is enabled...
                    if (isXChannel)
                    {
                        // and the server is online and the inquiry was looking at the local store...
                        if (!isOffLine && inquiry.isRetrieveFromStore())
                        {
                            // If SIM lookup is enabled, add the Serial Number button to the dialog.
                            if (isSimEnabled)
                            {
                                showWebOrInventorySearchDialog(bus, inquiry);
                            }
                            else // Otherwise, just add the WebStore and Cancel buttons.
                            {
                                showWebSearchDialog(bus, inquiry);
                            }
                        }
                        else
                        {
                            letter =  CommonLetterIfc.RETRY;
                        }
                    }
                    else
                    {
                        // Okay, no Cross Channel, so check for SIM lookup
                        if (isSimEnabled && !isOffLine)
                        {
                            letter =  CommonLetterIfc.SERIAL_NUMBER;
                        }
                        else
                        {
                        	// Changes starts for Rev 1.0 (Ashish : Items not found error)
                        	//showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null, CommonLetterIfc.RETRY);
                            letter =  CommonLetterIfc.RETRY;
                        	// Changes ends for Rev 1.0 (Ashish : Items not found error)
                        }
                    }                
                    break;
                    
                default:
                    String msg[] = new String[1];
                    msg[0] = utility.getErrorCodeString(de.getErrorCode());
                    barCodeNo =null;
                    showErrorDialog(bus,"DatabaseError",msg, "Invalid");
                    break;
            }
        }
        }
        finally
        {
            inquiry.setRetrieveFromStore(true);
        }
        
        /*
         * Proceed to the next site
         */
        if (letter !=null)
        {
           bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays error Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showErrorDialog(BusIfc bus, String id, String[] args, String letter)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(id);
        if (args != null)
        {
           model.setArgs(args);
        }
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * Displays error Dialog
     * 
     * @param bus
     */
    private void showErrorDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotAuthForSale");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays webService Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebSearchDialog(BusIfc bus, SearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotFoundInStore");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.RETRY);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Displays webService Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebOrInventorySearchDialog(BusIfc bus, SearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("SearchWebstoreSimInquiry");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEB_SIM_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_SIM, CommonLetterIfc.SERIAL_NUMBER);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.RETRY);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * Check the kit item is authorized for sale
     * 
     * @param itemKit ItemKit
     * @return boolean true if it is authorized, otherwise return false
     */
    protected boolean isItemKitAuthForSale(ItemKit itemKit)
    {
        KitComponentIfc kitComponents[] = itemKit.getComponentItems();
        if (kitComponents != null)
        {
            for (int i = 0; i < kitComponents.length; i++)
            {
                if (kitComponents[i].isKitHeader())
                {
                    if (!isItemKitAuthForSale((ItemKit)kitComponents[i]))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!isItemAuthForSale(kitComponents[i]))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check the item is authorized for sale
     * 
     * @param pluItem PLUItemIfc
     * @return boolean true if it is authorized, otherwise return false
     */
    protected boolean isItemAuthForSale(PLUItemIfc pluItem)
    {
        ItemClassificationIfc classification = pluItem.getItemClassification();
        if (classification != null && !classification.isAuthorizedForSale())
        {
            return false;
        }
        
        return true;
    }

    /**
     * Determines if extended code indicates the repository is off line.
     * @param extendedCode
     * @return true if off line
     */
    protected boolean isOffLine(int extendedCode)
    {
        boolean isOffLine = false;
        if (DataException.ERROR_CODE_EXTENDED_OFFLINE == extendedCode)
        {
            isOffLine = true;
        }
        
        return isOffLine;
    }
    
    /**
     * Checks whether IMEI is enalbed or not
     * @param bus
     * @return true if SIM lookup is enabled.
     */
    protected boolean isSimLookupEnabled(BusIfc bus)
    {

        boolean result = false;
        boolean IMEIEnabled = false;
        boolean serialisationEnabled = false;
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        IMEIEnabled = utility.getIMEIProperty();
        serialisationEnabled = utility.getSerialisationProperty();
        if(IMEIEnabled && serialisationEnabled)
        {
            result=true;
        }
        else
        {
            result=false;
        }
        return (result);
    }
    
}

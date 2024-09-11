package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.ItemNumberEnteredAisle;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

import org.apache.log4j.Logger;

public class MAXItemNumberEnteredAisle extends ItemNumberEnteredAisle {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.sale.MAXItemNumberEnteredAisle.class);

	  public void traverse(BusIfc bus)
	    {
	        Letter letter = null;
	        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

	        // Get the user input
	        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	        PromptAndResponseModel parModel =
	            ((POSBaseBeanModel) ui.getModel(POSUIManagerIfc.SELL_ITEM)).getPromptAndResponseModel();
	        String itemID = parModel.getResponseText();
	        
			logger.info("before the IF condition item id" +itemID);
	        MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
			logger.info("before the IF condition" +cargo.getSelectedScanSheetItemID());
			if(cargo.getSelectedScanSheetItemID() != null && cargo.getSelectedScanSheetItemID() != ""){
				 logger.info("Inside the IF condition" +cargo.getSelectedScanSheetItemID());
				itemID = cargo.getSelectedScanSheetItemID();
				cargo.setSelectedScanSheetItemID("");
			}
			cargo.setNecBarCode(itemID);
			cargo.setLiqBarCode(itemID);
			
	        
	        if (itemID != null && itemID.equals("4444")) {
	            cargo.setNFCScan(true);
	            bus.mail("LastItem", BusIfc.CURRENT);
	          } else if (itemID != null && itemID.equals("5555")) {
	            cargo.setNFCScan(false);
	            showErrorDialog(ui);
	          }
	        else {
	        // Check Digit if not in training mode
	        if (cargo.getRegister().getWorkstation().isTrainingMode() == false && 
	            utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_ITEMNUMBER, itemID) == false)
	        {
	            showInvalidNumberDialog(utility, ui);
	        }
	        else
	        {
	        	boolean isScanned = false;
	        	String itemNumber = null;
	            // Store the item number in the cargo
	        	if(cargo.getSingleBarCodeData()!=null)
	        	{
	        		SingleBarCodeData data = cargo.getSingleBarCodeData();
	        		itemNumber = data.getItemId();
					isScanned= true;
	        	}
	        	else
	        	{
					if (itemID == null) {
						showInvalidNumberDialog(utility, ui);
					} else if (itemID.length() == 18 && itemID.startsWith("99")) {
						itemNumber = itemID.substring(2, 11);
						// System.out.println("<----- itemID.length() == 18 ----> Item Id   "+itemNumber);

					} else {
						itemNumber = itemID;
						// System.out.println("<----- itemID.length() == 18 ----> Item Id   "+itemNumber);
					}

					isScanned = parModel.isScanned();
				}

	            cargo.setPLUItemID(itemNumber);
	            cargo.setItemScanned(isScanned);

	            letter = new Letter(CommonLetterIfc.VALID);
	        }

	        if (letter != null)
	        {
	            bus.mail(letter, BusIfc.CURRENT);
	        }
	    }
	    }
	  
	  private void showErrorDialog(POSUIManagerIfc ui)
	  {
	    DialogBeanModel dialogModel = new DialogBeanModel();
	    dialogModel.setResourceID("NCF_CARD_LAST_ITEM");
	    dialogModel.setType(1);
	    dialogModel.setButtonLetter(0, "LastItem");
	    ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	  }
}
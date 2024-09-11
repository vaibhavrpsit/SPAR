/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	22-Oct-2018		Jyoti Yadav		LS Edge Phase 2	
*
********************************************************************************/
package max.retail.stores.pos.services.customer.edge;

import java.util.HashMap;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.domain.utility.MAXGSTUtility;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXEnrolEdgeCustomerSite extends PosSiteActionAdapter {
	
	private static final long serialVersionUID = 5389913639680162857L;
	MAXCRMCustomerStatusUtility customerSearchUtility= MAXCRMCustomerStatusUtility.getInstance();
	public void arrive(BusIfc bus) {
		HashMap requestAttributes;
		String response = "";
		boolean edgeItemPresent = false;
		boolean edgeItemScanned = false;
		String edgeItemNumScanned = "";
		String saleItemId = "";
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXConfigParametersIfc config = MAXGSTUtility.getConfigparameter();
		// Validate edge item is present in transaction
		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){
			for(int i=0; i<((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItems().length; i++){
				if(((MAXSaleReturnLineItem)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItems()[i]).isEdgeItem()){
					edgeItemPresent = true;
					//break;
				}else{
					if(saleItemId != null && saleItemId.equalsIgnoreCase("")){
						saleItemId = ((MAXSaleReturnLineItem)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItems()[i]).getItemID();	
					}else if(saleItemId != null && !saleItemId.equalsIgnoreCase("")){
						saleItemId = saleItemId + "," + ((MAXSaleReturnLineItem)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItems()[i]).getItemID();	
					}
				}
			}
		}
		
		if(config != null && config.getEdgeItemValues() != null && !config.getEdgeItemValues().equalsIgnoreCase("")
				&& !config.getEdgeItemValues().equalsIgnoreCase("null") && saleItemId != null && !saleItemId.equalsIgnoreCase("")){
			String saleItems[] = saleItemId.split(",");
			String configuredItems[] = config.getEdgeItemValues().split(",");
			for(int i=0; i<saleItems.length; i++){
				for(int j=0; j<configuredItems.length; j++){
					if(saleItems[i].contains(configuredItems[j])){
						edgeItemScanned = true;
						if(edgeItemNumScanned != null && edgeItemNumScanned.equalsIgnoreCase("")){
							edgeItemNumScanned = saleItems[i];
						}else{
							edgeItemNumScanned = edgeItemNumScanned + "," + saleItems[i];
						}
						break;
					}	
				}
			}
		}
		
		boolean flag = false;
		Vector<AbstractTransactionLineItemIfc> lines =  ((SaleReturnTransactionIfc) cargo.getTransaction()).getItemContainerProxy().getLineItemsVector();
		for (int y = 0; y < lines.size(); y++) {
			if (lines.get(y) != null) {
				PLUItemIfc item = ((SaleReturnLineItem) lines.get(y)).getPLUItem();
				if(item instanceof MAXPLUItemIfc) {
					if(((MAXPLUItemIfc) item).isEdgeItem()) {
						flag = true;
					}
				}
			}
		}
		
		if(flag) {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = edgeItemNumScanned;
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID("ENROL_EDGE_ERROR");
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}else if(edgeItemPresent){
			customerSearchUtility.displayErrorDialogMessage(bus, "EDGE_ITEM_ALREADY_ADDED", "Failure");
			return;
		}else if(edgeItemScanned){
			//Display error message for item added through sale screen and not Enrol Edge functionality
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = edgeItemNumScanned;
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID("ENROL_EDGE_ERROR");
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}else{
			//customerSearchUtility.lmrCustomerValidationStatus(bus);
			
			// Request CRM to validate customer status
			requestAttributes = new HashMap();
			MAXTICCustomerIfc ticCustomer = cargo.getTICCustomer();
			requestAttributes = customerSearchUtility.populateValidationHashMap(bus, requestAttributes);
			String urlParameters = customerSearchUtility.createValidationURL(requestAttributes, bus);
			String URL = Gateway.getProperty("application", "searchEdgeURL", null);
			logger.info("URL of Search Edge API is " + URL);
			logger.info("Request of Search Edge API is " + urlParameters);
			// Call CRM API
			response = customerSearchUtility.executeValidationPost(URL, urlParameters);
			//response = customerSearchUtility.dummyValidationResponse(urlParameters);
			
			// Process response and navigate accordingly
			processValidationSuccessResponse(bus, response);	
		}
	}
	
	protected void processValidationSuccessResponse(BusIfc bus, String response) {
		MAXCustomerMainCargo mainCargo = (MAXCustomerMainCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String result = "";
		String resultCode = "";
		String message = "";
		String trimString = response.toString().trim();
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(trimString);
			if (jsonObject.get("result") != null)
				result = (String) jsonObject.get("result").toString();
			if (jsonObject.get("resultCode") != null)
				resultCode = (String) jsonObject.get("resultCode");
			if (jsonObject.get("message") != null)
				message = (String) jsonObject.get("message");
		} catch (Exception e) {

		}
		if (response.equalsIgnoreCase("Timeout")) {
			customerSearchUtility.displayConnectionErrorDialogMessage(bus, "CRM_CONNECTIVITY_ERROR", "Failure");
			return;
		} else {
			if (result.trim().equalsIgnoreCase("true") && resultCode.trim().equalsIgnoreCase("SUCCESS")) {
				//mainCargo.setTicCustomerUpdateTier(message.trim());
				if(message.trim().equalsIgnoreCase("EDGE")){
					customerSearchUtility.displayErrorDialogMessage(bus, "EDGE_CUSTOMER", "Failure");
				}else if(message.trim().equalsIgnoreCase("NON-EDGE")){
					POSBaseBeanModel model = new POSBaseBeanModel();
					ui.showScreen(MAXPOSUIManagerIfc.ENTER_EDGE_ITEM, model);
					return;
				}
			} else {
				// in other cases
				customerSearchUtility.displayCRMResponseError(bus, message.trim(), "CRM_RESPONSE_ERROR", "Failure");
			}
		}
	}

}

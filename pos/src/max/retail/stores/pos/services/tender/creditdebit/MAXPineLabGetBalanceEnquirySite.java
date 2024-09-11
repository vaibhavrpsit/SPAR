/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *   
 * Rev 1.0  	Dec 08, 2014  	Shavinki/Deepshikha     Resolution for LSIPL-FES:-Multiple Tender using Innoviti  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.edc.pinelab.CallingOnlinePineLabDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site invokes Balance Enquiry Dialog.
 */
public class MAXPineLabGetBalanceEnquirySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1L;
    public void arrive(BusIfc bus)
	{
    	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
    	String transactionTime = "2012-07-21T13:55:58.0Z";
		String amountString = BigDecimalConstants.ONE_AMOUNT.multiply(new BigDecimal("100.00")).intValue() + "";
		String invoiceNumber = "123"; // no need in sale
    	HashMap responseMap = null;
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel beanModel = new POSBaseBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, beanModel);
		CallingOnlinePineLabDebitCardTender edcObj = new CallingOnlinePineLabDebitCardTender();

		try 
		{
//			responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(), amountString, invoiceNumber, transactionTime,
//						"15", "1");
			responseMap = edcObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
					amountString, invoiceNumber, transactionTime, "0", "10",MAXPineLabTransactionConstantsIfc.PLUTUS_POINTS_INQUIRY_TRANSACTION_TYPE,null);

			if (responseMap != null && !responseMap.isEmpty())
			{
				String hostResponseCode = (String)responseMap.get("HostResponse");
				if (hostResponseCode.equals("APPROVED"))
				{
					DialogBeanModel dialogModel = new DialogBeanModel();
			        dialogModel.setResourceID("LoyaltyPointsInfo");
			        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "OnlineCredit");
			        String args[] = new String[6];
			        args[0] ="Loyalty Card Number:";
		    	    args[1] = (String)responseMap.get("CardNumber");
		    	    args[2] ="Available Loyalty Points:";
		    	    args[3] = (String)responseMap.get("Points");
		    	    args[4] ="Equivalent INR value:";
		    	    args[5] = (String)responseMap.get("StateAmount");
			        dialogModel.setArgs(args);
		        	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);   	
		        	
				}
				else
				{
					showDialogBoxMethod(responseMap,bus,"OnlineCredit");
				}
			
			}
			else{
				responseMap.put("HostResponseMessage", "Unable to connect with EDC");
				showDialogBoxMethod(responseMap,bus,"OnlineCredit");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	   }   
    
    public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[6];
		dialogModel.setResourceID("RESPONSE_DETAILS");
		msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
		msg[1] = "Your Credit/Debit Card has been Swiped";
		msg[2] = " Response Code Returned Is ";
		if(responseMap!=null){
		if(responseMap.get("HostResponseMessage")!=null)
		msg[3] = responseMap.get("HostResponseMessage").toString();
		}
		msg[4] = "Press ENTER To Proceed / Using another Tender";
		msg[5] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}

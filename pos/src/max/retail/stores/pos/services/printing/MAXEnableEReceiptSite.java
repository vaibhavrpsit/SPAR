/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 05, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;

/**
@author Purushotham Reddy Sirison
**/

import max.retail.stores.pos.receipt.blueprint.MAXParameterConstantsIfc;
import max.retail.stores.pos.services.common.MAXCommonLetterIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXEnableEReceiptSite extends PosSiteActionAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
    {
        MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
        TenderableTransactionIfc trans = cargo.getTransaction();
        boolean gstCustomerInvoice = false;
        if(trans instanceof MAXSaleReturnTransactionIfc) {
        	MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc)trans;
        	if(transaction.getGSTINNumber() != null && !transaction.getGSTINNumber().isEmpty()) {
        		gstCustomerInvoice = true;
        	}
        }
        
        boolean eReceiptConfig = false;
        try{
        	eReceiptConfig = pm.getBooleanValue(MAXParameterConstantsIfc.ENABLEERECEIPT);
        }
        catch (ParameterException e){
            logger.error("Could not find the parameter "+ MAXParameterConstantsIfc.ENABLEERECEIPT +" : ", e);
        }

        if (eReceiptConfig && trans.getTransactionType()== TransactionIfc.TYPE_SALE && !gstCustomerInvoice ){
        	DialogBeanModel model=new DialogBeanModel();
    		model.setType(DialogScreensIfc.CONFIRMATION);
    		model.setResourceID("EReceiptConfirmationDialog");
    		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, MAXCommonLetterIfc.SENDERECEIPT);
    		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, MAXCommonLetterIfc.PRINT);
    		
    		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else {
            bus.mail(MAXCommonLetterIfc.PRINT, BusIfc.CURRENT);
        }
       
    }
}

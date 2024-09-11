//changes by shyvanshu mehra....

package max.retail.stores.pos.services.tender;

import java.math.BigDecimal;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCashValidationSite extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5688628480985131996L;
	private static final String CASH_LIMIT_PARAMETER = null;

	public void arrive(BusIfc bus) {
	
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
	//DialogBeanModel model = new DialogBeanModel();
	//POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	BigDecimal customerDetails;
	
	MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
			.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
	
	//cargo.getCurrentTransactionADO().getBalanceDue();
	
	//System.out.println("check value===="+cargo.getCurrentTransactionADO().getBalanceDue());
	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	DataInputBeanModel model1 =new DataInputBeanModel();
	
	POSBaseBeanModel beanmodel = (POSBaseBeanModel) ui.getModel("MCOUPON_PHONE_NUMBER");
	String k = ui.getInput();
	//int customerDetails ;
	
	//cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()== cargo.getTransaction().getCustomerInfo().getPhoneNumber().getPhoneNumber()
	if(k.length()==10||(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()!=null && !(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals(""))))
			{
	//if(k!=null || !(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals("")) )
//	{
		if(k.length()==10)
		{
			cargo.getCustomerInfo().getPhoneNumber().setPhoneNumber(k);
		}
	
		try {
			MAXConfigParametersIfc configParam = getAllConfigparameter();
			BigDecimal totalCash = configParam.getCashLimitParameter();
			//System.out.println("ttl===="+configParam.getCashLimitParameter());
			cargo.getTransaction().setCustomerInfo(cargo.getCustomerInfo());
			customerDetails = ((MAXHotKeysTransaction) hotKeysTransaction)
					.getCustomerTransactionDetails(cargo.getTransaction());
			//System.out.println(cargo.getCustomerInfo().getPhoneNumber().toString());
			//System.out.println("shanu  ===="+customerDetails);
			
			CurrencyIfc customerDetails1 = cargo.getCurrentTransactionADO().getBalanceDue();
			
			//BigDecimal m=customerDetails1.getDecimalValue();
			//System.out.println("shanu1  ===="+customerDetails1);
			
		 BigDecimal sum = customerDetails.add(customerDetails1.getDecimalValue());
		// System.out.println("print===="+sum);
			
		// if(sum)
		  if(sum.compareTo(totalCash)==-1)
			{
				bus.mail("CashValidated");
				//System.out.println(123);
			}
			else if (sum.compareTo(totalCash)==0)
			{
				bus.mail("CashValidated");
			}
			else if ( customerDetails1!=null && sum.compareTo(customerDetails1.getDecimalValue())==0 && sum.compareTo(totalCash)==1)
			{
				MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
				BigDecimal[] msg = new BigDecimal[2];
				msg[0] = totalCash.subtract(customerDetails);        
//                msg[0] = configParam.getCashLimitParameter().subtract(sum);        
                //msg[1] = configParam.getCashLimitParameter();
                msg[1] = totalCash;
                
                String m[] = new String[2];
                m[0] = msg[0].toString();
                m[1] = msg[1].toString();
                dialogModel.setArgs(m);
				dialogModel.setResourceID("Cash_Limit_Exceeded");
				dialogModel.setDescription("Cash_Limit_Exceeded");
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);                    
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");
                //POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                // Display the dialog.
			}
			else 
			{
				MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
                //System.out.println(dialogModel);
                BigDecimal[] msg = new BigDecimal[2];
                msg[0] = totalCash.subtract(customerDetails);        
//                msg[0] = configParam.getCashLimitParameter().subtract(sum);        
                //msg[1] = configParam.getCashLimitParameter();
                msg[1] = totalCash;
                
                String m[] = new String[2];
                m[0] = msg[0].toString();
                m[1] = msg[1].toString();
                dialogModel.setArgs(m);
                dialogModel.setResourceID("Maximum_Cash_Limit_Exceeded");
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);                    
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");
                //POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                // Display the dialog.

		    
				
				
				//System.out.println(456);
			}
			//System.out.println("34 customerDetails============ :\n"+customerDetails.toString());
		 
		 } catch (oracle.retail.stores.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	else if(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()==null || cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals(""))
	{
		bus.mail("Yes");
	}
}

	private MAXConfigParametersIfc getAllConfigparameter() {			 

        MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
        MAXConfigParametersIfc configParameters = null;
        configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
                .create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

            try {
				configParameters = configTransaction.selectConfigParameters();
			} catch (oracle.retail.stores.foundation.manager.data.DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       
        return configParameters;
    }

	     
}
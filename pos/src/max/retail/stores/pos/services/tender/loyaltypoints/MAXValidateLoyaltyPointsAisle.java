/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    Rev 1.1     03/11/2015     Deepshikha Singh       Changes done for online loyalty points redemption
	Rev 1.0 	20/05/2013		Prateek		          Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.loyaltypoints;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateLoyaltyPointsAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus)
	{
//		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
//		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.MAX_TIC_LOYALTY_POINTS);
//		PromptAndResponseModel prmodel = model.getPromptAndResponseModel();
//		String rspText = prmodel.getResponseText();
		TenderCargo cargo = (TenderCargo)bus.getCargo();	
		HashMap map = cargo.getTenderAttributes();
		String rspText = (String)map.get(TenderConstants.AMOUNT);
		BigDecimal amount = new BigDecimal(rspText);
		/*changes start for rev 1.1*/
		int intLoyaltyAmount=amount.intValue();
		String loyaltyAmount=Integer.toString(intLoyaltyAmount);
		/*BigDecimal decimalLoyaltyAmount=new BigDecimal(intLoyaltyAmount);
		CurrencyIfc loyaltyAmount = DomainGateway.getBaseCurrencyInstance();
		loyaltyAmount.setDecimalValue(decimalLoyaltyAmount);*/
		map.put(TenderConstants.AMOUNT, loyaltyAmount);
		/*changes end for rev 1.1*/
		boolean overtenderable = false;
		try
		{
			ParameterManagerIfc param = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
			
			/**Check for over tender**/
			String[] invalidOvertenderTenders = param
					.getStringValues("TendersNotAllowedForOvertender");
			
			for (int i = 0; i < invalidOvertenderTenders.length; i++)
			{
				if (invalidOvertenderTenders[i].equals("LoyaltyPoints")) 
				{
					CurrencyIfc balanceDue = cargo.getCurrentTransactionADO()
							.getBalanceDue();
					try 
					{
						CurrencyIfc tenderAmount = parseAmount((String) cargo
								.getTenderAttributes().get(TenderConstants.AMOUNT));
						if (tenderAmount.compareTo(balanceDue) == CurrencyIfc.GREATER_THAN)
						{
							
							overtenderable = true;
						}
					} 
					catch (TenderException e1)
					{
						e1.printStackTrace();
					}
				}
			}
			if(!overtenderable)
			{
				/* changes start for rev 1.1*/
				/**Below line commented because maximum limit of loyalty point is remove from requirement**/
				//Double amntt = param.getDoubleValue("MaximumLoyaltyPointRedemptionLimit");
				/**The method isMultipleOf() are used to check that entered amount is multiple of 100 but now 
				 * the requirement change that amount is not less then 100 but greater then or equal to 100 **/
				/*if(isMultipleOf(amount, 100))*/
				  if(intLoyaltyAmount>=100)
				  {
					  /* changes start for rev 1.1*/
					  bus.mail("Success");
					/*if(amount.compareTo(new BigDecimal(amntt.doubleValue()))<=0)
						bus.mail("Success");	 
					else
						showConfirmationDialog(bus, "InvalidLoyalyPointsAmount", amntt+"");*/
						/* changes end for rev 1.1*/
				  }
				  else
					showConfirmationDialog(bus, "InvalidLoyalyPointsDenomination");
			}
			else
				showConfirmationDialog(bus, "OvertenderNotAllowed");
		}
		/* changes start for rev 1.1*/
		//catch(ParameterException ex)
		/* changes end for rev 1.1*/
		catch(Exception ex)
		{
			/* changes start for rev 1.1*/
			//showConfirmationDialog(bus, "InvalidLoyaltyPointsParameter");
			/* changes end for rev 1.1*/
			ex.printStackTrace();
		}
	}
	/* changes start for rev 1.1*/
	/**The method isMultipleOf() are used to check that entered amount is multiple of 100 but now 
	 * the requirement change that amount is not less then 100 but greater then or equal to 100 **/
	/*public boolean isMultipleOf(BigDecimal n, int d)
	{
		double div = n.doubleValue();
		if(div%d==0)
			return true;
		return false;
	}*/
	
	/*protected boolean validAmount(BusIfc bus, BigDecimal amt) throws ParameterException
	{
		ParameterManagerIfc param = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		Double amount = param.getDoubleValue("MaximumLoyaltyPointRedemptionLimit");
		if(amt.compareTo(new BigDecimal(amount.doubleValue()))<0)
			return true;
		return false;
		
	}
	
	private void showConfirmationDialog(BusIfc bus, String message, String args)
	{
		String agrgs[] = {args};
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(message);        
        model.setArgs(agrgs);
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}*/
	/*changes end for rev 1.1*/
	
	private void showConfirmationDialog(BusIfc bus, String message)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(message);        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
	protected CurrencyIfc parseAmount(String amountString)
			throws TenderException {
		CurrencyIfc amount = null;
		try {
			amount = DomainGateway.getBaseCurrencyInstance(amountString);
		} catch (Exception e) {
			throw new TenderException("Attempted to parse amount string",
					TenderErrorCodeEnum.INVALID_AMOUNT, e);
		}
		return amount;
	}
}

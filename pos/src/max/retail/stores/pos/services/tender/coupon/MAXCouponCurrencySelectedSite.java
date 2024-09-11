package max.retail.stores.pos.services.tender.coupon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCountBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCouponCurrencySelectedSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5991757340506896199L;

	public void arrive(BusIfc bus) {

		
		//changes by shyvanshu mehra
		
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		int quantity = 0;

		try {
			quantity = Integer.parseInt(ui.getInput());
		} catch (Exception e) {
			quantity = 0;
		}

		LetterIfc currentLetter = bus.getCurrentLetter();
		if(currentLetter.getName().equals("2Wheeler") || currentLetter.getName().equals("4Wheeler"))
		{			//POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			//UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			//ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
			//cargo.getTransaction().getBusinessDay().dayOfWeek();
			
			try {
				//String parCou = (pm.getStringValue("ParkingCouponValidation"));
				String twoWheChrgWD  = (pm.getStringValue("TwoWheelerChargeWD"));
				String twoWheChrgWE  = (pm.getStringValue("TwoWheelerChargeWE"));
				String fourWheChrgWD  = (pm.getStringValue("fourWheelerChargeWD"));
				String fourWheChrgWE  = (pm.getStringValue("fourWheelerChargeWE"));
				
			//	cargo.getTransaction().getTenderTransactionTotals().getSubtotal().subtract(twoWheChrgWE)
				
				//CurrencyIfc parkingCoupon = DomainGateway.getBaseCurrencyInstance(parCou);
				CurrencyIfc twoWheelerChargeWD = DomainGateway.getBaseCurrencyInstance(twoWheChrgWD);
				CurrencyIfc twoWheelerChargeWE = DomainGateway.getBaseCurrencyInstance(twoWheChrgWE);
				CurrencyIfc fourWheelerChargeWE = DomainGateway.getBaseCurrencyInstance(fourWheChrgWE);
				CurrencyIfc fourWheelerChargeWD = DomainGateway.getBaseCurrencyInstance(fourWheChrgWD);
				
				
				
				/*
				 * if(cargo.getTransaction().getTenderTransactionTotals().getSubtotal()!=null &&
				 * cargo.getTransaction().getTenderTransactionTotals().getSubtotal().compareTo(
				 * parkingCoupon)==1) {
				 */
					if(bus.getCurrentLetter().getName().equals("2Wheeler")) 
					{
						if(cargo.getTransaction().getBusinessDay().dayOfWeek()==1||cargo.getTransaction().
								getBusinessDay().dayOfWeek()==2||cargo.getTransaction().getBusinessDay().dayOfWeek()==3
								||cargo.getTransaction().getBusinessDay().dayOfWeek()==4
								||cargo.getTransaction().getBusinessDay().dayOfWeek()==5)
						{				  
							cargo.getTransaction().getTenderTransactionTotals().setSubtotal(
									cargo.getTransaction().getTenderTransactionTotals().getSubtotal()
									.subtract(twoWheelerChargeWD));
					  //System.out.println("twoWheelerChargeWD======= "+ cargo.getTransaction().getTenderTransactionTotals().getSubtotal());
								cargo.setParkingTotal(twoWheelerChargeWD);
								
							bus.mail(new Letter("Success"), BusIfc.CURRENT);
					  //bus.mail("Next"); 
					  
					}
						else {
							 cargo.getTransaction().getTenderTransactionTotals().setSubtotal(
									  cargo.getTransaction().getTenderTransactionTotals().getSubtotal().subtract(twoWheelerChargeWE));
							 // System.out.println("twoWheelerChargeWD======= "+ cargo.getTransaction().getTenderTransactionTotals().getSubtotal());
							 cargo.setParkingTotal(twoWheelerChargeWE);
							 bus.mail(new Letter("Success"), BusIfc.CURRENT);
							  //bus.mail("Next"); 
						}
					}
				 
					else if(bus.getCurrentLetter().getName().equals("4Wheeler")) 
					{
						if(cargo.getTransaction().getBusinessDay().dayOfWeek()==1||cargo.getTransaction().getBusinessDay().dayOfWeek()==2||
								cargo.getTransaction().getBusinessDay().dayOfWeek()==3||cargo.getTransaction().getBusinessDay().dayOfWeek()==4
								||cargo.getTransaction().getBusinessDay().dayOfWeek()==5)
						{				  
							cargo.getTransaction().getTenderTransactionTotals().setSubtotal(
							  cargo.getTransaction().getTenderTransactionTotals().getSubtotal().subtract(fourWheelerChargeWD));
							//System.out.println("fourWheelerChargeWD======= "+ cargo.getTransaction().getTenderTransactionTotals().getSubtotal());
							cargo.setParkingTotal(fourWheelerChargeWD);
							bus.mail(new Letter("Success"), BusIfc.CURRENT);
							//bus.mail("Next"); 
						}
						else {
							 cargo.getTransaction().getTenderTransactionTotals().setSubtotal(
									  cargo.getTransaction().getTenderTransactionTotals().getSubtotal().subtract(fourWheelerChargeWE));
							  //System.out.println("fourWheelerChargeWD======= "+ cargo.getTransaction().getTenderTransactionTotals().getSubtotal());
							 cargo.setParkingTotal(fourWheelerChargeWD); 
							 bus.mail(new Letter("Success"), BusIfc.CURRENT); 
							  //bus.mail("Next"); 
						}
				  }
					cargo.setCouponType(15);
					cargo.setCouponName("Parking");
					//}
				/*
				 * else { bus.mail("No"); }
				 */
			} catch (NumberFormatException | ParameterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				
		
		}

		else
		{
		MAXCouponDenominationBeanModel couponDenoBeanModel = (MAXCouponDenominationBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION);
		String amount=null;
		
		try {
			Serializable[] parameterValues = pm.getParameterValues("CouponDenomination" + currentLetter.getName().substring(8, currentLetter.getName().length()));
			if (parameterValues != null && parameterValues.length == 1
					&& parameterValues[0] instanceof String)
				amount=(String)parameterValues[0];
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ArrayList couponDenominationCountBeanModel = couponDenoBeanModel
				.getCouponDenominationCountBeanModel();

		
		
		Iterator iterator = couponDenominationCountBeanModel.iterator();
		while(iterator.hasNext())
		{
			MAXCouponDenominationCountBeanModel couponDeno = (MAXCouponDenominationCountBeanModel)iterator.next();
			if(couponDeno.getDescription().equals(amount))
			{
				/*int oldQuantity = couponDeno.getQuantity();
				int newQuantity = oldQuantity+quantity;*/
				couponDeno.setQuantity(quantity);
				couponDeno.setAmount(DomainGateway
						.getBaseCurrencyInstance(new BigDecimal(amount).multiply(new BigDecimal(quantity))));
				break;
			}
		}
		
		couponDenoBeanModel
				.setCouponDenominationCountBeanModel(couponDenominationCountBeanModel);
		((PromptAndResponseModel)couponDenoBeanModel.getPromptAndResponseModel()).setResponseText("");
		ui.setModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION, couponDenoBeanModel);
		

		bus.mail("Next");

	}
	}
}

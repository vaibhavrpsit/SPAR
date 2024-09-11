/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.2	Nitesh		04/Jan/2017 	Changes done for till reconcillation	
  Rev 1.1	Prateek		18/June/2013	Changes done for BUG 6506
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.domain.tender.MAXCouponTypes;
import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXDetailCouponBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXDisplayCouponOptionSite extends PosSiteActionAdapter {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.dailyoperations.poscount.coupon.MAXDisplayCouponOptionSite.class);
	private MAXDetailCouponBeanModel beanModel;

	public void arrive(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		String[] couponTypes = null;
		//Read CouponTypes Parameter
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		List couponList = new ArrayList();
		List couponList1= new ArrayList();
		try {
			//couponList= Arrays.asList(pm.getStringValues("FoodCouponTypeList"));
			couponList1 = Arrays.asList(pm.getStringValues("NonFoodCouponTypeList"));
			//changes for rev 1.2 starts
			couponTypes = new String[(couponList.size()+couponList1.size())];
			//couponTypes = new String[(couponList.size()+couponList1.size())-1];
			//changes for rev 1.2 ends
			int i=0 ,j=0;
			while(i<couponList.size())
				couponTypes[i] = (String)couponList.get(i++);
			while(j<couponList1.size())
			{
				//Changes for Carry Bag : Start--->By Gaurav
				String couponName = (String)couponList1.get(j++);
				if(!couponName.equalsIgnoreCase("CARRY BAG DISCOUNT COUPON"))
				couponTypes[i++] = couponName;
				//Changes for Carry Bag : End--->By Gaurav
			}
			cargo.setCouponTypes(couponTypes);
		} 
		catch (ParameterException e) {
			logger.error(e);
		}
		cargo.setCurrentActivityOrCharge(
	            DomainGateway.getFactory()
	                         .getTenderTypeMapInstance()
	                         .getDescriptor(TenderLineItemIfc.TENDER_TYPE_COUPON));
		//Unload Data From Cargo for Coupon Counts 
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        int key =2;
        for(int i=0;i<couponTypes.length;i++)
        {
        	if(key<=8)
        		navModel.addButton("Button", couponTypes[i], couponTypes[i], true,"F"+(key++));
        	else
        	{
				/**MAX Rev 1.1 Change : Start**/
				i--;
				/**MAX Rev 1.1 Change : End**/
				key=2;
			}
        }
        POSBaseBeanModel pos =new POSBaseBeanModel();
        pos.setLocalButtonBeanModel(navModel);
        
        //Start Setting up workspace model
        HashMap couponMap = cargo.getCouponCargo();
        //Obtain the data of coupons from cargo if there is any information saved earlier and set it on to the bean model
        //else create a new model with default values and set the cargo.
        CurrencyIfc couponValue = DomainGateway.getBaseCurrencyInstance();
        
        beanModel = new MAXDetailCouponBeanModel();
        beanModel.setCouponName(couponTypes);
        CurrencyIfc currency[] = new CurrencyIfc[couponTypes.length];

        if (couponMap!=null)
        {
        	
        	for(int i=0;i<couponTypes.length;i++)
        	{
        		if(couponMap.containsKey(couponTypes[i]))
        		{
	        		MAXCouponTypes cpnTypes = (MAXCouponTypes)couponMap.get(couponTypes[i]);
	        		List cpnDenmList = cpnTypes.getDenominationCount();
	        		for(int j=0;j<cpnDenmList.size();j++)
	        		{
	        			MAXDenominationCount count = (MAXDenominationCount)cpnDenmList.get(j);
	        			CurrencyIfc crrncy = count.getCurrency().multiply(new BigDecimal(count.getQuantity()));
	        			couponValue = couponValue.add(crrncy);
	        		}
	        		currency[i]=couponValue;
	        		couponValue = DomainGateway.getBaseCurrencyInstance();
        		}
        		else
        			currency[i]=DomainGateway.getBaseCurrencyInstance();
        	}
            beanModel.setCouponValue(currency);
        }
        else
        {
            for(int i =0;i <currency.length;i++)
            	currency[i] = DomainGateway.getBaseCurrencyInstance("0.00");
            beanModel.setCouponValue(currency);
        }
        
        beanModel.setLocalButtonBeanModel(navModel);
        ui.showScreen(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT, beanModel);
	}
}

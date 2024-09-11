/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.3   Nitesh		04/Jan/2013		Changes done for till reconcillation
  Rev 1.3   Prateek		16/July/2013	Changes done for BUG 7231
  Rev 1.2	Prateek		1/July/2013		Changes done for BUG 6742
  Rev 1.1	Prateek		26/June/2013	Changes done for BUG 6626
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import max.retail.stores.domain.financial.MAXFinancialCountTenderItem;
import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.domain.tender.MAXCouponTypes;
import max.retail.stores.domain.tender.MAXCreditCardDetails;
import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.domain.tender.MAXTIDDetails;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItem;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillCountSucceededRoad;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;

public class MAXTillCountSucceededRoad extends TillCountSucceededRoad {

	public void traverse(BusIfc bus)
    {

        // Count was a success, add financial count to till
        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        RegisterIfc r = cargo.getRegister();
        FinancialTotalsIfc ft = cargo.getTillTotals();
        ft = updateTotals(ft);
       
        int i=0;
        ft.getCombinedCount().getEntered().setTenderItems(updateFlag(ft.getCombinedCount().getEntered().getTenderItems()));
        cargo.setTillTotals(ft);
        TillIfc till = r.getTillByID(cargo.getTillID());
        r.removeTill(cargo.getTillID());
        till.setTotals(ft);
        r.addTill(till);
        //r.getTillByID(cargo.getTillID()).addTotals(ft);
        r.addTotals(ft);
    }
	private FinancialTotalsIfc updateTotals(FinancialTotalsIfc ft)
	{
		FinancialCountTenderItemIfc tenderCount[] = ft.getCombinedCount().getEntered().getTenderItems();
		if(((MAXFinancialTotals)ft).getCouponDenominationCount() !=null)
		{
			Map map = ((MAXFinancialTotals)ft).getCouponDenominationCount();
			tenderCount = updateCouponInfo(tenderCount, map);
		}
		tenderCount = adjustNullValues(tenderCount);
		if(((MAXFinancialTotals)ft).getAcquirerBankDetails() !=null)
		{
			Map map = ((MAXFinancialTotals)ft).getAcquirerBankDetails();
			tenderCount = updateCreditInfo(tenderCount, map);
		}
		/**MAX Rev 1.3 Change : Start**/
		/*if(((MAXFinancialTotals)ft).getCashDenomination() !=null){
			tenderCount = updateCashTender(tenderCount);
		}*/
		/*tenderCount = adjustNullValues(tenderCount);
		tenderCount = updateCashTender(tenderCount);*/
		/**MAX Rev 1.3 Change : End**/
		tenderCount = adjustNullValues(tenderCount);
		tenderCount = updateEnteredTotals(tenderCount, ((MAXFinancialTotals)ft).getEnteredTotals());
		/**MAX Rev 1.2 Change : Start**/
		tenderCount = adjustNullValues(tenderCount);
		
		/**MAX Rev 1.2 Change : End**/
		ft.getCombinedCount().getEntered().setTenderItems(tenderCount);
		return ft;
	}
	private FinancialCountTenderItemIfc[] updateCouponInfo(FinancialCountTenderItemIfc tenderCount[], Map map)
	{
		int j=0;
		FinancialCountTenderItemIfc[] tenderCount1 = null;
		List couponTypes = new ArrayList();
		for(int i=0; i<tenderCount.length;i++)
		{
			if(tenderCount[i].getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_COUPON)
			{
				tenderCount[i] = null;
			}
		}
		
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			MAXFinancialCountTenderItem fcti = new MAXFinancialCountTenderItem();
		    //fcti = (MAXFinancialCountTenderItem)tenderCount[i].clone();
		    Map.Entry pairs = (Map.Entry)it.next();
		    MAXCouponTypes cpnTypes = (MAXCouponTypes)pairs.getValue();
		    fcti.setTenderType(TenderLineItemConstantsIfc.TENDER_TYPE_COUPON);
		    fcti.setDescription("Coupon");
		    fcti.setTenderSubType(cpnTypes.getCouponName());
		    fcti.setAmountIn(getCouponAmount(cpnTypes));
		    fcti.setNumberItemsIn(getCouponCount(cpnTypes));
		    couponTypes.add(fcti);
		
		}
		
	
		tenderCount1 = new FinancialCountTenderItemIfc[tenderCount.length+couponTypes.size()];
		int counter = 0;
		for(int i=0;i<tenderCount.length;i++)
		{
			if(tenderCount[i]!=null && tenderCount[i].getTenderType() != TenderLineItemConstantsIfc.TENDER_TYPE_COUPON)
				tenderCount1[counter++] = tenderCount[i]; 
		}
		for(int i=0;i<couponTypes.size();i++)
		{
			tenderCount1[counter++] = (FinancialCountTenderItemIfc)couponTypes.get(i);
		}
		
		return tenderCount1;
	}
	public int getCouponCount(MAXCouponTypes cpnTypes)
	{
		int count = 0;
		List denm = cpnTypes.getDenominationCount();
		for(int i=0;i<denm.size();i++)
		{
			MAXDenominationCount cnt = (MAXDenominationCount)denm.get(i);
			if(cnt.getQuantity()>0)
				count=count+cnt.getQuantity();			
		}
		return count;
	}
	private CurrencyIfc getCouponAmount(MAXCouponTypes type)
	{
		CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
		List dnm = type.getDenominationCount();
		for(int i=0;i<dnm.size();i++)
		{
			MAXDenominationCount dnmc =(MAXDenominationCount)dnm.get(i);
			CurrencyIfc amt = dnmc.getCurrency().multiply(new BigDecimal(dnmc.getQuantity()));
			total = total.add(amt);
		}
		return total;
	}
	private FinancialCountTenderItemIfc[] updateCreditInfo(FinancialCountTenderItemIfc tenderCount[], Map map)
	{
		FinancialCountTenderItemIfc[] tenderCount1 = null;
		List bankType = getListOfBankFromMap(map, tenderCount);
//		tenderCount1 = new FinancialCountTenderItemIfc[bankType.size()];
//		bankType.toArray(tenderCount1);		
//		List bankType = new ArrayList();
//		for(int i=0; i<tenderCount.length;i++)
//		{
//			if(tenderCount[i].getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE)
//			{
//				Iterator it = map.entrySet().iterator();
//				while (it.hasNext())
//				{
//					FinancialCountTenderItemIfc fcti = new FinancialCountTenderItem();
//				    fcti = (FinancialCountTenderItemIfc)tenderCount[i].clone();
//				    Map.Entry pairs = (Map.Entry)it.next();
//				    MAXCreditCardDetails card = (MAXCreditCardDetails)pairs.getValue();
//				    fcti.setTenderSubType(card.getBankName());
//				    fcti.setAmountIn(getCardAmount(card));
//				    bankType.add(fcti);
//				}
//			}
//		}
		tenderCount1 = new FinancialCountTenderItemIfc[tenderCount.length+bankType.size()];
		int counter = 0;
		for(int i=0;i<tenderCount.length;i++)
		{
			if(tenderCount[i].getTenderType() != TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE)
				tenderCount1[counter++] = tenderCount[i]; 
		}
		for(int i=0;i<bankType.size();i++)
		{
			tenderCount1[counter++] = (FinancialCountTenderItemIfc)bankType.get(i);
		}
		return tenderCount1;
	}
	/**MAX Rev 1.2 Change : Start**/
	private List getListOfBankFromMap(Map map, FinancialCountTenderItemIfc tenderCount[])
	{
		List bankType = new ArrayList();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			FinancialCountTenderItemIfc fcti = new MAXFinancialCountTenderItem();
		    Map.Entry pairs = (Map.Entry)it.next();
		    MAXCreditCardDetails card = (MAXCreditCardDetails)pairs.getValue();
		    int index = indexof(tenderCount, card.getBankName());
		    if(index > -1)
		    {
		    	fcti = tenderCount[index];
		    	fcti.setAmountIn(getCardAmount(card));
		    	fcti.setNumberItemsIn(card.getTidDetails().size());	
		    }
		    else
			{
			    fcti.setTenderType(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE);
			    fcti.setDescription(card.getBankName());
			    fcti.setTenderSubType(card.getBankName());
			    fcti.setAmountIn(getCardAmount(card));
			    fcti.setNumberItemsIn(card.getTidDetails().size());
			    fcti.setSummaryDescription(PosCountCargo.CHARGE);
			}
		    bankType.add(fcti);
		}
		return bankType;
	}

	private int indexof(FinancialCountTenderItemIfc tenderCount[], String bankType)
	{
		for(int i=0; i<tenderCount.length;i++)
		{
			if(tenderCount[i].getDescription().equalsIgnoreCase(bankType))
				return i;
		}
		return -1;
	}
	/**MAX Rev 1.2 Change : End**/
	private CurrencyIfc getCardAmount(MAXCreditCardDetails card)
	{
		CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
		for(int i =0;i<card.getTidDetails().size();i++)
		{
			MAXTIDDetails tid = (MAXTIDDetails)card.getTidDetails().get(i);
			total = total.add(tid.getAmount());
		}
		return total;
	}
	/**MAX Rev 1.2 Change : Start**/
	/****
	*Function removes all the null values from FinancialCountTenderItemIfc
	* so that null pointer exception can be avoided
	****/
	private FinancialCountTenderItemIfc[] adjustNullValues(FinancialCountTenderItemIfc[] tenderCount)
	{
		List list = new ArrayList();				
		for(int i=0; i<tenderCount.length;i++)
		{
			if(tenderCount[i] != null)
				list.add(tenderCount[i]);
		}
		FinancialCountTenderItemIfc[] tenderCount1 = new FinancialCountTenderItemIfc[list.size()];
		for(int i=0;i<list.size();i++)
			tenderCount1[i] = (FinancialCountTenderItemIfc)list.get(i);
		return tenderCount1;
	}
	/**MAX Rev 1.2 Change : End**/
	/**MAX Rev 1.3 Change : Start**/
	/*private FinancialCountTenderItemIfc[] updateCashTender(FinancialCountTenderItemIfc[] tenderCount)
	{
		/*FinancialCountTenderItemIfc[] tenderCnt = new FinancialCountTenderItemIfc[tenderCount.length];
		int c= 0;
		for(int i=0; i<tenderCount.length;i++)
		{
			if(tenderCount[i] != null)
			{
				if(tenderCount[i].getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_CASH && tenderCount[i].getTenderSubType() != null 
						&&	tenderCount[i].getTenderSubType().length() >0)
				{
					tenderCnt[c++] = tenderCount[i];
				}
			}
		}
		return tenderCnt;
	}*/
	/**MAX Rev 1.3 Change : End**/
	
	private FinancialCountTenderItemIfc[] updateFlag(FinancialCountTenderItemIfc[] tenderCount)
	{
		for(int i=0; i<tenderCount.length;i++)
		{
			((MAXFinancialCountTenderItem)tenderCount[i]).setTenderEntered(true);
		}
		return tenderCount;
	}
	private FinancialCountTenderItemIfc[] updateEnteredTotals(FinancialCountTenderItemIfc[] tenderCount, List enteredTotals)
	{
		int k=0;
		boolean flag = false;
		for(int i=0; i<tenderCount.length;i++)
		{
			for(int j=0;j<enteredTotals.size();j++)
			{
				//changes for rev 1.4 starts 
				String tenderName = (String)enteredTotals.get(j);
				//String tenderName = ((FinancialCountTenderItem)enteredTotals.get(j)).getDescription();
				//changes for rev 1.4 ends
				if(tenderName.equalsIgnoreCase(tenderCount[i].getDescription()))
				{	
					flag = true;
					break;
				}
				if(tenderCount[i].getTenderType()== TenderLineItemConstantsIfc.TENDER_TYPE_CASH);
				{
					if(tenderName.equals(tenderCount[i].getSummaryDescription()))
					{
						flag = true;
						break;
					}
				}
			}
			if(flag)
				((MAXFinancialCountTenderItem)tenderCount[i]).setTenderEntered(true);
			else
			{
				//tenderCount[i].setAmountIn(DomainGateway.getBaseCurrencyInstance("0.00"));
				tenderCount[i] = null;
			}
			flag = false;
				
		}
		return tenderCount;
	}
}

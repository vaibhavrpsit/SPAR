/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		26/June/2013	Changes done for BUG 6626
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

public class MAXPosCountCargo extends PosCountCargo {
	protected HashMap couponCargo = null;
	protected String selectedCoupon = null;
	protected String[] couponTypes= null;
	
	protected HashMap acquirerBankDetails = null;
	protected String selectedAcquirerBankName = null;
	protected String[] acquirerBanks= null;
	
	protected List giftCertList = null;	
	protected List cashDenomination = null;
	
	protected boolean isFirstTime = true;

	
	public HashMap getCouponCargo() {
		
		return couponCargo;
	}

	public void setCouponCargo(HashMap couponCargo) {
		this.couponCargo = couponCargo;
	}

	public String getSelectedCoupon() {
		return selectedCoupon;
	}

	public void setSelectedCoupon(String selectedCoupon) {
		this.selectedCoupon = selectedCoupon;
	}

	public String[] getCouponTypes() {
		return couponTypes;
	}

	public void setCouponTypes(String[] couponTypes) {
		this.couponTypes = couponTypes;
	}
	
	
	//Changes started here for acquirer banks for credit card reconcilation

	public HashMap getAcquirerBankDetails() {
		return acquirerBankDetails;
	}

	public void setAcquirerBankDetails(HashMap acquirerBankDetails) {
		this.acquirerBankDetails = acquirerBankDetails;
	}

	public String getSelectedAcquirerBankName() {
		return selectedAcquirerBankName;
	}

	public void setSelectedAcquirerBankName(String selectedAcquirerBankName) {
		this.selectedAcquirerBankName = selectedAcquirerBankName;
	}

	public String[] getAcquirerBanks() {
		return acquirerBanks;
	}

	public void setAcquirerBanks(String[] acquirerBanks) {
		this.acquirerBanks = acquirerBanks;
	}
	//Changes for Gift Certificate

	public List getGiftCertList() {
		return giftCertList;
	}

	public void setGiftCertList(List giftCertList) {
		this.giftCertList = giftCertList;
	}

	public List getCashDenomination() {
		return cashDenomination;
	}

	public void setCashDenomination(List cashDenomination) {
		this.cashDenomination = cashDenomination;
	}
	
	
	
    public void setTenderModels(SummaryCountBeanModel[] scbm)
    {
        this.tenderModels = scbm;
    }
	
	  //----------------------------------------------------------------------
    /**
        Builds the list of Tender types.
        <P>
        @return the list of tender types
    **/
    //----------------------------------------------------------------------
    public SummaryCountBeanModel[] getTenderModels()
    {
        if (tenderModels == null)
        {
            Vector modelsVector              = new Vector();
            SummaryCountBeanModel       scbm = null;
            CurrencyIfc             currency = null;

            FinancialCountIfc fc = financialTotals.getTenderCount();
            FinancialCountTenderItemIfc[] fctis = fc.getTenderItems();
            for (int i = 0; i < fctis.length; i++)
            {
                if (fctis[i].isSummary())
                {
                    scbm = new SummaryCountBeanModel();
                    currency = fctis[i].getAmountTotal();
                    currency = (CurrencyIfc)currency.clone();
                    currency.setStringValue(STRING_ZERO);
                    scbm.setAmount(currency);
                    scbm.setDescription(fctis[i].getDescription());
                    scbm.setNegativeAllowed(new Boolean(true));
                    modelsVector.addElement(scbm);
                }
            }

            tenderModels = new SummaryCountBeanModel[modelsVector.size()];
            modelsVector.copyInto(tenderModels);
         }

        return tenderModels;
    }

    //----------------------------------------------------------------------
    /**
        Sets the list of Tender types.
        <P>
        @param scbm  The summary count bean model array of charges.
    **/
    //----------------------------------------------------------------------
    public void setChargeModels(SummaryCountBeanModel[] scbm)
    {
        chargeModels = scbm;

    }
	/**MAX Rev 1.1 Change : Start**/
	//----------------------------------------------------------------------
    /**
        Builds the list of Credit SummaryCountBeanModels from the Expected
        Counts in the TenderCount Totals object.<P>
        @return SummaryCountBeanModel[]  The list of Credit SummaryCountBeanModels
    **/
    //----------------------------------------------------------------------
    public SummaryCountBeanModel[] getChargeModels()
    {
        if (chargeModels == null)
        {
            SummaryCountBeanModel scbm = null;

            FinancialCountTenderItemIfc[] fctis = financialTotals.getCombinedCount().
                getExpected().getTenderItems();
            Set set = new HashSet();
            Set setTester = new HashSet();
            for (int i = 0; i < fctis.length; i++)
            {
                if (fctis[i].getSummaryDescription().equals(CHARGE))
                {
                    scbm = new SummaryCountBeanModel();
                    scbm.setDescription(fctis[i].getDescription());
                    scbm.setAmount((fctis[i].getAmountIn()).subtract(fctis[i].getAmountOut()));                    
                    scbm.setNegativeAllowed(new Boolean(true));
                    boolean success = setTester.add(scbm.getDescription());
                    if ( success )
                    {
                        set.add(scbm);
                    }
                }
            }
            chargeModels = new SummaryCountBeanModel[set.size()];
            chargeModels = (SummaryCountBeanModel[])set.toArray(chargeModels);
        }

        return chargeModels;
    }
	/**MAX Rev 1.1 Change : End**/

	public boolean isFirstTime() {
		return isFirstTime;
	}

		public void setFirstTime(boolean isFirstTime) {
			this.isFirstTime = isFirstTime;
		}
	    
		public CurrencyIfc getEnteredAmount(String description)
		{
	    CurrencyIfc expectedAmount = null;
	    CurrencyIfc tmpAmount = null;
	
	    FinancialCountTenderItemIfc[] fcti = 
	      this.financialTotals.getCombinedCountForTenderReconcile().getEntered().getTenderItems();
	    for (int i = 0; i < fcti.length; i++)
	    {
	      String itemDescription = fcti[i].getDescription();
	      if ((fcti[i] != null) && 
	        (itemDescription != null) && 
	        (itemDescription.equals(description)))
	      {
	        tmpAmount = fcti[i].getAmountTotal();
	        if (tmpAmount != null)
	        {
	          if (expectedAmount == null)
	          {
	            expectedAmount = tmpAmount;
	          }
	          else
	          {
	            expectedAmount = expectedAmount.add(tmpAmount);
	          }
	        }
	      }
	
	    }
	
	    if (expectedAmount == null)
	    {
	      expectedAmount = DomainGateway.getBaseCurrencyInstance();
	    }
	
	    return expectedAmount;
	}
		
	protected List enteredTender = new ArrayList();


	public List getEnteredTender() {
		return enteredTender;
	}

	public void setEnteredTender(List enteredTender) {
		this.enteredTender = enteredTender;
	}
	
    protected SummaryCountBeanModel[] getForeignTenderModels()
    {
        SummaryCountBeanModel[] ftm = null;
        
        String foreignCurrency = getCurrentForeignCurrency();

        if (foreignCurrency != null)
        {    
            ftm = (SummaryCountBeanModel[])foreignTenderModels.get(foreignCurrency);
        }
        
        return ftm;
    }
	
}

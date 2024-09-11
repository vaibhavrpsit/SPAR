/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.1	Veeresh Singh		3/04/2013		Initial Draft:	Food Totals requirement.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;

/**
 * @author Administrator
 *
 */
public class MAXTenderBeanModel extends TenderBeanModel {

	protected BigDecimal foodTotal = null;
	
	protected BigDecimal nonFoodTotal = null;
	
	protected BigDecimal easyBuyTotal = null;
	
	 /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    
    // Tender line items
    protected Vector fieldTenderLineItems = new Vector();
    
    // Transaction totals
    protected TransactionTotalsIfc totals = null;
    
    // totals model
    protected TotalsBeanModel totalsModel = new TotalsBeanModel();
    
    // Used to specify the amount of cash refund in case of split tender on a return
    protected CurrencyIfc cashRefundPortion = null;
    
    // Local currency nationality string constant
    protected String localCurrencyNationality = "U.S.";
    
    // Alternate currency 
    protected CurrencyIfc alternateCurrency = null;
    
    // Transaction is a return flag
    protected boolean isReturn = false;
    
    // Tender to delete
    protected TenderLineItemIfc tenderToDelete = null;
    
    // index of tender to delete
    protected int indexOfTenderToDelete;
    
    //---------------------------------------------------------------------
    /**
     * TenderBeanModel constructor comment.
     */
    //---------------------------------------------------------------------
    public MAXTenderBeanModel() 
    {
        super();
    }
    
    //---------------------------------------------------------------------
    /**
     * Gets the tenderLineItems property (Vector) value.
     * @return The tenderLineItems property value.
     * @see #setTenderLineItems
     */
    //---------------------------------------------------------------------
    public Vector getTenderLineItems() 
    {
        return fieldTenderLineItems;
    }

    //---------------------------------------------------------------------
    /**
       Gets the TransactionTotals

       @return TransactionTotals object
     */
    //---------------------------------------------------------------------
    public TransactionTotalsIfc getTransactionTotals() 
    {
        return totals;
    }
    
    public TotalsBeanModel getTotalsModel()
    {
        return totalsModel;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the tenderLineItems property (Vector) value.
     * @param tenderLineItems The new value for the property.
     * @see #getTenderLineItems
     */
    //---------------------------------------------------------------------
    public void setTenderLineItems(Vector tenderLineItems) 
    {
        fieldTenderLineItems = tenderLineItems;
    }

    //---------------------------------------------------------------------
    /**
       Sets the transation totals
       @param totals TransactionTotals
    **/
    //---------------------------------------------------------------------
    public void setTransactionTotals(TransactionTotalsIfc totals) 
    {
        this.totals = totals;
        totalsModel.setTotals(totals);
    }

    public void setTotalsModel(TotalsBeanModel model)
    {
        totalsModel = model;
    }
    
    //---------------------------------------------------------------------
    /**
       Retrieves the cashRefundPortion field
       @return cashRefundPortion String
    **/
    //--------------------------------------------------------------------- 
    public CurrencyIfc getCashRefundPortion()
    {
        return cashRefundPortion;
    }

    //---------------------------------------------------------------------
    /**
       Sets the cashRefundPortion field
       @param String new value to set cashRefundPortion
    **/
    //--------------------------------------------------------------------- 
    public void setCashRefundPortion(CurrencyIfc cashPortion)
    {
        cashRefundPortion = cashPortion;
    }
    
    //----------------------------------------------------------------------------
    /**
     Get local currency nationality string value
     @return return the value of localCurrencyNationality
    **/
    //----------------------------------------------------------------------------
    public String getLocalCurrencyNationality()
    {  
        return(localCurrencyNationality);
    }
    
    //----------------------------------------------------------------------------
    /**
     Set localCurrencyNationality value
     @param  the value of localCurrencyNationality
    **/
    //----------------------------------------------------------------------------
    public void setLocalCurrencyNationality(String value)
    {
        localCurrencyNationality = value;
    }
    
    //----------------------------------------------------------------------------
    /**
     Get alternate currency
     @return return the alternate currency
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getAlternateCurrency()
    {  
        return(alternateCurrency);
    }
    
    //----------------------------------------------------------------------------
    /**
     Set alternateCurrency value
     @param  the value of alternateCurrency
    **/
    //----------------------------------------------------------------------------
    public void setAlternateCurrency(CurrencyIfc value)
    {
        alternateCurrency = value;
    }

    //----------------------------------------------------------------------------
    /**
          Get Return flag value
          @return return the value of the return flag
    **/
    //----------------------------------------------------------------------------
    public boolean isReturn()
    {
        return(isReturn);
    }

    //----------------------------------------------------------------------------
    /**
          Set Return flag value
          @param return the value of the return flag
    **/
    //----------------------------------------------------------------------------
    public void setReturn(boolean ret)
    {
        isReturn = ret;
    }
    
    /**
     * @return
     */
    public TenderLineItemIfc getTenderToDelete()
    {
        return tenderToDelete;
    }

    /**
     * @param tendersToDelete
     */
    public void setTenderToDelete(TenderLineItemIfc tenderToDelete)
    {
        this.tenderToDelete = tenderToDelete;
    }

    /**
     * @return
     */
    public int getIndexOfTenderToDelete()
    {
        return indexOfTenderToDelete;
    }

    /**
     * @param indexOfTenderToDelete
     */
    public void setIndexOfTenderToDelete(int indexOfTenderToDelete)
    {
        this.indexOfTenderToDelete = indexOfTenderToDelete;
    }

	public BigDecimal getFoodTotal(){
		return foodTotal;
	}
	
	public void setFoodTotal(BigDecimal value){
		foodTotal = value;
	}
	
	public BigDecimal getNonFoodTotal(){
		return nonFoodTotal;
	}
	
	public void setNonFoodTotal(BigDecimal value){
		nonFoodTotal = value;
	}
	public BigDecimal getEasyBuyTotal(){
		return easyBuyTotal;
	}
	
	public void setEasyBuyTotal(BigDecimal value){
		easyBuyTotal = value;
	}
}

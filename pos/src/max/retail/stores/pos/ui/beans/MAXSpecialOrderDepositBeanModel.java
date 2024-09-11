/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order - Suggested Tender Types
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

// pos imports

//------------------------------------------------------------------------------
/**
 *  Data model for the SpecialOrderDepositBean.
 *  @version $Revision: 4$
 */
//------------------------------------------------------------------------------    
public class MAXSpecialOrderDepositBeanModel extends POSBaseBeanModel
{
    /** Generated Serial Version UID */
    private static final long serialVersionUID = -157841985051702936L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: 4$";
        
    /** value for customer name */
    protected String customerValue = "";
    
    /** value for special order number */
    protected String specialOrderNumberValue = "";
    
    /** value for balance due */
    protected String balanceDueValue = "";
    
    /** value for minimum deposit */
    protected String minimumDepositValue = "";
    
	/**MAX Rev 1.1 Change : Start**/
	protected String suggestedTender = null;
	/**MAX Rev 1.1 Change : End**/

    /** value for deposit amount */
    protected CurrencyIfc depositAmountValue = null;
    
    protected EYSDate expectedDeliveryDate = null;
    
    public EYSDate getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public EYSTime getExpectedDeliveryTime() {
		return expectedDeliveryTime;
	}

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime) {
		this.expectedDeliveryTime = expectedDeliveryTime;
	}

	protected EYSTime expectedDeliveryTime = null;
        
//------------------------------------------------------------------------------
/**
 *  Gets the value that is displayed in the customer field.
 *  @return the customer's name
 */
    public String getCustomerValue()
    {
        return customerValue;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the value that is displayed in the special order number field.
 *  @return the special order number
 */
    public String getSpecialOrderNumberValue()
    {
        return specialOrderNumberValue;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the value that is displayed in the balance due field.
 *  @return the balance due
 */
    public String getBalanceDueValue()
    {
        return balanceDueValue;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the value that is displayed in the minimum deposit field.
 *  @return the minimum deposit
 */
    public String getMinimumDepositValue()
    {
        return minimumDepositValue;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the value that is entered in the deposit amount field.
 *  @return the deposit amount
 */
    public CurrencyIfc getDepositAmountValue()
    {
        return depositAmountValue;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the value to display in the customer field.
 *  @param propValue the customer's name
 */
    public void setCustomerValue(String propValue)
    {
        customerValue = propValue;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the value to display in the specialOrderNumber field.
 *  @param propValue the Special Order Number
 */
    public void setSpecialOrderNumberValue(String propValue)
    {
        specialOrderNumberValue = propValue;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the value to display in the balance due field.
 *  @param propValue the balance due
 */
    public void setBalanceDueValue(String propValue)
    {
        balanceDueValue = propValue;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the value to display in the MinimumDeposit field.
 *  @param propValue the minimum deposit
 */
    public void setMinimumDepositValue(String propValue)
    {
        minimumDepositValue = propValue;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the value to display in the DepositAmount field.
 *  @param propValue the deposit amount
 */
    public void setDepositAmountValue(CurrencyIfc propValue)
    {
        if (propValue == null)
        {
            depositAmountValue = DomainGateway.getBaseCurrencyInstance("0.00");
        }
        else
        {
            depositAmountValue = propValue;
        }
    }
	/**MAX Rev 1.1 Change : Start**/
    public String getSuggestedTender() {
    	return suggestedTender;
    }

    public void setSuggestedTender(String suggestedTender) {
    	this.suggestedTender = suggestedTender;
    }
	/**MAX Rev 1.1 Change : End**/
    //---------------------------------------------------------------------
    /**
        Return string representation of the SpecialOrderDepositBeanModel values.
        <P>
        @return SpecialOrderDepositBeanModel values as a string
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buf=new StringBuffer("SpecialOrderDepositBeanModel{");
        if(getCustomerValue() != null)
        {
            buf.append("\n Customer Name = ").append(getCustomerValue()).append(",");
        }       
        if(getSpecialOrderNumberValue() != null)
        {
            buf.append("\n Special Order Number = ").append(getSpecialOrderNumberValue()).append(",");
        }
        if(getBalanceDueValue() != null)
        {
            buf.append("\n Balance Due = ").append(getBalanceDueValue().toString()).append(",");
        }
        if(getMinimumDepositValue() != null)
        {
            buf.append("\n Minimum Deposit Due = ").append(getMinimumDepositValue().toString()).append(",");
        }
        if(getDepositAmountValue() != null)
        {
            buf.append("\n Deposit Paid = ").append(getDepositAmountValue().toFormattedString()).append(",");
        }
        buf.append("}\n");
        return buf.toString();
    }

}
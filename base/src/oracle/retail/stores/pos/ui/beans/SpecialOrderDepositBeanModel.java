/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderDepositBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         8/7/2007 4:45:03 PM    Maisa De Camargo
 *       Updated type of SpecialOrderDepositBeanModel.depositAmountValue field
 *        from String to CurrencyIfc.
 *  3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:27  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:50:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:00   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 04 2001 12:15:44   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

// pos imports

//------------------------------------------------------------------------------
/**
 *  Data model for the SpecialOrderDepositBean.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------    
public class SpecialOrderDepositBeanModel extends POSBaseBeanModel
{
    /** Generated Serial Version UID */
    private static final long serialVersionUID = -157841985051702936L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
        
    /** value for customer name */
    protected String customerValue = "";
    
    /** value for special order number */
    protected String specialOrderNumberValue = "";
    
    /** value for balance due */
    protected String balanceDueValue = "";
    
    /** value for minimum deposit */
    protected String minimumDepositValue = "";
    
    /** value for deposit amount */
    protected CurrencyIfc depositAmountValue = null;
        
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

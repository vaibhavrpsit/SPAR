/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/PaymentHistoryInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    2    360Commerce 1.1         4/25/2007 10:00:52 AM  Anda D. Cadar   I18N
 *         merge
 *    1    360Commerce 1.0         12/13/2005 4:47:56 PM  Barry A. Pape   
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class is used for Payment History Info. 
 * This info is used for Layaway/Order from IRS Customer viewpoint 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class PaymentHistoryInfo implements PaymentHistoryInfoIfc
{    
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1;
    
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        tender type
    **/
    protected String tenderType = "";
    /**
        country code
    **/
    protected String countryCode = "";
    /**
        tender amount
    **/
    protected CurrencyIfc tenderAmount = null;
    
    //---------------------------------------------------------------------
    /**
        PaymentHistoryInfo constructor. <P>
    **/
    //---------------------------------------------------------------------
    public PaymentHistoryInfo()
    {  
        tenderAmount = DomainGateway.getBaseCurrencyInstance();
    }
    
    /**
     * @return Returns the CountryCode.
     */
    public String getCountryCode() 
    {
        return countryCode;
    }
    /**
     * @param countryCode The CountryCode to set.
     */
    public void setCountryCode(String countryCode) 
    {
        this.countryCode = countryCode;
    }
    /**
     * @return Returns the tenderAmount.
     */
    public CurrencyIfc getTenderAmount() 
    {
        return tenderAmount;
    }
    /**
     * @param tenderAmount The tenderAmount to set.
     */
    public void setTenderAmount(CurrencyIfc tenderAmount) 
    {
        this.tenderAmount = tenderAmount;
    }
    /**
     * @return Returns the tenderType.
     */
    public String getTenderType() 
    {
        return tenderType;
    }
    /**
     * @param tenderType The tenderType to set.
     */
    public void setTenderType(String tenderType) 
    {
        this.tenderType = tenderType;
    }
    
    
    //---------------------------------------------------------------------
    /**
        Determine if two payment history info objects are identical. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param obj  object to compare with
        @return boolean  true if the objects are identical
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        if (obj instanceof PaymentHistoryInfo)
        {                               // begin compare  objects
            // downcast the input object
            PaymentHistoryInfo paymentHistoryInfo = (PaymentHistoryInfo) obj;

            // compare all the attributes 
            if (Util.isObjectEqual(this.tenderType, paymentHistoryInfo.getTenderType()) &&                    
                Util.isObjectEqual(this.countryCode, paymentHistoryInfo.getCountryCode()) &&
                Util.isObjectEqual(this.tenderAmount, paymentHistoryInfo.getTenderAmount()))
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }                               // end compare objects

        return isEqual;
    }
    
    //---------------------------------------------------------------------
    /**
        Clones this object. <P>
        @return cloned object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        PaymentHistoryInfo paymentHistoryInfo = new PaymentHistoryInfo();

        // set attributes in clone
        setCloneAttributes(paymentHistoryInfo);

        return paymentHistoryInfo;
    }                                   // end clone()


    //---------------------------------------------------------------------
    /**
        Sets attributes in clone. <P>
        @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(PaymentHistoryInfo newClass)
    {                                   // begin setCloneAttributes()
        newClass.setTenderType(this.getTenderType());
        newClass.setCountryCode(this.getCountryCode()); 
        
        if (this.getTenderAmount() != null)
        {
            newClass.setTenderAmount((CurrencyIfc)this.getTenderAmount().clone());
        }
        else
        {
            newClass.setTenderAmount(null);
        }
    }                                    // end setCloneAttributes()

}

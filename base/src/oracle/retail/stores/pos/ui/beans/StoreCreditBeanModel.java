/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreCreditBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//------------------------------------------------------------------------------ 
/**
    @version $KW=@(#); $Ver=pos_4.5.0:11; $EKW;
**/
//------------------------------------------------------------------------------ 
public class StoreCreditBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:11; $EKW;";
    
    /**
        Store Credit ID
    **/
    protected String id = "";

    /**
        Store Credit Amount
    **/
    protected CurrencyIfc amount = null;

    /** 
        Store Credit Expiration Date
    **/
    protected EYSDate expirationDate = null;
    
    /**
        Store Credit expiration date required boolean
    **/
    protected boolean expirationDateRequired = true;
    

    //---------------------------------------------------------------------
    /**
        Constructs StoreCreditBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public StoreCreditBeanModel()
    {
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the Store Credit ID. <P>
    **/
    //---------------------------------------------------------------------
    public String getStoreCreditID()
    {
        return id;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Store Credit ID. <P>
    **/
    //---------------------------------------------------------------------
    public void setStoreCreditID(String scid)
    {
        id = scid;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Store Credit Amount. <P>
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getAmount()
    {
        return amount;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Store Credit Amount. <P>
    **/
    //---------------------------------------------------------------------
    public void setAmount(CurrencyIfc storeCreditAmount)
    {
        amount = storeCreditAmount;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Store Credit Expiration Date. <P>
    **/
    //---------------------------------------------------------------------
    public EYSDate getExpirationDate()
    {
        return expirationDate;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Store Credit Expiration Date. <P>
    **/
    //---------------------------------------------------------------------
    public void setExpirationDate(EYSDate storeCreditExpirationDate)
    {
        expirationDate = storeCreditExpirationDate;
    }

    //---------------------------------------------------------------------
    /**
        Tests whether expiration date is required. <P>
    **/
    //---------------------------------------------------------------------
    public boolean isExpirationDateRequired()
    {
        return expirationDateRequired;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Store Credit Expiration Date required boolean. <P>
    **/
    //---------------------------------------------------------------------
    public void setExpirationDateRequired(boolean required)
    {
        expirationDateRequired = required;
    }

    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: StoreCreditBeanModel Revision: " + revisionNumber + "\n");
        buff.append("id [ " + id + "]\n");
        buff.append("amount [ " + amount + "]\n");
        buff.append("expirationDate [ " + expirationDate + "]\n");

        return(buff.toString());
    }
}


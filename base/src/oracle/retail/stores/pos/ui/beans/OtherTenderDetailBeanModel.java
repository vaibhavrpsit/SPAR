/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OtherTenderDetailBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

//----------------------------------------------------------------------------
/**
     Move data between the service and the OtherTenderDetailBean.<P>
     @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class OtherTenderDetailBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        The amount from each tender item the user enters
    **/
    private CurrencyIfc[] tenderAmounts = null;
    /**
        The amount from each tender item the user enters
    **/
    private CurrencyIfc   total = null;
    /**
        The tender description
    **/
    private String description            = null;

    /**
        The tender summary description
    **/
    private String summaryDescription     = null;

    //----------------------------------------------------------------------------
    /**
        Constructs the object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //----------------------------------------------------------------------------
    public OtherTenderDetailBeanModel()
    {
    }

    //----------------------------------------------------------------------------
    /**
        Retrives the tender amount array. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the tender amount array
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc[] getTenderAmounts()
    {
        return(tenderAmounts);
    }

    //----------------------------------------------------------------------------
    /**
        Sets the tender amount array. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param the tender amount array
    **/
    //----------------------------------------------------------------------------
    public void setTenderAmounts(CurrencyIfc[] value)
    {
        tenderAmounts = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrives the total. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the tender amount array
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getTotal()
    {
        return(total);
    }

    //----------------------------------------------------------------------------
    /**
        Sets the total. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param the tender amount array
    **/
    //----------------------------------------------------------------------------
    public void setTotal(CurrencyIfc value)
    {
        total = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves the tender description. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the tender description.
    **/
    //----------------------------------------------------------------------------
    public String getDescription()
    {                                   // begin getdescription()
        return(description);
    }                                   // end getdescription()

    //----------------------------------------------------------------------------
    /**
        Sets the tender description. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param the tender description.
    **/
    //----------------------------------------------------------------------------
    public void setDescription(String value)
    {                                   // begin setdescription()
        description = value;
    }                                   // end setdescription()

    //----------------------------------------------------------------------------
    /**
        Retrieves the tender summary description. <P>
        @return the tender description.
    **/
    //----------------------------------------------------------------------------
    public String getSummaryDescription()
    {                                   // begin getdescription()
        return(summaryDescription);
    }                                   // end getdescription()

    //----------------------------------------------------------------------------
    /**
        Sets the tender summay description. <P>
        @param the tender description.
    **/
    //----------------------------------------------------------------------------
    public void setSummaryDescription(String value)
    {                                   // begin setdescription()
        summaryDescription = value;
    }                                   // end setdescription()

}

/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CurrencyDetailBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:16 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 06 2004 17:05:50   DCobb
 * Added Currency Detail screens for Pickup & Loan. 
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     Move data between the service and the CurrencyDetailBean.<P>
     @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class CurrencyDetailBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        Holds the counts for each denomination.
    **/
    private Long[] denominationCounts = null;
    /**
        Holds the type of currency being counted and returns total.
    **/
    private CurrencyIfc total = null;
    /**
        Holds the name of the count type.
    **/
    private String summaryCurrencyDescription = null;

    /**
        Flag to indicate that pickup & loan are operating with a safe.
    **/
    boolean operateWithSafeFlag = true;
    /**
        Flag to indicate that the operation is a pickup
    **/
    boolean pickupFlag = false;
    /**
        Flag to indicate that the operation is a loan.
    **/
    boolean loanFlag = false;
    /**
        Holds the to/from register id for a pickup/loan when operating without a safe.
    **/
    private String register = null;

    //----------------------------------------------------------------------------
    /**
        Constructs the bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
    **/
    //----------------------------------------------------------------------------
    public CurrencyDetailBeanModel()
    {
    }

    //----------------------------------------------------------------------------
    /**
        Get the demonination count array. <P>
        @return the count array
    **/
    //----------------------------------------------------------------------------
    public Long[] getDenominationCounts()
    {
        return(denominationCounts);
    }

    //----------------------------------------------------------------------------
    /**
        Set the demonination count array. <P>
        @param the count array
    **/
    //----------------------------------------------------------------------------
    public void setDenominationCounts(Long[] values)
    {
        denominationCounts = values;
    }

    //---------------------------------------------------------------------
    /**
        Gets the total currency object. <P>
        @return CurrencyIfc total currency object.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getTotal()
    {
        return total;
    }

    //---------------------------------------------------------------------
    /**
        Sets the total currency object. <P>
        @param CurrencyIfc total currency object.
    **/
    //---------------------------------------------------------------------
    public void setTotal(CurrencyIfc value)
    {
        total = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves Added the text in the prompt area. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return Added the text in the prompt area
    **/
    //----------------------------------------------------------------------------
    public String getSummaryCurrencyDescription()
    {                                   // begin getSummaryCurrencyDescription()
        return(summaryCurrencyDescription);
    }                                   // end getSummaryCurrencyDescription()

    //----------------------------------------------------------------------------
    /**
        Sets Added the text in the prompt area. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  Added the text in the prompt area
    **/
    //----------------------------------------------------------------------------
    public void setSummaryCurrencyDescription(String value)
    {                                   // begin setSummaryCurrencyDescription()
        summaryCurrencyDescription = value;
    }                                   // end setSummaryCurrencyDescription()

    //---------------------------------------------------------------------
    /**
        Gets the to/from register id for a Pickup/Loan when operating 
        without a safe. <P>
        @return String the register id
    **/
    //---------------------------------------------------------------------    
    public String getRegister()
    {
        return register;
    }

    //---------------------------------------------------------------------
    /**
        Sets the to/from register id for a Pickup/Loan when operating  
        without a safe. <P>
        @param String the 'to' register id
    **/
    //---------------------------------------------------------------------     
    public void setRegister(String value)
    {
        register = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the flag that indicates when operating with a safe. 
        <P>
        @return boolean operate with safe flag
    **/
    //---------------------------------------------------------------------     
    public boolean getOperateWithSafeFlag()
    {
        return operateWithSafeFlag;
    }

    //---------------------------------------------------------------------
    /**
        Sets the flag that indicates when operating with a safe.
        <P>
        @param boolean operate with safe flag
    **/
    //---------------------------------------------------------------------     
    public void setOperateWithSafeFlag(boolean value)
    {
        operateWithSafeFlag = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the flag that indicates the operation is a pickup. 
        <P>
        @return boolean pickup flag
    **/
    //---------------------------------------------------------------------     
    public boolean getPickupFlag()
    {
        return pickupFlag;
    }

    //---------------------------------------------------------------------
    /**
        Sets the flag that indicates the operation is a pickup.
        <P>
        @param boolean pickup flag
    **/
    //---------------------------------------------------------------------     
    public void setPickupFlag(boolean value)
    {
        pickupFlag = value;
    } 
    
    //---------------------------------------------------------------------
    /**
        Gets the flag that indicates the operation is a loan. 
        <P>
        @return boolean loan flag
    **/
    //---------------------------------------------------------------------     
    public boolean getLoanFlag()
    {
        return loanFlag;
    }

    //---------------------------------------------------------------------
    /**
        Sets the flag that indicates the operation is a loan.
        <P>
        @param boolean loan flag
    **/
    //---------------------------------------------------------------------     
    public void setLoanFlag(boolean value)
    {
        loanFlag = value;
    }                 

    //----------------------------------------------------------------------------
    /**
        Converts contents to a String. <P>
        @return The string
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        // build result string
        String strResult = new String("Class:  CurrencyDetailBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

}

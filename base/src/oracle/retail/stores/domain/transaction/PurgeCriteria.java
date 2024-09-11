/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/PurgeCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:30 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:40:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 22 2003 10:41:48   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    This class is used for setting a search criteria. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class PurgeCriteria implements PurgeCriteriaIfc
{                                       // Begin class PurgeCriteria
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3800653191411929429L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
         Age in days that a completed transaction must be in order to be
         eligible for deletion.
    **/
    protected int transactionAge = 14;
    /**
         Age in days that a completed order must be in order to be
         eligible for deletion.
    **/
    protected int orderAge = 0;
    /**
         Age in days that a completed layaway must be in order to be
         eligible for deletion.
    **/
    protected int layawayAge = 0;
    /**
         Age in days that a completed permanent price change must
         be in order to be eligible for deletion.
    **/
    protected int permanentPriceChangeAge = 0;
    /**
         Age in days that a completed temporary price change must
         be in order to be eligible for deletion.
    **/
    protected int temporaryPriceChangeAge = 0;
    /**
         Age in days that a completed job must
         be in order to be eligible for deletion.
    **/
    protected int jobAge = 0;
    /**
         Age in days that a completed advanced pricing rule must
         be in order to be eligible for deletion.
    **/
    protected int advancedPricingAge = 0;

    //---------------------------------------------------------------------
    /**
        Constructs PurgeCriteria object.
    **/
    //---------------------------------------------------------------------
    public PurgeCriteria()
    {
    }

    //---------------------------------------------------------------------
    /**
        Sets the transaction age.
        @param value days old a transaction must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setTransactionAge(int value)
    {
        transactionAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the transaction age.
        @return days old a transaction must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getTransactionAge()
    {
        return transactionAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the order age.
        @param value days old a order must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setOrderAge(int value)
    {
        orderAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the  age.
        @return days old a order must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getOrderAge()
    {
        return orderAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the layaway age.
        @param value days old a layaway must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setLayawayAge(int value)
    {
        layawayAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the  age.
        @return days old a layaway must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getLayawayAge()
    {
        return layawayAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Permanent Price Change Age.
        @param value days old a Permanent Price Change must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setPermanentPriceChangeAge(int value)
    {
        permanentPriceChangeAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Permanent Price Change Age.
        @return days old a Permanent Price Change must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getPermanentPriceChangeAge()
    {
        return permanentPriceChangeAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Temporary Price Change Age.
        @param value days old a Temporary Price Change must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setTemporaryPriceChangeAge(int value)
    {
        temporaryPriceChangeAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Temporary Price Change Age.
        @return days old a Temporary Price Change must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getTemporaryPriceChangeAge()
    {
        return temporaryPriceChangeAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Job Change Age.
        @param value days old a Job Change must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setJobAge(int value)
    {
        jobAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Job Change Age.
        @return days old a Job Change must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getJobAge()
    {
        return jobAge;
    }

    //---------------------------------------------------------------------
    /**
        Sets the AdvancedPricing Change Age.
        @param value days old a AdvancedPricing Change must be to be deleted.
    **/
    //---------------------------------------------------------------------
    public void setAdvancedPricingAge(int value)
    {
        advancedPricingAge = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the AdvancedPricing Change Age.
        @return days old a AdvancedPricing Change must be to be deleted.
     **/
    //---------------------------------------------------------------------
    public int getAdvancedPricingAge()
    {
        return advancedPricingAge;
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        StringBuffer values = new StringBuffer(Util.EOL);

        values.append("transactionAge:          [");
        values.append(transactionAge);
        values.append("]" + Util.EOL);

        values.append("orderAge:                [");
        values.append(orderAge);
        values.append("]" + Util.EOL);

        values.append("layawayAge:              [");
        values.append(layawayAge);
        values.append("]" + Util.EOL);

        values.append("permanentPriceChangeAge: [");
        values.append(permanentPriceChangeAge);
        values.append("]" + Util.EOL);

        values.append("temporaryPriceChangeAge: [");
        values.append(temporaryPriceChangeAge);
        values.append("]" + Util.EOL);

        values.append("jobAge: [");
        values.append(jobAge);
        values.append("]" + Util.EOL);

        values.append("advancedPricingAge: [");
        values.append(advancedPricingAge);
        values.append("]" + Util.EOL);
        return values.toString();
    }
}                                       // End class PurgeCriteria

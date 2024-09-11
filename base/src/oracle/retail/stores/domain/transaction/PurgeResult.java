/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/PurgeResult.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
public class PurgeResult implements PurgeResultIfc
{                                       // Begin class PurgeResult
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1971751805930769174L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
         The number of transactions deleted from the database.
    **/
    protected int transactionsPurged = 0;
    /**
         The number of orders deleted from the database.
    **/
    protected int ordersPurged = 0;
    /**
         The number of layaways deleted from the database.
    **/
    protected int layawaysPurged = 0;
    /**
         The number of advanced pricing rules deleted from the database.
    **/
    protected int adavncedPricingRulesPurged = 0;
    /**
         The number of timed items deleted from the database.
    **/
    protected int timedItemsPurged = 0;
    /**
         The number of financial history items deleted from the database.
    **/
    protected int financialHistoryPurged = 0;
    /**
         The number of jobs deleted from the database.
    **/
    protected int jobsPurged = 0;

    //---------------------------------------------------------------------
    /**
        Constructs PurgeResult object.
    **/
    //---------------------------------------------------------------------
    public PurgeResult()
    {
    }

    //---------------------------------------------------------------------
    /**
        Sets the transactions purged.
        @param value the number of transactions purged.
    **/
    //---------------------------------------------------------------------
    public void setTransactionsPurged(int value)
    {
        transactionsPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the transactions purged.
        @return the number of transactions purged.
     **/
    //---------------------------------------------------------------------
    public int getTransactionsPurged()
    {
        return transactionsPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the orders purged.
        @param value the number of orders purged.
    **/
    //---------------------------------------------------------------------
    public void setOrdersPurged(int value)
    {
        ordersPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the orders purged.
        @return the number of orders purged.
     **/
    //---------------------------------------------------------------------
    public int getOrdersPurged()
    {
        return ordersPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the layaways purged.
        @param value the number of layaways purged.
    **/
    //---------------------------------------------------------------------
    public void setLayawaysPurged(int value)
    {
        layawaysPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the layaways purged.
        @return the number of layaways purged.
     **/
    //---------------------------------------------------------------------
    public int getLayawaysPurged()
    {
        return layawaysPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Adavnced Pricing Rules Purged.
        @param value the numer of Adavnced Pricing Rules Purged.
    **/
    //---------------------------------------------------------------------
    public void setAdavncedPricingRulesPurged(int value)
    {
        adavncedPricingRulesPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Adavnced Pricing Rules Purged.
        @return the numer of Adavnced Pricing Rules Purged.
     **/
    //---------------------------------------------------------------------
    public int getAdavncedPricingRulesPurged()
    {
        return adavncedPricingRulesPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Timed Items Purged.
        @param value the numer of Timed Items Purged.
    **/
    //---------------------------------------------------------------------
    public void setTimedItemsPurged(int value)
    {
        timedItemsPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Timed Items Purged.
        @return the numer of Timed Items Purged.
     **/
    //---------------------------------------------------------------------
    public int getTimedItemsPurged()
    {
        return timedItemsPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Financial History Records Purged.
        @param value the number of Financial History Records Purged.
     **/
    //---------------------------------------------------------------------
    public void setFinancialHistoryPurged(int value)
    {
        financialHistoryPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Financial History Records Purged.
        @return the number of Financial History Records Purged.
     **/
    //---------------------------------------------------------------------
    public int getFinancialHistoryPurged()
    {
        return financialHistoryPurged;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Job Records Purged.
        @param value the number of Job Records Purged.
     **/
    //---------------------------------------------------------------------
    public void setJobsPurged(int value)
    {
        jobsPurged = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the Job Records Purged.
        @return the number of Job Records Purged.
     **/
    //---------------------------------------------------------------------
    public int getJobsPurged()
    {
        return jobsPurged;
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

        values.append("transactionsPurged:          [");
        values.append(transactionsPurged);
        values.append("]" + Util.EOL);

        values.append("ordersPurged:                [");
        values.append(ordersPurged);
        values.append("]" + Util.EOL);

        values.append("layawaysPurged:              [");
        values.append(layawaysPurged);
        values.append("]" + Util.EOL);

        values.append("adavncedPricingRulesPurged:  [");
        values.append(adavncedPricingRulesPurged);
        values.append("]" + Util.EOL);

        values.append("timedItemsPurged:            [");
        values.append(timedItemsPurged);
        values.append("]" + Util.EOL);

        values.append("financialHistoryPurged:      [");
        values.append(financialHistoryPurged);
        values.append("]" + Util.EOL);

        values.append("jobsPurged:                  [");
        values.append(jobsPurged);
        values.append("]" + Util.EOL);

        return values.toString();
    }
}                                       // End class PurgeResult

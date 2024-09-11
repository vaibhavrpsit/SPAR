/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FinancialTotalsSummaryEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:05 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
     This class describes a line entry for the FinancialTotalsSummary. <P>
     @see FinancialTotals, CurrencyIfc, FinancialTotalsSummaryBean.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class FinancialTotalsSummaryEntry implements FinancialTotalsSummaryEntryIfc
{                                       // begin class FinancialTotalsSummaryEntry
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7627748241567384347L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        type of entry
    **/
    protected String type = "";
    /**
        entered count amount
    **/
    protected CurrencyIfc entered = null;
    /**
        expected count amount
    **/
    protected CurrencyIfc expected = null;
    /**
        Indicates if the expected value should be displayed.
        Loans and pickups do not have and expected amount.
    **/
    protected boolean displayExpected = true;

    //---------------------------------------------------------------------
    /**
        Constructs FinancialTotalsSummaryEntry object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsSummaryEntry()
    {                                   // begin FinancialTotalsSummaryEntry()
        entered = DomainGateway.getBaseCurrencyInstance();
        expected = DomainGateway.getBaseCurrencyInstance();
    }                                   // end FinancialTotalsSummaryEntry()

    //----------------------------------------------------------------------------
    /**
        Retrieves type of entry. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return type of entry
    **/
    //----------------------------------------------------------------------------
    public String getType()
    {                                   // begin getType()
        return(type);
    }                                   // end getType()

    //----------------------------------------------------------------------------
    /**
        Sets type of entry. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  type of entry
    **/
    //----------------------------------------------------------------------------
    public void setType(String value)
    {                                   // begin setType()
        type = value;
    }                                   // end setType()

    //----------------------------------------------------------------------------
    /**
        Retrieves entered count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return entered count amount
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getEntered()
    {                                   // begin getEntered()
        return(entered);
    }                                   // end getEntered()

    //----------------------------------------------------------------------------
    /**
        Sets entered count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  entered count amount
    **/
    //----------------------------------------------------------------------------
    public void setEntered(CurrencyIfc value)
    {                                   // begin setEntered()
        entered = value;
    }                                   // end setEntered()

    //----------------------------------------------------------------------------
    /**
        Retrieves expected count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return expected count amount
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getExpected()
    {                                   // begin getExpected()
        return(expected);
    }                                   // end getExpected()

    //----------------------------------------------------------------------------
    /**
        Sets expected count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  expected count amount
    **/
    //----------------------------------------------------------------------------
    public void setExpected(CurrencyIfc value)
    {                                   // begin setExpected()
        expected = value;
    }                                   // end setExpected()

    //----------------------------------------------------------------------------
    /**
        Retrieves display expected indicator. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return display expected indicator
    **/
    //----------------------------------------------------------------------------
    public boolean getDisplayExpected()
    {
        return displayExpected;
    }

    //----------------------------------------------------------------------------
    /**
        Sets display expected indicator. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  display expected indicator
    **/
    //----------------------------------------------------------------------------
    public void setDisplayExpected(boolean value)
    {
        displayExpected = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  FinancialTotalsSummaryEntry (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        strResult += "type:                                   [" + type + "]\n";
        if (entered == null)
        {
            strResult += "entered:                                [null]\n";
        }
        else
        {
            strResult += "entered:                                [" + entered.toString() + "]\n";
        }
        if (expected == null)
        {
            strResult += "expected:                               [null]\n";
        }
        else
        {
            strResult += "expected:                               [" + expected.toString() + "]\n";
        }
        strResult += "displayExpected:                            [" + displayExpected + "]\n";
        // pass back result
        return(strResult);
    }                                   // end toString()

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

    //---------------------------------------------------------------------
    /**
        FinancialTotalsSummaryEntry main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        FinancialTotalsSummaryEntry c = new FinancialTotalsSummaryEntry();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class FinancialTotalsSummaryEntry

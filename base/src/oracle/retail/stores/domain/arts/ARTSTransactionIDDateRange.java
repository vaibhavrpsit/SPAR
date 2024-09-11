/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSTransactionIDDateRange.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
     Packages up key values for transaction-ID-and-date-range retrieval. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class ARTSTransactionIDDateRange implements Serializable
{                                       // begin class ARTSTransactionIDDateRange
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8865787257567639257L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        transaction identifier
    **/
    protected TransactionIDIfc transactionID = null;
    /**
        begin date range
    **/
    protected EYSDate beginDate = null;
    /**
        end date range
    **/
    protected EYSDate endDate = null;
    
        //---------------------------------------------------------------------
        /**
                Constructs ARTSTransactionIDDateRange object. <P>
        **/
        //---------------------------------------------------------------------
        public ARTSTransactionIDDateRange()
        {                                   // begin ARTSTransactionIDDateRange()
        }                                   // end ARTSTransactionIDDateRange()

    //---------------------------------------------------------------------
    /**
        Sets transaction identifier object. <P>
        @param value transaction identifier object 
    **/
    //--------------------------------------------------------------------- 
    public void setTransactionID(TransactionIDIfc value)
    {                                   // begin setTransactionID()
        transactionID = value;
    }                                   // end setTransactionID()

    //---------------------------------------------------------------------
    /**
        Returns transaction identifier object. <P>
        @return transaction identifier object 
    **/
    //--------------------------------------------------------------------- 
    public TransactionIDIfc getTransactionID()
    {                                   // begin getTransactionID()
        return(transactionID);
    }                                   // end getTransactionID()

    //---------------------------------------------------------------------
    /**
        Sets begin date. <P>
        @param value begin date 
    **/
    //--------------------------------------------------------------------- 
    public void setBeginDate(EYSDate value)
    {                                   // begin setBeginDate()
        beginDate = value;
    }                                   // end setBeginDate()

    //---------------------------------------------------------------------
    /**
        Returns begin date. <P>
        @return begin date 
    **/
    //--------------------------------------------------------------------- 
    public EYSDate getBeginDate()
    {                                   // begin getBeginDate()
        return(beginDate);
    }                                   // end getBeginDate()

    //---------------------------------------------------------------------
    /**
        Sets end date. <P>
        @param value end date 
    **/
    //--------------------------------------------------------------------- 
    public void setEndDate(EYSDate value)
    {                                   // end setEndDate()
        endDate = value;
    }                                   // end setEndDate()

    //---------------------------------------------------------------------
    /**
        Returns end date. <P>
        @return end date 
    **/
    //--------------------------------------------------------------------- 
    public EYSDate getEndDate()
    {                                   // end getEndDate()
        return(endDate);
    }                                   // end getEndDate()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  ARTSBeginDateDateRange (Revision " + 
                                      getRevisionNumber() +
                                      ") @" + 
                                      hashCode());
        strResult += "\n";
        // add attributes to string
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
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        ARTSBeginDateDateRange main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
    }                                   // end main()
}                                       // end class ARTSBeginDateDateRange

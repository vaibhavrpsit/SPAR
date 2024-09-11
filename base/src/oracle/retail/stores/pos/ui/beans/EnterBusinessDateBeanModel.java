/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterBusinessDateBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    Bean model for EnterBusinessDateBean class. <P>
    @version $KW=@(#); $Ver=pos_4.5.0:72; $EKW;
**/
//------------------------------------------------------------------------------
public class EnterBusinessDateBeanModel extends POSBaseBeanModel
{                                       // begin class EnterBusinessDateBeanModel

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:72; $EKW;";
    /**
        business date field
    **/
    protected EYSDate businessDate = null;
    /**
        next business date field
    **/
    protected EYSDate nextBusinessDate = null;
    
    //---------------------------------------------------------------------
    /**
        Constructs EnterBusinessDateBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public EnterBusinessDateBeanModel()
    {                                   // begin EnterBusinessDateBeanModel()
        businessDate = DomainGateway.getFactory().getEYSDateInstance();
        nextBusinessDate = DomainGateway.getFactory().getEYSDateInstance();
        businessDate.initialize(EYSDate.TYPE_DATE_ONLY);
        nextBusinessDate.initialize(EYSDate.TYPE_DATE_ONLY);
    }                                   // end EnterBusinessDateBeanModel()
    
    //----------------------------------------------------------------------
    /**
        Returns the business date. <P>
        @return The business date.
    **/
    //----------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {                                   // begin getBusinessDate()
        return businessDate;
    }                                   // end getBusinessDate()

    //----------------------------------------------------------------------
    /**
        Sets the business date. <P>
        @param  value  The business date.
    **/
    //----------------------------------------------------------------------
    public void setBusinessDate(EYSDate value)
    {                                   // begin setBusinessDate()
        businessDate = value;
    }                                   // end setBusinessDate()

    //----------------------------------------------------------------------
    /**
        Returns the next business date. <P>
        @return The next business date.
    **/
    //----------------------------------------------------------------------
    public EYSDate getNextBusinessDate()
    {                                   // begin getNextBusinessDate()
        return nextBusinessDate;
    }                                   // end getNextBusinessDate()

    //----------------------------------------------------------------------
    /**
        Sets the next business date. <P>
        @param  value  The next business date.
    **/
    //----------------------------------------------------------------------
    public void setNextBusinessDate(EYSDate value)
    {                                   // begin setNextBusinessDate()
        nextBusinessDate = value;
    }                                   // end setNextBusinessDate()
    
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()

        // result string
        String strResult = new String("Class:  EnterBusinessDateBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n Business Date: " + businessDate +
                     "\n Next Business Date: " + nextBusinessDate;
                                                   
        // pass back result
        return(strResult);
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        EnterBusinessDateBeanModel main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        EnterBusinessDateBeanModel cls = new EnterBusinessDateBeanModel();
        // output toString()
        System.out.println(cls.toString());
    }                                  // end main()

}                                      // end class EnterBusinessDateBeanModel

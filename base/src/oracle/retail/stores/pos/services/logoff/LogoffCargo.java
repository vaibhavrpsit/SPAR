/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/logoff/LogoffCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:54  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.logoff;
// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

//--------------------------------------------------------------------------
/**
    The cargo needed by the POS service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogoffCargo extends AbstractFinancialCargo
{                                       // begin class LogoffCargo
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The operator ID prompt text for this service
    **/
    public static final String operatorIdPromptText = "Enter cashier ID.";
    /**
        The screen name for this service
    **/
    public static final String operatorIdScreenName = "Cashier Logoff";

    //----------------------------------------------------------------------
    /**
        Gets the prompt Enter ID prompt text for this service.
        <P>
        @return the prompt text.
    **/
    //----------------------------------------------------------------------
    public String getOperatorIdPromptText()
    {                                   // begin getOperatorIdPromptText()
        return operatorIdPromptText;
    }                                   // end getOperatorIdPromptText()

    /**
        Gets the screen name for this service.
        <P>
        @return the screen name.
    **/
    //----------------------------------------------------------------------
    public String getOperatorIdScreenName()
    {                                   // begin getOperatorIdScreenName()
        return operatorIdScreenName;
    }                                   // end getOperatorIdScreenName()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  LogoffCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        LogoffCargo main method. <P>
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
        LogoffCargo c = new LogoffCargo();
        // output toString()
        System.out.println(c.toString());
        AbstractFinancialCargo afc = (AbstractFinancialCargo)c;
        System.out.println("OperatorIdPromptText: " + afc.getOperatorIdPromptText());
        System.out.println("OperatorIdScreenName: " + afc.getOperatorIdScreenName());
        
    }                                   // end main()
}                                       // end class LogoffCargo

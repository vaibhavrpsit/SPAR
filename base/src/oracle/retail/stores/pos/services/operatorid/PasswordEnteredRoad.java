/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/PasswordEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         10/12/2006 8:17:50 AM  Christian Greene
 *         Adding new functionality for PasswordPolicy.  Employee password
 *         will now be persisted as a byte[] in hexadecimal.  Updates include
 *         UI changes, persistence changes, and AppServer configuration
 *         changes.  A database rebuild with the new SQL scripts will be
 *         required.
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:00 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

// foundation imports
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This road stores the password in the cargo.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PasswordEnteredRoad extends LaneActionAdapter
{
    /** Logger for debugging. */
    protected static final Logger logger = Logger.getLogger(PasswordEnteredRoad.class);
    
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores the password in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        try
        {
            cargo.setEmployeePasswordBytes(ui.getInput().getBytes(EmployeeIfc.PASSWORD_CHARSET));
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Unable to use correct password character set", e);
            if (logger.isDebugEnabled())
                logger.debug("Defaulting to system character set: " + ui.getInput());
            cargo.setEmployeePasswordBytes(ui.getInput().getBytes());
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  PasswordEnteredRoad (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

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

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        PasswordEnteredRoad obj = new PasswordEnteredRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}

/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/IsNotRetrySignal.java /main/10 2013/07/12 15:00:49 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  07/03/13 - Handle situation where manager (override) does not
 *                         have permission to access point
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         10/9/2006 5:10:32 PM   Rohit Sachdeva
 *         21237: Login Updates to Handle Impacts of Password Policy
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/02/13 19:43:06  jriggins
 *   @scr 0 Removed elements causing compiler warnings
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
 *    Rev 1.0   Aug 29 2003 16:03:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    Indicates if the user has lockout

     <P>
     @version $Revision: /main/10 $
**/
//--------------------------------------------------------------------------
public class IsNotRetrySignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4186415224902409918L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.operatorid.IsNotRetrySignal.class);

    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /main/10 $";
    /**
        signal name
    **/
    public static final String SIGNALNAME = "IsNotRetrySignal";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed

       @param bus the bus trying to proceed
       @return true if attempts have been exhausted; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {

        boolean ret = false;
        OperatorIdCargo cargo  = (OperatorIdCargo)bus.getCargo();

        // The site should not do a retry if the error is NOT a no_data exception OR
        // if there is already a lockout OR
        // this is a part of the manager override flow

        boolean checkLockOut = cargo.isLockOut();
        if ((cargo.getErrorType() != DataException.NO_DATA || checkLockOut) && !cargo.getSecurityOverrideFlag())
        {
            ret = true;
        }
        return ret;
    }
 
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
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + SIGNALNAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()

}

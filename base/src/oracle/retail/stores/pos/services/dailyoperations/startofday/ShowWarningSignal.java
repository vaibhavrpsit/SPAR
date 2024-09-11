/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/ShowWarningSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:14 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:29:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:44   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:28:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal checks to see if the service should display a
    confirmation screen before proceeding.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ShowWarningSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6519205783499953701L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Checks to see if the confirmation screen should be displayed.
        <p>
        @param  bus the bus trying to proceed
        @return true if screen should be displayed; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        StartOfDayCargo cargo = (StartOfDayCargo)bus.getCargo();
        return(cargo.getShowWarning());
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  ShowWarningSignal (Revision "
                                      + getRevisionNumber()
                                      + ")" + hashCode());
        return(strResult);
    }
}

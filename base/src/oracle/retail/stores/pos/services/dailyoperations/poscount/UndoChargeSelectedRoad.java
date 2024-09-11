/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/UndoChargeSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    desc.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UndoChargeSelectedRoad extends LaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sends a Next letter.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        cargo.setCurrentActivity(PosCountCargo.NONE);
        cargo.setCurrentCharge(PosCountCargo.NONE);

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
        String strResult = new String("Class:  UndoChargeSelectedRoad (Revision " +
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
        UndoChargeSelectedRoad obj = new UndoChargeSelectedRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}

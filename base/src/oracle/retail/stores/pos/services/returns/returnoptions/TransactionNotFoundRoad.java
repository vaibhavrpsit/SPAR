/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/TransactionNotFoundRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Feb 09 2004 12:46:08   baa
 * Initial revision.
 * 
 *    Rev 1.0   Dec 23 2003 14:58:10   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:28   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:24:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the user presses the accept key from
    the RETURN_LINKED_TRANS screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TransactionNotFoundRoad extends LaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Gets the selected transacation index from the ui.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Update the cargo
        ReturnOptionsCargo cargo = (ReturnOptionsCargo )bus.getCargo();
        cargo.setTransactionFound(false);

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
        String strResult = new String("Class:  GetSelectedTransactionRoad (Revision " +
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

}

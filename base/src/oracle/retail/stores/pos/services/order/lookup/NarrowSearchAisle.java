/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/NarrowSearchAisle.java /main/11 2012/07/13 12:43:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:24  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:01:14   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

//foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;

//------------------------------------------------------------------------------
/**
    Determines whether to narrow the search for orders using a status search
    or a date range search.
    <P>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------

public class NarrowSearchAisle extends PosLaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "NarrowSearchAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //--------------------------------------------------------------------------
    /**
       Determines letter to mail based upon the search method (status or date range).
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        Letter result = new Letter(CommonLetterIfc.DATERANGE); // date range search

        bus.mail(result,BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
    **/
    //----------------------------------------------------------------------

    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  NarrowSearchAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------

    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}

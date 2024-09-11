/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/NoMatchConversionAisle.java /main/12 2012/07/13 12:43:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:08 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:03:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:01:14   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;

//------------------------------------------------------------------------------
/**
    Converts the current letter to NoMatch or Status based upon the search method.
    <P>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------

public class NoMatchConversionAisle extends PosLaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "NoMatchConversionAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
       Converts letter to NoMatch if called by PickList, Status if performing
       a status based search, or DateRange is performing a date range search.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // defaut letter
        Letter result = new Letter (CommonLetterIfc.NOMATCH); // default letter for NoMatch
        bus.mail(result,BusIfc.CURRENT);

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
        String strResult = new String("Class: NoMatchConversionAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

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

}

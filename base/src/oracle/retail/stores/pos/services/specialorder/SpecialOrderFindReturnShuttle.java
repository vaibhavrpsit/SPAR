/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/SpecialOrderFindReturnShuttle.java /main/11 2014/06/02 18:02:53 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/02/14 - Add Amount Paid for order pick or order cancel in
 *                         show sale screen.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.3  2004/05/20 19:47:44  jeffp
 *   @scr 2389 - set the last printable transaction id.
 *
 *   Revision 1.2  2004/02/12 16:52:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:00   msg
 * Initial revision.
 * 
 *    Rev 1.2   11 Mar 2002 20:08:28   pdd
 * Removed reference to find cargo.
 * Resolution for POS SCR-1549: Cleanup special order services
 * 
 *    Rev 1.1   09 Jan 2002 15:05:56   jbp
 * changes so special order modify calls order service
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 10 2001 19:40:36   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the current service with the information
    from the find special order service.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class SpecialOrderFindReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 15903382480503684L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       Child cargo.
    **/
    protected OrderCargo orderCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        orderCargo = (OrderCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        //SpecialOrderCargoIfc cargo = (SpecialOrderCargoIfc)bus.getCargo();
        //cargo.setOrder(orderCargo.getOrder());
        SpecialOrderCargo cargo = (SpecialOrderCargo)bus.getCargo();
        cargo.setOrder(orderCargo.getOrder());
        cargo.setOrderTransaction(orderCargo.getOrderTransaction());
        cargo.setLastReprintableTransactionID(orderCargo.getLastReprintableTransactionID());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ReturnFindTransReturnShuttle (Revision " +
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

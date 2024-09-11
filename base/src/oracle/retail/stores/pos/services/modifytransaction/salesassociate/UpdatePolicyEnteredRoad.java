/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/salesassociate/UpdatePolicyEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:15  mcs
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
 *    Rev 1.0   Aug 29 2003 16:02:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.salesassociate;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when an Update Policy is selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UpdatePolicyEnteredRoad extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores the response in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        ModifyTransactionSalesAssociateCargo cargo;
        cargo = (ModifyTransactionSalesAssociateCargo)bus.getCargo();

        /*
         * Check to see if a Yes Letter caused us to travel this road
         */
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.YES))
        {
            cargo.setUpdateAllItemsFlag(true);
        }
        else // NO Letter
        {
            cargo.setUpdateAllItemsFlag(false);
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
    {
        String strResult = new String("Class:  UpdatePolicyEnteredRoad (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
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
}

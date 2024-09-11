/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/CustomerSearchSetupRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:23  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:17:30   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Sets the clearUIFields flag to re-initialize the Narrow Search screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CustomerSearchSetupRoad extends LaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "CustomerSearchSetupRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Sets the clearUIFields flag for the ui.
       @param bus the bus arriving at this road
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = ( POSUIManagerIfc ) bus.getManager( UIManagerIfc.TYPE );
        POSBaseBeanModel posModel = ( POSBaseBeanModel ) ui.getModel( POSUIManagerIfc.NARROW_SEARCH );
        DateSearchBeanModel model = new DateSearchBeanModel();
        if( posModel instanceof DateSearchBeanModel )
        {
            model = ( DateSearchBeanModel ) posModel;
        }        
        model.setclearUIFields(true); // clear dates in ui - narrow search
        ui.setModel(POSUIManagerIfc.NARROW_SEARCH, model);

    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + LANENAME + " (Revision "
                                      + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}

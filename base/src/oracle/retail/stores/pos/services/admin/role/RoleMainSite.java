/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/RoleMainSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:54:19  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    This site prompts the user to select Find or Add Role.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class RoleMainSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "RoleMainSite";

    //--------------------------------------------------------------------------
    /**
       Displays the screen menu to select add or find role.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ROLE_OPTIONS, new POSBaseBeanModel());

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
        String strResult = new String("Class: RoleMainSite"
                                      + " (Revision " + getRevisionNumber() + ")"
                                      + hashCode());

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

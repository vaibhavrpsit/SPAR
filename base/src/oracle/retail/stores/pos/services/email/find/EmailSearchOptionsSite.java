/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/EmailSearchOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 15:24:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:17:30   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    This site is used to allow the user to choose an email search option.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EmailSearchOptionsSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "EmailSearchOptionsSite";

    //--------------------------------------------------------------------------
    /**
       Display email search options by Order ID, Customer or InBox.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to Email Cargo for this service
        EmailCargo cargo = (EmailCargo)bus.getCargo();

        cargo.setDateRange(false); // reset flag on entry - do not need dates yet

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.EMAIL_SEARCH, model);

    }
}

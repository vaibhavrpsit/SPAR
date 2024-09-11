/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/DisplayLayawayConfirmDeleteSite.java /main/13 2012/08/27 11:22:37 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/14/12 - wptg - removed placeholder from key
 *                         LayawayConfirmDelete
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 29 2002 14:11:12   jriggins
 * Changed String.toLowerCase() call to String.toLowerCase(Locale)
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:20:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:46   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2002 18:00:26   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   16 Jan 2002 16:35:18   jbp
 * modified for security access
 * Resolution for POS SCR-638: Manager Override not working for Layaway Delete
 *
 *    Rev 1.0   Sep 21 2001 11:21:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the layaway confirm delete screen.
    <P>
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class DisplayLayawayConfirmDeleteSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "DisplayLayawayConfirmDeleteSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
        layaway confirm delete screen name
    **/
    private static final String RESOURCE_ID = "LayawayConfirmDelete";

    //--------------------------------------------------------------------------
    /**
       Displays the Layaway Confirm Delete screen.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // init dialog model
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog and title
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.CONFIRMATION);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}

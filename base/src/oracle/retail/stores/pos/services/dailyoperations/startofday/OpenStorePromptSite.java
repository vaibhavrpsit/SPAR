/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/OpenStorePromptSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:29:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:30   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:28:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:00:10   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:16:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;

// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site confirms that the user wants to open the store. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OpenStorePromptSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * store prompt tag
     */
    protected static String STORE_PROMPT_TAG = "StoreRegisterTillClosed.Store";
    /**
     * store prompt
     */
    protected static String STORE_PROMPT = "store";

    //--------------------------------------------------------------------------
    /**
        Confirms that the user wants to open the store. <P>
        @param bus the bus
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Display the confirmation screen
        DialogBeanModel model = new DialogBeanModel();
        String args[] = new String[1];
        args[0] = utility.retrieveDialogText(STORE_PROMPT_TAG,
                                             STORE_PROMPT);
        model.setResourceID(AbstractFinancialCargo.STORE_REGISTER_TILL_CLOSED);
        model.setType(DialogScreensIfc.CONFIRMATION);
        model.setArgs(args);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}

/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/SecurityMenuSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:07 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse   
 * $
 * Revision 1.8  2004/09/20 22:07:30  kll
 * @scr 7219: retract fix for SCR #6609
 *
 * Revision 1.7  2004/07/29 18:58:15  lwalters
 * @scr 6609
 *
 * Commented out code which keeps the defaults from securityuicfg.xml from loading
 *
 * Revision 1.6  2004/03/26 21:18:19  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.5  2004/03/24 23:23:55  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.4 2004/03/14 21:19:34 tfritz @scr 3884 - New Training Mode Functionality
 * 
 * Revision 1.3 2004/02/12 16:49:01 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:37:32 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:14 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 15:53:42 CSchellenger Initial revision.
 * 
 * Rev 1.0 Apr 29 2002 15:37:08 msg Initial revision.
 * 
 * Rev 1.1 Mar 18 2002 23:07:54 msg - updated copyright
 * 
 * Rev 1.0 Mar 18 2002 11:21:32 msg Initial revision.
 * 
 * Rev 1.1 21 Jan 2002 17:50:34 baa converting to new security model Resolution for POS SCR-309: Convert to new
 * Security Override design.
 * 
 * Rev 1.0 Sep 21 2001 11:10:54 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;


//------------------------------------------------------------------------------
/**
 * The SecurityMenu site allows the user to select the security function she wishes to use.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------

public class SecurityMenuSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "SecurityMenuSite";

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //--------------------------------------------------------------------------
    /**
     * The SecurityMenu site allows the user to select the security function she wishes to use.
     * 
     * @param bus
     *            the bus arriving at this site
     */
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to <ServiceName>Cargo
        SecurityCargo cargo = (SecurityCargo) bus.getCargo();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        RegisterIfc register = cargo.getRegister();
        boolean trainingModeOn = false;
        boolean transReentry = false;

        if (register != null)
        {
            trainingModeOn = register.getWorkstation().isTrainingMode();
            transReentry = register.getWorkstation().isTransReentryMode();
        }

        if (trainingModeOn)
        {
            nModel.setButtonEnabled(CommonActionsIfc.PARAMETERS, false);
            nModel.setButtonEnabled(CommonActionsIfc.REASON_CODES, false);
        }
        else
        {
            nModel.setButtonEnabled(CommonActionsIfc.PARAMETERS, true);
            nModel.setButtonEnabled(CommonActionsIfc.REASON_CODES, true);
        }

        // if trans reentry is on, "Roles" should be off.
        nModel.setButtonEnabled(CommonActionsIfc.ROLES, !transReentry);

        pModel.setLocalButtonBeanModel(nModel);
        pModel.setInTraining(trainingModeOn);

        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SECURITY_OPTIONS, pModel);

    }

}

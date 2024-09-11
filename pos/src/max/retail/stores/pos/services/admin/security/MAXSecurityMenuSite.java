/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	Veeresh		8/06/2013		Initial Draft: Changes for Employee, Role and Parameter button disable
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.admin.security;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.admin.security.SecurityCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
 * The SecurityMenu site allows the user to select the security function she wishes to use.
 * 
 * @version $Revision: 3$
 */
//------------------------------------------------------------------------------

public class MAXSecurityMenuSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "SecurityMenuSite";

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: 3$";

    /**
     * Constant for the Roles button action name
     */
    public static final String ROLES_ACTION = "Roles";
    /**
     * Constant for parameters operations button action name
     */
    public static final String PARAMETERS_ACTION = "Parameters";
    /**
     * Constant for reason codes button action name
     */
    public static final String REASON_CODES_ACTION = "ReasonCodes";

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
            nModel.setButtonEnabled(PARAMETERS_ACTION, false);
            nModel.setButtonEnabled(REASON_CODES_ACTION, false);
        }
        else
        {
        	// Rev 1.0 start
            nModel.setButtonEnabled(PARAMETERS_ACTION, false);
            nModel.setButtonEnabled(REASON_CODES_ACTION, true);
        }

        // if trans reentry is on, "Roles" should be off.
       // nModel.setButtonEnabled(ROLES_ACTION, !transReentry);
        nModel.setButtonEnabled(ROLES_ACTION, false);
        // Rev 1.0 end
        pModel.setLocalButtonBeanModel(nModel);
        pModel.setInTraining(trainingModeOn);

        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SECURITY_OPTIONS, pModel);

    }

}

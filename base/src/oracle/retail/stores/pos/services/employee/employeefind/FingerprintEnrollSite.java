/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/FingerprintEnrollSite.java /main/2 2011/02/23 18:52:23 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   05/18/10 - fingerprint enroll site
 *    
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    The FingerprintEnrollSite site presents the screen which enables the fingerprint reader
    and captures fingerprint enrollment biometric data.
    
    @version $Revision: /main/2 $
**/
//------------------------------------------------------------------------------

public class FingerprintEnrollSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 7653460580641129446L;
    
    public static final String revisionNumber = "$Revision: /main/2 $";


    /*
     * 
     */
    public void arrive(BusIfc bus)
    {
        POSBaseBeanModel model = new POSBaseBeanModel();

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_ENROLL_FINGERPRINT, model);
    }
}

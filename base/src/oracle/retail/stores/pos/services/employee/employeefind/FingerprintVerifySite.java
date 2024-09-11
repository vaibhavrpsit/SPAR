/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/FingerprintVerifySite.java /main/1 2011/02/23 18:52:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   02/15/11 - Fingerprint Verify Site
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
    The FingerprintVerifySite site presents the screen which puts the fingerprint reader
    in verify mode.
    
    This site begins the enrollment verification process which is performed in
    FingerprintVerifyAisle.
    
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------

public class FingerprintVerifySite extends PosSiteActionAdapter
{

    public static final String revisionNumber = "$Revision: /main/1 $";


    /*
     * Display the verify fingerprint screen (which enables the fingerprint reader in verify mode).
     */
    public void arrive(BusIfc bus)
    {
        POSBaseBeanModel model = new POSBaseBeanModel();

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_VERIFY_FINGERPRINT, model);
    }
}

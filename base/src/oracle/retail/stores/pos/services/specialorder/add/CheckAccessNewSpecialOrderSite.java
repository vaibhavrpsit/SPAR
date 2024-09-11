/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/CheckAccessNewSpecialOrderSite.java /main/2 2014/04/07 13:59:26 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/31/14 - The class is deprecated.
 *
 * ===========================================================================
 * $Log:
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//------------------------------------------------------------------------------
/**
  Access check for creating a special order. 
  CheckAccessSite cannot be used since functionID is retrieved from cargo.getAccessFunctionID() in 
  CheckAccessSite and SpecialOrderCargo always returns RoleFunctionIfc.CANCEL_ORDER.
  <P>
  @version $Revision: /main/2 $
  @deprecated as of 14.0
**/
//------------------------------------------------------------------------------
public class CheckAccessNewSpecialOrderSite extends SiteActionAdapter {
    
    private static final long serialVersionUID = 1L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /main/2 $";

    /**
     * Check access and mail appropriate letter.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // return variable to indicate whether the user has access to the
        // code function or not.
        boolean access = false;

        // Default letter assumes that the operator will need to override the
        // access credentials of the current user.
        String letter = CommonLetterIfc.OVERRIDE;

        // get the cargo for the information needed to check the access.
        UserAccessCargoIfc cargo = (UserAccessCargoIfc) bus.getCargo();

        // get the security manager to be able to check the access to the code
        // function of the current user.
        SecurityManagerIfc securityManager =
                (SecurityManagerIfc) Gateway.getDispatcher(). getManager(
                SecurityManagerIfc.TYPE);
 
        // Check the access of the user to the code function
        access = securityManager.checkAccess(cargo.getAppID(), RoleFunctionIfc.ORDERS);
               

        // if access has been granted then need to send a CONTINUE letter.
        if (access)
        {
            letter = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}

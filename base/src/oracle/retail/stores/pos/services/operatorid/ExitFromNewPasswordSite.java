/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/ExitFromNewPasswordSite.java /main/10 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:32 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:58 PM  Robert Pearse   
 *
 Revision 1.3  2004/03/16 18:30:49  cdb
 @scr 0 Removed tabs from all java source code.
 *
 Revision 1.2  2004/02/13 19:43:06  jriggins
 @scr 0 Removed elements causing compiler warnings
 *
 Revision 1.1  2004/02/13 16:35:20  jriggins
 @scr 3782 Initial revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site is traversed when a user decides to cancel or undo while in the
 * process of creating a new password. It will log them out since they had
 * successfully logged in with their temporary passwords but had chosen not to
 * update their passwords as required.
 */
public class ExitFromNewPasswordSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8970221963079237458L;
    /**
     * revision number of this class 
     */
    public static String revisionNumber = "$Revision: /main/10 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Log the employee out.        
        String employeeID = null;
        String appID = null;
        try
        {
            OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
            employeeID = cargo.getEmployeeID();
            appID = cargo.getAppID();
            SecurityManagerIfc securityManager = 
                (SecurityManagerIfc)Gateway.getDispatcher().getManager(SecurityManagerIfc.TYPE);
            securityManager.logoutUser(appID, employeeID);
        }
        catch (LoginException le)
        {
            logger.error("Unable to logout employee " + employeeID, le);
        }
        
        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
        bus.mail(letter, BusIfc.CURRENT);
    }
}

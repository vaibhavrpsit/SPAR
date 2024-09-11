/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/CheckBusinessDateAccessSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
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
 *  1    360Commerce 1.0         4/1/2008 2:30:37 PM    Deepti Sharma   CR
 *       31016 forward port from v12x -> trunk
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

// Foundation imports
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This site checks to see if the current operator has access to the
    specified function.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckBusinessDateAccessSite extends SiteActionAdapter
{
    /**  */
    private static final long serialVersionUID = 7245188871923196450L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Check access and mail appropriate letter.
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
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
        cargo.setAccessFunctionID(RoleFunctionIfc.START_OF_DAY);

        // get the security manager to be able to check the access to the code
        // function of the current user.
        SecurityManagerIfc securityManager =
                (SecurityManagerIfc) Gateway.getDispatcher(). getManager(
                SecurityManagerIfc.TYPE);

        // Check the access of the user to the code function
        access = securityManager.checkAccess(cargo.getAppID(),
                cargo.getAccessFunctionID());

        // if access has been granted then need to send a CONTINUE letter.
        if (access)
        {
            letter = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}

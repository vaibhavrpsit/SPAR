/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/IsNewPasswordNeededSignal.java /main/9 2011/02/23 10:45:33 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/22/11 - Modified to handle multiple password policies
 *                         (introduction of biometrics)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         11/2/2006 7:10:46 AM   Rohit Sachdeva  21237:
 *       Activating Password Policy Evaluation and Change Password
 *  6    360Commerce 1.5         10/25/2006 3:14:09 PM  Rohit Sachdeva  21237:
 *       Password Policy TDO updates
 *  5    360Commerce 1.4         10/16/2006 3:35:03 PM  Rohit Sachdeva  21237:
 *       Password Policy Flow Updates
 *  4    360Commerce 1.3         10/9/2006 1:55:07 PM   Rohit Sachdeva  21237:
 *       Change Password Updates
 *  3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:18 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 * $
 * Revision 1.3  2004/09/23 00:07:17  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.2  2004/02/26 22:57:54  jriggins
 * @scr 3872 code review changes
 *
 * Revision 1.1  2004/02/24 17:28:24  jriggins
 * @scr 3782 Added IsNewPasswordNeededSignal in order to test for the need to prompt the user to enter a new password.  Removed this logic from ValidateLoginSite.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;

import org.apache.log4j.Logger;

//------------------------------------------------------------------------------
/**
 * Determines whether or not the tour needs to prompt the user to change password
 * based on first time login after reset password.
 * @version $Revision: /main/9 $
 */
//------------------------------------------------------------------------------

public class IsNewPasswordNeededSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7200344098060898561L;
    /**
     The logger to which log messages will be sent
     **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.operatorid.IsNewPasswordNeededSignal.class);


    //--------------------------------------------------------------------------
    /**
     * Determines whether or not the tour needs to prompt the user 
     * to change password based on first time login after reset password.
     * 
     * @param bus the bus trying to proceed
     * @return true if a password change is needed; false otherwise
     */
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        boolean passwordChangeRequired = false;
        EmployeeIfc employee = cargo.getSelectedEmployee();
        PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();
        if (tdo.checkEmployeeComplianceEvaluationAllowed(bus) && 
        	tdo.checkEmployeeApplyPolicy(employee))
        {
        	passwordChangeRequired = tdo.checkPasswordChangeByFirstTime(bus);            
        }
       
        return passwordChangeRequired;
    }


    /**
       Creates Instance of Password Policy TDO.
       @return PasswordPolicyTDOIfc instance of Password Policy TDO
    **/
	private PasswordPolicyTDOIfc getPasswordPolicyTDO()
	{
	    return Utility.getUtil().getPasswordPolicyTDO();
		
	}

}

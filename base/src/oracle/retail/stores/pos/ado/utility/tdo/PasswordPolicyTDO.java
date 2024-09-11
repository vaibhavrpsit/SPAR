/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/tdo/PasswordPolicyTDO.java /main/9 2011/02/23 10:45:34 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/22/11 - password policies abstracted to
 *                         AbstractPasswordPolicyTDO
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/1/2008 5:01:08 AM    Anil Kandru     The
 *         maximum failure attempts and the number of failed login attempts
 *         were set in the cargo.
 *    5    360Commerce 1.4         11/2/2006 7:06:30 AM   Rohit Sachdeva
 *         21237: Activating Password Policy Evaluation and Change Password 
 *    4    360Commerce 1.3         10/25/2006 3:12:05 PM  Rohit Sachdeva
 *         21237: Password Policy TDO updates
 *    3    360Commerce 1.2         10/20/2006 11:13:16 AM Rohit Sachdeva
 *         21237: Password Policy Flow Updates
 *    2    360Commerce 1.1         10/17/2006 4:21:10 PM  Rohit Sachdeva
 *         21237: Password Policy Flow Updates to Handle Impacts of Password
 *         Policy for Logged In and Not Logged In User
 *    1    360Commerce 1.0         10/17/2006 4:11:02 PM  Rohit Sachdeva  
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility.tdo;


import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;


//------------------------------------------------------------------------------
/**
    This TDO is used for Password Policy various evaluations and
    its affects in terms of flow in the application. 
    @version $Revision: /main/9 $
**/
//------------------------------------------------------------------------------
public class PasswordPolicyTDO extends AbstractPasswordPolicyTDO implements PasswordPolicyTDOIfc
{
    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /main/9 $";
    
    /** 
     * Spring Key used to load this bean
     */ 
    public static final String PASSWORD_POLICY_TDO_BEAN_KEY = "application_PasswordPolicyTDO";
    
    
    /**
     * Default Constructor for PasswordPolicyTDO
     */
    public PasswordPolicyTDO()
    {
        super();
    }
    
   //----------------------------------------------------------------------
    /**
       Checks if Employee Compliance is Allowed.
       @param bus reference to bus
       @return true if employee compliance is allowed, otherwise false
    **/
    //----------------------------------------------------------------------
	public boolean checkEmployeeComplianceEvaluationAllowed(BusIfc bus) 
	{
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		if (cargo.getSecurityOverrideFlag())
		{
			return false;
		}
		boolean trainingModeOn = checkTrainingMode(bus);
		if(trainingModeOn)
		{
			return false;
		}
		return true;
	}
}
